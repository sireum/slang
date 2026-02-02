/*
 Copyright (c) 2017-2026,Robby, Kansas State University
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions are met:

 1. Redistributions of source code must retain the above copyright notice, this
    list of conditions and the following disclaimer.
 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
// Edit using ANTLRWorks from: https://github.com/sireum/antlrworks/releases
grammar SlangLl2;

options {
  output = AST;
  ASTLabelType = Object;
  k = 2;
}

@header { package org.sireum.lang.parser.antlrv3; }

@lexer::header { package org.sireum.lang.parser.antlrv3; }


file: program EOF ;

expFile: annot? exp EOF ;

stmtFile: annot? stmt EOF ;

program: annot? imprt* mainMember* pkg* ;

imprt: IMPORT ID ( DOT importSuffix )? ;

importSuffix: UNDERSCORE annot?
            | ID ( annot | DOT importSuffix )?
            | LBRACE importRename ( COMMA importRename )* RBRACE ;

importRename: ID ARROW ID annot? ;

mainMember: stmt | typeDefn ;

pkg: PACKAGE mod* name? annot? imprt* member* ;

init: DOT DOT LBRACE annot? stmt* RBRACE ;

member: varDefn | defDefn | typeDefn | init ;

mod: AT ID ( LSQUARE args RSQUARE )? ;

args: annot? rhs ( COMMA annot? rhs )*
    | namedArg ( COMMA namedArg )* ;
    
namedArg: ID ASSIGN annot? rhs ;

name: ID ( DOT ID )* ;

typeDefn: TYPE typeParams? mod* ID ( COLON enumMembers
                                   | ASSIGN type
                                   | params? supers? annot? ( LBRACE member* RBRACE )? ) ;

typeParams: LSQUARE typeParam ( COMMA typeParam )* RSQUARE ;

typeParam: mod* ID ;

enumMembers: LBRACE ID ( COMMA ID )* RBRACE ;

params: LPAREN param ( COMMA param )* RPAREN ;

param: VAR? mod* ID COLON ARROW? type ;

supers: COLON supr ( COMMA supr )* ;

supr: annot? name typeArgs? ;

annot: AT LSQUARE args? RSQUARE ;

varDefn: VAR mod* ID COLON type annot? ( ASSIGN annot? rhs )? ;

defDefn: DEF typeParams? mod* defId defParams? COLON type annot? ( ASSIGN annot? rhs )? ;

defId: ID | OP | SYMBOL ;

defParams: LPAREN defParam defParamSuffix? RPAREN ;

defParam: mod* ID COLON type ;

defParamSuffix: COMMA ( TO defParam
                      | defParam defParamSuffix? ) ;

stmt: expOrAssignStmt | varPattern | ifStmt | whileStmt | forStmt | deduceStmt | matchStmt | defStmt ;

defStmt: DEF typeParams? mod* defId defParams? COLON type annot? ASSIGN annot? rhs ;

expOrAssignStmt: ID ( annot
                    | ASSIGN annot? rhs
                    | COLON annot? )?
               | exp0 access+ annot? ( ASSIGN annot? rhs )?
               | DO annot? ( exp | mod* block ) ;

varPattern: VAR pattern ASSIGN annot? rhs ;

rhs: exp | block | ifStmt | matchStmt ;

ifStmt: IF exp annot? block els? ;

block: LBRACE annot? blockContent RBRACE ;

blockContent: stmt* ret? ;

ret: RETURN annot? rhs? ;

els: ELSE ( IF exp annot? block els?
          | block                    ) ;

whileStmt: WHILE exp annot? block ;

forStmt: FOR forRange+ block ;

forRange: ID COLON exp ( ( TO |  UNTIL ) exp ( COMMA exp )? )? annot? ;

matchStmt: MATCH exp annot? LBRACE cas+ RBRACE ;

pattern:  annot? ( lit
                 | patterns
                 | name patterns?
                 | ID COLON type1
                 | ID AT name patterns
                 | UNDERSCORE ( COLON type1 )? ) ;

patterns: LPAREN patternsArg RPAREN ;

patternsArg: pattern ( COMMA pattern )*
	         | ID ASSIGN pattern ( COMMA ID ASSIGN pattern )* ;

exp: exp3 | forExp | defAnon | quant ;

exp3: exp2 infixSuffix* condSuffix? ;

infixSuffix: ( OP | SYMBOL ) exp2 ;

exp2: exp1 access* ;

exp1: OP? ( exp0 | paren ) ;

exp0: ID | THIS | SUPER | lit | interp ;

condSuffix: QUESTION ( exp COLON exp
                     | LBRACE cas+ RBRACE );

access: DOT ID typeArgs? | LPAREN args? RPAREN fn? ;

fn: LBRACE ARROW annot? ( blockContent | cas+ ) RBRACE ;

lit: TRUE | FALSE | INT | HEX | BIN | REAL | STRING |  MSTR /* | MSTRING */ ;

paren: LPAREN annot? parenArgs RPAREN ;

parenArgs
	: exp annot? ( COMMA exp annot? )*
	| ID ASSIGN exp annot? ( COMMA ID ASSIGN exp annot? )* ;

cas: CASE pattern ( IF exp )?  ARROW annot? blockContent ;

forExp: YIELD annot? forRange+ ARROW annot? rhs ;

defAnon: DEF mod* defParams ( COLON type )? DOT annot? rhs ;

quant: ( ALL | SOME | SYMBOL ) quantRange+ ARROW annot? rhs ;

quantRange: ( ID COMMA )* ID annot? COLON annot? exp ( ( TO |  UNTIL ) annot? exp )? ; // first exp can refer to a type

deduceStmt: DEDUCE ( truthTable
                   | LBRACE proofStep* RBRACE
                   | COLON sequent ( LBRACE proofStep* RBRACE )?
                   | LPAREN expJustOpt ( COMMA expJustOpt )* RPAREN ) ;

expJustOpt: exp just? ;

proofStep: proofId DOT ( exp just | subProof ) ;

subProof: LBRACE freshIds* proofStep+ RBRACE ;

freshIds: ID ( COMMA ID)* ( COLON type )? ;

proofId: INT | STRING ;

just: name ( LPAREN args RPAREN )? ( LSQUARE proofId ( COMMA proofId )* RSQUARE )? ;

sequent: ( exp ( COMMA exp )* )? SEQUENT exp ;

truthTable: OP+                      // OP = *
            HLINE
            ID+ ( COLON exp )+
            HLINE
            ID+ ( COLON ID+ )+       // ID consists only T or F
            HLINE
            truthTableConclusion? ;

truthTableConclusion: LSQUARE ID RSQUARE ( LBRACE truthTableCase* RBRACE )? ;

truthTableCase: CASE ID ARROW ( truthTableAssignment ( COMMA truthTableAssignment )* )? ;

truthTableAssignment: ID+ ;

type: type1 ( ARROW annot? type1 )* ;

type1: LPAREN typeParenArgs RPAREN
     | type0 ( ( OP | SYMBOL ) type0 )* ;

typeParenArgs
	:	annot? type ( COMMA annot? type )*
	| ID ASSIGN annot? type ( COMMA ID ASSIGN annot? type )* ;
	        
type0: ID typeArgs? ;

typeArgs: LSQUARE typeParenArgs RSQUARE ;

interp: SP | SPB sinterp | MSTRP | MSTRPB mstrinterp /* | MSP | MSPB minterp */ ;

sinterp: exp ( SPM sinterp | SPE ) ;

strinterp: exp ( MSTRPM sinterp | MSTRPE ) ;

mstrinterp: exp ( MSTRPM mstrinterp | MSTRPE ) ;

// Lexical definitions
ALL:        '∀'           ; ARROW:      '=>'          ; ASSIGN:     '='           ; AT:         '@'           ;
COMMA:      ','           ; COLON:      ':'           ; DOT:        '.'           ; UNDERSCORE: '_'           ;
LBRACE:     '{'           ; LPAREN:     '('           ; LSQUARE:    '['           ; QUESTION:   '?'           ;
RBRACE:     '}'           ; RPAREN:     ')'           ; RSQUARE:    ']'           ; SEQUENT:    '⊢' | '|-'    ;
SOME:       '∃'           ; TO:         '..'          ; UNTIL:      '..<'         ;

CASE:       'case'        ; DEDUCE:     'deduce'      ; DEF:        'def'         ; DO:         'do'          ;
FALSE:      'false'       ; ELSE:       'else'        ; FOR:        'for'         ; TYPE:       'type'        ;
IF:         'if'          ; IMPORT:     'import'      ; MATCH:      'match'       ; PACKAGE:    'package'     ;
RETURN:     'return'      ; SUPER:      'super'       ; THIS:       'this'        ; TRUE:       'true'        ; 
WHILE:      'while'       ; YIELD:      'yield'       ; VAR:        'val' | 'var' ;

SYMBOL: '\\' IDF ;

STRING: '"' ( ESC_SEQ | ~( '\\' | '"' ) )* '"' ;

SP: IDF '"' SPI* '"' ;

SPB: IDF '"' SPI* '$' ;

SPM: '$' SPI* '$' ;

SPE: '$' SPI* '"' ;

MSTR:	( '#' MSTRF WSF? )* '#' MSTRF ;

MSTRP:	IDF ( '#' MSTRF WSF? )* '#' MSTRF ;

MSTRPB: IDF ( '#' MSTRF WSF? )* '#' MSTRI '$' ;

MSTRPM: '$' MSTRF WSF? ( '#' MSTRF WSF? )* '#' MSTRI '$' ;

MSTRPE: '$' MSTRF WSF? ( '#' MSTRF WSF? )* '#' MSTRF ;

ID: IDF | IDESC;

HLINE: '-' '-' '-' '-' '-'+ ;

OP: ( OPSYM+ | '\\' IDF ) ;

HEX: '0x'  HEX_DIGIT ( '_' | HEX_DIGIT )* ;

BIN: '0b' ( '0' | '1' ) ( '0' | '1' | '_' )* ;

INT: ( '0' | '-'? '1'..'9' ( DIGIT | '_' )* ) ;

REAL: ( '0' | '-'? '1'..'9' ( DIGIT | '_' )* ) ( '.' DIGIT ( DIGIT | '_' )* EXPONENT? | EXPONENT ) ( 'f' | 'F' | 'd' | 'D' )? ;

CHAR: '\'' ( ESC_SEQ | ~('\''|'\\') ) '\'' ;

COMMENT: '//' ~( '\n' | '\r' )* '\r'? '\n'           {$channel=HIDDEN;}
       | '/*' ( ~'*' | '*' ~'/' )* ( '*/' | '**/' )  {$channel=HIDDEN;} ;

WS: ( ' ' | '\t' | '\r' | '\n' )+                    {$channel=HIDDEN;} ;

fragment MSTRF:	~( '\n' | '\r' )* '\r'? '\n' ;

fragment MSTRI:	( ~( '\n' | '\r' | '$' ) | '$$' )* ;

fragment WSF:	( ' ' | '\t' )+ ;

fragment IDESC: '`' ~( '\n' | '\r' | '\t' )* '`' ;

fragment IDF: ( LETTER | '_' | '$' ) ( LETTER | DIGIT | '_' | '$' )* ( '_' OPSYM+ )? ;

fragment SPI: ESC_SEQ | ~( '\\' | '"' | '$' ) | '$$' ;


fragment LETTER: 'a'..'z' | 'A'..'Z';

fragment DIGIT: '0'..'9' ;

fragment OPSYM: ( '\u2200' .. '\u22FF' ) // https://en.wikipedia.org/wiki/Mathematical_Operators
              | ( '\u2A00' .. '\u2AFF' ) // https://en.wikipedia.org/wiki/Supplemental_Mathematical_Operators
              | ( '\u27C0' .. '\u27EF' ) // https://en.wikipedia.org/wiki/Miscellaneous_Mathematical_Symbols-A
              | ( '\u2980' .. '\u29FF' ) // https://en.wikipedia.org/wiki/Miscellaneous_Mathematical_Symbols-B
              | '+' | '-' | '*' | '/' | '%' | '<' | '>' | '!' | '&' | '^' | '|' | '~' | ':' | '=' ;

fragment EXPONENT: ( 'e' | 'E' ) ( '+' | '-' )? ( DIGIT | '_' )+ ;

fragment HEX_DIGIT: ( DIGIT | 'a'..'f' | 'A'..'F' ) ;

fragment ESC_SEQ: '\\' ( 'b' | 't' | 'n' | 'f' | 'r' | '\"' | '\'' | '\\' ) | UNICODE_ESC ;

fragment UNICODE_ESC: '\\' 'u' HEX_DIGIT HEX_DIGIT HEX_DIGIT HEX_DIGIT HEX_DIGIT? ;

/*
minterp: exp ( MSPM minterp | MSPE ) ;

MSTRING: '"""' ( ~'"'|  '"' ~'"' | '""' ~'"' )* ( '"""' | '""""' | '"""""' ) ;

MSP: IDF '"""' MSPI* '"""' ;

MSPB: IDF '"""' MSPI* '$' ;

MSPM: '$' MSPI* '$' ;

MSPE: '$' MSPI* ( '"""' | '""""' | '"""""' ) ;

fragment MSPI: ~( '"' | '$' ) | '$$' | '"' ~'"' | '""' ~'"' ;
*/