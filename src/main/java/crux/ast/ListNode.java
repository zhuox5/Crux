package crux.ast;

import java.util.List;

/**
 * Class that is a list of nodes. Extended for StatementList and others.
 */

public abstract class ListNode<T extends Node> extends BaseNode implements java.io.Serializable {
  static final long serialVersionUID = 12022L;
  protected final List<T> children;

  ListNode(Position position, List<T> children) {
    super(position);
    this.children = children;
  }

  @Override
  public List<Node> getChildren() {
    return List.copyOf(children);
  }
}
