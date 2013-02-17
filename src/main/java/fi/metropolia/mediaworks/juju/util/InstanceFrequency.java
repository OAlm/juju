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
package fi.metropolia.mediaworks.juju.util;
/**
 * Tallentaa urin nimen ja kyseisen luokan esiintymien määrän dokumentissa
 * @author alm
 *
 */

public class InstanceFrequency implements Comparable<InstanceFrequency> {

	String uri;
	int freq;
	
	public InstanceFrequency(String uri, int freq) {
		this.uri = uri;
		this.freq = freq;
	}

	public int getFreq() {
		return this.freq;
	}
	
	@Override
	public int compareTo(InstanceFrequency another) {
		if (another != null) {
			int anotherFreq = another.getFreq();  
		    return this.freq - anotherFreq;
		} else {
			return 0;
		}
	}
	
	public String getUri() {
		return this.uri; 
	}
	
//	public String toString() {
//		return this.uri+": "+this.freq;
//	}
	//modified version for web api
	@Override
	public String toString() {
		return this.uri;
	}
}
