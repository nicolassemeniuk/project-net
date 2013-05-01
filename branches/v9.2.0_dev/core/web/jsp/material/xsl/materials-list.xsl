<?xml version="1.0" ?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:display="xalan://net.project.base.property.PropertyProvider"
	xmlns:format="xalan://net.project.util.XSLFormat" extension-element-prefixes="display format">
	<xsl:output method="html" indent="yes" />

	<xsl:template match="materials-list">
		<table border="0" cellpadding="0" cellspacing="0" width="100%">
			<tr align="left" class="tableHeader">
				<td class="tableHeader" width="1%">
					<xsl:text disable-output-escaping="yes"></xsl:text>
				</td>
				<td class="tableHeader" colspan="2"><xsl:value-of select="display:get('prm.material.main.list.name')"/></td>
				<td class="tableHeader"><xsl:value-of select="display:get('prm.material.main.list.type')"/></td>
				<td class="tableHeader"><xsl:value-of select="display:get('prm.material.main.list.cost')"/></td>
				<td class="tableHeader" align="center" ><xsl:value-of select="display:get('prm.material.main.list.consumable')"/></td>				
				<td class="tableHeader"><xsl:value-of select="display:get('prm.material.main.list.description')"/></td>				
			</tr>
			<tr class="tableLine">
				<td colspan="7" class="tableLine">
					<img src="../images/spacers/trans.gif" width="1" height="2" border="0" />
				</td>
			</tr>
			<xsl:apply-templates select="material">
			</xsl:apply-templates>			
		</table>
	</xsl:template>
	
	<xsl:template match="material" >
		<tr align="left" valign="middle" class="tableContent">
			<!-- Radio Option -->
			<td class="tableContent">
			<input type="radio" name="selected" value="{id}" id="{name}" />			
			</td>
			<td colspan="2" align="left">
			<a href="../material/MaterialDetail.jsp?module=260&amp;id={id}" >
			<xsl:value-of select="name" disable-output-escaping="yes"/>
			</a>			
			</td>
			<td class="tableContent" align="left">
				<xsl:value-of select="type" />
			</td>
			<td class="tableContent" align="left">
				<xsl:value-of select="cost" />
			</td>
			<td class="tableContent" align="center">
				<xsl:choose>
					<xsl:when test="consumable = 'true'">
						<img src="../images/check_green.gif" />
					</xsl:when>
					<xsl:otherwise>
						<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
					</xsl:otherwise>
				</xsl:choose>				
			</td>
			<td class="tableContent" align="left">
				<xsl:value-of select="description" />
			</td>
		</tr>
		<tr class="tableLine">
			<td colspan="7">
				<img src="../images/spacers/trans.gif" width="1" height="1" border="0" />
			</td>
		</tr>
	</xsl:template>
</xsl:stylesheet>