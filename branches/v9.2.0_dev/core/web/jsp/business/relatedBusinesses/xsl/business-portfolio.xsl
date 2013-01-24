<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:display="xalan://net.project.base.property.PropertyProvider"
	xmlns:format="xalan://net.project.util.XSLFormat"			
    extension-element-prefixes="display format" >
		
    <xsl:output method="html"/>
        <xsl:template match="/">
            <table border="0" cellspacing="0" cellpadding="0" width="100%">
                <tr align="left" class="tableHeader">
                    <td></td>
                    <td><xsl:value-of select="display:get('prm.business.businessportfolio.business.label')"/></td>
                    <td><xsl:value-of select="display:get('prm.business.businessportfolio.businesstype.label')"/></td>
                    <td><xsl:value-of select="display:get('prm.business.businessportfolio.people.label')"/></td>
                </tr>
                <tr>
                    <td colspan="4" class="tableLine"><img src="../images/spacers/trans.gif" height="2" border="0"/></td>
                </tr>
            <xsl:for-each select="BusinessPortfolio/BusinessSpace">
                <tr align="center" class="tableContent"> 
                    <td align="left" class="tableContent"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
                    <td align="left" class="tableContent">
                        <xsl:element name="a">
                            <xsl:attribute name="href">../../business/Main.jsp?id=<xsl:value-of select="businessID"/></xsl:attribute>
                        <xsl:value-of select="name"/>
                        </xsl:element>
                    </td>

                    <td align="left" class="tableContent">
                        <xsl:value-of select="businessType"/>
                    </td>

                    <td class="tableContent" align="left">
                        <xsl:value-of select="format:formatNumber(numMembers)"/>
                    </td>
                </tr>  
                <tr class="tableLine">
                    <td colspan="4">
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

