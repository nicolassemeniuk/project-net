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

 /*-----------------------------------------------------------------------------+
|
|    $RCSfile$
|   $Revision: 18397 $
|       $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|     $Author: umesha $
|
+-----------------------------------------------------------------------------*/
package net.project.security;

import java.util.Arrays;

import net.project.base.property.PropertyProvider;

/**
 * Provides an enumeration of access grants each of which provides a message
 * to be displayed.
 *
 * @author Matthew Flower
 */
public abstract class SecurityAccess {

    //
    // Static Members
    //

    /** Access Denied when an attempt is made to visit an inactive space. */
    public static final SecurityAccess INACTIVE_SPACE = new DeniedSecurityAccess("Permission denied; Inactive space", "prm.security.securityprovider.accessdenied.inactivespace.message");

    /** Access Denied when no module parameter was specified in a URL. */
    public static final SecurityAccess NO_MODULE_SPECIFIED = new DeniedSecurityAccess("Permission denied; No module", "prm.security.securityprovider.accessdenied.nomodulespecified.message");

    /**
     * Access Denied when access to an object is attempted; module permission is
     * granted but no object permission is found for roles or principal role.
     */
    public static final SecurityAccess NO_ACCESS_TO_ONE_OR_MORE = new DeniedSecurityAccess("Permission denied to Object(s)", "prm.security.securityprovider.accessdenied.missingaccesspermission.message");


    /**
     * Access Denied to module.
     * Module permission is turned off for all roles that a user is a member of.
     */
    public static final class ACCESS_DENIED_TO_MODULE extends DeniedParameterizedSecurityAccess {
        public ACCESS_DENIED_TO_MODULE(Object[] parameters) {
            super("Permission denied to module", "prm.security.securityprovider.checkaccess.module.message", parameters);
        }
    }

    /**
     * Access Denied to module.
     * Module permission is turned off specifically in a user's principal role.
     */
    public static final class ACCESS_DENIED_TO_MODULE_FOR_PRINCIPAL extends DeniedParameterizedSecurityAccess {
        public ACCESS_DENIED_TO_MODULE_FOR_PRINCIPAL(Object[] parameters) {
            super("Permission denied to module for principal", "prm.security.securityprovider.checkprincipalaccess.module.message", parameters);
        }
    };

    /**
     * Access denied to an object.
     * Module permission is granted but object permission is turned off specifically
     * in a user's principal role.
     */
    public static final class ACCESS_DENIED_TO_OBJECT_FOR_PRINCIPAL extends DeniedParameterizedSecurityAccess {
        public ACCESS_DENIED_TO_OBJECT_FOR_PRINCIPAL(Object[] parameters) {
            super("Permission denied to object for principal", "prm.security.securityprovider.checkprincipalaccess.object.message", parameters);
        }
    }

    /**
     * Access denied to an object.
     * Module permission is granted but no object permission for any role that
     * a user is a member of.
     */
    public static final class ACCESS_DENIED_TO_OBJECT extends DeniedParameterizedSecurityAccess {
        public ACCESS_DENIED_TO_OBJECT(Object[] parameters) {
            super("Permission denied to object", "prm.security.securityprovider.checkaccess.object.message", parameters);
        }
    }

    /** Permission Granted. */
    public static final SecurityAccess GRANTED = new GrantedSecurityAccess("Permission granted");

    //
    // Instance Members
    //

    private final boolean isGranted;
    private final String informationString;
    private final String messageToken;

    /**
     * Creates a new SecurityAccess indicating whether permission is isGranted
     * or denied.
     * @param isGranted true if this access type indicates permission is isGranted;
     * false otherwise
     * @param informationString the string representation of this security access
     * type for information or debugging purposes
     */
    private SecurityAccess(boolean isGranted, String informationString) {
        this(isGranted, informationString, null);
    }

    /**
     * Creates a new SecurityAccess specifying a token to use for the display
     * message.
     * @param isGranted true if this access type indicates permission is isGranted;
     * false otherwise
     * @param informationString the string representation of this security access
     * type for information or debugging purposes
     * @param messageToken the token name to use for displaying a message
     */
    private SecurityAccess(boolean isGranted, String informationString, String messageToken) {
        this.isGranted = isGranted;
        this.informationString = informationString;
        this.messageToken = messageToken;
    }

    /**
     * Returns the token used to provide the message.
     * @return the token name
     */
    protected String getMessageToken() {
        return this.messageToken;
    }

    /**
     * Returns the display message for this SecurityAccess type.
     * This method should only be called if the a message token was specified.
     * @return the message displayed for the specified token
     */
    public String getMessage() {
        return PropertyProvider.get(messageToken);
    }

    /**
     * Indicates whether this SecurityAccess means permission was granted.
     * @return true if permission is granted; false otherwise
     */
    public boolean isGranted() {
        return isGranted;
    }

    /**
     * Provides a string representation of this SecurityAccess, suitable
     * for informational purposes or debugging.
     * @return the string representation of this SecurityAccess
     */
    public String toString() {
        return informationString;
    }

    //
    // Nested top-level classes
    //

    /**
     * Provides a SecurityAccess type where permission is granted.
     */
    public static class GrantedSecurityAccess extends SecurityAccess {
        protected GrantedSecurityAccess(String informationString) {
            super(true, informationString);
        }
    }

    /**
     * Provides a SecurityAccess type where permission is denied with
     * a particular message token supplying a display message.
     */
    public static class DeniedSecurityAccess extends SecurityAccess {
        protected DeniedSecurityAccess(String informationString, String messageToken) {
            super(false, informationString, messageToken);
        }
    }

    /**
     * Provides a DeniedSecurityAccess class which allows specification of
     * of replacement parameters to be passed to a message.
     */
    public static class DeniedParameterizedSecurityAccess extends DeniedSecurityAccess {

        private Object[] parameters;

        /**
         * Creates a new DeniedParameterizedSecurityAccess with the specified
         * token for the message and replacement parameters.
         * @param informationString information string used for <code>toString</code>
         * @param messageToken the token name for the display message
         * @param parameters the replacement parameters for the message
         */
        public DeniedParameterizedSecurityAccess(String informationString, String messageToken, Object[] parameters) {
            super(informationString, messageToken);
            this.parameters = parameters;
        }

        /**
         * Returns the display message for this security access type, with
         * specified replacement parameters inserted.
         * @return the display message
         */
        public String getMessage() {
            return PropertyProvider.get(getMessageToken(), parameters);
        }

        public String toString() {
            return super.toString() + ", Parameters: " + Arrays.asList(this.parameters);
        }
    }

}
