<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

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
	
  <xsl:template match="methodology_portfolio">
  	<table cellpadding="0" cellspacing="0" border="0" width="100%">
		<tr class="tableHeader" align="left" valign="top">
			<td class="tableHeader" width="1%"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
			<td class="tableHeader" width="25%"><xsl:value-of select="$translation[@name='prm.template.portfolio.name']" /></td>
			<td class="tableHeader" width="25%"><xsl:value-of select="$translation[@name='prm.template.portfolio.owner']" /></td>
			<td class="tableHeader"><xsl:value-of select="$translation[@name='prm.template.portfolio.author']" /></td>
		</tr>
		<tr class="tableLine">
			<td colspan="7" class="tableLine">
                <img src="{$JSPRootURL}/images/spacers/trans.gif" width="1" height="2" border="0" />
			</td>
		</tr>
			<xsl:if test="count(methodology)=0">
    			<tr class="tableContent">
	    			<td colspan="7"><xsl:value-of select="$translation[@name='prm.template.nonefound.message']" /></td>
		</tr>
			</xsl:if>
			<xsl:apply-templates select="methodology"/>
	</table>
	</xsl:template>
	
	<xsl:template match="methodology_portfolio/methodology">
		<tr align="left" valign="middle" class="tableContent">
			<td class="tableContent">
                <input type="radio" name="selected" value="{object_id}" />
			</td>
            <td class="tableContent">
                <a href="{$JSPRootURL}/methodology/Main.htm?id={object_id}"><xsl:value-of select="./name"/></a>
			</td>
            <td class="tableContent"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text><xsl:value-of select="./parent_space_name"/></td>
			<td class="tableContent"><xsl:value-of select="./created_by"/></td>
         </tr>
	<tr class="tableLine">
		<td colspan="7">
            <img src="{$JSPRootURL}/images/spacers/trans.gif" width="1" height="1" border="0" />
		</td>
	</tr>
	</xsl:template>
	
</xsl:stylesheet>
