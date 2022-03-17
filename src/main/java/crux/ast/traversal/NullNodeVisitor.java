package crux.ast.traversal;

import crux.ast.*;

public class NullNodeVisitor<T> implements NodeVisitor<T> {
  @Override
  public T visit(ArrayAccess access) {
    return null;
  }

  @Override
  public T visit(ArrayDeclaration arrayDeclaration) {
    return null;
  }

  @Override
  public T visit(Assignment assignment) {
    return null;
  }

  @Override
  public T visit(Break brk) {
    return null;
  }

  @Override
  public T visit(Call call) {
    return null;
  }

  @Override
  public T visit(DeclarationList declarationList) {
    return null;
  }

  @Override
  public T visit(For forloop) {
    return null;
  }

  @Override
  public T visit(FunctionDefinition functionDefinition) {
    return null;
  }

  @Override
  public T visit(IfElseBranch ifElseBranch) {
    return null;
  }

  @Override
  public T visit(LiteralBool literalBool) {
    return null;
  }

  @Override
  public T visit(LiteralInt literalInt) {
    return null;
  }

  @Override
  public T visit(OpExpr operation) {
    return null;
  }

  @Override
  public T visit(Return ret) {
    return null;
  }

  @Override
  public T visit(StatementList statementList) {
    return null;
  }

  @Override
  public T visit(VariableDeclaration variableDeclaration) {
    return null;
  }

  @Override
  public T visit(VarAccess vaccess) {
    return null;
  }
}
