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
package fi.metropolia.mediaworks.juju;

import java.net.URL;
import java.util.concurrent.ExecutionException;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import fi.metropolia.mediaworks.juju.corpus.Corpus;

public class Juju {	
	private static LoadingCache<String, Corpus> corpusCache = CacheBuilder.newBuilder()
		.maximumSize(5)
		.build(new CacheLoader<String, Corpus>() {
			@Override
			public Corpus load(String key) throws Exception {
				URL url = getClass().getResource(String.format("/corpus/%s.corpus", key));
				if (url != null) {
					return Corpus.load(url);
				} else {
					throw new Exception("Corpus not found!");
				}
			}
		});
	
	public static Corpus accessCorpus() {
		try {
			return corpusCache.get("wikipedia");
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		return new Corpus();
	}
	
	public static Corpus accessCorpus(String name) throws Exception {
		try {
			return corpusCache.get(name);
		} catch (ExecutionException e) {
			e.printStackTrace();
			throw new Exception(String.format("Could not access \"%s\" corpus!", name));
		}
	}
	
	public static void main(String[] args) throws Exception {
		Corpus c = accessCorpus();
		System.out.println(c);
	}
}
