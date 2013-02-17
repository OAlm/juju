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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

public class RDFLabelExtractor {
	private static final Logger log = Logger.getLogger(RDFLabelExtractor.class);
	
	private static final String subjectCheck = "^[a-zA-Z][0-9]+$";
	
	private File file;
	private RDFType rdfType = RDFType.XML;
	
	private LabelCollector labelCollector;
	
	private String filter;

	public RDFLabelExtractor(String filePath) throws FileNotFoundException {
		this(filePath, null);
	}
	
	public RDFLabelExtractor(String filePath, String filter) throws FileNotFoundException {
		file = new File(filePath);
		
		if (!file.exists()) {
			throw new FileNotFoundException();
		}
		
		log.info(String.format("Using file \"%s\"", filePath));
		
		if (filePath.contains(".")) {
			String ext = filePath.substring(filePath.lastIndexOf('.')+1, filePath.length());
			rdfType = RDFType.getRDFType(ext);
			log.debug(String.format("Extension: %s", ext));
		} else {
			log.warn("File doesn't have extension. Using default type");
		}
		
		log.debug(String.format("Filetype: %s", rdfType.getType()));
		
		this.filter = filter;
	}
	
	private String findNamespace(Model model) {
		StmtIterator iter = model.listStatements(new CustomSelector("prefLabel"));
		
		log.debug("Searching label's namespace");
		while (iter.hasNext()) {
			Statement s = iter.next();
			String ns = s.getPredicate().getNameSpace();
			
			if (filter != null && !ns.contains(filter)) {
				continue;
			}
			
			
			log.info(String.format("Found label's predicate's namespace: %s", ns));
			return ns;
		}
		return null;
	}
	
	public void extract() throws Exception {		
		labelCollector = new LabelCollector();
		
		Model model = ModelFactory.createDefaultModel();
		
		InputStream in;
		try {
			in = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		}
		
		log.debug("Parsing RDF model");
		model.read(in, null, rdfType.getType());
		
		String ns = findNamespace(model);
		if (ns==null) {
			throw new Exception("Could not find labels namespace from RDF!");
		}
		
		Property labelProperty = model.createProperty(ns, "prefLabel");
		Property altLabelProperty = model.createProperty(ns, "altLabel");
		
		log.debug("Extracting prefLabels");
		findLabels(model.listStatements(null, labelProperty, (RDFNode)null));
		
		
		log.debug("Extracting altLabels");
		findLabels(model.listStatements(null, altLabelProperty, (RDFNode)null));
		
		log.debug("Labels extracted!");
	}
	
	private void findLabels(StmtIterator iter) {
		while(iter.hasNext()) {
			Statement labelStatement = iter.next();
			Resource r = labelStatement.getSubject();
			String subject = r.getLocalName();
			
			Literal labelLiteral = (Literal)labelStatement.getObject();
			
			if (!subject.matches(subjectCheck)) {
				continue;
			}
			
			labelCollector.addLabel(subject, labelLiteral.getLanguage(), labelLiteral.getString());
		}
	}
	
	/**
	 * 
	 * @param folder
	 * @param fileFormat Format to save files. Must contain "{LANG}" for language info.
	 * @throws Exception
	 */
	public void saveTo(File folder, String fileFormat) throws Exception {
		if (!folder.isDirectory()) {
			log.error("Incorrect folder: " + folder.toString());
			throw new Exception("folder is not directory!");
		}
		
		if (!fileFormat.contains("{LANG}")) {
			throw new Exception("fileFormat must contain \"{LANG}\"!");
		}
		
		for (String language : labelCollector.getLanguages()) {
			File file = new File(folder, fileFormat.replace("{LANG}", language));
			
			FileWriter outFile = new FileWriter(file);
			PrintWriter out = new PrintWriter(outFile);
			
			int concepts = labelCollector.getConcepts(language).size();
			int labels = 0;
			
			for (Entry<String, List<String>> subject : labelCollector.getConcepts(language)) {
				out.println(subject.getKey() + " : " + StringUtils.join(subject.getValue(), ", "));
				labels += subject.getValue().size();
			}
			
			out.close();
			
			log.info(String.format("Wrote %.1f kB to %s. %d concepts and %d labels.", file.length()/1024.0, file, concepts, labels));
		}
	}
		
	public void findDuplicates() {
		for (String language : labelCollector.getLanguages()) {
			System.out.println("");
			System.out.println(language);
			System.out.println("");
			
			Multiset<String> labels = HashMultiset.create();
			for (Entry<String, List<String>> e : labelCollector.getConcepts(language)) {
				labels.addAll(e.getValue());
			}
			
			for (Multiset.Entry<String> e : labels.entrySet()) {
				if (e.getCount() > 1) {
					System.out.println(e.getElement() + " " + e.getCount());
				}
			}
		}
	}
	
	public static void main(String[] args) throws Exception {
		
		String path = "C://data/bench/ontology/";
		String file = "koko";
		String suffix = ".ttl";
		RDFLabelExtractor le = new RDFLabelExtractor(path+file+suffix);
		le.extract();
		le.saveTo(new File(path), file+"-labels.{LANG}");
		
	}
}
