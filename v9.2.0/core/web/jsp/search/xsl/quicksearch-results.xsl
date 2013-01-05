<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:display="xalan://net.project.base.property.PropertyProvider"
        extension-element-prefixes="display" >

<xsl:output method="html" />

<xsl:template match="*|/"><xsl:apply-templates/></xsl:template>

<xsl:template match="text()|@*"><xsl:value-of select="."/></xsl:template>

<xsl:template match="/"><xsl:apply-templates select="SearchResults" />
  </xsl:template>

<xsl:template match="channel/table_def"><tr class="tableHeader" align="center"> 
	<xsl:for-each select="col">
		<td align="left" nowrap="true" class="tableHeader">
			<xsl:value-of select="." />
		</td>
	</xsl:for-each>
</tr>
<tr class="tableLine">
	<td colspan="100" class="tableLine"><img src="../images/spacers/trans.gif" width="1" height="2" border="0" alt="" /></td>
</tr>
  </xsl:template>

<xsl:template match="channel/content/row">
 <tr align="center" class="tableContent">
	<td align="left" class="tableContent"><xsl:apply-templates select="data_href" /></td>
	<xsl:for-each select="data">
		<td align="left" class="tableContent">
			<xsl:apply-templates select="data" />
			<xsl:value-of select="." />
		</td>
	</xsl:for-each>
 </tr>
 <tr class="tableLine">
 	<td colspan="100" class="tableLine"><img src="../images/spacers/trans.gif" width="1" height="1" border="0" alt="" /></td>
 </tr>
  </xsl:template>

<xsl:template match="channel/content/row/data"><xsl:if test=".=''">
	<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
</xsl:if>
<xsl:apply-templates/>
  </xsl:template>

<xsl:template match="channel/content/row/data_href">
    <xsl:variable name="labelTruncated">
        <xsl:choose>
            <xsl:when test="string-length(label) > 40">
                <xsl:value-of select="display:get('prm.global.textformatter.truncatedstringformat', substring(label,1,40))"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="label"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:variable>

    <a href="{./href}"><xsl:value-of select="$labelTruncated"/></a>
</xsl:template>

<xsl:template match="channel/content/row/img"><td nowrap="true" align="left" class="tableContent">
	<img src="..{./src}" width="{./width}" height="{./height}" border="{./border}" />
</td>
  </xsl:template>

<xsl:template match="channel/content/row/data_img"><td nowrap="true" align="left" class="tableContent">
	<img src="..{./src}" width="{./width}" height="{./height}" border="{./border}" />
	<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
	<xsl:value-of select="./label"/>
</td>
  </xsl:template>

<xsl:template match="SearchResults"><table border="0" cellpadding="0" cellspacing="0" width="100%">
<xsl:choose>
	<xsl:when test="SearchSpace/SearchObject/channel">
		<xsl:apply-templates select="SearchSpace" />
	</xsl:when>
	<xsl:otherwise>
	<tr>
		<td><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
		<td class="tableHeader" colspan="2" width="96%"><xsl:value-of select="display:get('prm.global.search.results.error.nothingfound.message')"/></td>
		<td><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
	</tr>
	</xsl:otherwise>
</xsl:choose>
</table>
</xsl:template>

<xsl:template match="SearchSpace"><!--	<tr class="channelHeader">
		<td class="channelHeader" width="1%"><img src="../images/icons/channelbar-left_end.gif" width="8" height="15" alt="" border="0" /></td>
		<td class="channelHeader" colspan="2" width="96%"><xsl:value-of select="Name" /></td>
		<td class="channelHeader" width="1%" algin="right"><img src="../images/icons/channelbar-right_end.gif" width="8" height="15" alt="" border="0" /></td>
	</tr>
-->
<!-- Only display if there is data in this space -->
<xsl:if test="SearchObject/channel">
	<tr>
		<td><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
		<td class="pageTitle" colspan="2" width="96%"><xsl:value-of select="Name" /></td>
		<td><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
	</tr>
	<tr><td colspan="4"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td></tr>
	<xsl:apply-templates select="SearchObject" />
</xsl:if></xsl:template>

<xsl:template match="SearchObject"><!-- Only display if there is data for this object -->
<xsl:if test="channel">
<tr class="channelHeader">
	<td class="channelHeader" width="1%"><img src="../images/icons/channelbar-left_end.gif" width="8" height="15" alt="" border="0" /></td>
	<td colspan="2" class="channelHeader" width="96%"><xsl:value-of select="Name" /></td>
	<td class="channelHeader" width="1%" align="right"><img src="../images/icons/channelbar-right_end.gif" width="8" height="15" alt="" border="0" /></td>
</tr>
<tr>
	<td><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
	<td colspan="2"><xsl:apply-templates select="channel" /></td>
	<td><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
</tr>
<tr>
	<td colspan="2"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
</tr>
</xsl:if></xsl:template>

<xsl:template match="channel"><table border="0" cellpadding="0" cellspacing="0" width="100%">
<xsl:apply-templates/>
</table></xsl:template>

</xsl:stylesheet>
