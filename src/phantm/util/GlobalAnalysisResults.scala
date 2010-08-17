package phantm.util

import phantm.types.{Type, TypeEnvironment, ObjectStore}
import phantm.symbols.FunctionSymbol

class GlobalAnalysisResults {
    var inlineCache: Map[FunctionSymbol, Map[(List[Type], ObjectStore), (Type, ObjectStore)]] = Map().withDefaultValue(Map())
    var globalCalls: Map[FunctionSymbol, Map[String, TypeEnvironment]] = Map().withDefaultValue(Map())
    var endGlobals: Option[TypeEnvironment] = None
    var reachableFromMain: Map[FunctionSymbol, Set[FunctionSymbol]] = Map().withDefaultValue(Set())
    var summary: Map[FunctionSymbol, Int] = Map()
    var inlineStack: Set[FunctionSymbol] = Set()
}
