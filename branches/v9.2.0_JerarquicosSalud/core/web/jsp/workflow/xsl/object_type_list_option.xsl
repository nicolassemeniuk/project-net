<?xml version="1.0"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	    xmlns:display="xalan://net.project.base.property.PropertyProvider"
		xmlns:java="http://xml.apache.org/xslt/java" 
		extension-element-prefixes="java" exclude-result-prefixes="java">

<xsl:output method="html"/>

<xsl:template match="*|/"><xsl:apply-templates/></xsl:template>

<xsl:template match="text()|@*"><xsl:value-of select="."/></xsl:template>

<xsl:template match="object_type">
<xsl:choose>
	<xsl:when test="sub_type">
		<xsl:apply-templates select="sub_type"/>
	</xsl:when>
	<xsl:otherwise>
	<option value="{name}:">
		<xsl:value-of select="display:get('prm.workflow.edit.objecttypelistoption.option.any.name' ,	string(description))"/>
</option>
		<!-- <option value="{name}:"><xsl:value-of select="concat(description, ' : ', 'Any')" /> -->
	</xsl:otherwise>
</xsl:choose>
</xsl:template>

<xsl:template match="object_type_list">
<!-- All types of objects -->
<option value=":"><xsl:value-of select="display:get('prm.workflow.edit.objecttypelistoption.option.all.name')"/>
<xsl:apply-templates select="object_type" /></option>
</xsl:template>

<xsl:template match="sub_type">
<option value="{../name}:{sub_type_id}"><xsl:value-of select="concat(../description, ' : ', description)" /></option></xsl:template>

</xsl:stylesheet>
