<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:display="xalan://net.project.base.property.PropertyProvider"
    xmlns:format="xalan://net.project.util.XSLFormat"
    extension-element-prefixes="display format" >

<xsl:output method="html" />

<xsl:template match="Invoice">
	<table width="100%" border="0" cellpadding="0" cellspacing="0">	
	    <table width="100%" border="0" cellpadding="0" cellspacing="0">
		    <tr class="tableHeader">
        		<td align="left" class="tableHeader">Invoice ID : <xsl:value-of select="InvoiceID"/></td>
			</tr>
			<tr><td><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td></tr>
		</table>
		<table width="100%" border="0" cellpadding="0" cellspacing="0">
	    	<tr class="tableHeader">
        		<td align="left" class="tableHeader">Ledger ID </td>
				<td align="left" class="tableHeader">Bill ID </td>
				<td align="left" class="tableHeader">Party Name</td>
				<td align="left" class="tableHeader">Due Since </td>
				<td align="left" class="tableHeader">Bill Group</td>
<!--				<td align="left" class="tableHeader">Part Details</td> -->
				<td align="left" class="tableHeader">Payment Information</td>
				<td align="left" class="tableHeader">Quantity</td>
				<td align="left" class="tableHeader">Unit Price</td>
				
			</tr>
		<xsl:apply-templates select="LedgerEntryCollection/LedgerEntry"/>		
		</table>
	</table>
	
</xsl:template>

<xsl:template match="LedgerEntryCollection/LedgerEntry">
	<tr class="tableContent">
    	<td align="left" class="tableContent">
		<xsl:value-of select="LedgerID"/>
		</td>
    	<td align="left" class="tableContent">
		<xsl:value-of select="BillID"/>
		</td>
		
		<xsl:apply-templates select="ResponsibleParty"/>			
		<td align="left" class="tableContent">
		<xsl:call-template name="Date">
                <xsl:with-param name="date" select="DueSinceDate" />
        </xsl:call-template>
		</td>
		
	<!--	<xsl:apply-templates select="Category/CategoryID"/> -->
		<xsl:apply-templates select="BillGroup"/>
		<!-- <xsl:apply-templates select="PartDetails"/>-->
		<xsl:apply-templates select="PaymentInformation"/>
		<xsl:apply-templates select="Quantity"/>
		<xsl:apply-templates select="Money"/>  					
	</tr>	
</xsl:template>
			
<xsl:template match="LedgerEntry/ResponsibleParty">
	
	<xsl:variable name="PartyName" select="PartyName"/>
	<xsl:choose>
		<xsl:when test="$PartyName!=''">
		<td align="left" class="tableContent"><xsl:value-of select="PartyName"/></td>
		</xsl:when>
		<xsl:otherwise>
		<td><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
		</xsl:otherwise>
	</xsl:choose>
</xsl:template>	

<xsl:template match="LedgerEntry/BillGroup">
        <td align="left" class="tableContent"><xsl:value-of select="Description" /></td>
</xsl:template>	

<xsl:template match="LedgerEntry/Category/CategoryID">
	<xsl:variable name="ID" select="ID" />     
        <td align="left" class="tableContent">
		<xsl:choose>
			<xsl:when test="$ID='100'">
				License Usage
			</xsl:when> 
			<xsl:otherwise>
				License Maintenance
			</xsl:otherwise>
		</xsl:choose>	
		</td>

</xsl:template>		

<xsl:template match="LedgerEntry/PartDetails">
        <td align="left"  class="tableContent">
		<xsl:value-of select="PartNumber"/>
		</td>
   
</xsl:template>

<xsl:template match="PaymentInformation">
    
        <xsl:apply-templates select="PaymentModel" />
    
</xsl:template>



<xsl:template match="LedgerEntry/Quantity">
        <td align="left" class="tableContent"><xsl:value-of select="format:formatNumber(Value)"/></td>
</xsl:template>

<xsl:template match="LedgerEntry/Money">
        <td align="left" class="tableContent">USD: <xsl:value-of select="Value" /></td>
</xsl:template>
			
			
<xsl:template match="PaymentModel">
     
    <xsl:apply-templates />
</xsl:template>

<!-- Charge Code -->
<xsl:template match="PaymentModel/ChargeCode">
        
        <td align="left" class="tableContent"><xsl:value-of select="Value" /></td>
    
</xsl:template>
<!-- Credit Card Number -->
<xsl:template match="PaymentModel/CreditCard">
    
        <td align="left" class="tableContent"><xsl:value-of select="CardNumber" /></td>
          
        <td align="left" class="tableHeader"> : </td>
        <td align="left" class="tableContent"><xsl:value-of select="ExpiryMonth" /> / <xsl:value-of select="ExpiryYear" /></td>
    
</xsl:template>
<!-- Trial -->
<xsl:template match="PaymentModel/Trial">
    
        <td align="left" class="tableContent" >Trial</td>
    
</xsl:template>	

<xsl:template match="Money">

        <td align="left" class="tableContent" ><xsl:value-of select="Value"/></td>
    
</xsl:template>		
			
<xsl:template name="Date">
    <xsl:param name="date" />
    <xsl:choose>
        <xsl:when test="$date">
            <xsl:value-of select="format:formatISODateTime($date)" />
        </xsl:when>
        <xsl:otherwise>
            <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
        </xsl:otherwise>
    </xsl:choose>
</xsl:template>			
			
			
</xsl:stylesheet>
