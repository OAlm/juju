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

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;

import fi.metropolia.mediaworks.juju.persistence.dbtrie.TrieIndex;

public class Vocabulary {
	private TrieIndex trie;
	private Map<String, String[]> labels;
	
	public Vocabulary(File file) throws Exception {
		trie = new TrieIndex(file, true);
		
		labels = new TreeMap<String, String[]>();
		
		try {
			FileInputStream fis = new FileInputStream(file);
			BufferedReader br = new BufferedReader(new InputStreamReader(new DataInputStream(fis), "UTF-8"));
		
			String line;
			while ((line = br.readLine()) != null) {
				String[] parts = line.split(":", 2);
				if (parts.length == 2) {
					String key = parts[0].trim();
					String[] props = parts[1].trim().split("[ ]*,[ ]*");
					labels.put(key, props);
				}
			}
			br.close();
		} catch (Exception e) {
			throw new Exception("Could not parse file!", e);
		}
	}
	
	public int size() {
		return labels.keySet().size();
	}
	
	public TrieIndex getTrie() {
		return trie;
	}
	
	public String getLabels(String id) {
		String[] l = labels.get(id);
		if (l != null) {
			return StringUtils.join(l, ", ");
		} else {
			return "NOT FOUND";
		}
	}
	
	public String getLabel(String id) {
		return this.getLabel(id, 0);
	}
	
	public String getLabel(String id, int index) {
		String[] l = labels.get(id);
		if (l != null) {
			int i = Math.min(index, l.length-1);
			return l[i];
		} else {
			return "NOT FOUND";
		}
	}
	
	public static void main(String[] args) throws Exception {
		
		//example
		String path = "C://data/bench/ontology/";
		Vocabulary v = new Vocabulary(new File(path+"magazine-labels.fi"));
		System.out.println(v.getLabel("m00006"));
		
	
		
	}
}
