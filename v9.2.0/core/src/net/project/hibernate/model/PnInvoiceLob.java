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
package net.project.hibernate.model;


import java.sql.Clob;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * PnInvoiceLob generated by hbm2java
 */
@Entity
@Table(name="PN_INVOICE_LOB"
)
public class PnInvoiceLob  implements java.io.Serializable {

    /** identifier field */
    private Integer invoiceId;

    /** nullable persistent field */
    private Clob invoiceLobData;

    /** full constructor */
    public PnInvoiceLob() {
    }

	/** minimal constructor */
    public PnInvoiceLob(Integer invoiceId) {
        this.invoiceId = invoiceId;
    }
    
    public PnInvoiceLob(Integer invoiceId, Clob invoiceLobData) {
       this.invoiceId = invoiceId;
       this.invoiceLobData = invoiceLobData;
    }
   
    @Id 
    @Column(name="INVOICE_ID", nullable=false)
    public Integer getInvoiceId() {
        return this.invoiceId;
    }
    
    public void setInvoiceId(Integer invoiceId) {
        this.invoiceId = invoiceId;
    }
    
    @Column(name="INVOICE_LOB_DATA", length=4000)
    public Clob getInvoiceLobData() {
        return this.invoiceLobData;
    }
    
    public void setInvoiceLobData(Clob invoiceLobData) {
        this.invoiceLobData = invoiceLobData;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("invoiceId", getInvoiceId())
            .toString();
    }

}


