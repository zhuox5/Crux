package crux.printing;

import crux.ast.*;
import crux.ast.SymbolTable.Symbol;
import crux.ast.traversal.DefaultNodeVisitor;
import crux.ast.types.TypeChecker;

import java.io.PrintStream;
import java.util.stream.Collectors;

public final class ASTPrinter {
  private static final String indent = "  ";
  private final PrinterVisitor printer;
  private boolean typesEnabled = false;
  private PrintStream stdout;

  public ASTPrinter(PrintStream stdout) {
    this(stdout, null);
  }

  public ASTPrinter(PrintStream stdout, TypeChecker checker) {
    printer = new PrinterVisitor(checker);
    this.stdout = stdout;
  }

  public void enableTypes() {
    typesEnabled = true;
  }

  public void print(Node node) {
    stdout.println(node.accept(printer));
  }

  private final class PrinterVisitor extends DefaultNodeVisitor<String> {
    private final TypeChecker checker;
    int indentlevel = 0;

    private PrinterVisitor(TypeChecker checker) {
      this.checker = checker;
    }

    public String visitDefault(Node n) {
      return null;
    }

    @Override
    public String visit(ArrayAccess aaccess) {
      String str = aaccess.getBase().getName();
      if (checker != null)
        str = "(/*" + aaccess.getBase().getType() + "*/ " + str + ")";
      str += "[" + aaccess.getIndex().accept(this) + "]";
      if (checker != null && checker.getType(aaccess) != null)
        str = "/*" + checker.getType(aaccess).toString() + "*/ " + str;
      return str;
    }

    @Override
    public String visit(ArrayDeclaration arrayDeclaration) {
      return visit(arrayDeclaration.getSymbol());
    }

    @Override
    public String visit(Assignment assign) {
      return assign.getLocation().accept(this) + " = " + assign.getValue().accept(this);
    }

    @Override
    public String visit(Break cont) {
      return "break";
    }

    @Override
    public String visit(Call call) {
      String str = call.getCallee().getName() + "(";
      boolean isFirst = true;
      for (Expression e : call.getArguments()) {
        if (isFirst)
          isFirst = false;
        else
          str += ", ";
        str += e.accept(this);
      }
      str += ")";
      return str;
    }

    @Override
    public String visit(DeclarationList dlist) {
      String str = "\n";
      for (Node s : dlist.getChildren()) {
        str += s.accept(this);
        str += needSemi(str) + "\n";
      }
      return str;
    }

    @Override
    public String visit(FunctionDefinition functionDefinition) {
      String str = functionDefinition.getSymbol().getName() + "(";
      boolean isFirst = true;
      for (Symbol p : functionDefinition.getParameters()) {
        if (isFirst)
          isFirst = false;
        else
          str += ", ";
        str += visit(p);
      }
      str += ") ";
      str += functionDefinition.getStatements().accept(this);
      return str;
    }

    @Override
    public String visit(IfElseBranch iebranch) {
      String str = "if (" + iebranch.getCondition().accept(this) + ")";
      String thenblock = iebranch.getThenBlock().accept(this);
      String elseblock = iebranch.getElseBlock().accept(this);
      str += " " + thenblock + " else " + elseblock;
      return str;
    }

    @Override
    public String visit(LiteralBool literalBool) {
      var valueString = literalBool.getValue() ? "true" : "false";
      if (checker != null && checker.getType(literalBool) != null)
        valueString = "/*" + checker.getType(literalBool).toString() + "*/ " + valueString;
      return valueString;
    }

    @Override
    public String visit(LiteralInt literalInt) {
      String valueString = Long.toString(literalInt.getValue());
      if (checker != null && checker.getType(literalInt) != null)
        valueString = "/*" + checker.getType(literalInt).toString() + "*/ " + valueString;
      return valueString;
    }

    @Override
    public String visit(For loop) {
      String str = "for(" + loop.getInit().accept(this) + ";" + loop.getCond().accept(this) + ";"
          + loop.getIncrement().accept(this) + ") " + loop.getBody().accept(this);
      return str;
    }

    @Override
    public String visit(OpExpr op) {
      if (op.getRight() == null) {
        String str = "(" + op.getOp() + op.getLeft().accept(this) + ")";
        if (checker != null && checker.getType(op) != null)
          str = "/*" + checker.getType(op).toString() + "*/ " + str;
        return str;
      } else {
        String str = "(" + op.getLeft().accept(this) + " " + op.getOp() + " "
            + op.getRight().accept(this) + ")";
        if (checker != null && checker.getType(op) != null)
          str = "/*" + checker.getType(op).toString() + "*/ " + str;
        return str;
      }
    }

    @Override
    public String visit(Return ret) {
      if (ret.getValue() != null)
        return "return " + ret.getValue().accept(this);
      else
        return "return";
    }

    public String indent() {
      String str = "";
      for (int i = 0; i < indentlevel; i++)
        str += "  ";
      return str;
    }

    @Override
    public String visit(StatementList list) {
      String str = "{\n";
      indentlevel++;
      for (Node s : list.getChildren()) {
        str += indent() + s.accept(this);
        str += needSemi(str) + "\n";
      }
      indentlevel--;
      str += indent() + "}";
      return str;
    }

    public String needSemi(String str) {
      if (str.charAt(str.length() - 1) == '}')
        return "";
      else
        return ";";
    }

    public String visit(Symbol symbol) {
      String var = symbol.getName();
      if (typesEnabled)
        var = symbol.getType() + " " + var;
      return var;
    }

    @Override
    public String visit(VarAccess name) {
      String str = name.getSymbol().getName();
      if (checker != null && checker.getType(name) != null)
        str = "/*" + checker.getType(name).toString() + "*/ " + str;
      return str;
    }

    @Override
    public String visit(VariableDeclaration variableDeclaration) {
      return visit(variableDeclaration.getSymbol());
    }
  }
}
