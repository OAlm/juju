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

import java.util.ArrayList;

/**
 * Word location container, index based on the
 * sentence-token -pairs
 * @author alm
 *
 */

public class WordLocationContainer {
	private int sentence; //1-N
	private int firstToken; //0-N
	private int lastToken; // for multiword concepts. for one-word concepts, only firsttoken is used
	public WordLocationContainer(int sentence, int token) {
		this.sentence = sentence;
		this.firstToken = token;
		this.lastToken = -1;
	}
	
	public WordLocationContainer(int sentence, int first, int last) {
		this.sentence = sentence;
		this.firstToken = first;
		this.lastToken = last;
	}

	public int getSentence() {
		return this.sentence;				
	}
	
	public int getToken() {
		return this.firstToken;
	}
	
	/**
	 * use this if you want to check multiword-units
	 * @return
	 */
	public ArrayList<Integer> getTokens() {
		ArrayList<Integer> result = new ArrayList<Integer>();
		result.add(firstToken);
		if(lastToken != -1) {
			result.add(lastToken);
		}
		return result;
	}
	
	@Override
	public String toString() {
		if(lastToken == -1) {
			return "["+sentence+", "+firstToken+"]";
		} else {
			return "["+sentence+", "+firstToken+"-"+lastToken+"]";
		}
	}
}

