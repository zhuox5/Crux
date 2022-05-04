package crux.ir;

import crux.ast.SymbolTable.Symbol;
import crux.ast.*;
import crux.ast.OpExpr.Operation;
import crux.ast.traversal.NodeVisitor;
import crux.ast.types.*;
import crux.ir.insts.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

class InstPair {
}


/**
 * Convert AST to IR and build the CFG
 */
public final class ASTLower implements NodeVisitor<InstPair> {
  private Program mCurrentProgram = null;
  private Function mCurrentFunction = null;

  private Map<Symbol, LocalVar> mCurrentLocalVarMap = null;

  /**
   * A constructor to initialize member variables
   */
  public ASTLower() {}

  public Program lower(DeclarationList ast) {
    visit(ast);
    return mCurrentProgram;
  }

  @Override
  public InstPair visit(DeclarationList declarationList) {
    mCurrentProgram = new Program();
    mCurrentLocalVarMap = new HashMap<>();
    for (var children : declarationList.getChildren()){
      mCurrentFunction = null;
      children.accept(this);
    }
    return null;
  }

  /**
   * This visitor should create a Function instance for the functionDefinition node, add parameters
   * to the localVarMap, add the function to the program, and init the function start Instruction.
   */
  @Override
  public InstPair visit(FunctionDefinition functionDefinition) {
    return null;
  }

  @Override
  public InstPair visit(StatementList statementList) {
    for (var children : statementList.getChildren()){
      children.accept(this);
    }
    return null;
  }

  /**
   * Declarations, could be either local or Global
   */
  @Override
  public InstPair visit(VariableDeclaration variableDeclaration) {
    String myVariableName = variableDeclaration.getSymbol().getName();
    Type myVariableType = variableDeclaration.getSymbol().getType();
    if(mCurrentFunction == null){
      //TODO
      //var myAddrVar = new AddressVar(myVariableType, myVariableName);
      var myNumElement = IntegerConstant.get(mCurrentProgram, 1);
      var myGlobalDecl = new GlobalDecl(variableDeclaration.getSymbol(), myNumElement);
      mCurrentProgram.addGlobalVar(myGlobalDecl);
    }
    else{
      LocalVar myLocalvar = new LocalVar(myVariableType, myVariableName);
      mCurrentLocalVarMap.put(variableDeclaration.getSymbol(), myLocalvar);
    }
    return null;
  }

  /**
   * Create a declaration for array and connected it to the CFG
   */
  @Override
  public InstPair visit(ArrayDeclaration arrayDeclaration) { //only global
    ArrayType myArrayType = (ArrayType) arrayDeclaration.getSymbol().getType();
    var myNumElement = IntegerConstant.get(mCurrentProgram, 1);
    var myGlobalDecl = new GlobalDecl(arrayDeclaration.getSymbol(), myNumElement);
    mCurrentProgram.addGlobalVar(myGlobalDecl);
    return null;
  }

  /**
   * LookUp the name in the map(s). For globals, we should do a load to get the value to load into a
   * LocalVar.
   */
  @Override
  public InstPair visit(VarAccess name) {
    if(mCurrentLocalVarMap.get(name.getSymbol()) == null){    //if not local, then is global
      mCurrentLocalVarMap.put(name.getSymbol(), new LocalVar(name.getType()));
    }
    return null;
  }

  /**
   * If the location is a VarAccess to a LocalVar, copy the value to it. If the location is a
   * VarAccess to a global, store the value. If the location is ArrayAccess, store the value.
   */
  @Override
  public InstPair visit(Assignment assignment) {
    //Expression lhs = assignment.getLocation();
    Expression rhs = assignment.getValue();
    //lhs.accept(this); //lhs
    rhs.accept(this);//rhs
   if(assignment.getLocation() instanceof ArrayAccess){

   }
   else if(assignment.getLocation() instanceof VarAccess &&     //is VarAccess and is global
           (mCurrentLocalVarMap.get(assignment) == null)){
        //AddressAt
   }
   else{

   }
    return null;
  }

  /**
   * Lower a Call.
   */
  @Override
  public InstPair visit(Call call) {
    List<LocalVar> args = new ArrayList<LocalVar>();
    for(Expression exp : call.getArguments()){
      exp.accept(this);
      //args.add();
    }
    return null;
  }

  /**
   * Handle operations like arithmetics and comparisons. Also handle logical operations (and,
   * or, not).
   */
  @Override
  public InstPair visit(OpExpr operation) {

    return null;
  }

  private InstPair visit(Expression expression) {
    expression.accept(this);
    return null;
  }

  /**
   * It should compute the address into the array, do the load, and return the value in a LocalVar.
   */
  @Override
  public InstPair visit(ArrayAccess access) {
    ArrayType myArrayType = (ArrayType) access.getBase().getType();
    access.getIndex().accept(this);
    var dst = mCurrentFunction.getTempAddressVar(myArrayType.getBase());

    return null;
  }

  /**
   * Copy the literal into a tempVar
   */
  @Override
  public InstPair visit(LiteralBool literalBool) {
    return null;
  }

  /**
   * Copy the literal into a tempVar
   */
  @Override
  public InstPair visit(LiteralInt literalInt) {

    return null;
  }

  /**
   * Lower a Return.
   */
  @Override
  public InstPair visit(Return ret) {
    ret.getValue().accept(this);
    if(mCurrentProgram.getFunctions())
    return null;
  }


  /**
   * Break Node
   */
  @Override
  public InstPair visit(Break brk) {
    return null;
  }

  /**
   * Implement If Then Else statements.
   */
  @Override
  public InstPair visit(IfElseBranch ifElseBranch) {
    ifElseBranch.getCondition().accept(this);
    return null;
  }

  /**
   * Implement for loops.
   */
  @Override
  public InstPair visit(For loop) {
    loop.getCond().accept(this);
    return null;
  }
}
