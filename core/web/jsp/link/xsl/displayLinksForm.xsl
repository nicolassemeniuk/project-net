<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
         xmlns:display="xalan://net.project.base.property.PropertyProvider"
         extension-element-prefixes="display" >

<xsl:output method="html"/>
	<xsl:template match="/link_list">
  		<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr class="channelHeader">
		<td class="channelHeader" width="1%"><img src="../images/icons/channelbar-left_end.gif" width="8" height="15" alt="" border="0"/></td>
		<td nowrap="true" class="channelHeader" width="85%" colspan="3"><B><xsl:value-of select="display:get('prm.global.links.modify.channel.currentlinks.title')"/></B></td>
		<td class="channelHeader" width="1%" align="right"><img src="../images/icons/channelbar-right_end.gif" width="8" height="15" alt="" border="0"/></td>

		</tr>
		<tr class="tableHeader" align="center">
		<td align="left" nowrap="true" class="tableHeader"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
		<td align="left" nowrap="true" class="tableHeader"><B><xsl:value-of select="display:get('prm.global.links.modify.name.column')"/></B></td>
		<td align="left" nowrap="true" class="tableHeader"><B><xsl:value-of select="display:get('prm.global.links.modify.type.column')"/></B></td>
		<td align="left" nowrap="true" class="tableHeader"><B><xsl:value-of select="display:get('prm.global.links.modify.direction.column')"/></B></td>	
		<td align="left" nowrap="true" class="tableHeader"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>

		</tr>
		 <tr class="tableLine">
		 <td colspan="5" class="tableLine"><img src="../images/spacers/trans.gif" width="1" height="2" border="0" alt=""/></td>
	         </tr>			
			<xsl:for-each select="linked_object">
			<tr align="center" class="tableContent">
			<td align="left" nowrap="true" class="tableHeader"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
			<td nowrap="true" align="left" class="tableContent">
			<xsl:element name="input">
				<xsl:attribute name="type">radio</xsl:attribute>
				<xsl:attribute name="name">linked_id</xsl:attribute>
				<xsl:attribute name="value"><xsl:value-of select="./object_id"/></xsl:attribute>
			</xsl:element>

                <xsl:variable name="objectNameTruncated">
                    <xsl:choose>
                        <xsl:when test="string-length(object_name) > 40">
                            <xsl:value-of select="display:get('prm.global.textformatter.truncatedstringformat', substring(object_name,1,40))"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:value-of select="object_name"/>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:variable>

                <xsl:choose>
                    <!-- Deleted Links are displayed differently -->
                    <xsl:when test="RecordStatus/ID = 'D'">
                        <font color='#999999'><i><xsl:value-of select="$objectNameTruncated" /></i></font>
                    </xsl:when>
                    <!-- Non-deleted links are clickable -->
                    <xsl:otherwise>
                        <xsl:value-of select="$objectNameTruncated"/>
                    </xsl:otherwise>
                </xsl:choose>
			</td>
			<td nowrap="true" align="left" class="tableContent">
				<xsl:value-of select="./object_type_description"/>
			</td>
			<td nowrap="true" align="left" class="tableContent">
				<xsl:choose>
					<xsl:when test="./link_type='to'">
	            		<xsl:value-of select="display:get('prm.global.linktype.to.text')"/>  <!-- Linked to -->
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="display:get('prm.global.linktype.from.text')"/> <!-- Linked from -->
					</xsl:otherwise>
				</xsl:choose>
				<xsl:element name="input">
					<xsl:attribute name="type">hidden</xsl:attribute>
					<xsl:attribute name="name"><xsl:value-of select="./object_id"/></xsl:attribute>
					<xsl:attribute name="value"><xsl:value-of select="./link_type"/></xsl:attribute>
				</xsl:element>
			</td>
			<td align="left" nowrap="true" class="tableHeader"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>

         		</tr>
         		<tr class="tableLine">
			<td colspan="5" class="tableLine"><img src="../images/spacers/trans.gif" width="1" height="1" border="0" alt="" /></td>
			</tr>
			</xsl:for-each>
		</table>
	</xsl:template>
</xsl:stylesheet>

