///**
// * Copyright 2011 The ARIES Consortium (http://www.ariesonline.org) and
// * www.integratedmodelling.org. 
//
//   This file is part of Thinklab.
//
//   Thinklab is free software: you can redistribute it and/or modify
//   it under the terms of the GNU General Public License as published
//   by the Free Software Foundation, either version 3 of the License,
//   or (at your option) any later version.
//
//   Thinklab is distributed in the hope that it will be useful, but
//   WITHOUT ANY WARRANTY; without even the implied warranty of
//   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
//   General Public License for more details.
//
//   You should have received a copy of the GNU General Public License
//   along with Thinklab.  If not, see <http://www.gnu.org/licenses/>.
// */
//package org.integratedmodelling.geospace.gazetteers;
//
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Properties;
//
//import org.integratedmodelling.exceptions.ThinklabException;
//import org.integratedmodelling.geospace.Geospace;
//import org.integratedmodelling.geospace.interfaces.IGazetteer;
//import org.integratedmodelling.geospace.literals.ShapeValue;
//import org.integratedmodelling.searchengine.QueryString;
//import org.integratedmodelling.thinklab.api.knowledge.query.IQueriable;
//import org.integratedmodelling.thinklab.api.knowledge.query.IQuery;
//import org.integratedmodelling.thinklab.literals.TextValue;
//import org.integratedmodelling.utils.MiscUtilities;
//
///**
// * The most trivial of gazetteers - configured through properties. Good to insert a few important
// * polygons without the mess of database configuration. The shapes are kept in memory. Use 
// * sparingly and sensibly.
// * 
// * Whatever is in the properties file that contains entries like
// * 
// * geospace.gazetteer.entry.myplace = EPSG:4326 POLYGON(.....
// * 
// * will define a "myplace" location. A query needs to match the location name exactly, will
// * only return one result or zero, and will set the fields "id" and "label" to myplace and
// * the shape field (IGazetteer.SHAPE_FIELD) to the polygon. The WKT is expected to be in 
// * EPSG:4326 with the latitude along the X axis, no exceptions.
// * 
// * @author Ferdinando
// *
// */
//public class SimpleGazetteer implements IGazetteer {
//
//	public static final String ENTRY_PROPERTY_PREFIX = "geospace.gazetteer.entry.";
//	
//	HashMap<String, ShapeValue> locations = new HashMap<String, ShapeValue>();
//	private int priority = 0;
//	
//	class SingleResult implements IQueryResult {
//
//		private String id;
//		private ShapeValue shape;
//		private float score = 1.0f;
//
//		SingleResult(String id, ShapeValue shape) {
//			this.id = id;
//			this.shape = shape;
//		}
//		
//		@Override
//		public IValue getBestResult(ISession session) throws ThinklabException {
//			return shape;
//		}
//
//		@Override
//		public IQueriable getQueriable() {
//			return SimpleGazetteer.this;
//		}
//
//		@Override
//		public IQuery getQuery() {
//			return new QueryString(id);
//		}
//
//		@Override
//		public IValue getResult(int n, ISession session)
//				throws ThinklabException {
//			return shape;
//		}
//
//		@Override
//		public Polylist getResultAsList(int n,
//				HashMap<String, String> references) throws ThinklabException {
//			return null;
//		}
//
//		@Override
//		public int getResultCount() {
//			return shape == null ? 0 : 1;
//		}
//
//		@Override
//		public IValue getResultField(int n, String schemaField)
//				throws ThinklabException {
//
//			if (schemaField.equals("id") || schemaField.equals("label"))
//				return new TextValue(id);
//			else if (schemaField.equals(IGazetteer.SHAPE_FIELD))
//				return shape;
//			return null;
//		}
//
//		@Override
//		public int getResultOffset() {
//			return 0;
//		}
//
//		@Override
//		public float getResultScore(int n) {
//			return score;
//		}
//
//		@Override
//		public int getTotalResultCount() {
//			return getResultCount();
//		}
//
//		@Override
//		public void moveTo(int currentItem, int itemsPerPage)
//				throws ThinklabException {
//		}
//
//		@Override
//		public float setResultScore(int n, float score) {
//			float sc = this.score;
//			this.score = score;
//			return sc;
//		}
//		
//	}
//	
//	public SimpleGazetteer() {
//		// expect initialize()
//	}
//	
//	public SimpleGazetteer(Properties properties) throws ThinklabException {
//		initialize(properties);
//	}
//	
//	@Override
//	public Collection<String> getKnownNames(Collection<String> container) {
//		if (container == null)
//			container = new ArrayList<String>();
//		container.addAll(locations.keySet());
//		return container;
//	}
//
//	@Override
//	public Collection<ShapeValue> resolve(String name,
//			Collection<ShapeValue> container, Properties options)
//			throws ThinklabException {
//		Collection<ShapeValue> ret = container;
//		if (ret == null)
//			ret = new ArrayList<ShapeValue>();
//		ShapeValue sh = locations.get(name);
//		if (sh != null)
//			ret.add(sh);
//		return ret;
//	}
//
//	@Override
//	public void addLocation(String id, ShapeValue shape,
//			Map<String, Object> metadata) throws ThinklabException {
//		
//		// just ignore any metadata, but do add it, even if we're read only, who cares
//		locations.put(id, shape);
//	}
//
//	@Override
//	public void importLocations(String url, Properties properties) throws ThinklabException {
//		// do nothing, we're read only
//	}
//
//	@Override
//	public boolean isReadOnly() {
//		return true;
//	}
//
//	@Override
//	public void initialize(Properties properties) throws ThinklabException {
//		
//		for (Object p : properties.keySet()) {	
//			String prop = p.toString();
//			if (prop.startsWith(ENTRY_PROPERTY_PREFIX)) {
//				String loc = MiscUtilities.getFileExtension(prop);
//				ShapeValue shape = 
//					new ShapeValue(properties.getProperty(prop), Geospace.get().getStraightGeoCRS());
//				locations.put(loc, shape);
//			} else if (prop.equals(PRIORITY_PROPERTY)) {
//				this.priority = Integer.parseInt(properties.getProperty(prop).trim());
//			}
//		}
//	}
//
//	@Override
//	public IQuery parseQuery(String toEval) throws ThinklabException {
//		return new QueryString(toEval);
//	}
//
//	@Override
//	public IQueryResult query(IQuery q) throws ThinklabException {
//		Collection<ShapeValue> res = resolve(q.asText(), null, null);
//		return new SingleResult(q.asText(), res.size() > 0 ? res.iterator().next() : null);
//	}
//
//	@Override
//	public IQueryResult query(IQuery q, int offset, int maxResults)
//			throws ThinklabException {
//		return query(q);
//	}
//
//	@Override
//	public IQueryResult query(IQuery q, String[] metadata, int offset,
//			int maxResults) throws ThinklabException {
//		return query(q);
//	}
//
//	@Override
//	public int getPriority() {
//		return this.priority;
//	}
//
//	@Override
//	public void resetToEmpty() throws ThinklabException {
//		// can't be called, we're read only
//	}
//
//
//}
