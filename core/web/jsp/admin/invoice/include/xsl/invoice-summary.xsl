<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:display="xalan://net.project.base.property.PropertyProvider"
	xmlns:format="xalan://net.project.util.XSLFormat"		
    extension-element-prefixes="display format" >

<xsl:output method="html" />

<xsl:template match="Invoice">
<xsl:apply-templates select="InvoiceSummary"/>
</xsl:template>

<xsl:template match="InvoiceSummary">
	<table width="100%" border="0" cellpadding="0" cellspacing="0">	
		<tr><td>
	    <table width="100%" border="0" cellpadding="0" cellspacing="0">
		    <tr><td><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td></tr>
			<tr class="tableHeader">
        		<td align="left" class="tableHeader">Invoice ID : <xsl:value-of select="InvoiceID"/></td>
				<td align="right" class="tableHeader">Invoice Date : 
				<xsl:call-template name="Date">
				<xsl:with-param name="date" select="InvoiceDate" />
				</xsl:call-template>
				</td>
			</tr>
			<tr>
				<td align="left" class="tableHeader">Project Net Info : </td>
				<td align="right" class="tableHeader">Terms : NET <xsl:value-of select="InvoiceTerm"/></td>
			</tr>
			<tr>
				<td align="left" class="tableContent"><xsl:value-of select="ProjectNetInfo"/></td>
				<td align="right" class="tableHeader">Due Date : 
				<xsl:call-template name="Date">
				<xsl:with-param name="date" select="DueDate" />
				</xsl:call-template>
				</td>
			</tr>
			<tr><td><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td></tr>
			<tr>
			<td align="left" class="tableHeader">Billing Info : </td> 
			<td align="right" class="tableHeader">Date Range : 
				<xsl:call-template name="Date">
				<xsl:with-param name="date" select="LedgerSummary/DateRange/MinDate" />
				</xsl:call-template> 
				To
				<xsl:call-template name="Date">
				<xsl:with-param name="date" select="LedgerSummary/DateRange/MaxDate" />
				</xsl:call-template>
			</td>
			
			</tr>
			<tr>
			<td align="left" class="tableContent"><xsl:value-of select="CustomerBillingInfo"/></td>
			</tr>
			<tr><td><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td></tr>
			
			<tr><td><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td></tr>
			
			<tr class="tableLine">
	    	<td colspan="2" class="tableLine"><img src="../../images/spacers/trans.gif" width="1" height="2" border="0" alt=""/></td>
			</tr>
			
		</table>
		</td></tr>
		<tr><td>
		<table width="100%" border="0" cellpadding="0" cellspacing="0">
	    	<tr class="tableHeader">
        		<td align="left" class="tableHeader">Quantity</td>
				<td align="left" class="tableHeader">Description</td>
				<td align="left" class="tableHeader">Unit Price</td>
				<td align="left" class="tableHeader">Amount</td>
			</tr>
			<tr class="tableLine">
	    	<td colspan="4" class="tableLine"><img src="../../images/spacers/trans.gif" width="1" height="2" border="0" alt=""/></td>
		</tr>

		<xsl:apply-templates select="LedgerSummary"/>
		
		</table>
		</td></tr>
	</table>
	
</xsl:template>

<xsl:template match="LedgerSummary">
	<tr class="tableContent">
    	<xsl:apply-templates select="LicenseUsage"/>
		
	</tr>
	<tr class="tableContent">
    	
		<xsl:apply-templates select="LicenseMaintenance"/>
		
	</tr>
	<tr class="tableContent">
    	
		<xsl:apply-templates select="TrialLicense"/>
		
	</tr>
	<tr class="tableLine">
	    	<td colspan="4" class="tableLine"><img src="../../images/spacers/trans.gif" width="1" height="2" border="0" alt=""/></td>
		</tr>
	<tr>
	<td><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
	<td><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
	<td align="right" class="tableHeader"><xsl:text disable-output-escaping="yes">All Total &amp;nbsp;&amp;nbsp; </xsl:text></td>
	<td align="left" class="tableHeader"><xsl:value-of select="AllTotal"/></td>
	</tr>
				
</xsl:template>

<xsl:template match="LicenseUsage">
	<td align="left" class="tableContent">
	<xsl:value-of select="format:formatNumber(TotalUsageQuantity)"/>
	</td>
	<td align="left" class="tableContent">
	Project.Net User Licenses
	</td>
	<td align="left" class="tableContent">
	<xsl:value-of select="UsageUnitCost"/>
	</td>
	<td align="left" class="tableContent">
	<xsl:value-of select="TotalUsageCost"/>
	</td>
</xsl:template>

<xsl:template match="LicenseMaintenance">
	<td align="left" class="tableContent">
	<xsl:value-of select="TotalMaintenanceQuantity"/>
	</td>
	<td align="left" class="tableContent">
	Project.Net Maintenance
	</td>
	<td align="left" class="tableContent">
	<xsl:value-of select="MaintenanceUnitCost"/>
	</td>
	<td align="left" class="tableContent">
	<xsl:value-of select="TotalMaintenanceCost"/>
	</td>
</xsl:template>

<xsl:template match="TrialLicense">
	<td align="left" class="tableContent">
	<xsl:value-of select="format:formatNumber(TotalTrialQuantity)"/>
	</td>
	<td align="left" class="tableContent">
	Trial Licenses
	</td>
	<td align="left" class="tableContent">
	<xsl:value-of select="TrialUnitCost"/>
	</td>
	<td align="left" class="tableContent">
	<xsl:value-of select="TotalTrialCost"/>
	</td>
</xsl:template>

		
<xsl:template name="Date">
    <xsl:param name="date" />
    <xsl:choose>
        <xsl:when test="$date">
            <xsl:value-of select="format:formatISODate($date)" />
        </xsl:when>
        <xsl:otherwise>
            <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
        </xsl:otherwise>
    </xsl:choose>
</xsl:template>			
			
			
</xsl:stylesheet>

