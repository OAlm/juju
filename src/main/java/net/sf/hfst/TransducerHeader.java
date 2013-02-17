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

//import java.io.DataInputStream;
import java.io.InputStream;

/**
 * On instantiation reads the transducer's header and provides an interface to
 * it.
 */
@SuppressWarnings("unused")
public class TransducerHeader {
	private int number_of_input_symbols;
	private int number_of_symbols;
	private int size_of_transition_index_table;
	private int size_of_transition_target_table;
	private int number_of_states;
	private int number_of_transitions;

	private Boolean weighted;
	private Boolean deterministic;
	private Boolean input_deterministic;
	private Boolean minimized;
	private Boolean cyclic;
	private Boolean has_epsilon_epsilon_transitions;
	private Boolean has_input_epsilon_transitions;
	private Boolean has_input_epsilon_cycles;
	private Boolean has_unweighted_input_epsilon_cycles;

	private Boolean hfst3;
	private Boolean intact;

	/**
	 * Read in the (56 bytes of) header information, which unfortunately is
	 * mostly in little-endian unsigned form.
	 */
	public TransducerHeader(InputStream file) throws java.io.IOException {
		hfst3 = false;
		intact = true; // could add some checks to toggle this and check outside
		ByteArray head = new ByteArray(5);
		file.read(head.getBytes());
		if (begins_hfst3_header(head)) {
			skip_hfst3_header(file);
			file.read(head.getBytes());
			hfst3 = true;
		}
		ByteArray b = new ByteArray(head, 56);
		file.read(b.getBytes(), 5, 51);

		number_of_input_symbols = b.getUShort();
		number_of_symbols = b.getUShort();
		size_of_transition_index_table = (int) b.getUInt();
		size_of_transition_target_table = (int) b.getUInt();
		number_of_states = (int) b.getUInt();
		number_of_transitions = (int) b.getUInt();

		weighted = b.getBool();
		deterministic = b.getBool();
		input_deterministic = b.getBool();
		minimized = b.getBool();
		cyclic = b.getBool();
		has_epsilon_epsilon_transitions = b.getBool();
		has_input_epsilon_transitions = b.getBool();
		has_input_epsilon_cycles = b.getBool();
		has_unweighted_input_epsilon_cycles = b.getBool();
	}

	public Boolean begins_hfst3_header(ByteArray bytes) {
		if (bytes.getSize() < 5) {
			return false;
		}
		// HFST\0
		return (bytes.getUByte() == 72 && bytes.getUByte() == 70 && bytes.getUByte() == 83 && bytes.getUByte() == 84 && bytes.getUByte() == 0);
	}

	public void skip_hfst3_header(InputStream file) throws java.io.IOException {
		ByteArray len = new ByteArray(2);
		file.read(len.getBytes());
		file.skip(len.getUShort() + 1);
	}

	public int getInputSymbolCount() {
		return number_of_input_symbols;
	}

	public int getSymbolCount() {
		return number_of_symbols;
	}

	public int getIndexTableSize() {
		return size_of_transition_index_table;
	}

	public int getTargetTableSize() {
		return size_of_transition_target_table;
	}

	public Boolean isWeighted() {
		return weighted;
	}

	public Boolean hasHfst3Header() {
		return hfst3;
	}

	public Boolean isIntact() {
		return intact;
	}
}
