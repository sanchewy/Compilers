import java.util.ArrayList;

public class IRNode {
	String temp;		//temp variable to which the generated code stores.
	ArrayList<String> code = new ArrayList<String>();
	String type;
	
	public IRNode(ArrayList<String> c) {
		this.code = c;
	}
	
	public IRNode(String tmp, ArrayList<String> c) {
		this.temp = tmp;
		this.code = c;
	}
	
	public IRNode(ArrayList<String> c, String type) {
		this.type = type;
		this.code = c;
	}
	
	public IRNode(String tmp, ArrayList<String> c, String type) {
		this.temp = tmp;
		this.code = c;
		this.type = type;
	}
	
}
