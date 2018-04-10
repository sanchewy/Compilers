grammar Little;

// First line of this file must be the grammar name.
// So, please keep the first line as it is provided.
// Please write your grammar below this line.

/* Program */
program : PROGRAM id BEGIN pgm_bdy END;     //For some stupid reason ANTLR needs the first rule to be lowercase or it freaks out. Note: in ANTLR Lexers start with a capital and Parsers start with a lowercase.
id : IDENTIFIER;
pgm_bdy : decl func_declarations;
decl : string_decl decl | var_decl decl | ;
empty : WHITESPACE;

/* Global String Decleration */
string_decl : STRING id ASSIGNOP str SEMICOLON; 
str : STRINGLITERAL;

/* Variable Declaration */
var_decl : var_type id_list;
var_type : FLOAT | INT;
any_type : VOID| FLOAT| INT;
id_list : id id_tail;
id_tail : COMMA id id_tail | SEMICOLON | ;

/* Function Parameter List */
param_decl_list : param_decl param_decl_tail | ;
param_decl : var_type id;
param_decl_tail : COMMA param_decl param_decl_tail | ;

/* Function Declarations */
func_declarations : func_decl func_declarations | ;
func_decl : FUNCTION any_type id OPENPAR param_decl_list CLOSEPAR BEGIN func_body END;
func_body : decl stmt_list;

/* Statement List */
stmt_list : stmt stmt_list | ;
stmt : base_stmt | if_stmt | while_stmt;
base_stmt : assign_stmt | read_stmt | write_stmt | return_stmt;

/* Basic Statements*/
assign_stmt : assign_expr SEMICOLON;
assign_expr : id ASSIGNOP expr;
read_stmt : READ OPENPAR id_list CLOSEPAR SEMICOLON;
write_stmt : WRITE OPENPAR id_list CLOSEPAR SEMICOLON;
return_stmt : RETURN expr SEMICOLON;

/* Expressions */
expr : expr_prefix factor;
expr_prefix : expr_prefix factor ADDOP | ;
factor : factor_prefix postfix_expr;
factor_prefix : factor_prefix postfix_expr MULOP | ;
postfix_expr : primary | call_expr;
call_expr : id OPENPAR expr_list CLOSEPAR;
expr_list : expr expr_list_tail | ;
expr_list_tail : COMMA expr expr_list_tail | ;
primary : OPENPAR expr CLOSEPAR | id | INTLITERAL | FLOATLITERAL;

/* Complex Statements and Condition */
if_stmt : IF OPENPAR cond CLOSEPAR decl stmt_list else_part ENDIF;
else_part : ELSE decl stmt_list | ;
cond : expr COMPOP expr;


/* While Statements */
while_stmt : WHILE OPENPAR cond CLOSEPAR decl stmt_list ENDWHILE;


/**
 * Fragments
 */

fragment DIGIT0     : '0'..'9' ;
fragment DIGIT1     : '1'..'9' ;
fragment NL         : '\r'? '\n' 
                    | '\r' ;


/**
 * Lexers
 */

PROGRAM             : 'PROGRAM';
BEGIN               : 'BEGIN';
END                 : 'END';
FUNCTION            : 'FUNCTION';
READ                : 'READ';
WRITE               : 'WRITE';
IF                  : 'IF';
ELSE                : 'ELSE';
FOR                 : 'FOR';
RETURN              : 'RETURN';
INT                 : 'INT';
VOID                : 'VOID';
STRING              : 'STRING';
FLOAT               : 'FLOAT';
WHILE               : 'WHILE';
ENDIF               : 'ENDIF';
ENDWHILE            : 'ENDWHILE';
ASSIGNOP            : ':=';
COMMA               : ',';
OPENPAR             : '(';
CLOSEPAR            : ')';
SEMICOLON           : ';';
ADDOP               : '+' | '-';
MULOP               : '*' | '/';
COMPOP              : '<' | '>' | '=' | '!=' | '<=' | '>=';

IDENTIFIER          : ([a-z] | [A-Z]) ([a-z]+ | [A-Z]+ | DIGIT0+)* ;
INTLITERAL          : DIGIT1* DIGIT0+ ;
FLOATLITERAL        : INTLITERAL '.' DIGIT0* 
                    | '.' DIGIT0+ 
                    ;
STRINGLITERAL       : '"' ~('"')* '"' ;
COMMENT             : '--' ~[\r\n]* NL -> skip
                    //| '--' ~('\r')* NL
                    //| '--' ~('\r\n')* NL             Can't match on more than one character in a lexer set, so window's new line doesn't work.
                    ;
OPERATOR            : ASSIGNOP
                    | ADDOP
                    | MULOP
                    | COMPOP
                    | OPENPAR
                    | CLOSEPAR
                    | SEMICOLON
                    | COMMA
                    ;
WHITESPACE          : [ \n\t\r]+ -> skip;
