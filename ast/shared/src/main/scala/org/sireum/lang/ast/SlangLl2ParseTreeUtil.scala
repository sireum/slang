// #Sireum
/*
 Copyright (c) 2017-2026,Robby, Kansas State University
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

package org.sireum.lang.ast


import org.sireum._
import org.sireum.S32._
import org.sireum.message.Position
import org.sireum.parser._
object SlangLl2ParseTreeUtil {

  @datatype class BinaryPrecedenceOps extends ParseTree.BinaryPrecedenceOps[B, ParseTree, ParseTree] {

    @strictpure override def messageKind: String = "SlangLl2ParseTreeUtil.BinaryPrecedenceOps"

    @strictpure override def isBinary(t: ParseTree): B = t.ruleName == "Binary"

    @strictpure override def isRightAssoc(t: ParseTree): B = t match {
      case t: ParseTree.Leaf => t.text match {
        case "->" => T
        case "-->" => T
        case "__>:" => T
        case "___>:" => T
        case _ => F
      }
      case t: ParseTree.Node if isBinary(t) => isRightAssoc(t.children.atS32(s32"1"))
      case _ => F
    }

    @strictpure override def isHigherPrecedence(n1: Z, n2: Z): B = n1 > n2

    @strictpure override def lowestPrecedence: Z = 0

    @strictpure override def shouldParenthesizeOperands(t: ParseTree): B = F

    @strictpure override def precedence(t: ParseTree): Option[Z] = t match {
      case t: ParseTree.Leaf =>
        if (t.text == "->" || t.text == "-->" || t.text == "__>:" || t.text == "___>:") {
          Some(1)
        } else {
          ops.StringOps(t.text).first match {
            case '|' => Some(2)
            case '^' => Some(3)
            case '&' => Some(4)
            case '=' => Some(5)
            case '!' => Some(5)
            case '<' => Some(6)
            case '>' => Some(6)
            case ':' => Some(7)
            case '+' => Some(8)
            case '-' => Some(8)
            case '*' => Some(9)
            case '/' => Some(9)
            case '%' => Some(9)
            case _ => Some(lowestPrecedence)
          }
        }
      case t: ParseTree.Node if isBinary(t) => precedence(t.children.atS32(s32"1"))
      case _ => None()
    }

    @strictpure override def posOpt(t: ParseTree): Option[Position] = t.posOpt

    @strictpure override def parenthesize(builder: B, t: ParseTree): ParseTree =
      ParseTree.Node(IS[S32, ParseTree](t), "paren", 0)

    @strictpure override def binary(builder: B, left: ParseTree, op: ParseTree, right: ParseTree): ParseTree =
      ParseTree.Node(IS[S32, ParseTree](left, op, right), "Binary", -1)

    @strictpure override def transform(builder: B, tree: ParseTree): ParseTree = tree match {
      case tree: ParseTree.Leaf if tree.text == "->" => tree(text = "__>:")
      case tree: ParseTree.Leaf if tree.text == "-->" => tree(text = "___>:")
      case _ => tree
    }

    @strictpure override def toString(t: ParseTree): String = t.toST.render
  }

  val binaryPrecedenceOps: BinaryPrecedenceOps = BinaryPrecedenceOps()

  /** Transforms an `exp3` parse tree from its flat LL(k) form
    * `exp3(exp2, infixSuffix(infixOp(op), exp2), ...)` into a precedence-resolved
    * binary tree of `Binary(left, op, right)` nodes using Scala's first-character
    * operator precedence rules. The `->` and `-->` operators are right-associative;
    * all others are left-associative. */
  def exp3ToBinary(tree: ParseTree, reporter: message.Reporter): ParseTree = {
    val node = tree.asInstanceOf[ParseTree.Node]
    var newChildren = ISZ[ParseTree]()
    for (child <- node.children) {
      child match {
        case n: ParseTree.Node if n.ruleName == "infixSuffix" =>
          for (c <- n.children) {
            c match {
              case m: ParseTree.Node if m.ruleName == "infixOp" =>
                newChildren = newChildren :+ m.children.atS32(s32"0")
              case _ =>
                newChildren = newChildren :+ c
            }
          }
        case _ =>
          newChildren = newChildren :+ child
      }
    }
    return ParseTree.rewriteBinary(T, binaryPrecedenceOps, newChildren, reporter)
  }

}