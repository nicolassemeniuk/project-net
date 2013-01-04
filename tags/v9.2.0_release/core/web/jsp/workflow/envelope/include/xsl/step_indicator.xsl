<?xml version="1.0"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:display="xalan://net.project.base.property.PropertyProvider"
		xmlns:format="xalan://net.project.util.XSLFormat"
        extension-element-prefixes="display format" >

<xsl:template match="*|/"><xsl:apply-templates/></xsl:template>

<xsl:template match="text()|@*"><xsl:value-of select="."/></xsl:template>

<xsl:template match="step_list">
<table border="1" bordercolor="#FFFFFF" cellpadding="0" cellspacing="0">
	<tr>
		<xsl:apply-templates select="step" mode="header" />
	</tr>
	<tr bordercolor="#FFEAAA">
	<xsl:apply-templates select="step" />
	</tr>
</table>
</xsl:template>

<xsl:template match="step" mode="header">

<xsl:choose>
	<xsl:when test="step_id=../selected_step_id">
		<td class="tableContentHighlight" valign="top" bordercolor="#FFEAAA" align="center" nowrap="nowrap">
			<span class="tableContentFontOnly">
				<xsl:value-of select="display:get('prm.workflow.envelope.include.stepindicator.currentstep.label')"/>
			</span>
		</td>
	</xsl:when>
	<xsl:otherwise>
		<td class="tableContent" valign="top" nowrap="nowrap">
			<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
		</td>
	</xsl:otherwise>
</xsl:choose>

</xsl:template>

<xsl:template match="step">

<xsl:choose>
	<xsl:when test="step_id=../selected_step_id">
		<td class="tableContentHighlight" valign="top" nowrap="nowrap">
			<span class="tableContentFontOnly">
				<xsl:text disable-output-escaping="yes">&amp;nbsp;&amp;nbsp;&amp;nbsp;</xsl:text>
				<xsl:value-of select="name" /><br />
				<xsl:apply-templates select="transition_list/transition">
					<xsl:with-param name="makeLink" select="boolean(1)" />
				</xsl:apply-templates>
			</span>
		</td>
	</xsl:when>
	<xsl:otherwise>
		<td class="tableContent" valign="top" nowrap="nowrap">
			<xsl:text disable-output-escaping="yes">&amp;nbsp;&amp;nbsp;&amp;nbsp;</xsl:text>
			<xsl:value-of select="name" /><br />
			<xsl:apply-templates select="transition_list/transition">
				<xsl:with-param name="makeLink" select="boolean(0)" />
			</xsl:apply-templates>
		</td>
	</xsl:otherwise>
</xsl:choose>

</xsl:template>

<xsl:template match="transition">
	<xsl:param name="makeLink" select="boolean(false)" />

<xsl:if test="position()!=1"><br /></xsl:if>
<xsl:text disable-output-escaping="yes">&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;</xsl:text>
	<xsl:choose>
		<xsl:when test="$makeLink">
			<a href="javascript:performTransition('{./transition_id}')"><xsl:value-of select="./transition_verb" /></a>
		</xsl:when>
		<xsl:otherwise>
			<xsl:value-of select="./transition_verb" />
		</xsl:otherwise>
	</xsl:choose>

</xsl:template>

</xsl:stylesheet>
