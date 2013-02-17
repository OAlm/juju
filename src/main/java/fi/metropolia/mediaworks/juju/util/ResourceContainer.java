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
package fi.metropolia.mediaworks.juju.util;
/**
 * Pitää kirjaa dokument(e)ista löytyneistä resursseista
 * --> Entinen PokaInstance
 */
 

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.apache.log4j.Logger;

public class ResourceContainer {
	private static final Logger log = Logger.getLogger(ResourceContainer.class);
	public DecimalFormat df = new DecimalFormat("###.##");
	
	//mäppäys uri --> resurssi
	private HashMap <String, PokaResource> uris;
	private int occurenceCounter;
	public ResourceContainer() {
		uris = new HashMap <String, PokaResource>();
		occurenceCounter = 0;
	
	}
	
	public void clear() {

		if (uris != null) {

			uris.clear();
			occurenceCounter = 0;
		}

	}
/**
 * ----
 * Huom! ei käytössä tällä hetkellä. Jos sanakohtaista indeksointia
 * ei tarvita (= mihin käsitteisiin dokumnentin sana x viittaa?),
 * tätä voidaan käyttää (ja vastaavasti poistaa getSentence & getToken
 * DocumentExtractorin addConcept-metodista..
 * ----
 * 
 * Urin lisäys
 * @param uri uri
 * @param newOccurence jos true, kasvatetaan laskuria. False, kun lisätään monisanaisten
 *        käsitteiden muita osia (DocumentExtractor: checkLongTerm)
 */
	public void addURI(String uri, boolean newOccurence) {
		if(uris.containsKey(uri)) {
			PokaResource i = uris.get(uri);
			if(newOccurence) {
				i.add();
			}
		} else { //urin ilmentymiä ei ole, luodaan uri
			PokaResource i = new PokaResource(uri);
			this.uris.put(uri, i);
		}
		if(newOccurence) {
			occurenceCounter++; //esiintymien lukumäärän laskuri
		}
	}
	
	public HashMap<String, PokaResource> getResources() {
		return this.uris;
	}
	
	public HashMap<String, Integer> getFrequencies() {
		HashMap<String, Integer> result = new HashMap<String, Integer>();
		
		for (String uri : this.uris.keySet()) {
			result.put(uri, this.uris.get(uri).getCount());
		}
		
		return result;
	}
	
	public int size() {
		return this.uris.keySet().size();
	}
	
	
	/**
	 * Urin lisäys
	 * @param uri uri
	 * @param sent esiintymän lause 
	 * @param token esiintymän token 
	 * @param newOccurence jos true, kasvatetaan laskuria. False, kun lisätään monisanaisten
	 *        käsitteiden muita osia (extractMultiWord)
	 */
	public void addURI(String uri, int sent, int token) {

		PokaResource res;
		if(uris.containsKey(uri)) {
			
			res = uris.get(uri);
			res.add();
		} else { //urin ilmentymiä ei ole, luodaan uri
			
			res = new PokaResource(uri);
			this.uris.put(uri, res);
		}
		res.addLocation(sent, token);
		occurenceCounter++; //esiintymien lukumäärän laskuri
		
	}
	
	public void addMultiwordOccurence(Collection <String> urisToAdd, int sent, int startToken, int endToken) {
		
		for(String s: urisToAdd) {
			PokaResource res;
			if(!uris.containsKey(s)) { // if uri found first time, create it, e.g. jalkapallo

				res = new PokaResource(s);
				this.uris.put(s, res);	//associate uri to resource . jalkapallo ---> resource[jalkapallo]
			} else {

				res = uris.get(s);
				res.add(); //increase count for resource jalkapallo
			}
			res.addLocation(sent, startToken, endToken); // add location found to index
			occurenceCounter++; //occurence counter over total entities, e.g. jalkapallo, suunnistus
		
		}
	}
	
	public void addURI(Collection <String> urisToAdd, int sent, int token) {
		
		for(String uri: urisToAdd) {
			this.addURI(uri, sent, token);
		}
		
	}
	
	
	/**
     * TODO: voinee poistaa tulevaisuudessa, käytössä FrameExtractorCookissa
	 * 
	 * Urin sekä käsitteen esiintymän indeksin lisäys
	 * @param uri uri
	 * @param sent esiintymän lause
	 * @param token esiintymän token
	 * @param newOccurence jos true, kasvatetaan laskuria. False, kun lisätään monisanaisten
	 *        käsitteiden muita osia (DocumentExtractor: checkLongTerm)
	 * @param start esiintymän alkuindeksi
	 * @param end esiintymän loppuindeksi
	 */
//	public void addURI(String uri, int sent, int token, boolean newOccurence,
//				       int start, int end) {
//		if(uris.containsKey(uri)) {
//			PokaResource i = uris.get(uri);
//			if(newOccurence) {
//				i.add();
//			}
//			i.addLocation(start, end);
//		} else { //urin ilmentymiä ei ole, luodaan uri
//			PokaResource i = new PokaResource(uri);
//			i.addLocation(start, end);
//			this.uris.put(uri, i);
//		}
//		if(newOccurence) {
//			occurenceCounter++; //esiintymien lukumäärän laskuri
//		}
//	}
	
	
	public Set<String> getUris() {
		return this.uris.keySet();
	}

/**
 * Palauttaa järjestetyn merkkijonotaulukon löydetyistä
 * resursseista uri + count
 * @param desceding jos true, laskeva järjestys
 * @return
 */
	public ArrayList<String> getOrderedResults(boolean descending) {

		//purkka
		Iterator <String> i = uris.keySet().iterator(); 
		
		ArrayList <InstanceFrequency> al = new ArrayList <InstanceFrequency> ();
		
		String current;
		
		//palautetaan null jos ei vielä parsittu
		if(!i.hasNext()) {
			return null;
		}
		
		
		while(i.hasNext()) {
			current = i.next();
			
			al.add(new InstanceFrequency(current, uris.get(current).getCount()));		
		}	
		Collections.sort((al));
		
		String [] result = new String[uris.size()];
//		ArrayList <String> result = new ArrayList<String>(uris.size());
		
		int it;
		if(descending) {
			int size = uris.size()-1;
			for(it = 0; it <= size; it++) {
				result[it] =  al.get(size-it).getUri()+": "+ al.get(size-it).getFreq();
			}
		} else {
			it = 0;
			for(InstanceFrequency iF:al) {
				result[it] =  iF.getUri()+": "+iF.getFreq();
				it++;
			}
		}
		
		return new ArrayList<String>(Arrays.asList(result));
	}
	
	
	/**
	 * Palauttaa järjestetyn merkkijonotaulukon löydetyistä
	 * resursseista uri + count
	 * @param desceding jos true, laskeva järjestys
	 * @return
	 */
		public ArrayList<String> getOrderedUris(boolean descending) {
			
			ArrayList <InstanceFrequency> al = new ArrayList <InstanceFrequency> ();
			
			String current;
			
			Iterator <String> i = uris.keySet().iterator(); 
			//palautetaan null jos ei vielä parsittu
			if(!i.hasNext()) {
				return null;
			}
			
			
			while(i.hasNext()) {
				current = i.next();
				
				al.add(new InstanceFrequency(current, uris.get(current).getCount()));		
			}	
			Collections.sort((al));
			
			String [] result = new String[uris.size()];
//			ArrayList <String> result = new ArrayList<String>(uris.size());
			
			int it;
			if(descending) {
				int size = uris.size()-1;
				for(it = 0; it <= size; it++) {
					result[it] =  al.get(size-it).getUri();
				}
			} else {
				it = 0;
				for(InstanceFrequency iF:al) {
					result[it] =  iF.getUri();
					it++;
				}
			}
			
			return new ArrayList<String>(Arrays.asList(result));
		}

	
	/**
	 * Palauttaa käsitteen esiintymien määrän
	 * Jos URIa ei löydy, palautetaan -1
	 * @param uri merkkijono pitkässä muodossa, esim: http://yso.fi/YSO#liput
	 * @return esiintymien lukumäärän, jos uria ei löydy, -1 palautetaan
	 */
	
	public int getHits(String uri) {
		if(uris.containsKey(uri)) {
			return this.uris.get(uri).getCount(); 
		}
		log.debug("URIA ei löydy: "+uri);
		log.debug(uris.keySet().toString());
		return -1;
	}
	
	public PokaResource getInstance(String uri) {
		return uris.get(uri);
	}
	
	
//vanha toString, joka palauttaa yhteenvedon sekä löydetyt käsitteet
//järjestettynä, järjestämiseen käytetään instanceFrequencyä.
	@Override
	public String toString() {
		
		String occ = "0";
		if(uris.size()!=0) {
			occ = df.format(((double)occurenceCounter/uris.size()));
		}
		
		String result = "Concepts found   : " + uris.size() + "\n"+
                        "Occurences found : " + occurenceCounter + "\n"+
                        "#OCC / #CONCEPTS : " +occ+"\n";
	
		
		Iterator <String> i = uris.keySet().iterator(); 

		ArrayList <InstanceFrequency> al = new ArrayList <InstanceFrequency> ();
		
		String current;
		while(i.hasNext()) {
			current = i.next();
			al.add(new InstanceFrequency(current, uris.get(current).getCount()));		
		}	
		
		Collections.sort(al,Collections.reverseOrder());
		for(InstanceFrequency iF:al) {
			result += iF.toString()+"- #"+iF.getFreq()+", "+df.format((((double)iF.getFreq()/occurenceCounter)))+"%\n";
		}
		return result;
	}
	//modified version of toString for the web api
	public String toStringWebOutput() {
		String result="";
//		String result = "Käsitteitä yhteensä    : " + uris.size() + "\n"+
//                        "Käsitteiden esiintymiä : " + occurenceCounter + "\n"+
//                        "Esiintymiä per käsite  : " +((double)occurenceCounter/uris.size())+"\n";
//	
		
		Iterator <String> i = uris.keySet().iterator(); 

		ArrayList <InstanceFrequency> al = new ArrayList <InstanceFrequency> ();
		
		String current;
		while(i.hasNext()) {
			current = i.next();
			al.add(new InstanceFrequency(current, uris.get(current).getCount()));		
		}	
		Collections.sort(al,Collections.reverseOrder());
		for(InstanceFrequency iF:al) {
			if(!iF.toString().substring(0,1).equals("0")){
			result += iF.toString();
			break;
			}
		}
		return result;
	}
	/**
	 * print with labels
	 * @param trie
	 */
	public String toString(Vocabulary vocab) {
		String occ = "0";
		if(uris.size()!=0) {
			occ = df.format(((double)occurenceCounter/uris.size()));
		}
		
		String result = "Concepts found   : " + uris.size() + "\n"+
                        "Occurences found : " + occurenceCounter + "\n"+
                        "#OCC / #CONCEPTS : " +occ+"\n";
	
		
		Iterator <String> i = uris.keySet().iterator(); 

		ArrayList <InstanceFrequency> al = new ArrayList <InstanceFrequency> ();
		
		String current;
		while(i.hasNext()) {
			current = i.next();
			al.add(new InstanceFrequency(current, uris.get(current).getCount()));		
		}	
		
		Collections.sort(al,Collections.reverseOrder());
		for(InstanceFrequency iF:al) {
			result += iF.toString()+"- "+vocab.getLabel(iF.getUri())+" - #"+iF.getFreq()+", "+df.format((((double)iF.getFreq()/occurenceCounter)))+"%\n";
		}
		return result;
	}
	
//	public String toString(OntologyQueryInterface oqi) {
//		String result = "";
//		ArrayList <InstanceFrequency> al = new ArrayList <InstanceFrequency> ();
//		Iterator <String> i = uris.keySet().iterator(); 
//
//		String current;
//		while(i.hasNext()) {
//			current = i.next();
//			al.add(new InstanceFrequency(current, uris.get(current).getCount()));		
//		}	
//		Collections.sort((al));
//		for(InstanceFrequency iF : al) {
//			result += oqi.getLabel(iF.getUri()+" / ")+iF.toString()+"\n";
//		}
//		return result;	
//	}

}
