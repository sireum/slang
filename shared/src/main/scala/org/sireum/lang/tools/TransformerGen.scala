// #Sireum
/*
 Copyright (c) 2017, Robby, Kansas State University
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

package org.sireum.lang.tools

import org.sireum._

object TransformerGen {

  @sig trait Template {
    def main(licenseOpt: Option[String],
             fileUriOpt: Option[String],
             packageNames: ISZ[String],
             name: String,
             preMethods: ISZ[ST],
             postMethod: ISZ[ST],
             transformHelper: ISZ[ST],
             transformMethod: ISZ[ST]): ST

    @pure def preMethodRoot(typeName: ST,
                            tpe: ST,
                            preMethodRootCases: ISZ[ST]): ST

    @pure def preMethodRootCase(typeName: ST, tpe: ST): ST

    @pure def preMethod(typeName: ST, tpe: ST, superType: ST): ST

    @pure def postMethodRoot(typeName: ST, tpe: ST, postMethodRootCases: ISZ[ST]): ST

    @pure def postMethodRootCase(typeName: ST, tpe: ST): ST

    @pure def postMethod(typeName: ST, tpe: ST, superType: ST): ST

    @pure def transformMethod(typeName: ST,
                              tpe: ST,
                              transformMethodMatch: ST,
                              preAdaptOpt: Option[ST],
                              postAdaptOpt: Option[ST]): ST

    @pure def preAdaptDown(tpe: ST): ST

    @pure def preAdaptUp(tpe: ST): ST

    @pure def postAdaptDown(tpe: ST): ST

    @pure def postAdaptUp(tpe: ST): ST

    @pure def transformMethodMatch(tpe: ST,
                                   transformMethodCases: ISZ[ST]): ST

    @pure def transformMethodMatchSimple(ac: AdtChild): ST

    @pure def transformMethodCase(tpe: ST,
                                  ac: AdtChild): ST

    @pure def transformMethodCaseMember(i: Z,
                                        j: Z,
                                        typeName: ST,
                                        tpe: ST,
                                        fieldName: String): ST

    @pure def transformMethodCaseMemberIS(i: Z,
                                          j: Z,
                                          indexType: ST,
                                          typeName: ST,
                                          tpe: ST,
                                          fieldName: String): ST

    @pure def transformMethodCaseMemberMS(i: Z,
                                          j: Z,
                                          indexType: ST,
                                          typeName: ST,
                                          tpe: ST,
                                          fieldName: String): ST

    @pure def transformMethodCaseMemberOption(i: Z,
                                              j: Z,
                                              typeName: ST,
                                              tpe: ST,
                                              fieldName: String): ST

    @pure def transformMethodCaseMemberMOption(i: Z,
                                               typeName: ST,
                                               tpe: ST,
                                               fieldName: String): ST

    @pure def transformMethodCaseChanged(i: Z): ST

    @pure def transformMethodCaseUpdate(i: Z, fieldName: String): ST

    @pure def transformOption: ST

    @pure def transformMOption(): ST

    @pure def transformIS(indexType: ST): ST

    @pure def transformMS(indexType: ST): ST
  }

  object Template {
    val empty: ST = st""

    @pure def opt(stOpt: Option[ST]): ST = {
      return stOpt.getOrElse(empty)
    }

    @pure def stOpt[T](tOpt: Option[T], f: T => ST): ST = {
      return opt(tOpt.map(f))
    }

    @datatype class Transformer extends TransformerSig // WORKAROUND: scalameta string interpolation escape char issue in Windows

    @datatype class MTransformer extends MTransformerSig // WORKAROUND: scalameta string interpolation escape char issue in Windows

    @sig trait TransformerSig extends Template {
      @pure def main(licenseOpt: Option[String],
                     fileUriOpt: Option[String],
                     packageNames: ISZ[String],
                     name: String,
                     preMethods: ISZ[ST],
                     postMethods: ISZ[ST],
                     transformHelpers: ISZ[ST],
                     transformMethods: ISZ[ST]): ST = {
        val license: ST = stOpt(licenseOpt, (text: String) =>
          st"""
              |/*
              | $text
              | */)
              |""")
        val fileUri: ST = stOpt(fileUriOpt, (text: String) =>
          st"\n// This file is auto-generated from $text\n")
        val packageName: ST = if (packageNames.nonEmpty) st"\npackage ${(packageNames, ".")}\n" else empty
        return st"""// #Sireum
                   |// @formatter:off
                   |$license$fileUri$packageName
                   |import org.sireum._
                   |
                   |object $name {
                   |
                   |  @datatype class PreResult[Context, T](ctx: Context,
                   |                                        continue: B,
                   |                                        resultOpt: Option[T])
                   |
                   |  @datatype class Result[Context, T](ctx: Context,
                   |                                     resultOpt: Option[T])
                   |
                   |
                   |
                   |  @sig trait PrePost[Context] {
                   |
                   |    ${(preMethods, "\n\n")}
                   |
                   |    ${(postMethods, "\n\n")}
                   |
                   |  }
                   |
                   |  ${(transformHelpers, "\n\n")}
                   |
                   |}
                   |
                   |import $name._
                   |
                   |@datatype class $name[Context](pp: PrePost[Context]) {
                   |
                   |  ${(transformMethods, "\n\n")}
                   |
                 |}"""
      }

      @pure def preMethodRoot(typeName: ST,
                              tpe: ST,
                              preMethodRootCases: ISZ[ST]): ST = {
        return st"""@pure def pre$typeName(ctx: Context, o: $tpe): PreResult[Context, $tpe] = {
                   |  o match {
                   |    ${(preMethodRootCases, "\n")}
                   |  }
                   |}"""
      }

      @pure def preMethodRootCase(typeName: ST, tpe: ST): ST = {
        return st"case o: $tpe => return pre$typeName(ctx, o)"
      }

      @pure def preMethod(typeName: ST, tpe: ST, superType: ST): ST = {
        return st"""@pure def pre$typeName(ctx: Context, o: $tpe): PreResult[Context, $superType] = {
                   |  return PreResult(T, None())
                   |}"""
      }

      @pure def postMethodRoot(typeName: ST, tpe: ST, postMethodRootCases: ISZ[ST]): ST = {
        return st"""@pure def post$typeName(ctx: Context, o: $tpe): Result[Context, $tpe] = {
                   |  o match {
                   |    ${(postMethodRootCases, "\n")}
                   |  }
                   |}"""
      }

      @pure def postMethodRootCase(typeName: ST, tpe: ST): ST = {
        return st"case o: $tpe => return post$typeName(ctx, o)"
      }

      @pure def postMethod(typeName: ST, tpe: ST, superType: ST): ST = {
        return st"""@pure def post$typeName(ctx: Context, o: $tpe): Result[Context, $superType] = {
                   |  return Result(ctx, None())
                   |}"""
      }

      @pure def transformMethod(typeName: ST,
                                tpe: ST,
                                transformMethodMatch: ST,
                                preAdaptOpt: Option[ST],
                                postAdaptOpt: Option[ST]): ST = {
        return st"""@pure def transform$typeName(ctx: Context, o: $tpe): Result[Context, $tpe] = {
                   |  val preR: PreResult[Context, $tpe] = pp.pre$typeName(ctx, o)${opt(preAdaptOpt)}
                   |  val r: Result[Context, $tpe] = if (preR.continue) {
                   |    val o2: $tpe = preR.resultOpt.getOrElse(o)
                   |    val hasChanged: B = preR.resultOpt.nonEmpty
                   |    $transformMethodMatch
                   |  } else if (preR.resultOpt.nonEmpty) {
                   |    Result(preR.ctx, Some(preR.resultOpt.getOrElse(o)))
                   |  } else {
                   |    Result(preR.ctx, None())
                   |  }
                   |  val hasChanged: B = r.resultOpt.nonEmpty
                   |  val o2: $tpe = r.resultOpt.getOrElse(o)
                   |  val postR: Result[Context, $tpe] = pp.post$typeName(r.ctx, o2)${opt(postAdaptOpt)}
                   |  if (postR.resultOpt.nonEmpty) {
                   |    return postR
                   |  } else if (hasChanged) {
                   |    return Result(postR.ctx, Some(o2))
                   |  } else {
                   |    return Result(postR.ctx, None())
                   |  }
                   |}"""
      }

      @pure def preAdaptDown(tpe: ST): ST = {
        return st""" match {
                   |   case PreResult(preCtx, continue, Some(r: $tpe)) => PreResult(preCtx, continue, Some[$tpe](r))
                   |   case PreResult(preCtx, continue, _) => assert(F); PreResult(preCtx, F, None[$tpe]())
                   | }"""
      }

      @pure def preAdaptUp(tpe: ST): ST = {
        return st""" match {
                   |   case PreResult(preCtx, continue, Some(r)) => PreResult(preCtx, continue, Some[$tpe](r))
                   |   case PreResult(preCtx, continue, _) => PreResult(preCtx, continue, None[$tpe]())
                   | }"""
      }

      @pure def postAdaptDown(tpe: ST): ST = {
        return st""" match {
                   |   case Result(postCtx, Some(result: $tpe)) => Result(postCtx, Some[$tpe](result))
                   |   case Result(postCtx, _) => assert(F); Result(postCtx, None[$tpe]())
                   | }"""
      }

      @pure def postAdaptUp(tpe: ST): ST = {
        return st""" match {
                   |   case Result(postCtx, Some(r)) => Result(postCtx, Some[$tpe](r))
                   |   case Result(postCtx, _) => Result(postCtx, None[$tpe]())
                   | }"""
      }

      @pure def transformMethodMatch(tpe: ST,
                                     transformMethodCases: ISZ[ST]): ST = {
        return st"""val rOpt: Result[Context, $tpe] = o2 match {
                   |  ${(transformMethodCases, "\n")}
                   |}
                   |rOpt"""
      }

      @pure def transformMethodMatchSimple(ac: AdtChild): ST = {
        val i = ac.i
        val ctx: ST = if (i < z"0") st"ctx" else st"r$i.ctx"
        val updates: ST =
          if (ac.transformMethodCaseUpdates.isEmpty) empty
          else st"(${(ac.transformMethodCaseUpdates, ", ")})"
        return st"""${(ac.transformMethodCaseMembers, "\n")}
                   |if (hasChanged${ac.transformMethodCaseChanges})
                   |  Result($ctx, Some(o2$updates))
                   |else
                   |  Result($ctx, None())"""
      }

      @pure def transformMethodCase(tpe: ST,
                                    ac: AdtChild): ST = {
        val i = ac.i
        val ctx: ST = if (i < z"0") st"ctx" else st"r$i.ctx"
        val updates: ST =
          if (ac.transformMethodCaseUpdates.isEmpty) empty
          else st"(${(ac.transformMethodCaseUpdates, ", ")})"
        return st"""case o2: $tpe =>
                   |  ${(ac.transformMethodCaseMembers, "\n")}
                   |  if (hasChanged${ac.transformMethodCaseChanges})
                   |    Result($ctx, Some(o2$updates))
                   |  else
                   |    Result($ctx, None())"""
      }

      @pure def transformMethodCaseMember(i: Z,
                                          j: Z,
                                          typeName: ST,
                                          tpe: ST,
                                          fieldName: String): ST = {
        val ctx: ST = if (j < z"0") st"ctx" else st"r$j.ctx"
        return st"val r$i: Result[Context, $tpe] = transform$typeName($ctx, o2.$fieldName)"
      }

      @pure def transformMethodCaseMemberIS(i: Z,
                                            j: Z,
                                            indexType: ST,
                                            typeName: ST,
                                            tpe: ST,
                                            fieldName: String): ST = {
        val ctx: ST = if (j < z"0") st"ctx" else st"r$j.ctx"
        return st"val r$i: Result[Context, IS[$indexType, $tpe]] = transformIS$indexType($ctx, o2.$fieldName, transform$typeName _)"
      }

      @pure def transformMethodCaseMemberMS(i: Z,
                                            j: Z,
                                            indexType: ST,
                                            typeName: ST,
                                            tpe: ST,
                                            fieldName: String): ST = {
        val ctx: ST = if (j < z"0") st"ctx" else st"r$j.ctx"
        return st"val r$i: Result[Context, MS[$indexType, $tpe]] = transformMS$indexType($ctx, o2.$fieldName, transform$typeName _)"
      }

      @pure def transformMethodCaseMemberOption(i: Z,
                                                j: Z,
                                                typeName: ST,
                                                tpe: ST,
                                                fieldName: String): ST = {
        val ctx: ST = if (j < z"0") st"ctx" else st"r$j.ctx"
        return st"val r$i: Result[Context, Option[$tpe]] = transformOption($ctx, o2.$fieldName, transform$typeName _)"
      }

      @pure def transformMethodCaseMemberMOption(i: Z,
                                                 typeName: ST,
                                                 tpe: ST,
                                                 fieldName: String): ST = {
        return empty
      }

      @pure def transformMethodCaseChanged(i: Z): ST = {
        return st" || r$i.resultOpt.nonEmpty"
      }

      @pure def transformMethodCaseUpdate(i: Z, fieldName: String): ST = {
        return st"$fieldName = r$i.resultOpt.getOrElse(o2.$fieldName)"
      }

      @pure def transformOption: ST = {
        return st"""@pure def transformOption[Context, T](ctx: Context, option: Option[T], @pure f: (Context, T) => Result[Context, T]): Result[Context, Option[T]] = {
                   |  option match {
                   |    case Some(v) =>
                   |      val r = f(ctx, v)
                   |      r.resultOpt match {
                   |        case Some(_) => return Result(r.ctx, Some(r.resultOpt))
                   |        case _ => return Result(r.ctx, None())
                   |      }
                   |    case _ => return Result(ctx, None())
                   |  }
                   |}"""
      }

      @pure def transformMOption(): ST = {
        return empty
      }

      @pure def transformIS(indexType: ST): ST = {
        return st"""@pure def transformIS$indexType[Context, T](ctx: Context, s: IS[$indexType, T], f: (Context, T) => Result[Context, T]): Result[Context, IS[$indexType, T]] = {
                   |  val s2: MS[$indexType, T] = SI.toMS(s)
                   |  var changed: B = F
                   |  var ctxi = ctx
                   |  for (i <- s2.indices) {
                   |    val e: T = s(i)
                   |    val r: Result[Context, T] = f(ctxi, e)
                   |    ctxi = r.ctx
                   |    changed = changed || r.resultOpt.nonEmpty
                   |    s2(i) = r.resultOpt.getOrElse(e)
                   |  }
                   |  if (changed) {
                   |    return Result(ctxi, Some(SM.toIS(s2)))
                   |  } else {
                   |    return Result(ctxi, None())
                   |  }
                   |}"""
      }

      @pure def transformMS(indexType: ST): ST = {
        return empty
      }

    }

    @sig trait MTransformerSig extends Template {
      @pure def main(licenseOpt: Option[String],
                     fileUriOpt: Option[String],
                     packageNames: ISZ[String],
                     name: String,
                     preMethods: ISZ[ST],
                     postMethods: ISZ[ST],
                     transformHelpers: ISZ[ST],
                     transformMethods: ISZ[ST]): ST = {
        val license: ST = stOpt(licenseOpt, (text: String) =>
          st"""
              |/*
              | $text
              | */)
              |""")
        val fileUri: ST = stOpt(fileUriOpt, (text: String) =>
          st"\n// This file is auto-generated from $text\n")
        val packageName: ST = if (packageNames.nonEmpty) st"\npackage ${(packageNames, ".")}\n" else empty
        return st"""// #Sireum
                   |// @formatter:off
                   |$license$fileUri$packageName
                   |import org.sireum._
                   |
                   |object $name {
                   |
                   |  @record class PreResult[T](continue: B,
                   |                             resultOpt: MOption[T])
                   |
                   |  @sig trait PrePost {
                   |
                   |    ${(preMethods, "\n\n")}
                   |
                   |    ${(postMethods, "\n\n")}
                   |
                   |  }
                   |
                   |  ${(transformHelpers, "\n\n")}
                   |
                   |}
                   |
                   |import $name._
                   |
                   |@record class $name(pp: PrePost) {
                   |
                   |  ${(transformMethods, "\n\n")}
                   |
                   |}"""
      }

      @pure def preMethodRoot(typeName: ST,
                              tpe: ST,
                              preMethodRootCases: ISZ[ST]): ST = {
        return st"""def pre$typeName(o: $tpe): PreResult[$tpe] = {
                   |  o match {
                   |    ${(preMethodRootCases, "\n")}
                   |  }
                   |}"""
      }

      @pure def preMethodRootCase(typeName: ST, tpe: ST): ST = {
        return st"case o: $tpe => return pre$typeName(o)"
      }

      @pure def preMethod(typeName: ST, tpe: ST, superType: ST): ST = {
        return st"""def pre$typeName(o: $tpe): PreResult[$superType] = {
                   |  return PreResult(T, MNone())
                   |}"""
      }

      @pure def postMethodRoot(typeName: ST, tpe: ST, postMethodRootCases: ISZ[ST]): ST = {
        return st"""def post$typeName(o: $tpe): MOption[$tpe] = {
                   |  o match {
                   |    ${(postMethodRootCases, "\n")}
                   |  }
                   |}"""
      }

      @pure def postMethodRootCase(typeName: ST, tpe: ST): ST = {
        return st"case o: $tpe => return post$typeName(o)"
      }

      @pure def postMethod(typeName: ST, tpe: ST, superType: ST): ST = {
        return st"""def post$typeName(o: $tpe): MOption[$superType] = {
                   |  return MNone()
                   |}"""
      }

      @pure def transformMethod(typeName: ST,
                                tpe: ST,
                                transformMethodMatch: ST,
                                preAdaptOpt: Option[ST],
                                postAdaptOpt: Option[ST]): ST = {
        return st"""def transform$typeName(o: $tpe): MOption[$tpe] = {
                   |  val preR: PreResult[$tpe] = pp.pre$typeName(o)${opt(preAdaptOpt)}
                   |  val r: MOption[$tpe] = if (preR.continue) {
                   |    val o2: $tpe = preR.resultOpt.getOrElse(o)
                   |    val hasChanged: B = preR.resultOpt.nonEmpty
                   |    $transformMethodMatch
                   |  } else if (preR.resultOpt.nonEmpty) {
                   |    MSome(preR.resultOpt.getOrElse(o))
                   |  } else {
                   |    MNone()
                   |  }
                   |  val hasChanged: B = r.nonEmpty
                   |  val o2: $tpe = r.getOrElse(o)
                   |  val postR: MOption[$tpe] = pp.post$typeName(o2)${opt(postAdaptOpt)}
                   |  if (postR.nonEmpty) {
                   |    return postR
                   |  } else if (hasChanged) {
                   |    return MSome(o2)
                   |  } else {
                   |    return MNone()
                   |  }
                   |}"""
      }

      @pure def preAdaptDown(tpe: ST): ST = {
        return st""" match {
                   |   case PreResult(continue, MSome(r: $tpe)) => PreResult(continue, MSome[$tpe](r))
                   |   case _ => assert(F); PreResult(F, MNone[$tpe]())
                   | }"""
      }

      @pure def preAdaptUp(tpe: ST): ST = {
        return st""" match {
                   |   case PreResult(continue, MSome(r)) => PreResult(continue, MSome[$tpe](r))
                   |   case PreResult(continue, _) => PreResult(continue, MNone[$tpe]())
                   | }"""
      }

      @pure def postAdaptDown(tpe: ST): ST = {
        return st""" match {
                   |   case MSome(result: $tpe) => MSome[$tpe](result)
                   |   case _ => assert(F); MNone[$tpe]()
                   | }"""
      }

      @pure def postAdaptUp(tpe: ST): ST = {
        return st""" match {
                   |   case MSome(r) => MSome[$tpe](r)
                   |   case _ => MNone[$tpe]()
                   | }"""
      }

      @pure def transformMethodMatch(tpe: ST,
                                     transformMethodCases: ISZ[ST]): ST = {
        return st"""val rOpt: MOption[$tpe] = o2 match {
                   |  ${(transformMethodCases, "\n")}
                   |}
                   |rOpt"""
      }


      @pure def transformMethodMatchSimple(ac: AdtChild): ST = {
        val updates: ST =
          if (ac.transformMethodCaseUpdates.isEmpty) empty
          else st"(${(ac.transformMethodCaseUpdates, ", ")})"
        return st"""${(ac.transformMethodCaseMembers, "\n")}
                   |if (hasChanged${ac.transformMethodCaseChanges})
                   |  MSome(o2$updates)
                   |else
                   |  MNone()"""
      }

      @pure def transformMethodCase(tpe: ST,
                                    ac: AdtChild): ST = {
        val updates: ST =
          if (ac.transformMethodCaseUpdates.isEmpty) empty
          else st"(${(ac.transformMethodCaseUpdates, ", ")})"
        return st"""case o2: $tpe =>
                   |  ${(ac.transformMethodCaseMembers, "\n")}
                   |  if (hasChanged${ac.transformMethodCaseChanges})
                   |    MSome(o2$updates)
                   |  else
                   |    MNone()"""
      }

      @pure def transformMethodCaseMember(i: Z,
                                          j: Z,
                                          typeName: ST,
                                          tpe: ST,
                                          fieldName: String): ST = {
        return st"val r$i: MOption[$tpe] = transform$typeName(o2.$fieldName)"
      }

      @pure def transformMethodCaseMemberIS(i: Z,
                                            j: Z,
                                            indexType: ST,
                                            typeName: ST,
                                            tpe: ST,
                                            fieldName: String): ST = {
        return st"val r$i: MOption[IS[$indexType, $tpe]] = transformIS$indexType(o2.$fieldName, transform$typeName _)"
      }

      @pure def transformMethodCaseMemberMS(i: Z,
                                            j: Z,
                                            indexType: ST,
                                            typeName: ST,
                                            tpe: ST,
                                            fieldName: String): ST = {
        return st"val r$i: MOption[MS[$indexType, $tpe]] = transformMS$indexType(o2.$fieldName, transform$typeName _)"
      }

      @pure def transformMethodCaseMemberOption(i: Z,
                                                j: Z,
                                                typeName: ST,
                                                tpe: ST,
                                                fieldName: String): ST = {
        return st"val r$i: MOption[Option[$tpe]] = transformOption(o2.$fieldName, transform$typeName _)"
      }

      @pure def transformMethodCaseMemberMOption(i: Z,
                                                 typeName: ST,
                                                 tpe: ST,
                                                 fieldName: String): ST = {
        return st"val r$i: MOption[MOption[$tpe]] = transformOption(o2.$fieldName, transform$typeName _)"
      }

      @pure def transformMethodCaseChanged(i: Z): ST = {
        return st" || r$i.nonEmpty"
      }

      @pure def transformMethodCaseUpdate(i: Z, fieldName: String): ST = {
        return st"$fieldName = r$i.getOrElse(o2.$fieldName)"
      }

      @pure def transformOption: ST = {
        return st"""def transformOption[T](option: Option[T], f: T => MOption[T]): MOption[Option[T]] = {
                   |  option match {
                   |    case Some(v) =>
                   |      val r = f(v)
                   |      r match {
                   |        case MSome(v2) => return MSome(Some(v2))
                   |        case _ => return MNone()
                   |      }
                   |    case _ => return MNone()
                   |  }
                   |}"""
      }

      @pure def transformMOption(): ST = {
        return st"""def transformMOption[T](option: MOption[T], f: T => MOption[T]): MOption[MOption[T]] = {
                   |  option match {
                   |    case MSome(v) =>
                   |      val r = f(v)
                   |      r match {
                   |        case MSome(_) => return MSome(r)
                   |        case _ => return MNone()
                   |      }
                   |    case _ => return MNone()
                   |  }
                   |}"""
      }

      @pure def transformIS(indexType: ST): ST = {
        return st"""def transformIS$indexType[T](s: IS[$indexType, T], f: T => MOption[T]): MOption[IS[$indexType, T]] = {
                   |  val s2: MS[$indexType, T] = SI.toMS(s)
                   |  var changed: B = F
                   |  for (i <- s2.indices) {
                   |    val e: T = s(i)
                   |    val r: MOption[T] = f(e)
                   |    changed = changed || r.nonEmpty
                   |    s2(i) = r.getOrElse(e)
                   |  }
                   |  if (changed) {
                   |    return MSome(SM.toIS(s2))
                   |  } else {
                   |    return MNone()
                   |  }
                   |}"""
      }

      @pure def transformMS(indexType: ST): ST = {
        return st"""def transformIS$indexType[T](s: IS[$indexType, T], f: T => MOption[T]): MOption[IS[$indexType, T]] = {
                   |  var s2: MS[$indexType, T] = MS[Z, T]()
                   |  var changed: B = F
                   |  for (i <- s2.indices) {
                   |    val e: T = s(i)
                   |    val r: MOption[T] = f(e)
                   |    changed = changed || r.nonEmpty
                   |    s2 = s2 :+ r.getOrElse(e)
                   |  }
                   |  if (changed) {
                   |    return MSome(s2)
                   |  } else {
                   |    return MNone()
                   |  }
                   |}"""
      }

    }

  }

  val transformerGenKind: String = "TransformerGen"

  @datatype class AdtChild(i: Z,
                           transformMethodCaseMembers: ISZ[ST],
                           transformMethodCaseChanges: ISZ[ST],
                           transformMethodCaseUpdates: ISZ[ST])

}
