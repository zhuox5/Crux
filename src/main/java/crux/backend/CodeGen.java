package crux.backend;

import crux.ast.SymbolTable.Symbol;
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

  private HashMap<Variable, Integer> varIndexMap;
  private int numslots;
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
      }
      argIndex++;
    }

    /**
     * Generate Body
     **/
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
          } else if (f.getName().equals("main")) {
            out.printCode("movq $0, %rax");
            out.printCode("leave");
            out.printCode("ret");
          }
          else{
            out.printCode("leave");
            out.printCode("ret");
          }
        }
    }
    if(numslots % 2 != 0){
      numslots++;           //if uneven
    }
    out.printCode(".globl " + f.getName());
    out.printLabel(f.getName() + ":");
    out.printCode("enter $(8 * " + numslots + "), $0");
  }


  public void visit(AddressAt i) {

  }

  public void visit(BinaryOperator i) {}

  public void visit(CompareInst i) {}

  public void visit(CopyInst i) {}

  public void visit(JumpInst i) {}

  public void visit(LoadInst i) {}

  public void visit(NopInst i) {
    printInstructionInfor(i);
  }

  public void visit(StoreInst i) {}

  public void visit(ReturnInst i) {}

  public void visit(CallInst i) {}

  public void visit(UnaryNotInst i) {
    printInstructionInfor(i);
    out.printCode("Unnnnnary");
  }
}
