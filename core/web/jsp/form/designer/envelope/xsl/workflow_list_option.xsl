<?xml version="1.0"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:display="xalan://net.project.base.property.PropertyProvider" extension-element-prefixes="display" >

<xsl:output method="html"/>

<xsl:template match="*|/"><xsl:apply-templates/></xsl:template>

<xsl:template match="text()|@*"><xsl:value-of select="."/></xsl:template>

<xsl:template match="workflow">
<option value="{workflow_id}"><xsl:apply-templates select="name" /></option>
</xsl:template>

<xsl:template match="name"><xsl:apply-templates/></xsl:template>

<xsl:template match="workflow_list">
<xsl:apply-templates select="workflow" />
<xsl:if test="not(workflow)">
	<option value=""><xsl:value-of select="display:get('prm.workflow.envelope.wizardpage1.option.noworkflow.name')"/></option>
</xsl:if>
</xsl:template>

</xsl:stylesheet>
