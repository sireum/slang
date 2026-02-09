/*
 Copyright (c) 2017-2026, Robby, Kansas State University
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
// Tree-sitter grammar for Slang, converted from SlangLl2.g (ANTLR v3)

// Fragment helpers (not exposed as tree-sitter nodes)

const LETTER = /[a-zA-Z]/;
const DIGIT = /[0-9]/;
const HEX_DIGIT = /[0-9a-fA-F]/;
const OPSYM = /[\u2200-\u22FF\u2A00-\u2AFF\u27C0-\u27EF\u2980-\u29FF+\-*/%<>!&^|~:=]/;
const ESC_SEQ = /\\[btnfr"'\\]|\\u[0-9a-fA-F]{4,5}/;
const IDF = /[a-zA-Z_][a-zA-Z0-9_$]*(?:_[\u2200-\u22FF\u2A00-\u2AFF\u27C0-\u27EF\u2980-\u29FF+\-*/%<>!&^|~:=]+)?/;
const IDESC = /`[^\n\r\t]*`/;

module.exports = grammar({
  name: 'slang',

  extras: $ => [
    /[ \t\r\n]+/,
    $.line_comment,
    $.block_comment,
  ],

  word: $ => $.ID,

  conflicts: $ => [
    [$.name],
  ],

  rules: {

    // ==================== Top-level entry points ====================

    // file is the start rule; tree-sitter allows the start rule to match empty
    file: $ => seq(
      optional($.annot),
      repeat($.imprt),
      repeat($.mainMember),
      repeat($.pkg),
    ),

    expFile: $ => seq(optional($.annot), $.exp),

    stmtFile: $ => seq(optional($.annot), $.stmt),

    // ==================== Imports ====================

    imprt: $ => prec.right(seq('import', $.ID, optional($.importIdSuffix))),

    importIdSuffix: $ => seq('.', choice(
      $.importWildcardSuffix,
      $.importQualSuffix,
      $.importRenamesSuffix,
    )),

    importWildcardSuffix: $ => prec.right(seq('_', optional($.annot))),

    importQualSuffix: $ => prec.right(seq($.ID, optional($.importIdSuffix))),

    importRenamesSuffix: $ => seq('{', $.importRename, repeat($.importRenameSuffix), optional(','), '}'),

    importRenameSuffix: $ => seq(',', $.importRename),

    importRename: $ => prec.right(seq($.ID, '=>', $.ID, optional($.annot))),

    // ==================== Members and packages ====================

    mainMember: $ => choice($.stmt, $.typeDefn),

    pkg: $ => prec.right(seq(
      'package',
      repeat($.mod),
      optional($.name),
      optional($.annot),
      repeat($.imprt),
      choice(
        repeat($.member),
        $.pkgSuffix,
      ),
    )),

    pkgSuffix: $ => seq('{', repeat($.mainMember), '}'),

    init: $ => seq('.', '.', '{', optional($.annot), repeat($.stmt), '}'),

    member: $ => choice($.varDefn, $.defDefn, $.typeDefn, $.init),

    mod: $ => seq('@', $.ID, optional(seq('[', $.args, ']'))),

    // ==================== Arguments ====================

    args: $ => choice(
      seq(optional($.annot), $.rhs, repeat($.argSuffix)),
      seq($.namedArg, repeat($.namedArgSuffix)),
    ),

    argSuffix: $ => seq(',', optional($.annot), $.rhs),

    namedArgSuffix: $ => seq(',', $.namedArg),

    namedArg: $ => seq($.ID, '=', optional($.annot), $.rhs),

    // ==================== Names ====================

    name: $ => seq($.ID, repeat($.nameSuffix)),

    nameSuffix: $ => seq('.', $.ID),

    // ==================== Type definitions ====================

    typeDefn: $ => prec.right(seq(
      'type',
      repeat($.mod),
      $.ID,
      optional($.typeParams),
      optional(choice(
        $.typeDefnEnumSuffix,
        $.typeDefnAliasSuffix,
        $.typeDefnAdtSuffix,
      )),
    )),

    typeDefnEnumSuffix: $ => seq(':', $.enumMembers),

    typeDefnAliasSuffix: $ => seq('=', $.type),

    typeDefnAdtSuffix: $ => prec.right(choice(
      seq($.params, optional($.supers), optional($.annot), optional($.typeDefnAdtMembers)),
      seq($.supers, optional($.annot), optional($.typeDefnAdtMembers)),
      seq($.annot, optional($.typeDefnAdtMembers)),
      $.typeDefnAdtMembers,
    )),

    typeDefnAdtMembers: $ => seq('{', repeat($.member), '}'),

    typeParams: $ => seq('[', $.typeParam, repeat($.typeParamSuffix), ']'),

    typeParamSuffix: $ => seq(',', $.typeParam),

    typeParam: $ => seq(repeat($.mod), $.ID),

    enumMembers: $ => seq('{', $.ID, repeat($.commaId), optional(','), '}'),

    commaId: $ => seq(',', $.ID),

    // ==================== Parameters ====================

    params: $ => seq('(', $.param, repeat($.commaParams), optional(','), ')'),

    commaParams: $ => seq(',', $.param),

    param: $ => seq(optional($.VAR), repeat($.mod), $.ID, ':', optional('=>'), $.type),

    supers: $ => seq(':', $.supr, repeat($.commaSuper)),

    commaSuper: $ => seq(',', $.supr),

    supr: $ => prec.right(seq(optional($.annot), $.name, optional($.typeArgs))),

    // ==================== Annotations ====================

    annot: $ => seq('@', '[', repeat($.annotArg), ']'),

    annotArg: $ => seq(
      choice($.ID, $.STRING),
      choice(
        seq($.exp, repeat($.commaExp)),
        $.annotArgNested,
      ),
    ),

    annotArgNested: $ => seq('[', repeat1($.annotArg), ']'),

    // ==================== Variable definitions ====================

    varDefn: $ => prec.right(seq(
      $.VAR,
      repeat($.mod),
      $.ID,
      optional($.colonType),
      optional($.annot),
      optional($.assignSuffix),
    )),

    assignSuffix: $ => seq('=', optional($.annot), $.rhs),

    // ==================== Def definitions ====================

    defDefn: $ => prec.right(seq(
      'def',
      repeat($.mod),
      $.defId,
      optional($.typeParams),
      optional($.defParams),
      optional($.defnTypeSuffix),
      optional($.assignSuffix),
    )),

    defnTypeSuffix: $ => prec.right(seq(':', $.type, optional($.annot))),

    defId: $ => choice($.ID, $.OP, $.SYMBOL),

    defParams: $ => seq('(', $.defParam, optional($.defParamSuffix), optional(','), ')'),

    defParam: $ => seq(repeat($.mod), $.ID, ':', $.type),

    defParamSuffix: $ => seq(',', choice(
      $.defParamSuffixVarargs,
      seq($.defParam, optional($.defParamSuffix)),
    )),

    defParamSuffixVarargs: $ => seq('..', $.defParam),

    // ==================== Statements ====================

    stmt: $ => choice(
      $.expOrAssignStmt,
      $.varPattern,
      $.ifStmt,
      $.whileStmt,
      $.forStmt,
      $.deduceStmt,
      $.matchStmt,
      $.defStmt,
      $.assertumeStmt,
    ),

    assertumeStmt: $ => seq(choice('assert', 'assume'), $.exp, optional($.commaExp)),

    defStmt: $ => prec.right(seq(
      'def',
      repeat($.mod),
      $.defId,
      optional($.typeParams),
      optional($.defParams),
      optional($.defnTypeSuffix),
      optional($.assignSuffix),
    )),

    expOrAssignStmt: $ => choice($.idStmt, $.expStmt, $.doStmt),

    idStmt: $ => prec.right(seq($.ID, optional($.idStmtSuffix))),

    idStmtSuffix: $ => choice($.annot, $.assignSuffix, $.labelSuffix),

    labelSuffix: $ => prec.right(seq(':', optional($.annot))),

    expStmt: $ => prec.right(seq($.exp0, repeat1($.access), optional($.annot), optional($.assignSuffix))),

    doStmt: $ => prec.right(seq('do', optional($.annot), choice($.exp, seq(repeat($.mod), $.block)))),

    varPattern: $ => seq($.VAR, $.pattern0, optional($.colonType1), '=', optional($.annot), $.rhs),

    rhs: $ => choice($.exp, $.block, $.ifStmt, $.matchStmt),

    // ==================== Control flow ====================

    ifStmt: $ => prec.right(seq('if', $.exp, optional($.annot), $.block, optional($.els))),

    block: $ => seq('{', optional($.annot), optional($.blockContent), '}'),

    blockContent: $ => choice(
      seq(repeat1($.stmt), optional($.ret)),
      $.ret,
    ),

    ret: $ => prec.right(seq(choice('return', 'halt'), optional($.annot), optional($.rhs))),

    els: $ => seq('else', choice($.elsIf, $.block)),

    elsIf: $ => prec.right(seq('if', $.exp, optional($.annot), $.block, optional($.els))),

    whileStmt: $ => prec.right(seq('while', $.exp, optional($.annot), $.block)),

    forStmt: $ => seq('for', $.forRange, repeat($.commaForRange), $.block),

    forRange: $ => prec.right(seq(
      choice($.ID, '_'),
      ':',
      $.exp,
      optional($.rangeSuffix),
      optional($.ifExp),
      optional($.annot),
    )),

    commaForRange: $ => seq(',', $.forRange),

    rangeSuffix: $ => seq(choice('..', '..<'), $.exp, optional($.byExp)),

    byExp: $ => seq('by', $.exp),

    commaExp: $ => seq(',', $.exp),

    // ==================== Match ====================

    matchStmt: $ => prec.right(seq('match', $.exp, optional($.annot), $.matchCases)),

    matchCases: $ => seq('{', repeat1($.cas), '}'),

    // ==================== Patterns ====================

    pattern: $ => seq(
      optional($.annot),
      choice($.idTypePattern, $.pattern0, $.wildCardPattern, $.wildCardSeqPattern),
    ),

    pattern0: $ => choice(
      $.lit,
      $.refPattern,
      $.patterns,
      seq($.name, optional($.patterns)),
      $.idNamePattern,
    ),

    refPattern: $ => seq('.', $.name),

    idTypePattern: $ => seq($.ID, $.colonType1),

    colonType1: $ => seq(':', $.type1),

    idNamePattern: $ => seq($.ID, '@', $.name, $.patterns),

    wildCardPattern: $ => prec.right(seq('_', optional($.colonType1))),

    wildCardSeqPattern: $ => '*',

    patterns: $ => seq('(', $.patternsArg, optional(','), ')'),

    patternsArg: $ => choice(
      seq($.pattern, repeat($.commaPattern)),
      seq($.namedPattern, repeat($.commaNamedPattern)),
    ),

    namedPattern: $ => seq($.ID, '=', $.pattern),

    commaPattern: $ => seq(',', $.pattern),

    commaNamedPattern: $ => seq(',', $.ID, '=', $.pattern),

    // ==================== Expressions ====================

    exp: $ => choice($.exp3, $.forExp, $.defAnon, $.quant),

    exp3: $ => prec.right(seq($.exp2, repeat($.infixSuffix))),

    infixSuffix: $ => seq($.infixOp, $.exp2),

    infixOp: $ => choice($.OP, $.SYMBOL, '<', '>', '<>', '*'),

    exp2: $ => prec.right(seq($.exp1, repeat($.access), optional($.eta))),

    eta: $ => '_',

    exp1: $ => seq(optional($.OP), choice($.exp0, $.paren)),

    exp0: $ => choice($.idExp, $.thisExp, $.superExp, $.lit, $.interp, $.pureBlock, $.jsonLit),

    pureBlock: $ => seq('@', '{', repeat1($.stmt), '}'),

    idExp: $ => prec.right(seq($.ID, optional($.typeArgs))),

    thisExp: $ => 'this',

    superExp: $ => 'super',

    // ==================== Access ====================

    access: $ => seq(optional('?'), choice($.fieldAccess, $.applyAccess)),

    fieldAccess: $ => prec.right(seq('.', $.ID, optional($.typeArgs))),

    applyAccess: $ => prec.right(seq('(', optional($.args), optional(','), ')', optional($.fn))),

    fn: $ => seq('{', ':', optional($.annot), optional($.fnBody), '}'),

    fnBody: $ => choice($.blockContent, repeat1($.cas)),

    // ==================== Literals ====================

    lit: $ => choice(
      'true',
      'false',
      $.INT,
      $.HEX,
      $.BIN,
      $.REAL,
      $.STRING,
      $.MSTR,
    ),

    // ==================== JSON literals ====================

    jsonLit: $ => seq('`', choice($.jsonObject, $.jsonArray, $.jsonParen)),

    jsonParen: $ => seq('(', $.jsonExp, ')'),

    json: $ => choice($.jsonObject, $.jsonArray, $.jsonExp),

    jsonObject: $ => seq('{', $.jsonKeyValue, repeat($.commaJsonKeyValue), optional(','), '}'),

    jsonKeyValue: $ => seq($.jsonKey, ':', $.json),

    jsonKey: $ => choice($.ID, $.STRING),

    commaJsonKeyValue: $ => seq(',', $.jsonKeyValue),

    jsonArray: $ => seq('[', $.json, $.commaJson, optional(','), ']'),

    commaJson: $ => seq(',', $.json),

    jsonExp: $ => choice($.exp, $.jsonNull),

    jsonNull: $ => 'null',

    // ==================== Parenthesized expressions ====================

    paren: $ => seq('(', optional($.annot), $.parenArgs, optional(','), ')'),

    parenArgs: $ => choice(
      seq($.exp, optional($.annot), repeat($.commaExpAnnot)),
      seq($.namedExpAnnot, repeat($.commaNamedExpAnnot)),
    ),

    namedExpAnnot: $ => prec.right(seq($.ID, '=', $.exp, optional($.annot))),

    commaExpAnnot: $ => prec.right(seq(',', $.exp, optional($.annot))),

    commaNamedExpAnnot: $ => prec.right(seq(',', $.ID, '=', $.exp, optional($.annot))),

    // ==================== Case ====================

    cas: $ => prec.right(seq('case', $.pattern, optional($.ifExp), '=>', optional($.annot), optional($.blockContent))),

    ifExp: $ => seq('if', $.exp),

    // ==================== For/yield, anonymous def, quantifiers ====================

    forExp: $ => seq('yield', optional($.annot), $.forRange, repeat($.commaForRange), '=>', optional($.annot), $.rhs),

    defAnon: $ => prec.right(seq('def', repeat($.mod), $.defParams, optional($.colonType), '.', optional($.annot), $.rhs)),

    colonType: $ => seq(':', $.type),

    quant: $ => seq(
      choice('\u2200', '\u2203', $.SYMBOL),
      repeat1($.quantRange),
      '=>',
      optional($.annot),
      $.rhs,
    ),

    quantRange: $ => prec.right(seq(
      repeat($.idComma),
      $.ID,
      optional($.annot),
      ':',
      optional($.annot),
      $.exp,
      optional($.quantRangeSuffix),
    )),

    idComma: $ => seq($.ID, ','),

    quantRangeSuffix: $ => seq(choice('..', '..<'), optional($.annot), $.exp),

    // ==================== Deduce ====================

    deduceStmt: $ => seq('deduce', choice($.truthTable, $.proof, repeat1($.sequent), $.expProof)),

    proof: $ => seq('{', repeat($.proofStep), '}'),

    sequent: $ => prec.right(seq(':', optional($.exps), choice('\u22A2', '|-'), $.exp, optional($.proof))),

    exps: $ => seq($.exp, repeat($.commaExp)),

    expProof: $ => seq('(', $.expJustOpt, repeat($.commaExpJustOpt), optional(','), ')'),

    commaExpJustOpt: $ => seq(',', $.expJustOpt),

    expJustOpt: $ => prec.right(seq($.exp, optional($.just))),

    proofStep: $ => prec.right(seq(alias(choice($._proofStepIntDot, $._proofStepStrDot), $.proofId), choice(
      seq($.exp, $.just),
      $.subProof,
      $.assumeProofStep,
      $.assertProofStep,
    ))),

    assumeProofStep: $ => seq('assume', $.exp),

    assertProofStep: $ => seq('assert', $.exp, $.subProof),

    subProof: $ => seq('{', repeat($.freshIds), repeat1($.proofStep), '}'),

    freshIds: $ => seq($.ID, repeat($.commaId), optional($.colonType)),

    proofId: $ => choice($.INT, $.STRING),

    just: $ => prec.right(seq('by', $.name, optional($.justTypeArgs), optional($.justArgs), repeat($.proofId))),

    justArgs: $ => seq('(', $.args, optional(','), ')'),

    justTypeArgs: $ => seq('[', $.type, repeat($.commaType), ']'),

    commaType: $ => seq(',', $.type),

    // ==================== Truth tables ====================

    truthTable: $ => prec.right(seq(
      repeat1('*'),
      $.HLINE,
      repeat1($.ID),
      repeat1($.colonExp),
      $.HLINE,
      repeat1($.ID),
      repeat1($.colonIds),
      $.HLINE,
      optional($.truthTableConclusion),
    )),

    colonExp: $ => seq(':', $.exp),

    colonIds: $ => seq(':', repeat1($.ID)),

    truthTableConclusion: $ => seq('[', $.ID, ']', optional($.truthTableCases)),

    truthTableCases: $ => seq('{', repeat($.truthTableCase), '}'),

    truthTableCase: $ => seq('case', $.ID, '=>', optional($.truthTableAssignments)),

    truthTableAssignments: $ => seq($.truthTableAssignment, repeat($.commaTruthTableAssignment)),

    truthTableAssignment: $ => repeat1($.ID),

    commaTruthTableAssignment: $ => seq(',', $.truthTableAssignment),

    // ==================== Types ====================

    type: $ => seq($.type1, repeat($.typeSuffix)),

    typeSuffix: $ => seq('=>', optional($.annot), $.type1),

    type1: $ => choice($.parenType, seq($.type0, repeat($.type0Suffix))),

    parenType: $ => seq('(', $.typeParenArgs, optional(','), ')'),

    type0Suffix: $ => seq(choice($.OP, $.SYMBOL), $.type0),

    typeParenArgs: $ => choice(
      seq(optional($.annot), $.type, repeat($.commaAnnotType)),
      seq($.namedType, repeat($.commaNamedType)),
    ),

    commaAnnotType: $ => seq(',', optional($.annot), $.type),

    namedType: $ => seq($.ID, '=', optional($.annot), $.type),

    commaNamedType: $ => seq(',', $.ID, '=', optional($.annot), $.type),

    type0: $ => prec.right(seq($.ID, optional($.typeArgs))),

    typeArgs: $ => seq('[', $.typeParenArgs, ']'),

    // ==================== String interpolation ====================

    interp: $ => choice(
      $.SP,
      seq($.SPB, $.sinterp),
      $.MSTRP,
      seq($.MSTRPB, $.mstrinterp),
    ),

    sinterp: $ => seq($.exp, choice(
      seq($.SPM, $.sinterp),
      $.SPE,
    )),

    mstrinterp: $ => seq($.exp, choice(
      seq($.MSTRPM, $.mstrinterp),
      $.MSTRPE,
    )),

    // ==================== Lexical tokens ====================

    _proofStepIntDot: $ => token(seq(
      choice('0', seq(optional('-'), /[1-9]/, repeat(/[0-9_]/))),
      optional(seq(LETTER, choice(IDF, IDESC))),
      '.',
    )),

    _proofStepStrDot: $ => token(seq(
      '"', repeat(choice(ESC_SEQ, /[^\\"]/)), '"',
      '.',
    )),

    VAR: $ => choice('val', 'var'),

    SYMBOL: $ => seq('\\', token.immediate(IDF)),

    STRING: $ => token(seq('"', repeat(choice(ESC_SEQ, /[^\\"]/)), '"')),

    SP: $ => token(seq(IDF, '"', repeat(choice(ESC_SEQ, /[^\\"$]/, '$$')), '"')),

    SPB: $ => token(seq(IDF, '"', repeat(choice(ESC_SEQ, /[^\\"$]/, '$$')), '$')),

    SPM: $ => token(seq('$', repeat(choice(ESC_SEQ, /[^\\"$]/, '$$')), '$')),

    SPE: $ => token(seq('$', repeat(choice(ESC_SEQ, /[^\\"$]/, '$$')), '"')),

    MSTR: $ => token(seq(
      repeat(seq('#', /(?:[^\n\r$]|\$\$)*\r?\n/, optional(/[ \t]+/))),
      '#', /(?:[^\n\r$]|\$\$)*\r?\n/,
    )),

    MSTRP: $ => token(seq(
      IDF,
      repeat(seq('#', /(?:[^\n\r$]|\$\$)*\r?\n/, optional(/[ \t]+/))),
      '#', /(?:[^\n\r$]|\$\$)*\r?\n/,
    )),

    MSTRPB: $ => token(seq(
      IDF,
      repeat(seq('#', /(?:[^\n\r$]|\$\$)*\r?\n/, optional(/[ \t]+/))),
      '#', /(?:[^\n\r$]|\$\$)*/, '$',
    )),

    MSTRPM: $ => token(seq(
      '$', /(?:[^\n\r$]|\$\$)*\r?\n/, optional(/[ \t]+/),
      repeat(seq('#', /(?:[^\n\r$]|\$\$)*\r?\n/, optional(/[ \t]+/))),
      '#', /(?:[^\n\r$]|\$\$)*/, '$',
    )),

    MSTRPE: $ => token(seq('$', optional(/(?:[^\n\r$]|\$\$)*\r?\n/))),

    ID: $ => token(choice(seq(optional("'"), IDF), IDESC)),

    HLINE: $ => token(/-----+/),

    OP: $ => token(choice(
      /[\u2200-\u22FF\u2A00-\u2AFF\u27C0-\u27EF\u2980-\u29FF+\-*/%<>!&^|~:=]+/,
      seq('\\', IDF),
    )),

    HEX: $ => token(seq('0x', HEX_DIGIT, repeat(choice('_', HEX_DIGIT)))),

    BIN: $ => token(seq('0b', /[01]/, repeat(/[01_]/))),

    INT: $ => token(seq(
      choice('0', seq(optional('-'), /[1-9]/, repeat(/[0-9_]/))),
      optional(seq(LETTER, choice(IDF, IDESC))),
    )),

    REAL: $ => token(seq(
      choice('0', seq(optional('-'), /[1-9]/, repeat(/[0-9_]/))),
      choice(
        seq('.', DIGIT, repeat(/[0-9_]/), optional(seq(/[eE]/, optional(/[+-]/), repeat1(/[0-9_]/)))),
        seq(/[eE]/, optional(/[+-]/), repeat1(/[0-9_]/)),
      ),
      optional(/[fFdD]/),
    )),

    // ==================== Comments (extras) ====================

    line_comment: $ => token(seq('//', /[^\n\r]*/, optional(seq(optional('\r'), '\n')))),

    block_comment: $ => token(seq('/*', /(?:[^*]|\*[^/])*/, choice('*/', '**/'))),
  },
});
