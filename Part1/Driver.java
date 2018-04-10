import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Driver{
	public static void main(String[] args) throws Exception {
	    // args[0] contains the address of current testcase
		String testcase_filename = args[0];
		
		// load testcase file into a stream for ANTLR
	    CharStream stream = CharStreams.fromFileName(testcase_filename);
		LittleLexer lexer = new LittleLexer(stream);
		Token t = lexer.nextToken();
		Vocabulary voc = lexer.getVocabulary();
		while(t.getType() != t.EOF) {
		    System.out.println("Token Type: "+voc.getSymbolicName(t.getType()));
		    System.out.println("Value: "+t.getText());
		    t = lexer.nextToken();
		}
        // CommonTokenStream tokens = new CommonTokenStream(lexer);     -\
        // ExpParser parser = new ExpParser(tokens);                    -- Don't have a parser yet
        // parser.eval();                                               -/
	}
}