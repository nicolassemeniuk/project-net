<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:output method="html"/>

    <xsl:template match="ProjectPortfolio">
        <!--<xsl:apply-templates select="ProjectSpace"/>-->
        <xsl:apply-templates select="ProjectPortfolioEntry"/>
        <xsl:if test="count(ProjectPortfolioEntry)=0">
            <xsl:call-template name="NoProjects"/>
        </xsl:if>
    </xsl:template>

    <!-- <xsl:template match="ProjectSpace">-->
    <xsl:template match="ProjectPortfolioEntry">
        <xsl:choose>
            <xsl:when test='position() = 1'>
                <tr align="left" valign="middle" class="tableContent">
                    <td class="tableHeader">
                        <input type="Checkbox" onClick="CheckAll()" name="SELALL">
                            <xsl:text>Select All</xsl:text>
                        </input>
                    </td>
                </tr>
            </xsl:when>
        </xsl:choose>

        <tr align="left" valign="middle" class="tableContent">
            <td class="tableContent">
                <xsl:element name="input">
                    <xsl:attribute name="type">
                        <xsl:text>CheckBox</xsl:text>
                    </xsl:attribute>
                    <xsl:attribute name="name">
                        <xsl:text>projectID_</xsl:text>
                        <xsl:value-of select="position()"/>
                    </xsl:attribute>
                    <xsl:attribute name="value">
                        <xsl:value-of select="./project_id"/>
                    </xsl:attribute>
                    <xsl:value-of select="./name"/>
                </xsl:element>
            </td>
        </tr>
    </xsl:template>

    <xsl:template name="NoProjects">
        <tr class="tableContent" align="left">
            <td class="tableContent" colspan="6"> No  Project found for this Business</td>
        </tr>
    </xsl:template>

</xsl:stylesheet>

