package crux.ast.types;

import crux.ast.SymbolTable.Symbol;
import crux.ast.*;
import crux.ast.traversal.NullNodeVisitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This class will associate types with the AST nodes from Stage 2
 */
public final class TypeChecker {
  private final ArrayList<String> errors = new ArrayList<>();

  public ArrayList<String> getErrors() {
    return errors;
  }

  public void check(DeclarationList ast) {
    var inferenceVisitor = new TypeInferenceVisitor();
    inferenceVisitor.visit(ast);
  }

  /**
   * Helper function, should be used to add error into the errors array
   */
  private void addTypeError(Node n, String message) {
    errors.add(String.format("TypeError%s[%s]", n.getPosition(), message));
  }

  /**
   * Helper function, should be used to record Types if the Type is an ErrorType then it will call
   * addTypeError
   */
  private void setNodeType(Node n, Type ty) {
    ((BaseNode) n).setType(ty);
    if (ty.getClass() == ErrorType.class) {
      var error = (ErrorType) ty;
      addTypeError(n, error.getMessage());
    }
  }

  /**
   * Helper to retrieve Type from the map
   */
  public Type getType(Node n) {
    return ((BaseNode) n).getType();
  }


  /**
   * This calls will visit each AST node and try to resolve it's type with the help of the
   * symbolTable.
   */
  private final class TypeInferenceVisitor extends NullNodeVisitor<Void> {
    @Override
    public Void visit(VarAccess vaccess) {
      setNodeType(vaccess, vaccess.getSymbol().getType());
      return null;
      //TODO
    }

    @Override
    public Void visit(ArrayDeclaration arrayDeclaration) {
      return null;
    }

    @Override
    public Void visit(Assignment assignment) {
      Expression myLocation = assignment.getLocation();
      myLocation.accept(this);
      Expression myValue = assignment.getValue();
      myValue.accept(this);
      setNodeType(assignment, getType(myLocation).assign(getType(myValue)));
      return null;
    }

    @Override
    public Void visit(Break brk) {
      return null;
    }

    @Override
    public Void visit(Call call) {
      return null;
    }

    @Override
    public Void visit(DeclarationList declarationList) {
      for(Node myDeclarations : declarationList.getChildren()){
        myDeclarations.accept(this);
      }
      return null;
    }

    @Override
    public Void visit(FunctionDefinition functionDefinition) {
      return null;
    }

    @Override
    public Void visit(IfElseBranch ifElseBranch) {
      ifElseBranch.getCondition().accept(this);
      return null;
    }

    @Override
    public Void visit(ArrayAccess access) {
      access.getIndex().accept(this);
      setNodeType(access, access.getBase().getType());
      return null;
      //TODO
    }

    @Override
    public Void visit(LiteralBool literalBool) {
      setNodeType(literalBool, new BoolType());
      return null;
    }

    @Override
    public Void visit(LiteralInt literalInt) {
      setNodeType(literalInt, new IntType());
      return null;
    }

    @Override
    public Void visit(For forloop) {
      return null;
    }

    @Override
    public Void visit(OpExpr op) {
      return null;
    }

    @Override
    public Void visit(Return ret) {
      return null;
    }

    @Override
    public Void visit(StatementList statementList) {
      for(Node myStatement : statementList.getChildren()){
        myStatement.accept(this);
      }
      return null;
    }

    @Override
    public Void visit(VariableDeclaration variableDeclaration) {

      return null;
    }
  }
}
