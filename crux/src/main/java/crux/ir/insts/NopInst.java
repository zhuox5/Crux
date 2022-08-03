package crux.ir.insts;

import crux.ir.Instruction;
import crux.ir.Value;

import java.util.List;
import java.util.function.Function;

/**
 * Does nothing. Can be useful during lowering from AST to IR when a dummy instruction is needed.
 */
public final class NopInst extends Instruction implements java.io.Serializable {
  static final long serialVersionUID = 12022L;

  public NopInst() {
    super(List.of());
  }

  @Override
  public void accept(InstVisitor v) {
    v.visit(this);
  }

  @Override
  public String format(Function<Value, String> valueFormatter) {
    return "nop";
  }
}
