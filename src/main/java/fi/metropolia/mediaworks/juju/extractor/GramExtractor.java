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
package fi.metropolia.mediaworks.juju.extractor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;
import com.google.common.collect.Range;
import com.google.common.collect.Ranges;

import fi.metropolia.mediaworks.juju.document.Document;
import fi.metropolia.mediaworks.juju.document.Sentence;
import fi.metropolia.mediaworks.juju.document.Token;
import fi.metropolia.mediaworks.juju.extractor.keyphrase.filter.LengthFilter;
import fi.metropolia.mediaworks.juju.syntax.parser.DocumentBuilder;

public class GramExtractor {
	private Document document;
	private Collection<Gram> grams;

	public GramExtractor(Document document) {
		this.document = document;
	}
	
	public GramExtractor(Document document, int min, int max) {
		this.document = document;
		this.findGrams(min, max);
	}
	
	public void filter(Predicate<Gram> filter) {
		grams = new ArrayList<Gram>(Collections2.filter(grams, filter));
	}
	
	public void filterByFrequency(Range<Integer> freq) {
		ArrayList<Gram> newGrams = new ArrayList<Gram>(grams.size());
		
		Multiset<Gram> freqs = getGramFrequencies();
		for (Gram g : grams) {
			if (freq.contains(freqs.count(g))) {
				newGrams.add(g);
			}
		}
		
		grams = newGrams;
	}
	
	public void findGrams(int min, int max) {
		ArrayList<Gram> result = new ArrayList<Gram>();
		ArrayList<Token> list = new ArrayList<Token>();

		int pointer = 0;
		
		for (Sentence s : document) {
			list.clear();
			pointer = 0;
			for (Token t : s) {
				if (t==null) {
					System.out.println("eek");
				}
				list.add(t);
				pointer++;
				
				
				for (int start = pointer - max; start <= pointer - min; start++) {
					if (start >= 0) {
						result.add(new Gram(ImmutableList.copyOf(list.subList(start, pointer))));
					}
				}
			}
		}
		
		grams = result;
	}

	public Document getDocument() {
		return document;
	}
	
	public Multiset<Gram> getGramFrequencies() {
		Multiset<Gram> t = HashMultiset.create();
		Multiset<String> counter = HashMultiset.create();
		
		for (Gram g : grams) {
			counter.add(g.getMatchString());
		}
		
		for (Gram g : grams) {
			t.add(g, counter.count(g.getMatchString()));
		}
		
		return Multisets.unmodifiableMultiset(t);
	}
	
	public List<Gram> getGrams() {
		return ImmutableList.copyOf(grams);
	}
	
	public Set<Grams> getGroupedGrams() {
		Multimap<String, Gram> m = HashMultimap.create();

		for (Gram g : grams) {
			m.get(g.getMatchString()).add(g);
		}

		SortedSet<Grams> o = new TreeSet<Grams>();
		for (String key : m.keySet()) {
			Grams g = new Grams(m.get(key));
			o.add(g);
		}

		return Collections.unmodifiableSet(o);
	}
	
	public void removeOverlappingGrams() {
		Collections.sort(Lists.newArrayList(grams), new Comparator<Gram>() {
			@Override
			public int compare(Gram o1, Gram o2) {
				return o2.size() - o1.size();
			}
		});
		
		List<Gram> all = ImmutableList.copyOf(grams);
		Collection<Gram> multiGrams = Collections2.filter(all, new LengthFilter(Ranges.atLeast(2)));
		
		for (Gram gram : multiGrams) {
			if (gram.size() > 1) {
				Collection<Gram> subGrams = Collections2.filter(all, new LengthFilter(Ranges.lessThan(gram.size())));
				for (Gram sGram : subGrams) {
					for (Token t : sGram) {
						if (gram.contains(t)) {
							grams.remove(sGram);
						}
					}
				}
			}
		}
	}
	
	@Override
	public String toString() {
		return "[" + StringUtils.join(grams, "], [") + "]";
	}
	
	public static void main(String[] args) {
		Document d = DocumentBuilder.parseDocument("Maailman parhaat pelaajat kokoontuvat kauppaan! Arkkitehtuurin ja yhteiskunnan on selvittävä tästä.", "fi");
		GramExtractor ge = new GramExtractor(d, 1, 3);
		System.out.println(ge.getGroupedGrams());
	}
}
