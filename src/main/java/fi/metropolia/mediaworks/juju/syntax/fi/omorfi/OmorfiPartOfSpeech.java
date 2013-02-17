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

import java.util.List;

import com.google.common.collect.Lists;

import fi.metropolia.mediaworks.juju.document.PartOfSpeech;

public enum OmorfiPartOfSpeech {	
	/**
	 * Example: esim.
	 */
	ABBREVIATION("ABBREVIATION", PartOfSpeech.CONNECTION),
	/**
	 * Example: STT
	 */
	ACRONYM("ACRONYM", PartOfSpeech.ACRONYM),
	/**
	 * Example: kaunis
	 */
	ADJECTIVE("ADJECTIVE",PartOfSpeech.ADJECTIVE),
	/**
	 * Example: mukaan
	 */
	ADPOSITION("ADPOSITION", PartOfSpeech.CONNECTION),
	/**
	 * Example: nopeasti
	 */
	ADVERB("ADVERB", PartOfSpeech.CONNECTION),
	/**
	 * Example: että
	 */
	CONJUNCTION("CONJUNCTION", PartOfSpeech.CONNECTION),
	/**
	 * Example: hei
	 */
	INTERJECTION("INTERJECTION", PartOfSpeech.CONNECTION),
	/**
	 * Example: talo
	 */
	NOUN("NOUN", null, PartOfSpeech.BASE),
	/**
	 * Helsinki
	 */
	PROPER_NOUN("NOUN", "PROPER", PartOfSpeech.BASE),
	/**
	 * Example: neljä
	 */
	NUMERAL("NUMERAL", PartOfSpeech.NUMERAL),
	/**
	 * Example: no
	 */
	PARTICLE("PARTICLE", PartOfSpeech.CONNECTION),
	/**
	 * Example: epä-
	 */
	PREFIX("PREFIX", PartOfSpeech.CONNECTION),
	/**
	 * Example: sellaisissa
	 */
	PROADJECTIVE("PROADJECTIVE", PartOfSpeech.CONNECTION),
	/**
	 * Example: hän
	 */
	PRONOUN("PRONOUN", PartOfSpeech.PRONOUN),
	/**
	 * Example: tuolloin, kuinka, jonne
	 */
	PROADVERB("PROADVERB", PartOfSpeech.CONNECTION),
	/**
	 * Example: , .
	 */
	PUNCTUATION("PUNCTUATION", PartOfSpeech.SYMBOL),
	/**
	 * Example: @
	 */
	SYMBOL("SYMBOL", PartOfSpeech.SYMBOL),
	/**
	 * Example: -vaiheinen
	 */
	SUFFIX("SUFFIX", PartOfSpeech.CONNECTION),
	UNIDENTIFIED("UNIDENTIFIED", PartOfSpeech.UNIDENTIFIED),
	
	
	/**
	 * Example: kutoa
	 */
	VERB("VERB", PartOfSpeech.VERB);
	
	public static OmorfiPartOfSpeech getWithPosAndSubcat(String pos, String subcat) {
		for (OmorfiPartOfSpeech p : values()) {
			if (p.posText.equals(pos)) {
				if (p.subcatText != null && p.subcatText.equals(subcat) || p.subcatText == ALL || p.subcatText == subcat) {
					return p;
				}
			} else if (p.posText.equals(subcat)) {
				return p;
			}
		}
		System.err.println("Missing pos: " + pos + ", " + subcat);
		return UNIDENTIFIED;
	}
	public static List<OmorfiPartOfSpeech> getWithPos(String pos) {
		List<OmorfiPartOfSpeech> posses = Lists.newArrayList();
		for (OmorfiPartOfSpeech p : values()) {
			if (p.pos.equals(pos)) {
				posses.add(p);
			}
		}
		return posses;
	}

	private static final String ALL = "*";
	public final String posText;
	public final String subcatText;
	public final PartOfSpeech pos;
	
	private OmorfiPartOfSpeech(String posText, PartOfSpeech pos) {
		this(posText, ALL, pos);
	}
	
	private OmorfiPartOfSpeech(String posText, String subcatText, PartOfSpeech pos) {
		this.posText = posText;
		this.subcatText = subcatText;
		this.pos = pos;
	}
}
