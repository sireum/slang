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
import org.sireum.lang.{ast => AST}
import org.sireum.message.Position
import org.sireum.parser.ParseTree

object SlangLl2AstBuilder {

  def build(fileUriOpt: Option[String], tree: ParseTree, reporter: message.Reporter): Option[AST.TopUnit.Program] = {
    val node = tree.asInstanceOf[ParseTree.Node]
    node.ruleName.native match {
      case "file" => return Some(buildFile(fileUriOpt, node, reporter))
      case _ =>
        reporter.error(node.posOpt, "SlangLl2AstBuilder", st"Expected 'file' rule but got '${node.ruleName}'".render)
        return None()
    }
  }

  // ─── helpers ────────────────────────────────────────────────────────

  @strictpure def attr(tree: ParseTree): AST.Attr = AST.Attr(tree.posOpt)

  @strictpure def typedAttr(tree: ParseTree): AST.TypedAttr = AST.TypedAttr(tree.posOpt, None())

  @strictpure def resolvedAttr(tree: ParseTree): AST.ResolvedAttr = AST.ResolvedAttr(tree.posOpt, None(), None())

  @strictpure def emptyAttr: AST.Attr = AST.Attr(None())

  @strictpure def emptyTypedAttr: AST.TypedAttr = AST.TypedAttr(None(), None())

  @strictpure def emptyResolvedAttr: AST.ResolvedAttr = AST.ResolvedAttr(None(), None(), None())

  @strictpure def mkId(value: String, tree: ParseTree): AST.Id = AST.Id(value, attr(tree))

  @strictpure def mkName(ids: ISZ[AST.Id], tree: ParseTree): AST.Name = AST.Name(ids, attr(tree))

  def children(tree: ParseTree): IS[S32, ParseTree] = {
    return tree.asInstanceOf[ParseTree.Node].children
  }

  def text(tree: ParseTree): String = {
    return tree.asInstanceOf[ParseTree.Leaf].text
  }

  def isLeaf(tree: ParseTree): B = {
    tree match {
      case _: ParseTree.Leaf => return T
      case _ => return F
    }
  }

  def isNode(tree: ParseTree): B = {
    tree match {
      case _: ParseTree.Node => return T
      case _ => return F
    }
  }

  def findChild(node: ParseTree.Node, ruleName: String): Option[ParseTree.Node] = {
    for (c <- node.children) {
      c match {
        case c: ParseTree.Node if c.ruleName == ruleName => return Some(c)
        case _ =>
      }
    }
    return None()
  }

  def findChildren(node: ParseTree.Node, ruleName: String): ISZ[ParseTree.Node] = {
    var r = ISZ[ParseTree.Node]()
    for (c <- node.children) {
      c match {
        case c: ParseTree.Node if c.ruleName == ruleName => r = r :+ c
        case _ =>
      }
    }
    return r
  }

  def findLeaf(node: ParseTree.Node, tokenText: String): Option[ParseTree.Leaf] = {
    for (c <- node.children) {
      c match {
        case c: ParseTree.Leaf if c.text == tokenText => return Some(c)
        case _ =>
      }
    }
    return None()
  }

  def findLeafByRule(node: ParseTree.Node, ruleName: String): Option[ParseTree.Leaf] = {
    for (c <- node.children) {
      c match {
        case c: ParseTree.Leaf if c.ruleName == ruleName => return Some(c)
        case _ =>
      }
    }
    return None()
  }

  def hasLeaf(node: ParseTree.Node, tokenText: String): B = {
    for (c <- node.children) {
      c match {
        case c: ParseTree.Leaf if c.text == tokenText => return T
        case _ =>
      }
    }
    return F
  }

  def hasChild(node: ParseTree.Node, ruleName: String): B = {
    for (c <- node.children) {
      c match {
        case c: ParseTree.Node if c.ruleName == ruleName => return T
        case _ =>
      }
    }
    return F
  }

  def leafText(node: ParseTree.Node, ruleName: String): String = {
    for (c <- node.children) {
      c match {
        case c: ParseTree.Leaf if c.ruleName == ruleName => return c.text
        case _ =>
      }
    }
    halt(st"Expected leaf with ruleName '$ruleName' in ${node.ruleName}".render)
  }

  def idText(node: ParseTree.Node): String = {
    return leafText(node, "ID")
  }

  def firstLeaf(node: ParseTree.Node): ParseTree.Leaf = {
    for (c <- node.children) {
      c match {
        case c: ParseTree.Leaf => return c
        case _ =>
      }
    }
    halt(st"Expected leaf in ${node.ruleName}".render)
  }

  def firstNode(node: ParseTree.Node): ParseTree.Node = {
    for (c <- node.children) {
      c match {
        case c: ParseTree.Node => return c
        case _ =>
      }
    }
    halt(st"Expected node in ${node.ruleName}".render)
  }

  // ─── Annotations / mods ────────────────────────────────────────────

  @datatype class ModInfo(val names: ISZ[String],
                          val args: ISZ[(String, IS[S32, ParseTree])])

  def buildMods(mods: ISZ[ParseTree.Node]): ISZ[ModInfo] = {
    var r = ISZ[ModInfo]()
    for (m <- mods) {
      r = r :+ buildMod(m)
    }
    return r
  }

  def buildMod(node: ParseTree.Node): ModInfo = {
    // mod: AT ID ( LSQUARE args RSQUARE )?
    val name = idText(node)
    val argsNode = findChild(node, "args")
    argsNode match {
      case Some(an) =>
        var argPairs = ISZ[(String, IS[S32, ParseTree])]()
        // For now, just store raw children
        argPairs = argPairs :+ ((name, an.children))
        return ModInfo(names = ISZ(name), args = argPairs)
      case _ =>
        return ModInfo(names = ISZ(name), args = ISZ())
    }
  }

  def hasMod(mods: ISZ[ModInfo], name: String): B = {
    for (m <- mods) {
      if (ops.ISZOps(m.names).contains(name)) {
        return T
      }
    }
    return F
  }

  def findMod(mods: ISZ[ModInfo], name: String): Option[ModInfo] = {
    for (m <- mods) {
      if (ops.ISZOps(m.names).contains(name)) {
        return Some(m)
      }
    }
    return None()
  }

  // ─── Annotations (LL2 annotation block) ────────────────────────────

  def buildAnnot(nodeOpt: Option[ParseTree.Node], reporter: message.Reporter): ISZ[(String, IS[S32, ParseTree])] = {
    nodeOpt match {
      case Some(node) =>
        // annot: AT LSQUARE annotArg* RSQUARE
        val annotArgs = findChildren(node, "annotArg")
        var r = ISZ[(String, IS[S32, ParseTree])]()
        for (aa <- annotArgs) {
          val key = firstLeaf(aa).text
          var values = IS[S32, ParseTree]()
          for (c <- aa.children) {
            c match {
              case c: ParseTree.Leaf if c.ruleName == "ID" || c.ruleName == "STRING" =>
              case _ => values = values :+ c
            }
          }
          r = r :+ ((key, values))
        }
        return r
      case _ => return ISZ()
    }
  }

  def buildLoopContract(annotOpt: Option[ParseTree.Node], reporter: message.Reporter): AST.LoopContract = {
    annotOpt match {
      case Some(annotNode) =>
        val annotArgs = findChildren(annotNode, "annotArg")
        var invs = ISZ[AST.Exp]()
        var modifies = ISZ[AST.Exp.Ref]()
        for (aa <- annotArgs) {
          val key = firstLeaf(aa).text
          key.native match {
            case "modifies" =>
              // collect comma-separated exps
              for (c <- aa.children) {
                c match {
                  case c: ParseTree.Node =>
                    val e = buildExp(c, reporter)
                    e match {
                      case e: AST.Exp.Ident => modifies = modifies :+ e
                      case e: AST.Exp.Select => modifies = modifies :+ e
                      case _ =>
                    }
                  case _ =>
                }
              }
            case "inv" =>
              for (c <- aa.children) {
                c match {
                  case c: ParseTree.Node =>
                    c.ruleName.native match {
                      case "annotArgNested" =>
                      case _ =>
                        val e = buildExp(c, reporter)
                        invs = invs :+ e
                    }
                  case c: ParseTree.Leaf =>
                    c.ruleName.native match {
                      case "ID" =>
                      case "STRING" =>
                      case "COMMA" =>
                      case _ =>
                    }
                }
              }
            case _ =>
          }
        }
        return AST.LoopContract(invariants = invs, modifies = modifies, maxItOpt = None())
      case _ => return AST.LoopContract.empty
    }
  }

  def buildMethodContract(annotOpt: Option[ParseTree.Node], reporter: message.Reporter): AST.MethodContract = {
    annotOpt match {
      case Some(annotNode) =>
        val annotArgs = findChildren(annotNode, "annotArg")
        var reads = ISZ[AST.Exp.Ref]()
        var requires = ISZ[AST.Exp]()
        var modifiesRefs = ISZ[AST.Exp.Ref]()
        var ensures = ISZ[AST.Exp]()
        var cases = ISZ[AST.MethodContract.Case]()
        var isCases = F
        for (aa <- annotArgs) {
          val key = firstLeaf(aa).text
          key.native match {
            case "reads" =>
              for (c <- aa.children) {
                c match {
                  case c: ParseTree.Node =>
                    val e = buildExp(c, reporter)
                    e match {
                      case e: AST.Exp.Ident => reads = reads :+ e
                      case e: AST.Exp.Select => reads = reads :+ e
                      case _ =>
                    }
                  case _ =>
                }
              }
            case "requires" =>
              for (c <- aa.children) {
                c match {
                  case c: ParseTree.Node =>
                    c.ruleName.native match {
                      case "annotArgNested" =>
                      case _ =>
                        val e = buildExp(c, reporter)
                        requires = requires :+ e
                    }
                  case c: ParseTree.Leaf =>
                    c.ruleName.native match {
                      case "ID" =>
                      case "STRING" =>
                      case "COMMA" =>
                      case _ =>
                    }
                }
              }
            case "modifies" =>
              for (c <- aa.children) {
                c match {
                  case c: ParseTree.Node =>
                    val e = buildExp(c, reporter)
                    e match {
                      case e: AST.Exp.Ident => modifiesRefs = modifiesRefs :+ e
                      case e: AST.Exp.Select => modifiesRefs = modifiesRefs :+ e
                      case _ =>
                    }
                  case _ =>
                }
              }
            case "ensures" =>
              for (c <- aa.children) {
                c match {
                  case c: ParseTree.Node =>
                    c.ruleName.native match {
                      case "annotArgNested" =>
                      case _ =>
                        val e = buildExp(c, reporter)
                        ensures = ensures :+ e
                    }
                  case c: ParseTree.Leaf =>
                    c.ruleName.native match {
                      case "ID" =>
                      case "STRING" =>
                      case "COMMA" =>
                      case _ =>
                    }
                }
              }
            case "cases" =>
              isCases = T
              val nested = findChildren(aa, "annotArgNested")
              for (n <- nested) {
                val innerArgs = findChildren(n, "annotArg")
                cases = cases :+ buildContractCase(innerArgs, reporter)
              }
            case _ =>
              // Could be a contract case label (STRING)
              if (ops.StringOps(key).startsWith("\"")) {
                // case label
                val nested = findChild(aa, "annotArgNested")
                nested match {
                  case Some(nn) =>
                    isCases = T
                    val innerArgs = findChildren(nn, "annotArg")
                    val label = parseStringLit(key)
                    cases = cases :+ buildContractCaseWithLabel(label, innerArgs, aa, reporter)
                  case _ =>
                }
              }
          }
        }
        if (isCases) {
          return AST.MethodContract.Cases(
            readsClause = AST.MethodContract.Accesses(reads, attr(annotNode)),
            modifiesClause = AST.MethodContract.Accesses(modifiesRefs, attr(annotNode)),
            cases = cases,
            attr = attr(annotNode))
        } else {
          return AST.MethodContract.Simple(
            readsClause = AST.MethodContract.Accesses(reads, attr(annotNode)),
            requiresClause = AST.MethodContract.Claims(requires, attr(annotNode)),
            modifiesClause = AST.MethodContract.Accesses(modifiesRefs, attr(annotNode)),
            ensuresClause = AST.MethodContract.Claims(ensures, attr(annotNode)),
            infoFlowsClause = AST.MethodContract.InfoFlows.empty,
            attr = attr(annotNode))
        }
      case _ => return AST.MethodContract.Simple.empty
    }
  }

  def buildContractCase(annotArgs: ISZ[ParseTree.Node], reporter: message.Reporter): AST.MethodContract.Case = {
    return buildContractCaseWithLabel("", annotArgs, annotArgs(0), reporter)
  }

  def buildContractCaseWithLabel(label: String, annotArgs: ISZ[ParseTree.Node], posTree: ParseTree, reporter: message.Reporter): AST.MethodContract.Case = {
    var requires = ISZ[AST.Exp]()
    var ensures = ISZ[AST.Exp]()
    for (aa <- annotArgs) {
      val key = firstLeaf(aa).text
      key.native match {
        case "requires" =>
          for (c <- aa.children) {
            c match {
              case c: ParseTree.Node =>
                c.ruleName.native match {
                  case "annotArgNested" =>
                  case _ =>
                    val e = buildExp(c, reporter)
                    requires = requires :+ e
                }
              case _ =>
            }
          }
        case "ensures" =>
          for (c <- aa.children) {
            c match {
              case c: ParseTree.Node =>
                c.ruleName.native match {
                  case "annotArgNested" =>
                  case _ =>
                    val e = buildExp(c, reporter)
                    ensures = ensures :+ e
                }
              case _ =>
            }
          }
        case _ =>
      }
    }
    return AST.MethodContract.Case(
      label = AST.Exp.LitString(label, attr(posTree)),
      requiresClause = AST.MethodContract.Claims(requires, attr(posTree)),
      ensuresClause = AST.MethodContract.Claims(ensures, attr(posTree)))
  }

  // ─── file / program ────────────────────────────────────────────────

  def buildFile(fileUriOpt: Option[String], node: ParseTree.Node, reporter: message.Reporter): AST.TopUnit.Program = {
    // file: program EOF
    val programNode = findChild(node, "program").get
    return buildProgram(fileUriOpt, programNode, reporter)
  }

  def buildProgram(fileUriOpt: Option[String], node: ParseTree.Node, reporter: message.Reporter): AST.TopUnit.Program = {
    // program: annot? imprt* mainMember* pkg*
    val annotOpt = findChild(node, "annot")
    val imports = findChildren(node, "imprt")
    val mainMembers = findChildren(node, "mainMember")
    val pkgs = findChildren(node, "pkg")

    var stmts = ISZ[AST.Stmt]()

    // imports
    for (imp <- imports) {
      stmts = stmts :+ buildImport(imp, reporter)
    }

    // main members (stmt | typeDefn)
    for (mm <- mainMembers) {
      stmts = stmts ++ buildMainMember(mm, reporter)
    }

    // packages
    for (pkg <- pkgs) {
      stmts = stmts :+ buildPkg(pkg, reporter)
    }

    // First check if there is a package name from annotation
    val packageName: AST.Name = annotOpt match {
      case Some(_) => AST.Name(ISZ(), emptyAttr)
      case _ => AST.Name(ISZ(), emptyAttr)
    }

    return AST.TopUnit.Program(
      fileUriOpt = fileUriOpt,
      packageName = packageName,
      body = AST.Body(stmts = stmts, undecls = ISZ()))
  }

  // ─── import ────────────────────────────────────────────────────────

  def buildImport(node: ParseTree.Node, reporter: message.Reporter): AST.Stmt.Import = {
    // imprt: IMPORT ID importIdSuffix?
    val firstId = findLeafByRule(node, "ID").get
    val suffixOpt = findChild(node, "importIdSuffix")
    val importer = buildImporter(ISZ(mkId(firstId.text, firstId)), suffixOpt, node, reporter)
    return AST.Stmt.Import(importers = ISZ(importer), attr = attr(node))
  }

  def buildImporter(ids: ISZ[AST.Id], suffixOpt: Option[ParseTree.Node], posTree: ParseTree, reporter: message.Reporter): AST.Stmt.Import.Importer = {
    suffixOpt match {
      case Some(suffix) =>
        // importIdSuffix: DOT ( importWildcardSuffix | importQualSuffix | importRenamesSuffix )
        val wildcardOpt = findChild(suffix, "importWildcardSuffix")
        val qualOpt = findChild(suffix, "importQualSuffix")
        val renamesOpt = findChild(suffix, "importRenamesSuffix")
        wildcardOpt match {
          case Some(_) =>
            return AST.Stmt.Import.Importer(
              name = mkName(ids, posTree),
              selectorOpt = Some(AST.Stmt.Import.WildcardSelector()))
          case _ =>
        }
        qualOpt match {
          case Some(qual) =>
            // importQualSuffix: ID importIdSuffix?
            val nextId = findLeafByRule(qual, "ID").get
            val nextSuffix = findChild(qual, "importIdSuffix")
            return buildImporter(ids :+ mkId(nextId.text, nextId), nextSuffix, posTree, reporter)
          case _ =>
        }
        renamesOpt match {
          case Some(renames) =>
            // importRenamesSuffix: LBRACE importRename importRenameSuffix* COMMA? RBRACE
            var selectors = ISZ[AST.Stmt.Import.NamedSelector]()
            val firstRename = findChild(renames, "importRename").get
            selectors = selectors :+ buildImportRename(firstRename, reporter)
            val renameSuffixes = findChildren(renames, "importRenameSuffix")
            for (rs <- renameSuffixes) {
              val rename = findChild(rs, "importRename").get
              selectors = selectors :+ buildImportRename(rename, reporter)
            }
            return AST.Stmt.Import.Importer(
              name = mkName(ids, posTree),
              selectorOpt = Some(AST.Stmt.Import.MultiSelector(selectors)))
          case _ =>
        }
        return AST.Stmt.Import.Importer(name = mkName(ids, posTree), selectorOpt = None())
      case _ =>
        return AST.Stmt.Import.Importer(name = mkName(ids, posTree), selectorOpt = None())
    }
  }

  def buildImportRename(node: ParseTree.Node, reporter: message.Reporter): AST.Stmt.Import.NamedSelector = {
    // importRename: ID ARROW ID annot?
    var idLeaves = ISZ[ParseTree.Leaf]()
    for (c <- node.children) {
      c match {
        case c: ParseTree.Leaf if c.ruleName == "ID" => idLeaves = idLeaves :+ c
        case _ =>
      }
    }
    return AST.Stmt.Import.NamedSelector(
      from = mkId(idLeaves(0).text, idLeaves(0)),
      to = mkId(idLeaves(1).text, idLeaves(1)))
  }

  // ─── mainMember ────────────────────────────────────────────────────

  def buildMainMember(node: ParseTree.Node, reporter: message.Reporter): ISZ[AST.Stmt] = {
    // mainMember: stmt | typeDefn
    val stmtOpt = findChild(node, "stmt")
    stmtOpt match {
      case Some(s) => return ISZ(buildStmt(s, reporter))
      case _ =>
    }
    val typeDefnOpt = findChild(node, "typeDefn")
    typeDefnOpt match {
      case Some(td) => return ISZ(buildTypeDefn(td, reporter))
      case _ =>
    }
    return ISZ()
  }

  // ─── package ───────────────────────────────────────────────────────

  def buildPkg(node: ParseTree.Node, reporter: message.Reporter): AST.Stmt.Object = {
    // pkg: PACKAGE mod* name? annot? imprt* ( member* | pkgSuffix )
    val mods = buildMods(findChildren(node, "mod"))
    val nameOpt = findChild(node, "name")
    val annotOpt = findChild(node, "annot")
    val imports = findChildren(node, "imprt")
    val members = findChildren(node, "member")
    val pkgSuffixOpt = findChild(node, "pkgSuffix")

    val isApp = hasMod(mods, "app")
    val extMod = findMod(mods, "ext")
    val extNameOpt: Option[String] = extMod match {
      case Some(em) =>
        if (em.args.isEmpty) {
          Some("")
        } else {
          // extract string from args
          val argsChildren = em.args(0)._2
          var extName: String = ""
          for (c <- argsChildren) {
            c match {
              case c: ParseTree.Node =>
                val rhsOpt = findChild(c, "rhs")
                rhsOpt match {
                  case Some(rhs) =>
                    val expOpt = findChild(rhs, "exp")
                    expOpt match {
                      case Some(expNode) =>
                        val e = buildExp(expNode, reporter)
                        e match {
                          case e: AST.Exp.LitString => extName = e.value
                          case _ =>
                        }
                      case _ =>
                    }
                  case _ =>
                    val e = buildExp(c, reporter)
                    e match {
                      case e: AST.Exp.LitString => extName = e.value
                      case _ =>
                    }
                }
              case c: ParseTree.Leaf =>
                c.ruleName.native match {
                  case "STRING" =>
                    extName = parseStringLit(c.text)
                  case _ =>
                }
            }
          }
          Some(extName)
        }
      case _ => None()
    }

    val id: String = nameOpt match {
      case Some(nameNode) => idText(nameNode)
      case _ => ""
    }

    var stmts = ISZ[AST.Stmt]()

    for (imp <- imports) {
      stmts = stmts :+ buildImport(imp, reporter)
    }

    // members
    for (m <- members) {
      stmts = stmts ++ buildMember(m, extNameOpt.nonEmpty, reporter)
    }

    // pkgSuffix: LBRACE member* RBRACE
    pkgSuffixOpt match {
      case Some(ps) =>
        val pkgMembers = findChildren(ps, "member")
        for (m <- pkgMembers) {
          stmts = stmts ++ buildMember(m, extNameOpt.nonEmpty, reporter)
        }
      case _ =>
    }

    return AST.Stmt.Object(
      isApp = isApp,
      extNameOpt = extNameOpt,
      id = mkId(id, node),
      stmts = stmts,
      attr = attr(node))
  }

  // ─── member ────────────────────────────────────────────────────────

  def buildMember(node: ParseTree.Node, isExt: B, reporter: message.Reporter): ISZ[AST.Stmt] = {
    // member: varDefn | defDefn | typeDefn | init
    val varDefnOpt = findChild(node, "varDefn")
    varDefnOpt match {
      case Some(vd) => return ISZ(buildVarDefn(vd, reporter))
      case _ =>
    }
    val defDefnOpt = findChild(node, "defDefn")
    defDefnOpt match {
      case Some(dd) =>
        if (isExt) {
          return ISZ(buildExtMethod(dd, reporter))
        } else {
          return ISZ(buildDefDefn(dd, reporter))
        }
      case _ =>
    }
    val typeDefnOpt = findChild(node, "typeDefn")
    typeDefnOpt match {
      case Some(td) => return ISZ(buildTypeDefn(td, reporter))
      case _ =>
    }
    // init: TO LBRACE annot? stmt* RBRACE
    // For now, we skip init blocks as they're not directly mapped
    return ISZ()
  }

  // ─── typeDefn ──────────────────────────────────────────────────────

  def buildTypeDefn(node: ParseTree.Node, reporter: message.Reporter): AST.Stmt = {
    // typeDefn: TYPE mod* ID typeParams? ( typeDefnEnumSuffix | typeDefnAliasSuffix | typeDefnAdtSuffix )?
    val mods = buildMods(findChildren(node, "mod"))
    val id = mkId(idText(node), node)
    val typeParams = buildTypeParams(findChild(node, "typeParams"), reporter)

    // Check for enum
    if (hasMod(mods, "enum")) {
      val enumSuffix = findChild(node, "typeDefnEnumSuffix")
      enumSuffix match {
        case Some(es) =>
          val enumMembers = findChild(es, "enumMembers").get
          return buildEnum(id, enumMembers, node, reporter)
        case _ =>
          return AST.Stmt.Enum(id = id, elements = ISZ(), attr = attr(node))
      }
    }

    // Check for @range/@bits
    if (hasMod(mods, "range")) {
      return buildSubZRange(id, mods, node, reporter)
    }
    if (hasMod(mods, "bits")) {
      return buildSubZBits(id, mods, node, reporter)
    }

    // Check for alias
    if (hasMod(mods, "alias")) {
      val aliasSuffix = findChild(node, "typeDefnAliasSuffix")
      aliasSuffix match {
        case Some(as) =>
          val tipe = buildType(firstNode(as), reporter)
          return AST.Stmt.TypeAlias(id = id, typeParams = typeParams, tipe = tipe, attr = attr(node))
        case _ =>
          halt("Expected alias type")
      }
    }

    // Check for @sig / @msig / @datatype / @record
    val isSig = hasMod(mods, "sig")
    val isMsig = hasMod(mods, "msig")
    val isRecord = hasMod(mods, "record")
    val isTrait = hasMod(mods, "trait")
    val isSealed = hasMod(mods, "sealed")
    val isUnclonable = hasMod(mods, "unclonable")

    if (isSig || isMsig) {
      // Sig
      val adtSuffix = findChild(node, "typeDefnAdtSuffix")
      var parents = ISZ[AST.Type.Named]()
      var stmts = ISZ[AST.Stmt]()
      adtSuffix match {
        case Some(as) =>
          val supersOpt = findChild(as, "supers")
          supersOpt match {
            case Some(sup) => parents = buildSupers(sup, reporter)
            case _ =>
          }
          val membersOpt = findChild(as, "typeDefnAdtMembers")
          membersOpt match {
            case Some(mem) =>
              val memberNodes = findChildren(mem, "member")
              for (m <- memberNodes) {
                stmts = stmts ++ buildMember(m, F, reporter)
              }
            case _ =>
          }
        case _ =>
      }
      return AST.Stmt.Sig(
        isImmutable = !isMsig,
        isSealed = isSealed,
        isExt = F,
        id = id,
        typeParams = typeParams,
        parents = parents,
        stmts = stmts,
        attr = attr(node))
    }

    // ADT (default)
    val adtSuffix = findChild(node, "typeDefnAdtSuffix")
    var params = ISZ[AST.AdtParam]()
    var parents = ISZ[AST.Type.Named]()
    var stmts = ISZ[AST.Stmt]()
    adtSuffix match {
      case Some(as) =>
        val paramsOpt = findChild(as, "params")
        paramsOpt match {
          case Some(ps) => params = buildAdtParams(ps, reporter)
          case _ =>
        }
        val supersOpt = findChild(as, "supers")
        supersOpt match {
          case Some(sup) => parents = buildSupers(sup, reporter)
          case _ =>
        }
        val membersOpt = findChild(as, "typeDefnAdtMembers")
        membersOpt match {
          case Some(mem) =>
            val memberNodes = findChildren(mem, "member")
            for (m <- memberNodes) {
              stmts = stmts ++ buildMember(m, F, reporter)
            }
          case _ =>
        }
      case _ =>
    }

    return AST.Stmt.Adt(
      isRoot = isTrait,
      isDatatype = !isRecord,
      isUnclonable = isUnclonable,
      id = id,
      typeParams = typeParams,
      params = params,
      parents = parents,
      stmts = stmts,
      attr = attr(node))
  }

  def buildEnum(id: AST.Id, enumMembers: ParseTree.Node, posTree: ParseTree, reporter: message.Reporter): AST.Stmt.Enum = {
    // enumMembers: LBRACE ID commaId* COMMA? RBRACE
    var elements = ISZ[AST.Id]()
    for (c <- enumMembers.children) {
      c match {
        case c: ParseTree.Leaf if c.ruleName == "ID" => elements = elements :+ mkId(c.text, c)
        case c: ParseTree.Node if c.ruleName == "commaId" =>
          val idLeaf = findLeafByRule(c, "ID").get
          elements = elements :+ mkId(idLeaf.text, idLeaf)
        case _ =>
      }
    }
    return AST.Stmt.Enum(id = id, elements = elements, attr = attr(posTree))
  }

  def buildSubZRange(id: AST.Id, mods: ISZ[ModInfo], posTree: ParseTree, reporter: message.Reporter): AST.Stmt.SubZ = {
    val rangeMod = findMod(mods, "range").get
    var hasMin = F
    var hasMax = F
    var min: Z = 0
    var max: Z = 0
    var isIndex = F
    var index: Z = 0

    for (pair <- rangeMod.args) {
      val argsChildren = pair._2
      for (c <- argsChildren) {
        c match {
          case c: ParseTree.Node if c.ruleName == "namedArg" =>
            val argName = idText(c)
            val assignSuffix = findChild(c, "assignSuffix")
            assignSuffix match {
              case Some(as) =>
                val rhsNode = findChild(as, "rhs")
                rhsNode match {
                  case Some(rhs) =>
                    val expNode = findChild(rhs, "exp")
                    expNode match {
                      case Some(en) =>
                        val e = buildExp(en, reporter)
                        argName.native match {
                          case "min" =>
                            hasMin = T
                            e match {
                              case e: AST.Exp.LitZ => min = e.value
                              case _ =>
                            }
                          case "max" =>
                            hasMax = T
                            e match {
                              case e: AST.Exp.LitZ => max = e.value
                              case _ =>
                            }
                          case "index" =>
                            isIndex = T
                            e match {
                              case e: AST.Exp.LitZ => index = e.value
                              case _ =>
                            }
                          case _ =>
                        }
                      case _ =>
                    }
                  case _ =>
                }
              case _ =>
            }
          case _ =>
        }
      }
    }

    return AST.Stmt.SubZ(
      id = id,
      isSigned = T,
      isBitVector = F,
      isWrapped = F,
      hasMin = hasMin,
      hasMax = hasMax,
      bitWidth = 0,
      min = min,
      max = max,
      isIndex = isIndex,
      index = index,
      attr = attr(posTree))
  }

  def buildSubZBits(id: AST.Id, mods: ISZ[ModInfo], posTree: ParseTree, reporter: message.Reporter): AST.Stmt.SubZ = {
    val bitsMod = findMod(mods, "bits").get
    var isSigned = F
    var bitWidth: Z = 0
    var hasMin = F
    var hasMax = F
    var min: Z = 0
    var max: Z = 0
    var isIndex = F
    var index: Z = 0

    for (pair <- bitsMod.args) {
      val argsChildren = pair._2
      for (c <- argsChildren) {
        c match {
          case c: ParseTree.Node if c.ruleName == "namedArg" =>
            val argName = idText(c)
            val rhsOpt = findChild(c, "assignSuffix")
            rhsOpt match {
              case Some(as) =>
                val rhs = findChild(as, "rhs")
                rhs match {
                  case Some(rhsN) =>
                    val expOpt = findChild(rhsN, "exp")
                    expOpt match {
                      case Some(en) =>
                        val e = buildExp(en, reporter)
                        argName.native match {
                          case "signed" =>
                            e match {
                              case e: AST.Exp.LitB => isSigned = e.value
                              case _ =>
                            }
                          case "width" =>
                            e match {
                              case e: AST.Exp.LitZ => bitWidth = e.value
                              case _ =>
                            }
                          case "min" =>
                            hasMin = T
                            e match {
                              case e: AST.Exp.LitZ => min = e.value
                              case _ =>
                            }
                          case "max" =>
                            hasMax = T
                            e match {
                              case e: AST.Exp.LitZ => max = e.value
                              case _ =>
                            }
                          case "index" =>
                            isIndex = T
                            e match {
                              case e: AST.Exp.LitZ => index = e.value
                              case _ =>
                            }
                          case _ =>
                        }
                      case _ =>
                    }
                  case _ =>
                }
              case _ =>
            }
          case _ =>
        }
      }
    }

    return AST.Stmt.SubZ(
      id = id,
      isSigned = isSigned,
      isBitVector = T,
      isWrapped = F,
      hasMin = hasMin,
      hasMax = hasMax,
      bitWidth = bitWidth,
      min = min,
      max = max,
      isIndex = isIndex,
      index = index,
      attr = attr(posTree))
  }

  // ─── typeParams ────────────────────────────────────────────────────

  def buildTypeParams(nodeOpt: Option[ParseTree.Node], reporter: message.Reporter): ISZ[AST.TypeParam] = {
    nodeOpt match {
      case Some(node) =>
        // typeParams: LSQUARE typeParam typeParamSuffix* RSQUARE
        var r = ISZ[AST.TypeParam]()
        val firstTP = findChild(node, "typeParam").get
        r = r :+ buildTypeParam(firstTP, reporter)
        val suffixes = findChildren(node, "typeParamSuffix")
        for (s <- suffixes) {
          val tp = findChild(s, "typeParam").get
          r = r :+ buildTypeParam(tp, reporter)
        }
        return r
      case _ => return ISZ()
    }
  }

  def buildTypeParam(node: ParseTree.Node, reporter: message.Reporter): AST.TypeParam = {
    // typeParam: mod* ID
    val mods = buildMods(findChildren(node, "mod"))
    val id = mkId(idText(node), node)
    val kind: Typed.VarKind.Type = if (hasMod(mods, "index")) {
      Typed.VarKind.Index
    } else if (hasMod(mods, "mut")) {
      Typed.VarKind.Mutable
    } else {
      Typed.VarKind.Immutable
    }
    return AST.TypeParam(id = id, kind = kind)
  }

  // ─── params ────────────────────────────────────────────────────────

  def buildAdtParams(node: ParseTree.Node, reporter: message.Reporter): ISZ[AST.AdtParam] = {
    // params: LPAREN param commaParams* COMMA? RPAREN
    var r = ISZ[AST.AdtParam]()
    val firstParam = findChild(node, "param").get
    r = r :+ buildAdtParam(firstParam, reporter)
    val suffixes = findChildren(node, "commaParams")
    for (s <- suffixes) {
      val p = findChild(s, "param").get
      r = r :+ buildAdtParam(p, reporter)
    }
    return r
  }

  def buildAdtParam(node: ParseTree.Node, reporter: message.Reporter): AST.AdtParam = {
    // param: VAR? mod* ID COLON ARROW? type
    val mods = buildMods(findChildren(node, "mod"))
    val isHidden = hasMod(mods, "hidden")
    val varLeafOpt = findLeafByRule(node, "VAR")
    val isVal: B = varLeafOpt match {
      case Some(v) => v.text == "val"
      case _ => T
    }
    val id = mkId(idText(node), node)
    val typeNodes = findChildren(node, "type")
    var tipe: AST.Type = AST.Type.Named(name = mkName(ISZ(), node), typeArgs = ISZ(), attr = emptyTypedAttr)
    if (typeNodes.nonEmpty) {
      tipe = buildType(typeNodes(0), reporter)
    } else {
      findChild(node, "type1") match {
        case Some(t1) => tipe = buildType1(t1, reporter)
        case _ =>
          findChild(node, "type0") match {
            case Some(t0) => tipe = buildType0(t0, reporter)
            case _ => halt(st"Expected type in param ${node.toST.render}".render)
          }
      }
    }
    return AST.AdtParam(isHidden = isHidden, isVal = isVal, id = id, tipe = tipe)
  }

  def buildParams(node: ParseTree.Node, reporter: message.Reporter): ISZ[AST.Param] = {
    // params: LPAREN param commaParams* COMMA? RPAREN
    var r = ISZ[AST.Param]()
    val firstParam = findChild(node, "param").get
    r = r :+ buildParam(firstParam, reporter)
    val suffixes = findChildren(node, "commaParams")
    for (s <- suffixes) {
      val p = findChild(s, "param").get
      r = r :+ buildParam(p, reporter)
    }
    return r
  }

  def buildParam(node: ParseTree.Node, reporter: message.Reporter): AST.Param = {
    // param: VAR? mod* ID COLON ARROW? type
    val mods = buildMods(findChildren(node, "mod"))
    val isHidden = hasMod(mods, "hidden")
    val id = mkId(idText(node), node)
    val typeNodes = findChildren(node, "type")
    if (typeNodes.isEmpty) {
      halt(st"Expected type in param ${node.toST.render}".render)
    }
    val tipe: AST.Type = buildType(typeNodes(0), reporter)
    return AST.Param(isHidden = isHidden, id = id, tipe = tipe)
  }

  // ─── supers ────────────────────────────────────────────────────────

  def buildSupers(node: ParseTree.Node, reporter: message.Reporter): ISZ[AST.Type.Named] = {
    // supers: COLON supr commaSuper*
    var r = ISZ[AST.Type.Named]()
    val firstSuper = findChild(node, "supr").get
    r = r :+ buildSuper(firstSuper, reporter)
    val suffixes = findChildren(node, "commaSuper")
    for (s <- suffixes) {
      val sup = findChild(s, "supr").get
      r = r :+ buildSuper(sup, reporter)
    }
    return r
  }

  def buildSuper(node: ParseTree.Node, reporter: message.Reporter): AST.Type.Named = {
    // supr: annot? name typeArgs?
    val nameNode = findChild(node, "name").get
    val name = buildName(nameNode, reporter)
    val typeArgsOpt = findChild(node, "typeArgs")
    val typeArgs = buildTypeArgsList(typeArgsOpt, reporter)
    return AST.Type.Named(name = name, typeArgs = typeArgs, attr = typedAttr(node))
  }

  // ─── name ──────────────────────────────────────────────────────────

  def buildName(node: ParseTree.Node, reporter: message.Reporter): AST.Name = {
    // name: ID nameSuffix*
    var ids = ISZ[AST.Id]()
    val firstId = findLeafByRule(node, "ID").get
    ids = ids :+ mkId(firstId.text, firstId)
    val suffixes = findChildren(node, "nameSuffix")
    for (s <- suffixes) {
      val idLeaf = findLeafByRule(s, "ID").get
      ids = ids :+ mkId(idLeaf.text, idLeaf)
    }
    return mkName(ids, node)
  }

  // ─── type ──────────────────────────────────────────────────────────

  def buildType(node: ParseTree.Node, reporter: message.Reporter): AST.Type = {
    // type: type1 typeSuffix*
    val type1Node = findChild(node, "type1").get
    val typeSuffixes = findChildren(node, "typeSuffix")
    var result = buildType1(type1Node, reporter)
    for (ts <- typeSuffixes) {
      // typeSuffix: ARROW annot? type1
      val annotOpt = findChild(ts, "annot")
      val isPure = annotOpt.nonEmpty
      val retType1 = findChild(ts, "type1").get
      val retType = buildType1(retType1, reporter)
      result match {
        case tupleResult: AST.Type.Tuple =>
          result = AST.Type.Fun(isPure = isPure, isByName = F, args = tupleResult.args, ret = retType, attr = typedAttr(ts))
        case _ =>
          result = AST.Type.Fun(isPure = isPure, isByName = F, args = ISZ(result), ret = retType, attr = typedAttr(ts))
      }
    }
    return result
  }

  def buildType1(node: ParseTree.Node, reporter: message.Reporter): AST.Type = {
    // type1: parenType | type0 type0Suffix*
    val parenOpt = findChild(node, "parenType")
    parenOpt match {
      case Some(pt) => return buildParenType(pt, reporter)
      case _ =>
    }
    val type0Nodes = findChildren(node, "type0")
    if (type0Nodes.isEmpty) {
      halt(st"Expected type0 in type1 ${node.toST.render}".render)
    }
    return buildType0(type0Nodes(0), reporter)
  }

  def buildParenType(node: ParseTree.Node, reporter: message.Reporter): AST.Type = {
    // parenType: LPAREN typeParenArgs COMMA? RPAREN
    val argsNode = findChild(node, "typeParenArgs").get
    // typeParenArgs: annot? type commaAnnotType* | namedType commaNamedType*

    // Check for by-name type: if annotated and only one type => Type.Fun(isByName = T)
    val annotOpt = findChild(argsNode, "annot")

    val typeNodes = findChildren(argsNode, "type")
    val commaAnnotTypes = findChildren(argsNode, "commaAnnotType")

    if (typeNodes.size == 1 && commaAnnotTypes.isEmpty) {
      // single type - either by-name annotation or just a parenthesized type
      val t = buildType(typeNodes(0), reporter)
      return t
    }

    // Multiple types => Tuple
    var types = ISZ[AST.Type]()
    for (tn <- typeNodes) {
      types = types :+ buildType(tn, reporter)
    }
    for (cat <- commaAnnotTypes) {
      val tn = findChild(cat, "type").get
      types = types :+ buildType(tn, reporter)
    }

    // namedType path (skip for now, as PP doesn't generate named type args for types)

    if (types.size == 1) {
      return types(0)
    }
    return AST.Type.Tuple(args = types, attr = typedAttr(node))
  }

  def buildType0(node: ParseTree.Node, reporter: message.Reporter): AST.Type.Named = {
    // type0: ID dotID* typeArgs?
    val id = findLeafByRule(node, "ID").get
    var ids = ISZ[AST.Id](mkId(id.text, id))
    val dotIds = findChildren(node, "dotID")
    for (d <- dotIds) {
      val did = findLeafByRule(d, "ID").get
      ids = ids :+ mkId(did.text, did)
    }
    val typeArgsOpt = findChild(node, "typeArgs")
    val typeArgs = buildTypeArgsList(typeArgsOpt, reporter)
    return AST.Type.Named(
      name = mkName(ids, node),
      typeArgs = typeArgs,
      attr = typedAttr(node))
  }

  def buildTypeArgsList(nodeOpt: Option[ParseTree.Node], reporter: message.Reporter): ISZ[AST.Type] = {
    nodeOpt match {
      case Some(node) =>
        // typeArgs: LSQUARE typeParenArgs RSQUARE
        val argsNode = findChild(node, "typeParenArgs").get

        val typeNodes = findChildren(argsNode, "type")
        val commaAnnotTypes = findChildren(argsNode, "commaAnnotType")

        var types = ISZ[AST.Type]()
        for (tn <- typeNodes) {
          types = types :+ buildType(tn, reporter)
        }
        for (cat <- commaAnnotTypes) {
          val tn = findChild(cat, "type").get
          types = types :+ buildType(tn, reporter)
        }
        return types
      case _ => return ISZ()
    }
  }

  // ─── pattern ───────────────────────────────────────────────────────

  def buildPattern(node: ParseTree.Node, reporter: message.Reporter): AST.Pattern = {
    // pattern: annot? ( idTypePattern | pattern0 | wildCardPattern | wildCardSeqPattern)
    val idTypePat = findChild(node, "idTypePattern")
    idTypePat match {
      case Some(itp) => return buildIdTypePattern(itp, reporter)
      case _ =>
    }
    val pat0 = findChild(node, "pattern0")
    pat0 match {
      case Some(p0) => return buildPattern0(p0, reporter)
      case _ =>
    }
    val wildcard = findChild(node, "wildCardPattern")
    wildcard match {
      case Some(wc) => return buildWildcardPattern(wc, reporter)
      case _ =>
    }
    val wildSeq = findChild(node, "wildCardSeqPattern")
    wildSeq match {
      case Some(_) => return AST.Pattern.SeqWildcard(attr = typedAttr(node))
      case _ =>
    }
    halt(st"Could not build pattern from ${node.toST.render}".render)
  }

  def buildIdTypePattern(node: ParseTree.Node, reporter: message.Reporter): AST.Pattern.VarBinding = {
    // idTypePattern: ID colonType1
    val id = mkId(idText(node), node)
    val colonType = findChild(node, "colonType1").get
    val type1 = findChild(colonType, "type1").get
    val tipe = buildType1(type1, reporter)
    return AST.Pattern.VarBinding(id = id, tipeOpt = Some(tipe), idContext = ISZ(), attr = typedAttr(node))
  }

  def buildWildcardPattern(node: ParseTree.Node, reporter: message.Reporter): AST.Pattern.Wildcard = {
    // wildCardPattern: UNDERSCORE colonType1?
    val colonTypeOpt = findChild(node, "colonType1")
    val typeOpt: Option[AST.Type] = colonTypeOpt match {
      case Some(ct) =>
        val type1 = findChild(ct, "type1").get
        Some(buildType1(type1, reporter))
      case _ => None()
    }
    return AST.Pattern.Wildcard(typeOpt = typeOpt, attr = typedAttr(node))
  }

  def buildPattern0(node: ParseTree.Node, reporter: message.Reporter): AST.Pattern = {
    // pattern0: lit | refPattern | patterns | name patterns? | idNamePattern
    val litOpt = findChild(node, "lit")
    litOpt match {
      case Some(l) =>
        val lit = buildLit(l, reporter)
        return AST.Pattern.Literal(lit = lit)
      case _ =>
    }

    val refPatOpt = findChild(node, "refPattern")
    refPatOpt match {
      case Some(rp) =>
        // refPattern: DOT name
        val nameNode = findChild(rp, "name").get
        val name = buildName(nameNode, reporter)
        return AST.Pattern.Ref(isAccess = T, name = name, receiverTipeOpt = None(), idContext = ISZ(), attr = resolvedAttr(node))
      case _ =>
    }

    val idNamePatOpt = findChild(node, "idNamePattern")
    idNamePatOpt match {
      case Some(inp) =>
        // idNamePattern: ID AT name patterns
        val id = mkId(idText(inp), inp)
        val nameNode = findChild(inp, "name").get
        val name = buildName(nameNode, reporter)
        val patternsNode = findChild(inp, "patterns").get
        val pats = buildPatterns(patternsNode, reporter)
        return AST.Pattern.Structure(
          idOpt = Some(id),
          nameOpt = Some(name),
          patterns = pats,
          idContext = ISZ(),
          attr = resolvedAttr(node))
      case _ =>
    }

    val nameOpt = findChild(node, "name")
    val patternsOpt = findChild(node, "patterns")
    nameOpt match {
      case Some(nameNode) =>
        val name = buildName(nameNode, reporter)
        patternsOpt match {
          case Some(patsNode) =>
            val pats = buildPatterns(patsNode, reporter)
            return AST.Pattern.Structure(
              idOpt = None(),
              nameOpt = Some(name),
              patterns = pats,
              idContext = ISZ(),
              attr = resolvedAttr(node))
          case _ =>
            // Just a name - could be a VarBinding without type
            if (name.ids.size == 1) {
              return AST.Pattern.VarBinding(
                id = name.ids(0),
                tipeOpt = None(),
                idContext = ISZ(),
                attr = typedAttr(node))
            } else {
              return AST.Pattern.Ref(isAccess = F, name = name, receiverTipeOpt = None(), idContext = ISZ(), attr = resolvedAttr(node))
            }
        }
      case _ =>
    }

    patternsOpt match {
      case Some(patsNode) =>
        val pats = buildPatterns(patsNode, reporter)
        return AST.Pattern.Structure(
          idOpt = None(),
          nameOpt = None(),
          patterns = pats,
          idContext = ISZ(),
          attr = resolvedAttr(node))
      case _ =>
    }

    halt(st"Could not build pattern0 from ${node.toST.render}".render)
  }

  def buildPatterns(node: ParseTree.Node, reporter: message.Reporter): ISZ[AST.Pattern] = {
    // patterns: LPAREN patternsArg COMMA? RPAREN
    val argsNode = findChild(node, "patternsArg").get
    // patternsArg: pattern commaPattern* | namedPattern commaNamedPattern*
    var r = ISZ[AST.Pattern]()
    val patternNodes = findChildren(argsNode, "pattern")
    for (p <- patternNodes) {
      r = r :+ buildPattern(p, reporter)
    }
    val commaPatterns = findChildren(argsNode, "commaPattern")
    for (cp <- commaPatterns) {
      val p = findChild(cp, "pattern").get
      r = r :+ buildPattern(p, reporter)
    }
    return r
  }

  // ─── expression ────────────────────────────────────────────────────

  def buildExp(node: ParseTree.Node, reporter: message.Reporter): AST.Exp = {
    node.ruleName.native match {
      case "exp" => return buildExpTop(node, reporter)
      case "exp3" => return buildExp3(node, reporter)
      case "exp2" => return buildExp2(node, reporter)
      case "exp1" => return buildExp1(node, reporter)
      case "exp0" => return buildExp0(node, reporter)
      case "idExp" => return buildIdExp(node, reporter)
      case "thisExp" => return AST.Exp.This(owner = ISZ(), attr = typedAttr(node))
      case "superExp" => return AST.Exp.Super(idOpt = None(), attr = typedAttr(node))
      case "lit" => return buildLitExp(node, reporter)
      case "interp" => return buildInterp(node, reporter)
      case "pureBlock" => return buildPureBlock(node, reporter)
      case "paren" => return buildParenExp(node, reporter)
      // "Binary" case no longer needed - handled directly in buildExp3
      case "rhs" => return buildRhs(node, reporter)
      case "ite" => return buildIte(node, reporter)
      case _ =>
        // Try to dispatch based on children
        for (c <- node.children) {
          c match {
            case c: ParseTree.Node =>
              c.ruleName.native match {
                case "exp" => return buildExpTop(c, reporter)
                case "exp3" => return buildExp3(c, reporter)
                case "exp2" => return buildExp2(c, reporter)
                case "exp1" => return buildExp1(c, reporter)
                case "exp0" => return buildExp0(c, reporter)
                case _ =>
              }
            case _ =>
          }
        }
        halt(st"Could not build exp from '${node.ruleName}': ${node.toST.render}".render)
    }
  }

  def buildExpTop(node: ParseTree.Node, reporter: message.Reporter): AST.Exp = {
    // exp: exp3 | forExp | defAnon | quant | ite
    val exp3Opt = findChild(node, "exp3")
    exp3Opt match {
      case Some(e3) => return buildExp3(e3, reporter)
      case _ =>
    }
    val forExpOpt = findChild(node, "forExp")
    forExpOpt match {
      case Some(fe) => return buildForExp(fe, reporter)
      case _ =>
    }
    val defAnonOpt = findChild(node, "defAnon")
    defAnonOpt match {
      case Some(da) => return buildDefAnon(da, reporter)
      case _ =>
    }
    val quantOpt = findChild(node, "quant")
    quantOpt match {
      case Some(q) => return buildQuant(q, reporter)
      case _ =>
    }
    val iteOpt = findChild(node, "ite")
    iteOpt match {
      case Some(i) => return buildIte(i, reporter)
      case _ =>
    }
    halt(st"Could not build exp from ${node.toST.render}".render)
  }

  def buildIte(node: ParseTree.Node, reporter: message.Reporter): AST.Exp = {
    // ite: QUESTION exp COLON exp COLON exp
    val exps = findChildren(node, "exp")
    val cond = buildExpTop(exps(0), reporter)
    val thenExp = buildExpTop(exps(1), reporter)
    val elseExp = buildExpTop(exps(2), reporter)
    return AST.Exp.If(
      cond = cond,
      thenExp = thenExp,
      elseExp = elseExp,
      attr = typedAttr(node))
  }

  def buildExp3(node: ParseTree.Node, reporter: message.Reporter): AST.Exp = {
    // exp3: exp2 infixSuffix*
    val exp2Node = findChild(node, "exp2").get
    val infixSuffixes = findChildren(node, "infixSuffix")
    if (infixSuffixes.isEmpty) {
      return buildExp2(exp2Node, reporter)
    }
    val binaryTree = SlangLl2ParseTreeUtil.exp3ToBinary(node, reporter)
    return buildBinaryFromTree(binaryTree, node, reporter)
  }

  def buildBinaryFromTree(tree: ParseTree, contextNode: ParseTree, reporter: message.Reporter): AST.Exp = {
    tree match {
      case n: ParseTree.Node if n.ruleName == "Binary" =>
        val left = buildBinaryFromTree(n.children.atS32(s32"0"), contextNode, reporter)
        val opLeaf = n.children.atS32(s32"1").asInstanceOf[ParseTree.Leaf]
        val right = buildBinaryFromTree(n.children.atS32(s32"2"), contextNode, reporter)
        return AST.Exp.Binary(
          left = left,
          op = opLeaf.text,
          right = right,
          attr = resolvedAttr(contextNode),
          opPosOpt = opLeaf.posOpt)
      case n: ParseTree.Node =>
        return buildExp2(n, reporter)
      case _ =>
        halt(st"Unexpected leaf in buildBinaryFromTree: ${tree.ruleName}".render)
    }
  }

  def buildExp2(node: ParseTree.Node, reporter: message.Reporter): AST.Exp = {
    // exp2: exp1 access* eta?
    val exp1Node = findChild(node, "exp1").get
    var result = buildExp1(exp1Node, reporter)
    val accesses = findChildren(node, "access")
    for (acc <- accesses) {
      result = buildAccess(result, acc, reporter)
    }
    val etaOpt = findChild(node, "eta")
    etaOpt match {
      case Some(_) =>
        result match {
          case result: AST.Exp.Ident =>
            return AST.Exp.Eta(ref = result, attr = typedAttr(node))
          case result: AST.Exp.Select =>
            return AST.Exp.Eta(ref = result, attr = typedAttr(node))
          case _ =>
            halt(st"Expected ref for eta, got ${result}".render)
        }
      case _ =>
    }
    return result
  }

  def buildExp1(node: ParseTree.Node, reporter: message.Reporter): AST.Exp = {
    // exp1: OP? ( exp0 | paren )
    val opOpt = findLeafByRule(node, "OP")
    val exp0Opt = findChild(node, "exp0")
    val parenOpt = findChild(node, "paren")
    var inner: AST.Exp = AST.Exp.LitB(value = F, attr = attr(node))
    exp0Opt match {
      case Some(e0) => inner = buildExp0(e0, reporter)
      case _ =>
        parenOpt match {
          case Some(p) => inner = buildParenExp(p, reporter)
          case _ => halt(st"Expected exp0 or paren in exp1 ${node.toST.render}".render)
        }
    }
    opOpt match {
      case Some(opLeaf) =>
        val opText = opLeaf.text
        var unaryOp: AST.Exp.UnaryOp.Type = AST.Exp.UnaryOp.Not
        opText.native match {
          case "!" => unaryOp = AST.Exp.UnaryOp.Not
          case "+" => unaryOp = AST.Exp.UnaryOp.Plus
          case "-" => unaryOp = AST.Exp.UnaryOp.Minus
          case "~" => unaryOp = AST.Exp.UnaryOp.Complement
          case _ => halt(st"Unknown unary operator: $opText".render)
        }
        return AST.Exp.Unary(
          op = unaryOp,
          exp = inner,
          attr = resolvedAttr(node),
          opPosOpt = opLeaf.posOpt)
      case _ =>
    }
    return inner
  }

  def buildExp0(node: ParseTree.Node, reporter: message.Reporter): AST.Exp = {
    // exp0: idExp | thisExp | superExp | lit | interp | pureBlock | jsonLit
    for (c <- node.children) {
      c match {
        case c: ParseTree.Node =>
          c.ruleName.native match {
            case "idExp" => return buildIdExp(c, reporter)
            case "thisExp" => return AST.Exp.This(owner = ISZ(), attr = typedAttr(c))
            case "superExp" => return AST.Exp.Super(idOpt = None(), attr = typedAttr(c))
            case "lit" => return buildLitExp(c, reporter)
            case "interp" => return buildInterp(c, reporter)
            case "pureBlock" => return buildPureBlock(c, reporter)
            case "jsonLit" => halt("jsonLit not supported in AST builder")
            case _ =>
          }
        case _ =>
      }
    }
    halt(st"Could not build exp0 from ${node.toST.render}".render)
  }

  def buildIdExp(node: ParseTree.Node, reporter: message.Reporter): AST.Exp = {
    // idExp: ID typeArgs?
    val idLeaf = findLeafByRule(node, "ID").get
    val typeArgsOpt = findChild(node, "typeArgs")
    val targs = buildTypeArgsList(typeArgsOpt, reporter)
    if (targs.nonEmpty) {
      return AST.Exp.Select(
        receiverOpt = None(),
        id = mkId(idLeaf.text, idLeaf),
        targs = targs,
        attr = resolvedAttr(node))
    }
    return AST.Exp.Ident(id = mkId(idLeaf.text, idLeaf), attr = resolvedAttr(node))
  }

  def buildAccess(receiver: AST.Exp, node: ParseTree.Node, reporter: message.Reporter): AST.Exp = {
    // access: QUESTION? ( fieldAccess | applyAccess )
    val questionOpt = findLeafByRule(node, "QUESTION")
    if (questionOpt.nonEmpty) {
      halt("?. access not supported in AST builder")
    }
    val fieldOpt = findChild(node, "fieldAccess")
    fieldOpt match {
      case Some(fa) => return buildFieldAccess(receiver, fa, reporter)
      case _ =>
    }
    val applyOpt = findChild(node, "applyAccess")
    applyOpt match {
      case Some(aa) => return buildApplyAccess(receiver, aa, reporter)
      case _ =>
    }
    halt(st"Could not build access from ${node.toST.render}".render)
  }

  def buildFieldAccess(receiver: AST.Exp, node: ParseTree.Node, reporter: message.Reporter): AST.Exp = {
    // fieldAccess: DOT ID typeArgs?
    val idLeaf = findLeafByRule(node, "ID").get
    val typeArgsOpt = findChild(node, "typeArgs")
    val targs = buildTypeArgsList(typeArgsOpt, reporter)
    return AST.Exp.Select(
      receiverOpt = Some(receiver),
      id = mkId(idLeaf.text, idLeaf),
      targs = targs,
      attr = resolvedAttr(node))
  }

  def buildApplyAccess(receiver: AST.Exp, node: ParseTree.Node, reporter: message.Reporter): AST.Exp = {
    // applyAccess: LPAREN args? COMMA? RPAREN fn?
    val argsOpt = findChild(node, "args")
    argsOpt match {
      case Some(argsNode) =>
        // args: annot? rhs argSuffix* | namedArg namedArgSuffix*
        val namedArgNodes = findChildren(argsNode, "namedArg")
        if (namedArgNodes.nonEmpty) {
          // Named args
          var namedArgs = ISZ[AST.NamedArg]()
          var idx: Z = 0
          for (na <- namedArgNodes) {
            val argId = mkId(idText(na), na)
            val assignSuffix = findChild(na, "assignSuffix")
            assignSuffix match {
              case Some(as) =>
                val rhs = findChild(as, "rhs").get
                val argExp = buildRhs(rhs, reporter)
                namedArgs = namedArgs :+ AST.NamedArg(id = argId, arg = argExp, index = idx)
                idx = idx + 1
              case _ =>
                // namedArg: ID ASSIGN annot? rhs
                val rhsOpt = findChild(na, "rhs")
                rhsOpt match {
                  case Some(rhs) =>
                    val argExp = buildRhs(rhs, reporter)
                    namedArgs = namedArgs :+ AST.NamedArg(id = argId, arg = argExp, index = idx)
                    idx = idx + 1
                  case _ =>
                }
            }
          }
          val namedArgSuffixes = findChildren(argsNode, "namedArgSuffix")
          for (nas <- namedArgSuffixes) {
            val na = findChild(nas, "namedArg").get
            val argId = mkId(idText(na), na)
            val rhsOpt = findChild(na, "rhs")
            rhsOpt match {
              case Some(rhs) =>
                val argExp = buildRhs(rhs, reporter)
                namedArgs = namedArgs :+ AST.NamedArg(id = argId, arg = argExp, index = idx)
                idx = idx + 1
              case _ =>
            }
          }
          val ident: AST.Exp.Ident = receiver match {
            case receiver: AST.Exp.Ident => receiver
            case receiver: AST.Exp.Select =>
              AST.Exp.Ident(id = receiver.id, attr = resolvedAttr(node))
            case _ =>
              AST.Exp.Ident(id = AST.Id("apply", emptyAttr), attr = resolvedAttr(node))
          }
          val rcvOpt: Option[AST.Exp] = receiver match {
            case _: AST.Exp.Ident => None()
            case receiver: AST.Exp.Select => receiver.receiverOpt
            case _ => Some(receiver)
          }
          return AST.Exp.InvokeNamed(
            receiverOpt = rcvOpt,
            ident = ident,
            targs = ISZ(),
            args = namedArgs,
            attr = resolvedAttr(node))
        } else {
          // Positional args
          var args = ISZ[AST.Exp]()
          val rhsNodes = findChildren(argsNode, "rhs")
          for (rhs <- rhsNodes) {
            args = args :+ buildRhs(rhs, reporter)
          }
          val argSuffixes = findChildren(argsNode, "argSuffix")
          for (asf <- argSuffixes) {
            val rhs = findChild(asf, "rhs").get
            args = args :+ buildRhs(rhs, reporter)
          }

          val ident: AST.Exp.Ident = receiver match {
            case receiver: AST.Exp.Ident => receiver
            case receiver: AST.Exp.Select =>
              AST.Exp.Ident(id = receiver.id, attr = resolvedAttr(node))
            case _ =>
              AST.Exp.Ident(id = AST.Id("apply", emptyAttr), attr = resolvedAttr(node))
          }
          val rcvOpt: Option[AST.Exp] = receiver match {
            case _: AST.Exp.Ident => None()
            case receiver: AST.Exp.Select => receiver.receiverOpt
            case _ => Some(receiver)
          }
          return AST.Exp.Invoke(
            receiverOpt = rcvOpt,
            ident = ident,
            targs = ISZ(),
            args = args,
            attr = resolvedAttr(node))
        }
      case _ =>
        // Empty args: receiver()
        val ident: AST.Exp.Ident = receiver match {
          case receiver: AST.Exp.Ident => receiver
          case receiver: AST.Exp.Select =>
            AST.Exp.Ident(id = receiver.id, attr = resolvedAttr(node))
          case _ =>
            AST.Exp.Ident(id = AST.Id("apply", emptyAttr), attr = resolvedAttr(node))
        }
        val rcvOpt: Option[AST.Exp] = receiver match {
          case _: AST.Exp.Ident => None()
          case receiver: AST.Exp.Select => receiver.receiverOpt
          case _ => Some(receiver)
        }
        return AST.Exp.Invoke(
          receiverOpt = rcvOpt,
          ident = ident,
          targs = ISZ(),
          args = ISZ(),
          attr = resolvedAttr(node))
    }
  }

  def buildParenExp(node: ParseTree.Node, reporter: message.Reporter): AST.Exp = {
    // paren: LPAREN annot? parenArgs COMMA? RPAREN
    val annotOpt = findChild(node, "annot")
    val parenArgsNode = findChild(node, "parenArgs").get
    // parenArgs: exp annot? commaExpAnnot* | namedExpAnnot commaNamedExpAnnot*

    // Check for labeled expression: (@l ...)  or (@l(n) ...)
    annotOpt match {
      case Some(annotNode) =>
        val annotArgs = findChildren(annotNode, "annotArg")
        for (aa <- annotArgs) {
          val key = firstLeaf(aa).text
          if (key == "l") {
            // Labeled expression
            val expNodes = findChildren(parenArgsNode, "exp")
            if (expNodes.nonEmpty) {
              val innerExp = buildExp(expNodes(0), reporter)
              // Check for num argument
              val numOpt: Option[AST.Exp.LitZ] = if (aa.children.sizeS32 > s32"1") {
                var found: Option[AST.Exp.LitZ] = None()
                for (c <- aa.children) {
                  c match {
                    case c: ParseTree.Node =>
                      val e = buildExp(c, reporter)
                      e match {
                        case e: AST.Exp.LitZ => found = Some(e)
                        case _ =>
                      }
                    case _ =>
                  }
                }
                found
              } else {
                None()
              }
              return AST.Exp.Labeled(numOpt = numOpt, exp = innerExp, attr = attr(node))
            }
          }
        }
      case _ =>
    }

    // Check for named args
    val namedExpAnnots = findChildren(parenArgsNode, "namedExpAnnot")
    if (namedExpAnnots.nonEmpty) {
      // InvokeNamed with apply
      var namedArgs = ISZ[AST.NamedArg]()
      var idx: Z = 0
      for (nea <- namedExpAnnots) {
        val argId = mkId(idText(nea), nea)
        val expNode = findChild(nea, "exp").get
        val argExp = buildExp(expNode, reporter)
        namedArgs = namedArgs :+ AST.NamedArg(id = argId, arg = argExp, index = idx)
        idx = idx + 1
      }
      val commaNamedExpAnnots = findChildren(parenArgsNode, "commaNamedExpAnnot")
      for (cnea <- commaNamedExpAnnots) {
        val argId = mkId(idText(cnea), cnea)
        val expNode = findChild(cnea, "exp").get
        val argExp = buildExp(expNode, reporter)
        namedArgs = namedArgs :+ AST.NamedArg(id = argId, arg = argExp, index = idx)
        idx = idx + 1
      }
      return AST.Exp.InvokeNamed(
        receiverOpt = None(),
        ident = AST.Exp.Ident(id = AST.Id("apply", emptyAttr), attr = emptyResolvedAttr),
        targs = ISZ(),
        args = namedArgs,
        attr = resolvedAttr(node))
    }

    // Positional args
    val expNodes = findChildren(parenArgsNode, "exp")
    val commaExpAnnots = findChildren(parenArgsNode, "commaExpAnnot")

    var args = ISZ[AST.Exp]()
    for (en <- expNodes) {
      args = args :+ buildExp(en, reporter)
    }
    for (cea <- commaExpAnnots) {
      val en = findChild(cea, "exp").get
      args = args :+ buildExp(en, reporter)
    }

    // Check for conditional expression: (cond? thenExp : elseExp)
    // In LL2 PP: (cond? thenExp : elseExp)
    // The annotation will have args with key matching the condition pattern
    // Actually, in LL2 format, conditional is: (exp ? exp : exp)
    // This needs special handling - check if there's annotation for ternary
    // Actually looking at PP: st"(${printExp(o.cond)}? ${printExp(o.thenExp)} : ${printExp(o.elseExp)})"
    // This produces paren(exp QUESTION exp COLON exp) which the parser probably
    // handles differently. Let's just handle tuple/single

    if (args.size == 1) {
      return args(0)
    }
    if (args.size > 1) {
      return AST.Exp.Tuple(args = args, attr = typedAttr(node))
    }

    halt(st"Empty paren expression ${node.toST.render}".render)
  }

  def buildPureBlock(node: ParseTree.Node, reporter: message.Reporter): AST.Exp.StrictPureBlock = {
    // pureBlock: AT LBRACE stmt+ RBRACE
    var stmts = ISZ[AST.Stmt]()
    val stmtNodes = findChildren(node, "stmt")
    for (s <- stmtNodes) {
      stmts = stmts :+ buildStmt(s, reporter)
    }
    val block = AST.Stmt.Block(
      contract = AST.MethodContract.Simple.empty,
      body = AST.Body(stmts = stmts, undecls = ISZ()),
      attr = attr(node))
    return AST.Exp.StrictPureBlock(block = block, attr = typedAttr(node))
  }

  // ─── literal ───────────────────────────────────────────────────────

  def parseStringLit(text: String): String = {
    val sops = ops.StringOps(text)
    if (sops.startsWith("\"") && sops.endsWith("\"")) {
      var inner = sops.substring(1, text.size - 1)
      inner = ops.StringOps(inner).replaceAllLiterally("\\n", "\n")
      inner = ops.StringOps(inner).replaceAllLiterally("\\t", "\t")
      inner = ops.StringOps(inner).replaceAllLiterally("\\r", "\r")
      inner = ops.StringOps(inner).replaceAllLiterally("\\\\", "\\")
      inner = ops.StringOps(inner).replaceAllLiterally("\\\"", "\"")
      inner = ops.StringOps(inner).replaceAllLiterally("\\'", "'")
      return inner
    }
    return text
  }

  def parseMStr(text: String): String = {
    // Multi-line strings: lines separated by # with | margin
    return text
  }

  def buildLit(node: ParseTree.Node, reporter: message.Reporter): AST.Lit = {
    val leaf = firstLeaf(node)
    return buildLitFromLeaf(leaf)
  }

  def buildLitExp(node: ParseTree.Node, reporter: message.Reporter): AST.Exp = {
    val leaf = firstLeaf(node)
    leaf.ruleName.native match {
      case "INT" =>
        val text = leaf.text
        val chars = conversions.String.toCis(text)
        var splitIdx: Z = text.size
        var i: Z = 0
        while (i < chars.size && splitIdx == text.size) {
          val c = chars(i)
          if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')) {
            splitIdx = i
          }
          i = i + 1
        }
        if (splitIdx < text.size) {
          val numPart = ops.StringOps(text).substring(0, splitIdx)
          val suffix = ops.StringOps(text).substring(splitIdx, text.size)
          val cleanNum = ops.StringOps(numPart).replaceAllLiterally("_", "")
          if (suffix == "r") {
            return AST.Exp.LitR(value = R(cleanNum).get, attr = attr(leaf))
          } else {
            return AST.Exp.StringInterpolate(
              prefix = suffix,
              lits = ISZ(AST.Exp.LitString(value = cleanNum, attr = attr(leaf))),
              args = ISZ(),
              attr = typedAttr(leaf))
          }
        }
      case "REAL" =>
        val text = leaf.text
        val sops = ops.StringOps(text)
        if (sops.endsWith("h") || sops.endsWith("H")) {
          val numStr = sops.substring(0, text.size - 1)
          val cleanNum = ops.StringOps(numStr).replaceAllLiterally("_", "")
          return AST.Exp.StringInterpolate(
            prefix = "f16",
            lits = ISZ(AST.Exp.LitString(value = cleanNum, attr = attr(leaf))),
            args = ISZ(),
            attr = typedAttr(leaf))
        }
      case _ =>
    }
    return buildLitFromLeaf(leaf)
  }

  def buildLitFromLeaf(leaf: ParseTree.Leaf): AST.Lit = {
    leaf.ruleName.native match {
      case "TRUE" => return AST.Exp.LitB(value = T, attr = attr(leaf))
      case "FALSE" => return AST.Exp.LitB(value = F, attr = attr(leaf))
      case "INT" =>
        val text = leaf.text
        return parseIntLit(text, leaf)
      case "HEX" =>
        val text = leaf.text
        val sops = ops.StringOps(text)
        val hexStr = sops.substring(2, text.size) // skip 0x
        val cleanHex = ops.StringOps(hexStr).replaceAllLiterally("_", "")
        val value = Z(st"0x$cleanHex".render).get
        return AST.Exp.LitZ(value = value, attr = attr(leaf))
      case "BIN" =>
        val text = leaf.text
        val sops = ops.StringOps(text)
        val binStr = sops.substring(2, text.size) // skip 0b
        val cleanBin = ops.StringOps(binStr).replaceAllLiterally("_", "")
        var value: Z = 0
        for (c <- conversions.String.toCis(cleanBin)) {
          value = value * 2 + (if (c == '1') 1 else 0)
        }
        return AST.Exp.LitZ(value = value, attr = attr(leaf))
      case "REAL" =>
        val text = leaf.text
        val sops = ops.StringOps(text)
        if (sops.endsWith("f") || sops.endsWith("F")) {
          val numStr = sops.substring(0, text.size - 1)
          val cleanNum = ops.StringOps(numStr).replaceAllLiterally("_", "")
          return AST.Exp.LitF32(value = F32(cleanNum).get, attr = attr(leaf))
        } else if (sops.endsWith("d") || sops.endsWith("D")) {
          val numStr = sops.substring(0, text.size - 1)
          val cleanNum = ops.StringOps(numStr).replaceAllLiterally("_", "")
          return AST.Exp.LitF64(value = F64(cleanNum).get, attr = attr(leaf))
        } else {
          val cleanNum = ops.StringOps(text).replaceAllLiterally("_", "")
          return AST.Exp.LitF64(value = F64(cleanNum).get, attr = attr(leaf))
        }
      case "STRING" =>
        val value = parseStringLit(leaf.text)
        return AST.Exp.LitString(value = value, attr = attr(leaf))
      case "CHAR" =>
        val sops = ops.StringOps(leaf.text)
        val inner = sops.substring(1, leaf.text.size - 1)
        val c: C = if (ops.StringOps(inner).startsWith("\\")) {
          val escChar = ops.StringOps(inner).substring(1, inner.size)
          escChar.native match {
            case "n" => '\n'
            case "t" => '\t'
            case "r" => '\r'
            case "\\" => '\\'
            case "'" => '\''
            case "\"" => '"'
            case _ => conversions.String.toCis(inner)(0)
          }
        } else {
          conversions.String.toCis(inner)(0)
        }
        return AST.Exp.LitC(value = c, attr = attr(leaf))
      case "MSTR" =>
        val value = parseMStr(leaf.text)
        return AST.Exp.LitString(value = value, attr = attr(leaf))
      case _ =>
        halt(st"Unknown literal type: ${leaf.ruleName} = ${leaf.text}".render)
    }
  }

  def parseIntLit(text: String, leaf: ParseTree.Leaf): AST.Lit = {
    val sops = ops.StringOps(text)
    val cleanText = ops.StringOps(text).replaceAllLiterally("_", "")
    return AST.Exp.LitZ(value = Z(cleanText).get, attr = attr(leaf))
  }

  // ─── interp ────────────────────────────────────────────────────────

  def buildInterp(node: ParseTree.Node, reporter: message.Reporter): AST.Exp = {
    // interp: SP | SPB sinterp | MSTRP | MSTRPB mstrinterp
    val spOpt = findLeafByRule(node, "SP")
    spOpt match {
      case Some(sp) =>
        // SP: IDF '"' SPI* '"' - e.g., st"hello"
        return parseSP(sp)
      case _ =>
    }

    val spbOpt = findLeafByRule(node, "SPB")
    spbOpt match {
      case Some(spb) =>
        // SPB starts an interpolation: prefix"...$ => then sinterp follows
        return parseSPBInterp(spb, node, reporter)
      case _ =>
    }

    val mstrpOpt = findLeafByRule(node, "MSTRP")
    mstrpOpt match {
      case Some(mstrp) =>
        return parseMSTRP(mstrp)
      case _ =>
    }

    val mstrpbOpt = findLeafByRule(node, "MSTRPB")
    mstrpbOpt match {
      case Some(mstrpb) =>
        return parseMSTRPBInterp(mstrpb, node, reporter)
      case _ =>
    }

    halt(st"Could not build interp from ${node.toST.render}".render)
  }

  def parseSP(leaf: ParseTree.Leaf): AST.Exp.StringInterpolate = {
    // SP: IDF '"' SPI* '"' - full string interpolation with no embedded expressions
    val text = leaf.text
    // Find the prefix (IDF part) and the string content
    val sops = ops.StringOps(text)
    val firstQuote = sops.indexOf('"')
    val prefix = sops.substring(0, firstQuote)
    val content = sops.substring(firstQuote + 1, text.size - 1)
    return AST.Exp.StringInterpolate(
      prefix = prefix,
      lits = ISZ(AST.Exp.LitString(content, attr(leaf))),
      args = ISZ(),
      attr = typedAttr(leaf))
  }

  def parseSPBInterp(spb: ParseTree.Leaf, node: ParseTree.Node, reporter: message.Reporter): AST.Exp.StringInterpolate = {
    // SPB: IDF '"' SPI* '$'
    val text = spb.text
    val sops = ops.StringOps(text)
    val firstQuote = sops.indexOf('"')
    val prefix = sops.substring(0, firstQuote)
    val firstLit = sops.substring(firstQuote + 1, text.size - 1) // before the $

    var lits = ISZ[AST.Exp.LitString](AST.Exp.LitString(firstLit, attr(spb)))
    var args = ISZ[AST.Exp]()

    // Collect sinterp children: sinterp: exp ( SPM sinterp | SPE )
    collectSinterp(node, lits, args, reporter) match {
      case (newLits, newArgs) =>
        lits = newLits
        args = newArgs
    }

    return AST.Exp.StringInterpolate(prefix = prefix, lits = lits, args = args, attr = typedAttr(node))
  }

  def collectSinterp(node: ParseTree.Node, lits: ISZ[AST.Exp.LitString], args: ISZ[AST.Exp], reporter: message.Reporter): (ISZ[AST.Exp.LitString], ISZ[AST.Exp]) = {
    var rLits = lits
    var rArgs = args
    val sinterpOpt = findChild(node, "sinterp")
    sinterpOpt match {
      case Some(sinterp) =>
        // sinterp: exp ( SPM sinterp | SPE )
        val expNodes = findChildren(sinterp, "exp")
        if (expNodes.nonEmpty) {
          rArgs = rArgs :+ buildExp(expNodes(0), reporter)
        }
        val spmOpt = findLeafByRule(sinterp, "SPM")
        spmOpt match {
          case Some(spm) =>
            val spmText = spm.text
            val spmSops = ops.StringOps(spmText)
            // SPM: '$' SPI* '$'
            val litContent = spmSops.substring(1, spmText.size - 1)
            rLits = rLits :+ AST.Exp.LitString(litContent, attr(spm))
            return collectSinterp(sinterp, rLits, rArgs, reporter)
          case _ =>
        }
        val speOpt = findLeafByRule(sinterp, "SPE")
        speOpt match {
          case Some(spe) =>
            val speText = spe.text
            val speSops = ops.StringOps(speText)
            // SPE: '$' SPI* '"'
            val litContent = speSops.substring(1, speText.size - 1)
            rLits = rLits :+ AST.Exp.LitString(litContent, attr(spe))
          case _ =>
        }
      case _ =>
    }
    return (rLits, rArgs)
  }

  def parseMSTRP(leaf: ParseTree.Leaf): AST.Exp.StringInterpolate = {
    // MSTRP: IDF ( '#' MSTRF WSF? )* '#' MSTRF - no interpolation
    val text = leaf.text
    // Parse prefix and content
    val sops = ops.StringOps(text)
    // Find first '#'
    val firstHash = sops.indexOf('#')
    val prefix = sops.substring(0, firstHash)
    val content = sops.substring(firstHash, text.size)
    // Convert multi-line string content
    return AST.Exp.StringInterpolate(
      prefix = prefix,
      lits = ISZ(AST.Exp.LitString(content, attr(leaf))),
      args = ISZ(),
      attr = typedAttr(leaf))
  }

  def parseMSTRPBInterp(mstrpb: ParseTree.Leaf, node: ParseTree.Node, reporter: message.Reporter): AST.Exp.StringInterpolate = {
    val text = mstrpb.text
    val sops = ops.StringOps(text)
    val firstHash = sops.indexOf('#')
    val prefix = sops.substring(0, firstHash)
    val firstLit = sops.substring(firstHash, text.size - 1) // before the $

    var lits = ISZ[AST.Exp.LitString](AST.Exp.LitString(firstLit, attr(mstrpb)))
    var args = ISZ[AST.Exp]()

    val mstrinterpOpt = findChild(node, "mstrinterp")
    mstrinterpOpt match {
      case Some(mi) =>
        collectMstrinterp(mi, lits, args, reporter) match {
          case (newLits, newArgs) =>
            lits = newLits
            args = newArgs
        }
      case _ =>
    }

    return AST.Exp.StringInterpolate(prefix = prefix, lits = lits, args = args, attr = typedAttr(node))
  }

  def collectMstrinterp(node: ParseTree.Node, lits: ISZ[AST.Exp.LitString], args: ISZ[AST.Exp], reporter: message.Reporter): (ISZ[AST.Exp.LitString], ISZ[AST.Exp]) = {
    var rLits = lits
    var rArgs = args
    // mstrinterp: exp ( MSTRPM mstrinterp | MSTRPE )
    val expNodes = findChildren(node, "exp")
    if (expNodes.nonEmpty) {
      rArgs = rArgs :+ buildExp(expNodes(0), reporter)
    }
    val mstrpmOpt = findLeafByRule(node, "MSTRPM")
    mstrpmOpt match {
      case Some(mstrpm) =>
        val mText = mstrpm.text
        val mSops = ops.StringOps(mText)
        // MSTRPM: '$' MSTRF WSF? ( '#' MSTRF WSF? )* '#' MSTRI '$'
        val litContent = mSops.substring(1, mText.size - 1)
        rLits = rLits :+ AST.Exp.LitString(litContent, attr(mstrpm))
        val nextInterp = findChild(node, "mstrinterp")
        nextInterp match {
          case Some(ni) => return collectMstrinterp(ni, rLits, rArgs, reporter)
          case _ =>
        }
      case _ =>
    }
    val mstrpeOpt = findLeafByRule(node, "MSTRPE")
    mstrpeOpt match {
      case Some(mstrpe) =>
        val mText = mstrpe.text
        val mSops = ops.StringOps(mText)
        // MSTRPE: '$' MSTRF?
        val litContent = mSops.substring(1, mText.size)
        rLits = rLits :+ AST.Exp.LitString(litContent, attr(mstrpe))
      case _ =>
    }
    return (rLits, rArgs)
  }

  // ─── forExp / defAnon / quant ──────────────────────────────────────

  def buildForExp(node: ParseTree.Node, reporter: message.Reporter): AST.Exp.ForYield = {
    // forExp: YIELD annot? forRange commaForRange* ARROW annot? rhs
    val forRanges = findChildren(node, "forRange")
    val commaForRanges = findChildren(node, "commaForRange")
    val rhsNode = findChild(node, "rhs").get

    var enumGens = ISZ[AST.EnumGen.For]()
    for (fr <- forRanges) {
      enumGens = enumGens :+ buildForRange(fr, reporter)
    }
    for (cfr <- commaForRanges) {
      val fr = findChild(cfr, "forRange").get
      enumGens = enumGens :+ buildForRange(fr, reporter)
    }

    val exp = buildRhs(rhsNode, reporter)
    return AST.Exp.ForYield(enumGens = enumGens, exp = exp, attr = typedAttr(node))
  }

  def buildDefAnon(node: ParseTree.Node, reporter: message.Reporter): AST.Exp.Fun = {
    // defAnon: DEF mod* defParams colonType? DOT annot? rhs
    val defParamsOpt = findChild(node, "defParams")
    val colonTypeOpt = findChild(node, "colonType")
    val rhsNode = findChild(node, "rhs").get

    var params = ISZ[AST.Exp.Fun.Param]()
    defParamsOpt match {
      case Some(dp) => params = buildDefAnonParams(dp, reporter)
      case _ =>
    }

    val exp = buildRhsAsAssignExp(rhsNode, reporter)
    return AST.Exp.Fun(context = ISZ(), params = params, exp = exp, attr = typedAttr(node))
  }

  def buildDefAnonParams(node: ParseTree.Node, reporter: message.Reporter): ISZ[AST.Exp.Fun.Param] = {
    // defParams: LPAREN ( defParam defParamSuffix? COMMA? )? RPAREN
    var r = ISZ[AST.Exp.Fun.Param]()
    findChild(node, "defParam") match {
      case Some(firstParam) =>
        r = r :+ buildDefAnonParam(firstParam, reporter)
        val suffixOpt = findChild(node, "defParamSuffix")
        suffixOpt match {
          case Some(suffix) =>
            val nextParam = findChild(suffix, "defParam")
            nextParam match {
              case Some(np) =>
                r = r :+ buildDefAnonParam(np, reporter)
                val innerSuffix = findChild(suffix, "defParamSuffix")
                innerSuffix match {
                  case Some(is) => r = r ++ buildDefParamSuffixChain(is, reporter)
                  case _ =>
                }
              case _ =>
            }
          case _ =>
        }
      case _ =>
    }
    return r
  }

  def buildDefParamSuffixChain(node: ParseTree.Node, reporter: message.Reporter): ISZ[AST.Exp.Fun.Param] = {
    var r = ISZ[AST.Exp.Fun.Param]()
    val nextParam = findChild(node, "defParam")
    nextParam match {
      case Some(np) =>
        r = r :+ buildDefAnonParam(np, reporter)
        val innerSuffix = findChild(node, "defParamSuffix")
        innerSuffix match {
          case Some(is) => r = r ++ buildDefParamSuffixChain(is, reporter)
          case _ =>
        }
      case _ =>
    }
    return r
  }

  def buildDefAnonParam(node: ParseTree.Node, reporter: message.Reporter): AST.Exp.Fun.Param = {
    // defParam: mod* ID COLON type
    val id = mkId(idText(node), node)
    val typeNodes = findChildren(node, "type")
    val tipeOpt: Option[AST.Type] = if (typeNodes.nonEmpty) {
      Some(buildType(typeNodes(0), reporter))
    } else {
      None()
    }
    return AST.Exp.Fun.Param(idOpt = Some(id), tipeOpt = tipeOpt, typedOpt = None())
  }

  def buildQuant(node: ParseTree.Node, reporter: message.Reporter): AST.Exp = {
    // quant: ( ALL | SOME | SYMBOL ) quantRange+ ARROW annot? rhs
    val isForall: B = findLeafByRule(node, "ALL") match {
      case Some(_) => T
      case _ =>
        findLeafByRule(node, "SYMBOL") match {
          case Some(s) => s.text == "\\forall" || s.text == "∀"
          case _ => F
        }
    }

    val quantRanges = findChildren(node, "quantRange")
    val rhsNode = findChild(node, "rhs").get

    if (quantRanges.size == 1) {
      return buildQuantRange(isForall, quantRanges(0), rhsNode, node, reporter)
    }

    // Multiple quantRange - each becomes a separate param
    var params = ISZ[AST.Exp.Fun.Param]()
    for (qr <- quantRanges) {
      val qrId = mkId(idText(qr), qr)
      val typeExpOpt = findChildren(qr, "exp")
      val tipeOpt: Option[AST.Type] = if (typeExpOpt.nonEmpty) {
        // The exp is actually a type reference
        val typeExp = buildExp(typeExpOpt(0), reporter)
        typeExp match {
          case typeExp: AST.Exp.Ident =>
            Some(AST.Type.Named(
              name = mkName(ISZ(typeExp.id), typeExpOpt(0)),
              typeArgs = ISZ(),
              attr = typedAttr(typeExpOpt(0))))
          case typeExp: AST.Exp.Select =>
            val ids: ISZ[AST.Id] = typeExp.receiverOpt match {
              case Some(rcv) =>
                rcv match {
                  case rcv: AST.Exp.Ident => ISZ(rcv.id, typeExp.id)
                  case _ => ISZ(typeExp.id)
                }
              case _ => ISZ(typeExp.id)
            }
            Some(AST.Type.Named(
              name = mkName(ids, typeExpOpt(0)),
              typeArgs = typeExp.targs,
              attr = typedAttr(typeExpOpt(0))))
          case _ => None()
        }
      } else {
        None()
      }
      params = params :+ AST.Exp.Fun.Param(idOpt = Some(qrId), tipeOpt = tipeOpt, typedOpt = None())
    }

    val body = buildRhsAsAssignExp(rhsNode, reporter)
    val fun = AST.Exp.Fun(context = ISZ(), params = params, exp = body, attr = typedAttr(node))
    return AST.Exp.QuantType(isForall = isForall, fun = fun, attr = attr(node))
  }

  def buildQuantRange(isForall: B, qr: ParseTree.Node, rhsNode: ParseTree.Node, posTree: ParseTree, reporter: message.Reporter): AST.Exp = {
    // quantRange: idComma* ID annot? COLON annot? exp quantRangeSuffix?
    val qrId = mkId(idText(qr), qr)
    val expNodes = findChildren(qr, "exp")
    val rangeSuffix = findChild(qr, "quantRangeSuffix")

    val body = buildRhsAsAssignExp(rhsNode, reporter)
    val param = AST.Exp.Fun.Param(idOpt = Some(qrId), tipeOpt = None(), typedOpt = None())
    val fun = AST.Exp.Fun(context = ISZ(), params = ISZ(param), exp = body, attr = typedAttr(posTree))

    rangeSuffix match {
      case Some(rs) =>
        // quantRangeSuffix: ( TO | UNTIL ) annot? exp
        val isInclusive = findLeafByRule(rs, "TO").nonEmpty
        val lo = buildExp(expNodes(0), reporter)
        val hiExpNodes = findChildren(rs, "exp")
        val hi = buildExp(hiExpNodes(0), reporter)
        return AST.Exp.QuantRange(
          isForall = isForall,
          lo = lo,
          hi = hi,
          hiExact = isInclusive,
          fun = fun,
          attr = resolvedAttr(posTree))
      case _ =>
        if (expNodes.nonEmpty) {
          val typeOrSeqExp = buildExp(expNodes(0), reporter)
          // Determine if it's a type (QuantType) or sequence (QuantEach)
          typeOrSeqExp match {
            case typeOrSeqExp: AST.Exp.Ident =>
              // Could be either type or seq
              val tipe = AST.Type.Named(
                name = mkName(ISZ(typeOrSeqExp.id), expNodes(0)),
                typeArgs = ISZ(),
                attr = typedAttr(expNodes(0)))
              val paramWithType = AST.Exp.Fun.Param(idOpt = Some(qrId), tipeOpt = Some(tipe), typedOpt = None())
              val funWithType = AST.Exp.Fun(context = ISZ(), params = ISZ(paramWithType), exp = body, attr = typedAttr(posTree))
              return AST.Exp.QuantType(isForall = isForall, fun = funWithType, attr = attr(posTree))
            case typeOrSeqExp: AST.Exp.Select =>
              // Multi-component name => could be type like ISZ[Z]
              val ids: ISZ[AST.Id] = typeOrSeqExp.receiverOpt match {
                case Some(rcv) =>
                  rcv match {
                    case rcv: AST.Exp.Ident => ISZ(rcv.id, typeOrSeqExp.id)
                    case _ => ISZ(typeOrSeqExp.id)
                  }
                case _ => ISZ(typeOrSeqExp.id)
              }
              if (typeOrSeqExp.targs.nonEmpty) {
                val tipe = AST.Type.Named(
                  name = mkName(ids, expNodes(0)),
                  typeArgs = typeOrSeqExp.targs,
                  attr = typedAttr(expNodes(0)))
                val paramWithType = AST.Exp.Fun.Param(idOpt = Some(qrId), tipeOpt = Some(tipe), typedOpt = None())
                val funWithType = AST.Exp.Fun(context = ISZ(), params = ISZ(paramWithType), exp = body, attr = typedAttr(posTree))
                return AST.Exp.QuantType(isForall = isForall, fun = funWithType, attr = attr(posTree))
              }
              return AST.Exp.QuantEach(isForall = isForall, seq = typeOrSeqExp, fun = fun, attr = resolvedAttr(posTree))
            case _ =>
              return AST.Exp.QuantEach(isForall = isForall, seq = typeOrSeqExp, fun = fun, attr = resolvedAttr(posTree))
          }
        }
        halt(st"Could not build quant from ${qr.toST.render}".render)
    }
  }

  // ─── statements ────────────────────────────────────────────────────

  def buildStmt(node: ParseTree.Node, reporter: message.Reporter): AST.Stmt = {
    // stmt: expOrAssignStmt | varPattern | ifStmt | whileStmt | forStmt | deduceStmt | matchStmt | defStmt | assertumeStmt
    for (c <- node.children) {
      c match {
        case c: ParseTree.Node =>
          c.ruleName.native match {
            case "expOrAssignStmt" => return buildExpOrAssignStmt(c, reporter)
            case "varPattern" => return buildVarPattern(c, reporter)
            case "ifStmt" => return buildIfStmt(c, reporter)
            case "whileStmt" => return buildWhileStmt(c, reporter)
            case "forStmt" => return buildForStmt(c, reporter)
            case "deduceStmt" => return buildDeduceStmt(c, reporter)
            case "matchStmt" => return buildMatchStmt(c, reporter)
            case "defStmt" => return buildDefStmt(c, reporter)
            case "assertumeStmt" => return buildAssertumeStmt(c, reporter)
            case _ =>
          }
        case _ =>
      }
    }
    halt(st"Could not build stmt from ${node.toST.render}".render)
  }

  def buildAssertumeStmt(node: ParseTree.Node, reporter: message.Reporter): AST.Stmt = {
    // assertumeStmt: ( ASSERT | ASSUME ) exp commaExp?
    val isAssert = findLeafByRule(node, "ASSERT").nonEmpty
    val expNodes = findChildren(node, "exp")
    val commaExpOpt = findChild(node, "commaExp")
    var args = ISZ[AST.Exp]()
    for (en <- expNodes) {
      args = args :+ buildExp(en, reporter)
    }
    commaExpOpt match {
      case Some(ce) =>
        val en = findChild(ce, "exp").get
        args = args :+ buildExp(en, reporter)
      case _ =>
    }
    val name: String = if (isAssert) "assert" else "assume"
    val ident = AST.Exp.Ident(id = AST.Id(name, emptyAttr), attr = emptyResolvedAttr)
    val invoke = AST.Exp.Invoke(
      receiverOpt = None(),
      ident = ident,
      targs = ISZ(),
      args = args,
      attr = resolvedAttr(node))
    return AST.Stmt.Expr(exp = invoke, attr = typedAttr(node))
  }

  def buildExpOrAssignStmt(node: ParseTree.Node, reporter: message.Reporter): AST.Stmt = {
    // expOrAssignStmt: idStmt | expStmt | doStmt
    val idStmtOpt = findChild(node, "idStmt")
    idStmtOpt match {
      case Some(ids) => return buildIdStmt(ids, reporter)
      case _ =>
    }
    val expStmtOpt = findChild(node, "expStmt")
    expStmtOpt match {
      case Some(es) => return buildExpStmt(es, reporter)
      case _ =>
    }
    val doStmtOpt = findChild(node, "doStmt")
    doStmtOpt match {
      case Some(ds) => return buildDoStmt(ds, reporter)
      case _ =>
    }
    halt(st"Could not build expOrAssignStmt from ${node.toST.render}".render)
  }

  def buildIdStmt(node: ParseTree.Node, reporter: message.Reporter): AST.Stmt = {
    // idStmt: ID idStmtSuffix?
    val idLeaf = findLeafByRule(node, "ID").get
    val suffixOpt = findChild(node, "idStmtSuffix")

    suffixOpt match {
      case Some(suffix) =>
        // idStmtSuffix: annot | assignSuffix | labelSuffix
        val annotOpt = findChild(suffix, "annot")
        val assignOpt = findChild(suffix, "assignSuffix")
        val labelOpt = findChild(suffix, "labelSuffix")
        assignOpt match {
          case Some(as) =>
            // Assignment: ID = rhs
            val lhs = AST.Exp.Ident(id = mkId(idLeaf.text, idLeaf), attr = resolvedAttr(idLeaf))
            val rhs = buildAssignSuffix(as, reporter)
            return AST.Stmt.Assign(lhs = lhs, rhs = rhs, attr = attr(node))
          case _ =>
        }
        annotOpt match {
          case Some(an) =>
            // ID with annotation - could be @induct or @rw etc.
            val annotArgs = findChildren(an, "annotArg")
            for (aa <- annotArgs) {
              val key = firstLeaf(aa).text
              key.native match {
                case "induct" =>
                  val exp = AST.Exp.Ident(id = mkId(idLeaf.text, idLeaf), attr = resolvedAttr(idLeaf))
                  return AST.Stmt.Induct(exp = exp, context = ISZ(), locals = ISZ(), attr = attr(node))
                case "rw" =>
                  // val @rw ID = exp (but this comes from varDefn, not idStmt)
                  return AST.Stmt.Expr(
                    exp = AST.Exp.Ident(id = mkId(idLeaf.text, idLeaf), attr = resolvedAttr(idLeaf)),
                    attr = typedAttr(node))
                case _ =>
              }
            }
            return AST.Stmt.Expr(
              exp = AST.Exp.Ident(id = mkId(idLeaf.text, idLeaf), attr = resolvedAttr(idLeaf)),
              attr = typedAttr(node))
          case _ =>
        }
        labelOpt match {
          case Some(_) =>
            // Label: ID : annot?
            return AST.Stmt.Expr(
              exp = AST.Exp.Ident(id = mkId(idLeaf.text, idLeaf), attr = resolvedAttr(idLeaf)),
              attr = typedAttr(node))
          case _ =>
        }
      case _ =>
    }
    // Just an ID expression
    return AST.Stmt.Expr(
      exp = AST.Exp.Ident(id = mkId(idLeaf.text, idLeaf), attr = resolvedAttr(idLeaf)),
      attr = typedAttr(node))
  }

  def buildExpStmt(node: ParseTree.Node, reporter: message.Reporter): AST.Stmt = {
    // expStmt: exp0 access+ annot? assignSuffix?
    val exp0Node = findChild(node, "exp0").get
    var result = buildExp0(exp0Node, reporter)
    val accesses = findChildren(node, "access")
    for (acc <- accesses) {
      result = buildAccess(result, acc, reporter)
    }
    val assignOpt = findChild(node, "assignSuffix")
    assignOpt match {
      case Some(as) =>
        val rhs = buildAssignSuffix(as, reporter)
        val lhs: AST.Exp = result match {
          case invoke: AST.Exp.Invoke if invoke.receiverOpt.isEmpty =>
            invoke(
              receiverOpt = Some(invoke.ident),
              ident = AST.Exp.Ident(id = AST.Id(value = "apply", attr = emptyAttr), attr = invoke.attr))
          case invoke: AST.Exp.InvokeNamed if invoke.receiverOpt.isEmpty =>
            invoke(
              receiverOpt = Some(invoke.ident),
              ident = AST.Exp.Ident(id = AST.Id(value = "apply", attr = emptyAttr), attr = invoke.attr))
          case _ => result
        }
        return AST.Stmt.Assign(lhs = lhs, rhs = rhs, attr = attr(node))
      case _ =>
    }
    return AST.Stmt.Expr(exp = result, attr = typedAttr(node))
  }

  def buildDoStmt(node: ParseTree.Node, reporter: message.Reporter): AST.Stmt = {
    // doStmt: DO annot? ( exp | mod* block )
    val annotOpt = findChild(node, "annot")
    val blockOpt = findChild(node, "block")
    val expOpt = findChild(node, "exp")

    // Check for @spec annotation
    val isSpec: B = annotOpt match {
      case Some(an) =>
        val annotArgs = findChildren(an, "annotArg")
        var found = F
        for (aa <- annotArgs) {
          if (firstLeaf(aa).text == "spec") {
            found = T
          }
        }
        found
      case _ => F
    }

    blockOpt match {
      case Some(blk) =>
        val block = buildBlock(blk, reporter)
        if (isSpec) {
          return AST.Stmt.SpecBlock(block = block)
        }
        return block
      case _ =>
    }

    expOpt match {
      case Some(expNode) =>
        val exp = buildExp(expNode, reporter)
        return AST.Stmt.Expr(exp = exp, attr = typedAttr(node))
      case _ =>
    }
    halt(st"Could not build doStmt from ${node.toST.render}".render)
  }

  def buildAssignSuffix(node: ParseTree.Node, reporter: message.Reporter): AST.AssignExp = {
    // assignSuffix: ASSIGN annot? rhs
    val rhsNode = findChild(node, "rhs").get
    return buildRhsAsAssignExp(rhsNode, reporter)
  }

  def buildRhs(node: ParseTree.Node, reporter: message.Reporter): AST.Exp = {
    // rhs: exp | block | ifStmt | matchStmt
    val expOpt = findChild(node, "exp")
    expOpt match {
      case Some(e) => return buildExp(e, reporter)
      case _ =>
    }
    // For block/if/match, wrap in appropriate expression
    val blockOpt = findChild(node, "block")
    blockOpt match {
      case Some(blk) =>
        val block = buildBlock(blk, reporter)
        halt(st"Block as expression not directly supported; use buildRhsAsAssignExp".render)
      case _ =>
    }
    halt(st"Could not build rhs as exp from ${node.toST.render}".render)
  }

  def buildRhsAsAssignExp(node: ParseTree.Node, reporter: message.Reporter): AST.AssignExp = {
    // rhs: exp | block | ifStmt | matchStmt
    val expOpt = findChild(node, "exp")
    expOpt match {
      case Some(e) =>
        val exp = buildExp(e, reporter)
        return AST.Stmt.Expr(exp = exp, attr = typedAttr(e))
      case _ =>
    }
    val blockOpt = findChild(node, "block")
    blockOpt match {
      case Some(blk) => return buildBlock(blk, reporter)
      case _ =>
    }
    val ifOpt = findChild(node, "ifStmt")
    ifOpt match {
      case Some(is) => return buildIfStmt(is, reporter)
      case _ =>
    }
    val matchOpt = findChild(node, "matchStmt")
    matchOpt match {
      case Some(ms) => return buildMatchStmt(ms, reporter)
      case _ =>
    }
    halt(st"Could not build rhs from ${node.toST.render}".render)
  }

  // ─── varDefn ───────────────────────────────────────────────────────

  def buildVarDefn(node: ParseTree.Node, reporter: message.Reporter): AST.Stmt = {
    // varDefn: VAR mod* ID colonType? annot? assignSuffix?
    val varLeaf = findLeafByRule(node, "VAR").get
    val isVal = varLeaf.text == "val"
    val mods = buildMods(findChildren(node, "mod"))
    val isSpec = hasMod(mods, "spec")
    val isRw = hasMod(mods, "rw")
    val id = mkId(idText(node), node)

    val colonTypeOpt = findChild(node, "colonType")
    val tipeOpt: Option[AST.Type] = colonTypeOpt match {
      case Some(ct) =>
        val typeNode = findChild(ct, "type").get
        Some(buildType(typeNode, reporter))
      case _ => None()
    }

    val assignSuffixOpt = findChild(node, "assignSuffix")

    if (isSpec && tipeOpt.nonEmpty && assignSuffixOpt.isEmpty) {
      return AST.Stmt.SpecVar(
        isVal = isVal,
        id = id,
        tipe = tipeOpt.get,
        attr = resolvedAttr(node))
    }

    if (isRw) {
      if (assignSuffixOpt.isEmpty) {
        halt("Expected init for @rw var")
      }
      val init = buildRhs(findChild(assignSuffixOpt.get, "rhs").get, reporter)
      return AST.Stmt.RsVal(id = id, init = init, attr = resolvedAttr(node))
    }

    val initOpt: Option[AST.AssignExp] = assignSuffixOpt match {
      case Some(as) => Some(buildAssignSuffix(as, reporter))
      case _ => None()
    }

    return AST.Stmt.Var(
      isSpec = isSpec,
      isVal = isVal,
      id = id,
      tipeOpt = tipeOpt,
      initOpt = initOpt,
      attr = resolvedAttr(node))
  }

  // ─── varPattern ────────────────────────────────────────────────────

  def buildVarPattern(node: ParseTree.Node, reporter: message.Reporter): AST.Stmt.VarPattern = {
    // varPattern: VAR pattern0 colonType1? ASSIGN annot? rhs
    val varLeaf = findLeafByRule(node, "VAR").get
    val isVal = varLeaf.text == "val"
    val pattern = buildPattern0(findChild(node, "pattern0").get, reporter)
    val colonTypeOpt = findChild(node, "colonType1")
    val tipeOpt: Option[AST.Type] = colonTypeOpt match {
      case Some(ct) =>
        val type1Node = findChild(ct, "type1").get
        Some(buildType1(type1Node, reporter))
      case _ => None()
    }
    val rhsNode = findChild(node, "rhs").get
    val init = buildRhsAsAssignExp(rhsNode, reporter)
    return AST.Stmt.VarPattern(
      isSpec = F,
      isVal = isVal,
      pattern = pattern,
      tipeOpt = tipeOpt,
      init = init,
      attr = attr(node))
  }

  // ─── defDefn / defStmt ─────────────────────────────────────────────

  def buildDefDefn(node: ParseTree.Node, reporter: message.Reporter): AST.Stmt = {
    return buildDefCommon(node, reporter)
  }

  def buildDefStmt(node: ParseTree.Node, reporter: message.Reporter): AST.Stmt = {
    return buildDefCommon(node, reporter)
  }

  def buildDefCommon(node: ParseTree.Node, reporter: message.Reporter): AST.Stmt = {
    // defDefn/defStmt: DEF mod* defId typeParams? defParams? defnTypeSuffix? assignSuffix?
    val mods = buildMods(findChildren(node, "mod"))
    val defIdNode = findChild(node, "defId").get
    val idLeaf = firstLeaf(defIdNode)
    val id = mkId(idLeaf.text, idLeaf)
    val typeParams = buildTypeParams(findChild(node, "typeParams"), reporter)
    val defParamsOpt = findChild(node, "defParams")

    val hasParams = defParamsOpt.nonEmpty
    val params: ISZ[AST.Param] = defParamsOpt match {
      case Some(dp) => buildDefParams(dp, reporter)
      case _ => ISZ()
    }

    val defnTypeSuffixOpt = findChild(node, "defnTypeSuffix")
    val returnTypeOpt: Option[AST.Type] = defnTypeSuffixOpt match {
      case Some(dts) =>
        val typeNode = findChild(dts, "type").get
        Some(buildType(typeNode, reporter))
      case _ => None()
    }

    // Get annotation from defnTypeSuffix if present
    val contractAnnotOpt: Option[ParseTree.Node] = defnTypeSuffixOpt match {
      case Some(dts) => findChild(dts, "annot")
      case _ => None()
    }

    val assignSuffixOpt = findChild(node, "assignSuffix")

    // Determine what kind of def based on mods
    if (hasMod(mods, "fact")) {
      return buildFact(id, typeParams, hasParams, params, assignSuffixOpt, node, reporter)
    }
    if (hasMod(mods, "theorem") || hasMod(mods, "lemma")) {
      val isLemma = hasMod(mods, "lemma")
      return buildTheorem(isLemma, id, typeParams, hasParams, params, assignSuffixOpt, node, reporter)
    }
    if (hasMod(mods, "inv")) {
      return buildInv(id, assignSuffixOpt, node, reporter)
    }
    if (hasMod(mods, "just")) {
      val justMod = findMod(mods, "just").get
      val etaOpt: Option[AST.Exp.LitString] = if (justMod.args.nonEmpty) {
        // Extract the string from the args
        var found: Option[AST.Exp.LitString] = None()
        for (pair <- justMod.args) {
          val argsChildren = pair._2
          for (c <- argsChildren) {
            c match {
              case c: ParseTree.Leaf if c.ruleName == "STRING" =>
                found = Some(AST.Exp.LitString(parseStringLit(c.text), attr(c)))
              case c: ParseTree.Node =>
                val e = buildExp(c, reporter)
                e match {
                  case e: AST.Exp.LitString => found = Some(e)
                  case _ =>
                }
              case _ =>
            }
          }
        }
        found
      } else {
        None()
      }
      val returnType: AST.Type = returnTypeOpt match {
        case Some(rt) => rt
        case _ => AST.Type.Named(
          name = mkName(ISZ(AST.Id("Unit", emptyAttr)), node),
          typeArgs = ISZ(),
          attr = emptyTypedAttr)
      }
      val sig = AST.MethodSig(
        purity = AST.Purity.Impure,
        annotations = ISZ(),
        id = id,
        typeParams = typeParams,
        hasParams = hasParams,
        params = params,
        returnType = returnType)
      return AST.Stmt.JustMethod(etaOpt = etaOpt, sig = sig, attr = resolvedAttr(node))
    }
    if (hasMod(mods, "spec")) {
      val returnType: AST.Type = returnTypeOpt match {
        case Some(rt) => rt
        case _ => AST.Type.Named(
          name = mkName(ISZ(AST.Id("Unit", emptyAttr)), node),
          typeArgs = ISZ(),
          attr = emptyTypedAttr)
      }
      val sig = AST.MethodSig(
        purity = AST.Purity.Impure,
        annotations = ISZ(),
        id = id,
        typeParams = typeParams,
        hasParams = hasParams,
        params = params,
        returnType = returnType)
      return AST.Stmt.SpecMethod(sig = sig, attr = resolvedAttr(node))
    }

    // Regular method
    val purity: AST.Purity.Type = if (hasMod(mods, "pure")) {
      AST.Purity.Pure
    } else if (hasMod(mods, "strictpure")) {
      AST.Purity.StrictPure
    } else if (hasMod(mods, "memoize")) {
      AST.Purity.Memoize
    } else if (hasMod(mods, "abs")) {
      AST.Purity.Abs
    } else {
      AST.Purity.Impure
    }

    val returnType: AST.Type = returnTypeOpt match {
      case Some(rt) => rt
      case _ => AST.Type.Named(
        name = mkName(ISZ(AST.Id("Unit", emptyAttr)), node),
        typeArgs = ISZ(),
        attr = emptyTypedAttr)
    }

    val contract = buildMethodContract(contractAnnotOpt, reporter)

    val sig = AST.MethodSig(
      purity = purity,
      annotations = ISZ(),
      id = id,
      typeParams = typeParams,
      hasParams = hasParams,
      params = params,
      returnType = returnType)

    val bodyOpt: Option[AST.Body] = assignSuffixOpt match {
      case Some(as) =>
        val rhsNode = findChild(as, "rhs").get
        val blockOpt = findChild(rhsNode, "block")
        blockOpt match {
          case Some(blk) =>
            val block = buildBlock(blk, reporter)
            if (purity == AST.Purity.StrictPure || purity == AST.Purity.Abs) {
              val expAttr = attr(rhsNode)
              val rId = AST.Id("_r_", expAttr)
              val varStmt = AST.Stmt.Var(
                isSpec = F,
                isVal = T,
                id = rId,
                tipeOpt = Some(returnType),
                initOpt = Some(block),
                attr = AST.ResolvedAttr(expAttr.posOpt, None(), None()))
              val ident = AST.Exp.Ident(id = rId, attr = AST.ResolvedAttr(expAttr.posOpt, None(), None()))
              val retStmt = AST.Stmt.Return(expOpt = Some(ident), attr = typedAttr(rhsNode))
              Some(AST.Body(stmts = ISZ(varStmt, retStmt), undecls = ISZ()))
            } else {
              Some(block.body)
            }
          case _ =>
            val exp = buildRhsAsAssignExp(rhsNode, reporter)
            if (purity == AST.Purity.StrictPure || purity == AST.Purity.Abs) {
              val expAttr = attr(rhsNode)
              val rId = AST.Id("_r_", expAttr)
              val varStmt = AST.Stmt.Var(
                isSpec = F,
                isVal = T,
                id = rId,
                tipeOpt = Some(returnType),
                initOpt = Some(exp),
                attr = AST.ResolvedAttr(expAttr.posOpt, None(), None()))
              val ident = AST.Exp.Ident(id = rId, attr = AST.ResolvedAttr(expAttr.posOpt, None(), None()))
              val retStmt = AST.Stmt.Return(expOpt = Some(ident), attr = typedAttr(rhsNode))
              Some(AST.Body(stmts = ISZ(varStmt, retStmt), undecls = ISZ()))
            } else {
              Some(AST.Body(stmts = ISZ(exp.asStmt), undecls = ISZ()))
            }
        }
      case _ => None()
    }

    return AST.Stmt.Method(
      typeChecked = F,
      purity = purity,
      modifiers = ISZ(),
      sig = sig,
      mcontract = contract,
      bodyOpt = bodyOpt,
      attr = resolvedAttr(node))
  }

  def buildExtMethod(node: ParseTree.Node, reporter: message.Reporter): AST.Stmt.ExtMethod = {
    // Same structure as defDefn but produces ExtMethod
    val mods = buildMods(findChildren(node, "mod"))
    val defIdNode = findChild(node, "defId").get
    val idLeaf = firstLeaf(defIdNode)
    val id = mkId(idLeaf.text, idLeaf)
    val typeParams = buildTypeParams(findChild(node, "typeParams"), reporter)
    val defParamsOpt = findChild(node, "defParams")
    val hasParams = defParamsOpt.nonEmpty
    val params: ISZ[AST.Param] = defParamsOpt match {
      case Some(dp) => buildDefParams(dp, reporter)
      case _ => ISZ()
    }
    val defnTypeSuffixOpt = findChild(node, "defnTypeSuffix")
    val returnType: AST.Type = defnTypeSuffixOpt match {
      case Some(dts) =>
        val typeNode = findChild(dts, "type").get
        buildType(typeNode, reporter)
      case _ => AST.Type.Named(
        name = mkName(ISZ(AST.Id("Unit", emptyAttr)), node),
        typeArgs = ISZ(),
        attr = emptyTypedAttr)
    }
    val contractAnnotOpt: Option[ParseTree.Node] = defnTypeSuffixOpt match {
      case Some(dts) => findChild(dts, "annot")
      case _ => None()
    }
    val contract = buildMethodContract(contractAnnotOpt, reporter)
    val sig = AST.MethodSig(
      purity = AST.Purity.Impure,
      annotations = ISZ(),
      id = id,
      typeParams = typeParams,
      hasParams = hasParams,
      params = params,
      returnType = returnType)
    return AST.Stmt.ExtMethod(isPure = F, sig = sig, contract = contract, attr = resolvedAttr(node))
  }

  def buildDefParams(node: ParseTree.Node, reporter: message.Reporter): ISZ[AST.Param] = {
    // defParams: LPAREN ( defParam defParamSuffix? COMMA? )? RPAREN
    var r = ISZ[AST.Param]()
    findChild(node, "defParam") match {
      case Some(firstParam) =>
        r = r :+ buildDefParam(firstParam, reporter)
        val suffixOpt = findChild(node, "defParamSuffix")
        suffixOpt match {
          case Some(suffix) => r = r ++ buildDefParamSuffixChainP(suffix, reporter)
          case _ =>
        }
      case _ =>
    }
    return r
  }

  def buildDefParamSuffixChainP(node: ParseTree.Node, reporter: message.Reporter): ISZ[AST.Param] = {
    // defParamSuffix: COMMA ( defParamSuffixVarargs | defParam defParamSuffix? )
    var r = ISZ[AST.Param]()
    val nextParam = findChild(node, "defParam")
    nextParam match {
      case Some(np) =>
        r = r :+ buildDefParam(np, reporter)
        val innerSuffix = findChild(node, "defParamSuffix")
        innerSuffix match {
          case Some(is) => r = r ++ buildDefParamSuffixChainP(is, reporter)
          case _ =>
        }
      case _ =>
    }
    return r
  }

  def buildDefParam(node: ParseTree.Node, reporter: message.Reporter): AST.Param = {
    // defParam: mod* ID COLON type
    val mods = buildMods(findChildren(node, "mod"))
    val isHidden = hasMod(mods, "hidden")
    val id = mkId(idText(node), node)
    val typeNode = findChild(node, "type").get
    val tipe = buildType(typeNode, reporter)
    return AST.Param(isHidden = isHidden, id = id, tipe = tipe)
  }

  def buildFact(id: AST.Id, typeParams: ISZ[AST.TypeParam], hasParams: B, params: ISZ[AST.Param], assignSuffixOpt: Option[ParseTree.Node], posTree: ParseTree, reporter: message.Reporter): AST.Stmt.Fact = {
    var claims = ISZ[AST.Exp]()
    var descOpt: Option[AST.Exp.LitString] = None()
    var isFun = hasParams

    assignSuffixOpt match {
      case Some(as) =>
        val rhsNode = findChild(as, "rhs").get
        val expOpt = findChild(rhsNode, "exp")
        expOpt match {
          case Some(expNode) =>
            val exp = buildExp(expNode, reporter)
            // The rhs could be a paren with multiple exps
            exp match {
              case exp: AST.Exp.Tuple =>
                for (arg <- exp.args) {
                  arg match {
                    case arg: AST.Exp.LitString =>
                      descOpt = Some(arg)
                    case _ =>
                      claims = claims :+ arg
                  }
                }
              case _ =>
                claims = claims :+ exp
            }
          case _ =>
        }
      case _ =>
    }

    if (isFun && claims.nonEmpty) {
      // Wrap claims in quant with params
      var funParams = ISZ[AST.Exp.Fun.Param]()
      for (p <- params) {
        funParams = funParams :+ AST.Exp.Fun.Param(idOpt = Some(p.id), tipeOpt = Some(p.tipe), typedOpt = None())
      }
      val body: AST.AssignExp = if (claims.size == 1) {
        AST.Stmt.Expr(exp = claims(0), attr = typedAttr(posTree))
      } else {
        AST.Stmt.Expr(exp = claims(0), attr = typedAttr(posTree))
      }
      val fun = AST.Exp.Fun(context = ISZ(), params = funParams, exp = body, attr = typedAttr(posTree))
      val quant = AST.Exp.QuantType(isForall = T, fun = fun, attr = attr(posTree))
      claims = ISZ(quant)
    }

    return AST.Stmt.Fact(
      id = id,
      typeParams = typeParams,
      descOpt = descOpt,
      claims = claims,
      isFun = isFun,
      attr = resolvedAttr(posTree))
  }

  def buildTheorem(isLemma: B, id: AST.Id, typeParams: ISZ[AST.TypeParam], hasParams: B, params: ISZ[AST.Param], assignSuffixOpt: Option[ParseTree.Node], posTree: ParseTree, reporter: message.Reporter): AST.Stmt.Theorem = {
    var claim: AST.Exp = AST.Exp.LitB(value = T, attr = attr(posTree))
    var descOpt: Option[AST.Exp.LitString] = None()
    var isFun = hasParams

    assignSuffixOpt match {
      case Some(as) =>
        val rhsNode = findChild(as, "rhs").get
        val expOpt = findChild(rhsNode, "exp")
        expOpt match {
          case Some(expNode) =>
            claim = buildExp(expNode, reporter)
          case _ =>
        }
      case _ =>
    }

    if (isFun) {
      var funParams = ISZ[AST.Exp.Fun.Param]()
      for (p <- params) {
        funParams = funParams :+ AST.Exp.Fun.Param(idOpt = Some(p.id), tipeOpt = Some(p.tipe), typedOpt = None())
      }
      val body = AST.Stmt.Expr(exp = claim, attr = typedAttr(posTree))
      val fun = AST.Exp.Fun(context = ISZ(), params = funParams, exp = body, attr = typedAttr(posTree))
      claim = AST.Exp.QuantType(isForall = T, fun = fun, attr = attr(posTree))
    }

    return AST.Stmt.Theorem(
      isLemma = isLemma,
      id = id,
      typeParams = typeParams,
      descOpt = descOpt,
      claim = claim,
      isFun = isFun,
      proof = AST.ProofAst(steps = ISZ(), attr = attr(posTree)),
      attr = resolvedAttr(posTree))
  }

  def buildInv(id: AST.Id, assignSuffixOpt: Option[ParseTree.Node], posTree: ParseTree, reporter: message.Reporter): AST.Stmt.Inv = {
    var claims = ISZ[AST.Exp]()
    assignSuffixOpt match {
      case Some(as) =>
        val rhsNode = findChild(as, "rhs").get
        val expOpt = findChild(rhsNode, "exp")
        expOpt match {
          case Some(expNode) =>
            val exp = buildExp(expNode, reporter)
            exp match {
              case exp: AST.Exp.Tuple =>
                claims = exp.args
              case _ =>
                claims = ISZ(exp)
            }
          case _ =>
        }
      case _ =>
    }
    return AST.Stmt.Inv(id = id, claims = claims, attr = resolvedAttr(posTree))
  }

  // ─── if/while/for/match ────────────────────────────────────────────

  def buildIfStmt(node: ParseTree.Node, reporter: message.Reporter): AST.Stmt.If = {
    // ifStmt: IF exp annot? block els?
    val expNode = findChild(node, "exp").get
    val cond = buildExp(expNode, reporter)
    val blockNode = findChild(node, "block").get
    val thenBlock = buildBlock(blockNode, reporter)
    val elsOpt = findChild(node, "els")
    val elseBody: AST.Body = elsOpt match {
      case Some(els) => buildElse(els, reporter)
      case _ => AST.Body(stmts = ISZ(), undecls = ISZ())
    }
    return AST.Stmt.If(
      cond = cond,
      thenBody = thenBlock.body,
      elseBody = elseBody,
      attr = typedAttr(node))
  }

  def buildElse(node: ParseTree.Node, reporter: message.Reporter): AST.Body = {
    // els: ELSE ( elsIf | block )
    val elsIfOpt = findChild(node, "elsIf")
    elsIfOpt match {
      case Some(ei) =>
        // elsIf: IF exp annot? block els?
        val expNode = findChild(ei, "exp").get
        val cond = buildExp(expNode, reporter)
        val blockNode = findChild(ei, "block").get
        val thenBlock = buildBlock(blockNode, reporter)
        val elsOpt = findChild(ei, "els")
        val elseBody: AST.Body = elsOpt match {
          case Some(els) => buildElse(els, reporter)
          case _ => AST.Body(stmts = ISZ(), undecls = ISZ())
        }
        val ifStmt = AST.Stmt.If(
          cond = cond,
          thenBody = thenBlock.body,
          elseBody = elseBody,
          attr = typedAttr(ei))
        return AST.Body(stmts = ISZ(ifStmt), undecls = ISZ())
      case _ =>
    }
    val blockOpt = findChild(node, "block")
    blockOpt match {
      case Some(blk) =>
        val block = buildBlock(blk, reporter)
        return block.body
      case _ =>
    }
    return AST.Body(stmts = ISZ(), undecls = ISZ())
  }

  def buildBlock(node: ParseTree.Node, reporter: message.Reporter): AST.Stmt.Block = {
    // block: LBRACE annot? blockContent RBRACE
    val blockContent = findChild(node, "blockContent").get
    val stmts = buildBlockContent(blockContent, reporter)
    return AST.Stmt.Block(
      contract = AST.MethodContract.Simple.empty,
      body = AST.Body(stmts = stmts, undecls = ISZ()),
      attr = attr(node))
  }

  def buildBlockContent(node: ParseTree.Node, reporter: message.Reporter): ISZ[AST.Stmt] = {
    // blockContent: stmt* ret?
    var stmts = ISZ[AST.Stmt]()
    val stmtNodes = findChildren(node, "stmt")
    for (s <- stmtNodes) {
      stmts = stmts :+ buildStmt(s, reporter)
    }
    val retOpt = findChild(node, "ret")
    retOpt match {
      case Some(r) => stmts = stmts :+ buildReturn(r, reporter)
      case _ =>
    }
    return stmts
  }

  def buildReturn(node: ParseTree.Node, reporter: message.Reporter): AST.Stmt.Return = {
    // ret: ( RETURN | HALT ) annot? rhs?
    val rhsOpt = findChild(node, "rhs")
    val expOpt: Option[AST.Exp] = rhsOpt match {
      case Some(rhs) =>
        val expNode = findChild(rhs, "exp")
        expNode match {
          case Some(en) => Some(buildExp(en, reporter))
          case _ => None()
        }
      case _ => None()
    }
    val isHalt = findLeafByRule(node, "HALT").nonEmpty
    if (isHalt) {
      // halt("...") is represented as a return with halt expression
      val haltArgs: ISZ[AST.Exp] = expOpt match {
        case Some(e) => ISZ(e)
        case _ => ISZ()
      }
      val haltIdent = AST.Exp.Ident(id = AST.Id("halt", emptyAttr), attr = emptyResolvedAttr)
      val haltInvoke = AST.Exp.Invoke(
        receiverOpt = None(),
        ident = haltIdent,
        targs = ISZ(),
        args = haltArgs,
        attr = resolvedAttr(node))
      return AST.Stmt.Return(expOpt = Some(haltInvoke), attr = typedAttr(node))
    }
    return AST.Stmt.Return(expOpt = expOpt, attr = typedAttr(node))
  }

  def buildWhileStmt(node: ParseTree.Node, reporter: message.Reporter): AST.Stmt.While = {
    // whileStmt: WHILE exp annot? block
    val expNode = findChild(node, "exp").get
    val cond = buildExp(expNode, reporter)
    val annotOpt = findChild(node, "annot")
    val contract = buildLoopContract(annotOpt, reporter)
    val blockNode = findChild(node, "block").get
    val block = buildBlock(blockNode, reporter)
    return AST.Stmt.While(
      context = ISZ(),
      cond = cond,
      contract = contract,
      body = block.body,
      attr = attr(node))
  }

  def buildForStmt(node: ParseTree.Node, reporter: message.Reporter): AST.Stmt.For = {
    // forStmt: FOR forRange commaForRange* block
    var enumGens = ISZ[AST.EnumGen.For]()
    val forRanges = findChildren(node, "forRange")
    for (fr <- forRanges) {
      enumGens = enumGens :+ buildForRange(fr, reporter)
    }
    val commaForRanges = findChildren(node, "commaForRange")
    for (cfr <- commaForRanges) {
      val fr = findChild(cfr, "forRange").get
      enumGens = enumGens :+ buildForRange(fr, reporter)
    }
    val blockNode = findChild(node, "block").get
    val block = buildBlock(blockNode, reporter)
    return AST.Stmt.For(
      context = ISZ(),
      enumGens = enumGens,
      body = block.body,
      attr = attr(node))
  }

  def buildForRange(node: ParseTree.Node, reporter: message.Reporter): AST.EnumGen.For = {
    // forRange: ( ID | UNDERSCORE ) COLON exp rangeSuffix? ifExp? annot?
    val idOpt: Option[AST.Id] = findLeafByRule(node, "ID") match {
      case Some(idLeaf) => Some(mkId(idLeaf.text, idLeaf))
      case _ => None()
    }
    val expNodes = findChildren(node, "exp")
    val rangeSuffixOpt = findChild(node, "rangeSuffix")
    val ifExpOpt = findChild(node, "ifExp")
    val annotOpt = findChild(node, "annot")
    val contract = buildLoopContract(annotOpt, reporter)

    val condOpt: Option[AST.Exp] = ifExpOpt match {
      case Some(ie) =>
        val condExp = findChild(ie, "exp").get
        Some(buildExp(condExp, reporter))
      case _ => None()
    }

    val range: AST.EnumGen.Range = rangeSuffixOpt match {
      case Some(rs) =>
        // rangeSuffix: ( TO | UNTIL ) exp byExp?
        val isInclusive = findLeafByRule(rs, "TO").nonEmpty
        val startExp = buildExp(expNodes(0), reporter)
        val endExpNodes = findChildren(rs, "exp")
        val endExp = buildExp(endExpNodes(0), reporter)
        val byExpOpt: Option[AST.Exp] = findChild(rs, "byExp") match {
          case Some(be) =>
            val beExp = findChild(be, "exp").get
            Some(buildExp(beExp, reporter))
          case _ => None()
        }
        AST.EnumGen.Range.Step(
          isInclusive = isInclusive,
          start = startExp,
          end = endExp,
          byOpt = byExpOpt,
          attr = attr(node))
      case _ =>
        if (expNodes.isEmpty) {
          halt(st"Expected expression in forRange ${node.toST.render}".render)
        }
        AST.EnumGen.Range.Expr(exp = buildExp(expNodes(0), reporter), attr = attr(node))
    }

    return AST.EnumGen.For(idOpt = idOpt, range = range, condOpt = condOpt, contract = contract)
  }

  def buildMatchStmt(node: ParseTree.Node, reporter: message.Reporter): AST.Stmt.Match = {
    // matchStmt: MATCH exp annot? matchCases
    val expNode = findChild(node, "exp").get
    val exp = buildExp(expNode, reporter)
    val matchCasesNode = findChild(node, "matchCases").get
    val cases = buildMatchCases(matchCasesNode, reporter)
    return AST.Stmt.Match(isInduct = F, exp = exp, cases = cases, attr = typedAttr(node))
  }

  def buildMatchCases(node: ParseTree.Node, reporter: message.Reporter): ISZ[AST.Case] = {
    // matchCases: LBRACE cas+ RBRACE
    val casNodes = findChildren(node, "cas")
    var r = ISZ[AST.Case]()
    for (c <- casNodes) {
      r = r :+ buildCase(c, reporter)
    }
    return r
  }

  def buildCase(node: ParseTree.Node, reporter: message.Reporter): AST.Case = {
    // cas: CASE pattern ifExp? ARROW annot? blockContent
    val patternNode = findChild(node, "pattern").get
    val pattern = buildPattern(patternNode, reporter)
    val ifExpOpt = findChild(node, "ifExp")
    val condOpt: Option[AST.Exp] = ifExpOpt match {
      case Some(ie) =>
        val exp = findChild(ie, "exp").get
        Some(buildExp(exp, reporter))
      case _ => None()
    }
    val blockContent = findChild(node, "blockContent").get
    val stmts = buildBlockContent(blockContent, reporter)
    return AST.Case(pattern = pattern, condOpt = condOpt, body = AST.Body(stmts = stmts, undecls = ISZ()))
  }

  // ─── deduce / proof / sequent ──────────────────────────────────────

  def buildDeduceStmt(node: ParseTree.Node, reporter: message.Reporter): AST.Stmt = {
    // deduceStmt: DEDUCE ( truthTable | proof | sequent+ | expProof )
    val proofOpt = findChild(node, "proof")
    proofOpt match {
      case Some(p) =>
        val steps = buildProof(p, reporter)
        return AST.Stmt.DeduceSteps(steps = steps, attr = attr(node))
      case _ =>
    }

    val sequentNodes = findChildren(node, "sequent")
    if (sequentNodes.nonEmpty) {
      var sequents = ISZ[AST.Sequent]()
      for (s <- sequentNodes) {
        sequents = sequents :+ buildSequent(s, reporter)
      }
      return AST.Stmt.DeduceSequent(justOpt = None(), sequents = sequents, attr = attr(node))
    }

    val expProofOpt = findChild(node, "expProof")
    expProofOpt match {
      case Some(ep) =>
        // expProof: LPAREN expJustOpt commaExpJustOpt* COMMA? RPAREN
        var steps = ISZ[AST.ProofAst.Step]()
        val expJustOpts = findChildren(ep, "expJustOpt")
        val commaExpJustOpts = findChildren(ep, "commaExpJustOpt")
        var stepNum: Z = 1
        for (ejo <- expJustOpts) {
          steps = steps :+ buildExpJustOpt(ejo, stepNum, reporter)
          stepNum = stepNum + 1
        }
        for (cejo <- commaExpJustOpts) {
          val ejo = findChild(cejo, "expJustOpt").get
          steps = steps :+ buildExpJustOpt(ejo, stepNum, reporter)
          stepNum = stepNum + 1
        }
        return AST.Stmt.DeduceSteps(steps = steps, attr = attr(node))
      case _ =>
    }

    val truthTableOpt = findChild(node, "truthTable")
    truthTableOpt match {
      case Some(_) =>
        halt("Truth table not yet supported in AST builder")
      case _ =>
    }

    halt(st"Could not build deduceStmt from ${node.toST.render}".render)
  }

  def buildExpJustOpt(node: ParseTree.Node, stepNum: Z, reporter: message.Reporter): AST.ProofAst.Step = {
    // expJustOpt: exp just?
    val expNode = findChild(node, "exp").get
    val claim = buildExp(expNode, reporter)
    val justOpt = findChild(node, "just")
    val stepId = AST.ProofAst.StepId.Num(no = stepNum, attr = attr(node))
    justOpt match {
      case Some(j) =>
        val just = buildJust(j, reporter)
        return AST.ProofAst.Step.Regular(id = stepId, claim = claim, just = just, attr = attr(node))
      case _ =>
        // No justification - treat as regular with a default justification
        val ref = AST.Exp.Ident(id = AST.Id("Premise", emptyAttr), attr = emptyResolvedAttr)
        val just = AST.ProofAst.Step.Justification.Ref(
          ref = ref,
          hasWitness = F,
          witnesses = ISZ())
        return AST.ProofAst.Step.Regular(id = stepId, claim = claim, just = just, attr = attr(node))
    }
  }

  def buildProof(node: ParseTree.Node, reporter: message.Reporter): ISZ[AST.ProofAst.Step] = {
    // proof: LBRACE proofStep* RBRACE
    val stepNodes = findChildren(node, "proofStep")
    var r = ISZ[AST.ProofAst.Step]()
    for (s <- stepNodes) {
      r = r :+ buildProofStep(s, reporter)
    }
    return r
  }

  def buildProofStep(node: ParseTree.Node, reporter: message.Reporter): AST.ProofAst.Step = {
    // proofStep: proofId DOT ( exp just | subProof | assumeProofStep | assertProofStep )
    val proofIdNode = findChild(node, "proofId").get
    val stepId = buildProofId(proofIdNode, reporter)

    val assumeOpt = findChild(node, "assumeProofStep")
    assumeOpt match {
      case Some(ap) =>
        // assumeProofStep: ASSUME exp
        val expNode = findChild(ap, "exp").get
        val claim = buildExp(expNode, reporter)
        return AST.ProofAst.Step.Assume(id = stepId, claim = claim, attr = attr(node))
      case _ =>
    }

    val assertOpt = findChild(node, "assertProofStep")
    assertOpt match {
      case Some(ap) =>
        // assertProofStep: ASSERT exp subProof
        val expNode = findChild(ap, "exp").get
        val claim = buildExp(expNode, reporter)
        val subProofNode = findChild(ap, "subProof").get
        val steps = buildSubProof(subProofNode, reporter)
        return AST.ProofAst.Step.Assert(id = stepId, claim = claim, steps = steps, attr = attr(node))
      case _ =>
    }

    val subProofOpt = findChild(node, "subProof")
    subProofOpt match {
      case Some(sp) =>
        val freshIds = findChildren(sp, "freshIds")
        if (freshIds.nonEmpty) {
          // Let step
          var params = ISZ[AST.ProofAst.Step.Let.Param]()
          for (fi <- freshIds) {
            // freshIds: ID commaId* colonType?
            val idLeaf = findLeafByRule(fi, "ID").get
            val colonTypeOpt = findChild(fi, "colonType")
            val tipeOpt: Option[AST.Type] = colonTypeOpt match {
              case Some(ct) =>
                val typeNode = findChild(ct, "type").get
                Some(buildType(typeNode, reporter))
              case _ => None()
            }
            params = params :+ AST.ProofAst.Step.Let.Param(id = mkId(idLeaf.text, idLeaf), tipeOpt = tipeOpt)
            val commaIds = findChildren(fi, "commaId")
            for (ci <- commaIds) {
              val ciLeaf = findLeafByRule(ci, "ID").get
              params = params :+ AST.ProofAst.Step.Let.Param(id = mkId(ciLeaf.text, ciLeaf), tipeOpt = None())
            }
          }
          val steps = buildSubProofSteps(sp, reporter)
          return AST.ProofAst.Step.Let(id = stepId, params = params, steps = steps, context = ISZ(), attr = attr(node))
        } else {
          val steps = buildSubProof(sp, reporter)
          return AST.ProofAst.Step.SubProof(id = stepId, steps = steps, attr = attr(node))
        }
      case _ =>
    }

    // Regular step: exp just
    val expNodes = findChildren(node, "exp")
    val justNode = findChild(node, "just")
    if (expNodes.nonEmpty && justNode.nonEmpty) {
      val claim = buildExp(expNodes(0), reporter)
      val just = buildJust(justNode.get, reporter)
      return AST.ProofAst.Step.Regular(id = stepId, claim = claim, just = just, attr = attr(node))
    }

    halt(st"Could not build proofStep from ${node.toST.render}".render)
  }

  def buildSubProof(node: ParseTree.Node, reporter: message.Reporter): ISZ[AST.ProofAst.Step] = {
    // subProof: LBRACE freshIds* proofStep+ RBRACE
    return buildSubProofSteps(node, reporter)
  }

  def buildSubProofSteps(node: ParseTree.Node, reporter: message.Reporter): ISZ[AST.ProofAst.Step] = {
    val stepNodes = findChildren(node, "proofStep")
    var r = ISZ[AST.ProofAst.Step]()
    for (s <- stepNodes) {
      r = r :+ buildProofStep(s, reporter)
    }
    return r
  }

  def buildProofId(node: ParseTree.Node, reporter: message.Reporter): AST.ProofAst.StepId = {
    // proofId: INT | STRING
    val leaf = firstLeaf(node)
    leaf.ruleName.native match {
      case "INT" =>
        val value = Z(leaf.text).get
        return AST.ProofAst.StepId.Num(no = value, attr = attr(leaf))
      case "STRING" =>
        val value = parseStringLit(leaf.text)
        return AST.ProofAst.StepId.Str(isSynthetic = F, value = value, attr = attr(leaf))
      case _ =>
        halt(st"Unknown proofId type: ${leaf.ruleName}".render)
    }
  }

  def buildJust(node: ParseTree.Node, reporter: message.Reporter): AST.ProofAst.Step.Justification = {
    // just: BY name justTypeArgs? justArgs? proofId*
    val nameNode = findChild(node, "name").get
    val name = buildName(nameNode, reporter)
    val justTypeArgsOpt = findChild(node, "justTypeArgs")
    val justArgsOpt = findChild(node, "justArgs")
    val witnessIds = findChildren(node, "proofId")

    var witnesses = ISZ[AST.ProofAst.StepId]()
    for (w <- witnessIds) {
      witnesses = witnesses :+ buildProofId(w, reporter)
    }
    val hasWitness = witnesses.nonEmpty

    val targs: ISZ[AST.Type] = justTypeArgsOpt match {
      case Some(jta) =>
        // justTypeArgs: LSQUARE type commaType* RSQUARE
        var types = ISZ[AST.Type]()
        val typeNodes = findChildren(jta, "type")
        for (tn <- typeNodes) {
          types = types :+ buildType(tn, reporter)
        }
        val commaTypes = findChildren(jta, "commaType")
        for (ct <- commaTypes) {
          val tn = findChild(ct, "type").get
          types = types :+ buildType(tn, reporter)
        }
        types
      case _ => ISZ()
    }

    justArgsOpt match {
      case Some(ja) =>
        // justArgs: LPAREN args COMMA? RPAREN
        val argsNode = findChild(ja, "args").get
        var args = ISZ[AST.Exp]()
        val rhsNodes = findChildren(argsNode, "rhs")
        for (rhs <- rhsNodes) {
          val expOpt = findChild(rhs, "exp")
          expOpt match {
            case Some(en) => args = args :+ buildExp(en, reporter)
            case _ =>
          }
        }
        val argSuffixes = findChildren(argsNode, "argSuffix")
        for (asf <- argSuffixes) {
          val rhs = findChild(asf, "rhs").get
          val expOpt = findChild(rhs, "exp")
          expOpt match {
            case Some(en) => args = args :+ buildExp(en, reporter)
            case _ =>
          }
        }

        if (name.ids.size == 1) {
          val ident = AST.Exp.Ident(id = name.ids(0), attr = resolvedAttr(nameNode))
          val invoke = AST.Exp.Invoke(
            receiverOpt = None(),
            ident = ident,
            targs = targs,
            args = args,
            attr = resolvedAttr(node))
          return AST.ProofAst.Step.Justification.Apply(
            invoke = invoke,
            hasWitness = hasWitness,
            witnesses = witnesses)
        } else {
          // Multi-component name
          val lastId = name.ids(name.ids.size - 1)
          var rcv: AST.Exp = AST.Exp.Ident(id = name.ids(0), attr = resolvedAttr(nameNode))
          for (i <- 1 until name.ids.size - 1) {
            rcv = AST.Exp.Select(receiverOpt = Some(rcv), id = name.ids(i), targs = ISZ(), attr = resolvedAttr(nameNode))
          }
          val ident = AST.Exp.Ident(id = lastId, attr = resolvedAttr(nameNode))
          val invoke = AST.Exp.Invoke(
            receiverOpt = Some(rcv),
            ident = ident,
            targs = targs,
            args = args,
            attr = resolvedAttr(node))
          return AST.ProofAst.Step.Justification.Apply(
            invoke = invoke,
            hasWitness = hasWitness,
            witnesses = witnesses)
        }
      case _ =>
        // No args - just a ref
        if (name.ids.size == 1) {
          val ref: AST.Exp.Ref = if (targs.nonEmpty) {
            AST.Exp.Select(receiverOpt = None(), id = name.ids(0), targs = targs, attr = resolvedAttr(nameNode))
          } else {
            AST.Exp.Ident(id = name.ids(0), attr = resolvedAttr(nameNode))
          }
          return AST.ProofAst.Step.Justification.Ref(
            ref = ref,
            hasWitness = hasWitness,
            witnesses = witnesses)
        } else {
          var rcv: AST.Exp = AST.Exp.Ident(id = name.ids(0), attr = resolvedAttr(nameNode))
          for (i <- 1 until name.ids.size - 1) {
            rcv = AST.Exp.Select(receiverOpt = Some(rcv), id = name.ids(i), targs = ISZ(), attr = resolvedAttr(nameNode))
          }
          val ref = AST.Exp.Select(
            receiverOpt = Some(rcv),
            id = name.ids(name.ids.size - 1),
            targs = targs,
            attr = resolvedAttr(nameNode))
          return AST.ProofAst.Step.Justification.Ref(
            ref = ref,
            hasWitness = hasWitness,
            witnesses = witnesses)
        }
    }
  }

  def buildSequent(node: ParseTree.Node, reporter: message.Reporter): AST.Sequent = {
    // sequent: COLON exps? SEQUENT exp proof?
    val expsOpt = findChild(node, "exps")
    var premises = ISZ[AST.Exp]()
    expsOpt match {
      case Some(exps) =>
        val expNodes = findChildren(exps, "exp")
        for (en <- expNodes) {
          premises = premises :+ buildExp(en, reporter)
        }
        val commaExps = findChildren(exps, "commaExp")
        for (ce <- commaExps) {
          val en = findChild(ce, "exp").get
          premises = premises :+ buildExp(en, reporter)
        }
      case _ =>
    }
    val expNodes = findChildren(node, "exp")
    val conclusion = buildExp(expNodes(0), reporter)
    val proofOpt = findChild(node, "proof")
    val steps: ISZ[AST.ProofAst.Step] = proofOpt match {
      case Some(p) => buildProof(p, reporter)
      case _ => ISZ()
    }
    return AST.Sequent(premises = premises, conclusion = conclusion, steps = steps, attr = attr(node))
  }

}
