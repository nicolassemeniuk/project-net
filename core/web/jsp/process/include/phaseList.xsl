<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:display="xalan://net.project.base.property.PropertyProvider"
    xmlns:format="xalan://net.project.util.XSLFormat"
    extension-element-prefixes="display format" >
	
<xsl:output method="html"/>

	<xsl:template match="/PhaseList">
		<table border="0" align="left" width="100%"  cellpadding="0" cellspacing="0">
		<tr class="tableHeader" align="center">
			<td align="left" nowrap="true" class="tableHeader"><xsl:value-of select="display:get('prm.project.process.phaselist.name.column')"/></td>
			<td align="left" nowrap="true" class="tableHeader"><xsl:value-of select="display:get('prm.project.process.phaselist.startdate.column')"/></td>
			<td align="left" nowrap="true" class="tableHeader"><xsl:value-of select="display:get('prm.project.process.phaselist.enddate.column')"/></td>
			<td align="left" nowrap="true" class="tableHeader"><xsl:value-of select="display:get('prm.project.process.phaselist.gatedate.column')"/></td>
			<td align="left" nowrap="true" class="tableHeader"><xsl:value-of select="display:get('prm.project.process.phaselist.status.column')"/></td>
			<td colspan="2" align="left" nowrap="true" class="tableHeader"><xsl:value-of select="display:get('prm.project.process.phaselist.progress.column')"/></td>
		</tr>
		<tr class="tableLine">
		      <td colspan="100" class="tableLine"><img src="{jsp_root_url}/images/spacers/trans.gif" width="1" height="2" border="0" alt=""/></td>
		</tr>
		<xsl:for-each select="phase">
		<tr  align="center" class="tableContent">
		<td  nowrap="true" align="left" class="tableContent">
		<xsl:element name="input">
					<xsl:attribute name="type">radio</xsl:attribute>
					<xsl:attribute name="name">selected</xsl:attribute>
					<xsl:attribute name="value"><xsl:value-of select="./phase_id"/></xsl:attribute>
		</xsl:element>
		<xsl:element name="A">
			<xsl:attribute name="href">javascript:viewPhase(<xsl:value-of select="./phase_id"/>);</xsl:attribute>
			<xsl:value-of select="./phase_name"/>
		</xsl:element>
		<xsl:text disable-output-escaping="yes">&amp;nbsp;&amp;nbsp;</xsl:text>
		</td>
		<td nowrap="true" align="left" class="tableContent"><xsl:value-of select="start_date"/><xsl:text disable-output-escaping="yes">&amp;nbsp;&amp;nbsp;</xsl:text></td>
		<td nowrap="true" align="left" class="tableContent"><xsl:value-of select="end_date"/><xsl:text disable-output-escaping="yes">&amp;nbsp;&amp;nbsp;</xsl:text></td>
		<td nowrap="true" align="left" class="tableContent"><xsl:value-of select="gate/gate_date"/><xsl:text disable-output-escaping="yes">&amp;nbsp;&amp;nbsp;</xsl:text></td>
		<td nowrap="true" align="left" class="tableContent"><xsl:value-of select="status"/><xsl:text disable-output-escaping="yes">&amp;nbsp;&amp;nbsp;</xsl:text></td>
		<td nowrap="true" align="left" class="tableContent"><xsl:value-of select="format:formatPercent(percent_complete,0,0)"/><xsl:text disable-output-escaping="yes">&amp;nbsp;&amp;nbsp;</xsl:text></td>
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
				<img src="{jsp_root_url}/images/lgreen.gif" width="{$percentageWidth}" height="8" />
				</td>
			</tr>
		</table>
		</td>
		<td>
			<xsl:choose>
                <xsl:when test="./activeEnvelopeId > 0">
                    <img src="{jsp_root_url}/images/document/workflow-button.gif" height="18" width="19" border="0" alt=""/><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
                </xsl:when>
                <xsl:otherwise>
                    <img src="{jsp_root_url}/images/spacers/trans.gif" height="18" width="19" border="0"/><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
                </xsl:otherwise>
            </xsl:choose>
        </td>
		</tr>
		<tr class="tableLine">
		<td  colspan="100" class="tableLine"><img src="{jsp_root_url}/images/spacers/trans.gif" width="1" height="1" border="0" alt=""/></td>
		</tr>
		</xsl:for-each>
		</table>
	</xsl:template>
</xsl:stylesheet>