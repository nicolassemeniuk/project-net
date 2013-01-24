<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:display="xalan://net.project.base.property.PropertyProvider" extension-element-prefixes="display" >
<xsl:output method="html"/>

	<xsl:template match="/process">
		<table border="0" align="left"  cellpadding="0" cellspacing="0">
			<tr>
				<td align="left" nowrap="true" width="100" class="tableHeader"><xsl:value-of select="display:get('prm.project.process.name.label')"/></td>
				<td nowrap="true" align="left" class="tableContent">
					<xsl:value-of select="process_name"/>
					<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text> 
				</td>
			</tr>
			<tr align="left" bgcolor="#FFFFCC">
				<td align="left" valign="top" width="100" class="tableHeader"><xsl:value-of select="display:get('prm.project.process.description.label')"/></td>
				<td align="left" class="tableContent">
					<xsl:value-of select="process_desc"/>
					<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text> 
				</td>
			</tr>
		</table>
</xsl:template>
</xsl:stylesheet>