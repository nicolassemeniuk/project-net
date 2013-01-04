<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:display="xalan://net.project.base.property.PropertyProvider"
	xmlns:format="xalan://net.project.util.XSLFormat"
        extension-element-prefixes="display format" >
	
<xsl:output method="html"/>

	<xsl:template match="/gate">
		<table border="0" align="left" width="100%"  cellpadding="0" cellspacing="2">
		<tr>
			<td align="left" nowrap="true" class="tableHeader" width="110"><xsl:value-of select="display:get('prm.project.process.viewphase.gate.name.label')"/></td>
			<td nowrap="true" align="left" class="tableContent"><xsl:value-of select="gate_name"/></td>
		</tr>
		<tr>
			<td align="left" nowrap="true" class="tableHeader"><xsl:value-of select="display:get('prm.project.process.viewphase.gate.status.label')"/></td>
			<td nowrap="true" align="left" class="tableContent"><xsl:value-of select="status"/></td>
		</tr>
		<tr>
			<td align="left" nowrap="true" class="tableHeader"><xsl:value-of select="display:get('prm.project.process.viewphase.gate.reviewdate.label')"/></td>
			<td nowrap="true" align="left" class="tableContent"><xsl:value-of select="format:formatISODate(gate_date)"/>
			</td>
		</tr>
		<tr>
			<td align="left" valign="top" nowrap="true"  class="tableHeader"><xsl:value-of select="display:get('prm.project.process.viewphase.gate.description.label')"/></td>
			<td align="left" valign="top" class="tableContent"><xsl:value-of select="gate_desc"/>
			<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
			</td>
		</tr>
		
		</table>
</xsl:template>
</xsl:stylesheet>