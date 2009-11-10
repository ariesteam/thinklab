/**
 * SearchEngine.java
 * ----------------------------------------------------------------------------------
 * 
 * Copyright (C) 2008 www.integratedmodelling.org
 * Created: Jan 21, 2008
 *
 * ----------------------------------------------------------------------------------
 * This file is part of ThinklabSearchEnginePlugin.
 * 
 * ThinklabSearchEnginePlugin is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * ThinklabSearchEnginePlugin is distributed in the hope that it will be useful,
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
 * @date      Jan 21, 2008
 * @license   http://www.gnu.org/licenses/gpl.txt GNU General Public License v3
 * @link      http://www.integratedmodelling.org
 **/

package org.integratedmodelling.searchengine;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Hashtable;
import java.util.Properties;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexModifier;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockObtainFailedException;
import org.integratedmodelling.searchengine.exceptions.ThinklabInvalidIndexException;
import org.integratedmodelling.searchengine.exceptions.ThinklabInvalidQueryException;
import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabIOException;
import org.integratedmodelling.thinklab.exception.ThinklabResourceNotFoundException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.IKnowledgeRepository;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;
import org.integratedmodelling.thinklab.interfaces.knowledge.IKnowledgeSubject;
import org.integratedmodelling.thinklab.interfaces.knowledge.IOntology;
import org.integratedmodelling.thinklab.interfaces.knowledge.IRelationship;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;
import org.integratedmodelling.thinklab.interfaces.query.IQueriable;
import org.integratedmodelling.thinklab.interfaces.query.IQuery;
import org.integratedmodelling.thinklab.interfaces.query.IQueryResult;
import org.integratedmodelling.thinklab.interfaces.storage.IKBox;
import org.integratedmodelling.thinklab.kbox.KBoxManager;
import org.integratedmodelling.thinklab.literals.BooleanValue;
import org.integratedmodelling.thinklab.literals.ObjectReferenceValue;
import org.integratedmodelling.thinklab.owlapi.Session;
import org.integratedmodelling.thinklab.plugin.ThinklabPlugin;
import org.integratedmodelling.utils.CopyURL;
import org.integratedmodelling.utils.MiscUtilities;

/**
 * A search index for thinklab. The plugin maintains a list of these. Should only be created
 * through the plugin.
 * 
 * @author Ferdinando Villa
*/
public final class SearchEngine implements IQueriable {

	/* cache directories */
	File docCacheDir = null;
	File ontCacheDir = null;
	File kboxCacheDir = null;
	
	Properties properties = null;
	
	String indexedOntologies = null;

	private class IndexField {
		// link or text
		public String indexType;
		public String property;
		public double weight = 1.0;
	}
	
	/*
	 * the array of fields to index may be empty, meaning that we want to index
	 * all frickin' fields.
	 */
	ArrayList<IndexField> indexedFields = new ArrayList<IndexField>();
	
    /* main Lucene analyzer */
    Analyzer analyzer;
    IndexModifier index;

    /* options */
    boolean indexIndividuals = false;
    boolean indexUncommented = false;
    private String indexPath = null;

	private ArrayList<IKBox> kBoxes;
	private ArrayList<IOntology> ontologies;
	private String[] iTypes = null;
	private String id;
	
	/*
	 * track while being reindexed
	 */
	private boolean isSynchronized = true;
	
	private class CacheEntry implements Serializable {

		private static final long serialVersionUID = -3891738499729676230L;
		
		String entryID;
		long entryDate = 0;
		String entryType;
		String localCopy;
		
		public CacheEntry(String id) {
			entryID = id;
		}
		
	}

	private class Cache extends Hashtable<String, CacheEntry> {

		public CacheEntry getEntry(String id) {

			CacheEntry c = get(id);
			if (c == null) {
				c = new CacheEntry(id);
				put(id, c);
			}
			return c;
		}

	}
	
	private Cache cache = null;
	private ThinklabPlugin declaringPlugin;
	
	private void readCache() throws ThinklabIOException {
		
		File inp = new File(ontCacheDir + "/cache.obj");
		
		if (inp.exists()) {
			try {
				FileInputStream istream = new FileInputStream(inp);
				ObjectInputStream q = new ObjectInputStream(istream);
				cache = (Cache) q.readObject();
			} catch (Exception e) {
				throw new ThinklabIOException("searchengine: " + id + ": can't read object cache");
			}
		} else {
			cache = new Cache();
		}
	}
	
	private void writeCache() throws ThinklabIOException {
		
		if (cache.size() > 0) {
			File inp = new File(ontCacheDir + "/cache.obj");
//			try {
//	
// FIXME not serializable as is	
//				FileOutputStream ostream = new FileOutputStream(inp);
//				ObjectOutputStream p = new ObjectOutputStream(ostream);
//				p.writeObject(cache);
//				p.flush();
//				ostream.close();
//				
//			} catch (Exception e) {
//				throw new ThinklabIOException("searchengine: " + id + ": can't write object cache");
//			}
		}
	}
	

    SearchEngine(String id, Properties properties) throws ThinklabException {

    	this.properties = properties;
    	this.id = id;
    	
    	this.indexIndividuals = 
    		BooleanValue.parseBoolean(
    				properties.getProperty(
    						SearchEnginePlugin.SEARCHENGINE_INDEX_INDIVIDUALS_PROPERTY,
    						"false"));
    	
    	this.indexPath   = 
    		properties.getProperty(
    				SearchEnginePlugin.SEARCHENGINE_INDEX_PATH_PROPERTY,
    				SearchEnginePlugin.get().getScratchPath() + "/" + id + "/index");
    	
    	this.indexUncommented = 
    		BooleanValue.parseBoolean(
    				properties.getProperty(
    						SearchEnginePlugin.SEARCHENGINE_INDEX_UNCOMMENTED_PROPERTY,
    						"false"));

    	this.indexedOntologies   = 
    		properties.getProperty(
    				SearchEnginePlugin.SEARCHENGINE_INDEX_ONTOLOGIES_PROPERTY,
    				"");
    	
    	String itypes = 
    		properties.getProperty(
    				SearchEnginePlugin.SEARCHENGINE_INDEX_TYPES_PROPERTY,
    				"");
    	
    	if (!itypes.equals("")) {
    		iTypes = itypes.trim().split(",");
    	}
    	
    	/* make sure we have all cache dirs */
    	File scratchDir = SearchEnginePlugin.get().getScratchPath();
    	
    	docCacheDir = new File(scratchDir + "/cache/" + id + "/doc");
    	ontCacheDir = new File(scratchDir + "/cache/" + id + "/ontology");
    	kboxCacheDir = new File(scratchDir + "/cache/" + id + "/kbox");
    	
    	docCacheDir.mkdirs();
    	ontCacheDir.mkdirs();
    	kboxCacheDir.mkdirs();
    	
    	/* create all caches and initialize them from their dir contents */
    	
    	
    	// create stated analyzer, defaulting to standard (English).
    	String analyzerClass =
    		properties.getProperty(
    				SearchEnginePlugin.SEARCHENGINE_ANALYZER_CLASS_PROPERTY,
    				"org.apache.lucene.analysis.standard.StandardAnalyzer");
    	
		try {
			Class<?> aClass = Class.forName(analyzerClass);
	    	analyzer = (Analyzer) aClass.newInstance();
		} catch (Exception e1) {
			throw new ThinklabResourceNotFoundException(
					"searchengine: " + 
					id + 
					": can't create analyzer: " +
					e1.getMessage());
		}
	
    	/* create or open existing index */
    	boolean create_index = !IndexReader.indexExists(indexPath);
    	
    	try {
    		/*
    		 * We only initialize each engine once, so if we find a lock, it must be stale.
    		 */
    		if (IndexReader.isLocked(indexPath)) {
    			IndexReader.unlock(FSDirectory.getDirectory(indexPath, false));
    		}
    		index = new IndexModifier(indexPath, analyzer, create_index);
    	} catch (IOException e) {
    		throw new ThinklabIOException(e);
    	}
    }

    public boolean isOntologyIncluded(String name) {
    	return 
    	// FIXME this check is weak
    	(indexedOntologies.contains("all") && !indexedOntologies.contains("!" + name)) || 
    		indexedOntologies.contains(name);
    }
    
    public boolean isOntologySkipped(String name) {
    	return  !isOntologyIncluded(name);
    }
    
    public boolean isConceptIncluded(IInstance concept) {
    	return true;
    }

    public String getID() {
    	return id;
    }
    
    public Collection<IKBox> getKBoxes() throws ThinklabException {
    	
    	if (this.kBoxes == null) {
    		
    		this.kBoxes = new ArrayList<IKBox>();

    		String kBoxList   = 
    			properties.getProperty(
    					SearchEnginePlugin.SEARCHENGINE_KBOX_LIST_PROPERTY,
    					"");

    		if (kBoxList == null || kBoxList.trim().equals(""))
    			return this.kBoxes;
    		
    		String[] kboxx = kBoxList.split(",");

    		for (String kbox : kboxx) {
    			
    			/* just retrieve it, initializing what needs to */
    			IKBox kb = KBoxManager.get().retrieveGlobalKBox(kbox);
    			if (kb == null) {
    				throw new ThinklabIOException("searchengine: " + id + ": failed to open kbox " + kbox);
    			} 
    			kBoxes.add(kb);
    		}
    	}
    	
    	return this.kBoxes;
    }
    
    /**
     * Destroy the search engine persistent data, but not its configuration. Calling
     * clear() and initialize() should build a fresh index from the configured
     * sources.
     * @throws ThinklabIOException 
     */
    public void clear() throws ThinklabIOException {
    	
    	try {
    		
			// index.close();
    	
			File ipath = new File(indexPath);
    	 
    		if (IndexReader.isLocked(indexPath)) {
    				IndexReader.unlock(FSDirectory.getDirectory(indexPath, false));
    		}

    		if (ipath.exists()) {
    			MiscUtilities.deleteDirectory(ipath);
    		}
    		
    		/* reopen */
			index = new IndexModifier(indexPath, analyzer, true);
			
		} catch (Exception e) {
    		throw new ThinklabIOException(e);
		}

    }
    
    /**
     * Check that all caches are current and things are up to date. 
     * Should the process be spawned as a separate thread?
     * 
     * @throws ThinklabIOException 
     * @throws ThinklabException 
     *
     */
    public void initialize() throws ThinklabException {
    	
    	isSynchronized = false;
    	
    	// initialize cache
    	readCache();
    	
       	/* index all configured ontologies */
    	for (IOntology o : getOntologies()) {
    		
    			SearchEnginePlugin.get().logger().info("search engine: " + id + ": indexing " + o);     			
    			indexModel(o);
    	}
    	
    	/*
    	 * index all configured kboxes
    	 */
    	for (IKBox kb : getKBoxes()) {
   			indexKBox(kb);
    	}
    	
		/* write back cache */
		writeCache();

		/*
    	 * close index
    	 */
    	try {
			index.close();
		} catch (IOException e) {
			throw new ThinklabIOException(e);
		}
		
		isSynchronized = true;
    }
    
    public boolean isSynchronized() {
    	return isSynchronized;
    }
    
    private Collection<IOntology> getOntologies() throws ThinklabException {
    	
    	if (this.ontologies == null) {

        	IKnowledgeRepository rep = KnowledgeManager.get().getKnowledgeRepository();
    		this.ontologies = new ArrayList<IOntology>();

    		String olist   = 
    			properties.getProperty(
    					SearchEnginePlugin.SEARCHENGINE_INDEX_ONTOLOGIES_PROPERTY,
    					"");

    		if (olist == null || olist.trim().equals(""))
    			return this.ontologies;
    		
    		String[] oolist = olist.split(",");

    		for (String ont : oolist) {
    			this.ontologies.add(rep.requireOntology(ont));
    		}
    	}
    	
    	return this.ontologies;
	}

	private void indexKBox(IKBox kb) throws ThinklabException {
    	
		IQueryResult qr = kb.query(null);
		ISession session = new Session();
		
		for (int i = 0; i < qr.getResultCount(); i++) {
			
			IValue val = qr.getResult(i, session);
			IInstance ii = null;
			
			if (val instanceof ObjectReferenceValue) {
				ii = ((ObjectReferenceValue)val).getObject();
				try {
					index.addDocument(submitIndividual(kb, ii));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					throw new ThinklabIOException(e);
				}
			}

			// TODO may want to delete the object, although for very linked kboxes that could
			// just decrease performance.
		}
	}


    public void indexModel(IOntology o) throws ThinklabException {

    	CacheEntry ontEntry = cache.getEntry(o.getConceptSpace());
    	
    	/* TODO: see if indexed recently */
    	if (false && ontEntry.entryDate >= o.getLastModificationDate()) {
    		SearchEnginePlugin.get().logger().info("searchengine: index of " + o + " is up to date: skipping");
    		return;
		}
    	
    	ontEntry.entryDate = new Date().getTime();

    	/* iterate over all concepts */
    	for (IConcept c : o.getConcepts()) {
    		// submit concept for indexing
    		SearchEnginePlugin.get().logger().info("searchengine: indexing concept " + c);
    		submitConcept(c);
    	}

    	/* iterate over all individuals if requested */
    	if (indexIndividuals) {
    		for (IInstance i : o.getInstances()) {
    			// submit concept for indexing
    			SearchEnginePlugin.get().logger().info("searchengine: indexing individual " + i);
    			submitIndividual(null, i);
    		}
    	}
    }

    
    Document submitExternalResource(String uri) throws ThinklabException {

    	Document ret = null;

		CacheEntry cacheEntry = cache.getEntry(uri);
		
		String fname = MiscUtilities.getFileName(uri);
		File outfile = new File(docCacheDir + "/" + fname);

		if (declaringPlugin != null) {
			
			URL resource = declaringPlugin.getResourceURL(uri);
			if (resource != null)
				CopyURL.copy(resource, outfile);
			
		} else {
			if (uri.contains("://")) {
				try {
					CopyURL.copy(new URL(uri), outfile);				
				} catch (MalformedURLException e) {
					throw new ThinklabValidationException("cannot access external resource using malformed URI " + uri);
				}
			} else { 
				outfile = new File(uri);
				if (!outfile.exists()) {
					throw new ThinklabIOException("cannot access external resource using locator " + uri);
				}
			}
		}
		
		if (outfile == null || !outfile.exists()) {
			SearchEnginePlugin.get().logger().warn("resource " + uri + " could not be found for indexing");
			return null;
		}
		
		if (outfile.toString().endsWith(".pdf")) {
//			try {
//				SearchEnginePlugin.get().logger().warn("converting PDF document from " + uri);
//				ret = new LucenePDFDocument().convertDocument(outfile);
//			} catch (IOException e) {
//				throw new ThinklabIOException(e);
//			}
		} else if (outfile.toString().endsWith(".html") || outfile.toString().endsWith(".htm")) {
			/* TODO */
		}
			
    	return ret;
    }
    
    /**
     * Merge all field of source document into destination document using the 
     * given weight
     * 
     * @param source
     * @param destination
     * @param weigth
     */
    void mergeDocuments(Document source, Document destination, float weight) {
    
    	// should be ok?
    	for (Object f : source.getFields()) {
    		Field field = (Field)f;
    		field.setBoost(field.getBoost()*weight);
    		destination.add(field);
    	}
    	
    }
    
    private Document indexMetadata(IKnowledgeSubject object, IKBox kbox) {
    	
    	Document d = null;
    	
    	/* tbi we should loop over all supported languages */
		String c = object.getDescription();
		String l = object.getLabel();

		if (indexUncommented && (l == null || l.equals(""))) {
			l = object.getLocalName();
		}
		
		String id = object.getSemanticType().toString();
		if (kbox != null) {
			id = kbox.getUri() + "#" + object.getLocalName();
		}
		
		/*
		 * create one Lucene document for each class with at least one
		 * annotation property
		 */
		if (c != null || l != null) {

			d = new Document();

			d.add(new Field("id", id,
					Field.Store.YES, Field.Index.NO));

			if (c != null)
				d.add(new Field("rdfs:comment", c, Field.Store.YES,
						Field.Index.TOKENIZED));
			if (l != null)
				d.add(new Field("rdfs:label", l, Field.Store.YES,
						Field.Index.TOKENIZED));

			/*
			 * add concept space so we can delete all concepts quickly. Needs to
			 * be indexed or deleteDocuments won't work.
			 */
			//d.add(new Field("ks", object.getConceptSpace(), Field.Store.YES,
			//		Field.Index.UN_TOKENIZED));

			/*
			 * TODO add ontology version, date, property descriptions, other
			 * metadata. In searching, property description fields should be
			 * used with a lower boost factor.
			 */
		}
		
		return d;

    }
    
    /*
     * 
     * 
	 * we could use one function taking an OntResource, but I prefer to have
	 * them separated to possibly handle things differently later
	 */
	public Document submitConcept(IConcept cl) throws ThinklabIOException {

		Document d = indexMetadata(cl, null);
		
		if (d != null) {
		
			try {
				index.addDocument(d);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				throw new ThinklabIOException(e);
			}
		}
		
		return d;
	}

	/**
	 * @param i
	 */
	public Document submitIndividual(IKBox kbox, IInstance i) throws ThinklabException {

    	/* first check if we want to index this object at all - based on the class */
		if (!checkObjectClass(i.getDirectType())) {
			return null;
		}
    	
		/* TODO check cache to see if document needs to be rebuilt */
		CacheEntry cacheEntry = cache.getEntry(i.toString());
		
    	/* check if object has been indexed already; if so only proceed if we're indexing as
    	 * a sub-object */
    	
    	/* index label and comment */
		Document d = indexMetadata(i, kbox);
		
		/*
		 * put in the rest as specified
		 */
		for (IndexField f : indexedFields) {
						
			/*
			 * basic RDF metadata are indexed already
			 */
			if (f.property.equals("rdfs:comment") || f.property.equals("rdfs:label"))
				continue;
			
			for (IRelationship r : i.getRelationships(f.property)) {

				/* could be literal, object, classification, but we just use what the user has told us. */
				if (f.indexType.equals("store")) {
					
					/* make field, do not index */
					String value = r.getValue().toString();
					Field field = new Field(r.getProperty().toString(), value, Field.Store.YES, Field.Index.NO);
					d.add(field);
					
				} else if (f.indexType.equals("text")) {
					
					/* make field */
					String value = r.getValue().toString();
					Field field = new Field(r.getProperty().toString(), value, Field.Store.YES, Field.Index.TOKENIZED);
					d.setBoost((float)f.weight);
					d.add(field);
					
				} if (f.indexType.equals("download")) {

					/*
					 * download linked text and if OK, index contents and link it to main
					 * document.
					 */
					String uri = r.getValue().toString();
					
					if (uri == null || uri.trim().equals(""))
						continue;
					
					Document td = submitExternalResource(uri);
					
					if (td != null) {
						mergeDocuments(td, d, (float)f.weight);
					}
					
				} else if (f.indexType.equals("link") && r.isObject()) {

					/* create document for linked object and link it to main document, multiplying the
					 * intrinsic weight of the fields by the weigth factor. */
					IInstance inst = r.getValue().asObjectReference().getObject();
					
					if (inst != null) {
						Document ld = submitIndividual(kbox, inst);
						
						if (ld != null) {
							mergeDocuments(ld, d, (float)f.weight);
						}
					}
				}

				
			}
		}
		
		for (IRelationship r : i.getRelationships()) {
		
	
		}
		
		return d;
	}
    
    private boolean checkObjectClass(IConcept directType) throws ThinklabException {

		boolean ret = iTypes == null;
			
		if (!ret) {
			for (String s : iTypes) {
				IConcept c = KnowledgeManager.get().retrieveConcept(s);
				
				if (c != null && directType.is(c)) {
					ret = true;
					break;
				}
			}
		}
		
		return ret;
    
    }

	/*
	 * Use this one to parse a query - must be synchronized as the parser is not
	 * thread safe.
	 */
    protected static synchronized Query parseQuery(String query, QueryParser parser) throws ParseException {
    	return parser.parse(query);
    }

	public IQueryResult query(IQuery q, int offset, int maxResults)
			throws ThinklabException {
		return query(q, null, offset, maxResults);
	}

	public IQueryResult query(IQuery q, String[] metadata, int offset, int maxResults) 
		throws ThinklabException {

		ResultContainer ret = new ResultContainer(this, q, offset, maxResults);
		
		if ( !(q instanceof QueryString)) 
			throw new ThinklabValidationException("search engine: only textual query strings are admitted");
		
		// TODO parameterize from properties
		String[] searchFields = new String [indexedFields == null ? 0: indexedFields.size()];
		
		if (indexedFields != null)
			for (int i = 0; i < indexedFields.size(); i++)
				searchFields[i] = indexedFields.get(i).property.toString();
		
    	IndexSearcher isearch = null;
    	
    	MultiFieldQueryParser parser = new MultiFieldQueryParser(searchFields, analyzer);
    	try {
			isearch = new IndexSearcher(indexPath);
		} catch (IOException e1) {
			throw new ThinklabInvalidIndexException(e1);
		}

    	try {
    		
			Query qr = parseQuery(q.asText(), parser);
			Hits hits = isearch.search(qr);			
			ret.setResultCount(hits.length());;
			
			if (maxResults == -1)
				maxResults = hits.length() - offset;
			
			for (int i = offset; (i < offset + maxResults) && (i < hits.length()); i++) {
				
                Document doc = hits.doc(i);
                float score = hits.score(i)/hits.score(0);
                ret.addDocument(doc, score);
	        }
			
		} catch (Exception e) {
			throw new ThinklabInvalidQueryException(e);
		}
		
		return ret;

	}

	public IQueryResult query(IQuery q) throws ThinklabException {
		return query(q, null, 0, -1);
	}

	public IQuery parseQuery(String toEval) throws ThinklabException {
		return new QueryString(toEval);
	}

	public void addIndexField(String property, String itype, double weigh) {
		
		IndexField inf = new IndexField();
		
		inf.indexType = itype;
		inf.weight = weigh;
		inf.property = property;
		
		indexedFields.add(inf);
	}

	public void setResourceFinder(ThinklabPlugin resourceFinder) {
		this.declaringPlugin = resourceFinder;
	}

	/**
	 * Redefine the ontologies we want to index. Should be followed (or preceded)
	 * by clear() and followed by initialize().
	 * 
	 * @param ontprop
	 */
	public void setOntologies(String ontprop) {

		properties.setProperty(
				SearchEnginePlugin.SEARCHENGINE_INDEX_ONTOLOGIES_PROPERTY,
				ontprop);
	}

}
