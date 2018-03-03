# The Sireum Programming Language (Slang)

[![Build Status](https://travis-ci.org/sireum/slang.svg?branch=master)](https://travis-ci.org/sireum/slang)

Slang is an OO/FP programming language with contract and proof languages
designed for formal analyses; it serves as the basis for the next generation
[Logika](http://logika.sireum.org) verifier and proof checker, as well as for other
formal method-based analysis techniques.

Slang is (currently) a subset of Scala 2.x with different memory models 
enabled via Scala's 
[macro](https://github.com/sireum/runtime/blob/master/macros/shared/src/main/scala/org/sireum/%24internal/Macro.scala) 
and  [compiler plugin](https://github.com/sireum/scalac-plugin) 
facilities, with support for [IntelliJ](https://github.com/sireum/intellij-injector).
While tied to Scala 2.x, the programming language can be realized
on top of a similar family of languages with meta-programming 
and/or compiler plugin facilities.
  
This repository holds the Slang front-end components 
(i.e., parser, symbol resolver, and type checker).
With the exception of a small part of its 
[runtime library](https://github.com/sireum/runtime) and its
parser that uses [scalameta](http://scalameta.org), 
the runtime library and the Slang codebase itself 
(and analyses on top of it) are written using Slang.

Slang currently runs on the JVM (Java 8+) and Javascript
(in the browser and Node.js via [Scala.js](http://scala-js.org)).

## Testing

```bash
./test.sh
```

It builds the Slang runtime library and the front-end,
then uses the result to parse, resolve, and type check the codebase itself.
After type checking, it serializes the codebase AST along with symbol and type information
and deserializes them back and check for equivalence before/after de/serialization.

The above runs the Slang front-end test suite under a JVM; to run it under Node.js after the above
(*caution:* it is a lot slower):

```bash
./mill slang.frontend.js.tests.test
```
