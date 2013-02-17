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
package fi.metropolia.mediaworks.juju.rle;

import org.apache.commons.lang3.ArrayUtils;

public enum RDFType {
	XML("RDF/XML", new String[] {"rdf", "owl"}), TURTLE("TURTLE", new String[] {"ttl"});
	
	private String type;
	private String[] extensions;
	
	RDFType(String type, String[] extensions) {
		this.type = type;
		this.extensions = extensions;
	}
	
	public String getType() {
		return type;
	}
	
	public String[] getExtensions() {
		return extensions;
	}
	
	public static RDFType getRDFType(String extension) {
		for (RDFType t : RDFType.values()) {
			if (ArrayUtils.contains(t.extensions, extension)) {
				return t;
			}
		}
		return XML;
	}
}
