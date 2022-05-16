package crux.ir;

import crux.ast.SymbolTable.Symbol;
import crux.ast.*;
import crux.ast.OpExpr.Operation;
import crux.ast.traversal.NodeVisitor;
import crux.ast.types.*;
import crux.ir.insts.*;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.Stack;

class InstPair {
  Instruction start;
  Instruction end;
  LocalVar value;

  public InstPair(Instruction s, Instruction e, LocalVar v){
    start = s;
    end = e;
    value = v;
  }

  public InstPair(Instruction s, Instruction e){
    start = s;
    end = e;
    value = null;
  }

  public InstPair(Instruction s, LocalVar v){
    start = s;
    end = s;
    value = v;
  }

  public InstPair(Instruction a){
    start = a;
    end = a;
    value = null;
  }


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
    for (var children : declarationList.getChildren()){
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
    mCurrentFunction = new Function(functionDefinition.getSymbol().getName(),
            (FuncType) functionDefinition.getSymbol().getType());
    mCurrentLocalVarMap = new HashMap<Symbol, LocalVar>();
    List<LocalVar> myArgs = new ArrayList<LocalVar>();
    for(var children : functionDefinition.getParameters()){
      LocalVar tempVar = mCurrentFunction.getTempVar(children.getType());
      mCurrentLocalVarMap.put(children, tempVar);
      myArgs.add(tempVar);
    }
    mCurrentFunction.setArguments(myArgs);
    mCurrentProgram.addFunction(mCurrentFunction);
    mCurrentFunction.setStart(functionDefinition.getStatements().accept(this).start);
    mCurrentFunction = null;
    mCurrentLocalVarMap = null;
    return null;
  }

  @Override
  public InstPair visit(StatementList statementList) {
    NopInst head = new NopInst();           //statmentlist is empty, juts nop
    if(statementList.getChildren().size() == 0){
      return new InstPair(head);
    }
    Instruction tempInst = head;
    for (var children : statementList.getChildren()){
      var temp = children.accept(this);
      //System.out.println("Begin ---------- ");
      //System.out.println(tempInst);
      //System.out.println("End ----------- ");
      //System.out.println();
      tempInst.setNext(0, temp.start);
      //TODO some error here, i tried to add a null checker for temp
      tempInst = temp.end;
    }
    return new InstPair(head, tempInst);   //start and end
  }

  /**
   * Declarations, could be either local or Global
   */
  @Override
  public InstPair visit(VariableDeclaration variableDeclaration) {
    Type myVariableType = variableDeclaration.getSymbol().getType();
    if(mCurrentFunction == null){                                 // is a global variable
      var myNumElement = IntegerConstant.get(mCurrentProgram, 1);
      var myGlobalDecl = new GlobalDecl(variableDeclaration.getSymbol(), myNumElement);
      mCurrentProgram.addGlobalVar(myGlobalDecl);
    }
    else{                                                         // is a local variable
      LocalVar myLocalvar = mCurrentFunction.getTempVar(myVariableType);
      mCurrentLocalVarMap.put(variableDeclaration.getSymbol(), myLocalvar);
    }
    return new InstPair(new NopInst()); //return a InstPair of NopInst()
  }

  /**
   * Create a declaration for array and connected it to the CFG
   */
  @Override
  public InstPair visit(ArrayDeclaration arrayDeclaration) {      //only global
    ArrayType myArrayType = (ArrayType) arrayDeclaration.getSymbol().getType();
    var myNumElement = IntegerConstant.get(mCurrentProgram, myArrayType.getExtent());
    var myGlobalDecl = new GlobalDecl(arrayDeclaration.getSymbol(), myNumElement);
    mCurrentProgram.addGlobalVar(myGlobalDecl);
    //System.out.println(myNumElement.getValue());
    return null;
  }

  /**
   * LookUp the name in the map(s). For globals, we should do a load to get the value to load into a
   * LocalVar.
   */
  @Override
  public InstPair visit(VarAccess name) {
    if(mCurrentLocalVarMap.get(name.getSymbol()) == null){    //if not local, then is global
      mCurrentLocalVarMap.put(name.getSymbol(), mCurrentFunction.getTempVar(name.getSymbol().getType()));
      AddressVar myDstVar =  mCurrentFunction.getTempAddressVar(name.getSymbol().getType());
      AddressAt addrAtInst = new AddressAt(myDstVar, name.getSymbol());
      //System.out.println("AddressAt is : ---");
      //System.out.println(addrAtInst);
      var temp = mCurrentFunction.getTempVar(name.getType());
      LoadInst myLoadInst = new LoadInst(temp, myDstVar);
      addrAtInst.setNext(0, myLoadInst);
      return new InstPair(addrAtInst, myLoadInst, temp);
      //load is like getting the value to an address, such as x+1;
    }
    else{
      AddressVar myDstVar =  mCurrentFunction.getTempAddressVar(name.getSymbol().getType());
      //AddressAt addrAtInst = new AddressAt(myDstVar, name.getSymbol());
      LocalVar myLocalVar = mCurrentLocalVarMap.get(name.getSymbol());
      return new InstPair(new NopInst(), myLocalVar);
    }
  }

  /**
   * If the location is a VarAccess to a LocalVar, copy the value to it. If the location is a
   * VarAccess to a global, store the value. If the location is ArrayAccess, store the value.
   */
  @Override
  public InstPair visit(Assignment assignment) {
    Expression lhs = assignment.getLocation();
    Expression rhs = assignment.getValue();
    boolean isArrayAccess = assignment.getLocation() instanceof ArrayAccess;

    if (isArrayAccess){
      Symbol sym = ((ArrayAccess)assignment.getLocation()).getBase();   //ArrayAccess
      InstPair myIndex = ((ArrayAccess) assignment.getLocation()).getIndex().accept(this);
      AddressVar myDestAddr = mCurrentFunction.getTempAddressVar(assignment.getType());
      InstPair i = rhs.accept(this); //rhs
      LocalVar mySrcVal = i.value;
      StoreInst storeInst = new StoreInst(mySrcVal, myDestAddr);
      AddressAt myAddressAt = new AddressAt(myDestAddr, sym, myIndex.value);
      System.out.println("---------AddressVar Assignment ------");
      System.out.println(myDestAddr);
      System.out.println(mySrcVal);
      System.out.println(myDestAddr);
      System.out.println(myIndex.value);
      myIndex.end.setNext(0, i.start);
      i.end.setNext(0, myAddressAt);
      myAddressAt.setNext(0, storeInst);
      return new InstPair(myIndex.start, storeInst);
    }

    Symbol mySymbol = ((VarAccess)assignment.getLocation()).getSymbol(); //VarAccess
    //var s = (VarAccess) assignment.getLocation();
    boolean isGlobalVarAccess = mCurrentLocalVarMap.get(mySymbol) == null; //(assignment.getLocation() instanceof VarAccess) &&
    if (isGlobalVarAccess){
      InstPair i = rhs.accept(this); //rhs
      LocalVar mySrcVal = i.value;
      AddressVar myDestAddr = mCurrentFunction.getTempAddressVar(((VarAccess)assignment.getLocation()).getType());
      System.out.println("Assignment ------------------------");
      System.out.println(i.start);
      System.out.println(mySrcVal);
      System.out.println(myDestAddr);
      StoreInst storeInst = new StoreInst(mySrcVal, myDestAddr);
      Symbol sym = ((VarAccess)assignment.getLocation()).getSymbol(); //VarAccess
      AddressAt myAddressAt = new AddressAt(myDestAddr, sym);
      myAddressAt.setNext(0, i.start);
      i.end.setNext(0, storeInst);
      return new InstPair(myAddressAt, storeInst);
    }
    else{
      LocalVar mySrcVal = mCurrentLocalVarMap.get(mySymbol);
      InstPair i = rhs.accept(this); //rhs
      CopyInst copyInst = new CopyInst(mySrcVal, i.value);
      i.end.setNext(0, copyInst);
      return new InstPair(i.start, copyInst);
    }
  }

  /**
   * Lower a Call.
   */
  @Override
  public InstPair visit(Call call) {
    NopInst head = new NopInst();
    List<LocalVar> args = new ArrayList<LocalVar>();
    int counter = 0;
    Instruction tempInst = head;
    Instruction begin = head;
    for(Expression exp : call.getArguments()){
      var temp = exp.accept(this);
      args.add(temp.value);
      if(counter == 0){
        head.setNext(0, temp.start);
        begin = temp.start;
        tempInst = temp.end;
      }
      else{
        tempInst.setNext(0, temp.start);
        tempInst = temp.end;
      }
      counter++;
    }

    CallInst myCallInst;
    FuncType myFuncType = (FuncType)call.getCallee().getType();
    if (myFuncType.getRet() instanceof VoidType){
      /*
      //System.out.println("check begin ---");
      //System.out.println(call.getCallee());
      //System.out.println(args);
      //System.out.println("check ends ---");

       */
      myCallInst = new CallInst(call.getCallee(), args);
    } else {
      LocalVar dst = mCurrentFunction.getTempVar(myFuncType.getRet());
      myCallInst = new CallInst(dst, call.getCallee(), args);
    }
    //TODO Error: tempInst is null :((
    tempInst.setNext(0, myCallInst);
    return new InstPair(head, myCallInst); //start and end
  }


  /**
   * Handle operations like arithmetics and comparisons. Also handle logical operations (and,
   * or, not).
   */
  @Override
  public InstPair visit(OpExpr operation) {
    InstPair lhs;
    InstPair rhs;
    if(operation.getRight() != null){
      lhs = operation.getLeft().accept(this);
      rhs = operation.getRight().accept(this);
    }
    else {
      lhs = operation.getLeft().accept(this);
      LocalVar myDstVar = mCurrentFunction.getTempVar(new BoolType());
      UnaryNotInst myUnaryNotInst = new UnaryNotInst(myDstVar, mCurrentFunction.getTempVar(operation.getType()));
      lhs.end.setNext(0, myUnaryNotInst);
      return new InstPair(lhs.start, myUnaryNotInst, myDstVar);
    }

    CompareInst.Predicate myComparePredicate = null;
    BinaryOperator.Op myBinaryOp = null;
    String myBoolOp = null;
    if(operation.getOp().toString().equals("==")){
      myComparePredicate = CompareInst.Predicate.EQ;
    }else if(operation.getOp().toString().equals("!=")){
      myComparePredicate = CompareInst.Predicate.NE;
    }else if(operation.getOp().toString().equals(">")){
      myComparePredicate = CompareInst.Predicate.GT;
    }else if(operation.getOp().toString().equals("<")){
      myComparePredicate = CompareInst.Predicate.LT;
    }else if(operation.getOp().toString().equals(">=")){
      myComparePredicate = CompareInst.Predicate.GE;
    }else if(operation.getOp().toString().equals("<=")){
      myComparePredicate = CompareInst.Predicate.LE;
    }else if(operation.getOp().toString().equals("+")){
      myBinaryOp = BinaryOperator.Op.Add;
    }else if(operation.getOp().toString().equals("-")){
      myBinaryOp = BinaryOperator.Op.Sub;
    }else if(operation.getOp().toString().equals("*")){
      myBinaryOp = BinaryOperator.Op.Mul;
    }else if(operation.getOp().toString().equals("/")){
      myBinaryOp = BinaryOperator.Op.Div;
    }else if(operation.getOp().toString().equals("&&")){
      myBoolOp = "&&";
    }else if(operation.getOp().toString().equals("||")){
      myBoolOp = "||";
    }
    if(myComparePredicate != null){
      LocalVar myLocalVar = mCurrentFunction.getTempVar(new BoolType());
      CompareInst myCompareInst = new CompareInst(myLocalVar, myComparePredicate,
              lhs.value,
              rhs.value);
      lhs.end.setNext(0, rhs.start);
      rhs.end.setNext(0, myCompareInst);
      return new InstPair(lhs.start, myCompareInst, myLocalVar);
    }
    else if(myBinaryOp != null){
      LocalVar myLocalVar = mCurrentFunction.getTempVar(new IntType());
      BinaryOperator myOpInst = new BinaryOperator(myBinaryOp, myLocalVar,
              lhs.value,
              rhs.value);
      lhs.end.setNext(0, rhs.start);
      rhs.end.setNext(0, myOpInst);
      return new InstPair(lhs.start, myOpInst, myLocalVar);
    }
    else if(myBoolOp.equals("&&") || myBoolOp.equals("||")){
      LocalVar myLocalVar = mCurrentFunction.getTempVar(new BoolType());
      JumpInst myJump;

      NopInst myMergeInst = new NopInst();
      NopInst myThenBranch = new NopInst();
      if(myBoolOp.equals("&&")){
        myJump = new JumpInst(myLocalVar);
        lhs.end.setNext(0, myJump);
        CopyInst myCopyInst = new CopyInst(myLocalVar, lhs.value);
        myJump.setNext(0, myCopyInst);
        myCopyInst.setNext(0, myMergeInst);
        myJump.setNext(1, myThenBranch);
        myThenBranch.setNext(0, myMergeInst);
        CopyInst myLastInst = new CopyInst(myLocalVar, rhs.value);
        myThenBranch.setNext(0, myLastInst);
        return new InstPair(myCopyInst, myCopyInst, myLocalVar); //TODO
      }
      else{
        //var t = mCurrentFunction.getTempVar(operation.getType());
        System.out.println(lhs.value); //TODO error lhs's value is null

        myJump = new JumpInst(lhs.value);
        lhs.end.setNext(0, myJump);
        CopyInst myCopyInst = new CopyInst(myLocalVar, rhs.value); //rhs here
        myCopyInst.setNext(0, myMergeInst);
        myJump.setNext(1, myThenBranch);
        myThenBranch.setNext(0, new CopyInst(myLocalVar, lhs.value));
        return new InstPair(myCopyInst, myCopyInst, myLocalVar); //TODO
      }

    }
    return null;
  }

  private InstPair visit(Expression expression) {
    return expression.accept(this);
    //return null;
  }

  /**
   * It should compute the address into the array, do the load, and return the value in a LocalVar.
   */
  @Override
  public InstPair visit(ArrayAccess access) {
    ArrayType myArrayType = (ArrayType) access.getBase().getType();
    InstPair myInstPair = access.getIndex().accept(this);
    System.out.println("ArrayAccess test !!! ------------ ");
    System.out.println();
    //AddressVar myDstVar = mCurrentFunction.getTempAddressVar(access.getBase().getType());
    AddressVar myDstVar = mCurrentFunction.getTempAddressVar(access.getType());

    var temp = mCurrentFunction.getTempVar(access.getType()); //TODO some error here
    AddressAt addrAtInst = new AddressAt(myDstVar, access.getBase(), myInstPair.value);
    myInstPair.end.setNext(0, addrAtInst);
    LoadInst myLoadInst = new LoadInst(temp, myDstVar);
    System.out.println("check begin ------------");
    System.out.println(myInstPair.value);
    System.out.println(myDstVar);
    System.out.println(temp);
    System.out.println("check end ------------");
    addrAtInst.setNext(0, myLoadInst);
    return new InstPair(myInstPair.start, myLoadInst, temp);
  }

  /**
   * Copy the literal into a tempVar
   */
  @Override
  public InstPair visit(LiteralBool literalBool) {
    BooleanConstant myBoolVal = BooleanConstant.get(mCurrentProgram, literalBool.getValue());
    var dest = mCurrentFunction.getTempVar(new BoolType());
    var copyInst = new CopyInst(dest, myBoolVal);
    return new InstPair(copyInst, copyInst, dest);
  }

  /**
   * Copy the literal into a tempVar
   */
  @Override
  public InstPair visit(LiteralInt literalInt) {
    System.out.println("---------------------- LiteralInt ----------------------");
    System.out.println(literalInt.getValue());
    IntegerConstant myIntVal = IntegerConstant.get(mCurrentProgram, literalInt.getValue());
    var dest = mCurrentFunction.getTempVar(new IntType());
    var copyInst = new CopyInst(dest, myIntVal);
    return new InstPair(copyInst, copyInst, dest);
  }

  /**
   * Lower a Return.
   */
  @Override
  public InstPair visit(Return ret) {
    LocalVar retValue = mCurrentFunction.getTempVar(ret.getType());
    //ret.accept(this);
    var temp = ret.getValue().accept(this);
    ReturnInst myRetInst = new ReturnInst(retValue);
    temp.end.setNext(0, myRetInst);
    return new InstPair(temp.start, myRetInst);
  }


  /**
   * Break Node
   */
  @Override
  public InstPair visit(Break brk) {
    var myLoopExit = record.pop();
    List<Node> brkChildren = brk.getChildren();
    for(Node e : brkChildren){
      e.accept(this);  //TODO ?
    }
    //record.pop();
    return new InstPair(myLoopExit, new NopInst());
  }

  /**
   * Implement If Then Else statements.
   */

  @Override
  public InstPair visit(IfElseBranch ifElseBranch) {
    var ifElse = ifElseBranch.getCondition().accept(this);
    InstPair init = new InstPair(ifElse.start); //TODO start?
    JumpInst myJumpInst = new JumpInst(ifElse.value);
    init.end.setNext(0, myJumpInst);
    InstPair myElse = new InstPair(new NopInst());
    if(ifElseBranch.getElseBlock() != null){
      myElse = ifElseBranch.getElseBlock().accept(this);
    }
    var myThen = ifElseBranch.getThenBlock().accept(this);
    myJumpInst.setNext(0, myElse.start);
    myJumpInst.setNext(1, myThen.start);
    NopInst myExit = new NopInst();
    //System.out.println(myExit);
    //System.out.println(myElse.end);
    //System.out.println("endd");
    if(myElse.end != null){
      myElse.end.setNext(0, myExit);
    }
    myThen.end.setNext(0, myExit);
    return new InstPair(init.start, myExit);
    //return null;
  }

  /**
   * Implement for loops.
   */
  public Stack<Instruction> record = new Stack<Instruction>();
  public int rrr = 9;
  @Override
  public InstPair visit(For loop) {
    NopInst myBegin = new NopInst();
    NopInst myExit = new NopInst();
    //System.out.println("Record is : -------------------------");
    //System.out.println(record);

    record.push(myExit);
    var myInit = loop.getInit().accept(this);
    var myCond = loop.getCond().accept(this);
    var myBody = loop.getBody().accept(this);
    var myIncre = loop.getIncrement().accept(this);
    //InstPair init = new InstPair(new NopInst());
    myInit.end.setNext(0, myCond.start);
    JumpInst myJump = new JumpInst(myCond.value); //TODO correct?
    myCond.end.setNext(0, myJump);
    myJump.setNext(0, myExit);
    myJump.setNext(1, myBody.start);
    myBody.end.setNext(0, myIncre.start);
    myIncre.end.setNext(0, myCond.start);
    //record.pop(); //what is the purpose of this?
    return new InstPair(myInit.start, myExit);
  }
}
