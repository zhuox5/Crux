// Generated from crux/pt/Crux.g4 by ANTLR 4.7.2
package crux.pt;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link CruxParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface CruxVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link CruxParser#program}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProgram(CruxParser.ProgramContext ctx);
	/**
	 * Visit a parse tree produced by {@link CruxParser#declarationList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDeclarationList(CruxParser.DeclarationListContext ctx);
	/**
	 * Visit a parse tree produced by {@link CruxParser#declaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDeclaration(CruxParser.DeclarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link CruxParser#variableDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVariableDeclaration(CruxParser.VariableDeclarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link CruxParser#arrayDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArrayDeclaration(CruxParser.ArrayDeclarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link CruxParser#functionDefinition}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunctionDefinition(CruxParser.FunctionDefinitionContext ctx);
	/**
	 * Visit a parse tree produced by {@link CruxParser#op0}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOp0(CruxParser.Op0Context ctx);
	/**
	 * Visit a parse tree produced by {@link CruxParser#op1}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOp1(CruxParser.Op1Context ctx);
	/**
	 * Visit a parse tree produced by {@link CruxParser#op2}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOp2(CruxParser.Op2Context ctx);
	/**
	 * Visit a parse tree produced by {@link CruxParser#expression0}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpression0(CruxParser.Expression0Context ctx);
	/**
	 * Visit a parse tree produced by {@link CruxParser#expression1}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpression1(CruxParser.Expression1Context ctx);
	/**
	 * Visit a parse tree produced by {@link CruxParser#expression2}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpression2(CruxParser.Expression2Context ctx);
	/**
	 * Visit a parse tree produced by {@link CruxParser#expression3}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpression3(CruxParser.Expression3Context ctx);
	/**
	 * Visit a parse tree produced by {@link CruxParser#designator}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDesignator(CruxParser.DesignatorContext ctx);
	/**
	 * Visit a parse tree produced by {@link CruxParser#type}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitType(CruxParser.TypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link CruxParser#literal}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLiteral(CruxParser.LiteralContext ctx);
	/**
	 * Visit a parse tree produced by {@link CruxParser#callExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCallExpression(CruxParser.CallExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link CruxParser#expressionList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpressionList(CruxParser.ExpressionListContext ctx);
	/**
	 * Visit a parse tree produced by {@link CruxParser#parameter}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParameter(CruxParser.ParameterContext ctx);
	/**
	 * Visit a parse tree produced by {@link CruxParser#parameterList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParameterList(CruxParser.ParameterListContext ctx);
	/**
	 * Visit a parse tree produced by {@link CruxParser#assignmentStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAssignmentStatement(CruxParser.AssignmentStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link CruxParser#assignmentStatementNoSemi}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAssignmentStatementNoSemi(CruxParser.AssignmentStatementNoSemiContext ctx);
	/**
	 * Visit a parse tree produced by {@link CruxParser#callStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCallStatement(CruxParser.CallStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link CruxParser#ifStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIfStatement(CruxParser.IfStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link CruxParser#forStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitForStatement(CruxParser.ForStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link CruxParser#breakStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBreakStatement(CruxParser.BreakStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link CruxParser#returnStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitReturnStatement(CruxParser.ReturnStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link CruxParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStatement(CruxParser.StatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link CruxParser#statementList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStatementList(CruxParser.StatementListContext ctx);
	/**
	 * Visit a parse tree produced by {@link CruxParser#statementBlock}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStatementBlock(CruxParser.StatementBlockContext ctx);
}