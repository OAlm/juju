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
package fi.metropolia.mediaworks.juju.extractor.keyphrase.filter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.junit.Test;

import fi.metropolia.mediaworks.juju.document.Document;
import fi.metropolia.mediaworks.juju.document.OmorfiToken;
import fi.metropolia.mediaworks.juju.document.Token;
import fi.metropolia.mediaworks.juju.extractor.Gram;
import fi.metropolia.mediaworks.juju.extractor.GramExtractor;
import fi.metropolia.mediaworks.juju.extractor.Grams;
import fi.metropolia.mediaworks.juju.extractor.keyphrase.KeyphraseExtractor;
import fi.metropolia.mediaworks.juju.syntax.parser.DocumentBuilder;
//@author alm
public class EntityFilterTest {

//	@Test
//	public void parserTest() {
	@Test
	public static void main(String [] args)  {
//		Tika tika = new Tika();
//		Document doc = DocumentBuilder.parseDocument("Koira Kissa on Koira entiteetti Koira", "fi");
//		GramExtractor g = new GramExtractor(doc, 1,3);
		
		//FIXME: now with Tokens, not even PosToken or OmorfiToken
		
		Token t1 = new Token("Koira");
		Token t2 = new Token("Alfred");
		Token t3 = new Token("J.");
		Token t4 = new Token("Kvack");
		Token t5 = new Token("ei");
		
		List <Token> l1 = new ArrayList<Token>();
		l1.add(t1);
		
		Gram g1 = new Gram(l1);
		
		l1 = new ArrayList<Token>();
		l1.add(t1);
		l1.add(t2);
		
		Gram g2 = new Gram(l1);
		l1 = new ArrayList<Token>();
		l1.add(t1);
		l1.add(t2);
		l1.add(t3);
		
		Gram g3 = new Gram(l1);

		l1 = new ArrayList<Token>();
		l1.add(t1);
		l1.add(t5);
		l1.add(t3);
		Gram g4 = new Gram(l1);
		
		l1 = new ArrayList<Token>();
		l1.add(t5);
		l1.add(t1);
		Gram g5 = new Gram(l1);
		
		
		EntityFilter e = new EntityFilter();
		assertEquals(e.apply(g1), true);
		assertEquals(e.apply(g2), true);
		assertEquals(e.apply(g3), false);
		assertEquals(e.apply(g4), false);
		assertEquals(e.apply(g5), false);
		

	}
	
}
