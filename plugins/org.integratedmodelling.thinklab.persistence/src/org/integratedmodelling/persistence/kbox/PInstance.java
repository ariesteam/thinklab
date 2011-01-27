/**
 * PInstance.java
 * ----------------------------------------------------------------------------------
 * 
 * Copyright (C) 2008 www.integratedmodelling.org
 * Created: Jan 21, 2008
 *
 * ----------------------------------------------------------------------------------
 * This file is part of ThinklabPersistencePlugin.
 * 
 * ThinklabPersistencePlugin is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * ThinklabPersistencePlugin is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with the software; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 * ----------------------------------------------------------------------------------
 * 
 * @copyright 2008 www.integratedmodelling.org
 * @author    Ioannis N. Athanasiadis (ioannis@athanasiadis.info)
 * @date      Jan 21, 2008
 * @license   http://www.gnu.org/licenses/gpl.txt GNU General Public License v3
 * @link      http://www.integratedmodelling.org
 **/
package org.integratedmodelling.persistence.kbox;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.integratedmodelling.persistence.factory.CGUtils;
import org.integratedmodelling.thinklab.SemanticType;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabStorageException;
import org.integratedmodelling.thinklab.exception.ThinklabUnimplementedFeatureException;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstanceImplementation;
import org.integratedmodelling.thinklab.interfaces.knowledge.IKnowledge;
import org.integratedmodelling.thinklab.interfaces.knowledge.IOntology;
import org.integratedmodelling.thinklab.interfaces.knowledge.IProperty;
import org.integratedmodelling.thinklab.interfaces.knowledge.IRelationship;
import org.integratedmodelling.thinklab.interfaces.knowledge.IResource;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;
import org.integratedmodelling.thinklab.interfaces.query.IConformance;
import org.integratedmodelling.thinklab.literals.AlgorithmValue;
import org.integratedmodelling.utils.Polylist;

/**
 * The default Persistent Instance Storage. 
 * It wraps a Java Object annotated with ontologies as an IInstance.
 * Java Classes should be annotated with ConceptURI and Java Properties with the PropertyURI 
 * annotations.
 * 
 * A PInstance is defined with respect to a PersistentKBox that makes ontologies and 
 * Java Class impementations available.
 * In principle a PInstance is not persistent. This happens only when the Session 
 * in which it exists it is saved. 
 * 
 * 
 * @author Ioannis N. Athanasiadis
 * @since Apr.27, 2007
 *
 */
public class PInstance implements IInstance {
	
	private Class cls;
	private Object obj;
	private PersistentKBox kBox;
	
	Logger log  = Logger.getLogger("org.integratedmodelling.persistence.kbox.PInstance");
	
	public PInstance(IConcept concept, PersistentKBox kBox) throws ThinklabException {
		this.kBox = kBox;
		String classname = CGUtils.getJavaNameWithPath(concept);
		try {
			this.cls = kBox.PersistentClassLoader.loadClass(classname);
		} catch (ClassNotFoundException e) {
			throw new ThinklabStorageException("Class not found for resource:" + concept+". \n"+e);
		}
		try {
			this.obj = cls.newInstance();
		} catch (InstantiationException e) {
			throw new ThinklabStorageException("Cannot instantiate from resource:" + concept+". \n"+e);
		} catch (IllegalAccessException e) {
			throw new ThinklabStorageException("Illegal access of resource:" + concept+". \n"+e);
		}


	}
	public PInstance(String name, IConcept concept, PersistentKBox kBox) {
	
		Method getID = null;
		try {
			getID = cls.getMethod("setID", new Class[0]);
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			getID.invoke(obj, new Object[]{Long.getLong(name)});
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}


	public IInstance clone(IOntology session) throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	public IConcept getDirectType() {
		// TODO Auto-generated method stub
		return null;
	}

	public Collection<IInstance> getEquivalentInstances() {
		// TODO Auto-generated method stub
		return null;
	}

	public IInstanceImplementation getImplementation() throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isConformant(IInstance otherInstance,
			IConformance conformance) throws ThinklabException {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isValidated() {
		// TODO Auto-generated method stub
		return false;
	}

	public Polylist toList(String oref) throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	public void validate() throws ThinklabException {
		// TODO Auto-generated method stub

	}

	public void validate(boolean validateOWL) throws ThinklabException {
		// TODO Auto-generated method stub

	}

	public IValue get(String property) throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	public int getNumberOfRelationships(String property) throws ThinklabException {
		// TODO Auto-generated method stub
		return 0;
	}

	public Collection<IRelationship> getRelationships() throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	public Collection<IRelationship> getRelationships(String property)
			throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	public Collection<IRelationship> getRelationshipsTransitive(String property)
			throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	public IConcept getType() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean equals(SemanticType s) {
		// TODO Auto-generated method stub
		return false;
	}

	public String getLocalName() {
		// TODO Auto-generated method stub
		return null;
	}

	public SemanticType getSemanticType() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean is(IKnowledge concept) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean is(String conceptType) {
		// TODO Auto-generated method stub
		return false;
	}

	public void addDescription(String desc) {
		// TODO Auto-generated method stub

	}

	public void addDescription(String desc, String language) {
		// TODO Auto-generated method stub

	}

	public void addLabel(String desc) {
		// TODO Auto-generated method stub

	}

	public void addLabel(String desc, String language) {
		// TODO Auto-generated method stub

	}

	public boolean equals(String s) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean equals(IResource r) {
		// TODO Auto-generated method stub
		return false;
	}

	public String getConceptSpace() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getDescription(String languageCode) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getLabel() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getLabel(String languageCode) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getURI() {
		// TODO Auto-generated method stub
		return null;
	}

	public IValue execute(AlgorithmValue algorithm, ISession session)
			throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	public IValue execute(AlgorithmValue algorithm, ISession session,
			Map<String, IValue> arguments) throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	public void addClassificationRelationship(String p, IConcept cls) throws ThinklabException {
		// TODO Auto-generated method stub
		throw new ThinklabUnimplementedFeatureException();
		
	}
	public void addLiteralRelationship(IProperty p, Object literal) throws ThinklabException {
		// TODO Auto-generated method stub
		
	}
	public void addObjectRelationship(IProperty p, IInstance object) throws ThinklabException {
		// TODO Auto-generated method stub
		
	}
	public void addClassificationRelationship(IProperty p, IConcept cls) throws ThinklabException {
		// TODO Auto-generated method stub
		
	}
	public void addLiteralRelationship(String p, Object literal) throws ThinklabException {
		// TODO Auto-generated method stub
		
	}
	public void addObjectRelationship(String p, IInstance instance) throws ThinklabException {
		// TODO Auto-generated method stub
		
	}
	public Polylist toList(String oref, HashMap<String, String> refTable)
			throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}
	public void setImplementation(IInstanceImplementation second)
			throws ThinklabException {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void addObjectRelationship(IProperty p, URI externalObject)
			throws ThinklabException {
		// TODO Auto-generated method stub
		
	}
	@Override
	public IOntology getOntology() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void addAnnotation(String property, String value) {
		// TODO Auto-generated method stub
		
	}

}
