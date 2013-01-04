<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:template match="/">
    <xsl:apply-templates select="groupList" />
</xsl:template>

<xsl:template match="groupList">
    <xsl:apply-templates select="group" />
</xsl:template>

<xsl:template match="group">
<tr>
    <td class="tableContent">
        <input type="checkbox" name="Persons" value="{id}" />
	</td>
	<td class="tableContent"><xsl:value-of select="./group_name"/></td>
	<td class="tableContent"><xsl:value-of select="./group_desc"/></td>	
</tr>
<tr class="tableLine">
    <td  colspan="3" class="tableLine"><img src="../../images/spacers/trans.gif" width="1" height="1" border="0" alt=""/></td>
</tr>
</xsl:template>

</xsl:stylesheet>