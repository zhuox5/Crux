package crux.ast;

import crux.ast.traversal.NodeVisitor;
import java.util.List;

/**
 * AST node for StatementList
 */

public final class StatementList extends ListNode<Statement> implements java.io.Serializable {
  static final long serialVersionUID = 12022L;

  /**
   * @param position is the position of the StatementList in the code.
   * @param statements is a List of statements that comprise the StatementList.
   */

  public StatementList(Position position, List<Statement> statements) {
    super(position, statements);
  }

  @Override
  public <T> T accept(NodeVisitor<? extends T> visitor) {
    return visitor.visit(this);
  }
}
