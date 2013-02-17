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

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.log4j.Logger;

public class OmorfiRawHandler {
	private static final Logger log = Logger.getLogger(OmorfiRawHandler.class);
	
	/**
	 * 
	 * @return lemmas where inner list may contain multiple baseforms for each [ <lakata, lakka>, <sataa, satama>]
	 * @throws FileNotFoundException
	 */
	public static  ArrayList<ArrayList<String>> getLemmasArray(ArrayList<String> omorfiOutput) {

		ArrayList <ArrayList<String>> result = new ArrayList<ArrayList<String>>();

		for(String s: omorfiOutput) {
			result.add(parseLemmaFromRaw(s));
		}
		
		return result;
	}
	
	/**
	 * get lemmas as text, only one occurence for each token
	 * @param omorfiOutput omorfi tokens separated in rows
	 * @return lemmas in string format
	 */
	public static String getLemmas(List<String> omorfiOutput) {
		StringBuffer result = new StringBuffer();

		for(String s: omorfiOutput) {
			result.append(parseLemmaFromRawText(s)+" ");
		}
		
		return result.toString().trim();
	}
	
	/**
	 * Retrieve lemmas in textual format, only one occurence from each line, the first one.
	 * @param row
	 * @return
	 */
	private static String parseLemmaFromRawText(String row) {
		log.debug("Processing omorfi output: "+row);
		
		Scanner s = new Scanner(row);
		try {
			String original = s.next(); // original form of the word
	
			StringBuffer lemmatized = new StringBuffer();
	
			String token; // parse lemmas
			while(null != (token = s.findInLine("\\[LEMMA='.*?'\\]"))) { //NONGREEDY!
				log.debug("parsing lemma: "+token);
				lemmatized.append(token.substring(8, token.length()-2));
			} 
			
			if(lemmatized.toString().isEmpty()) { // if lemmas missing, add original
				log.debug("lemmas missing, return original: "+original);
				return original;
			} else {
				return lemmatized.toString();
			}
		} finally {
			s.close();
		}
	}
	

	/**
	 * example input: 'lakkaa [BOUNDARY=ULTIMATE][LEMMA='lakata'][POS=VERB][KTN=73][KAV=A][VOICE=ACT][MOOD=IMPV][PRS=SG2][BOUNDARY=ULTIMATE]	8.964844' 
	 * 
	 * parse lemma from 'raw' token input, each row may contain multiple lemmas
	 * @param row of omorfi output
	 * @return
	 */
	private static ArrayList<String> parseLemmaFromRaw(String row) {
//		log.debug("ROW:"+row);
		
		ArrayList<String> lemmas = new ArrayList<String>();
		Scanner s = new Scanner(row);
		String original = s.next(); // original form of the word

//		log.debug("original is :"+original);

		String token; // parse lemmas
		while(null != (token = s.findInLine("\\[LEMMA='.*'\\]"))) {
//			log.debug("LEMMA: '"+token.substring(8, token.length()-2)+"'");
			lemmas.add(token.substring(8, token.length()-2));
		}
		
		if(lemmas.isEmpty()) { // if lemmas missing, add original
			lemmas.add(original);
		}
		
		s.close();
		return lemmas;
	}

}
