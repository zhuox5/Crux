package crux.ast;

import crux.ast.traversal.NodeVisitor;

import java.util.List;

/**
 * AST Node for DeclarationList.
 */
public final class DeclarationList extends ListNode<Declaration> implements java.io.Serializable {
  static final long serialVersionUID = 12022L;

  public DeclarationList(Position position, List<Declaration> declarations) {
    super(position, declarations);
  }

  @Override
  public <T> T accept(NodeVisitor<? extends T> visitor) {
    return visitor.visit(this);
  }
}
