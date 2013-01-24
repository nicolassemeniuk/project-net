<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:template match="*|/"><xsl:apply-templates/></xsl:template>

<xsl:template match="text()|@*"><xsl:value-of select="."/></xsl:template>

<xsl:variable name="defaultBrand" select="2000" />

<xsl:template match="brand_glossary"><xsl:variable name="brands" select='//element[@name="brandID"]' />
<xsl:variable name="defaultElements" select="//element[@brandID=$defaultBrand]" />
<form>
<table border="1">

	<xsl:call-template name="displayHeaders" />

	<xsl:for-each select="$defaultElements">

		<tr>
			<td><xsl:value-of select="@name" /></td>
			<td><xsl:value-of select="@value" /></td>
			<xsl:call-template name="getTokenValues">
				<xsl:with-param name="token" select="@name" />
			</xsl:call-template>
		</tr>


	</xsl:for-each>

</table>
</form>

	</xsl:template>

<xsl:template match="element"><xsl:apply-templates/></xsl:template>

<xsl:template match="getTokenValues" name="getTokenValues"><xsl:param name="token" />

<xsl:variable name="tokenValues" select="//element[@name=$token]" />

<xsl:choose>
	<xsl:when test="$tokenValues[@brandID != $defaultBrand]">
		<xsl:for-each select="$tokenValues">
			<xsl:if test="@brandID != $defaultBrand">
				<td><input type="text" width="60" maxlength="250" value="{.}" /></td>
			</xsl:if>
		</xsl:for-each>
	</xsl:when>
	<xsl:otherwise>
		<td><input type="text" width="60" maxlength="250" /></td>
	</xsl:otherwise>
</xsl:choose></xsl:template>

<xsl:template match="displayHeaders" name="displayHeaders"><xsl:variable name="brands" select="//element[@name='brandID']" />

<tr>
	<td>Token</td>
	<td>Project.net Default</td>
	<xsl:for-each select="$brands">
		<xsl:variable name="id" select="@brandID" />
		<xsl:if test="@brandID != $defaultBrand">
			<xsl:variable name="names" select="//element[@name='prm.global.brand.name']" />
			<xsl:variable name="brandName" select="$names[@brandID=$id]" />
			<td><xsl:value-of select="$brandName" /> (<xsl:value-of select="@language" />)</td>
		</xsl:if>
	</xsl:for-each>
</tr>
<tr>
</tr></xsl:template>

</xsl:stylesheet>
