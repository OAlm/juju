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
package fi.metropolia.mediaworks.juju.syntax.fi.omorfi;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Scanner;

import net.sf.hfst.NoTokenizationException;
import net.sf.hfst.UnweightedTransducer;
import net.sf.hfst.WeightedTransducer;

import org.apache.log4j.Logger;

public class Hyphenator {
	private static Logger log = Logger.getLogger(Hyphenator.class);
//	public interface transducer {
//		Collection<String> analyze(String str);
//	}
	
	public static void runTransducer(net.sf.hfst.Transducer t, String str)
	{
		System.out.println("Ready for input.");
		
		Scanner s = new Scanner(str);
		
		
		while (s.hasNext())
		{
			try {
				System.out.println(t.analyze(s.next()));
			} catch(NoTokenizationException e ) {
				log.error("no tokenization exception");
			}
		}
		
		s.close();
	}
	public static void main(String[] args) throws IOException {

		String str = "Halosen Niinistön Wahlströmin";
		
		FileInputStream transducerfile = null;
		String path = "C://data/workspace/omorfi3/src/transducer/hyphenation.dict.hfstol";
		try
		{ 

			transducerfile = new FileInputStream(path); 

		}
		catch (java.io.FileNotFoundException e)
		{
			System.err.println("File not found: couldn't read transducer file " + path + ".");
			System.exit(1);
		}
		
		System.out.println("Reading header...");
		net.sf.hfst.TransducerHeader h = new net.sf.hfst.TransducerHeader(transducerfile);
		DataInputStream charstream = new DataInputStream(transducerfile);
		System.out.println("Reading alphabet...");
		net.sf.hfst.TransducerAlphabet a = new net.sf.hfst.TransducerAlphabet(charstream, h.getSymbolCount());
		System.out.println("Reading transition and index tables...");
		if (h.isWeighted()) {
			WeightedTransducer transducer = new WeightedTransducer(transducerfile, h, a);
			runTransducer(transducer,str);
		} else {
			UnweightedTransducer transducer = new UnweightedTransducer(transducerfile, h, a);
			runTransducer(transducer,str);
		}
    }
}
