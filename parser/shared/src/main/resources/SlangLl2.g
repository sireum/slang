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

// Use ANTLRWorks from: https://github.com/sireum/antlrworks/releases
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

program: impor* topMember* pkg* ;

impor: 'import' ID ( '.' importSuffix )? ;

importSuffix
  : ID ( '.' importSuffix )?
  | '{' importRename ( ',' importRename )* '}'
  | '_'
  ;

importRename: ID '=>' ID ;

topMember: typeDef | stmt ;

packageMember: typeDef | varDef | defDef ;

pkg: 'package' mod* name impor* packageMember* ;

mod: ID '[' args ']' ;

args
  : namedArg ( ',' namedArg )*
  | exp ( ',' exp )*
  ;
  
namedArg: ID '=' exp ;

name: ID ( '.' ID )* ;

typeDef: 'type' typeParams? modsID ( ':' enumMembers
                                   | '=' type
                                   | params? suprs? annot? typeMembers?
                                   )
                                   ;

typeParams: '[' typeParam ( ',' typeParam )* ']' ;

typeParam: ID annot? ;

modsID: mod modsID | ID ;

enumMembers: '{' ID ( ',' ID )* '}' ;
         
params: '(' param ( ',' param )* ')' ;

param: ( 'val' | 'var' )? modsID ':' '=>'? type ;

suprs: ':' supr ( ',' supr )* ;

supr: name typeArgs? ;

annot: '@' '(' ( exp ( ',' exp )* )? ')' ;

typeMembers: '{' typeMember* '}' ;

typeMember: varDef | defDef ;

varDef: ( 'val' | 'var' ) modsID ':' type ( '=' rhs )? ;

defDef: 'def' typeParams? modsIDOP defParams? ':' type ( '=' annot? rhs? )? ;

modsIDOP: ID '[' args ']' modsIDOP | ID | OP ;

defParams: '(' defParam ( ',' defParam )? ')' ;

defParam: modsID ':' type ;

stmt: doStmt | varPattern | ifStmt | whileStmt | forStmt | defStmt | deduceStmt | matchExp | groundExpStmt ;

doStmt: 'do' ( exp | mod* block ) ;

groundExpStmt: ID '=' exp | ground expSuffix+ ( '=' exp )? ;
  
varPattern: ( 'val' | 'var' ) pattern '=' rhs ;

rhs: exp | block | ifStmt ;

ifStmt: 'if' exp block els? ;

block: '{' blockContent '}' ;

blockContent: stmt* ( ret | 'yield' rhs )? ;

ret: 'return' rhs? ;

els: 'else' ( 'if' exp block els?
            | block
            )
            ;
   
whileStmt: 'while' exp annot? block ;

forStmt: 'for' forRange+ block ;

forRange: ID 'in' exp ( ( '..' |  '..<' ) exp ( 'by' exp )? )? annot? ;

defStmt: 'def' typeParams? modsIDOP defParams? ':' type '=' annot? rhs ;

pattern
  : lit
  | ID '@' name patterns
  | name patterns?
  | patterns
  | ID ':' baseType
  | '_' ( ':' baseType )?
  ;
  
patterns: '(' pattern ( ',' pattern )* ')' ;

exp: term ( OP term )*  ( '?' exp ':' exp )? | matchExp | fun ;

term: fact expSuffix* ;
 
expSuffix: '.' ID ( '[' type ( ',' type )* ']' )? ( '(' args? ')' )? ;
 
fact: OP? ( ground | paren ) ;

ground: lit | ID | interp | 'this' | 'super' ;
 
lit: 'true' | 'false' | '⊤' | '⊥' | INT | HEX | BIN | REAL | STRING | MSTRING ;

paren: '(' exp ( ',' exp )* ')' ;

matchExp: 'match' exp '{' cas+ '}' ;

cas: 'case' pattern ( 'if' exp )? '=>' blockContent ;

fun: funId mod* defParams ( ':' type )? '.' annot? rhs ;

funId: 'fun' | 'all' | 'some' | 'λ' | '∀' | '∃' ;

deduceStmt: 'deduce' ( '(' expJustOpt ( ',' expJustOpt )* ')' 
                     | '{' proofStep* '}'
                     | ':' sequent ( '{' proofStep* '}' )?
                     | truthTable
                     )
                     ;
            
expJustOpt: exp just? ;

proofStep: proofId '.' ( exp just | subProof ) ;

subProof: '{' ( '(' ID ( ',' ID)* ( ':' type )? ')' ':' )? proofStep+ '}' ;

proofId: INT | STRING ;

just: name ( '(' args ')' )? proofId* ;
            
sequent: ( exp ( ',' exp )* )? ( '|-' | '⊢' ) exp ;

truthTable: OP+                    // OP = *
            TTL
            ID+ ( ':' exp )+  
            TTL
            ID+ ( ':' ID+ )+       // ID consists only T or F
            TTL
            truthTableConclusion?
            ;

truthTableConclusion: '[' ID ']' ( '{' truthTableCase* '}' )? ;

truthTableCase: 'case' ID ':' ( truthTableAssignment ( ',' truthTableAssignment )* )? ;

truthTableAssignment: ID+ ;


type: baseType ( '=>' baseType )* annot? ;

baseType
  : ID typeArgs?
  | '(' ( type ( ',' type )+ )? ')'
  ;

typeArgs: '[' type ( ',' type )* ']' ;

interp: SP | MSP | SPB sinterp | MSPB minterp ;

sinterp: exp ( SPM sinterp | SPE ) ;

minterp: exp ( MSPM minterp | MSPE ) ;


// Lexical definitions

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

TTL: '-' '-' '-' '-' '-'+ ;

OP: ( OPSYM+ | '\\' IDF ) ;

HEX: '0x'  HEX_DIGIT ( '_' | HEX_DIGIT )* ( '.' IDF )?;

BIN: '0b' ( '0' | '1' ) ( '0' | '1' | '_' )* ( '.' IDF )? ;

INT: ( '0' | '-'? '1'..'9' ( DIGIT | '_' )* ) ( '.' IDF )? ;

REAL: ( '0' | '-'? '1'..'9' ( DIGIT | '_' )* ) ( '.' DIGIT ( DIGIT | '_' )* EXPONENT? | EXPONENT ) ( ( '.' IDF | 'f' | 'F' | 'd' | 'D' ) )? ;

CHAR: '\'' ( ESC_SEQ | ~('\''|'\\') ) '\'' ;

COMMENT
  : '//' ~( '\n' | '\r' )* '\r'? '\n'          {$channel=HIDDEN;}
  | '/*' ( ~'*' | '*' ~'/' )* ( '*/' | '**/' ) {$channel=HIDDEN;}
  ;

WS: ( ' ' | '\t' | '\r' | '\n' )+ {$channel=HIDDEN;} ;

fragment IDESC: '`' ~( '\n' | '\r' | '\t' )* '`' ;

fragment IDF: ( LETTER | '_' ) ( LETTER | DIGIT | '_' | '$' ( LETTER | DIGIT | '_' ) )* ;

fragment SPI: ESC_SEQ | ~( '\\' | '"' | '$' ) | '$$' ;

fragment MSPI: ~( '"' | '$' ) | '$$' | '"' ~'"' | '""' ~'"' ;

fragment LETTER: 'a'..'z' | 'A'..'Z';

fragment DIGIT: '0'..'9' ;

fragment OPSYM
  : '+' | '-' | '*' | '/' | '%' | ':=' | '<' | '>' | '!' | '&' | '^' | '|' | '~' | ':' | '='
  | ( '\u2200' .. '\u22FF' ) // https://en.wikipedia.org/wiki/Mathematical_Operators
  | ( '\u2A00' .. '\u2AFF' ) // https://en.wikipedia.org/wiki/Supplemental_Mathematical_Operators
  | ( '\u27C0' .. '\u27EF' ) // https://en.wikipedia.org/wiki/Miscellaneous_Mathematical_Symbols-A
  | ( '\u2980' .. '\u29FF' ) // https://en.wikipedia.org/wiki/Miscellaneous_Mathematical_Symbols-B
  ;

fragment EXPONENT: ( 'e' | 'E' ) ( '+' | '-' )? ( DIGIT | '_' )+ ;

fragment HEX_DIGIT: ( DIGIT | 'a'..'f' | 'A'..'F' ) ;

fragment ESC_SEQ: '\\' ( 'b' | 't' | 'n' | 'f' | 'r' | '\"' | '\'' | '\\' ) | UNICODE_ESC ;

fragment UNICODE_ESC: '\\' 'u' HEX_DIGIT HEX_DIGIT HEX_DIGIT HEX_DIGIT ;
