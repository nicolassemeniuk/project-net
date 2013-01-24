<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:display="xalan://net.project.base.property.PropertyProvider"
	xmlns:format="xalan://net.project.util.XSLFormat"		
    extension-element-prefixes="display format" >

<xsl:output method="html" />

<xsl:template match="InvoiceList">
	<table width="100%" border="0" cellpadding="0" cellspacing="0">	
	<tr><td colspan="4"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td></tr>
	<tr>
		<td class="tableHeader" align="left">Invoice Number</td>
		<td class="tableHeader" align="left">Invoice Date</td>
		<td class="tableHeader" align="left">Due Date</td>
		<td class="tableHeader" align="left">Invoice Amount</td>
	</tr>
	<tr class="tableLine">
	   	<td colspan="4" class="tableLine"><img src="../../images/spacers/trans.gif" width="1" height="2" border="0" alt=""/></td>
	</tr>
	<xsl:apply-templates select="Invoice/InvoiceSummary">
	<xsl:sort select="InvoiceDate" order="descending"/>
	</xsl:apply-templates>
	<tr><td colspan="4"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td></tr>
	</table>

</xsl:template>

<xsl:template match="InvoiceSummary">
	<tr class="tableContent">
       	<td align="left">
			<xsl:text disable-output-escaping="yes">&lt;a href="</xsl:text>
			<xsl:value-of select="//JSPRootURL"/>
			/admin/invoice/ViewInvoiceProcessing.jsp?invoiceID=<xsl:value-of select="InvoiceID"/>
			&amp;theAction=submit&amp;module=240&amp;action=1
			<xsl:text disable-output-escaping="yes">"&gt;</xsl:text>
			<xsl:value-of select="InvoiceID" />
			<xsl:text disable-output-escaping="yes">&lt;/a&gt;</xsl:text>
		</td>
		<td align="left"> 
			<xsl:call-template name="Date">
			<xsl:with-param name="date" select="InvoiceDate" />
			</xsl:call-template>
		</td>
		<td align="left">
			<xsl:call-template name="Date">
			<xsl:with-param name="date" select="DueDate" />
			</xsl:call-template>
		</td>
		<td align="left"><xsl:value-of select="LedgerSummary/AllTotal"/></td>	
	</tr>
	<tr class="tableLine">
	   	<td colspan="4" class="tableLine"><img src="../../images/spacers/trans.gif" width="1" height="1" border="0" alt=""/></td>
	</tr>
			
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
