/**
 * SQLServer.java
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

import java.net.URI;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.DriverManagerConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.log4j.Logger;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabStorageException;

/**
 * Wraps an internal or external SQL server and provides a pooling connection mechanism based on
 * Apache Commons DBCP. If the server is external, define stopServer() and startServer() to do
 * nothing. 
 * 
 * Note that an execute() function is provided to exec SQL update statements without further
 * complication, and the query() function returns a conveniently stored result set which is,
 * however, using up memory and may become unwieldy when the query selects large amounts
 * of information. 
 * 
 * When a query is executed and allocation must be controlled, there is no way to avoid the messy manual
 * setup, because the result set is destroyed when the statement is released and we just can't return
 * a ResultSet. The general pattern of usage for a generic SQL query (sqlQuery) on an initialized server
 * (server) when you need to control allocation and can't use query() is the familiar one:
 * 
 *    Connection conn = null;
 *    Statement stmt = null;
 *    ResultSet rset = null;
 *
 *    try {
 *        conn = server.getConnection();
 *        stmt = conn.createStatement();
 *        rset = stmt.executeQuery(sqlQuery);
 *        
 *        // do your thing with the result set, e.g.
 *        int numcols = rset.getMetaData().getColumnCount();
 *        while(rset.next()) {
 *            for(int i=1;i<=numcols;i++) {
 *                System.out.print("\t" + rset.getString(i));
 *            }
 *            System.out.println("");
 *        }
 *    } catch(SQLException e) {
 *        // do something with the exception
 *    } finally {
 *        try { rset.close(); } catch(Exception e) { }
 *        try { stmt.close(); } catch(Exception e) { }
 *        try { conn.close(); } catch(Exception e) { }
 *    }
 *
 * 
 * @author Ferdinando Villa
 *
 */
public abstract class SQLServer {

	private int port;
	private String user;
	private String passwd;
	private String database;
	private String host;
	
	private boolean readOnly = false;
	private boolean autoCommit = true;
	// FIXME should be true, but it never worked so far
	private boolean usePooling = false;
	
	public static SQLServer newInstance(String uri, Properties properties) throws ThinklabException {
		return SQLPlugin.get().createSQLServer(uri, properties);
	}
	
	public void setDatabase(String db) {
		database = db;
	}

	public void setUser(String db) {
		user = db;
	}

	public void setPassword(String db) {
		passwd = db;
	}

	public void setHost(String host) {
		this.host = host;
	}
	
	public String getUser() {
		return user;
	}

	public String getPassword() {
		return passwd;
	}

	public String getDatabase() {
		return database;
	}

	public String getHost() {
		return host;
	}
	
	public int getPort() {
		return port;
	}

	private DataSource dataSource = null;
	private Logger logger = null;

    private DataSource setupDataSource(String connectURI) throws ThinklabStorageException {
    	
        ObjectPool connectionPool = new GenericObjectPool(null);
        ConnectionFactory connectionFactory = 
        	new DriverManagerConnectionFactory(connectURI, getUser(), getPassword());
        @SuppressWarnings("unused")
		PoolableConnectionFactory poolableConnectionFactory =
        	new PoolableConnectionFactory(connectionFactory,connectionPool,
        			null,null,readOnly,autoCommit);
        PoolingDataSource dataSource = new PoolingDataSource(connectionPool);
    	
    	return dataSource;
    	
    }
	
    private void parseURI(URI uri) {

    	port = getPort(uri);
		if (port == -1) {
			port = getDefaultPort();
		}
		
		host = getHost(uri);
		user = getUser(uri);
		passwd = getPassword(uri);
		database = getDatabaseName(uri);
	}

    /**
     * Call before initialize() if you want to control commits yourself. Otherwise each 
     * transaction is automatically committed on successful exit.
     */
    public void preventAutoCommit() {
    	autoCommit = false;
    }
    
    /**
     * Call this before initialize() if you want to prevent the server from using
     * DBCP connection pooling. The only good reason to do that is if connection
     * pooling generates mysterious errors that you can't make sense of, which is
     * unfortunately my case.
     */
    public void preventConnectionPooling() {
    	usePooling = false;
    }
    
    /**
     * Call before initialize() if you want the DB to be read only.
     */
    public void setReadOnly() {
    	readOnly = true;
    }
    

    /**
     * Call this one when you don't need to pass any information because you've set 
     * user, password etc through setXXXX() and the server is external or is started
     * without configurable properties.
     * 
     * @throws ThinklabStorageException
     */
    public void initialize() throws ThinklabStorageException {

    	try {
            Class.forName(getDriverClass(), true, SQLPlugin.get().getClassLoader());
        } catch (ClassNotFoundException e) {
        	throw new ThinklabStorageException(e);
        }

        startServer(null);
    	if (usePooling)
    		dataSource = setupDataSource(getURI());
    }

    /**
     * Call this one when all connection details can be extracted from the passed URI. 
     * Note that the URI is not the connection URI for the database, but your own way
     * to pass username, database name, port, and password information.
     * 
     * @param uri
     * @throws ThinklabStorageException
     */
    public void initialize(URI uri) throws ThinklabStorageException {

    	try {
            Class.forName(getDriverClass(), true, SQLPlugin.get().getClassLoader());
        } catch (ClassNotFoundException e) {
        	throw new ThinklabStorageException(e);
        }
    	
    	parseURI(uri);
        startServer(null);
        if (usePooling)
        	dataSource = setupDataSource(getURI());
    }
   
    /**
     * Call this one when you have a URI specifying DB parameters and a property file that you
     * want to pass to startServer().
     * 
     * @param uri
     * @param properties
     * @throws ThinklabStorageException
     */
	public void initialize(URI uri, Properties properties) throws ThinklabStorageException {
		
        try {
            Class.forName(getDriverClass(), true, SQLPlugin.get().getClassLoader());
        } catch (ClassNotFoundException e) {
        	throw new ThinklabStorageException(e);
        }
    	
    	parseURI(uri);
    	
    	/* set generic properties from passed properties, defaulting to the overall properties 
    	 * set for the SQL plugin, and to sensible defaults if even those are not there. */
    	usePooling = Boolean.parseBoolean(properties.getProperty("sql.use.pooling", 
    			SQLPlugin.get().getProperties().getProperty("sql.use.pooling", "false")));
    	
    	
    	if (Boolean.parseBoolean(properties.getProperty("sql.log.queries", 
    			SQLPlugin.get().getProperties().getProperty("sql.log.queries", "false")))) {
 
    		setLogger();
    		logger.info("sql: initializing database " + uri);
    		logger.info("sql: " + (usePooling ? "using" : "not using") + " connection pooling");
 
    	}
    	
        startServer(properties);     
 
        /*
         * TODO lookup and memorize metadata schema
         */
        
        if (usePooling)
        	dataSource = setupDataSource(getURI());
    }
    
    public void finalize() {
    	
    	try {
        	((BasicDataSource)dataSource).close();
			stopServer();
		} catch (Exception e) {
		}
    }

    public static String getDatabaseName(URI url) {

    	String ret = "";
    	String ppath = url.getPath();
    	if (ppath != null) {
    		while (ppath.startsWith("/"))
    			ppath = ppath.substring(1);
    	
    		String path[] = ppath.split("/");
    		ret = path.length > 0 ? path[0] : "";
    	}
    	return ret;
	}

	public static String getPassword(URI url) {

    	String ret = "";
		String uinfo = url.getUserInfo();
    	
    	if (uinfo != null) {
    	
    		String user[] = url.getUserInfo().split(":");
    	
    		if (user.length > 1) {
    			ret = user[1];
    		}
    	}
    	return ret;	
    }

	public static String getUser(URI url) {

		String ret = "";
		String uinfo = url.getUserInfo();
    	
    	if (uinfo != null) {
    		String user[] = url.getUserInfo().split(":");
    		ret = user[0];
    	}
    	return ret;
	}

	public static int getPort(URI url) {
		return url.getPort();
	}

	public static String getHost(URI url) {
		return url.getHost();
	}

	/**
     * Must return the default port number. Used when no port is indicated in the URL.
     * @return
     */
    public abstract int getDefaultPort();
    
    /**
     * Return the class of the JDBC driver.
     * @return
     */
    public abstract String getDriverClass();
    	    
    /**
     * Return the connection URI for the database.
     * @return
     */
    public abstract String getURI();    
    
    /**
     * Start the server if we are implementing one internally. If the server we're connecting
     * to is external, just define this to do nothing.
     * 
     * @param properties passed through initialize() if any. May be null.
     * @throws ThinklabStorageException
     */
    protected abstract void startServer(Properties properties) throws ThinklabStorageException;

    /**
     * Stop the server (typically by sending a shutdown statement). If the server is
     * external, define it to do nothing.
     * 
     * @throws ThinklabStorageException
     */
    protected abstract void stopServer() throws ThinklabStorageException;
    
    public Connection getConnection() throws ThinklabStorageException {
    	try {
			return 
				usePooling ?
						dataSource.getConnection() :
						DriverManager.getConnection(getURI(), getUser(), getPassword());
						
		} catch (SQLException e) {
			throw new ThinklabStorageException(e);
		}
    }

    /**
     * Execute a set of statements separated by semicolons.
     * @param statements
     * @throws ThinklabStorageException
     */
    public void submit(String statements) throws ThinklabStorageException {
    	String[] stst = statements.split(";");
    	for (String ss : stst) {
    		execute(ss.trim());
    	}
    }
    
    public void execute(String sql) throws ThinklabStorageException {
    	    	
// FIXME please
// trying to make sense of why I get "no suitable driver" when using connection
// pooling, which shows that everything is fine, drivers are there and like the URI, but it
// just does not work anyway when used within a thinklab plugin. Funny thing is, the same
// code OUTSIDE of thinklab works just fine with connection pooling. No jar conflicts to
// speak of. What to do?
//
//    	System.out.println("In execute()");
//    	Enumeration<Driver> en = DriverManager.getDrivers();
//    	
//    	while (en.hasMoreElements()) {
//    		Driver dd = en.nextElement();
//    		System.out.println("got driver: " + dd);
//        	try {
//				System.out.println("Does the stupid driver accept my URL " + 
//						getURI()+ 
//						"? " + dd.acceptsURL(getURI()) );
//			} catch (SQLException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//    	}

    	if (logger != null) {
    		logger.info(sql);
    	}
    	
    	Connection conn = null;
    	Statement stmt  = null;
    	
		try {
			conn = getConnection();
			stmt = conn.createStatement();
	    	stmt.executeUpdate(sql);
		} catch (SQLException e) {
			throw new ThinklabStorageException(e);
		} finally {
	        try { stmt.close(); } catch(Exception e) { }
	        try { conn.close(); } catch(Exception e) { }
	    }
    }

    /**
     * Execute a query and dump the results.
     * 
     * @param sql an SQL query.
     * @throws ThinklabStorageException
     */
    public void dumpQuery(String sql) throws ThinklabStorageException {

    	if (logger != null) {
    		logger.info(sql);
    	}
    	
		Connection conn = null;
		Statement stmt = null;
		ResultSet rset = null;

		try {
			conn = getConnection();
			stmt = conn.createStatement();
			rset = stmt.executeQuery(sql);
			int numcols = rset.getMetaData().getColumnCount();
			while (rset.next()) {
				for (int i = 1; i <= numcols; i++) {
					System.out.print("\t" + rset.getString(i));
				}
				System.out.println("");
			}
		} catch (SQLException e) {
			throw new ThinklabStorageException(e);
		} finally {
			try {
				rset.close();
			} catch (Exception e) {
			}
			try {
				stmt.close();
			} catch (Exception e) {
			}
			try {
				conn.close();
			} catch (Exception e) {
			}
		}
	}
    
	public boolean haveTable(String tableName) throws ThinklabStorageException {

		boolean ret = false;
		
		Connection conn = getConnection();
		ResultSet rset = null;

		try {
			rset = conn.getMetaData().getTables(null, null, tableName, null);
			ret = rset.first();
		} catch (SQLException e) {

		} finally {
			try {
				rset.close();
				conn.close();
			} catch (Exception e) {
			}
		}
		return ret;
	}

	/**
	 * Submits the given query to the server and saves the result in a new
	 * QueryResult object. To be used knowingly. 
	 * 
	 * @param sql
	 * @return
	 * @throws ThinklabStorageException
	 */
	public QueryResult query(String sql) throws ThinklabStorageException {

    	if (logger != null) {
    		logger.info(sql);
    	}
		
		QueryResult ret = null;
		
		Connection conn = null;
		Statement stmt = null;
		ResultSet rset = null;

		try {
			conn = getConnection();
			stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			rset = stmt.executeQuery(sql);
			
			ret = new QueryResult(rset);
			
		} catch (SQLException e) {
			throw new ThinklabStorageException(e);
		} finally {
			try {
				rset.close();
			} catch (Exception e) {
			}
			try {
				stmt.close();
			} catch (Exception e) {
			}
			try {
				conn.close();
			} catch (Exception e) {
			}
		}
		return ret;
		
	}
	
	/**
	 * Return one string corresponding to field 0 of row 0 of the result after
	 * executing the passed query. Return null if no results are returned or
	 * query generates errors.
	 * 
	 * @param sql
	 * @return
	 * @throws ThinklabStorageException 
	 */
	public String getResult(String sql) throws ThinklabStorageException {
		
    	if (logger != null) {
    		logger.info(sql);
    	}

		
		String ret = null;
		
		Connection conn = null;
		Statement stmt = null;
		ResultSet rset = null;

		try {
			conn = getConnection();
			stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			rset = stmt.executeQuery(sql);
			
			if (rset.first()) {
				for (; !rset.isAfterLast(); rset.next()) {
					ret = rset.getString(1);
					break;
				}
			}
		} catch (SQLException e) {
			throw new ThinklabStorageException(e);
		} finally {
			try {
				rset.close();
			} catch (Exception e) {
			}
			try {
				stmt.close();
			} catch (Exception e) {
			}
			try {
				conn.close();
			} catch (Exception e) {
			}
		}
		return ret;
	}
	
	public double getResultAsDouble(String sql, double def) throws ThinklabStorageException {
		String r = getResult(sql);
		if (r == null)
			return def;
		return Double.parseDouble(r);
	}
	
	public int getResultAsInteger(String sql, int def) throws ThinklabStorageException {
		String r = getResult(sql);
		if (r == null)
			return def;
		return Integer.valueOf(r);
	}

	public void setLogger() {
		logger  = Logger.getLogger(this.getClass());
	}
    
}
