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
package fi.metropolia.mediaworks.juju.label;

import java.util.ServiceLoader;

import org.apache.log4j.Logger;

import fi.metropolia.mediaworks.juju.document.Document;
import fi.metropolia.mediaworks.juju.extractor.Gram;
import fi.metropolia.mediaworks.juju.syntax.parser.DocumentBuilder;

abstract public class LabelMaker<A> {
	private static final Logger log = Logger.getLogger(LabelMaker.class);
	
	private static final LabelMaker<Object> FALLBACK = new LabelMaker<Object>() {
		@Override
		public boolean canHandle(Object object) {
			return true;
		}
		
		@Override
		public String createLabel(Object object) {
			return object.toString();
		}
	};
	
	@SuppressWarnings("rawtypes")
	private static final ServiceLoader<LabelMaker> labelMakerLoader = ServiceLoader.load(LabelMaker.class);
	
	public static String getLabel(Object object) {
		int priority = Integer.MIN_VALUE;
		
		LabelMaker<Object> selectedLM = null;
		
		for (LabelMaker<Object> lm : labelMakerLoader) {
			if (lm.getPriority() > priority && lm.canHandle(object)) {
				selectedLM = lm;
				priority = lm.getPriority();
			}
		}
		
		if (selectedLM == null) {
			selectedLM = FALLBACK;
		}

		String label = selectedLM.createLabel(object); 
		log.debug(String.format("Found label \"%s\" using \"%s\"", label, selectedLM));
		return label;
	}
	
	abstract public boolean canHandle(Object object);
		
	abstract public String createLabel(A object);
	
	/**
	 * Label priority
	 * 
	 * @return Priority
	 */
	public int getPriority() {
		return 0;
	}
	
	public static void main(String[] args) {
//		String text = "arkkitehtuurin ja yhteiskunnan";
		String text = "maailman parhaan pelaajan";
		Document d = DocumentBuilder.parseDocument(text, "fi");
		Gram g = new Gram(d.get(0));
		System.out.println(getLabel(g));
	}
}
