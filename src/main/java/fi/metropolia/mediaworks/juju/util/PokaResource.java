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
/**
 * Yksittäisen löydetyn resurssin tiedon tallettava container
 */

package fi.metropolia.mediaworks.juju.util;

import java.util.ArrayList;

import com.google.common.collect.Multiset;
import com.google.common.collect.TreeMultiset;

public class PokaResource {

	private String uri; // toisteista tietoa, eikai haitanne?
	private int count; // frekvenssilaskuri

	private ArrayList<WordLocationContainer> location;

	public PokaResource(String uri) {
		this.uri = uri;
		this.count = 1;
		this.location = new ArrayList<WordLocationContainer>();
	}

	public String getUri() {
		return this.uri;
	}

	public void add() {
		this.count += 1;
	}

	public int getCount() {
		return this.count;
	}

	public void addLocation(int sentence, int token) {
		location.add(new WordLocationContainer(sentence, token));
	}

	public void addLocation(int sentence, int startToken, int endToken) {
		location.add(new WordLocationContainer(sentence, startToken, endToken));
	}
	public ArrayList<WordLocationContainer> getLocations() {
		return this.location;
	}

	public Multiset<Integer> getDistribution() {
		Multiset<Integer> out = TreeMultiset.create();
		for (WordLocationContainer wl : location) {
			out.add(wl.getSentence());			
		}
		return out;
	}
}
