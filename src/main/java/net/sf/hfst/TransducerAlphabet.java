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
 * 
 * Original version of hfst-optimized lookup-java published under Apache 2 license http://www.apache.org/licenses/LICENSE-2.0,
 * Copyright (c) 2011 Sam Hardwick
 * http://sourceforge.net/p/hfst/code/2894/tree/trunk/hfst-optimized-lookup/hfst-optimized-lookup-java/
 * 
 ******************************************************************************/
package net.sf.hfst;

import java.io.DataInputStream;
import java.util.Vector;
import java.util.Hashtable;

/**
 * On instantiation reads the transducer's alphabet and provides an interface to
 * it. Flag diacritic parsing is also handled here.
 */
public class TransducerAlphabet {
	public Vector<String> keyTable;
	public Hashtable<Integer, FlagDiacriticOperation> operations;
	public Integer features;

	public TransducerAlphabet(DataInputStream charstream, int number_of_symbols) throws java.io.IOException {
		keyTable = new Vector<String>();
		operations = new Hashtable<Integer, FlagDiacriticOperation>();
		Hashtable<String, Integer> feature_bucket = new Hashtable<String, Integer>();
		Hashtable<String, Integer> value_bucket = new Hashtable<String, Integer>();
		features = 0;
		Integer values = 1;
		value_bucket.put("", 0); // neutral value
		int i = 0;
		int charindex;
		byte[] chars = new byte[1000]; // FIXME magic number
		while (i < number_of_symbols) {
			charindex = 0;
			chars[charindex] = charstream.readByte();
			while (chars[charindex] != 0) {
				++charindex;
				chars[charindex] = charstream.readByte();
			}
			String ustring = new String(chars, 0, charindex, "UTF-8");
			if (ustring.length() > 5 && ustring.charAt(0) == '@' && ustring.charAt(ustring.length() - 1) == '@' && ustring.charAt(2) == '.') { // flag diacritic identified
				HfstOptimizedLookup.FlagDiacriticOperator op;
				String[] parts = ustring.substring(1, ustring.length() - 1).split("\\.");
				/* Not a flag diacritic after all, ignore it */
				if (parts.length < 2) {
					keyTable.add("");
					i++;
					continue;
				}
				String ops = parts[0];
				String feats = parts[1];
				String vals;
				if (parts.length == 3) {
					vals = parts[2];
				} else {
					vals = "";
				}
				if (ops.equals("P")) {
					op = HfstOptimizedLookup.FlagDiacriticOperator.P;
				} else if (ops.equals("N")) {
					op = HfstOptimizedLookup.FlagDiacriticOperator.N;
				} else if (ops.equals("R")) {
					op = HfstOptimizedLookup.FlagDiacriticOperator.R;
				} else if (ops.equals("D")) {
					op = HfstOptimizedLookup.FlagDiacriticOperator.D;
				} else if (ops.equals("C")) {
					op = HfstOptimizedLookup.FlagDiacriticOperator.C;
				} else if (ops.equals("U")) {
					op = HfstOptimizedLookup.FlagDiacriticOperator.U;
				} else { // Not a valid operator, ignore the operation
					keyTable.add("");
					i++;
					continue;
				}
				if (value_bucket.containsKey(vals) == false) {
					value_bucket.put(vals, values);
					values++;
				}
				if (feature_bucket.containsKey(feats) == false) {
					feature_bucket.put(feats, features);
					features++;
				}
				operations.put(i, new FlagDiacriticOperation(op, feature_bucket.get(feats), value_bucket.get(vals)));
				keyTable.add("");
				i++;
				continue;
			}
			keyTable.add(ustring);
			i++;
		}
		keyTable.set(0, ""); // epsilon is zero
	}
}
