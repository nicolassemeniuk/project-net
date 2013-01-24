<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:display="xalan://net.project.base.property.PropertyProvider"
	xmlns:format="xalan://net.project.util.XSLFormat"		
    extension-element-prefixes="display format" >

<xsl:output method="html" />

<xsl:template match="Invoice">
	<table width="100%" border="0" cellpadding="0" cellspacing="0">	
		<tr><td>
	    <table width="100%" border="0" cellpadding="0" cellspacing="0">
		    <tr><td><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td></tr>
			<tr class="tableHeader">
        		<td align="left" class="tableHeader">Invoice ID : <xsl:value-of select="InvoiceSummary/InvoiceID"/></td>
				<td align="right" class="tableHeader">Date Range : 
				<xsl:call-template name="Date">
				<xsl:with-param name="date" select="InvoiceSummary/LedgerSummary/DateRange/MinDate" />
				</xsl:call-template> 
				To
				<xsl:call-template name="Date">
				<xsl:with-param name="date" select="InvoiceSummary/LedgerSummary/DateRange/MaxDate" />
				</xsl:call-template>
				</td>
			</tr>
			<tr><td><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td></tr>
		</table>
		</td></tr>
		<tr><td>
		<table width="100%" border="0" cellpadding="0" cellspacing="0">
	    <tr><td>
		<xsl:apply-templates select="LedgerEntryCollection/LedgerDetails"/>
		</td></tr>
		</table>
		</td></tr>
	</table>
	
</xsl:template>

<xsl:template match="LedgerEntryCollection/LedgerDetails">
	<table width="100%" border="0" cellpadding="0" cellspacing="0">
		<tr><td align="left" class="fieldRequired">Charge Code</td></tr>
		<tr class="tableLine">
	    	<td colspan="9" class="tableLine"><img src="../../images/spacers/trans.gif" width="1" height="2" border="0" alt=""/></td>
		</tr>
		 <tr><td><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td></tr>
		<tr><td>
		<xsl:apply-templates select="ChargeCodeEntries/ChargeCode"/>
		</td></tr>
		<tr><td><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td></tr>
		
		<!-- Currently commented since we don't expect any credit card entries for now
		<tr><td align="left" class="tableHeader">Credit Card</td></tr>
		<tr><td><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td></tr>
		<tr class="tableLine">
	    	<td colspan="9" class="tableLine"><img src="../../images/spacers/trans.gif" width="1" height="2" border="0" alt=""/></td>
		</tr>
		<tr><td>
		<xsl:apply-templates select="CreditCardEntries/CreditCard"/>
		</td></tr> 
		-->
		
		<tr><td><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td></tr>
		<tr><td align="left" class="fieldRequired">Trials</td></tr>
		
		<tr class="tableLine">
	    	<td colspan="9" class="tableLine"><img src="../../images/spacers/trans.gif" width="1" height="2" border="0" alt=""/></td>
		</tr>
		<tr><td><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td></tr>
		<tr><td>
		<xsl:apply-templates select="TrialEntries"/>
		</td></tr>
		<tr><td><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td></tr>
	</table>	
		
</xsl:template>

<xsl:template match="ChargeCode">
	<table width="100%" border="0" cellpadding="0" cellspacing="0">
		
		<tr>
		<td align="left" class="tableHeader">CC : </td><td align="left" class="tableContentHighLight"><xsl:value-of select="ChargeCodeNumber"/></td>
		<td align="right" class="tableHeader">SubTotal : </td><td align="left" class="tableContentHighLight">USD: <xsl:value-of select="ChargeCodeSubTotal"/></td>
		<td colspan="5"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
		</tr>
		<tr><td colspan="9"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td></tr>
		<tr>
			<td><img src="../../images/spacers/trans.gif" width="1" height="1" border="0" alt=""/></td>
			<td colspan="8" class="tableLine"><img src="../../images/spacers/trans.gif" width="1" height="1" border="0" alt=""/></td>
		</tr>
		<tr class="tableHeader">
			<td><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
       		<td align="left" class="tableHeader">Ledger ID </td>
			<td align="left" class="tableHeader">Type </td>
			<td align="left" class="tableHeader">Party Name</td>
			<td align="left" class="tableHeader">Email</td>
			<td align="left" class="tableHeader">Due Since </td>
			<td align="left" class="tableHeader">Qty</td>
			<td align="left" class="tableHeader">U Price</td>
			<td align="left" class="tableHeader">Total</td>
	</tr>
	<tr>
		<td><img src="../../images/spacers/trans.gif" width="1" height="1" border="0" alt=""/></td>
		<td colspan="8" class="tableLine"><img src="../../images/spacers/trans.gif" width="1" height="1" border="0" alt=""/></td>
	</tr>	
		<tr><td><xsl:apply-templates select="LedgerEntry"/></td></tr>
		<tr><td><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td></tr>
	</table>	
</xsl:template>

<xsl:template match="CreditCard">
	<table width="100%" border="0" cellpadding="0" cellspacing="0">
		<tr class="tableLine">
	    	<td colspan="9" class="tableLine"><img src="../../images/spacers/trans.gif" width="1" height="2" border="0" alt=""/></td>
		</tr>
		<tr>
		<td align="left" class="tableHeader">CCN : </td><td align="left" class="fieldRequired"><xsl:value-of select="CreditCardNumber"/></td>
		<td align="right" class="tableHeader">SubTotal : </td><td align="left" class="tableContentHighLight">USD: <xsl:value-of select="CreditCardSubTotal"/></td>
		</tr>
		<tr><td><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td></tr>
		<tr class="tableLine">
			<td><img src="../../images/spacers/trans.gif" width="1" height="1" border="0" alt=""/></td>
	    	<td colspan="8" class="tableLine"><img src="../../images/spacers/trans.gif" width="1" height="1" border="0" alt=""/></td>
		</tr>
		<tr class="tableHeader">
			<td align="left" class="tableHeader">Ledger ID </td>
			<td align="left" class="tableHeader">Type </td>
			<td align="left" class="tableHeader">Party Name</td>
			<td align="left" class="tableHeader">Email</td>
			<td align="left" class="tableHeader">Due Since </td>
			<td align="left" class="tableHeader">Qty</td>
			<td align="left" class="tableHeader">U Price</td>
			<td align="left" class="tableHeader">Total</td>
	</tr>
	<tr class="tableLine">
		<td><img src="../../images/spacers/trans.gif" width="1" height="1" border="0" alt=""/></td>
	   	<td colspan="8" class="tableLine"><img src="../../images/spacers/trans.gif" width="1" height="1" border="0" alt=""/></td>
	</tr>	
		<tr><td><xsl:apply-templates select="LedgerEntry"/></td></tr>
		<tr><td><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td></tr>
	</table>	
</xsl:template>

<xsl:template match="TrialEntries">
	<table width="100%" border="0" cellpadding="0" cellspacing="0">
		
		<tr>
		<td><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text> </td><td><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text> </td>
		<td align="left" class="tableHeader">SubTotal : </td><td align="left" class="tableContentHighLight">USD: <xsl:value-of select="TrialSubTotal"/></td>
		<td colspan="5"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
		</tr>
		<tr><td colspan="9"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td></tr>
		<tr>
			<td colspan="1"><img src="../../images/spacers/trans.gif" width="1" height="1" border="0" alt=""/></td>
	    	<td colspan="8" class="tableLine"><img src="../../images/spacers/trans.gif" width="1" height="1" border="0" alt=""/></td>
		</tr>
		<tr class="tableHeader">
		    <td><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
			<td align="left" class="tableHeader">Ledger ID </td>
			<td align="left" class="tableHeader">Type </td>
			<td align="left" class="tableHeader">Party Name</td>
			<td align="left" class="tableHeader">Email</td>
			<td align="left" class="tableHeader">Due Since </td>
			<td align="left" class="tableHeader">Qty</td>
			<td align="left" class="tableHeader">U Price</td>
			<td align="left" class="tableHeader">Total</td>
	</tr>
	<tr>
		<td><img src="../../images/spacers/trans.gif" width="1" height="1" border="0" alt=""/></td>
	   	<td colspan="8" class="tableLine"><img src="../../images/spacers/trans.gif" width="1" height="1" border="0" alt=""/></td>
	</tr>	
		<tr><td colspan="9"><xsl:apply-templates select="LedgerEntry"/></td></tr>
		<tr><td colspan="9"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td></tr>
	</table>	
</xsl:template>

<xsl:template match="LedgerEntry">
	
	<tr class="tableContent">
		<td><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
    	<td align="left" class="tableContent">
		<xsl:value-of select="LedgerID"/>
		</td>
    	<xsl:apply-templates select="Category/CategoryID"/> 
		<xsl:apply-templates select="ResponsibleParty"/>			
		<td align="left" class="tableContent">
		<xsl:call-template name="Date">
                <xsl:with-param name="date" select="DueSinceDate" />
        </xsl:call-template>
		</td>
		<xsl:apply-templates select="Quantity"/>
		<xsl:apply-templates select="Money"/>  					
		<td align="left" class="tableContent"><xsl:value-of select="TotalCost" /></td>
	</tr>	
</xsl:template>
			
<xsl:template match="LedgerEntry/ResponsibleParty">
	
	<xsl:variable name="PartyName" select="PartyName"/>
	<xsl:variable name="Email" select="Email"/>
	<xsl:choose>
		<xsl:when test="$PartyName!=''">
		<td align="left" class="tableContent"><xsl:value-of select="PartyName"/></td>
		</xsl:when>
		<xsl:otherwise>
		<td><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
		</xsl:otherwise>
	</xsl:choose>
	<xsl:choose>
		<xsl:when test="$Email!=''">
		<td align="left" class="tableContent"><xsl:value-of select="Email"/></td>
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
            <xsl:value-of select="format:formatISODate($date)" />
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
