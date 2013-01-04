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

 package net.project.document;

import javax.servlet.http.HttpServletResponse;

import net.project.base.PnetException;


/** 
    This class represents a Document stored directly in the project.net database by the DefaultDocumentProvider..
*/
public interface IDocument
{
       

    // setter and adder methods.  Need to add more.
    
    /** Adds a content element to this document */
//    public boolean addContentElement(IContentElement element);
       
    // getter methods.  Need to add more.

    /** @returns an ArrayList of IContentElements for this document */
//    public ArrayList getContentElements();


 
    // Actor methods.  Need to add more


    public void download (HttpServletResponse response) throws PnetException;

    public void add() throws PnetException;

    /** Check in this document */
     public void checkIn () throws PnetException;

   /** Check out this document */
    public void checkOut () throws PnetException;

    /** undo the check out on this document */
    public void undoCheckOut() throws PnetException;

    public boolean isCheckedOut();

}


