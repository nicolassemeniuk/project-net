<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	 xmlns:display="xalan://net.project.base.property.PropertyProvider"
     xmlns:format="xalan://net.project.util.XSLFormat"
     extension-element-prefixes="display format" >

<xsl:output method="html" />

<xsl:template match="/">
    <xsl:apply-templates />
</xsl:template>

<xsl:template match="LicenseHistory">
    <table width="100%" border="0" cellpadding="0" cellspacing="0">
	    <xsl:apply-templates select="HistoryEntryCollection" />
    </table>
</xsl:template>

<xsl:template match="HistoryEntryCollection">
    <tr class="tableHeader">
        <td class="tableHeader"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
        <td class="tableHeader">Created Date</td>
        <td class="tableHeader">License Key</td>
        <td class="tableHeader">Trial?</td>
        <td class="tableHeader">Usage</td>
        <td class="tableHeader">Payment</td>
    </tr>
    <xsl:apply-templates select="HistoryEntry">
        <xsl:sort select="CreatedDatetime" />
    </xsl:apply-templates>
</xsl:template>

<xsl:template match="HistoryEntry">
	<tr class="tableContent">
        <td class="tableContent"><xsl:value-of select="position()" /></td>
        <td class="tableContent"><xsl:apply-templates select="CreatedDatetime" /></td>
        <td class="tableContent"><xsl:value-of select="License/LicenseKey/DisplayString" /></td>
        <td class="tableContent"><xsl:apply-templates select="License/IsTrial" /></td>
        <td class="tableContent">
            <xsl:choose>
                <xsl:when test="License/LicenseCertificate/LicenseModelCollection/UsageLimit">
                    <xsl:apply-templates select="License/LicenseCertificate/LicenseModelCollection/UsageLimit" />
                </xsl:when>
                <xsl:otherwise>
                    <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
                </xsl:otherwise>
            </xsl:choose>
        </td>
        <td class="tableContent">
            <xsl:apply-templates select="License/PaymentInformation" />
        </td>
    </tr>
</xsl:template>

<xsl:template match="PaymentInformation">
    <xsl:apply-templates select="PaymentModel" />
</xsl:template>

<xsl:template match="PaymentModel/ChargeCode">
    Charge Code: <xsl:value-of select="Value" />
</xsl:template>

<xsl:template match="PaymentModel/CreditCard">
    Credit Card
</xsl:template>

<xsl:template match="PaymentModel/Trial">
    Trial
</xsl:template>

<xsl:template match="IsTrial">
    <xsl:choose>
        <xsl:when test=".=1">
            Yes
        </xsl:when>
        <xsl:otherwise>
            No
        </xsl:otherwise>
    </xsl:choose>
</xsl:template>


<xsl:template match="UsageLimit">
	<xsl:value-of select="display:get('prm.admin.profile.license.history.usage.message', CurrentCount, MaxCount)" />
</xsl:template>

<xsl:template match="CreatedDatetime">
    <xsl:call-template name="Date">
        <xsl:with-param name="date" select="." />
    </xsl:call-template>
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
