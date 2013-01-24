<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:display="xalan://net.project.base.property.PropertyProvider" extension-element-prefixes="display" >

<xsl:output method="html"/>

<xsl:template match="/">
    <xsl:apply-templates />
</xsl:template>

<xsl:template match="ProjectXML">
    <xsl:apply-templates select="Content" />
</xsl:template>

<xsl:template match="Content">
    <xsl:apply-templates />
</xsl:template>

<xsl:template match="SpaceList">
<table width="100%" border="0" cellpadding="0" cellspacing="0">
    <xsl:apply-templates select="Space">
        <xsl:sort select="SpaceType/id" />
    </xsl:apply-templates>
    <xsl:if test="not(Space)">
        <tr>
            <td class="tableContent">
                <xsl:value-of select="display:get('prm.directory.roles.groupcreate.inherit.noworkspace.message')"/>
            </td>
        </tr>
    </xsl:if>
</table>
</xsl:template>
	
<xsl:template match="Space">
<tr>
    <td class="tableContent">
        <input type="checkbox" name="selectedSpaceID" value="{id}" />
        <xsl:value-of select="name" />
    </td>
</tr>
</xsl:template>

</xsl:stylesheet>
