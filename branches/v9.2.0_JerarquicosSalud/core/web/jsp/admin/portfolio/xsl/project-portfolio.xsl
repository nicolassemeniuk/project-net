<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	 xmlns:display="xalan://net.project.base.property.PropertyProvider"
     xmlns:format="xalan://net.project.util.XSLFormat"
     extension-element-prefixes="display format" >
	
  <xsl:template match="ProjectPortfolio">
  	<table cellpadding="0" cellspacing="0" border="0" width="100%">
		<tr class="tableHeader" align="left" valign="top">
		<td class="tableHeader" width="3%" >
                <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
            </td>
			<td class="tableHeader" width="25%">Project</td>
			<td class="tableHeader" width="25%">Business</td>
			<td colspan="2" class="tableHeader" width="20%">Status</td>
			<td colspan="2" class="tableHeader">Completion Percentage</td>
		</tr>
		<tr class="tableLine">

			<td colspan="6" class="tableLine">
				<xsl:element name="img">
					<xsl:attribute name="src">../../images/spacers/trans.gif</xsl:attribute>
					<xsl:attribute name="width">1</xsl:attribute>
					<xsl:attribute name="height">2</xsl:attribute>
					<xsl:attribute name="border">0</xsl:attribute>
				</xsl:element>
			</td>
		</tr>
			<xsl:apply-templates select="ProjectSpace"/>
			<xsl:if test="count(ProjectSpace)=0">
            	<xsl:call-template name="NoProjects"/>
        	</xsl:if>
	</table>
	</xsl:template>
	
	<xsl:template match="ProjectSpace">
		<tr align="left" valign="middle" class="tableContent">
		 <td class="tableContent"><input name="selected" type="Checkbox" value="{project_id}"/></td>
			<td class="tableContent">
				<xsl:element name="a">
					<xsl:attribute name="href">../../project/Dashboard?id=<xsl:value-of select="./project_id"/></xsl:attribute>
					<xsl:value-of select="./name"/>
				</xsl:element>
			</td>
            <td class="tableContent"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text><xsl:value-of select="./ParentBusinessName"/></td>
			<td class="tableContent">
				<xsl:element name="img">
					<xsl:attribute name="src">../../<xsl:value-of select="./color_code_image_url"/></xsl:attribute>
					<xsl:attribute name="width">12</xsl:attribute>
					<xsl:attribute name="height">12</xsl:attribute>
				</xsl:element>
			</td>
			<td class="tableContent"><xsl:value-of select="./status_code"/></td>
			<td class="tableContent" NOWRAP="1" width="110">
				<table border="1" width="100" height="8" cellspacing="0" cellpadding="0">
				<tr>
				<td bgcolor="#FFFFFF">
					<xsl:element name="img">
						<xsl:attribute name="src">../../images/lgreen.gif</xsl:attribute>
						<xsl:attribute name="width"><xsl:value-of select="./percent_complete"/></xsl:attribute>
						<xsl:attribute name="height">8</xsl:attribute>
					</xsl:element>
				</td>
				</tr>
				</table>
			</td>
			<td class="tableContent" align="left" ><xsl:value-of select="format:formatPercent(percent_complete)"/></td>
         </tr>
	<tr class="tableLine">
		<td colspan="8">
			<xsl:element name="img">
				<xsl:attribute name="src">../../images/spacers/trans.gif</xsl:attribute>
				<xsl:attribute name="width">1</xsl:attribute>
				<xsl:attribute name="height">1</xsl:attribute>
				<xsl:attribute name="border">0</xsl:attribute>
			</xsl:element>
		</td>
	</tr>
	</xsl:template>
	 <xsl:template name="NoProjects">
        <tr class="tableContent" align="left">
            <td class="tableContent" colspan="8"> No Such Project found</td>
        </tr>
    </xsl:template>
</xsl:stylesheet>
