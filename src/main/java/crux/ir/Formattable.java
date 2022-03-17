package crux.ir;

import crux.ir.Value;

import java.util.function.Function;

public interface Formattable {
  String format(Function<Value, String> valueFormatter);
}
