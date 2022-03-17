package crux.ast;

import crux.ast.traversal.NodeVisitor;

import java.util.List;

/**
 * AST node for For.
 */
public final class For extends BaseNode implements Statement, java.io.Serializable {
  static final long serialVersionUID = 12022L;
  private final Assignment init;
  private final Expression cond;
  private final Assignment increment;
  private final StatementList body;

  /**
   * @param position is the position of the loop in the source code.
   * @param init is the initializer assignment.
   * @param cond is the loop condition.
   * @param increment is the increment assignment.
   * @param body is the StatementList for the body of the loop.
   */

  public For(Position position, Assignment init, Expression cond, Assignment increment,
      StatementList body) {
    super(position);
    this.init = init;
    this.cond = cond;
    this.increment = increment;
    this.body = body;
  }

  public Assignment getInit() {
    return init;
  }

  public Expression getCond() {
    return cond;
  }

  public Assignment getIncrement() {
    return increment;
  }

  public StatementList getBody() {
    return body;
  }

  @Override
  public List<Node> getChildren() {
    return List.of(body);
  }

  @Override
  public <T> T accept(NodeVisitor<? extends T> visitor) {
    return visitor.visit(this);
  }
}
