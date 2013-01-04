<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:display="xalan://net.project.base.property.PropertyProvider"
    extension-element-prefixes="display" >

<xsl:output method="html" />

<xsl:template match="/">
	<xsl:apply-templates select="ProjectXML" />
</xsl:template>

<xsl:template match="ProjectXML">
	<xsl:apply-templates select="Content" />
</xsl:template>

<xsl:template match="Content">


	<xsl:apply-templates select="InviteeList" />
</xsl:template>

<xsl:template match="InviteeList">
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr class="tableHeader">
			<td class="tableHeader"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
			<td class="tableHeader" align="left"><xsl:value-of select="display:get('prm.directory.invite.currentinvitees.column.name')"/></td>
			<td class="tableHeader" align="left"><xsl:value-of select="display:get('prm.directory.invite.currentinvitees.column.emailaddress')"/></td>
		</tr>
		<tr class="tableLine">
			<td colspan="3" class="tableLine">
				<img src="../images/spacers/trans.gif" width="1" height="2" border="0" />
			</td>
		</tr>
		<xsl:choose>
			<xsl:when test="Invitee">
				<xsl:apply-templates select="Invitee" />	
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="noInvitees" />
			</xsl:otherwise>
		</xsl:choose>
	</table>
</xsl:template>

<xsl:template match="Invitee">
    <tr>
        <td class="tableContent">
			<xsl:element name="a">
			<xsl:attribute name="href"><xsl:text disable-output-escaping="yes">javascript:removeInvitee('</xsl:text> <xsl:value-of select="Email" /><xsl:text disable-output-escaping="yes">')</xsl:text></xsl:attribute>
			<xsl:value-of select="display:get('prm.directory.invite.currentinvitees.remove.link')"/>
			</xsl:element>
          <!--  <a href="javascript:removeInvitee();">Remove</a> -->
        </td>
    	<td class="tableContent">
			<xsl:value-of select="LastName" />, <xsl:value-of select="FirstName" />
        </td>
        <td class="tableContent">
            <xsl:value-of select="Email" />
        </td>
    </tr>
</xsl:template>

<xsl:template name="noInvitees">
	<tr class="tableContent">
		<td class="tableContent" colspan="3">
			<xsl:value-of select="display:get('prm.directory.invite.currentinvitees.noinvitees.message')"/>
		</td>
	</tr>
</xsl:template>

</xsl:stylesheet>