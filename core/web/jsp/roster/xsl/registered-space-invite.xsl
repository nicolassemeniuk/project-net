<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:url="xalan://net.project.notification.XSLFormatNotificationURL"
    xmlns:display="xalan://net.project.base.property.PropertyProvider"
    extension-element-prefixes="url">

<xsl:output method="text"/>

<xsl:template match="/">
	<xsl:apply-templates select="InviteNotification" />
</xsl:template>

<xsl:template match="InviteNotification">
	<xsl:variable name="inviterName" select="TeamMemberWizard/user/full-name" />
<xsl:value-of select="display:get('prm.notification.dear')"/> <xsl:value-of select="TeamMemberWizard/inviteeFullName"/>,
<xsl:text>&#xa;</xsl:text><xsl:text>&#xa;</xsl:text>

<xsl:value-of select="display:get('prm.notification.roster.registered_space_invite.invited_by')"/> <xsl:value-of select="$inviterName"/> <xsl:value-of select="display:get('prm.notification.roster.registered_space_invite.participate_workspace')"/>
<xsl:text>&#xa;</xsl:text><xsl:text>&#xa;</xsl:text>

<xsl:value-of select="display:get('prm.notification.roster.registered_space_invite.workspace_name')"/> <xsl:value-of disable-output-escaping="yes" select="TeamMemberWizard/Space/name"/>
<xsl:text>&#xa;</xsl:text>
<xsl:value-of select="display:get('prm.notification.roster.registered_space_invite.description')"/> <xsl:value-of disable-output-escaping="yes" select="TeamMemberWizard/Space/description"/><xsl:if test="TeamMemberWizard/Space/description=''">(<xsl:value-of select="display:get('prm.notification.none')"/>)</xsl:if>
<xsl:text>&#xa;</xsl:text>
<xsl:value-of select="display:get('prm.notification.roster.registered_space_invite.responsibilities')"/> <xsl:value-of disable-output-escaping="yes" select="TeamMemberWizard/inviteeResponsibilities"/><xsl:if test="TeamMemberWizard/inviteeResponsibilities=''">(<xsl:value-of select="display:get('prm.notification.none')"/>)</xsl:if>
<xsl:text>&#xa;</xsl:text>
<xsl:value-of select="display:get('prm.notification.roster.registered_space_invite.message')"/> <xsl:value-of disable-output-escaping="yes" select="TeamMemberWizard/inviteeMessage"/><xsl:if test="TeamMemberWizard/inviteeMessage=''">(<xsl:value-of select="display:get('prm.notification.none')"/>)</xsl:if>
<xsl:text>&#xa;</xsl:text><xsl:text>&#xa;</xsl:text>

<xsl:value-of select="display:get('prm.notification.roster.registered_space_invite.workspace_visit_click')"/>
<xsl:text>&#xa;</xsl:text>
		<xsl:value-of select="url:formatAppURL(spaceUrl)"/>	
<xsl:text>&#xa;</xsl:text><xsl:text>&#xa;</xsl:text><xsl:text>&#xa;</xsl:text>

<xsl:value-of select="display:get('prm.notification.roster.registered_space_invite.questions_contact_help')"/> <xsl:value-of select="url:formatAppURL('/help/HelpDesk.jsp')"/>
<xsl:text>&#xa;</xsl:text><xsl:text>&#xa;</xsl:text>

</xsl:template>
</xsl:stylesheet>
