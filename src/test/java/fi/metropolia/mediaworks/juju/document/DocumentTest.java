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

import java.util.Iterator;

import static org.junit.Assert.*;

import org.junit.Test;

public class DocumentTest {
	
	@Test
	public void testTokenIterator() {
		Sentence s;
		
		Document d = new Document();
		s = new Sentence();
		d.add(s);
		Token t11 = new Token("This");
		s.add(t11);
		Token t12 = new Token("is");
		s.add(t12);
		Token t13 = new Token("test");
		s.add(t13);
		Token t14 = new Token(".");
		s.add(t14);
		
		s = new Sentence();
		d.add(s);
		Token t21 = new Token("This");
		s.add(t21);
		Token t22 = new Token("is");
		s.add(t22);
		Token t23 = new Token("a");
		s.add(t23);
		Token t24 = new Token("another");
		s.add(t24);
		Token t25 = new Token("sentence");
		s.add(t25);
		Token t26 = new Token("!");
		s.add(t26);
		
		Iterator<Token> itr = d.tokenIterator();
		assertEquals(t11, itr.next());
		assertTrue(itr.hasNext());
		assertEquals(t12, itr.next());
		assertTrue(itr.hasNext());
		assertEquals(t13, itr.next());
		assertTrue(itr.hasNext());
		assertEquals(t14, itr.next());
		assertTrue(itr.hasNext());
		
		assertEquals(t21, itr.next());
		assertTrue(itr.hasNext());
		assertEquals(t22, itr.next());
		assertTrue(itr.hasNext());
		assertEquals(t23, itr.next());
		assertTrue(itr.hasNext());
		assertEquals(t24, itr.next());
		assertTrue(itr.hasNext());
		assertEquals(t25, itr.next());
		assertTrue(itr.hasNext());
		assertEquals(t26, itr.next());
		assertFalse(itr.hasNext());
	}
}
