import java.util.ArrayList;
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
	// An AST generated using the listener callbacks triggered through the driver
	// using the ParseTreeWalker
	ASTNode finalAST;
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
		try {
            ASTNode pgm_bdy = this.nodeStack.pop();
			ASTNode id = this.nodeStack.pop(); // We actually don't need this, but it will be on the stack anyway.
			if (!pgm_bdy.callBackName.equals("pgm_bdy") || !id.callBackName.equals("id")) {
				System.out.println(";Error: In exit program, the 2 nodes on top of stack didn't match the pattern (id, pgm_bdy).");
			} else {
				pgm_bdy.callBackName = "program";
				this.finalAST = pgm_bdy;
				while (!this.nodeStack.empty()) {
					System.out.println(";Error: on exit pogram the nodeStack in Listener.java was not empty.");
	                System.out.println(";   ");
	                this.nodeStack.pop().print();
				}
			}
		} catch (EmptyStackException e) {
			System.out.println("Error: Stack was empty program.");
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
		// exitId should only ever have 2 children (id ;)
		try {
			ASTNode<String> newId = new ASTNode<String>("id", ctx.getChild(0).getText()); // Create new id node
			this.nodeStack.push(newId);
		} catch (EmptyStackException e) {
			System.out.println("Error: Stack was empty id.");
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
		// pgm_bdy : decl func_declarations;
		try {
			ASTNode func_declarations = this.nodeStack.peek();
			if (!func_declarations.callBackName.equals("func_declarations")) {
				System.out.println("Error: Top of node stack for exit_pgm_bdy was not a func_declarations node.");
			} else {
				func_declarations.callBackName = "pgm_bdy";
			}
		} catch (EmptyStackException e) {
			System.out.println("Error: Stack was empty exit pgm_bdy.");
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
		// string_decl : STRING id ASSIGNOP str SEMICOLON; 
		// This code only exists to remove an 'id' from the stack
		ASTNode id = this.nodeStack.pop();     // Pop and discard
		if (!id.callBackName.equals("id")) {
		    System.out.println("Error: exit string_decl top of stack was not an 'id'");
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
	public void enterVar_decl(LittleParser.Var_declContext ctx) throws ParseCancellationException {
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
		// var_decl : var_type id_list;
		// This code only exists to remove an 'id_list' from the stack
		ASTNode id_list = this.nodeStack.pop();     // Pop and discard
		if (!id_list.callBackName.equals("id_list")) {
		    System.out.println("Error: exit var_decl top of stack was not an 'id_list'");
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
	@SuppressWarnings("unchecked")
	public void exitId_list(LittleParser.Id_listContext ctx) {
		// id id_tail;
		ASTNode id_tail = this.nodeStack.pop();
		ASTNode id = this.nodeStack.pop();
		ArrayList<ASTNode> beginning = new ArrayList<ASTNode>(); // Keep children nodes in order by adding to the front.
		beginning.add(id);
		beginning.addAll(id_tail.childrenList); // If id_tail was "lambda" this will be empty, but that doesn't matter.
		id_tail.childrenList = beginning;
		id_tail.data = "id_list";
		id_tail.callBackName = "id_list";
		this.nodeStack.push(id_tail);
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
	@SuppressWarnings("unchecked")
	public void exitId_tail(LittleParser.Id_tailContext ctx) {
		// COMMA id id_tail | SEMICOLON | ;
		int numChildren = ctx.getChildCount();
		if (numChildren == 0 || numChildren == 1) { // empty tail or semicolon
			ASTNode<String> tail = new ASTNode<String>("id_tail", "lambda");
			this.nodeStack.push(tail);
		} else if (numChildren == 3) { // COMMA id id_tail
			ASTNode id_tail = this.nodeStack.pop();
			ASTNode id = this.nodeStack.pop();
			ArrayList<ASTNode> beginning = new ArrayList<ASTNode>(); // Keep children nodes in order by adding to the
																		// front.
			beginning.add(id);
			beginning.addAll(id_tail.childrenList); // If id_tail was "lambda" this will be empty, but that doesn't
													// matter.
			id_tail.childrenList = beginning;
			id_tail.data = "id_list";
			this.nodeStack.push(id_tail);
		} else {
			System.out.println("Error: exit id_tail had neither 0 nor 3 children.");
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
	@SuppressWarnings("unchecked")
	public void exitFunc_declarations(LittleParser.Func_declarationsContext ctx) {
		// We will not have return types, or param_decl_list
		// func_declarations : func_decl func_declarations | ;
		if (ctx.getChildCount() == 0) { // empty func_declarations
			ASTNode empty_func_declarations = new ASTNode("func_declarations", "lambda");
			this.nodeStack.push(empty_func_declarations);
		} else if (ctx.getChildCount() == 2 && ctx.getChild(1).getText().equals("")) { // func_decl
			ASTNode empty_func_delcarations = this.nodeStack.pop(); // Pop and discard
			if (!empty_func_delcarations.data.equals("lambda") || !empty_func_delcarations.callBackName.equals("func_declarations")) {
				System.out.println("Error: On exit_func_declarations with 1 ctx child, the top of stack was not a lambda func_declarations.");
			}
			ASTNode func_decl = this.nodeStack.peek();
			if (!empty_func_delcarations.callBackName.equals("func_decl")) {
			    func_decl.callBackName = "func_declarations";
			} else {
			    System.out.println("Error: stack peek for func_declarations was not of type 'func_decl'");
			}
		} else if (ctx.getChildCount() == 2 && !ctx.getChild(1).getText().equals("")) { // func_decl func_declarations
			ASTNode func_declarations = this.nodeStack.pop();
			ArrayList<ASTNode> oldList = func_declarations.childrenList;
			ASTNode func_decl = this.nodeStack.pop();
			ArrayList<ASTNode> beginning = new ArrayList<ASTNode>(); // Used to maintain order of children nodes
																		// (func_decl)
																		// for func_declarations
			beginning.add(func_decl);
			for (ASTNode n : oldList) {
				beginning.add(n);
			}
			func_declarations.childrenList = beginning;
			func_declarations.callBackName = "func_declarations";
			this.nodeStack.push(func_declarations);
		} else {
			System.out.println("Error: exit func_declarations didn't have 0, 1, or 2 children.");
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
		// We will not have return types, or param_decl_list because we only have main()
		// FUNCTION any_type func_name OPENPAR param_decl_list CLOSEPAR BEGIN func_body
		// END ;
		if (!(ctx.getChildCount() == 8 || ctx.getChildCount() == 9)) {
			System.out.println("Error: exit func_decl undexpected number of children (not 8 or 9): "
					+ Integer.toString(ctx.getChildCount()));
		}
		try { // func_name func_body
			ASTNode func_body = this.nodeStack.pop();
			ASTNode func_name = this.nodeStack.pop();
			if (func_body.callBackName.equals("func_body") && func_name.callBackName.equals("func_name")) {
				// change callBackName to func_decl
				func_name.callBackName = "func_decl";
				func_name.addChild(func_body);
				this.nodeStack.push(func_name);
			} else { // Top of stack was not id node.
				System.out.println(
						"Error: callBackName for node in exit func_decl expr was not 'func_name' or 'func_body'");
			}
		} catch (EmptyStackException e) {
			System.out.println("Error: Stack was empty func_decl.");
		}
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
		try { // id
			ASTNode id = this.nodeStack.peek();
			if (id.callBackName.equals("id")) { // change callBackName to func_name
				id.callBackName = "func_name";
			} else { // Top of stack was not id node.
				System.out.println("Error: callBackName for node in exit func_name expr was not 'id'");
			}
		} catch (EmptyStackException e) {
			System.out.println("Error: Stack was empty func_name.");
		}
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
		// func_body : decl stmt_list;
		// I think we can ignore 'decl' here because that is taken care of in the symbol
		// table (and we dont write assembly for decl).
		try { // stmt_list
			ASTNode stmt_list = this.nodeStack.peek();
			if (stmt_list.callBackName.equals("stmt_list")) { // change callBackName to func_body
				stmt_list.callBackName = "func_body";
			} else { // Top of stack was not id node.
				System.out.println("Error: callBackName for node in exit func_body expr was not 'stmt_list'");
			}
		} catch (EmptyStackException e) {
			System.out.println("Error: Stack was empty func_body.");
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
	@SuppressWarnings("unchecked")
	public void exitStmt_list(LittleParser.Stmt_listContext ctx) {
		// System.out.println("Exit statement list"+ctx.getText());
		// stmt_list : stmt stmt_list | ;
		int numChildren = ctx.getChildCount();
		if (numChildren == 0) { // empty list
			ASTNode empty_stmt_list = new ASTNode("stmt_list", "lambda");
			this.nodeStack.push(empty_stmt_list);
		} else if (numChildren == 1) { // stmt
			ASTNode empty_stmt_list = this.nodeStack.pop(); // Pop and discard lambda node
			if (!empty_stmt_list.data.equals("lambda")) {
				System.out.println(
						"Error: On exit_stmt_list there was 1 child, but top of stack was not lambda stmt_list");
			}
			ASTNode stmt = this.nodeStack.peek();
			stmt.callBackName = "stmt_list";
		} else if (numChildren == 2) { // stmt stmt_list
			ASTNode stmt_list = this.nodeStack.pop();
			ArrayList<ASTNode> oldList = stmt_list.childrenList;
			ASTNode stmt = this.nodeStack.pop();
			stmt.callBackName = "stmt_list";
			ArrayList<ASTNode> beginning = new ArrayList<ASTNode>(); // Used to maintain order of children nodes (stmts)
																		// for stmt_list
			beginning.add(stmt);
			for (ASTNode n : oldList) {
				beginning.add(n);
			}
			stmt_list.childrenList = beginning;
			this.nodeStack.push(stmt_list);
		} else {
			System.out.println("Error: exit stmt_list didn't have 0, 1, or 2 children.");
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
		// stmt : base_stmt | if_stmt | while_stmt;
		// System.out.println(";exit statement " + this.nodeStack.peek().callBackName);
		if (this.nodeStack.peek().callBackName.equals("base_stmt")) {
			this.nodeStack.peek().callBackName = "stmt";
		} else if (this.nodeStack.peek().callBackName.equals("if_stmt")) {
			// TODO: Part 2 implement if statements
			this.nodeStack.peek().callBackName = "stmt";
		} else if (this.nodeStack.peek().callBackName.equals("while_stmt")) {
			// TODO: Part 2 implement while statements
			this.nodeStack.peek().callBackName = "stmt";
		} else {
			System.out.println(
					"Error: in exit stmt the nodeStack.pop.callBackName wasn't base_stmt or if_stmt or while_stmt.");
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
		// base_stmt : assign_stmt | read_stmt | write_stmt | return_stmt;
		if (this.nodeStack.peek().callBackName.equals("assign_stmt")
				|| this.nodeStack.peek().callBackName.equals("read_stmt")
				|| this.nodeStack.peek().callBackName.equals("write_stmt")) {
			ASTNode stmt = this.nodeStack.peek();
			stmt.callBackName = "base_stmt";
		} else {
			System.out.println(
					"Error: in exit base_stmt the nodeStack.pop.callBackName wasn't assign_stmt or read_stmt or write_stmt.");
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
		// assign_expr SEMICOLON
		ASTNode assign_expr;
		if (this.nodeStack.peek().callBackName.equals("assign_expr")) {
			assign_expr = this.nodeStack.peek();
			assign_expr.callBackName = "assign_stmt";
		} else {
			System.out.println("Error: in assign_stmt the nodeStack.pop.callBackName wasn't assign_expr.");
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
		// assign_expr : id ASSIGNOP expr;
		ASTNode expr = this.nodeStack.pop();
		ASTNode id = this.nodeStack.pop();
		//System.out.println(";the expression is "+ expr.callBackName+ " and the id is "+id.callBackName);
		if (expr.callBackName.equals("expr") && id.callBackName.equals("id")) {

			ASTNode assignop = new ASTNode("assign_expr", ":=");
			assignop.addChild(id);
			assignop.addChild(expr);
			this.nodeStack.push(assignop);

		} else {
			System.out.println(";Error: in assign_expr the nodeStack.pop.callBackName wasn't expr or id");
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
	@SuppressWarnings("unchecked")
	public void exitRead_stmt(LittleParser.Read_stmtContext ctx) {
		// System.out.println("Exit read statement "+ctx.getText());
		// READ OPENPAR id_list CLOSEPAR SEMICOLON;
		ASTNode id_list = this.nodeStack.pop();
		ASTNode read = new ASTNode("read_stmt", "read");
		ArrayList<ASTNode> children = id_list.childrenList;
		for (ASTNode n : children) { // Add each of the ids from the id_list to the read node.
			read.addChild(n);
		}
		this.nodeStack.push(read);
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
	@SuppressWarnings("unchecked")
	public void exitWrite_stmt(LittleParser.Write_stmtContext ctx) {
		// System.out.println("Exit write statement "+ctx.getText());
		// WRITE OPENPAR id_list CLOSEPAR SEMICOLON;
		ASTNode id_list = this.nodeStack.pop();
		ASTNode write = new ASTNode("write_stmt", "write");
		ArrayList<ASTNode> children = id_list.childrenList;
		for (ASTNode n : children) { // Add each of the ids from the id_list to the write node.
			write.addChild(n);
		}
		this.nodeStack.push(write);
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
		// expr : expr_prefix factor;
		int numChildren = ctx.getChildCount();
		if (ctx.getChild(0).getText().equals("")) { // factor
			ASTNode factor = this.nodeStack.pop();
			ASTNode empty_expr_prefix = this.nodeStack.pop(); // Pop and discard lambda node
			if (!empty_expr_prefix.data.equals("lambda")) {
				System.out
						.println("Error: On exit_expr there was 1 child, but top of stack was not lambda expr_prefix.");
			}
			factor.callBackName = "expr";
			this.nodeStack.push(factor);
		} else if (!ctx.getChild(0).getText().equals("")) { // expr_prefix factor
			ASTNode factor = this.nodeStack.pop();
			ASTNode expr_prefix = this.nodeStack.pop();
			expr_prefix.addChild(factor); // (expr_prefix) ADDOP (factor)
			expr_prefix.callBackName = "expr";
			this.nodeStack.push(expr_prefix);
		} else {
			System.out.println("Error: exit expr had neither 1 nor 2 children.");
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
		// expr_prefix factor ADDOP | ;
		int numChildren = ctx.getChildCount();
		if (numChildren == 0) { // Empty expr_prefix
			// Lambda node keeps stack straight when popping for other subtrees.
			ASTNode<String> emptyNode = new ASTNode<String>("expr_prefix", "lambda");
			this.nodeStack.push(emptyNode);
		} else if (ctx.getChild(0).getText().equals("")) { // factor ADDOP
			ASTNode factor = this.nodeStack.pop();
			ASTNode empty_expr_prefix = this.nodeStack.pop();
			ASTNode ADDOP = new ASTNode("expr_prefix", ctx.getChild(2).getText());
			ADDOP.addChild(factor);
			this.nodeStack.push(ADDOP);
		} else if (!ctx.getChild(0).getText().equals("")) { // expr_prefix factor ADDOP
			ASTNode factor = this.nodeStack.pop();
			ASTNode expr_prefix = this.nodeStack.pop();
			expr_prefix.addChild(factor); // expr_prefix's right child must be the new factor
			ASTNode ADDOP = new ASTNode("expr_prefix", ctx.getChild(2).getText());
			ADDOP.addChild(expr_prefix);
			this.nodeStack.push(ADDOP);
		} else {
			System.out.println("Error: exit expr_prefix did not have 0, 2, or 3 children.");
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
		// factor : factor_prefix postfix_expr;
		int numChildren = ctx.getChildCount();
		if (ctx.getChild(0).getText().equals("")) { // postfix_expr
			ASTNode postfix_expr = this.nodeStack.pop();
			ASTNode empty_factor_prefix = this.nodeStack.pop(); // Pop and discard lambda node
			if (!postfix_expr.callBackName.equals("postfix_expr")) {
			    System.out.println("Error: exit factor stack pop was not 'postfix_expr'.");   
			}
			if (!empty_factor_prefix.data.equals("lambda")) {
				System.out.println("Error: On exit_factor there was 1 ctx child, but top of stack was not lambda factor_prefix.");
			}
			postfix_expr.callBackName = "factor";
			this.nodeStack.push(postfix_expr);
		} else if (!ctx.getChild(0).getText().equals("")) { // factor_prefix postfix_expr
			ASTNode postfix = this.nodeStack.pop();
			ASTNode factor_prefix = this.nodeStack.pop();
			if (!postfix.callBackName.equals("postfix_expr") || !factor_prefix.callBackName.equals("factor_prefix")) {
			    System.out.println("Error: exit factor stack pop was not 'postfix_expr' or 'factor_prefix'.");
			}
			factor_prefix.addChild(postfix); // (postfix_expr) MULOP (factor_prefix)
			factor_prefix.callBackName = "factor";
			this.nodeStack.push(factor_prefix);
		} else {
			System.out.println("Error: exit factor had neither 1 nor 2 children.");
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
		if (numChildren == 0) { // Empty factor prefix
			// Lambda node keeps stack straight when popping for other subtrees.
			ASTNode<String> emptyNode = new ASTNode<String>("factor_prefix", "lambda");
			this.nodeStack.push(emptyNode);
		} else if (ctx.getChild(0).getText().equals("")) { // postfix_expr, MULOP
			ASTNode postfix = this.nodeStack.pop();
			ASTNode empty_factor_prefix = this.nodeStack.pop();
			ASTNode MULOP = new ASTNode("factor_prefix", ctx.getChild(2).getText());
			MULOP.addChild(postfix);
			this.nodeStack.push(MULOP);
		} else if (!ctx.getChild(0).getText().equals("")) { // factor_prefix, postfix_expr, MULOP
			ASTNode postfix = this.nodeStack.pop();
			ASTNode factor_prefix = this.nodeStack.pop();
			factor_prefix.addChild(postfix); // factor_prefix's right child
			ASTNode MULOP = new ASTNode("factor_prefix", ctx.getChild(2).getText());
			MULOP.addChild(factor_prefix);
			this.nodeStack.push(MULOP);
		} else {
			System.out.println("Error: exit factor_prefix didn't have 0,2, or 3 children.");
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
           System.out.println("Error: Stack was empty postfix_expr.");
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
		// primary : OPENPAR expr CLOSEPAR | id | INTLITERAL | FLOATLITERAL;
		int numChildren = ctx.getChildCount();
		if (numChildren == 3) { // OPENPAR expr CLOSEPAR
			try {
				ASTNode expr = this.nodeStack.peek();
				expr.callBackName = "primary";
			} catch (EmptyStackException e) {
				System.out.println("Error: Stack was empty primary.");
			}
		} else if (numChildren == 1) { // {id | INTLITERAL | FLOATLITERAL}
			ASTNode checkId = this.nodeStack.peek();
			// This could cause problems if there is ever an "id" on the top of the stack
			// that is not intended for this primary.
			if (checkId.callBackName == "id") { // If the child is an ID id already has a node on the stack
				checkId.callBackName = "primary";
			} else { // Otherwise, it is a float literal or an intliteral
				try {
					// Create new primary node
					ASTNode<String> newPrimary = new ASTNode<String>("primary", ctx.getChild(0).getText());
					this.nodeStack.push(newPrimary);
				} catch (EmptyStackException e) {
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
		// TODO: Part 2 if conditions
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
		// TODO: Part 2 else conditions
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
		// TODO: Part 2 while conditions
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

	public ASTNode getAST() {
		return finalAST;
	}
}
