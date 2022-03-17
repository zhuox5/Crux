package crux.ir.insts;

import crux.ir.Instruction;
import crux.ir.LocalVar;
import crux.ir.Value;

import java.util.List;
import java.util.function.Function;

/**
 * Store the return value and leave the current function, returning the control to the caller.
 * <p>
 * Operation (pseudo-code):
 * 
 * <pre>
 * {@code
 * $ReturnRegister = retValue
 * releaseCurrentStackFrame()
 * return
 * }
 * </pre>
 */
public final class ReturnInst extends Instruction implements java.io.Serializable {
  static final long serialVersionUID = 12022L;

  public ReturnInst(LocalVar retValue) {
    super(List.of(retValue));
  }

  public LocalVar getReturnValue() {
    return (LocalVar) mOperands.get(0);
  }

  @Override
  public void accept(InstVisitor v) {
    v.visit(this);
  }

  @Override
  public String format(Function<Value, String> valueFormatter) {
    return String.format("return %s", valueFormatter.apply(getReturnValue()));
  }
}
