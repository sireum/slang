// $ANTLR 3.5.2 /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g 2023-08-16 15:08:23
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
	// /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g:41:1: table : stars hlinep header hlinep rows hlines ( conclusion )? ;
	public final SlangTruthTableParser.table_return table() throws RecognitionException {
		SlangTruthTableParser.table_return retval = new SlangTruthTableParser.table_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		ParserRuleReturnScope stars3 =null;
		ParserRuleReturnScope hlinep4 =null;
		ParserRuleReturnScope header5 =null;
		ParserRuleReturnScope hlinep6 =null;
		ParserRuleReturnScope rows7 =null;
		ParserRuleReturnScope hlines8 =null;
		ParserRuleReturnScope conclusion9 =null;


		try {
			// /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g:41:6: ( stars hlinep header hlinep rows hlines ( conclusion )? )
			// /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g:41:8: stars hlinep header hlinep rows hlines ( conclusion )?
			{
			root_0 = (Object)adaptor.nil();


			pushFollow(FOLLOW_stars_in_table70);
			stars3=stars();
			state._fsp--;

			adaptor.addChild(root_0, stars3.getTree());

			pushFollow(FOLLOW_hlinep_in_table79);
			hlinep4=hlinep();
			state._fsp--;

			adaptor.addChild(root_0, hlinep4.getTree());

			pushFollow(FOLLOW_header_in_table88);
			header5=header();
			state._fsp--;

			adaptor.addChild(root_0, header5.getTree());

			pushFollow(FOLLOW_hlinep_in_table97);
			hlinep6=hlinep();
			state._fsp--;

			adaptor.addChild(root_0, hlinep6.getTree());

			pushFollow(FOLLOW_rows_in_table106);
			rows7=rows();
			state._fsp--;

			adaptor.addChild(root_0, rows7.getTree());

			pushFollow(FOLLOW_hlines_in_table115);
			hlines8=hlines();
			state._fsp--;

			adaptor.addChild(root_0, hlines8.getTree());

			// /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g:47:8: ( conclusion )?
			int alt1=2;
			int LA1_0 = input.LA(1);
			if ( ((LA1_0 >= 12 && LA1_0 <= 16)) ) {
				alt1=1;
			}
			switch (alt1) {
				case 1 :
					// /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g:47:8: conclusion
					{
					pushFollow(FOLLOW_conclusion_in_table124);
					conclusion9=conclusion();
					state._fsp--;

					adaptor.addChild(root_0, conclusion9.getTree());

					}
					break;

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
	// $ANTLR end "table"


	public static class stars_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "stars"
	// /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g:49:1: stars : others ( NL )+ ;
	public final SlangTruthTableParser.stars_return stars() throws RecognitionException {
		SlangTruthTableParser.stars_return retval = new SlangTruthTableParser.stars_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token NL11=null;
		ParserRuleReturnScope others10 =null;

		Object NL11_tree=null;

		try {
			// /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g:49:6: ( others ( NL )+ )
			// /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g:49:8: others ( NL )+
			{
			root_0 = (Object)adaptor.nil();


			pushFollow(FOLLOW_others_in_stars133);
			others10=others();
			state._fsp--;

			adaptor.addChild(root_0, others10.getTree());

			// /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g:49:15: ( NL )+
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
					// /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g:49:15: NL
					{
					NL11=(Token)match(input,NL,FOLLOW_NL_in_stars135); 
					NL11_tree = (Object)adaptor.create(NL11);
					adaptor.addChild(root_0, NL11_tree);

					}
					break;

				default :
					if ( cnt2 >= 1 ) break loop2;
					EarlyExitException eee = new EarlyExitException(2, input);
					throw eee;
				}
				cnt2++;
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
	// $ANTLR end "stars"


	public static class header_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "header"
	// /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g:51:1: header : others HASH others ( NL )+ ;
	public final SlangTruthTableParser.header_return header() throws RecognitionException {
		SlangTruthTableParser.header_return retval = new SlangTruthTableParser.header_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token HASH13=null;
		Token NL15=null;
		ParserRuleReturnScope others12 =null;
		ParserRuleReturnScope others14 =null;

		Object HASH13_tree=null;
		Object NL15_tree=null;

		try {
			// /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g:51:7: ( others HASH others ( NL )+ )
			// /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g:51:9: others HASH others ( NL )+
			{
			root_0 = (Object)adaptor.nil();


			pushFollow(FOLLOW_others_in_header145);
			others12=others();
			state._fsp--;

			adaptor.addChild(root_0, others12.getTree());

			HASH13=(Token)match(input,HASH,FOLLOW_HASH_in_header147); 
			HASH13_tree = (Object)adaptor.create(HASH13);
			adaptor.addChild(root_0, HASH13_tree);

			pushFollow(FOLLOW_others_in_header149);
			others14=others();
			state._fsp--;

			adaptor.addChild(root_0, others14.getTree());

			// /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g:51:28: ( NL )+
			int cnt3=0;
			loop3:
			while (true) {
				int alt3=2;
				int LA3_0 = input.LA(1);
				if ( (LA3_0==NL) ) {
					alt3=1;
				}

				switch (alt3) {
				case 1 :
					// /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g:51:28: NL
					{
					NL15=(Token)match(input,NL,FOLLOW_NL_in_header151); 
					NL15_tree = (Object)adaptor.create(NL15);
					adaptor.addChild(root_0, NL15_tree);

					}
					break;

				default :
					if ( cnt3 >= 1 ) break loop3;
					EarlyExitException eee = new EarlyExitException(3, input);
					throw eee;
				}
				cnt3++;
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
	// $ANTLR end "header"


	public static class hlinep_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "hlinep"
	// /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g:53:1: hlinep : HLINE ( NL )+ ;
	public final SlangTruthTableParser.hlinep_return hlinep() throws RecognitionException {
		SlangTruthTableParser.hlinep_return retval = new SlangTruthTableParser.hlinep_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token HLINE16=null;
		Token NL17=null;

		Object HLINE16_tree=null;
		Object NL17_tree=null;

		try {
			// /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g:53:7: ( HLINE ( NL )+ )
			// /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g:53:9: HLINE ( NL )+
			{
			root_0 = (Object)adaptor.nil();


			HLINE16=(Token)match(input,HLINE,FOLLOW_HLINE_in_hlinep160); 
			HLINE16_tree = (Object)adaptor.create(HLINE16);
			adaptor.addChild(root_0, HLINE16_tree);

			// /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g:53:15: ( NL )+
			int cnt4=0;
			loop4:
			while (true) {
				int alt4=2;
				int LA4_0 = input.LA(1);
				if ( (LA4_0==NL) ) {
					alt4=1;
				}

				switch (alt4) {
				case 1 :
					// /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g:53:15: NL
					{
					NL17=(Token)match(input,NL,FOLLOW_NL_in_hlinep162); 
					NL17_tree = (Object)adaptor.create(NL17);
					adaptor.addChild(root_0, NL17_tree);

					}
					break;

				default :
					if ( cnt4 >= 1 ) break loop4;
					EarlyExitException eee = new EarlyExitException(4, input);
					throw eee;
				}
				cnt4++;
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
	// $ANTLR end "hlinep"


	public static class rows_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "rows"
	// /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g:55:1: rows : ( row )+ ;
	public final SlangTruthTableParser.rows_return rows() throws RecognitionException {
		SlangTruthTableParser.rows_return retval = new SlangTruthTableParser.rows_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		ParserRuleReturnScope row18 =null;


		try {
			// /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g:55:5: ( ( row )+ )
			// /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g:55:7: ( row )+
			{
			root_0 = (Object)adaptor.nil();


			// /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g:55:7: ( row )+
			int cnt5=0;
			loop5:
			while (true) {
				int alt5=2;
				int LA5_0 = input.LA(1);
				if ( (LA5_0==OTHER) ) {
					alt5=1;
				}

				switch (alt5) {
				case 1 :
					// /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g:55:7: row
					{
					pushFollow(FOLLOW_row_in_rows171);
					row18=row();
					state._fsp--;

					adaptor.addChild(root_0, row18.getTree());

					}
					break;

				default :
					if ( cnt5 >= 1 ) break loop5;
					EarlyExitException eee = new EarlyExitException(5, input);
					throw eee;
				}
				cnt5++;
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
	// $ANTLR end "rows"


	public static class row_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "row"
	// /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g:57:1: row : others HASH others NL ;
	public final SlangTruthTableParser.row_return row() throws RecognitionException {
		SlangTruthTableParser.row_return retval = new SlangTruthTableParser.row_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token HASH20=null;
		Token NL22=null;
		ParserRuleReturnScope others19 =null;
		ParserRuleReturnScope others21 =null;

		Object HASH20_tree=null;
		Object NL22_tree=null;

		try {
			// /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g:57:4: ( others HASH others NL )
			// /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g:57:6: others HASH others NL
			{
			root_0 = (Object)adaptor.nil();


			pushFollow(FOLLOW_others_in_row180);
			others19=others();
			state._fsp--;

			adaptor.addChild(root_0, others19.getTree());

			HASH20=(Token)match(input,HASH,FOLLOW_HASH_in_row182); 
			HASH20_tree = (Object)adaptor.create(HASH20);
			adaptor.addChild(root_0, HASH20_tree);

			pushFollow(FOLLOW_others_in_row184);
			others21=others();
			state._fsp--;

			adaptor.addChild(root_0, others21.getTree());

			NL22=(Token)match(input,NL,FOLLOW_NL_in_row186); 
			NL22_tree = (Object)adaptor.create(NL22);
			adaptor.addChild(root_0, NL22_tree);

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


	public static class hlines_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "hlines"
	// /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g:59:1: hlines : HLINE ( NL )* ;
	public final SlangTruthTableParser.hlines_return hlines() throws RecognitionException {
		SlangTruthTableParser.hlines_return retval = new SlangTruthTableParser.hlines_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token HLINE23=null;
		Token NL24=null;

		Object HLINE23_tree=null;
		Object NL24_tree=null;

		try {
			// /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g:59:7: ( HLINE ( NL )* )
			// /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g:59:9: HLINE ( NL )*
			{
			root_0 = (Object)adaptor.nil();


			HLINE23=(Token)match(input,HLINE,FOLLOW_HLINE_in_hlines194); 
			HLINE23_tree = (Object)adaptor.create(HLINE23);
			adaptor.addChild(root_0, HLINE23_tree);

			// /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g:59:15: ( NL )*
			loop6:
			while (true) {
				int alt6=2;
				int LA6_0 = input.LA(1);
				if ( (LA6_0==NL) ) {
					alt6=1;
				}

				switch (alt6) {
				case 1 :
					// /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g:59:15: NL
					{
					NL24=(Token)match(input,NL,FOLLOW_NL_in_hlines196); 
					NL24_tree = (Object)adaptor.create(NL24);
					adaptor.addChild(root_0, NL24_tree);

					}
					break;

				default :
					break loop6;
				}
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
	// $ANTLR end "hlines"


	public static class conclusion_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "conclusion"
	// /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g:61:1: conclusion : ( 'Tautology' ( NL )* | 'Contradictory' ( NL )* | 'Contingent' NL cas ( NL ( cas )? )* | 'Valid' ( assign )+ ( NL )* | 'Invalid' ( assign )+ ( NL )* );
	public final SlangTruthTableParser.conclusion_return conclusion() throws RecognitionException {
		SlangTruthTableParser.conclusion_return retval = new SlangTruthTableParser.conclusion_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token string_literal25=null;
		Token NL26=null;
		Token string_literal27=null;
		Token NL28=null;
		Token string_literal29=null;
		Token NL30=null;
		Token NL32=null;
		Token string_literal34=null;
		Token NL36=null;
		Token string_literal37=null;
		Token NL39=null;
		ParserRuleReturnScope cas31 =null;
		ParserRuleReturnScope cas33 =null;
		ParserRuleReturnScope assign35 =null;
		ParserRuleReturnScope assign38 =null;

		Object string_literal25_tree=null;
		Object NL26_tree=null;
		Object string_literal27_tree=null;
		Object NL28_tree=null;
		Object string_literal29_tree=null;
		Object NL30_tree=null;
		Object NL32_tree=null;
		Object string_literal34_tree=null;
		Object NL36_tree=null;
		Object string_literal37_tree=null;
		Object NL39_tree=null;

		try {
			// /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g:61:11: ( 'Tautology' ( NL )* | 'Contradictory' ( NL )* | 'Contingent' NL cas ( NL ( cas )? )* | 'Valid' ( assign )+ ( NL )* | 'Invalid' ( assign )+ ( NL )* )
			int alt15=5;
			switch ( input.LA(1) ) {
			case 15:
				{
				alt15=1;
				}
				break;
			case 13:
				{
				alt15=2;
				}
				break;
			case 12:
				{
				alt15=3;
				}
				break;
			case 16:
				{
				alt15=4;
				}
				break;
			case 14:
				{
				alt15=5;
				}
				break;
			default:
				NoViableAltException nvae =
					new NoViableAltException("", 15, 0, input);
				throw nvae;
			}
			switch (alt15) {
				case 1 :
					// /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g:62:5: 'Tautology' ( NL )*
					{
					root_0 = (Object)adaptor.nil();


					string_literal25=(Token)match(input,15,FOLLOW_15_in_conclusion217); 
					string_literal25_tree = (Object)adaptor.create(string_literal25);
					adaptor.addChild(root_0, string_literal25_tree);

					// /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g:62:17: ( NL )*
					loop7:
					while (true) {
						int alt7=2;
						int LA7_0 = input.LA(1);
						if ( (LA7_0==NL) ) {
							alt7=1;
						}

						switch (alt7) {
						case 1 :
							// /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g:62:17: NL
							{
							NL26=(Token)match(input,NL,FOLLOW_NL_in_conclusion219); 
							NL26_tree = (Object)adaptor.create(NL26);
							adaptor.addChild(root_0, NL26_tree);

							}
							break;

						default :
							break loop7;
						}
					}

					}
					break;
				case 2 :
					// /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g:63:5: 'Contradictory' ( NL )*
					{
					root_0 = (Object)adaptor.nil();


					string_literal27=(Token)match(input,13,FOLLOW_13_in_conclusion226); 
					string_literal27_tree = (Object)adaptor.create(string_literal27);
					adaptor.addChild(root_0, string_literal27_tree);

					// /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g:63:21: ( NL )*
					loop8:
					while (true) {
						int alt8=2;
						int LA8_0 = input.LA(1);
						if ( (LA8_0==NL) ) {
							alt8=1;
						}

						switch (alt8) {
						case 1 :
							// /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g:63:21: NL
							{
							NL28=(Token)match(input,NL,FOLLOW_NL_in_conclusion228); 
							NL28_tree = (Object)adaptor.create(NL28);
							adaptor.addChild(root_0, NL28_tree);

							}
							break;

						default :
							break loop8;
						}
					}

					}
					break;
				case 3 :
					// /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g:64:5: 'Contingent' NL cas ( NL ( cas )? )*
					{
					root_0 = (Object)adaptor.nil();


					string_literal29=(Token)match(input,12,FOLLOW_12_in_conclusion235); 
					string_literal29_tree = (Object)adaptor.create(string_literal29);
					adaptor.addChild(root_0, string_literal29_tree);

					NL30=(Token)match(input,NL,FOLLOW_NL_in_conclusion237); 
					NL30_tree = (Object)adaptor.create(NL30);
					adaptor.addChild(root_0, NL30_tree);

					pushFollow(FOLLOW_cas_in_conclusion239);
					cas31=cas();
					state._fsp--;

					adaptor.addChild(root_0, cas31.getTree());

					// /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g:64:25: ( NL ( cas )? )*
					loop10:
					while (true) {
						int alt10=2;
						int LA10_0 = input.LA(1);
						if ( (LA10_0==NL) ) {
							alt10=1;
						}

						switch (alt10) {
						case 1 :
							// /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g:64:27: NL ( cas )?
							{
							NL32=(Token)match(input,NL,FOLLOW_NL_in_conclusion243); 
							NL32_tree = (Object)adaptor.create(NL32);
							adaptor.addChild(root_0, NL32_tree);

							// /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g:64:30: ( cas )?
							int alt9=2;
							int LA9_0 = input.LA(1);
							if ( (LA9_0==OTHER) ) {
								alt9=1;
							}
							switch (alt9) {
								case 1 :
									// /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g:64:30: cas
									{
									pushFollow(FOLLOW_cas_in_conclusion245);
									cas33=cas();
									state._fsp--;

									adaptor.addChild(root_0, cas33.getTree());

									}
									break;

							}

							}
							break;

						default :
							break loop10;
						}
					}

					}
					break;
				case 4 :
					// /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g:65:5: 'Valid' ( assign )+ ( NL )*
					{
					root_0 = (Object)adaptor.nil();


					string_literal34=(Token)match(input,16,FOLLOW_16_in_conclusion255); 
					string_literal34_tree = (Object)adaptor.create(string_literal34);
					adaptor.addChild(root_0, string_literal34_tree);

					// /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g:65:13: ( assign )+
					int cnt11=0;
					loop11:
					while (true) {
						int alt11=2;
						int LA11_0 = input.LA(1);
						if ( (LA11_0==LSQUARE) ) {
							alt11=1;
						}

						switch (alt11) {
						case 1 :
							// /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g:65:13: assign
							{
							pushFollow(FOLLOW_assign_in_conclusion257);
							assign35=assign();
							state._fsp--;

							adaptor.addChild(root_0, assign35.getTree());

							}
							break;

						default :
							if ( cnt11 >= 1 ) break loop11;
							EarlyExitException eee = new EarlyExitException(11, input);
							throw eee;
						}
						cnt11++;
					}

					// /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g:65:22: ( NL )*
					loop12:
					while (true) {
						int alt12=2;
						int LA12_0 = input.LA(1);
						if ( (LA12_0==NL) ) {
							alt12=1;
						}

						switch (alt12) {
						case 1 :
							// /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g:65:22: NL
							{
							NL36=(Token)match(input,NL,FOLLOW_NL_in_conclusion261); 
							NL36_tree = (Object)adaptor.create(NL36);
							adaptor.addChild(root_0, NL36_tree);

							}
							break;

						default :
							break loop12;
						}
					}

					}
					break;
				case 5 :
					// /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g:66:5: 'Invalid' ( assign )+ ( NL )*
					{
					root_0 = (Object)adaptor.nil();


					string_literal37=(Token)match(input,14,FOLLOW_14_in_conclusion268); 
					string_literal37_tree = (Object)adaptor.create(string_literal37);
					adaptor.addChild(root_0, string_literal37_tree);

					// /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g:66:15: ( assign )+
					int cnt13=0;
					loop13:
					while (true) {
						int alt13=2;
						int LA13_0 = input.LA(1);
						if ( (LA13_0==LSQUARE) ) {
							alt13=1;
						}

						switch (alt13) {
						case 1 :
							// /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g:66:15: assign
							{
							pushFollow(FOLLOW_assign_in_conclusion270);
							assign38=assign();
							state._fsp--;

							adaptor.addChild(root_0, assign38.getTree());

							}
							break;

						default :
							if ( cnt13 >= 1 ) break loop13;
							EarlyExitException eee = new EarlyExitException(13, input);
							throw eee;
						}
						cnt13++;
					}

					// /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g:66:24: ( NL )*
					loop14:
					while (true) {
						int alt14=2;
						int LA14_0 = input.LA(1);
						if ( (LA14_0==NL) ) {
							alt14=1;
						}

						switch (alt14) {
						case 1 :
							// /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g:66:24: NL
							{
							NL39=(Token)match(input,NL,FOLLOW_NL_in_conclusion274); 
							NL39_tree = (Object)adaptor.create(NL39);
							adaptor.addChild(root_0, NL39_tree);

							}
							break;

						default :
							break loop14;
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
	// /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g:68:1: cas : OTHER OTHER ( assign )+ ;
	public final SlangTruthTableParser.cas_return cas() throws RecognitionException {
		SlangTruthTableParser.cas_return retval = new SlangTruthTableParser.cas_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token OTHER40=null;
		Token OTHER41=null;
		ParserRuleReturnScope assign42 =null;

		Object OTHER40_tree=null;
		Object OTHER41_tree=null;

		try {
			// /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g:68:4: ( OTHER OTHER ( assign )+ )
			// /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g:68:6: OTHER OTHER ( assign )+
			{
			root_0 = (Object)adaptor.nil();


			OTHER40=(Token)match(input,OTHER,FOLLOW_OTHER_in_cas283); 
			OTHER40_tree = (Object)adaptor.create(OTHER40);
			adaptor.addChild(root_0, OTHER40_tree);

			OTHER41=(Token)match(input,OTHER,FOLLOW_OTHER_in_cas285); 
			OTHER41_tree = (Object)adaptor.create(OTHER41);
			adaptor.addChild(root_0, OTHER41_tree);

			// /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g:68:18: ( assign )+
			int cnt16=0;
			loop16:
			while (true) {
				int alt16=2;
				int LA16_0 = input.LA(1);
				if ( (LA16_0==LSQUARE) ) {
					alt16=1;
				}

				switch (alt16) {
				case 1 :
					// /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g:68:18: assign
					{
					pushFollow(FOLLOW_assign_in_cas287);
					assign42=assign();
					state._fsp--;

					adaptor.addChild(root_0, assign42.getTree());

					}
					break;

				default :
					if ( cnt16 >= 1 ) break loop16;
					EarlyExitException eee = new EarlyExitException(16, input);
					throw eee;
				}
				cnt16++;
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
	// /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g:70:1: assign : LSQUARE others RSQUARE ;
	public final SlangTruthTableParser.assign_return assign() throws RecognitionException {
		SlangTruthTableParser.assign_return retval = new SlangTruthTableParser.assign_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token LSQUARE43=null;
		Token RSQUARE45=null;
		ParserRuleReturnScope others44 =null;

		Object LSQUARE43_tree=null;
		Object RSQUARE45_tree=null;

		try {
			// /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g:70:7: ( LSQUARE others RSQUARE )
			// /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g:70:9: LSQUARE others RSQUARE
			{
			root_0 = (Object)adaptor.nil();


			LSQUARE43=(Token)match(input,LSQUARE,FOLLOW_LSQUARE_in_assign297); 
			LSQUARE43_tree = (Object)adaptor.create(LSQUARE43);
			adaptor.addChild(root_0, LSQUARE43_tree);

			pushFollow(FOLLOW_others_in_assign299);
			others44=others();
			state._fsp--;

			adaptor.addChild(root_0, others44.getTree());

			RSQUARE45=(Token)match(input,RSQUARE,FOLLOW_RSQUARE_in_assign301); 
			RSQUARE45_tree = (Object)adaptor.create(RSQUARE45);
			adaptor.addChild(root_0, RSQUARE45_tree);

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
	// /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g:72:1: others : ( OTHER )+ ;
	public final SlangTruthTableParser.others_return others() throws RecognitionException {
		SlangTruthTableParser.others_return retval = new SlangTruthTableParser.others_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token OTHER46=null;

		Object OTHER46_tree=null;

		try {
			// /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g:72:7: ( ( OTHER )+ )
			// /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g:72:9: ( OTHER )+
			{
			root_0 = (Object)adaptor.nil();


			// /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g:72:9: ( OTHER )+
			int cnt17=0;
			loop17:
			while (true) {
				int alt17=2;
				int LA17_0 = input.LA(1);
				if ( (LA17_0==OTHER) ) {
					alt17=1;
				}

				switch (alt17) {
				case 1 :
					// /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g:72:9: OTHER
					{
					OTHER46=(Token)match(input,OTHER,FOLLOW_OTHER_in_others309); 
					OTHER46_tree = (Object)adaptor.create(OTHER46);
					adaptor.addChild(root_0, OTHER46_tree);

					}
					break;

				default :
					if ( cnt17 >= 1 ) break loop17;
					EarlyExitException eee = new EarlyExitException(17, input);
					throw eee;
				}
				cnt17++;
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
	public static final BitSet FOLLOW_stars_in_table70 = new BitSet(new long[]{0x0000000000000040L});
	public static final BitSet FOLLOW_hlinep_in_table79 = new BitSet(new long[]{0x0000000000000200L});
	public static final BitSet FOLLOW_header_in_table88 = new BitSet(new long[]{0x0000000000000040L});
	public static final BitSet FOLLOW_hlinep_in_table97 = new BitSet(new long[]{0x0000000000000200L});
	public static final BitSet FOLLOW_rows_in_table106 = new BitSet(new long[]{0x0000000000000040L});
	public static final BitSet FOLLOW_hlines_in_table115 = new BitSet(new long[]{0x000000000001F002L});
	public static final BitSet FOLLOW_conclusion_in_table124 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_others_in_stars133 = new BitSet(new long[]{0x0000000000000100L});
	public static final BitSet FOLLOW_NL_in_stars135 = new BitSet(new long[]{0x0000000000000102L});
	public static final BitSet FOLLOW_others_in_header145 = new BitSet(new long[]{0x0000000000000020L});
	public static final BitSet FOLLOW_HASH_in_header147 = new BitSet(new long[]{0x0000000000000200L});
	public static final BitSet FOLLOW_others_in_header149 = new BitSet(new long[]{0x0000000000000100L});
	public static final BitSet FOLLOW_NL_in_header151 = new BitSet(new long[]{0x0000000000000102L});
	public static final BitSet FOLLOW_HLINE_in_hlinep160 = new BitSet(new long[]{0x0000000000000100L});
	public static final BitSet FOLLOW_NL_in_hlinep162 = new BitSet(new long[]{0x0000000000000102L});
	public static final BitSet FOLLOW_row_in_rows171 = new BitSet(new long[]{0x0000000000000202L});
	public static final BitSet FOLLOW_others_in_row180 = new BitSet(new long[]{0x0000000000000020L});
	public static final BitSet FOLLOW_HASH_in_row182 = new BitSet(new long[]{0x0000000000000200L});
	public static final BitSet FOLLOW_others_in_row184 = new BitSet(new long[]{0x0000000000000100L});
	public static final BitSet FOLLOW_NL_in_row186 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_HLINE_in_hlines194 = new BitSet(new long[]{0x0000000000000102L});
	public static final BitSet FOLLOW_NL_in_hlines196 = new BitSet(new long[]{0x0000000000000102L});
	public static final BitSet FOLLOW_15_in_conclusion217 = new BitSet(new long[]{0x0000000000000102L});
	public static final BitSet FOLLOW_NL_in_conclusion219 = new BitSet(new long[]{0x0000000000000102L});
	public static final BitSet FOLLOW_13_in_conclusion226 = new BitSet(new long[]{0x0000000000000102L});
	public static final BitSet FOLLOW_NL_in_conclusion228 = new BitSet(new long[]{0x0000000000000102L});
	public static final BitSet FOLLOW_12_in_conclusion235 = new BitSet(new long[]{0x0000000000000100L});
	public static final BitSet FOLLOW_NL_in_conclusion237 = new BitSet(new long[]{0x0000000000000200L});
	public static final BitSet FOLLOW_cas_in_conclusion239 = new BitSet(new long[]{0x0000000000000102L});
	public static final BitSet FOLLOW_NL_in_conclusion243 = new BitSet(new long[]{0x0000000000000302L});
	public static final BitSet FOLLOW_cas_in_conclusion245 = new BitSet(new long[]{0x0000000000000102L});
	public static final BitSet FOLLOW_16_in_conclusion255 = new BitSet(new long[]{0x0000000000000080L});
	public static final BitSet FOLLOW_assign_in_conclusion257 = new BitSet(new long[]{0x0000000000000182L});
	public static final BitSet FOLLOW_NL_in_conclusion261 = new BitSet(new long[]{0x0000000000000102L});
	public static final BitSet FOLLOW_14_in_conclusion268 = new BitSet(new long[]{0x0000000000000080L});
	public static final BitSet FOLLOW_assign_in_conclusion270 = new BitSet(new long[]{0x0000000000000182L});
	public static final BitSet FOLLOW_NL_in_conclusion274 = new BitSet(new long[]{0x0000000000000102L});
	public static final BitSet FOLLOW_OTHER_in_cas283 = new BitSet(new long[]{0x0000000000000200L});
	public static final BitSet FOLLOW_OTHER_in_cas285 = new BitSet(new long[]{0x0000000000000080L});
	public static final BitSet FOLLOW_assign_in_cas287 = new BitSet(new long[]{0x0000000000000082L});
	public static final BitSet FOLLOW_LSQUARE_in_assign297 = new BitSet(new long[]{0x0000000000000200L});
	public static final BitSet FOLLOW_others_in_assign299 = new BitSet(new long[]{0x0000000000000400L});
	public static final BitSet FOLLOW_RSQUARE_in_assign301 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_OTHER_in_others309 = new BitSet(new long[]{0x0000000000000202L});
}
