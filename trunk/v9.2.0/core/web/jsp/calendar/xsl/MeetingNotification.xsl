<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:display="xalan://net.project.base.property.PropertyProvider"
	xmlns:format="xalan://net.project.util.XSLFormat"
    xmlns:url="xalan://net.project.notification.XSLFormatNotificationURL"
	extension-element-prefixes="format url" >
<xsl:output method="text"/> 
<xsl:template match="/">

<xsl:value-of select="display:get('prm.notification.dear')"/> <xsl:value-of select="MeetingInvite/attendee/name"/>,
<xsl:text>&#xa;</xsl:text><xsl:text>&#xa;</xsl:text>

<xsl:value-of select="display:get('prm.notification.calendar.meeting.invited_to_by')"/> <xsl:value-of select="MeetingInvite/host/person/full_name"/>.
<xsl:text>&#xa;</xsl:text><xsl:text>&#xa;</xsl:text>

<xsl:value-of select="display:get('prm.notification.calendar.meeting.name')"/> <xsl:value-of disable-output-escaping="yes" select="MeetingInvite/meeting/name"/><xsl:text>&#xa;</xsl:text>
<xsl:value-of select="display:get('prm.notification.calendar.meeting.purpose')"/> <xsl:value-of disable-output-escaping="yes" select="MeetingInvite/meeting/purpose"/><xsl:if test="MeetingInvite/meeting/purpose[.='']">(<xsl:value-of select="display:get('prm.notification.none')"/>)</xsl:if><xsl:text>&#xa;</xsl:text>
<xsl:value-of select="display:get('prm.notification.calendar.meeting.time')"/> <xsl:value-of select="format:formatISODateTime(MeetingInvite/inviteeDateTime)"/><xsl:text>&#xa;</xsl:text>
<xsl:value-of select="display:get('prm.notification.calendar.meeting.location')"/> <xsl:value-of disable-output-escaping="yes" select="MeetingInvite/meeting/facility/description"/><xsl:if test="MeetingInvite/meeting/facility/description[.='']">(<xsl:value-of select="display:get('prm.notification.none')"/>)</xsl:if>
<xsl:text>&#xa;</xsl:text><xsl:text>&#xa;</xsl:text>

<xsl:value-of select="display:get('prm.notification.calendar.meeting.inviters_comments')"/> <xsl:value-of disable-output-escaping="yes" select="MeetingInvite/attendee/comment"/><xsl:text>&#xa;</xsl:text>
<xsl:value-of select="display:get('prm.notification.calendar.meeting.invitees')"/> <xsl:value-of disable-output-escaping="yes" select="MeetingInvite/meeting/AttendeesList/attendeesCSV"/>
<xsl:text>&#xa;</xsl:text><xsl:text>&#xa;</xsl:text>
        
<xsl:value-of select="display:get('prm.notification.calendar.meeting.further_details')"/>
<xsl:text>&#xa;</xsl:text><xsl:text>&#xa;</xsl:text>
<!-- Meeting URL: <xsl:value-of disable-output-escaping="yes" select="url:formatSiteURL(MeetingInvite/meetingurl)"/> -->
        
        
<xsl:value-of select="display:get('prm.notification.calendar.meeting.contact_inviter')"/> <xsl:value-of select="MeetingInvite/host/person/full_name"/>.<xsl:text>&#xa;</xsl:text>  
<xsl:value-of select="display:get('prm.notification.calendar.meeting.questions_contact_help')"/> <xsl:value-of select="url:formatAppURL('/help/HelpDesk.jsp')"/>.

</xsl:template>
</xsl:stylesheet>

