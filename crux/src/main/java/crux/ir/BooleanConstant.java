package crux.ir;

import crux.ast.types.BoolType;
import java.util.HashMap;

/**
 * A constant boolean (i.e. true or false). This is equivalent to {@link crux.ast.LiteralBool}.
 */
public final class BooleanConstant extends Constant implements java.io.Serializable {
  static final long serialVersionUID = 12022L;
  private boolean mValue;

  private BooleanConstant(Program ctx, boolean val) {
    super(new BoolType());
    mValue = val;
  }

  public boolean getValue() {
    return mValue;
  }

  public static BooleanConstant get(Program ctx, boolean value) {
    var currentMap = mBoolConstantPool.computeIfAbsent(ctx, p -> new HashMap<>());
    return currentMap.computeIfAbsent(value, p -> new BooleanConstant(ctx, value));
  }
}
