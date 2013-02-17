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
package fi.metropolia.mediaworks.juju.syntax.fi.omorfi;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

//import fi.seco.semweb.service.poka.filehandlers.InputReader;

/**
 * simple tokenizer, based on Poka TextParser
 * TODO: remove all unnecessary parts
 * @author alm
 *
 */

public class Tokenizer {
	private static final Logger log = Logger.getLogger(Tokenizer.class);
	
	public static char PUNCTUATIONMARKS[] = {'.', '?', '!'};
	public static final HashSet<Integer> PUNCTUATIONLIST =
				new HashSet<Integer>(conv_charArray2integerList(PUNCTUATIONMARKS)); 
	
	public static char SYMBOLMARKS[] = {';', ':', ',','_','|'};
	public static final HashSet<Integer> SYMBOLLIST =
				new HashSet<Integer>(conv_charArray2integerList(SYMBOLMARKS));
	
	public static List<Integer> conv_charArray2integerList(char[] ct)
	{
		List<Integer> l = new LinkedList<Integer>();
		for (int i = 0; i < ct.length; ++i)
			l.add(new Integer(ct[i]));
		return l;
	}

	public HashMap<Integer, String> typeHash = new HashMap<Integer, String>();
	//TYYPIT
	public static final int BASETYPE =    1;
	public static final int UPPERTYPE =   2;
	public static final int NUMERALTYPE = 3;
	public static final int SYMBOLTYPE =  4;
	public static final int VERBTYPE =  5;
	
	public static final int ERRORTYPE =  -1;
	
	public static final String INDENTATION = "   ";
	
	//counters
	protected int sentenceCount;
	protected int tokenCount;
	
	//yksittäinen token parserissa	
	protected StringBuffer token;
	protected boolean setTokenType;
/**
 * lause lopetetaan jos on '.' ja sen jälkeen iso kirjain
 */	
	protected boolean sentenceEnd;
	protected boolean wasDigit;
	protected boolean upperCaseLetter;
	
	protected int tokenType;
	
	//sisään syötettävä teksti
	protected String inputString;
	
	//result
	protected ArrayList<String> result;
	protected StringBuffer buffer;
	
	
	public Tokenizer(String text) {
		this();
		this.setInput(text); 
	}
	
	public Tokenizer() {
		result = new ArrayList<String>();
		this.sentenceCount = 1;
		this.tokenCount = 1;
		this.buffer = new StringBuffer();
		
		this.initHash();
	}
	
	private void initHash() {
		typeHash.put(BASETYPE, "BS");
		typeHash.put(VERBTYPE, "VB");
		typeHash.put(UPPERTYPE, "UP");
		typeHash.put(NUMERALTYPE, "NM");
		typeHash.put(SYMBOLTYPE, "SY");
		
	}
	
	//HUOM! setDocument löytyy jo tiedostokäsittelijältä,
	//joten käytetään sitä...
	public void setInput(String text) {
		result = new ArrayList<String>();
		this.sentenceCount = 1;
		this.tokenCount = 1;
		this.buffer = new StringBuffer();
		
		this.inputString = text;
	}
/**
 * setInput + parse
 */	
	public void parse(String text) {
		
		this.setInput(text);
		this.parse();
	}

	//palauttaa XML:n Stringinä?
	public ArrayList<String> getResult() {
		return result;
	}

/*
 * PARSERIPALIKAT
 * 
 */	
	public void parse() {
		parseStart();
	}

	public void parseStart() {
		
		this.tokenType = -1;
		this.setTokenType = true;
		this.wasDigit = false;
		this.upperCaseLetter = false;
		
		this.sentenceEnd = false; //lause lopetetaan jos on '.' ja sen jälkeen iso kirjain
		
		this.token = new StringBuffer();
		StringReader stringreader = null;
		try {

			stringreader = new StringReader (  this.inputString  ) ; 

			int c = stringreader.read () ; 

			while  (  c != -1  ) { 
				
				log.debug("Parsing character: '"+(char)c+"'");
				
				//SPACE
				if(Character.isSpaceChar(c)) {
					log.debug("spacechar '"+(char)c+"'");
					
					this.parseSpace(c);								

				//PUNCT
				} else if(PUNCTUATIONLIST.contains(c)) {
					log.debug("punct '"+(char)c+"'");
					
					this.parsePunctuation(c);

				//UPPER
				} else if(Character.isUpperCase(c)) {
					log.debug("uppercase");
					log.debug("upper '"+(char)c+"'");
									
					this.parseUpper(c);
				
				//LETTER					
				} else if(Character.isLetter(c)) {
					log.debug("letter '"+(char)c+"'");

					this.parseLetter(c);

				//DIGIT
				} else if(Character.isDigit(c)) {
					log.debug("digit '"+(char)c+"'");
					
					this.parseDigit(c);

				// EMPTY CHAR / linebreak?
				} else if(c==10) {
					log.debug("Linebreak char '"+c+"', ohitetaan!");
				
					//jos token määritelty, lisätään se
					if(token.length()>0) {
			
						// HUOM! rivinvaihtoa ei tulkita
						// lauseen loppumiseksi (TODO: onko syytä säätää
						// ko ominaisuus parametriksi / pitäiskö?)
 
						this.addToken(token, tokenType);
					}

			    //SYMBOL
				} else {
					log.debug("SUNBOL: '"+(char)c+"' num: "+c);
					if(c== 158 || c==142) { // strange empty symbol: TODO, CHECK!
						// skip, do nothing
						log.debug("Empty / strange symbol, skip '"+(char)c+"'");
					} else {
						log.debug("symbol: '"+(char)c+"'");
						this.parseSymbol(c);	
					}
					
				}
				c = stringreader.read();				
			}  

			stringreader.close (  ) ; 
 
		} catch  (  IOException e  ) {  
			log.error("IOException : " + e.getMessage()) ; 
		}
		
		if(token.length() > 0) {
			
			this.addToken(token, this.tokenType);
			sentenceEnd = false;
		}
	
	}
	
	/**
	 * FIXME: Why has parameter? -Joni
	 * @param c Does nothing  
	 */
	protected void parseSpace(int c) {
		//log.debug("--> parseSpace");
		if(sentenceEnd && token.length()>0) {
		
			//log.debug("--> sentenceEnd: '"+token.toString()+"'");
			this.addToken(token, Tokenizer.SYMBOLTYPE);
			this.newSentence();
			
			sentenceEnd = false;
		} else {
			//log.debug("--> else sentenceend");
			if(token.length()>0) {
				this.addToken(token, tokenType);
				tokenType = -1;
			}	
		}
	}
	
	protected void parseSymbol(int c) {
		
		/**
		 * Jos edellinen token on BS (=pieni kirjain) JA
		 * symbooli on '-', niin jatketaan tokenia, esim
		 * 'nimi-hirviö' muodostaa oman tokenin, vastaavasti
		 * 'Jukka-Pekka', 
		 * 
		 * FIXME: lisää numeraltype, jotta 1960-luku ym. tulee samaan tokeniin
		 */
		
		if(this.tokenType==Tokenizer.BASETYPE ||
			this.tokenType==Tokenizer.UPPERTYPE || this.tokenType==Tokenizer.NUMERALTYPE) { //--> LISÄÄ TÄMÄ JOSKUS, KUN LISÄYS, PITÄÄ KORJATA LEMMATIZERISSA (24.8.2012)
			
			if(c==':'|| c=='-'|| c=='\'') {
				token.append((char)c);
				return;
			}
		}
		
		if(token.length()>0) {
			this.addToken(token, tokenType);
		}	
		
//		 AINA TOSI?
 		 if(this.setTokenType) { 
			
			tokenType = Tokenizer.SYMBOLTYPE;
			//log.debug("\tType asetettu: SY");

//			this.setTokenType = true; 
			//--> EI TARVITA, yksittäinen symboli lisätään, jonka
			// jälkeen odotetaan, että uusi token tulee vastaan 
		
		}
		
		token = new StringBuffer();
		if(c=='&') {
			token.append("&amp;");
		} else if(c=='<') {
			token.append("&lt;");
		} else if(c=='>') {
			token.append("&gt;");
		} else {
			token.append((char)c);
		}
		this.addToken(token, tokenType);
	}
	
	protected void parsePunctuation(int c) {
		
		if(this.setTokenType) {
			
			tokenType = Tokenizer.SYMBOLTYPE;
			log.debug("\tType asetettu: SY");
			this.setTokenType = false;
		
		}
		
		if(upperCaseLetter && (c=='.')) { //jos edellinen iso kirjain, piste tulee samaan tokeniin
			token.append((char)c); //ja lause jatkuu (=sentenceEnd pidetään falsena
		} else {
			sentenceEnd = true;
			if(token.length()>0) {
				this.addToken(token, tokenType);
			}	
			
			tokenType = Tokenizer.SYMBOLTYPE;
			//log.debug("\tType asetettu: SY");
			this.setTokenType = false;
			token = new StringBuffer();
			token.append((char)c);			
		}
		
	}
	
	protected void parseUpper(int c) {
				
		if(sentenceEnd) {
			if(token.length()>0) {
				this.addToken(token, Tokenizer.SYMBOLTYPE);
			}
			this.newSentence();
			
			sentenceEnd = false;
		}
		
		if(this.setTokenType) {
			
			this.upperCaseLetter = true;
			tokenType = Tokenizer.UPPERTYPE;
			//log.debug("\tType asetettu: UP");
			this.setTokenType = false;
		} else {
			this.upperCaseLetter = false;
		}
		
		token.append((char)c);
		
	}
	
	protected void parseLetter(int c) {
		
		this.upperCaseLetter = false;
		
		if(token.toString().equals(".")) { //if the previous token was '.', separate it
			this.addToken(token, tokenType);
		}
		
		if(this.setTokenType) {
			tokenType = Tokenizer.BASETYPE;
			//log.debug("\tType asetettu: BS");
			setTokenType = false;
		}
		token.append((char)c);
		
	}
	
	protected void parseDigit(int c) {
		
		if(token.toString().equals(".")) { //if the previous token was '.', separate it
			this.addToken(token, tokenType);
		}
		
		if(this.setTokenType) {
			tokenType = Tokenizer.NUMERALTYPE;
			//log.debug("\tType asetettu: NUMERAL");
			setTokenType = false;
		}
		
		token.append((char)c);
		this.wasDigit = true;
	}
	
	protected void newSentence() {
		
		this.sentenceCount++;
		this.tokenCount = 1;
		
	}
	
	/**
	 * FIXME: Why has parameter tokenType? -Joni
	 * @param tokenType Does nothing
	 */
	protected void addToken(StringBuffer token, int tokenType) {
				
		//System.out.println("tokenizer, token '"+token.toString()+"'");
		
		result.add(token.toString());
		this.tokenCount++;
		this.token = new StringBuffer();
		this.setTokenType = true;
	}
}
