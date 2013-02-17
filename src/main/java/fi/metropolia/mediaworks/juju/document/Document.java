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
import java.util.AbstractList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.PeekingIterator;

public class Document extends AbstractList<Sentence> implements Serializable {
	private static final long serialVersionUID = 1L;
	private static int documentCounter = 0;
	
	private final List<Sentence> sentences = Lists.newArrayList();
	private final String id;
	
	public Document() {
		id = Integer.toString(++documentCounter);
	}

	@Override
	public void add(int index, Sentence element) {
		if (element.getDocument() == null) {
			element.setDocument(this);
			sentences.add(index, element);
		} else {
			throw new IllegalArgumentException("Sentence is already in document!");
		}
	}
	
	public String getId() {
		return id;
	}

	@Override
	public Sentence get(int index) {
		return sentences.get(index);
	}

	@Override
	public Sentence remove(int index) {
		Sentence s = sentences.remove(index);
		s.setDocument(null);
		return s;
	}
	
	@Override
	public Sentence set(int index, Sentence element) {
		if (element.getDocument() == null) {
			element.setDocument(this);
			Sentence s = sentences.set(index, element);
			s.setDocument(null);
			return s;
		}
		throw new IllegalArgumentException("Sentence is already in document!");
	}
	
	@Override
	public int size() {
		return sentences.size();
	}
	
	@Override
	public String toString() {
		return StringUtils.join(sentences, " ");
	}
	
	public void print() {
		System.out.println("Document:");
		for (Sentence s : this) {
			s.print();
		}
	}
	
	public Iterator<Token> tokenIterator() {
		return new TokenIterator(iterator());
	}
	
	private static class TokenIterator implements Iterator<Token> {
		private final PeekingIterator<Sentence> sentenceIterator;
		private Iterator<Token> tokenIterator = null;
		
		private TokenIterator(Iterator<Sentence> sentenceIterator) {
			this.sentenceIterator = Iterators.peekingIterator(sentenceIterator);
		}
		
		@Override
		public boolean hasNext() {
			if (tokenIterator != null && tokenIterator.hasNext()) {
				return true; 
			} else if (sentenceIterator.hasNext() && sentenceIterator.peek().size() > 0) {
				return true;
			}
			return false;
		}

		@Override
		public Token next() {
			if (tokenIterator != null && tokenIterator.hasNext()) {
				return tokenIterator.next();
			} else if (sentenceIterator.hasNext()) {
				tokenIterator = sentenceIterator.next().iterator();
				return next();
			}
			return null;
		}

		@Override
		public void remove() {
			if (tokenIterator != null) {
				tokenIterator.remove();
			}
		}
	}
}
