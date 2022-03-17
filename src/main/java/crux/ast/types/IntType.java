package crux.ast.types;

/**
 * Types for Integers values. This should implement the equivalent methods along with add, sub, mul,
 * div, and compare. The method equivalent will check if the param is an instance of IntType.
 */
public final class IntType extends Type implements java.io.Serializable {
  static final long serialVersionUID = 12022L;

  @Override
  public String toString() {
    return "int";
  }
}
