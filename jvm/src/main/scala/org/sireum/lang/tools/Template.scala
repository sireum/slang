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

@sig trait Template {
  def main(licenseOpt: Option[String],
           fileUriOpt: Option[String],
           packageNameOpt: Option[String],
           name: String,
           preMethods: ISZ[ST],
           postMethod: ISZ[ST],
           transformHelper: ISZ[ST],
           transformMethod: ISZ[ST]): ST

  def preMethodRoot(typeName: String,
                    tpe: String,
                    preMethodRootCases: ISZ[ST]): ST

  def preMethodRootCase(typeName: String, tpe: String): ST

  def preMethod(typeName: String, tpe: String, superType: String): ST

  def postMethodRoot(typeName: String, tpe: String, postMethodRootCases: ISZ[ST]): ST

  def postMethodRootCase(typeName: String, tpe: String): ST

  def postMethod(typeName: String, tpe: String, superType: String): ST

  def transformMethod(typeName: String,
                      tpe: String,
                      transformMethodMatch: ST,
                      preAdapt: Option[ST],
                      postAdap: Option[ST]): ST

  def preAdaptDown(tpe: String): ST

  def preAdaptUp(tpe: String): ST

  def postAdaptDown(tpe: String): ST

  def postAdaptUp(tpe: String): ST

  def transformMethodMatch(tpe: String,
                           transformMethodCases: ISZ[ST]): ST

  def transformMethodMatchSimple(transformMethodCaseMembers: ISZ[ST],
                                 transformMethodCaseChanges: ISZ[ST],
                                 transformMethodCaseUpdates: ISZ[ST]): ST

  def transformMethodCase(tpe: String,
                          transformMethodCaseMembers: ISZ[ST],
                          transformMethodCaseChanges: ISZ[ST],
                          transformMethodCaseUpdates: ISZ[ST]): ST

  def transformMethodCaseMember(i: Z,
                                typeName: String,
                                tpe: String,
                                fieldName: String): ST

  def transformMethodCaseMemberIS(i: Z,
                                  indexType: String,
                                  typeName: String,
                                  tpe: String,
                                  fieldName: String): ST

  def transformMethodCaseMemberMS(i: Z,
                                  indexType: String,
                                  typeName: String,
                                  tpe: String,
                                  fieldName: String): ST

  def transformMethodCaseMemberOption(i: Z,
                                      typeName: String,
                                      tpe: String,
                                      fieldName: String): ST

  def transformMethodCaseMemberMOption(i: Z,
                                       typeName: Z,
                                       tpe: String, fieldName: String): ST

  def transformMethodCaseChanged(i: Z): ST

  def transformMethodCaseUpdate(i: Z, fieldName: String): ST

  def transformOption: ST

  def transformMOption(): ST

  def transformIS(indexType: String): ST

  def transformMS(indexType: String): ST
}

object Template {
  val empty: ST = st""

  @pure def opt(stOpt: Option[ST]): ST = {
    return stOpt.getOrElse(empty)
  }

  @pure def stOpt[T](tOpt: Option[T], f: T => ST): ST = {
    return opt(tOpt.map(f))
  }

  @sig class MTransformerTemplate extends Template {
    def main(licenseOpt: Option[String],
             fileUriOpt: Option[String],
             packageNameOpt: Option[String],
             name: String,
             preMethods: ISZ[ST],
             postMethods: ISZ[ST],
             transformHelpers: ISZ[ST],
             transformMethods: ISZ[ST]): ST = {
      val license: ST = stOpt(licenseOpt, (text: String) =>
        st"""/*
            | $text
            | */)
            |""")
      val fileUri: ST = stOpt(fileUriOpt, (text: String) =>
        st"// This file is auto-generated from $text\n")
      val packageName: ST = stOpt(fileUriOpt, (text: String) =>
        st"package $text\n")
      return st"""// #Sireum
                 |// @formatter:off
                 |
                 |$license
                 |$fileUri
                 |$packageName
                 |
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

    def preMethodRoot(typeName: String,
                      tpe: String,
                      preMethodRootCases: ISZ[ST]): ST = {
      return st"""def pre$typeName(o: $tpe): PreResult[$tpe] = {
                 |  o match {
                 |    ${(preMethodRootCases, "\n")}
                 |  }
                 |}"""
    }

    def preMethodRootCase(typeName: String, tpe: String): ST = {
      return st"case o: $tpe => return pre$typeName(o)"
    }

    def preMethod(typeName: String, tpe: String, superType: String): ST = {
      return st"""def pre$typeName(o: $tpe): PreResult[$superType] = {
                 |  return PreResult(T, MNone())
                 |}"""
    }

    def postMethodRoot(typeName: String, tpe: String, postMethodRootCases: ISZ[ST]): ST = {
      return st"""def post$typeName(o: $tpe): MOption[$tpe] = {
                 |  o match {
                 |    ${(postMethodRootCases, "\n")}
                 |  }
                 |}"""
    }

    def postMethodRootCase(typeName: String, tpe: String): ST = {
      return st"case o: $tpe => return post$typeName(o)"
    }

    def postMethod(typeName: String, tpe: String, superType: String): ST = {
      return st"""def post$typeName(o: $tpe): MOption[$superType] = {
                 |  return MNone()
                 |}"""
    }

    def transformMethod(typeName: String,
                        tpe: String,
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

    def preAdaptDown(tpe: String): ST = {
      return st"""match {
                 |  case PreResult(continue, MSome(r: $tpe)) => PreResult(continue, MSome[$tpe](r))
                 |  case _ => assert(F); PreResult(F, MNone[$tpe]())
                 |}"""
    }

    def preAdaptUp(tpe: String): ST = {
      return st"""match {
                 |  case PreResult(continue, MSome(r)) => PreResult(continue, MSome[$tpe](r))
                 |  case PreResult(continue, _) => PreResult(continue, MNone[$tpe]())
                 |}"""
    }

    def postAdaptDown(tpe: String): ST = {
      return st"""match {
                 |  case MSome(result: $tpe) => MSome[$tpe](result)
                 |  case _ => assert(F); MNone[$tpe]()
                 |}"""
    }

    def postAdaptUp(tpe: String): ST = {
      return st"""match {
                 |  case MSome(r) => MSome[$tpe](r)
                 |  case _ => MNone[$tpe]()
                 |}"""
    }

    def transformMethodMatch(tpe: String,
                             transformMethodCases: ISZ[ST]): ST = {
      return st"""val rOpt: MOption[$tpe] = o2 match {
                 |  ${(transformMethodCases, "\n")}
                 |}
                 |rOpt"""
    }

    def transformMethodMatchSimple(transformMethodCaseMembers: ISZ[ST],
                                   transformMethodCaseChanges: ISZ[ST],
                                   transformMethodCaseUpdates: ISZ[ST]): ST = {
      val updates =
        if (transformMethodCaseUpdates.isEmpty) empty
        else st"(${(transformMethodCaseUpdates, ", ")})"
      return st"""${(transformMethodCaseMembers, "\n")}
                 |if (hasChanged$transformMethodCaseChanges)
                 |  MSome(o2$updates)
                 |else
                 |  MNone()"""
    }

    def transformMethodCase(tpe: String,
                            transformMethodCaseMembers: ISZ[ST],
                            transformMethodCaseChanges: ISZ[ST],
                            transformMethodCaseUpdates: ISZ[ST]): ST = {
      val updates =
        if (transformMethodCaseUpdates.isEmpty) empty
        else st"(${(transformMethodCaseUpdates, ", ")})"
      return st"""case o2: $tpe =>
                 |  ${(transformMethodCaseMembers, "\n")}
                 |  if (hasChanged$transformMethodCaseChanges)
                 |    MSome(o2$updates)
                 |  else
                 |    MNone()"""
    }

    def transformMethodCaseMember(i: Z,
                                  typeName: String,
                                  tpe: String,
                                  fieldName: String): ST = {
      return st"val r$i: MOption[$tpe] = transform$typeName(o2.$fieldName)"
    }

    def transformMethodCaseMemberIS(i: Z,
                                    indexType: String,
                                    typeName: String,
                                    tpe: String,
                                    fieldName: String): ST = {
      return st"val r$i: MOption[IS[$indexType, $tpe]] = transformIS$indexType(o2.$fieldName, transform$typeName _)"
    }

    def transformMethodCaseMemberMS(i: Z,
                                    indexType: String,
                                    typeName: String,
                                    tpe: String,
                                    fieldName: String): ST = {
      return st"val r$i: MOption[MS[$indexType, $tpe]] = transformMS$indexType(o2.$fieldName, transform$typeName _)"
    }

    def transformMethodCaseMemberOption(i: Z,
                                        typeName: String,
                                        tpe: String,
                                        fieldName: String): ST = {
      return st"val r$i: MOption[Option[$tpe]] = transformOption(o2.$fieldName, transform$typeName _)"
    }

    def transformMethodCaseMemberMOption(i: Z,
                                         typeName: Z,
                                         tpe: String, fieldName: String): ST = {
      return st"val r$i: MOption[MOption[$tpe]] = transformOption(o2.$fieldName, transform$typeName _)"
    }

    def transformMethodCaseChanged(i: Z): ST = {
      return st" || r$i.nonEmpty"
    }

    def transformMethodCaseUpdate(i: Z, fieldName: String): ST = {
      return st"$fieldName = r$i.getOrElse(o2.$fieldName)"
    }

    def transformOption: ST = {
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

    def transformMOption(): ST = {
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

    def transformIS(indexType: String): ST = {
      return st"""def transformIS$indexType[T](s: IS[$indexType, T], f: T => MOption[T]): MOption[IS[$indexType, T]] = {
                 |  val s2: MS[$indexType, T] = SI.toMS(s)
                 |  var changed: B = F
                 |  for (i <- s2.indices) {
                 |    val e: T = s(i)
                 |    val r: MOption[T] = f(e)
                 |    changed = changed | r.nonEmpty
                 |    s2(i) = r.getOrElse(e)
                 |  }
                 |  if (changed) {
                 |    return MSome(SM.toIS(s2))
                 |  } else {
                 |    return MNone()
                 |  }
                 |}"""
    }

    def transformMS(indexType: String): ST = {
      return st"""def transformIS$indexType[T](s: IS[$indexType, T], f: T => MOption[T]): MOption[IS[$indexType, T]] = {
                 |  var s2: MS[$indexType, T] = MS[Z, T]()
                 |  var changed: B = F
                 |  for (i <- s2.indices) {
                 |    val e: T = s(i)
                 |    val r: MOption[T] = f(e)
                 |    changed = changed | r.nonEmpty
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
