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

import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * A configuration utility for creating the hibernate configuration file for the object-relational mapping
 * ({@code hibernate.cfg.xml}).
 * 
 * @author Ioannis N. Athanasiadis
 * @since Feb 5, 2007
 * @version 0.2
 */
public abstract class HibernateConfigurator {
	static HashMap<String, String> properties = new HashMap<String, String>();
	static String name = "scratch";
	
	private static void initialize(){
		properties.put("hibernate.connection.driver_class","org.postgresql.Driver");
		properties.put("hibernate.connection.url","jdbc:postgresql://localhost/hb");
		properties.put("hibernate.connection.username","pgadmin");
		properties.put("hibernate.connection.password","pgadmin");
		properties.put("hibernate.dialect","org.hibernate.dialect.PostgreSQLDialect");
		
//		properties.put("hibernate.c3p0.min_size","5");
//		properties.put("hibernate.c3p0.max_size","20");
//		properties.put("hibernate.c3p0.timeout","300");
//		properties.put("hibernate.c3p0.max_statements","50");
//		properties.put("hibernate.c3p0.idle_test_period","3000");
	
//		properties.put("hibernate.show_sql","true");
//		properties.put("hibernate.format_sql","true");
		properties.put("hibernate.hbm2ddl.auto","update");//"create");
	}
	

	/**
	 * Specify the jdbc connection to be used
	 *  
	 * @param url 
	 * @param user
	 * @param passwd
	 */
	public static void setJDBCConnection(String url, String user, String passwd){
		if(properties.isEmpty()) initialize();
		properties.put("hibernate.connection.url", url);
		properties.put("hibernate.connection.username", user);
		properties.put("hibernate.connection.password", passwd);
	}
	
	public static void setName(String name){
		HibernateConfigurator.name = name;
	}
	
	/**
	 * Provides with the XML {@code hibernate.cfg.xml} mapping file 
	 * containing only the basic configuration (jdbc, user, pass).
	 *  
	 *  @return the XML Document
	 */
	protected static Document getHMConfiguration(){
		if(properties.isEmpty()) initialize();
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		Document document = null;
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			DOMImplementation hbmDomi = builder.getDOMImplementation();
			DocumentType hbmDocType = hbmDomi.createDocumentType("hibernate-configuration", "-//Hibernate/Hibernate Configuration DTD 3.0//EN", "http://hibernate.sourceforge.net/hibernate-configuration-3.0.dtd");
			document = hbmDomi.createDocument("", "hibernate-configuration", hbmDocType);
			Node root = document.getDocumentElement();
			Element sessionfactory = document.createElement("session-factory");
			root.appendChild(sessionfactory);
			
			for(String key:properties.keySet()){
				Element property = document.createElement("property");
				
				property.setAttribute("name", key);
				property.setTextContent(properties.get(key));
				sessionfactory.appendChild(property);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
			return document; 
	}

	/**
	 * Provides with the XML {@code persistence.xml} mapping file 
	 * containing only the basic configuration (jdbc, user, pass).
	 *  
	 *  @return the XML Document
	 */
	protected static Document getPMConfiguration(){
		if(properties.isEmpty()) initialize();
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		Document document = null;
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			DOMImplementation hbmDomi = builder.getDOMImplementation();
			document = hbmDomi.createDocument("", "persistence", null);
			Node root = document.getDocumentElement();
			
			Element persistenceunit = document.createElement("persistence-unit");
			persistenceunit.setAttribute("name", name);
			persistenceunit.setAttribute("transaction-type","RESOURCE_LOCAL");
			root.appendChild(persistenceunit);
			Element props = document.createElement("properties");
			persistenceunit.appendChild(props);
			for(String key:properties.keySet()){
				Element property = document.createElement("property");
				property.setAttribute("name", key);
				property.setAttribute("value", properties.get(key));
				props.appendChild(property);
			}
			Element property = document.createElement("property");
			property.setAttribute("name", "hibernate.archive.autodetection");
			property.setAttribute("value", "class, hbm");
			props.appendChild(property);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
			return document; 
	}
	
}
