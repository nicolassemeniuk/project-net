<?xml version="1.0"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:display="xalan://net.project.base.property.PropertyProvider" extension-element-prefixes="display" >
<xsl:output method="html"/>

<xsl:template match="*|/"><xsl:apply-templates/></xsl:template>

<xsl:template match="text()|@*"><xsl:value-of select="."/></xsl:template>

<xsl:template match="step">
<xsl:element name="option">
  <xsl:attribute name="value">
  	<xsl:value-of select="step_id" />
  </xsl:attribute>
  <xsl:apply-templates select="name" />
</xsl:element>
</xsl:template>

<xsl:template match="name"><xsl:apply-templates/></xsl:template>

<xsl:template match="step_list">
<xsl:apply-templates/>
<xsl:if test="not(step)">
	<option value=""><xsl:value-of select="display:get('prm.workflow.transitioncreate.steplist.option.nosteps.name')"/></option>
</xsl:if>
</xsl:template>

</xsl:stylesheet>
