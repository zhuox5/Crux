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

  @Override
  Type call(Type args) {
    if(this.equivalent(args)){
      return new VoidType();
    }
    return super.call(args);
  }

  @Override
  public boolean equivalent(Type that) {
    return that.getClass() == VoidType.class;
  }
}
