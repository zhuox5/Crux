package crux.backend;

import crux.ast.SymbolTable.Symbol;
import crux.ast.VarAccess;
import crux.ir.*;
import crux.ir.insts.*;
import crux.printing.IRValueFormatter;

import java.util.*;

/**
 * Convert the CFG into Assembly Instructions
 */
public final class CodeGen extends InstVisitor {
  private final Program p;
  private final CodePrinter out;

  //private boolean intelMac = false;
  private HashMap<Variable, Integer> varIndexMap = new HashMap<>();
  private int numslots = 0;
  private HashMap<Instruction, String> myLableMap;


  private final IRValueFormatter irFormat = new IRValueFormatter();
  private void printInstructionInfor(Instruction i){
    var info = String.format("/* %s */", i.format(irFormat));
    out.bufferCode(info);
    out.printBuffer();
  }

  public CodeGen(Program p) {
    this.p = p;
    // Do not change the file name that is outputted or it will
    // break the grader!
    out = new CodePrinter("a.s");
  }

  /**
   * It should allocate space for globals call genCode for each Function
   */
  public void genCode() {
    //TODO
    for(Iterator<GlobalDecl> glob_it = p.getGlobals(); glob_it.hasNext(); ){
      GlobalDecl g = glob_it.next();
      String name = g.getSymbol().getName();
      var size = ((IntegerConstant) g.getNumElement()).getValue() * 8;
      out.printCode(".comm " + name + ", " + size + ", 8");
    }

    int count[] = new int[1];
    for(Iterator<Function> func_it = p.getFunctions(); func_it.hasNext(); ){
      Function f = func_it.next();
      genCode(f, count);
    }
    out.close();
  }

  private String getSymbol(Symbol var){
    return var.toString();
  }


  private void genCode(Function f, int count[]){
    //TODO
    myLableMap = f.assignLabels(count);
    List<LocalVar> myArgs = f.getArguments();
    numslots = 0;
    int argIndex = 1;
    for (LocalVar myLocalVar : myArgs) {
      if (argIndex == 1) {
        out.printCode("movq %rdi, -8(%rbp)");
      } else if (argIndex == 2) {
        out.printCode("movq %rsi, -16(%rbp)");
      } else if (argIndex == 3) {
        out.printCode("movq %rdx, -24(%rbp)");
      } else if (argIndex == 4) {
        out.printCode("movq %rcx, -32(%rbp)");
      } else if (argIndex == 5) {
        out.printCode("movq %r8, -40(%rbp)");
      } else if (argIndex == 6) {
        out.printCode("movq %r9, -48(%rbp)");
      } else {
        out.printCode("push " + myLocalVar.getName());
        //TODO
      }
      argIndex++;
    }

    /**
     * Generate Body
     **/
    numslots = f.getNumTempVars() + f.getNumTempAddressVars();
    if(numslots % 2 != 0){
      numslots++;           //if uneven
    }
    out.printCode(".globl " + f.getName());
    out.printLabel(f.getName() + ":");
    out.printCode("enter $(8 * " + numslots + "), $0");

    Stack<Instruction> tovisited = new Stack<>();
    HashSet<Instruction> discovered = new HashSet<>();
    tovisited.push(f.getStart());
    while(!tovisited.isEmpty()){
      Instruction inst = tovisited.pop();
      if(myLableMap.containsKey(inst)){
        out.printCode(myLableMap.get(inst) + ":");
      }
      else{
        inst.accept(this);
        Instruction first = inst.getNext(0);
        Instruction second = inst.getNext(1);
        if (second != null && !discovered.contains(second)){
          tovisited.push(second);
          discovered.add(second);
        }
        if (first != null) {
          if(!discovered.contains(first)){
            tovisited.push(first);
            discovered.add(first);
          }
          if((tovisited.isEmpty() || first != tovisited.peek())){
            out.printCode("jmp " + myLableMap.get(first));
          }
        }
        //else if (f.getName().equals("main")) {
        //  out.printCode("movq $0, %rax");
        //  out.printCode("leave");
        //  out.printCode("ret");
        //}
        else{
          out.printCode("leave");
          out.printCode("ret");
        }
      }
    }

  }


  public void visit(AddressAt i) {
    printInstructionInfor(i);
    Symbol src = i.getBase();
    int dst = 0;
    if(varIndexMap.containsKey(i.getDst())){
      dst = varIndexMap.get(i.getDst());
    }
    else{
      varIndexMap.put(i.getDst(), numLocalVar);
      numLocalVar++;
      dst = numLocalVar;
    }
    dst *= -8;

    int offset = 0;
    if(varIndexMap.containsKey(i.getOffset())){
      offset = varIndexMap.get(i.getOffset());
    }
    else{
      varIndexMap.put(i.getOffset(), numLocalVar);
      numLocalVar++;
      offset = numLocalVar;
    }
    offset *= -8;

    if(i.getOffset() == null){
      out.printCode("movq " + src.getName() + "@GOTPCREL(%rip), %r11"); //
      out.printCode("movq %r11, " + dst + " (%rbp)");
    }
    else{
      out.printCode("movq " + offset + "(%rbp), %r11");
      out.printCode("imulq $8, %r10");
      out.printCode("movq " + src.getName() + "@GOTPCREL(%rip), %r10"); //
      out.printCode("addq %r10, %r11");
      out.printCode("movq %r11, " + dst + "(%rbp)");
    }
  }

  private int numLocalVar = 1;


  public void visit(BinaryOperator i) {
    //printInstructionInfor(i);
    String op = i.getOperator().toString();
    //out.printCode(op);
    if(op.equals("Add") || op.equals("Sub") || op.equals("Mul") || op.equals("Div")){
      out.printCode("/* BinaryOperator: "+ op + " */");
    }
    else{
      op = "No such operator"; //safe checker, should not reach here
      out.printCode(op);
    }
    int left;
    int right;
    if(varIndexMap.containsKey(i.getLeftOperand())){
      left = varIndexMap.get(i.getLeftOperand());
    }
    else{
      varIndexMap.put(i.getLeftOperand(), numLocalVar);
      numLocalVar++;
      left = numLocalVar;
    }
    left *= -8;

    if(varIndexMap.containsKey(i.getRightOperand())){
      right = varIndexMap.get(i.getRightOperand());

    }
    else{
      varIndexMap.put(i.getRightOperand(), numLocalVar);
      numLocalVar++;
      right = numLocalVar;
    }
    right *= -8;
    int dst = 0;
    if(varIndexMap.containsKey(i.getDst())){
      dst = varIndexMap.get(i.getDst());
    }
    else{
      varIndexMap.put(i.getDst(), numLocalVar);
      numLocalVar++;
      dst = numLocalVar;
      //out.printCode("FUCK --- " + dst);
    }
    dst *= -8;

    if(op.equals("Add") || op.equals("Sub") || op.equals("Mul") || op.equals("Div")){
      if(op.equals("Add")){
        out.printCode("movq "+ left + "(%rbp), %r10");
        out.printCode("addq "+ right + "(%rbp), %r10");
        out.printCode("movq %r10, "+ dst + "(%rbp)");
      }
      else if(op.equals("Sub")){
        out.printCode("movq "+ left + "(%rbp), %r10");
        out.printCode("subq "+ right + "(%rbp), %r10");
        out.printCode("movq %r10, "+ dst + "(%rbp)");
      }
      else if(op.equals("Mul")){
        out.printCode("movq "+ left + "(%rbp), %r10");
        out.printCode("mulq "+ right + "(%rbp), %r10");
        out.printCode("movq %r10, "+ dst + "(%rbp)");
      }
      else if(op.equals("Div")){
        out.printCode("movq " + left + "(%rbp), %rax");
        out.printCode("cqto");
        out.printCode("idivq " + right + "(%rbp)");
        out.printCode("movq %rax, " + dst + "(%rbp)");
      }
    }

  }

  public void visit(CompareInst i) {
    //printInstructionInfor(i);
    out.printCode("/* BinaryOperator */ ");
    int left;
    int right;
    if(varIndexMap.containsKey(i.getLeftOperand())){
      left = varIndexMap.get(i.getLeftOperand());
    }
    else{
      varIndexMap.put(i.getLeftOperand(), numLocalVar);
      numLocalVar++;
      left = numLocalVar;
    }
    left *= -8;

    if(varIndexMap.containsKey(i.getRightOperand())){
      right = varIndexMap.get(i.getRightOperand());
    }
    else{
      varIndexMap.put(i.getRightOperand(), numLocalVar);
      numLocalVar++;
      right = numLocalVar;
    }
    right *= -8;
    out.printCode("movq " + left + "(%rbp), %r10");
    out.printCode("movq " + right + "(%rbp), %r11");
    out.printCode("cmp %r11, %r10");
    out.printCode("movq $0, %r11");
    out.printCode("movq $1, %r10");
    String myPredicate = "";
    if(i.getPredicate() == CompareInst.Predicate.GE){
      myPredicate = "ge";
    }
    else if(i.getPredicate() == CompareInst.Predicate.LE){
      myPredicate = "le";
    }
    else if(i.getPredicate() == CompareInst.Predicate.GT){
      myPredicate = "g";
    }
    else if(i.getPredicate() == CompareInst.Predicate.LT){
      myPredicate = "l";
    }
    else if(i.getPredicate() == CompareInst.Predicate.EQ){
      myPredicate = "e";
    }
    else if(i.getPredicate() == CompareInst.Predicate.NE){
      myPredicate = "ne";
    }
    out.printCode("cmov" + myPredicate + " %r10, %r11");

    int dst;
    if(varIndexMap.containsKey(i.getDst())){
      dst = varIndexMap.get(i.getDst());
    }
    else{
      varIndexMap.put(i.getDst(), numLocalVar);
      numLocalVar++;
      dst = numLocalVar;
    }
    dst *= -8;
    out.printCode("movq %r11, " + dst + "(%rbp)");

  }

  public void visit(CopyInst i) {
    printInstructionInfor(i);
    var srcval = i.getSrcValue();
    int dst = 0;

    if(varIndexMap.containsKey(i.getDstVar())){
      dst = varIndexMap.get(i.getDstVar());
      //out.printCode("FUCK  ----- 1");
    }
    else{
      varIndexMap.put(i.getDstVar(), numLocalVar);
      //dst = ++numLocalVar;
      //numLocalVar++;
      dst = numLocalVar;
      //out.printCode("FUCK  ----- 2 --- " + dst );
    }
    dst *= -8;
    if(srcval instanceof IntegerConstant){
      //TODO error
      out.printCode("movq " + "$" + ((IntegerConstant)srcval).getValue() + ", " + "%r10");
      out.printCode("movq %r10, " + dst + "(%rbp)");
      //out.printCode("movq " + "$" + ((IntegerConstant)srcval).getValue() + ", " + dst + "(%rbp)");
    }
    else if(srcval instanceof BooleanConstant){
      if(((BooleanConstant) srcval).getValue()){
        out.printCode("movq " + "$1" + ", " + dst + "(%rbp)");
      }
      else{
        out.printCode("movq " + "$0" + ", " + dst + "(%rbp)");
      }
    }
    else if(srcval instanceof AddressVar){

    }
    else if(srcval instanceof LocalVar){
      var myLocalvar = ((LocalVar) srcval);
      int src;
      if(varIndexMap.containsKey(myLocalvar)){
        src = varIndexMap.get(myLocalvar);
      }
      else{
        varIndexMap.put(myLocalvar, numLocalVar);
        numLocalVar++;
        src = numLocalVar;
      }
      src *= -8;
      out.printCode("movq " + src + "(%rbp), %r10");
      out.printCode("movq " + "%r10" + ", " + dst + "(%rbp)");
    }
  }

  public void visit(JumpInst i) {
    printInstructionInfor(i);
    int myPredicatePos = 0;
    if(varIndexMap.containsKey(i.getPredicate())){
      myPredicatePos = varIndexMap.get(i.getPredicate());
    }
    else{
      varIndexMap.put(i.getPredicate(), numLocalVar);
      numLocalVar++;
      myPredicatePos = numLocalVar;
    }
    myPredicatePos *= -8;
    out.bufferCode("movq " + myPredicatePos + "(%rbp), %r10");
    out.bufferCode("movq $1, %r11");
    out.bufferCode("cmp %r10, %r11");
    out.bufferCode("je " + myLableMap.get(i.getNext(1)));
  }

  public void visit(LoadInst i) {
    printInstructionInfor(i);
    int src = 0;
    int dst = 0;

    if(varIndexMap.containsKey(i.getSrcAddress())){
      src = varIndexMap.get(i.getSrcAddress());
    }
    else{
      varIndexMap.put(i.getSrcAddress(), numLocalVar);
      numLocalVar++;
      src = numLocalVar;
    }
    src *= -8;
    if(varIndexMap.containsKey(i.getDst())){
      dst = varIndexMap.get(i.getDst());
    }
    else{
      varIndexMap.put(i.getDst(), numLocalVar);
      numLocalVar++;
      dst = numLocalVar;
    }
    dst *= -8;
    out.printCode("movq " + src + "(%rbp), %r10");
    out.printCode("movq (%r10), %r11");
    out.printCode("movq %r11, " + dst + "(%rbp)");

  }

  public void visit(NopInst i) {
    //printInstructionInfor(i);
  }

  public void visit(StoreInst i) {
    printInstructionInfor(i);
    int src = 0;
    int dst = 0;

    if(varIndexMap.containsKey(i.getSrcValue())){
      src = varIndexMap.get(i.getSrcValue());
    }
    else{
      varIndexMap.put(i.getSrcValue(), numLocalVar);
      numLocalVar++;
      src = numLocalVar;
    }
    src *= -8;
    if(varIndexMap.containsKey(i.getDestAddress())){
      dst = varIndexMap.get(i.getDestAddress());
    }
    else{
      varIndexMap.put(i.getDestAddress(), numLocalVar);
      numLocalVar++;
      dst = numLocalVar;
    }
    dst *= -8;
    out.printCode("movq " + dst + "(%rbp), %r10");
    out.printCode("movq " + src + "(%rbp), %r11");
    out.printCode("movq %r11, (%r10)");
  }

  public void visit(ReturnInst i) {
    printInstructionInfor(i);
    int ret = 0;
    if(varIndexMap.containsKey(i.getReturnValue())){
      ret = varIndexMap.get(i.getReturnValue());
    }
    else{
      varIndexMap.put(i.getReturnValue(), numLocalVar);
      numLocalVar++;
      ret = numLocalVar;
    }
    ret *= -8;
    out.bufferCode("movq " + ret + "(%rbp), %rax");
    out.bufferCode("leave");
    out.bufferCode("ret");
  }

  public void visit(CallInst i) {
    printInstructionInfor(i);
    for(int j=0; j<i.getParams().size(); j++){
      var param = i.getParams().get(j);
      //out.printCode("dddddd: ---- " + param);
      int pos = 0;

      if(varIndexMap.containsKey(param)){
        varIndexMap.put(param, numLocalVar++); //TODO may some error here !!!
        pos = varIndexMap.get(param);
      }
      else{
        varIndexMap.put(param, numLocalVar);
        numLocalVar++;
        pos = numLocalVar;
      }
      pos *= -8;


      if(j==0){
        out.printCode("movq " + pos + "(%rbp), %rdi");   //TODO error
      }
      else if(j==1){
        out.printCode("movq " + pos + "(%rbp), %rsi");
      }
      else if(j==2){
        out.printCode("movq " + pos + "(%rbp), %rdx");
      }
      else if(j==3){
        out.printCode("movq " + pos + "(%rbp), %rcx");
      }
      else if(j==4){
        out.printCode("movq " + pos + "(%rbp), %r8");
      }
      else if(j==5){
        out.printCode("movq " + pos + "(%rbp), %r9");
      }
      else{
        //out.printCode("Overflow");
        //will not reach this line
      }
    }
    out.printCode("call " + i.getCallee().getName());
    if (i.getParams().size() > 6){
      for (int index = i.getParams().size() - 1; index > 5; index--){
        var par = i.getParams().get(index);
        int stackPos = 0;
        if(varIndexMap.containsKey(par)){
          stackPos = varIndexMap.get(par);
        }
        else{
          varIndexMap.put(par, numLocalVar);
          numLocalVar++;
          stackPos = numLocalVar;
        }
        stackPos *= -8;
        out.printCode("movq " + stackPos + "(%rbp), %r10");
        out.printCode("movq %r10, " + (numLocalVar)*(-8) + "(%rbp)"); //modified here
      }
    }
  }

  public void visit(UnaryNotInst i) {
    printInstructionInfor(i);
    int dst = 0;
    int inner = 0;

    if(varIndexMap.containsKey(i.getDst())){
      dst = varIndexMap.get(i.getDst());
    }
    else{
      varIndexMap.put(i.getDst(), numLocalVar);
      numLocalVar++;
      dst = numLocalVar;
    }
    dst *= -8;

    if(varIndexMap.containsKey(i.getInner())){
      inner = varIndexMap.get(i.getInner());
    }
    else{
      varIndexMap.put(i.getInner(), numLocalVar);
      numLocalVar++;
      inner = numLocalVar;
    }
    inner *= -8;
    out.printCode("movq " + inner + "(%rbp), %r10");
    out.printCode("not %r10");
    out.printCode("movq %r10, " + dst + "(%rbp)");
  }
}
