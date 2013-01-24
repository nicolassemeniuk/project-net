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
import java.util.Iterator;
import java.util.List;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;

import net.project.base.directory.AuthenticationFailedException;
import net.project.base.directory.CommunicationException;
import net.project.base.directory.ConfigurationException;
import net.project.base.directory.DirectoryException;
import net.project.base.directory.TooManyEntriesException;

/**
 * Provides methods for testing an LDAP Configuration.
 * This communicates directly with the LDAP Servers.
 * It should only be used specifically for testing an LDAP Directory Configuration.
 * <p>
 * Typical usage: <pre><code>
 * LDAPConfigurationTester tester = new LDAPConfigurationTester(config);
 * tester.testAuthenticate("user", "pass");
 * tester.getXML();
 * </code></pre>
 * </p>
 */
public class LDAPConfigurationTester implements net.project.persistence.IXMLPersistence {

    /**
     * The LDAPDirectoryConfiguration providing settings to test.
     */
    private LDAPDirectoryConfiguration configuration = null;

    /**
     * The collection of results that occurred during a test.
     */
    private List resultList = null;

    /**
     * Creates a new configuration tester based on the specified configuration.
     * @param config the LDAP directory configuration settings
     */
    public LDAPConfigurationTester(LDAPDirectoryConfiguration config) {
        this.configuration = config;
    }

    /**
     * Tests authentication.
     * @param username the username to authenticate with
     * @param cleartextPassword the password to authenticate with
     */
    public void testAuthenticate(String username, String cleartextPassword) {
        
        // Clear out existing errors
        prepareTest();

        boolean isError = false;

        if (username == null || cleartextPassword == null) {
            addError("Username and password are required values.", null);
            return;
        }

        LDAPHelper ldap = new LDAPHelper();

        // First try and get a base context to an LDAP server
        DirContext context = null;

        try {
            context = ldap.getBaseContext(this.configuration);

            // Find out which provider URL was actually used
            String usedProviderURL = null;
            try {
                usedProviderURL = (String) context.getEnvironment().get(Context.PROVIDER_URL);
            } catch (NamingException e) {
                throw new DirectoryException("Unexpected error when requesting context environment", e);
            }

            addResult("Successfully contacted LDAP server and fetched a base context with the provider URL: " + usedProviderURL);

        } catch (CommunicationException e) {
            addError("There was a problem connecting with an LDAP server", e);
            isError = true;

        } catch (ConfigurationException e) {
            StringBuffer error = new StringBuffer();
            error.append("Connection to an LDAP server was successful, but there was a problem getting the base context. ");
            
            // Add more detail if specific username & password entered for non-authenticated access
            if (this.configuration.getNonAuthenticatedAccessType().equals(NonAuthenticatedAccessType.SPECIFIC_USER)) {
                error.append("Check the username and password entered for non-authenticated access ");
                error.append("(currently '" + this.configuration.getSpecificUserDN() + " / " + this.configuration.getSpecificUserPassword() + "'. ");
                error.append("The user DN must be a properly formatted DN (for example: uid=scott, ou=People), not simply a username. ");
            }
            
            addError(error.toString(), e);
            isError = true;
        
        } catch (DirectoryException e) {
            addError("Connection to an LDAP server was successful, but an unexpected error occured", e);
            isError = true;

        }

        // Get out of here if an error has occurred so far
        // Bad to have multiple exit points, but better than lots of nested
        // code blocks
        if (isError) {
            addError("Test Failed", null);
            return;
        }

        // Search for the user entry that contains the username; returns an absolute and relative DN
        LDAPHelper.UserDN userDN = null;
        
        try {
            userDN = ldap.findUserDN(context, this.configuration, username);
            addResult("Successfully found a single user record for the specified username. ");

        } catch (AuthenticationFailedException e) {
            // This actually means we couldn't find a user with the specified
            // username
            addError("Unable to find user record with username '" + username + "'. " +
                     "Check the spelling and ensure that the correct username attribute has been specified (currently '" + this.configuration.getUsernameAttribute() + "'). " +
                     "Check the object limit filter is correct (currently '" + this.configuration.getSearchFilterExpression() + "'). "
                     , e);
            isError = true;

        } catch (ConfigurationException e) {
            // Problem with the RDN of the base of the search
            addError("Unable to find user record with username '" + username + "'. "+
                     "LDAP server reported that the constructed search DN is invalid. " +
                     "Check the search base DN (currently '" + this.configuration.getSearchBaseDN() + "'). " +
                     (this.configuration.getSearchTypeID().equals(SearchType.LIMIT_SUBTREES.getID()) ? "Check the search subtree (currently '" + this.configuration.getSearchSubtrees() + "'). " : "") +
                     "Check the object limit filter is valid and contains parentheses (currently '" + this.configuration.getSearchFilterExpression() + "'). "
                     , e);
            isError = true;
        
        } catch (TooManyEntriesException e) {
            // More than one record found
            addError("Found more than one matching user records username '" + username + "'. " +
                     "Contact the LDAP Administrator.", e);
            isError = true;
        
        } catch (DirectoryException e) {
            addError("Unable to find user record with username '" + username + "'. " +
                     "An unexpected error occurred. ", e);
            isError = true;
        
        }

        if (isError) {
            addError("Test Failed", null);
            return;
        }

        // Now authenticate
        try {
            ldap.fetchAuthenticatedAttributes(context, userDN, cleartextPassword);
            addResult("Successfully authenticated against an LDAP server with specified username and password.");

        } catch (AuthenticationFailedException e) {
            // Bad password or possibly inactive user account
            addError("Unable to authenticate.  Check the password (make sure case is correct) and ensure the user account on the LDAP server is not Inactive.", e);
            isError = true;

        } catch (DirectoryException e) {
            addError("Unable to authenticate; an unexpected error occurred.", e);
            isError = true;
        }
        
        if (isError) {
            addError("Test Failed", null);
            return;
        }

        // At this point, the test was successful
        addResult("Test completed Successfully.");
    
    }

    /**
     * Prepares for performing a test, including clearing out of existing errors.
     */
    private void prepareTest() {
        this.resultList = new ArrayList();
    }

    /**
     * Adds a result to the result list.
     * @param message the message to add
     */
    private void addResult(String message) {
        TestResult result = new TestResult(message);
        this.resultList.add(result);
    }

    /**
     * Adds an error to the result list.
     * @param message the message to add
     * @param directoryException causing DirectoryException
     */
    private void addError(String message, DirectoryException directoryException) {
        TestResult error = new TestResult(message, directoryException);
        this.resultList.add(error);
    }

    /**
     * Returns the XML representation of the results of the test.
     * Includes the xml Version tag.
     * @return the xml representation
     */
    public String getXML() {
        return getXMLDocument().getXMLString();
    }

    /**
    * Returns the XML representation of the results of the test.
     * Does not include the xml Version tag.
     * @return the xml representation
     */
    public String getXMLBody() {
        return getXMLDocument().getXMLBodyString();
    }

    /**
     * Returns an <code>XMLDocument</code> for this LDAPSearchResult.
     * Example: <pre><code>
     * <TestResults>
     *     <Result isError='1'>
     *         <Message>Some message</Message>
     *         <OriginatingError>
     *             <Message>Some other message</Message>
     *             <Cause>Causing throwable message</Cause>
     *         </OriginatingError>
     *     </Result>
     *     <Result isError='0'>
     *         <Message>Some message</Message>
     *         <OriginatingError />
     *     </Result>
     * </TestResults>
     * </code></pre>
     * @return the XMLDocument; may be empty if there is a problem
     * creating it
     */
    protected net.project.xml.document.XMLDocument getXMLDocument() {
        net.project.xml.document.XMLDocument xml = new net.project.xml.document.XMLDocument();

        try {
            xml.startElement("TestResults");
            
            for (Iterator it = this.resultList.iterator(); it.hasNext(); ) {

                TestResult nextResult = (TestResult) it.next();

                xml.startElement("Result");
                xml.addAttribute("isError", new Boolean(nextResult.isError));
                xml.addElement("Message", nextResult.message);
                xml.startElement("OriginatingError");
                if (nextResult.directoryException != null) {
                    xml.addElement("Message", nextResult.directoryException.toString());
                    
                    xml.startElement("Cause");
                    Throwable cause = nextResult.directoryException.getCause();
                    if (cause != null) {
                        xml.addValue(cause.toString());
                    }
                    xml.endElement();
                }
                xml.endElement();
                xml.endElement();
            }


            xml.endElement();

        } catch (net.project.xml.document.XMLDocumentException e) {
            // Simply return empty XML

        }

        return xml;
    }

    /**
     * Provides a simple data structure for maintaining results that have occurred
     * during a test.
     */
    private static class TestResult {

        String message = null;
        DirectoryException directoryException = null;
        boolean isError = false;

        /**
         * Creates a new non-error test result.
         * @param message the message to be displayed
         */
        TestResult(String message) {
            this(message, false);
        }

        /**
         * Creates a new test result.
         * @param message the message to be displayed
         * @param isError specifies whether this is an error result; true
         * means it is an error; false means it is a success
         */
        TestResult(String message, boolean isError) {
            this.message = message;
            this.isError = isError;
        }

        /**
         * Creates a new error test result.
         * @param message the message to be displayed
         * @param directoryException the causing exception
         */
        TestResult(String message, DirectoryException directoryException) {
            this(message, true);
            this.directoryException = directoryException;
        }
    }


}

