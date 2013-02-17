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
package fi.metropolia.mediaworks.juju.corpus;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;

import fi.metropolia.mediaworks.juju.Juju;

public class Corpus {
	private HashMultiset<String> phraseCounts;
	private HashMultiset<String> phraseDocCounts;
	private int documentCount;

	public Corpus() {
		phraseCounts = HashMultiset.create();
		phraseDocCounts = HashMultiset.create();
		documentCount = 0;
	}

	public static Corpus load(URL url) throws IOException {
		return load(url.openStream());
	}
	
	public static Corpus load(File file) throws FileNotFoundException, IOException {
		return load(new FileInputStream(file));
	}
	
	public static Corpus load(InputStream input) throws IOException {
		Corpus corpus = new Corpus();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(input));
			String line = null;
			
			corpus.documentCount = Integer.parseInt(reader.readLine().trim());
			
			while ((line = reader.readLine()) != null) {
				String[] parts = line.trim().split("\t");
				corpus.phraseCounts.add(parts[0], Integer.parseInt(parts[1]));
				corpus.phraseDocCounts.add(parts[0], Integer.parseInt(parts[2]));
			}
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
		return corpus;
	}

	public List<String> getMostCommonTerms(int count) {
		ArrayList<String> o = new ArrayList<String>();
		Iterator<String> i = Multisets.copyHighestCountFirst(phraseCounts).elementSet().iterator();
		
		while (o.size() < count && i.hasNext()) {
			o.add(i.next());
		}
		
		return o;
	}
	
	public void printCommonTerms() {
		Multiset<String> d = Multisets.copyHighestCountFirst(phraseCounts);
		int i = 0;
		for (Multiset.Entry<String> e: d.entrySet()) {
			i++;
			if (i > 20) {
				break;
			}
			System.out.println(e.getElement() + ": " + e.getCount() + " (" + phraseDocCounts.count(e.getElement()) + ")");
		}
	}
	
	public int getWordCount() {
		return phraseCounts.entrySet().size();
	}
	
	@Override
	public String toString() {
		return String.format("Corpus with %d documents: %d/%d", documentCount, phraseCounts.size(), phraseDocCounts.size());
	}

	public void addDocument(Multiset<String> document) {
		documentCount++;
		for (Multiset.Entry<String> e : document.entrySet()) {
			phraseCounts.add(e.getElement(), e.getCount());
			phraseDocCounts.add(e.getElement());
		}
	}

	public void save(File file) throws FileNotFoundException, IOException {
		Writer writer = null;
		
		try {
			writer = new BufferedWriter(new FileWriter(file));
			
			writer.write(Integer.toString(documentCount));
			writer.write('\n');
			
			for (String word : phraseCounts.elementSet()) {
				writer.write(word);
				writer.write('\t');
				writer.write(Integer.toString(phraseCounts.count(word)));
				writer.write('\t');
				writer.write(Integer.toString(phraseDocCounts.count(word)));
				writer.write('\n');
			}
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
	}
	
	public int getTermFrequency(String term) {
		return phraseCounts.count(term);
	}
	
	public int getTermDocFrequency(String term) {
		return phraseDocCounts.count(term);
	}
	
	public double getInverseTermDocFrequency(String term) {
		double tf = getTermDocFrequency(term);
		double idf = Math.log(documentCount / tf);
		return idf;
	}
	
	public double getTermDocPercentage(String term) {
		 return (double)getTermDocFrequency(term) / (double)documentCount;
	}
	
	public double getAverageTermCount(String term) {
		int tdf = getTermDocFrequency(term);
		if (tdf > 0) {
			return (double)getTermFrequency(term) / (double)tdf;
		} else {
			return 0;
		}
	}
	
	public int getDocumentCount() {
		return documentCount;
	}
	
	public void print(String term) {
		System.out.printf("Term \"%s\" facts:\n", term);
		System.out.printf("\tTerm frequency: %d\n", getTermFrequency(term));
		System.out.printf("\tTerm document frequency: %d\n", getTermDocFrequency(term));
		System.out.printf("\tIDF: %f\n", getInverseTermDocFrequency(term));
		System.out.printf("\tTerm document percentage: %f\n", getTermDocPercentage(term));
		System.out.printf("\tAverage term count: %f\n", getAverageTermCount(term));
	}
	
	public static void main(String[] args) throws Exception {
		Corpus c = Juju.accessCorpus("hs");
		System.out.println(c.getInverseTermDocFrequency("koira"));
	}
}
