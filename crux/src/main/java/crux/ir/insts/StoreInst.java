package crux.ir.insts;

import crux.ir.AddressVar;
import crux.ir.Instruction;
import crux.ir.LocalVar;
import crux.ir.Value;

import java.util.List;
import java.util.function.Function;

/**
 * Stores the value of the source at the destination address.
 * <p>
 * Operation (pseudo-code): {@code *destAddress = srcValue}
 */
public final class StoreInst extends Instruction implements java.io.Serializable {
  static final long serialVersionUID = 12022L;

  public StoreInst(LocalVar srcValue, AddressVar destAddress) {
    super(List.of(srcValue, destAddress));
  }

  public LocalVar getSrcValue() {
    return (LocalVar) mOperands.get(0);
  }

  public AddressVar getDestAddress() {
    return (AddressVar) mOperands.get(1);
  }

  @Override
  public void accept(InstVisitor v) {
    v.visit(this);
  }

  @Override
  public String format(Function<Value, String> valueFormatter) {
    var srcVal = valueFormatter.apply(getSrcValue());
    var destAddr = valueFormatter.apply(getDestAddress());
    return String.format("store %s, %s", srcVal, destAddr);
  }
}
