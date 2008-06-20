/**
 * Created on Feb 29, 2008 
 * By Ioannis N. Athanasiadis
 *
 * Copyright 2007 Dalle Molle Institute for Artificial Intelligence
 * 
 * Licensed under the GNU General Public License.
 *
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.gnu.org/licenses/gpl.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package org.integratedmodelling.thinklab.impl.owlapi;

import java.io.File;
import java.io.FilenameFilter;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.net.URI;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.apache.log4j.Logger;
import org.integratedmodelling.thinklab.configuration.LocalConfiguration;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabIOException;
import org.integratedmodelling.thinklab.exception.ThinklabResourceNotFoundException;
import org.integratedmodelling.thinklab.interfaces.IConcept;
import org.integratedmodelling.thinklab.interfaces.IKnowledge;
import org.integratedmodelling.thinklab.interfaces.IKnowledgeRepository;
import org.integratedmodelling.thinklab.interfaces.IOntology;
import org.integratedmodelling.utils.FileTypeFilter;
import org.mindswap.pellet.owlapi.Reasoner;
import org.semanticweb.owl.apibinding.OWLManager;
import org.semanticweb.owl.inference.OWLReasoner;
import org.semanticweb.owl.model.OWLDataFactory;
import org.semanticweb.owl.model.OWLException;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLOntologyCreationException;
import org.semanticweb.owl.model.OWLOntologyManager;
import org.semanticweb.owl.util.DLExpressivityChecker;
import org.semanticweb.owl.util.SimpleURIMapper;
import org.semanticweb.owl.vocab.Namespaces;

/**
 * @author Ioannis N. Athanasiadis
 * 
 */
public class FileKnowledgeRepository implements IKnowledgeRepository {
	public static final String DEFAULT_TEMP_URI = "http://www.integratedmodelling.org/temporary/";
	private static Logger log = Logger.getLogger(FileKnowledgeRepository.class);
	protected OWLOntologyManager manager;
	private File repositoryDirectory = null;
	private File backupDirectory;
	private File tempDirectory;
	protected HashMap<String, IOntology> ontologies = new HashMap<String, IOntology>();
	protected Registry registry;
	protected OWLReasoner reasoner;
	private IConcept rootConcept;
	protected static OWLDataFactory df;
	protected static FileKnowledgeRepository KR =null;

	/**
	 * Default constructor. To be followed by initialize().
	 * 
	 * @throws ThinklabIOException
	 */
	public FileKnowledgeRepository() throws ThinklabIOException {
		if (KR == null) {
			KR = this;
			repositoryDirectory = LocalConfiguration
					.getDataDirectory("ontology/repository");
			backupDirectory = LocalConfiguration
					.getDataDirectory("ontology/backup");
			tempDirectory = LocalConfiguration.getDataDirectory("ontology/tmp");
			manager = OWLManager.createOWLOntologyManager();
			registry = Registry.get();
			registry.registerURI("owl", URI.create("http://www.w3.org/2002/07/owl"));
			df = manager.getOWLDataFactory();
			rootConcept = getRootConceptType();
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.integratedmodelling.thinklab.interfaces.IKnowledgeRepository#initialize()
	 */
	public void initialize() throws ThinklabException {
		FilenameFilter owlfilter = new FileTypeFilter(
				FileTypeFilter.OWLFileType);
		for (File f : repositoryDirectory.listFiles(owlfilter)) {
			String nspace = null;
			try {
				URL url = f.toURL();
				// This is the filename without the .owl ending. It will be used
				// as the short namespace!
				nspace = f.getName().substring(0, f.getName().length() - 4);
				importOntology(url, nspace);
			} catch (Exception ex) {
				// log.warn("Cant load ontology for the file: " + f.getName()
				// + " " + ex.getMessage());
			}
		}
		if (LocalConfiguration.hasResource("thinklab.reasoner.url")) {
			// connectReasoner(LocalConfiguration.getResource("thinklab.reasoner.url"));
			// log.info(reasonerConnected()? "Connected to reasoner: "+
			// reasoner.getIdentity().getName() : "Reasoner not connected.");
		} else {
			// log.info("Reasoner support not configured");
		}
		
//		rootConcept
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.integratedmodelling.thinklab.interfaces.IKnowledgeRepository#importOntology(java.net.URL,
	 *      java.lang.String)
	 */
	public String importOntology(URL url, String name) throws ThinklabException {
		try {
			URI physicalURI = url.toURI();
			OWLOntology ontology = manager
					.loadOntologyFromPhysicalURI(physicalURI);
			name = registry.registerURI(name, ontology.getURI());
			Ontology onto = new Ontology(ontology, this);
			ontologies.put(name, onto);
			registry.updateRegistry(manager, ontology);
			onto.initialize(name);
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
	public IOntology createTemporaryOntology(String name)
			throws ThinklabException {
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
				ontologies.put(name, onto);
				registry.updateRegistry(manager, ontology);
				onto.initialize(name);
				return onto;
			} catch (OWLOntologyCreationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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
	public String refreshOntology(URL url, String cs) throws ThinklabException {
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
		return importOntology(url, cs);
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
	public long getOntologyLastModifiedDate(String ontName)
			throws ThinklabResourceNotFoundException {
		// TODO Auto-generated method stub
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.integratedmodelling.thinklab.interfaces.IKnowledgeRepository#getRootConceptType()
	 */
	public IConcept getRootConceptType() {
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
		if (!ontologies.containsKey(ontName))
			throw new ThinklabResourceNotFoundException("Ontology " + ontName
					+ " does not exist");
		return ontologies.get(ontName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.integratedmodelling.thinklab.interfaces.IKnowledgeRepository#retrieveAllOntologies()
	 */
	public Collection<IOntology> retrieveAllOntologies() {
		return ontologies.values();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.integratedmodelling.thinklab.interfaces.IKnowledgeRepository#retrieveOntology(java.lang.String)
	 */
	public IOntology retrieveOntology(String ontName) {
		return ontologies.get(ontName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.integratedmodelling.thinklab.interfaces.IKnowledgeRepository#reasonerConnected()
	 */
	public boolean reasonerConnected() {
		return (reasoner!=null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.integratedmodelling.thinklab.interfaces.IKnowledgeRepository#classifyTaxonomy()
	 */
	public void classifyTaxonomy() {
		try {
			reasoner.classify();
		} catch (OWLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
	
        
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.integratedmodelling.thinklab.interfaces.IKnowledgeRepository#connectReasoner(java.net.URL)
	 */
	public void connectReasoner(URL reasonerURL) {
		// This is the real question on which ontologies are we reasoning?
		// Probably the reasoning methods should be transferred to the Session
		// and have session-based reasoning, which will be internal of the session...
		Set<OWLOntology> importsClosure = manager.getOntologies();     

//		String reasonerClassName = "org.mindswap.pellet.owlapi.Reasoner";
//        Class reasonerClass;
//		try {
//			reasonerClass = Class.forName(reasonerClassName);
//			Constructor<OWLReasoner> con = reasonerClass.getConstructor(OWLOntologyManager.class);
//	        this.reasoner =  con.newInstance(manager);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		
		this.reasoner = new Reasoner(this.manager);

//		reasoner.setAutoClassify(true);
		try {
			reasoner.loadOntologies(importsClosure);
		} catch (OWLException e) {
			e.printStackTrace();
		}
		
		 DLExpressivityChecker checker = new DLExpressivityChecker(importsClosure);
         System.out.println("Expressivity: " + checker.getDescriptionLogicName());
	}
	
	protected IKnowledge resolveURI(URI uri) {
		IKnowledge result = null;
		Iterator<IOntology> ontos = retrieveAllOntologies().iterator();	 
		while(result==null && ontos.hasNext()){
			IOntology ont = ontos.next();
			if( ont instanceof Ontology){
				result = ((Ontology) ont).resolveURI(uri);
			}
		}
		return result;
	}
	
	

}
