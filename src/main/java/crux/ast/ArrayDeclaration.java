package crux.ast;

import crux.ast.SymbolTable.Symbol;
import crux.ast.traversal.NodeVisitor;

/**
 * AST node for a global ArrayDeclaration. Each global ArrayDeclaration has a symbol for the array
 * name.
 */

public final class ArrayDeclaration extends BaseNode
    implements Declaration, Statement, java.io.Serializable {
  static final long serialVersionUID = 12022L;
  private final Symbol symbol;

  public ArrayDeclaration(Position position, Symbol symbol) {
    super(position);
    this.symbol = symbol;
  }

  /**
   * Returns symbol for array access.
   */

  public Symbol getSymbol() {
    return symbol;
  }

  @Override
  public <T> T accept(NodeVisitor<? extends T> visitor) {
    return visitor.visit(this);
  }
}
