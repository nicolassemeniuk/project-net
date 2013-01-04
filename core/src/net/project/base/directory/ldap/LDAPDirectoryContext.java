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

import javax.naming.directory.DirContext;

import net.project.base.directory.search.DirectorySearchException;
import net.project.base.directory.search.ISearchResults;
import net.project.base.directory.search.SearchControls;
import net.project.base.directory.search.SearchFilter;
import net.project.resource.IPersonAttributes;

/**
 * Provides a context for searching this LDAPSearchableDirectory.
 * Note that this is the directory context as applies to 
 * Project.net searching; we reused the terminology from JNDI.
 */
public class LDAPDirectoryContext implements net.project.base.directory.search.IDirectoryContext {

    /**
     * The DirectoryProvider to search with.
     */
    private LDAPDirectoryProvider provider = null;

    /**
     * Creates an LDAPDirectoryContext for searching against the
     * specified provider.
     * @param provider the provider with which to search
     */
    LDAPDirectoryContext(LDAPDirectoryProvider provider) {
        this.provider = provider;
    }


    /**
     * Searches the LDAP directory.
     * @param filter the filter to apply while searching
     * @param controls the parameters affecting the search
     * @return the results of the search
     * @throws DirectorySearchException if there is a problem searching
     */
    public ISearchResults search(SearchFilter filter, SearchControls controls) 
            throws DirectorySearchException {
        
        LDAPDirectoryConfiguration config = (LDAPDirectoryConfiguration) this.provider.getConfiguration();
        LDAPAttributeMap attributeMap = config.getAttributeMap();
        String additionalFilterExpression = config.getSearchFilterExpression();

        //
        // Construct a filter expression from the current filter
        //
        
        String nameFilterExpression = null;
        String name = filter.get("name");
        if (name != null && name.trim().length() > 0) {
            
            // We have a name, so build an expression
            // suitable for searching by name
            // Tt will be of the form:
            //    (|(sn=...)(givenName=...)(cn=...))
            // we build the expression depending on
            // whether there is a mapping for that component

            // Build the expression for the name component
            nameFilterExpression = buildNameFilterExpression(name, attributeMap);
        }

        // Figure out which expressions we actually have and whether we have multiple expressions
        // When we have multiple expressions, must AND them all together
        boolean hasAdditionalFilterExpression = (additionalFilterExpression != null && additionalFilterExpression.trim().length() > 0);
        boolean hasNameFilterExpression = (nameFilterExpression != null && nameFilterExpression.trim().length() > 0);
        boolean hasMultipleExpressions = (hasAdditionalFilterExpression && hasNameFilterExpression);
        
        StringBuffer finalFilterExpression = new StringBuffer();

        if (hasMultipleExpressions) {
            // Outer AND of all expressions
            finalFilterExpression.append("(&");
        }

        if (hasAdditionalFilterExpression) {
            finalFilterExpression.append(additionalFilterExpression);
        }
        
        if (hasNameFilterExpression) {
            finalFilterExpression.append(nameFilterExpression);
        }

        if (hasMultipleExpressions) {
            // Close the outer AND of all expressions
            finalFilterExpression.append(")");
        }

        //
        // Now perform the actual search
        //

        LDAPSearchResults searchResults = null;

        try {
            // get context
            LDAPHelper ldap = new LDAPHelper();
        
            // Get a context to the search base
            // By using the same context we potentially maintain
            // a single connection with the LDAP server
            // (This statement yet to be proven)
            DirContext context = ldap.getBaseContext(config);
        
            // Search for records matching filter
            // and build SearchResults            
            searchResults = new LDAPSearchResults(
                    ldap.search(context, config, finalFilterExpression.toString(), controls),
                    config.getAttributeMap() );
        
        } catch (net.project.base.directory.DirectoryException e) {
            throw new DirectorySearchException("Problem performing search: " + e, e);
        
        }
    
        return searchResults;
    }

    /**
     * Builds an LDAP filter expression for searching on the
     * specified name.
     * @param name the name value to search on
     * @return the search filter or null if no ldap attributes have
     * been mapped for the profile attributes that are searched during
     * a name search.
     */
    private String buildNameFilterExpression(String name, LDAPAttributeMap attributeMap) {
        
        boolean isAtLeastOneName = true;
        String nameFilterExpression = null;

        String firstNameAttribute = attributeMap.getLDAPAttributeName(IPersonAttributes.FIRSTNAME_ATTRIBUTE);
        String lastNameAttribute = attributeMap.getLDAPAttributeName(IPersonAttributes.LASTNAME_ATTRIBUTE);
        String displayNameAttribute = attributeMap.getLDAPAttributeName(IPersonAttributes.DISPLAYNAME_ATTRIBUTE);

        StringBuffer nameExpression = new StringBuffer();
        nameExpression.append("(|");

        if (firstNameAttribute != null) {
            nameExpression.append("(").append(firstNameAttribute).append("=*").append(name).append("*)");
            isAtLeastOneName = true;
        }

        if (lastNameAttribute != null) {
            nameExpression.append("(").append(lastNameAttribute).append("=*").append(name).append("*)");
            isAtLeastOneName = true;
        }

        if (displayNameAttribute != null) {
            nameExpression.append("(").append(displayNameAttribute).append("=*").append(name).append("*)");
            isAtLeastOneName = true;
        }

        nameExpression.append(")");

        if (isAtLeastOneName) {
            // We did add some component to the expression
            // We want to return it
            nameFilterExpression = nameExpression.toString();
        
        } else {
            // remains null
        
        }

        return nameFilterExpression;
    }

    //
    // Unit test
    //

    /**
     * Usage:
     * LDAPDirectoryProvider [searchName]
     *     searchName  -  the name to search on
     */
    public static void main(String[] args) {

        String searchName = null;

        for (int i = 0; i < args.length; i++) {
            switch (i) {
            case 0:
                searchName = args[i];
                break;
            }
        }

        try {
            LDAPDirectoryConfiguration config = new LDAPDirectoryConfiguration();
            config.setHostnameValues("hippo.project.net");
            config.setSecureHostnameValues("hippo.project.net");
            config.setSearchBaseDN("dc=project,dc=net");
//            config.setSearchType(SearchType.ALL_SUBTREES);
            config.setSearchType(SearchType.LIMIT_SUBTREES);
            config.setSearchSubtrees("ou=People");
            config.setUsernameAttribute("uid");
            config.setUseSSL(false);
            config.setNonAuthenticatedAccessType(NonAuthenticatedAccessType.ANONYMOUS);
            
            // Make an attribute map
            LDAPAttributeMap map = new LDAPAttributeMap();
            map.put(new net.project.base.attribute.TextAttribute("person.email","Email"), new LDAPAttributeMap.LDAPAttributeDefinition("mail"));
            map.put(new net.project.base.attribute.TextAttribute("person.lastname", "Last Name"), new LDAPAttributeMap.LDAPAttributeDefinition("sn"));
            map.put(new net.project.base.attribute.TextAttribute("person.firstname", "First Name"), new LDAPAttributeMap.LDAPAttributeDefinition("givenName", 0));
            map.put(new net.project.base.attribute.TextAttribute("person.displayname", "Display Name"), new LDAPAttributeMap.LDAPAttributeDefinition("cn", 0));
            map.put(new net.project.base.attribute.TextAttribute("address.address1", "Address 1"), new LDAPAttributeMap.LDAPAttributeDefinition("postaladdress", 0));
            map.put(new net.project.base.attribute.TextAttribute("address.city", "City"), new LDAPAttributeMap.LDAPAttributeDefinition("postaladdress", 1));
            map.put(new net.project.base.attribute.TextAttribute("address.zipcode", "Zipcode"), new LDAPAttributeMap.LDAPAttributeDefinition("postaladdress", 2));
            config.setAttributeMap(map);


            LDAPDirectoryProvider provider = new LDAPDirectoryProvider();
            provider.setConfiguration(config);

            LDAPDirectoryContext directoryContext = new LDAPDirectoryContext(provider);

            System.out.println("Configuration: " + config);

            // Search
            System.out.println("Searching...");
            System.out.println("searchName: " + searchName);


            SearchFilter searchFilter = new SearchFilter();
            searchFilter.add("name", searchName);
            SearchControls searchControls = new SearchControls();

            System.out.println("Searched with results: ");

            ISearchResults searchResults = directoryContext.search(searchFilter, searchControls);

            System.out.println(searchResults.toString());
            
            System.out.println("Done");


        } catch (Exception e) {
            e.printStackTrace();

        }

    }

}
