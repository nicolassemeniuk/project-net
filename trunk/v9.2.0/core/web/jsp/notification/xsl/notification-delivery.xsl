<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:display="xalan://net.project.base.property.PropertyProvider"
    xmlns:url="xalan://net.project.notification.XSLFormatNotificationURL"
    extension-element-prefixes="display" >

<xsl:output method="text" />

<xsl:template match="/">
    <xsl:apply-templates select="notification_deliverable"/>
</xsl:template>

<xsl:template match="notification_deliverable">
<!-- START OF MESSAGE -->
<xsl:value-of select="display:get('prm.notification.dear')"/> <xsl:value-of select="delivery_info/recipient_name" />,
<xsl:text>&#xa;</xsl:text><xsl:text>&#xa;</xsl:text>

<xsl:apply-templates select="notification_collection" />
------------------------------------------------------------
<xsl:text>&#xa;</xsl:text>
<xsl:value-of select="display:get('prm.notification.delivery.info')"/><xsl:text>&#xa;</xsl:text>
<xsl:value-of select="url:getAppURL()" />
<xsl:text>&#xa;</xsl:text><xsl:text>&#xa;</xsl:text>

<xsl:value-of select="display:get('prm.notification.delivery.questions_contact_help')"/> <xsl:value-of select="url:formatAppURL('/help/HelpDesk.jsp')"/>
<xsl:text>&#xa;</xsl:text>
</xsl:template>

<xsl:template match="notification_collection">
    <xsl:apply-templates select="notification"/>
</xsl:template>

<xsl:template match="notification">
<!-- One match for each notification in the email -->
<xsl:apply-templates select="notification_type" /> <xsl:apply-templates select="initiator" />
    <xsl:text>
    </xsl:text>
    <xsl:choose>
        <xsl:when test="Space/SpaceName">
			<xsl:text>&#xa;</xsl:text>
			<xsl:value-of select="display:get('prm.notification.delivery.workspace')"/><xsl:value-of select="./Space/SpaceName" />
			<xsl:text>&#xa;</xsl:text>
        </xsl:when>
    </xsl:choose>
    <xsl:apply-templates select="target_object_xml" />
		<xsl:text>&#xa;</xsl:text>    
		<xsl:value-of select="display:get('prm.notification.delivery.default_message')"/><xsl:value-of disable-output-escaping="yes" select="custom_message"/><xsl:if test="custom_message=''">(<xsl:value-of select="display:get('prm.notification.none')"/>)</xsl:if>
		<xsl:text>&#xa;</xsl:text>

<xsl:text>&#xa;</xsl:text>
<xsl:value-of select="display:get('prm.notification.delivery.login_to_review')"/><xsl:value-of select="url:getAppURL()"/>
<xsl:text>&#xa;</xsl:text>
<xsl:text>

</xsl:text>
</xsl:template>

<xsl:template match="notification_type">
    <xsl:value-of select="display:get(default_message)"/>
</xsl:template>

<xsl:template match="initiator"><xsl:value-of select="display:get('prm.notification.delivery.by')"/><xsl:value-of select="name" /> (<xsl:value-of select="display:get('prm.notification.delivery.mailto')"/><xsl:value-of select="email_address" />).<xsl:text>&#xa;</xsl:text></xsl:template>

<xsl:template match="target_object_xml">
    <xsl:apply-templates />
</xsl:template>

<xsl:template match="document">
<xsl:text>&#xa;</xsl:text>
<xsl:value-of select="display:get('prm.notification.delivery.path')"/><xsl:apply-templates select="document_properties/path" /><xsl:text>&#xa;</xsl:text>
<xsl:value-of select="display:get('prm.notification.delivery.document_name')"/><xsl:value-of select="document_properties/name" /><xsl:text>&#xa;</xsl:text>
<xsl:value-of select="display:get('prm.notification.delivery.description')"/><xsl:value-of select="document_properties/description" /><xsl:text>&#xa;</xsl:text>
<xsl:value-of select="display:get('prm.notification.delivery.author')"/><xsl:value-of select="document_properties/author" /><xsl:text>&#xa;</xsl:text>
<xsl:value-of select="display:get('prm.notification.delivery.url')"/><xsl:value-of select="url:getSiteURL()" /><xsl:value-of select="document_properties/url" /><xsl:text><xsl:text>&#xa;</xsl:text>
</xsl:text>
</xsl:template>
<xsl:template match="document/document_properties/path"><xsl:value-of select="display:get('@prm.document.container.topfolder.name')" /><xsl:apply-templates select="node"><xsl:sort select="level" /></xsl:apply-templates></xsl:template>
<xsl:template match="document/document_properties/path/node"><![CDATA[ > ]]><xsl:value-of select="name" /></xsl:template>

<xsl:template match="envelope"><xsl:text>
</xsl:text>
<xsl:text>&#xa;</xsl:text>
<xsl:value-of select="display:get('prm.notification.delivery.envelope_details')"/><xsl:text>&#xa;</xsl:text>
============================================================
<xsl:text>&#xa;</xsl:text>
<xsl:value-of select="display:get('prm.notification.delivery.envelope_name')"/><xsl:value-of select="name" />
<xsl:value-of select="display:get('prm.notification.delivery.description')"/><xsl:value-of select="description" />
<xsl:apply-templates select="current_version" />

<xsl:text>&#xa;</xsl:text><xsl:text>&#xa;</xsl:text>
<xsl:value-of select="display:get('prm.notification.delivery.last_comments')"/><xsl:text>&#xa;</xsl:text>
============================================================
<xsl:text>&#xa;</xsl:text>
<xsl:apply-templates select="envelope_version_list" /></xsl:template>

<xsl:template match="current_version">
<xsl:text>&#xa;</xsl:text>
<xsl:value-of select="display:get('prm.notification.delivery.status')"/><xsl:value-of select="status_name" /><xsl:text>&#xa;</xsl:text>
<xsl:value-of select="display:get('prm.notification.delivery.current_step')"/><xsl:value-of select="step_name" />
<xsl:text>&#xa;</xsl:text><xsl:text>&#xa;</xsl:text>

<xsl:value-of select="display:get('prm.notification.delivery.instructions')"/><xsl:text>&#xa;</xsl:text>
<xsl:value-of select="step_notes" />
</xsl:template>

<xsl:template match="envelope/envelope_version_list"><xsl:apply-templates select="envelope_version[position()>last()-5]" /></xsl:template>

<xsl:template match="envelope/envelope_version_list/envelope_version">
<xsl:value-of select="created_datetime" /> - <xsl:value-of select="created_by_full_name" /><xsl:text>
</xsl:text>

<xsl:choose>
	<xsl:text>&#xa;</xsl:text>
    <xsl:when test="transition_verb=''"><xsl:value-of select="display:get('prm.notification.delivery.created_at_step')"/><xsl:value-of select="step_name" /></xsl:when>
    <xsl:otherwise><xsl:value-of select="display:get('prm.notification.delivery.transitioned_using')"/><xsl:apply-templates select="transition_verb" /><xsl:value-of select="display:get('prm.notification.delivery.to_step')"/><xsl:value-of select="step_name" /></xsl:otherwise>
</xsl:choose>
<xsl:text>
</xsl:text>
<xsl:value-of select="comments" />
<xsl:if test="position()!=last()">
------------------------------------------------------------
</xsl:if>
</xsl:template>

<xsl:template match="container">
<xsl:text>&#xa;</xsl:text>
<xsl:value-of select="display:get('prm.notification.delivery.folder_name')"/><xsl:value-of select="container_properties/name" /><xsl:text>&#xa;</xsl:text>
<xsl:value-of select="display:get('prm.notification.delivery.full_path')"/><xsl:apply-templates select="container_properties/path" /><xsl:text>&#xa;</xsl:text>
</xsl:template>
<xsl:template match="container/container_properties/path"><xsl:value-of select="display:get('@prm.document.container.topfolder.name')" /><xsl:apply-templates select="node"><xsl:sort select="level" /></xsl:apply-templates><![CDATA[ > ]]><xsl:value-of select="../name" /></xsl:template>
<xsl:template match="container/container_properties/path/node"><![CDATA[ > ]]><xsl:value-of select="name" /></xsl:template>

<xsl:template match="news">
<xsl:text>&#xa;</xsl:text>
<xsl:value-of select="display:get('prm.notification.delivery.news_item')"/><xsl:value-of select="topic" /><xsl:text>&#xa;</xsl:text>
</xsl:template>

<xsl:template match="DiscussionGroup">
<xsl:text>&#xa;</xsl:text>
<xsl:value-of select="display:get('prm.notification.delivery.discussion_group_name')"/><xsl:value-of select="discussion_group_name" /><xsl:text>&#xa;</xsl:text>
<xsl:value-of select="display:get('prm.notification.delivery.charter')"/><xsl:value-of select="charter" /><xsl:text>&#xa;</xsl:text>
</xsl:template>

<xsl:template match="summary">
<xsl:text>&#xa;</xsl:text>
<xsl:value-of select="display:get('prm.notification.delivery.task_name')"/><xsl:value-of select="name" /><xsl:text>&#xa;</xsl:text>
<xsl:value-of select="display:get('prm.notification.delivery.priority')"/><xsl:value-of select="priorityString" /><xsl:text>&#xa;</xsl:text>
<xsl:value-of select="display:get('prm.notification.delivery.status2')"/><xsl:value-of select="status" /><xsl:text>&#xa;</xsl:text>
<xsl:value-of select="display:get('prm.notification.delivery.start_date')"/><xsl:value-of select="startDate" /><xsl:text>&#xa;</xsl:text>
<xsl:value-of select="display:get('prm.notification.delivery.end_date')"/><xsl:value-of select="endDate" /><xsl:text>&#xa;</xsl:text>
</xsl:template>

<xsl:template match="task">
<xsl:text>&#xa;</xsl:text>
<xsl:value-of select="display:get('prm.notification.delivery.task_name')"/><xsl:value-of select="name" /><xsl:text>&#xa;</xsl:text>
<xsl:value-of select="display:get('prm.notification.delivery.priority')"/><xsl:value-of select="priorityString" /><xsl:text>&#xa;</xsl:text>
<xsl:value-of select="display:get('prm.notification.delivery.status2')"/><xsl:value-of select="status" /><xsl:text>&#xa;</xsl:text>
<xsl:value-of select="display:get('prm.notification.delivery.start_date')"/><xsl:value-of select="startDate" /><xsl:text>&#xa;</xsl:text>
<xsl:value-of select="display:get('prm.notification.delivery.end_date')"/><xsl:value-of select="endDate" /><xsl:text>&#xa;</xsl:text>
<xsl:value-of select="display:get('prm.notification.delivery.url2')"/>
<xsl:choose>
	<xsl:when test="url">
		<xsl:value-of select="url:getSiteURL()" /><xsl:value-of select="url" /><xsl:text>&#xa;</xsl:text>
	</xsl:when>
</xsl:choose>

</xsl:template>

<xsl:template match="milestone">
<xsl:text>&#xa;</xsl:text>
<xsl:value-of select="display:get('prm.notification.delivery.task_name')"/><xsl:value-of select="name" /><xsl:text>&#xa;</xsl:text>
<xsl:value-of select="display:get('prm.notification.delivery.priority')"/><xsl:value-of select="priorityString" /><xsl:text>&#xa;</xsl:text>
<xsl:value-of select="display:get('prm.notification.delivery.status2')"/><xsl:value-of select="status" /><xsl:text>&#xa;</xsl:text>
<xsl:value-of select="display:get('prm.notification.delivery.start_date')"/><xsl:value-of select="startDate" /><xsl:text>&#xa;</xsl:text>
<xsl:value-of select="display:get('prm.notification.delivery.end_date')"/><xsl:value-of select="endDate" /><xsl:text>&#xa;</xsl:text>
</xsl:template>

<xsl:template match="FormData">
<xsl:text>&#xa;</xsl:text>
<xsl:value-of select="display:get('prm.notification.delivery.form_name')"/><xsl:value-of select="FormName" /><xsl:text>&#xa;</xsl:text>
<xsl:value-of select="display:get('prm.notification.delivery.row_id')"/><xsl:value-of select="rowID" /><xsl:text>&#xa;</xsl:text>
<xsl:value-of select="display:get('prm.notification.delivery.url')"/><xsl:value-of select="url:getSiteURL()" /><xsl:value-of select="url" /><xsl:text>&#xa;</xsl:text>
</xsl:template>

<xsl:template match="post">
<xsl:text>&#xa;</xsl:text>
<xsl:value-of select="display:get('prm.notification.delivery.discussion_group_name2')"/><xsl:value-of select="groupName" /><xsl:text>&#xa;</xsl:text>
<xsl:value-of select="display:get('prm.notification.delivery.posted_for')"/><xsl:value-of select="subject" /><xsl:text>&#xa;</xsl:text>
<xsl:value-of select="display:get('prm.notification.delivery.posted_by')"/><xsl:value-of select="person" /><xsl:text>&#xa;</xsl:text>
</xsl:template>

<xsl:template match="Form">
<xsl:text>&#xa;</xsl:text>
<xsl:value-of select="display:get('prm.notification.delivery.form_name')"/><xsl:value-of select="name" /><xsl:text>&#xa;</xsl:text>
<xsl:value-of select="display:get('prm.notification.delivery.description')"/><xsl:value-of select="description" />
</xsl:template>

<xsl:template match="group">
<xsl:text>&#xa;</xsl:text>
<xsl:value-of select="display:get('prm.notification.delivery.role_name')"/><xsl:value-of select="group_name" /><xsl:text>&#xa;</xsl:text>
<xsl:value-of select="display:get('prm.notification.delivery.description')"/><xsl:value-of select="group_desc" />
</xsl:template>

<xsl:template match="Blog">
<xsl:text>&#xa;</xsl:text>
<xsl:value-of select="display:get('prm.notification.delivery.blog_name')"/><xsl:value-of select="BlogName" /><xsl:text>&#xa;</xsl:text>
<xsl:value-of select="display:get('prm.notification.delivery.blog_url')"/><xsl:value-of select="url:getSiteURL()" /><xsl:value-of select="BlogUrl" />
</xsl:template>

<xsl:template match="Wiki">
<xsl:text>&#xa;</xsl:text>
<xsl:value-of select="display:get('prm.notification.delivery.wiki_page_name')"/><xsl:value-of select="WikiPageName" /><xsl:text>&#xa;</xsl:text>
<xsl:value-of select="display:get('prm.notification.delivery.wiki_page_url')"/><xsl:value-of select="url:getSiteURL()" /><xsl:value-of select="WikiPageUrl" />
</xsl:template>

<xsl:template match="Project">
<xsl:text>&#xa;</xsl:text>
<xsl:value-of select="display:get('prm.notification.delivery.project_name')"/><xsl:value-of select="ProjectName" /><xsl:text>&#xa;</xsl:text>
<xsl:value-of select="display:get('prm.notification.delivery.project_url')"/><xsl:value-of select="url:getSiteURL()" /><xsl:value-of select="ProjectUrl" />
</xsl:template>

<xsl:template match="Participant">
<xsl:text>&#xa;</xsl:text>
<xsl:value-of select="display:get('prm.notification.delivery.participant_name')"/><xsl:value-of select="Name" /><xsl:text>&#xa;</xsl:text>
<xsl:value-of select="display:get('prm.notification.delivery.participant_url')"/><xsl:value-of select="url:getSiteURL()" /><xsl:value-of select="URL" />
</xsl:template>

</xsl:stylesheet>
