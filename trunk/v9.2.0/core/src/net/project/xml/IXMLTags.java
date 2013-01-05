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

package net.project.xml;

public interface IXMLTags {

    public static final String XML_VERSION_STRING = "<?xml version=\"1.0\"?>";    
    public static final String  OPEN_BEGINNING_XML_TAG		    = "<";
    public static final String  OPEN_ENDING_XML_TAG	    = "</";
    public static final String CLOSE_XML_TAG		    = ">";
    public static final String CLOSE_COMPOUND_XML_TAG		    = "/>";
    public static final char  EQUALS = '=';
    public static final char SPACE = ' ';
    public static final char DOUBLE_QUOTE = '"';

}

