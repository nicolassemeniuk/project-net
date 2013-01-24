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
|   $Revision: 18397 $
|       $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|     $Author: umesha $
|                                                                       
+----------------------------------------------------------------------*/
package net.project.license.cost;

import net.project.base.money.Money;
import net.project.base.quantity.Quantity;

/**
 * A LicenseCharge is the actual charge for a license cost.
 * It is based on a license and a specific cost.  While the cost may be
 * a per-usage price or a percentage of another cost, the LicenseCharge is always 
 * an actual price.
 *
 * @author Tim Morrow
 * @since Gecko Update 2
 */
public class LicenseCharge {
    private Quantity quantity = null;
    private Money unitPrice = null;

    public LicenseCharge() {
        // Do nothing
    }

    void setQuantity(Quantity quantity) {
        this.quantity = quantity;
    }

    public Quantity getQuantity() {
        return this.quantity;
    }

    void setUnitPrice(Money unitPrice) {
        this.unitPrice = unitPrice;
    }

    public Money getUnitPrice() {
        return this.unitPrice;
    }

}
