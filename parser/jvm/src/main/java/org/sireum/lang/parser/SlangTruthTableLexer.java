// $ANTLR 3.5.2 /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g 2023-08-16 15:08:23
 package org.sireum.lang.parser; 

import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

@SuppressWarnings("all")
public class SlangTruthTableLexer extends Lexer {
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
	// delegators
	public Lexer[] getDelegates() {
		return new Lexer[] {};
	}

	public SlangTruthTableLexer() {} 
	public SlangTruthTableLexer(CharStream input) {
		this(input, new RecognizerSharedState());
	}
	public SlangTruthTableLexer(CharStream input, RecognizerSharedState state) {
		super(input,state);
	}
	@Override public String getGrammarFileName() { return "/Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g"; }

	// $ANTLR start "T__12"
	public final void mT__12() throws RecognitionException {
		try {
			int _type = T__12;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g:4:7: ( 'Contingent' )
			// /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g:4:9: 'Contingent'
			{
			match("Contingent"); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__12"

	// $ANTLR start "T__13"
	public final void mT__13() throws RecognitionException {
		try {
			int _type = T__13;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g:5:7: ( 'Contradictory' )
			// /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g:5:9: 'Contradictory'
			{
			match("Contradictory"); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__13"

	// $ANTLR start "T__14"
	public final void mT__14() throws RecognitionException {
		try {
			int _type = T__14;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g:6:7: ( 'Invalid' )
			// /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g:6:9: 'Invalid'
			{
			match("Invalid"); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__14"

	// $ANTLR start "T__15"
	public final void mT__15() throws RecognitionException {
		try {
			int _type = T__15;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g:7:7: ( 'Tautology' )
			// /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g:7:9: 'Tautology'
			{
			match("Tautology"); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__15"

	// $ANTLR start "T__16"
	public final void mT__16() throws RecognitionException {
		try {
			int _type = T__16;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g:8:7: ( 'Valid' )
			// /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g:8:9: 'Valid'
			{
			match("Valid"); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__16"

	// $ANTLR start "HLINE"
	public final void mHLINE() throws RecognitionException {
		try {
			int _type = HLINE;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g:76:6: ( '-' '-' '-' '-' ( '-' )+ )
			// /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g:76:8: '-' '-' '-' '-' ( '-' )+
			{
			match('-'); 
			match('-'); 
			match('-'); 
			match('-'); 
			// /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g:76:24: ( '-' )+
			int cnt1=0;
			loop1:
			while (true) {
				int alt1=2;
				int LA1_0 = input.LA(1);
				if ( (LA1_0=='-') ) {
					alt1=1;
				}

				switch (alt1) {
				case 1 :
					// /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g:76:24: '-'
					{
					match('-'); 
					}
					break;

				default :
					if ( cnt1 >= 1 ) break loop1;
					EarlyExitException eee = new EarlyExitException(1, input);
					throw eee;
				}
				cnt1++;
			}

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "HLINE"

	// $ANTLR start "HASH"
	public final void mHASH() throws RecognitionException {
		try {
			int _type = HASH;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g:78:5: ( '#' )
			// /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g:78:7: '#'
			{
			match('#'); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "HASH"

	// $ANTLR start "NL"
	public final void mNL() throws RecognitionException {
		try {
			int _type = NL;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g:80:3: ( '\\n' )
			// /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g:80:5: '\\n'
			{
			match('\n'); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "NL"

	// $ANTLR start "LSQUARE"
	public final void mLSQUARE() throws RecognitionException {
		try {
			int _type = LSQUARE;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g:82:8: ( '[' )
			// /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g:82:10: '['
			{
			match('['); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "LSQUARE"

	// $ANTLR start "RSQUARE"
	public final void mRSQUARE() throws RecognitionException {
		try {
			int _type = RSQUARE;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g:84:8: ( ']' )
			// /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g:84:10: ']'
			{
			match(']'); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "RSQUARE"

	// $ANTLR start "COMMENT"
	public final void mCOMMENT() throws RecognitionException {
		try {
			int _type = COMMENT;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g:86:8: ( '//' (~ ( '\\n' | '\\r' ) )* ( '\\r' )? '\\n' )
			// /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g:86:10: '//' (~ ( '\\n' | '\\r' ) )* ( '\\r' )? '\\n'
			{
			match("//"); 

			// /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g:86:15: (~ ( '\\n' | '\\r' ) )*
			loop2:
			while (true) {
				int alt2=2;
				int LA2_0 = input.LA(1);
				if ( ((LA2_0 >= '\u0000' && LA2_0 <= '\t')||(LA2_0 >= '\u000B' && LA2_0 <= '\f')||(LA2_0 >= '\u000E' && LA2_0 <= '\uFFFF')) ) {
					alt2=1;
				}

				switch (alt2) {
				case 1 :
					// /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g:
					{
					if ( (input.LA(1) >= '\u0000' && input.LA(1) <= '\t')||(input.LA(1) >= '\u000B' && input.LA(1) <= '\f')||(input.LA(1) >= '\u000E' && input.LA(1) <= '\uFFFF') ) {
						input.consume();
					}
					else {
						MismatchedSetException mse = new MismatchedSetException(null,input);
						recover(mse);
						throw mse;
					}
					}
					break;

				default :
					break loop2;
				}
			}

			// /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g:86:33: ( '\\r' )?
			int alt3=2;
			int LA3_0 = input.LA(1);
			if ( (LA3_0=='\r') ) {
				alt3=1;
			}
			switch (alt3) {
				case 1 :
					// /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g:86:33: '\\r'
					{
					match('\r'); 
					}
					break;

			}

			match('\n'); 
			_channel=HIDDEN;
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "COMMENT"

	// $ANTLR start "WS"
	public final void mWS() throws RecognitionException {
		try {
			int _type = WS;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g:88:3: ( ( ' ' | '\\t' | '\\r' )+ )
			// /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g:88:5: ( ' ' | '\\t' | '\\r' )+
			{
			// /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g:88:5: ( ' ' | '\\t' | '\\r' )+
			int cnt4=0;
			loop4:
			while (true) {
				int alt4=2;
				int LA4_0 = input.LA(1);
				if ( (LA4_0=='\t'||LA4_0=='\r'||LA4_0==' ') ) {
					alt4=1;
				}

				switch (alt4) {
				case 1 :
					// /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g:
					{
					if ( input.LA(1)=='\t'||input.LA(1)=='\r'||input.LA(1)==' ' ) {
						input.consume();
					}
					else {
						MismatchedSetException mse = new MismatchedSetException(null,input);
						recover(mse);
						throw mse;
					}
					}
					break;

				default :
					if ( cnt4 >= 1 ) break loop4;
					EarlyExitException eee = new EarlyExitException(4, input);
					throw eee;
				}
				cnt4++;
			}

			_channel=HIDDEN;
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "WS"

	// $ANTLR start "OTHER"
	public final void mOTHER() throws RecognitionException {
		try {
			int _type = OTHER;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g:90:6: ( . )
			// /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g:90:8: .
			{
			matchAny(); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "OTHER"

	@Override
	public void mTokens() throws RecognitionException {
		// /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g:1:8: ( T__12 | T__13 | T__14 | T__15 | T__16 | HLINE | HASH | NL | LSQUARE | RSQUARE | COMMENT | WS | OTHER )
		int alt5=13;
		int LA5_0 = input.LA(1);
		if ( (LA5_0=='C') ) {
			int LA5_1 = input.LA(2);
			if ( (LA5_1=='o') ) {
				int LA5_13 = input.LA(3);
				if ( (LA5_13=='n') ) {
					int LA5_24 = input.LA(4);
					if ( (LA5_24=='t') ) {
						int LA5_25 = input.LA(5);
						if ( (LA5_25=='i') ) {
							alt5=1;
						}
						else if ( (LA5_25=='r') ) {
							alt5=2;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 5 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 5, 25, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

					}

					else {
						int nvaeMark = input.mark();
						try {
							for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
								input.consume();
							}
							NoViableAltException nvae =
								new NoViableAltException("", 5, 24, input);
							throw nvae;
						} finally {
							input.rewind(nvaeMark);
						}
					}

				}

				else {
					int nvaeMark = input.mark();
					try {
						for (int nvaeConsume = 0; nvaeConsume < 3 - 1; nvaeConsume++) {
							input.consume();
						}
						NoViableAltException nvae =
							new NoViableAltException("", 5, 13, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

			}

			else {
				alt5=13;
			}

		}
		else if ( (LA5_0=='I') ) {
			int LA5_2 = input.LA(2);
			if ( (LA5_2=='n') ) {
				alt5=3;
			}

			else {
				alt5=13;
			}

		}
		else if ( (LA5_0=='T') ) {
			int LA5_3 = input.LA(2);
			if ( (LA5_3=='a') ) {
				alt5=4;
			}

			else {
				alt5=13;
			}

		}
		else if ( (LA5_0=='V') ) {
			int LA5_4 = input.LA(2);
			if ( (LA5_4=='a') ) {
				alt5=5;
			}

			else {
				alt5=13;
			}

		}
		else if ( (LA5_0=='-') ) {
			int LA5_5 = input.LA(2);
			if ( (LA5_5=='-') ) {
				alt5=6;
			}

			else {
				alt5=13;
			}

		}
		else if ( (LA5_0=='#') ) {
			alt5=7;
		}
		else if ( (LA5_0=='\n') ) {
			alt5=8;
		}
		else if ( (LA5_0=='[') ) {
			alt5=9;
		}
		else if ( (LA5_0==']') ) {
			alt5=10;
		}
		else if ( (LA5_0=='/') ) {
			int LA5_10 = input.LA(2);
			if ( (LA5_10=='/') ) {
				alt5=11;
			}

			else {
				alt5=13;
			}

		}
		else if ( (LA5_0=='\t'||LA5_0=='\r'||LA5_0==' ') ) {
			alt5=12;
		}
		else if ( ((LA5_0 >= '\u0000' && LA5_0 <= '\b')||(LA5_0 >= '\u000B' && LA5_0 <= '\f')||(LA5_0 >= '\u000E' && LA5_0 <= '\u001F')||(LA5_0 >= '!' && LA5_0 <= '\"')||(LA5_0 >= '$' && LA5_0 <= ',')||LA5_0=='.'||(LA5_0 >= '0' && LA5_0 <= 'B')||(LA5_0 >= 'D' && LA5_0 <= 'H')||(LA5_0 >= 'J' && LA5_0 <= 'S')||LA5_0=='U'||(LA5_0 >= 'W' && LA5_0 <= 'Z')||LA5_0=='\\'||(LA5_0 >= '^' && LA5_0 <= '\uFFFF')) ) {
			alt5=13;
		}

		else {
			NoViableAltException nvae =
				new NoViableAltException("", 5, 0, input);
			throw nvae;
		}

		switch (alt5) {
			case 1 :
				// /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g:1:10: T__12
				{
				mT__12(); 

				}
				break;
			case 2 :
				// /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g:1:16: T__13
				{
				mT__13(); 

				}
				break;
			case 3 :
				// /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g:1:22: T__14
				{
				mT__14(); 

				}
				break;
			case 4 :
				// /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g:1:28: T__15
				{
				mT__15(); 

				}
				break;
			case 5 :
				// /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g:1:34: T__16
				{
				mT__16(); 

				}
				break;
			case 6 :
				// /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g:1:40: HLINE
				{
				mHLINE(); 

				}
				break;
			case 7 :
				// /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g:1:46: HASH
				{
				mHASH(); 

				}
				break;
			case 8 :
				// /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g:1:51: NL
				{
				mNL(); 

				}
				break;
			case 9 :
				// /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g:1:54: LSQUARE
				{
				mLSQUARE(); 

				}
				break;
			case 10 :
				// /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g:1:62: RSQUARE
				{
				mRSQUARE(); 

				}
				break;
			case 11 :
				// /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g:1:70: COMMENT
				{
				mCOMMENT(); 

				}
				break;
			case 12 :
				// /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g:1:78: WS
				{
				mWS(); 

				}
				break;
			case 13 :
				// /Users/robby/Repositories/Sireum/kekinian/slang/parser/shared/src/main/resources/SlangTruthTable.g:1:81: OTHER
				{
				mOTHER(); 

				}
				break;

		}
	}



}
