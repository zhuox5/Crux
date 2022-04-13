package crux.ast;

import crux.ast.*;
import crux.ast.OpExpr.Operation;
import crux.pt.CruxBaseVisitor;
import crux.pt.CruxParser;
import crux.ast.types.*;
import crux.ast.SymbolTable.Symbol;
import org.antlr.v4.runtime.ParserRuleContext;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class will convert the parse tree generated by ANTLR to AST It follows the visitor pattern
 * where declarations will be by DeclarationVisitor Class Statements will be resolved by
 * StatementVisitor Class Expressions will be resolved by ExpressionVisitor Class
 */

public final class ParseTreeLower {
  private final DeclarationVisitor declarationVisitor = new DeclarationVisitor();
  private final StatementVisitor statementVisitor = new StatementVisitor();
  private final ExpressionVisitor expressionVisitor = new ExpressionVisitor();

  private final SymbolTable symTab;

  public ParseTreeLower(PrintStream err) {
    symTab = new SymbolTable(err);
  }

  private static Position makePosition(ParserRuleContext ctx) {
    var start = ctx.start;
    return new Position(start.getLine());
  }

  public Type getType(String typeName){

    if(typeName.equals("bool")){
        return new BoolType();
    }
    else if(typeName.equals("void")){
        return new VoidType();
    }
    else if(typeName.equals("int")){
        return new IntType();
    }
    return new ErrorType(typeName);
  }


  /**
   *
   * @return True if any errors
   */
  public boolean hasEncounteredError() {
    return symTab.hasEncounteredError();
  }


  /**
   * Lower top-level parse tree to AST
   *
   * @return a {@link DeclarationList} object representing the top-level AST.
   */

  public DeclarationList lower(CruxParser.ProgramContext program) {
    List<Declaration> myDeclaration = new ArrayList<>();
    for(CruxParser.DeclarationContext d : program.declarationList().declaration()){
       myDeclaration.add(d.accept(declarationVisitor));
    }
    if(hasEncounteredError()){
      //myDeclaration = null;
      return null;
    }
    Position myPosition = makePosition(program);   //or just makePosition(program)?
    return new DeclarationList(myPosition, myDeclaration);

  }

  /**
   * Lower statement list by lower individual statement into AST.
   *
   * @return a {@link StatementList} AST object.
   */


   private StatementList lower(CruxParser.StatementListContext statementList) {
     List<Statement> myStatement = new ArrayList<>();
     for(CruxParser.StatementContext s : statementList.statement()){
       myStatement.add(s.accept(statementVisitor));
     }
     if(hasEncounteredError()){
       //myStatement = null;
       return null;
     }
     return new StatementList(makePosition(statementList), myStatement);
   }


  /**
   * Similar to {@link #lower(CruxParser.StatementListContext)}, but handles symbol table as well.
   *
   * @return a {@link StatementList} AST object.
   */

  private StatementList lower(CruxParser.StatementBlockContext statementBlock) {
      symTab.enter();
      StatementList myStatementList = lower(statementBlock.statementList());
      symTab.exit();
      return myStatementList;
  }


  /**
   * A parse tree visitor to create AST nodes derived from {@link Declaration}
   */
  private final class DeclarationVisitor extends CruxBaseVisitor<Declaration> {
    /**
     * Visit a parse tree variable declaration and create an AST {@link VariableDeclaration}
     *
     * @return an AST {@link VariableDeclaration}
     */

      @Override
      public VariableDeclaration visitVariableDeclaration(CruxParser.VariableDeclarationContext ctx) {
         //Lower to VariableDeclaration
        Position myPosition = makePosition(ctx);
        String myName = ctx.Identifier().getText();
        String typeName = ctx.type().Identifier().getText();
        Type myType = getType(typeName);
        Symbol mySymbol = symTab.add(myPosition, myName, myType);

        return new VariableDeclaration(myPosition, mySymbol);
      }


    /**
     * Visit a parse tree array declaration and creates an AST {@link ArrayDeclaration}
     *
     * @return an AST {@link ArrayDeclaration}
     */


     @Override
     public Declaration visitArrayDeclaration(CruxParser.ArrayDeclarationContext ctx) {
       Position myPosition = makePosition(ctx);
       String myName = ctx.Identifier().getText();
       String typeName = ctx.type().Identifier().getText();
       Type myType = getType(typeName);
       Symbol mySymbol = symTab.add(myPosition, myName,
               new ArrayType(Long.parseLong(ctx.Integer().getText()), myType));
       //Parse String to Long
       return new ArrayDeclaration(myPosition, mySymbol);
     }


    /**
     * Visit a parse tree function definition and create an AST {@link FunctionDefinition}
     *
     * @return an AST {@link FunctionDefinition}
     */


     @Override
     public Declaration visitFunctionDefinition(CruxParser.FunctionDefinitionContext ctx) {
       Position myPosition = makePosition(ctx);
       TypeList myTypeList = new TypeList();
       for(CruxParser.ParameterContext parameter : ctx.parameterList().parameter()) {
         Type temp = getType(parameter.type().Identifier().getText());
         myTypeList.append(temp);
       }

       String typeName = ctx.Identifier().getText();
       Type myReturnType = getType(typeName);
       List<Symbol> myParameters = new ArrayList<>();
       Symbol mySymbol = symTab.add(myPosition,
               typeName,
               new FuncType(myTypeList, myReturnType));

       symTab.enter();
       for(CruxParser.ParameterContext parameter : ctx.parameterList().parameter()){
           String paramName = parameter.Identifier().getText();
           Type paramType = getType(parameter.type().Identifier().getText());
           Symbol temp = symTab.add(makePosition(parameter),
                   paramName,
                   paramType);
           myParameters.add(temp);
       }
       StatementList myStatementList = lower(ctx.statementBlock().statementList());
       FunctionDefinition myFD = new FunctionDefinition(myPosition, mySymbol, myParameters, myStatementList);
       symTab.exit();
       return myFD;
     }
  }


  /**
   * A parse tree visitor to create AST nodes derived from {@link Statement}
   */

  private final class StatementVisitor extends CruxBaseVisitor<Statement> {
    /**
     * Visit a parse tree variable declaration and create an AST {@link VariableDeclaration}. Since
     * {@link VariableDeclaration} is both {@link Declaration} and {@link Statement}, we simply
     * delegate this to
     * {@link DeclarationVisitor#visitArrayDeclaration(CruxParser.ArrayDeclarationContext)} which we
     * implement earlier.
     *
     * @return an AST {@link VariableDeclaration}
     */


     @Override
     public Statement visitVariableDeclaration(CruxParser.VariableDeclarationContext ctx) {
       return declarationVisitor.visitVariableDeclaration(ctx);
     }


    /**
     * Visit a parse tree assignment statement and create an AST {@link Assignment}
     *
     * @return an AST {@link Assignment}
     */


     @Override
     public Statement visitAssignmentStatement(CruxParser.AssignmentStatementContext ctx) {
       //return statementVisitor.visitAssignmentStatement(ctx);
         Position myPosition = makePosition(ctx);
         Expression lhs = ctx.designator().accept(expressionVisitor);
         Expression rhs = ctx.expression0().accept(expressionVisitor);
         return new Assignment(myPosition, lhs, rhs);
     }


    /**
     * Visit a parse tree assignment nosemi statement and create an AST {@link Assignment}
     *
     * @return an AST {@link Assignment}
     */


     @Override
     public Statement visitAssignmentStatementNoSemi(CruxParser.AssignmentStatementNoSemiContext ctx) {
         Position myPosition = makePosition(ctx);
         Expression lhs = ctx.designator().accept(expressionVisitor);
         Expression rhs = ctx.expression0().accept(expressionVisitor);
         return new Assignment(myPosition, lhs, rhs);
     }


    /**
     * Visit a parse tree call statement and create an AST {@link Call}. Since {@link Call} is both
     * {@link Expression} and {@link Statement}, we simply delegate this to
     * {@link ExpressionVisitor#visitCallExpression(CruxParser.CallExpressionContext)} that we will
     * implement later.
     *
     * @return an AST {@link Call}
     */


     @Override
     public Statement visitCallStatement(CruxParser.CallStatementContext ctx) {
       return expressionVisitor.visitCallExpression(ctx.callExpression());
     }


    /**
     * Visit a parse tree if-else branch and create an AST {@link IfElseBranch}. The template code
     * shows partial implementations that visit the then block and else block recursively before
     * using those returned AST nodes to construct {@link IfElseBranch} object.
     *
     * @return an AST {@link IfElseBranch}
     */


     @Override
     public Statement visitIfStatement(CruxParser.IfStatementContext ctx) {
       IfElseBranch myIEB;
       Position myPosition = makePosition(ctx);
       Expression myExpression = ctx.expression0().accept(expressionVisitor);
       if (ctx.statementBlock().size() >= 2) {              //Muti-statement
         myIEB = new IfElseBranch(myPosition, myExpression, lower(ctx.statementBlock(0)),
                 lower(ctx.statementBlock(1)));
       }
       else {                                               //Solo-statement
         StatementList myElseBlock = new StatementList(myPosition, new ArrayList<>());
         StatementList myThenBlock = lower(ctx.statementBlock(0));
         myIEB = new IfElseBranch(myPosition, myExpression, myThenBlock, myElseBlock);
       }
       return myIEB;
     }


    /**
     * Visit a parse tree for loop and create an AST {@link For}. You'll going to use a similar
     * techniques as {@link #visitIfStatement(CruxParser.IfStatementContext)} to decompose this
     * construction.
     *
     * @return an AST {@link For}
     */


     //FOR OPEN_PAREN assignmentStatement expression0
     //SemiColon assignmentStatementNoSemi CLOSE_PAREN statementBlock
     @Override
     public Statement visitForStatement(CruxParser.ForStatementContext ctx) {
         symTab.enter();
         Position myPosition = makePosition(ctx);
         var myInit = (Assignment) ctx.assignmentStatement().accept(statementVisitor);
         Expression myCond = ctx.expression0().accept(expressionVisitor);
         var myIncrement = (Assignment) ctx.assignmentStatementNoSemi().accept(statementVisitor);
         StatementList myBody = lower(ctx.statementBlock());
         symTab.exit();
         return new For(myPosition, myInit, myCond, myIncrement, myBody);

     }



    /**
     * Visit a parse tree return statement and create an AST {@link Return}. Here we show a simple
     * example of how to lower a simple parse tree construction.
     *
     * @return an AST {@link Return}
     */


     @Override
     public Statement visitReturnStatement(CruxParser.ReturnStatementContext ctx) {
       Position myPosition = makePosition(ctx);
       Expression myExpression = ctx.expression0().accept(expressionVisitor);
       return new Return(myPosition, myExpression);
     }

    /**
     * Creates a Break node
     */

     @Override
     public Statement visitBreakStatement(CruxParser.BreakStatementContext ctx) {
         Position myPosition = makePosition(ctx);
         return new Break(myPosition);
     }

  }

  private final class ExpressionVisitor extends CruxBaseVisitor<Expression> {
    /**
     * Parse Expression0 to OpExpr Node Parsing the expression should be exactly as described in the
     * grammer
     */

     @Override
     public Expression visitExpression0(CruxParser.Expression0Context ctx) {
         List<CruxParser.Expression1Context> exp1Context = ctx.expression1();
         Position myPosition = makePosition(ctx);
         Expression left_expression = exp1Context.get(0).accept(expressionVisitor);

         if(ctx.op0() == null || ctx.expression1(1) == null){
             return left_expression;
         }
         else{
             Expression right_expression = exp1Context.get(1).accept(expressionVisitor);
             if(ctx.op0(0).getText().equals(">=")){
                 return new OpExpr(myPosition, Operation.GE, left_expression, right_expression);
             }
             else if(ctx.op0(0).getText().equals("<=")){
                 return new OpExpr(myPosition, Operation.LE, left_expression, right_expression);
             }
             else if(ctx.op0(0).getText().equals("!=")){
                 return new OpExpr(myPosition, Operation.NE, left_expression, right_expression);
             }
             else if(ctx.op0(0).getText().equals("==")){
                 return new OpExpr(myPosition, Operation.EQ, left_expression, right_expression);
             }
             else if(ctx.op0(0).getText().equals(">")){
                 return new OpExpr(myPosition, Operation.GT, left_expression, right_expression);
             }
             else if(ctx.op0(0).getText().equals("<")){
                 return new OpExpr(myPosition, Operation.LT, left_expression, right_expression);
             }
         }
         return null;
     }


    /**
     * Parse Expression1 to OpExpr Node Parsing the expression should be exactly as described in the
     * grammer
     */


    @Override
     public Expression visitExpression1(CruxParser.Expression1Context ctx) {
        CruxParser.Expression2Context exp2Context = ctx.expression2();
        Position myPosition = makePosition(ctx);
        Expression myExpression = exp2Context.accept(expressionVisitor);
        if(ctx.op1() == null){
            return myExpression;
        }
        else {
            Expression left_expression = ctx.expression1().accept(expressionVisitor);  //exp1
            Expression right_expression = exp2Context.accept(expressionVisitor);       //exp2
            if(ctx.op1().getText().equals("+")){
                return new OpExpr(myPosition, Operation.ADD, left_expression, right_expression);
            }
            else if(ctx.op1().getText().equals("-")){
                return new OpExpr(myPosition, Operation.SUB, left_expression, right_expression);
            }
            else if(ctx.op1().getText().equals("||")){
                return new OpExpr(myPosition, Operation.LOGIC_OR, left_expression, right_expression);
            }
            else{
                return null;
            }
        }
    }


    /**
     * Parse Expression2 to OpExpr Node Parsing the expression should be exactly as described in the
     * grammer
     */


     @Override
     public Expression visitExpression2(CruxParser.Expression2Context ctx) {
         CruxParser.Expression3Context exp3Context = ctx.expression3();
         Position myPosition = makePosition(ctx);
         Expression myExpression = exp3Context.accept(expressionVisitor);
         if(ctx.op2() == null){
             return myExpression;
         }
         else {
             Expression left_expression = ctx.expression2().accept(expressionVisitor);  //exp1
             Expression right_expression = exp3Context.accept(expressionVisitor);       //exp2
             if(ctx.op2().getText().equals("*")){
                 return new OpExpr(myPosition, Operation.MULT, left_expression, right_expression);
             }
             else if(ctx.op2().getText().equals("/")){
                 return new OpExpr(myPosition, Operation.DIV, left_expression, right_expression);
             }
             else if(ctx.op2().getText().equals("&&")){
                 return new OpExpr(myPosition, Operation.LOGIC_AND, left_expression, right_expression);
             }
             else{
                 return new OpExpr(myPosition, Operation.LOGIC_OR, left_expression, right_expression);
             }
         }
     }


    /**
     * Parse Expression3 to OpExpr Node Parsing the expression should be exactly as described in the
     * grammer
     */


    @Override
     public Expression visitExpression3(CruxParser.Expression3Context ctx) {
        Position myPosition = makePosition(ctx);
        //Expression left_expression = ctx.expression3().accept(expressionVisitor);
        if(ctx.expression3() != null){
            Expression left_expression = ctx.expression3().accept(expressionVisitor);
            return new OpExpr(myPosition, Operation.LOGIC_NOT, left_expression, null);
        }
        else if(ctx.expression0() != null){
            return ctx.expression0().accept(expressionVisitor);
        }
        else if(ctx.designator() != null){
            return ctx.designator().accept(expressionVisitor);
        }
        else if(ctx.callExpression() != null){
            return ctx.callExpression().accept(expressionVisitor);
        }
        else if(ctx.literal() != null){
            return ctx.literal().accept(expressionVisitor);
        }
        return ctx.accept(expressionVisitor);
     }


    /**
     * Create an Call Node
     */

     @Override
     public Call visitCallExpression(CruxParser.CallExpressionContext ctx) {
         Position myPosition = makePosition(ctx);
         Symbol mySymbol = symTab.lookup(myPosition, ctx.Identifier().getSymbol().getText());
         List<Expression> expressions = new ArrayList<>();
         for (CruxParser.Expression0Context exp : ctx.expressionList().expression0()){
             expressions.add(expressionVisitor.visitExpression0(exp));
         }
         return new Call(myPosition, mySymbol, expressions);
     }


    /**
     * visitDesignator will check for a name or ArrayAccess FYI it should account for the case when
     * the designator was dereferenced
     */

    @Override
     public Expression visitDesignator(CruxParser.DesignatorContext ctx) {
        Position myPosition = makePosition(ctx);
        if(ctx.expression0() == null || ctx.expression0(0) == null){                  //VarAccess
            Symbol mySymbol = symTab.lookup(myPosition, ctx.Identifier().getSymbol().getText());
            return new VarAccess(myPosition, mySymbol);
        }
        else{                                           //ArrayAccess
            var myIndex = ctx.expression0(0).accept(expressionVisitor);
            Symbol mySymbol = symTab.lookup(myPosition, ctx.Identifier().getSymbol().getText());
            return new ArrayAccess(myPosition, mySymbol, myIndex);
        }
    }

    /**
     * Create an Literal Node
     */

    @Override
    public Expression visitLiteral(CruxParser.LiteralContext ctx) {
        Position myPosition = makePosition(ctx);
        if(ctx.getText().equals("true")){
            return new LiteralBool(myPosition, true);
        }
        else if(ctx.getText().equals("false")){
            return new LiteralBool(myPosition, false);
        }
        else{
            return new LiteralInt(myPosition,Long.parseLong(ctx.getText()));
        }
    }

  }
}
