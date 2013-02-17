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
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.util.FileManager;



/**
 * Tänne kasataan yleistavaraa termitiedostojen käsittelyyn
 * @author alm
 *
 */

public class FileUtils {
	private static final Logger log = Logger.getLogger(FileUtils.class);

	public static String lastEncodingUsed;

	/**
	 *  
	 * Termimallia 1 verrataan 2:een ja päällekkäiset resurssit
	 * poistetaan jälkimmäisenä parametrina annetusta.
	 * Käytetään YSO-termistöjen päällekkäisyyksien poistamiseen, sillä
	 * osa termistöistä on tehty käsin ja Terms-all-tiedoston käsinsäätö olisi 
	 * raskasta...
	 * 
	 * @param m1 malli 1, säilyvä
	 * @param m2 malli 2, josta poistetaan ne urit, jotka löytyvät myös m1:stä
	 * 
	 * 
	 */

	public static Model removeDuplicateResources(Model m1, Model m2) {
		StmtIterator si = m1.listStatements();
		while(si.hasNext()) {
			Statement s = si.next();
			System.out.println(s.toString());
			if(m2.contains(s)) {
				System.out.println("\t--> sama löytyy!");
				m2.remove(s);
			}
		}
		si.close();
		return m2;
	}
/**
 * Read stream, DB-käyttöön
 * @param filename
 * @param encoding
 * @return
 * @throws IOException
 */	
	public static InputStream readFileStream(String filename)
		throws IOException {
		
		InputStream in = null;
		in = FileManager.get().openNoMap( filename );
		if (in == null) {
			throw new IllegalArgumentException(
					"File '" + filename + "' not found");
		}

		return in;
	}

	/**
	 * Tiedoston lukija, lukee ontologian sisään
	 */

	public static Model readOntologyFile(String filename, String encoding)
	throws IOException {
		InputStream in = null;
		in = FileManager.get().openNoMap( filename );
		if (in == null) {
			throw new IllegalArgumentException(
					"File '" + filename + "' not found");
		}
		Model model = ModelFactory.createDefaultModel();
		model.read(new InputStreamReader(in, encoding), "");

		return model;
	}
	
	/**
	 * FIXME: Why has parameter encoding? -Joni
	 * @param encoding Does nothing
	 */
	public static Model readOntologyFile(String filename, String encoding, String format) {
		
		Model model = FileManager.get().loadModel(filename, format);
		return model;
	}
	/**
	 * Tiedoston kirjoittaja, pukkaa ulos UTF-8-koodausta
	 */	
	public static void writeModelToFile(String filename, Model m) 
	throws IOException {

		if(filename == null) { //jos null, käytä oletusta
			return;
		}
		File f = new File(filename);
		FileOutputStream fos = new FileOutputStream(f);
		m.write(fos);
		fos.flush();
		fos.close();
	}
	
	public static void writeModelToFile(String filename, Model m, String format) 
	throws IOException {

		if(filename == null) { //jos null, käytä oletusta
			return;
		}
		File f = new File(filename);
		FileOutputStream fos = new FileOutputStream(f);
		m.write(fos, format);
		fos.flush();
		fos.close();
	}

	public static ArrayList <String> readFileToArrayList(String filename, String encoding) {

		
		ArrayList <String> result = new ArrayList <String>();

		try {
			BufferedReader in;
			File myFile = new File(filename);
			log.debug("reading file: "+myFile.getAbsolutePath());
//			System.out.println(myFile.getAbsolutePath());
//			System.out.println(myFile.exists());
			String line;				
			//PARSE FILE				
			if(myFile.exists()) {
				in = new BufferedReader(
						new InputStreamReader(
								new FileInputStream(myFile), encoding));
				while ((line = in.readLine()) != null) {
					
					result.add(line);
				}
			} else {
				log.error("File "+filename+" not found");
				System.exit(0);
			}

		} catch (Exception e){
			log.error("File "+filename+" not found");
			System.exit(0);
		}
		return result;
	}

	public static ArrayList <String> readFileToArrayList(File file, String encoding) {

		ArrayList <String> result = new ArrayList <String>();

		try {
			BufferedReader in;
			File myFile = file;

			String line;				
			//PARSE FILE				
			if(myFile.exists()) {
				in = new BufferedReader(
						new InputStreamReader(
								new FileInputStream(myFile), encoding));
				while ((line = in.readLine()) != null) {
					result.add(line);
				}
			}

		} catch (Exception e){
			System.err.println("Tiedostoa "+file.getName()+" ei löydy!");
			System.exit(0);
		}
		return result;
	}
	/**
	 * Geneerinen tiedostonlukija, lukee tiedoston ja palauttaa Stringin
	 * @param filename
	 * @param encoding
	 * @return
	 */
	public static String readFile(File filename, String encoding) 
	throws ArrayIndexOutOfBoundsException, Exception {

		StringBuffer textIn = new StringBuffer();


		BufferedReader in;
		File myFile = filename;
		if(myFile.exists()) {
			in = new BufferedReader(
					new InputStreamReader(
							new FileInputStream(myFile), encoding));
			textIn = new StringBuffer((int)myFile.length());
			String temp;
			while((temp = in.readLine()) != null) {
				textIn.append(temp + "\n");
				log.debug(".");
			}
			log.info("\n...valmis!");
			in.close();
		}															

		return textIn.toString();

	}

	/**
	 * Geneerinen tiedostonlukija, lukee tiedoston ja palauttaa Stringin
	 * @param filename
	 * @param encoding
	 * @return
	 */
	public static String readFileOrURL(String filename, String encoding) 
	throws ArrayIndexOutOfBoundsException, 
	MalformedURLException,
	Exception {

		String defaultEncoding = encoding;

		URL myUrl;
		StringBuffer textIn = new StringBuffer();

//		try {
		BufferedReader in;
		File myFile = new File(filename);
		if(myFile.exists()) {
			in = new BufferedReader(
					new InputStreamReader(
							new FileInputStream(myFile), encoding));
			textIn = new StringBuffer((int)myFile.length());
		} else {

			myUrl = new URL(filename);

			URLConnection urlconnection = myUrl.openConnection();

			urlconnection.setRequestProperty( "User-Agent", "Mozilla/4.0 (compatible; MSIE 5.5; Windows NT 5.0; H010818)" );

			String pageEncoding =  urlconnection.getContentEncoding();

			if(pageEncoding == null) {

				String charsetEncoding = urlconnection.getContentType();
				int index = charsetEncoding.indexOf("charset="); 
				if(index > 1) {
					encoding = charsetEncoding.substring(index+"charset=".length()).toUpperCase();
					//System.out.println("ENC:"+encoding);
				}


			} else {
				encoding = pageEncoding;
			}

			Charset pageCharset;
			try {
				pageCharset = Charset.forName(encoding);
				FileUtils.lastEncodingUsed = encoding;

			} catch(IllegalCharsetNameException e) {
				log.info("Illegal charset '"+encoding+"', using default ("+defaultEncoding+").");
			} catch(UnsupportedCharsetException e) {
				log.info("Charset '"+encoding+"' not supported, using default ("+defaultEncoding+").");
			} finally {
				pageCharset = Charset.forName(defaultEncoding); // alkup. param. annettu
				FileUtils.lastEncodingUsed = defaultEncoding;
			}

			in = new BufferedReader(
					new InputStreamReader(
							urlconnection.getInputStream(), pageCharset));


		}
		int luku = 1;
		String temp;
		while((temp = in.readLine()) != null) {
			textIn.append(temp + "\n");
			
			if(luku%1000==0) {
				System.out.print(".");
			
			}
			if(luku%100000==0) {
				System.out.println();
			}
					
//			System.out.println("Rivi "+luku);
			luku++;
		}
		System.out.println("\n...valmis: "+luku+ " riviä luettu.");
		in.close();
		/*
			} catch(ArrayIndexOutOfBoundsException e) {
					log.error("Array index out of bounds:: " + e);
			} catch(MalformedURLException e) {
					log.error("URL error: " + e);
					//System.exit(0);
			} catch(Exception e) {
				System.out.println("Error: " + e);
				//System.exit(0);
			}
		 */			
		return textIn.toString();

	}
	
	public static void writeToFile(String filename, String text) throws Exception {
		
			// Create file 
			FileWriter fstream = new FileWriter(filename);
			BufferedWriter out = new BufferedWriter(fstream);
			out.write(text.toString());
			//Close the output stream
			out.close();

	}
	
	public static void writeToFile(File file, String text) throws Exception {
		
		// Create file 
		FileWriter fstream = new FileWriter(file);
		BufferedWriter out = new BufferedWriter(fstream);
		out.write(text.toString());
		//Close the output stream
		out.close();

	}

	public static String getLastEncodingUsed() {
		return lastEncodingUsed;
	}

	public static void main(String [] args) throws IOException {
		System.out.print("Luetaan malli 1..");
		Model m1 = FileUtils.readOntologyFile("/home/alm/ontologies/ontologies/poka/frame/Terms-place-relativeFIXED.rdf","ISO-8859-1");
		System.out.println(".ok!");
		System.out.print("Luetaan malli 2..");
		Model m2 = FileUtils.readOntologyFile("/home/alm/ontologies/ontologies/poka/frame/All-animals-bugs.rdf","UTF-8");
		System.out.println(".ok!");
		System.out.print("Poistetaan duplikaatit..");
		Model m3 = FileUtils.removeDuplicateResources(m1,m2);
		System.out.println(".ok!");
		System.out.print("Kirjoitetaan tiedostoon..");
		FileUtils.writeModelToFile("/home/alm/ontologies/ontologies/poka/frame/All-animals-bugs-paikka.rdf", m3);
		System.out.println(".ok!");

	}


}
