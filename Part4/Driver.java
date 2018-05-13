import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import org.antlr.v4.runtime.RuleContext.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Map;
import java.util.LinkedHashMap;
import java.lang.reflect.Method;
import org.antlr.v4.runtime.misc.ParseCancellationException;

// import LittleParser.java;
// import LittleBaseListner.java;

public class Driver {
	public static void main(String[] args) throws Exception {

		// args[0] contains the address of current testcase
		String testcase_filename = args[0];

		try {
			// load testcase file into a stream for ANTLR
			CharStream stream = CharStreams.fromFileName(testcase_filename);
			LittleLexer lexer = new LittleLexer(stream);
			// ---------------------Part 1 Prints
			// Token t = lexer.nextToken();
			// Vocabulary voc = lexer.getVocabulary();
			// while(t.getType() != t.EOF) {
			// System.out.println("Token Type: "+voc.getSymbolicName(t.getType())); //Note:
			// This will not work because we changed KEYWORK tokens (e.g. KEYWORD :
			// 'PROGRAM' => PROGRAM : 'PROGRAM')
			// System.out.println("Value: "+t.getText());
			// t = lexer.nextToken();
			// }
			CommonTokenStream tokens = new CommonTokenStream(lexer);
			LittleParser parser = new LittleParser(tokens);
			parser.removeErrorListeners();
			// ---------------------Part 2 Prints
			// parser.program();
			// int numErrors = parser.getNumberOfSyntaxErrors();
			// if(numErrors == 0) {
			// System.out.println("Accepted");
			// }
			// else {
			// System.out.println("Not accepted");
			// }
			
			try {
	 			Listener listener = new Listener();
    			ParseTreeWalker walker = new ParseTreeWalker();
    			walker.walk(listener, parser.program());
    			// This returns the hash of hashes <String scopeName, <String scopeName,
    			// EntryObj>>
				LinkedHashMap map = (LinkedHashMap) listener.getSymbolTable();
				ASTNode ast = listener.getAST();
				System.out.println(";---------Print AST------------");
				//ast.prettyPrint();
				System.out.println(";---------Print IR------------");
				IRParser irp = new IRParser();
				IRNode ir = irp.parseHelper(ast);
				Iterator it = ir.code.iterator();
				IRTranslator trans = new IRTranslator();
				
				/*while(it.hasNext()) {
				   trans.IRtoAss(it.next());
				    System.out.println(it.next());
				}*/
				System.out.println(";---------Print Tiny------------");
				trans.IRtoAss(ir.code, map);
				// ---------------------Part 3 Prints
//				Iterator it = map.entrySet().iterator();
//				while (it.hasNext()) {
//					Map.Entry pair = (Map.Entry) it.next();
//					SymbolTable st = (SymbolTable) pair.getValue();
//					st.prettyPrint();
//					it.remove();
//				}
			}catch (ParseCancellationException e) {
					System.out.println(e.getMessage());
			} 
// 			try {
// 			    LittleParser.ProgramContext cst = parser.program();
// 			    AstNode ast = new BuildAstVisitor().visitProgram(cst);
// 			    System.out.println(ast.name);
// 			} catch (Exception e) {
// 			    System.out.println("oh no " + e.toString());
// 			}
		} catch (Exception e) {
			System.out.println("Error in execution: " + e.toString());
		}
		// LittleBaseListener listener = new LittleBaseListener();
		// parser.addParseListener(listener);
		// for(Method m : listener.getClass().getDeclaredMethods()) {
		// System.out.println(m.getName());
		// }
	}
}
