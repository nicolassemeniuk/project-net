<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:display="xalan://net.project.base.property.PropertyProvider" extension-element-prefixes="display" >
    <xsl:output method="html"/>
    <xsl:variable name="translation" select="/ProjectXML/Properties/Translation/property"/>

    <!-- Get the idcsv variable that I passed into this XSL -->
    <xsl:variable name="idcsv" select="$translation[@name='idcsv']"/>
    <!--<xsl:variable name="idcsv" select="1,2,3"/>-->

    <xsl:template match="ProjectXML">
        <xsl:apply-templates select="Content" />
    </xsl:template>

    <xsl:template match="Content">
        <xsl:apply-templates/>
    </xsl:template>

    <xsl:template match="BusinessPortfolio">
        <table border="0" cellspacing="0" cellpadding="0" width="80%">
            <tr align="left" class="tableHeader">
                <td></td>
                <td><xsl:value-of select="display:get('prm.form.designer.formsharing.name.column')"/></td>
                <td><xsl:value-of select="display:get('prm.form.designer.formsharing.type.column')"/></td>
            </tr>
            <tr>
                <td colspan="3" class="tableLine"><img src="../images/spacers/trans.gif" height="2" border="0"/></td>
            </tr>
        <xsl:for-each select="BusinessSpace">
            <tr align="center" class="tableContent">
                <xsl:choose>
                <xsl:when test="contains($idcsv, businessID)">
                <td align="left" class="tableContent"><input name="spaceToShareWith" type="checkbox" value="{businessID}" checked="true"/></td>
                </xsl:when>
                <xsl:otherwise>
                <td align="left" class="tableContent"><input name="spaceToShareWith" type="checkbox" value="{businessID}"/></td>
                </xsl:otherwise>
                </xsl:choose>
                <td align="left" class="tableContent">
                    <xsl:element name="a">
                        <xsl:attribute name="href">../../business/Main.jsp?id=<xsl:value-of select="businessID"/></xsl:attribute>
                    <xsl:value-of select="name"/>
                    </xsl:element>
                </td>

                <td align="left" class="tableContent">
                    <xsl:value-of select="businessType"/>
                </td>

            </tr>  
            <tr class="tableLine">
                <td colspan="3">
                    <xsl:element name="img">
                    <xsl:attribute name="src">../images/spacers/trans.gif</xsl:attribute>
                    <xsl:attribute name="width">1</xsl:attribute>
                    <xsl:attribute name="height">1</xsl:attribute>
                    <xsl:attribute name="border">0</xsl:attribute>
                    </xsl:element>
                </td>
            </tr>
        </xsl:for-each>
        </table>
    </xsl:template>
</xsl:stylesheet>

