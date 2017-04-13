/*
Copyright (c) 2016, Robby, Kansas State University
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

grammar Slang;

/*
==============================================
 Symbol     Unicode    ASCII
----------------------------------------------
   ⊤         22A4      true       T
   ⊥         22A5      false      F       _|_
   ≤         2264      <=
   ≥         2265      >=
   ≠         2260      !=
   ¬         00AC      not   neg  !        ~
   ∧         2227      and        &        ^
   ∨         2228      or         |        V
   ⊕         2295      xor        ^|
   →         21D2      implies    ->
   ∀         2200      forall     all      A
   ∃         2203      exists     some     E
   ⊢         22A2      |-
==============================================

Note: ---+ means at least three minus (-) characters
and it is used for truth table and a different form of sequent
*/

@header {
// @formatter:off
}

file
  : truthTable EOF                                      #TruthTableFile
  | NL* sequent NL* proof? NL* EOF                      #SequentFile
  ;

stmtFile
  : NL* proof NL* EOF                                   #ProofFile
  | NL* facts NL* EOF                                   #FactsFile
  | NL* theorems NL* EOF                                #TheoremsFile
  | NL* invariants NL* EOF                              #InvariantsFile
  ;

loopInvariantFile
  : NL* loopInvariant NL* EOF
  ;

methodContractfile
  : NL* methodContract NL* EOF
  ;

funDefFile
  : NL* funDefs NL* EOF
  ;

truthTable
  : NL*
    star='*' NL+
    HLINE NL+
    vars+=ID+ bar='|' formula NL+
    HLINE
    ( NL+ row )*
    NL+ HLINE
    ( NL+ status )?
    NL*
  ;

row
  : model+=bool* bar='|' eval+=bool*
  ;

bool
  : t=( 'T' | '⊤' | 'F' | '⊥' | '1' | '0' )
  ;

status
  : ID // ID in { "Tautology", "Contradictory", "Contingent" }
    ( // when "Contingent"
      NL+
      t='-' ( 'T' | '⊤' ) ':'
      ( NL* tContingentAssignments+=assignments )*
      NL+
      '-' ( 'F' | '⊥' ) ':'
      ( NL* fContingentAssignments+=assignments )*
    )?
  ;

assignments
  : '[' bool+ ']'
  ;

sequent // note: all newlines inside a sequent are whitespaces
  : ( premises+=formula ( ',' premises+=formula )* )?
    ( '|-' | '⊢' )
    conclusions+=formula ( ',' conclusions+=formula )*
  | premises+=formula*
    HLINE
    conclusions+=formula+
  ;

proof: tb='{' proofStep? ( NL+ proofStep? )* te='}' NL* ;

proofStep
  : NUM '.' formula justification                       #Step
  | sub=NUM '.' NL* '{' NL*
    assume=NUM '.'
    ( ID (':' type )?
    | formula ate='assume'
    | ID (':' type )? formula ate='assume' )
    ( NL+ proofStep? )*
    '}'                                                 #SubProof
  ;

primFormula
  : t=( 'true' | 'T' | '⊤'
      | 'false' | 'F' | '_|_' | '⊥' )                   #Boolean
  | '(' formula ')'                                     #Paren
  | ID                                                  #Var
  | NUM                                                 #Int
  | INT                                                 #IntLit
  | REAL                                                #RLit
  | FLOAT                                               #FloatLit
  ;

formula
  : primFormula primFormulaSuffix*                      #PFormula
  | op='-' formula                                      #Unary       // programming logic
  | op=( 'not' | 'neg' | '!' | '~' | '¬' ) formula      #Unary       // propositional logic
  | l=formula op=( '*' | '/' | '%' ) NL? r=formula      #Binary      // programming logic
  | l=formula op=( '+' | '-' ) NL? r=formula            #Binary      // programming logic
  | <assoc=right> l=formula op='+:' NL? r=formula       #Binary      // programming logic
  | l=formula op=':+' NL? r=formula                     #Binary      // programming logic
  | l=formula
    op=( '<' | '<=' | '≤' | '>' | '>=' | '≥'
       | '>>' | '>>>' | '<<' ) NL? r=formula            #Binary      // programming logic
  | l=formula
    op=( '=' |'==' | '!=' | '≠' ) NL?
    r=formula                                           #Binary      // programming logic
  | l=formula
    op=( 'and' | '&' | '^' | '∧' ) NL? r=formula        #Binary      // propositional logic
  | l=formula op=( 'xor' | '^|' | '⊕' ) NL? r=formula   #Binary      // propositional logic
  | l=formula
    op=( 'or' | '|' | 'V' | '∨' ) NL? r=formula         #Binary      // propositional logic
  | <assoc=right> l=formula
    op=( 'implies' | '->' | '→' ) NL? r=formula         #Binary      // propositional logic
  | qformula                                            #Quant       // predicate logic
  ;

primFormulaSuffix
  : '(' formulaUpdates ')'                              #FormulaUpdatesSuffix
  |  typeArgs? '(' formulaArgs? ')'                     #FormulaApplySuffix
  | '.' ID typeArgs? '(' formulaArgs? ')'               #FormulaMethodInvokeSuffix
  | '.' ID                                              #FormulaAccessSuffix
  ;

formulaArgs
  : formula ( ',' formula )*                            #PositionalFormulaArgs
  | ID '=' formula ( ',' ID '=' formula )*              #NamedFormulaArgs
  ;

formulaUpdates
  : formula '->' formula ( ',' formula '->' formula )*
  ;

qformula
  : q=( 'forall' | 'all' | 'A' | '∀'
      | 'exists' | 'some' | 'E' | '∃' )
    vars+=ID ( ',' vars+=ID )*
    ( ':' type
    | ':' '(' lo=formula ll='<'? '..' lh='<'? hi=formula ')'
    )? NL*
    qf=formula
  ;

name
  : ID ( '.' ID )*
  ;

type
  : name ( '[' type ( ',' type )* ']' )?
  | '(' type ( ',' type )+ ')'
  ;

typeArgs
  : '[' type ( ',' type )* ']'
  ;

typeParams
  : '[' typeParamArg ( ',' typeParamArg )* ']'
  ;

typeParamArg
  : ID ( '<:' type )?
  ;

justification
  : t='premise'                                         #Premise
  | ( tb='andi'  | tb=( '&' | '∧' | '^' ) ID ) // ID=="i"
    lStep=NUM rStep=NUM                                 #AndIntro
  | ( tb=('ande1' | 'ande2' )
    | tb=( '&' | '∧' | '^' ) ID ) // ID=="e1" or ID=="e2"
    andStep=NUM                                         #AndElim
  | ( tb=( 'ori1' | 'Vi1' | 'ori2' | 'Vi2' )
    | tb=( '|' | '∨' ) ID ) // ID=="i1" or ID=="i2"
    step=NUM                                            #OrIntro
  | ( tb=( 'ore' | 'Ve' )
    | tb=( '|' | '∨' ) ID ) // ID=="e"
    orStep=NUM lSubProof=NUM rSubProof=NUM              #OrElim
  | tb='impliesi' subProof=NUM                          #ImpliesIntro
  | tb='impliese' impliesStep=NUM antecedentStep=NUM    #ImpliesElim
  | {("->".equals(_input.LT(1).getText()) ||
      "→".equals(_input.LT(1).getText())) &&
     "i".equals(_input.LT(2).getText())}?
    tb=( '->' | '→' ) ID // ID=="i"
    subProof=NUM                                        #ImpliesIntro
  | {("->".equals(_input.LT(1).getText()) ||
      "→".equals(_input.LT(1).getText())) &&
      "e".equals(_input.LT(2).getText())}?
    tb=( '->' | '→' ) ID // ID=="e"
    impliesStep=NUM antecedentStep=NUM                  #ImpliesElim
  | tb=( 'noti' | 'negi' ) subProof=NUM                 #NegIntro
  | tb=('note' | 'nege') step=NUM negStep=NUM           #NegElim
  | {("!".equals(_input.LT(1).getText()) ||
      "~".equals(_input.LT(1).getText()) ||
      "¬".equals(_input.LT(1).getText())) &&
     "i".equals(_input.LT(2).getText())}?
    tb=( '!' | '~' | '¬' ) ID // ID=="i"
    subProof=NUM                                        #NegIntro
  | {("!".equals(_input.LT(1).getText()) ||
      "~".equals(_input.LT(1).getText()) ||
      "¬".equals(_input.LT(1).getText())) &&
     "e".equals(_input.LT(2).getText())}?
    tb=( '!' | '~' | '¬' ) ID // ID=="e"
    step=NUM negStep=NUM                                #NegElim
  | ( tb=( 'bottome' | 'falsee' )
    | tb=('_|_' | '⊥' ) ID // ID=="e"
    ) bottomStep=NUM                                    #BottomElim
  | tb='pbc' subProof=NUM                               #Pbc
  | tb='subst1' eqStep=NUM step=NUM                     #Subst1
  | tb='subst2' eqStep=NUM step=NUM                     #Subst2
  | tb='algebra' steps+=NUM*                            #Algebra
  | tb=('foralli' | 'alli' | 'Ai') subProof=NUM         #ForallIntro
  | tb=('foralle' | 'alle' | 'Ae')
    step=NUM formula+                                   #ForallElim
  | {"∀".equals(_input.LT(1).getText()) &&
     "i".equals(_input.LT(2).getText())}?
    tb='∀' ID // ID=="i"
    subProof=NUM                                        #ForallIntro
  | {"∀".equals(_input.LT(1).getText()) &&
     "e".equals(_input.LT(2).getText())}?
    tb='∀' ID // ID=="e"
    step=NUM formula+                                   #ForallElim
  | tb=( 'existsi' | 'somei' | 'Ei' )
    existsStep=NUM formula+                             #ExistsIntro
  | tb=( 'existse' | 'somee' | 'Ee' )
    step=NUM subProof=NUM                               #ExistsElim
  | {"∃".equals(_input.LT(1).getText()) &&
     "i".equals(_input.LT(2).getText())}?
    tb='∃' ID existsStep=NUM formula+                   #ExistsIntro
  | {"∃".equals(_input.LT(1).getText()) &&
     "e".equals(_input.LT(2).getText())}?
    tb='∃' t=ID // ID=="e"
    step=NUM subProof=NUM                               #ExistsElim
  | tb='invariant'                                      #Invariant
  | tb='fact' name                                      #Fct
  | tb='auto' steps+=NUM*                               #Auto
  | tb='coq' path                                       #Coq
  ;

path
  : t=( '..' | '.' | '/' | ID )
  ;

facts
  : tb='{' NL*
    'fact' NL*
    fact ( NL+ fact? )*
    te='}' NL*
  ;

theorems
  : tb='{' NL*
    'theorem' NL*
    theorem ( NL+ theorem? )*
    te='}' NL*
  ;

fact: ID typeParams? '.' NL* formula ;

theorem: ID typeParams? '.' NL* formula NL* proof ;

funDefs
  : '{'
    NL* funDefCond ( NL+ funDefCond )*
    ( NL+ where? )?
    '}'                                                 #FunDefConds
  | '{'
    NL* funDefSimple
    ( NL+ where? )?
    '}'                                                 #FunDefEq
  ;

funDefCond
  : '=' e=formula ',' NL* 'if' c=formula '(' ID ')'
  | '=' e=formula ',' NL* 'case' case '(' ID ')'
  | '=' e=formula ',' NL* 'case' case 'if' c=formula '(' ID ')'
  ;

case
  : ID ':' type                                         #TypeCase
  | ( ID '@' )? name?
    '(' ( pattern ( ',' pattern )* )? ')'               #PatternCase
  | '_'                                                 #WildcardCase
  ;

pattern
  : ID                                                  #IdPattern
  | ( ID '@' )? name?
    '(' ( pattern ( ',' pattern )* )? ')'               #StructurePattern
  ;

where
  : 'where' whereDef ( NL+ whereDef ) NL+
  ;

whereDef
  : ID ':' type '=' NL? formula
  ;

funDefSimple
  : '=' formula
  ;

loopInvariant
  : tb='{' NL*
    reads?
    modifies
    te='}' NL*
  | tb='{' NL*
    reads
    modifies?
    te='}' NL*
  | tb='{' NL*
    itb='invariant' NL* formula ( NL+ formula? )*
    reads?
    modifies?
    te='}' NL*
  ;

modifies
  : tb='modifies' NL* name ( ',' NL* name )* NL* ;

methodContract
  : tb='{' NL*
    contract NL*
    ( ( NL+ subContract )+ NL* )?
    te='}' NL*
  ;

contract
  : reads?
    ( ( 'requires' | 'pre' ) NL*
      rs+=formula ( NL+ rs+=formula? )* )? NL*
    modifies?
    ( ( 'ensures' | 'post' ) NL*
      es+=formula ( NL+ es+=formula? )* )?
  ;

subContract
  : ID '.' contract
  ;

reads
  : tb='reads' NL* name ( ',' NL* name )* NL* ;

invariants
  : tb='{' NL*
    'invariant' NL* formula ( NL+ formula? )*
    te='}' NL*
  ;

HLINE: '-' '-' '-'+ ;

NUM: '0' | [1-9] [0-9]* ;

ID: [a-zA-Z] [a-zA-Z0-9_]* ;

REAL // space is ignored
    : 'r' '"' '-'? ( '0' | [1-9] [ 0-9]* ) ( '.' ' '* [0-9] [ 0-9]* )? '"'
    ;

INT // space is ignored
    : ( 'z' | 'z8' | 'z16' | 'z32' | 'z64'
      | 'n' | 'n8' | 'n16' | 'n32' | 'n64'
      | 's8' | 's16' | 's32' | 's64'
      | 'u8' | 'u16' | 'u32' | 'u64' )
      '"' ( '0' | '-'? ' '* [0-9] [ 0-9]* | '0x' ' '*  [0-9a-fA-F] [ 0-9a-fA-F]* ) '"'
    ;

RESERVED
  : 'abstract' | 'case' | 'catch' | 'class'
  | 'do' | 'extends' | 'final'
  | 'finally' | 'for' | 'forSome' | 'implicit'
  | 'lazy' | 'macro' | 'match' | 'new'
  | 'null' | 'object' | 'override' | 'package' | 'private'
  | 'protected' | 'sealed' | 'super' | 'this'
  | 'throw' | 'trait' | 'try' | 'type'
  | 'with' | 'yield'
  | '<-' | '<:' | '<%' | '>:' | '#' | '@'
  ;

NL: '\r'? '\n' ;
// newlines are processed after lexing according to:
// http://www.scala-lang.org/files/archive/spec/2.11/01-lexical-syntax.html#newline-characters

LINE_COMMENT: '//' ~[\r\n]* -> channel(1) ;

COMMENT: '/*' .*? '*/' -> channel(1) ;

TAB: '\t'+ -> channel(2) ;

WS: [ \u000C]+ -> channel(2) ;


// From https://github.com/antlr/grammars-v4/blob/master/java8/Java8.g4
/*
 * [The "BSD license"]
 *  Copyright (c) 2014 Terence Parr
 *  Copyright (c) 2014 Sam Harwell
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *
 *  1. Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *  3. The name of the author may not be used to endorse or promote products
 *     derived from this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 *  IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 *  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 *  IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 *  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 *  NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 *  DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 *  THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 *  THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
// §3.10.5 String Literals
STRING
	: '"' StringCharacters? '"'
	;

FLOAT
    : FloatingPointLiteral
    ;

fragment
StringCharacters
	:	StringCharacter+
	;
fragment
StringCharacter
	:	~["\\]
	|	EscapeSequence
	;
// §3.10.6 Escape Sequences for Character and String Literals
fragment
EscapeSequence
	:	'\\' [btnfr"'\\]
    |   UnicodeEscape // This is not in the spec but prevents having to preprocess the input
	;

// This is not in the spec but prevents having to preprocess the input
fragment
UnicodeEscape
    :   '\\' 'u' HexDigit HexDigit HexDigit HexDigit
    ;

// §3.10.1 Integer Literals

fragment
HexDigit
	:	[0-9a-fA-F]
	;

fragment
Digits
	:	Digit (DigitsAndUnderscores? Digit)?
	;

fragment
Digit
	:	'0'
	|	NonZeroDigit
	;

fragment
NonZeroDigit
	:	[1-9]
	;

fragment
DigitsAndUnderscores
	:	DigitOrUnderscore+
	;

fragment
DigitOrUnderscore
	:	Digit
	|	'_'
	;

fragment
HexNumeral
	:	'0' [xX] HexDigits
	;

fragment
HexDigits
	:	HexDigit (HexDigitsAndUnderscores? HexDigit)?
	;

fragment
HexDigitsAndUnderscores
	:	HexDigitOrUnderscore+
	;

fragment
HexDigitOrUnderscore
	:	HexDigit
	|	'_'
	;

// §3.10.2 Floating-Point Literals

fragment
FloatingPointLiteral
	:	DecimalFloatingPointLiteral
	|	HexadecimalFloatingPointLiteral
	;

fragment
DecimalFloatingPointLiteral
	:	/* the second Digits is modified to be non-optional */
	    Digits '.' Digits ExponentPart? FloatTypeSuffix?
	|	'.' Digits ExponentPart? FloatTypeSuffix?
	|	Digits ExponentPart FloatTypeSuffix?
	|	Digits FloatTypeSuffix
	;

fragment
ExponentPart
	:	ExponentIndicator SignedInteger
	;

fragment
ExponentIndicator
	:	[eE]
	;

fragment
SignedInteger
	:	Sign? Digits
	;

fragment
Sign
	:	[+-]
	;

fragment
FloatTypeSuffix
	:	[fFdD]
	;

fragment
HexadecimalFloatingPointLiteral
	:	HexSignificand BinaryExponent FloatTypeSuffix?
	;

fragment
HexSignificand
	:	HexNumeral '.'?
	|	'0' [xX] HexDigits? '.' HexDigits
	;

fragment
BinaryExponent
	:	BinaryExponentIndicator SignedInteger
	;

fragment
BinaryExponentIndicator
	:	[pP]
	;

ERROR_CHAR: . ;
