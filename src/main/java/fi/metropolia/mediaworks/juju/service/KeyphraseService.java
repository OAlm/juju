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
package fi.metropolia.mediaworks.juju.service;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;

import fi.metropolia.mediaworks.juju.corpus.Corpus;
import fi.metropolia.mediaworks.juju.corpus.CorpusGenerator;
import fi.metropolia.mediaworks.juju.data.DataItem;
import fi.metropolia.mediaworks.juju.document.Document;
import fi.metropolia.mediaworks.juju.extractor.Grams;
import fi.metropolia.mediaworks.juju.extractor.keyphrase.KeyphraseExtractor;
import fi.metropolia.mediaworks.juju.syntax.parser.DocumentBuilder;

public class KeyphraseService {
	public static Map<DataItem, Map<Grams, Double>> getDocumentGroupKeyphrases(List<DataItem> documents) {
		Corpus c = CorpusGenerator.generateCorpus(documents);
		KeyphraseExtractor ke = new KeyphraseExtractor(c);
		
		Map<DataItem, Map<Grams, Double>> result = Maps.newLinkedHashMap();
		
		for (DataItem d : documents) {
			Document doc = DocumentBuilder.parseDocument(d.getText(), "fi");
			result.put(d, ke.process(doc));
		}
		
		return result;
	}
	
	public static Map<Grams, Double> getKeyphrases(String document) {
		Document d = DocumentBuilder.parseDocument(document, "fi");
		KeyphraseExtractor ke = new KeyphraseExtractor();
		return ke.process(d);
	}
}
