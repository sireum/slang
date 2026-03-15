# Slang Language Constructs

* Slang Scala-based and LL(2) surface syntaxes are described in 'slang-syntax.md', only refer to it when you need a mapping between the two

## Block and Method Rules (both LL(2) and Scala-based)

### Value markers at leaf positions
- Non-Unit method body leaves must use `return exp` or `halt` — bare expressions are not implicit returns
- Value-producing block leaves (rhs of val/var/assignment, `@strictpure` body, if/match branches in rhs) must use `\ exp` or `halt`
- `halt` is valid in any leaf position (method body or value block)

### Pure context restrictions
- `@strictpure` and `@abs` method bodies must not contain `var`, `while`, or `for`
- Strict pure block expressions (`@{ ... }`) must not contain `var`, `while`, or `for`

### Explicit type annotations
- `val`/`var` declarations with block (`{ ... }`), `if`, or `match` as rhs must have explicit type annotations
- Example: `val x: Z = { ... }` (OK), `val x = { ... }` (error)