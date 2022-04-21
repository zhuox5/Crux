package crux.ast.types;

/**
 * The variable base is the type of the array element. This could be int or bool. The extent
 * variable is number of elements in the array.
 *
 */
public final class ArrayType extends Type implements java.io.Serializable {
  static final long serialVersionUID = 12022L;
  private final Type base;
  private final long extent;

  public ArrayType(long extent, Type base) {
    this.extent = extent;
    this.base = base;
  }

  public Type getBase() {
    return base;
  }

  public long getExtent() {
    return extent;
  }

  @Override
  public String toString() {
    return String.format("array[%d,%s]", extent, base);
  }

  // "that" is index and it should be IntType, and then return the base of array
  @Override
  Type index(Type that) {
    if(that.getClass() == IntType.class){
      return ((ArrayType) that).getBase();
    }
    return super.index(that);
  }

  @Override
  public boolean equivalent(Type that) {
    if(that.getClass() == ArrayType.class){
      return this.base.getClass() == ((ArrayType) that).getBase().getClass() &&
              this.getExtent() == ((ArrayType) that).getExtent();
    }
    return false;
  }
}
