package org.integratedmodelling.geospace.feature;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.exceptions.ThinklabIOException;

import com.Ostermiller.util.CSVParse;
import com.Ostermiller.util.CSVParser;
import com.Ostermiller.util.ExcelCSVParser;
import com.Ostermiller.util.LabeledCSVParser;

public class AttributeTable {

	boolean isExcel = false;
	boolean hasHeaders = false;
	int ncols = 0;
	int nrows = 0;
	
	String[] headers = null;
	
	public AttributeTable(URL url, boolean hasHeaders, boolean isExcel) throws ThinklabIOException {
		this.hasHeaders = hasHeaders;
		this.isExcel = isExcel;
		initialize(url.toString());
	}

	/**
	 * Ask to create a (thread safe) hash from the two fields in the table, and return a handle that 
	 * we can use to perform searches later.
	 *  
	 * @param keyField
	 * @param valueField
	 * @return
	 */
	public int index(String keyField, String valueField) throws ThinklabException {
		return 0;
	}
	
	/**
	 * Search a previously indexed column
	 * 
	 * @param keyField
	 * @param indexHandle
	 * @return
	 */
	public String getIndexedValue(String keyField, int indexHandle) {
		return null;
	}
	
	public void initialize(String url) throws ThinklabIOException {	
		
		InputStream input = null;
		
		/*
		 * Open url, obtain reader
		 */
		try {

			URL source = new URL(url);
			input = source.openStream();
		
		} catch (Exception e) {
			throw new ThinklabIOException(e);
		}
		
		CSVParse parser = isExcel ? new ExcelCSVParser(input) : new CSVParser(input);
		
		try {
			
			if (hasHeaders) {
				parser = new LabeledCSVParser(parser);
				LabeledCSVParser p = (LabeledCSVParser) parser;
				headers = p.getLabels();
			}
			
			ncols = 0;
			nrows = 0;
			

			String[] row = null;
			
			while ((row = parser.getLine()) != null) {
			
				nrows ++;
				
				/* get string values */
				for (int i = 0; i < row.length; i++) {
					//data.add(row[i]);
				}
			}
			
			parser.close();
			
		} catch (IOException e) {
			throw new ThinklabIOException(e);
		}
		
		
	}

	
}
