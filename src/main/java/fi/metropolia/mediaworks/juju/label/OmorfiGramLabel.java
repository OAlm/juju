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

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Lists;

import fi.metropolia.mediaworks.juju.document.OmorfiToken;
import fi.metropolia.mediaworks.juju.document.Token;
import fi.metropolia.mediaworks.juju.extractor.Gram;

public class OmorfiGramLabel extends GramLabel {
	@Override
	public int getPriority() {
		return super.getPriority() + 1;
	}
	
	@Override
	public boolean canHandle(Object object) {
		if (super.canHandle(object)) {
			for (Token t : (Gram)object) {
				if (!(t instanceof OmorfiToken)) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	@Override
	public String createLabel(Gram gram) {
		List<String> words = Lists.newArrayListWithCapacity(gram.size());
		
		for (Token t : gram) {
			if (t instanceof OmorfiToken) {
				OmorfiToken ot = (OmorfiToken)t;
				words.add(ot.getLemma());
			}
		}
		
		return StringUtils.join(words, " ").trim();
	}
}
