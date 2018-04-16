import java.util.EmptyStackException;
import java.util.LinkedHashMap;
import java.util.Stack;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.TerminalNode;

public class Listener extends LittleBaseListener {

	public static int Blockdepth = 1;
	// A hash map of the form <Key=scope name, value=symbol table> used to contain
	// all the sub-symbol tables
	LinkedHashMap<String, SymbolTable> st = new LinkedHashMap<String, SymbolTable>();
	// A stack to maintain the current scope name (which can be used to lookup the
	// corresponding SymbolTable through 'st')
	Stack<String> scopeStack = new Stack<String>();
	// A stack to maintain all of the Nodes and subtrees (by root) created so far.
	Stack<ASTNode> nodeStack = new Stack<ASTNode>();

	// Empty constructor, initialization of hashmap 'st' starts with 'enterProgram'
	// GLOBAL scope.

	public Listener() {

	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>
	 * The default implementation does nothing.
	 * </p>
	 */
	@Override
	public void enterProgram(LittleParser.ProgramContext ctx) {
		// Insert GLOBAL symbol table
		this.st.put("GLOBAL", new SymbolTable("GLOBAL"));
		this.scopeStack.push("GLOBAL");
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>
	 * The default implementation does nothing.
	 * </p>
	 */
	@Override
	public void exitProgram(LittleParser.ProgramContext ctx) {
		this.scopeStack.pop();
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>
	 * The default implementation does nothing.
	 * </p>
	 */
	@Override
	public void enterId(LittleParser.IdContext ctx) {
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>
	 * The default implementation does nothing.
	 * </p>
	 */
	@Override
	public void exitId(LittleParser.IdContext ctx) {
	   // System.out.println("Exit ID"+ctx.getText());
		//exitId should only ever have 2 children (id ;)
       try {
	       ASTNode<String> newId = new ASTNode<String>("id", ctx.getChild(0).getText());	//Create new id node
	       this.nodeStack.push(newId);
       } 
        catch(EmptyStackException e) {
           System.out.println("Error: Stack was empty primary.");
       } 
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>
	 * The default implementation does nothing.
	 * </p>
	 */
	@Override
	public void enterPgm_bdy(LittleParser.Pgm_bdyContext ctx) {
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>
	 * The default implementation does nothing.
	 * </p>
	 */
	@Override
	public void exitPgm_bdy(LittleParser.Pgm_bdyContext ctx) {
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>
	 * The default implementation does nothing.
	 * </p>
	 */
	@Override
	public void enterDecl(LittleParser.DeclContext ctx) {
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>
	 * The default implementation does nothing.
	 * </p>
	 */
	@Override
	public void exitDecl(LittleParser.DeclContext ctx) {
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>
	 * The default implementation does nothing.
	 * </p>
	 */
	@Override
	public void enterEmpty(LittleParser.EmptyContext ctx) {
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>
	 * The default implementation does nothing.
	 * </p>
	 */
	@Override
	public void exitEmpty(LittleParser.EmptyContext ctx) {
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>
	 * The default implementation does nothing.
	 * </p>
	 */
	@Override
	public void enterString_decl(LittleParser.String_declContext ctx) throws ParseCancellationException {
		// Insert a new Symbol into the current scope's symbol table
		if (ctx.getChildCount() != 4) { // Full declaration, should be the only possibility
			boolean exists = this.st.get(this.scopeStack.peek()).lookup(ctx.getChild(1).getText()); // Child(1) = string
																									// name/id
			// Check if the given scope already contains a variable by the given name/id
			if (exists) {
				throw new ParseCancellationException("DECLARATION ERROR " + ctx.getChild(1).getText());
			} else {
				this.st.get(this.scopeStack.peek()).insert(ctx.getChild(1).getText(),
						new EntryObj(ctx.getChild(1).getText(), ctx.getChild(0).getText(), ctx.getChild(3).getText()));
			}
		} else {
			System.out.println("Error, there was a string declaration that was not complete.");
		}
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>
	 * The default implementation does nothing.
	 * </p>
	 */
	@Override
	public void exitString_decl(LittleParser.String_declContext ctx) {
	   // System.out.println("Exit String declare"+ctx.getText());
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>
	 * The default implementation does nothing.
	 * </p>
	 */
	@Override
	public void enterStr(LittleParser.StrContext ctx) {
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>
	 * The default implementation does nothing.
	 * </p>
	 */
	@Override
	public void exitStr(LittleParser.StrContext ctx) {
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>
	 * The default implementation does nothing.
	 * </p>
	 */
	@Override
	public void enterVar_decl(LittleParser.Var_declContext ctx) throws ParseCancellationException{
		// There can be a list of variable names in a declaration, so we need to check
		// them all.
		String[] children = ctx.getChild(1).getText().split(",");
		for (String c : children) {
			c = c.replace(";", "");
			boolean exists = this.st.get(this.scopeStack.peek()).lookup(c);
			// Error out if the child exists in the given scope.
			if (exists) {
				throw new ParseCancellationException("DECLARATION ERROR " + c);
			} else {
				this.st.get(this.scopeStack.peek()).insert(c, new EntryObj(c, ctx.getChild(0).getText()));
			}
		}
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>
	 * The default implementation does nothing.
	 * </p>
	 */
	@Override
	public void exitVar_decl(LittleParser.Var_declContext ctx) {
	   // System.out.println("Exit var declare"+ctx.getText());
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>
	 * The default implementation does nothing.
	 * </p>
	 */
	@Override
	public void enterVar_type(LittleParser.Var_typeContext ctx) {
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>
	 * The default implementation does nothing.
	 * </p>
	 */
	@Override
	public void exitVar_type(LittleParser.Var_typeContext ctx) {
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>
	 * The default implementation does nothing.
	 * </p>
	 */
	@Override
	public void enterAny_type(LittleParser.Any_typeContext ctx) {
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>
	 * The default implementation does nothing.
	 * </p>
	 */
	@Override
	public void exitAny_type(LittleParser.Any_typeContext ctx) {
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>
	 * The default implementation does nothing.
	 * </p>
	 */
	@Override
	public void enterId_list(LittleParser.Id_listContext ctx) {
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>
	 * The default implementation does nothing.
	 * </p>
	 */
	@Override
	public void exitId_list(LittleParser.Id_listContext ctx) {
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>
	 * The default implementation does nothing.
	 * </p>
	 */
	@Override
	public void enterId_tail(LittleParser.Id_tailContext ctx) {
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>
	 * The default implementation does nothing.
	 * </p>
	 */
	@Override
	public void exitId_tail(LittleParser.Id_tailContext ctx) {
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>
	 * The default implementation does nothing.
	 * </p>
	 */
	@Override
	public void enterParam_decl_list(LittleParser.Param_decl_listContext ctx) {
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>
	 * The default implementation does nothing.
	 * </p>
	 */
	@Override
	public void exitParam_decl_list(LittleParser.Param_decl_listContext ctx) {
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>
	 * The default implementation does nothing.
	 * </p>
	 */
	@Override
	public void enterParam_decl(LittleParser.Param_declContext ctx) {
		String c = ctx.getChild(1).getText();
		boolean exists = this.st.get(this.scopeStack.peek()).lookup(c);
		if (exists) {
			System.out.println("DECLARATION  ERROR  " + c);
		} else {
			this.st.get(this.scopeStack.peek()).insert(c, new EntryObj(c, ctx.getChild(0).getText()));
		}
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>
	 * The default implementation does nothing.
	 * </p>
	 */
	@Override
	public void exitParam_decl(LittleParser.Param_declContext ctx) {
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>
	 * The default implementation does nothing.
	 * </p>
	 */
	@Override
	public void enterParam_decl_tail(LittleParser.Param_decl_tailContext ctx) {
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>
	 * The default implementation does nothing.
	 * </p>
	 */
	@Override
	public void exitParam_decl_tail(LittleParser.Param_decl_tailContext ctx) {
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>
	 * The default implementation does nothing.
	 * </p>
	 */
	@Override
	public void enterFunc_declarations(LittleParser.Func_declarationsContext ctx) {
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>
	 * The default implementation does nothing.
	 * </p>
	 */
	@Override
	public void exitFunc_declarations(LittleParser.Func_declarationsContext ctx) {
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>
	 * The default implementation does nothing.
	 * </p>
	 */
	@Override
	public void enterFunc_decl(LittleParser.Func_declContext ctx) {
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>
	 * The default implementation does nothing.
	 * </p>
	 */
	@Override
	public void exitFunc_decl(LittleParser.Func_declContext ctx) {
		this.scopeStack.pop();
	}

	@Override
	public void enterFunc_name(LittleParser.Func_nameContext ctx) {
		// for(int i = 0; i < ctx.getChildCount(); i++) {
		// System.out.println("Child " + i + " " + ctx.getChild(i).getText());
		// }
		// Add a new symbol table to the hash of hashes
		this.st.put(ctx.getChild(0).getText(), new SymbolTable(ctx.getChild(0).getText()));
		// Push the new scope name onto the scope tracker stack
		this.scopeStack.push(ctx.getChild(0).getText());
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>
	 * The default implementation does nothing.
	 * </p>
	 */
	@Override
	public void exitFunc_name(LittleParser.Func_nameContext ctx) {
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>
	 * The default implementation does nothing.
	 * </p>
	 */

	/**
	 * {@inheritDoc}
	 *
	 * <p>
	 * The default implementation does nothing.
	 * </p>
	 */
	@Override
	public void enterFunc_body(LittleParser.Func_bodyContext ctx) {
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>
	 * The default implementation does nothing.
	 * </p>
	 */
	@Override
	public void exitFunc_body(LittleParser.Func_bodyContext ctx) {
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>
	 * The default implementation does nothing.
	 * </p>
	 */
	@Override
	public void enterStmt_list(LittleParser.Stmt_listContext ctx) {
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>
	 * The default implementation does nothing.
	 * </p>
	 */
	@Override
	public void exitStmt_list(LittleParser.Stmt_listContext ctx) {
	   // System.out.println("Exit statement list"+ctx.getText());
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>
	 * The default implementation does nothing.
	 * </p>
	 */
	@Override
	public void enterStmt(LittleParser.StmtContext ctx) {
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>
	 * The default implementation does nothing.
	 * </p>
	 */
	@Override
	public void exitStmt(LittleParser.StmtContext ctx) {
	   // System.out.println("Exit statement"+ctx.getText());
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>
	 * The default implementation does nothing.
	 * </p>
	 */
	@Override
	public void enterBase_stmt(LittleParser.Base_stmtContext ctx) {
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>
	 * The default implementation does nothing.
	 * </p>
	 */
	@Override
	public void exitBase_stmt(LittleParser.Base_stmtContext ctx) {
	   // System.out.println("Exit base statement"+ctx.getText());
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>
	 * The default implementation does nothing.
	 * </p>
	 */
	@Override
	public void enterAssign_stmt(LittleParser.Assign_stmtContext ctx) {
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>
	 * The default implementation does nothing.
	 * </p>
	 */
	@Override
	public void exitAssign_stmt(LittleParser.Assign_stmtContext ctx) {
	   // System.out.println("Exit assign statement"+ctx.getText());
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>
	 * The default implementation does nothing.
	 * </p>
	 */
	@Override
	public void enterAssign_expr(LittleParser.Assign_exprContext ctx) {
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>
	 * The default implementation does nothing.
	 * </p>
	 */
	@Override
	public void exitAssign_expr(LittleParser.Assign_exprContext ctx) {
	   // System.out.println("Exit assign expression"+ctx.getText());
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>
	 * The default implementation does nothing.
	 * </p>
	 */
	@Override
	public void enterRead_stmt(LittleParser.Read_stmtContext ctx) {
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>
	 * The default implementation does nothing.
	 * </p>
	 */
	@Override
	public void exitRead_stmt(LittleParser.Read_stmtContext ctx) {
	   // System.out.println("Exit read statement "+ctx.getText());
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>
	 * The default implementation does nothing.
	 * </p>
	 */
	@Override
	public void enterWrite_stmt(LittleParser.Write_stmtContext ctx) {
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>
	 * The default implementation does nothing.
	 * </p>
	 */
	@Override
	public void exitWrite_stmt(LittleParser.Write_stmtContext ctx) {
	   // System.out.println("Exit write statement "+ctx.getText());
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>
	 * The default implementation does nothing.
	 * </p>
	 */
	@Override
	public void enterReturn_stmt(LittleParser.Return_stmtContext ctx) {
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>
	 * The default implementation does nothing.
	 * </p>
	 */
	@Override
	public void exitReturn_stmt(LittleParser.Return_stmtContext ctx) {
	   // System.out.println("Exit return statement "+ctx.getText());
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>
	 * The default implementation does nothing.
	 * </p>
	 */
	@Override
	public void enterExpr(LittleParser.ExprContext ctx) {
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>
	 * The default implementation does nothing.
	 * </p>
	 */
	@Override
	public void exitExpr(LittleParser.ExprContext ctx) {
	   // System.out.println("Exit Expression Context: "+ctx.getText());
		
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>
	 * The default implementation does nothing.
	 * </p>
	 */
	@Override
	public void enterExpr_prefix(LittleParser.Expr_prefixContext ctx) {
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>
	 * The default implementation does nothing.
	 * </p>
	 */
	@Override
	public void exitExpr_prefix(LittleParser.Expr_prefixContext ctx) {
	   // System.out.println("Exit Expression Prefix Context: "+ctx.getText());
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>
	 * The default implementation does nothing.
	 * </p>
	 */
	@Override
	public void enterFactor(LittleParser.FactorContext ctx) {
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>
	 * The default implementation does nothing.
	 * </p>
	 */
	@Override
	public void exitFactor(LittleParser.FactorContext ctx) {
	   // System.out.println("Exit factor "+ctx.getText());
	   int numChildren = ctx.getChildCount();
	   if(numChildren == 2) {   // postfix_expr ; 
		   ASTNode postfix_expr = this.nodeStack.pop();
		   postfix_expr.callBackName = "factor";
	       this.nodeStack.push(postfix_expr);
	   }else if(numChildren == 3) {		// factor_prefix postfix_expr ;
		   ASTNode factor_prefix = this.nodeStack.pop();
		   ASTNode postfix = this.nodeStack.pop();
		   factor_prefix.addChild(postfix);		// (postfix_expr) MULOP (factor_prefix)
		   factor_prefix.callBackName = "factor";
		   this.nodeStack.push(factor_prefix);
	   } else {
		   System.out.println("Error: exit factor had neither 2 nor 3 children.");
	   }
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>
	 * The default implementation does nothing.
	 * </p>
	 */
	@Override
	public void enterFactor_prefix(LittleParser.Factor_prefixContext ctx) {
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>
	 * The default implementation does nothing.
	 * </p>
	 */
	@Override
	public void exitFactor_prefix(LittleParser.Factor_prefixContext ctx) {
	   // System.out.println("Exit Factor Prefix Context: "+ctx.getText());
	   int numChildren = ctx.getChildCount();
	   if(numChildren == 0) {   // Empty factor prefix
	       ASTNode<String> emptyNode = new ASTNode<String>("factor_prefix", "lambda");  //Lambda node keeps stack straight when popping for other subtrees.
	       this.nodeStack.push(emptyNode);
	   }else if(numChildren == 2) {		//postfix_expr, MULOP
		   ASTNode empty_factor_prefix = this.nodeStack.pop();
		   ASTNode postfix = this.nodeStack.pop();
		   ASTNode MULOP = new ASTNode("factor_prefix", "mulop");
		   MULOP.addChild(postfix);
		   this.nodeStack.push(MULOP);
	   }
	   else if(numChildren == 3) {	// factor_prefix, postfix_expr, MULOP
		   ASTNode factor_prefix = this.nodeStack.pop();
		   ASTNode postfix = this.nodeStack.pop();
		   ASTNode MULOP = new ASTNode("factor_prefix", "mulop");
		   MULOP.addChild(factor_prefix);
		   MULOP.addChild(postfix);
		   this.nodeStack.push(MULOP);
	   } else {
		   System.out.println("Error: exit factor_prefix had neither 0 nor 3 children.");
	   }
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>
	 * The default implementation does nothing.
	 * </p>
	 */
	@Override
	public void enterPostfix_expr(LittleParser.Postfix_exprContext ctx) {
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>
	 * The default implementation does nothing.
	 * </p>
	 */
	@Override
	public void exitPostfix_expr(LittleParser.Postfix_exprContext ctx) {
	   // System.out.println("Exit postfix expresion "+ctx.getText());
       try {    // primary or call_expr
           ASTNode subtreeRoot = this.nodeStack.peek();
           if(subtreeRoot.callBackName.equals("primary")) { // Handle primary case
               subtreeRoot.callBackName = "postfix_expr";
           }
           else {   // Should not be 'call_expr' (only main function) or anything else
               System.out.println("Error: callBackName for node in exit postfix expr was not 'primary'");
           }
       } 
        catch(EmptyStackException e) {
           System.out.println("Error: Stack was empty postfix_expr.")
       }
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>
	 * The default implementation does nothing.
	 * </p>
	 */
	@Override
	public void enterCall_expr(LittleParser.Call_exprContext ctx) {
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>
	 * The default implementation does nothing.
	 * </p>
	 */
	@Override
	public void exitCall_expr(LittleParser.Call_exprContext ctx) {
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>
	 * The default implementation does nothing.
	 * </p>
	 */
	@Override
	public void enterExpr_list(LittleParser.Expr_listContext ctx) {
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>
	 * The default implementation does nothing.
	 * </p>
	 */
	@Override
	public void exitExpr_list(LittleParser.Expr_listContext ctx) {
	   // System.out.println("Exit expression list "+ctx.getText());
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>
	 * The default implementation does nothing.
	 * </p>
	 */
	@Override
	public void enterExpr_list_tail(LittleParser.Expr_list_tailContext ctx) {
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>
	 * The default implementation does nothing.
	 * </p>
	 */
	@Override
	public void exitExpr_list_tail(LittleParser.Expr_list_tailContext ctx) {
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>
	 * The default implementation does nothing.
	 * </p>
	 */
	@Override
	public void enterPrimary(LittleParser.PrimaryContext ctx) {
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>
	 * The default implementation does nothing.
	 * </p>
	 */
	@Override
	public void exitPrimary(LittleParser.PrimaryContext ctx) {
	   // System.out.println("Exit primary "+ctx.getText());
	   int numChildren = ctx.getChildCount();
	   if(numChildren == 4) {      // OPENPAR expr CLOSEPAR ;
	       try {
	           ASTNode subtreeRoot = this.nodeStack.peek();
	           subtreeRoot.callBackName = "primary";
           } 
            catch(EmptyStackException e) {
            	System.out.println("Error: Stack was empty primary.");
	       }   
	   } else if(numChildren == 2) {       // {id | INTLITERAL | FLOATLITERAL} ;
		   ASTNode checkId = this.nodeStack.peek();
		   if(checkId.callBackName == "id") {		//If the child is an ID id already has a node on the stack
			   checkId.callBackName = "primary";
		   }
		   else {		//Otherwise, it is a float literal or an intliteral
		       try {
	   	           ASTNode<String> newPrimary = new ASTNode<String>("primary", ctx.getChild(0).getText());	//Create new primary node
	   	           this.nodeStack.push(newPrimary);
	           } 
	            catch(EmptyStackException e) {
		           System.out.println("Error: Stack was empty primary.");
		       }   
		   }
	   } else {
	       System.out.println("Error: exitPrimary had an 'impossible' number of children.");
	   }
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>
	 * The default implementation does nothing.
	 * </p>
	 */
	@Override
	public void enterIf_stmt(LittleParser.If_stmtContext ctx) {
		String block = "BLOCK " + Integer.toString(Blockdepth);
		this.st.put(block, new SymbolTable(block));
		this.scopeStack.push(block);
		Blockdepth++;
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>
	 * The default implementation does nothing.
	 * </p>
	 */
	@Override
	public void exitIf_stmt(LittleParser.If_stmtContext ctx) {
		this.scopeStack.pop();

	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>
	 * The default implementation does nothing.
	 * </p>
	 */
	@Override
	public void enterElse_part(LittleParser.Else_partContext ctx) {
		if (ctx.getChildCount() > 0) {
			String block = "BLOCK " + Integer.toString(Blockdepth);
			this.st.put(block, new SymbolTable(block));
			this.scopeStack.push(block);
			Blockdepth++;
		} else {

		}

	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>
	 * The default implementation does nothing.
	 * </p>
	 */
	@Override
	public void exitElse_part(LittleParser.Else_partContext ctx) {
		if (ctx.getChildCount() > 0) {
			this.scopeStack.pop();
		}

	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>
	 * The default implementation does nothing.
	 * </p>
	 */
	@Override
	public void enterCond(LittleParser.CondContext ctx) {
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>
	 * The default implementation does nothing.
	 * </p>
	 */
	@Override
	public void exitCond(LittleParser.CondContext ctx) {
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>
	 * The default implementation does nothing.
	 * </p>
	 */
	@Override
	public void enterWhile_stmt(LittleParser.While_stmtContext ctx) {
		String block = "BLOCK " + Integer.toString(Blockdepth);
		this.st.put(block, new SymbolTable(block));
		this.scopeStack.push(block);
		Blockdepth++;

	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>
	 * The default implementation does nothing.
	 * </p>
	 */
	@Override
	public void exitWhile_stmt(LittleParser.While_stmtContext ctx) {
		this.scopeStack.pop();

	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>
	 * The default implementation does nothing.
	 * </p>
	 */
	@Override
	public void enterEveryRule(ParserRuleContext ctx) {
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>
	 * The default implementation does nothing.
	 * </p>
	 */
	@Override
	public void exitEveryRule(ParserRuleContext ctx) {
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>
	 * The default implementation does nothing.
	 * </p>
	 */
	@Override
	public void visitTerminal(TerminalNode node) {
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>
	 * The default implementation does nothing.
	 * </p>
	 */
	@Override
	public void visitErrorNode(ErrorNode node) {
	}

	// Additional Methods

	public LinkedHashMap<String, SymbolTable> getSymbolTable() {
		return st;
	}

}
