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


import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.log4j.Logger;

import com.google.common.collect.Lists;

public class StringSim {
	private static Logger log = Logger.getLogger(StringSim.class);

	private static Comparator<SortedSet<String>> GROUP_COMPARATOR = new GroupComparator();
	
	//constant P for JaroWinkler, by default 0.1, should not exceed 0.25 according to wikipedia
	// bigger value gives more weight to string prefix
	public static final double P = 0.10;
	
	//max prefix to be included
	public static final int  MAXPREFIX= 4;
	
	/**
	 * see http://en.wikipedia.org/wiki/Jaro%E2%80%93Winkler_distance AND
	 * http://lingpipe-blog.com/2006/12/13/code-spelunking-jaro-winkler-string-comparison/ 
	 * @param one
	 * @param two
	 * @return
	 */
	public static double jaroWinkler(String s1, String s2) {
		log.debug("JaroWinkler for "+s1+" and "+s2);
		double jaro = StringSim.jaroImpl(s1, s2);
//		System.out.println("common prefix: "+ commonprefix);
		double jw = jaro + commonprefix*P*(1-jaro);  
		
		return Math.round(jw*1000.0)/1000.0;
	}
	
	
	public static double prefixSimilarity(String s1, String s2) {
		int l1 = s1.length();
		int l2 = s2.length();
		
		String shorter;
		String longer;
		if(l1 < l2) {
			shorter = s1;
			longer = s2;
		} else {
			shorter = s2;
			longer = s1;
		}
		int count = 0;
		for(int i=0;i<shorter.length();i++) {
//			log.debug("comparing "+ shorter.charAt(i) +" vs " + longer.charAt(i));
			if(shorter.charAt(i) == longer.charAt(i)) {
				count++;
			} else {
				break;
			}
		}
		return (double)count / longer.length();
	}
		
	
	public static double jaro(String s1, String s2) {
		log.debug("Jaro for "+s1+" and "+s2);
		return StringSim.jaroImpl(s1, s2);
	}
	
	private static double jaroImpl(String s1, String s2) {
		
		int len1 = s1.length();
		int len2 = s2.length();
		
		int dist = StringSim.jaroMatchingDistance(len1, len2);
		log.debug("max dist: "+dist);
		
		StringSim.jaroCountMT(s1, s2, dist);
		
		log.debug("matches: "+m);
		log.debug("transpositions: "+t);
		
		log.debug("val:"  +(double)m/len1);
		log.debug("val2:"  +(double)m/len2);
		log.debug("val3:"  +(double)(m-t)/m);
		
		if(m==0) {
			return 0.0;
		} else {
			return Math.round(
					(double)1/3*( (double)m/len1+ (double)m/len2 + (double)(m-t)/m )*1000.0)/1000.0;
		}
		
	}

	/**
	 * number for max distance between characters, in order to consider them matching
	 * floor( max(|s1|, |s2|)/2 ) - 1 
	 *  
	 * @param s1
	 * @param s2
	 * @return
	 */
	private static int jaroMatchingDistance(int len1, int len2) {
		log.debug("len1: "+len1+", len2: "+len2);
		double max = Math.max(len1, len2)/2;
		return (int)Math.floor(max)-1;

	}
	/**
	 * number of matching characters
	 */
	private static int t;
	private static int m;
	private static int commonprefix; // for jarowinkler
	
	private static void jaroCountMT(String s1, String s2, int maxDist) {
		String shorter;
		String longer;

		if(s1.length() <= s2.length() ) {

			shorter = s1;
			longer = s2;

		} else {

			shorter = s2;
			longer = s1;

		}
		m = 0;
		t = 0;
		
		int count = 0;
		int inner;
		
		outer:

			for(int i = 0 ; i < shorter.length(); i++) {
				char c = shorter.charAt(i);
				log.debug("processing shorter, letter: "+c);

				int diff = i - maxDist; 
				inner = (diff <= 0)? 0 : diff;
				int max = (maxDist+i <= longer.length()) ? maxDist+i : longer.length();
				
				log.debug("maxdist: "+(max));
				for(int j = inner ; j < max ; j++) {
					log.debug("comparing s:"+i+" - l:"+j);
					log.debug(longer.charAt(j));
					if(c == longer.charAt(j)) {
						log.debug("Match: "+c+" - "+longer.charAt(j));
						m++;
						
						if(i>j) { // number of transpositions
							t++;
						}
						
						if(i==j && i==commonprefix) {// addition to common prefix
							log.debug("--> common");
							commonprefix++;
						}
						continue outer;
					}

					count ++;

				}

			}
		
		if(commonprefix > MAXPREFIX) {
			commonprefix = MAXPREFIX;
		}
		log.debug("Made "+count + " comparison for '"+shorter+"' and '"+longer+"'");
//		System.out.println("having t: "+t);
//		return m;
	}
	
	public static SortedSet<SortedSet<String>> groupSimilarStrings(Collection<String> strings, double threshold) {
		SortedSet<SortedSet<String>> result = new TreeSet<SortedSet<String>>(GROUP_COMPARATOR);
		
		stringLoop: for (String string : strings) {
			for (SortedSet<String> set : result) {
				for (String other : set) {
					if (prefixSimilarity(string, other) > threshold) {
						set.add(string);
						continue stringLoop;
					}
				}
			}
			
			SortedSet<String> newSet = new TreeSet<String>();
			newSet.add(string);
			result.add(newSet);
		}
		
		return result;
	}

	public static void main(String[] args) {
		List<String> test = Lists.newArrayList();
		test.add("Arno Breker");
		test.add("Arno Brekerin");
		test.add("Eva Braun");
		test.add("Eva Braunia");
		test.add("Theodor Morelliin");
		test.add("Theodor Morell");
		test.add("Joni Mertoniemi");
		test.add("Joni Mertoniemellä");
		test.add("Joni Mertoniemestä");
		test.add("Joakim Granbohm");
		Collections.shuffle(test);
		System.out.println(groupSimilarStrings(test, 0.7));
	}
	
	private static class GroupComparator implements Comparator<SortedSet<String>> {
		@Override
		public int compare(SortedSet<String> o1, SortedSet<String> o2) {
			if (o1.size() > 0 && o2.size() > 0) {
				return o1.first().compareTo(o2.first());
			}
			return 0;
		}
		
	}
}
