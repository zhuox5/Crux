package crux.ast;

import crux.ast.traversal.NodeVisitor;
import java.util.List;
import crux.ast.SymbolTable.Symbol;

/**
 * AST node for ArrayAccess.
 */

public final class ArrayAccess extends BaseNode implements Expression, java.io.Serializable {
  static final long serialVersionUID = 12022L;
  private final Symbol base;
  private final Expression index;

  /**
   * @param base is the {@link crux.ast.Symbol} for the globalarray.
   * @param index is an Expression for the index to access.
   */

  public ArrayAccess(Position position, Symbol base, Expression index) {
    super(position);
    this.base = base;
    this.index = index;
  }

  /**
   * Returns the base symbol for this array access.
   */

  public Symbol getBase() {
    return base;
  }

  /**
   * Returns the index expression for this array access.
   */

  public Expression getIndex() {
    return index;
  }

  @Override
  public List<Node> getChildren() {
    return List.of(index);
  }

  @Override
  public <T> T accept(NodeVisitor<? extends T> visitor) {
    return visitor.visit(this);
  }
}
