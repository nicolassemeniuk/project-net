<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
		xmlns:display="xalan://net.project.base.property.PropertyProvider"
		xmlns:java="http://xml.apache.org/xslt/java" 
		xmlns:format="xalan://net.project.util.XSLFormat"
		extension-element-prefixes="display format java" exclude-result-prefixes="java">
			
  <xsl:template match="subscription_collection">     
  	
	<table cellpadding="0" cellspacing="0" border="0" width="100%">
		<tr class="tableHeader" align="left" valign="top">
			<td class="tableHeader" width="1%"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
			<td class="tableHeader" width="25%">Subscription</td>
			<td class="tableHeader" width="25%">Created</td>
			<td class="tableHeader">WorkSpace Name</td>
			<td class="tableHeader" width="24%">WorkSpace Type</td>
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
			<xsl:apply-templates select="subscription"/>    
			<xsl:if test="count(subscription)=0">
            			<xsl:call-template name="NoSubscriptions"/>
        	</xsl:if>                                                          
	</table>
	</xsl:template>
	
	<xsl:template match="subscription_collection/subscription">                                                                                                                                 
		<tr align="left" valign="middle" class="tableContent">
			<td class="tableContent">
				<xsl:element name="input">
					<xsl:attribute name="type">radio</xsl:attribute>
					<xsl:attribute name="name">subscriptionID</xsl:attribute>
					<xsl:attribute name="value"><xsl:value-of select="./object_id"/></xsl:attribute>
				</xsl:element>
			</td><td class="tableContent">
				<xsl:element name="a">
					<xsl:attribute name="href">../notification/CreateSubscription2.jsp?subscriptionID=<xsl:value-of select="./object_id"/>
					<xsl:text disable-output-escaping="yes">&amp;spaceID=</xsl:text><xsl:value-of select="./Space/id"/>
					</xsl:attribute>
					<xsl:value-of select="./name"/>
				</xsl:element>
			</td>
			<td class="tableContent"><xsl:value-of select="format:formatISODate(created_date)"/></td>
			<td class="tableContent"><xsl:value-of select="./Space/name"/></td>
			<td class="tableContent"><xsl:value-of select="./Space/SpaceType/name"/></td>
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
	</xsl:template>
	<xsl:template name="NoSubscriptions">
        <tr class="tableContent" align="left">
            <td class="tableContent" colspan="6"> There are currently no subscriptions</td>
        </tr>
    </xsl:template>
</xsl:stylesheet>
