<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:display="xalan://net.project.base.property.PropertyProvider"
        extension-element-prefixes="display" >

<xsl:output method="html"/>

<xsl:variable name="BOOKMARK_APP_ICON_URL">/images/appicons/url.gif</xsl:variable>

<xsl:template match="/">
	<xsl:apply-templates select="container_result" />
</xsl:template>

<xsl:template match = "container_result">
	<!-- Table already started.  Insert HEADER row -->
	<tr class="tableHeader" align="center">
		<td align="left" nowrap="true" class="tableHeader" colspan="2"></td>
	    <td align="left" nowrap="true" class="tableHeader">
			<div align="left"><xsl:value-of select="display:get('prm.global.links.modify.addlink.browse.name.column')"/></div>
       	</td>
	    <td align="left" nowrap="true" class="tableHeader"> 
			<div align="left"><xsl:value-of select="display:get('prm.global.links.modify.addlink.browse.format.column')"/></div>
        </td>
	    <td align="left" nowrap="true" class="tableHeader"> 
			<div align="left"><xsl:value-of select="display:get('prm.global.links.modify.addlink.browse.author.column')"/></div>
        </td>
	    <td align="left" nowrap="true" class="tableHeader"> 
			<div align="right"><xsl:value-of select="display:get('prm.global.links.modify.addlink.browse.size.column')"/></div>
        </td>
	</tr>
	 
	<!-- Insert Line below header -->
	<tr class="tableLine">
		<td colspan="100" class="tableLine"><img src="../images/spacers/trans.gif" width="1" height="2" border="0" alt=""/></td>
    </tr>	 

	<!-- Now insert container -->
	<xsl:apply-templates select="container" />

</xsl:template>


<xsl:template match="container">
	<xsl:apply-templates select="container_contents" />	
</xsl:template>


<xsl:template match="container_contents">
	<!-- First Containers, then non-containers -->
	<xsl:apply-templates select="entry[@type='doc_container']" />
	<xsl:apply-templates select="entry[@type!='doc_container']" />
</xsl:template>

<xsl:template match="entry">
	<xsl:choose>
		<xsl:when test="@type='doc_container'">
			<xsl:call-template name="entry_doc_container" />
		</xsl:when>
		<xsl:when test="@type='bookmark'">
			<xsl:call-template name="entry_bookmark" />
		</xsl:when>
		<xsl:otherwise>
			<xsl:call-template name="entry_document" />
		</xsl:otherwise>
	</xsl:choose>
</xsl:template>

<xsl:template name="entry_doc_container">
	<!-- Insert an entry for a doc_container -->
	<tr align="center" class="tableContent"> 
		<td nowrap="true" align="left" class="tableContent"> 
			<input type="hidden" name="{object_id}" value="{object_type}" />
	    </td>
	    <td nowrap="true" align="left" class="tableContent"><img src="../images/folder.gif"/></td>
	    <td nowrap="true" align="left" class="tableContent">
			<xsl:element name="a">
				<xsl:attribute name="href"><xsl:value-of select="/container_result/container/container_properties/app_url"/>/link/traverseFolder.jsp?module=<xsl:value-of select="/container_result/module"/>&amp;action=<xsl:value-of select="/container_result/action"/>&amp;cid=<xsl:value-of select="./object_id"/>&amp;id=<xsl:value-of select="/container_result/return_id"/>&amp;type_pick=<xsl:value-of select="/container_result/type_pick"/>&amp;search_level=<xsl:value-of select="/container_result/search_level"/></xsl:attribute>
				<xsl:value-of select="./name"/>
			</xsl:element>
	    </td> 
	    <td nowrap="true" align="center" class="tableContent">
			<div align="left"><xsl:value-of select="display:get('prm.global.links.modify.addlink.browse.folder.text')"/></div>
        </td>
        <td align="left"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
        <td align="left"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
	</tr>
	<tr class="tableLine">
		<td colspan="100" class="tableLine"><img src="../images/spacers/trans.gif" width="1" height="1" border="0" alt="" /></td>
	</tr>
</xsl:template>

<xsl:template name="entry_bookmark">
	<tr>
		<td nowrap="true" align="left" class="tableContent"> 
			<input type="radio" name="selected" value="{object_id}" />
			<input type="hidden" name="{object_id}" value="{object_type}" />
	    </td>
	    <td nowrap="true" align="left" class="tableContent">
			<img border="0" src="..{$BOOKMARK_APP_ICON_URL}"/>
	    </td>
	    <td nowrap="true" align="left" class="tableContent">
			<xsl:value-of select="./name"/>
	    </td>
	    <td nowrap="true" align="left" class="tableContent"><xsl:value-of select="./format"/></td>
	    <td nowrap="true" align="left" class="tableContent"><xsl:value-of select="./author"/></td>
 	    <td nowrap="true" align="right" class="tableContent"><xsl:value-of select="./file_size"/></td>
    </tr>
	<tr class="tableLine">
		<td colspan="100" class="tableLine"><img src="../images/spacers/trans.gif" width="1" height="1" border="0" alt="" /></td>
	</tr>
</xsl:template>

<xsl:template name="entry_document">
	<tr align="center" class="tableContent"> 	
		<td nowrap="true" align="left" class="tableContent"> 
			<input type="radio" name="selected" value="{object_id}" />
			<input type="hidden" name="{object_id}" value="{object_type}" />
	    </td>
	    <td nowrap="true" align="left" class="tableContent">
			<img border="0" src="..{./app_icon_url}"/>
	    </td>
	    <td nowrap="true" align="left" class="tableContent">
			<xsl:value-of select="./name"/>
	    </td>
	    <td nowrap="true" align="left" class="tableContent"><xsl:value-of select="./format"/></td>
	    <td nowrap="true" align="left" class="tableContent"><xsl:value-of select="./author"/></td>
 	    <td nowrap="true" align="right" class="tableContent"><xsl:value-of select="./file_size"/></td>
    </tr>
	<tr class="tableLine">
		<td colspan="100" class="tableLine"><img src="../images/spacers/trans.gif" width="1" height="1" border="0" alt="" /></td>
	</tr>
</xsl:template>

</xsl:stylesheet>

