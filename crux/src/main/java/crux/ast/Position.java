package crux.ast;

/**
 * Position class that is used to record the line number that nodes in the AST come from.
 */

public final class Position implements java.io.Serializable {
  static final long serialVersionUID = 12022L;
  public final int line;

  public Position(int line) {
    this.line = line;
  }

  @Override
  public String toString() {
    return String.format("(%d)", line);
  }
}
