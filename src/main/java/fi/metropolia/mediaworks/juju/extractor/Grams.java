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

import java.util.Collection;
import java.util.HashSet;

import com.google.common.collect.ImmutableSet;

import fi.metropolia.mediaworks.juju.label.LabelMaker;

public class Grams extends HashSet<Gram> implements Comparable<Grams>  {
	private static final long serialVersionUID = -1674999660582160406L;
	private String matchString;
	private String displayString;
	
	public Grams(Collection<Gram> grams) {
		super(ImmutableSet.copyOf(grams));
		
		Gram firstGram = iterator().next();
		
		matchString = firstGram.getMatchString();
		displayString = LabelMaker.getLabel(firstGram);
	}
	
	@Override
	public int compareTo(Grams o) {
		return matchString.compareTo(o.matchString);
	}
	
	public String getMatchString() {
		return matchString;
	}
	
	@Override
	public String toString() {
		return displayString;
	}
}
