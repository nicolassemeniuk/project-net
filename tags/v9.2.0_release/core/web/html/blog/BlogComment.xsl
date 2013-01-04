<?xml version="1.0"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:display="xalan://net.project.base.property.PropertyProvider"
	xmlns:format="xalan://net.project.util.XSLFormat"
	xmlns:url="xalan://net.project.notification.XSLFormatNotificationURL"
	extension-element-prefixes="display format url">

	<xsl:output method="html" />
	<xsl:template match="/">
	<xsl:variable name="owner" select="CommentNotification/blogOwner" />
		<xsl:value-of select="display:get('prm.notification.dear')"/>  <xsl:value-of select="CommentNotification/personName" />, <br/><br/>
		<xsl:choose>
		<xsl:when test="$owner = 'true'">
			<span style="color: red; "><xsl:value-of select="display:get('prm.blog.viewblog.addacomment.link')"/> has been posted on your blog: </span><br/>
		</xsl:when>
		<xsl:otherwise>
			<span style="color: blue; "><xsl:value-of select="display:get('prm.blog.viewblog.addacomment.link')"/> posted on blog that you commented: </span><br/>
		</xsl:otherwise>
		</xsl:choose>
		
		<span style="color: blue; ">On Project : <xsl:value-of select="CommentNotification//blogProject" /></span><br/>
		<br/>
		<span style="color: blue; ">Comment <xsl:value-of select="display:get('prm.blog.viewblog.entrypostedby.label')"/>: <xsl:value-of select="CommentNotification//personByCommentPosted" /></span><br/>
		<br/>
		<span style="color: red; ">---------------- comment start ------------------</span><br/>
		<xsl:value-of disable-output-escaping="yes" select="CommentNotification//commentPosted" /><br/>
		<span style="color: red; ">---------------- comment end ------------------</span><br/><br/>

		<span style="color: red; ">---------------- blog details start -------------------</span><br/>
		<xsl:value-of select="display:get('prm.global.nav.blog.title')"/>: <xsl:value-of select="CommentNotification//blogEntryTitle" /><br/><br/>
		<xsl:value-of disable-output-escaping="yes" select="CommentNotification//blogContent" /><br/>
		<span style="color: red; ">----------------- blog details end --------------------</span><br/>
		
		<span style="color: red; "><xsl:value-of select="display:get('prm.blog.viewblog.blogentryurl.label')"/> </span><br/><br/>
		<xsl:value-of disable-output-escaping="yes" select="CommentNotification//blogEntryUrl" /><br/>
		
		<br/>
		<br/>
		<xsl:value-of select="display:get('prm.notification.delivery.questions_contact_help')"/> - <xsl:value-of select="url:formatAppURL('/help/HelpDesk.jsp')"/>

	</xsl:template>
</xsl:stylesheet>