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
package net.project.creditcard;

import java.util.Map;

/**
 * A vendor account represents the person who will receive money for normal
 * debit transactions.  (Often, this means it is Project.Net's account.  For
 * OEM customers, this might be their own account.)
 *
 * @author Matthew Flower
 * @since Version 7.4
 */
public interface IMerchantAccount {
    /**
     * This method will get a map which contains any information required to be
     * submitted to a credit card processor in order to process a transaction.
     *
     * It would have been preferable to not have to return a map, but every
     * processor has slightly different requirements, so this will just make it
     * easier.
     *
     * @return a <code>Map</code> which contains the parameters necessary to
     * credit the vendor's account.
     */
    public Map getParameters();
}
