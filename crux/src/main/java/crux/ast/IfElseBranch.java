package crux.ast;

import crux.ast.traversal.NodeVisitor;

import java.util.List;

/**
 * AST node for if stmt.
 */
public final class IfElseBranch extends BaseNode implements Statement, java.io.Serializable {
  static final long serialVersionUID = 12022L;
  private final Expression condition;
  private final StatementList thenBlock;
  private final StatementList elseBlock;

  /**
   * @param position is the location of the statement in the source,
   * @param condition is the expression for the if condition.
   * @param thenBlock is the StatementList for the code that is executed if the condition is true.
   * @param elseBlock is the StatementList for the code that is executed if the condition is true.
   */

  public IfElseBranch(Position position, Expression condition, StatementList thenBlock,
      StatementList elseBlock) {
    super(position);
    this.condition = condition;
    this.thenBlock = thenBlock;
    this.elseBlock = elseBlock;
  }

  public Expression getCondition() {
    return condition;
  }

  public StatementList getThenBlock() {
    return thenBlock;
  }

  public StatementList getElseBlock() {
    return elseBlock;
  }

  @Override
  public List<Node> getChildren() {
    return List.of(condition, thenBlock, elseBlock);
  }

  @Override
  public <T> T accept(NodeVisitor<? extends T> visitor) {
    return visitor.visit(this);
  }
}
