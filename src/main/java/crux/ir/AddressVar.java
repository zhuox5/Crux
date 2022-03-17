package crux.ir;

import crux.ast.types.Type;

/**
 * An address variable is a variable that contains an address to a location in memory. This can be
 * an array or a global variable.
 */
public class AddressVar extends Variable implements java.io.Serializable {
  static final long serialVersionUID = 12022L;

  /**
   * Do not directly create a AddressVar. Instead use the getTempAddressVar method in the Function
   * class.
   */

  public AddressVar(Type type) {
    super(type);
  }

  public AddressVar(Type type, String name) {
    super(type, name);
    mName = String.format("%%%s", mName);
  }

  public String toString() {
    return mName;
  }
}
