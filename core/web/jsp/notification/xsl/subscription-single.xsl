<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
		xmlns:display="xalan://net.project.base.property.PropertyProvider"
		xmlns:java="http://xml.apache.org/xslt/java" 
		xmlns:format="xalan://net.project.util.XSLFormat"
		extension-element-prefixes="display format java" exclude-result-prefixes="java">
	<xsl:template match="subscription">  
  	
        <table cellpadding="0" cellspacing="0" border="0" width="100%">
		<tr class="tableHeader" align="left" valign="top">
			<td class="tableHeader" width="1%"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
			<td class="tableHeader" width="10%">Name</td>
			<td class="tableHeader" width="10%">Description</td>
                        <td class="tableHeader" width="10%">Message</td>
                        <td class="tableHeader" width="10%">Type</td>
                        <td class="tableHeader" width="10%">Delivered Via</td>
                        <td class="tableHeader" width="10%">Delivered To</td>
                        <td class="tableHeader" width="10%">Created On</td>
                        <td class="tableHeader" width="10%">Created By</td>
		</tr>
		<tr class="tableLine">
			<td colspan="7" class="tableLine">
				<xsl:element name="img"> 
					<xsl:attribute name="src">../images/spacers/trans.gif</xsl:attribute> 
					<xsl:attribute name="width">1</xsl:attribute>
					<xsl:attribute name="height">2</xsl:attribute>
					<xsl:attribute name="border">0</xsl:attribute>
				</xsl:element>
			</td>
		</tr>
	
         <tr align="left" valign="middle" class="tableContent">
         
			<td class="tableContent" width="1%"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
			<td class="tableContent" width="10%"><xsl:value-of select="./name"/></td>
			<td class="tableContent" width="10%"><xsl:value-of select="./description"/></td>
                        <td class="tableContent" width="10%"><xsl:value-of select="./custom_message"/></td>
                        <td class="tableContent" width="10%"><xsl:value-of select="./subscription_type"/></td>
                        <td class="tableContent" width="10%"><xsl:value-of select="./delivered_via"/></td>
                        <td class="tableContent" width="10%"><xsl:value-of select="./delivered_to"/></td>
                        <td class="tableContent" width="10%"><xsl:value-of select="format:formatISODateTime(created_date)"/></td>
                        <td class="tableContent" width="10%"><xsl:value-of select="./created_by_id"/></td>
		</tr>
   	<tr class="tableLine">
		<td colspan="7">
			<xsl:element name="img">
				<xsl:attribute name="src">../images/spacers/trans.gif</xsl:attribute>
				<xsl:attribute name="width">1</xsl:attribute>
				<xsl:attribute name="height">1</xsl:attribute>
				<xsl:attribute name="border">0</xsl:attribute>
			</xsl:element>
		</td>
	</tr>   
        </table>

	</xsl:template>
	
</xsl:stylesheet>
