<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:display="xalan://net.project.base.property.PropertyProvider"
	xmlns:format="xalan://net.project.util.XSLFormat"
        extension-element-prefixes="display format" >
<xsl:output method="html"/>

  <xsl:template match="document_list">

	<xsl:apply-templates select="/document_list/document" /> 	

  </xsl:template>

  <xsl:template match = "/document_list/document">

        <tr align="center" > 	

	    <td align="left" class="tableContent">
		<xsl:element name="a">
		<xsl:attribute name="href"><xsl:value-of select="./url"/></xsl:attribute>
		<img border="0" src="..{./app_icon_url}"/>
		</xsl:element> 	
	    </td>

	    <td align="left" class="tableContent">
		<xsl:element name="a">
		<xsl:attribute name="href"><xsl:value-of select="./url"/></xsl:attribute>
		<xsl:value-of select="./name"/>	</xsl:element> 
	    </td>
	    <td align="left" class="tableContent">
	    	<xsl:if test="space_name='Personal Space'">
	    		Personal
	    	</xsl:if>
	    	<xsl:if test="space_name!='Personal Space'">
	    		<xsl:value-of select="./space_name"/>
	    	</xsl:if>
	    </td>
	    <!-- <td align="left" class="tableContent"><xsl:value-of select="./format"/></td>
	    <td align="left" class="tableContent"><xsl:value-of select="./status"/></td> -->
	    <td align="left" class="tableContent"><xsl:value-of select="./cko_date"/> </td>
	    <!-- <td align="left" class="tableContent"><xsl:value-of select="format:formatISODate(./cko_return)"/> </td> -->

        </tr>
        	<!-- tr class="tableLine">
			<td  colspan="7" class="tableLine"><img src="../images/spacers/trans.gif" width="1" height="1" border="0" alt=""/></td>
	</tr -->
   
   </xsl:template>

</xsl:stylesheet>
