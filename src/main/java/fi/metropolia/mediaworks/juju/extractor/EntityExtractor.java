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
package fi.metropolia.mediaworks.juju.extractor;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import fi.metropolia.mediaworks.juju.document.Document;
import fi.metropolia.mediaworks.juju.document.Token;
import fi.metropolia.mediaworks.juju.extractor.keyphrase.filter.SmartFilter;

public class EntityExtractor {
	private Document document;

	public EntityExtractor(Document document) {
		this.document = document;
	}

	public List<Gram> getEntities() {
		List<Gram> o = new ArrayList<Gram>();
		
		GramExtractor ge = new GramExtractor(document);
		ge.findGrams(1, 1);

		ge.filter(new SmartFilter());

		Set<Grams> ggs = ge.getGroupedGrams();

		for (Grams gs : ggs) {
			boolean notFirstUpper = false;
			boolean allUpper = true;
				
			for (Gram g : gs) {
				Token t = g.firstToken();
				
				if (t.getIndex() > 0 && t.getCaseChange().upperCase) {
					notFirstUpper = true;
				} else if (!t.getCaseChange().upperCase){
					allUpper = false;
					break;
				}
			}

			if (notFirstUpper && allUpper) {
				o.addAll(gs);
			}
		}

		return o;
	}
}
