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
package org.integratedmodelling.thinklab.interfaces;

import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
import java.util.Collection;
import java.util.List;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.exceptions.ThinklabIOException;
import org.integratedmodelling.exceptions.ThinklabResourceNotFoundException;
import org.integratedmodelling.thinklab.api.knowledge.IConcept;
import org.integratedmodelling.thinklab.api.knowledge.IOntology;

/**
 * <p>A knowledge repository (KR) is a place to store knowledge, discretized into ontologies. The Knowledge Manager handles the 
 * inter-ontology connections. The knowledge repository should support multiple separate "knowledge spaces" that are selected
 * by the API/user and either implemented locally or synchronized with remote ones. This feature will be crucial to handle
 * shared and local knowledge efficiently, as well as to link local and remote KBs transparently, but will come later.</p>
 * 
 * <p>Implementations of the KR can be persistent or in-memory, but the KR is supposed to remember its state across invocations 
 * anyway. So if the ontologies are not stored permanently in a DB, the submitted ontologies should be saved and read again
 * at initialization.</p> 
 * 
 * <p>Ontologies in the KR are always referenced by a simple name, either supplied at import time or inferred from the 
 * original URL. This name must be unique and stored </p>
 * 
 * @author Ferdinando Villa, Ecoinformatics Collaboratory, UVM
 * @author Ioannis N. Athanasiadis, Dalle Molle Institute for Artificial Intelligence, USI/SUPSI
 */
public interface IKnowledgeRepository {

	public abstract void initialize() throws ThinklabException;

	/**
	 * import an ontology from URL with the passed short name. Name
	 * may be null; if so, assign a name from URL base name. 
	 * @param url the ontology URL, either local or web based.
	 * @param name the name by which we want the ontology to be known, or null if we don't care.
	 * @param saveToRepository pass true if you want the ontology to be automatically loaded at the next
	 *        instantiation of the repository.
	 * @return the ontology short name. If null has been passed for the name parameter, it's calculated from the URL
	 * basename. Otherwise it's the same passed. Names may not be duplicated - if generated, a unique one is forced.
	 * @exception ThinklabException if anything goes wrong
	 */
	public abstract String importOntology(URL url, String name, boolean saveToReposioty) throws ThinklabException;
	
	/**
	 * Supposed to check if the ontology we're trying to import is already in the repository, and
	 * only load it if it's not there or if there is an older version. Versioning is not formally
	 * defined at this stage, so it's likely to only translate to "load if not present" for the 
	 * time being.
	 * 
	 * @param url the ontology URL, either local or web based.
	 * @param name the name by which we want the ontology to be known, or null if we don't care.
	 * @param saveToRepository pass true if you want the ontology to be automatically loaded at the next
	 *        instantiation of the repository.
	 * @return the ID of the ontology if it has been loaded or refreshed, null otherwise
	 * @throws ThinklabException if anything goes wrong
	 */
	public abstract String refreshOntology(URL url, String name, boolean saveToRepository) throws ThinklabException;


    /**
     * The Knowledge Repository is supposed to know what concept is the mother of all concepts, and
     * return it on request.
     * @return the root concept, e.g. owl:Thing.
     */
    public abstract IConcept getRootConceptType();
	
	/**
	 * Write the ontology as OWL (or other language: not mandated) on the passed OutputStream 
	 * @param writer the OutputStream to write on
	 * @param oname the ontology we want written
	 * @throws ThinklabIOException if the ontology is not there.
	 */
	public abstract void exportOntologyByName(OutputStream writer, String oname)
			throws ThinklabIOException;
	
	/**
	 * Write the ontology as OWL (or other language: not mandated) on the passed File URI 
	 * @param fileuri the URI of the File to write
	 * @param oname the ontology we want written
	 * @throws ThinklabIOException if the ontology is not there, or if the file is not writable or missing.
	 */
	public void exportOntologyByName(URI fileuri, String oname) throws ThinklabIOException;
	
	/**
	 * Release the passed ontology and delete from repository. Nothing should happen if name is not found.
	 * @param s name of ontology to release.
	 */
	public abstract void releaseOntology(String s);

	/**
	 * Empty the repository.
	 */
	public abstract void releaseAllOntologies();
	
	/**
	 * retrieve the named ontology as an Ontology object.
	 * @param ontName the name of the ontology
	 * @return the Ontology object.
	 */
	public abstract IOntology retrieveOntology(String ontName);

    /**
     * retrieve the named ontology as an Ontology object. Ontology must be present or exception is thrown.
     * @param ontName the name of the ontology
     * @return the Ontology object.
     * @throws ThinklabResourceNotFoundException if the ontology is not there.
     */
    public abstract IOntology requireOntology(String ontName) throws ThinklabResourceNotFoundException;
    
    /**
     * retrieve a Collection of all ontologies in repository
     */
    public abstract Collection<IOntology> retrieveAllOntologies();
    
    /**
     * Get the last modification date of passed ontology.
     * @param ontName
     * @return
     * @throws ThinklabResourceNotFoundException 
     */
    public abstract long getOntologyLastModifiedDate(String ontName) throws ThinklabResourceNotFoundException;
    
    /**
     * Return a base URI to identify models when they are in the KR (not their own URI, but the URI of their
     * model in the KR).
     */
    public abstract String getURI();

	/**
	 * Create a new ontology with given ID, using defaults for the rest of the URI. Complain if
	 * the ontology exists.
	 * 
	 * @param id
	 * @return
	 */
	public abstract IOntology createOntology(String id, String ontologyPrefix) throws ThinklabException;

	/** 
     * Create an ontology with the specified name and make sure it does not end up in the permanent repository.
     * Throws an exception if name is there already.
     * @param string
     * @return
     */
	public abstract IOntology createTemporaryOntology(String string) throws ThinklabException;

	public abstract IConcept getNothingType();

	/**
	 * Return all concepts whose parent is the root concept (e.g. owl:Thing).
	 * 
	 * @return
	 */
	public abstract List<IConcept> getAllRootConcepts();

	/**
	 * 
	 */
	public abstract List<IConcept> getAllConcepts();


}