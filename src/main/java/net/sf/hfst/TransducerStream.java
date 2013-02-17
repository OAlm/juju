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

/**
 * A simple extension of DataInputStream to handle unsigned little-endian data.
 */
public class TransducerStream extends DataInputStream {
	/**
	 * Invokes the DataInputStream constructor with a BufferedInputStream
	 * argument.
	 * 
	 * @param stream
	 *            BufferedInputStream containing little-endian unsigned
	 *            variables
	 */
	public TransducerStream(DataInputStream stream) {
		super(stream);
	}

	/**
	 * Reads the next two bytes as an unsigned little-endian short.
	 * 
	 * @return an int representing the unsigned short
	 */
	public int getUShort() throws java.io.IOException {
		short byte1 = (short) this.readUnsignedByte();
		short byte2 = (short) this.readUnsignedByte();
		int result = 0;
		result |= byte2;
		result <<= 8;
		result |= byte1;
		return result;
	}

	/**
	 * Reads the next four bytes as an unsigned little-endian int.
	 * 
	 * @return a long representing the unsigned int
	 */
	public long getUInt() throws java.io.IOException {
		short byte1 = (short) this.readUnsignedByte();
		short byte2 = (short) this.readUnsignedByte();
		short byte3 = (short) this.readUnsignedByte();
		short byte4 = (short) this.readUnsignedByte();
		long result = 0;
		result |= byte4;
		result <<= 8;
		result |= byte3;
		result <<= 8;
		result |= byte2;
		result <<= 8;
		result |= byte1;
		return result;
	}

	/**
	 * Reads four bytes (sic), returns false if they're all zero and true
	 * otherwise.
	 * 
	 * @return a boolean representing the underlying unsigned int
	 */
	public Boolean getBool() throws java.io.IOException {
		if (this.getUInt() == 0) {
			return false;
		}
		return true;
	}
}
