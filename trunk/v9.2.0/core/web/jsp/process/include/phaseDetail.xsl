<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:display="xalan://net.project.base.property.PropertyProvider"
	xmlns:format="xalan://net.project.util.XSLFormat"
    extension-element-prefixes="display format" >
	
<xsl:output method="html"/>

	<xsl:template match="/phase">
		<table border="0" align="left" cellpadding="0" cellspacing="2">
			<tr>
				<td align="left" nowrap="true" width="110" class="tableHeader"><xsl:value-of select="display:get('prm.project.process.viewphase.name.label')"/></td>
				<td nowrap="true" align="left" class="tableContent" >
					<!-- bug-3552 fix
					<xsl:choose>
						<xsl:when test="string-length(./phase_name) > 12">
							<label>
								<xsl:attribute name="title"><xsl:value-of select="./phase_name"/></xsl:attribute>
								<xsl:value-of select="display:get('prm.global.textformatter.truncatedstringformat', substring(phase_name,1,12))"/>
				 			</label>
				 		</xsl:when>	
				 		<xsl:otherwise>
	                        <xsl:value-of select="./phase_name"/>
                        </xsl:otherwise>
				 	 </xsl:choose>
				 	 -->
				 	 <xsl:value-of select="./phase_name"/>
				</td>
			</tr>
			<tr>
				<td align="left" nowrap="true" class="tableHeader"><xsl:value-of select="display:get('prm.project.process.viewphase.status.label')"/></td>
				<td nowrap="true" align="left" class="tableContent"><xsl:value-of select="status"/></td>
			</tr>
			<tr>
				<td align="left" nowrap="true" class="tableHeader"><xsl:value-of select="display:get('prm.project.process.viewphase.complete.label')"/></td>
				<td align="left">
				<table border="0" width="104" height="11" cellspacing="0" cellpadding="0">
				<tr>
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
				<td align="left" class="tableContent"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text><xsl:value-of select="format:formatPercent(percent_complete,0,0)"/></td>
				</tr>
				</table>
				</td>
			</tr>
			
			<tr>
				<td align="left" nowrap="true" class="tableHeader"><xsl:value-of select="display:get('prm.project.process.viewphase.startdate.label')"/></td>
				<td nowrap="true" align="left" class="tableContent"><xsl:value-of select="start_date"/></td>
			</tr>
			<tr>
				<td align="left" nowrap="true" class="tableHeader"><xsl:value-of select="display:get('prm.project.process.viewphase.finishdate.label')"/></td>
				<td nowrap="true" align="left" class="tableContent"><xsl:value-of select="end_date"/></td>
			</tr>
			<tr>
				<td align="left" valign="top" class="tableHeader"><xsl:value-of select="display:get('prm.project.process.viewphase.description.label')"/></td>
				<td align="left" class="tableContent"><xsl:value-of select="phase_desc"/>
				<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
				</td>
			</tr>
		</table>
</xsl:template>
</xsl:stylesheet>