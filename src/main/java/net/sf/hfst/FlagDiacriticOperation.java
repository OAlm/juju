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
 ******************************************************************************/
package net.sf.hfst;

/**
 * A representation of one flag diacritic statement
 */
public class FlagDiacriticOperation {
	public HfstOptimizedLookup.FlagDiacriticOperator op;
	public Integer feature;
	public Integer value;

	public FlagDiacriticOperation(HfstOptimizedLookup.FlagDiacriticOperator operation, Integer feat, Integer val) {
		op = operation;
		feature = feat;
		value = val;
	}

	public FlagDiacriticOperation() {
		op = HfstOptimizedLookup.FlagDiacriticOperator.P;
		feature = HfstOptimizedLookup.NO_SYMBOL_NUMBER;
		value = 0;
	}

	public Boolean isFlag() {
		return feature != HfstOptimizedLookup.NO_SYMBOL_NUMBER;
	}
}
