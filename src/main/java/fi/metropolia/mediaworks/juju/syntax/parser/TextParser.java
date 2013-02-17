/*******************************************************************************
 * Copyright (c) 2013 Olli Alm / Metropolia www.metropolia.fi
 * 
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 * 
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND 
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 ******************************************************************************/
package fi.metropolia.mediaworks.juju.syntax.parser;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import fi.metropolia.mediaworks.juju.document.Document;
import fi.metropolia.mediaworks.juju.document.PartOfSpeech;
import fi.metropolia.mediaworks.juju.document.PosToken;
import fi.metropolia.mediaworks.juju.document.Sentence;

/**
 * Tuottaa tekstimuotoisesta datasta poka-formaatin XML:ää
 * 
 * Formaatti: <A> <S no="1"> <UP no="1"><T>Tänään</T><L>tänään</L></UP> <BS
 * no="2"><T>on</T><L>on</L></BS> <BS no="3"><T>kiva</T><L>kiva</L></BS> <BS
 * no="4"><T>päivä</T><L>päivä</L></BS> <SY no="5"><T>.</T><L>.</L></SY> </S> *
 * UP --> isolla alkukirjaimella BS --> perussanat SY --> symboolit NM -->
 * numeraalit
 * 
 * perusjäsennyksessä ei tuoteta seuraavia: VB --> verbit PN --> pronominit OT
 * --> muut (varten, että)
 * 
 * @author alm
 * 
 */

public class TextParser implements IDocumentParser {
	public static char PUNCTUATIONMARKS[] = { '.', '?', '!' };
	public static final HashSet<Integer> PUNCTUATIONLIST = new HashSet<Integer>(conv_charArray2integerList(PUNCTUATIONMARKS));

	public static char SYMBOLMARKS[] = { ';', ':', ',', '_', '|' };
	public static final HashSet<Integer> SYMBOLLIST = new HashSet<Integer>(conv_charArray2integerList(SYMBOLMARKS));

	private static List<Integer> conv_charArray2integerList(char[] ct) {
		List<Integer> l = new LinkedList<Integer>();
		for (int i = 0; i < ct.length; ++i)
			l.add(new Integer(ct[i]));
		return l;
	}

	//TYYPIT
	public static final int BASETYPE = 1;
	public static final int UPPERTYPE = 2;
	public static final int NUMERALTYPE = 3;
	public static final int SYMBOLTYPE = 4;
	public static final int VERBTYPE = 5;

	public static final int ERRORTYPE = -1;

	//yksittäinen token parserissa	
	protected StringBuffer token;
	protected boolean setTokenType;
	/**
	 * lause lopetetaan jos on '.' ja sen jälkeen iso kirjain
	 */
	protected boolean sentenceEnd;
	protected boolean wasDigit;
	protected boolean upperCaseLetter;

	private PartOfSpeech tokenType;

//	public TextParser(String text) {
//		this();
//		this.setInput(text); 
//		this.parse();
//	}

	public TextParser() {
	}

	private Sentence currentSentence;
	private Document currentDocument;

	@Override
	public Document parseDocument(String content) {
		currentDocument = new Document();
		currentSentence = new Sentence();
		currentDocument.add(currentSentence);

		this.tokenType = PartOfSpeech.UNIDENTIFIED;
		this.setTokenType = true;
		this.wasDigit = false;
		this.upperCaseLetter = false;

		this.sentenceEnd = false; //lause lopetetaan jos on '.' ja sen jälkeen iso kirjain

		this.token = new StringBuffer();
		StringReader stringreader = null;
		try {

			stringreader = new StringReader(content);

			int c = stringreader.read();

			while (c != -1) {

				//	log.debug("Parsitaan merkki: '"+(char)c+"'");

				//SPACE
				if (Character.isSpaceChar(c)) {

					//		log.debug("spacechar '"+(char)c+"'");

					this.parseSpace(c);

					//PUNCT
				} else if (PUNCTUATIONLIST.contains(c)) {
					//	log.debug("punct '"+(char)c+"'");

					this.parsePunctuation(c);

					//UPPER
				} else if (Character.isUpperCase(c)) {
					//	log.debug("upper '"+(char)c+"'");

					this.parseUpper(c);

					//LETTER					
				} else if (Character.isLetter(c)) {
					//	log.debug("letter '"+(char)c+"'");

					this.parseLetter(c);

					//DIGIT
				} else if (Character.isDigit(c)) {
					//	log.debug("digit '"+(char)c+"'");

					this.parseDigit(c);

					// EMPTY CHAR / linebreak?
				} else if (c == 10 || c == '\t') {

					//	log.debug("Linebreak char '"+c+"', ohitetaan!");

					//jos token määritelty, lisätään se
					if (token.length() > 0) {

						// HUOM! rivinvaihtoa ei tulkita
						// lauseen loppumiseksi (TODO: onko syytä säätää
						// ko ominaisuus parametriksi / pitäiskö?)

						this.addToken(token, tokenType);
					}

					//SYMBOL
				} else {
//				} else if(SYMBOLLIST.contains(c)) {
					//	log.debug("symbol: '"+(char)c+"'");
					this.parseSymbol(c);

				}
				c = stringreader.read();
			}

			stringreader.close();

		} catch (IOException e) {
			//	log.error("IOException : " + e.getMessage()) ; 
		}

		if (token.length() > 0) {

			this.addToken(token, this.tokenType);
			sentenceEnd = false;
		}

		return currentDocument;
	}

	/**
	 * FIXME: Why has parameter? -Joni
	 * 
	 * @param c
	 *            Does nothing
	 */
	protected void parseSpace(int c) {
		if (sentenceEnd && token.length() > 0) {
			this.addToken(token, PartOfSpeech.SYMBOL);
			this.newSentence();

			sentenceEnd = false;
		} else {
			if (token.length() > 0) {
				this.addToken(token, tokenType);
				tokenType = PartOfSpeech.UNIDENTIFIED;
			}
		}
	}

	protected void parseSymbol(int c) {

		/**
		 * Jos edellinen token on BS (=pieni kirjain) JA symbooli on '-', niin
		 * jatketaan tokenia, esim 'nimi-hirviö' muodostaa oman tokenin,
		 * vastaavasti 'Jukka-Pekka'
		 */

		if (this.tokenType == PartOfSpeech.BASE) {

			if (c == '-') {
				token.append("-");
				return;
			}
		}

		if (token.length() > 0) {
			this.addToken(token, tokenType);
		}

		if (this.setTokenType) {
			tokenType = PartOfSpeech.SYMBOL;
		}

		token = new StringBuffer();
		if (c == '&') {
			token.append("&amp;");
		} else if (c == '<') {
			token.append("&lt;");
		} else if (c == '>') {
			token.append("&gt;");
		} else {
			token.append((char) c);
		}
		this.addToken(token, tokenType);
	}

	protected void parsePunctuation(int c) {

		if (this.setTokenType) {
			tokenType = PartOfSpeech.SYMBOL;
			this.setTokenType = false;
		}

		if (upperCaseLetter && (c == '.')) { //jos edellinen iso kirjain, piste tulee samaan tokeniin
			token.append((char) c); //ja lause jatkuu (=sentenceEnd pidetään falsena
		} else {
			sentenceEnd = true;
			if (token.length() > 0) {
				this.addToken(token, tokenType);
			}

			tokenType = PartOfSpeech.SYMBOL;
			//log.debug("\tType asetettu: SY");
			this.setTokenType = false;
			token = new StringBuffer();
			token.append((char) c);
		}

	}

	protected void parseUpper(int c) {

		if (sentenceEnd) {
			if (token.length() > 0) {
				this.addToken(token, PartOfSpeech.SYMBOL);
			}
			this.newSentence();

			sentenceEnd = false;
		}

		if (this.setTokenType) {
			this.upperCaseLetter = true;
			tokenType = PartOfSpeech.BASE;
			this.setTokenType = false;
		} else {
			this.upperCaseLetter = false;
		}

		token.append((char) c);

	}

	protected void parseLetter(int c) {
		this.upperCaseLetter = false;
		if (this.setTokenType) {
			tokenType = PartOfSpeech.BASE;
			setTokenType = false;
		}
		token.append((char) c);
	}

	protected void parseDigit(int c) {
		if (this.setTokenType) {
			tokenType = PartOfSpeech.NUMERAL;
			setTokenType = false;
		}

		token.append((char) c);
		this.wasDigit = true;
	}

	protected void newSentence() {
		currentSentence = new Sentence();
		currentDocument.add(currentSentence);
	}

	protected String processLemma(String input) {
		return input;
	}

	protected void addToken(StringBuffer token, PartOfSpeech tokenType) {
		String text = token.toString();
		String lemma = this.processLemma(token.toString().toLowerCase());

		PosToken posToken = new PosToken(text, lemma, tokenType);
		currentSentence.add(posToken);

		this.token = new StringBuffer();
		this.setTokenType = true;
	}

	@Override
	public List<String> getSupportedLanguages() {
		return null;
	}
	
	@Override
	public int getPriority() {
		return 0;
	}
}
