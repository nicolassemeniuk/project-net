<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:output method="html" />

<xsl:template match="/">
    <!-- Add a hidden form field containing the current page start number
         This is required so form submits have a value to pass back to
         the pager display page -->
    <xsl:element name="input">
        <xsl:attribute name="type">hidden</xsl:attribute>
        <xsl:attribute name="name">page_start</xsl:attribute>
        <xsl:attribute name="value">
            <xsl:choose>
                <xsl:when test="PageIndex/Page[@isCurrent=1]">
                    <xsl:value-of select="PageIndex/Page[@isCurrent=1]/PageStart" />
                </xsl:when>
                <xsl:otherwise>
                    0
                </xsl:otherwise>
            </xsl:choose>
        </xsl:attribute>
    </xsl:element>
            
	<!-- Only display pager index when there is a Next or Previous link available.
	     When neither is available, then there is only one page -->
	<xsl:choose>
		<xsl:when test="PageIndex/Next[@isAvailable=1] or PageIndex/Previous[@isAvailable=1]">
			Result Page: <xsl:apply-templates select="PageIndex" />
		</xsl:when>
		<xsl:otherwise>
			<!-- Nothing -->
			<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
		</xsl:otherwise>
	</xsl:choose>
</xsl:template>

<xsl:template match="PageIndex">

	<xsl:if test="Previous/@isAvailable=1">
		<xsl:variable name="href" select="Previous/Href" />
		<a href="{$href}">&lt; Previous</a>
		<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
	</xsl:if>

	<xsl:if test="./Page">
		<xsl:apply-templates select="Page" />
	</xsl:if>

	<xsl:if test="Next/@isAvailable=1">
		<xsl:variable name="href" select="Next/Href" />
		<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
		<a href="{$href}">Next &gt;</a>
	</xsl:if>

</xsl:template>

<xsl:template match="Page">
	<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
	<xsl:choose>
		<xsl:when test="@isCurrent=0">
			<!-- Non current pages are rendered as hyperlinks -->
			<a href="{Href}"><xsl:value-of select="DisplayNumber" /></a>
		</xsl:when>
		<xsl:otherwise>
			<!-- Current page is rendered as a number -->
			<xsl:value-of select="DisplayNumber" />
		</xsl:otherwise>
	</xsl:choose>
	<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
</xsl:template>

</xsl:stylesheet>

