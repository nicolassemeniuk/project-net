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
|   $Revision: 20994 $
|       $Date: 2010-06-25 12:35:41 -0300 (vie, 25 jun 2010) $
|     $Author: umesha $
|                                                                       
+----------------------------------------------------------------------*/
package net.project.security.domain;

import net.project.base.directory.search.AbstractSearchResult;
import net.project.base.directory.search.AbstractSearchResults;

/**
 * Search Results from searching a UserDomain directory.
 * Currently not implemented.
 */
public class UserDomainDirectorySearchResults extends AbstractSearchResults  {

    /** Temporary placeholder for future results.  TBC. */
    private java.util.List results = new java.util.ArrayList();

    /**
     * Creates new results.
     * Currently these are always empty.
     */
    protected UserDomainDirectorySearchResults() {
        // TBC
    }

    /**
     * Returns a list iterator over the domain directory search
     * results, beginning at the specified index.
     */
    public java.util.ListIterator listIterator(int index) {
        return new ListItr(index);
    }
    
    /**
     * Currently returns 0 always.
     */
    public int size() {
        return results.size();
    }

    /**
     * Indicates whether there is a search result at the specified index.
     * @param index the index position
     * @return true if there is a search result at the index; false otherwise
     */
    public boolean hasElement(int index) {
        return (index < size());
    }
    
    /**
     * A single search result.
     */
    private static class DomainSearchResult extends AbstractSearchResult {
        
        /**
         * The ID of this result.
         */
        private String id = null;

        // TODO
        // Pass in a Person object or similar that is the result
        // of searching a domain
        DomainSearchResult(String id) {
            this.id = id;
        }
        
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
    
        public String getID() {
            return this.id;
        }

        public String getFirstName() {
            return "";
        }

        public String getLastName() {
            return "";
        }

        public String getDisplayName() {
            return "";
        }

        public String getEmail() {
            return "";
        }
        
        public String getInvite() {
            return "";
        }
        
        public String getPersonId() {
			return "";
		}
        
        public String getSearchedDisplayName(){
        	return getFirstName()+", "+getLastName();
        }
        
		public boolean getOnline() {
			return false;
		}
		
		public void setOnline(boolean isOnline) {
			// Do nothing here 
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

        public void setDisplayName(String displayName) { }

		public void setEmail(String email) { }
    }


    /**
     * Provides a list iterator over the search results.
     * Each element is an <code>ISearchResult</code>.
     */
    private class ListItr extends AbstractSearchResults.ListItr {
        
        /**
         * Reference to the results iterator.
         */
        private java.util.ListIterator itr = null;

        /**
         * Creates a new ListItr.
         */
        public ListItr(int index) {
            // Grab a reference to the results list iterator
            this.itr = UserDomainDirectorySearchResults.this.results.listIterator(index);
        }

        public boolean hasPrevious() {
            return this.itr.hasPrevious();
        }

	public Object previous() {
            // TODO
            // Pass in some object from the real search results

            // Make ISearchResult object
            String previousIndex = String.valueOf(previousIndex());
            return new DomainSearchResult(previousIndex);
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
            // TODO
            // Pass in some object from the real search results
            
            // Make ISearchResult object
            String nextIndex = String.valueOf(nextIndex());
            return new DomainSearchResult(nextIndex);
        }

    }

}
