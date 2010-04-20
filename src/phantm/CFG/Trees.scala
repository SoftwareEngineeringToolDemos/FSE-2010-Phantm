package phantm.CFG
import phantm.analyzer.Symbols._
import phantm.AST.Trees._
import phantm.Positional

object Trees {

  sealed abstract class CFGTree extends Positional {
    override def toString = stringRepr(this)
  }

  var nextID = 0;

  def nextStatementID = {
    nextID += 1
    nextID
  }

  sealed abstract class CFGStatement extends CFGTree {
    self =>
    var uniqueID: Int = nextStatementID
  }

  case class CFGUnset(variable: CFGVariable) extends CFGStatement
  case class CFGAssign(variable: CFGVariable, value: CFGSimpleValue) extends CFGStatement

  case class CFGAssignUnary(variable: CFGVariable,
                            unOp: CFGUnaryOperator,
                            expr: CFGSimpleValue) extends CFGStatement

  case class CFGAssignBinary(variable: CFGVariable,
                             lhs: CFGSimpleValue,
                             binOp: CFGBinaryOperator,
                             rhs: CFGSimpleValue) extends CFGStatement


  case class CFGError() extends CFGStatement {
    override def toString = stringRepr(this);
  }

  case class CFGAssume(lhs: CFGSimpleValue, relOp: CFGRelationalOperator, rhs: CFGSimpleValue) extends CFGStatement
  case class CFGPrint(rhs: CFGSimpleValue) extends CFGStatement

  case object CFGSkip extends CFGStatement

  sealed abstract class CFGExpression extends CFGStatement
  sealed abstract class CFGSimpleValue extends CFGExpression
  sealed abstract class CFGVariable extends CFGSimpleValue
  sealed abstract class CFGSimpleVariable extends CFGVariable

  /** Used to represent the identifiers from the original program. */
  case class CFGIdentifier(symbol: VariableSymbol) extends CFGSimpleVariable with Symbolic {
    override def getSymbol = symbol
    override def setSymbol(s: Symbol) = this
    override def toString = stringRepr(this)
  }

  /** Used to represent intermediate values (fresh identifiers). */
  case class CFGTempID(value: String) extends CFGSimpleVariable
  case class CFGClassProperty(symbol: PropertySymbol) extends CFGSimpleVariable

  case class CFGVariableVar(v: CFGSimpleValue) extends CFGVariable
  case class CFGArrayEntry(arr: CFGSimpleValue, index: CFGSimpleValue) extends CFGVariable
  case class CFGNextArrayEntry(arr: CFGSimpleValue) extends CFGVariable
  case class CFGObjectProperty(obj: CFGSimpleValue, index: CFGSimpleValue) extends CFGVariable
  case class CFGVariableClassProperty(cl: ClassRef, index: CFGSimpleValue) extends CFGVariable
  case class CFGNone() extends CFGVariable

  case class CFGLong(value: Long) extends CFGSimpleValue
  case class CFGFloat(value: Float) extends CFGSimpleValue
  case class CFGString(value: String) extends CFGSimpleValue
  case class CFGTrue() extends CFGSimpleValue
  case class CFGAny() extends CFGSimpleValue
  case class CFGFalse() extends CFGSimpleValue
  case class CFGNull() extends CFGSimpleValue
  case class CFGThis() extends CFGSimpleValue
  case class CFGEmptyArray() extends CFGSimpleValue
  case class CFGInstanceof(lhs: CFGSimpleValue, cl: ClassRef) extends CFGSimpleValue
  case class CFGCast(to: CastType, e: CFGSimpleValue) extends CFGSimpleValue
  case class CFGArrayNext(ar: CFGSimpleValue) extends CFGSimpleValue
  case class CFGArrayCurElement(ar: CFGSimpleValue) extends CFGSimpleValue
  case class CFGArrayCurKey(ar: CFGSimpleValue) extends CFGSimpleValue
  case class CFGArrayCurIsValid(ar: CFGSimpleValue) extends CFGSimpleValue

  case class CFGConstant(cs: ConstantSymbol) extends CFGSimpleValue
  case class CFGClassConstant(cs: ClassConstantSymbol) extends CFGSimpleValue
  case class CFGVariableClassConstant(cl: ClassRef, name: Identifier) extends CFGSimpleValue

  case class CFGTernary(cond: CFGSimpleValue,
                         then: CFGSimpleValue,
                         elze: CFGSimpleValue) extends CFGSimpleValue

  case class CFGFunctionCall(id: Identifier,
                             params: List[CFGSimpleValue]) extends CFGSimpleValue

  case class CFGStaticMethodCall(cl: ClassRef,
                                 id: Identifier,
                                 params: List[CFGSimpleValue]) extends CFGSimpleValue

  case class CFGMethodCall(receiver: CFGSimpleValue,
                                 id: Identifier,
                                 params: List[CFGSimpleValue]) extends CFGSimpleValue

  case class CFGNew(cl: ClassRef, params: List[CFGSimpleValue]) extends CFGSimpleValue
  case class CFGClone(obj: CFGSimpleValue) extends CFGSimpleValue

  sealed abstract class CFGBinaryOperator
  sealed trait CFGRelationalOperator

  case object PLUS extends CFGBinaryOperator { override def toString = "+" }
  case object MINUS extends CFGBinaryOperator { override def toString = "-" }
  case object MULT extends CFGBinaryOperator { override def toString = "*" }
  case object DIV extends CFGBinaryOperator { override def toString = "/" }
  case object CONCAT extends CFGBinaryOperator { override def toString = "." }
  case object MOD extends CFGBinaryOperator { override def toString = "%" }
  case object INSTANCEOF extends CFGBinaryOperator { override def toString = "instanceof" }

  case object BOOLEANAND extends CFGBinaryOperator { override def toString = "&&" }
  case object BOOLEANOR extends CFGBinaryOperator { override def toString = "||" }
  case object BOOLEANXOR extends CFGBinaryOperator { override def toString = "xor" }

  case object BITWISEAND extends CFGBinaryOperator { override def toString = "&" }
  case object BITWISEOR extends CFGBinaryOperator { override def toString = "|" }
  case object BITWISEXOR extends CFGBinaryOperator { override def toString = "^" }

  case object SHIFTLEFT extends CFGBinaryOperator { override def toString = "<<" }
  case object SHIFTRIGHT extends CFGBinaryOperator { override def toString = ">>" }

  case object LT extends CFGBinaryOperator with CFGRelationalOperator { override def toString = "<" }
  case object LEQ extends CFGBinaryOperator with CFGRelationalOperator { override def toString = "<=" }
  case object GEQ extends CFGBinaryOperator with CFGRelationalOperator { override def toString = ">=" }
  case object GT extends CFGBinaryOperator with CFGRelationalOperator { override def toString = ">" }

  case object EQUALS extends CFGBinaryOperator with CFGRelationalOperator { override def toString = "==" }
  case object IDENTICAL extends CFGBinaryOperator with CFGRelationalOperator { override def toString = "===" }
  case object NOTEQUALS extends CFGBinaryOperator with CFGRelationalOperator { override def toString = "!=" }
  case object NOTIDENTICAL extends CFGBinaryOperator with CFGRelationalOperator { override def toString = "!==" }

  sealed abstract class CFGUnaryOperator
  case object BOOLEANNOT extends CFGUnaryOperator { override def toString = "!" }
  case object BITSIWENOT extends CFGUnaryOperator { override def toString = "~" }
  case object PREINC extends CFGUnaryOperator { override def toString = "++ (pre)" }
  case object POSTINC extends CFGUnaryOperator { override def toString = "++ (post)" }
  case object PREDEC extends CFGUnaryOperator { override def toString = "-- (pre)" }
  case object POSTDEC extends CFGUnaryOperator { override def toString = "-- (post)" }
  case object SILENCE extends CFGUnaryOperator { override def toString = "@" }

  def stringRepr(tree: CFGTree): String = {
    val assOp = " := "

    tree match {
      case CFGAssignUnary(v, u, e) => v + assOp + u + e
      case CFGAssignBinary(v, l, b, r) => v + assOp + l + " " + b + " " + r
      case CFGStaticMethodCall(r, mid, p) => r + "::" + mid.value + p.mkString("(", ", ", ")")
      case CFGMethodCall(r, mid, p) => r + "->" + mid.value + p.mkString("(", ", ", ")")
      case CFGFunctionCall(fid, p) => fid.value + p.mkString("(", ", ", ")")
      case CFGConstant(cs) => cs.name
      case CFGClassConstant(cs) => cs.cs.name + "::" + cs.name
      case CFGVariableClassConstant(cl, cid) => cl + "::" + cid.value
      case CFGTernary(i, then, elze) => i + " ? " + then + " : " + elze
      case CFGAssign(v, e) => v + assOp + e
      case CFGCast(to, e) => "("+to+")" + e
      case CFGSkip => "..."
      case CFGAssume(l, o, r) => "[" + l + o + r + "]"
      case CFGPrint(v) => "print("+v+")"
      case CFGUnset(v) => "unset("+v+")"
      case CFGString(value) => "\"" + value + "\""
      case CFGLong(value) => value.toString
      case CFGFloat(value) => value.toString
      case CFGNew(tpe, params) => "new " + tpe + params.mkString("(", ", ", ")")
      case CFGClone(obj) => "clone " + obj
      case CFGTrue() => "true"
      case CFGNull() => "null"
      case CFGEmptyArray() => "array()"
      case CFGFalse() => "false"
      case CFGAny() => "any"
      case CFGNone() => "none"
      case CFGError() => "error"
      case CFGThis() => "this"
      case CFGArrayNext(a) => a + ".next"
      case CFGArrayCurKey(a) => a + ".key"
      case CFGArrayCurElement(a) => a + ".current"
      case CFGArrayCurIsValid(a) => a + ".valid"
      case CFGInstanceof(obj, StaticClassRef(_, _, id)) => obj + " instanceof "+id.value
      case CFGInstanceof(obj, _) => obj + " instanceof ?"
      case CFGIdentifier(sym) => sym.name
      case CFGTempID(value) => value
      case CFGVariableVar(v) => "*("+v+")"
      case CFGArrayEntry(arr, index) => arr+"["+index+"]"
      case CFGNextArrayEntry(arr) => arr+"[]"
      case CFGObjectProperty(obj, prop) => obj+"->"+prop;
      case CFGClassProperty(sym) => sym.cs.name+"::$"+sym.name;
      case CFGVariableClassProperty(cl, prop) => cl+"::$"+prop;
    }
  }
}