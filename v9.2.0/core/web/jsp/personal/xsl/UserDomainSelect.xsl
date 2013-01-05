<?xml version="1.0"?>
<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:display="xalan://net.project.base.property.PropertyProvider"
    xmlns:format="xalan://net.project.util.XSLFormat"
    extension-element-prefixes="display format" >

<xsl:output method="html" />

<xsl:template match="/">
	<xsl:apply-templates select="ProjectXML" />
</xsl:template>

<xsl:template match="ProjectXML">
	<xsl:apply-templates select="Content" />
</xsl:template>

<xsl:template match="Content">
	<xsl:apply-templates select="UserDomainCollection" />
</xsl:template>

<xsl:template match="UserDomainCollection">
	<table width="100%" border="0" cellpadding="0" cellspacing="0">
		<xsl:apply-templates select="UserDomain" />
		<xsl:if test="count(UserDomain)=0">
            <xsl:call-template name="NoUserDomains"/>
        </xsl:if>    
	</table>
</xsl:template>

<xsl:template match="UserDomain">
	<tr class="tableContent">
		<td width="10%"><input type="radio" name="selected" value="{id}" /></td>
		<td class="tableHeader"><xsl:value-of select="name" /></td>
	</tr>
    <tr class="tableContent">
    	<td width="10%"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
    	<td class="tableContent">
        <xsl:choose>
            <xsl:when test="parsedRegistrationInstructions!=''">
                <xsl:value-of select="format:formatText(parsedRegistrationInstructions)" disable-output-escaping="yes"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
            </xsl:otherwise>
        </xsl:choose>
        </td>
    </tr>
    <tr class="tableContent">
        <td colspan="2">
            <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
        </td>
    </tr>
</xsl:template>

<xsl:template name="NoUserDomains">
        <tr class="tableContent" align="left">
            <td class="tableContent" colspan="2"><xsl:value-of select="display:get('prm.personal.profile.domain.nodomains.message')"/></td>
        </tr>
</xsl:template>
</xsl:stylesheet>

