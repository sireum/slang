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
  ASTLabelType=Object;
  k = 2;
}

@header { package org.sireum.lang.parser; }

@lexer::header { package org.sireum.lang.parser; }


file: ( script | program )? EOF ;

expFile: exp EOF ;

stmtFile: stmt EOF ;

script: impor* ( stmt+ scriptMember* | scriptMember+ ) ;

scriptMember: sigDef | typeDef | ext ;

program: 'package' name impor* packageMember* ;

name: ID ( '.' ID )* ;

packageMember: sigDef | typeDef | ext | def ;

impor: 'import' ID ( '.' importSuffix )? ;

importSuffix
  : ID ( '.' importSuffix )?
  | '{' importRename ( ',' importRename )* '}'
  | '_'
  ;

modsID: ID+ ;

importRename: ID '=>' ID ;

sigDef: 'sig' modsID typeParams? suprs? typeMembers? ; // mods = ( mut )

typeParams: '[' typeParam (  ',' typeParam )* ']' ;

typeParam: modsID ; // mods = ( mut )

suprs: ':' supr ( ',' supr )* ;

supr: name typeArgs? ;

typeDef: 'type' modsID  // mods = ( mut | abstract )* 
         ( typeParams? ( ':=' ( type | ID callArgs ) // ID = { bits, range }
                       | params? suprs? typeMembers?                       
                       )
         | enumMembers
         )
         ;

params: '(' param ( ',' param )* ')' ;

param: 'var'? modsID ':' '=>'? type ; // mods = { hidden }

typeMembers: '{' typeMember* '}' ;

typeMember: typeDef | typeCase | var | def ;

typeCase: 'case' modsID params? suprs? typeMembers? ; // mods = ( mut )

enumMembers: '{' ID+ '}';

ext: 'ext' ( '(' name ')' )? ID '{' extMember* '}' ;

extMember: var | def ;

var: ( 'val' | 'var' ) modsID ':' type ( ':=' rhs )? ; // mods = ( spec | static )

rhs: exp;

def: 'def' defModsID typeParams? defParams? ':' type contract? ( ':=' rhs | block )? ;

defModsID
  : ID+ OP?
  | OP
  ; // mods = ( strict | memoize | pure | spec | static )

defParams: '(' defParam ( ',' defParam )? ')' ;

defParam: modsID ':' '=>'? type ;

contract: 'spec' '{' contractElement* '}' ;

contractElement
  : ID contractLabel? ':' exp ( ',' exp )*
  | 'case' contractLabel? '{' contractElement+ '}'
  ;

contractLabel: '[' ID ( ',' ID )* ']' ;

stmt: doStmt | varPattern | def | ifStmt | whileStmt | forStmt | deduceStmt | idStmt ;

idStmt: ID ( accessSuffix assignSuffix?
           | assignSuffix       
           | codeBlock
           )
           ;

assignSuffix: ':=' rhs ;

doStmt: 'do' ( rhs | 'spec' block ) ;

varPattern: ( 'val' | 'var' ) pattern ( ':' type )? ':=' rhs ;

ifStmt: 'if'
        ( '{' ifCase+ '}'
        | expNoBlock ( '{' ifCase+ '}' | block els? )
        ) 
        ;

ifCase
  : 'case' pattern ( OP exp )? ':' blockContent  // OP = &&
  | '_' exp? ':' blockContent
  ;

block: '{' blockContent '}' ;

blockContent: stmt* ret? ;

ret: 'return' exp? ;

els: 'else'
     ( 'if' expNoBlock block els?
     | block
     )
     ;
   
whileStmt: 'while' expNoBlock contract? block ;

forStmt: 'for' ( forRange contract? )+ block ;

forRange: ID 'in' expNoBlock ( ( '..' |  '..<' ) expNoBlock ( ',' expNoBlock )? )? ;

pattern
  : ID ( '(' tpattern ( ',' tpattern )* ')' )?
  | '(' tpattern ( ',' tpattern )* ')'
  ;

tpattern
  : ID ( '(' tpattern ( ',' tpattern )* ')' | ':' type )?
  | '(' tpattern ( ',' tpattern )* ')'
  | '_' ( ':' type )?
  ;

deduceStmt: 'deduce'
            ( '{' proofStep+ '}'
            | truthTable
            | sequent ( '{' proofStep* '}' )?
            )
            ;

sequent: ( expNoBlock ( ',' expNoBlock )* )? ( '|-' | '⊢' ) expNoBlock ;

truthTable: OP+         // OP = *
            '-----'+
            ID+ ( ':' expNoBlock )+  
            '-----'+
            ID+ ( ':' ID+ )+  // ID consists only T or F
            '-----'+
            truthTableConclusion?
            ;

truthTableConclusion: '[' ID ']' ( '{' truthTableCase* '}' )? ;

truthTableCase: 'case' ID ':' ( truthTableAssignment ( ',' truthTableAssignment )* )? ;

truthTableAssignment: ID+ ;

proofStep: proofId '.' ( exp just | subProof ) ;

subProof
  : '{' '[' ID ( ',' ID)* ( ':' type )? ']' proofStep+ '}'
  | '{' proofStep+ '}'
  ;

proofId: INT | STRING ;

just: name callArgs? proofId* ;

exp: quant | yild | let | ifExp | accesses ;

expNoBlock: quant | yild | let | ifExp | accessesNoBlock ;
  
accesses: access ( OP access )* ifExp? ;
  
accessesNoBlock: accessNoBlock ( OP accessNoBlock )* ifExp? ;

quant: OP? ( '\\all' | '∀' | '\\some' | '∃' ) quantSuffix ;

quantSuffix
  : quantRange (',' quantRange )* ':' expNoBlock
  | ID ( ',' ID)* ':' type '.' expNoBlock
  ;

quantRange: ID 'in' exp ( ( '..' | '..<' ) exp )? ;

yild: 'yield' forRange+ ':' expNoBlock ;

let: 'let' binding+ 'in' expNoBlock ;

binding: ID ':=' exp ;

ifExp: '?' ( '{' ifExpMultiCase+ '}' | expNoBlock ':' expNoBlock );

ifExpMultiCase
  : 'case' pattern ( OP exp )? ':' exp // OP = &&
  | '_' exp? ':' exp
  ;

access: OP? term codeBlocks? accessSuffix? ;

accessSuffix
  : memberAccessSuffix
  | callAccessSuffix
  | typeArgsSuffix
  ;

memberAccessSuffix: '.' ID codeBlocks? accessSuffix? ;

callAccessSuffix: callArgs codeBlocks? ( memberAccessSuffix | callAccessSuffix )? ;

typeArgsSuffix: typeArgs codeBlocks? ( memberAccessSuffix | callAccessSuffix )? ;

codeBlocks: codeBlock+ ;

accessNoBlock: OP? term accessNoBlockSuffix? ;

accessNoBlockSuffix
  : memberAccessNoBlockSuffix
  | callAccessNoBlockSuffix
  | typeArgsNoBlockSuffix
  ;

memberAccessNoBlockSuffix: '.' ID accessNoBlockSuffix? ;

callAccessNoBlockSuffix: callArgs ( memberAccessNoBlockSuffix | callAccessNoBlockSuffix )? ;

typeArgsNoBlockSuffix: typeArgs ( memberAccessNoBlockSuffix | callAccessNoBlockSuffix )? ;

callArgs: '(' ( idExp ( ',' idExp )* )? ')' ;

idExp: ( ID ':=' )? exp ;

term: lit | ID | paren | interp | codeBlock ;

lit: 'true' | 'false' | '⊤' | '⊥' | INT | HEX | BIN | REAL | STRING | MSTRING ;

paren: '(' exp ( ',' exp )* ')' ;

codeBlock: '{' ( lambda | blockContent ) '}' ;

lambda: lparam ( ',' lparam )* '.' funSpec? blockContent ;

lparam: ID ':' type ;

funSpec: '[' modsID ( ',' name | ( '.' ID )* ) ']' ;  // mods = ( pure )

type: baseType ( '=>' funSpec? baseType )* ;

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

OP: ( OPSYM+ | '\\' IDF ) ;

HEX: '0x'  HEX_DIGIT ( '_' | HEX_DIGIT )* ( '.' IDF )?;

BIN: '0b' ( '0' | '1' ) ( '0' | '1' | '_' )* ( '.' IDF )? ;

INT: ( '0' | '-'? '1'..'9' ( DIGIT | '_' )* ) ( '.' IDF )? ;

REAL: ( '0' | '-'? '1'..'9' ( DIGIT | '_' )* ) ( '.' DIGIT ( DIGIT | '_' )* EXPONENT? | EXPONENT ) ( ( '.' IDF | 'f' | 'F' | 'd' | 'D' ) )? ;

CHAR: '\'' ( ESC_SEQ | ~('\''|'\\') ) '\'' ;

COMMENT
  : '//' ~( '\n' | '\r' )* '\r'? '\n' {$channel=HIDDEN;}
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
