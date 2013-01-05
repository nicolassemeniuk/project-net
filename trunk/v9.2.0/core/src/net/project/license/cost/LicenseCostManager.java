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

import java.math.BigDecimal;
import java.util.Currency;

import net.project.base.money.Money;
import net.project.base.quantity.Percentage;
import net.project.license.LicenseException;
import net.project.license.system.MasterProperties;
import net.project.license.system.PropertyName;
import net.project.persistence.PersistenceException;

/**
 * Provides a registry of license costs.
 */
public class LicenseCostManager {

    public static LicenseCostManager getInstance() {
        return new LicenseCostManager();
    }


    /**
     * @deprecated Use {@link LicenseCostManager#getInstance()} instead.
     */
    public LicenseCostManager() {

    }

    /**
     * Returns the base cost unit price.
     * @return the base cost unit price
     */
    public Money getBaseCostUnitPrice() throws LicenseException, PersistenceException {
        // Currently specified entirely by a property value
        BigDecimal value = new BigDecimal(MasterProperties.getInstance().get(PropertyName.LICENSE_COST_BASE).getValue());
        return new Money(value);
    }

    /**
     * Returns the maintenance cost unit price.
     * @return the maintenance cost unit price
     */
    public Percentage getMaintenanceCostPercentage() throws LicenseException,
        PersistenceException {

        // Currently specified entirely by a property value
        BigDecimal value = new BigDecimal(MasterProperties.getInstance().get(PropertyName.LICENSE_COST_MAINTENANCE).getValue());
        return new Percentage(value);
    }

    /**
     * Returns the trial cost unit price.
     * @return the trial cost unit price
     */
    public Money getTrialCostUnitPrice() throws LicenseException,
        PersistenceException {

        // Currently specified entirely by a property value
        BigDecimal value = new BigDecimal(MasterProperties.getInstance().get(PropertyName.LICENSE_COST_TRIAL).getValue());
        return new Money(value);
    }

    public Money getCreditCardTransactionFee(Money totalCost) throws LicenseException, PersistenceException {
        Money transactionFee;

        CreditCardServiceChargeType type =  CreditCardServiceChargeType.getForID(
            MasterProperties.getInstance().get(PropertyName.CREDIT_CARD_SERVICE_CHARGE_TYPE).getValue()
        );

        if (type.equals(CreditCardServiceChargeType.FIXED_COST)) {
            String fixedCost = MasterProperties.getInstance().get(PropertyName.CREDIT_CARD_SERVICE_CHARGE).getValue();
            String currency = MasterProperties.getInstance().get(PropertyName.CREDIT_CARD_SERVICE_CHARGE_CURRENCY).getValue();
            transactionFee = new Money(fixedCost, Currency.getInstance(currency));
        } else {
            String percentage = MasterProperties.getInstance().get(PropertyName.CREDIT_CARD_SERVICE_CHARGE).getValue();
            Percentage transactionFeePercentage = new Percentage(percentage);
            transactionFee = totalCost.multiply(transactionFeePercentage);
        }

        return transactionFee;
    }

}

