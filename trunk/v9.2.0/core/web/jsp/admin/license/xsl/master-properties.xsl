<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:output method="html" />

<xsl:template match="/">
	<xsl:apply-templates />
</xsl:template>


<xsl:template match="MasterProperties">
    <table width="100%" border="0" cellpadding="0" cellspacing="0">
        <xsl:apply-templates select="PropertyCollection/Property" />
    </table>	
</xsl:template>

<xsl:template match="Property">
    <tr class="tableContent">
        <td class="tableContent"><xsl:value-of select="PropertyName/Name" /></td>
        <td class="tableContent"><xsl:value-of select="Value" /></td>
    </tr>	
</xsl:template>

</xsl:stylesheet>
