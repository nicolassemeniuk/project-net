<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:output method="html" />

<xsl:param name="JspRootUrl" />
<xsl:template match="/">
    <xsl:apply-templates select="ProjectXML" />
</xsl:template>

<xsl:template match="ProjectXML">
    <xsl:apply-templates select="Content" />
</xsl:template>

<xsl:template match="Content">
    <xsl:apply-templates />
</xsl:template>

<xsl:template match="TestResults">
  	<table cellpadding="0" cellspacing="1" border="0" width="100%">
    	<xsl:apply-templates select="Result" />
	</table>
</xsl:template>

<xsl:template match="Result">
    <tr class="tableContent">
        <td align="left" class="tableContent" valign="top">
            <xsl:choose>
                <xsl:when test="@isError='0'">
                    <img src="{$JspRootUrl}/images/green_light.gif" border="0" alt="" />
                </xsl:when>
                <xsl:otherwise>
                    <img src="{$JspRootUrl}/images/red_light.gif" border="0" alt="" />
                </xsl:otherwise>
            </xsl:choose>
			<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
        </td>
        <td class="tableContent" colspan="2">
            <xsl:choose>
                <xsl:when test="@isError='0'">
                    <xsl:value-of select="Message" />
                </xsl:when>
                <xsl:otherwise>
                    <span class="fieldWithError">
                        <xsl:value-of select="Message" />
                    </span>
                </xsl:otherwise>
            </xsl:choose>
        </td>
    </tr>
    <xsl:if test="OriginatingError!=''">
        <tr class="tableContent">
			<td><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
			<td><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
            <td align="left" class="tableContent">
				<span class="fieldWithError">
                	<xsl:value-of select="OriginatingError/Message" />
<!-- Exclude the Cause exception for now;  it is virtually 
     always added to the OriginatingError Message -->
<!--
					<xsl:if test="OriginatingError/Cause!=''">
						<br />
						<xsl:value-of select="OriginatingError/Cause" />
					</xsl:if>
-->
				</span>
            </td>
        </tr>
    </xsl:if>

</xsl:template>


</xsl:stylesheet>
