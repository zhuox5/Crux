package crux.ir.insts;

import crux.ir.AddressVar;
import crux.ir.Instruction;
import crux.ir.Value;
import crux.ir.Variable;
import crux.ir.LocalVar;
import crux.ast.SymbolTable.Symbol;

import java.util.List;
import java.util.function.Function;

/**
 * Calculates the address given a base address and an offset.
 * <p>
 * Operation (pseudo-code): {@code destVar = base + offset}
 */
public final class AddressAt extends Instruction implements java.io.Serializable {
  static final long serialVersionUID = 12022L;
  Symbol base;

  public AddressAt(AddressVar destVar, Symbol base, LocalVar offset) {
    super(destVar, List.of(offset));
    this.base = base;
  }

  public AddressAt(AddressVar destVar, Symbol base) {
    super(destVar, List.of());
    this.base = base;
  }

  public Symbol getBase() {
    return base;
  }

  public LocalVar getOffset() {
    return mOperands.size() != 0 ? (LocalVar) mOperands.get(0) : null;
  }

  public AddressVar getDst() {
    return (AddressVar) mDestVar;
  }

  @Override
  public void accept(InstVisitor v) {
    v.visit(this);
  }

  @Override
  public String format(Function<Value, String> valueFormatter) {
    var dest = valueFormatter.apply(mDestVar);
    var base = getBase().getName();
    var offset = valueFormatter.apply(getOffset());
    return String.format("%s = addressAt %s, %s", dest, base, offset);
  }
}
