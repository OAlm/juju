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

import java.util.HashSet;
import java.util.Scanner;

import com.google.common.base.Predicate;

import fi.metropolia.mediaworks.juju.document.PosToken;
import fi.metropolia.mediaworks.juju.document.Token;
import fi.metropolia.mediaworks.juju.extractor.Gram;

public class StopwordFilter implements Predicate<Gram> {
	HashSet<String> stopwords;

	public StopwordFilter() {
		init();
	}

	private void init() {
		stopwords = new HashSet<String>();

		Scanner s;

		s = new Scanner(getClass().getResourceAsStream("/omorfi/stopwords_fi.txt"));
		while (s.hasNextLine()) {
			stopwords.add(s.nextLine());
		}
		s.close();
	}

	@Override
	public boolean apply(Gram gram) {
		for (Token t : gram) {
			if (isStopword(t)) {
				return false;
			}
		}
		return true;
	}

	public boolean isStopword(Token token) {
		String word;
		if (token instanceof PosToken) {
			word = ((PosToken) token).getLemma();
		} else {
			word = token.getText();
		}
		return stopwords.contains(word);
	}
}
