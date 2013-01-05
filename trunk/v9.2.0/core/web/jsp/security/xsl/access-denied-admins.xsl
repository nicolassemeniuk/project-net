<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:display="xalan://net.project.base.property.PropertyProvider" extension-element-prefixes="display" >
<xsl:output method="html"/>
<xsl:variable name="translation" 
    	select="/ProjectXML/Properties/Translation/property" />
	<xsl:variable name="JSPRootURL" select="$translation[@name='JSPRootURL']" />

<xsl:template match="/">
    <xsl:apply-templates />
</xsl:template>

<xsl:template match="ProjectXML">
    <xsl:apply-templates select="Content" />
</xsl:template>

<xsl:template match="Content">
    <xsl:apply-templates />
</xsl:template>

<xsl:template match="person_list">
<table width="100%">
<tr class="tableLine">
    <td colspan="4" class="tableLine"><img src="{$JSPRootURL}/images/spacers/trans.gif" width="1" height="1" border="0" alt=""/></td>
</tr>

<xsl:apply-templates select="person" />

<tr class="tableLine">
    <td colspan="4" class="tableLine"><img src="{$JSPRootURL}/images/spacers/trans.gif" width="1" height="1" border="0" alt=""/></td>
</tr>
</table>
</xsl:template>


<xsl:template match="person">
<tr class="tableContent"> 
    <td class="tableContent">
	   	<xsl:element name="img">
			<xsl:attribute name="src"><xsl:value-of select="$translation[@name='JSPRootURL']"/>/images/group_person_small.gif</xsl:attribute>
			<xsl:attribute name="width">16</xsl:attribute>
			<xsl:attribute name="height">16</xsl:attribute>
			<xsl:attribute name="border">0</xsl:attribute>
			<xsl:attribute name="alt"><xsl:value-of select="display:get('prm.workflow.stepedit.roles.image.person.alttext')"/></xsl:attribute>
		</xsl:element>
    </td>
    <td class="tableContent" nowrap="true">
        <xsl:value-of select="full_name" />
    </td>
    <td class="tableContent">
        <a href="mailto:{email_address}"><xsl:value-of select="email_address" /></a>
    </td>
    <td width="50%"></td>
</tr>
</xsl:template>


</xsl:stylesheet>
