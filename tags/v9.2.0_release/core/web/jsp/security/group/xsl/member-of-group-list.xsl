<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:display="xalan://net.project.base.property.PropertyProvider"
    extension-element-prefixes="display" >

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
        <th align="left" class="over-table"><xsl:value-of select="display:get('prm.directory.memberview.rolename.label')"/></th>
        <th align="left" class="over-table"><xsl:value-of select="display:get('prm.directory.memberview.roledescription.label')"/></th>
 	</tr>
 	<tr class="tableLine">
 		<td colspan="2" class="tableLine">
 			<img src="/images/spacers/trans.gif" width="1" height="2" border="0" />
		</td>
	</tr>
   	<xsl:apply-templates select="group[is_principal!='1']">
        <!-- Sort order:
             groups owned by current space
             for groups not owned by current space, order by space name
             system groups first
             then by group name -->
   	    <xsl:sort select="Space/isOwner" order="descending" />
   	    <xsl:sort select="OwningSpace/Space/name" order="ascending" />       
   	    <xsl:sort select="is_system" order="descending" />
   	    <xsl:sort select="group_name" order="ascending" />
    </xsl:apply-templates>
    <xsl:if test="not(group)">
        <tr class="tableContent">
            <td class="tableContent" colspan="2"><xsl:value-of select="display:get('prm.directory.memberview.notgroup.message')"/></td>
        </tr>
    </xsl:if>
</table>
</xsl:template>
	
<xsl:template match="group">
<tr class="tableContent">
	<td class="tableContent">
        <xsl:choose>
        <xsl:when test="Space/isOwner = '1'">
            <xsl:value-of select="group_name"/>
        </xsl:when>
        <xsl:otherwise>
            <xsl:value-of select="OwningSpace/Space/name" /> : <xsl:value-of select="group_name"/>
        </xsl:otherwise>
        </xsl:choose>
    </td>
	<td class="tableContent"><xsl:value-of select="group_desc"/></td>
</tr>
<tr class="tableLine">
	<td colspan="2" class="tableLine">
		<img src="/images/spacers/trans.gif" width="1" height="1" border="0" />
	</td>
</tr>
</xsl:template>

</xsl:stylesheet>
