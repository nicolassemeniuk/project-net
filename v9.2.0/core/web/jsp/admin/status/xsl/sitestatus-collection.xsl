<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="html" />

  <xsl:template match="SiteStatusManager">

  	<table cellpadding="0" cellspacing="0" border="0" width="100%">
		<tr class="tableHeader" align="left" valign="top">
		<td class="tableHeader" width="3%" ></td>
			<td class="tableHeader" >Message Title</td>
			<td class="tableHeader" >Status</td>
		</tr>
		<tr class="tableLine">

			<td colspan="5" class="tableLine">
				<xsl:element name="img">
					<xsl:attribute name="src">../images/spacers/trans.gif</xsl:attribute>
					<xsl:attribute name="width">1</xsl:attribute>
					<xsl:attribute name="height">2</xsl:attribute>
					<xsl:attribute name="border">0</xsl:attribute>
				</xsl:element>
			</td>
		</tr>
			<xsl:apply-templates select="StatusMessageCollection/status_message"/>
			 	<xsl:if test="count(status_message)=0"><xsl:call-template name="NoMessages" /></xsl:if>
	</table>
	</xsl:template>

	<xsl:template match="status_message">
		
		<tr align="left" valign="middle" class="tableContent">
		
		 <td class="tableContent"><input name="selected" type="radio" value="{message_id}"/></td>
			
			<td class="tableContent">
				<xsl:element name="a">
					<xsl:attribute name="href">SystemStatusEdit.jsp?selected=<xsl:value-of select="message_id"/><xsl:text disable-output-escaping="yes">&amp;</xsl:text>module=240<xsl:text disable-output-escaping="yes">&amp;</xsl:text>action=2</xsl:attribute>
					<xsl:value-of select="./message_title"/>
				</xsl:element>
			</td>
           
			<td class="tableContent">
				<xsl:value-of select="message_status"/>
			</td>
			 <!--
			<td class="tableContent">
				<xsl:value-of select="authenticatorName"/>
			</td>

			<td class="tableContent">
				<xsl:value-of select="userCount"/>
			</td>
			-->
         </tr>

	<tr class="tableLine">
		<td colspan="5">
			<xsl:element name="img">
				<xsl:attribute name="src">../images/spacers/trans.gif</xsl:attribute>
				<xsl:attribute name="width">1</xsl:attribute>
				<xsl:attribute name="height">1</xsl:attribute>
				<xsl:attribute name="border">0</xsl:attribute>
			</xsl:element>
		</td>
	</tr>
	</xsl:template>

<xsl:template name="NoMessages">
<tr class="tableContent" align="left"> 
  <td class="tableContent" colspan="5">There are currently no Site Messages to display</td>
</tr>
</xsl:template>
</xsl:stylesheet>
