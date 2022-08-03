package crux.ast.types;

import crux.ast.SymbolTable.Symbol;
import crux.ast.*;
import crux.ast.traversal.NullNodeVisitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    System.out.println(n.getClass().toString());
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

    public Symbol currentFunctionSymbol;
    public boolean lastStatementReturns;
    //Helpful Class Variable

    @Override
    public Void visit(VarAccess vaccess) {
      setNodeType(vaccess, vaccess.getSymbol().getType());
      return null;
    }

    @Override
    public Void visit(ArrayDeclaration arrayDeclaration) {
      ArrayType myArrayType = (ArrayType) arrayDeclaration.getSymbol().getType();
      Type myBaseType = myArrayType.getBase();
      if(myBaseType.getClass() != BoolType.class && myBaseType.getClass() != IntType.class){
        //Type newType = new ErrorType("The Base class is a Void Type");
        //setNodeType(arrayDeclaration, newType);
        addTypeError(arrayDeclaration, "arrayDeclaration is void type");
      }
      else{ //TODO need this?
        setNodeType(arrayDeclaration, myArrayType);
      }
      lastStatementReturns = false;
      return null;
    }

    @Override
    public Void visit(Assignment assignment) {
      Expression myLocation = assignment.getLocation();
      myLocation.accept(this);
      Expression myValue = assignment.getValue();
      myValue.accept(this);
      setNodeType(assignment, getType(myLocation).assign(getType(myValue)));
      lastStatementReturns = false;
      return null;
    }

    @Override
    public Void visit(Break brk) {
      for(Node n : brk.getChildren()){
        n.accept(this);
      }
      lastStatementReturns = false;
      return null;
    }

    @Override
    public Void visit(Call call) {
      FuncType myFuncType = (FuncType) call.getCallee().getType();
      TypeList myCallArguments = new TypeList();
      if(call.getArguments() != null){
        myCallArguments = new TypeList();
        for (Expression args : call.getArguments()){
          args.accept(this);
          myCallArguments.append(getType(args));
        }
      }
      //System.out.println(myFuncType);
      //System.out.println(myFuncType.call(myCallArguments));
      //For Test
      setNodeType(call, myFuncType.call(myCallArguments));

      lastStatementReturns = false;
      return null;
    }

    @Override
    public Void visit(DeclarationList declarationList) {
        for(Node myDeclarations : declarationList.getChildren()) {
          myDeclarations.accept(this);
        }
        return null;
    }

    @Override
    public Void visit(FunctionDefinition functionDefinition) {
      Symbol functionSymbol = functionDefinition.getSymbol();
      currentFunctionSymbol = functionSymbol; //TODO
      FuncType retType = (FuncType)functionSymbol.getType();
      TypeList argTypes = retType.getArgs();
      FuncType myFuncType = (FuncType)functionDefinition.getSymbol().getType();
      List<Symbol> myArguments = functionDefinition.getParameters();
      for(Symbol arg : myArguments){
        boolean isBoolType = arg.getType().toString().equals("bool");
        boolean isIntType = arg.getType().toString().equals("int");
        boolean isVoidType = arg.getType().toString().equals("void");
        if(!(isBoolType || isIntType || isVoidType)){
          addTypeError(functionDefinition, "Arguments is Invalid Type in Function Definition.");
        }
      }

      List<Node> myChildren = functionDefinition.getStatements().getChildren();
      for(Node children : myChildren){
        children.accept(this);
        if(children instanceof Return){
          if(getType(children) == null){ //TODO is correct?
            addTypeError(children, String.format("Function %s return type is different.", functionDefinition));
            lastStatementReturns = false;
          }
          else if ((myFuncType != null) && !(getType(children).toString().equals(myFuncType.getRet().toString()))){
            addTypeError(children, String.format("Function %s return type is not same.", functionDefinition));
            lastStatementReturns = false;
          }
        }
      }
      if ((lastStatementReturns && myFuncType.getRet().toString().equals("void"))){   //TODO !lastStatementReturns
        addTypeError(functionDefinition, "Exists return when return type is not void using");
      }
      return null;
    }


    @Override
    public Void visit(IfElseBranch ifElseBranch) {
      ifElseBranch.getCondition().accept(this);
      if(getType(ifElseBranch.getCondition()).getClass() != BoolType.class){
        addTypeError(ifElseBranch, "IfElseBranch's condition has to be Bool type");
      }
      //else{
      ifElseBranch.getElseBlock().accept(this);
      ifElseBranch.getThenBlock().accept(this);
      //}
      if(ifElseBranch.getThenBlock().getType() == null ||
              ifElseBranch.getThenBlock().getType().toString().equals("void")) {
        lastStatementReturns = false;       //TODO ???
      }
      else{
        lastStatementReturns = true;
      }
      return null;
    }

    @Override
    public Void visit(ArrayAccess access) {
      if(access.getIndex() != null){
        access.getIndex().accept(this);
        Type myType = access.getBase().getType().index(getType(access.getIndex()));
        setNodeType(access, myType);
      }
      else{
        setNodeType(access, access.getBase().getType());
      }
      return null;
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
      forloop.getCond().accept(this);
      if(getType(forloop.getCond()).getClass() != BoolType.class){
        addTypeError(forloop, "For Loop's condition has to be Bool type");
      }
      else{
        forloop.getBody().accept(this);
        forloop.getIncrement().accept(this);
        forloop.getInit().accept(this);
      }
      return null;
    }

    @Override
    public Void visit(OpExpr op) {
      Expression lhs = null;
      if(op.getLeft() != null){
        lhs = op.getLeft();
        lhs.accept(this);
      }
      Expression rhs = null;
      if(op.getRight() != null){
        rhs = op.getRight();
        rhs.accept(this);
      }
      boolean compareCase = op.getOp().toString().equals(">=") || op.getOp().toString().equals("<=") ||
              op.getOp().toString().equals("!=") || op.getOp().toString().equals("==") ||
              op.getOp().toString().equals("<") || op.getOp().toString().equals(">");
      if(compareCase){
        setNodeType(op, getType(lhs).compare(getType(rhs)));
      }
      else if(op.getOp().toString().equals("+")){
        setNodeType(op, getType(lhs).add(getType(rhs)));
      }
      else if(op.getOp().toString().equals("-")){
        setNodeType(op, getType(lhs).sub(getType(rhs)));
      }
      else if(op.getOp().toString().equals("*")){
        setNodeType(op, getType(lhs).mul(getType(rhs)));
      }
      else if(op.getOp().toString().equals("/")){
        setNodeType(op, getType(lhs).div(getType(rhs)));
      }
      else if(op.getOp().toString().equals("&&")){
        setNodeType(op, getType(lhs).and(getType(rhs)));
      }
      else if(op.getOp().toString().equals("||")){
        setNodeType(op, getType(lhs).or(getType(rhs)));
      }
      else if(op.getOp().toString().equals("!")){
        setNodeType(op, getType(lhs).not());
      }
      else{
        addTypeError(op, "Invalid operation detected, please double check");
      }
      return null;
    }

    @Override
    public Void visit(Return ret) {
      ret.getValue().accept(this);
      if(getType(ret.getValue()).equivalent((FuncType)currentFunctionSymbol.getType())){
        addTypeError(ret, "Return type is Error");
      }
      else{
        setNodeType(ret, getType(ret.getValue()));
      }
      lastStatementReturns = true;  //True!
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
      if(variableDeclaration.getSymbol().getType().getClass() == IntType.class ||
              variableDeclaration.getSymbol().getType().getClass() == BoolType.class){
        //setNodeType(variableDeclaration, getType(variableDeclaration));
        //setNodeType(variableDeclaration, variableDeclaration.getSymbol().getType());
      }
      else {
        addTypeError(variableDeclaration, "variableDeclaration is Void type");
      }
      lastStatementReturns = false;
      return null;
    }
  }
}


