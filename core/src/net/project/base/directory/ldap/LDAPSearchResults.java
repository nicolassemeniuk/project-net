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
|   $Revision: 14743 $
|       $Date: 2006-02-06 22:26:39 +0530 (Mon, 06 Feb 2006) $
|     $Author: andrewr $
|                                                                       
+----------------------------------------------------------------------*/
package net.project.base.directory.ldap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

import javax.naming.NamingEnumeration;

import net.project.base.directory.search.AbstractSearchResult;
import net.project.base.directory.search.AbstractSearchResults;
import net.project.base.directory.search.ISearchResult;
import net.project.resource.IPersonAttributes;
import net.project.resource.Person;

/**
 * Search Results from searching an LDAP server.
 * The results are backed by the original results of the search;
 * they have not all been read from the LDAP server.
 */
public class LDAPSearchResults extends AbstractSearchResults {

    /**
     * The LDAP attribute from which to build results.
     */
    private LDAPAttributeMap attributeMap = null;

    /**
     * The naming enumeration that is the results.
     */
    private NamingEnumeration results = null;

    /**
     * Cache of results since NamingEnumeration doesn't permit
     * backwards navigation.
     */
    private List cachedResults = null;

    /**
     * Index by ID into cachedResults.  Used for getting results
     * by ID.
     */
    private HashMap cachedResultsIDIndex = null;

    /**
     * Flag for tracking "hasMoreElements".  This is necessary
     * due to a bug in JDK 1.3.  Calling "hasMoreElements" on
     * a NamingEnumeration after it has already returned false
     * results in a NullPointerException.
     * Bug is fixed in JDK 1.4.
     */
    boolean hasMoreElements = true;

    /**
     * Creates a new SearchResults for the specified results
     * and attribute map.
     * @param results the LDAP enumeration resulting from a search
     * @param attributeMap the configuration's attribute map, required
     * to build meaningful results
     */
    protected LDAPSearchResults(NamingEnumeration results, LDAPAttributeMap attributeMap) {
        this.results = results;
        this.hasMoreElements = true;
        this.attributeMap = attributeMap;
        this.cachedResults = new ArrayList();
        this.cachedResultsIDIndex = new HashMap();
    }

    /*
     * Returns a list iterator over the Search Results.
     * Each element is of type <code>ISearchResult</code>.
     * It is an unmodifiable ListIterator: <code>add</code>, <code>remove</code>
     * and <code>set</code> will throw <code>UnsupportedOperationException</code>s.
     * @throws IndexOutOfBoundsException if the specified index is out of
     * range (<tt>index &lt; 0 || index &gt;= size()</tt> after all results
     * have been read).
     */
    public ListIterator listIterator(int index) {
        return new ListItr(index);
    }

    /**
     * Indicates the number of <code>SearchResult</code>s currently in this list.
     * <b>Note:</b> This breaks the contract of <code>java.util.List.size()</code>:
     * this number represents the <i>current</i> size of read results; this
     * number may grow if more results are returned.
     * @return the number of search results.
     */
    public int size() {
        return cachedResults.size();
    }

    /**
     * Returns a list containing <code>ISearchResult</code> objects
     * with the specified IDs.
     * The size of the returned list will only be the same as the length of
     * the array if <code>ISearchResult</code> objects were found
     * for each of the IDs.
     * <p>
     * Only looks in currently fetched results for matching IDs.
     * </p>
     * @param resultIDs the ID values for the <code>ISearchResult</code>s
     * to return
     * @return the <code>ISearchResult</code>s with matching IDs
     */
    public java.util.List getForIDs(String[] resultIDs) {

        List returnList = new ArrayList();

        // Lookup each resultID in the index
        // and add it to a return list if found

        for (int i = 0; i < resultIDs.length; i++) {
            ISearchResult result = (ISearchResult) this.cachedResultsIDIndex.get(resultIDs[i]);
            if (result != null) {
                returnList.add(result);
            }

        }

        return returnList;
    }


    /**
     * Wrapper for <code>this.results.hasMoreElements()</code> necessary
     * to circumvent a JDK 1.3 bug.
     * In that release, calling NamingEnumeration.hasMoreElements() after
     * it has previously returned false results in a NullPointerException
     * like this: <code><pre>
     * java.lang.NullPointerException
     *         at com.sun.jndi.ldap.LdapNamingEnumeration.getNextBatch(LdapNamingEnumeration.java:113)
     *         at com.sun.jndi.ldap.LdapNamingEnumeration.hasMore(LdapNamingEnumeration.java:167)
     * </pre></code>
     * This bug has been fixed in JDK 1.4.
     * @return true if results has more elements; false otherwise
     * @see javax.naming.NamingEnumeration#hasMoreElements
     */
    private boolean hasMoreElements() {
        
        // If we've previously set this.hasMoreElements to false
        // then use its value
        // Otherwise, use (and save) this.results.hasMoreElements()
        if (this.hasMoreElements) {
            this.hasMoreElements = this.results.hasMoreElements();
        }
        return this.hasMoreElements;
    }

    /**
     * Creates an LDAPDIrectoryEntry for the specified search result.
     * @param result the LDAP directory search result
     * @return the directory entry created from the LDAP search result
     * @throws net.project.base.directory.DirectoryException if there is a 
     * problem making the directory entry
     */
    private LDAPDirectoryEntry makeDirectoryEntry(javax.naming.directory.SearchResult result) 
            throws net.project.base.directory.DirectoryException {
        
        LDAPHelper ldap = new LDAPHelper();
        return ldap.makeDirectoryEntry(this.attributeMap, result.getAttributes());
    }


   /**
     * Ensures that enough results have been read to return an element
     * at the specified index.
     * Note: If we run out of elements no Exceptions are thrown; the
     * cached results are simply to few.
     * @param index the index position to read to
     */
    private void ensureReadResults(int index) {

        int currentSize = this.cachedResults.size();

        // If we don't have enough results
        // then read some more
        if (index >= currentSize) {
            
            LDAPSearchResult nextResult = null;

            // Loop while there are more results
            // And we haven't read up to 
            for (int nextIndex = currentSize; (hasMoreElements() && nextIndex <= index); nextIndex++) {

                try {
                    // Construct a search result by making a directory
                    // entry
                    nextResult = new LDAPSearchResult(
                            LDAPSearchResults.this.makeDirectoryEntry((javax.naming.directory.SearchResult) this.results.nextElement()),
                            String.valueOf(nextIndex)
                        );

                    // Add result to cached results
                    // and to the index map
                    this.cachedResults.add(nextResult);
                    this.cachedResultsIDIndex.put(nextResult.getID(), nextResult);

                } catch (net.project.base.directory.DirectoryException e) {
                    // What can we do?
                    // Simply return a null value?
                    throw new IllegalStateException("Unable to get next: " + e);

                }

            }

        }
    
    }

    /**
     * Indicates whether there is a search result at the specified index.
     * <b>Note:</b> This causes elements to be read up to specified index
     * position if not already available.
     * @param index the index position
     * @return true if there is a search result at the index; false otherwise
     */
    public boolean hasElement(int index) {
        ensureReadResults(index);
        // last index position available is one less than size of results
        return (index < size());
    }

    //
    // Inner classes
    //

    /**
     * A single search result.
     */
    private static class LDAPSearchResult extends AbstractSearchResult {
        
        /** 
         * The entry from which the result is built.
         */
        private LDAPDirectoryEntry entry = null;

        /**
         * The ID of this result.
         */
        private String id = null;
        
        private boolean disable; 
        
        private boolean isOnline;
        
        private Person person = null;
        
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
		 * @param isOnline the isOnline to set
		 */
		public void setOnline(boolean isOnline) {
			this.isOnline = isOnline;
		}
		
		public LDAPSearchResult(Person person){
			this.person = person;
		}
		
        /**
         * Creates a new LDAPSearchResult from the specified directory
         * entry.
         * @param entry the LDAPDirectoryEntry for this result
         * @param id the ID of this result; it must be unique within
         * the entire list of results
         */
        private LDAPSearchResult(LDAPDirectoryEntry entry, String id) {
            this.entry = entry;
            this.id = id;
        }
    
        public String getID() {
            return this.id;
        }

        public String getFirstName() {
            return this.entry.getAttributeValue(IPersonAttributes.FIRSTNAME_ATTRIBUTE);
        }

        public String getLastName() {
            return this.entry.getAttributeValue(IPersonAttributes.LASTNAME_ATTRIBUTE);
        }

        public String getDisplayName() {
            return this.entry.getAttributeValue(IPersonAttributes.DISPLAYNAME_ATTRIBUTE);
        }

        public String getEmail() {
            return this.entry.getAttributeValue(IPersonAttributes.EMAIL_ATTRIBUTE);
        }
        
        public String getPersonId() {
			return "";
		}
        
        public String getSearchedDisplayName(){
        	return getFirstName()+", "+getLastName();
        }
        
        public boolean getOnline(){
        	return this.isOnline;
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
     * Provides an iterator over the LDAP search results.
     * Each element is an <code>ISearchResult</code>.
     */
    private class ListItr extends AbstractSearchResults.ListItr {
                
        /**
         * Cursor pointing to current position so that we know
         * whether to read from the cached results or from the
         * NamingEnumeration.
         * The cursor always points to the "next" element to be read.
         * Sometimes this is beyond the end of the cached results; in
         * that case, enough results are read to return that element.
         */
        private int cursor = 0;

        /**
         * Creates a new ListItr.
         */
        public ListItr(int index) {
            // Note: Constructor is public as opposed to private to 
            // avoid a runtime error: java.lang.VerifyError when
            // compiled with jikes 1.15
            // See: http://oss.software.ibm.com/developerworks/opensource/jikes/
            // and bug number 2256 for status
            cursor = index;
        }

        public boolean hasPrevious() {
            // There is a previous entry if the cursor is not at
            // the start
            return cursor != 0;
        }

	public Object previous() {
            try {
                return LDAPSearchResults.this.cachedResults.get(--cursor);
            } catch(IndexOutOfBoundsException e) {
		throw new NoSuchElementException();
	    }
	}

	public int previousIndex() {
	    return cursor-1;
	}

        public boolean hasNext() {
            // Iterator has a next element if there is another
            // item in the cached results at the cursor
            // Or there is another element in the results
            return (cursor < LDAPSearchResults.this.cachedResults.size()) ||
                   (LDAPSearchResults.this.hasMoreElements());
        }

	public int nextIndex() {
	    return cursor;
	}

        public Object next() {
            LDAPSearchResult nextResult = null;

            // Ensure we have enough results up to the cursor
            LDAPSearchResults.this.ensureReadResults(cursor);

            if (LDAPSearchResults.this.cachedResults.size() <= cursor) {
                // Even after trying to fill up, there are not enough
                // items in the cached results to return a "next"
		throw new NoSuchElementException();
            
            } else {
                // Get the next item
                // Note that it is the item at the current cursor
                // position; cursor is increment _after_ fetching it
                nextResult = (LDAPSearchResult) LDAPSearchResults.this.cachedResults.get(cursor++);
                
            }

            return nextResult;
        }

    }
        
}
