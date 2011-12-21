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
package org.integratedmodelling.utils;

import org.apache.log4j.Logger;


/**
 * Escapes and Unescapes undesirable characters using % (URLEncoding)
 *
 * @author vpro
 * @version $Id: URLEscape.java,v 1.6 2005/07/28 09:23:19 pierre dead $
 */
public class UrlEscape {

    // logger
    private static Logger log = Logger.getLogger(UrlEscape.class.getName());

    /**
     * List for all ASCII characters whether it can be part of an
     * URL line.
     */
    static boolean isacceptable[] = {
        false, false, false, false, false, false, false, false,    //  !"#$%&'
        false, false, true, true, true, true, true, false,         // ()*+,-./
        true, true, true, true, true, true, true, true,            // 01234567
        true, true, true, false, false, false, false, false,       // 89:;<=>?
        true, true, true, true, true, true, true, true,            // @ABCDEFG
        true, true, true, true, true, true, true, true,            // HIJKLMNO
        true, true, true, true, true, true, true, true,            // PQRSTUVW
        true, true, true, false, false, false, false, true,        // XYZ[\]^_
        false, true, true, true, true, true, true, true,           // `abcdefg
        true, true, true, true, true, true, true, true,            // hijklmno
        true, true, true, true, true, true, true, true,            // pqrstuvw
        true, true, true, false, false, false, false, false        // xyz{|}~
    };

    /**
     * Hex characters
     */
    static char hex[] = {
        '0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'
    };

    /**
     * Character to use for escaping invalid characters
     */
    static char HEX_ESCAPE='%';

    /**
     * Escape a url.
     * Replaces 'invalid characters' with their Escaped code, i.e.
     * the questionmark (?) is escaped with %3F.
     * @param url the urls to escape
     * @return the escaped url.
     */
    public static String escapeurl(String str) {
        byte buf[];
        int i,a;
        StringBuffer esc=new StringBuffer();

        buf=new byte[str.length()];
        str.getBytes(0,str.length(),buf,0);

        for (i = 0; i<str.length();i++) {
            a = (int)buf[i] & 0xff;
            if (a>=32 && a<128 && isacceptable[a-32]) {
                esc.append((char)a);
            } else {
                esc.append(HEX_ESCAPE);
                esc.append(hex[a >> 4]);
                esc.append(hex[a & 15]);
            }
        }
        return esc.toString();
    }

    /**
     * converts a HEX-character to its approprtiate byte value.
     * i.e. 'A' is returned as '/011'
     * @param c teh Hex character
     * @return the byte value as a <code>char</code>
     */
    private static char from_hex(char c) {
        return (char)(c >= '0' && c <= '9' ? c - '0'
            : c >= 'A' && c <= 'F' ? c - 'A' + 10
            : c - 'a' + 10);            /* accept small letters just in case */
    }

    /**
     * Unescape a url.
     * Replaces escapesequenced with the actual character.
     * i.e %3F is replaced with the the questionmark (?).
     * @param url the urls to unescape
     * @return the unescaped url.
     */
    public static String unescapeurl(String str) {
        int i;
        char j,t;
        StringBuffer esc=new StringBuffer();

        if (str!=null) {
            for (i=0;i<str.length();i++) {
                t=str.charAt(i);
                if (t==HEX_ESCAPE) {
                    t=str.charAt(++i);
                    j=(char)(from_hex(t)*16);
                    t=str.charAt(++i);
                    j+=from_hex(t);
                    esc.append(j);
                } else {
                    esc.append(t);
                }
            }
        } else {
            log.warn("Unescapeurl -> Bogus parameter");
        }
        return esc.toString();
    }

    /**
     * Method for testing this class from the command line
     */
    public static void main(String args[]) {
        for (int i=0;i<args.length;i++) {
            log.info("Original : '"+args[i]+"'");
            log.info("Escaped : '"+escapeurl(args[i])+"'");
            log.info("Unescaped again : '"+unescapeurl(escapeurl(args[i]))+"'");
        }

    }
}