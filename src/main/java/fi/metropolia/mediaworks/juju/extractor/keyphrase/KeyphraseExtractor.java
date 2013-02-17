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
package fi.metropolia.mediaworks.juju.extractor.keyphrase;

import java.util.List;
import java.util.Map;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Ranges;

import fi.metropolia.mediaworks.juju.Juju;
import fi.metropolia.mediaworks.juju.corpus.Corpus;
import fi.metropolia.mediaworks.juju.document.Document;
import fi.metropolia.mediaworks.juju.extractor.Gram;
import fi.metropolia.mediaworks.juju.extractor.GramExtractor;
import fi.metropolia.mediaworks.juju.extractor.Grams;
import fi.metropolia.mediaworks.juju.extractor.keyphrase.filter.EntityFilter;
import fi.metropolia.mediaworks.juju.extractor.keyphrase.filter.SmartFilter;
import fi.metropolia.mediaworks.juju.extractor.keyphrase.filter.StopwordFilter;
import fi.metropolia.mediaworks.juju.extractor.keyphrase.filter.TextLengthFilter;
import fi.metropolia.mediaworks.juju.extractor.keyphrase.outputfilter.IOutputFilter;
import fi.metropolia.mediaworks.juju.extractor.keyphrase.outputfilter.ThresholdOutputFilter;
import fi.metropolia.mediaworks.juju.util.Tools;
import fi.metropolia.mediaworks.juju.weighting.CorpusWeighter;
import fi.metropolia.mediaworks.juju.weighting.ItemWeighter;
import fi.metropolia.mediaworks.juju.weighting.Weighter;

public class KeyphraseExtractor {	
	public static List<Predicate<Gram>> DEFAULT_FILTERS;
	public static List<IOutputFilter> DEFAULT_OUTPUT_FILTERS;
	public static List<ItemWeighter<Grams>> DEFAULT_WEIGHTERS;
	
	static {
		DEFAULT_FILTERS = ImmutableList.of(
			new TextLengthFilter(Ranges.atLeast(3)),
			Predicates.not(new EntityFilter()), // remove words that look like names
			new StopwordFilter(), // remove grams that contain stop words TODO: now language-specific, generalize
			new SmartFilter() // remove unword tokens, verbs, numerals etc.
		);
		
		List<IOutputFilter> listOf = Lists.newArrayList();
		listOf.add(new ThresholdOutputFilter(Ranges.atLeast(0.52)));
		DEFAULT_OUTPUT_FILTERS = ImmutableList.copyOf(listOf);
		
		List<ItemWeighter<Grams>> listIw = Lists.newArrayList();
		listIw.add(new CorpusWeighter(Juju.accessCorpus()));
		DEFAULT_WEIGHTERS = ImmutableList.copyOf(listIw);
	}
	
	private List<Predicate<Gram>> filters = Lists.newArrayList();
	private List<ItemWeighter<Grams>> weighters = Lists.newArrayList();
	private List<IOutputFilter> outputFilters = Lists.newArrayList();
	
	public KeyphraseExtractor() {
		filters.addAll(DEFAULT_FILTERS);
		outputFilters.addAll(DEFAULT_OUTPUT_FILTERS);
		weighters.addAll(DEFAULT_WEIGHTERS);
	}
	
	public KeyphraseExtractor(Corpus corpus) {
		this();
		
		Tools.removeElementsByClass(weighters, CorpusWeighter.class);
		weighters.add(new CorpusWeighter(corpus));
	}
	
	public List<Predicate<Gram>> getFilters() {
		return filters;
	}
	
	public void clearFilters() {
		filters.clear();
	}
	
	public void addFilter(Predicate<Gram> filter) {
		filters.add(filter);
	}
	
	public List<ItemWeighter<Grams>> getWeighters() {
		return weighters;
	}
	
	public void addWeighter(ItemWeighter<Grams> weighter) {
		weighters.add(weighter);
	}
	
	public void clearWeighters() {
		weighters.clear();
	}
	
	public Map<Grams, Double> process(Document document) {
		return process(document, 1, 3);
	}
	
	public Map<Grams, Double> process(Document document, int minGramLength, int maxGramLength) {
		GramExtractor ge = new GramExtractor(document);
		ge.findGrams(minGramLength, maxGramLength);	
		
		for (Predicate<Gram> f : filters) {
			ge.filter(f);
		}
		
		//ge.removeOverlappingGrams();
		
		Weighter<Grams> weighter = new Weighter<Grams>();
		for (Grams g : ge.getGroupedGrams()) {
			weighter.addItem(g, g.size());
		}
		
		for (ItemWeighter<Grams> w : weighters) {
			weighter.weight(w);
		}
		
		Map<Grams, Double> result = weighter.getResult(true);
		
		for (IOutputFilter of : outputFilters) {
			result = of.filter(result);
		}
		
		return result;
	}
}
