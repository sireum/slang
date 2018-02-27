# The Sireum Programming Language (Slang)

[![Build Status](https://travis-ci.org/sireum/slang.svg?branch=master)](https://travis-ci.org/sireum/slang)

Slang is a OO/FP programming language designed for formal analyses.
It is (currently) a subset of Scala 2.x with different memory models 
enabled via Scala's 
[macro](https://github.com/sireum/runtime/blob/master/macros/shared/src/main/scala/org/sireum/%24internal/Macro.scala) 
and 
[compiler plugin](https://github.com/sireum/scalac-plugin) 
facilities.
While tied to Scala 2.x, the programming language can be realized
on top of similar family of languages with meta-programming 
and/or compiler plugin facilities.
  
This repository stores the Slang front-end  
(i.e., parser, symbol resolver, and type checker).
With the exception of a small part of its 
[runtime library](https://github.com/sireum/runtime) and its
parser that uses [scalameta](http://scalameta.org), 
most of the runtime library and the Slang codebase itself 
(and analyses on top of it) are written using Slang.

Slang currently runs on the JVM (Java 8+) and Javascript
(via [Scala.js](http://scala-js.org)).

## Testing

(Currently on JVM only due to [lihaoyi/mill#164](https://github.com/lihaoyi/mill/issues/164))

```bash
./test.sh
```

The test run builds the Slang runtime library and the front-end,
then uses the result to parse, resolve, and type check the
codebase itself. After type checking, 
it serializes the codebase AST along with symbol and type information
and deserializes them back and check for equivalence before/after
de/serialization.