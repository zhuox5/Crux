package crux.ast;

import crux.ast.traversal.NodeVisitor;
import crux.ast.SymbolTable.Symbol;

import java.util.List;

/**
 * AST node for FunctionDefinition.
 */

public final class FunctionDefinition extends BaseNode
    implements Declaration, java.io.Serializable {
  static final long serialVersionUID = 12022L;
  private final Symbol symbol;
  private final List<Symbol> parameters;
  private final StatementList statements;

  /*
   * Function Definition in AST.
   * 
   * @param position location in source code.
   * 
   * @param symbol is the function symbol for this declaration.
   * 
   * @param parameters is a list of symbols for the function's parameters.
   * 
   * @param statements is the StatementList for the function's body.
   */

  public FunctionDefinition(Position position, Symbol symbol, List<Symbol> parameters,
      StatementList statements) {
    super(position);
    this.symbol = symbol;
    this.parameters = parameters;
    this.statements = statements;
  }

  /**
   * Returns the Symbol for the function name.
   */

  public Symbol getSymbol() {
    return symbol;
  }

  /**
   * Returns the list of symbols for the parameters for the function.
   */
  public List<Symbol> getParameters() {
    return parameters;
  }

  /**
   * Returns the StatementList for the method body.
   */
  public StatementList getStatements() {
    return statements;
  }

  @Override
  public List<Node> getChildren() {
    return List.of(statements);
  }

  @Override
  public <T> T accept(NodeVisitor<? extends T> visitor) {
    return visitor.visit(this);
  }
}
