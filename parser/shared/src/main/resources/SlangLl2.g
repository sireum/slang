/*
 Copyright (c) 2017-2022, Robby, Kansas State University
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

@header { package org.sireum.lang.parser; }

@lexer::header { package org.sireum.lang.parser; }


file: program EOF ;

expFile: exp EOF ;

stmtFile: stmt EOF ;

program: main? pkg* ;

main: impor* mainMember+ ;

impor: IMPORT ID ( DOT importSuffix )? ;

importSuffix: UNDERSCORE
            | ID ( DOT importSuffix )?
            | LBRACE importRename ( COMMA importRename )* RBRACE ;

importRename: ID ARROW ID ;

mainMember: typeDef | defStmt | commonStmt ;

packageMember: typeDef | varDef | defDef ;

pkg: PACKAGE mod* name? impor* packageMember* ;

mod: ID LSQUARE args RSQUARE ;

args: exp ( COMMA exp )*
    | namedArg ( COMMA namedArg )* ;
  
namedArg: ID ASSIGN exp ;

name: ID ( DOT ID )* ;

typeDef: TYPE typeParams? mod* ID ( COLON enumMembers
                                  | ASSIGN type
                                  | params? suprs? annot? typeMembers? ) ;

typeParams: LSQUARE typeParam ( COMMA typeParam )* RSQUARE ;

typeParam: ID annot? ;

enumMembers: LBRACE ID ( COMMA ID )* RBRACE ;
         
params: LPAREN param ( COMMA param )* RPAREN ;

param: VAR? mod* ID COLON ARROW? type ;

suprs: COLON supr ( COMMA supr )* ;

supr: name typeArgs? ;

annot: AT LSQUARE ( rhs ( COMMA rhs )* )? RSQUARE ;

typeMembers: LBRACE typeMember* RBRACE ;

typeMember: varDef | defDef ;

varDef: VAR mod* ID COLON type ( ASSIGN rhs )? ;

defDef: DEF typeParams? mod* ( ID | OP ) defParams? COLON type ( ASSIGN annot? rhs? )? ;

defParams: LPAREN defParam ( COMMA defParam )? RPAREN ;

defParam: mod* ID COLON type ;

stmt: commonStmt | funStmt ;

commonStmt: varPattern | doStmt | ifStmt | whileStmt | forStmt | deduceStmt | matchStmt | exp0Stmt ;

defStmt: DEF typeParams? mod* ( ID | OP ) defParams? COLON type ASSIGN annot? rhs ;

doStmt: DO ( exp | mod* block ) ;

exp0Stmt: ID ( ASSIGN rhs )?
        | exp0 access+ ( ASSIGN rhs )? ;
  
varPattern: VAR pattern ASSIGN rhs ;

rhs: exp | block | ifStmt | matchStmt ;

ifStmt: IF exp block els? ;

block: LBRACE blockContent RBRACE ;

blockContent: stmt* ( ret | YIELD exp )? ;

ret: RETURN rhs? ;

els: ELSE ( IF exp block els?
            | block           ) ;

whileStmt: WHILE exp annot? block ;

forStmt: FOR forRange+ block ;

forRange: ID COLON exp ( ( TO |  UNTIL ) exp ( ASSIGN exp )? )? annot? ;

funStmt: FUN mod* ( ID | OP ) defParams? COLON type ASSIGN annot? rhs ;

matchStmt: MATCH exp LBRACE cas+ RBRACE ;

pattern: lit
       | patterns
       | name patterns?
       | ID COLON baseType
       | ID AT name patterns
       | UNDERSCORE ( COLON baseType )? ;

patterns: LPAREN pattern ( COMMA pattern )* RPAREN ;

exp: exp3 | fun ;

exp3: exp2 ( OP exp2 )* condSuffix? ;

exp2: exp1 access* ;
 
exp1: OP? ( exp0 | paren ) ;

exp0: lit | ID | interp | THIS | SUPER ;

condSuffix: QUESTION ( exp COLON exp 
                     | LBRACE cas+ RBRACE );

access: DOT ID typeArgs? ( LPAREN args? RPAREN )? fn? ;

fn: LBRACE annot? ARROW ( blockContent | cas+ ) RBRACE ; 
 
lit: TRUE | FALSE | TOP | BOTTOM | INT | HEX | BIN | REAL | STRING | MSTRING ;

paren: LPAREN exp ( COMMA exp )* RPAREN ;

cas: CASE pattern ( IF exp )? ARROW blockContent ;

fun: funId mod* defParams ( COLON type )? DOT annot? rhs ;

funId: FUN | LAMBDA | ALL | SOME | SYMBOL ;

deduceStmt: DEDUCE ( truthTable
                   | LBRACE proofStep* RBRACE
                   | COLON sequent ( LBRACE proofStep* RBRACE )?
                   | LPAREN expJustOpt ( COMMA expJustOpt )* RPAREN ) ;
            
expJustOpt: exp just? ;

proofStep: proofId DOT ( exp just | subProof ) ;

subProof: LBRACE freshIds? proofStep+ RBRACE ;

freshIds: ID ( COMMA ID)* ( COLON type )? ;

proofId: INT | STRING ;

just: name ( LPAREN args RPAREN )? proofId* ;
            
sequent: ( exp ( COMMA exp )* )? SEQUENT exp ;

truthTable: OP+                    // OP = *
            HLINE
            ID+ ( COLON exp )+  
            HLINE
            ID+ ( COLON ID+ )+       // ID consists only T or F
            HLINE
            truthTableConclusion? ;

truthTableConclusion: LSQUARE ID RSQUARE ( LBRACE truthTableCase* RBRACE )? ;

truthTableCase: CASE ID ARROW ( truthTableAssignment ( COMMA truthTableAssignment )* )? ;

truthTableAssignment: ID+ ;

type: baseType ( ARROW baseType annot? )* ;

baseType: ID typeArgs?
        | LPAREN ( type ( COMMA type )+ )? RPAREN ;

typeArgs: LSQUARE type ( COMMA type )* RSQUARE ;

interp: SP | MSP | SPB sinterp | MSPB minterp ;

sinterp: exp ( SPM sinterp | SPE ) ;

minterp: exp ( MSPM minterp | MSPE ) ;


// Lexical definitions
ALL:        '∀'           ;
ARROW:      '=>'          ;
ASSIGN:     ':='          ;
AT:         '@'           ;
BOTTOM:     '⊥'           ;
COMMA:      ','           ;
COLON:      ':'           ; 
DOT:        '.'           ;
LAMBDA:     'λ'           ;
LBRACE:     '{'           ;
LPAREN:     '('           ;
LSQUARE:    '['           ;
QUESTION:   '?'           ;
RBRACE:     '}'           ;
RPAREN:     ')'           ;
RSQUARE:    ']'           ;
SEQUENT:    '⊢' | '|-'    ;
SOME:       '∃'           ;
TOP:        '⊤'           ;
TO:         '..'          ;
UNDERSCORE: '_'           ;
UNTIL:      '..<'         ;

CASE:       'case'        ;
DEDUCE:     'deduce'      ;
DEF:        'def'         ;
DO:         'do'          ;
FALSE:      'false'       ;
ELSE:       'else'        ;
FOR:        'for'         ;
FUN:        'fun'         ;
IF:         'if'          ;
IMPORT:     'import'      ;
MATCH:      'match'       ;
PACKAGE:    'package'     ;
RETURN:     'return'      ;
SUPER:      'super'       ;
THIS:       'this'        ;
TRUE:       'true'        ;
TYPE:       'type'        ;
VAR:        'val' | 'var' ;
WHILE:      'while'       ;
YIELD:      'yield'       ;

SYMBOL: '\\' IDF ;

STRING: '"' (ESC_SEQ | ~( '\\' | '"' ) )* '"' ;

SP: IDF '"' SPI* '"' ;

SPB: IDF '"' SPI* '$' ;

SPM: '$' SPI* '$' ;

SPE: '$' SPI* '"' ;

MSTRING: '"""' ( ~'"'|  '"' ~'"' | '""' ~'"' )* ( '"""' | '""""' | '"""""' ) ;

MSP: IDF '"""' MSPI* '"""' ;

MSPB: IDF '"""' MSPI* '$' ;

MSPM: '$' MSPI* '$' ;

MSPE: '$' MSPI* ( '"""' | '""""' | '"""""' ) ;

ID: IDF | IDESC;

HLINE: '-' '-' '-' '-' '-'+ ;

OP: ( OPSYM+ | '\\' IDF ) ;

HEX: '0x'  HEX_DIGIT ( '_' | HEX_DIGIT )* ;

BIN: '0b' ( '0' | '1' ) ( '0' | '1' | '_' )* ;

INT: ( '0' | '-'? '1'..'9' ( DIGIT | '_' )* ) ;

REAL: ( '0' | '-'? '1'..'9' ( DIGIT | '_' )* ) ( '.' DIGIT ( DIGIT | '_' )* EXPONENT? | EXPONENT ) ( 'f' | 'F' | 'd' | 'D' )? ;

CHAR: '\'' ( ESC_SEQ | ~('\''|'\\') ) '\'' ;

COMMENT: '//' ~( '\n' | '\r' )* '\r'? '\n'          {$channel=HIDDEN;}
       | '/*' ( ~'*' | '*' ~'/' )* ( '*/' | '**/' ) {$channel=HIDDEN;} ;

WS: ( ' ' | '\t' | '\r' | '\n' )+ {$channel=HIDDEN;} ;

fragment IDESC: '`' ~( '\n' | '\r' | '\t' )* '`' ;

fragment IDF: ( LETTER | '_' ) ( LETTER | DIGIT | '_' | '$' ( LETTER | DIGIT | '_' ) )* ;

fragment SPI: ESC_SEQ | ~( '\\' | '"' | '$' ) | '$$' ;

fragment MSPI: ~( '"' | '$' ) | '$$' | '"' ~'"' | '""' ~'"' ;

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
