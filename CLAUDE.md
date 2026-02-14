# Slang Language Constructs

Slang has two surface syntaxes: the original **Scala-based** syntax (parsed by the scalameta-based `SlangParser`) and a dedicated **LL(2)** syntax (parsed by the generated `SlangLl2Parser`). Both produce the same AST defined in `AST.scala`.

## Key Syntactic Differences

The LL(2) syntax differs from Scala-based syntax primarily in:
- **No parentheses** around `if`/`while`/`for`/`match` conditions
- **Keywords precede subjects**: `match exp` instead of `exp match`
- **`package` replaces `object`** for singleton containers
- **`type` keyword prefix** for all type definitions (`@datatype`, `@record`, `@sig`, `@enum`, `@range`, `@bits`, `@alias`)
- **`do` prefix** for expression statements that aren't invocations or selections
- **`def` keyword prefix** for anonymous functions
- **`yield` keyword prefix** for `for`-yield expressions
- **Annotation blocks** `@[...]` for method contracts and loop contracts (instead of `Contract(...)`, `Invariant(...)`)
- **`do @spec { ... }`** for spec blocks (instead of `Spec { ... }`)
- **File extensions**: Scala-based uses `.sc` (script) and `.scala` (full program); LL(2) uses `.sl` (script) and `.slang` (full program)
- **No semicolons** — LL(2) is not whitespace-significant; it uses statement keywords and braces to determine statement termination (with some exceptional conventions for expression statements)
- **Omission** of `import org.sireum._` (implicit in LL(2))

## Construct Mapping Table

| Category | Scala-based Syntax | LL(2) Syntax | AST Node |
|---|---|---|---|
| **Program** | `// #Sireum \n package p import org.sireum._ ...` | `package p\n...` | `TopUnit.Program` |
| **Import** | `import a.b.c` | `import a.b.c` | `Stmt.Import` |
| **Import wildcard** | `import a.b._` | `import a.b._` | `Stmt.Import` + `WildcardSelector` |
| **Import rename** | `import a.b.{x => y}` | `import a.b.{x => y}` | `Stmt.Import` + `MultiSelector` |
| **Package/Object** | `object Foo { ... }` | `package Foo { ... }` | `Stmt.Object` |
| **App object** | `object Foo extends App { ... }` | `package @app Foo { ... }` | `Stmt.Object(isApp=T)` |
| **Ext object** | `@ext object Foo { ... }` | `package @ext Foo { ... }` | `Stmt.Object(extNameOpt=Some(""))` |
| **Named ext object** | `@ext("Bar") object Foo { ... }` | `package @ext("Bar") Foo { ... }` | `Stmt.Object(extNameOpt=Some("Bar"))` |
| **Datatype class** | `@datatype class Foo(val x: Z)` | `type @data Foo(x: Z)` | `Stmt.Adt(isDatatype=T, isRoot=F)` |
| **Datatype trait** | `@datatype trait Foo` | `type @sealed @trait Foo` | `Stmt.Adt(isDatatype=T, isRoot=T)` |
| **Record class** | `@record class Foo(var x: Z)` | `type @data @mut Foo(var x: Z)` | `Stmt.Adt(isDatatype=F, isRoot=F)` |
| **Record trait** | `@record trait Foo` | `type @sealed @trait @mut Foo` | `Stmt.Adt(isDatatype=F, isRoot=T)` |
| **Sig trait** | `@sig trait Foo` | `type @trait Foo` | `Stmt.Sig(isImmutable=T)` |
| **Sealed sig** | `@sig sealed trait Foo` | `type @sealed @trait Foo` | `Stmt.Sig(isSealed=T)` |
| **Msig trait** | `@msig trait Foo` | `type @trait @mut Foo` | `Stmt.Sig(isImmutable=F)` |
| **Enum** | `@enum object Color { "Red"\n "Green" }` | `type @enum Color: { Red\n Green }` | `Stmt.Enum` |
| **Range type** | `@range(min = 0, max = 10) class Idx` | `type @range(min = 0, max = 10) Idx` | `Stmt.SubZ(isBitVector=F)` |
| **Bits type** | `@bits(signed = F, width = 8) class U8` | `type @bits(signed = F, width = 8) U8` | `Stmt.SubZ(isBitVector=T)` |
| **Type alias** | `type Foo = Bar[Z]` | `type @alias Foo = Bar[Z]` | `Stmt.TypeAlias` |
| **Type params** | `[T, @mut U]` | `[@imm T, @mut U]` | `TypeParam` |
| **Supertypes** | `extends A, B` | `: A, B` | (parents field) |
| **Val** | `val x: Z = 0` | `val x: Z = 0` | `Stmt.Var(isVal=T)` |
| **Var** | `var x: Z = 0` | `var x: Z = 0` | `Stmt.Var(isVal=F)` |
| **Spec val** | `@spec val x: Z = $` | `val @spec x: Z` | `Stmt.Var(isSpec=T)` / `Stmt.SpecVar` |
| **RsVal** | `@rw val rs = RS(...)` | `val @rw rs = RS(...)` | `Stmt.RsVal` |
| **Val pattern** | `val (a, b): (Z, Z) = f()` | `val (a, b): (Z, Z) = f()` | `Stmt.VarPattern` |
| **Method** | `def foo(x: Z): Z = { ... }` | `def foo(x: Z): Z = { ... }` | `Stmt.Method` |
| **Pure method** | `@pure def foo(x: Z): Z = { ... }` | `def @pure foo(x: Z): Z = { ... }` | `Stmt.Method(purity=Pure)` |
| **Strict pure method** | `@strictpure def foo(x: Z): Z = exp` | `def @strictpure foo(x: Z): Z = { ... }` | `Stmt.Method(purity=StrictPure)` |
| **Memoize method** | `@memoize def foo: Z = { ... }` | `def @memoize foo: Z = { ... }` | `Stmt.Method(purity=Memoize)` |
| **Abs method** | `@abs def foo(x: Z): Z = $` | `def @abs foo(x: Z): Z` | `Stmt.Method(purity=Abs)` |
| **Spec method** | `@spec def foo(x: Z): Z = $` | `def @spec foo(x: Z): Z` | `Stmt.SpecMethod` |
| **Just method** | `@just def foo(x: Z): Z = $` | `def @just foo(x: Z): Z` | `Stmt.JustMethod` |
| **Ext method** | (inside `@ext object`) `def bar(x: Z): Z = $` | (inside `package @ext`) `def bar(x: Z): Z` | `Stmt.ExtMethod` |
| **Method contract** | `Contract(Requires(...), Ensures(...))` | `def foo(): Z @[\n  requires ...\n  ensures ...\n] = { ... }` | `MethodContract.Simple` |
| **Contract cases** | `Contract(Case(...), Case(...))` | `def foo(): Z @[\n  cases[\n    ...\n  ]\n] = { ... }` | `MethodContract.Cases` |
| **Fact** | `@spec def f = Fact(...)` | `def @fact f = (...)` | `Stmt.Fact` |
| **Theorem** | `@spec def t = Theorem(...)` | `def @theorem t = ...` | `Stmt.Theorem(isLemma=F)` |
| **Lemma** | `@spec def l = Lemma(...)` | `def @lemma l = ...` | `Stmt.Theorem(isLemma=T)` |
| **Invariant** | `@spec def i = Invariant(...)` | `def @inv i = (...)` | `Stmt.Inv` |
| **Assignment** | `x = expr` | `x = expr` | `Stmt.Assign` |
| **If** | `if (cond) { ... }` | `if cond { ... }` | `Stmt.If` |
| **If-else** | `if (cond) { ... } else { ... }` | `if cond { ... } else { ... }` | `Stmt.If` |
| **While** | `while (cond) { ... }` | `while cond { ... }` | `Stmt.While` |
| **Loop contract** | `Invariant(Modifies(...), ...)` | `while cond @[\n  modifies ...\n  inv ...\n] { ... }` | `LoopContract` |
| **For** | `for (x <- xs) { ... }` | `for x: xs { ... }` | `Stmt.For` |
| **For range** | `for (i <- 0 until n) { ... }` | `for i: 0 ..< n { ... }` | `Stmt.For` + `EnumGen.Range.Step` |
| **For range incl** | `for (i <- 0 to n) { ... }` | `for i: 0 .. n { ... }` | `Stmt.For` + `EnumGen.Range.Step` |
| **For by** | `for (i <- 0 until n by 2) { ... }` | `for i: 0 ..< n by 2 { ... }` | `EnumGen.Range.Step(byOpt=Some(...))` |
| **Match** | `exp match { case ... }` | `match exp { case ... }` | `Stmt.Match` |
| **Return** | `return expr` | `return expr` | `Stmt.Return` |
| **Halt** | `halt("msg")` | `halt "msg"` | `Stmt.Return` (via halt) |
| **Block** | `{ ... }` | `do { ... }` | `Stmt.Block` |
| **Expr stmt (invoke)** | `foo(x)` | `foo(x)` | `Stmt.Expr` |
| **Expr stmt (other)** | `x + y` | `do x + y` | `Stmt.Expr` |
| **Spec block** | `Spec { ... }` | `do @spec { ... }` | `Stmt.SpecBlock` |
| **Induct** | `(exp: @induct)` | `(@induct exp)` | `Stmt.Induct` |
| **Deduce steps** | `Deduce(1. claim ...)` | `deduce { 1. claim ... }` | `Stmt.DeduceSteps` |
| **Deduce sequent** | `Deduce(⊢ ...)` | `deduce : premises ⊢ conclusion` | `Stmt.DeduceSequent` |
| **Assert** | `assert(exp)` | `assert exp [, exp]` | `Stmt.Expr` (assert builtin) |
| **Assume** | `assume(exp)` | `assume exp [, exp]` | `Stmt.Expr` (assume builtin) |

### Expressions

| Category | Scala-based Syntax | LL(2) Syntax | AST Node |
|---|---|---|---|
| **Identifier** | `x` | `x` | `Exp.Ident` |
| **Select** | `x.y` | `x.y` | `Exp.Select` |
| **Invoke** | `f(a, b)` | `f(a, b)` | `Exp.Invoke` |
| **Invoke named** | `f(x = 1, y = 2)` | `f(x = 1, y = 2)` | `Exp.InvokeNamed` |
| **Apply** | `x(i)` | `x(i)` | `Exp.Invoke(ident.id="apply")` |
| **Binary** | `a + b` | `a + b` | `Exp.Binary` |
| **Unary** | `-x`, `!x` | `-x`, `!x` | `Exp.Unary` |
| **If exp** | `if (c) t else e` | `if (c) t else e` | `Exp.If` |
| **Tuple** | `(a, b)` | `(a, b)` | `Exp.Tuple` |
| **This** | `this` | `this` | `Exp.This` |
| **Super** | `super` | `super` | `Exp.Super` |
| **Eta** | `f _` | `f _` | `Exp.Eta` |
| **Lambda** | `(x: Z) => x + 1` | `def (x: Z). x + 1` | `Exp.Fun` |
| **For-yield** | `for (x <- xs) yield f(x)` | `yield x: xs => f(x)` | `Exp.ForYield` |
| **For-yield range** | `for (i <- 0 until n) yield f(i)` | `yield i: 0 ..< n => f(i)` | `Exp.ForYield` |
| **Quant forall type** | `∀((x: T) => P(x))` | `∀ x: T => P(x)` | `Exp.QuantType` |
| **Quant exists type** | `∃((x: T) => P(x))` | `∃ x: T => P(x)` | `Exp.QuantType` |
| **Quant range** | `∀(lo until hi)(x => P(x))` | `∀ x: lo ..< hi => P(x)` | `Exp.QuantRange` |
| **Quant each** | `∀(xs)(x => P(x))` | `∀ x: xs => P(x)` | `Exp.QuantEach` |
| **String interp** | `s"hello $x"` | `s"hello $x"` | `Exp.StringInterpolate` |
| **Lit bool** | `T` / `F` | `true` / `false` | `Exp.LitB` |
| **Lit int** | `z"42"` or `42` | `42` | `Exp.LitZ` |
| **Lit float32** | `f32"1.0"` or `1.0f` | `1.0f` / `1.0F` | `Exp.LitF32` |
| **Lit float64** | `f64"1.0"` or `1.0d` | `1.0d` / `1.0D` | `Exp.LitF64` |
| **Lit real** | `r"1.0"` | `1.0` | `Exp.LitR` |
| **Lit string** | `"hello"` | `"hello"` | `Exp.LitString` |
| **Lit char** | `'c'` | `'c'` | `Exp.LitC` |
| **Input** | `In(x)` | `In(x)` | `Exp.Input` |
| **Old** | `Old(x)` | `Old(x)` | `Exp.Old` |
| **Result** | `Res` | `Res` | `Exp.Result` |
| **At** | `At(x, n)` | `At(x, n)` | `Exp.At` |
| **Labeled** | (via annotation) | `(@l(n) exp)` | `Exp.Labeled` |
| **RS** | `RS(a, b)` | `RS(a, b)` | `Exp.RS` |
| **Strict pure block** | (via annotation) | `@{ stmts }` | `Exp.StrictPureBlock` |
| **Multi-line string** | `"""line1\nline2"""` | `#line1\n #line2` (`MSTR`) | `Exp.LitString` |
| **Multi-line interp** | `prefix"""...$exp..."""` | `prefix#...$exp...` (`MSTRP`/etc.) | `Exp.StringInterpolate` |

### Types

| Category | Scala-based Syntax | LL(2) Syntax | AST Node |
|---|---|---|---|
| **Named** | `Z`, `ISZ[Z]` | `Z`, `ISZ[Z]` | `Type.Named` |
| **Tuple** | `(Z, B)` | `(Z, B)` | `Type.Tuple` |
| **Function** | `Z => B` | `Z => B` | `Type.Fun` |
| **Pure function** | `Z => B @pure` | `Z => @pure B` | `Type.Fun(isPure=T)` |
| **Multi-param fn** | `(Z, B) => R` | `(Z, B) => R` | `Type.Fun` |
| **By-name** | `=> T` | `=> T` | `Type.Fun(isByName=T)` |

### Patterns

| Category | Scala-based Syntax | LL(2) Syntax | AST Node |
|---|---|---|---|
| **Literal** | `case 42 =>` | `case 42 =>` | `Pattern.Literal` |
| **Variable** | `case x =>` | `case x =>` | `Pattern.VarBinding` |
| **Typed variable** | `case x: Z =>` | `case x: Z =>` | `Pattern.VarBinding(tipeOpt=Some)` |
| **Wildcard** | `case _ =>` | `case _ =>` | `Pattern.Wildcard` |
| **Typed wildcard** | `case _: Z =>` | `case _: Z =>` | `Pattern.Wildcard(typeOpt=Some)` |
| **Seq wildcard** | `case ISZ(_, _*) =>` | `case ISZ(_, *) =>` | `Pattern.SeqWildcard` |
| **Constructor** | `case Foo(x, y) =>` | `case Foo(x, y) =>` | `Pattern.Structure` |
| **Ref** | `case Enum.Value =>` | `case .Enum.Value =>` | `Pattern.Ref` |
| **Name binding** | `case x@Foo(y) =>` | `case x@Foo(y) =>` | `Pattern.Structure(idOpt=Some)` |

### Proof Constructs

| Category | Scala-based Syntax | LL(2) Syntax | AST Node |
|---|---|---|---|
| **Proof block** | `Proof(...)` | `deduce { ... }` | `ProofAst` |
| **Regular step** | `n (claim) by just` | `n. claim  by just` | `ProofAst.Step.Regular` |
| **Assume step** | `n Assume(claim)` | `n. assume claim` | `ProofAst.Step.Assume` |
| **Assert step** | `n Assert(claim, SubProof(...))` | `n. assert claim { ... }` | `ProofAst.Step.Assert` |
| **SubProof** | `n SubProof(...)` | `n. { steps }` | `ProofAst.Step.SubProof` |
| **Let step** | `n Let((x: T) => ...)` | `n. { x: T  steps }` | `ProofAst.Step.Let` |
| **Justification** | `by just` | `by just` | `ProofAst.Step.Justification.*` |
| **Sequent** | `⊢(premises, conclusion)` | `: premises ⊢ conclusion` | `Sequent` |

### LL(2)-Only Constructs

These constructs exist in the LL(2) grammar but have no direct Scala-based equivalent.

| Category | LL(2) Syntax | AST Node | Notes |
|---|---|---|---|
| **Truth table** | `deduce * * * ...` | `TopUnit.TruthTableUnit` | Inline truth table with `-----` separators, `T`/`F` rows, and optional `[Tautology]`/`[Contradictory]`/`[Contingent]` conclusion |
| **JSON literal** | `` `{ "key": value }`` | (none) | Backtick-prefixed JSON object/array/paren for embedded JSON; to be added to standard AST |
| **Optional chain** | `exp?.field` | (none) | `QUESTION` prefix on field/apply access; to be added to standard AST |

### Scala-Only Constructs

These AST nodes are produced by the Scala-based parser but not yet supported in the LL(2) pretty printer (`halt("TODO")`).

| Category | Scala-based Syntax | AST Node | Notes |
|---|---|---|---|
| **Assert agree** | `AssertAgree(channel)(outAgrees)` | `Exp.AssertAgree` | Information flow assertion for HAMR |
| **Assume agree** | `AssumeAgree(channel)(requires)(inAgrees)` | `Exp.AssumeAgree` | Information flow assumption for HAMR |
| **Info flow invariant** | `InfoFlowInvariant(...)` | `Exp.InfoFlowInvariant` | Information flow invariant for HAMR |
| **Loop index** | (internal) | `Exp.LoopIndex` | Generated by type checker for loop variable references |
| **State sequence** | (internal) | `Exp.StateSeq` | Symexe-related state sequence expression |
| **Symbolic exp** | (internal) | `Exp.Sym` | Symbolic expression used internally by Logika |
| **Type condition** | `T[args](fun)` | `Exp.TypeCond` | Type-conditional expression |
| **Data refinement** | `Contract(DataRefinement(...))` | `Stmt.DataRefinement` | Data refinement specification |
| **Spec label** | `Spec("label")` | `Stmt.SpecLabel` | Named spec label |
| **Havoc** | `Havoc(refs)` | `Stmt.Havoc` | Havoc specification for frame conditions |

## Grammars

All LL(2) parser grammars are in `parser/shared/src/main/resources/`:
- **ANTLR v3 grammar**: `SlangLl2.g` — the canonical LL(2) grammar specification
- **Tree-sitter grammar**: `grammar.js` — created from `SlangLl2.g`
- **Grammar-Kit BNF**: `SlangLl2.bnf` — created from `SlangLl2.g`

## Key Files

- **Generated parser**: `parser/shared/src/main/scala/org/sireum/lang/parser/SlangLl2Parser.scala`
- **AST definitions**: `ast/shared/src/main/scala/org/sireum/lang/ast/AST.scala`
- **Pretty printer** (AST to LL(2)): `ast/shared/src/main/scala/org/sireum/lang/ast/SlangLl2PrettyPrinter.scala`
- **AST builder** (LL(2) parse tree to AST): `ast/shared/src/main/scala/org/sireum/lang/ast/SlangLl2AstBuilder.scala`
- **Parse tree utilities**: `ast/shared/src/main/scala/org/sireum/lang/ast/SlangLl2ParseTreeUtil.scala`
- **Scala-based parser**: `parser/shared/src/main/scala/org/sireum/lang/parser/SlangParser.scala`
