package crux.ast;

import crux.ast.SymbolTable.Symbol;
import crux.ast.traversal.NodeVisitor;

/**
 * AST node for VariableDeclaration. Used for declaring a global variable or a local variable.
 */
public final class VariableDeclaration extends BaseNode
    implements Declaration, Statement, java.io.Serializable {
  private final Symbol symbol;
  static final long serialVersionUID = 12022L;

  public VariableDeclaration(Position position, Symbol symbol) {
    super(position);
    this.symbol = symbol;
  }

  @Override
  public <T> T accept(NodeVisitor<? extends T> visitor) {
    return visitor.visit(this);
  }

  public Symbol getSymbol() {
    return symbol;
  }
}
