<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output method="html"/>

    <xsl:template match="/">
        <table border="0" width="100%" style="border: solid thin #000000">
            <tr>
                <td></td>
                <td class="tableHeader">File Location</td>
                <td class="tableHeader">Size</td>
                <td class="tableHeader">Last Modification Date</td>
            </tr>
            <tr><td class="headerSep" colspan="4"></td></tr>
            <xsl:apply-templates/>
        </table>
    </xsl:template>

    <xsl:template match="ClassPathEntry">
        <tr>
            <td class="tableContent"><xsl:value-of select="Seq"/></td>
            <td class="tableContent"><xsl:value-of select="Location"/></td>
            <td class="tableContent"><xsl:value-of select="FileSize"/> bytes</td>
            <td class="tableContent"><xsl:value-of select="LastModified"/></td>
        </tr>
    </xsl:template>
    <!-- Place your <xsl:template> objects here -->
</xsl:stylesheet>