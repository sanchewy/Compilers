import java.util.ArrayList;

// Keinan is giving the listener a shot rather than using a visitor implementation. This is his node class.

public class ASTNode<T> {
	String callBackName; // This represents the grammar production / rule that created this node
	ArrayList<ASTNode> childrenList = new ArrayList<ASTNode>();
	ASTNode parent;
	T data;

	public ASTNode(String cbn, T data) {
		this.callBackName = cbn;
		this.data = data;
	}
	
	public void addChild(ASTNode child) {
	    this.childrenList.append(child);
	}
	
	public void setParent(ASTNode parent) {
	    this.parent = parent;
	}

}
