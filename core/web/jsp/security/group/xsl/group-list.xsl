<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:display="xalan://net.project.base.property.PropertyProvider"
	xmlns:format="xalan://net.project.util.XSLFormat"
    extension-element-prefixes="display format" >

<xsl:output method="html"/>

<!-- Build translation properties node list -->
<xsl:variable name="translation" 
    select="/ProjectXML/Properties/Translation/property" />
<xsl:variable name="JSPRootURL" select="$translation[@name='JSPRootURL']" />

<xsl:template match="/">
    <xsl:apply-templates />
</xsl:template>

<xsl:template match="ProjectXML">
    <xsl:apply-templates select="Content" />
</xsl:template>

<xsl:template match="Content">
    <xsl:apply-templates />
</xsl:template>

<xsl:template match="groupList">
<table width="100%" border="0" cellspacing="0" cellpadding="0" vspace="0">
	<tr class="table-header"> 
    	<th class="over-table"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></th>
        <th align="left" class="over-table"><xsl:value-of select="display:get('prm.directory.roles.grouplistview.name.colum')"/></th>
        <th align="left" class="over-table"><xsl:value-of select="display:get('prm.directory.roles.grouplistview.members.colum')"/></th>
        <th align="left" class="over-table"><xsl:value-of select="display:get('prm.directory.roles.grouplistview.description.colum')"/></th>
        <th align="left" class="over-table"><xsl:value-of select="display:get('prm.directory.roles.grouplistview.email.column')"/></th>
 	</tr>
   	<xsl:apply-templates select="group[is_principal!='1']">
        <!-- Sort system groups first, then by group name -->
   	    <xsl:sort select="is_system" order="descending" />
   	    <xsl:sort select="group_name" order="ascending" />
    </xsl:apply-templates>
    <xsl:if test="not(group)">
        <tr class="tableContent">
            <td><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
            <td class="tableContent" colspan="4"><xsl:value-of select="display:get('prm.directory.roles.grouplistview.emailstatus.none.name')"/></td>
        </tr>
    </xsl:if>
</table>
</xsl:template>
	
<xsl:template match="group">
<tr class="tableContent">
    <!-- All other Roles are modifiable -->
	<td class="tableContent" style="padding-left:5px;">
		<input type="radio" name="selected" value="{id}" />
	</td>
	<td class="tableContent">
		<a href="{$JSPRootURL}/security/group/GroupEdit.jsp?module=140&amp;id={id}"><xsl:value-of select="./group_name"/></a>
	</td>
	<td class="tableContent" align="left">
        <xsl:if test="not(@hideSize = 1)">
            <xsl:value-of select="format:formatNumber(size)"/>
        </xsl:if>
    </td>
	<td class="tableContent"><xsl:value-of select="./group_desc"/></td>
	<td class="tableContent">
        <xsl:if test="not(@hideSendMail = 1)">
		<a href="javascript:sendMail('{id}');"><xsl:value-of select="display:get('prm.directory.roles.grouplistview.emailstatus.send.link')"/></a>
        </xsl:if>
	</td>
</tr>
<tr class="tableLine">
	<td colspan="5" class="tableLine">
		<img src="{$JSPRootURL}/images/spacers/trans.gif" width="1" height="1" border="0" />
	</td>
</tr>
</xsl:template>

</xsl:stylesheet>
