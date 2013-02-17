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

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.vocabulary.RDF;

//import fi.seco.semweb.service.poka.util.fdg.FDGConnection;
//import fi.seco.semweb.service.poka.util.fdg.FDGQizxLemmatizer;
//import fi.seco.semweb.service.poka.util.fdg.FDGSAXLemmatizer;
//import fi.seco.semweb.service.poka.util.fdg.LemmaListerFDG;
//import fi.seco.semweb.service.poka.util.omorfi.OMorfiConnection;


//FIXME: FDG & ei-robust mäkeen, korvataanko cache-toteutuksella vai monisanalähetyksellä?
// --> jotta monisanalähetys toimii on tokenisointi tehtävä ennen?
// --> onko tokenisointi tehtävä vastaavalla tavalla kuin dokumentin tokenisointi
// jos esim. 14.4.2008 muuttuu muotoon '14 . 4 . 2008', käsitteistössä saa olla
// käsite, joka alkaa '14.4.', koska dokumentin merkkejä katenoidaan yhteen
// käänteistä ei kuitenkaan pidä sallia: jos dokumentissa on '14.4.', sitä ei
// voida täsmätä käsitteeseen, jonka label on '14 . 4 .'
// --> on järkevää, että tokenisointi on yhdenmukainen tai niin päin, että
// käsite ei sisällä useampia sellaisia asioita, joita tekstissä esittää
//yksittäinen token. Molemmissa lienee järkevää käyttää yhdenmukaista tokenisointitapaa

/**
 * Termistöjen luontia hallinnoiva luokka, laaja luokka
 *	 joka sisältää myös kokoelman sekalaiseen käyttöön
 * 
 * @author alm
 *
 */

public class TermModelCreatorEngine {
	private static final Logger log = Logger.getLogger(TermModelCreatorEngine.class);
	
	public static final String ABBR = "oma";
	public static final String DEFAULTNAMESPACE = "http://www.seco.hut.fi/ns/2005/10/alm#";
	public static final String TERMCLASSNAME = "term";
	public static final String TERMLABEL = "termlabel";
	
	protected Model inputModel;
	protected Model termModel;
	protected HashSet <Property> propertySet;
	protected HashMap <String, String> objectpropertyMap;
	protected HashSet <String> langSet;
	
	
	protected final String DEFAULTHIERARCHICALPROPERTY = "http://www.w3.org/2000/01/rdf-schema#subClassOf";
	protected final String RDFNAMESPACE = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
	protected final String RDFTYPE = "type";
    
    public static final String NOLANG = "<NULL>";
    
    protected ArrayList <String> problemlabels = new ArrayList<String>();    
    
    protected int conceptCounter, labelCounter;
    protected Property termLabel;

    protected Resource termClass;
    
    protected String encoding;
    protected boolean encodingSet = false;
    
    protected boolean lemma;
    
	//valitut ominaisuudet
	protected ArrayList <Property> selectedProperties;
	
	//valitut kielet
	protected ArrayList <String> selectedLangs;
	
	//kielikohtaiset labelien lukumäärät
	protected HashMap <String, Integer> langToCount;	
	
//	private FDGQizxLemmatizer lemmatizer = new FDGQizxLemmatizer();
//	protected LemmatizerInterface lemmatizer;
	
	protected boolean maintainOriginalLabels;
	
	//valitut ranget 
	protected ArrayList <Resource> selectedRanges;

	protected Property rdftypeProperty;
	protected Property rdfsSubclassOfProperty;
	protected RDFNode rdfsClass;
	protected RDFNode owlClass;
        
	protected Property rdfType;
    
    //pitää kirjaa lemmattavien resurssien järjestyksestä
	protected ArrayList<Resource> orderedResourceURIs;
    
	protected StringBuffer allLemmas;
	protected ArrayList <String> unLemmatizedLabels;
    
    public TermModelCreatorEngine() {
		propertySet = new HashSet<Property>();
		objectpropertyMap = new HashMap<String, String>();
		
		langSet = new HashSet <String>();
		
		this.maintainOriginalLabels = false;
		
//		fdg = new FDGConnection();
		
		conceptCounter = 0;
		labelCounter = 0;
		
		termModel = ModelFactory.createDefaultModel();
		//nimiavaruuden lyhenne
		termModel.setNsPrefix(ABBR,DEFAULTNAMESPACE);
		termLabel = ResourceFactory.createProperty(DEFAULTNAMESPACE, TERMLABEL);

		//termi-instanssien tyyppi (TODO: päivitä Trunkiin!)
		termClass = ResourceFactory.createResource(DEFAULTNAMESPACE+TERMCLASSNAME);
			
		rdftypeProperty = ResourceFactory.createProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#","type");
		rdfsSubclassOfProperty = ResourceFactory.createProperty("http://www.w3.org/2000/01/rdf-schema#subClassOf");
			
		this.rdfsClass = ResourceFactory.createResource("http://www.w3.org/2000/01/rdf-schema#Class");
		this.owlClass = ResourceFactory.createResource("http://www.w3.org/2002/07/owl#Class");			

		
		this.selectedProperties = new ArrayList<Property>();
		this.selectedLangs = new ArrayList<String>();
		this.selectedRanges = new ArrayList<Resource>();
		
		
		//lemma true aina, stemmer käytössä
		this.lemma = false;
		
    }
    
//    public void setLemmatizer(LemmatizerInterface lemmatizer) {
//    	if(lemmatizer==null) {
//    		this.lemma = false;
//    	} else {
//    		this.lemma = true;
//    		this.lemmatizer = lemmatizer;	
//    	}
//    	
//    }
	
	static BufferedReader stdin =
		     new BufferedReader(new InputStreamReader(System.in));

  	public String readLine() {
  		String strValue=null;
		boolean ok;
			do {
				try {
					strValue = stdin.readLine();
					ok = true;
				} catch (Exception e) {
					System.out.println("Virhe rivin lukemisessa. Anna uusi!");
					ok = false;
				}
		    } while (!ok);
	    return strValue;
  	}
 


 /*
  * LEMMA
  */
  	public void setLemmaTrue() {
  		this.lemma = true;
  	}
  	
  	public void setLemmaFalse() {
  		this.lemma = false;
  	}
  	
  	public void lemmaAndMaintainOriginalLabels() {
  		this.maintainOriginalLabels = true;
  	}
  	
/**
 * vaihtaa lemman truesta falseksi ja toisin päin
 * (käliä varten)
 */
  	public void setLemma() {
  		this.lemma = !this.lemma;
  	}
  	
/*
 * LOPPUU
 */
 
  	
  	public boolean setModel(Model model) {
  		this.inputModel = model;
  		return true;
  	}
  	

  	
  	public boolean setModel(InputStream in, String encoding) {
  		inputModel = ModelFactory.createDefaultModel();
		
		try {

			inputModel.read(new InputStreamReader(in, encoding), "");
						
		} catch(Exception e) {
			System.out.println("Mallin lukemisessa ongelmia, tarkista tiedoston oikeellisuus!");
            e.printStackTrace();
            //System.exit(0);
        }
		return true;
  	}
  	
  	public boolean readFile(String filename, String encoding) {
  		return readFile(filename, encoding, null);
  	}
  	
  	/**
  	 * FIXME: Why has parameter encoding? -Joni
  	 * @param encoding Does nothing
  	 */
  	public boolean readFile(String filename, String encoding, String ser) {
  		log.info("reading file: "+filename);
		//InputStream in = null;
		//in = FileManager.get().open( filename );
  		if(ser == null) { //deault
  			ser = "RDF/XML";
  		}
  		
  		//FIXME
		Model m = FileManager.get().loadModel(filename, ser);
	    /*if (in == null) {
			System.out.println("File '" + filename + "' not found.");
			return false;
		}*/
	    
	    log.info("file read ok");
	    return this.setModel(m);
	    
  	}
  	
  	public boolean addFile(String filename, String encoding) {
  		return this.addFile(filename, encoding, null);
  	}
  	
  	/**
  	 * FIXME: Why has parameter encoding? -Joni
	 * @param encoding Does nothing 
	 */
  	public boolean addFile(String filename, String encoding, String serializationType) {

  		if(serializationType == null) {
  			serializationType = "RDF/XML";
  		}
  		log.info("reading file: "+filename);
  		Model m = FileManager.get().loadModel(filename, serializationType);
	    /*if (in == null) {
			System.out.println("File '" + filename + "' not found.");
			return false;
		}*/
	    
	    log.info("file read ok");
	    if(inputModel == null) {
	    	this.inputModel = m;
	    } else {
	    	this.inputModel.add(m);
	    }
		return true;
	    
  	}
/**
 * Kaivaa mallista esiin literaaliominaisuudet.
 * RDF-literaalit, eli:
 * ne ominaisuudet, jotka esiintyvät statementin keskimmäisenä
 * osana, joiden statementeissä kuvataan literaaliominaisuutta.
 *
 * --> tuetaan RDF:ää & OWL:ia..
 * 
 * lisäys 13.12.07: otetaan samalla objektityyppiset ominaisuudet talteen
 * ja järjestetään ne nätisti.
 *
 */  	
  	public void parseProperties() {
  		
  		StmtIterator si = inputModel.listStatements();
  		while(si.hasNext()) {
  			Statement statement = si.nextStatement();
  			if(statement.getObject().isLiteral()) {
  				//propertySet.add(statement.getPredicate().getURI());
  				propertySet.add(statement.getPredicate());
  				
  			} else { //objekti
  				objectpropertyMap.put(statement.getPredicate().getURI(), statement.getPredicate().getLocalName());
  			}
  		}
  	}
  		
  	
  	public Model getTermModel() {
  		return this.termModel;
  	}
  	
  	
  	/**
  	 * 
  	 * --> robusti, uusi versio metodista checkRange
  	 * 
  	 * Tarkistaa yksittäisiltä rangen resursseilta, onko niille
  	 * määritelty kieli ja property jota vaadittiin, jos
  	 * on, resurssi lisätään listaan ja vastaava lemmattava stringbufferiin
  	 * @param resource
  	 */  	
  	protected void addConceptsToTermModel(Iterator <Resource> concepts) {

  		if(!concepts.hasNext()) {
//			log.info("ResIterator concepts empty");
  			return;
  		}

  		/*
  		 * paikalliset muuttujat
  		 */
  		Resource termModelResource;
  		Resource resWithType;
  		StmtIterator conceptPropertyIterator;
  		Statement statement = null;
  		String literalValue;
  		/*
  		 * paikalliset muuttujat loppuu...
  		 */

  		while(concepts.hasNext()) {
  			log.debug("Iteroidaan resurssit");

  			resWithType = concepts.next();

  			//iteroi läpi valitun resurssin ominaisuuksien arvot
  			Property currentProperty;			
  			for(Property termProperty: this.selectedProperties) {

  				log.debug("Property on: "+termProperty.getURI());
  				log.debug("Käsite on  : "+resWithType.getURI());

  				StmtIterator testi = resWithType.listProperties();
  				if(!testi.hasNext()) {
  					System.out.println("resurssilla ei ominaisuuksia: "+resWithType.getURI());
  				}

  				currentProperty = termProperty; 

  				conceptPropertyIterator = resWithType.listProperties(currentProperty);

  				if(conceptPropertyIterator.hasNext()) {
  					log.debug("Käsitteellä "+resWithType.getLocalName()+" ominaisuus");
  					conceptCounter++;
  				} else {
  					log.debug("conceptPropertyIterator tyhjä!");					
  				}

  				while(conceptPropertyIterator.hasNext()) {

//					luo vastaava ominaisuus termimalliin
  					termModelResource =  termModel.createResource(resWithType.getURI());			
  					log.debug("Luodaan termitiedostoon URI: "+resWithType.getURI());
  					this.termModel.add(this.termModel.createStatement(termModelResource, this.rdftypeProperty, this.termClass));  					

  					labelCounter++;

  					try {
  						statement = conceptPropertyIterator.nextStatement();
  					} catch (NoSuchElementException e) {
  						System.out.println("No such element: " + e);
  						continue;
  					}
  					//iteroi kielimääreet
  					for(String lang: this.selectedLangs) {

  						log.debug("Iteroidaan käsitteen kielimääre"+lang);

  						if(statement.getLanguage().equals(lang)) {
  							langToCount.put(lang, langToCount.get(lang)+1);

  							literalValue = statement.getLiteral().getString().toLowerCase();
  							if(literalValue.equals("")) {
  								continue;
  							}
  							
  							log.debug("lang: '"+lang+ "'/ Label: "+literalValue);


  							if(lemma) { //FIXME, lemma disabled 20.4.2011

//  								if(maintainOriginalLabels) {
//
//  									termModelResource.addProperty(termLabel, 
//  											termModel.createLiteral(literalValue.toLowerCase()));
//
//  								}
//  								for(String lemma: this.lemmatizer.lemmaList(literalValue)) {
//  									termModelResource.addProperty(termLabel, 
//  	  										termModel.createLiteral(lemma));	
//  								}
  								

  							} else {

  								termModelResource.addProperty(termLabel, 
  										termModel.createLiteral(literalValue.toLowerCase()));

  							}

  							//kielimääreetön tagi mukaan
  						} else if(lang.equals(TermModelCreatorEngine.NOLANG) && statement.getLanguage().length()==0) {
  							langToCount.put(lang, langToCount.get(lang)+1);

  							literalValue = statement.getLiteral().getString().toLowerCase();

  							if(literalValue.equals("")) {
  								continue;
  							}
  							
  							log.debug("nolang / Label: "+literalValue);

  							
  								
  							
  							if(lemma) { //FIXME, lemma disabled 20.4.2011

//  								log.debug("nolang & lemma / Label: "+literalValue);
//
//  								if(maintainOriginalLabels) {
//
//  									termModelResource.addProperty(termLabel, 
//  											termModel.createLiteral(literalValue.toLowerCase()));
//  								}
//
//  								for(String lemma: this.lemmatizer.lemmaList(literalValue)) {
//  									termModelResource.addProperty(termLabel, 
//  	  										termModel.createLiteral(lemma));	
//  								}

  							} else {
  								log.debug("nolang & unlemma / Label: "+literalValue.toLowerCase());

  								termModelResource.addProperty(termLabel, 
  										termModel.createLiteral(literalValue.toLowerCase()));
  							}  							



  						}

  					}

  				}

  			}

  		}

  	}
  	
  	
/**
 * Palauttaa tiedot tehdyistä valinnoista
 * @return
 */
  	public ArrayList<String> getSummary() {
  		ArrayList <String> result = new ArrayList<String>();
		
  		result.add("Concepts: "+ conceptCounter);
		result.add("Labels : "+ labelCounter);
		for(String lang: langToCount.keySet()) {
			result.add(lang+": "+ langToCount.get(lang));
		}
		result.add("Terms: "+this.termModel.listStatements(null, this.termLabel, (RDFNode)null).toList().size());
  		return result;
  	}
  	
/**
 * Tämän käytöstä on ehkä syytä luopua, FDG-tyyppiset
 * ongelmat mäkeen.
 * 
 * --> ei päivitetty tallettamaan sekä lemmat että originaalit
 * 
 */
//  	public void createFDGTermModelUnstable() {
//  		log.debug("createTermModelNoRange");
//
//  		rdfType = inputModel.getProperty(RDFNAMESPACE, RDFTYPE);
//
//  		ResIterator concepts = inputModel.listSubjectsWithProperty(rdfType);
//
//  		log.debug("ResIterator concepts empty: "+!concepts.hasNext());
//
//  		Resource resWithType;
//
//  		Resource termModelResource;
//
//
//  		StmtIterator conceptPropertyIterator;
//  		Statement statement = null;
//  		String literalValue;
//
//  		ArrayList <String> unLemmatizedLabels = new ArrayList <String>(); 
//
//  		StringBuffer allLemmas = new StringBuffer(390000);
//
//  		//iteroi jokainen rdf:typen omaava resurssi
//  		while(concepts.hasNext()) {
//  			log.debug("Iteroidaan resurssit");
//
//  			resWithType = concepts.nextResource();
//
//  			//iteroi läpi valitun resurssin ominaisuuksien arvot
//  			Property currentProperty;			
//  			for(Property termProperty: this.selectedProperties) {
//
//  				currentProperty = termProperty; 
//  				//inputModel.getProperty(termProperty);
//
//  				conceptPropertyIterator = resWithType.listProperties(currentProperty);
//  				if(conceptPropertyIterator.hasNext()) {
//  					conceptCounter++;
//  				} 
//
////				TODO: yhdistelmä '._' lemman lopussa aiheuttaa ongelman
////				---> viimeinen '.' pitäisi poistaa
//  				while(conceptPropertyIterator.hasNext()) {
//  					labelCounter++;
//
//  					try {
//  						statement = conceptPropertyIterator.nextStatement();
//  					} catch (NoSuchElementException e) {
//  						System.out.println("No such element: " + e);
//  					}
//
//  					//iteroi kielimääreet
//  					for(String lang: this.selectedLangs) {
//  						if(statement.getLanguage().equals(lang)) {
//  							langToCount.put(lang, langToCount.get(lang)+1);
//
//  							literalValue = statement.getLiteral().getString();
//
//  							log.debug("lang: '"+lang+ "'/ Label: "+literalValue);
//
//  							if(lemma) {
//  								/* debug
//									String val = literalValue.replaceAll("\\?|_"," ")+"_ ";
//									if(!lemmatizer.hasCorrectEnding(val)) {
//										System.out.println("problemLemma: "+ val);
//										this.problemlabels.add(literalValue);
//									}
//  								 */									
//
//  								allLemmas.append(literalValue.replaceAll("\\?|_"," ")+"_ ");					
//  							} else {
//  								unLemmatizedLabels.add(literalValue);
//  							}
//
//  							//kielimääreetön tagi mukaan
//  						} else if(lang.equals(this.NOLANG) && statement.getLanguage().length()==0) {
//  							langToCount.put(lang, langToCount.get(lang)+1);
//
//  							literalValue = statement.getLiteral().getString();
//  							/*	DEBUG							
//								if(literalValue.startsWith("Decalci")||
//									literalValue.startsWith("Dekalsi")) {
//
//									System.out.println("DEK:");
//									System.out.println("literal: '"+literalValue+"'");
//									System.out.println("repla: '"+literalValue.replaceAll("\\?|_"," ")+"_ '");
//									System.out.println("lemma: '"+this.lemmatizer.getLemmaString(literalValue.replaceAll("\\?|_"," ")+"_ ")+"'");
//								}
//  							 */
//
//
//  							if(lemma) {
//  								log.debug("nolang & lemma / Label: "+literalValue);
//
//  								String val = literalValue.replaceAll("\\?|_"," ")+"_ ";
//  								/* DEBUG
//									if(!lemmatizer.hasCorrectEnding(val)) {
//										System.out.println("problemLemma: "+ val);
//										this.problemlabels.add(literalValue);
//									}
//  								 */
//
//
//  								allLemmas.append(literalValue.replaceAll("\\?|_"," ")+"_ ");					
//  							} else {
//  								log.debug("nolang & unlemma / Label: "+literalValue);
//
//  								unLemmatizedLabels.add(literalValue);
//  							}
//
//  						}
//  					}
//
//
//  				}
//
//  			}
//
//  		}
//
//  		System.out.println("------------------------------");
//  		System.out.println("Yhteenveto ontologiasta:");
//  		System.out.println("------------------------------");
//  		System.out.println("Käsitteitä yhteensä: "+ conceptCounter);
//  		System.out.println("Labeleita yhteensä : "+ labelCounter);
//
//  		for(String lang: langToCount.keySet()) {
//  			System.out.println(lang +"-labeleita: "+ langToCount.get(lang));
//  		}
//
//  		System.out.println("------------------------------");
//
//  		concepts = inputModel.listSubjectsWithProperty(rdfType);
//
//  		java.util.Iterator <String> termLabelIterator;
//
//  		//if(lemma) {
//  		fdg.setInput(allLemmas.toString());
//  		System.out.print("Lemmataan (koko: " +allLemmas.toString().length()+")...");
//  		InputSource is = fdg.getFDG();
//  		System.out.println("...ok!");
//  		LemmaListerFDG ll = new LemmaListerFDG();
//  		System.out.print("Parsitaan...");
//  		ll.parse(is);
//  		System.out.println("...ok!");
//
//  		ArrayList <String> tempL = ll.getResult();
//
//  		log.debug("--> Lemma-listan koko: " + tempL.size()+" <--");
//
//  		termLabelIterator = tempL.iterator();
//  		//} else {
//  		log.debug("--> Unlemma-listan koko: " + unLemmatizedLabels.size()+" <--");
//  		//termLabelIterator = unLemmatizedLabels.iterator();
//  		Iterator <String> unLemmaIterator = unLemmatizedLabels.iterator();
//  		//}
//
//  		//iteroi inputModelin käsitteet			
//  		while(concepts.hasNext()) {
//
//  			resWithType = concepts.nextResource();
//
//  			//luo vastaava ominaisuus termimalliin
//  			termModelResource =  termModel.createResource(resWithType.getURI());			
//  			log.debug("Luodaan termitiedostoon URI: "+resWithType.getURI());
//
//  			//UUSI TODO: päivitä Trunk
//  			this.termModel.add(this.termModel.createStatement(termModelResource, this.rdftypeProperty, this.termClass));
//
//
//  			//iteroi inputModelin ominaisuudet
//  			Property currentProperty;			
//  			for(Property termProperty: this.selectedProperties) {
//
//  				//currentProperty = inputModel.getProperty(termProperty);
//  				currentProperty = termProperty;
//
//  				conceptPropertyIterator = resWithType.listProperties(currentProperty);
//
//  				while(conceptPropertyIterator.hasNext()) {
//
//  					try {
//  						statement = conceptPropertyIterator.nextStatement();
//  					} catch (NoSuchElementException e) {
//  						System.out.println("No such element: " + e);
//  					}
//
//  					literalValue = statement.getLiteral().getString();
//
//  					for(String lang: this.selectedLangs) {
//  						if(statement.getLanguage().equals(lang)) {
//
//  							log.debug("Lang chosen: "+literalValue);
//
//  							String label;
//  							if(lemma) {
//  								label = termLabelIterator.next();
//
//  								//debug
//  								log.info(label +" <---> "+unLemmaIterator.next());
//  							} else {
//  								label = unLemmaIterator.next();							
//  							}
//  							//log.debug(label);
//
//
//  							termModelResource.addProperty(termLabel, 
//  									termModel.createLiteral(label));	
//
//  						} else if(lang.equals(TermModelCreatorEngine.NOLANG) && 
//  								statement.getLanguage().length()==0) {
//
//  							log.debug("No lang: "+literalValue);
//  							String label;
//
//  							if(lemma) {
//  								label = termLabelIterator.next();
//
//  								//debug
//  								log.info(label +" <---> "+unLemmaIterator.next());
//  							} else {
//  								label = unLemmaIterator.next();							
//  							}
//  							//log.debug(label);
//
//
//  							termModelResource.addProperty(termLabel, 
//  									termModel.createLiteral(label));
//
//  						}	
//  					}
//  				}
//
//
//
//
//  			}
//
//  		}
//
//
//  	}
/*
 * ROBUSTI TERMMODELCREATOR (toinen versio nopeampi, mutta ei vakaa
 */

  	public void createTermModelNoRangeRobust() {
  		log.debug("createTermModelNoRangeRobust");
 		

  		ResIterator concepts = inputModel.listSubjectsWithProperty(RDF.type);

  		log.debug("ResIterator concepts empty: "+!concepts.hasNext());

  		Resource resWithType;

  		Resource termModelResource;

  		int count = 0;

  		StmtIterator conceptPropertyIterator;
  		Statement statement = null;
  		String literalValue;

  		log.debug("Iteroidaan resurssit");
  		//iteroi jokainen rdf:typen omaava resurssi
  		//log.info("Käsitteitä: "+concepts.toSet().size());
  		while(concepts.hasNext()) {
  			System.out.print(".");
  			if(count%100 == 0 ){
  				System.out.println();
  			}
  			log.debug("käsite "+count);

  			resWithType = concepts.nextResource();

  			//iteroi läpi valitun resurssin ominaisuuksien arvot
  			Property currentProperty;			
  			for(Property termProperty: this.selectedProperties) {

  				currentProperty = termProperty; 
  				//inputModel.getProperty(termProperty);

  				conceptPropertyIterator = resWithType.listProperties(currentProperty);
  				if(conceptPropertyIterator.hasNext()) {
  					conceptCounter++;
  				}

  				while(conceptPropertyIterator.hasNext()) {

//					luo vastaava ominaisuus termimalliin
  					termModelResource =  termModel.createResource(resWithType.getURI());			
  					log.debug("Luodaan termitiedostoon URI: "+resWithType.getURI());
  					this.termModel.createStatement(termModelResource, this.rdftypeProperty, this.termClass);
  					
  					this.termModel.add(this.termModel.createStatement(termModelResource, this.rdftypeProperty, this.termClass));

  					labelCounter++;

  					try {
  						statement = conceptPropertyIterator.nextStatement();
  					} catch (NoSuchElementException e) {
  						System.out.println("No such element: " + e);
  						continue;
  					}
  					//iteroi kielimääreet
  					for(String lang: this.selectedLangs) {
  						if(statement.getLanguage().equals(lang)) {
  							langToCount.put(lang, langToCount.get(lang)+1);

  							literalValue = statement.getLiteral().getString().toLowerCase();

  							if(literalValue.equals("")) {
  								continue;
  							}
  							
  							log.debug("lang: '"+lang+ "'/ Label: "+literalValue);

  							//lisätään malliin sekä lemma & alkuperäinen

  								if(lemma) { //FIXME, lemma disabled 20.4.2011

//  		  							if(this.maintainOriginalLabels) {
//
//  		  								termModelResource.addProperty(termLabel, 
//  		  										termModel.createLiteral(literalValue));
//  		  							}
//  									
//  	  								for(String lemma: this.lemmatizer.lemmaList(literalValue)) {
//  	  									termModelResource.addProperty(termLabel, 
//  	  	  										termModel.createLiteral(lemma));	
//  	  								}

  	  							} else {
  	  								termModelResource.addProperty(termLabel, 
  	  										termModel.createLiteral(literalValue));

  	  							}
	
  							
  							
  							//kielimääreetön tagi mukaan
  						} else if(lang.equals(TermModelCreatorEngine.NOLANG) && statement.getLanguage().length()==0) {
  							langToCount.put(lang, langToCount.get(lang)+1);

  							literalValue = statement.getLiteral().getString();

  							if(literalValue.equals("")) {
  								continue;
  							}
  							
  							//lisätään malliin sekä lemma & alkuperäinen
  							else {
  	  							if(lemma) { //FIXME, lemma disabled 20.4.2011s
//  	  								log.debug("nolang & lemma / Label: "+literalValue);
//
//  	  								if(this.maintainOriginalLabels) {
//  	    							
//  	  									termModelResource.addProperty(termLabel, 
//  	  											termModel.createLiteral(literalValue));
//  	  								}
//  	  								
//  	  								termModelResource.addProperty(termLabel, 
//  	  										termModel.createLiteral(this.lemmatizer.lemma(literalValue)));

  	  								//allLemmas.append(literalValue.replaceAll("\\?|_"," ")+"_ ");					
  	  							} else {
  	  								log.debug("nolang & unlemma / Label: "+literalValue);

  	  								termModelResource.addProperty(termLabel, 
  	  										termModel.createLiteral(literalValue));

  	  							}
  								
  							}
  							

  						}

  					}

  				}

  			}
  			count++;
  		}

  		System.out.println("------------------------------");
  		System.out.println("Yhteenveto ontologiasta:");
  		System.out.println("------------------------------");
  		System.out.println("Käsitteitä yhteensä: "+ conceptCounter);
  		System.out.println("Labeleita yhteensä : "+ labelCounter);

  		for(String lang: langToCount.keySet()) {
  			System.out.println(lang +"-labeleita: "+ langToCount.get(lang));
  		}

  		System.out.println("------------------------------");



  	}

/*
 * END 
 */
	public boolean writeTermmodelToFile(String filename, String encoding) throws Exception {
		File outputFile = new File(filename);
		PrintWriter pw = new PrintWriter(outputFile, encoding);
		termModel.write(pw);
		return true;
	}
  	
  	
	public static void main(String [] args) {
		org.apache.log4j.PropertyConfigurator.configure("/home/alm/workspace/Trunk/fi/seco/semweb/service/poka/plainlog4jconfig.txt");
		org.apache.log4j.Logger.getRootLogger().setLevel(Level.INFO); 
		
		Logger termoLogger = Logger.getLogger("fi.seco.semweb.service.poka.TermModelCreator");
		termoLogger.setLevel(Level.INFO);
		
		TermModelCreator tee = new TermModelCreator(); 
		

		
		//LemmatizerInterface lemmatizer = new SnowballLemmatizer(Configuration.SNOWBALLENGLISH);
		//LemmatizerInterface lemmatizer = new FDGSAXLemmatizer();
//		LemmatizerInterface lemmatizer = new OMorfiConnection();
//		tee.setLemmatizer(lemmatizer);
/*		
		System.out.println("------------------------------------------------");
		System.out.println("Pokan termitiedostonluoja-wiz. (CTRL+C lopettaa)");
		System.out.println("------------------------------------------------");
		
		String filename = "";
		String encoding = "UTF-8";
		
		boolean encodingExist = false;
		
		while(!encodingExist) {
			System.out.println("Valitse tiedoston enkoodaus (oletus UTF-8):");
			encoding = tee.readLine();
			if(encoding.length()==0) {
				encoding = "UTF-8";
				encodingExist = true;
			} else {
				if(tee.isAllowedEncoding(encoding)) {
					encodingExist = true;
				} else if(encoding.equals("?")) {
					for(String s: Charset.availableCharsets().keySet()) {
						System.out.print("'"+s+"' ");
					}
					System.out.println();
				} else {
					System.out.println("Enkoodaus '"+encoding+"' tuntematon.\n'?' - listaa vaihtoehdot");
						
				}
			}
		}
				
		boolean fileExist = false;
				
		while(!fileExist) {
			System.out.println("Valitse tiedosto, josta löytyviä käsitteitä halutaan eristää:");
			filename = tee.readLine();
			if(tee.readFile(filename, encoding)) {
				fileExist = true;
				System.out.println("Tiedosto '"+filename+"' ok.");
			}
		}
		
		tee.parseProperties();
		
		boolean propertiesOk = false;
		String propertyName;
		//ArrayList <Property> selectedProps = new ArrayList<Property>();
		
		while(!propertiesOk) {
			System.out.println("Valitse ominaisuudet, jotka sisältävät käsitteen merkkijonoesityksen \n\t'?' - listaa ominaisuudet \n\t'q' - lopeta valintojen tekeminen");
			
			propertyName = tee.readLine();

			if(propertyName.equals("?")) {
				for(String propUri: tee.getPropertyURIS()) {
					System.out.println(propUri);
				}	
			} else {
				if(propertyName.equals("q")) {
					propertiesOk = true;
					break;
				}
				if(tee.containsProperty(propertyName)) {
					
					if(tee.selectedPropertiesContains(propertyName)) {
						tee.removeProperty(propertyName);
						System.out.println("Ominaisuus '"+propertyName+"' poistettu valinnoista");
					} else {
						tee.addProperty(propertyName);
						System.out.println("Ominaisuus '"+propertyName+"' lisätty valintoihin");
					}
				}
			}
					
			System.out.println("-----------");
			System.out.println("Valitut ominaisuudet: ");
			if(tee.selectedPropEmpty() || propertiesOk) {
				System.out.println("\t<tyhjä>");
			}
			for(String selected: tee.getSelectedPropertyURIs()) {
				System.out.println("\t"+selected);
			}
			System.out.println("-----------");
		}
		
		//tarkistaa ominaisuuksille käytetyt kielirajoitteet
		tee.parseLangs();
		
		boolean langOk = false;
		//ArrayList <String> langChosen = new ArrayList<String>();
		String langName;
		
		while(!langOk) {
			System.out.println("Valitse kieli, jonka merkkijonoja eristetään? \n\t'?' - " +
					"listaa kielet \n\t'q' - lopeta valintojen tekeminen. Jos kieltä ei valittu, eristetään kaikkia.");
			langName = tee.readLine();
			
			if(langName.equals("?")) {
				for(String langTag: tee.getLangs()) {
					System.out.println(langTag);
				}	
			} else {
				if(langName.equals("q")) {
					langOk = true;
					break;
				}
				if(tee.containsLang(langName)) {
					
					if(tee.containsSelectedLang(langName)) {
						tee.removeLang(langName);
						System.out.println("Kieli '"+langName+"' poistettu valinnoista");
					} else {
						tee.addLang(langName);
						System.out.println("Ominaisuus '"+langName+"' lisätty valintoihin");
					}
				}
			}
					
			System.out.println("-----------");
			System.out.println("Valitut kielet: ");
			if(tee.selectedLangIsEmpty()) {
				System.out.println("\t<tyhjä>");
			}
			for(String selected: tee.getSelectedLangs()) {
				System.out.println("\t"+selected);
			}
			System.out.println("-----------");
			
		}
		
		
		
		boolean lemma = false;
		String lemmaInput = "";

		tee.setLemmaTrue();

		while(!lemma) {
			System.out.println("Lemmataanko käsitteiden merkkijonot? (k / e)?");
			lemmaInput = tee.readLine();
			if(lemmaInput.equals("k")) {
				lemma = true;
			} else if(lemmaInput.equals("e")) {
							
				lemma=false;
			}
		}
		
		
		boolean outputOk = false;
		String outputDefaultFilename = "output.rdf";
		String outputFilename = "";
		
		
		while(!outputOk) {
			System.out.println("Anna kirjoitettavan tiedoston nimi (oletus: '"+outputDefaultFilename+"')");
			outputFilename = tee.readLine();
			if(outputFilename.length()==0) {
				outputFilename = outputDefaultFilename;
				outputOk = true;
			} else {
				outputOk = true;
			}
		}		

		System.out.println("----------------------------------------- ");
		System.out.println("Luodaan termitiedosto seuraavin asetuksin ");
		System.out.println("----------------------------------------- ");
		System.out.println("\tLähdetiedosto: '"+filename+"' ("+encoding+")");
		System.out.println("\tOminaisuudet:");
		for(String props: tee.getSelectedPropertyURIs()) {
			System.out.println("\t\t'"+props+"'");
		}
		if(lemmaInput.equals("k")) {
			System.out.println("\tLemmaus kytketty päälle.");
			tee.setLemmaTrue();
			//lemma = true;
		} else {
			System.out.println("\tLemmaus kytketty pois.");
			tee.setLemmaFalse();
			//lemma = false;
		}
		System.out.println("\tKohdetiedosto: '"+outputFilename+"' ("+encoding+")");
		System.out.println("----------------------------------------- ");
		
		boolean proceed = false;
		String proceedInput;
		while(!proceed) {
			System.out.println("Jatketaanko (k / e)?");
			proceedInput = tee.readLine();
			if(proceedInput.equals("k")) {
				proceed = false;
				break;
			} else if(proceedInput.equals("e")) {
				System.out.println("Lopetetaan suoritus.");
				System.exit(0);
			}
		}
*/	
		String encoding = "UTF-8";
		
		//FIXME: isoja termitiedostoja ei voi tehdä kerralla ajaen läpi FDG:stä
		// jos alaviiva jää edellisen / seuraavan FDG-parsinnan väliin, 
		// tokenisointi saattaa mennä pieleen --> käytä robustia parsintaa
		
		//tee.readFile("/local/alm/tervesuomi/ts-subject.rdf", encoding);
		//tee.readFile("/local/alm/tervesuomi/mesh.rdf", encoding);

		//tee.readFile("/home/alm/workspace/KulsaAnnotator/fi/seco/semweb/service/poka/kulsa/ontres/YSOMAOadditional.owl", encoding);
		tee.readFile("/local/alm/KulsaOntologiat/setti-11-09-07/paikat_koordinaatti.owl", encoding, null);
		//tee.readFile("/local/alm/tervesuomi/ts-subject-unlemma-modified.rdf", encoding);
		//tee.readFile("/local/alm/tervesuomi/test.rdf", encoding);
		//tee.addProperty("http://www.w3.org/2004/02/skos/core#prefLabel");
		//tee.addProperty("http://www.w3.org/2004/02/skos/core#altLabel");


		
//		String kulsaPath = "/local/alm/KulsaOntologiat/";
		//tee.readFile(kulsaPath+"toimijat/skstoimijat.rdf", encoding);
		
		
//		tee.addProperty("http://kulttuurisampo.fi/annotaatio#nimi");
		tee.addProperty("http://seco.tkk.fi/onto/toimo#display_name");
		tee.addProperty("http://xmlns.com/foaf/0.1/name");
//		tee.addLang("fi");
//		tee.addLang("en");
//		tee.addLang("sv");
		tee.addLang(TermModelCreatorEngine.NOLANG);
		
//		tee.addToRange("http://kulttuurisampo.fi/annotaatio#teos");
		
		//physical examinations
		//tee.addToRange("http://www.yso.fi/onto/mesh/D010808");
		//String outputFilename = "/local/alm/tervesuomi/testing.rdf";
//		String outputFilename = "/home/alm/workspace/KulsaAnnotator/fi/seco/semweb/service/poka/kulsa/ontres/skstoimijat_terms.rdf";
		//tee.setLemmaTrue();
		//tee.setLemmaFalse();
		tee.lemmaAndMaintainOriginalLabels();		
		//FIXME: Lemmatizer heittää lemmatermeihin ylimääräisen rivivaihdon!
		boolean robust = true;
		
		String hierarchicalPropertyName = "http://www.w3.org/2000/01/rdf-schema#subClassOf";
		
		tee.modelContruct(robust, null);
		//tee.modelContruct(robust, "http://www.w3.org/2004/02/skos/core#broader");
		//tee.createTermModelNoRange();
/*
		try {
			System.out.println("Kirjoitetaan malli tiedostoon");
			tee.writeToFile(outputFilename, encoding);
			System.out.println("Valmis!");
		} catch(Exception e) {
			System.err.println("Virhe kirjoitettaessa tiedostoon: "+e);
		}
*/
		for(Resource r : tee.listRootClasses(hierarchicalPropertyName)) {
			System.out.println(r.getURI());	
		}
		

	}





}
