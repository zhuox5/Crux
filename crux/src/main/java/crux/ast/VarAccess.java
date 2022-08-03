package crux.ast;

import crux.ast.traversal.NodeVisitor;
import crux.ast.SymbolTable.Symbol;

/**
 * AST node for VarAccess. VarAccess nodes are used for accesses (both reads and writes) to global
 * and local variables.
 */
public final class VarAccess extends BaseNode implements Expression, java.io.Serializable {
  static final long serialVersionUID = 12022L;
  private final Symbol symbol;

  /**
   * @param position is the position of the name in the source code.
   * @param symbol is the symbol for the name.
   */
  public VarAccess(Position position, Symbol symbol) {
    super(position);
    this.symbol = symbol;
  }

  public Symbol getSymbol() {
    return symbol;
  }

  @Override
  public <T> T accept(NodeVisitor<? extends T> visitor) {
    return visitor.visit(this);
  }
}
