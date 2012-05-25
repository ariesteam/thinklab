/**
 * Copyright 2011 The ARIES Consortium (http://www.ariesonline.org) and
 * www.integratedmodelling.org. 

   This file is part of Thinklab.

   Thinklab is free software: you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published
   by the Free Software Foundation, either version 3 of the License,
   or (at your option) any later version.

   Thinklab is distributed in the hope that it will be useful, but
   WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with Thinklab.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.integratedmodelling.thinklab.owlapi;

import java.io.File;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import org.integratedmodelling.exceptions.ThinklabConfigurationException;
import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.exceptions.ThinklabIOException;
import org.integratedmodelling.exceptions.ThinklabInternalErrorException;
import org.integratedmodelling.exceptions.ThinklabResourceNotFoundException;
import org.integratedmodelling.thinklab.Thinklab;
import org.integratedmodelling.thinklab.api.configuration.IConfiguration;
import org.integratedmodelling.thinklab.api.knowledge.IConcept;
import org.integratedmodelling.thinklab.api.knowledge.IKnowledge;
import org.integratedmodelling.thinklab.api.knowledge.IOntology;
import org.integratedmodelling.thinklab.api.plugin.IPluginLifecycleListener;
import org.integratedmodelling.thinklab.api.plugin.IThinklabPlugin;
import org.integratedmodelling.thinklab.configuration.Configuration;
import org.integratedmodelling.thinklab.interfaces.IKnowledgeRepository;
import org.semanticweb.owl.apibinding.OWLManager;
import org.semanticweb.owl.inference.OWLClassReasoner;
import org.semanticweb.owl.inference.OWLConsistencyChecker;
import org.semanticweb.owl.inference.OWLIndividualReasoner;
import org.semanticweb.owl.inference.OWLPropertyReasoner;
import org.semanticweb.owl.inference.OWLReasoner;
import org.semanticweb.owl.model.OWLDataFactory;
import org.semanticweb.owl.model.OWLException;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLOntologyCreationException;
import org.semanticweb.owl.model.OWLOntologyManager;
import org.semanticweb.owl.util.AutoURIMapper;
import org.semanticweb.owl.util.DLExpressivityChecker;
import org.semanticweb.owl.util.SimpleURIMapper;
import org.semanticweb.owl.vocab.Namespaces;

import uk.ac.manchester.cs.owl.inference.dig11.DIGReasoner;

/**
 * @author Ioannis N. Athanasiadis
 * 
 */
public class FileKnowledgeRepository implements IKnowledgeRepository {
	
	public static final String DEFAULT_TEMP_URI = "http://www.integratedmodelling.org/temporary/";
	protected OWLOntologyManager manager;
	private File repositoryDirectory = null;
	private File tempDirectory;
	protected Hashtable<String, IOntology> ontologies = new Hashtable<String, IOntology>();
	protected Hashtable<String, String> ontologyfn = new Hashtable<String, String>();
	protected Hashtable<String, IOntology> tempontologies = new Hashtable<String, IOntology>();
	
	protected Registry registry;
	
	private OWLClassReasoner classReasoner;
	private OWLIndividualReasoner instanceReasoner;
	private OWLPropertyReasoner propertyReasoner;
	private OWLConsistencyChecker consistencyReasoner;
	
	private IConcept rootConcept;
	private IConcept noConcept;
	
	protected static OWLDataFactory df;

	
	public OWLClassReasoner getClassReasoner() {
		return classReasoner;
	}
	public OWLPropertyReasoner getPropertyReasoner() {
		return propertyReasoner;
	}
	public OWLConsistencyChecker getConsistencyReasoner() {
		return consistencyReasoner;
	}
	public OWLIndividualReasoner getInstanceReasoner() {
		return instanceReasoner;
	}
	
	class UriPublisher implements IPluginLifecycleListener {

		@Override
		public void onPluginLoaded(IThinklabPlugin plugin) {
		}

		@Override
		public void onPluginUnloaded(IThinklabPlugin plugin) {
		}

		@Override
		public void prePluginLoaded(IThinklabPlugin thinklabPlugin) {

			/* add an autourimapper for the ontologies directory if any exists */
			File ontologiesFolder = 
				thinklabPlugin.getWorkspace(IConfiguration.SUBSPACE_KNOWLEDGE);
				
			if (ontologiesFolder.exists()) {
				
				Thinklab.get().logger().info(
						"publishing " + ontologiesFolder + 
						" location into ontology manager");
				AutoURIMapper mapper = new AutoURIMapper(ontologiesFolder, true);
				manager.addURIMapper(mapper);
			}
		}

		@Override
		public void prePluginUnloaded(IThinklabPlugin thinklabPlugin) {
		}
		
	}
	
	/**
	 * Default constructor. To be followed by initialize().
	 * 
	 * @throws ThinklabIOException
	 */
	public FileKnowledgeRepository() throws ThinklabIOException {
		
		repositoryDirectory = Thinklab.get().getScratchArea("ontology/repository");
		tempDirectory = Thinklab.get().getScratchArea("ontology/tmp");
		manager = OWLManager.createOWLOntologyManager();
		registry = Registry.get();
		registry.registerURI("owl", URI.create("http://www.w3.org/2002/07/owl"));
		df = manager.getOWLDataFactory();
		rootConcept = getRootConcept();

		/* add an autourimapper for the ontologies directory if any exists */
		File ontologiesFolder = 
				Thinklab.get().getLoadPath(IConfiguration.SUBSPACE_KNOWLEDGE);
			
		if (ontologiesFolder.exists()) {
			
			Thinklab.get().logger().info(
					"publishing " + ontologiesFolder + 
					" location into ontology manager");
			AutoURIMapper mapper = new AutoURIMapper(ontologiesFolder, true);
			manager.addURIMapper(mapper);
		}

		
		/*
		 * TODO this is disabled, needs to be done on a project basis.
		 * 
		 * register a plugin listener that will publish the physical location of
		 * ontologies in plugins, so we don't need to be online to use thinklab.
		 */
		Thinklab.get().addPluginLifecycleListener(new UriPublisher());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.integratedmodelling.thinklab.interfaces.IKnowledgeRepository#initialize()
	 */
	public void initialize() throws ThinklabException {
//		FilenameFilter owlfilter = new FileTypeFilter(
//				FileTypeFilter.OWLFileType);
//		for (File f : repositoryDirectory.listFiles(owlfilter)) {
//			String nspace = null;
//			try {
//				URL url = f.toURI().toURL();
//				// This is the filename without the .owl ending. It will be used
//				// as the short namespace!
//				nspace = f.getName().substring(0, f.getName().length() - 4);
//				importOntology(url, nspace, false);
//			} catch (Exception ex) {
//				// log.warn("Cant load ontology for the file: " + f.getName()
//				// + " " + ex.getMessage());
//			}
//		}
		/*
		 * either connect to a configured DIG reasoner, or use a simple transitive one.
		 */
		connectReasoner();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.integratedmodelling.thinklab.interfaces.IKnowledgeRepository#importOntology(java.net.URL,
	 *      java.lang.String)
	 */
	public String importOntology(URL url, String name, boolean saveToRepository) throws ThinklabException {
		try {
			URI physicalURI = url.toURI();			
			String fname = null;
			
			/*
			 * if file, put that away
			 */
			URL uu = new URL(physicalURI.toString());
			if (uu.toString().startsWith("file:")) {
				fname = uu.getFile();
			}
			
			OWLOntology ontology = manager.loadOntology(physicalURI);
			name = registry.registerURI(name, ontology.getURI());
			Ontology onto = new Ontology(ontology, this);
			ontologies.put(name, onto);
			if (fname != null) {
				ontologyfn.put(name, fname);
			}
			registry.updateRegistry(manager, ontology);
			onto.initialize(name);
			
			/*
			 * at the very least it's a class reasoner
			 */
			if (classReasoner != null) {
				classReasoner.loadOntologies(Collections.singleton(onto.ont));
			}
			
			return name;
		} catch (Exception e) {
			throw new ThinklabException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.integratedmodelling.thinklab.interfaces.IKnowledgeRepository#createTemporaryOntology(java.lang.String)
	 */
	public IOntology createTemporaryOntology(String name) throws ThinklabException {
		if (!registry.containsConceptSpace(name)) {
			File f = new File(tempDirectory, name + ".owl");
			URI physicalURI = f.toURI();
			URI logicalURI = URI.create(DEFAULT_TEMP_URI + name + ".owl");
			SimpleURIMapper mapper = new SimpleURIMapper(logicalURI,
					physicalURI);
			manager.addURIMapper(mapper);
			try {
				OWLOntology ontology = manager.createOntology(logicalURI);
				name = registry.registerURI(name, logicalURI);
				Ontology onto = new Ontology(ontology, this);
				onto.isSystem = false;
				tempontologies.put(name, onto);
				// FIXME check -- 
				// ontologies.put(name, onto);
				// registry.updateRegistry(manager, ontology);
				onto.initialize(name);
				return onto;
			} catch (OWLOntologyCreationException e) {
				throw new ThinklabInternalErrorException(e);
			}

		}
		return null;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.integratedmodelling.thinklab.interfaces.IKnowledgeRepository#refreshOntology(java.net.URL,
	 *      java.lang.String)
	 */
	public String refreshOntology(URL url, String cs, boolean saveToRepository) throws ThinklabException {

		if (registry.containsConceptSpace(cs)) {
			try {
				URI physicalURI = url.toURI();
				URI logicalURI = registry.getURI(cs);
				SimpleURIMapper mapper = new SimpleURIMapper(logicalURI,
						physicalURI);
				manager.addURIMapper(mapper);
				manager.reloadOntology(logicalURI);
			} catch (Exception e) {
				throw new ThinklabException(e);
			}
		}
		return importOntology(url, cs, saveToRepository);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.integratedmodelling.thinklab.interfaces.IKnowledgeRepository#exportOntologyByName(java.io.OutputStream,
	 *      java.lang.String)
	 */
	public void exportOntologyByName(OutputStream writer, String oname)
			throws ThinklabIOException {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.integratedmodelling.thinklab.interfaces.IKnowledgeRepository#exportOntologyByName(java.net.URI,
	 *      java.lang.String)
	 */
	public void exportOntologyByName(URI fileuri, String oname)
			throws ThinklabIOException {
		Ontology onto = (Ontology) ontologies.get(oname);
		File outfile = new File(fileuri);
//		onto.

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.integratedmodelling.thinklab.interfaces.IKnowledgeRepository#getOntologyLastModifiedDate(java.lang.String)
	 */
	public long getOntologyTimestamp(String ontName)
			throws ThinklabResourceNotFoundException {
		// TODO Auto-generated method stub
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.integratedmodelling.thinklab.interfaces.IKnowledgeRepository#getRootConceptType()
	 */
	public IConcept getRootConcept() {
		if (rootConcept == null) {
			rootConcept = new Concept(manager.getOWLDataFactory().getOWLClass(URI.create(Namespaces.OWL + "Thing")));
		}
		return rootConcept;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.integratedmodelling.thinklab.interfaces.IKnowledgeRepository#getURI()
	 */
	public String getURI() {
		return "kr://knowledgerepository";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.integratedmodelling.thinklab.interfaces.IKnowledgeRepository#releaseAllOntologies()
	 */
	public void releaseAllOntologies() {
		for (String onto : ontologies.keySet()) {
			releaseOntology(onto);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.integratedmodelling.thinklab.interfaces.IKnowledgeRepository#releaseOntology(java.lang.String)
	 */
	public void releaseOntology(String cs) {
		ontologies.remove(cs);
		manager.removeOntology(registry.getURI(cs));
		registry.removeConceptSpace(cs);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.integratedmodelling.thinklab.interfaces.IKnowledgeRepository#requireOntology(java.lang.String)
	 */
	public IOntology requireOntology(String ontName)
			throws ThinklabResourceNotFoundException {
		
		IOntology ret = null;
		
		if (ontologies.containsKey(ontName))
			ret = ontologies.get(ontName);
		if (ret == null)
			ret = tempontologies.get(ontName);
		if (ret == null)
			throw new ThinklabResourceNotFoundException("Ontology " + ontName
					+ " does not exist");
		return ret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.integratedmodelling.thinklab.interfaces.IKnowledgeRepository#retrieveAllOntologies()
	 */
	public Collection<IOntology> getOntologies() {
		return ontologies.values();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.integratedmodelling.thinklab.interfaces.IKnowledgeRepository#retrieveOntology(java.lang.String)
	 */
	public IOntology retrieveOntology(String ontName) {
		
		IOntology ret = null;
		
		if (ontologies.containsKey(ontName))
			ret = ontologies.get(ontName);
		if (ret == null)
			ret = tempontologies.get(ontName);
		
		return ret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.integratedmodelling.thinklab.interfaces.IKnowledgeRepository#connectReasoner(java.net.URL)
	 */
	public void connectReasoner() throws ThinklabException {

		URL reasonerURL = null;
		String reasonerClass = null /*"org.mindswap.pellet.owlapi.Reasoner"*/;
		
		if (Thinklab.get().getProperties().containsKey("thinklab.reasoner.url")) {
			try {
				reasonerURL = new URL(Thinklab.get().getProperty("thinklab.reasoner.url").toString());
			} catch (MalformedURLException e) {
				throw new ThinklabConfigurationException(e);
			}
		} else if (Thinklab.get().getProperties().containsKey("thinklab.reasoner.class")) {
			reasonerClass = Thinklab.get().getProperty("thinklab.reasoner.class").toString();
		}

		try {
			
			OWLReasoner reasoner = null;
			
			if (reasonerURL != null) {

				 reasoner = new DIGReasoner(manager);
				((DIGReasoner) reasoner).getReasoner().setReasonerURL(
						reasonerURL);
			} else if (reasonerClass != null) {
				
				try {
					Class<?> rClass = Class.forName(reasonerClass);
					Constructor<OWLReasoner> con = (Constructor<OWLReasoner>) rClass.getConstructor(OWLOntologyManager.class);
					reasoner = con.newInstance(manager);
				} catch (Exception e) {
					Thinklab.get().logger().error(
							"cannot instantiate reasoner from class " + reasonerClass + 
							"; defaulting to no reasoner",
							e);
				}
				
			}
			if (reasoner != null) {

				String capabilities = "";
			
				if (reasoner instanceof OWLClassReasoner) {
					classReasoner = reasoner;
					capabilities += "class ";
				}
				if (reasoner instanceof OWLIndividualReasoner) {
					instanceReasoner = reasoner;
					capabilities += "individual ";
				}
				if (reasoner instanceof OWLPropertyReasoner) {
					propertyReasoner = reasoner;
					capabilities += "property ";
				}
				if (reasoner instanceof OWLConsistencyChecker) {
					consistencyReasoner = reasoner;
					capabilities += "consistency ";
				}
				
				Thinklab.get().logger().info(
						"created reasoner: " + 
						(reasonerURL == null ? "default" : "DIG@"+reasonerURL) + 
						": capabilities = {" + capabilities + "}");

				// This is the real question on which ontologies are we reasoning?
				// Probably the reasoning methods should be transferred to the
				// Session
				// and have session-based reasoning, which will be internal of the
				// session...
				Set<OWLOntology> importsClosure = manager.getOntologies();
				classReasoner.loadOntologies(importsClosure);
				DLExpressivityChecker checker = new DLExpressivityChecker(importsClosure);
				Thinklab.get().logger().info("Expressivity: " + checker.getDescriptionLogicName());

			} else {
				Thinklab.get().logger().info("not using a reasoner");
			}

		} catch (OWLException e) {
			throw new ThinklabIOException(e);
		}

	}
	
//	protected synchronized IKnowledge resolveURI(URI uri) {
//		IKnowledge result = null;
//		Iterator<IOntology> ontos = retrieveAllOntologies().iterator();	 
//		while(result==null && ontos.hasNext()){
//			IOntology ont = ontos.next();
//			if( ont instanceof Ontology){
//				result = ((Ontology) ont).resolveURI(uri);
//			}
//		}
//		return result;
//	}
	
	protected synchronized IKnowledge resolveURI(URI uri) {
		IKnowledge result = null;
		for (IOntology o : ontologies.values())
			if ( (result = ((Ontology)o).resolveURI(uri)) != null)
				break;
		return result;
	}

	@Override
	public IConcept getNothingType() {
		
		if (noConcept == null) {
			noConcept = new Concept(manager.getOWLDataFactory().getOWLClass(URI.create(Namespaces.OWL + "Nothing")));
		}
		return noConcept;
	}

	@Override
	public List<IConcept> getRootConcepts() {
		
		ArrayList<IConcept> ret = new ArrayList<IConcept>();
		for (IOntology onto : ontologies.values()) {
			for (IConcept c : onto.getConcepts()){
				Collection<IConcept> pp = c.getParents();
				if (pp.size() == 0 || (pp.size() == 1 && pp.iterator().next().is(rootConcept)))
					ret.add(c);
			}
		}
		return ret;
	}

	@Override
	public List<IConcept> getConcepts() {
		
		ArrayList<IConcept> ret = new ArrayList<IConcept>();
		for (IOntology onto : ontologies.values()) {
			for (IConcept c : onto.getConcepts()){
				ret.add(c);
			}
		}
		return ret;
	}

	public File getFileForOntology(String argument) {
		String fname = ontologyfn.get(argument);
		if (fname != null) {
			return new File(fname);
		}
		return null;
	}

	@Override
	public IOntology createOntology(String name, String urlPrefix) throws ThinklabException {
		
		if (!registry.containsConceptSpace(name)) {
			File f = new File(tempDirectory, name + ".owl");
			URI physicalURI = f.toURI();
			URI logicalURI = URI.create(urlPrefix + "/" + name + ".owl");
			SimpleURIMapper mapper = new SimpleURIMapper(logicalURI,
					physicalURI);
			manager.addURIMapper(mapper);
			try {
				OWLOntology ontology = manager.createOntology(logicalURI);
				name = registry.registerURI(name, logicalURI);
				Ontology onto = new Ontology(ontology, this);
				onto.isSystem = false;
				tempontologies.put(name, onto);
				// FIXME check -- 
				// ontologies.put(name, onto);
				// registry.updateRegistry(manager, ontology);
				onto.initialize(name);
				return onto;
			} catch (OWLOntologyCreationException e) {
				throw new ThinklabInternalErrorException(e);
			}

		}
		return null;

	}
}
