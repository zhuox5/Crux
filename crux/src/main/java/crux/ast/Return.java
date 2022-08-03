package crux.ast;

import crux.ast.traversal.NodeVisitor;

import java.util.List;

/**
 * AST node for Return statement.
 */
public final class Return extends BaseNode implements Statement, java.io.Serializable {
  static final long serialVersionUID = 12022L;
  private final Expression value;

  /**
   * @param position is the position of the return statement in the code.
   * @param value is the expression that computes the return value.
   */

  public Return(Position position, Expression value) {
    super(position);
    this.value = value;
  }

  public Expression getValue() {
    return value;
  }

  @Override
  public List<Node> getChildren() {
    return List.of(value);
  }

  @Override
  public <T> T accept(NodeVisitor<? extends T> visitor) {
    return visitor.visit(this);
  }
}
