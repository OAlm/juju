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
package fi.metropolia.mediaworks.juju.extractor.keyphrase.filter;

import org.apache.log4j.Logger;

import com.google.common.base.Predicate;

import fi.metropolia.mediaworks.juju.document.OmorfiToken;
import fi.metropolia.mediaworks.juju.document.PosToken;
import fi.metropolia.mediaworks.juju.document.PartOfSpeech;
import fi.metropolia.mediaworks.juju.document.Token;
import fi.metropolia.mediaworks.juju.extractor.Gram;
import fi.metropolia.mediaworks.juju.syntax.fi.omorfi.OmorfiPartOfSpeech;

/**
 * Description of class
 * 
 * @author ollial
 * 
 */
public class EntityFilter implements Predicate<Gram> {
	private static final Logger log = Logger.getLogger(EntityFilter.class);
	
	private static final String CHECK = "\\p{L}{1,}(-\\p{L}{1,})*";

	@Override
	public boolean apply(Gram gram) {
		log.info("apply to: "+gram);
		if ((gram.size() > 1 && isEntity(gram.firstToken()) && isEntity(gram.lastToken())) || (gram.size() == 1 && isEntity(gram.firstToken()))) {
			log.info("--> true");
//			System.exit(0);
			return true;
		} else {
//			log.info("--> false");
			return false;
		}
	}

	public static boolean isEntity(Token token) {
		if (token instanceof OmorfiToken) {
			OmorfiToken ot = (OmorfiToken)token;
			if (ot.getOmorfiPartOfSpeech() == OmorfiPartOfSpeech.PROPER_NOUN) { // tagged as Proper noun by Omorfi
				return true;
			} else if ((ot.getOmorfiPartOfSpeech() == OmorfiPartOfSpeech.UNIDENTIFIED || ot.getOmorfiPartOfSpeech() == OmorfiPartOfSpeech.NOUN) && checkToken(ot)) {
				return true; // NOUN or UNIDENTIFIED with right form
			}
		} else if (token instanceof PosToken) {
			PosToken ct = (PosToken) token;
			if ((ct.getPartOfSpeech() == PartOfSpeech.BASE || ct.getPartOfSpeech() == PartOfSpeech.UNIDENTIFIED) && checkToken(ct)) { // all starting with uppercase letter, no POS information
				return true;
			}
		} else {
			if (checkToken(token)) { // all starting with uppercase letter, no POS information
				return true;
			}
		}
		return false;
	}
	
	private static boolean checkToken(Token token) {
		return token.getCaseChange().upperCase && token.getText().matches(CHECK);
	}
}
