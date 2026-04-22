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

## Method and Block Rules (both syntaxes)

### Purity annotations
- `@pure` — pure method (no side effects, deterministic)
- `@strictpure` — strict pure method (single expression body, no `var`/`while`/`for`)
- `@abs` — functional abstraction (abstracting over behavior; NOT "abstract")
- `@pure` can be used for both method definitions (with body) and method declarations (without body, in traits)

### Procedural syntax (Unit-returning methods)
- Methods that return `Unit` use **procedural syntax**: a block body with no return type and no `=`
- `def foo() { ... }` — Unit-returning method with explicit empty params
- `def foo { ... }` — Unit-returning method; empty `()` params auto-injected
- `Unit` is not directly usable as a type in user code; use `()` in type position instead (e.g., `Z => ()`)
- The AST always has a return type node (`Unit`) and `hasParams = T`; the compiler injects both

### Parameter-less methods
- Methods without parameter list (no `()`) and **non-procedural** must be `@pure`, `@strictpure`, or `@abs`
- Example: `def @pure get: T = { return value }` — OK
- Example: `def get: T = { return value }` — error
- Exception: procedural syntax (`def foo { ... }`) is allowed without `()` — params are auto-injected

### Value markers at leaf positions
- Non-Unit method body leaves must use `return exp` or `halt` — bare expressions are not implicit returns
- Value-producing block leaves (rhs, `@strictpure` body, if/match branches in rhs) must use `\ exp` (LL(2)) / block result (Scala-based) or `halt`

### Pure context restrictions
- `@strictpure` and `@abs` method bodies must not contain `var`, `while`, or `for`
- Strict pure block expressions must not contain `var`, `while`, or `for`

### Explicit type annotations
- `val`/`var` declarations with block, `if`, or `match` as rhs must have explicit type annotations

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
| **Datatype class** | `@datatype class Foo(val x: Z)` | `type @datatype Foo(x: Z)` | `Stmt.Adt(isDatatype=T, isRoot=F)` |
| **Datatype trait** | `@datatype trait Foo` | `type @datatype @trait Foo` | `Stmt.Adt(isDatatype=T, isRoot=T)` |
| **Record class** | `@record class Foo(var x: Z)` | `type @record Foo(var x: Z)` | `Stmt.Adt(isDatatype=F, isRoot=F)` |
| **Record trait** | `@record trait Foo` | `type @record @trait Foo` | `Stmt.Adt(isDatatype=F, isRoot=T)` |
| **Sig trait** | `@sig trait Foo` | `type @sig Foo` | `Stmt.Sig(isImmutable=T)` |
| **Sealed sig** | `@sig sealed trait Foo` | `type @sig @sealed Foo` | `Stmt.Sig(isSealed=T)` |
| **Msig trait** | `@msig trait Foo` | `type @msig Foo` | `Stmt.Sig(isImmutable=F)` |
| **Ext sig** | `@ext trait Foo` | `type @sig @ext Foo` | `Stmt.Sig(isImmutable=T, isExt=T)` |
| **Ext msig** | *(new)* | `type @msig @ext Foo` | `Stmt.Sig(isImmutable=F, isExt=T)` |
| **Enum** | `@enum object Color { "Red"\n "Green" }` | `type @enum Color: { Red, Green }` | `Stmt.Enum` |
| **Range type** | `@range(min = 0, max = 10) class Idx` | `type @range[min = 0, max = 10] Idx` | `Stmt.SubZ(isBitVector=F)` (LL(2) annotation args use `[...]`, not `(...)`) |
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
| **Procedural method** | `def foo(x: Z): Unit = { ... }` | `def foo(x: Z) { ... }` | `Stmt.Method` (Unit auto-injected) |
| **Parameterless proc** | `def foo(): Unit = { ... }` | `def foo { ... }` | `Stmt.Method` (Unit + `()` auto-injected) |
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
| **If exp (ternary)** | `if (c) t else e` | `? c: t else e` | `Exp.If` |
| **Tuple** | `(a, b)` | `(a, b)` | `Exp.Tuple` |
| **This** | `this` | `this` | `Exp.This` |
| **Super** | `super` | `super` | `Exp.Super` |
| **Eta** | `f _` | `f _` | `Exp.Eta` |
| **Lambda** | `(x: Z) => x + 1` | `\(x: Z) x + 1` | `Exp.Fun` |
| **For-yield** | `for (x <- xs) yield f(x)` | `yield x: xs => f(x)` | `Exp.ForYield` |
| **For-yield range** | `for (i <- 0 until n) yield f(i)` | `yield i: 0 ..< n => f(i)` | `Exp.ForYield` |
| **Quant forall type** | `∀((x: T) => P(x))` | `∀ x: T => P(x)` | `Exp.QuantType` |
| **Quant exists type** | `∃((x: T) => P(x))` | `∃ x: T => P(x)` | `Exp.QuantType` |
| **Quant range** | `∀(lo until hi)(x => P(x))` | `∀ x: lo ..< hi => P(x)` | `Exp.QuantRange` |
| **Quant each** | `∀(xs)(x => P(x))` | `∀ x: xs => P(x)` | `Exp.QuantEach` |
| **String interp** | `s"hello $x"` / `s"${x + 1}"` | `s"hello $x$"` / `s"$x + 1$"` (single-line uses `$exp$` — no braces; interior is any `exp`, parenthesize only for precedence) | `Exp.StringInterpolate` |
| **Lit bool** | `T` / `F` | `T`/`true` / `F`/`false` | `Exp.LitB` |
| **Lit int** | `z"42"` or `42` | `42` | `Exp.LitZ` |
| **Lit SubZ** | `s32"100"`, `u8"42"` | `100s32`, `42u8` | `Exp.StringInterpolate` |
| **Lit float32** | `f32"1.0"` or `1.0f` | `1.0f` / `1.0F` | `Exp.LitF32` |
| **Lit float64** | `f64"1.0"` or `1.0d` | `1.0d` / `1.0D` / unsuffixed `1.0` | `Exp.LitF64` |
| **Lit float16** | `f16"1.0"` | `1.0h` / `1.0H` | `Exp.StringInterpolate` |
| **Lit real** | `r"1.0"` | `4r` (INT with `r` suffix) | `Exp.LitR` |
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
| **Multi-line interp** | `prefix"""...$exp..."""` | `prefix#...${exp}$...` (`MSTRP`/etc. — each line `#`-prefixed; interp uses `${exp}$` with braces, NOT the single-line `$exp$` form) | `Exp.StringInterpolate` |

### Types

| Category | Scala-based Syntax | LL(2) Syntax | AST Node |
|---|---|---|---|
| **Named** | `Z`, `ISZ[Z]` | `Z`, `ISZ[Z]`, `Color.Type` | `Type.Named` |
| **Unit** | `Unit` | `()` | `Type.Named("Unit")` |
| **Tuple** | `(Z, B)` | `(Z, B)` | `Type.Tuple` |
| **Function** | `Z => B` | `Z => B` | `Type.Fun` |
| **Procedure fn** | `Z => Unit` | `Z => ()` | `Type.Fun` (return = Unit) |
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
- **Grammar-Kit BNF**: `SlangLl2.bnf` — created from `SlangLl2.g`

## Key Files

- **Generated parser**: `parser/shared/src/main/scala/org/sireum/lang/parser/SlangLl2Parser.scala`
- **AST definitions**: `ast/shared/src/main/scala/org/sireum/lang/ast/AST.scala`
- **Pretty printer** (AST to LL(2)): `ast/shared/src/main/scala/org/sireum/lang/ast/SlangLl2PrettyPrinter.scala`
- **AST builder** (LL(2) parse tree to AST): `ast/shared/src/main/scala/org/sireum/lang/ast/SlangLl2AstBuilder.scala`
- **Parse tree utilities**: `ast/shared/src/main/scala/org/sireum/lang/ast/SlangLl2ParseTreeUtil.scala`
- **Scala-based parser**: `parser/shared/src/main/scala/org/sireum/lang/parser/SlangParser.scala`

## Parallel and Partitioning Constructs

Annotations modify `for` (statement) and `yield` (expression) semantics. Grammar:
```
forStmt: FOR annot? forRange commaForRange* block ;
forExp:  YIELD annot? forRange commaForRange* ARROW annot? rhs ;
```

### `for @fork` — Structured Concurrency

Fork-join: all generators execute in parallel, implicit join before body executes.

```
for @fork a: computeA(),
          b: computeB(),
          c: computeC() {
  // a, b, c computed in parallel, all available here
}
```

- Replaces the previous `do @par { @fork val }` design
- Each generator is an independent task — no data dependencies between them
- `@fork` return types may be `@record` or `@datatype` — Slang's return-copy semantics
  guarantee each thread receives an independent deep copy with no shared mutable state
- AST: `Stmt.For` gains optional annotation field; `@fork` semantics on generators
- Cancellation: exiting a `for @fork` scope cancels unfinished tasks.  No additional
  language features required — cancellation is structural (scope-based).  Two runtime
  mechanisms provide cooperative cancellation:
  1. **Loop back-edges**: compiler inserts cancellation checks at `for`/`while` back-edges
     inside `@fork` scopes (covers Slang code)
  2. **`@ext` calls**: runtime invokes a registered cancellation hook when the scope exits
     (covers long-running native/external calls, e.g., Z3 `interrupt()`)

### `for @par` — Data-Parallel Iteration

Each iteration of the loop body runs in parallel over elements of a collection.

```
for @par x: xs {
  // body runs in parallel for each x
}
```

### `yield @par` — Data-Parallel Map

Expression form — produces a collection via parallel comprehension.

```
val results: ISZ[R] = yield @par x: xs => f(x)
```

Inlines the body at each call site — no closure created. Each worker receives a
per-site static function + captured vals by value.

### `for @part` — Tensor Partitioning

Parallel tiling of an `@mtensor` into non-overlapping sub-tensors (inspired by CUDA grid
decomposition); non-overlap guaranteed by tile geometry — no runtime check needed.
Source tensor is shadowed (inaccessible) inside the block.

```
for @part tile: mmat.tile(4, 8) {
  // each tile processed in parallel — no interference, tiles don't overlap
  // mmat is inaccessible here
  tile = tile + bias
}
// mmat accessible again, reflects mutations made through tiles
```

- `.tile(tileDims...)` decomposes tensor into equal-sized non-overlapping sub-tensors
- Non-overlap is structural (from tile geometry) — no runtime overlap check needed
- `for @part` is **parallel by default** (tiles are independent, like CUDA thread blocks)
- For sequential dependence between tiles, use a regular `for` loop with index arithmetic
- Immutable `@tensor`: `.tile()` freely returns read-only sub-tensors (no `@part` needed)
