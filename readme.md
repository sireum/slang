# The Sireum Programming Language (Slang)

| [![Actions Status](https://github.com/sireum/slang/workflows/CI/badge.svg)](https://github.com/sireum/slang/actions) | [![](https://jitpack.io/v/org.sireum/kekinian.svg)](https://jitpack.io/#org.sireum.kekinian/slang-frontend) |
| :---: | :---: | 
| <sub><sup>amd64: mac, linux, windows</sup></sub> | <sub><sup>maven package repository</sup></sub> |

This repository holds the [Slang](https://github.com/sireum/kekinian) 
front-end components (i.e., parser, symbol resolver, and type checker).

## Testing

* **macOS/Linux**

  ```bash
  bin/build.cmd test
  ```
  
* **Windows**

  ```cmd
  bin\build.cmd test
  ```

Passing `test` builds the Slang runtime library and the front-end,
then uses the result to parse, resolve, and type check the codebase itself.
After type checking, it serializes the codebase AST along with symbol and type information
and deserializes them back and check for equivalence before/after de/serialization.

The above runs the Slang front-end test suite under a JVM; 
to run it under Node.js after the above
(*caution:* it is a lot slower):

* **macOS/Linux**

  ```bash
  export NODEJS_MAX_HEAP=4096 # MB
  bin/build.cmd test-js
  ```
  
* **Windows**

  ```cmd
  set NODEJS_MAX_HEAP=4096
  bin\build.cmd test-js
  ```
