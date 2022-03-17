package crux.ir;

import crux.ast.types.Type;

/**
 * Any sort of value in the memory which holds a value that can be read or written by instructions.
 * This includes both variables declared in the AST, as well as temporaries.
 */
public abstract class Variable extends Value implements java.io.Serializable {
  static final long serialVersionUID = 12022L;
  protected String mName = "";

  protected Variable(Type type) {
    super(type);
  }

  protected Variable(Type type, String name) {
    this(type);
    mName = name;
  }

  public String getName() {
    return mName;
  }
}
