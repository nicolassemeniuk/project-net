<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:display="xalan://net.project.base.property.PropertyProvider"
	xmlns:format="xalan://net.project.util.XSLFormat"		
    extension-element-prefixes="display format" >

<xsl:output method="html" />

<xsl:template match="LedgerEntryList">
	<table width="100%" border="0" cellpadding="0" cellspacing="0">	
		<tr><td>
	    <table width="100%" border="0" cellpadding="0" cellspacing="0">
			<tr><td><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td></tr>
	    	<tr class="tableHeader">
        		<td align="left" class="tableHeader">Ledger ID </td>
				<td align="left" class="tableHeader">Due Since </td>
				<td align="left" class="tableHeader">Type </td>
				<td align="left" class="tableHeader">Bill Group</td>
				<td align="left" class="tableHeader">Pay Info</td>
				<td align="left" class="tableHeader">Qty</td>
				<td align="left" class="tableHeader">Unit Price</td>
				<td align="left" class="tableHeader">Total Cost</td>
				<td align="left" class="tableHeader">Invoiced?</td>
				
			</tr>
			<tr class="tableLine">
	    	<td colspan="9" class="tableLine"><img src="../../images/spacers/trans.gif" width="1" height="2" border="0" alt=""/></td>
			</tr>
			<tr><td><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td></tr>
		<xsl:apply-templates select="LedgerEntry">
			<xsl:sort select="BillGroup/GroupType/GroupTypeID/ID"/>
			<xsl:sort select="BillGroup/PaymentInformation/PaymentModel"/> 
		</xsl:apply-templates>
		<tr class="tableLine">
	    	<td colspan="9" class="tableLine"><img src="../../images/spacers/trans.gif" width="1" height="2" border="0" alt=""/></td>
		</tr>		
		<tr><td><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td></tr>
		<tr>
			<td align="right" colspan="7" class="tableHeader"><xsl:text disable-output-escaping="yes">Total Amount Not Invoiced  &amp;nbsp; : &amp;nbsp;</xsl:text></td>
			<td align="left" class="tableContent"><xsl:value-of select="TotalAmountNotInvoiced"/></td>
		</tr>
		</table>
		</td></tr>
	</table>
	
</xsl:template>

<xsl:template match="LedgerEntry">
	<tr class="tableContent">
    	<td align="left" class="tableContent">
		
		<xsl:text disable-output-escaping="yes">&lt;a href="</xsl:text>
		<xsl:value-of select="//JSPRootURL"/>
		/admin/invoice/LedgerEntryDetailView.jsp?ledgerID=<xsl:value-of select="LedgerID"/>
		&amp;module=240&amp;action=1
		<xsl:text disable-output-escaping="yes">"&gt;</xsl:text>
		<xsl:value-of select="LedgerID" />
		<xsl:text disable-output-escaping="yes">&lt;/a&gt;</xsl:text>
		</td>
				
		<td align="left" class="tableContent">
		<xsl:call-template name="Date">
                <xsl:with-param name="date" select="DueSinceDate" />
        </xsl:call-template>
		</td>
	
		<xsl:apply-templates select="Category/CategoryID"/> 
		<xsl:apply-templates select="BillGroup"/>
		<!-- <xsl:apply-templates select="PartDetails"/>-->
		<xsl:apply-templates select="PaymentInformation"/>
		<xsl:apply-templates select="Quantity"/>
		<xsl:apply-templates select="Money"/>  					
		<td align="left" class="tableContent"><xsl:value-of select="TotalCost"/></td>
		<td align="center" class="tableContent">
			<xsl:apply-templates select="InvoiceStatus"/>
		</td>
		
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
	<xsl:variable name="id" select="GroupType/GroupTypeID/ID"/>
	<td align="left" class="tableContent">
	<xsl:choose>
		<xsl:when test="$id='100'">
	        Charge Code
		</xsl:when>
		<xsl:when test="$id='200'">
    	    Credit Card
		</xsl:when>
		<xsl:when test="$id='300'">
        	Trial
		</xsl:when>
		<xsl:otherwise>
	        Unspecified
		</xsl:otherwise>
	</xsl:choose>	
	</td>
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
</xsl:template>
<!-- Trial -->
<xsl:template match="PaymentModel/Trial">
    
        <td align="left" class="tableContent" >Trial</td>
    
</xsl:template>			

<xsl:template match="InvoiceStatus">
	<xsl:variable name="status" select="StatusID"/>
	<xsl:choose>
		<xsl:when test="$status='200'">
		<img src="../../images/check_green.gif" alt=""/>
		</xsl:when>
		<xsl:otherwise>
		<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
		</xsl:otherwise>
	</xsl:choose>
</xsl:template>
			
<xsl:template name="Date">
    <xsl:param name="date" />
    <xsl:choose>
        <xsl:when test="$date">
            <xsl:value-of select="format:formatISODate($date)" />
            <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
           <!--
		    <xsl:value-of select="substring-after($date, 'T')" />
			-->
        </xsl:when>
        <xsl:otherwise>
            <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
        </xsl:otherwise>
    </xsl:choose>
</xsl:template>			
			
			
</xsl:stylesheet>

