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
package fi.metropolia.mediaworks.juju.syntax.en;

import fi.metropolia.mediaworks.juju.document.GrammaticalNumber;
import fi.metropolia.mediaworks.juju.document.PartOfSpeech;

public enum OpenNLPPartOfSpeech {
	/**
	 * Coordinating conjunction
	 */
	CC("CC", PartOfSpeech.CONNECTION),
	/**
	 * Cardinal number
	 */
	CD("CD", PartOfSpeech.NUMERAL),
	/**
	 * Determiner
	 */
	DT("DT", PartOfSpeech.CONNECTION),
	/**
	 * Existential there
	 */
	EX("EX", PartOfSpeech.CONNECTION),
	/**
	 * Foreign word
	 */
	FW("FW", PartOfSpeech.FOREIGN_WORD),
	/**
	 * Preposition or subordinating conjunction
	 */
	IN("IN", PartOfSpeech.CONNECTION),
	/**
	 * Adjective
	 */
	JJ("JJ", PartOfSpeech.ADJECTIVE),
	/**
	 * Adjective, comparative
	 */
	JJR("JJR", PartOfSpeech.ADJECTIVE),
	/**
	 * Adjective, superlative
	 */
	JJS("JJS", PartOfSpeech.ADJECTIVE),
	/**
	 * List item marker
	 */
	LS("LS"),
	/**
	 * Modal
	 */
	MD("MD", PartOfSpeech.CONNECTION),
	/**
	 * Noun, singular or mass
	 */
	NN("NN", PartOfSpeech.BASE, GrammaticalNumber.SINGULAR),
	/**
	 * Noun, plural
	 */
	NNS("NNS", PartOfSpeech.BASE, GrammaticalNumber.PLURAL),
	/**
	 * Proper noun, singular
	 */
	NNP("NNP", PartOfSpeech.BASE, GrammaticalNumber.SINGULAR),
	/**
	 * Proper noun, plural
	 */
	NNPS("NNPS", PartOfSpeech.BASE, GrammaticalNumber.PLURAL),
	/**
	 * Predeterminer
	 */
	PDT("PDT", PartOfSpeech.CONNECTION),
	/**
	 * Possessive ending
	 */
	POS("POS", PartOfSpeech.CONNECTION),
	/**
	 * Personal pronoun
	 */
	PRP("PRP", PartOfSpeech.CONNECTION),
	/**
	 * Possessive pronoun
	 */
	PRPS("PRP$", PartOfSpeech.CONNECTION),
	/**
	 * Adverb
	 */
	RB("RB", PartOfSpeech.CONNECTION),
	/**
	 * Adverb, comparative
	 */
	RBR("RBR", PartOfSpeech.CONNECTION),
	/**
	 * Adverb, superlative
	 */
	RBS("RBS", PartOfSpeech.CONNECTION),
	/**
	 * Particle
	 */
	RP("RP", PartOfSpeech.CONNECTION),
	/**
	 * Symbol
	 */
	SYM("SYM", PartOfSpeech.SYMBOL),
	/**
	 * To
	 */
	TO("TO", PartOfSpeech.CONNECTION),
	/**
	 * Interjection
	 */
	UH("UH", PartOfSpeech.CONNECTION),
	/**
	 * Verb, base form
	 */
	VB("VB", PartOfSpeech.VERB),
	/**
	 * Verb, past tense
	 */
	VBD("VBD", PartOfSpeech.VERB),
	/**
	 * Verb, gerund or present participle
	 */
	VBG("VBG", PartOfSpeech.VERB),
	/**
	 * Verb, past participle
	 */
	VBN("VBN", PartOfSpeech.VERB),
	/**
	 * Verb, non-3rd person singular present
	 */
	VBP("VBP", PartOfSpeech.VERB),
	/**
	 * Verb, 3rd person singular present
	 */
	VBZ("VBZ", PartOfSpeech.VERB),
	/**
	 * Wh-determiner
	 */
	WDT("WDT", PartOfSpeech.CONNECTION),
	/**
	 * Wh-pronoun
	 */
	WP("WP", PartOfSpeech.CONNECTION),
	/**
	 * Possessive wh-pronoun
	 */
	WPS("WP$", PartOfSpeech.CONNECTION),
	/**
	 * Wh-adverb
	 */
	WRB("WRB", PartOfSpeech.CONNECTION),
	/**
	 * Unknown, uncertain, or unbracketable
	 */
	X("X", PartOfSpeech.UNIDENTIFIED),
	;
	
	public static OpenNLPPartOfSpeech getWithTag(String tag) {
		for (OpenNLPPartOfSpeech p : values()) {
			if (p.tag.equals(tag)) {
				return p;
			}
		}
		System.err.println("Missing: " + tag);
		return X;
	}

	public final PartOfSpeech pos;
	public final String tag;
	public final GrammaticalNumber number;
	
	private OpenNLPPartOfSpeech(String tag) {
		this(tag, null);
	}
	
	private OpenNLPPartOfSpeech(String tag, PartOfSpeech pos) {
		this(tag, pos, null);
	}
	
	private OpenNLPPartOfSpeech(String tag, PartOfSpeech pos, GrammaticalNumber number) {
		this.tag = tag;
		this.pos = pos;
		this.number = number; 
	}
}
