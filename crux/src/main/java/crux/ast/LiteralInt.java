package crux.ast;

import crux.ast.traversal.NodeVisitor;

/**
 * AST node for LiteralInt
 */
public final class LiteralInt extends BaseNode implements Expression, java.io.Serializable {
  static final long serialVersionUID = 12022L;
  private final long value;

  /**
   * @param position is the position of the int literal in the code.
   * @param value is the 64-bit value of the literal.
   */

  public LiteralInt(Position position, long value) {
    super(position);
    this.value = value;
  }

  public long getValue() {
    return value;
  }

  @Override
  public <T> T accept(NodeVisitor<? extends T> visitor) {
    return visitor.visit(this);
  }
}
