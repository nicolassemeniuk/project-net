/* 
 * Copyright 2000-2009 Project.net Inc.
 *
 * This file is part of Project.net.
 * Project.net is free software: you can redistribute it and/or modify it under the terms of 
 * the GNU General Public License as published by the Free Software Foundation, version 3 of the License.
 * 
 * Project.net is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Project.net.
 * If not, see http://www.gnu.org/licenses/gpl-3.0.html
*/

 /*----------------------------------------------------------------------+
|
|    $RCSfile$
|   $Revision: 18397 $
|       $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|     $Author: umesha $
|                                                                       
+----------------------------------------------------------------------*/
package net.project.util;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.ArrayList;

/**
  * Project.net StringTokenizer
  * This differs from the Java version : it allows the specification of an
  * escape delimiter which causes every character within a pair of the escape
  * delimiter to be processed as a single token.<br>
  * For example, assume the escape delimiter is a double quote, "<br>
  * <code>a,b,"c d e",f</code><br>
  * will result in 4 tokens with the third token as<br>
  * <code>c d e</code><br>
  * <b>Note : Following functionality NOT implement yet.  Double-escape-delimeters
  * will simply be removed due to nature of algorithm.</b>
  * Additionally, pairs of adjacent escape delimiters will be converted to a single character <br>
  * For example, assume the escape delimiter is a double quote, "<br>
  * <code>a,b,"c ""d"" e",f</code><br>
  * will result in 4 tokens with the third token as<br>
  * <code>c "d" e</code><br>
  */
public class StringTokenizer
{
    private String m_string = new String();
    private String m_delim = ",";
    private ArrayList m_tokens = new ArrayList();

    private Character escapeDelimeter = null;
    
    public  StringTokenizer( String string )
    {
        this.m_string = string;
    }

    public  StringTokenizer( String string, String delim )
    {
        this.m_string = string;
        this.m_delim = delim;
    }

    /**
      * Set the character which is used to escape the contents of a token
      */
    public void setEscapeDelimiter(char escapeDelimeter) {
        this.escapeDelimeter = new Character(escapeDelimeter);
    }

    public char getEscapeDelimiter() {
        return this.escapeDelimeter.charValue();
    }

    /**
      * Parse string
      */
    public ArrayList parse()
    {
        this.m_tokens = new ArrayList();
        StringCharacterIterator iterator = new StringCharacterIterator( this.m_string );
        StringBuffer item = new StringBuffer(new String());

        for(char c = iterator.first(); c != CharacterIterator.DONE; c = iterator.next())
        {
            if (isEscapeDelimeter(c)) {
                // Suck up all characters until next escape delimeter
                
                // c is currently the first escape delimeter.  Ignore it.
                c = iterator.next();
                while(!isEscapeDelimeter(c))
                {
                    item.append(c);
                    c = iterator.next();
                }
                // c is now the final escape delimeter.  Ignore it.

            } else if(  (( new Character( c )).toString()).equals( this.m_delim ) ) {
                // Found delimeter, add current item to token list and continue
                this.m_tokens.add( item.toString() );
                item = new StringBuffer(new String());
            
            } else {
                // Just add to item
                item.append( c );
            }
        }

        return this.m_tokens;
    }


    /**
      * Indicates whether character is the escape delimeter
      * @param c the character
      * @return true if it is the escape delimeter
      */
    private boolean isEscapeDelimeter(char c) {
        if (escapeDelimeter != null) {
            if (c == escapeDelimeter.charValue()) {
                return true;
            }
        }
        return false;
    }

    // test code

    public static void main( String[] args )
    {
        String str = "70,1,181,\"Version,,, \"1.0\" Development\",105d,No,1,0d,,Mon 1/3/00,Fri 5/26/00,Mon 1/3/00,Fri 5/26/00,Mon 1/3/00,Fri 5/26/00,0d,0d,0ed,32%,Mon 1/3/00,NA,NA,NA,As Soon As Possible,,Fri 1/7/00,Fri 1/7/00,Mon 12/27/99,540.27d,0d,155.62d,$485934.55,$0.00,$0.00,$154126.78,$331807.77,1,Medium,No,Yes,Yes,,,,,,,,,,,$0.00,$0.00,$0.00,0d,0d,0d,No,No,No,No,No,No,No,No,No,No,No,0,0,0,0,0,,,NA,NA,NA,NA,NA,NA,NA,NA,NA,NA";

        StringTokenizer t = new StringTokenizer( str );
        t.setEscapeDelimiter('"');
        ArrayList array = t.parse();

        for( int i=0 ; i<array.size() ; ++i )
        {
            System.out.println("item["+i+"]  = " + array.get(i) );
        }
    }
}
