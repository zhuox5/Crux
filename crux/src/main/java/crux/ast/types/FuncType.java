package crux.ast.types;

/**
 * The field args is a TypeList with a type for each param. The type ret is the type of the function
 * return. The function return could be int, bool, or void. This class should implement the call
 * method.
 */
public final class FuncType extends Type implements java.io.Serializable {
  static final long serialVersionUID = 12022L;

  private TypeList args;
  private Type ret;

  public FuncType(TypeList args, Type returnType) {
    this.args = args;
    this.ret = returnType;
  }

  public Type getRet() {
    return ret;
  } //return type

  public TypeList getArgs() {
    return args;
  } //arguments, is a TypeList

  @Override
  public String toString() {
    return "func(" + args + "):" + ret;
  }

  @Override
  Type call(Type args) {
    if(args.getClass() == TypeList.class){
      if (((TypeList) args).equivalent(this.args)) {
        return this.ret;
      }
    }
    return super.call(args);
  }

  @Override
  public boolean equivalent(Type that) {
    return ((this.getRet() == ((FuncType) that).getRet()) &&
            (this.getArgs() == ((FuncType) that).getArgs()));
  }
}
