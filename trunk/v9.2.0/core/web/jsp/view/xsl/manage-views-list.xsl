<?xml version='1.0'?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:display="xalan://net.project.base.property.PropertyProvider"
    extension-element-prefixes="display" >

<xsl:output method="html" />

<xsl:template match="/">
    <xsl:apply-templates select="ProjectXML" />	
</xsl:template>


<xsl:template match="ProjectXML">
	<xsl:apply-templates select="Content" />
</xsl:template>

<xsl:template match="Content">
	<xsl:apply-templates select="ViewList" />
</xsl:template>

<xsl:template match="ViewList">
<table width="100%" border="0" cellpadding="0" cellspacing="0">
    <tr>
        <td class="tableHeader" width="1%"><xsl:text disable-output-escaping="yes" /></td>
        <td class="tableHeader"><xsl:value-of select="display:get('prm.global.view.manage.viewlist.column.name.label')" /></td>
        <td class="tableHeader"><xsl:value-of select="display:get('prm.global.view.manage.viewlist.column.description.label')" /></td>
    </tr>
    <tr class="tableLine">
        <td colspan="3" class="tableLine">
            <img src="../images/spacers/trans.gif" width="1" height="2" border="0" />
        </td>
    </tr>
    <xsl:choose>
        <xsl:when test="count(PersonalPortfolioView)=0">
            <tr>
                <td class="tableContent"><xsl:text disable-output-escaping="yes" /></td>
                <td class="tableContent" colspan="2">
                    <xsl:value-of select="display:get('prm.global.view.manage.viewlist.noviews')" />
                </td>
            </tr>
        </xsl:when>
        <xsl:otherwise>
            <xsl:apply-templates select="PersonalPortfolioView" />
        </xsl:otherwise>
    </xsl:choose>
</table>
</xsl:template>

<xsl:template match="PersonalPortfolioView">
<tr>
    <td class="tableContent">
        <input type="radio" name="viewID" value="{ID}" />
    </td>
    <td class="tableContent">
        <a href="javascript:changeView('{ID}');"><xsl:value-of select="Name" /></a>
    </td>
    <td class="tableContent">
        <xsl:value-of select="Description" />
    </td>
</tr>
    <tr class="tableLine">
        <td colspan="3">
            <img src="../images/spacers/trans.gif" width="1" height="1" border="0" />
        </td>
    </tr>
</xsl:template>

</xsl:stylesheet>