<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:display="xalan://net.project.base.property.PropertyProvider"
    extension-element-prefixes="display" >

<xsl:output method="html"/>

<!-- Build translation properties node list -->
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
    <xsl:apply-templates select="SearchResults" />
</xsl:template>

<xsl:template match="SearchResults">
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr class="tableHeader">
			<td class="tableHeader"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
			<td class="tableHeader" align="left"><xsl:value-of select="display:get('prm.directory.invite.directorysearchresults.column.name')"/></td>
			<td class="tableHeader" align="left"><xsl:value-of select="display:get('prm.directory.invite.directorysearchresults.column.emailaddress')"/></td>
		</tr>
		<tr class="tableLine">
			<td colspan="3" class="tableLine">
				<img src="../images/spacers/trans.gif" width="1" height="2" border="0" />
			</td>
		</tr>
		<xsl:choose>
			<xsl:when test="SearchResult">
				<xsl:apply-templates select="SearchResult" />	
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="noResults" />
			</xsl:otherwise>
		</xsl:choose>
	</table>
</xsl:template>
	
<xsl:template match="SearchResult">
	<tr class="tableContent">
		<td class="tableContent">
			<input type="checkBox" name="resultID" value="{ID}" />
		</td>
		<td class="tableContent">
			<xsl:value-of select="LastName" />, <xsl:value-of select="FirstName" />
		</td>
		<td class="tableContent">
			<xsl:value-of select="Email" />
		</td>
	</tr>
	<tr class="tableLine">
		<td colspan="3">
			<img src="../images/spacers/trans.gif" width="1" height="1" border="0" />
		</td>
	</tr>
</xsl:template>

<xsl:template name="noResults">
	<tr class="tableContent">
		<td class="tableContent" colspan="3">
			<xsl:value-of select="display:get('prm.directory.invite.directorysearchresults.nomatch.message')"/>
		</td>
	</tr>
</xsl:template>
</xsl:stylesheet>
