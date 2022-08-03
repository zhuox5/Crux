package crux.ir.insts;

import crux.ir.Instruction;
import crux.ir.LocalVar;
import crux.ir.Value;

import java.util.List;
import java.util.function.Function;

/**
 * Copies the source in to the destination.
 * <p>
 * Operation (pseudo-code): {@code destVar = source}
 */
public final class CopyInst extends Instruction implements java.io.Serializable {
  static final long serialVersionUID = 12022L;

  public CopyInst(LocalVar destVar, Value source) {
    super(destVar, List.of(source));
  }

  public Value getSrcValue() {
    return mOperands.get(0);
  }

  public LocalVar getDstVar() {
    return (LocalVar) mDestVar;
  }

  @Override
  public void accept(InstVisitor v) {
    v.visit(this);
  }

  @Override
  public String format(Function<Value, String> valueFormatter) {
    var dest = valueFormatter.apply(mDestVar);
    var source = valueFormatter.apply(getSrcValue());
    return String.format("%s = %s", dest, source);
  }
}
