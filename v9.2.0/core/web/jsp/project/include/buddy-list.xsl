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

<xsl:template match="buddy_list">
	<TABLE cellpadding="1" cellspacing="1" width="100%" border="0">
	<TBODY>
	<xsl:apply-templates select="user"/>
	</TBODY>
	</TABLE>
</xsl:template>

<xsl:template match="buddy_list/user">
	<tr>
		<td class="tableContent" NOWRAP="1">
		<xsl:choose>
		<xsl:when test="string(./activity_status)=string('InActive')">
			<i>
			<a class="user-online" href="{$JSPRootURL}/blog/view/{user-id}/{user-id}/person/140?module=140">
            <!-- a href="{$JSPRootURL}/roster/PersonalDirectoryView.htm?memberid={user-id}&amp;module=140&amp;referrer=dashboard" -->
				<xsl:value-of select="./full-name"/>
				<xsl:text disable-output-escaping="yes">&amp;nbsp;(</xsl:text>
				<xsl:value-of select="display:get('prm.project.main.inactive.label')"/>
				<xsl:text disable-output-escaping="yes">)</xsl:text>
            </a>
			</i>
		</xsl:when>	
		<xsl:otherwise>
			  <a  class="user" href="{$JSPRootURL}/blog/view/{user-id}/{user-id}/person/140?module=140">
			  <!-- a href="{$JSPRootURL}/roster/PersonalDirectoryView.htm?memberid={user-id}&amp;module=140&amp;referrer=dashboard" -->
				<xsl:value-of select="./full-name"/>
            	</a>
		</xsl:otherwise>
		</xsl:choose>			
		</td>
	</tr>
</xsl:template>

</xsl:stylesheet>