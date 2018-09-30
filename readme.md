# The Sireum Programming Language (Slang)

[![Build Status](https://travis-ci.org/sireum/slang.svg?branch=master)](https://travis-ci.org/sireum/slang)

This repository holds the [Slang](https://github.com/sireum/kekinian) 
front-end components (i.e., parser, symbol resolver, and type checker).

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
NODEJS_MAX_HEAP=4096 <path-to-repo>/mill-standalone slang.frontend.js.tests.test
```
