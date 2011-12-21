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
package org.integratedmodelling.persistence.factory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.text.DateFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.integratedmodelling.persistence.PersistencePlugin;
import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabIOException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.knowledge.IProperty;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;


/**
 * The main template that generates the various textual transformations of an IConcept
 * 
 * @author Ioannis N. Athanasiadis
 * @since Feb 5, 2007
 * @version 0.2
 */
public class JavaBeanTemplate implements Serializable{
	private static final long serialVersionUID = -1599045636697801781L;

	String name;
	String packagename;
	//File file;
	IConcept concept;
	Map<String,Attribute> attributes = new HashMap<String,Attribute>();
	boolean factoryroot=false;
	boolean abstractdeco=false;
	Logger log = Logger.getLogger("org.integratedmodelling.persistence.factory.JavaBeanTemplate");
	
	public JavaBeanTemplate(IConcept concept) throws ThinklabException{
		this.name = CGUtils.getJavaName(concept);
		this.packagename = CGUtils.getJavaPackageName(concept);
		//this.file = CGUtils.getJavaClassFile(concept);
		this.concept = concept;
		this.factoryroot = isFactoryRoot(concept);
		this.abstractdeco = isAbstractDecorated(concept);
		assignAttributes();
	}
	
	static boolean  isFactoryRoot(IConcept c){
		IValue flag=null;
		try {
			flag = c.get(PersistencePlugin.FACTORY_PERSISTENCY_PROPERTY.toString());
		} catch (ThinklabException e) {
			return false;
		}
		if(flag!=null && flag.toString()=="true")
			return  true;
		return false;
	}
	static boolean  isAbstractDecorated(IConcept c){
		IValue flag=null;
		try {
			flag = c.get(PersistencePlugin.ABSTRACT_PERSISTENCY_PROPERTY.toString());
		} catch (ThinklabException e) {
			return false;
		}
		if(flag!=null && flag.toString()=="true")
			return  true;
		return false;
	}

	private void assignAttributes() {
		
		//TODO: Sort attributes alphabetically :(
		for(IProperty p:concept.getProperties()){
			Attribute a = new Attribute(p);
			attributes.put(p.getSemanticType().toString(),a);
		}
		if(!concept.getLabel("en").equalsIgnoreCase("")) attributes.put("1",new Attribute("Label_en"));
		if(!concept.getLabel("gms").equalsIgnoreCase("")) attributes.put("2",new Attribute("Label_gms"));
		if(!concept.getLabel("aps").equalsIgnoreCase("")) attributes.put("3",new Attribute("Label_aps"));
	}


	public String getHJBSource(){  
		String s = "";
		s += "package " + packagename + "; \n";
		s += "\n";
		s += "import java.io.Serializable;\n";
		s += "import org.integratedmodelling.persistence.annotations.ConceptURI;\n";
		s += "import org.integratedmodelling.persistence.annotations.PropertyURI;\n";

		Set<String> imports = new HashSet<String>();
		for(Attribute a : attributes.values())
			imports.addAll(a.toJavaImportDeclaration());
		for(String decl:imports)
			s+= decl + "\n";
		
		s += "\n";
		s += "/**\n";
		s += " * \n";
		s += " * Generated code for concept "+ concept.getSemanticType() +" \n";
		s += " * originally from "+ concept.getURI() +" \n";
		s += " *" + concept.getDescription() + "\n";
		s += " * \n";
		s += " * @author Thinklab Persistence Plugin\n";
		s += " * @since " + DateFormat.getDateInstance().format(new Date());
		s += "**/\n";
		s += "\n";
		s += "@ConceptURI(\""+concept.getURI()+"\")\n";
		s +=  "public class " + name;
		
//		Fixed:  Deal with inheritance!!!
//      Inheritance is implemented through interfaces - why not!
//		
		Collection<IConcept> parents = concept.getParents();
		parents.remove(KnowledgeManager.Thing());
		String extend = "";
		for(IConcept parent:parents)
			extend += ", " +CGUtils.getJavaInterfaceNameWithPath(parent);	
		s +=" implements Serializable" + extend + " {\n";
		
//		Without inheritance
//		s +=" implements Serializable {\n";
//		s += "\n";
		
		// print the attributes
		s+="\n private Long id;";
		for(Attribute a: attributes.values()){
			s +=" "+  a.toJavaAttributeDecleration();
		}
		s+="\n";
		// add default constructor
		s+="public " + name + "() {} \n"; 

		// add getters and setters
		s+="\npublic Long getId() { \n return id; \n }\n@SuppressWarnings(\"unused\")\npublic void setId(Long id) {\n	this.id = id;\n } \n";
		for(Attribute a: attributes.values()){
			s +=" "+ a.toJavaSetterAndGetter();
		}
		
		s +="\npublic int hashCode(){\nint hash = 0;\n  hash += (this.id != null ? this.id.hashCode() : 0);\n  return hash;\n} \n";
		s +="\npublic boolean equals(Object object){\n"  + //if (!(object instanceof "+name+")) {\n    return false;\n  } \n  "+name+" other = ("+name+")object;\n  if(this.id != other.id && (this.id == null || !this.id.equals(other.id))) return false;\n  return true;\n}\n";
		                                                "if(this.id == null ) return false;\n  if (!(object instanceof "+name+")) return false;\n  "+name+" other = ("+name+") object;\n  if(this.id.equals(other.id)) return true;\n  return false;\n}\n"; 
		s +="\npublic String toString(){\n  return \""+packagename+"."+name +"[id=\" + id + \"]\";\n}\n";

		s +=  "}\n";

		return s;	
	}


	public String getJavaInterfaceSource(){  
		String s = "";
		s += "package " + packagename + "; \n";
		s += "\n";
		s += "import java.io.Serializable;\n";
		s += "import org.integratedmodelling.persistence.annotations.ConceptURI;\n";
		s += "import org.integratedmodelling.persistence.annotations.PropertyURI;\n";

		Set<String> imports = new HashSet<String>();
		for(Attribute a : attributes.values())
			imports.addAll(a.toJavaImportDeclaration());
		for(String decl:imports)
			s+= decl + "\n";
		
		s += "\n";
		s += "/**\n";
		s += " * \n";
		s += " * Generated code for concept "+ concept.getSemanticType() +" \n";
		s += " * originally from "+ concept.getURI() +" \n";
		s += " *" + concept.getDescription() + "\n";
		s += " * \n";
		s += " * @author Thinklab Persistence Plugin\n";
		s += " * @since " + DateFormat.getDateInstance().format(new Date());
		s += "**/\n";
		s += "\n";
		s += "@ConceptURI(\""+concept.getURI()+"\")\n";
		s +=  "public interface " + CGUtils.getJavaInterfaceName(concept);
		
//		Fixed:  Dealing with inheritance through interfaces		
		Collection<IConcept> parents = concept.getParents();
		parents.remove(KnowledgeManager.Thing());
		String extend = "";
		for(IConcept parent:parents)
			extend += " " +CGUtils.getJavaInterfaceNameWithPath(parent)+", ";
		
		s +=" extends " + extend +" Serializable";
		s +=" {\n";
		s += "\n";
		

		// add getters and setters
		s+="\n public Long getId();\n";
		s+="\n public void setId(Long id);\n";
		
		for(Attribute a: attributes.values()){
			s +=" "+ a.toJavaSetterAndGetterMethodsOnly();
		}
		
		s +=  "}\n";

		return s;	
	}

	
	
	
	private String controllerInterfaceSource(String kind){
		String s = "";
		s += "package " + packagename + "; \n";
		s += "\n";
		s += "import javax.ejb."+kind+";\n";
		s += "import java.io.Serializable;\n";
		s += "\n";
		s += "/**\n";
		s += " * Generated interface for  class "+ concept.getSemanticType() +"\n";
		s += " *" + concept.getDescription() + "\n";
		s += " * @author Thinklab Persistence Plugin\n";
		s += "**/\n";
		s += "\n";
		s += "@"+kind+" ";
		s +=  "public interface " + name+"Controller"+kind;
		
		s += " {\n";
		//CREATE
		s += "public " + name + " create"+name+"();\n";
		//RETRIEVE
		s += "public "+name+" retrieve"+name+"(Long id);\n";
		//UPDATE
		s += "public void update"+name+"("+name + " object);\n";
		//DELETE
		s += "public void delete"+name+"("+name + " object);\n";
		s +=  "}\n";
		return s;	
	}
	public String getHJBControllerRemoteSource(){
		return controllerInterfaceSource("Remote");
	}

	public String getHJBControllerLocalSource(){
		return controllerInterfaceSource("Local");
	}
	
	
	public String getHJBControllerBeanSource(){
		String s = "";
		s += "package " + packagename + "; \n";
		s += "\n";
    	s += "import javax.ejb.Stateless;\n";
    	s += "import javax.persistence.EntityManager;\n";    	
    	s += "import javax.persistence.PersistenceContext;\n";
 		s += "\n";
		s += "/**\n";
		s += " * Generated class for  concept "+ concept.getSemanticType() +"\n";
		s += " *" + concept.getDescription() + "\n";
		s += " * @author Thinklab Persistence Plugin\n";
		s += "**/\n";
		s += "\n";
		s += "@Stateless ";
		s +=  "public class " + name+"ControllerBean";
		s += " implements "+name+"ControllerRemote, "+name+"ControllerLocal";
		s += " {\n";
		s += "@PersistenceContext EntityManager entityManager;\n";
		//CREATE
		s += "public " + name + " create"+name+"(){\n";
		s += "  "+ name + " object = new "+name+"();\n";
		s += "  entityManager.persist(object);\n";
		s += "  return object;\n";
		s += "}\n";
		//RETRIEVE
		s += "public "+name+" retrieve"+name+"(Long id){\n";
		s += "  return entityManager.find("+name+".class,id);\n";
		s += "}\n";
		
		//UPDATE
		s += "public void update"+name+"("+name + " object){\n";
		s += "  entityManager.merge(object);\n";
		s += "}\n";
		
		//DELETE
		s += "public void delete"+name+"("+name + " object){\n";
		s += "  entityManager.remove(object);\n";
		s += "}\n";
		
		
		s +=  "}\n";
		return s;	
	}
	


	public Document getHibernateMapping() {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		Document document = null;
		
		if(concept.getLocalName().equalsIgnoreCase("CropGroup")){
			System.out.println("Here I am at CG");
		}
		
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();

			DOMImplementation hbmDomi = builder.getDOMImplementation();
			DocumentType hbmDocType = hbmDomi.createDocumentType("hibernate-mapping", "-//Hibernate/Hibernate Mapping DTD//EN", "http://hibernate.sourceforge.net/hibernate-mapping-3.0.dtd");

			document = hbmDomi.createDocument("", "hibernate-mapping", hbmDocType);
			Element root = document.getDocumentElement();
			root.setAttribute("default-cascade","all");
			// first create the class element that contains java class- table mapping
			// TODO: Create Inheritance
			// For this version we support inheritance only for "factories"
			// using the table per concrete class pattern
			
			IConcept fatherclass = null;
			for(IConcept c: concept.getAllParents()){
				if(!c.toString().equalsIgnoreCase(concept.toString())){
					IValue flag;
					try {
						flag = c.get(PersistencePlugin.FACTORY_PERSISTENCY_PROPERTY.toString());
						if(flag!=null && flag.toString()=="true")
							fatherclass=c;
					} catch (ThinklabException e) {
						log.info(e);
					}
				}
			}
			
			//Generate the class element and the id generators
			Element classelem = null;
			if(fatherclass== null && !factoryroot ){ // a normal class 
				classelem = document.createElement("class");
				//        append the node to the tree
				root.appendChild(classelem);
				classelem.setAttribute("name", CGUtils.getJavaNameWithPath(concept));
				
				classelem.setAttribute("table", CGUtils.getSQLName(concept));
	
		//add comment 
				Element comment = document.createElement("comment");
				classelem.appendChild(comment);
				comment.setTextContent(concept.getDescription()+concept.getURI());
				
				// add default id node
				Element idelem = document.createElement("id");
				classelem.appendChild(idelem);
				idelem.setAttribute("name", "id");
				idelem.setAttribute("column", "ID");
				Element genelem = document.createElement("generator");
				idelem.appendChild(genelem);
				genelem.setAttribute("class", "increment");
		}
//			else if(fatherclass!=null && factoryroot) // I am a messed-up factory root...
//				return document;
			
			 
			else if(fatherclass== null && factoryroot){ // I am a real factory root
				classelem = document.createElement("class");
				//        append the node to the tree
				root.appendChild(classelem);
				classelem.setAttribute("name", CGUtils.getJavaInterfaceNameWithPath(concept));
				classelem.setAttribute("table", CGUtils.getSQLName(concept));
				classelem.setAttribute("abstract","true");
				classelem.setAttribute("lazy","false");

		//add comment 
				Element comment = document.createElement("comment");
				classelem.appendChild(comment);
				comment.setTextContent(concept.getDescription()+concept.getURI());

				// add default id node
				Element idelem = document.createElement("id");
				classelem.appendChild(idelem);
				idelem.setAttribute("name", "id");
				idelem.setAttribute("column", "ID");
				Element genelem = document.createElement("generator");
				idelem.appendChild(genelem);
				genelem.setAttribute("class", "increment");
			}
			else { // a factory child implementation 
				// Here I set the "extends" attribute to be the factory root. 
				// It should be ok, as we duplicate all properties in all children
				
				classelem = document.createElement("union-subclass");
				//        append the node to the tree
				root.appendChild(classelem);
				if(abstractdeco==true){
					classelem.setAttribute("name", CGUtils.getJavaInterfaceNameWithPath(concept));
					classelem.setAttribute("lazy","false");
				} else{
				    classelem.setAttribute("name", CGUtils.getJavaNameWithPath(concept));
				}
				classelem.setAttribute("table", CGUtils.getSQLName(concept));
				// thats my dad!
				classelem.setAttribute("extends", CGUtils.getJavaInterfaceNameWithPath(fatherclass));
				
		//add comment 
				Element comment = document.createElement("comment");
				classelem.appendChild(comment);
				comment.setTextContent(concept.getDescription() + " See also: " + concept.getURI());

				// add default id node
//				Element idelem = document.createElement("id");
//				classelem.appendChild(idelem);
//				idelem.setAttribute("name", "id");
//				idelem.setAttribute("column", "ID");
//				Element genelem = document.createElement("generator");
//				idelem.appendChild(genelem);
//				genelem.setAttribute("class", "increment");
			
			}
			
			Map<String, Attribute> myattributes = new HashMap<String, Attribute>();
			myattributes.putAll(attributes);
			Map<String, Attribute> sortedattributes = sortMapByKey(myattributes);
			if(fatherclass!= null && !factoryroot){
				try {
					Map<String,Attribute> fatherattributes = (new JavaBeanTemplate(fatherclass)).attributes;
		
					for(String key : fatherattributes.keySet()){
						sortedattributes.remove(key);
						
					}
				} catch (ThinklabException e) {
					log.info(e);
				}
				
			}

			for(Attribute a: sortedattributes.values()){
				// Attributes can be of the following cases:
				// I. Literals
				// II. Objects

				// Case I literal relationships or annotations
				// In this case either is a single-valued characteristic of the object (Case Ia)
				// or is there is a  several valued characteristic of the object (Case Ib)
				if(a.annotation||a.prop.isLiteralProperty()){
					if(a.functional){
						Element property = document.createElement("property");
						classelem.appendChild(property);
						property.setAttribute("name", a.name);
						property.setAttribute("column", a.sqlname);
						if(a.prop.getAllParents().contains(PersistencePlugin.LONG_TEXT_PROPERTY))
							property.setAttribute("type", "text");
				//add comment 
//						Element comment = document.createElement("comment");
//						property.appendChild(comment);
//						comment.setTextContent(a.prop.getDescription());
					} else{ //Case Ib
						Element set = document.createElement("set");
						classelem.appendChild(set);
						set.setAttribute("name", a.name);
						set.setAttribute("table", CGUtils.getSQLName(concept)+"_"+a.sqlname);
				//add comment 
//						Element comment = document.createElement("comment");
//						set.appendChild(comment);
//						comment.setTextContent(a.prop.getDescription());
						Element key = document.createElement("key");
						set.appendChild(key);
						key.setAttribute("column","id");
						Element elem = document.createElement("element");
						set.appendChild(elem);
						elem.setAttribute("column", a.sqlname);
						elem.setAttribute("type", a.sqltype);
						if(a.prop.getAllParents().contains(PersistencePlugin.LONG_TEXT_PROPERTY))
							elem.setAttribute("type", "text");
					}
				} else{// It is an object
					// Case II object relationships
					// Case IIa   : Functional relationship
					//		   i  : not inverse (one-to-one, unidirectional)
					//         ii : inverse of a functional property (one-to-one, biderectional)
					//         iii: inverse of non functional property (one-to-many inverse of IIbii)
					// Case IIb non-functional relatioship
					//		   i  : not inverse (many-to-many unidirectional)
					//         ii : inverse of a functional property (many-to-one inverse of IIaiii)
					//         iii: inverse of non functional property (many-to-many bidirectional)      	  
					if (a.functional) {
						if (!a.inverse) { 
							// Case IIa i: not inverse (one-to-one, unidirectional)
							Element many = document.createElement("many-to-one");
								classelem.appendChild(many);
								many.setAttribute("name", a.name);
								many.setAttribute("column", a.sqlname);
						//		many.setAttribute("unique", "true");
					// TODO: Restore the not-null argument
					//			many.setAttribute("not-null", "true");
								//many.setAttribute("cascade", "all");
								
						//add comment 
//								Element comment = document.createElement("comment");
//								many.appendChild(comment);
//								comment.setTextContent(a.prop.getDescription());
								

						} else // is inverse
							if (a.type.getMaxCardinality(a.inverseProp) == 1) { 
								// Case IIa ii: inverse of a functional property (one-to-one, biderectional)			
								if (!PersistentStorageFactory.processedConcepts.contains(a.type.getSemanticType().toString())) {
									Element many = document.createElement("many-to-one");
										classelem.appendChild(many);
										many.setAttribute("name", a.name);
										many.setAttribute("column", a.sqlname);
						//				many.setAttribute("unique", "true");
						//TODO: Restore the not null argunent
						//				many.setAttribute("not-null", "true");
										many.setAttribute("cascade", "all");
						
						//add comment 
//										Element comment = document.createElement("comment");
//										many.appendChild(comment);
//										comment.setTextContent(a.prop.getDescription());
								} else {
									Element one = document.createElement("one-to-one");
										classelem.appendChild(one);
										one.setAttribute("name", a.name);
										one.setAttribute("property-ref", (new Attribute(a.inverseProp).name));
										one.setAttribute("cascade", "all");
						//add comment 
//										Element comment = document.createElement("comment");
//										one.appendChild(comment);
//										comment.setTextContent(a.prop.getDescription());
								}
							} else {
								// Case IIa iii: inverse of non functional property (one-to-many) // functional side
								Element many = document.createElement("many-to-one");
									classelem.appendChild(many);
									many.setAttribute("name", a.name);
									many.setAttribute("column", a.name);
									
					//TODO: Restore the not-null argunent				
					//				many.setAttribute("not-null", "true");
									many.setAttribute("cascade", "all");
 				//add comment 
//									Element comment = document.createElement("comment");
//									many.appendChild(comment);
//									comment.setTextContent(a.prop.getDescription());
								
//		DOES NOT WORK WITH JOIN TABLES!						
//								
//							 Element join = document.createElement("join");
//								 classelem.appendChild(join);
//								 join.setAttribute("table", (new Attribute(a.inverseProp).name)+"_"+a.name);
//								 join.setAttribute("inverse", "true");
//								 join.setAttribute("optional", "true");
//							 Element key = document.createElement("key");
//								join.appendChild(key);
//								key.setAttribute("column", concept.getLocalName()+"_id");
//							 Element many = document.createElement("many-to-one");
//								join.appendChild(many);
//								many.setAttribute("name", a.name);
//								many.setAttribute("not-null", "true");
//								many.setAttribute("column", (new Attribute(a.inverseProp).name));
//								many.setAttribute("cascade", "all");
							}
					} else { // not functional
						if (!a.inverse) {
							// many - to - many unidirectional
						Element set = document.createElement("set");
							classelem.appendChild(set);
							set.setAttribute("name", a.name);
							set.setAttribute("table", CGUtils.getSQLName(concept)+a.name);
					//add comment 
//							Element comment = document.createElement("comment");
//							set.appendChild(comment);
//							comment.setTextContent(a.prop.getDescription());
						Element key = document.createElement("key");
							set.appendChild(key);
							key.setAttribute("column", CGUtils.getSQLName(concept)+"_id");
						Element many = document.createElement("many-to-many");
							set.appendChild(many);
							if(isFactoryRoot(a.type)||isAbstractDecorated(a.type))
								many.setAttribute("class", CGUtils.getJavaInterfaceNameWithPath(a.type));
							else
								many.setAttribute("class", CGUtils.getJavaNameWithPath(a.type));
							many.setAttribute("column", CGUtils.getSQLName(a.type)+"_id");


							 } else // not inverse
								 
							if (a.type.getMaxCardinality(a.prop) == 1) {
//								 this is the inverse of Case IIa iii
								Element set = document.createElement("set");
									classelem.appendChild(set);
									set.setAttribute("name", a.name);
									set.setAttribute("inverse", "true");
						//add comment 
//									Element comment = document.createElement("comment");
//									set.appendChild(comment);
//									comment.setTextContent(a.prop.getDescription());
								Element key = document.createElement("key");
									set.appendChild(key);
									key.setAttribute("column", new Attribute(a.inverseProp).sqlname);
								Element many = document.createElement("one-to-many");
									set.appendChild(many);
									if(isFactoryRoot(a.type)||isAbstractDecorated(a.type))
										many.setAttribute("class", CGUtils.getJavaInterfaceNameWithPath(a.type));
									else
										many.setAttribute("class", CGUtils.getJavaNameWithPath(a.type));
								 
									
//								Element set = document.createElement("set");
//									classelem.appendChild(set);
//									set.setAttribute("name", a.name);
//									set.setAttribute("table", a.name+"_"+(new Attribute(a.inverseProp).name));
//								Element key = document.createElement("key");
//									set.appendChild(key);
//									key.setAttribute("column", concept.getLocalName()+"_id");
//								Element many = document.createElement("many-to-many");
//									set.appendChild(many);
//									many.setAttribute("column", a.type.getLocalName()+"_id");
//									many.setAttribute("unique", "true");
//									many.setAttribute("class", CodeGeneratorUtils.createJavaName(a.type.getURI()));
							 } else{
								 // finally the many-to-many bi-directional
								 if (!PersistentStorageFactory.processedConcepts.contains(a.type.getSemanticType().toString())){
									 Element set = document.createElement("set");
										 classelem.appendChild(set);
										 set.setAttribute("name", a.name);
										 set.setAttribute("table", CGUtils.getSQLName(concept)+CGUtils.getSQLName(a.type)+CGUtils.getSQLName(a.prop));
				//add comment 
//											Element comment = document.createElement("comment");
//											set.appendChild(comment);
//											comment.setTextContent(a.prop.getDescription());
								    Element key = document.createElement("key");
										 set.appendChild(key);
										 key.setAttribute("column", CGUtils.getSQLName(concept)+"_id");
									 Element many = document.createElement("many-to-many");
										 set.appendChild(many);
										 if(isFactoryRoot(a.type)||isAbstractDecorated(a.type))
												many.setAttribute("class", CGUtils.getJavaInterfaceNameWithPath(a.type));
											else
												many.setAttribute("class", CGUtils.getJavaNameWithPath(a.type));
										 many.setAttribute("column", CGUtils.getSQLName(a.type)+"_id");
								 } else{
									 Element set = document.createElement("set");
										 classelem.appendChild(set);
										 set.setAttribute("name", a.name);
										 set.setAttribute("inverse", "true");
										 set.setAttribute("table", CGUtils.getSQLName(a.type)+CGUtils.getSQLName(concept)+CGUtils.getSQLName(a.inverseProp));
				//add comment 
//									Element comment = document.createElement("comment");
//									     set.appendChild(comment);
//										 comment.setTextContent(a.prop.getDescription());
										 
								     Element key = document.createElement("key");
										 set.appendChild(key);
										 key.setAttribute("column", CGUtils.getSQLName(concept)+"_id");
									 Element many = document.createElement("many-to-many");
										 set.appendChild(many);
										 if(isFactoryRoot(a.type)||isAbstractDecorated(a.type))
												many.setAttribute("class", CGUtils.getJavaInterfaceNameWithPath(a.type));
											else
												many.setAttribute("class", CGUtils.getJavaNameWithPath(a.type));
										 many.setAttribute("column", CGUtils.getSQLName(a.type)+"_id");
								 }
							 }
					}
				}

			}

		} catch (ParserConfigurationException pce) {
			// Parser with specified options can't be built
			pce.printStackTrace();
		} 
		return document;
	}

	public void storeHibernateSource() throws ThinklabException{
		Document dom = getHibernateMapping();
		try {
			OutputFormat format = new OutputFormat(dom);
			XMLSerializer output = new XMLSerializer(new FileOutputStream(CGUtils.getHibernateFile(concept)), format);
			output.serialize(dom);
			PersistentStorageFactory.processedConcepts.add(concept.getSemanticType().toString());
		}
		catch (IOException e) {
			throw new ThinklabIOException(e);
		}

	} 
	
	
	public void storeJavaInterfaceSource() throws ThinklabException{
			File f = CGUtils.getJavaInterfaceFile(concept);
			FileWriter fw;
			try {
				fw = new FileWriter(f);
				fw.write(getJavaInterfaceSource());
				fw.close();
			} catch (IOException e) {
				throw new ThinklabIOException("Can't write java interface source for "
						+ concept.getSemanticType() + " to file");
			}
		
	} 
	
	public void storeJavaSource() throws ThinklabException{
		storeJavaInterfaceSource();
		if (!(isFactoryRoot(concept)||isAbstractDecorated(concept))) {
			File f = CGUtils.getJavaClassFile(concept);

			FileWriter fw;
			try {
				fw = new FileWriter(f);
				fw.write(getHJBSource());
				fw.close();
			} catch (IOException e) {
				throw new ThinklabIOException("Can't write java source for "
						+ concept.getSemanticType() + " to file");
			}
		}
	} 
	
	public void storeHJBManager() throws ThinklabException{
		File f = CGUtils.getHJBManagerFile(concept);

		FileWriter fw;
		try {
			fw = new FileWriter(f);
			fw.write(getHJBControllerBeanSource());
			fw.close();
		} catch (IOException e) {
			throw new ThinklabIOException("Can't write HJB Manager source for " + concept.getSemanticType() + " to file");				
		}
	} 
	
	public void storeHJBManagerRemote() throws ThinklabException{
		File f = CGUtils.getHJBManagerRemoteFile(concept);

		FileWriter fw;
		try {
			fw = new FileWriter(f);
			fw.write(getHJBControllerRemoteSource());
			fw.close();
		} catch (IOException e) {
			throw new ThinklabIOException("Can't write HJB Manager Remote source for " + concept.getSemanticType() + " to file");				
		}
	} 
	
	public void storeHJBManagerLocal() throws ThinklabException{
		File f = CGUtils.getHJBManagerLocalFile(concept);

		FileWriter fw;
		try {
			fw = new FileWriter(f);
			fw.write(getHJBControllerLocalSource());
			fw.close();
		} catch (IOException e) {
			throw new ThinklabIOException("Can't write HJB Manager Remote source for " + concept.getSemanticType() + " to file");				
		}
	} 
	


	private class Attribute{
		IProperty prop;
		IConcept type;
		String name;
		String sqlname;
		String sqltype;
		String packageName;
		boolean custom = false;
		String customtype;
		boolean functional;
		// only object properties can be inverse!
		boolean inverse=false;
		IProperty inverseProp;
		boolean annotation=false;
		String uri;

//		Cardinality cardinality;
		
		Attribute(String name) {
			this.name = name;
			this.sqlname = name;
			this.custom = true;
			this.functional = true;
			this.inverse = false;
			this.customtype = "String";
			this.sqltype = "string";
			this.annotation=true;
			this.uri = "http://www.w3.org/2000/01/rdf-schema#label@"+name.substring(6);
			try {
				this.prop = KnowledgeManager.KM.getAbstractProperty();
			} catch (ThinklabValidationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}

		Attribute(IProperty p) {
			this.prop = p;
			this.name = CGUtils.getJavaName(p);
			this.sqlname = CGUtils.getSQLName(p);
			this.sqltype =  CGUtils.getSQLName(p).toLowerCase();
			this.uri = prop.getURI();

			// Assign the IConcept type if it is an object property
			// else assign a customtype String
			Collection<IConcept> range = p.getRange();
			if(range.size()==1){
				this.type = range.iterator().next();
			} 
			else if(range.size()>0){
				Iterator<IConcept> iter = range.iterator();

				IConcept c = iter.next();
				while(iter.hasNext())
					c.getLeastGeneralCommonConcept(iter.next());
				this.type = c;
			}

			if(type==null){
				custom = true;
				customtype = "String"; //TODO: This should be Object, but Hibernate cant handle it
			}else if(type.equals(KnowledgeManager.Thing())){
				custom = true;
				customtype = "Object";
			}else if(type.equals(KnowledgeManager.Boolean())){
				custom = true;
				customtype = "Boolean";
			} else if(type.equals(KnowledgeManager.Double())){
				custom = true;
				customtype = "Double";
			}else if(type.equals(KnowledgeManager.Float())){
				custom = true;
				customtype = "Float";
			}else if(type.equals(KnowledgeManager.Integer())){
				custom = true;
				customtype = "Integer";
			}else if(type.equals(KnowledgeManager.Long())){
				custom = true;
				customtype = "Long";
			}else if(type.equals(KnowledgeManager.Text())){
				custom = true;
				customtype = "String";
			}
			
			if(custom){
				this.sqltype = customtype.toLowerCase(); 
			}
			
			this.functional = p.isFunctional();
			
			if (p.getInverseProperty()==null){
				this.inverse=false;
			} else{
				this.inverse = true;
				inverseProp = p.getInverseProperty();
			}
			
		}

		Set<String> toJavaImportDeclaration(){
			Set<String> set = new HashSet<String>();
			if (!functional){ 
				set.add("import java.util.Set;\n");
				set.add("import java.util.HashSet;\n");
			}

			if (!custom)
				if(isFactoryRoot(type))
					set.add("import " + CGUtils.getJavaInterfaceNameWithPath(type)+";\n");	
				else
					set.add("import " + CGUtils.getJavaNameWithPath(type)+";\n");
			return set;
		}

		String toJavaAttributeDecleration(){
			String s ="\n";
			if(inverse)
				s+= "public ";
			else
				s+= "private ";
			if (!custom){
				String tp = isFactoryRoot(type) ?CGUtils.getJavaInterfaceName(type) : CGUtils.getJavaName(type);
				s+= this.functional==false? "Set<"+tp+">" :tp;
				s+= " " + this.name.toLowerCase();
				s+= this.functional==false? "= new HashSet<"+tp+">();": ";";
			}
			else{
				s+= this.functional==false? "Set<"+customtype+">" :customtype;
				s+= " " + this.name.toLowerCase();
				s+= this.functional==false? "= new HashSet<"+customtype+">();": ";"; 
			}
			return s;
		}

		String toJavaSetterAndGetter() {
			String s = "\n";
			s+= "/*\n";
			s+= "* Setters and getters for the method " ;//+ this.prop.getSemanticType() +"\n";
			s+= "*/\n";
			s+="@PropertyURI(\""+this.uri+"\")\n";
			
			// Getter
			s+= "public ";
			if (!custom){
				String tp = isFactoryRoot(type) ?CGUtils.getJavaInterfaceName(type) : CGUtils.getJavaName(type);
				s+= this.functional==false? "Set<"+tp+">" : tp;
			}
			else
					s+= this.functional==false? "Set<"+customtype+">" :customtype;
			s+= "  get"+name+"(){\n";
			s+= "  return "+name.toLowerCase()+";\n";
			s+= "  }\n";
			s+= "  \n";
			
			//Setter
			s+="@PropertyURI(\""+this.uri+"\")\n";
			s+= "public void set"+name+"(";
			if (!custom){
				String tp = isFactoryRoot(type) ?CGUtils.getJavaInterfaceName(type) : CGUtils.getJavaName(type);
				s+= this.functional==false? "Set<"+tp+">" :tp;	
			}
				else
					s+= this.functional==false? "Set<"+customtype+">" :customtype;
			s+= " arg){\n";
			s+= "  this."+name.toLowerCase() + " = arg;\n";
//			if(inverse && functional && inverseProp.getMaximumCardinality()==1 && processedConcepts.contains(type.getSemanticType().toString()))
//				s+="  arg."+(new Attribute(inverseProp).name.toLowerCase()) + " = this;\n";
			s+= "  }\n";
			s+= "  \n";
			return s;	
		}
		
		String toJavaSetterAndGetterMethodsOnly() {
			String s = "\n";
			s+= "/*\n";
			s+= "* Setters and getters for the method " ;//+ this.prop.getSemanticType() +"\n";
			s+= "*/\n";
			//Setter
			s+="@PropertyURI(\""+this.uri+"\")\n";
			s+= "public ";
			if (!custom){
				String tp = isFactoryRoot(type) ?CGUtils.getJavaInterfaceName(type) : CGUtils.getJavaName(type);
				s+= this.functional==false? "Set<"+tp+">" :tp;
			}else
					s+= this.functional==false? "Set<"+customtype+">" :customtype;
			s+= "  get"+name+"();\n";
			s+= "  \n";
			//Setter
			s+="@PropertyURI(\""+this.uri+"\")\n";
			s+= "public void set"+name+"(";
			if (!custom){
				String tp = type.getLocalName().toString();
				if(isFactoryRoot(type)) tp = "I"+tp;
				s+= this.functional==false? "Set<"+tp+">" :tp;	
			}else
					s+= this.functional==false? "Set<"+customtype+">" :customtype;
			s+= " arg);\n";
			s+= "  \n";
			return s;	
		}



	}

	
	  @SuppressWarnings("unused")
	private static void sortList(List<String> aItems){
		    Collections.sort(aItems, String.CASE_INSENSITIVE_ORDER);
		  }


	  static public <T extends Object> Map<String, T> sortMapByKey(Map<String, T> aItems){
		    TreeMap<String, T> result = new TreeMap<String, T>(String.CASE_INSENSITIVE_ORDER);
		    result.putAll(aItems);
		    return result;
		  }
	  
	  
}
