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
package fi.metropolia.mediaworks.juju.document;

import java.util.List;

public class PosToken extends Token {
	private static final long serialVersionUID = 1L;
	
	private final String lemma;
	private final PartOfSpeech pos;
	private final GrammaticalCase grammaticalCase;
	private final GrammaticalNumber grammaticalNumber;
	
	public PosToken(String text, String lemma, PartOfSpeech pos) {
		this(text, lemma, pos, null, null, null);
	}

	public PosToken(String text, String lemma, PartOfSpeech pos, GrammaticalCase grammaticalCase, GrammaticalNumber grammaticalNumber, CaseChange caseChange) {
		super(text, caseChange);

		this.lemma = lemma;		
		this.pos = pos;
		this.grammaticalCase = grammaticalCase;
		this.grammaticalNumber = grammaticalNumber;
	}
	
	public final String getLemma() {
		if (lemma != null) {
			return lemma;
		}
		return getText();
	}
	
	public final PartOfSpeech getPartOfSpeech() {
		return pos;
	}
	
	public final GrammaticalCase getGrammaticalCase() {
		return grammaticalCase;
	}

	public final GrammaticalNumber getGrammaticalNumber() {
		return grammaticalNumber;
	}
	
	@Override
	protected List<String> getDebugStringParts() {
		List<String> parts = super.getDebugStringParts();
		
		if (lemma != null) {
			parts.add(lemma);
		}
		
		if (pos != null) {
			parts.add(pos.toString());
		}
		if (grammaticalCase != null) {
			parts.add(grammaticalCase.toString());
		}
		if (grammaticalNumber != null) {
			parts.add(grammaticalNumber.toString());
		}
		return parts;
	}
}
