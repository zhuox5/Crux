package crux.ast;

import crux.ast.traversal.NodeVisitor;

/**
 * AST node for LiteralBool
 */
public final class LiteralBool extends BaseNode implements Expression, java.io.Serializable {
  static final long serialVersionUID = 12022L;
  private final boolean value;

  /**
   * @param position is the position of the boolean in the code.
   * @param value is the value of the boolean.
   */

  public LiteralBool(Position position, boolean value) {
    super(position);
    this.value = value;
  }

  public boolean getValue() {
    return value;
  }

  @Override
  public <T> T accept(NodeVisitor<? extends T> visitor) {
    return visitor.visit(this);
  }
}
