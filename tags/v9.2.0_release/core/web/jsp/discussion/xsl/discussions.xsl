<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:display="xalan://net.project.base.property.PropertyProvider"
	xmlns:format="xalan://net.project.util.XSLFormat"
    extension-element-prefixes="display format" >
<xsl:output method="html"/> 

<!-- Build translation properties node list -->
<xsl:variable name="translation" select="/ProjectXML/Properties/Translation/property" />
<xsl:variable name="JSPRootURL" select="$translation[@name='JSPRootURL']" />

<xsl:template match="/"><xsl:apply-templates/></xsl:template>

<xsl:template match="ProjectXML"><xsl:apply-templates select="Content" /></xsl:template>

<xsl:template match="Content"><xsl:apply-templates/></xsl:template>

<xsl:template match="DiscussionGroupsList">
<table width="99%" border="0" cellspacing="0" cellpadding="0">
    <xsl:variable name="spacerWidth">
        <xsl:choose>
            <xsl:when test="$translation[@name='isSelectable']='1'">5</xsl:when>
            <xsl:otherwise>6</xsl:otherwise>
        </xsl:choose>
    </xsl:variable>

	<tr class="tableHeader">
        <xsl:if test="$translation[@name='isSelectable']='1'">
		<th width="2%" class="tableHeader" ><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></th>
        </xsl:if>
        <th class="tableHeader" align="left"><nobr class="tableHeader"><xsl:value-of select="display:get('prm.discussion.main.grouplist.group.column')"/><xsl:text disable-output-escaping="yes">&amp;nbsp;&amp;nbsp;</xsl:text></nobr></th>
        <th class="tableHeader" align="center"><xsl:value-of select="display:get('prm.discussion.main.grouplist.posts.column')"/><xsl:text disable-output-escaping="yes">&amp;nbsp;&amp;nbsp;</xsl:text></th>
        <th class="tableHeader" align="center"><xsl:value-of select="display:get('prm.discussion.main.grouplist.unread.column')"/><xsl:text disable-output-escaping="yes">&amp;nbsp;&amp;nbsp;</xsl:text></th>
        <th class="tableHeader" align="left"><xsl:value-of select="display:get('prm.discussion.main.grouplist.description.column')"/></th>
        <td width="1%" align="right"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
  	</tr>
	<tr class="tableLine">
		<td colspan="{$spacerWidth}" class="tableLine">
			<xsl:element name="img">
				<xsl:attribute name="src">../images/spacers/trans.gif</xsl:attribute>
				<xsl:attribute name="width">1</xsl:attribute>
				<xsl:attribute name="height">2</xsl:attribute>
				<xsl:attribute name="border">0</xsl:attribute>
			</xsl:element>
		</td>
	</tr>
	<xsl:apply-templates select="group" />
</table>
</xsl:template>

<xsl:template match="group">
    <xsl:variable name="spacerWidth">
        <xsl:choose>
            <xsl:when test="$translation[@name='isSelectable']='1'">5</xsl:when>
            <xsl:otherwise>6</xsl:otherwise>
        </xsl:choose>
    </xsl:variable>

	<tr>
        <xsl:if test="$translation[@name='isSelectable']='1'">
            <td class="tableContent" valign="top">
                <xsl:element name="input">
                    <xsl:attribute name="type">radio</xsl:attribute>
                    <xsl:attribute name="name">selected</xsl:attribute>
                    <xsl:attribute name="value"><xsl:value-of select="./group_id"/></xsl:attribute>
                </xsl:element>
            </td>
        </xsl:if>
		<td align="left" valign="top" class="tableContent">
			<xsl:element name="a">
				<xsl:attribute name="href"><xsl:value-of select="$JSPRootURL" />/discussion/GroupView.jsp<xsl:value-of select="./query_string"/></xsl:attribute>
				<xsl:value-of select="./group_name"/>
			</xsl:element>
		</td>
		<td align="center" valign="top" class="tableContent"><xsl:value-of select="format:formatNumber(num_posts)"/><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
		<td align="center" valign="top" class="tableContent"><xsl:value-of select="format:formatNumber(unread_posts)"/><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
		<td valign="top" class="tableContent"><xsl:value-of select="./description"/><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
         </tr>
    	<tr class="tableLine">
    		<td colspan="{$spacerWidth}" class="tableLine"><img src="{$JSPRootURL}/images/spacers/trans.gif" width="1" height="1" border="0" alt=""/></td>
	</tr>
</xsl:template>

</xsl:stylesheet>
