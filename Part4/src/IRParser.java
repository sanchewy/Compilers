import java.util.ArrayList;
import java.util.Arrays; 

public class IRParser {
	int TempTracker = 0;

	public IRParser() {

	}
	@SuppressWarnings("unchecked")
	public IRNode parseHelper(ASTNode root) {
	    ASTNode body = (ASTNode)root.childrenList.get(0);
		IRNode func_bodyNode = this.parse(body);
		ArrayList<String> generatedCode = new ArrayList<String>();
		generatedCode.add(";IR code");
		generatedCode.add(";LABEL main");
		generatedCode.add(";LINK ");
		generatedCode.addAll(func_bodyNode.code);
		generatedCode.add(";RET");
		func_bodyNode.code = generatedCode;
		return func_bodyNode;
	}
	@SuppressWarnings("unchecked")
	public IRNode parse(ASTNode root) {
		ASTNode n = root;
		ArrayList<String> generatedCode = new ArrayList<String>();
	    try {
			if (n.idPointer != null) {	// ASTNode is an ID. This should also catch 'postfix_expr' and some 'factor'.
			    System.out.println("ID triggered");
				return new IRNode(n.idPointer.name, new ArrayList<String>(), n.idPointer.type);  //temp = id name, Empty ArrayList and no code generated
			} else if (n.callBackName.equals("expr") || n.callBackName.equals("expr_prefix") || n.callBackName.equals("postfix_expr")) {
			    System.out.println("Expression triggered");
				if (n.data.equals("+")) {	//ADDOP
					IRNode leftNode = parse((ASTNode) n.childrenList.get(0));
					IRNode rightNode = parse((ASTNode) n.childrenList.get(1));
					ArrayList<String> rightSubTreeCode = rightNode.code;
					ArrayList<String> leftSubTreeCode = leftNode.code;
					String rightTemp = rightNode.temp;
					String leftTemp = leftNode.temp;
					generatedCode.addAll(leftSubTreeCode);
					generatedCode.addAll(rightSubTreeCode);
					if (leftNode.type.equals("INT") && rightNode.type.equals("INT")) {
						generatedCode.add(";ADDI "+leftTemp+" "+rightTemp+" $T"+Integer.toString(++TempTracker));
						return new IRNode("$T"+Integer.toString(TempTracker),generatedCode, "INT");
					} else if (leftNode.type.equals("FLOAT") && rightNode.type.equals("FLOAT")) {
						generatedCode.add(";ADDF "+leftTemp+" "+rightTemp+" $T"+Integer.toString(++TempTracker));
						return new IRNode("$T"+Integer.toString(TempTracker),generatedCode, "FLOAT");
					} else {
					    System.out.println("Error: for expr(+), right and left IR nodes did not match 'int' or 'float': "+n.idPointer.type);
					}
				} else if (n.data.equals("-")) {	//SUBOP
					IRNode leftNode = parse((ASTNode) n.childrenList.get(0));
					IRNode rightNode = parse((ASTNode) n.childrenList.get(1));
					ArrayList<String> rightSubTreeCode = rightNode.code;
					ArrayList<String> leftSubTreeCode = leftNode.code;
					String rightTemp = rightNode.temp;
					String leftTemp = leftNode.temp;
					generatedCode.addAll(leftSubTreeCode);
					generatedCode.addAll(rightSubTreeCode);
					if (leftNode.type.equals("INT") && rightNode.type.equals("INT")) {
						generatedCode.add(";SUBI "+leftTemp+" "+rightTemp+" $T"+Integer.toString(++TempTracker));
						return new IRNode("$T"+Integer.toString(TempTracker),generatedCode, "INT");
					} else if (leftNode.type.equals("FLOAT") && rightNode.type.equals("FLOAT")) {
						generatedCode.add(";SUBF "+leftTemp+" "+rightTemp+" $T"+Integer.toString(++TempTracker));
						return new IRNode("$T"+Integer.toString(TempTracker),generatedCode, "FLOAT");
					} else {
					    System.out.println("Error: for expr(-), right and left IR nodes did not match 'int' or 'float'");
					}
		        } else if (n.data.equals("*")) {	//MULOP
					IRNode leftNode = parse((ASTNode) n.childrenList.get(0));
					IRNode rightNode = parse((ASTNode) n.childrenList.get(1));
					ArrayList<String> rightSubTreeCode = rightNode.code;
					ArrayList<String> leftSubTreeCode = leftNode.code;
					String rightTemp = rightNode.temp;
					String leftTemp = leftNode.temp;
					generatedCode.addAll(leftSubTreeCode);
					generatedCode.addAll(rightSubTreeCode);
					if (leftNode.type.equals("INT") && rightNode.type.equals("INT")) {
						generatedCode.add(";MULTI "+leftTemp+" "+rightTemp+" $T"+Integer.toString(++TempTracker));
						return new IRNode("$T"+Integer.toString(TempTracker),generatedCode, "INT");
					} else if (leftNode.type.equals("FLOAT") && rightNode.type.equals("FLOAT")) {
						generatedCode.add(";MULTF "+leftTemp+" "+rightTemp+" $T"+Integer.toString(++TempTracker));
						return new IRNode("$T"+Integer.toString(TempTracker),generatedCode, "FLOAT");
					} else {
					    System.out.println("Error: for factor(*), right and left IR nodes did not match 'int' or 'float'");
					}
					
				} else if (n.data.equals("/")) {	//DIVOP
				    IRNode leftNode = parse((ASTNode) n.childrenList.get(0));
					IRNode rightNode = parse((ASTNode) n.childrenList.get(1));
					ArrayList<String> rightSubTreeCode = rightNode.code;
					ArrayList<String> leftSubTreeCode = leftNode.code;
					String rightTemp = rightNode.temp;
					String leftTemp = leftNode.temp;
					generatedCode.addAll(leftSubTreeCode);
					generatedCode.addAll(rightSubTreeCode);
					if (leftNode.type.equals("INT") && rightNode.type.equals("INT")) {
						generatedCode.add(";DIVI "+leftTemp+" "+rightTemp+" $T"+Integer.toString(++TempTracker));
						return new IRNode("$T"+Integer.toString(TempTracker),generatedCode, "INT");
					} else if (leftNode.type.equals("FLOAT") && rightNode.type.equals("FLOAT")) {
						generatedCode.add(";DIVF "+leftTemp+" "+rightTemp+" $T"+Integer.toString(++TempTracker));
						return new IRNode("$T"+Integer.toString(TempTracker),generatedCode, "FLOAT");
					} else {
					    System.out.println("Error: for factor (/), right and left IR nodes did not match 'int' or 'float'");
					}
				} else {	//Literal, dont need to worry about children because this should be a leaf
					String[] split = n.data.split(" ");
					if(split[0].equals("FLOAT")) {		//Float literal
						generatedCode.add(";STOREF "+split[1]+" $T"+Integer.toString(++TempTracker));
						return new IRNode("$T"+Integer.toString(TempTracker),generatedCode,"FLOAT");
					} else if (split[0].equals("INT")) {		//Int literal
						generatedCode.add(";STOREI "+split[1]+" $T"+Integer.toString(++TempTracker));
						return new IRNode("$T"+Integer.toString(TempTracker),generatedCode,"FLOAT");
					} else {
						System.out.println("Error: Literal 'expr' was not of type float or type int.");
						n.print();
					}
				}
			} else if (n.data.equals(":=")) {		//This should cover 'statement_list' which are actually just renamed 'stmt's.
		            System.out.println(":= triggered");
					IRNode rightNode = parse((ASTNode) n.childrenList.get(1));
					IRNode leftNode = parse((ASTNode) n.childrenList.get(0));
					ArrayList<String> rightSubTreeCode = rightNode.code;
					ArrayList<String> leftSubTreeCode = leftNode.code;
					String rightTemp = rightNode.temp;
					String leftTemp = leftNode.temp;		//Should just be an ID
					generatedCode.addAll(rightSubTreeCode);
					generatedCode.addAll(leftSubTreeCode);	//Should be empty
					if (leftNode.type.equals("INT")) {
						generatedCode.add(";STOREI "+rightTemp+" "+leftTemp);
					} else if (leftNode.type.equals("FLOAT")) {
						generatedCode.add(";STOREF "+rightTemp+" "+leftTemp);
					} else {
						System.out.println("LeftNode.type in n.data.equals(':=') was not 'int' or 'float': "+leftNode.type);
					}
					return new IRNode(generatedCode); 	//No temp and no type for an assignment
			} else if (n.callBackName.equals("factor")) {
			    System.out.println("Factor triggered");
				if (n.data.equals("*")) {	//MULOP
					IRNode leftNode = parse((ASTNode) n.childrenList.get(0));
					IRNode rightNode = parse((ASTNode) n.childrenList.get(1));
					ArrayList<String> rightSubTreeCode = rightNode.code;
					ArrayList<String> leftSubTreeCode = leftNode.code;
					String rightTemp = rightNode.temp;
					String leftTemp = leftNode.temp;
					generatedCode.addAll(leftSubTreeCode);
					generatedCode.addAll(rightSubTreeCode);
					if (leftNode.type.equals("INT") && rightNode.type.equals("INT")) {
						generatedCode.add(";MULTI "+leftTemp+" "+rightTemp+" $T"+Integer.toString(++TempTracker));
						return new IRNode("$T"+Integer.toString(TempTracker),generatedCode, "INT");
					} else if (leftNode.type.equals("FLOAT") && rightNode.type.equals("FLOAT")) {
						generatedCode.add(";MULTF "+leftTemp+" "+rightTemp+" $T"+Integer.toString(++TempTracker));
						return new IRNode("$T"+Integer.toString(TempTracker),generatedCode, "FLOAT");
					} else {
					    System.out.println("Error: for factor(*), right and left IR nodes did not match 'int' or 'float'");
					}
				} else if (n.data.equals("/")) {	//DIVOP
				    IRNode leftNode = parse((ASTNode) n.childrenList.get(0));
					IRNode rightNode = parse((ASTNode) n.childrenList.get(1));
					ArrayList<String> rightSubTreeCode = rightNode.code;
					ArrayList<String> leftSubTreeCode = leftNode.code;
					String rightTemp = rightNode.temp;
					String leftTemp = leftNode.temp;
					generatedCode.addAll(leftSubTreeCode);
					generatedCode.addAll(rightSubTreeCode);
					if (leftNode.type.equals("INT") && rightNode.type.equals("INT")) {
						generatedCode.add(";DIVI "+leftTemp+" "+rightTemp+" $T"+Integer.toString(++TempTracker));
						return new IRNode("$T"+Integer.toString(TempTracker),generatedCode, "INT");
					} else if (leftNode.type.equals("FLOAT") && rightNode.type.equals("FLOAT")) {
						generatedCode.add(";DIVF "+leftTemp+" "+rightTemp+" $T"+Integer.toString(++TempTracker));
						return new IRNode("$T"+Integer.toString(TempTracker),generatedCode, "FLOAT");
					} else {
					    System.out.println("Error: for factor (/), right and left IR nodes did not match 'int' or 'float'");
					}
				} else {     // Just a handed up postfix_expr > primary > (expr) | id | LITERAL
				    // I dont think this should even generate code, just add subtree code.
				    if(n.data.matches("INT \\d+") || n.data.matches("FLOAT \\d+")) {    //If the factor is just a literal
    					String[] split = n.data.split(" ");
    					if(split[0].equals("FLOAT")) {		//Float literal
    						generatedCode.add(";STOREF "+split[1]+" $T"+Integer.toString(++TempTracker));
    						return new IRNode("$T"+Integer.toString(TempTracker),generatedCode,"FLOAT");
    					} else if (split[0].equals("INT")) {		//Int literal
    						generatedCode.add(";STOREI "+split[1]+" $T"+Integer.toString(++TempTracker));
    						return new IRNode("$T"+Integer.toString(TempTracker),generatedCode,"INT");
    					} else {
    						System.out.println("Error: Literal 'factor > expr' was not of type float or type int.");
    					}
				    } else {        // factor is expr or id
        				ArrayList<ASTNode> childList = n.childrenList;
        				String type = null;
        				for(ASTNode sn: childList) {		// 'factor' can have an expression as a child, so we need to iterate over its children
        					IRNode subsubTreeNode = parse(sn);
        					type = subsubTreeNode.type;
        					generatedCode.addAll(subsubTreeNode.code);
        				}
    				    return new IRNode(generatedCode, type);	//No temp    
				    }
				} 
				// else {	//Literal, dont need to worry about children because this should be a leaf
				// 	System.out.println("Error: Factor was neither a 'mulop' nor a 'divop'.");
				// }
			} else if (n.data.equals("write")) {
			    System.out.println("write triggered");
				ArrayList<ASTNode> childList = n.childrenList;
			    for(ASTNode sn: childList) { //Can have multiple ID children
			        sn.print();
    				IRNode varNode = parse((ASTNode) sn);
    				ArrayList<String> varNodeSubTreeCode = varNode.code;
    				if (varNode.type.equals("INT")) {
    					generatedCode.addAll(varNodeSubTreeCode);	//This should be empty
    					generatedCode.add(";WRITEI "+(sn.idPointer.name));
    				} else if (varNode.type.equals("FLOAT")) {
    					generatedCode.addAll(varNodeSubTreeCode);	//This should be empty
    					generatedCode.add(";WRITEF "+(sn.idPointer.name));
    				} else if (varNode.type.equals("STRING")) {
    					generatedCode.addAll(varNodeSubTreeCode);	//This should be empty
    					generatedCode.add(";WRITES "+(sn.idPointer.name));
    				} else {
    					System.out.println("Error: IR write child ID was not of type 'int' or 'float'");
    				}
			    }
				return new IRNode(generatedCode);		//No type and no temp
			} else if (n.data.equals("read")) {
			    System.out.println("read triggered");
				ArrayList<ASTNode> childList = n.childrenList;
			    for(ASTNode sn: childList) { //Can have multiple ID children
			        sn.print();
    				IRNode varNode = parse((ASTNode) n.childrenList.get(0));		//Should only have 1 child
    				ArrayList<String> varNodeSubTreeCode = varNode.code;
    				if (varNode.type.equals("INT")) {
    					generatedCode.addAll(varNodeSubTreeCode);	//This should be empty
    					generatedCode.add(";READI "+((ASTNode) n.childrenList.get(0)).idPointer.name);
    					return new IRNode(generatedCode);		//No type and no temp
    				} else if (varNode.type.equals("FLOAT")) {
    					generatedCode.addAll(varNodeSubTreeCode);	//This should be empty
    					generatedCode.add(";READF "+((ASTNode) n.childrenList.get(0)).idPointer.name);
    					return new IRNode(generatedCode);		//No type and no temp
    				} else {
    					System.out.println("Error: IR write child ID was not of type 'int' or 'float'");
    				}
			    }
			}		
			else if (n.callBackName.equals("func_body")) {
			    System.out.println("func_body triggered");
				ArrayList<ASTNode> childList = n.childrenList;
				for(ASTNode sn: childList) {		// 'func_body' can have unlimited children, so we need to iterate over them and visit them
					IRNode subsubTreeNode = parse(sn);
					generatedCode.addAll(subsubTreeNode.code);
				}
				return new IRNode(generatedCode);	//No temp and no type for an 'func_body'
			}
			else {
			    System.out.print("Nothing matched this node: ");
			    n.print();
			}
	    } catch (NullPointerException e) {
	        System.out.println("----------------------------");
	        System.out.print("Error: null pointer on node: ");
	        n.print();
	        ArrayList<ASTNode> childList = n.childrenList;
			for(ASTNode sn: childList) {		// 'func_body' can have unlimited children, so we need to iterate over them and visit them
				sn.print();
			}
	        System.out.println("----------------------------");
	    }
		return new IRNode(new ArrayList<String>(Arrays.asList("This should never happen.")));
	}
}
