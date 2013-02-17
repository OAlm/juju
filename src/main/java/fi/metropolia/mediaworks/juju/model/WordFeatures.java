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
package fi.metropolia.mediaworks.juju.model;

public class WordFeatures implements Comparable<WordFeatures> {
	private String uri;
	private int hits;
	private double tf;
	private double idf;
	private double firstOccurence;
	private double lastOccurence;
	private double concurrency;
	
	public WordFeatures(String uri, int hits, double tf, double idf, double firstOccurence, double lastOccurence, double concurrency) {
		this.uri = uri;
		this.hits = hits;
		this.tf = tf;
		this.idf = idf;
		this.firstOccurence = firstOccurence;
		this.lastOccurence = lastOccurence;
		this.concurrency = concurrency;
	}
	
	public String getUri() {
		return uri;
	}

	public int getHits() {
		return hits;
	}

	public double getTf() {
		return tf;
	}

	public double getIdf() {
		return idf;
	}

	public double getFirstOccurence() {
		return firstOccurence;
	}

	public double getLastOccurence() {
		return lastOccurence;
	}

	public double getConcurrency() {
		return concurrency;
	}
	
	public double getTfidf() {
		return tf * idf;
	}
	
	public double getSpread() {
		return lastOccurence - firstOccurence;
	}
	
	public double getScore() {
		double score = 0;
		
		score += getTfidf();
		
		double posScore = 0;
		posScore += getSpread();
		posScore += 1 - getFirstOccurence();
		
		score *= posScore / 2;
		
		return score;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("Hits: %5d", hits));
		sb.append(String.format(" | TF: %f", tf));
		sb.append(String.format(" | IDF: %f", idf));
		sb.append(String.format(" | TFIDF: %f", getTfidf()));
		sb.append(String.format(" | FO: %f", firstOccurence));
		sb.append(String.format(" | LO: %f", lastOccurence));
		sb.append(String.format(" | S: %f", getSpread()));
		sb.append(String.format(" | C: %f", concurrency));
		sb.append(String.format(" | %f", getScore()));
		return sb.toString();
	}

	@Override
	public int compareTo(WordFeatures o) {
		return Double.compare(getScore(), o.getScore()) ;
	}
}
