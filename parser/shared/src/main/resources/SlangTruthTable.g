/*
 Copyright (c) 2017-2025, Robby, Kansas State University
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
grammar SlangTruthTable;

options {
  output = AST;
  ASTLabelType = Object;
  k = 1;
}

@header { package org.sireum.lang.parser; }

@lexer::header { package org.sireum.lang.parser; }


file: nls? table EOF ;

table: stars
       hlinep
       header
       hlinep
       rows
       hlines
       conclusion? ;

stars: OTHER+ NL+ ; // others should be *

header: others HASH others NL+ ;

hlinep: HLINE NL+ ;

rows: row* ;

row: others HASH others NL ;

hlines: HLINE NL* ;
        
conclusion:
    'Tautology' NL*
  | 'Contradictory' NL*
  | 'Contingent' NL ( cas ( NL cas? )* )?
  | 'Valid' assign*  NL*
  | 'Invalid' assign*  NL* ;

cas: OTHER OTHER assign+ ; // second OTHER has to be colon

assign: LSQUARE others RSQUARE ;

others: OTHER* ;

nls: NL+;

// Lexical definitions

HLINE: '-' '-' '-' '-' '-'+ ;

HASH: '#' ;

NL: '\n' ;

LSQUARE: '[' ;

RSQUARE: ']' ;

COMMENT: '//' ~( '\n' | '\r' )* '\r'? '\n'  {$channel=HIDDEN;} ;

WS: ( ' ' | '\t' | '\r' )+                  {$channel=HIDDEN;} ;

OTHER: . ;