<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:display="xalan://net.project.base.property.PropertyProvider"
    xmlns:url="xalan://net.project.notification.XSLFormatNotificationURL"
    extension-element-prefixes="display url" >
	
<xsl:output method="text"/>
<xsl:template match="/">
<xsl:value-of select="display:get('prm.notification.dear')"/> <xsl:value-of select="ParticipantDelete/DeletedMember/person/full_name"/>,
<xsl:text>&#xa;</xsl:text><xsl:text>&#xa;</xsl:text>

<xsl:value-of select="display:get('prm.notification.roster.participant_delete.removed_from')"/> <xsl:value-of disable-output-escaping="yes" select="ParticipantDelete/Space/SpaceType/name"/> <xsl:value-of select="display:get('prm.notification.roster.participant_delete.workspace_within_application')"/>
<xsl:text>&#xa;</xsl:text><xsl:text>&#xa;</xsl:text>

<xsl:value-of disable-output-escaping="yes" select="ParticipantDelete/Space/SpaceType/name"/> <xsl:value-of select="display:get('prm.notification.roster.participant_delete.workspace_name')"/> <xsl:value-of disable-output-escaping="yes" select="ParticipantDelete/Space/name"/>
<xsl:text>&#xa;</xsl:text>
<xsl:value-of select="display:get('prm.notification.roster.participant_delete.removed_by')"/> <xsl:value-of select="ParticipantDelete/Administrator/user/first-name"/><xsl:text> </xsl:text><xsl:value-of select="ParticipantDelete/Administrator/user/last-name"/>
<xsl:text>&#xa;</xsl:text><xsl:text>&#xa;</xsl:text>

<xsl:value-of select="display:get('prm.notification.roster.participant_delete.no_access_workspace')"/>
<xsl:text>&#xa;</xsl:text><xsl:text>&#xa;</xsl:text>  

<xsl:value-of select="display:get('prm.notification.roster.participant_delete.questions_about_delete')"/> <xsl:value-of disable-output-escaping="yes" select="ParticipantDelete/Space/SpaceType/name"/><xsl:value-of select="display:get('prm.notification.roster.participant_delete.contact_sender')"/>
<xsl:text>&#xa;</xsl:text> 
<xsl:value-of select="display:get('prm.notification.roster.participant_delete.questions_contact_help')"/> <xsl:value-of select="url:formatAppURL('/help/HelpDesk.jsp')"/>
<xsl:text>&#xa;</xsl:text>

</xsl:template>
</xsl:stylesheet>
