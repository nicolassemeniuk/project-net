<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:display="xalan://net.project.base.property.PropertyProvider" extension-element-prefixes="display" >

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
        <td><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
        <td class="tableHeader"><xsl:value-of select="display:get('prm.personal.profile.license.history.createddate.column')"/></td>
        <td class="tableHeader"><xsl:value-of select="display:get('prm.personal.profile.license.history.licensekey.column')"/></td>
        <td class="tableHeader"><xsl:value-of select="display:get('prm.personal.profile.license.history.trial.column')"/></td>
        <td class="tableHeader"><xsl:value-of select="display:get('prm.personal.profile.license.history.usage.column')"/></td>
        <td class="tableHeader"><xsl:value-of select="display:get('prm.personal.profile.license.history.payment.column')"/></td>
    </tr>
    <xsl:apply-templates select="HistoryEntry">
        <xsl:sort select="CreatedDatetime" />
    </xsl:apply-templates>
        
</xsl:template>


<xsl:template match="HistoryEntry">
	<tr class="tableContent">
        <td class="tableContent"><xsl:value-of select="position()" /></td>
        <td><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
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
    <xsl:value-of select="display:get('prm.personal.profile.license.history.payment.status.chargecode.name')"/><xsl:value-of select="Value" />
</xsl:template>

<xsl:template match="PaymentModel/CreditCard">
    <xsl:value-of select="display:get('prm.personal.profile.license.history.payment.status.creditcard.name')"/>
</xsl:template>

<xsl:template match="PaymentModel/Trial">
    <xsl:value-of select="display:get('prm.personal.profile.license.history.payment.status.trial.name')"/>
</xsl:template>

<xsl:template match="IsTrial">
    <xsl:choose>
        <xsl:when test=".=1">
            <xsl:value-of select="display:get('prm.personal.profile.license.history.trial.status.yes.name')"/>
        </xsl:when>
        <xsl:otherwise>
            <xsl:value-of select="display:get('prm.personal.profile.license.history.trial.status.no.name')"/>
        </xsl:otherwise>
    </xsl:choose>
</xsl:template>


<xsl:template match="UsageLimit">
	<xsl:value-of select="display:get('prm.personal.profile.license.history.usage.status.number.name', CurrentCount, MaxCount)"/>
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
            <xsl:value-of select="substring-before($date, 'T')" />
            <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
            <xsl:value-of select="substring-after($date, 'T')" />
        </xsl:when>
        <xsl:otherwise>
            <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
        </xsl:otherwise>
    </xsl:choose>
</xsl:template>

</xsl:stylesheet>
