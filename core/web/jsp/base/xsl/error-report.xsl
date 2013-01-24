<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output method="html"/>
    <xsl:param name="JspRootUrl" />
    <xsl:template match="ErrorReport">
        <table width="100%">
            <xsl:apply-templates/>
        </table>
    </xsl:template>

    <xsl:template match="SummaryDescription">
        <tr>
            <td class="errorMessage" colspan="2"><xsl:value-of select="."/></td>
        </tr>
    </xsl:template>

    <xsl:template match="DetailedErrors/ErrorDescription">
        <tr>
            <td class="errorMessage" colspan="2"><li><xsl:value-of select="ErrorText"/></li></td>
        </tr>
    </xsl:template>

    <xsl:template match="Warnings/Warning">
       <tr id="warningId">
            <td><img src="{$JspRootUrl}/images/warning_16x16.gif" border="0" width="16" height="16"/></td>
            <td class="warningMessage"><xsl:value-of select="Description"/></td>
        </tr>
    </xsl:template>
</xsl:stylesheet>