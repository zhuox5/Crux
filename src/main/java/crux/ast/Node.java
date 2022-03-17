package crux.ast;

import crux.ast.traversal.NodeVisitor;

import java.util.List;

/**
 * Interface for AST Node
 */
public interface Node {
  Position getPosition();

  List<Node> getChildren();

  <T> T accept(NodeVisitor<? extends T> visitor);
}
