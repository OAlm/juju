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
package fi.metropolia.mediaworks.juju.extractor.keyphrase.filter;

import com.google.common.base.Predicate;

import fi.metropolia.mediaworks.juju.document.PosToken;
import fi.metropolia.mediaworks.juju.document.PartOfSpeech;
import fi.metropolia.mediaworks.juju.document.Token;
import fi.metropolia.mediaworks.juju.extractor.Gram;

public class SmartFilter implements Predicate<Gram> {
	private static final String CHECK = "\\p{L}{1,}(-\\p{L}{1,})*";

	private boolean testToken(PosToken token) {
		return token.getPartOfSpeech() == PartOfSpeech.BASE || (token.getPartOfSpeech() == PartOfSpeech.UNIDENTIFIED && token.getLemma().matches(CHECK));
	}

	@Override
	public boolean apply(Gram gram) {
		Token first = gram.firstToken();
		Token last = gram.lastToken();

		if (first instanceof PosToken && last instanceof PosToken) {
			PosToken cFirst = (PosToken) first;
			PosToken cLast = (PosToken) last;

			if (testToken(cFirst) && testToken(cLast)) {
				int n = gram.size();
				if (n > 2) {
					for (int i = 1; i < n - 1; i++) {
						Token t = gram.get(i);
						
						if (t instanceof PosToken) {
							PosToken ct = (PosToken)t;
							
							if (ct.getPartOfSpeech() == PartOfSpeech.NUMERAL || ct.getPartOfSpeech() == PartOfSpeech.VERB) {
								return false;
							}

							if (!t.getText().matches(CHECK)) {
								return false;
							}	
						}
					}
				}
				return true;
			}
		}
		return false;
	}

}
