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
package net.project.base.compatibility.modern;

import net.project.base.compatibility.AbstractCompatibilityProviderImpl;
import net.project.base.compatibility.ICompatibilityProvider;

/**
 * Provides implementation for a generic modern container compatibility.
 * <p/>
 * <b>Note:</b> This class should never be used directly; only through reflection when it can be guaranteed to exist.
 * It is only compiled into the deployment for certain builds. </p>
 *
 * @author Tim Morrow
 * @since Version 7.7
 */
public final class ModernCompatibilityProviderImpl extends AbstractCompatibilityProviderImpl implements ICompatibilityProvider {

    public ModernCompatibilityProviderImpl() {
        super(new LocalSessionProvider(),
                new ContainerResourceProvider(),
                new ContainerXSLProvider(),
                new ContainerConfigurationProvider(),
                new DataSourceConnectionProvider(),
                new ModernClobProvider(),
                new ContainerMailSessionProvider());
    }

}
