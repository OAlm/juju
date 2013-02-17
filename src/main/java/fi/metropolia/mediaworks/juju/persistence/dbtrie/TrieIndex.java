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
package fi.metropolia.mediaworks.juju.persistence.dbtrie;

//Vanha,yksinkertainen versio
//Olli: 28.5.2011, vaihdetaan pois ontologioista, luetaan
// sisään key-value mappi, jossa per rivi avain, erotin on ':' arvot
// pilkulla erotettuna oikealla. keyn ei tarvitse olla URI

/**
 * Toteuttaa termitiedostolle trie-indeksoinnin
 */

//TODO: hajota erilleen YSOn, termien ja verbien käsittely:
//yläluokka + 3 alaluokkaa
// OntoQuery --- YSOQuery / TermoQuery / VerboQuery
//
//Olli 28.5.
import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import fi.metropolia.mediaworks.juju.syntax.fi.omorfi.Lemmatizer;
import fi.metropolia.mediaworks.juju.util.PropertyUtil;
import fi.metropolia.mediaworks.juju.util.TernarySearchTrie;

public class TrieIndex implements TrieInterface {
	private TernarySearchTrie<Set<String>> trieIndex;
	private Multimap<String, String> labels = HashMultimap.create();

	/**
	 * Konstruktori, joka lukee suoraan mallista. --> parametri: vocabulary,
	 * configbundlena
	 * 
	 * @throws Exception
	 */
	public TrieIndex(File file, boolean lemmatize) throws Exception {
		trieIndex = new TernarySearchTrie<Set<String>>();
		trieIndex.setMatchAlmostDiff(2);
		loadVocabulary(file, lemmatize);
	}

	/**
	 * give lemmatizer as an input (for using same instance)
	 * 
	 * @param filename
	 * @param lemmatize
	 * @throws Exception
	 */
	public TrieIndex(File file) throws Exception {
		this(file, true);
	}

	protected void loadVocabulary(File file, boolean lemmatize) throws Exception {
		Map<String, String[]> m = PropertyUtil.parseProperties(PropertyUtil.loadProperties(file));

		Lemmatizer l = new Lemmatizer();

		for (Map.Entry<String, String[]> e : m.entrySet()) {
			for (String w : e.getValue()) {
				String text = lemmatize ? l.lemmatize(w) : w;
				put(text, e.getKey());
			}
		}
	}

	@Override
	public boolean containsPrefix(String term) {
		return !(trieIndex.matchPrefix(term)).isEmpty();
	}

	public List<String> getTermsForPrefix(String term) {
		return trieIndex.matchPrefix(term);
	}

	@Override
	public boolean contains(String term) {
		return trieIndex.get(term) != null;
	}


	/**
	 * get concept label by id
	 * 
	 * 
	 * 
	 */
	@Override
	public Collection<String> getLabels(String id) {
		return labels.get(id);
	}
	@Override
	public String getLabel(String id) {
		return this.getLabels(id).iterator().next();
	}
	
	/**
	 * Hakee puusta annetulla merkkijonolla alkavat merkkijonot
	 * 
	 * @param term
	 *            hakusana
	 * @return palauttaa täsmäävät avaimet
	 */
	@Override
	public List<String> matchByPrefix(String term) {
		return trieIndex.matchPrefix(term);
	}

	/**
	 * Epätäsmällinen haku puusta. Toimii huonosti, ei kannata käyttää.
	 * 
	 * @param term
	 *            hakutermi
	 * @param hits
	 *            jotakin, pitää tarkistaa
	 * @return osumat listana
	 */
	public List<String> matchAlmost(String term, int hits) {
		return trieIndex.matchAlmost(term);
	}

	/**
	 * Palauttaa labelia vastaavan urien joukon
	 * 
	 * @param label
	 * @return
	 */

	@Override
	public Set<String> getIds(String label) {
		return trieIndex.get(label);
	}

	/**
	 * Käsitteen lisäys triehen, käytetään ainoastaan käsitteen tai labelin
	 * lisäysmetodista.
	 * 
	 * @param key
	 *            merkkijonoavain
	 * @param objUri
	 *            lisättävä uri
	 */

	protected void put(String key, String objUri) {
		Set<String> temp = trieIndex.get(key);
		if (temp == null) {
			temp = new HashSet<String>();
		}
		temp.add(objUri);
		trieIndex.put(key, temp);
		
		labels.get(objUri).add(key);
	}

	@Override
	public int size() {
		return trieIndex.numDataNodes();
	}

	protected void remove(String key, String objUri) {
		Set<String> temp = trieIndex.get(key);
		if (temp == null) {
			return;
		}
		temp.remove(objUri);
		if (temp.isEmpty()) {
			this.trieIndex.remove(key);
		}
	}
}
