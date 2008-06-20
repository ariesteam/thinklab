/**
 * DOMHelper.java
 * ----------------------------------------------------------------------------------
 * 
 * Copyright (C) 2008 www.integratedmodelling.org
 * Created: Jan 17, 2008
 *
 * ----------------------------------------------------------------------------------
 * This file is part of Thinklab.
 * 
 * Thinklab is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Thinklab is distributed in the hope that it will be useful,
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
 * @author    Ioannis N. Athanasiadis (ioannis@athanasiadis.info)
 * @date      Jan 17, 2008
 * @license   http://www.gnu.org/licenses/gpl.txt GNU General Public License v3
 * @link      http://www.integratedmodelling.org
 **/
package org.integratedmodelling.utils;

import java.io.IOException;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabIOException;
import org.integratedmodelling.thinklab.exception.ThinklabInternalErrorException;
import org.integratedmodelling.thinklab.exception.ThinklabXMLException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * A simple wrapper for an XML document to be handled using the DOM tree methods of the installed parser. Simplifies
 * access. Intended for use in read-only mode, may be extended later.
 * @author Ferdinando Villa
 */
public class DOMHelper {
    
    Document dom;
    
    private void parseXmlFile(String url) throws ThinklabException {
        //get the factory
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        
        try {
            
            //Using factory get an instance of document builder
            DocumentBuilder db = dbf.newDocumentBuilder();
            
            //parse using builder to get DOM representation of the XML file
            dom = db.parse(url);
            

        } catch(ParserConfigurationException pce) {
            throw new ThinklabInternalErrorException(pce);
        } catch(SAXException se) {
           throw new ThinklabXMLException(se);
        } catch(IOException ioe) {
            throw new ThinklabIOException(ioe);
        }
    }
    
    private void parseDocument(){
        //get the root elememt
        Element docEle = dom.getDocumentElement();
        
        //get a nodelist of <employee> elements
        NodeList nl = docEle.getElementsByTagName("Employee");
        if(nl != null && nl.getLength() > 0) {
            for(int i = 0 ; i < nl.getLength();i++) {
                
                //get the employee element
                Element el = (Element)nl.item(i);
            }
        }
    }
    
    public DOMHelper(URL url) throws ThinklabException {
        parseXmlFile(url.toString());
        parseDocument();
    }

    public DOMHelper(String url) throws ThinklabException {
        parseXmlFile(url);
        parseDocument();
        
    }
    
    Node getRootNode() {
        return null;
    }
}
