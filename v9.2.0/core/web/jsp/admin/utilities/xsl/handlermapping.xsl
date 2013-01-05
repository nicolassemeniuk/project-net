<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output method="html"/>

    <xsl:template match="/">
        <table width="97%">
            <tr class="tableHeader">
                <td>URL</td>
                <td>Class name</td>
                <td>Exists</td>
            </tr>
            <tr><td colspan="3" class="headerSep"></td></tr>
            <xsl:apply-templates select="/entries/entry"/>
        </table>
    </xsl:template>

    <xsl:template match="entry">
        <xsl:variable name="rowClass">
            <xsl:choose>
                <xsl:when test="exists='false'">
                    fieldWithError
                </xsl:when>
            </xsl:choose>
        </xsl:variable>

        <tr class="tableContent {$rowClass}">
            <td><xsl:value-of select="url"/></td>
            <td><xsl:value-of select="className"/></td>
            <td><xsl:value-of select="exists"/></td>
        </tr>
        <tr><td colspan="3" class="rowSep"></td></tr>
    </xsl:template>
</xsl:stylesheet>