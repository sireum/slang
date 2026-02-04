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
	 * Visit a parse tree produced by {@link SlangLl2Parser#importIdSuffix}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitImportIdSuffix(SlangLl2Parser.ImportIdSuffixContext ctx);
	/**
	 * Visit a parse tree produced by {@link SlangLl2Parser#importWildcardSuffix}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitImportWildcardSuffix(SlangLl2Parser.ImportWildcardSuffixContext ctx);
	/**
	 * Visit a parse tree produced by {@link SlangLl2Parser#importQualSuffix}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitImportQualSuffix(SlangLl2Parser.ImportQualSuffixContext ctx);
	/**
	 * Visit a parse tree produced by {@link SlangLl2Parser#importRenamesSuffix}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitImportRenamesSuffix(SlangLl2Parser.ImportRenamesSuffixContext ctx);
	/**
	 * Visit a parse tree produced by {@link SlangLl2Parser#importIdDotIdSuffix}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitImportIdDotIdSuffix(SlangLl2Parser.ImportIdDotIdSuffixContext ctx);
	/**
	 * Visit a parse tree produced by {@link SlangLl2Parser#importRenameSuffix}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitImportRenameSuffix(SlangLl2Parser.ImportRenameSuffixContext ctx);
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
	 * Visit a parse tree produced by {@link SlangLl2Parser#argSuffix}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArgSuffix(SlangLl2Parser.ArgSuffixContext ctx);
	/**
	 * Visit a parse tree produced by {@link SlangLl2Parser#namedArgSuffix}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNamedArgSuffix(SlangLl2Parser.NamedArgSuffixContext ctx);
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
	 * Visit a parse tree produced by {@link SlangLl2Parser#nameSuffix}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNameSuffix(SlangLl2Parser.NameSuffixContext ctx);
	/**
	 * Visit a parse tree produced by {@link SlangLl2Parser#typeDefn}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTypeDefn(SlangLl2Parser.TypeDefnContext ctx);
	/**
	 * Visit a parse tree produced by {@link SlangLl2Parser#typeDefnEnumSuffix}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTypeDefnEnumSuffix(SlangLl2Parser.TypeDefnEnumSuffixContext ctx);
	/**
	 * Visit a parse tree produced by {@link SlangLl2Parser#typeDefnAliasSuffix}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTypeDefnAliasSuffix(SlangLl2Parser.TypeDefnAliasSuffixContext ctx);
	/**
	 * Visit a parse tree produced by {@link SlangLl2Parser#typeDefnAdtSuffix}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTypeDefnAdtSuffix(SlangLl2Parser.TypeDefnAdtSuffixContext ctx);
	/**
	 * Visit a parse tree produced by {@link SlangLl2Parser#typeDefnAdtMembers}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTypeDefnAdtMembers(SlangLl2Parser.TypeDefnAdtMembersContext ctx);
	/**
	 * Visit a parse tree produced by {@link SlangLl2Parser#typeParams}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTypeParams(SlangLl2Parser.TypeParamsContext ctx);
	/**
	 * Visit a parse tree produced by {@link SlangLl2Parser#typeParamSuffix}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTypeParamSuffix(SlangLl2Parser.TypeParamSuffixContext ctx);
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
	 * Visit a parse tree produced by {@link SlangLl2Parser#commaId}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCommaId(SlangLl2Parser.CommaIdContext ctx);
	/**
	 * Visit a parse tree produced by {@link SlangLl2Parser#params}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParams(SlangLl2Parser.ParamsContext ctx);
	/**
	 * Visit a parse tree produced by {@link SlangLl2Parser#commaParams}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCommaParams(SlangLl2Parser.CommaParamsContext ctx);
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
	 * Visit a parse tree produced by {@link SlangLl2Parser#commaSuper}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCommaSuper(SlangLl2Parser.CommaSuperContext ctx);
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
	 * Visit a parse tree produced by {@link SlangLl2Parser#assignSuffix}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAssignSuffix(SlangLl2Parser.AssignSuffixContext ctx);
	/**
	 * Visit a parse tree produced by {@link SlangLl2Parser#defDefn}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDefDefn(SlangLl2Parser.DefDefnContext ctx);
	/**
	 * Visit a parse tree produced by {@link SlangLl2Parser#defnTypeSuffix}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDefnTypeSuffix(SlangLl2Parser.DefnTypeSuffixContext ctx);
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
	 * Visit a parse tree produced by {@link SlangLl2Parser#defParamSuffixVarargs}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDefParamSuffixVarargs(SlangLl2Parser.DefParamSuffixVarargsContext ctx);
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
	 * Visit a parse tree produced by {@link SlangLl2Parser#idStmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIdStmt(SlangLl2Parser.IdStmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link SlangLl2Parser#idStmtSuffix}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIdStmtSuffix(SlangLl2Parser.IdStmtSuffixContext ctx);
	/**
	 * Visit a parse tree produced by {@link SlangLl2Parser#labelSuffix}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLabelSuffix(SlangLl2Parser.LabelSuffixContext ctx);
	/**
	 * Visit a parse tree produced by {@link SlangLl2Parser#expStmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpStmt(SlangLl2Parser.ExpStmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link SlangLl2Parser#doStmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDoStmt(SlangLl2Parser.DoStmtContext ctx);
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
	 * Visit a parse tree produced by {@link SlangLl2Parser#elsIf}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitElsIf(SlangLl2Parser.ElsIfContext ctx);
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
	 * Visit a parse tree produced by {@link SlangLl2Parser#rangeSuffix}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRangeSuffix(SlangLl2Parser.RangeSuffixContext ctx);
	/**
	 * Visit a parse tree produced by {@link SlangLl2Parser#commaExp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCommaExp(SlangLl2Parser.CommaExpContext ctx);
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
	 * Visit a parse tree produced by {@link SlangLl2Parser#idTypePattern}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIdTypePattern(SlangLl2Parser.IdTypePatternContext ctx);
	/**
	 * Visit a parse tree produced by {@link SlangLl2Parser#colonType1}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitColonType1(SlangLl2Parser.ColonType1Context ctx);
	/**
	 * Visit a parse tree produced by {@link SlangLl2Parser#idNamePattern}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIdNamePattern(SlangLl2Parser.IdNamePatternContext ctx);
	/**
	 * Visit a parse tree produced by {@link SlangLl2Parser#wildCardPattern}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWildCardPattern(SlangLl2Parser.WildCardPatternContext ctx);
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
	 * Visit a parse tree produced by {@link SlangLl2Parser#namedPattern}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNamedPattern(SlangLl2Parser.NamedPatternContext ctx);
	/**
	 * Visit a parse tree produced by {@link SlangLl2Parser#commaPattern}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCommaPattern(SlangLl2Parser.CommaPatternContext ctx);
	/**
	 * Visit a parse tree produced by {@link SlangLl2Parser#commaNamedPattern}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCommaNamedPattern(SlangLl2Parser.CommaNamedPatternContext ctx);
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
	 * Visit a parse tree produced by {@link SlangLl2Parser#infixOp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInfixOp(SlangLl2Parser.InfixOpContext ctx);
	/**
	 * Visit a parse tree produced by {@link SlangLl2Parser#exp2}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExp2(SlangLl2Parser.Exp2Context ctx);
	/**
	 * Visit a parse tree produced by {@link SlangLl2Parser#eta}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEta(SlangLl2Parser.EtaContext ctx);
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
	 * Visit a parse tree produced by {@link SlangLl2Parser#idExp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIdExp(SlangLl2Parser.IdExpContext ctx);
	/**
	 * Visit a parse tree produced by {@link SlangLl2Parser#thisExp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitThisExp(SlangLl2Parser.ThisExpContext ctx);
	/**
	 * Visit a parse tree produced by {@link SlangLl2Parser#superExp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSuperExp(SlangLl2Parser.SuperExpContext ctx);
	/**
	 * Visit a parse tree produced by {@link SlangLl2Parser#condSuffix}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCondSuffix(SlangLl2Parser.CondSuffixContext ctx);
	/**
	 * Visit a parse tree produced by {@link SlangLl2Parser#condIteSuffix}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCondIteSuffix(SlangLl2Parser.CondIteSuffixContext ctx);
	/**
	 * Visit a parse tree produced by {@link SlangLl2Parser#condMatchSuffix}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCondMatchSuffix(SlangLl2Parser.CondMatchSuffixContext ctx);
	/**
	 * Visit a parse tree produced by {@link SlangLl2Parser#access}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAccess(SlangLl2Parser.AccessContext ctx);
	/**
	 * Visit a parse tree produced by {@link SlangLl2Parser#fieldAccess}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFieldAccess(SlangLl2Parser.FieldAccessContext ctx);
	/**
	 * Visit a parse tree produced by {@link SlangLl2Parser#applyAccess}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitApplyAccess(SlangLl2Parser.ApplyAccessContext ctx);
	/**
	 * Visit a parse tree produced by {@link SlangLl2Parser#fn}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFn(SlangLl2Parser.FnContext ctx);
	/**
	 * Visit a parse tree produced by {@link SlangLl2Parser#fnBody}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFnBody(SlangLl2Parser.FnBodyContext ctx);
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
	 * Visit a parse tree produced by {@link SlangLl2Parser#namedExpAnnot}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNamedExpAnnot(SlangLl2Parser.NamedExpAnnotContext ctx);
	/**
	 * Visit a parse tree produced by {@link SlangLl2Parser#commaExpAnnot}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCommaExpAnnot(SlangLl2Parser.CommaExpAnnotContext ctx);
	/**
	 * Visit a parse tree produced by {@link SlangLl2Parser#commaNamedExpAnnot}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCommaNamedExpAnnot(SlangLl2Parser.CommaNamedExpAnnotContext ctx);
	/**
	 * Visit a parse tree produced by {@link SlangLl2Parser#cas}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCas(SlangLl2Parser.CasContext ctx);
	/**
	 * Visit a parse tree produced by {@link SlangLl2Parser#casIf}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCasIf(SlangLl2Parser.CasIfContext ctx);
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
	 * Visit a parse tree produced by {@link SlangLl2Parser#colonType}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitColonType(SlangLl2Parser.ColonTypeContext ctx);
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
	 * Visit a parse tree produced by {@link SlangLl2Parser#idComma}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIdComma(SlangLl2Parser.IdCommaContext ctx);
	/**
	 * Visit a parse tree produced by {@link SlangLl2Parser#quantRangeSuffix}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitQuantRangeSuffix(SlangLl2Parser.QuantRangeSuffixContext ctx);
	/**
	 * Visit a parse tree produced by {@link SlangLl2Parser#deduceStmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDeduceStmt(SlangLl2Parser.DeduceStmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link SlangLl2Parser#proof}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProof(SlangLl2Parser.ProofContext ctx);
	/**
	 * Visit a parse tree produced by {@link SlangLl2Parser#sequent}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSequent(SlangLl2Parser.SequentContext ctx);
	/**
	 * Visit a parse tree produced by {@link SlangLl2Parser#expProof}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpProof(SlangLl2Parser.ExpProofContext ctx);
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
	 * Visit a parse tree produced by {@link SlangLl2Parser#commaType}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCommaType(SlangLl2Parser.CommaTypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link SlangLl2Parser#justWitnesses}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitJustWitnesses(SlangLl2Parser.JustWitnessesContext ctx);
	/**
	 * Visit a parse tree produced by {@link SlangLl2Parser#proofIds}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProofIds(SlangLl2Parser.ProofIdsContext ctx);
	/**
	 * Visit a parse tree produced by {@link SlangLl2Parser#commaProofId}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCommaProofId(SlangLl2Parser.CommaProofIdContext ctx);
	/**
	 * Visit a parse tree produced by {@link SlangLl2Parser#truthTable}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTruthTable(SlangLl2Parser.TruthTableContext ctx);
	/**
	 * Visit a parse tree produced by {@link SlangLl2Parser#colonExp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitColonExp(SlangLl2Parser.ColonExpContext ctx);
	/**
	 * Visit a parse tree produced by {@link SlangLl2Parser#colonIds}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitColonIds(SlangLl2Parser.ColonIdsContext ctx);
	/**
	 * Visit a parse tree produced by {@link SlangLl2Parser#truthTableConclusion}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTruthTableConclusion(SlangLl2Parser.TruthTableConclusionContext ctx);
	/**
	 * Visit a parse tree produced by {@link SlangLl2Parser#truthTableCases}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTruthTableCases(SlangLl2Parser.TruthTableCasesContext ctx);
	/**
	 * Visit a parse tree produced by {@link SlangLl2Parser#truthTableCase}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTruthTableCase(SlangLl2Parser.TruthTableCaseContext ctx);
	/**
	 * Visit a parse tree produced by {@link SlangLl2Parser#truthTableAssignments}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTruthTableAssignments(SlangLl2Parser.TruthTableAssignmentsContext ctx);
	/**
	 * Visit a parse tree produced by {@link SlangLl2Parser#truthTableAssignment}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTruthTableAssignment(SlangLl2Parser.TruthTableAssignmentContext ctx);
	/**
	 * Visit a parse tree produced by {@link SlangLl2Parser#commaTruthTableAssignment}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCommaTruthTableAssignment(SlangLl2Parser.CommaTruthTableAssignmentContext ctx);
	/**
	 * Visit a parse tree produced by {@link SlangLl2Parser#type}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitType(SlangLl2Parser.TypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link SlangLl2Parser#typeSuffix}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTypeSuffix(SlangLl2Parser.TypeSuffixContext ctx);
	/**
	 * Visit a parse tree produced by {@link SlangLl2Parser#type1}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitType1(SlangLl2Parser.Type1Context ctx);
	/**
	 * Visit a parse tree produced by {@link SlangLl2Parser#parenType}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParenType(SlangLl2Parser.ParenTypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link SlangLl2Parser#type0Suffix}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitType0Suffix(SlangLl2Parser.Type0SuffixContext ctx);
	/**
	 * Visit a parse tree produced by {@link SlangLl2Parser#typeParenArgs}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTypeParenArgs(SlangLl2Parser.TypeParenArgsContext ctx);
	/**
	 * Visit a parse tree produced by {@link SlangLl2Parser#commaAnnotType}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCommaAnnotType(SlangLl2Parser.CommaAnnotTypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link SlangLl2Parser#namedType}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNamedType(SlangLl2Parser.NamedTypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link SlangLl2Parser#commaNamedType}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCommaNamedType(SlangLl2Parser.CommaNamedTypeContext ctx);
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