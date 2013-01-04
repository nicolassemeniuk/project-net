<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
     xmlns:display="xalan://net.project.base.property.PropertyProvider"
     xmlns:format="xalan://net.project.util.XSLFormat"
     extension-element-prefixes="display format" >

<xsl:output method="html" />

<xsl:template match="/">
	<xsl:apply-templates />
</xsl:template>


<xsl:template match="License">
	<table width="100%" border="0" cellpadding="0" cellspacing="0">
        <tr class="tableContent">
            <td class="tableHeader">Internal ID:</td>
            <td class="tableContent"><xsl:value-of select="LicenseID" /></td>
        </tr>
        <tr class="tableContent">
            <td class="tableHeader">Trial License:</td>
            <td class="tableContent">
                <xsl:choose>
                    <xsl:when test="IsTrial='1'">
                        Yes
                    </xsl:when>
                    <xsl:otherwise>
                        No
                    </xsl:otherwise>
                </xsl:choose>
            </td>
        </tr>
        <tr class="tableContent">
            <td class="tableHeader">License Key:</td>
            <td class="tableContent"><xsl:value-of select="LicenseKey/DisplayString" /></td>
        </tr>
        <tr class="tableContent"><td class="tableContent" colspan="2"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td></tr>
        <tr class="tableContent">
            <td class="tableContent" colspan="2"><xsl:apply-templates select="LicenseCertificate" /></td>
        </tr>
        <tr class="tableContent"><td class="tableContent" colspan="2"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td></tr>
        <tr class="tableHeader">
            <td class="tableHeader" colspan="2">Payment Information</td>
        </tr>
        <tr class="tableContent">
            <td class="tableContent" colspan="2"><xsl:apply-templates select="PaymentInformation" /></td>
        </tr>
    </table>
</xsl:template>

<xsl:template match="LicenseCertificate">
    <table width="100%" border="0" cellpadding="0" cellspacing="0">
        <xsl:apply-templates select="LicenseModelCollection" />
        <xsl:apply-templates select="LicenseCostCollection" />
    </table>
</xsl:template>

<xsl:template match="LicenseModelCollection">
    <xsl:apply-templates />
</xsl:template>

<xsl:template match="LicenseCostCollection">
    <xsl:apply-templates />
</xsl:template>

<xsl:template match="LicenseModelCollection/UsageLimit">
    <tr class="tableContent">
        <td class="tableHeader">Maximum Users:</td>
        <td class="tableContent"><xsl:value-of select="format:formatNumber(MaxCount)" /></td>
    </tr>
    <tr class="tableContent">
        <td class="tableHeader">Current Users:</td>
        <td class="tableContent"><xsl:value-of select="format:formatNumber(CurrentCount)" /></td>
    </tr>
</xsl:template>

<xsl:template match="LicenseModelCollection/TimeLimit">
    <tr class="tableContent">
        <td class="tableHeader">End Date:</td>
        <td class="tableContent">
            <xsl:call-template name="Date">
                <xsl:with-param name="date" select="EndDate" />
            </xsl:call-template>
        </td>
    </tr>
    <tr class="tableContent">
        <td class="tableHeader">Original Time Limit:</td>
        <td class="tableContent"><xsl:value-of select="DayDuration" /></td>
    </tr>
</xsl:template>

<xsl:template match="LicenseModelCollection/NodeLocked">
    <tr class="tableContent">
        <td class="tableHeader">Node ID:</td>
        <td class="tableContent"><xsl:value-of select="NodeID/DisplayString" /></td>
    </tr>
</xsl:template>

<xsl:template match="LicenseCostCollection/Base">
    <tr class="tableContent">
        <td class="tableHeader">Base License Cost:</td>
        <td class="tableContent">USD <xsl:value-of select="UnitPrice/Money/Value" /></td>
    </tr>
</xsl:template>

<xsl:template match="LicenseCostCollection/Maintenance">
    <tr class="tableContent">
        <td class="tableHeader">Maintenance License Percentage:</td>
        <td class="tableContent"><xsl:value-of select="format:formatPercent(Percentage/Percentage/Value)" /></td>
    </tr>
</xsl:template>

<xsl:template match="PaymentInformation">
    <table width="100%" border="0" cellpadding="0" cellspacing="0">
        <xsl:apply-templates select="PaymentModel" />
    </table>
</xsl:template>

<xsl:template match="PaymentModel">
    <xsl:apply-templates />
</xsl:template>


<xsl:template match="PaymentModel/ChargeCode">
    <tr class="tableContent">
        <td class="tableHeader">Charge Code:</td>
        <td class="tableContent"><xsl:value-of select="Value" /></td>
    </tr>
</xsl:template>

<xsl:template match="PaymentModel/CreditCard">
    <tr class="tableContent">
        <td class="tableHeader">Credit Card Number:</td>
        <td class="tableContent"><xsl:value-of select="CardNumber" /></td>
    </tr>
    <tr class="tableContent">
        <td class="tableHeader">Expires:</td>
        <td class="tableContent"><xsl:value-of select="ExpiryMonth" /> / <xsl:value-of select="ExpiryYear" /></td>
    </tr>
</xsl:template>

<xsl:template match="PaymentModel/Trial">
    <tr class="tableContent">
        <td class="tableContent" colspan="2">Trial</td>
    </tr>
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
