<?xml version="1.0"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:display="xalan://net.project.base.property.PropertyProvider" extension-element-prefixes="display" >

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

<xsl:template match="GroupMembers">
    <!-- Display all non-principal groups -->
	<xsl:apply-templates select="group[is_principal != 1]">
    </xsl:apply-templates>

    <!-- Display all persons -->
	<xsl:apply-templates select="person">
        <xsl:sort select="full_name" />
    </xsl:apply-templates>        
</xsl:template>

<xsl:template match="person">
<tr class="tableContent">
	<td class="tableContent">
        <xsl:choose>
        <xsl:when test="$translation[@name='groupTypeID']='300'">
            <!-- Team Member group, person non-moveable -->
            <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
        </xsl:when>
        <xsl:otherwise>
		    <input type="checkbox" name="person_{$translation[@name='checkboxName']}" value="{person_id}" />
        </xsl:otherwise>
        </xsl:choose>
	</td>
    <td class="tableContent">
   	<xsl:element name="img">
		<xsl:attribute name="src">../../images/group_person_small.gif</xsl:attribute>
		<xsl:attribute name="width">16</xsl:attribute>
		<xsl:attribute name="height">16</xsl:attribute>
		<xsl:attribute name="border">0</xsl:attribute>
		<xsl:attribute name="alt"><xsl:value-of select="display:get('prm.workflow.stepedit.roles.image.person.alttext')"/></xsl:attribute>
	</xsl:element>
    </td>
	<td class="tableContent">
        <a href="{$JSPRootURL}/roster/PersonalDirectoryView.htm?module=140&amp;memberid={person_id}"><xsl:value-of select="full_name" /></a>
    </td>
	<td class="tableContent"><xsl:value-of select="username" /></td>
</tr>
<tr class="tableLine">
    <td colspan="4" class="tableLine"><img src="{$JSPRootURL}/images/spacers/trans.gif" width="1" height="1" border="0" alt=""/></td>
</tr>
</xsl:template>

<xsl:template match="group">
<tr class="tableContent">
	<td class="tableContent">
		<input type="checkbox" name="group_{$translation[@name='checkboxName']}" value="{id}" />
	</td>
	<td class="tableContent">
   	<xsl:element name="img">
		<xsl:attribute name="src">../../images/group_group_small.gif</xsl:attribute>
		<xsl:attribute name="width">16</xsl:attribute>
		<xsl:attribute name="height">16</xsl:attribute>
		<xsl:attribute name="border">0</xsl:attribute>
		<xsl:attribute name="alt"><xsl:value-of select="display:get('prm.workflow.stepedit.roles.image.role.alttext')"/></xsl:attribute>
	</xsl:element>
    </td>
	<td class="tableContent">
        <xsl:choose>
        <xsl:when test="Space/isOwner = '1'">
            <a href="{$JSPRootURL}/security/group/GroupEdit.jsp?module=140&amp;id={id}"><xsl:value-of select="group_name"/></a>
        </xsl:when>
        <xsl:otherwise>
            <xsl:value-of select="OwningSpace/Space/name" /> : <xsl:value-of select="group_name"/>
        </xsl:otherwise>
        </xsl:choose>
    </td>
	<td class="tableContent"><xsl:value-of select="group_desc"/></td>
</tr>
<tr class="tableLine">
 	<td  colspan="4" class="tableLine"><img src="{$JSPRootURL}/images/spacers/trans.gif" width="1" height="1" border="0" alt=""/></td>
</tr>
</xsl:template>

</xsl:stylesheet>
