package crux.ast;

import com.sun.jdi.CharType;
import crux.ast.Position;
import crux.ast.types.*;


import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Symbol table will map each symbol from Crux source code to its declaration or appearance in the
 * source. The symbol table is made up of scopes, Each scope is a map which maps an identifier to
 * it's symbol. Scopes are inserted to the table starting from the first scope (Global Scope). The
 * Global scope is the first scope in each Crux program and it contains all the built in functions
 * and names. The symbol table is an ArrayList of scops.
 */
public final class SymbolTable {

  /**
   * Symbol is used to record the name and type of names in the code. Names include function names,
   * global variables, global arrays, and local variables.
   */
  static public final class Symbol implements java.io.Serializable {
    static final long serialVersionUID = 12022L;
    private final String name;
    private final Type type;
    private final String error;

    /**
     *
     * @param name String
     * @param type the Type
     */
    private Symbol(String name, Type type) {
      this.name = name;
      this.type = type;
      this.error = null;
    }

    private Symbol(String name, String error) {
      this.name = name;
      this.type = null;
      this.error = error;
    }

    private Symbol(String name){
      this.name = name;
      this.type = null;
      this.error = null;
    }

    /**
     *
     * @return String the name
     */
    public String getName() {
      return name;
    }

    /**
     *
     * @return the type
     */
    public Type getType() {
      return type;
    }

    @Override
    public String toString() {
      if (error != null) {
        return String.format("Symbol(%s:%s)", name, error);
      }
      return String.format("Symbol(%s:%s)", name, type);
    }

    public String toString(boolean includeType) {
      if (error != null) {
        return toString();
      }
      return includeType ? toString() : String.format("Symbol(%s)", name);
    }
  }

  private final PrintStream err;
  private final ArrayList<Map<String, Symbol>> symbolScopes = new ArrayList<>();

  private boolean encounteredError = false;

  SymbolTable(PrintStream err) {
    this.err = err;
    //TODO
    Map<String, Symbol> myScope = new HashMap<>();

    FuncType readIntFuncType = new FuncType(TypeList.of(), new IntType());
    myScope.put("readInt", new Symbol("readInt", readIntFuncType));

    FuncType readCharFuncType = new FuncType(TypeList.of(), new IntType());
    myScope.put("readChar", new Symbol("readChar", readCharFuncType));

    FuncType printBoolFuncType = new FuncType(TypeList.of(new BoolType()), new VoidType());
    myScope.put("printBool", new Symbol("printBool", printBoolFuncType));

    FuncType printIntFuncType = new FuncType(TypeList.of(new IntType()), new VoidType());
    myScope.put("printInt", new Symbol("printInt", printIntFuncType));

    FuncType printCharFuncType = new FuncType(TypeList.of(new IntType()), new VoidType());
    myScope.put("printChar", new Symbol("printChar", printCharFuncType));

    FuncType printlnFuncType = new FuncType(TypeList.of(), new VoidType());
    myScope.put("println", new Symbol("println", printlnFuncType));

    symbolScopes.add(myScope);

  }


  boolean hasEncounteredError() {
    return encounteredError;
  }

  /**
   * Called to tell symbol table we entered a new scope.
   */

  void enter() {
    //TODO
    symbolScopes.add(new HashMap<String, Symbol>());
  }

  /**
   * Called to tell symbol table we are exiting a scope.
   */

  void exit() {
    //TODO
    symbolScopes.remove(symbolScopes.size()-1);
  }

  /**
   * Insert a symbol to the table at the most recent scope. if the name already exists in the
   * current scope that's a declareation error.
   */
  Symbol add(Position pos, String name, Type type) {
    //TODO
    int currentIndex = symbolScopes.size()-1;
    Map<String, Symbol> recentScope = symbolScopes.get(currentIndex);
    if(recentScope.containsKey(name)){
      err.printf("DeclareSymbolError%s[Could not find %s.]%n", pos, name);
      encounteredError = true;
      return new Symbol(name, "DeclareSymbolError");
    }
    else {
      Symbol mySymbol = new Symbol(name, type);
      symbolScopes.get(symbolScopes.size()-1).put(name, mySymbol);
      return mySymbol;
    }
  }

  /**
   * lookup a name in the SymbolTable, if the name not found in the table it shouold encounter an
   * error and return a symbol with ResolveSymbolError error. if the symbol is found then return it.
   */
  Symbol lookup(Position pos, String name) {
    var symbol = find(name);
    if (symbol == null) {
      err.printf("ResolveSymbolError%s[Could not find %s.]%n", pos, name);
      encounteredError = true;
      return new Symbol(name, "ResolveSymbolError");
    } else {
      return symbol;
    }
  }

  /**
   * Try to find a symbol in the table starting form the most recent scope.
   */
  private Symbol find(String name) {
    //TODO
    if(name == null) return null;
    for(int i=symbolScopes.size()-1; i>-1; i--){
      Map<String, Symbol> recentScope = symbolScopes.get(i);
      if(recentScope.get(name) != null){
        return recentScope.get(name);
      }
    }
    return null;
  }
}
