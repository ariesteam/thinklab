/**
 * CGUtils.java
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
package org.integratedmodelling.persistence.factory;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.integratedmodelling.persistence.PersistencePlugin;
import org.integratedmodelling.thinklab.configuration.LocalConfiguration;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabIOException;
import org.integratedmodelling.thinklab.exception.ThinklabNoKMException;
import org.integratedmodelling.thinklab.exception.ThinklabPluginException;
import org.integratedmodelling.thinklab.exception.ThinklabResourceNotFoundException;
import org.integratedmodelling.thinklab.interfaces.IConcept;
import org.integratedmodelling.thinklab.interfaces.IKnowledge;
import org.integratedmodelling.thinklab.interfaces.IProperty;


/**
 * A code generator helper utility.
 * Provides with a set of textual transformations of {@link IConcepts} and {@link IProperties} URIs.
 * These transformations are to be used for Java source code elements and Hibernate XML declaration elements.
 * 
 * Main methods include the conversion of a URI to java class name (with and without package declaration).
 * Example: Lets have a Concept with Semantic Type {@code thinklab-core:Text} and URI {@code http://www.integratedmodelling.org/ks/thinklab/thinklab-core.owl#Text},
 * the corresponding textual transformations are:
 * - Java class file: {@code Text.java} 
 * - Java class name: {@code Text}
 * - Java interface name: {@code IText}
 * - Java package name: {@code org.integratedmodelling.ks.thinklab.thinklab_core}.
 * - Hibernate mapping file: {@code Text.hbm.xml}
 * 
 * @author Ioannis N. Athanasiadis
 * @since Feb 5, 2007
 * @version 0.2
 * 
 */
public final class CGUtils {
	
	static Logger log = Logger.getLogger("org.integratedmodelling.persistence.factory.CGUtils");
	static String PREFIX_TABLES_WITH_FOREIGN_KEYS ="";    //"Data_";
	static String PREFIX_TABLES_WITHOUT_FOREIGN_KEYS =""; //Code_";
	static String SUFFIX_SQL_RESERVED_WORDS ="_";
	static private File scratchFolder = null;
	
	/**
	 * Returns the scratch folder where generated files are to be saved. 
	 * The scratch folder should be defined through the "{@code persistence.jarpath}" attribute of the local configuration.
	 * @return the scratch folder 
	 * @throws ThinklabException 
	 * @see org.integratedmodelling.thinklab.configuration.LocalConfiguration
	 */
	public static File getPluginScratchFolder() throws ThinklabException{
		if(scratchFolder!=null)
			return scratchFolder;
		else{
		File userFolder = new File(System.getProperty("user.dir"));
		
		File pluginScratchFolder = null;
		try {
			pluginScratchFolder = PersistencePlugin.get().getScratchPath();
		} catch (ThinklabPluginException e) {
			log.debug(e);
		} catch (ThinklabNoKMException e) {
			log.debug(e);
		}
		
		URL jarpath = null;
		try {
			jarpath = LocalConfiguration.getResource("persistence.jarpath");
		} catch (ThinklabIOException e) {
			log.debug(e);
		}
		
		
		
		File folder;
		if(jarpath!=null)
			folder = new File(jarpath.getFile());
		else if (pluginScratchFolder!=null)
			folder = pluginScratchFolder;
		else if (userFolder!=null)
			folder = userFolder;
		else folder =  new File("tmp/TKLPersistence");
		folder.mkdirs();
		scratchFolder = folder;
		return folder;
		}
	}
	
	/**
	 * Creates a new File in the scratch folder.
	 * @param relativePath the relative path of the file (i.e. org/integratedmodelling/ks/thinklab/thinklab_core) 
	 * @return file the file created
	 * @throws ThinklabException 
	 */
	public static File createFileInPluginScratchFolder(String relativePath) throws ThinklabException {	 
		File f = new File(getPluginScratchFolder(),relativePath);
		f.getParentFile().mkdirs();
			try {
				f.createNewFile();
			} catch (IOException e) {
				log.info(e);
			}
		return f;
	}

	/**
	 * Returns a File for storing the POJO Java Class of a IConcept
	 * Example: from {@code thinklab-core:Text} a file is generated in {@code src/org/integratedmodelling/ks/thinklab/thinklab_core/Text.java}.
	 * @param res the IConcept  
	 * @return File created
	 * @throws ThinklabException 
	 */
	public static File getJavaClassFile(IConcept res) throws ThinklabException{
		return createFileInPluginScratchFolder("src/"+getJavaNameWithPath(res).replaceAll("\\.", "//")+".java");
	}
	
	/**
	 * Returns a File for storing the Interface Java Class of a IConcept
	 * Example: from {@code thinklab-core:Text} a file is generated in {@code src/org/integratedmodelling/ks/thinklab/thinklab_core/IText.java}.
	 * @param res the IConcept  
	 * @return a File
	 * @throws ThinklabException 
	 */
	public static File getJavaInterfaceFile(IConcept res) throws ThinklabException{
		return createFileInPluginScratchFolder("src/"+getJavaInterfaceNameWithPath(res).replaceAll("\\.", "//")+".java");
	}
	
	/**
	 * Returns a File for storing the ControllerBean Java Class associated to a POJO
	 * Example: from {@code thinklab-core:Text} a file is generated in {@code src/org/integratedmodelling/ks/thinklab/thinklab_core/TextControllerBean.java}.
	 * @param res the IConcept  
	 * @return a File
	 * @throws ThinklabException 
	 */
	public static File getHJBManagerFile(IConcept res) throws ThinklabException {
		return createFileInPluginScratchFolder("src/"+getJavaNameWithPath(res).replaceAll("\\.", "//")+"ControllerBean.java");
	}
	
	/**
	 * Returns a File for storing the ControllerRemote Java Class associated to a POJO
	 * Example: from {@code thinklab-core:Text} a file is generated in {@code src/org/integratedmodelling/ks/thinklab/thinklab_core/TextControllerRemote.java}.
	 * @param res the IConcept  
	 * @return a File
	 * @throws ThinklabException 
	 */
	public static File getHJBManagerRemoteFile(IConcept res) throws ThinklabException {
		return createFileInPluginScratchFolder("src/"+getJavaNameWithPath(res).replaceAll("\\.", "//")+"ControllerRemote.java");
	}
	
	/**
	 * Returns a File for storing the ControllerLocal Java Class associated to a POJO
	 * Example: from {@code thinklab-core:Text} a file is generated in {@code src/org/integratedmodelling/ks/thinklab/thinklab_core/TextControllerLocal.java}.
	 * @param res the IConcept  
	 * @return a File
	 * @throws ThinklabException 
	 */
	public static File getHJBManagerLocalFile(IConcept res) throws ThinklabException {
		return createFileInPluginScratchFolder("src/"+getJavaNameWithPath(res).replaceAll("\\.", "//")+"ControllerLocal.java");
	}
	
	/**
	 * Returns a File for storing the Hibernate Mapping XML file associated to a POJO
	 * Example: from {@code thinklab-core:Text} a file is generated in {@code src/org/integratedmodelling/ks/thinklab/thinklab_core/Text.hbm.xml}.
	 * @param res the IConcept  
	 * @return a File
	 * @throws ThinklabException 
	 */
	public static File getHibernateFile(IConcept res) throws ThinklabException {
		return createFileInPluginScratchFolder("src/"+getJavaNameWithPath(res).replaceAll("\\.", "//")+".hbm.xml");
	}
	
	/**
	 * This method translates a URI of an IConcept to a java package name equivalent. 
	 * Note that the translation is one way (from the URI to the package name, and not the vice-versa).
	 * 
	 * In the case of Concept with a URI like: {@code http://www.integratedmodelling.org/ks/thinklab/thinklab-core.owl#Text},
	 * the java package name generated is {@code org.integratedmodelling.ks.thinklab.thinklab_core}.
	 * 
	 * This is achieved with the following conventions:
	 * The host name of the URI ({@code www.integratedmodelling.org}) is parsed from right to left. 
	 * Then the path name ({@code /ks/thinklab/thinklab-core.owl}) from the left to the right.
	 * The fragment ({@code #Text}) is disgarded.
	 * Note that the {@code www} prefix and the {@code owl} suffix are ommited (for aesthetical reasons).
	 * 
	 * Also, the delimiter "{@code -}" (which is illegal for a Java package name) is substituted by the character "{@code _}".
	 * 
	 * @param the IConcept or IProperty
	 * @return textual transformation
	 */
	
	public static String getJavaPackageName(IKnowledge res){	
		String ret ="", host = "", path="";

		URI uri;
		try {
			uri = new URI(res.getURI());
			 host = uri.getHost();
			 path = uri.getPath();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		String[] part = host.split("\\.");
		for(int i=part.length-1;i>=0;i--){
				ret+=part[i].length()>0 ? part[i]+"." :"";
		}
		if(ret.endsWith("www."))
			ret = ret.substring(0, ret.length()-4);
		
		part = path.split("/");
		for(int i=0;i<part.length;i++){
			ret +=  part[i].length()>0 ? part[i]+"." :"" ;
		}
		if(ret.endsWith("owl."))
			ret = ret.substring(0, ret.length()-4);
		if(ret.endsWith("."))
			ret = ret.substring(0,ret.length()-1);
		ret = ret.replaceAll("-", "_");
		
		return ret; 
	}
	
	
	/**
	 * Return the Java name of a {@link IConcept} or an {@link IProperty}, to be used as 
	 * Java class name and Java member local name respectively.
	 * Example:
	 * The Concept {@code http://www.integratedmodelling.org/ks/thinklab/thinklab-core.owl#Text}
	 * is transformed to the java name: {@code Text}
	 * The Property {@code http://www.example.org/some/path/ontology.owl#property}
	 * is transformed to the java name: {@code Property}
	 * 
	 * The only peculiarity here is that the "has" prefix, that is usually found in 
	 * property relationships is cleared out.
	 * i.e. the property
	 * {@code http://somedomain.org/onto#hasSomething}
	 * will return a local name {@code Something} rather than {@code HasSomething}
	 * 
	 * @param res IConcept or IProperty
	 * @return textual transformation
	 */
	public static String getJavaName(IKnowledge res){
		String ret = "";
		try {
			URI uri = new URI(res.getURI());
			ret += uri.getFragment()==null ? "" :uri.getFragment();
			if(uri.getFragment()==null)
				return ret;
			else{
			if (ret.startsWith("has"))
				ret = ret.substring(3,4).toUpperCase()+ret.substring(4);
			else		
				ret = ret.substring(0,1).toUpperCase()+ret.substring(1);
			}
		} catch (URISyntaxException e) {
			// this shouldn't happen!
			log.error(new ThinklabResourceNotFoundException("Can't access the URI of resource: " + res.getSemanticType()));
		}	
		return ret;
	}
	
	
	/**
	 * Return the Java Interface name that corrensponds to an IConcept.
	 * Example:
	 * the Concept {@code http://www.integratedmodelling.org/ks/thinklab/thinklab-core.owl#Text}
	 * is transformed to the java name: {@code IText}
	 * 
	 * 
	 * @param res IConcept
	 * @return textual transformation
	 */
	public static String getJavaInterfaceName(IConcept res){
		return "I"+getJavaName(res);
	}

	
	/**
	 * Return the Java class name (with package name) that corrensponds to an IConcept.
	 * For example the Concept
	 * {@code http://www.integratedmodelling.org/ks/thinklab/thinklab-core.owl#Text}
	 * is transformed to the java name:
	 * {@code org.integratedmodelling.ks.thinklab.thinklab_core.Text}
	 * 	 
	 * @param res IConcept
	 * @return textual transformation
	 */
	public static String getJavaNameWithPath(IConcept res){
		return getJavaPackageName(res) +"."+ getJavaName(res);
	}
	
	
	/**
	 * Return the Java class name that corrensponds to a IConcept.
	 * For example the Concept
	 * {@code http://www.integratedmodelling.org/ks/thinklab/thinklab-core.owl#Text}
	 * is transformed to the java name:
	 * {@code org.integratedmodelling.ks.thinklab.thinklab_core.IText}
	 * 
	 * @param res IConcept
	 * @return textual transformation
	 */
	public static String getJavaInterfaceNameWithPath(IKnowledge res){
		return getJavaPackageName(res) +".I"+ getJavaName(res);
	}
	
	
	
	/**
	 * Return the SQL name that corrensponds to an IKnowledge item. It clears out SQL reserved words ()
	 * It returns the same as {@link getJavaName()} method, but clears SQL reserved words 
	 * (as defined in {@linkplain http://www.postgresql.org/docs/current/static/sql-keywords-appendix.html})
	 * 
	 * For clarity, tables for Concepts with object properties get a prefix (default {@code data_}) and those without
	 * an other one (default: {@code code_}).
	 * 
	 * @see ReservedWords
	 * @param res IConcept
	 * @return textual transformation
	 */
	public static String getSQLName(IKnowledge res){
		String s = getJavaName(res);
		if(ReservedWords.contains(s))
			 s += SUFFIX_SQL_RESERVED_WORDS;
		if (res instanceof IConcept) {
			IConcept concept = (IConcept) res;
			if(KMUtils.containsObjectProperties(concept))
				s = PREFIX_TABLES_WITH_FOREIGN_KEYS + s;
			else
				s = PREFIX_TABLES_WITHOUT_FOREIGN_KEYS + s;		
		}
		return s;
	}

	
	 /**
	 * 
	 * This is a list of SQL Reserved words as appear in 
	 * http://www.postgresql.org/docs/current/static/sql-keywords-appendix.html
	 * @author Ioannis N. Athanasiadis,  Dalle Molle Institute for Artificial Intelligence, USI/SUPSI
	 *
	 */
	private static class ReservedWords{
		  private static Set<String> words =  new HashSet<String>();
		  
		  public static boolean contains(String lexi){
			  if (words.isEmpty())
			  	setReservedWords();
			  return words.contains(lexi.toUpperCase());
		  }
		  
		  private static void setReservedWords(){
			  words.add("A");
			  words.add("ABORT");
			  words.add("ABS");
			  words.add("ABSOLUTE");
			  words.add("ACCESS");
			  words.add("ACTION");
			  words.add("ADA");
			  words.add("ADD");
			  words.add("ADMIN");
			  words.add("AFTER");
			  words.add("AGGREGATE");
			  words.add("ALIAS");
			  words.add("ALL");
			  words.add("ALLOCATE");
			  words.add("ALSO");
			  words.add("ALTER");
			  words.add("ALWAYS");
			  words.add("ANALYSE");
			  words.add("ANALYZE");
			  words.add("AND");
			  words.add("ANY");
			  words.add("ARE");
			  words.add("ARRAY");
			  words.add("AS");
			  words.add("ASC");
			  words.add("ASENSITIVE");
			  words.add("ASSERTION");
			  words.add("ASSIGNMENT");
			  words.add("ASYMMETRIC");
			  words.add("AT");
			  words.add("ATOMIC");
			  words.add("ATTRIBUTE");
			  words.add("ATTRIBUTES");
			  words.add("AUTHORIZATION");
			  words.add("AVG");
			  words.add("BACKWARD");
			  words.add("BEFORE");
			  words.add("BEGIN");
			  words.add("BERNOULLI");
			  words.add("BETWEEN");
			  words.add("BIGINT");
			  words.add("BINARY");
			  words.add("BIT");
			  words.add("BITVAR");
			  words.add("BIT_LENGTH");
			  words.add("BLOB");
			  words.add("BOOLEAN");
			  words.add("BOTH");
			  words.add("BREADTH");
			  words.add("BY");
			  words.add("C");
			  words.add("CACHE");
			  words.add("CALL");
			  words.add("CALLED");
			  words.add("CARDINALITY");
			  words.add("CASCADE");
			  words.add("CASCADED");
			  words.add("CASE");
			  words.add("CAST");
			  words.add("CATALOG");
			  words.add("CATALOG_NAME");
			  words.add("CEIL");
			  words.add("CEILING");
			  words.add("CHAIN");
			  words.add("CHAR");
			  words.add("CHARACTER");
			  words.add("CHARACTERISTICS");
			  words.add("CHARACTERS");
			  words.add("CHARACTER_LENGTH");
			  words.add("CHARACTER_SET_CATALOG");
			  words.add("CHARACTER_SET_NAME");
			  words.add("CHARACTER_SET_SCHEMA");
			  words.add("CHAR_LENGTH");
			  words.add("CHECK");
			  words.add("CHECKED");
			  words.add("CHECKPOINT");
			  words.add("CLASS");
			  words.add("CLASS_ORIGIN");
			  words.add("CLOB");
			  words.add("CLOSE");
			  words.add("CLUSTER");
			  words.add("COALESCE");
			  words.add("COBOL");
			  words.add("COLLATE");
			  words.add("COLLATION");
			  words.add("COLLATION_CATALOG");
			  words.add("COLLATION_NAME");
			  words.add("COLLATION_SCHEMA");
			  words.add("COLLECT");
			  words.add("COLUMN");
			  words.add("COLUMN_NAME");
			  words.add("COMMAND_FUNCTION");
			  words.add("COMMAND_FUNCTION_CODE");
			  words.add("COMMENT");
			  words.add("COMMIT");
			  words.add("COMMITTED");
			  words.add("COMPLETION");
			  words.add("CONCURRENTLY");
			  words.add("CONDITION");
			  words.add("CONDITION_NUMBER");
			  words.add("CONNECT");
			  words.add("CONNECTION");
			  words.add("CONNECTION_NAME");
			  words.add("CONSTRAINT");
			  words.add("CONSTRAINTS");
			  words.add("CONSTRAINT_CATALOG");
			  words.add("CONSTRAINT_NAME");
			  words.add("CONSTRAINT_SCHEMA");
			  words.add("CONSTRUCTOR");
			  words.add("CONTAINS");
			  words.add("CONTINUE");
			  words.add("CONVERSION");
			  words.add("CONVERT");
			  words.add("COPY");
			  words.add("CORR");
			  words.add("CORRESPONDING");
			  words.add("COUNT");
			  words.add("COVAR_POP");
			  words.add("COVAR_SAMP");
			  words.add("CREATE");
			  words.add("CREATEDB");
			  words.add("CREATEROLE");
			  words.add("CREATEUSER");
			  words.add("CROSS");
			  words.add("CSV");
			  words.add("CUBE");
			  words.add("CUME_DIST");
			  words.add("CURRENT");
			  words.add("CURRENT_DATE");
			  words.add("CURRENT_DEFAULT_TRANSFORM_GROUP");
			  words.add("CURRENT_PATH");
			  words.add("CURRENT_ROLE");
			  words.add("CURRENT_TIME");
			  words.add("CURRENT_TIMESTAMP");
			  words.add("CURRENT_TRANSFORM_GROUP_FOR_TYPE");
			  words.add("CURRENT_USER");
			  words.add("CURSOR");
			  words.add("CURSOR_NAME");
			  words.add("CYCLE");
			  words.add("DATA");
			  words.add("DATABASE");
			  words.add("DATE");
			  words.add("DATETIME_INTERVAL_CODE");
			  words.add("DATETIME_INTERVAL_PRECISION");
			  words.add("DAY");
			  words.add("DEALLOCATE");
			  words.add("DEC");
			  words.add("DECIMAL");
			  words.add("DECLARE");
			  words.add("DEFAULT");
			  words.add("DEFAULTS");
			  words.add("DEFERRABLE");
			  words.add("DEFERRED");
			  words.add("DEFINED");
			  words.add("DEFINER");
			  words.add("DEGREE");
			  words.add("DELETE");
			  words.add("DELIMITER");
			  words.add("DELIMITERS");
			  words.add("DENSE_RANK");
			  words.add("DEPTH");
			  words.add("DEREF");
			  words.add("DERIVED");
			  words.add("DESC");
			  words.add("DESCRIBE");
			  words.add("DESCRIPTOR");
			  words.add("DESTROY");
			  words.add("DESTRUCTOR");
			  words.add("DETERMINISTIC");
			  words.add("DIAGNOSTICS");
			  words.add("DICTIONARY");
			  words.add("DISABLE");
			  words.add("DISCONNECT");
			  words.add("DISPATCH");
			  words.add("DISTINCT");
			  words.add("DO");
			  words.add("DOMAIN");
			  words.add("DOUBLE");
			  words.add("DROP");
			  words.add("DYNAMIC");
			  words.add("DYNAMIC_FUNCTION");
			  words.add("DYNAMIC_FUNCTION_CODE");
			  words.add("EACH");
			  words.add("ELEMENT");
			  words.add("ELSE");
			  words.add("ENABLE");
			  words.add("ENCODING");
			  words.add("ENCRYPTED");
			  words.add("END");
			  words.add("END-EXEC");
			  words.add("EQUALS");
			  words.add("ESCAPE");
			  words.add("EVERY");
			  words.add("EXCEPT");
			  words.add("EXCEPTION");
			  words.add("EXCLUDE");
			  words.add("EXCLUDING");
			  words.add("EXCLUSIVE");
			  words.add("EXEC");
			  words.add("EXECUTE");
			  words.add("EXISTING");
			  words.add("EXISTS");
			  words.add("EXP");
			  words.add("EXPLAIN");
			  words.add("EXTERNAL");
			  words.add("EXTRACT");
			  words.add("0");
			  words.add("FETCH");
			  words.add("FILTER");
			  words.add("FINAL");
			  words.add("FIRST");
			  words.add("FLOAT");
			  words.add("FLOOR");
			  words.add("FOLLOWING");
			  words.add("FOR");
			  words.add("FORCE");
			  words.add("FOREIGN");
			  words.add("FORTRAN");
			  words.add("FORWARD");
			  words.add("FOUND");
			  words.add("FREE");
			  words.add("FREEZE");
			  words.add("FROM");
			  words.add("FULL");
			  words.add("FUNCTION");
			  words.add("FUSION");
			  words.add("G");
			  words.add("GENERAL");
			  words.add("GENERATED");
			  words.add("GET");
			  words.add("GLOBAL");
			  words.add("GO");
			  words.add("GOTO");
			  words.add("GRANT");
			  words.add("GRANTED");
			  words.add("GREATEST");
			  words.add("GROUP");
			  words.add("GROUPING");
			  words.add("HANDLER");
			  words.add("HAVING");
			  words.add("HEADER");
			  words.add("HIERARCHY");
			  words.add("HOLD");
			  words.add("HOST");
			  words.add("HOUR");
			  words.add("IDENTITY");
			  words.add("IF");
			  words.add("IGNORE");
			  words.add("ILIKE");
			  words.add("IMMEDIATE");
			  words.add("IMMUTABLE");
			  words.add("IMPLEMENTATION");
			  words.add("IMPLICIT");
			  words.add("IN");
			  words.add("INCLUDING");
			  words.add("INCREMENT");
			  words.add("INDEX");
			  words.add("INDEXES");
			  words.add("INDICATOR");
			  words.add("INFIX");
			  words.add("INHERIT");
			  words.add("INHERITS");
			  words.add("INITIALIZE");
			  words.add("INITIALLY");
			  words.add("INNER");
			  words.add("INOUT");
			  words.add("INPUT");
			  words.add("INSENSITIVE");
			  words.add("INSERT");
			  words.add("INSTANCE");
			  words.add("INSTANTIABLE");
			  words.add("INSTEAD");
			  words.add("INT");
			  words.add("INTEGER");
			  words.add("INTERSECT");
			  words.add("INTERSECTION");
			  words.add("INTERVAL");
			  words.add("INTO");
			  words.add("INVOKER");
			  words.add("IS");
			  words.add("ISNULL");
			  words.add("ISOLATION");
			  words.add("ITERATE");
			  words.add("JOIN");
			  words.add("K");
			  words.add("KEY");
			  words.add("KEY_MEMBER");
			  words.add("KEY_TYPE");
			  words.add("LANCOMPILER");
			  words.add("LANGUAGE");
			  words.add("LARGE");
			  words.add("LAST");
			  words.add("LATERAL");
			  words.add("LEADING");
			  words.add("LEAST");
			  words.add("LEFT");
			  words.add("LENGTH");
			  words.add("LESS");
			  words.add("LEVEL");
			  words.add("LIKE");
			  words.add("LIMIT");
			  words.add("LISTEN");
			  words.add("LN");
			  words.add("LOAD");
			  words.add("LOCAL");
			  words.add("LOCALTIME");
			  words.add("LOCALTIMESTAMP");
			  words.add("LOCATION");
			  words.add("LOCATOR");
			  words.add("LOCK");
			  words.add("LOGIN");
			  words.add("LOWER");
			  words.add("M");
			  words.add("MAP");
			  words.add("MATCH");
			  words.add("MATCHED");
			  words.add("MAX");
			  words.add("MAXVALUE");
			  words.add("MEMBER");
			  words.add("MERGE");
			  words.add("MESSAGE_LENGTH");
			  words.add("MESSAGE_OCTET_LENGTH");
			  words.add("MESSAGE_TEXT");
			  words.add("METHOD");
			  words.add("MIN");
			  words.add("MINUTE");
			  words.add("MINVALUE");
			  words.add("MOD");
			  words.add("MODE");
			  words.add("MODIFIES");
			  words.add("MODIFY");
			  words.add("MODULE");
			  words.add("MONTH");
			  words.add("MORE");
			  words.add("MOVE");
			  words.add("MULTISET");
			  words.add("MUMPS");
			  words.add("NAME");
			  words.add("NAMES");
			  words.add("NATIONAL");
			  words.add("NATURAL");
			  words.add("NCHAR");
			  words.add("NCLOB");
			  words.add("NESTING");
			  words.add("NEW");
			  words.add("NEXT");
			  words.add("NO");
			  words.add("NOCREATEDB");
			  words.add("NOCREATEROLE");
			  words.add("NOCREATEUSER");
			  words.add("NOINHERIT");
			  words.add("NOLOGIN");
			  words.add("NONE");
			  words.add("NORMALIZE");
			  words.add("NORMALIZED");
			  words.add("NOSUPERUSER");
			  words.add("NOT");
			  words.add("NOTHING");
			  words.add("NOTIFY");
			  words.add("NOTNULL");
			  words.add("NOWAIT");
			  words.add("NULL");
			  words.add("NULLABLE");
			  words.add("NULLIF");
			  words.add("NULLS");
			  words.add("NUMBER");
			  words.add("NUMERIC");
			  words.add("OBJECT");
			  words.add("OCTETS");
			  words.add("OCTET_LENGTH");
			  words.add("OF");
			  words.add("OFF");
			  words.add("OFFSET");
			  words.add("OIDS");
			  words.add("OLD");
			  words.add("ON");
			  words.add("ONLY");
			  words.add("OPEN");
			  words.add("OPERATION");
			  words.add("OPERATOR");
			  words.add("OPTION");
			  words.add("OPTIONS");
			  words.add("OR");
			  words.add("ORDER");
			  words.add("ORDERING");
			  words.add("ORDINALITY");
			  words.add("OTHERS");
			  words.add("OUT");
			  words.add("OUTER");
			  words.add("OUTPUT");
			  words.add("OVER");
			  words.add("OVERLAPS");
			  words.add("OVERLAY");
			  words.add("OVERRIDING");
			  words.add("OWNED");
			  words.add("OWNER");
			  words.add("PAD");
			  words.add("PARAMETER");
			  words.add("PARAMETERS");
			  words.add("PARAMETER_MODE");
			  words.add("PARAMETER_NAME");
			  words.add("PARAMETER_ORDINAL_POSITION");
			  words.add("PARAMETER_SPECIFIC_CATALOG");
			  words.add("PARAMETER_SPECIFIC_NAME");
			  words.add("PARAMETER_SPECIFIC_SCHEMA");
			  words.add("PARTIAL");
			  words.add("PARTITION");
			  words.add("PASCAL");
			  words.add("PASSWORD");
			  words.add("PATH");
			  words.add("PERCENTILE_CONT");
			  words.add("PERCENTILE_DISC");
			  words.add("PERCENT_RANK");
			  words.add("PLACING");
			  words.add("PLI");
			  words.add("POSITION");
			  words.add("POSTFIX");
			  words.add("POWER");
			  words.add("PRECEDING");
			  words.add("PRECISION");
			  words.add("PREFIX");
			  words.add("PREORDER");
			  words.add("PREPARE");
			  words.add("PREPARED");
			  words.add("PRESERVE");
			  words.add("PRIMARY");
			  words.add("PRIOR");
			  words.add("PRIVILEGES");
			  words.add("PROCEDURAL");
			  words.add("PROCEDURE");
			  words.add("PUBLIC");
			  words.add("QUOTE");
			  words.add("RANGE");
			  words.add("RANK");
			  words.add("READ");
			  words.add("READS");
			  words.add("REAL");
			  words.add("REASSIGN");
			  words.add("RECHECK");
			  words.add("RECURSIVE");
			  words.add("REF");
			  words.add("REFERENCES");
			  words.add("REFERENCING");
			  words.add("REGR_AVGX");
			  words.add("REGR_AVGY");
			  words.add("REGR_COUNT");
			  words.add("REGR_INTERCEPT");
			  words.add("REGR_R2");
			  words.add("REGR_SLOPE");
			  words.add("REGR_SXX");
			  words.add("REGR_SXY");
			  words.add("REGR_SYY");
			  words.add("REINDEX");
			  words.add("RELATIVE");
			  words.add("RELEASE");
			  words.add("RENAME");
			  words.add("REPEATABLE");
			  words.add("REPLACE");
			  words.add("RESET");
			  words.add("RESTART");
			  words.add("RESTRICT");
			  words.add("RESULT");
			  words.add("RETURN");
			  words.add("RETURNED_CARDINALITY");
			  words.add("RETURNED_LENGTH");
			  words.add("RETURNED_OCTET_LENGTH");
			  words.add("RETURNED_SQLSTATE");
			  words.add("RETURNING");
			  words.add("RETURNS");
			  words.add("REVOKE");
			  words.add("RIGHT");
			  words.add("ROLE");
			  words.add("ROLLBACK");
			  words.add("ROLLUP");
			  words.add("ROUTINE");
			  words.add("ROUTINE_CATALOG");
			  words.add("ROUTINE_NAME");
			  words.add("ROUTINE_SCHEMA");
			  words.add("ROW");
			  words.add("ROWS");
			  words.add("ROW_COUNT");
			  words.add("ROW_NUMBER");
			  words.add("RULE");
			  words.add("SAVEPOINT");
			  words.add("SCALE");
			  words.add("SCHEMA");
			  words.add("SCHEMA_NAME");
			  words.add("SCOPE");
			  words.add("SCOPE_CATALOG");
			  words.add("SCOPE_NAME");
			  words.add("SCOPE_SCHEMA");
			  words.add("SCROLL");
			  words.add("SEARCH");
			  words.add("SECOND");
			  words.add("SECTION");
			  words.add("SECURITY");
			  words.add("SELECT");
			  words.add("SELF");
			  words.add("SENSITIVE");
			  words.add("SEQUENCE");
			  words.add("SERIALIZABLE");
			  words.add("SERVER_NAME");
			  words.add("SESSION");
			  words.add("SESSION_USER");
			  words.add("SET");
			  words.add("SETOF");
			  words.add("SETS");
			  words.add("SHARE");
			  words.add("SHOW");
			  words.add("SIMILAR");
			  words.add("SIMPLE");
			  words.add("SIZE");
			  words.add("SMALLINT");
			  words.add("SOME");
			  words.add("SOURCE");
			  words.add("SPACE");
			  words.add("SPECIFIC");
			  words.add("SPECIFICTYPE");
			  words.add("SPECIFIC_NAME");
			  words.add("SQL");
			  words.add("SQLCODE");
			  words.add("SQLERROR");
			  words.add("SQLEXCEPTION");
			  words.add("SQLSTATE");
			  words.add("SQLWARNING");
			  words.add("SQRT");
			  words.add("STABLE");
			  words.add("START");
			  words.add("STATE");
			  words.add("STATEMENT");
			  words.add("STATIC");
			  words.add("STATISTICS");
			  words.add("STDDEV_POP");
			  words.add("STDDEV_SAMP");
			  words.add("STDIN");
			  words.add("STDOUT");
			  words.add("STORAGE");
			  words.add("STRICT");
			  words.add("STRUCTURE");
			  words.add("STYLE");
			  words.add("SUBCLASS_ORIGIN");
			  words.add("SUBLIST");
			  words.add("SUBMULTISET");
			  words.add("SUBSTRING");
			  words.add("SUM");
			  words.add("SUPERUSER");
			  words.add("SYMMETRIC");
			  words.add("SYSID");
			  words.add("SYSTEM");
			  words.add("SYSTEM_USER");
			  words.add("TABLE");
			  words.add("TABLESAMPLE");
			  words.add("TABLESPACE");
			  words.add("TABLE_NAME");
			  words.add("TEMP");
			  words.add("TEMPLATE");
			  words.add("TEMPORARY");
			  words.add("TERMINATE");
			  words.add("THAN");
			  words.add("THEN");
			  words.add("TIES");
			  words.add("TIME");
			  words.add("TIMESTAMP");
			  words.add("TIMEZONE_HOUR");
			  words.add("TIMEZONE_MINUTE");
			  words.add("TO");
			  words.add("TOP_LEVEL_COUNT");
			  words.add("TRAILING");
			  words.add("TRANSACTION");
			  words.add("TRANSACTIONS_COMMITTED");
			  words.add("TRANSACTIONS_ROLLED_BACK");
			  words.add("TRANSACTION_ACTIVE");
			  words.add("TRANSFORM");
			  words.add("TRANSFORMS");
			  words.add("TRANSLATE");
			  words.add("TRANSLATION");
			  words.add("TREAT");
			  words.add("TRIGGER");
			  words.add("TRIGGER_CATALOG");
			  words.add("TRIGGER_NAME");
			  words.add("TRIGGER_SCHEMA");
			  words.add("TRIM");
			  words.add("1");
			  words.add("TRUNCATE");
			  words.add("TRUSTED");
			  words.add("TYPE");
			  words.add("UESCAPE");
			  words.add("UNBOUNDED");
			  words.add("UNCOMMITTED");
			  words.add("UNDER");
			  words.add("UNENCRYPTED");
			  words.add("UNION");
			  words.add("UNIQUE");
			  words.add("UNKNOWN");
			  words.add("UNLISTEN");
			  words.add("UNNAMED");
			  words.add("UNNEST");
			  words.add("UNTIL");
			  words.add("UPDATE");
			  words.add("UPPER");
			  words.add("USAGE");
			  words.add("USER");
			  words.add("USER_DEFINED_TYPE_CATALOG");
			  words.add("USER_DEFINED_TYPE_CODE");
			  words.add("USER_DEFINED_TYPE_NAME");
			  words.add("USER_DEFINED_TYPE_SCHEMA");
			  words.add("USING");
			  words.add("VACUUM");
			  words.add("VALID");
			  words.add("VALIDATOR");
			  words.add("VALUE");
			  words.add("VALUES");
			  words.add("VARCHAR");
			  words.add("VARIABLE");
			  words.add("VARYING");
			  words.add("VAR_POP");
			  words.add("VAR_SAMP");
			  words.add("VERBOSE");
			  words.add("VIEW");
			  words.add("VOLATILE");
			  words.add("WHEN");
			  words.add("WHENEVER");
			  words.add("WHERE");
			  words.add("WIDTH_BUCKET");
			  words.add("WINDOW");
			  words.add("WITH");
			  words.add("WITHIN");
			  words.add("WITHOUT");
			  words.add("WORK");
			  words.add("WRITE");
			  words.add("YEAR");
			  words.add("ZONE");
		  }
		  

	  }
}
