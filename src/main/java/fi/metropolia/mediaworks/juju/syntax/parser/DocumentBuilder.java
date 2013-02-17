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
package fi.metropolia.mediaworks.juju.syntax.parser;

import java.net.URL;
import java.util.List;
import java.util.ServiceLoader;

import org.apache.tika.Tika;

import fi.metropolia.mediaworks.juju.document.Document;

public class DocumentBuilder {	
	public static final IDocumentParser EMPTY_PARSER = new IDocumentParser() {
		@Override
		public Document parseDocument(String content) {
			return new Document();
		}
		
		@Override
		public List<String> getSupportedLanguages() {
			return null;
		}
		
		@Override
		public int getPriority() {
			return -1;
		}
	};
	
	private static ServiceLoader<IDocumentParser> documentParserLoader = ServiceLoader.load(IDocumentParser.class);
	
	public static Document parseDocument(String content, String language) {
		return getBuilder(language).parseDocument(content);
	}
	
	public static IDocumentParser getBuilder(String language) {
		IDocumentParser selected = EMPTY_PARSER;
		
		for (IDocumentParser dp : documentParserLoader) {
			if ((dp.getSupportedLanguages() == null || dp.getSupportedLanguages().contains(language)) && dp.getPriority() > selected.getPriority()) {
				selected = dp;
			}
		}
		
		return selected;
	}
	
	public static void main(String[] args) throws Exception {
		Tika t = new Tika();
		String text = t.parseToString(new URL("http://www.kansallisbiografia.fi/kb/artikkeli/1408/"));
//		String text = "Koirissa on toivoa";
		Document d = parseDocument(text, "fi");
		d.print();
	}
}
