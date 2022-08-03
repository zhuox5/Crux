package crux.ast;

import crux.ast.traversal.NodeVisitor;
import crux.ast.SymbolTable.Symbol;
import java.util.List;
import java.util.ArrayList;

/**
 * AST node for function Call expression or statement.
 */
public final class Call extends BaseNode implements Expression, Statement, java.io.Serializable {
  static final long serialVersionUID = 12022L;
  private final Symbol callee;
  private final List<Expression> arguments;

  /**
   * Construct for Call ast node.
   * 
   * @param position Location of statement in source file.
   * @param callee {@link crux.ast.Symbol} for callee method.
   * @param arguments List of expressions that are arguments to the function call.
   */

  public Call(Position position, Symbol callee, List<Expression> arguments) {
    super(position);
    this.callee = callee;
    this.arguments = arguments;
  }

  /**
   * Returns a list of the arguments to this call.
   */

  public List<Expression> getArguments() {
    return arguments;
  }

  @Override
  public List<Node> getChildren() {
    return new ArrayList<Node>(arguments);
  }

  @Override
  public <T> T accept(NodeVisitor<? extends T> visitor) {
    return visitor.visit(this);
  }

  /**
   * Returns the {@link crux.ast.Symbol} for the method to be called.
   */
  public Symbol getCallee() {
    return callee;
  }
}
