import java.util.ArrayList;

// Keinan is giving the listener a shot rather than using a visitor implementation. This is his node class.

public class ASTNode<T> {
	String callBackName; // This represents the grammar production / rule that created this node
	ArrayList<ASTNode> childrenList = new ArrayList<ASTNode>();
	ASTNode parent;
	String data;

	public ASTNode(String cbn, String data) {
		this.callBackName = cbn;
		this.data = data;
	}
	
	public void addChild(ASTNode child) {
	    this.childrenList.add(child);
	}
	
	public void setParent(ASTNode parent) {
	    this.parent = parent;
	}
	
	public void prettyPrint() {
		System.out.println(";CallBackName: "+callBackName+" Data: "+data.toString());
		for(ASTNode n: childrenList) {
			System.out.print(callBackName+" child: ");
			n.prettyPrint();
		}
	}
	
	public void print() {
	    System.out.println(";CallBackName: "+callBackName+" Data: "+data.toString());
	}

}
