package crux.ir.insts;

import crux.ir.Instruction;
import crux.ir.LocalVar;
import crux.ir.Value;

import java.util.List;
import java.util.function.Function;

/**
 * A conditional jump, which jumps to its true-successor if the condition is true, otherwise it
 * continues with the next instruction. Note that the jump instruction does not contain the
 * destination. The two possible destinations are stored within the instruction graph of the
 * function.
 * <p>
 * Operation (pseudo-code):
 * 
 * <pre>
 * {@code
 * if (predicate)
 *     jump(trueInstruction)
 * else
 *     continue(falseInstruction)
 * }
 * </pre>
 */
public final class JumpInst extends Instruction implements java.io.Serializable {
  static final long serialVersionUID = 12022L;

  public JumpInst(LocalVar predicate) {
    super(List.of(predicate));
  }

  public LocalVar getPredicate() {
    return (LocalVar) mOperands.get(0);
  }

  @Override
  public void accept(InstVisitor v) {
    v.visit(this);
  }

  @Override
  public String format(Function<Value, String> valueFormatter) {
    return String.format("jump %s", valueFormatter.apply(getPredicate()));
  }
}
