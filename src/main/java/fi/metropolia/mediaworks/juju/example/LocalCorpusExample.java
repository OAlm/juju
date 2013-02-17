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
package fi.metropolia.mediaworks.juju.example;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.tika.Tika;

import com.google.common.collect.Lists;

import fi.metropolia.mediaworks.juju.data.DataItem;
import fi.metropolia.mediaworks.juju.extractor.Grams;
import fi.metropolia.mediaworks.juju.service.KeyphraseService;

public class LocalCorpusExample {
	public static void main(String[] args) {
		String[] ids = {
			"1408",
			"4222",
			"3108",
			"3101",
			"1888",
			"1532",
			"4497",
			"4828",
			"7599"
		};
		
		Tika t = new Tika();
		
		List<DataItem> documents = Lists.newArrayList();
		
		for (String id : ids) {
			try {
				String text = t.parseToString(new URL(String.format("http://www.kansallisbiografia.fi/kb/artikkeli/%s/", id)));
				documents.add(new DataItem(id, "", text));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		Map<DataItem, Map<Grams, Double>> r = KeyphraseService.getDocumentGroupKeyphrases(documents);
		
		for (Entry<DataItem, Map<Grams, Double>> e : r.entrySet()) {
			System.out.println(e.getKey());
			System.out.println("Local corpus: " + KeyphraseService.getKeyphrases(e.getKey().getText()));
			System.out.println("Normal: " + e.getValue());
			System.out.println();
		}
	}
}
