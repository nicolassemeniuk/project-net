<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
         xmlns:display="xalan://net.project.base.property.PropertyProvider"
         extension-element-prefixes="display" >

<xsl:output method="html"/>
	<xsl:template match="/link_list">
		<table border="0" align="left" width="100%"  cellpadding="0" cellspacing="0">
		<tr class="tableHeader" align="center">
		<td align="left" nowrap="true" class="tableHeader"><xsl:value-of select="display:get('prm.global.links.direction.column')"/></td>
		<td align="left" nowrap="true" class="tableHeader"><xsl:value-of select="display:get('prm.global.links.name.column')"/></td>
		<td align="left" nowrap="true" class="tableHeader"><xsl:value-of select="display:get('prm.global.links.type.column')"/></td>
		</tr>
		<tr class="tableLine">
		      <td colspan="100" class="tableLine"><img src="../images/spacers/trans.gif" width="1" height="2" border="0" alt=""/></td>
		</tr>

    	<xsl:for-each select="linked_object">
			<tr align="center" class="tableContent">
			<td  nowrap="true" align="left" class="tableContent">

			<xsl:choose>

				<xsl:when test="./link_type='to'">
            		<xsl:value-of select="display:get('prm.global.linktype.to.text')"/> <!-- Linked to  -->
				</xsl:when>

				<xsl:otherwise>
					<xsl:value-of select="display:get('prm.global.linktype.from.text')"/> <!-- Linked from -->
				</xsl:otherwise>

			</xsl:choose>

			</td>
			<td  nowrap="true" align="left" class="tableContent">



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

                    <!-- Deleted Links are not clickable; they have no href -->
                    <xsl:when test="RecordStatus/ID = 'D'">
                        <font color='#999999'><i><xsl:value-of select="$objectNameTruncated" /></i></font>
                    </xsl:when>

                    <!-- Non-deleted links are clickable -->
                    <xsl:otherwise>
                    	<!-- bfd-4479 Linked Posts cannot be opened from Deliverables page -->
                        <xsl:choose>
                            <xsl:when test="./object_type = 'bookmark'">                                
                                <xsl:element name="a">
                                	<xsl:attribute name="target">view_window</xsl:attribute>
                                	<xsl:attribute name="href"><xsl:value-of select="./href" disable-output-escaping="yes" /></xsl:attribute>
                                	<xsl:value-of select="$objectNameTruncated"/>
                                </xsl:element>
                            </xsl:when>
                            <xsl:when test="./object_type='document' or 
                            				./object_type='deliverable' or 
                            				./object_type='post' or 
                            				./object_type='task' or 
                            				./object_type='event' or 
                            				./object_type='form_data' or 
                            				./object_type='form_list'">                                
                                <xsl:element name="a">
                                	<xsl:attribute name="target">_top</xsl:attribute>
                                	<xsl:attribute name="href"><xsl:value-of select="./href" disable-output-escaping="yes" /></xsl:attribute>
                                	<xsl:value-of select="$objectNameTruncated"/>
                                </xsl:element>
                            </xsl:when>
                            <xsl:otherwise>                                
                                <xsl:element name="a">
                                	<xsl:attribute name="target">main</xsl:attribute>
                                	<xsl:attribute name="href"><xsl:value-of select="./href" disable-output-escaping="yes" /></xsl:attribute>
                                	<xsl:value-of select="$objectNameTruncated"/>
                                </xsl:element>
                            </xsl:otherwise>
                        </xsl:choose>
                    </xsl:otherwise>
 
                </xsl:choose>

			</td>
			<td  nowrap="true" align="left" class="tableContent">
			<xsl:value-of select="./object_type_description"/>
			</td>
         		</tr>
			<tr class="tableLine">
			<td  colspan="100" class="tableLine"><img src="../images/spacers/trans.gif" width="1" height="1" border="0" alt=""/></td>
			</tr>
		</xsl:for-each>

        </table>
	</xsl:template>
</xsl:stylesheet>
