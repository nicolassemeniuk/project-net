<?xml version="1.0" ?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:display="xalan://net.project.base.property.PropertyProvider"
	xmlns:format="xalan://net.project.util.XSLFormat" extension-element-prefixes="display format">

	<xsl:output method="html" indent="yes" />

	<xsl:variable name="maxDepth" select="//materials-list/maxdepth" />
	<xsl:variable name="numCols" select="number($maxDepth + 1 + 5)" />

	<xsl:template match="materials-list">
		<xsl:variable name="colspan" select="number($maxDepth + 1)" />
		<table border="0" cellpadding="0" cellspacing="0" width="100%">
			<tr align="left" class="tableHeader">
				<td class="tableHeader" width="1%">
					<xsl:text disable-output-escaping="yes"></xsl:text>
				</td>
				<td class="tableHeader" colspan="{$colspan}"><xsl:value-of select="display:get('prm.material.main.list.name')"/></td>
				<td class="tableHeader"><xsl:value-of select="display:get('prm.material.main.list.description')"/></td>
				<td class="tableHeader"><xsl:value-of select="display:get('prm.material.main.list.type')"/></td>
				<td class="tableHeader"><xsl:value-of select="display:get('prm.material.main.list.cost')"/></td>
				<td class="tableHeader">Consumable</td>				
			</tr>
			<tr class="tableLine">
				<td colspan="{$numCols}" class="tableLine">
					<img src="../images/spacers/trans.gif" width="1" height="2" border="0" />
				</td>
			</tr>
			<xsl:apply-templates select="node">
				<xsl:with-param name="level">
					0
				</xsl:with-param>
			</xsl:apply-templates>
		</table>
	</xsl:template>

	<xsl:template match="node">
		<xsl:param name="level" />
		<xsl:apply-templates select="value">
			<xsl:with-param name="level" select="$level" />
		</xsl:apply-templates>
		<xsl:apply-templates select="children">
			<xsl:with-param name="level" select="number($level + 1)" />
		</xsl:apply-templates>
	</xsl:template>

	<xsl:template match="value">
		<xsl:param name="level" />
		<xsl:apply-templates select="materialspace">
			<xsl:with-param name="level" select="$level" />
		</xsl:apply-templates>
	</xsl:template>

	<xsl:template match="children">
		<xsl:param name="level" />
		<xsl:apply-templates select="Node">
			<xsl:with-param name="level" select="$level" />
		</xsl:apply-templates>
	</xsl:template>

	<xsl:template match="materialspace" >
		<xsl:param name="level" />
		<xsl:variable name="colspan" select="number($maxDepth - $level + 1)" />

		<tr align="left" valign="middle" class="tableContent">
			<!-- Radio Option -->
			<td class="tableContent">
				<xsl:choose>
					<xsl:when test="../isowner = 1">
						<input type="radio" name="selected" value="{materialid}" id="{name}" />
					</xsl:when>
					<xsl:otherwise>
						<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
			</td>
			<!-- Insert appropriate number of padding columns -->
			<xsl:call-template name="indent">
				<xsl:with-param name="count" select="$level" />
			</xsl:call-template>
			<td colspan="{$colspan}" align="left">
				<xsl:choose >
					<xsl:when test="../isowner = 1">
						<a href="../material/MaterialDetail.jsp?module=260&amp;id={materialid}" >
							<xsl:value-of select="materialname" disable-output-escaping="yes"/>
						</a>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="materialname" />
					</xsl:otherwise>
				</xsl:choose>
			</td>
			<td class="tableContent" align="left">
				<xsl:value-of select="materialdescription" />
			</td>
			<td class="tableContent" align="left">
				<xsl:value-of select="materialtype" />
			</td>
			<td class="tableContent" align="left">
				<xsl:value-of select="materialcost" />
			</td>
			<td class="tableContent" align="left">
				<!-- <xsl:value-of select="materialconsumable" /> -->
				<xsl:choose>
					<xsl:when test="materialconsumable = 'true'">
						<img src="../images/check_green.gif" />
					</xsl:when>
					<xsl:otherwise>
						<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
					</xsl:otherwise>
				</xsl:choose>				
				
			</td>			
<!-- 			<td class="tableContent" align="center"> -->
<!-- 				<a href="javascript:showResourceAllocation(<xsl:value-of select="materialname />, currentdate)"> -->
<!-- 					<img src="../images/schedule/constraint.gif" border="0"/> -->
<!-- 				</a> -->
<!-- 			</td> -->
			
		</tr>
		<tr class="tableLine">
			<td colspan="{$numCols}">
				<img src="../images/spacers/trans.gif" width="1" height="1" border="0" />
			</td>
		</tr>
	</xsl:template>

	<xsl:template name="indent">
		<xsl:param name="count" />

		<xsl:if test="$count > 0">
			<xsl:choose>
				<xsl:when test="$count = 1">
					<td class="tableContent" align="left" width="2%">
						<img src="../images/ReplyArrow.gif" />
					</td>
				</xsl:when>
				<xsl:otherwise>
					<td class="tableContent" align="left" width="2%">
						<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
					</td>
				</xsl:otherwise>
			</xsl:choose>
			<xsl:call-template name="indent">
				<xsl:with-param name="count" select="number($count - 1)" />
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

</xsl:stylesheet>