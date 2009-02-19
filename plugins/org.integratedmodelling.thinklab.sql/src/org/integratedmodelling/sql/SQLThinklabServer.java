/**
 * SQLThinklabServer.java
 * ----------------------------------------------------------------------------------
 * 
 * Copyright (C) 2008 www.integratedmodelling.org
 * Created: Jan 17, 2008
 *
 * ----------------------------------------------------------------------------------
 * This file is part of ThinklabSQLPlugin.
 * 
 * ThinklabSQLPlugin is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * ThinklabSQLPlugin is distributed in the hope that it will be useful,
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
 * @author    Ferdinando Villa (fvilla@uvm.edu)
 * @date      Jan 17, 2008
 * @license   http://www.gnu.org/licenses/gpl.txt GNU General Public License v3
 * @link      http://www.integratedmodelling.org
 **/
package org.integratedmodelling.sql;

import java.io.File;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.integratedmodelling.thinklab.ConceptVisitor;
import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.constraint.Constraint;
import org.integratedmodelling.thinklab.constraint.Restriction;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabIOException;
import org.integratedmodelling.thinklab.exception.ThinklabInternalErrorException;
import org.integratedmodelling.thinklab.exception.ThinklabNoKMException;
import org.integratedmodelling.thinklab.exception.ThinklabStorageException;
import org.integratedmodelling.thinklab.exception.ThinklabUnimplementedFeatureException;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;
import org.integratedmodelling.thinklab.interfaces.knowledge.IKnowledgeSubject;
import org.integratedmodelling.thinklab.interfaces.knowledge.IProperty;
import org.integratedmodelling.thinklab.interfaces.knowledge.IRelationship;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;
import org.integratedmodelling.thinklab.interfaces.storage.IKBox;
import org.integratedmodelling.thinklab.literals.AlgorithmValue;
import org.integratedmodelling.thinklab.literals.BooleanValue;
import org.integratedmodelling.thinklab.literals.ObjectReferenceValue;
import org.integratedmodelling.thinklab.literals.Value;
import org.integratedmodelling.utils.Escape;
import org.integratedmodelling.utils.LogicalConnector;
import org.integratedmodelling.utils.MiscUtilities;
import org.integratedmodelling.utils.NameGenerator;
import org.integratedmodelling.utils.Pair;
import org.integratedmodelling.utils.Polylist;
import org.integratedmodelling.utils.Quantifier;
import org.integratedmodelling.utils.Triple;
import org.integratedmodelling.utils.XMLDocument;
import org.mvel.MVEL;
import org.w3c.dom.Node;


/** 
 * A wrapper for a SQLServer which handles the mapping between the IMT knowledge base structure
 * and an SQL-based database. It is initialized with a set of properties that
 * must contain at least a database URL and a protocol string (which specifies the
 * initial schema to be read). Another property (schema) may contain one or more additional
 * schemata to load.
 * 
 * The schema files (.sqx) are XML documents
 * that define non-standard data types and operators for
 * literals of user-defined IMT classes, so that any
 * SQL database can be used and there are ways to accommodate non-standard extensions. 
 */
public abstract class SQLThinklabServer {

	enum OpType {INFIX, PREFIX, POSTFIX, FUNCTION};
	enum UseRestrictions {TRUE, FALSE, PARTIAL};
	
	// TODO these could become bidirectional maps from Apache Commons Collections
	HashMap<Long, String> id2Type = new HashMap<Long, String>();
	HashMap<String, Long> type2ID  = new HashMap<String, Long>();
    HashMap<Long, String> id2Property  = new HashMap<Long, String>();
    HashMap<String, Long> property2ID  = new HashMap<String, Long>();

	private static  Logger log = Logger.getLogger(SQLThinklabServer.class);
	
    private String databaseIDString = null;
    
    /**
     * this one is bound to the property sql.use.restrictions. If true
     * (default), whenever a constraint selects a type, its full definition
     * constraint is used instead of the bare type, to compensate for the lack
     * of reasoning in the SQL database. This way all the specialized restrictions
     * in superclasses are used in a query, along with all the thinklab-specific
     * restrictions specified as constraints in the ontology. Otherwise, just the
     * bare type and its subclasses are selected.
     */
    private UseRestrictions useRestrictions = UseRestrictions.PARTIAL;
	
	static boolean initialized = false;
	
	/**
	 * The semantic type (language) of the interpreted algorithms we use for calculated
	 * fields. Defined through XML initialization. Defaults to the only one we have
	 * so far...
	 * TODO put in SQL.properties
	 */
	protected String scriptLanguage = null;

	/**
	 * Descriptor for a table in XML schema, read from plug-in.
	 * @author UVM Affiliate
	 */
	class TableDesc {
		
		public String name;
		public ArrayList<String> fieldNames = new ArrayList<String>();
		public ArrayList<String> fieldTypes = new ArrayList<String>();
		public ArrayList<String> fieldValues = new ArrayList<String>();
		public ArrayList<Boolean> isKey = new ArrayList<Boolean>();
		public ArrayList<Integer> system = new ArrayList<Integer>();
		public ArrayList<Integer> index = new ArrayList<Integer>();
		public boolean isTemplate = false;

		public TableDesc(String name) {
			this.name = name; 
		}
		
		/**
		 * SQL code to create table.
		 * @return SQL code
		 */
		public String creationCode() {

			String ret =  "CREATE TABLE " + name + " (\n";

			for (int i = 0; i < fieldNames.size(); i++) {
			    ret += 
			      "\t" + 
			      fieldNames.get(i) +
			      "\t" +
			      fieldTypes.get(i) +
			      (isKey.get(i) ? " PRIMARY KEY" : "") + 
			      (i == fieldNames.size() - 1 ? "\n" : ",\n");
			}

			ret += ");\n";

			  for (int i = 0; i < fieldNames.size(); i++) {
			    if (index.get(i) != 0 || isKey.get(i))
			      ret += 
			    	  "CREATE INDEX " + name + "_" + fieldNames.get(i) + " ON " + 
			    	  name + " (" + fieldNames.get(i) + ");\n";
			  }
			  
			return ret;
		}
		
		/**
		 * Read in XML specification. May be called more than once.
		 * @param n XML node to read from. Must be a &lt;table&gt; node.
		 */
		public void readXML(Node n) {
		
			String tAttr = XMLDocument.getAttributeValue(n, "template");
			if (tAttr != null) {
				isTemplate = BooleanValue.parseBoolean(tAttr);
			}
			
			for (Node nn = n.getFirstChild(); nn != null; nn = nn.getNextSibling()) {

				if (nn.getNodeName().equals("field")) {
					fieldNames.add(XMLDocument.getAttributeValue(nn, "name"));
					fieldTypes.add(XMLDocument.getAttributeValue(nn, "type"));
					fieldValues.add(XMLDocument.getNodeValue(nn));
					isKey.add(BooleanValue.parseBoolean(XMLDocument.getAttributeValue(nn, "primary-key", "false")));
					
					tAttr = XMLDocument.getAttributeValue(nn, "index", "false");
					index.add(new Integer(BooleanValue.parseBoolean(tAttr) ? 1 : 0));
					tAttr = XMLDocument.getAttributeValue(nn, "system", "false");
					system.add(new Integer(BooleanValue.parseBoolean(tAttr) ? 1 : 0));
				}
			}
		}
	}
	
	ArrayList<TableDesc> tables = new ArrayList<TableDesc>();
	
	public class OpTranslator {
		
		public String jimtName;
		public String sqlName;
		public String funcTemplate;
		public ArrayList<String> argTypes = new ArrayList<String>();
		public ArrayList<String> argTemplates = new ArrayList<String>();
		public OpType type;
		
		public OpTranslator() {
			type = OpType.INFIX;
		}
	}
	
	/**
	 * Holds the information relative to the translation of values of a given
	 * semantic type into SQL, and the operators it supports.
	 */
	public class TypeTranslator {
	
		// the semantic type we translate
		String semanticType;
		
		/** the sql type we translate this semantic type into, or a statement template
		 * if the translation is achieved by invoking a SQL statement.
		 */
		String sqlType;
		
		/**
		 * either the string template for the SQL value or the plugin name to
		 * translate value to sqlType
		 */
		String fLiteral;
		
		/**
		 * if true, the fLiteral field identifies a plugin capable of
		 * translating; // otherwise, translation is simply a template
		 * substitution of the original value into // the template
		 */
		boolean literalIsPlugin = false;
		
		/**
		 * If true, the value is created by a separate SQL instruction, whose
		 * value is stored in sqlType. If false, the value creation is simply a
		 * SQL literal.
		 */
		boolean createAsStatement = false;
		
		// the operations supported and their translations into SQL
		ArrayList<OpTranslator> operators = new ArrayList<OpTranslator>();
		
		// variables (name, code) that we want to create when a specific type of value is 
		// encountered.
		ArrayList<Pair<String,String>> variables = new ArrayList<Pair<String,String>>();
		
		public OpTranslator getOperator(String s) {
			OpTranslator ret = null;
			for (OpTranslator o : operators) {
				if (o.jimtName.equals(s)) {
					ret = o;
					break;
				}
			}
			return ret;
		}

		public String substituteVariables(String expr, IValue val, ISession session) throws ThinklabException {
			
			String ret = expr;
			
			/*
			 * calculate field in context of instance, add proper
			 * representation
			 */
			for (Pair<String, String> exp : variables) {
				
				IValue vv = null;
				
				if (scriptLanguage.equals("MVEL")) {
				
					HashMap<String, IValue> context = new HashMap<String, IValue>();
					context.put("self", val);
					vv = Value.getValueForObject(MVEL.eval(exp.getSecond(), context));

				} else {
				
					/**
					 * TODO modernize handling of other languages
					 */
					throw new ThinklabUnimplementedFeatureException(
							"sql: can't evaluate expression in " + 
							scriptLanguage);
							
				}
				
				ret = ret.replace(
						"$" + exp.getFirst(), 
						(vv == null ? "" : vv.toString()));
			}
			

			
			
			return ret;
			
		}

		public void copy(TypeTranslator pi) {

			// TODO Auto-generated method stub
			this.fLiteral = pi.fLiteral;
			this.literalIsPlugin = pi.literalIsPlugin;
			this.sqlType = pi.sqlType;
			
			// shallow copy should be ok
			this.operators = new ArrayList<OpTranslator>(pi.operators);
			this.variables = new ArrayList<Pair<String, String>>(pi.variables);

			this.createAsStatement = pi.createAsStatement;
		}
	}
	
    HashMap<String, TypeTranslator> typeTranslators = new HashMap<String, TypeTranslator>();
    
    private class SqlMatcher implements ConceptVisitor.ConceptMatcher {

        HashMap<String, TypeTranslator> hash;
        public TypeTranslator translator;
        
        public boolean match(IConcept c) {
            translator = hash.get(c.getSemanticType().toString());
            return translator != null;
        }
        
        public SqlMatcher(HashMap<String, TypeTranslator> h) {
            hash = h;
        }
    }

    private class IDContainer { // was ids
    	long objectID = 0;
		long conceptID = 0;
		long propertyID = 0;
	}
    
    IDContainer IDs = new IDContainer();
    
	protected SQLServer server = null;
	
	/*
	 * if true, this container stores objects by their local names; otherwise, names are redefined
	 */
	private boolean useLocalNames = true;
	private Properties properties;
        
	private Pair<String, Long> getRelationshipId(String s, IConcept c, IValue val, ISession session,
			String sql, boolean isLiteral) throws ThinklabException {

		long ret = 0;
		String ssql = sql;
		/*
		 * if relationship is there, set id, else create new one and instruction
		 * to store it
		 */
		Long ll = property2ID.get(s);

		if (ll == null) {

			ret = IDs.propertyID++;
			if (isLiteral) {

				/*
				 * See which field we need to create in the corresponding table.
				 * For now we assume the concept we relate with is one - if
				 * relationships can be with more than one concepts and these
				 * have different representations (which is a pretty degenerate
				 * case if you hear me), this will generate an SQL error.
				 */

				TypeTranslator tt = getTypeTranslator(c);

				String tabname = "literal_" + ret;

				ssql += "CREATE TABLE " + tabname + " (";

				TableDesc t = getTableDescriptor("literal");
				for (int i = 0; i < t.fieldTypes.size(); i++) {
					ssql += (i == 0 ? " " : ", ") + t.fieldNames.get(i) + " "
							+ t.fieldTypes.get(i);
				}

				if (!tt.createAsStatement)
					ssql += ", value " + tt.sqlType + ");\n";
				else
					ssql += ");\n";

				if (tt.createAsStatement) {

					/* substitute everything in instruction and output it */
					String ts = tt.sqlType;
					ts = ts.replace("$dbname", server.getDatabase());
					ts = ts.replace("$fieldname", "value");
					ts = ts.replace("$tablename", tabname);
					ts = tt.substituteVariables(ts, val, session);
					ssql += ts + "\n";
				}
			}

			ssql += "INSERT INTO relationship VALUES ('" + s
					+ "', " + ret + ");\n";

			property2ID.put(s, ret);
			id2Property.put(ret, s);
			
		} else
			ret = ll;

		return new Pair<String, Long>(ssql, ret);
	}
	
	private Triple<String, Long, String> getClassID(IKnowledgeSubject c, String sql) {

		long conceptID = 0;
		String objectID = null;
		String ssql = sql;

		/*
		 * find class; if not there, generate new id and instruction to store
		 * it. If concept is not an instance, use c, else do the same for all
		 * parents (generate or use a temp class if more than one).
		 */
		String theClass = null;

		if (c instanceof IInstance) {
			/**
			 * FIXME only supports instances with only one direct type. I guess
			 * we can let it go for now, and address more complex cases as
			 * needed.
			 */
			theClass = ((IInstance) c).getDirectType().toString();
		} else {
			theClass = c.getSemanticType().toString();
		}

		Long iid = type2ID.get(theClass);

		if (iid == null) {

			conceptID = IDs.conceptID++;

			type2ID.put(theClass, conceptID);
			id2Type.put(conceptID, theClass);

			ssql += "INSERT INTO concept VALUES ('" + theClass + "', "
					+ conceptID + ", '');\n";

		} else {
			conceptID = iid;
		}

		if (!useLocalNames ) {
			objectID = server.getDatabase() + "_" + IDs.objectID++;
		} else {
			objectID = c.getLocalName();
		}
		
		return new Triple<String, Long, String>(objectID, new Long(conceptID),
				ssql);
	}
		
	private Pair<Long, String> getBareConceptID(IConcept c, String sql) {
			
		long conid;
		String ssql = sql;

		/*
		 * find class; if not there, generate new id and instruction to store
		 * it. If concept is not an instance, use c, else do the same for all
		 * parents (generate or use a temp class if more than one).
		 */
		String theClass = c.getSemanticType().toString();

		/** see if class is there */
		Long ll = type2ID.get(theClass);

		if (ll == null) {

			conid = IDs.conceptID++;
			/** add to both dictionary and database */
			type2ID.put(theClass, conid);
			id2Type.put(conid, theClass);

			// TODO insert description (if necessary)
			
			ssql += "INSERT INTO concept VALUES ('" + theClass + "', " + conid
					+ ", '');\n";
		} else
			conid = ll;

		return new Pair<Long, String>(conid, ssql);
	}

	private String updateIDs(String sql) {
		
		String ssql =
			sql + 
			"UPDATE ids SET object_id = " + 
			IDs.objectID + 
			", concept_id = " +
			IDs.conceptID + 
			", property_id = " + 
			IDs.propertyID + 
			";\n";

		return ssql;
	}

	private void initializeIDs() throws ThinklabStorageException {

		QueryResult rset = server.query("SELECT object_id, concept_id, property_id FROM ids;");

		if (rset.size() == 0) {
			IDs.objectID = IDs.conceptID = IDs.propertyID = 1l;
			server.execute("INSERT INTO ids VALUES (1, 1, 1);");
		} else {
			IDs.objectID = rset.getLong(0,0);
			IDs.conceptID = rset.getLong(0,1);
			IDs.propertyID = rset.getLong(0,2);
		}
	}


	private TypeTranslator getTypeTranslator(IConcept c) throws ThinklabStorageException {
		
		TypeTranslator ret = null;
			
		SqlMatcher matcher = new SqlMatcher(typeTranslators);
		IConcept cc = ConceptVisitor.findMatchUpwards(matcher, c);

		if (cc != null)
			ret = matcher.translator;

		// TODO check implications of this
		/*
		 * if it's not even an IMA concept, it must be part of a classification
		 * relationship from an external ontology, so we treat it as the
		 * simplest concept
		 */
		// concept target = c.isa("ima:concept") ? c : KR::c_concept;

		if (ret == null)
			throw new ThinklabStorageException(
					"sql: don't know how to translate type " + c);

		return ret;
	}

	private String translateLiteral(IValue value, IConcept c, ISession session) throws ThinklabException {

		String ret = "";

		if (!value.isLiteral())
			throw new ThinklabStorageException(
					"sql: translation of non-literal value of type " + c);

		log.debug("getting type translator for \"" + value + "\" (" + c + ")");
		
		TypeTranslator tt = getTypeTranslator(c);
		String template = tt.fLiteral;
		
		// TODO: leave this for later (booring)
		// ttrans::literal_translation_plugin plu =
		// PLG::retrieve_existing_plugin
		// <ttrans::literal_translation_plugin>
		// (SQL_LITERAL_TRANSLATION_PLUGINS, tmpl);
		// ret = (*plu)(v);

		if (tt.literalIsPlugin) {
			throw new ThinklabStorageException(new ThinklabUnimplementedFeatureException(
					"sql: use of plugins in literal translation"));
		} else {
			
			if (value == null) {
				ret = "''";
			} else {
				String zt = value.toString();
				zt = Escape.forSQL(zt);
				String tmpl = tt.substituteVariables(template, value, session);
				ret = tmpl.replaceAll(Pattern.quote("$$"), Matcher.quoteReplacement(zt));
			}
		}

		return ret;
	}

	/*
	 * Return a proper quantifier condition for the query generated in translateRestriction().
	 * Will only handle the cases it handles, the others (hopefully) will never be passed.
	 * 
	 * @param quantifier
	 * @param relID
	 * @return
	 */
	String translateQuantifier(Quantifier quantifier, long relID, String qID) {

		String ret = "";
		
		if (quantifier.is(Quantifier.EXACT)) {
			
			ret = " c" + qID + " = " + quantifier.getExactValue();
			
		} else if (quantifier.is(Quantifier.RANGE)) {
			
			if (quantifier.isMaxUnbound()) {
				ret = " c" + qID + " >= " + quantifier.getMinValue();
 			} else if (quantifier.isMinUnbound()) {
				ret = " c" + qID + " <= " + quantifier.getMaxValue();				
			} else {
				
				ret = 
					" c" + qID + " BETWEEN " + 
					quantifier.getMinValue() + 
					" AND " + 
					quantifier.getMaxValue();
			}
			
		} else if (quantifier.is(Quantifier.ALL)) {
			
			ret = 
				"c" + qID + " = " +
				"(SELECT n_conc FROM rel_catalog WHERE object_id = "
				+ qID + 
				".object_id AND relationship_id = " +
				relID +
				")";
		}
		
		return ret;
	}
	
	private TableDesc getTableDescriptor(String tablename)
			throws ThinklabStorageException {

		TableDesc ret = null;
		for (TableDesc tt : tables) {
			if (tt.name.equals(tablename)) {
				ret = tt;
				break;
			}
		}

		if (ret == null)
			throw new ThinklabStorageException("internal: table " + tablename
					+ "not found");

		return ret;
	}

	/**
	 * Returns an SQL expression that selects all objects of the passed class or any of its
	 * superclasses, using the asserted class hierarchy only. 
	 * 
	 * @param concept
	 * @return an SQL expression, an empty string if we would select the whole kbox, or null if 
	 * no objects of those classes exist in the database.
	 * @throws ThinklabStorageException
	 */
	private String translateConceptRestriction(IConcept concept) throws ThinklabException {

		String ret = getTypeClosure(concept);
		
		if (ret == null || ret.equals(""))
			return ret;
		
		return 
			"SELECT object_id FROM object WHERE concept_id IN (" +
			ret +
			")";
	}
	
	/**
	 * 
	 * @param constraint
	 * @return an SQL query, an empty string if we end up selecting the whole database, 
	 *     or null if we can determine in advance that no results will be found.
	 * @throws ThinklabStorageException
	 */
	public String translateConstraint(Constraint constraint) throws ThinklabException {
		
		/*
		 * merge constraint with any (cached) restrictions that come with its
		 * type if that's what we want.
		 */
		setTypeRestriction(constraint); 
		
        String classSelector = translateConceptRestriction(constraint.getConcept());
			
        if (classSelector == null)
        	return null;
		
        String propSelector = translateRestriction(constraint.getRestrictions());
        
        if (propSelector == null) 
        	return null;
        
        /* both select the whole DB, we select the whole db */
        if (classSelector.equals("") && propSelector.equals(""))
        	return "";
        
        String ret = "";
        
        /*
         * Create select; if we have both class and property constraints, AND them together. 
         * Otherwise, the non-empty one will do. 
         */
        if (!classSelector.equals("") && !propSelector.equals("")) {
        	ret = 
        		"SELECT object_id FROM object WHERE object_id IN ((" + 
        		classSelector + 
        		") INTERSECT (" + 
        		propSelector + "))";
        } else if (!classSelector.equals("")) {
        	ret = classSelector;
        } else {
        	ret = propSelector;
        }
        
        return ret;
	}
	
	private void setTypeRestriction(Constraint constraint) throws ThinklabException {

		if (useRestrictions == UseRestrictions.TRUE ||
			(useRestrictions == UseRestrictions.PARTIAL && 
					!type2ID.containsKey(constraint.getConcept().toString()))) {
			
			Constraint c = constraint.getConcept().getDefinition();
			constraint.merge(c, LogicalConnector.INTERSECTION);
			constraint.setConcept(c.getConcept());
		}
	}

	/*
	 * Translate a restriction in the proper SQL that selects object IDs based on it.
	 * 
	 * FIXME make sure behavior ok with all cases. See:
	 * 
	 *  TLC-33: Make sure SQL plugin works OK with exclusions and mutual exclusions as well as intersections, unions
	 *    http://ecoinformatics.uvm.edu:8080/jira/browse/TLC-33
	 *  TLC-34: Revise implementation of Constraint ->SQL for speed and optimality
	 *    http://ecoinformatics.uvm.edu:8080/jira/browse/TLC-34
	 *  TLC-35: Honor quantifiers in translating Constraint->SQL
	 *	  http://ecoinformatics.uvm.edu:8080/jira/browse/TLC-35
	 *  
	 */
	private String translateRestriction(Restriction restriction) throws ThinklabException {

		if (restriction == null)
			return "";
		
		String ret = "";
		
		if (restriction.isConnector()) {
			
			LogicalConnector connector = restriction.getConnector();
			
			for (Restriction r : restriction.getChildren()) {
				
				String sql = translateRestriction(r);
				
				/* if any of the ANDed queries has no context, the whole thing has no context. */
				if (sql == null && connector.equals(LogicalConnector.INTERSECTION))
					return null;

				/* 
				 * if a negated query selects the whole database, we get nothing and no query is
				 * required. Otherwise, no need to use it.
				 */
				if (sql.equals("")) {
						continue;
				}
			
				/* connect properly to whatever was there */
				if (!ret.equals("")) {
					ret += ") " + getSQLConnector(connector) + " (";
				} else {
					ret = "(";
				}
				
				ret += sql;
			}
			ret += ")";
						
		} else {
			
			IProperty relContext = restriction.getProperty();
			long rid = 0l;
			Long ll = property2ID.get(relContext.toString());

            /*
             * if the relationship is not in the db, any query can only return nothing.
             */
            if (ll == null)
                return null;

            rid = ll;
            boolean negate = restriction.getQuantifier().is(Quantifier.NONE);
            boolean any = restriction.getQuantifier().is(Quantifier.ANY);
            
            String fieldToSelect = "object_id";
            
        	if (restriction.isClassification()) {

        		/* 
        		 * select all the domain objects of classification relationships whose
        		 * class is the expected classification or any of its subclasses.
        		 */         		
        		if ((ret = getTypeClosure(restriction.getClassificationConcept())) == null)
        			return null;

// this does not work. not sure if the quantifier is handled properly in the current approach, though:        		
//        		String selector = "class_id IN";
//        		
//        		if (any)
//        			selector = "EXISTS";
//        		else if (negate)
//        			selector = "NOT EXISTS";
// here's the right (?) one:
        		String selector = "class_id";
        		
        		if (negate)
        			selector += " NOT IN";
        		else
        			selector += " IN";
 // end of right(?) approach
        		
        		if (!ret.equals("")) {
        			ret = 
        				"SELECT @ZFIELDZ@ FROM object_classifications WHERE " + 
        				selector +
        				" (" +
        				ret + 
        				") AND relationship_id = " +
        				rid;
        			
        			fieldToSelect = "source_id";
        		}
        		
        	}  else if (restriction.isLiteral()) {
        		
        		/* select the domain objects of the literal relationships that correspond to the
        		 * property and satisfy the SQL form of the operator;  */
        		ret = translateOperator(restriction);

                if (ret.equals(""))
                    return ret;

                ret = 
                	"SELECT " + 
                	(any ? "DISTINCT" : "") +
                	" @ZFIELDZ@ FROM literal_" + rid + " WHERE " +
                	(negate ? " NOT(" : "") +
                	ret + 
                	(negate ? ")" : "");


        	} else if (restriction.isObject()) {
        
// this does not work. not sure if the quantifier is handled properly in the current approach, though:        		
//        		String selector = "class_id IN";
//        		
//        		if (any)
//        			selector = "EXISTS";
//        		else if (negate)
//        			selector = "NOT EXISTS";
// here's the right (?) one:
        		String selector = "target_id";
        		
        		if (negate)
        			selector += " NOT IN";
        		else
        			selector += " IN";
 // end of right(?) approach

        		/* 
        		 * select the domain objects of the object relationships that link to the
        		 * objects that satisfy the subconstraint.
        		 */
        		ret = translateConstraint(restriction.getSubConstraint());

        		if (ret == null)
        			return null;
        		
        		if (!ret.equals("")) {
        		
        			ret = 
        				"SELECT @ZFIELDZ@ FROM object_relationships WHERE " + 
        				selector + 
        				" (" +
        				ret +
        				") AND relationship_id = " 
        				+ rid;
        			
        			fieldToSelect = "source_id";
        		}
        		
        	} else {
        		
        		/* 
        		 * we have no operator and no constraint context, and we're not a 
        		 * connector, so all we're checking is the existence of the relationship. 
        		 */
        		ret = 
        			"SELECT " +
        			(any ? "DISTINCT " : "") +
        			"@ZFIELDZ@ FROM rel_catalog WHERE relationship_id " + 
        			(negate ? "<> " : "= ") + 
        			rid; 
        	}
        	
        	
        	if (!ret.equals("")) {
        		
        		/*
        		 * handle those quantifiers where the actual number of matching relationships
        		 * counts.
        		 */
        		if (!negate && !any) {

        			/*
        			 * The strategy is to turn the query, which at this point looks like 
        			 * 
        			 * SELECT source_id AS object_id FROM object_relationships WHERE target_id IN 
        			 *      ( (SELECT object_id FROM object WHERE concept_id IN (3))) AND relationship_id = 2
        			 *  
        			 *  into something like 
        			 *  
        			 *  SELECT object_id FROM 
        			 *  
        			 *    (SELECT COUNT(source_id) as count, source_id AS object_id FROM object_relationships WHERE target_id IN 
        			 *      ( (SELECT object_id FROM object WHERE concept_id IN (3))) AND relationship_id = 2 GROUP BY source_id)
        			 *    AS qrel_x WHERE [count BETWEEN 1 AND 3]
        			 *    
        			 *  where the part in square brackets obviously depends on the quantifier. When
        			 *  the quantifier is ALL, we need to involve the relation catalog, which messes it
        			 *  up further.
        			 * 
        			 */
        			String rname = NameGenerator.newName("q");
        			
        			ret = 
        				"SELECT object_id FROM (" +
        				ret.replaceAll(
        						"@ZFIELDZ@",
        						"COUNT(" + fieldToSelect + ") as c" + rname + ", " + fieldToSelect + " AS object_id") +  
        				" GROUP BY " +
        				fieldToSelect + 
        				") AS " + rname + " WHERE " +
        				translateQuantifier(restriction.getQuantifier(), rid, rname);
        				
        		} else {
        			
        			// just substitute the field 
        			ret = 
        				ret.replaceAll("@ZFIELDZ@", fieldToSelect + " AS object_id" );
        		}
        	}
        	
		}
		
		return ret;
		
	}
	
	/**
	 * just create the constraint (the part after WHERE) to match the operator.
	 * @param restriction
	 * @return
	 * @throws ThinklabException 
	 */
	private String translateOperator(Restriction restriction) throws ThinklabException {
		
    	/* determine the common base type of the range, or throw an exception. */
    	IConcept rcls = 
    		KnowledgeManager.get().getLeastGeneralCommonConcept(
    				restriction.getProperty().getRange());
		
		if (rcls == null)
			throw new ThinklabStorageException("sql: can't determine class context for property" +
					restriction.getProperty() +
					"; property has unknown or heterogeneous range");

	      /*
	       * according to operator type, we may need different strategies.
	       * First we must determine the type of the related literal, so
	       * we can translate the operator.
	       */
	     TypeTranslator tt = getTypeTranslator(rcls);
	     
	     /* translate the operator and the arguments */
	     OpTranslator op = tt.getOperator(restriction.getOperator().getOperatorId());
	     
	     if (op == null) {
	    	 throw new ThinklabStorageException("sql: operator " +
	    			 restriction.getOperator() +
	    			 "(" +
	    			 rcls +
	    			 ") does not have a SQL translation");
	     }

	     if (op.argTypes.size() != restriction.getOperatorArguments().length) {
	    	 
	    	 throw new ThinklabStorageException("sql: operator " +
	    			 op +
	    			 " (" +
	    			 rcls +
	    			 " ) should take " +
	    			 op.argTypes.size() +
	    			 " arguments; " +
	    			 restriction.getOperatorArguments().length +
	    			 " were passed");		    			 
	     }

	     /* prepare arguments */
	     String[] aa = new String[op.argTypes.size()];
	     
	     for (int a = 0; a <restriction.getOperatorArguments().length; a++) {
	    	 if (op.argTemplates.get(a) == null || op.argTemplates.get(a).equals("")) {
	    		 aa[a] = restriction.getOperatorArguments()[a].toString();
	    	 } else {
	    		 String templ = op.argTemplates.get(a);
	    		 aa[a] = templ.replaceAll(
	    				 Pattern.quote("$$"), 
	    				 Matcher.quoteReplacement(
	    						 restriction.getOperatorArguments()[a].toString()));
	    	 }
	     }
	     
	     String ret = "";
	     
	     switch (op.type) {
	     
	     case INFIX:
	    	 ret += "(value " + op.sqlName + " " + aa[0];
	    	 break;
	     case POSTFIX:
	    	 /* hmmm.... */
	    	 break;
	     case PREFIX:
	    	 ret += "(" + op.sqlName + " value";
	     case FUNCTION:
	    	 ret += "(" + op.sqlName + "(value";
	    	 for (String z : aa) {
	    		 ret += ", " + z;
	    	 }
	    	 ret += ")";
	    	 break;
	     }
	     
	     ret += ")";
	     

	     return ret;
	}

	/**
	 * Return the SQL connector corresponding to the passed one. It's very dumb and
	 * does not handle anything properly except and and or, which I hope will be 
	 * ok for some time.
	 * 
	 * @param connector
	 * @return
	 */
	public static String getSQLConnector(LogicalConnector connector) {
 
		String ret = null;
		
		if (connector.equals(LogicalConnector.INTERSECTION)) {
           ret = "INTERSECT";
        } else if (connector.equals(LogicalConnector.UNION )) {
           ret = "UNION";
        }
		return ret;
	}

	private boolean getClosureInternal(IConcept c, ArrayList<Long> addTo) {
		
		if (c.equals(KnowledgeManager.Thing()))
			return false;
		
		Long id = type2ID.get(c.toString());
		
		if (id != null) {
			addTo.add(id);
		}
		
		for (IConcept cc : c.getChildren()) {
			if (!getClosureInternal(cc, addTo))
				return false;
		}
		
		return true;
	}
	
	/**
	 * Get a concept's "closure" in a kbox, meaning return all the subtypes of X that
	 * are represented in it. We only use the asserted hierarchy, but a possible 
	 * improvement could easily use the reasoner to build it. 
	 * 
	 * Another improvement could be cacheing the closure to speed things up, particularly
	 * if we use the reasoner. In that case we should either tag the kbox as static
	 * (insensitive to changes in the abox during its lifetime) or implement 
	 * refresh mechanisms through listeners on the repository (which at the time
	 * do not exist).
	 * 
	 * @return a comma-separated string with the type ids if we find at least
	 * one, an empty string (meaning a non-constraining condition) if the types include 
	 * Thing(), or null if no types matching the concept exist in the kbox.
	 */
	private String getTypeClosure(IConcept concept) throws ThinklabException {
		
		ArrayList<Long> list = new ArrayList<Long>();

		if (!getClosureInternal(concept, list))
			return "";
		
		String ret = null;
		
		for (Long l : list) {
			ret = 
				(ret == null ?
					l.toString() :
					(ret + "," + l));
		}
		
		return ret;
	}

	
	/**
	 * Generate the SQL code that stores the passed individual and execute it. Return
	 * the ID of the newly created instance.
	 * 
	 * @param c the IInstance we want to store.
	 * @return the ID of the stored instance in the kbox.
	 * @throws ThinklabStorageException 
	 */
	synchronized public String storeInstance(IInstance c, ISession session) throws ThinklabException {
		
		Pair<String, String> sql = storeInstanceSQL(c, session);
		if (sql != null && !sql.getSecond().equals(""))
			server.execute(sql.getSecond());
		return sql.getFirst();
		
	}
	

	/**
	 * Obtain the SQL source code that stores the passed instance and the ID of the
	 * instance in the new kbox defined by the storage. Keep all references global - i.e., 
	 * if two instances are stored consecutively and both have an object property that links
	 * to the same object, the object will not be duplicated.
	 * 
	 * @param c
	 * @param referenceTable a hash map that has been created outside. As long as the same
	 * table is passed between invocations, no duplication of instances will happen. 
	 * @return
	 * @throws ThinklabStorageException 
	 */
	public Pair<String, String> storeInstanceSQL(IInstance c, ISession session,
												 HashMap<String, String> referenceTable) 
				throws ThinklabException {
		
		if (referenceTable == null)
			referenceTable = new HashMap<String, String>();
		
		Pair<String, String> ret = 
			storeInstanceSQLInternal(c, "", 0, "", 0, referenceTable, session);
		
		return ret;
	}
	
	
	/**
	 * Obtain the SQL source code that stores the passed instance and the ID of the
	 * instance in the new kbox defined by the storage. Keep all references local - i.e., 
	 * if two instances are stored consecutively and both have an object property that links
	 * to the same object, the object will be duplicated. If this is not desired, use the 
	 * correspondent one with the reference table parameter.
	 * @param c
	 * @return
	 * @throws ThinklabStorageException 
	 */
	public Pair<String, String> storeInstanceSQL(IInstance c, ISession session)
		throws ThinklabException {
		
		HashMap<String, String> references = new HashMap<String, String>();
		
		Pair<String, String> ret = 
			storeInstanceSQLInternal(c, "", 0, "", 0, references, session);
		
		return ret;
	}
	
	/**
	 * Workhorse for storeInstanceSQL, called recursively. Nothing you want a user to see.
	 * @param c
	 * @param query
	 * @param relationshipID
	 * @param conceptID
	 * @param total
	 * @param references
	 * @param session
	 * @return two strings: the ID of the instance in the kbox and the SQL instructions 
	 * that store it
	 * @throws ThinklabStorageException 
	 */
	private Pair<String, String> storeInstanceSQLInternal(IInstance c,
			String query, long relationshipID, String conceptID, int totalRels,
			HashMap<String, String> references, ISession session)
			throws ThinklabException {
		
		String sql = query;

		/*
		 * 1. check if object has already been put in abox. If so, success with
		 * no effort.
		 */
		if (references.get(c.getLocalName()) != null)
			return new Pair<String, String>(references.get(c.getLocalName()), sql);

		/* Retrieve new ID for concept and its ancestor concepts. */
		Triple<String, Long, String> cid = getClassID(c, sql);
		sql = cid.getThird();

		/* update references catalog so we don't store it more than once */
		references.put(c.getLocalName(), cid.getFirst());

		/*
		 * generate insert instruction for concept. Exec all extensions and add
		 * that as well. If concept is not an instance, set flag to define a
		 * concept literal.
		 */
		sql += "INSERT INTO object VALUES ('" + cid.getFirst() + // object_id
				"', " + cid.getSecond() + // concept_id
				", " + totalRels + // total_rel
				", " + ((c instanceof IInstance) ? "true" : "false");

		/* add all extensions, calculated fields */
		TableDesc tab = getTableDescriptor("object");

		for (int i = 0; i < tab.system.size(); i++) {

			if (tab.system.get(i) == 0) {

				if (scriptLanguage.equals("MVEL")) {
				
					HashMap<String, IInstance> context = new HashMap<String, IInstance>();
					context.put("self", c);
					IValue v = Value.getValueForObject(MVEL.eval(tab.fieldValues.get(i), context));
					sql += ", " + translateLiteral(v, v.getConcept(), session);

				} else {
				
					try {
						/* obtain an algorithm from the string stored in XML */
						AlgorithmValue aa = (AlgorithmValue) KnowledgeManager.get()
							.validateLiteral(
									KnowledgeManager.get().requireConcept(
											scriptLanguage),
									tab.fieldValues.get(i));

						/*
						 * calculate field in context of instance, add proper
						 * representation
						 */
						IValue v = c.execute(aa, session);
						sql += ", " + translateLiteral(v, v.getConcept(), session);
					} catch (ThinklabException e) {
						throw new ThinklabStorageException(e);
					}
				}
			}
		}

		/* finish off */
		sql += ");\n";

		if (c instanceof IInstance) {

			/*
			 * we make a little hash to keep track of how many relationships of
			 * each kind we get, and with how many instances and literals. This
			 * speeds up concept reconstruction when we retrieve it.
			 * 
			 * This must be one of the weirdest types ever.
			 */
			HashMap<Long, Triple<Integer, Integer, Integer>> relCatalog = 
				new HashMap<Long, Triple<Integer, Integer, Integer>>();

			int lrel = 0;
			int crel = 0;
			int ccls = 0;

			/* retrieve all relationships */
			Collection<IRelationship> rels = null;
			try {
				rels = c.getRelationships();
			} catch (ThinklabException e) {
				throw new ThinklabStorageException(e);
			}

			for (IRelationship rel : rels) {
				
				/*
				 * retrieve relationship id. If new relationship, generate
				 * instruction to store it and relative table, using type
				 * translation table. We pass is_literal() to make sure we're
				 * not trying to store a literal for a class that does not admit
				 * it - that should be considered an internal error.
				 */
				Pair<String, Long> rrget = getRelationshipId(rel.getProperty().toString(),
						rel.getConcept(), rel.getValue(), session, sql, rel.isLiteral());

				sql = rrget.getFirst();
				long rid = rrget.getSecond();

				/* see how many rels with this property we've already seen */
				Triple<Integer, Integer, Integer> rii = relCatalog.get(rid);

				if (rii != null) {
					lrel = rii.getFirst();
					crel = rii.getSecond();
					ccls = rii.getThird();
				} else
					lrel = crel = ccls = 0;

				/*
				 * see how many of these relationships the concept has, so we
				 * can fill the tot field properly.
				 */
				int tot = 0;
				try {
					tot = c.getNumberOfRelationships(rel.getProperty()
							.toString());
				} catch (ThinklabException e) {
					throw new ThinklabStorageException(e);
				}

				if (rel.isLiteral()) {

					lrel++;

					/*
					 * if it's a literal, store text literal, using plugin - if
					 * any exist - to transform the var into a proper SQL
					 * literal.
					 */
					sql += "INSERT INTO literal_"
							+ rid
							+ // rel is in table name
							" VALUES ('"
							+ cid.getFirst()
							+ // object_id
							"', "
							+ rid
							+ // relationship id
							", "
							+ tot
							+ // total rels
							", "
							+ cid.getSecond()
							+ // concept_id
							", '"
							+ rel.getValue().getConcept()
							+ "', "
							+ translateLiteral(rel.getValue(), rel.getConcept(), session)
							+ ");\n";

				} else if (rel.isObject()) {
					
					crel++;

					/* it's a concept: retrieve its ID (store if necessary) */
					Pair<String, String> iid = storeInstanceSQLInternal(
							((ObjectReferenceValue) rel.getValue()).getObject(),
							sql, rid, cid.getFirst(), tot, references, session);

					sql = iid.getSecond();

					/* store relationship only */
					sql += "INSERT INTO object_relationships VALUES (" + rid + // rel
																				// id
							", '" + cid.getFirst() + // object_id
							"', '" + iid.getFirst() + // id of stored
							"');\n";

				} else if (rel.isClassification()) {

					ccls++;

					/*
					 * have a separate table with objectid/propid/conceptid,
					 * keep the numbers separate, and store.
					 */
					Pair<Long, String> l = getBareConceptID(rel.getConcept(),
							sql);

					sql = l.getSecond();

					sql += "INSERT INTO object_classifications VALUES (" + rid + // rel
																					// id
							", '" + cid.getFirst() + // object_id
							"', " + l.getFirst() + // id of class
							");\n";
				} else {
				
					/*
					 * should never happen, but it did.
					 */
					throw new ThinklabStorageException("internal: sql: relationship of no recognizable type");
				}

				/* put away the numbers */
				relCatalog.put(rid, new Triple<Integer, Integer, Integer>(lrel,
						crel, ccls));
			}

			/* generate catalog of rel numbers */
			for (Long riid : relCatalog.keySet()) {

				Triple<Integer, Integer, Integer> icp = relCatalog.get(riid);

				sql += "INSERT INTO rel_catalog VALUES ('" + cid.getFirst() + // concept_id
						"', " + riid + // relationship_id
						", " + icp.getFirst() + // n_lits
						", " + icp.getSecond() + // n_conc
						", " + icp.getThird() + // n_clas
						");\n";
			}
		}

		if (relationshipID == 0 && conceptID.equals("") && totalRels == 0) {
			/* update IDs to their current values only if we're the main concept */
			sql = updateIDs(sql);
		}

		return new Pair<String, String>(cid.getFirst(), sql);
	}
	
	/**
	 * Retrieve the list representation of the instance identified by ID, or
	 * null if there is no such instance. The resulting list can be used to
	 * create the instance in a session or ontology. Definitions of all linked
	 * instances are also included in the result. To avoid duplication of
	 * objects in successive invocations, use the version that takes a
	 * references table as a parameter.
	 * 
	 * @param id
	 * @return a list that defines the instance, or null.
	 * @throws ThinklabStorageException
	 */
	public Polylist retrieveObjectAsList(String id) throws ThinklabStorageException {
		
		Polylist ret = null;
	
		HashMap<String, String> refs = new HashMap<String, String>();
	
		try {
			ret = retrieveObjectAsListInternal(id, refs);
		} catch (SQLException e) {
			throw new ThinklabStorageException(e);
		}
		
		return ret;
	}
	
	
	/**
	 * Retrieve the list representation of the instance identified by ID, or
	 * null if there is no such instance. Pass a hash table to keep track of
	 * references, which can be passed to several invocations. If the same hash
	 * table is passed, no duplicate objects will be created, but references to
	 * already created ones will be used. It is the user's responsibility to
	 * guarantee the lifetime of those objects. The references table should be
	 * used with only one kbox and within the same session.
	 * 
	 * @param id
	 * @param references
	 * @return a list that defines the instance, or null.
	 * @throws ThinklabStorageException
	 */
	public Polylist retrieveObjectAsList(String id, HashMap<String, String> references) 
	throws ThinklabStorageException {	

		Polylist ret = null;
	
		try {
			ret = retrieveObjectAsListInternal(id, references);
		} catch (SQLException e) {
			throw new ThinklabStorageException(e);
		}
		
		return ret;
	}
	
	public Polylist retrieveObjectAsListInternal(String id, HashMap<String, String> refs) 
	throws ThinklabStorageException, SQLException {	
		
		ArrayList<Object> alist = new ArrayList<Object>();

		if (refs.get(databaseIDString + "#" + id) != null) {
			/* just return the reference */
			return Polylist.list("#" + id);
		} 

		/*
		 * ontology-assisted retrieval. A bit messy, but the alternative wastes
		 * so much space that I want to be messy and lean. retrieve concept and
		 * see what class it belongs to. Start list with type.
		 */
		QueryResult res = server.query("SELECT object_id, concept_id, is_instance, label, description FROM object " +
				"WHERE object_id = '" +	
				id + 
				"';");

		if (res.size() == 0)
			return null;

		String pc = id2Type.get(res.getLong(0,1));

		if (pc == null) 
			throw new ThinklabStorageException("sql: can't find parent concept " +
					res.getString(0,1) + 
					" for " +
					id +
					": database is corrupted");
				       

		/* memorize instance encountered for posterity */
		refs.put(databaseIDString + "#" + id, "true");
		
		boolean isInstance = res.getBoolean(0,2);

		if (isInstance) {
			
			/* add type and note instance id */
			alist.add(pc + "#" + id);

			/* add label and description if any */
			String label = res.getString(0, 3);
			String descr = res.getString(0, 4);
			
			if (!label.equals(""))
				alist.add(Polylist.list("rdfs:label", label));

			if (!descr.equals(""))
				alist.add(Polylist.list("rdfs:comment", descr));
			
			
			/* use catalog to determine which literals to look into */
			QueryResult rst = 
				server.query("SELECT object_id, relationship_id, n_lits, n_conc, n_clas " +
						"FROM rel_catalog WHERE object_id = '" + id + "';");


			for (int row = 0; row < rst.nRows(); row++) {

				String pr = id2Property.get(rst.getLong(row, 1));

				int nl = rst.getInt(row, 2);
				int ni = rst.getInt(row, 3);
				int nc = rst.getInt(row, 4);

				if (nl > 0) {

					/* add all literals from respective shitters */
					QueryResult rsq = server.query(
							"SELECT object_id, value, type_id FROM literal_"
							+ rst.getString(row,1)
							+ " WHERE object_id = '"
							+ id + "';");

					for (int drow = 0; drow < rsq.nRows(); drow++) {
						alist.add(Polylist.list(pr, 
								Polylist.list(rsq.getString(drow, 2), rsq.getString(drow, 1))));
						/* 
						 * just the property and the value seem to work with the improved
						 * list reader.
						 */
						//alist.add(Polylist.list(pr, rsq.getString(drow, 1)));
					}
				}

				if (ni > 0) {

					QueryResult rsq = server.query(
							"SELECT source_id, target_id, relationship_id "
							+ "FROM object_relationships WHERE source_id = '"
							+ id
							+ "' AND relationship_id = "
							+ rst.getString(row, 1) + ";");

					for (int drow = 0; drow < rsq.nRows(); drow++) {
						alist.add(Polylist.list(pr,
								retrieveObjectAsListInternal(rsq
										.getString(drow, 1), refs)));
					}
				}

				if (nc > 0) {

					QueryResult rsq = server.query("SELECT source_id, source_id, class_id "
							+ "FROM object_classifications WHERE source_id = '"
							+ id
							+ "' AND relationship_id = "
							+ rst.getString(row, 1) + ";");

					for (int drow = 0; drow < rsq.nRows(); drow++) {
						alist.add(Polylist.list(pr, retrieveClassFromID(rsq.getLong(drow, 2))));
					}
				}
			}
		} else {
			
			// just a concept
			try {
				alist.add(KnowledgeManager.get().requireConcept(pc));
			} catch (ThinklabException e) {
				throw new ThinklabStorageException(e);
			}
		}

		return Polylist.PolylistFromArray(alist.toArray());
	}

		
	private String retrieveClassFromID(long id) throws ThinklabStorageException {
		String ret = id2Type.get(id);
		if (ret == null)
			throw new ThinklabStorageException("sql: no class corresponds to id " + id);
		return ret;
	}
		
	public Properties getProperties() {
		return properties;
	}
	
    /**
	 * returns SQL instructions that will initialize the database (empty string
	 * if it's already initialized). The URI path should specify additional
	 * schemata to load in order. E.g., if we want to read the time extensions
	 * and the geos extensions before initializing database mydata from
	 * <packagedir>/pg/schemata/...xml, the (maximal) URI will be
	 * 
	 * pg://<user>:<pass>@<host>:<port>/datetime/geos/mydata
	 * 
	 * A barebones postgres abox on database mydata on localhost using default
	 * access info will simply be
	 * 
	 * pg://localhost/mydata
	 * 
	 * TODO: ensure that registering a new kbox protocol defines the
	 * corresponding URLStreamHandler in URLStreamHandlerFactory (see URL
	 * javadocs) so the protocol is recognized and default ports can be
	 * established. This must be done elsewhere, when we get here we already
	 * have a URL so the protocol must be known.
     * @param properties 
	 * 
	 * @throws ThinklabStorageException
	 *             if URL is malformed or unrecognized
     * @throws SQLException 
	 */
    protected synchronized void initialize(String protocol, Properties properties) throws ThinklabException {
    	    	
		/* read core schema */
		try {
			readSchema(SQLPlugin.get().getSchema(protocol));
		} catch (ThinklabException e) {
			throw new ThinklabStorageException(e);
		}
			
		this.properties = properties;
    			    	        
		/* 
		 * read any other schemata mentioned in properties 
		 */
		String schemata = properties.getProperty(IKBox.KBOX_SCHEMA_PROPERTY);

		useLocalNames = 
			BooleanValue.parseBoolean(
					properties.getProperty(IKBox.KBOX_LOCALNAMES_PROPERTY, "true"));
		
		if (schemata != null && !schemata.equals("")) {
			
			String[] sch = schemata.split(",");
			for (String ss : sch)
				loadSchema(ss.trim());
		}
		
    	/* check if we need to create schema */
    	if (!isStorageInitialized()) {
    		
    		log.info("initializing database " + server.getDatabase() + "...");
    		
    		/* create SQL instructions from schema and run them */
    	    for (int i = 0; i < tables.size(); i++)
    	    	if (!tables.get(i).isTemplate) {
    	    		server.execute(tables.get(i).creationCode());
    	    	}

    		log.info("done initializing database " + server.getDatabase() + ".");

    	}

    	/* initialize ID table */
    	initializeIDs();

    	/* load all the classes and relationships we know about in the 
    	   dictionaries. */
    	QueryResult rsq = server.query("SELECT concept_type, concept_id, concept_def FROM concept;");

    	for (int row = 0; row < rsq.nRows(); row++) {

    		String s = rsq.getString(row, 0);
    		long id = rsq.getLong(row, 1);

    		type2ID.put(s, id);
    		id2Type.put(id, s);
    	}

		rsq = server.query("SELECT relationship_type, relationship_id FROM relationship;");

		for (int row = 0; row < rsq.nRows(); row++) {

			String s = rsq.getString(row, 0);
			long id = rsq.getLong(row, 1);

			property2ID.put(s, id);
			id2Property.put(id, s);
		}
    	
    }
	

	protected boolean isStorageInitialized() throws ThinklabStorageException {
		// just check if we have key tables
		return server.haveTable("object");
	}

	private void loadSchema(String schemaID) throws ThinklabException {
		
		URL schema = null;
		
		for (URL sch : SQLPlugin.get().schemata) {
			if (sch.toString().endsWith(schemaID + ".sqx")) { 
				schema = sch;
				break;
			}
		}

		if (schema == null) {
			throw new ThinklabIOException("schema " + schemaID + " referenced in kbox is not installed");
		}
		
		this.readSchema(schema);
		
		SQLPlugin.get().logger().info("sql: reading schema " + schema);
	}
	
	protected void readSchema(URL f) throws ThinklabStorageException {

		XMLDocument doc = null;

		try {
			doc = new XMLDocument(f);
		} catch (Exception e) {
			throw new ThinklabStorageException(e);
		}

		for (Node n = doc.root().getFirstChild(); n != null; n = n
				.getNextSibling()) {

			if (n.getNodeName().equals("schema")) {

				for (Node nn = n.getFirstChild(); nn != null; nn = nn
						.getNextSibling()) {

					if (!nn.getNodeName().equals("table"))
						continue;
					
					String tname = XMLDocument.getAttributeValue(nn, "name", "");

					/* retrieve table from repository, creating if necessary */
					TableDesc td = null;
					for (TableDesc ttd : tables) {
						if (ttd.name.equals(tname)) {
							td = ttd;
							break;
						}
					}
					if (td == null) {
						td = new TableDesc(tname);
					}

					td.readXML(nn);
					
					tables.add(td);
					
				}
			} else if (n.getNodeName().equals("type-translation")) {

				for (Node nn = n.getFirstChild(); nn != null; nn = nn
						.getNextSibling()) {

					TypeTranslator tt = new TypeTranslator();
					tt.semanticType = XMLDocument.getAttributeValue(nn, "name", "");

					String bo = XMLDocument.getAttributeValue(nn, "based-on");

					if (bo != null) {

						TypeTranslator pi = typeTranslators.get(bo);

						if (pi == null)
							throw new ThinklabStorageException("sql: schema "
									+ MiscUtilities.getNameFromURL(f.toString()) + ": cannot define "
									+ tt.semanticType
									+ " based on undefined type " + bo);

						tt.copy(pi);
					}

					for (Node ss = nn.getFirstChild(); ss != null; ss = ss
							.getNextSibling()) {

						if (ss.getNodeName().equals("sql-literal-format")) {

							tt.fLiteral = XMLDocument.getNodeValue(ss);
							tt.literalIsPlugin = BooleanValue
									.parseBoolean(XMLDocument
											.getAttributeValue(ss, "use-plugin", "false"));

						} else if (ss.getNodeName().equals("variable")) {
							
							tt.variables.add(
									new Pair<String, String>(
										XMLDocument.getAttributeValue(ss, "id", ""),
										XMLDocument.getNodeValue(ss)));
							
						} else if (ss.getNodeName().equals("sql-type")) {

							tt.sqlType = XMLDocument.getNodeValue(ss);
							String aa = XMLDocument.getAttributeValue(ss,
									"create-method", "");
							tt.createAsStatement = aa.equals("statement");

						} else if (ss.getNodeName().equals("sql-operator")) {

							OpTranslator td = new OpTranslator();

							td.jimtName = XMLDocument.getAttributeValue(ss,
									"imt-name", "");
							td.sqlName = td.funcTemplate = XMLDocument
									.getAttributeValue(ss, "sql-translation", "");

							String optype = XMLDocument.getAttributeValue(ss,
									"operator-type", "");

							if (optype.equals("infix"))
								td.type = OpType.INFIX;
							else if (optype.equals("prefix"))
								td.type = OpType.PREFIX;
							else if (optype.equals("postfix"))
								td.type = OpType.POSTFIX;
							else if (optype.equals("function"))
								td.type = OpType.FUNCTION;

							for (Node oa = ss.getFirstChild(); oa != null; oa = oa
									.getNextSibling()) {

								if (oa.getNodeName().equals("argument")) {

									td.argTypes.add(XMLDocument
											.getAttributeValue(oa, "type", ""));
									td.argTemplates.add(XMLDocument
											.getAttributeValue(oa, "template", ""));

								} else if (oa.getNodeName().equals(
										"function-template")) {

									td.funcTemplate = XMLDocument.getNodeValue(oa);
								}
							}

							tt.operators.add(td);
						}
					}
					typeTranslators.put(tt.semanticType, tt);
				}
			}
		}
	}

	public static String getAllObjectsQuery() {
		return  "SELECT object_id FROM object";		
	}
	
	public static String addSchemaFieldsToQuery(String query, Polylist resultSchema) throws ThinklabException {
		
		if (resultSchema == null || resultSchema.isEmpty()) 
			return query;
		
		int cnt = 0;
		String fields = "";
		Object[] flds = resultSchema.array();
		for (Object o : flds) {
			fields += o.toString();
			if (cnt++ < (flds.length - 1))
				fields += ", ";
		}
		
		if (!query.startsWith("SELECT object_id FROM object"))
			throw new ThinklabInternalErrorException("sql: query does not start with expected format when adding schema fields");
		
		query = 
			"SELECT object_id, " + 
			fields + 
			query.substring(16);
		
		return query;
		
	}

	public static String addLimitsToQuery(String query, int offset, int maxResults) {
		// TODO Auto-generated method stub
		return query;
	}

	/*
	 * FIXME this probably needs some serious work
	 */
	public static String addMainObjectConstraintToQuery(String query) {
		return query + " WHERE total_rel = 0";
	}
	
	public SQLThinklabServer(String protocol, SQLServer server, Properties properties) throws ThinklabException {

		this.server = server;		
		this.databaseIDString = NameGenerator.newName("DB" + protocol);
		
		/*
		 * initialize the language for calculating scripted fields
		 */
		scriptLanguage = properties.getProperty("sql.script.language",
				SQLPlugin.get().getProperties().getProperty(
						"sql.script.language", "MVEL"));
		
		/*
		 * define how we want to use the mock-reasoner
		 */
		String ur =  
					properties.getProperty("sql.use.restrictions",
							SQLPlugin.get().getProperties().getProperty(
									"sql.use.restrictions", "partial"));
		
		if (ur.toLowerCase().equals("true"))
			useRestrictions = UseRestrictions.TRUE;
		else if (ur.toLowerCase().equals("false"))
			useRestrictions = UseRestrictions.FALSE;
		else 
			useRestrictions = UseRestrictions.PARTIAL;
			
						
		
		initialize(protocol, properties);
		
	}

}