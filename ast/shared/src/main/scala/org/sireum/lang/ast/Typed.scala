// #Sireum
/*
 Copyright (c) 2017-2024, Robby, Kansas State University
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

@enum object MethodMode {
  "Method"
  "Spec"
  "Ext"
  "Just"
  "Constructor"
  "Copy"
  "Extractor"
  "ObjectConstructor"
  "Select"
  "Store"
}

@datatype trait Typed {

  @strictpure def isPureFun: B

  @pure def subst(map: HashMap[String, Typed]): Typed

  @pure override def hash: Z = {
    this match {
      case t: Typed.Name => return (t.ids, t.args).hash
      case t: Typed.TypeVar => return t.id.hash
      case t: Typed.Tuple => return t.args.hash
      case t: Typed.Method => return t.name.hash
      case t: Typed.Enum => return t.name.hash
      case t: Typed.Object => return t.name.hash
      case t: Typed.Methods => return t.methods.hash
      case t: Typed.Package => return t.name.hash
      case t: Typed.Fun => return (t.args, t.ret).hash
      case t: Typed.Fact => return t.name.hash
      case t: Typed.Theorem => return t.name.hash
      case t: Typed.Inv => return t.name.hash
    }
  }

  @pure def isEqual(other: Typed): B = {
    (this, other) match {
      case (t1: Typed.Name, t2: Typed.Name) =>
        if (t1.args.size != t2.args.size) {
          return F
        }
        if (t1.ids != t2.ids) {
          return F
        }
        for (i <- z"0" until t1.args.size) {
          if (t1.args(i) != t2.args(i)) {
            return F
          }
        }
        return T
      case (t1: Typed.Tuple, t2: Typed.Tuple) =>
        if (t1.args.size != t2.args.size) {
          return F
        }
        for (i <- z"0" until t1.args.size) {
          if (t1.args(i) != t2.args(i)) {
            return F
          }
        }
        return T
      case (t1: Typed.Fun, t2: Typed.Fun) =>
        if (t1.isPure != t2.isPure || t1.isByName != t2.isByName) {
          return F
        }
        if (t1.args.size != t2.args.size) {
          return F
        }
        if (t1.ret != t2.ret) {
          return F
        }
        for (i <- z"0" until t1.args.size) {
          if (t1.args(i) != t2.args(i)) {
            return F
          }
        }
        return T
      case (t1: Typed.TypeVar, t2: Typed.TypeVar) =>
        return t1.id == t2.id
      case (t1: Typed.Package, t2: Typed.Package) =>
        return t1.name == t2.name
      case (t1: Typed.Object, t2: Typed.Object) =>
        return t1.name == t2.name
      case (t1: Typed.Enum, t2: Typed.Enum) =>
        return t1.name == t2.name
      case (t1: Typed.Method, t2: Typed.Method) =>
        return t1.isInObject == t2.isInObject &&
          t1.mode == t2.mode &&
          t1.owner == t2.owner &&
          t1.name == t2.name &&
          t1.tpe == t2.tpe
      case (t1: Typed.Methods, t2: Typed.Methods) =>
        return t1.methods == t2.methods
      case _ =>
        if (this.isUnitType && other.isUnitType) {
          return T
        }
        return F
    }
  }

  @pure def normalized: Typed = {
    var map = HashMap.empty[String, Z]

    def normalizeFun(t: Typed.Fun): Typed.Fun = {
      var args = ISZ[Typed]()
      for (arg <- t.args) {
        val ta = normalize(arg)
        args = args :+ ta
      }
      val tr = normalize(t.ret)
      return t(args = args, ret = tr)
    }

    def normalizeMethod(t: Typed.Method): Typed.Method = {
      var newTypeParams = ISZ[String]()
      for (t <- t.typeParams) {
        val i: Z = map.get(t) match {
          case Some(n) => n
          case _ =>
            val n = map.size
            map = map + t ~> n
            n
        }
        newTypeParams = newTypeParams :+ s"$$$i"
      }
      return t(typeParams = newTypeParams, tpe = normalizeFun(t.tpe))
    }

    def normalize(t: Typed): Typed = {
      t match {
        case t: Typed.Name =>
          if (t.args.nonEmpty) {
            var args = ISZ[Typed]()
            for (arg <- t.args) {
              val ta = normalize(arg)
              args = args :+ ta
            }
            return t(args = args)
          } else {
            return t
          }
        case t: Typed.Tuple =>
          var args = ISZ[Typed]()
          for (arg <- t.args) {
            val ta = normalize(arg)
            args = args :+ ta
          }
          return t(args = args)
        case t: Typed.Fun => return normalizeFun(t)
        case t: Typed.TypeVar =>
          val i: Z = map.get(t.id) match {
            case Some(n) => n
            case _ =>
              val n = map.size
              map = map + t.id ~> n
              n
          }
          return t(id = s"$$$i")
        case t: Typed.Enum => return t
        case t: Typed.Method => return normalizeMethod(t)
        case t: Typed.Object => return t
        case t: Typed.Package => return t
        case t: Typed.Methods =>
          var newMethods = ISZ[Typed.Method]()
          for (m <- t.methods) {
            val newM = normalizeMethod(m)
            newMethods = newMethods :+ newM
          }
          return t(newMethods)
        case t: Typed.Fact => return t
        case t: Typed.Theorem => return t
        case t: Typed.Inv => return t
      }
    }

    val r = normalize(this)
    return r
  }

  @strictpure def isUnitType: B = this match {
    case t: Typed.Tuple if t.args.isEmpty => T
    case t: Typed.Name => t.args.isEmpty && t.ids == Typed.unit.ids
    case _ => F

  }

  @pure def typeVarSet: HashSSet[String] = {
    return HashSSet.empty[String] ++ collectTypeVars
  }

  @pure def collectTypeVars: ISZ[String]

  @pure def hasTypeVars: B
}

object Typed {

  @enum object VarKind {
    "Mutable"
    "Immutable"
    "Index"
  }

  @datatype class Name(val ids: ISZ[String], val args: ISZ[Typed]) extends Typed {
    @strictpure override def isPureFun: B = F

    @pure override def string: String = {
      return if (args.isEmpty) st"${(short(ids), ".")}".render
      else st"${(short(ids), ".")}[${(args, ", ")}]".render
    }

    @pure override def subst(m: HashMap[String, Typed]): Typed.Name = {
      if (m.isEmpty) {
        return this
      }
      return Name(ids, args.map(ta => ta.subst(m)))
    }

    @pure override def collectTypeVars: ISZ[String] = {
      return for (arg <- args; tv <- arg.collectTypeVars) yield tv
    }

    @pure override def hasTypeVars: B = {
      for (arg <- args if arg.hasTypeVars) {
        return T
      }
      return F
    }
  }

  @datatype class Tuple(val args: ISZ[Typed]) extends Typed {

    @strictpure override def isPureFun: B = F

    @pure override def string: String = {
      return st"(${(args, ", ")})".render
    }

    @pure override def subst(m: HashMap[String, Typed]): Typed.Tuple = {
      if (m.isEmpty) {
        return this
      }
      return Tuple(args.map(ta => ta.subst(m)))
    }

    @pure override def collectTypeVars: ISZ[String] = {
      return for (arg <- args; tv <- arg.collectTypeVars) yield tv
    }

    @pure override def hasTypeVars: B = {
      for (arg <- args if arg.hasTypeVars) {
        return T
      }
      return F
    }
  }

  @datatype class Fun(val isPure: B, val isByName: B, val args: ISZ[Typed], val ret: Typed) extends Typed {

    @strictpure override def isPureFun: B = isPure

    @pure override def string: String = {
      return if (isByName) st"=> $ret".render
      else if (isPure) st"((${(args, ", ")}) => $ret @pure)".render
      else st"(${(args, ", ")}) => $ret".render
    }

    @pure override def subst(m: HashMap[String, Typed]): Typed.Fun = {
      if (m.isEmpty) {
        return this
      }
      return Fun(isPure, isByName, args.map(ta => ta.subst(m)), ret.subst(m))
    }

    @pure override def collectTypeVars: ISZ[String] = {
      return (for (arg <- args; tv <- arg.collectTypeVars) yield tv) ++ ret.collectTypeVars
    }

    @pure override def hasTypeVars: B = {
      for (arg <- args if arg.hasTypeVars) {
        return T
      }
      return ret.hasTypeVars
    }

    @pure def curried: Typed.Fun = {
      if (isByName || args.size < 2) {
        return this
      }
      val rt: Typed = ret match {
        case ret: Typed.Fun => ret.curried
        case _ => ret
      }
      var r = Typed.Fun(isPureFun, F, ISZ(args(args.size - 1)), rt)
      for (i <- args.size - 2 to 0 by -1) {
        r = Typed.Fun(isPureFun, F, ISZ(args(i)), r)
      }
      return r
    }
  }

  @datatype class TypeVar(val id: String, val kind: Typed.VarKind.Type) extends Typed {
    @strictpure def isImmutable: B = kind != Typed.VarKind.Mutable
    @strictpure def isIndex: B = kind == Typed.VarKind.Index
    @strictpure override def isPureFun: B = F

    @pure override def subst(m: HashMap[String, Typed]): Typed = {
      if (m.isEmpty) {
        return this
      }
      m.get(id) match {
        case Some(t2) => return t2
        case _ => return this
      }
    }

    @pure override def string: String = {
      return id
    }

    @pure override def collectTypeVars: ISZ[String] = {
      return ISZ(id)
    }

    @pure override def hasTypeVars: B = {
      return T
    }
  }

  @datatype class Package(val name: ISZ[String]) extends Typed {

    @strictpure override def isPureFun: B = F

    @pure override def string: String = {
      return st"Package ${(name, ".")}".render
    }

    @pure override def subst(m: HashMap[String, Typed]): Typed.Package = {
      return this
    }

    @pure override def collectTypeVars: ISZ[String] = {
      return ISZ()
    }

    @pure override def hasTypeVars: B = {
      return F
    }
  }

  @datatype class Object(val owner: ISZ[String], val id: String) extends Typed {
    @strictpure def name: ISZ[String] = owner :+ id
    @strictpure override def isPureFun: B = F

    @pure override def string: String = {
      return st"Object ${(name, ".")}".render
    }

    @pure override def subst(m: HashMap[String, Typed]): Typed.Object = {
      return this
    }

    @pure override def collectTypeVars: ISZ[String] = {
      return ISZ()
    }

    @pure override def hasTypeVars: B = {
      return F
    }
  }

  @datatype class Enum(val name: ISZ[String]) extends Typed {

    @strictpure override def isPureFun: B = F

    @pure override def string: String = {
      return st"@enum ${(name, ".")}".render
    }

    @pure override def subst(m: HashMap[String, Typed]): Typed.Enum = {
      return this
    }

    @pure override def collectTypeVars: ISZ[String] = {
      return ISZ()
    }

    @pure override def hasTypeVars: B = {
      return F
    }
  }

  @datatype class Method(val isInObject: B,
                         val mode: MethodMode.Type,
                         val typeParams: ISZ[String],
                         val owner: ISZ[String],
                         val name: String,
                         val paramNames: ISZ[String],
                         val tpe: Typed.Fun) extends Typed {

    @strictpure override def isPureFun: B = F

    @pure override def string: String = {
      val mST = st"${(owner, ".")}${if (isInObject) "." else "#"}$name"
      mode match {
        case MethodMode.Extractor => return st"extractor of $mST".render
        case MethodMode.Ext => return st"@ext object method $mST".render
        case MethodMode.Just => return st"@just method $mST".render
        case MethodMode.Copy => return st"copy of $mST".render
        case MethodMode.Constructor => return st"constructor of $mST".render
        case MethodMode.Method => return st"method $mST".render
        case MethodMode.ObjectConstructor => return st"object constructor of $mST".render
        case MethodMode.Select => return st"indexing of $mST".render
        case MethodMode.Spec => return st"@spec method $mST".render
        case MethodMode.Store => return st"update of $mST".render
      }
    }

    @pure override def subst(m: HashMap[String, Typed]): Typed.Method = {
      if (m.isEmpty) {
        return this
      }
      return Method(isInObject, mode, typeParams, owner, name, paramNames, tpe.subst(m))
    }

    @pure override def collectTypeVars: ISZ[String] = {
      return tpe.collectTypeVars
    }

    @pure override def hasTypeVars: B = {
      return tpe.hasTypeVars
    }
  }

  @datatype class Methods(val methods: ISZ[Method]) extends Typed {

    @strictpure override def isPureFun: B = F

    @pure override def string: String = {
      return st"{ ${(methods, ", ")} }".render
    }

    @pure override def subst(m: HashMap[String, Typed]): Typed.Methods = {
      return this (methods.map(mt => mt.subst(m)))
    }

    @pure override def collectTypeVars: ISZ[String] = {
      return for (m <- methods; tv <- m.collectTypeVars) yield tv
    }

    @pure override def hasTypeVars: B = {
      for (m <- methods if m.hasTypeVars) {
        return T
      }
      return F
    }
  }

  @datatype class Fact(val owner: ISZ[String], val id: String) extends Typed {
    @strictpure def name: ISZ[String] = owner :+ id
    @strictpure override def isPureFun: B = F

    @pure override def string: String = {
      return st"Fact ${(name, ".")}".render
    }

    @pure override def subst(m: HashMap[String, Typed]): Typed.Fact = {
      return this
    }

    @pure override def collectTypeVars: ISZ[String] = {
      return ISZ()
    }

    @pure override def hasTypeVars: B = {
      return F
    }
  }


  @datatype class Theorem(val owner: ISZ[String], val id: String) extends Typed {
    @strictpure def name: ISZ[String] = owner :+ id
    @strictpure override def isPureFun: B = F

    @pure override def string: String = {
      return st"Theorem ${(name, ".")}".render
    }

    @pure override def subst(m: HashMap[String, Typed]): Typed.Theorem = {
      return this
    }

    @pure override def collectTypeVars: ISZ[String] = {
      return ISZ()
    }

    @pure override def hasTypeVars: B = {
      return F
    }
  }

  @datatype class Inv(val isInObject: B, val owner: ISZ[String], val id: String) extends Typed {
    @strictpure def name: ISZ[String] = owner :+ id
    @strictpure override def isPureFun: B = F

    @pure override def string: String = {
      return st"Inv ${(owner, ".")}#$id".render
    }

    @pure override def subst(m: HashMap[String, Typed]): Typed.Inv = {
      return this
    }

    @pure override def collectTypeVars: ISZ[String] = {
      return ISZ()
    }

    @pure override def hasTypeVars: B = {
      return F
    }
  }

  // @formatter:off
  val sireumName: ISZ[String] = ISZ("org", "sireum")
  val nothing: Typed.Name = Typed.Name(sireumName :+ "Nothing", ISZ())
  val nothingOpt: Option[Typed] = Some(nothing)
  val unit: Typed.Name = Typed.Name(sireumName :+ "Unit", ISZ())
  val unitOpt: Option[Typed] = Some(unit)
  val bName: ISZ[String] = sireumName :+ "B"
  val b: Typed.Name = Typed.Name(bName, ISZ())
  val bOpt: Option[Typed] = Some(b)
  val cName: ISZ[String] = sireumName :+ "C"
  val c: Typed.Name = Typed.Name(cName, ISZ())
  val cOpt: Option[Typed] = Some(c)
  val zName: ISZ[String] = sireumName :+ "Z"
  val z: Typed.Name = Typed.Name(zName, ISZ())
  val zOpt: Option[Typed] = Some(z)
  val z8: Typed.Name = Typed.Name(sireumName :+ "Z8", ISZ())
  val z16: Typed.Name = Typed.Name(sireumName :+ "Z16", ISZ())
  val z32: Typed.Name = Typed.Name(sireumName :+ "Z32", ISZ())
  val z64: Typed.Name = Typed.Name(sireumName :+ "Z64", ISZ())
  val n: Typed.Name = Typed.Name(sireumName :+ "N", ISZ())
  val n8: Typed.Name = Typed.Name(sireumName :+ "N8", ISZ())
  val n16: Typed.Name = Typed.Name(sireumName :+ "N16", ISZ())
  val n32: Typed.Name = Typed.Name(sireumName :+ "N32", ISZ())
  val n64: Typed.Name = Typed.Name(sireumName :+ "N64", ISZ())
  val s8: Typed.Name = Typed.Name(sireumName :+ "S8", ISZ())
  val s16: Typed.Name = Typed.Name(sireumName :+ "S16", ISZ())
  val s32: Typed.Name = Typed.Name(sireumName :+ "S32", ISZ())
  val s64: Typed.Name = Typed.Name(sireumName :+ "S64", ISZ())
  val u8: Typed.Name = Typed.Name(sireumName :+ "U8", ISZ())
  val u16: Typed.Name = Typed.Name(sireumName :+ "U16", ISZ())
  val u32: Typed.Name = Typed.Name(sireumName :+ "U32", ISZ())
  val u64: Typed.Name = Typed.Name(sireumName :+ "U64", ISZ())
  val f32Name: ISZ[String] = sireumName :+ "F32"
  val f32: Typed.Name = Typed.Name(f32Name, ISZ())
  val f32Opt: Option[Typed] = Some(f32)
  val f64Name: ISZ[String] = sireumName :+ "F64"
  val f64: Typed.Name = Typed.Name(f64Name, ISZ())
  val f64Opt: Option[Typed] = Some(f64)
  val rName: ISZ[String] = sireumName :+ "R"
  val r: Typed.Name = Typed.Name(rName, ISZ())
  val rOpt: Option[Typed] = Some(r)
  val stringName: ISZ[String] = sireumName :+ "String"
  val string: Typed.Name = Typed.Name(stringName, ISZ())
  val stringOpt: Option[Typed] = Some(string)
  val stName: ISZ[String] = sireumName :+ "ST"
  val st: Typed.Name = Typed.Name(stName, ISZ())
  val stOpt: Option[Typed] = Some(st)
  val stepIdName: ISZ[String] = sireumName :+ "StepId"
  val stepId: Typed.Name = Typed.Name(stepIdName, ISZ())
  val stepIdOpt: Option[Typed] = Some(stepId)
  val proc: Typed.Name = Typed.Name(sireumName :+ "OsProto" :+ "Proc", ISZ())
  val procOpt: Option[Typed] = Some(proc)
  val rsName: ISZ[String] = sireumName :+ "RS"
  val rs: Typed.Name = Typed.Name(rsName, ISZ())
  val rsOpt: Option[Typed] = Some(rs)

  val optionName: ISZ[String] = sireumName :+ "Option"
  val someName: ISZ[String] = sireumName :+ "Some"
  val noneName: ISZ[String] = sireumName :+ "None"
  val isName: ISZ[String] = sireumName :+ "IS"
  val msName: ISZ[String] = sireumName :+ "MS"
  val iszName: ISZ[String] = sireumName :+ "ISZ"
  val mszName: ISZ[String] = sireumName :+ "MSZ"
  val zsName: ISZ[String] = sireumName :+ "ZS"
  val jenName: ISZ[String] = sireumName :+ "Jen"
  val mjenName: ISZ[String] = sireumName :+ "MJen"
  val unapplySeqResOpt: Option[ResolvedInfo] = Some(ResolvedInfo.BuiltIn(ResolvedInfo.BuiltIn.Kind.UnapplySeq))
  val unapplyTupleResOpt: Option[ResolvedInfo] = Some(ResolvedInfo.BuiltIn(ResolvedInfo.BuiltIn.Kind.UnapplyTuple))

  val bConstructorType: Typed.Fun = Typed.Fun(T, F, ISZ(Typed.string), Typed.Name(optionName, ISZ(Typed.b)))
  val bConstructorMethodOpt: Option[Typed] = Some(Typed.Method(T, MethodMode.Constructor, ISZ(),
    sireumName, "B", ISZ(), Typed.bConstructorType))
  val bConstructorResOpt: Option[ResolvedInfo] = Some(ResolvedInfo.Method(T, MethodMode.Constructor, ISZ(),
    sireumName, "B", ISZ(), Some(Typed.bConstructorType), ISZ(), ISZ()))
  val cConstructorType: Typed.Fun = Typed.Fun(T, F, ISZ(Typed.string), Typed.Name(optionName, ISZ(Typed.c)))
  val cConstructorMethodOpt: Option[Typed] = Some(Typed.Method(T, MethodMode.Constructor, ISZ(),
    sireumName, "C", ISZ(), Typed.cConstructorType))
  val cConstructorResOpt: Option[ResolvedInfo] = Some(ResolvedInfo.Method(T, MethodMode.Constructor, ISZ(),
    sireumName, "C", ISZ(), Some(Typed.cConstructorType), ISZ(), ISZ()))
  val zConstructorType: Typed.Fun = Typed.Fun(T, F, ISZ(Typed.string), Typed.Name(optionName, ISZ(Typed.z)))
  val zConstructorMethodOpt: Option[Typed] = Some(Typed.Method(T, MethodMode.Constructor, ISZ(),
    sireumName, "Z", ISZ(), Typed.zConstructorType))
  val zConstructorResOpt: Option[ResolvedInfo] = Some(ResolvedInfo.Method(T, MethodMode.Constructor, ISZ(),
    sireumName, "Z", ISZ(), Some(Typed.zConstructorType), ISZ(), ISZ()))
  val z8ConstructorType: Typed.Fun = Typed.Fun(T, F, ISZ(Typed.string), Typed.Name(optionName, ISZ(Typed.z8)))
  val z8ConstructorMethodOpt: Option[Typed] = Some(Typed.Method(T, MethodMode.Constructor, ISZ(),
    sireumName, "Z8", ISZ(), Typed.z8ConstructorType))
  val z8ConstructorResOpt: Option[ResolvedInfo] = Some(ResolvedInfo.Method(T, MethodMode.Constructor, ISZ(),
    sireumName, "Z8", ISZ(), Some(Typed.z8ConstructorType), ISZ(), ISZ()))
  val z16ConstructorType: Typed.Fun = Typed.Fun(T, F, ISZ(Typed.string), Typed.Name(optionName, ISZ(Typed.z16)))
  val z16ConstructorMethodOpt: Option[Typed] = Some(Typed.Method(T, MethodMode.Constructor, ISZ(),
    sireumName, "Z16", ISZ(), Typed.z16ConstructorType))
  val z16ConstructorResOpt: Option[ResolvedInfo] = Some(ResolvedInfo.Method(T, MethodMode.Constructor, ISZ(),
    sireumName, "Z16", ISZ(), Some(Typed.z16ConstructorType), ISZ(), ISZ()))
  val z32ConstructorType: Typed.Fun = Typed.Fun(T, F, ISZ(Typed.string), Typed.Name(optionName, ISZ(Typed.z32)))
  val z32ConstructorMethodOpt: Option[Typed] = Some(Typed.Method(T, MethodMode.Constructor, ISZ(),
    sireumName, "Z32", ISZ(), Typed.z32ConstructorType))
  val z32ConstructorResOpt: Option[ResolvedInfo] = Some(ResolvedInfo.Method(T, MethodMode.Constructor, ISZ(),
    sireumName, "Z32", ISZ(), Some(Typed.z32ConstructorType), ISZ(), ISZ()))
  val z64ConstructorType: Typed.Fun = Typed.Fun(T, F, ISZ(Typed.string), Typed.Name(optionName, ISZ(Typed.z64)))
  val z64ConstructorMethodOpt: Option[Typed] = Some(Typed.Method(T, MethodMode.Constructor, ISZ(),
    sireumName, "Z64", ISZ(), Typed.z64ConstructorType))
  val z64ConstructorResOpt: Option[ResolvedInfo] = Some(ResolvedInfo.Method(T, MethodMode.Constructor, ISZ(),
    sireumName, "Z64", ISZ(), Some(Typed.z64ConstructorType), ISZ(), ISZ()))
  val nConstructorType: Typed.Fun = Typed.Fun(T, F, ISZ(Typed.string), Typed.Name(optionName, ISZ(Typed.n)))
  val nConstructorMethodOpt: Option[Typed] = Some(Typed.Method(T, MethodMode.Constructor, ISZ(),
    sireumName, "N", ISZ(), Typed.nConstructorType))
  val nConstructorResOpt: Option[ResolvedInfo] = Some(ResolvedInfo.Method(T, MethodMode.Constructor, ISZ(),
    sireumName, "N", ISZ(), Some(Typed.nConstructorType), ISZ(), ISZ()))
  val n8ConstructorType: Typed.Fun = Typed.Fun(T, F, ISZ(Typed.string), Typed.Name(optionName, ISZ(Typed.n8)))
  val n8ConstructorMethodOpt: Option[Typed] = Some(Typed.Method(T, MethodMode.Constructor, ISZ(),
    sireumName, "N8", ISZ(), Typed.n8ConstructorType))
  val n8ConstructorResOpt: Option[ResolvedInfo] = Some(ResolvedInfo.Method(T, MethodMode.Constructor, ISZ(),
    sireumName, "N8", ISZ(), Some(Typed.n8ConstructorType), ISZ(), ISZ()))
  val n16ConstructorType: Typed.Fun = Typed.Fun(T, F, ISZ(Typed.string), Typed.Name(optionName, ISZ(Typed.n16)))
  val n16ConstructorMethodOpt: Option[Typed] = Some(Typed.Method(T, MethodMode.Constructor, ISZ(),
    sireumName, "N16", ISZ(), Typed.n16ConstructorType))
  val n16ConstructorResOpt: Option[ResolvedInfo] = Some(ResolvedInfo.Method(T, MethodMode.Constructor, ISZ(),
    sireumName, "N16", ISZ(), Some(Typed.n16ConstructorType), ISZ(), ISZ()))
  val n32ConstructorType: Typed.Fun = Typed.Fun(T, F, ISZ(Typed.string), Typed.Name(optionName, ISZ(Typed.n32)))
  val n32ConstructorMethodOpt: Option[Typed] = Some(Typed.Method(T, MethodMode.Constructor, ISZ(),
    sireumName, "N32", ISZ(), Typed.n32ConstructorType))
  val n32ConstructorResOpt: Option[ResolvedInfo] = Some(ResolvedInfo.Method(T, MethodMode.Constructor, ISZ(),
    sireumName, "N32", ISZ(), Some(Typed.n32ConstructorType), ISZ(), ISZ()))
  val n64ConstructorType: Typed.Fun = Typed.Fun(T, F, ISZ(Typed.string), Typed.Name(optionName, ISZ(Typed.n64)))
  val n64ConstructorMethodOpt: Option[Typed] = Some(Typed.Method(T, MethodMode.Constructor, ISZ(),
    sireumName, "N64", ISZ(), Typed.n64ConstructorType))
  val n64ConstructorResOpt: Option[ResolvedInfo] = Some(ResolvedInfo.Method(T, MethodMode.Constructor, ISZ(),
    sireumName, "N64", ISZ(), Some(Typed.n64ConstructorType), ISZ(), ISZ()))
  val s8ConstructorType: Typed.Fun = Typed.Fun(T, F, ISZ(Typed.string), Typed.Name(optionName, ISZ(Typed.s8)))
  val s8ConstructorMethodOpt: Option[Typed] = Some(Typed.Method(T, MethodMode.Constructor, ISZ(),
    sireumName, "S8", ISZ(), Typed.s8ConstructorType))
  val s8ConstructorResOpt: Option[ResolvedInfo] = Some(ResolvedInfo.Method(T, MethodMode.Constructor, ISZ(),
    sireumName, "S8", ISZ(), Some(Typed.s8ConstructorType), ISZ(), ISZ()))
  val s16ConstructorType: Typed.Fun = Typed.Fun(T, F, ISZ(Typed.string), Typed.Name(optionName, ISZ(Typed.s16)))
  val s16ConstructorMethodOpt: Option[Typed] = Some(Typed.Method(T, MethodMode.Constructor, ISZ(),
    sireumName, "S16", ISZ(), Typed.s16ConstructorType))
  val s16ConstructorResOpt: Option[ResolvedInfo] = Some(ResolvedInfo.Method(T, MethodMode.Constructor, ISZ(),
    sireumName, "S16", ISZ(), Some(Typed.s16ConstructorType), ISZ(), ISZ()))
  val s32ConstructorType: Typed.Fun = Typed.Fun(T, F, ISZ(Typed.string), Typed.Name(optionName, ISZ(Typed.s32)))
  val s32ConstructorMethodOpt: Option[Typed] = Some(Typed.Method(T, MethodMode.Constructor, ISZ(),
    sireumName, "S32", ISZ(), Typed.s32ConstructorType))
  val s32ConstructorResOpt: Option[ResolvedInfo] = Some(ResolvedInfo.Method(T, MethodMode.Constructor, ISZ(),
    sireumName, "S32", ISZ(), Some(Typed.s32ConstructorType), ISZ(), ISZ()))
  val s64ConstructorType: Typed.Fun = Typed.Fun(T, F, ISZ(Typed.string), Typed.Name(optionName, ISZ(Typed.s64)))
  val s64ConstructorMethodOpt: Option[Typed] = Some(Typed.Method(T, MethodMode.Constructor, ISZ(),
    sireumName, "S64", ISZ(), Typed.s64ConstructorType))
  val s64ConstructorResOpt: Option[ResolvedInfo] = Some(ResolvedInfo.Method(T, MethodMode.Constructor, ISZ(),
    sireumName, "S64", ISZ(), Some(Typed.s64ConstructorType), ISZ(), ISZ()))
  val u8ConstructorType: Typed.Fun = Typed.Fun(T, F, ISZ(Typed.string), Typed.Name(optionName, ISZ(Typed.u8)))
  val u8ConstructorMethodOpt: Option[Typed] = Some(Typed.Method(T, MethodMode.Constructor, ISZ(),
    sireumName, "U8", ISZ(), Typed.u8ConstructorType))
  val u8ConstructorResOpt: Option[ResolvedInfo] = Some(ResolvedInfo.Method(T, MethodMode.Constructor, ISZ(),
    sireumName, "U8", ISZ(), Some(Typed.u8ConstructorType), ISZ(), ISZ()))
  val u16ConstructorType: Typed.Fun = Typed.Fun(T, F, ISZ(Typed.string), Typed.Name(optionName, ISZ(Typed.u16)))
  val u16ConstructorMethodOpt: Option[Typed] = Some(Typed.Method(T, MethodMode.Constructor, ISZ(),
    sireumName, "U16", ISZ(), Typed.u16ConstructorType))
  val u16ConstructorResOpt: Option[ResolvedInfo] = Some(ResolvedInfo.Method(T, MethodMode.Constructor, ISZ(),
    sireumName, "U16", ISZ(), Some(Typed.u16ConstructorType), ISZ(), ISZ()))
  val u32ConstructorType: Typed.Fun = Typed.Fun(T, F, ISZ(Typed.string), Typed.Name(optionName, ISZ(Typed.u32)))
  val u32ConstructorMethodOpt: Option[Typed] = Some(Typed.Method(T, MethodMode.Constructor, ISZ(),
    sireumName, "U32", ISZ(), Typed.u32ConstructorType))
  val u32ConstructorResOpt: Option[ResolvedInfo] = Some(ResolvedInfo.Method(T, MethodMode.Constructor, ISZ(),
    sireumName, "U32", ISZ(), Some(Typed.u32ConstructorType), ISZ(), ISZ()))
  val u64ConstructorType: Typed.Fun = Typed.Fun(T, F, ISZ(Typed.string), Typed.Name(optionName, ISZ(Typed.u64)))
  val u64ConstructorMethodOpt: Option[Typed] = Some(Typed.Method(T, MethodMode.Constructor, ISZ(),
    sireumName, "U64", ISZ(), Typed.u64ConstructorType))
  val u64ConstructorResOpt: Option[ResolvedInfo] = Some(ResolvedInfo.Method(T, MethodMode.Constructor, ISZ(),
    sireumName, "U64", ISZ(), Some(Typed.u64ConstructorType), ISZ(), ISZ()))
  val f32ConstructorType: Typed.Fun = Typed.Fun(T, F, ISZ(Typed.string), Typed.Name(optionName, ISZ(Typed.f32)))
  val f32ConstructorMethodOpt: Option[Typed] = Some(Typed.Method(T, MethodMode.Constructor, ISZ(),
    sireumName, "F32", ISZ(), Typed.f32ConstructorType))
  val f32ConstructorResOpt: Option[ResolvedInfo] = Some(ResolvedInfo.Method(T, MethodMode.Constructor, ISZ(),
    sireumName, "F32", ISZ(), Some(Typed.f32ConstructorType), ISZ(), ISZ()))
  val f64ConstructorType: Typed.Fun = Typed.Fun(T, F, ISZ(Typed.string), Typed.Name(optionName, ISZ(Typed.f64)))
  val f64ConstructorMethodOpt: Option[Typed] = Some(Typed.Method(T, MethodMode.Constructor, ISZ(),
    sireumName, "F64", ISZ(), Typed.f64ConstructorType))
  val f64ConstructorResOpt: Option[ResolvedInfo] = Some(ResolvedInfo.Method(T, MethodMode.Constructor, ISZ(),
    sireumName, "F64", ISZ(), Some(Typed.f64ConstructorType), ISZ(), ISZ()))
  val rConstructorType: Typed.Fun = Typed.Fun(T, F, ISZ(Typed.string), Typed.Name(optionName, ISZ(Typed.r)))
  val rConstructorMethodOpt: Option[Typed] = Some(Typed.Method(T, MethodMode.Constructor, ISZ(),
    sireumName, "R", ISZ(), Typed.rConstructorType))
  val rConstructorResOpt: Option[ResolvedInfo] = Some(ResolvedInfo.Method(T, MethodMode.Constructor, ISZ(),
    sireumName, "R", ISZ(), Some(Typed.rConstructorType), ISZ(), ISZ()))

  val basicConstructorMap: HashMap[ISZ[String], (Option[Typed], Option[ResolvedInfo])] =
    HashMap.emptyInit[ISZ[String], (Option[Typed], Option[ResolvedInfo])](30) ++ ISZ(
      b.ids ~> ((bConstructorMethodOpt, bConstructorResOpt)),
      c.ids ~> ((cConstructorMethodOpt, cConstructorResOpt)),
      z.ids ~> ((zConstructorMethodOpt, zConstructorResOpt)),
      z8.ids ~> ((z8ConstructorMethodOpt, z8ConstructorResOpt)),
      z16.ids ~> ((z16ConstructorMethodOpt, z16ConstructorResOpt)),
      z32.ids ~> ((z32ConstructorMethodOpt, z32ConstructorResOpt)),
      z64.ids ~> ((z64ConstructorMethodOpt, z64ConstructorResOpt)),
      n.ids ~> ((nConstructorMethodOpt, nConstructorResOpt)),
      n8.ids ~> ((n8ConstructorMethodOpt, n8ConstructorResOpt)),
      n16.ids ~> ((n16ConstructorMethodOpt, n16ConstructorResOpt)),
      n32.ids ~> ((n32ConstructorMethodOpt, n32ConstructorResOpt)),
      n64.ids ~> ((n64ConstructorMethodOpt, n64ConstructorResOpt)),
      s8.ids ~> ((s8ConstructorMethodOpt, s8ConstructorResOpt)),
      s16.ids ~> ((s16ConstructorMethodOpt, s16ConstructorResOpt)),
      s32.ids ~> ((s32ConstructorMethodOpt, s32ConstructorResOpt)),
      s64.ids ~> ((s64ConstructorMethodOpt, s64ConstructorResOpt)),
      u8.ids ~> ((u8ConstructorMethodOpt, u8ConstructorResOpt)),
      u16.ids ~> ((u16ConstructorMethodOpt, u16ConstructorResOpt)),
      u32.ids ~> ((u32ConstructorMethodOpt, u32ConstructorResOpt)),
      u64.ids ~> ((u64ConstructorMethodOpt, u64ConstructorResOpt)),
      f32.ids ~> ((f32ConstructorMethodOpt, f32ConstructorResOpt)),
      f64.ids ~> ((f64ConstructorMethodOpt, f64ConstructorResOpt)),
      r.ids ~> ((rConstructorMethodOpt, rConstructorResOpt))
    )
  // @formatter:on
  val floatingPointTypes: HashSet[Typed] = HashSet ++ ISZ[Typed](
    f32, f64
  )
  val builtInTypes: HashSet[Typed] = HashSet ++ ISZ[Typed](
    b, c, z, string, r
  ) ++ floatingPointTypes.elements

  @pure def short(ids: ISZ[String]): ISZ[String] = {
    return if (ids.size >= 2 && ids(0) == string"org" && ids(1) == string"sireum") ops.ISZOps(ids).drop(2)
    else ids
  }

  @pure def substOpt(tOpt: Option[Typed], substMap: HashMap[String, Typed]): Option[Typed] = {
    tOpt match {
      case Some(t) if substMap.nonEmpty =>
        val newT = t.subst(substMap)
        return if (newT == t) tOpt else Some(newT)
      case _ => return tOpt
    }
  }
}

