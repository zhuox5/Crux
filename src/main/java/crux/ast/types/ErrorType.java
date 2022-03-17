package crux.ast.types;

/**
 * ErrorType to recored errors during operations on types
 */
public final class ErrorType extends Type implements java.io.Serializable {
  static final long serialVersionUID = 12022L;

  private final String message;

  public ErrorType(String message) {
    this.message = message;
  }

  public String getMessage() {
    return message;
  }

  @Override
  public String toString() {
    return String.format("ErrorType(%s)", message);
  }
}
