package crux.ast;

import crux.ast.traversal.NodeVisitor;
import java.util.List;

/**
 * AST node for Assignment. Assignments have left hand side and right hand side. The left hand side
 * is a VarAccess or ArrayAccess and the right hand side is a value.
 */

public final class Assignment extends BaseNode implements Statement, java.io.Serializable {
  static final long serialVersionUID = 12022L;
  private final Expression location;
  private final Expression value;

  public Assignment(Position position, Expression location, Expression value) {
    super(position);
    this.location = location;
    this.value = value;
  }

  /**
   * Returns the left hand side (destination) of the assignment.
   */

  public Expression getLocation() {
    return location;
  }

  /**
   * Returns the right hand side (source) of the assignment.
   */

  public Expression getValue() {
    return value;
  }

  @Override
  public List<Node> getChildren() {
    return List.of(location, value);
  }

  @Override
  public <T> T accept(NodeVisitor<? extends T> visitor) {
    return visitor.visit(this);
  }
}
