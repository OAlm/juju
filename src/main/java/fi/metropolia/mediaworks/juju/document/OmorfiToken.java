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

import fi.metropolia.mediaworks.juju.syntax.fi.omorfi.OmorfiPartOfSpeech;


public class OmorfiToken extends PosToken {	
	private static final long serialVersionUID = 1L;
	
	private final OmorfiPartOfSpeech partOfSpeech;
	
	public OmorfiToken(String text, String lemma, OmorfiPartOfSpeech partOfSpeech, GrammaticalCase grammaticalCase, GrammaticalNumber grammaticalNumber, CaseChange caseChange) {
		super(text, lemma, partOfSpeech.pos, grammaticalCase, grammaticalNumber, caseChange);
		this.partOfSpeech = partOfSpeech;
	}
		
	public OmorfiPartOfSpeech getOmorfiPartOfSpeech() {
		return partOfSpeech;
	}
	
	@Override
	protected List<String> getDebugStringParts() {
		List<String> parts = super.getDebugStringParts();
		parts.add(partOfSpeech.toString());
		return parts;
	}
}
