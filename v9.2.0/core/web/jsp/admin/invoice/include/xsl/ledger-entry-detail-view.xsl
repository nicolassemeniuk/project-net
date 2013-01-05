<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:display="xalan://net.project.base.property.PropertyProvider"
	xmlns:format="xalan://net.project.util.XSLFormat"		
    extension-element-prefixes="display format" >

<xsl:output method="html" />

<xsl:template match="LedgerEntry">
	<table width="100%" border="0" cellpadding="0" cellspacing="0">	
		<tr><td>
	    <table width="100%" border="0" cellpadding="0" cellspacing="0">
			<tr><td><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td></tr>	
	    	<tr class="tableContent">
        		<td align="left" class="tableHeader">Ledger ID : </td>
				<td align="left" class="tableContent">
				<xsl:value-of select="LedgerID" />
				</td>
			</tr>	
			<tr>
				<td align="left" class="tableHeader">Bill ID  : </td> 
				
				<td align="left" class="tableContent">
				<xsl:value-of select="BillID"/>
				</td>
			</tr>
			<tr><td><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td></tr>	
			<tr>
				<td align="left" class="tableHeader">Due Since :</td>
				<td align="left" class="tableContent">
					<xsl:call-template name="Date">
            	    <xsl:with-param name="date" select="DueSinceDate" />
			        </xsl:call-template>
				</td>
			</tr>
			<tr><td><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td></tr>								
 			<xsl:apply-templates select="InvoiceStatus"/>
			<tr><td><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td></tr>		
				
			<tr class="tableContent">
    	    	<td align="left" class="tableHeader">Type :</td>
				<td align="left" class="tableContent">
				<xsl:apply-templates select="Category/CategoryID"/> 
				</td>	  					
			</tr>
			<tr><td><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td></tr>	
			<tr>
				<td align="left" class="tableHeader">Quantity :</td>
				<xsl:apply-templates select="Quantity"/>
			</tr>
			<tr>
				<td align="left" class="tableHeader">Unit Price : </td>
				<xsl:apply-templates select="Money"/>
			</tr>
			<tr>
				<td align="left" class="tableHeader">Total Cost :</td>
				<td align="left" class="tableContent">USD : <xsl:value-of select="TotalCost"/></td>
			</tr>
			<tr><td><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td></tr>	
			<tr>
				<td align="left" class="tableHeader">Party Info </td>
			</tr>
			<tr>
				<td align="left" class="tableHeader">Name :</td>
				<td align="left" class="tableContent">
				<xsl:apply-templates select="ResponsibleParty/PartyName"/>
				</td>
			</tr>
			<tr>
				<td align="left" class="tableHeader">Email :</td>
				<td align="left" class="tableContent">
				<xsl:apply-templates select="ResponsibleParty/Email"/>
				</td>
			</tr>
			<tr><td><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td></tr>	
			<!--
			<tr>
				<td align="left" class="tableHeader">Bill Group :</td>
				<xsl:apply-templates select="BillGroup"/>
			</tr>
			-->
				
			<tr>
				<td align="left" class="tableHeader">Pay Info :</td>
				<xsl:apply-templates select="PaymentInformation"/>
			</tr>
			
			<tr><td><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td></tr>
			
			<tr>
				<td align="left" class="tableHeader">Part Details </td> 
				
			</tr>
			<tr>
				<td align="left" class="tableHeader">
				Part Description :
				</td>
				<td align="left" class="tableContent">
				<xsl:apply-templates select="PartDetails/PartDescription"/>
				</td>
			</tr>
			<tr>
				<td align="left" class="tableHeader">
				Part Number :
				</td>
				<td align="left" class="tableContent">
				<xsl:apply-templates select="PartDetails/PartNumber"/>
				</td>
			</tr>
			<tr><td><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td></tr>
			
			
		</table>
		</td></tr>
	</table>
	
</xsl:template>
		
<xsl:template match="LedgerEntry/ResponsibleParty">
	
	<xsl:variable name="PartyName" select="PartyName"/>
	<xsl:variable name="Email" select="Email"/>
	<xsl:choose>
		<xsl:when test="$PartyName!=''">
		<td align="left" class="tableContent"><xsl:value-of select="PartyName"/></td>
		<td align="left" class="tableContent"><xsl:value-of select="Email"/></td>
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
        <xsl:choose>
			<xsl:when test="$ID='100'">
				License Usage
			</xsl:when> 
			<xsl:otherwise>
				License Maintenance
			</xsl:otherwise>
		</xsl:choose>	
		
</xsl:template>		

<xsl:template match="LedgerEntry/PartDetails">
		<td align="right"  class="tableHeader">Description :</td>
        <td align="left"  class="tableContent">
		<xsl:value-of select="PartNumber"/>
		</td>
		<td align="right"  class="tableHeader">License Key :</td>
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
        <td align="left" class="tableContent">Charge Code : <xsl:value-of select="Value" /></td>
  
</xsl:template>
<!-- Credit Card Number -->
<xsl:template match="PaymentModel/CreditCard">
        <td align="left" class="tableContent">Card Number: <xsl:value-of select="CardNumber" /></td>
        <td align="left" class="tableContent"><span class="tableHeader">Expiration Date: </span><xsl:value-of select="ExpiryMonth" /> / <xsl:value-of select="ExpiryYear" /></td>
</xsl:template>
<!-- Trial -->
<xsl:template match="PaymentModel/Trial">
    
        <td align="left" class="tableContent" >Trial</td>
    
</xsl:template>		

<xsl:template match="InvoiceStatus">

	<xsl:variable name="status" select="StatusID"/>
	<xsl:choose>
		<xsl:when test="$status='200'">
		<tr>
		<td align="left" class="tableHeader">Invoice Status  : </td>
		<td align="left" class="tableContent">Invoiced  </td>
		</tr>
		<tr>
		<td align="left" class="tableHeader">Invoice ID : </td>
		<td align="left" class="tableContent"><xsl:value-of select="//InvoiceID"/> </td>
		</tr>
		<tr>
		<td align="left" class="tableHeader">Invoice Date :</td>
		<td align="left" class="tableContent">
			<xsl:call-template name="Date">
           	    <xsl:with-param name="date" select="//InvoiceDate" />
			</xsl:call-template>
		</td>
		</tr>
		</xsl:when>
		<xsl:otherwise>
		<tr>
		<td align="left" class="tableHeader">Invoice Status  : </td>
		<td align="left" class="tableContent"> Not Invoiced </td>
		</tr>
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
