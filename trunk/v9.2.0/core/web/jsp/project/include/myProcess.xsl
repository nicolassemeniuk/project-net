<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:display="xalan://net.project.base.property.PropertyProvider"
	xmlns:format="xalan://net.project.util.XSLFormat"
        extension-element-prefixes="display format" >
				
<xsl:output method="html"/>

	<xsl:template match="/PhaseList">
		<table border="0" align="left" width="100%"  cellpadding="0" cellspacing="0">
		<tr class="tableHeader" align="center">
			<td align="left" nowrap="true" class="tableHeader"><xsl:value-of select="display:get('prm.project.main.name.label')"/></td>
			<td align="left" nowrap="true" class="tableHeader"><xsl:value-of select="display:get('prm.project.main.startdate.label')"/></td>
			<td align="left" nowrap="true" class="tableHeader"><xsl:value-of select="display:get('prm.project.main.enddate.label')"/></td>
			<td align="left" nowrap="true" class="tableHeader"><xsl:value-of select="display:get('prm.project.main.gatedate.label')"/></td>
			<td align="left" nowrap="true" class="tableHeader"><xsl:value-of select="display:get('prm.project.main.status.label')"/></td>
			<td colspan="2" align="left" nowrap="true" class="tableHeader"><xsl:value-of select="display:get('prm.project.main.progress.label')"/></td>
		</tr>
		<tr class="tableLine">
		      <td colspan="100" class="tableLine"><img src="../images/spacers/trans.gif" width="1" height="2" border="0" alt=""/></td>
		</tr>
		<xsl:for-each select="phase">
		<tr  align="center" class="tableContent">
		<td  nowrap="true" align="left" class="tableContent">
		<xsl:element name="A">
			<xsl:attribute name="href">javascript:viewPhase(<xsl:value-of select="./phase_id"/>);</xsl:attribute>
			<xsl:value-of select="./phase_name"/>
		</xsl:element>
		<xsl:text disable-output-escaping="yes">&amp;nbsp;&amp;nbsp;</xsl:text>
		</td>
		<td nowrap="true" align="left" class="tableContent"><xsl:value-of select="format:formatISODate(start_date)"/><xsl:text disable-output-escaping="yes">&amp;nbsp;&amp;nbsp;</xsl:text></td>
		<td nowrap="true" align="left" class="tableContent"><xsl:value-of select="format:formatISODate(end_date)"/><xsl:text disable-output-escaping="yes">&amp;nbsp;&amp;nbsp;</xsl:text></td>
		<td nowrap="true" align="left" class="tableContent"><xsl:value-of select="format:formatISODate(gate/gate_date)"/><xsl:text disable-output-escaping="yes">&amp;nbsp;&amp;nbsp;</xsl:text></td>
		<td nowrap="true" align="left" class="tableContent"><xsl:value-of select="status"/><xsl:text disable-output-escaping="yes">&amp;nbsp;&amp;nbsp;</xsl:text></td>
		<td nowrap="true" align="left" class="tableContent"><xsl:value-of select="format:formatPercent(percent_complete, 0, 0)"/><xsl:text disable-output-escaping="yes">&amp;nbsp;&amp;nbsp;</xsl:text></td>
		<td nowrap="true" align="left" class="tableContent">
		<table border="1" width="104" height="11" cellspacing="0" cellpadding="0">
			<tr>
				<td bgcolor="#FFFFFF">

                	<xsl:variable name="percentageWidth">
					<xsl:choose>
                    	<!-- A zero width box will render 1 pixel wide on IE and 8 pixels wide on
                             Netscape.  Let's make all of them display one pixel. -->
                        <xsl:when test="percent_complete = 0">1</xsl:when>
                        <xsl:when test="percent_complete = ''">1</xsl:when>
                        <xsl:otherwise><xsl:value-of select="format:formatPercent(percent_complete,0,0)"/></xsl:otherwise>
					</xsl:choose>
				</xsl:variable>
				<img src="../images/lgreen.gif" width="{$percentageWidth}" height="8" />
				</td>
			</tr>
		</table>
		</td>
		</tr>
		<tr class="tableLine">
		<td  colspan="100" class="tableLine"><img src="../images/spacers/trans.gif" width="1" height="1" border="0" alt=""/></td>
		</tr>
		</xsl:for-each>
		</table>
	</xsl:template>
</xsl:stylesheet>
