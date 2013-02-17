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
package fi.metropolia.mediaworks.juju.syntax.fi.omorfi;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import net.sf.hfst.NoTokenizationException;
import net.sf.hfst.TransducerAlphabet;
import net.sf.hfst.TransducerHeader;
import net.sf.hfst.WeightedTransducer;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import fi.metropolia.mediaworks.juju.util.LRUCache;
import fi.metropolia.mediaworks.juju.util.PropertyUtil;
import fi.metropolia.mediaworks.juju.util.Tools;

/**
 * Lemmatizer with LRU cache
 * 
 * @author ollial
 * 
 */
public class Lemmatizer {
	private static LRUCache<String, String> cache = new LRUCache<String, String>(10000);

	private static final Logger log = Logger.getLogger(Lemmatizer.class);

	final static public String NORESULT = "___NORESULT___";

	private static HashMap<String, Integer> preferredLemmas;

	private static WeightedTransducer transducer = null;

	static {
		preferredLemmas = new HashMap<String, Integer>();

		URL mappingFile = Lemmatizer.class.getResource("/omorfi/preferredTerms.txt");

		if (mappingFile == null) {
			log.error("List of preferred lemmatizer terms does not exist!");
		} else {
			Properties p;
			p = PropertyUtil.loadProperties(mappingFile);
			if (p == null) {
				log.error("loading properties failed for " + mappingFile);
			} else {
				for (Map.Entry<Object, Object> e : p.entrySet()) {

					String k = (String) e.getKey();
					Integer v = Integer.parseInt((String) e.getValue());

					if (k != null) {
						preferredLemmas.put(k, v);
					}
				}
			}
		}

		URL filePath = Lemmatizer.class.getResource("/omorfi/morphology.omor-2011.hfstol");
		InputStream stream = null;
		
		try {
			stream = filePath.openStream();
			
			log.debug("Reading header...");
			TransducerHeader h = new TransducerHeader(stream);

			DataInputStream charstream = new DataInputStream(stream);

			log.debug("Reading alphabet...");
			TransducerAlphabet a = new TransducerAlphabet(charstream, h.getSymbolCount());

			log.debug("Reading transition and index tables...");
			if (h.isWeighted()) {
				log.debug("IS WEIGHTED");
				transducer = new WeightedTransducer(stream, h, a);
				log.debug("...ok!");
			} else {
				log.error("Not weighted, cancelling...");
			}
		} catch (IOException e) {
			log.error("Error reading file: " + e);
		} finally {
			IOUtils.closeQuietly(stream);
		}
	}

	private static synchronized Collection<String> analyze(String token) throws NoTokenizationException {
		return transducer.analyze(token);
	}
	public static void main(String[] args) {
		Lemmatizer l = new Lemmatizer();
		System.out.println(l.lemmatize("kuvan kuvia"));

	}

	private static int cacheCount = 0;

	private Tokenizer tokenizer;

	private static int totalCount = 0;

	public Lemmatizer() {
		tokenizer = new Tokenizer();
	}

	public static void printStats() {
		System.out.println("Total tokens: " + totalCount + "\nTotal cached: " + cacheCount + "\nRatio: " + (((double) cacheCount) / ((double) totalCount)));
	}

	public String lemmatize(String text) {
		List<String> rawtokens;
		try {
			rawtokens = this.lemmaWithOriginal(text);
		} catch (NoTokenizationException e) {
			// TODO Auto-generated catch block
			System.out.println("NOTOKENIZATIONEXCEPTION");
			// e.printStackTrace();
			return null;
		}
		return OmorfiRawHandler.getLemmas(rawtokens);
	}

	/**
	 * NOTE: no more compound markup for lemmas: instead having jalka'|'pallo,
	 * we have jalkapallo. This will affect on some cases when matching strings,
	 * recall will fall. (7.12.2011)
	 * 
	 * @param content
	 * @return
	 * @throws NoTokenizationException
	 */
	public List<String> lemmaWithOriginal(String content) throws NoTokenizationException {

		ArrayList<String> result = new ArrayList<String>();

//		long start = System.currentTimeMillis();
		tokenizer.parse(content);
		ArrayList<String> contentTokenized = tokenizer.getResult();
		Iterator<String> tokens = contentTokenized.iterator();
		String lemma;

		while (tokens.hasNext()) {
			String token = tokens.next();
			log.debug("analysing token '" + token + "'");
			totalCount++;
			if (token.matches("=|#")) { // BUG in transducer / java-lookup
				lemma = NORESULT;
			} else if ((lemma = cache.get(token)) != null) {
				cacheCount++;
			} else {
				Collection<String> lemmas = null;
				try {
					lemmas = analyze(token);
					log.debug("lemmas HER:" + lemmas);
					if (lemmas.isEmpty()) {
						lemma = NORESULT;
						log.debug("transducer result empty");
					} else {
						lemma = this.parseLemmas(lemmas, token);
						log.debug("chosen lemma: '" + lemma + "'");
					}
					cache.put(token, lemma);
				} catch (NoTokenizationException e) {
					lemma = NORESULT;
				}
			}
			result.add(token + " " + lemma);
		}

		return result;
	}

	/**
	 * if compound is found, choose the non-compound form --> if compounds are
	 * not found, this method is not called [see analyse] if compounds are found
	 * without non-compounds, return null This a fix compound lemmas . e.g.
	 * kemianteollisuus --> kemiateollisuus,
	 * 
	 * @param choices
	 * @return
	 */
	private String parseCompoundTerm(ArrayList<String> choices) {
		log.debug("--> parse compound");

		String result = null;
		String lastCompoundPart = null; // halla-aho --> aho
		int index;

		for (String s : choices) {

			if (!s.contains("[GUESS=COMPOUND]")) { // if there is a choice
													// _without_ COMPOUND,
													// choose that
				result = s;

			} else if ((index = s.lastIndexOf("COMPOUND][LEMMA=")) != -1) { // if
																			// there
																			// is
																			// a
																			// compound
																			// with
																			// multiple
																			// parts
																			// (lemma),
																			// prefer
																			// the
																			// last
																			// part,
																			// combine
																			// the
																			// prefix
																			// in
																			// lemmaWithOrigina
				lastCompoundPart = s.substring(index + 9); // [LEMMA
				String prefix = s.substring(0, s.indexOf("[LEMMA")); // between
																		// beginning
																		// and
																		// first
																		// lemma

				lastCompoundPart = prefix + lastCompoundPart;

				String lemmascombined = StringUtils.join(Tools.containsRipStartEnd(s, "\\[LEMMA='.*?\\]", "[LEMMA='", "']"), "");
				lastCompoundPart = lastCompoundPart.replaceFirst("\\[LEMMA='.*?\\]", "[LEMMA='" + lemmascombined + "']");

				// FIXME: 1960-luku --> 1960luku

				log.debug("lastCompoundPart: " + lastCompoundPart);
			}
		}

		if (result == null) { // if result null, check if the compound is having
								// two parts
			return lastCompoundPart;
		}

		return result;
	}

	private String parseLemmas(Collection<String> lemmas, String token) {
		if (preferredLemmas.containsKey(token)) { // in preferredTerms: häät:5,
													// meaning that 5th lemma is
													// the correct
													// interpretation for the
													// token 'häät'

			log.debug("token: '" + token + "', preferring lemma: " + lemmas.toArray(new String[0])[preferredLemmas.get(token)]);
			return lemmas.toArray(new String[0])[preferredLemmas.get(token)];
		}

		// log.debug(lemmas);
		boolean hasCompound = false;
		ArrayList<String> choices = new ArrayList<String>();

		for (String one : lemmas) {
			log.debug("option: " + one);

			choices.add(one.replaceAll("'\\|'", "")); // remove compound symbols
														// from lemma

			if (one.contains("[POS=CONJUNCTION]")) { // if any of the forms
														// contains conjunction
														// (kuin over kuu)
				choices.clear();
				choices.add(one);
				break;
			}
			if (one.contains("[BOUNDARY=COMPOUND]")) { // if any of the forms
														// contains compound¨
				hasCompound = true;
			}
		}

		String result = choices.get(choices.size() - 1); // DEFAULT: last value

		if (hasCompound) {
			String noncompoundLemma = this.parseCompoundTerm(choices);
			if (noncompoundLemma != null) {
				result = noncompoundLemma;
			}
		} else {
			String particle = preferPOS(choices, "PARTICLE");
			if (particle != null) {
				result = particle;
			}

			String adverbOverNoun = preferPosOverAnother(choices, "NOUN", "ADVERB"); // -->
																						// fixing
																						// 'jälkeen'
																						// -->
																						// 'jälki'

			// todo, optimize
			if (adverbOverNoun != null) {
				result = adverbOverNoun;
				log.debug("--> preferred adverb, result: " + result);
			} else {
				String nounOrverAdj = preferPosOverAnother(choices, "ADJECTIVE", "NOUN"); // -->
																							// fixing
																							// 'Aino'
																							// (ADJ)
																							// -->
																							// 'Aino'
																							// (NOUN)
				if (nounOrverAdj != null) {
					result = nounOrverAdj;
					log.debug("--> preferred noun over adj, result: " + result);
				}

			}
		}

		return result;
	}

	/**
	 * if one of the choices is having POS-parameter, return the first one
	 * coming This a fix for particle words. e.g. myös --> myödä will not
	 * happenbecause of this.
	 * 
	 * @param choices
	 * @return
	 */
	public String preferPOS(ArrayList<String> choices, String preferredPOS) {
		String result = null; // DEFAULT: last one
		for (String s : choices) {
			if (s.contains("[POS=" + preferredPOS + "]")) {
				return s;
			}
		}
		return result;
	}

	/**
	 * POS = Part-Of-Speech = sanaluokka first parameter POS have to be found,
	 * if found and second one exist also, second one is chosen
	 * 
	 * @param choices
	 * @return
	 */
	private String preferPosOverAnother(ArrayList<String> choices, String posToReplace, String preferablePos) {
		boolean posFound = false;
		String preferable = null;

		for (String s : choices) {
			if (s.contains("[POS=" + posToReplace + "]")) {
				posFound = true;
			} else if (s.contains("[POS=" + preferablePos + "]")) {
				preferable = s;
			}
		}
		if (posFound && preferable != null) {
			return preferable;
		} else {
			return null;
		}
	}
}
