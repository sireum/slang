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

imprt: IMPORT ID importIdSuffix? ;

importIdSuffix: DOT ( importWildcardSuffix | importQualSuffix | importRenamesSuffix ) ;

importWildcardSuffix: UNDERSCORE annot? ;

importQualSuffix: ID importIdSuffix? ;

importRenamesSuffix: LBRACE importRename importRenameSuffix* RBRACE ;

importRenameSuffix: COMMA importRename ;

importRename: ID ARROW ID annot? ;

mainMember: stmt | typeDefn ;

pkg: PACKAGE mod* name? annot? imprt* member* ;

init: DOT DOT LBRACE annot? stmt* RBRACE ;

member: varDefn | defDefn | typeDefn | init ;

mod: AT ID ( LSQUARE args RSQUARE )? ;

args: annot? rhs argSuffix* | namedArg namedArgSuffix* ;

argSuffix: COMMA annot? rhs ;

namedArgSuffix: COMMA namedArg ;

namedArg: ID ASSIGN annot? rhs ;

name: ID nameSuffix* ;

nameSuffix: DOT ID ;

typeDefn: TYPE mod* ID typeParams? ( typeDefnEnumSuffix | typeDefnAliasSuffix | typeDefnAdtSuffix ) ;

typeDefnEnumSuffix: COLON enumMembers ;

typeDefnAliasSuffix: ASSIGN type ;

typeDefnAdtSuffix: params? supers? annot? typeDefnAdtMembers? ;

typeDefnAdtMembers: LBRACE member* RBRACE ;

typeParams: LSQUARE typeParam typeParamSuffix* RSQUARE ;

typeParamSuffix: COMMA typeParam ;

typeParam: mod* ID ;

enumMembers: LBRACE ID commaId* RBRACE ;

commaId: COMMA ID ;

params: LPAREN param commaParams* RPAREN ;

commaParams: COMMA param ;

param: VAR? mod* ID COLON ARROW? type ;

supers: COLON supr commaSuper* ;

commaSuper: COMMA supr ;

supr: annot? name typeArgs? ;

annot: AT LSQUARE args? RSQUARE ;

varDefn: VAR mod* ID COLON type annot? assignSuffix? ;

assignSuffix: ASSIGN annot? rhs ;

defDefn: DEF mod* defId typeParams? defParams? defnTypeSuffix? assignSuffix? ;

defnTypeSuffix: COLON type annot? ;

defId: ID | OP | SYMBOL ;

defParams: LPAREN defParam defParamSuffix? RPAREN ;

defParam: mod* ID COLON type ;

defParamSuffix: COMMA ( defParamSuffixVarargs | defParam defParamSuffix? ) ;

defParamSuffixVarargs: TO defParam ;

stmt: expOrAssignStmt | varPattern | ifStmt | whileStmt | forStmt | deduceStmt | matchStmt | defStmt ;

defStmt: DEF mod* defId typeParams? defParams? defnTypeSuffix? assignSuffix? ;

expOrAssignStmt: idStmt | expStmt | doStmt ;

idStmt: ID idStmtSuffix? ;

idStmtSuffix: annot | assignSuffix | labelSuffix ;

labelSuffix: COLON annot? ;

expStmt: exp0 access+ annot? assignSuffix? ;

doStmt: DO annot? ( exp | mod* block ) ;

varPattern: VAR pattern0 colonType1? ASSIGN annot? rhs ;

rhs: exp | block | ifStmt | matchStmt ;

ifStmt: IF exp annot? block els? ;

block: LBRACE annot? blockContent RBRACE ;

blockContent: stmt* ret? ;

ret: RETURN annot? rhs? ;

els: ELSE ( elsIf | block ) ;

elsIf: IF exp annot? block els? ;

whileStmt: WHILE exp annot? block ;

forStmt: FOR forRange commaForRange* block ;

forRange: ( ID | UNDERSCORE ) COLON exp rangeSuffix? ifExp? annot? ;

commaForRange: COMMA forRange ;

rangeSuffix: ( TO |  UNTIL ) exp byExp? ;

byExp: BY exp ;

commaExp: COMMA exp ;

matchStmt: MATCH exp annot? matchCases ;

matchCases: LBRACE cas+ RBRACE ;

pattern:  annot? ( idTypePattern | pattern0 | wildCardPattern | wildCardSeqPattern) ;

pattern0: lit | refPattern | patterns | name patterns? | idNamePattern ;

refPattern: DOT name ;

idTypePattern: ID colonType1 ;

colonType1: COLON type1 ;

idNamePattern: ID AT name patterns ;

wildCardPattern: UNDERSCORE colonType1? ;

wildCardSeqPattern: STAR ;

patterns: LPAREN patternsArg RPAREN ;

patternsArg: pattern commaPattern* | namedPattern commaNamedPattern* ;

namedPattern: ID ASSIGN pattern ;

commaPattern: COMMA pattern ;

commaNamedPattern: COMMA ID ASSIGN pattern ;

exp: exp3 | forExp | defAnon | quant ;

exp3: exp2 infixSuffix* ;

infixSuffix: infixOp exp2 ;

infixOp: OP | SYMBOL | LANGLE | RANGLE | LRANGLE | STAR ;

exp2: exp1 access* eta? ;

eta: UNDERSCORE ;

exp1: OP? ( exp0 | paren ) ;

exp0: idExp | thisExp | superExp | lit | interp ;

idExp: ID typeArgs? ;

thisExp: THIS ;

superExp: SUPER ;

access: fieldAccess | applyAccess ;

fieldAccess: DOT ID typeArgs? ;

applyAccess: LPAREN args? RPAREN fn? ;

fn: LBRACE ARROW annot? fnBody RBRACE ;

fnBody: blockContent | cas+ ;

lit: TRUE | FALSE | INT | HEX | BIN | REAL | STRING |  MSTR /* | MSTRING */ ;

paren: LPAREN annot? parenArgs RPAREN ;

parenArgs : exp annot? commaExpAnnot* | namedExpAnnot commaNamedExpAnnot* ;

namedExpAnnot: ID ASSIGN exp annot? ;

commaExpAnnot: COMMA exp annot? ;

commaNamedExpAnnot: COMMA ID ASSIGN exp annot? ;

cas: CASE pattern ifExp? ARROW annot? blockContent ;

ifExp: IF exp ;

forExp: YIELD annot? forRange commaForRange* ARROW annot? rhs ;

defAnon: DEF mod* defParams colonType? DOT annot? rhs ;

colonType: COLON type ;

quant: ( ALL | SOME | SYMBOL ) quantRange+ ARROW annot? rhs ;

quantRange: idComma* ID annot? COLON annot? exp quantRangeSuffix? ; // exp can refer to a type

idComma: ID COMMA ;

quantRangeSuffix: ( TO |  UNTIL ) annot? exp ;

deduceStmt: DEDUCE ( truthTable | proof | sequent | expProof ) ;

proof: LBRACE proofStep* RBRACE ;

sequent: COLON exps? SEQUENT exp proof? ;

exps: exp commaExp* ;

expProof: LPAREN expJustOpt commaExpJustOpt* RPAREN ;

commaExpJustOpt: COMMA expJustOpt ;

expJustOpt: exp just? ;

proofStep: proofId DOT ( exp just? | subProof ) ;

subProof: LBRACE freshIds* proofStep+ RBRACE ;

freshIds: ID commaId* colonType? ;

proofId: INT | STRING ;

just: BY name justTypeArgs? justArgs? proofId* DOT ;

justArgs: LPAREN args RPAREN ;

justTypeArgs: LSQUARE type commaType* RSQUARE ;

commaType: COMMA type ;

truthTable: STAR+
            HLINE
            ID+ colonExp+
            HLINE
            ID+ colonIds+            // ID consists only T or F
            HLINE
            truthTableConclusion? ;

colonExp: COLON exp ;

colonIds: COLON ID+ ;

truthTableConclusion: LSQUARE ID RSQUARE truthTableCases? ;

truthTableCases: LBRACE truthTableCase* RBRACE ;

truthTableCase: CASE ID ARROW truthTableAssignments? ;

truthTableAssignments: truthTableAssignment commaTruthTableAssignment* ;

truthTableAssignment: ID+ ;

commaTruthTableAssignment: COMMA truthTableAssignment ;

type: type1 typeSuffix* ;

typeSuffix: ARROW annot? type1 ;

type1: parenType | type0 type0Suffix* ;

parenType: LPAREN typeParenArgs RPAREN ;

type0Suffix: ( OP | SYMBOL ) type0 ;

typeParenArgs: annot? type commaAnnotType* | namedType commaNamedType* ;

commaAnnotType: COMMA annot? type ;

namedType: ID ASSIGN annot? type ;

commaNamedType: COMMA ID ASSIGN annot? type ;

type0: ID typeArgs? ;

typeArgs: LSQUARE typeParenArgs RSQUARE ;

interp: SP | SPB sinterp | MSTRP | MSTRPB mstrinterp /* | MSP | MSPB minterp */ ;

sinterp: exp ( SPM sinterp | SPE ) ;

//strinterp: exp ( MSTRPM sinterp | MSTRPE ) ;

mstrinterp: exp ( MSTRPM mstrinterp | MSTRPE ) ;


// Lexical definitions
ALL:        '∀'           ; ARROW:      '=>'          ; ASSIGN:     '='           ; AT:         '@'           ;
COMMA:      ','           ; COLON:      ':'           ; DOT:        '.'           ; UNDERSCORE: '_'           ;
LBRACE:     '{'           ; LPAREN:     '('           ; LSQUARE:    '['           ; QUESTION:   '?'           ;
RBRACE:     '}'           ; RPAREN:     ')'           ; RSQUARE:    ']'           ; SEQUENT:    '⊢' | '|-'    ;
SOME:       '∃'           ; TO:         '..'          ; UNTIL:      '..<'         ; LANGLE:     '<'           ;
RANGLE:     '>'           ; LRANGLE:    '<>'          ; STAR:       '*'           ;

CASE:       'case'        ; DEDUCE:     'deduce'      ; DEF:        'def'         ; DO:         'do'          ;
FALSE:      'false'       ; ELSE:       'else'        ; FOR:        'for'         ; TYPE:       'type'        ;
IF:         'if'          ; IMPORT:     'import'      ; MATCH:      'match'       ; PACKAGE:    'package'     ;
RETURN:     'return'      ; SUPER:      'super'       ; THIS:       'this'        ; TRUE:       'true'        ; 
WHILE:      'while'       ; YIELD:      'yield'       ; VAR:        'val' | 'var' ; BY:         'by'          ;

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

INT: ( '0' | '-'? '1'..'9' ( DIGIT | '_' )* ) ( LETTER ( IDF | IDESC ) )? ;

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