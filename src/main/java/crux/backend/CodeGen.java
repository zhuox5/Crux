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
  private int numLocalVar = 1;
  //private boolean intelMac = false;
  private HashMap<Variable, Integer> varIndexMap = new HashMap<>();
  private int numslots = 0;
  private HashMap<Instruction, String> myLableMap;

  private int getPositionRBP(Variable v){
    int output = 0;
    if(varIndexMap.containsKey(v)){
      output = varIndexMap.get(v);
    }
    else{
      varIndexMap.put(v, numLocalVar);
      output = numLocalVar;
      numLocalVar++;
    }
    output *= -8;
    return output;
  }

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
        int overflow = (argIndex - 3) * 8;
        out.printCode("movq " + overflow + "(%rbp), %r10");
        out.printCode("movq %r10, " + argIndex * (-8) + "(%rbp)");
      }
      argIndex++;
    }



    Stack<Instruction> tovisited = new Stack<>();
    HashSet<Instruction> discovered = new HashSet<>();
    tovisited.push(f.getStart());
    while(!tovisited.isEmpty()){
      Instruction inst = tovisited.pop();
      if(myLableMap.containsKey(inst)){
        out.printLabel(myLableMap.get(inst) + ":");
      }
      inst.accept(this);    //still need to visit
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
      else{
        out.printCode("leave");
        out.printCode("ret");
      }
    }
  }

  public void visit(AddressAt i) {
    printInstructionInfor(i);
    Symbol src = i.getBase();
    int dst = getPositionRBP(i.getDst());
    int offset = getPositionRBP(i.getOffset());
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
    int left = getPositionRBP(i.getLeftOperand());
    int right = getPositionRBP(i.getRightOperand());
    int dst = getPositionRBP(i.getDst());

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
        out.printCode("imulq "+ right + "(%rbp), %r10");
        out.printCode("movq %r10, "+ dst + "(%rbp)");
      }
      else{
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
    int left = getPositionRBP(i.getLeftOperand());
    int right = getPositionRBP(i.getRightOperand());
    out.printCode("movq $0, %rax");
    out.printCode("movq $1, %r10");
    out.printCode("movq " + left + "(%rbp), %r11");
    out.printCode("cmp " + right + ", %r11");

    if(i.getPredicate() == CompareInst.Predicate.GE){
      out.printCode("cmovge %r10, %rax");
    }
    else if(i.getPredicate() == CompareInst.Predicate.LE){
      out.printCode("cmovle %r10, %rax");
    }
    else if(i.getPredicate() == CompareInst.Predicate.GT){
      out.printCode("cmovg %r10, %rax");
    }
    else if(i.getPredicate() == CompareInst.Predicate.LT){
      out.printCode("cmovl %r10, %rax");
    }
    else if(i.getPredicate() == CompareInst.Predicate.EQ){
      out.printCode("cmove %r10, %rax");
    }
    else if(i.getPredicate() == CompareInst.Predicate.NE){
      out.printCode("cmovne %r10, %rax");
    }

    int dst = getPositionRBP(i.getDst());
    out.printCode("movq %rax, " + dst + "(%rbp)");

  }

  public void visit(CopyInst i) {
    printInstructionInfor(i);
    var srcval = i.getSrcValue();
    int dst = getPositionRBP(i.getDstVar());

    if(srcval instanceof IntegerConstant){
      out.printCode("movq " + "$" + ((IntegerConstant)srcval).getValue() + ", " + "%r10");
      out.printCode("movq %r10, " + dst + "(%rbp)");
    }
    else if(srcval instanceof BooleanConstant){
      if(((BooleanConstant) srcval).getValue()){
        out.printCode("movq " + "$1" + ", " + dst + "(%rbp)");
      }
      else{
        out.printCode("movq " + "$0" + ", " + dst + "(%rbp)");
      }
    }
    else if(srcval instanceof LocalVar){
      var myLocalvar = ((LocalVar) srcval);
      int src = getPositionRBP(myLocalvar);
      out.printCode("movq " + src + "(%rbp), %r10");
      out.printCode("movq " + "%r10" + ", " + dst + "(%rbp)");
    }
  }

  public void visit(JumpInst i) {
    printInstructionInfor(i);
    int myPredicatePos = getPositionRBP(i.getPredicate());
    out.printCode("movq " + myPredicatePos + "(%rbp), %r10");
    out.printCode("cmp $1, %r10");
    out.printCode("je " + myLableMap.get(i.getNext(1)));
  }

  public void visit(LoadInst i) {
    printInstructionInfor(i);
    int src = getPositionRBP(i.getSrcAddress());
    int dst = getPositionRBP(i.getDst());
    out.printCode("movq " + src + "(%rbp), %r10");
    out.printCode("movq (%r10), %r11");
    out.printCode("movq %r11, " + dst + "(%rbp)");

  }

  public void visit(NopInst i) {
    //printInstructionInfor(i);
  }

  public void visit(StoreInst i) {
    printInstructionInfor(i);
    int src = getPositionRBP(i.getSrcValue());
    int dst = getPositionRBP(i.getDestAddress());
    out.printCode("movq " + dst + "(%rbp), %r10");
    out.printCode("movq " + src + "(%rbp), %r11");
    out.printCode("movq %r11, (%r10)");
  }

  public void visit(ReturnInst i) {
    printInstructionInfor(i);
    int ret = getPositionRBP(i.getReturnValue());
    out.printCode("movq " + ret + "(%rbp), %rax");
    out.printCode("leave");
    out.printCode("ret");
  }

  public void visit(CallInst i) {
    printInstructionInfor(i);
    for(int j=0; j<i.getParams().size(); j++){
      var param = i.getParams().get(j);
      int pos = getPositionRBP(param);

      if(j==0){
        out.printCode("movq " + pos + "(%rbp), %rdi");
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

    if (i.getParams().size() > 6){
      for (int index = i.getParams().size() - 1; index > 5; index--){
        var par = i.getParams().get(index);
        int stackPos = getPositionRBP(par);
        out.printCode("movq " + stackPos + "(%rbp), %r10");
        out.printCode("movq %r10, " + (numLocalVar)*(-8) + "(%rbp)"); //modified here
      }
    }
    out.printCode("call " + i.getCallee().getName());
    int dst = getPositionRBP(i.getDst());
    out.printCode("movq %rax, " + dst + "(%rbq)");

  }

  public void visit(UnaryNotInst i) {
    printInstructionInfor(i);
    int dst = getPositionRBP(i.getDst());
    int inner = getPositionRBP(i.getInner());

    out.printCode("movq " + inner + "(%rbp), %r10");
    out.printCode("not %r10");
    out.printCode("movq %r10, " + dst + "(%rbp)");
  }
}
