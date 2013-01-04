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
|   $Revision: 20642 $
|       $Date: 2010-03-30 10:57:51 -0300 (mar, 30 mar 2010) $
|     $Author: umesha $
|                                                                       
+----------------------------------------------------------------------*/
package net.project.business;

import java.io.Serializable;

import net.project.base.directory.search.AbstractSearchResult;
import net.project.base.directory.search.AbstractSearchResults;
import net.project.resource.Person;
import net.project.resource.Roster;

/**
 * Search Results from searching a Business Space Directory.
 */
public class BusinessDirectorySearchResults extends AbstractSearchResults implements Serializable {

    /** 
     * The roster from which results are read.
     */
    private Roster roster = null;

    public BusinessDirectorySearchResults(){
    	
    }
    
    /**
     * Builds search results from the specified roster.
     * Assumes the roster contains search results.
     * @param roster the roster containing search results
     */
    protected BusinessDirectorySearchResults(Roster roster) {
        this.roster = roster;
    }

    /**
     * Returns a list iterator over the business directory search
     * results, beginning at the specified index.
     */
    public java.util.ListIterator listIterator(int index) {
        return new ListItr(index);
    }
    
    public int size() {
        return this.roster.size();
    }

    /**
     * Indicates whether there is a search result at the specified index.
     * @param index the index position
     * @return true if there is a search result at the index; false otherwise
     */
    public boolean hasElement(int index) {
        return (index < size());
    }

    //
    // Inner classes
    //

    /**
     * A single search result.
     */
    public class BusinessDirectorySearchResult extends AbstractSearchResult implements Serializable{
        
        /** 
         * A reference to the Person from which this result is built.
         */
        private Person person = null;

        /**
         * The ID of this result.
         */
        private String id = null;
        
        private boolean disable;
        /**
		 * @return the disable
		 */
		public boolean isDisable() {
			return disable;
		}
		/**
		 * @param disable the disable to set
		 */
		public void setDisable(boolean disable) {
			this.disable = disable;
		}
		/**
         * Creates a new SearchResult for the specified person.
         * @param person the person that this SearchResult represents
         * @param id the id of the result; this id must be unique
         * within the complete list of results
         */
        
        public BusinessDirectorySearchResult(Person person){
        	 this.person = person;
        }
        BusinessDirectorySearchResult(Person person, String id) {
            this.person = person;
            this.id = id;
        }
    
        public String getID() {
            return this.id;
        }
        
        public String getFirstName() {
            return person.getFirstName();
        }

        public String getLastName() {
            return person.getLastName();
        }

        public String getDisplayName() {
            return person.getDisplayName();
        }

        public String getEmail() {
            return person.getEmail();
        }
        
        public String getPersonId(){
        	return person.getID();
        }
        /**
    	 * @return the invite
    	 */
    	public String getInvite() {
    		return person.getInvite();
    	}
        
    	public boolean getOnline() {
    		return person.isOnline;
    	}
    	
        public void setID(String id) {
            this.id = id;
        }
        
        public void setFirstName(String firstName) {
        	 this.person.firstName = firstName;
        }

        public void setLastName(String lastName) {
        	 this.person.lastName = lastName;
        }

        public void setDisplayName(String displayName) {
        	this.person.displayName = displayName;
        } 	
        public void setEmail(String email) {
        	this.person.email = email;
        }
        
        public void setInvite(String invite) {
        	this.person.invite = invite;
        }
        public void setOnline(boolean isOnline) {
        	this.person.isOnline = isOnline;
    	}

        /**
         * Returns an <code>XMLDocument</code> for this LDAPSearchResult.
         * @return the XMLDocument; may be empty if there is a problem
         * creating it
         */
        protected net.project.xml.document.XMLDocument getXMLDocument() {
            net.project.xml.document.XMLDocument xml = new net.project.xml.document.XMLDocument();

            try {
                xml.startElement("SearchResult");
                addElements(xml);
                xml.endElement();
            
            } catch (net.project.xml.document.XMLDocumentException e) {
                // Simply return empty XML

            }

            return xml;
        }
        
        /**
         * @return first and last name of person
         */
        public String getSearchedDisplayName() {
    		return getFirstName()+" "+getLastName();
    	}

    }


    /**
     * Provides a list iterator over the roster search results.
     * Each element is an <code>ISearchResult</code>.
     * This iterator simply delegates to the roster's list iterator,
     * but modifies <code>next</code> and <code>previous</code> to
     * return objects of type <code>BusinessDirectorySearchResult</code>.
     */
    private class ListItr extends AbstractSearchResults.ListItr {
        
        /**
         * Reference to the roster results iterator.
         */
        private java.util.ListIterator itr = null;

        /**
         * Creates a new ListItr.
         */
        public ListItr(int index) {
            // Grab a reference to the roster's list iterator
            this.itr = BusinessDirectorySearchResults.this.roster.listIterator(index);
        }

        public boolean hasPrevious() {
            return this.itr.hasPrevious();
        }

	public Object previous() {
            // Make ISearchResult object
            String previousIndex = String.valueOf(previousIndex());
            return new BusinessDirectorySearchResult((Person) this.itr.previous(), previousIndex);
	}

	public int previousIndex() {
	    return this.itr.previousIndex();
        }

        public boolean hasNext() {
            return this.itr.hasNext();
        }

	public int nextIndex() {
	    return this.itr.nextIndex();
        }

        public Object next() {
            // Make ISearchResult object
            String nextIndex = String.valueOf(nextIndex());
            return new BusinessDirectorySearchResult((Person) this.itr.next(), nextIndex);
        }

    }

}
