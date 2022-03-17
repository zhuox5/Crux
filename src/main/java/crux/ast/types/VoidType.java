package crux.ast.types;

/**
 * Void is void
 */
public final class VoidType extends Type implements java.io.Serializable {
  static final long serialVersionUID = 12022L;

  @Override
  public String toString() {
    return "void";
  }
}
