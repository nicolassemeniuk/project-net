<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:display="xalan://net.project.base.property.PropertyProvider"
    xmlns:format="xalan://net.project.util.XSLFormat"
    extension-element-prefixes="display format" >

<xsl:output method="text" />

<xsl:template match="InvoiceNotification">
	<xsl:apply-templates select="Invoice"/>
</xsl:template>

<xsl:template match="Invoice">
Hello,
Please find attached an invoice with Invoice ID : <xsl:value-of select="InvoiceSummary/InvoiceID"/>
			    
		
			
<xsl:apply-templates select="LedgerEntryCollection/LedgerEntry"/>		
		
	
	
</xsl:template>

<xsl:template match="LedgerEntryCollection/LedgerEntry">
	
    	
		<xsl:value-of select="LedgerID"/><xsl:value-of select="BillID"/><xsl:apply-templates select="ResponsibleParty"/><xsl:call-template name="Date"><xsl:with-param name="date" select="DueSinceDate" /></xsl:call-template><!--	<xsl:apply-templates select="Category/CategoryID"/> --><xsl:apply-templates select="BillGroup"/><!-- <xsl:apply-templates select="PartDetails"/>--><xsl:apply-templates select="PaymentInformation"/><xsl:apply-templates select="Quantity"/><xsl:apply-templates select="Money"/>  					
		
</xsl:template>
			
<xsl:template match="LedgerEntry/ResponsibleParty">
	
	<xsl:variable name="PartyName" select="PartyName"/>
	<xsl:choose>
		<xsl:when test="$PartyName!=''">
		<xsl:value-of select="PartyName"/>
		</xsl:when>
		<xsl:otherwise>
		
		</xsl:otherwise>
	</xsl:choose>
</xsl:template>	

<xsl:template match="LedgerEntry/BillGroup">
        <xsl:value-of select="Description" />
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
        
		<xsl:value-of select="PartNumber"/>
		
   
</xsl:template>

<xsl:template match="PaymentInformation">
    
        <xsl:apply-templates select="PaymentModel" />
    
</xsl:template>



<xsl:template match="LedgerEntry/Quantity">
        <xsl:value-of select="format:formatNumber(Value)"/>
</xsl:template>

<xsl:template match="LedgerEntry/Money">
        USD: <xsl:value-of select="Value" />
</xsl:template>
			
			
<xsl:template match="PaymentModel">
     
    <xsl:apply-templates />
</xsl:template>

<!-- Charge Code -->
<xsl:template match="PaymentModel/ChargeCode">
        
        <xsl:value-of select="Value" />
    
</xsl:template>
<!-- Credit Card Number -->
<xsl:template match="PaymentModel/CreditCard">
    
        <xsl:value-of select="CardNumber" />
          
         : 
        <xsl:value-of select="ExpiryMonth" /> / <xsl:value-of select="ExpiryYear" />
    
</xsl:template>
<!-- Trial -->
<xsl:template match="PaymentModel/Trial">
    
        Trial
    
</xsl:template>		

<xsl:template match="Money">

        <xsl:value-of select="Value"/>
    
</xsl:template>	
			
<xsl:template name="Date">
    <xsl:param name="date" />
    <xsl:choose>
        <xsl:when test="$date">
            <xsl:value-of select="format:formatISODateTime($date)" />
        </xsl:when>
        <xsl:otherwise>
            
        </xsl:otherwise>
    </xsl:choose>
</xsl:template>			
			
			
</xsl:stylesheet>
