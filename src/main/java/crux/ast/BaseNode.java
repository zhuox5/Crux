package crux.ast;

import java.util.List;
import crux.ast.types.Type;

/**
 * BaseNode class that all AST Nodes classes extend.
 */

public abstract class BaseNode implements Node, java.io.Serializable {
  static final long serialVersionUID = 12022L;
  private final Position position;
  private Type type;

  BaseNode(Position position) {
    this.position = position;
  }

  @Override
  public List<Node> getChildren() {
    return List.of();
  }

  @Override
  public Position getPosition() {
    return position;
  }

  public void setType(Type t) {
    type = t;
  }

  public Type getType() {
    return type;
  }
}
