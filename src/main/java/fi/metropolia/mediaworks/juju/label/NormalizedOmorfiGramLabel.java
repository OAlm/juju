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
package fi.metropolia.mediaworks.juju.label;

import java.util.ArrayList;
import java.util.Iterator;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Lists;

import fi.metropolia.mediaworks.juju.document.GrammaticalCase;
import fi.metropolia.mediaworks.juju.document.OmorfiToken;
import fi.metropolia.mediaworks.juju.document.Token;
import fi.metropolia.mediaworks.juju.extractor.Gram;

public class NormalizedOmorfiGramLabel extends OmorfiGramLabel {
	@Override
	public boolean canHandle(Object object) {
		if (super.canHandle(object)) {
			Gram gram = (Gram)object;
			if (gram.size() > 1) {
				if (gram.size() == 3 && gram.get(1).getText().equalsIgnoreCase("ja")) {
					return false;
				}
				for (Token t : gram) {
					OmorfiToken ot = (OmorfiToken)t;
					if (isGenitive(ot)) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	@Override
	public int getPriority() {
		return super.getPriority() + 1;
	}
	
	private boolean isGenitive(OmorfiToken t) {
		if (t.getGrammaticalCase() == GrammaticalCase.GENITIVE || t.getText().endsWith(":n")) {
			return true;
		}
		return false;
	}

	@Override
	public String createLabel(Gram gram) {
		ArrayList<String> words = Lists.newArrayListWithCapacity(gram.size());

		Iterator<Token> itr = gram.iterator();
		
		while(itr.hasNext()) {
			OmorfiToken ot = (OmorfiToken)itr.next();
			if (itr.hasNext() && isGenitive(ot)) {
				words.add(ot.getText());
			} else {
				words.add(ot.getLemma());
			}
		}
		
		return StringUtils.join(words, " ").trim();
	}
}
