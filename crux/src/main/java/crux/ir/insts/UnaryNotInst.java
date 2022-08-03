package crux.ir.insts;

import crux.ir.Instruction;
import crux.ir.Value;
import crux.ir.LocalVar;

import java.util.List;
import java.util.function.Function;

/**
 * Inverts a boolean.
 * <p>
 * Operation (pseudo-code): {@code destVar = !operand}
 */
public final class UnaryNotInst extends Instruction implements java.io.Serializable {
  static final long serialVersionUID = 12022L;

  public UnaryNotInst(LocalVar destVar, LocalVar operand) {
    super(destVar, List.of(operand));
  }

  @Override
  public void accept(InstVisitor v) {
    v.visit(this);
  }

  public LocalVar getInner() {
    return (LocalVar) mOperands.get(0);
  }

  public LocalVar getDst() {
    return (LocalVar) mDestVar;
  }

  @Override
  public String format(Function<Value, String> valueFormatter) {
    var destVar = valueFormatter.apply(mDestVar);
    var operand = valueFormatter.apply(mOperands.get(0));
    return String.format("%s = not %s", destVar, operand);
  }
}
