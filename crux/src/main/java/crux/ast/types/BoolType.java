package crux.ast.types;

/**
 * Types for Booleans values This should implement the equivalent methods along with and,or, and not
 * equivalent will check if the param is instance of BoolType
 */
public final class BoolType extends Type implements java.io.Serializable {
  static final long serialVersionUID = 12022L;

  @Override
  public String toString() {
    return "bool";
  }

  @Override
  Type and(Type that) {
    if(this.equivalent(that)){
      return new BoolType();
    }
    return super.and(that);
  }

  @Override
  Type or(Type that) {
    if(this.equivalent(that)){
      return new BoolType();
    }
    return super.or(that);
  }

  @Override
  Type not() {
    if(this.equivalent(this)){
      return new BoolType();
    }
    return super.not();
  }

  @Override
  Type assign(Type source) {
    if(this.equivalent(source)){
      return new VoidType();
    }
    return super.assign(source);
  }

  @Override
  public boolean equivalent(Type that) {
    return that.getClass() == BoolType.class;
  }
}
