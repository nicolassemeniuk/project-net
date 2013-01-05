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
|    $Revision: 18397 $
|    $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|    $Author: umesha $
|                                                                       
+----------------------------------------------------------------------*/
package net.project.taglibs.search;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import net.project.base.property.PropertyProvider;

public class SearchLetterTag extends TagSupport {

    public SearchLetterTag() {
        super();
    }

    public int doStartTag() throws JspException {

        try {
                JspWriter out = pageContext.getOut();
                out.print(getSearchStrip());
            } catch(IOException ioe) {
              throw new JspTagException ("I/O exception: " + ioe);
            }

            return(SKIP_BODY);
    }

	
	
    private String getSearchStrip() 
	{
		/*
		NEW TOKENIZATION CODE (Brian Janko - 12/12/02)
		Token "prm.global.searchletter.alphabet.value" represents the alphabet of the client's language.  Its value is manipulated so that the uppercase of each letter is displayed on the webpage and the lowercase of each letter is passed in a JavaScript search() call which eventually becomes a key used to access the database.
		
		This code works well with most English characters, however, the further one gets from English, the more problems there are.  Also, characters which might work on their own encounter problems when being programmatically changed from upper- to lowercase or vice versa, (whether here or in the SQL).  Issues related to character sets still need to be examined to test for how accents and special characters of all kinds are affected.  
		*/

		// get value of client's alphabet from token
		// convert to lowercase
		String alphabetToken = PropertyProvider.get("prm.global.searchletter.alphabet.value").toLowerCase();
		
		// set char Array to the same size as client's alphabet
		char[] alphabetLetters = new char[alphabetToken.length()];
		
		// populate char Array with letters of client's alphabet
		for (int i = 0; i < alphabetToken.length(); i++)
		{
			alphabetLetters[i] = alphabetToken.charAt(i);
		}

		// Create StringBuffer and append the "All" link so that it is the first link
		StringBuffer sb = new StringBuffer();
		sb.append("<a href=\"javascript:search('');\">" + PropertyProvider.get("prm.global.searchletter.all.link") + "</a>&nbsp;"); // Token Value:  "All"

		// loop thru char Array of alphabet letters
		for (int i = 0; i < alphabetLetters.length; i++)
		{
			// convert each letter from a char to a String and store in variable
			String displayLetter = String.valueOf(alphabetLetters[i]);

			// use String version of letter to build link and append it to "sb"
			// -- the letter, already lowercase, is passed in the JavaScript function
			// -- the letter is converted to uppercase for display on the page			
			sb.append("<a href=\"javascript:search('" + displayLetter + "');\">" + displayLetter.toUpperCase() + "</a>&nbsp;");
		}

		// return full StringBuffer as a String
        return sb.toString();
		// // // // // // // // // // // // // // // // 
		// END NEW TOKENIZATION CODE
		// // // // // // // // // // // // // // // // 

// ************************************************************ //
/*  OLD CODE (replaced by the new tokenization code above)
        StringBuffer sb = new StringBuffer();
        sb.append("<a href=\"javascript:search('');\">All</a> <a href=\"javascript:search('a');\">A</a> ");
        sb.append("<a href=\"javascript:search('b');\">B</a> <a href=\"javascript:search('c');\">C</a> "); 
        sb.append("<a href=\"javascript:search('d');\">D</a> <a href=\"javascript:search('e');\">E</a> ");
        sb.append("<a href=\"javascript:search('f');\">F</a> <a href=\"javascript:search('g');\">G</a> ");
        sb.append("<a href=\"javascript:search('h');\">H</a> <a href=\"javascript:search('i');\">I</a> ");
        sb.append("<a href=\"javascript:search('j');\">J</a> <a href=\"javascript:search('k');\">K</a> ");
        sb.append("<a href=\"javascript:search('l');\">L</a> <a href=\"javascript:search('m');\">M</a> ");
        sb.append("<a href=\"javascript:search('n');\">N</a> <a href=\"javascript:search('o');\">O</a> ");
        sb.append("<a href=\"javascript:search('p');\">P</a> <a href=\"javascript:search('q');\">Q</a> ");
        sb.append("<a href=\"javascript:search('r');\">R</a> <a href=\"javascript:search('s');\">S</a> ");
        sb.append("<a href=\"javascript:search('t');\">T</a> <a href=\"javascript:search('u');\">U</a> ");
        sb.append("<a href=\"javascript:search('v');\">V</a> <a href=\"javascript:search('w');\">W</a> ");
        sb.append("<a href=\"javascript:search('x');\">X</a> <a href=\"javascript:search('y');\">Y</a> ");
        sb.append("<a href=\"javascript:search('z');\">Z</a> ");
        return sb.toString();
END OLD CODE  */
// ************************************************************ //
    }
        
}
