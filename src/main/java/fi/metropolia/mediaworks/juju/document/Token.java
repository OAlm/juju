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

import java.io.Serializable;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Lists;

public class Token implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private final String text;
	private final CaseChange caseChange;
	private Sentence sentence;

	public Token(String text) {
		this(text, null);
	}

	public Token(String text, CaseChange caseChange) {
		this.text = text;
		
		if (caseChange == null) {
			if (StringUtils.isAllUpperCase(text)) {
				this.caseChange = CaseChange.UP_ALL;
			} else if (text.length() > 0 && Character.isUpperCase(text.charAt(0))) {
				this.caseChange = CaseChange.UP_FIRST;
			} else {
				this.caseChange = CaseChange.NONE;
			}
		} else {
			this.caseChange = caseChange;
		}
	}

	public final String getText() {
		return text;
	}

	public final Sentence getSentence() {
		return sentence;
	}

	final void setSentence(Sentence sentence) {
		this.sentence = sentence;
	}

	@Override
	public String toString() {
		return text;
	}

	public final int getIndex() {
		if (sentence != null) {
			return sentence.indexOf(this);
		}
		return 0;
	}

	public CaseChange getCaseChange() {
		return caseChange;
	}
	
	public final String toDebugString() {
		return String.format("%s (%s)", text, StringUtils.join(getDebugStringParts(), ", "));
	}
	
	protected List<String> getDebugStringParts() {
		return Lists.newArrayList(caseChange.toString());
	}
}
