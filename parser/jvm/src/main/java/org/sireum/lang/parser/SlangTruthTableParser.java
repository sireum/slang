// $ANTLR 3.5.2 /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g 2023-08-16 13:37:46
 package org.sireum.lang.parser; 

import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

import org.antlr.runtime.tree.*;


@SuppressWarnings("all")
public class SlangTruthTableParser extends org.antlr.runtime.Parser {
	public static final String[] tokenNames = new String[] {
		"<invalid>", "<EOR>", "<DOWN>", "<UP>", "COMMENT", "HASH", "HLINE", "LSQUARE", 
		"NL", "OTHER", "RSQUARE", "WS", "'Contingent'", "'Contradictory'", "'Invalid'", 
		"'Tautology'", "'Valid'"
	};
	public static final int EOF=-1;
	public static final int T__12=12;
	public static final int T__13=13;
	public static final int T__14=14;
	public static final int T__15=15;
	public static final int T__16=16;
	public static final int COMMENT=4;
	public static final int HASH=5;
	public static final int HLINE=6;
	public static final int LSQUARE=7;
	public static final int NL=8;
	public static final int OTHER=9;
	public static final int RSQUARE=10;
	public static final int WS=11;

	// delegates
	public Parser[] getDelegates() {
		return new Parser[] {};
	}

	// delegators


	public SlangTruthTableParser(TokenStream input) {
		this(input, new RecognizerSharedState());
	}
	public SlangTruthTableParser(TokenStream input, RecognizerSharedState state) {
		super(input, state);
	}

	protected TreeAdaptor adaptor = new CommonTreeAdaptor();

	public void setTreeAdaptor(TreeAdaptor adaptor) {
		this.adaptor = adaptor;
	}
	public TreeAdaptor getTreeAdaptor() {
		return adaptor;
	}
	@Override public String[] getTokenNames() { return SlangTruthTableParser.tokenNames; }
	@Override public String getGrammarFileName() { return "/Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g"; }


	public static class file_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "file"
	// /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g:39:1: file : table EOF ;
	public final SlangTruthTableParser.file_return file() throws RecognitionException {
		SlangTruthTableParser.file_return retval = new SlangTruthTableParser.file_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token EOF2=null;
		ParserRuleReturnScope table1 =null;

		Object EOF2_tree=null;

		try {
			// /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g:39:5: ( table EOF )
			// /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g:39:7: table EOF
			{
			root_0 = (Object)adaptor.nil();


			pushFollow(FOLLOW_table_in_file60);
			table1=table();
			state._fsp--;

			adaptor.addChild(root_0, table1.getTree());

			EOF2=(Token)match(input,EOF,FOLLOW_EOF_in_file62); 
			EOF2_tree = (Object)adaptor.create(EOF2);
			adaptor.addChild(root_0, EOF2_tree);

			}

			retval.stop = input.LT(-1);

			adaptor.setTokenBoundaries(root_0, retval.start, retval.stop);
			retval.tree = (Object)adaptor.rulePostProcessing(root_0);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "file"


	public static class table_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "table"
	// /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g:41:1: table : others NL HLINE NL others HASH others NL HLINE NL ( row )+ HLINE ( NL )+ conclusion ;
	public final SlangTruthTableParser.table_return table() throws RecognitionException {
		SlangTruthTableParser.table_return retval = new SlangTruthTableParser.table_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token NL4=null;
		Token HLINE5=null;
		Token NL6=null;
		Token HASH8=null;
		Token NL10=null;
		Token HLINE11=null;
		Token NL12=null;
		Token HLINE14=null;
		Token NL15=null;
		ParserRuleReturnScope others3 =null;
		ParserRuleReturnScope others7 =null;
		ParserRuleReturnScope others9 =null;
		ParserRuleReturnScope row13 =null;
		ParserRuleReturnScope conclusion16 =null;

		Object NL4_tree=null;
		Object HLINE5_tree=null;
		Object NL6_tree=null;
		Object HASH8_tree=null;
		Object NL10_tree=null;
		Object HLINE11_tree=null;
		Object NL12_tree=null;
		Object HLINE14_tree=null;
		Object NL15_tree=null;

		try {
			// /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g:41:6: ( others NL HLINE NL others HASH others NL HLINE NL ( row )+ HLINE ( NL )+ conclusion )
			// /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g:41:8: others NL HLINE NL others HASH others NL HLINE NL ( row )+ HLINE ( NL )+ conclusion
			{
			root_0 = (Object)adaptor.nil();


			pushFollow(FOLLOW_others_in_table70);
			others3=others();
			state._fsp--;

			adaptor.addChild(root_0, others3.getTree());

			NL4=(Token)match(input,NL,FOLLOW_NL_in_table72); 
			NL4_tree = (Object)adaptor.create(NL4);
			adaptor.addChild(root_0, NL4_tree);

			HLINE5=(Token)match(input,HLINE,FOLLOW_HLINE_in_table82); 
			HLINE5_tree = (Object)adaptor.create(HLINE5);
			adaptor.addChild(root_0, HLINE5_tree);

			NL6=(Token)match(input,NL,FOLLOW_NL_in_table84); 
			NL6_tree = (Object)adaptor.create(NL6);
			adaptor.addChild(root_0, NL6_tree);

			pushFollow(FOLLOW_others_in_table93);
			others7=others();
			state._fsp--;

			adaptor.addChild(root_0, others7.getTree());

			HASH8=(Token)match(input,HASH,FOLLOW_HASH_in_table95); 
			HASH8_tree = (Object)adaptor.create(HASH8);
			adaptor.addChild(root_0, HASH8_tree);

			pushFollow(FOLLOW_others_in_table97);
			others9=others();
			state._fsp--;

			adaptor.addChild(root_0, others9.getTree());

			NL10=(Token)match(input,NL,FOLLOW_NL_in_table99); 
			NL10_tree = (Object)adaptor.create(NL10);
			adaptor.addChild(root_0, NL10_tree);

			HLINE11=(Token)match(input,HLINE,FOLLOW_HLINE_in_table108); 
			HLINE11_tree = (Object)adaptor.create(HLINE11);
			adaptor.addChild(root_0, HLINE11_tree);

			NL12=(Token)match(input,NL,FOLLOW_NL_in_table110); 
			NL12_tree = (Object)adaptor.create(NL12);
			adaptor.addChild(root_0, NL12_tree);

			// /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g:45:8: ( row )+
			int cnt1=0;
			loop1:
			while (true) {
				int alt1=2;
				int LA1_0 = input.LA(1);
				if ( (LA1_0==OTHER) ) {
					alt1=1;
				}

				switch (alt1) {
				case 1 :
					// /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g:45:8: row
					{
					pushFollow(FOLLOW_row_in_table119);
					row13=row();
					state._fsp--;

					adaptor.addChild(root_0, row13.getTree());

					}
					break;

				default :
					if ( cnt1 >= 1 ) break loop1;
					EarlyExitException eee = new EarlyExitException(1, input);
					throw eee;
				}
				cnt1++;
			}

			HLINE14=(Token)match(input,HLINE,FOLLOW_HLINE_in_table129); 
			HLINE14_tree = (Object)adaptor.create(HLINE14);
			adaptor.addChild(root_0, HLINE14_tree);

			// /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g:46:14: ( NL )+
			int cnt2=0;
			loop2:
			while (true) {
				int alt2=2;
				int LA2_0 = input.LA(1);
				if ( (LA2_0==NL) ) {
					alt2=1;
				}

				switch (alt2) {
				case 1 :
					// /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g:46:14: NL
					{
					NL15=(Token)match(input,NL,FOLLOW_NL_in_table131); 
					NL15_tree = (Object)adaptor.create(NL15);
					adaptor.addChild(root_0, NL15_tree);

					}
					break;

				default :
					if ( cnt2 >= 1 ) break loop2;
					EarlyExitException eee = new EarlyExitException(2, input);
					throw eee;
				}
				cnt2++;
			}

			pushFollow(FOLLOW_conclusion_in_table141);
			conclusion16=conclusion();
			state._fsp--;

			adaptor.addChild(root_0, conclusion16.getTree());

			}

			retval.stop = input.LT(-1);

			adaptor.setTokenBoundaries(root_0, retval.start, retval.stop);
			retval.tree = (Object)adaptor.rulePostProcessing(root_0);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "table"


	public static class row_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "row"
	// /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g:49:1: row : others HASH others NL ;
	public final SlangTruthTableParser.row_return row() throws RecognitionException {
		SlangTruthTableParser.row_return retval = new SlangTruthTableParser.row_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token HASH18=null;
		Token NL20=null;
		ParserRuleReturnScope others17 =null;
		ParserRuleReturnScope others19 =null;

		Object HASH18_tree=null;
		Object NL20_tree=null;

		try {
			// /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g:49:4: ( others HASH others NL )
			// /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g:49:6: others HASH others NL
			{
			root_0 = (Object)adaptor.nil();


			pushFollow(FOLLOW_others_in_row149);
			others17=others();
			state._fsp--;

			adaptor.addChild(root_0, others17.getTree());

			HASH18=(Token)match(input,HASH,FOLLOW_HASH_in_row151); 
			HASH18_tree = (Object)adaptor.create(HASH18);
			adaptor.addChild(root_0, HASH18_tree);

			pushFollow(FOLLOW_others_in_row153);
			others19=others();
			state._fsp--;

			adaptor.addChild(root_0, others19.getTree());

			NL20=(Token)match(input,NL,FOLLOW_NL_in_row155); 
			NL20_tree = (Object)adaptor.create(NL20);
			adaptor.addChild(root_0, NL20_tree);

			}

			retval.stop = input.LT(-1);

			adaptor.setTokenBoundaries(root_0, retval.start, retval.stop);
			retval.tree = (Object)adaptor.rulePostProcessing(root_0);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "row"


	public static class vals_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "vals"
	// /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g:51:1: vals : others ;
	public final SlangTruthTableParser.vals_return vals() throws RecognitionException {
		SlangTruthTableParser.vals_return retval = new SlangTruthTableParser.vals_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		ParserRuleReturnScope others21 =null;


		try {
			// /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g:51:5: ( others )
			// /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g:51:7: others
			{
			root_0 = (Object)adaptor.nil();


			pushFollow(FOLLOW_others_in_vals163);
			others21=others();
			state._fsp--;

			adaptor.addChild(root_0, others21.getTree());

			}

			retval.stop = input.LT(-1);

			adaptor.setTokenBoundaries(root_0, retval.start, retval.stop);
			retval.tree = (Object)adaptor.rulePostProcessing(root_0);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "vals"


	public static class conclusion_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "conclusion"
	// /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g:53:1: conclusion : ( 'Tautology' ( NL )* | 'Contradictory' ( NL )* | 'Contingent' NL cas ( NL ( cas )? )* | 'Valid' ( assign )+ ( NL )* | 'Invalid' ( assign )+ ( NL )* );
	public final SlangTruthTableParser.conclusion_return conclusion() throws RecognitionException {
		SlangTruthTableParser.conclusion_return retval = new SlangTruthTableParser.conclusion_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token string_literal22=null;
		Token NL23=null;
		Token string_literal24=null;
		Token NL25=null;
		Token string_literal26=null;
		Token NL27=null;
		Token NL29=null;
		Token string_literal31=null;
		Token NL33=null;
		Token string_literal34=null;
		Token NL36=null;
		ParserRuleReturnScope cas28 =null;
		ParserRuleReturnScope cas30 =null;
		ParserRuleReturnScope assign32 =null;
		ParserRuleReturnScope assign35 =null;

		Object string_literal22_tree=null;
		Object NL23_tree=null;
		Object string_literal24_tree=null;
		Object NL25_tree=null;
		Object string_literal26_tree=null;
		Object NL27_tree=null;
		Object NL29_tree=null;
		Object string_literal31_tree=null;
		Object NL33_tree=null;
		Object string_literal34_tree=null;
		Object NL36_tree=null;

		try {
			// /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g:53:11: ( 'Tautology' ( NL )* | 'Contradictory' ( NL )* | 'Contingent' NL cas ( NL ( cas )? )* | 'Valid' ( assign )+ ( NL )* | 'Invalid' ( assign )+ ( NL )* )
			int alt11=5;
			switch ( input.LA(1) ) {
			case 15:
				{
				alt11=1;
				}
				break;
			case 13:
				{
				alt11=2;
				}
				break;
			case 12:
				{
				alt11=3;
				}
				break;
			case 16:
				{
				alt11=4;
				}
				break;
			case 14:
				{
				alt11=5;
				}
				break;
			default:
				NoViableAltException nvae =
					new NoViableAltException("", 11, 0, input);
				throw nvae;
			}
			switch (alt11) {
				case 1 :
					// /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g:54:5: 'Tautology' ( NL )*
					{
					root_0 = (Object)adaptor.nil();


					string_literal22=(Token)match(input,15,FOLLOW_15_in_conclusion175); 
					string_literal22_tree = (Object)adaptor.create(string_literal22);
					adaptor.addChild(root_0, string_literal22_tree);

					// /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g:54:17: ( NL )*
					loop3:
					while (true) {
						int alt3=2;
						int LA3_0 = input.LA(1);
						if ( (LA3_0==NL) ) {
							alt3=1;
						}

						switch (alt3) {
						case 1 :
							// /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g:54:17: NL
							{
							NL23=(Token)match(input,NL,FOLLOW_NL_in_conclusion177); 
							NL23_tree = (Object)adaptor.create(NL23);
							adaptor.addChild(root_0, NL23_tree);

							}
							break;

						default :
							break loop3;
						}
					}

					}
					break;
				case 2 :
					// /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g:55:5: 'Contradictory' ( NL )*
					{
					root_0 = (Object)adaptor.nil();


					string_literal24=(Token)match(input,13,FOLLOW_13_in_conclusion184); 
					string_literal24_tree = (Object)adaptor.create(string_literal24);
					adaptor.addChild(root_0, string_literal24_tree);

					// /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g:55:21: ( NL )*
					loop4:
					while (true) {
						int alt4=2;
						int LA4_0 = input.LA(1);
						if ( (LA4_0==NL) ) {
							alt4=1;
						}

						switch (alt4) {
						case 1 :
							// /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g:55:21: NL
							{
							NL25=(Token)match(input,NL,FOLLOW_NL_in_conclusion186); 
							NL25_tree = (Object)adaptor.create(NL25);
							adaptor.addChild(root_0, NL25_tree);

							}
							break;

						default :
							break loop4;
						}
					}

					}
					break;
				case 3 :
					// /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g:56:5: 'Contingent' NL cas ( NL ( cas )? )*
					{
					root_0 = (Object)adaptor.nil();


					string_literal26=(Token)match(input,12,FOLLOW_12_in_conclusion193); 
					string_literal26_tree = (Object)adaptor.create(string_literal26);
					adaptor.addChild(root_0, string_literal26_tree);

					NL27=(Token)match(input,NL,FOLLOW_NL_in_conclusion195); 
					NL27_tree = (Object)adaptor.create(NL27);
					adaptor.addChild(root_0, NL27_tree);

					pushFollow(FOLLOW_cas_in_conclusion197);
					cas28=cas();
					state._fsp--;

					adaptor.addChild(root_0, cas28.getTree());

					// /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g:56:25: ( NL ( cas )? )*
					loop6:
					while (true) {
						int alt6=2;
						int LA6_0 = input.LA(1);
						if ( (LA6_0==NL) ) {
							alt6=1;
						}

						switch (alt6) {
						case 1 :
							// /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g:56:27: NL ( cas )?
							{
							NL29=(Token)match(input,NL,FOLLOW_NL_in_conclusion201); 
							NL29_tree = (Object)adaptor.create(NL29);
							adaptor.addChild(root_0, NL29_tree);

							// /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g:56:30: ( cas )?
							int alt5=2;
							int LA5_0 = input.LA(1);
							if ( (LA5_0==OTHER) ) {
								alt5=1;
							}
							switch (alt5) {
								case 1 :
									// /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g:56:30: cas
									{
									pushFollow(FOLLOW_cas_in_conclusion203);
									cas30=cas();
									state._fsp--;

									adaptor.addChild(root_0, cas30.getTree());

									}
									break;

							}

							}
							break;

						default :
							break loop6;
						}
					}

					}
					break;
				case 4 :
					// /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g:57:5: 'Valid' ( assign )+ ( NL )*
					{
					root_0 = (Object)adaptor.nil();


					string_literal31=(Token)match(input,16,FOLLOW_16_in_conclusion213); 
					string_literal31_tree = (Object)adaptor.create(string_literal31);
					adaptor.addChild(root_0, string_literal31_tree);

					// /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g:57:13: ( assign )+
					int cnt7=0;
					loop7:
					while (true) {
						int alt7=2;
						int LA7_0 = input.LA(1);
						if ( (LA7_0==LSQUARE) ) {
							alt7=1;
						}

						switch (alt7) {
						case 1 :
							// /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g:57:13: assign
							{
							pushFollow(FOLLOW_assign_in_conclusion215);
							assign32=assign();
							state._fsp--;

							adaptor.addChild(root_0, assign32.getTree());

							}
							break;

						default :
							if ( cnt7 >= 1 ) break loop7;
							EarlyExitException eee = new EarlyExitException(7, input);
							throw eee;
						}
						cnt7++;
					}

					// /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g:57:22: ( NL )*
					loop8:
					while (true) {
						int alt8=2;
						int LA8_0 = input.LA(1);
						if ( (LA8_0==NL) ) {
							alt8=1;
						}

						switch (alt8) {
						case 1 :
							// /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g:57:22: NL
							{
							NL33=(Token)match(input,NL,FOLLOW_NL_in_conclusion219); 
							NL33_tree = (Object)adaptor.create(NL33);
							adaptor.addChild(root_0, NL33_tree);

							}
							break;

						default :
							break loop8;
						}
					}

					}
					break;
				case 5 :
					// /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g:58:5: 'Invalid' ( assign )+ ( NL )*
					{
					root_0 = (Object)adaptor.nil();


					string_literal34=(Token)match(input,14,FOLLOW_14_in_conclusion226); 
					string_literal34_tree = (Object)adaptor.create(string_literal34);
					adaptor.addChild(root_0, string_literal34_tree);

					// /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g:58:15: ( assign )+
					int cnt9=0;
					loop9:
					while (true) {
						int alt9=2;
						int LA9_0 = input.LA(1);
						if ( (LA9_0==LSQUARE) ) {
							alt9=1;
						}

						switch (alt9) {
						case 1 :
							// /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g:58:15: assign
							{
							pushFollow(FOLLOW_assign_in_conclusion228);
							assign35=assign();
							state._fsp--;

							adaptor.addChild(root_0, assign35.getTree());

							}
							break;

						default :
							if ( cnt9 >= 1 ) break loop9;
							EarlyExitException eee = new EarlyExitException(9, input);
							throw eee;
						}
						cnt9++;
					}

					// /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g:58:24: ( NL )*
					loop10:
					while (true) {
						int alt10=2;
						int LA10_0 = input.LA(1);
						if ( (LA10_0==NL) ) {
							alt10=1;
						}

						switch (alt10) {
						case 1 :
							// /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g:58:24: NL
							{
							NL36=(Token)match(input,NL,FOLLOW_NL_in_conclusion232); 
							NL36_tree = (Object)adaptor.create(NL36);
							adaptor.addChild(root_0, NL36_tree);

							}
							break;

						default :
							break loop10;
						}
					}

					}
					break;

			}
			retval.stop = input.LT(-1);

			adaptor.setTokenBoundaries(root_0, retval.start, retval.stop);
			retval.tree = (Object)adaptor.rulePostProcessing(root_0);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "conclusion"


	public static class cas_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "cas"
	// /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g:60:1: cas : OTHER OTHER ( assign )+ ;
	public final SlangTruthTableParser.cas_return cas() throws RecognitionException {
		SlangTruthTableParser.cas_return retval = new SlangTruthTableParser.cas_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token OTHER37=null;
		Token OTHER38=null;
		ParserRuleReturnScope assign39 =null;

		Object OTHER37_tree=null;
		Object OTHER38_tree=null;

		try {
			// /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g:60:4: ( OTHER OTHER ( assign )+ )
			// /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g:60:6: OTHER OTHER ( assign )+
			{
			root_0 = (Object)adaptor.nil();


			OTHER37=(Token)match(input,OTHER,FOLLOW_OTHER_in_cas241); 
			OTHER37_tree = (Object)adaptor.create(OTHER37);
			adaptor.addChild(root_0, OTHER37_tree);

			OTHER38=(Token)match(input,OTHER,FOLLOW_OTHER_in_cas243); 
			OTHER38_tree = (Object)adaptor.create(OTHER38);
			adaptor.addChild(root_0, OTHER38_tree);

			// /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g:60:18: ( assign )+
			int cnt12=0;
			loop12:
			while (true) {
				int alt12=2;
				int LA12_0 = input.LA(1);
				if ( (LA12_0==LSQUARE) ) {
					alt12=1;
				}

				switch (alt12) {
				case 1 :
					// /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g:60:18: assign
					{
					pushFollow(FOLLOW_assign_in_cas245);
					assign39=assign();
					state._fsp--;

					adaptor.addChild(root_0, assign39.getTree());

					}
					break;

				default :
					if ( cnt12 >= 1 ) break loop12;
					EarlyExitException eee = new EarlyExitException(12, input);
					throw eee;
				}
				cnt12++;
			}

			}

			retval.stop = input.LT(-1);

			adaptor.setTokenBoundaries(root_0, retval.start, retval.stop);
			retval.tree = (Object)adaptor.rulePostProcessing(root_0);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "cas"


	public static class assign_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "assign"
	// /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g:62:1: assign : LSQUARE others RSQUARE ;
	public final SlangTruthTableParser.assign_return assign() throws RecognitionException {
		SlangTruthTableParser.assign_return retval = new SlangTruthTableParser.assign_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token LSQUARE40=null;
		Token RSQUARE42=null;
		ParserRuleReturnScope others41 =null;

		Object LSQUARE40_tree=null;
		Object RSQUARE42_tree=null;

		try {
			// /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g:62:7: ( LSQUARE others RSQUARE )
			// /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g:62:9: LSQUARE others RSQUARE
			{
			root_0 = (Object)adaptor.nil();


			LSQUARE40=(Token)match(input,LSQUARE,FOLLOW_LSQUARE_in_assign255); 
			LSQUARE40_tree = (Object)adaptor.create(LSQUARE40);
			adaptor.addChild(root_0, LSQUARE40_tree);

			pushFollow(FOLLOW_others_in_assign257);
			others41=others();
			state._fsp--;

			adaptor.addChild(root_0, others41.getTree());

			RSQUARE42=(Token)match(input,RSQUARE,FOLLOW_RSQUARE_in_assign259); 
			RSQUARE42_tree = (Object)adaptor.create(RSQUARE42);
			adaptor.addChild(root_0, RSQUARE42_tree);

			}

			retval.stop = input.LT(-1);

			adaptor.setTokenBoundaries(root_0, retval.start, retval.stop);
			retval.tree = (Object)adaptor.rulePostProcessing(root_0);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "assign"


	public static class others_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "others"
	// /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g:64:1: others : ( OTHER )+ ;
	public final SlangTruthTableParser.others_return others() throws RecognitionException {
		SlangTruthTableParser.others_return retval = new SlangTruthTableParser.others_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token OTHER43=null;

		Object OTHER43_tree=null;

		try {
			// /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g:64:7: ( ( OTHER )+ )
			// /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g:64:9: ( OTHER )+
			{
			root_0 = (Object)adaptor.nil();


			// /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g:64:9: ( OTHER )+
			int cnt13=0;
			loop13:
			while (true) {
				int alt13=2;
				int LA13_0 = input.LA(1);
				if ( (LA13_0==OTHER) ) {
					alt13=1;
				}

				switch (alt13) {
				case 1 :
					// /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g:64:9: OTHER
					{
					OTHER43=(Token)match(input,OTHER,FOLLOW_OTHER_in_others267); 
					OTHER43_tree = (Object)adaptor.create(OTHER43);
					adaptor.addChild(root_0, OTHER43_tree);

					}
					break;

				default :
					if ( cnt13 >= 1 ) break loop13;
					EarlyExitException eee = new EarlyExitException(13, input);
					throw eee;
				}
				cnt13++;
			}

			}

			retval.stop = input.LT(-1);

			adaptor.setTokenBoundaries(root_0, retval.start, retval.stop);
			retval.tree = (Object)adaptor.rulePostProcessing(root_0);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "others"

	// Delegated rules



	public static final BitSet FOLLOW_table_in_file60 = new BitSet(new long[]{0x0000000000000000L});
	public static final BitSet FOLLOW_EOF_in_file62 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_others_in_table70 = new BitSet(new long[]{0x0000000000000100L});
	public static final BitSet FOLLOW_NL_in_table72 = new BitSet(new long[]{0x0000000000000040L});
	public static final BitSet FOLLOW_HLINE_in_table82 = new BitSet(new long[]{0x0000000000000100L});
	public static final BitSet FOLLOW_NL_in_table84 = new BitSet(new long[]{0x0000000000000200L});
	public static final BitSet FOLLOW_others_in_table93 = new BitSet(new long[]{0x0000000000000020L});
	public static final BitSet FOLLOW_HASH_in_table95 = new BitSet(new long[]{0x0000000000000200L});
	public static final BitSet FOLLOW_others_in_table97 = new BitSet(new long[]{0x0000000000000100L});
	public static final BitSet FOLLOW_NL_in_table99 = new BitSet(new long[]{0x0000000000000040L});
	public static final BitSet FOLLOW_HLINE_in_table108 = new BitSet(new long[]{0x0000000000000100L});
	public static final BitSet FOLLOW_NL_in_table110 = new BitSet(new long[]{0x0000000000000200L});
	public static final BitSet FOLLOW_row_in_table119 = new BitSet(new long[]{0x0000000000000240L});
	public static final BitSet FOLLOW_HLINE_in_table129 = new BitSet(new long[]{0x0000000000000100L});
	public static final BitSet FOLLOW_NL_in_table131 = new BitSet(new long[]{0x000000000001F100L});
	public static final BitSet FOLLOW_conclusion_in_table141 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_others_in_row149 = new BitSet(new long[]{0x0000000000000020L});
	public static final BitSet FOLLOW_HASH_in_row151 = new BitSet(new long[]{0x0000000000000200L});
	public static final BitSet FOLLOW_others_in_row153 = new BitSet(new long[]{0x0000000000000100L});
	public static final BitSet FOLLOW_NL_in_row155 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_others_in_vals163 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_15_in_conclusion175 = new BitSet(new long[]{0x0000000000000102L});
	public static final BitSet FOLLOW_NL_in_conclusion177 = new BitSet(new long[]{0x0000000000000102L});
	public static final BitSet FOLLOW_13_in_conclusion184 = new BitSet(new long[]{0x0000000000000102L});
	public static final BitSet FOLLOW_NL_in_conclusion186 = new BitSet(new long[]{0x0000000000000102L});
	public static final BitSet FOLLOW_12_in_conclusion193 = new BitSet(new long[]{0x0000000000000100L});
	public static final BitSet FOLLOW_NL_in_conclusion195 = new BitSet(new long[]{0x0000000000000200L});
	public static final BitSet FOLLOW_cas_in_conclusion197 = new BitSet(new long[]{0x0000000000000102L});
	public static final BitSet FOLLOW_NL_in_conclusion201 = new BitSet(new long[]{0x0000000000000302L});
	public static final BitSet FOLLOW_cas_in_conclusion203 = new BitSet(new long[]{0x0000000000000102L});
	public static final BitSet FOLLOW_16_in_conclusion213 = new BitSet(new long[]{0x0000000000000080L});
	public static final BitSet FOLLOW_assign_in_conclusion215 = new BitSet(new long[]{0x0000000000000182L});
	public static final BitSet FOLLOW_NL_in_conclusion219 = new BitSet(new long[]{0x0000000000000102L});
	public static final BitSet FOLLOW_14_in_conclusion226 = new BitSet(new long[]{0x0000000000000080L});
	public static final BitSet FOLLOW_assign_in_conclusion228 = new BitSet(new long[]{0x0000000000000182L});
	public static final BitSet FOLLOW_NL_in_conclusion232 = new BitSet(new long[]{0x0000000000000102L});
	public static final BitSet FOLLOW_OTHER_in_cas241 = new BitSet(new long[]{0x0000000000000200L});
	public static final BitSet FOLLOW_OTHER_in_cas243 = new BitSet(new long[]{0x0000000000000080L});
	public static final BitSet FOLLOW_assign_in_cas245 = new BitSet(new long[]{0x0000000000000082L});
	public static final BitSet FOLLOW_LSQUARE_in_assign255 = new BitSet(new long[]{0x0000000000000200L});
	public static final BitSet FOLLOW_others_in_assign257 = new BitSet(new long[]{0x0000000000000400L});
	public static final BitSet FOLLOW_RSQUARE_in_assign259 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_OTHER_in_others267 = new BitSet(new long[]{0x0000000000000202L});
}
