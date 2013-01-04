<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="html"/>
	<xsl:template match="/">
  		<table bgcolor="#99cccc" border="0" cellpadding="2" cellspacing="0" width="100%">
		<tbody>
		<tr>
		<td><B>Search Results</B></td>
		</tr>
		<tr>
		<td colspan="2">
		<table border="0" cellpadding="2" cellspacing="1" class="WINDOW" width="100%">
		<tbody>
		<tr bgcolor="#E6E6E6">
		<td><B><xsl:value-of select="results_list/desc_one_label"/></B></td>
		<td><B><xsl:value-of select="results_list/desc_two_label"/></B></td>
		</tr>
			
			<xsl:for-each select="results_list/result">
			<tr bgcolor="#FFFFFF" align="left" valign="middle">
			<td align="left">
			<xsl:element name="A">
				<xsl:attribute name="href"><xsl:value-of select="./href"/></xsl:attribute>
				<xsl:value-of select="./desc_one"/>
				<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
			</xsl:element>
			</td>
			<td align="center">
				<xsl:value-of select="./desc_two"/>
				<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
			</td>
         		</tr>
			</xsl:for-each>
		</tbody>
		</table>
		</td>
		</tr>
		</tbody>
		</table>
	</xsl:template>
</xsl:stylesheet>