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
package fi.metropolia.mediaworks.juju.vocabulary;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.RDFS;

public class TermModelCreator extends TermModelCreatorEngine {
	private static final Logger log = Logger.getLogger(TermModelCreator.class);

	public static final String SKOSNS    = "http://www.w3.org/2004/02/skos/core#";
	public static final String SKOSBROADERSTR    = SKOSNS+"broader";
	
	public TermModelCreator() {
		super();
		log.info("Init model ok");
		
	}

	/**
	 * Tarkistaa onko enkoodaus sallittujen arvojen joukossa.
	 * @param encoding
	 * @return
	 */
	public boolean isAllowedEncoding(String encoding) {
		return (Charset.availableCharsets().containsKey(encoding));  		  		
	}
	/**
	 * Palautta sisään ladatun mallin
	 * @return
	 */
	public Model getInputModel() {
		return this.inputModel;
	}

	/*
	 * SETTERIT ja GETTERIT
	 * 
	 * 
	 * 
	 */ 

	/**
	 * Sets encoding
	 */
	public boolean setEncoding(String encoding) {
		this.encoding = encoding;
		this.encodingSet = true;

		return this.isAllowedEncoding(this.encoding);
	}

	/*
	 * CLASSES
	 * 
	 */ 

	public boolean selectedRangeContains(String uri) {
		return this.selectedRanges.contains(this.inputModel.getResource(uri));
	}

	public boolean selectedRangeEmpty() {
		return this.selectedRanges.isEmpty();
	}

	public void addToRange(String uri) {
		if(!this.selectedRangeContains(uri)) { 
			this.selectedRanges.add(this.inputModel.getResource(uri));
		}
	} 

	public void removeFromRange(String uri) {
		if(this.selectedRangeContains(uri)) { 
			this.selectedRanges.remove(this.inputModel.getResource(uri));
		}
	}

	public ArrayList<Resource> getSelectedRange() {
		return this.selectedRanges;
	}

	public ArrayList<String> getSelectedRangeURIs() {
		ArrayList <String> result = new ArrayList<String>();  
		for(Resource r: selectedRanges) {
			result.add(r.getURI());
		}
		return result;
	}

	/*
	 *PROPERTIES 
	 *
	 */  	
	public boolean selectedPropertiesContains(String uri) {
		return this.selectedProperties.contains(this.inputModel.getProperty(uri));
	}

	public boolean selectedPropEmpty() {
		return this.selectedProperties.isEmpty();
	}

	public void addProperty(String uri) {
		if(!this.selectedPropertiesContains(uri)) { 
			this.selectedProperties.add(this.inputModel.getProperty(uri));
		}
	}
	public void removeProperty(String uri) {
		if(this.selectedPropertiesContains(uri)) {
			this.selectedProperties.remove(this.inputModel.getProperty(uri));
		}
	}

	public ArrayList<Property> getSelectedProperties() {
		return this.selectedProperties;
	}

	public ArrayList<String> getSelectedPropertyURIs() {
		ArrayList <String> result = new ArrayList<String>();  
		for(Property p: selectedProperties) {
			result.add(p.getURI());
		}
		return result;
	}
	/*
	 * LANGS  
	 *
	 */  	
	public void addLang(String lang) {
		if(!this.selectedLangs.contains(lang)) {
			this.selectedLangs.add(lang);
		} 		
	}

	public void removeLang(String lang) {
		if(this.selectedLangs.contains(lang)) {
			this.selectedLangs.remove(lang);
		} 		
	}

	public ArrayList <String> getSelectedLangs() {
		return this.selectedLangs;
	}
	public boolean containsSelectedLang(String lang) {
		return this.selectedLangs.contains(lang);
	}

	public boolean selectedLangIsEmpty() {
		return this.selectedLangs.isEmpty();
	}


	/**
	 * Kaivaa määritettyjen ominaisuuksien perusteella ominaisuuksien
	 * kielimääreet esille.
	 *
	 */
	public void parseLangs() {


		for(Property prop: propertySet) {

			StmtIterator si = inputModel.listStatements((Resource)null, prop, (String)null);
			boolean noLangChecked = false;

			while(si.hasNext()) {
				String lang = si.nextStatement().getLanguage();
				if(lang.length()!=0) {
					if(!langSet.contains(lang)) {
						langSet.add(lang);
					}	
				} else {
					if(!noLangChecked) { //jos määreettömiä löytyy, lisätään ne vaihtoehdoksi
						langSet.add(NOLANG);
					}
				}
			}				
		}

	}

	public HashSet<String> getLangs() {
		return this.langSet;
	}

	public boolean containsLang(String name) {
		return langSet.contains(name);
	}

	public HashSet <Property> getProperties() {
		return this.propertySet;
	}

	public ArrayList <String> getPropertyLocalNames() {
		ArrayList<String> result = new ArrayList<String>();

		for(Property p: propertySet) {
			result.add(p.getLocalName());
		}
		return result;	 		

	}

	public ArrayList<String> getPropertyURIS() {
		ArrayList<String> result = new ArrayList<String>();

		for(Property p: propertySet) {
			result.add(p.getURI());
		}
		return result;	 		
	}
	/**
	 * Palauttaa rangea varten listauksen luokista:
	 * ekana subclassOf, sitten skos:broader jos löytyy,
	 * loput aakkosittain 
	 * 
	 * @return
	 */  	
	public LinkedHashMap<String, String> getObjectPropertyMap() {
		LinkedHashMap<String, String> result = new LinkedHashMap<String,String>();

		result.put(RDFS.subClassOf.getURI(), "rdfs:subClassOf");

		this.objectpropertyMap.remove(RDFS.subClassOf.getURI());

		if(this.objectpropertyMap.containsKey(SKOSBROADERSTR)) {
			result.put(SKOSBROADERSTR, "skos:broader");
			this.objectpropertyMap.remove(SKOSBROADERSTR);
		}
		ArrayList <String> alphabetical = new ArrayList<String>(this.objectpropertyMap.keySet()); 

		//(Arrays.asList(this.objectpropertySet.toArray()));

		Collections.sort(alphabetical);

		for(String s: alphabetical) { //FIXME: järjestys nyt koko nimen perusteella, ei toimi...
			result.put(s, this.objectpropertyMap.get(s));
		}

		return result;	 		
	}
	/**
	 * Return roots via given property
	 * @param byProperty
	 * @return
	 */
	public HashSet<Resource> listRootClasses(String byPropertyStr) {

		log.info("Listing root classes...");

		//palauta sellaiset resurssit, joilla on alaluokkia, mutta
		//jotka eivät ole saman kaaren kautta jonkin luokan aliluokkia 
		String qString =
			"SELECT ?middleClass " +
			"WHERE {?subClass <"+byPropertyStr+"> ?middleClass . " +
			"   OPTIONAL {?middleClass <"+byPropertyStr+"> ?superClass} " +
			"   FILTER (!bound(?superClass)) " +
			"}";

		Query q = null;
		try {
			q = QueryFactory.create(qString, com.hp.hpl.jena.query.Syntax.syntaxSPARQL);
		} catch(com.hp.hpl.jena.query.QueryException e) {
			log.error("Error in query:\n"+qString+"\nError:"+e);
		}

		com.hp.hpl.jena.query.QueryExecution qe = QueryExecutionFactory.create(q, this.inputModel);

		HashSet<Resource> result = new HashSet<Resource>();
		try {
			ResultSet results = qe.execSelect();
			System.out.println(results.hasNext());
			for(; results.hasNext() ;) {
				QuerySolution res = results.nextSolution();
				Resource temp = (Resource)res.get("middleClass");
				if(temp.getURI()!= null) {
					result.add(temp);
				}
			}
		} finally { qe.close(); }

		log.info("...root classes ok!");

		return result;
	}

	/**
	 * Returns root-classes of the model
	 *
	 * @return			<code>ArrayList</code> containing URIs of root classes
	 */
	public ArrayList<Resource> listRootClasses() {

		try {
			ResIterator rdfsClassIter = this.inputModel.listSubjectsWithProperty(this.rdftypeProperty, this.rdfsClass);
			ResIterator ontClassIter = this.inputModel.listSubjectsWithProperty(this.rdftypeProperty, this.owlClass);

			//System.out.println(this.rdftypeProperty.getURI());
			if(this.rdfsClass.isURIResource()) {
				System.out.println(((Resource)rdfsClass).getURI());
			}

			if(this.owlClass.isURIResource()) {
				System.out.println(((Resource)owlClass).getURI());
			}

			ArrayList<Resource> rootClasses = new ArrayList<Resource>();

			log.info("listataan rdf -luokat");
			while (rdfsClassIter.hasNext()) {

				Resource r = rdfsClassIter.nextResource();

				//log.info("rdf:Class, root: "+r.getURI());

				if((!this.inputModel.contains(r, this.rdfsSubclassOfProperty)) &&
						r.getURI() != null) {
					log.info("rdf:Class, root: "+r.getURI());
					rootClasses.add(r);
				}
				/*				
					OntClass c = (OntClass)ext.next();

					if (c != null && c.getURI() != null) {

						ExtendedIterator ext2 = c.listSuperClasses();

						if (!ext2.hasNext())
							classes.addElement(c.getURI());
					}
				 */				
			}
			log.info("rdf -luokat ok");
			while (ontClassIter.hasNext()) {

				Resource r = ontClassIter.nextResource();

				if(!(this.inputModel.contains(r, this.rdfsSubclassOfProperty)) &&
						r.getURI() != null) {
					log.info("owl:Class, root: "+r.getURI());
					rootClasses.add(r);
				}
			}
			log.info("owl -luokat ok");			

			return rootClasses;
		}
		catch (Exception e) {

			log.error("Failed to list root classes");
			return null;
		}

	}

	/**
	 * Returns direct sub-classes of a class
	 *
	 * @return		<code>Vector</code> containing URIs of classes
	 */
	public ArrayList<Resource> listSubclassesDirect(Resource classRes, Property hierarchicalProperty) {


//		System.out.println("list subclasses");
		try {
			ArrayList<Resource> classes = new ArrayList<Resource>();

			ResIterator i =
				this.inputModel.listSubjectsWithProperty(
						hierarchicalProperty,
						classRes);

			while (i.hasNext()) {

				Resource node = i.nextResource();
				if(node.getURI() != null) {
					//				System.out.println("sub: "+node.getURI());
					classes.add(node);	
				}


				/*				
					if (subClass != null) {

						classes.addElement(subClass.getURI());
					}
				 */
			}

			return classes;
		} catch (Exception e) {
			System.err.println("Failed to list subclasses of the class "+classRes.getURI());

		}
		return null;
	}

	public ArrayList<Resource> listInstances(Resource classRes) {

		try {
			ArrayList<Resource> classes = new ArrayList<Resource>();

			ResIterator i =
				this.inputModel.listSubjectsWithProperty(
						this.rdfType,
						classRes);

			while (i.hasNext()) {

				Resource node = i.nextResource();
				if(node.getURI() != null) {
					classes.add(node);	
				}

			}

			return classes;
		} catch (Exception e) {
			System.err.println("Failed to list subclasses of the class "+classRes.getURI());

		}
		return null;

	}



	public boolean containsProperty(String uri) {

		return propertySet.contains(this.termModel.getProperty(uri));
	}

	/**
	 * Luo tietyn luokan aliluokista ja tyypeistä
	 * resurssiluettelon rekursiivisesti.
	 * kerää tulokset ResourceLabelMapperiin
	 * 
	 * @param initparam: ylimmän tason luokat, rekursiolla kutsutaan myöhemmin
	 * 
	 */
	public void checkRange(ArrayList<Resource> resourceList, Property hierarchicalProperty) {

//		log.info("checkRange: "+resourceList);

		if(resourceList==null) {
			return;
		}

		for(Resource r: resourceList) {

			//tarkista "luokan" suorat alaluokat
			this.checkRange(this.listSubclassesDirect(r, hierarchicalProperty), hierarchicalProperty);

			//tarkista "luokan" instanssit
			this.checkRange(this.listInstances(r), hierarchicalProperty);

		}

		//syvyyssuuntainen rekursio, ensin käydään pohjalla
		//tarkistaen ensin alaluokkaisuus, sitten
		//luokkien instanssit ja 
		//lopuksi kyseisen tason resurssit

//		log.info("Tarkistaan property ja lang listalle: "+resourceList);

		//VANHA, poistettu
		//this.hasPropertyAndLang(resourceList.iterator());
		this.addConceptsToTermModel(resourceList.iterator());

	}

	public void createTermModel() {
		this.modelContruct(true, null);
	}

	public void modelContruct(boolean robust, String hierarchicalPropertyName) {

		termModel = ModelFactory.createDefaultModel();
		Property hierarchicalProperty;
		if(hierarchicalPropertyName == null) {

			hierarchicalProperty = termModel.createProperty(this.DEFAULTHIERARCHICALPROPERTY);
		} else {

			hierarchicalProperty = termModel.createProperty(hierarchicalPropertyName);
		} 

		//ei tehdä mitään ilman valintoja
		if(this.selectedLangs.isEmpty() ||
				this.selectedProperties.isEmpty()) {

			return;
		}

		unLemmatizedLabels = new ArrayList <String>(); 
		allLemmas = new StringBuffer(390000);

		rdfType = inputModel.getProperty(RDFNAMESPACE, RDFTYPE);

		this.conceptCounter = 0;
		this.labelCounter = 0;

		//merkkijonoja vastaavat resurssit
		orderedResourceURIs = new ArrayList <Resource>();


		/* 
   POIS, tarpeeton nykyisessä käytössä
  		if(this.selectedLangs.isEmpty()) {
			log.debug("Ei kielimääreitä");
			allLanguages = true;
		}
		 */ 
		this.langToCount = new HashMap <String, Integer>();
		for(String lang: selectedLangs) {
			this.langToCount.put(lang, 0);
		}

		if(this.selectedRanges.isEmpty()) {
//			log.info("range tyhjä, luodaan suora malli");
			if(robust) {
				this.createTermModelNoRangeRobust();
			} else {
//				this.createFDGTermModelUnstable(); //CHECK FIXME
			}

		} else {
//			log.info("rangessa kamaa, luodaan vastaava malli");
			this.checkRange(this.selectedRanges, hierarchicalProperty);

			//VANHA: poistettu
			//this.finalizeTermModel();
		}

		System.out.println("------------------------------");
		System.out.println("Summary");
		System.out.println("------------------------------");
		System.out.println("Concepts in selection: "+ conceptCounter);
		//System.out.println("Labeleita yhteensä : "+ labelCounter);
		for(String lang: langToCount.keySet()) {
			System.out.println(lang +"-labels: "+ langToCount.get(lang));
		}
		System.out.println("Terms selected: "+this.termModel.listStatements(null, this.termLabel, (RDFNode)null).toList().size());


		System.out.println("------------------------------");

	}
	
	public static void main(String [] args) {

		Logger termoLogger = Logger.getLogger("fi.seco.semweb.service.poka");
		termoLogger.setLevel(Level.INFO);
		
		Logger omf = Logger.getLogger("fi.seco.semweb.service.poka.util.omorfi");
		omf.setLevel(Level.DEBUG);
		
//		LemmatizerInterface lemmatizer = new OMorfiConnection();
//		TermModelCreator tmc = new TermModelCreator(lemmatizer);
		TermModelCreator tmc = new TermModelCreator();
		tmc.lemmaAndMaintainOriginalLabels();
		
		tmc.readFile("/local/alm/KulsaOntologiat/setti-11-09-07/MAO.owl", "UTF-8");
		
	//	tmc.addLang(TermModelCreator.NOLANG);
		tmc.addLang("fi");
		
		tmc.addProperty("http://kulttuurisampo.fi/annotaatio#nimi");
		//tmc.addProperty(ResourcenameConstants.RDFSLABELSTR);
		
		tmc.createTermModel();
		try {
			tmc.writeTermmodelToFile("testi.rdf", "UTF-8");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

}
