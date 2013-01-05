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

<xsl:template match="text()|@*"><xsl:value-of select="."/></xsl:template>

<xsl:template match="methodology_portfolio">
<table border="0" width="100%">
<xsl:if test="count(methodology)=0">
    <tr class="tableContent">
	    <td><xsl:value-of select="$translation[@name='prm.template.nonefound.message']" /></td>
	</tr>
</xsl:if>
<xsl:apply-templates select="methodology" />
</table>
</xsl:template>

<xsl:template match="methodology">
<tr>
    <td colspan="3">
        <table border="0" vspace="0" cellpadding="0" cellspacing="0"  width="100%">
		    <tr class="channelHeader"> 
			    <td width="1%"><img src="../images/icons/channelbar-left_end.gif" width="8" height="15" border="0" /></td>
				<th valign="middle" align="left" class="channelHeader">
                    <xsl:value-of select="$translation[@name='prm.template.browser.name']" />: <xsl:value-of select="./name" /></th>
				<td width="1%" align="right"><img src="../images/icons/channelbar-right_end.gif" width="8" height="15" border="0" /></td>
			</tr>
	    </table>
	</td>
</tr>
		
<tr>
    <td width="7%"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
	<th align ="left" width="15%" class="tableHeader"><xsl:value-of select="$translation[@name='prm.template.browser.owner']" />:</th>
	<td width="78%" class="tableContent">
	    <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text><xsl:value-of select="./parent_space_name" />
	</td>
</tr>

<tr>
    <td width="7%"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
	<th align="left" width="15%" class="tableHeader"><xsl:value-of select="$translation[@name='prm.template.browser.description']" />:</th>
	<td width="78%" class="tableContent">
	    <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text><xsl:value-of select="./description" />
	</td>
</tr>

<tr>
    <td width="7%"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
	<th align="left" width="15%" class="tableHeader"><xsl:value-of select="$translation[@name='prm.template.browser.usescenario']" />:</th>
	<td width="78%" class="tableContent">
	    <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text><xsl:value-of select="./use_scenario" />
	</td>
</tr>

<tr>
    <td colspan="3"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
</tr>

</xsl:template>

</xsl:stylesheet>