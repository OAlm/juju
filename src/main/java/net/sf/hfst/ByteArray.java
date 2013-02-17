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
 * Original version published under Apache 2 license http://www.apache.org/licenses/LICENSE-2.0,
 * Copyright (c) Sam Hardwick
 * 
 ******************************************************************************/


package net.sf.hfst;

import java.lang.Math; // heh

/**
 * A way of handling unsigned little-endian data
 */
public class ByteArray {
	private byte[] bytes;
	private int index;
	private int size;

	public ByteArray(int s) {
		size = s;
		bytes = new byte[size];
		index = 0;
	}

	public ByteArray(ByteArray another, int s) {
		size = Math.max(s, another.getSize());
		bytes = new byte[size];
		for (int i = 0; i < another.getSize(); ++i) {
			bytes[i] = another.get(i);
		}
		index = 0;
	}

	public int getSize() {
		return size;
	}

	public byte get(int i) {
		return bytes[i];
	}

	public byte[] getBytes() {
		return bytes;
	}

	public short getUByte() {
		short result = 0;
		result |= bytes[index];
		index += 1;
		return result;
	}

	public int getUShort() {
		int result = 0;
		result |= (bytes[index + 1] & 0xFF);
		// even java's bytes are always signed - isn't that convenient?
		result <<= 8;
		result |= (bytes[index] & 0xFF);
		index += 2;
		return result;
	}

	public long getUInt() {
		long result = 0;
		result |= (bytes[index + 3] & 0xFF);
		result <<= 8;
		result |= (bytes[index + 2] & 0xFF);
		result <<= 8;
		result |= (bytes[index + 1] & 0xFF);
		result <<= 8;
		result |= (bytes[index] & 0xFF);
		index += 4;
		return result;
	}

	public Boolean getBool() {
		if (this.getUInt() == 0) {
			return false;
		}
		return true;
	}

	public float getFloat() {
		int bits = 0;
		bits |= (bytes[index + 3] & 0xFF);
		bits <<= 8;
		bits |= (bytes[index + 2] & 0xFF);
		bits <<= 8;
		bits |= (bytes[index + 1] & 0xFF);
		bits <<= 8;
		bits |= (bytes[index] & 0xFF);
		index += 4;
		return Float.intBitsToFloat(bits);
	}
}
