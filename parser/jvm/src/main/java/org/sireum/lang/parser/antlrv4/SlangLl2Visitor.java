// Generated from /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangLl2.g4 by ANTLR 4.13.2
package org.sireum.lang.parser.antlrv4;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link SlangLl2Parser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface SlangLl2Visitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link SlangLl2Parser#file}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFile(SlangLl2Parser.FileContext ctx);
	/**
	 * Visit a parse tree produced by {@link SlangLl2Parser#expFile}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpFile(SlangLl2Parser.ExpFileContext ctx);
	/**
	 * Visit a parse tree produced by {@link SlangLl2Parser#stmtFile}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStmtFile(SlangLl2Parser.StmtFileContext ctx);
	/**
	 * Visit a parse tree produced by {@link SlangLl2Parser#program}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProgram(SlangLl2Parser.ProgramContext ctx);
	/**
	 * Visit a parse tree produced by {@link SlangLl2Parser#imprt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitImprt(SlangLl2Parser.ImprtContext ctx);
	/**
	 * Visit a parse tree produced by {@link SlangLl2Parser#importSuffix}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitImportSuffix(SlangLl2Parser.ImportSuffixContext ctx);
	/**
	 * Visit a parse tree produced by {@link SlangLl2Parser#importRename}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitImportRename(SlangLl2Parser.ImportRenameContext ctx);
	/**
	 * Visit a parse tree produced by {@link SlangLl2Parser#mainMember}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMainMember(SlangLl2Parser.MainMemberContext ctx);
	/**
	 * Visit a parse tree produced by {@link SlangLl2Parser#pkg}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPkg(SlangLl2Parser.PkgContext ctx);
	/**
	 * Visit a parse tree produced by {@link SlangLl2Parser#init}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInit(SlangLl2Parser.InitContext ctx);
	/**
	 * Visit a parse tree produced by {@link SlangLl2Parser#member}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMember(SlangLl2Parser.MemberContext ctx);
	/**
	 * Visit a parse tree produced by {@link SlangLl2Parser#mod}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMod(SlangLl2Parser.ModContext ctx);
	/**
	 * Visit a parse tree produced by {@link SlangLl2Parser#args}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArgs(SlangLl2Parser.ArgsContext ctx);
	/**
	 * Visit a parse tree produced by {@link SlangLl2Parser#namedArg}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNamedArg(SlangLl2Parser.NamedArgContext ctx);
	/**
	 * Visit a parse tree produced by {@link SlangLl2Parser#name}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitName(SlangLl2Parser.NameContext ctx);
	/**
	 * Visit a parse tree produced by {@link SlangLl2Parser#typeDefn}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTypeDefn(SlangLl2Parser.TypeDefnContext ctx);
	/**
	 * Visit a parse tree produced by {@link SlangLl2Parser#typeParams}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTypeParams(SlangLl2Parser.TypeParamsContext ctx);
	/**
	 * Visit a parse tree produced by {@link SlangLl2Parser#typeParam}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTypeParam(SlangLl2Parser.TypeParamContext ctx);
	/**
	 * Visit a parse tree produced by {@link SlangLl2Parser#enumMembers}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEnumMembers(SlangLl2Parser.EnumMembersContext ctx);
	/**
	 * Visit a parse tree produced by {@link SlangLl2Parser#params}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParams(SlangLl2Parser.ParamsContext ctx);
	/**
	 * Visit a parse tree produced by {@link SlangLl2Parser#param}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParam(SlangLl2Parser.ParamContext ctx);
	/**
	 * Visit a parse tree produced by {@link SlangLl2Parser#supers}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSupers(SlangLl2Parser.SupersContext ctx);
	/**
	 * Visit a parse tree produced by {@link SlangLl2Parser#supr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSupr(SlangLl2Parser.SuprContext ctx);
	/**
	 * Visit a parse tree produced by {@link SlangLl2Parser#annot}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAnnot(SlangLl2Parser.AnnotContext ctx);
	/**
	 * Visit a parse tree produced by {@link SlangLl2Parser#varDefn}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVarDefn(SlangLl2Parser.VarDefnContext ctx);
	/**
	 * Visit a parse tree produced by {@link SlangLl2Parser#defDefn}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDefDefn(SlangLl2Parser.DefDefnContext ctx);
	/**
	 * Visit a parse tree produced by {@link SlangLl2Parser#defId}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDefId(SlangLl2Parser.DefIdContext ctx);
	/**
	 * Visit a parse tree produced by {@link SlangLl2Parser#defParams}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDefParams(SlangLl2Parser.DefParamsContext ctx);
	/**
	 * Visit a parse tree produced by {@link SlangLl2Parser#defParam}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDefParam(SlangLl2Parser.DefParamContext ctx);
	/**
	 * Visit a parse tree produced by {@link SlangLl2Parser#defParamSuffix}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDefParamSuffix(SlangLl2Parser.DefParamSuffixContext ctx);
	/**
	 * Visit a parse tree produced by {@link SlangLl2Parser#stmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStmt(SlangLl2Parser.StmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link SlangLl2Parser#defStmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDefStmt(SlangLl2Parser.DefStmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link SlangLl2Parser#expOrAssignStmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpOrAssignStmt(SlangLl2Parser.ExpOrAssignStmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link SlangLl2Parser#varPattern}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVarPattern(SlangLl2Parser.VarPatternContext ctx);
	/**
	 * Visit a parse tree produced by {@link SlangLl2Parser#rhs}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRhs(SlangLl2Parser.RhsContext ctx);
	/**
	 * Visit a parse tree produced by {@link SlangLl2Parser#ifStmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIfStmt(SlangLl2Parser.IfStmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link SlangLl2Parser#block}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBlock(SlangLl2Parser.BlockContext ctx);
	/**
	 * Visit a parse tree produced by {@link SlangLl2Parser#blockContent}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBlockContent(SlangLl2Parser.BlockContentContext ctx);
	/**
	 * Visit a parse tree produced by {@link SlangLl2Parser#ret}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRet(SlangLl2Parser.RetContext ctx);
	/**
	 * Visit a parse tree produced by {@link SlangLl2Parser#els}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEls(SlangLl2Parser.ElsContext ctx);
	/**
	 * Visit a parse tree produced by {@link SlangLl2Parser#whileStmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWhileStmt(SlangLl2Parser.WhileStmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link SlangLl2Parser#forStmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitForStmt(SlangLl2Parser.ForStmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link SlangLl2Parser#forRange}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitForRange(SlangLl2Parser.ForRangeContext ctx);
	/**
	 * Visit a parse tree produced by {@link SlangLl2Parser#matchStmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMatchStmt(SlangLl2Parser.MatchStmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link SlangLl2Parser#pattern}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPattern(SlangLl2Parser.PatternContext ctx);
	/**
	 * Visit a parse tree produced by {@link SlangLl2Parser#patterns}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPatterns(SlangLl2Parser.PatternsContext ctx);
	/**
	 * Visit a parse tree produced by {@link SlangLl2Parser#patternsArg}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPatternsArg(SlangLl2Parser.PatternsArgContext ctx);
	/**
	 * Visit a parse tree produced by {@link SlangLl2Parser#exp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExp(SlangLl2Parser.ExpContext ctx);
	/**
	 * Visit a parse tree produced by {@link SlangLl2Parser#exp3}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExp3(SlangLl2Parser.Exp3Context ctx);
	/**
	 * Visit a parse tree produced by {@link SlangLl2Parser#infixSuffix}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInfixSuffix(SlangLl2Parser.InfixSuffixContext ctx);
	/**
	 * Visit a parse tree produced by {@link SlangLl2Parser#exp2}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExp2(SlangLl2Parser.Exp2Context ctx);
	/**
	 * Visit a parse tree produced by {@link SlangLl2Parser#exp1}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExp1(SlangLl2Parser.Exp1Context ctx);
	/**
	 * Visit a parse tree produced by {@link SlangLl2Parser#exp0}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExp0(SlangLl2Parser.Exp0Context ctx);
	/**
	 * Visit a parse tree produced by {@link SlangLl2Parser#condSuffix}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCondSuffix(SlangLl2Parser.CondSuffixContext ctx);
	/**
	 * Visit a parse tree produced by {@link SlangLl2Parser#access}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAccess(SlangLl2Parser.AccessContext ctx);
	/**
	 * Visit a parse tree produced by {@link SlangLl2Parser#fn}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFn(SlangLl2Parser.FnContext ctx);
	/**
	 * Visit a parse tree produced by {@link SlangLl2Parser#lit}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLit(SlangLl2Parser.LitContext ctx);
	/**
	 * Visit a parse tree produced by {@link SlangLl2Parser#paren}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParen(SlangLl2Parser.ParenContext ctx);
	/**
	 * Visit a parse tree produced by {@link SlangLl2Parser#parenArgs}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParenArgs(SlangLl2Parser.ParenArgsContext ctx);
	/**
	 * Visit a parse tree produced by {@link SlangLl2Parser#cas}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCas(SlangLl2Parser.CasContext ctx);
	/**
	 * Visit a parse tree produced by {@link SlangLl2Parser#forExp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitForExp(SlangLl2Parser.ForExpContext ctx);
	/**
	 * Visit a parse tree produced by {@link SlangLl2Parser#defAnon}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDefAnon(SlangLl2Parser.DefAnonContext ctx);
	/**
	 * Visit a parse tree produced by {@link SlangLl2Parser#quant}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitQuant(SlangLl2Parser.QuantContext ctx);
	/**
	 * Visit a parse tree produced by {@link SlangLl2Parser#quantRange}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitQuantRange(SlangLl2Parser.QuantRangeContext ctx);
	/**
	 * Visit a parse tree produced by {@link SlangLl2Parser#deduceStmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDeduceStmt(SlangLl2Parser.DeduceStmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link SlangLl2Parser#expJustOpt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpJustOpt(SlangLl2Parser.ExpJustOptContext ctx);
	/**
	 * Visit a parse tree produced by {@link SlangLl2Parser#proofStep}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProofStep(SlangLl2Parser.ProofStepContext ctx);
	/**
	 * Visit a parse tree produced by {@link SlangLl2Parser#subProof}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSubProof(SlangLl2Parser.SubProofContext ctx);
	/**
	 * Visit a parse tree produced by {@link SlangLl2Parser#freshIds}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFreshIds(SlangLl2Parser.FreshIdsContext ctx);
	/**
	 * Visit a parse tree produced by {@link SlangLl2Parser#proofId}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProofId(SlangLl2Parser.ProofIdContext ctx);
	/**
	 * Visit a parse tree produced by {@link SlangLl2Parser#just}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitJust(SlangLl2Parser.JustContext ctx);
	/**
	 * Visit a parse tree produced by {@link SlangLl2Parser#justArgs}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitJustArgs(SlangLl2Parser.JustArgsContext ctx);
	/**
	 * Visit a parse tree produced by {@link SlangLl2Parser#justTypeArgs}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitJustTypeArgs(SlangLl2Parser.JustTypeArgsContext ctx);
	/**
	 * Visit a parse tree produced by {@link SlangLl2Parser#justWitnesses}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitJustWitnesses(SlangLl2Parser.JustWitnessesContext ctx);
	/**
	 * Visit a parse tree produced by {@link SlangLl2Parser#sequent}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSequent(SlangLl2Parser.SequentContext ctx);
	/**
	 * Visit a parse tree produced by {@link SlangLl2Parser#truthTable}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTruthTable(SlangLl2Parser.TruthTableContext ctx);
	/**
	 * Visit a parse tree produced by {@link SlangLl2Parser#truthTableConclusion}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTruthTableConclusion(SlangLl2Parser.TruthTableConclusionContext ctx);
	/**
	 * Visit a parse tree produced by {@link SlangLl2Parser#truthTableCase}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTruthTableCase(SlangLl2Parser.TruthTableCaseContext ctx);
	/**
	 * Visit a parse tree produced by {@link SlangLl2Parser#truthTableAssignment}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTruthTableAssignment(SlangLl2Parser.TruthTableAssignmentContext ctx);
	/**
	 * Visit a parse tree produced by {@link SlangLl2Parser#type}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitType(SlangLl2Parser.TypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link SlangLl2Parser#type1}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitType1(SlangLl2Parser.Type1Context ctx);
	/**
	 * Visit a parse tree produced by {@link SlangLl2Parser#typeParenArgs}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTypeParenArgs(SlangLl2Parser.TypeParenArgsContext ctx);
	/**
	 * Visit a parse tree produced by {@link SlangLl2Parser#type0}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitType0(SlangLl2Parser.Type0Context ctx);
	/**
	 * Visit a parse tree produced by {@link SlangLl2Parser#typeArgs}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTypeArgs(SlangLl2Parser.TypeArgsContext ctx);
	/**
	 * Visit a parse tree produced by {@link SlangLl2Parser#interp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInterp(SlangLl2Parser.InterpContext ctx);
	/**
	 * Visit a parse tree produced by {@link SlangLl2Parser#sinterp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSinterp(SlangLl2Parser.SinterpContext ctx);
	/**
	 * Visit a parse tree produced by {@link SlangLl2Parser#strinterp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStrinterp(SlangLl2Parser.StrinterpContext ctx);
	/**
	 * Visit a parse tree produced by {@link SlangLl2Parser#mstrinterp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMstrinterp(SlangLl2Parser.MstrinterpContext ctx);
}