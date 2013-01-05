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
|     $RCSfile$
|    $Revision: 18948 $
|        $Date: 2009-02-21 09:39:24 -0200 (sáb, 21 feb 2009) $
|      $Author: ritesh $
|                                                                       
+----------------------------------------------------------------------*/
package net.project.base.compatibility;

/**
 * Provides basic implementation for a compatibility provider.
 * 
 * @author Tim Morrow
 * @since Version 7.7
 */
public class AbstractCompatibilityProviderImpl implements ICompatibilityProvider {

    private final ISessionProvider sessionProvider;
    private final IResourceProvider resourceProvider;
    private final IXSLProvider xslProvider;
    private final IConfigurationProvider configurationProvider;
    private final IConnectionProvider connectionProvider;
    private final IClobProvider clobProvider;
    private final IMailSessionProvider mailSessionProvider;

    public AbstractCompatibilityProviderImpl(ISessionProvider sessionProvider, IResourceProvider resourceProvider,
            IXSLProvider xslProvider, IConfigurationProvider configurationProvider,
            IConnectionProvider connectionProvider, IClobProvider clobProvider,
            IMailSessionProvider mailSessionProvider) {

        this.sessionProvider = sessionProvider;
        this.resourceProvider = resourceProvider;
        this.xslProvider = xslProvider;
        this.configurationProvider = configurationProvider;
        this.connectionProvider = connectionProvider;
        this.clobProvider = clobProvider;
        this.mailSessionProvider = mailSessionProvider;
    }

    public final ISessionProvider getSessionProvider() {
        return this.sessionProvider;
    }

    public final IResourceProvider getResourceProvider() {
        return this.resourceProvider;
    }

    public final IXSLProvider getXSLProvider() {
        return this.xslProvider;
    }

    public final IConfigurationProvider getConfigurationProvider() {
        return this.configurationProvider;
    }

    public final IConnectionProvider getConnectionProvider() {
        return this.connectionProvider;
    }

    public IClobProvider getClobProvider() {
        return this.clobProvider;
    }

    public IMailSessionProvider getMailSessionProvider() {
        return this.mailSessionProvider;
    }

}
