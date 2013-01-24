<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:display="xalan://net.project.base.property.PropertyProvider"
	xmlns:format="xalan://net.project.util.XSLFormat"
    xmlns:url="xalan://net.project.notification.XSLFormatNotificationURL"
        extension-element-prefixes="display format url" >
	
<xsl:output method="text"/>
<xsl:template match="/">
<xsl:value-of select="display:get('prm.notification.dear')"/> 
<xsl:value-of select="assignment-notification/assigneeName"/>,

<xsl:value-of select="display:get('prm.notification.assignment.assignment_task_by')"/> 
<xsl:value-of select="assignment-notification/user/first-name"/>
<xsl:text> </xsl:text>
<xsl:value-of select="assignment-notification/user/last-name"/>.

<xsl:value-of select="display:get('prm.notification.assignment.project_name')"/>
<xsl:value-of disable-output-escaping="yes" select="assignment-notification/project_name"/>
<xsl:text>&#xa;</xsl:text> 

<xsl:value-of select="display:get('prm.notification.assignment.task_name')"/>
<xsl:value-of disable-output-escaping="yes" select="assignment-notification//name"/>
<xsl:text>&#xa;</xsl:text>

<xsl:value-of select="display:get('prm.notification.assignment.description')"/>
<xsl:value-of disable-output-escaping="yes" select="assignment-notification//description"/> 
<xsl:if test="assignment-notification//description[.='']"> 
	<xsl:value-of select="display:get('prm.notification.none')"/>
</xsl:if>
<xsl:text>&#xa;</xsl:text>

<xsl:value-of select="display:get('prm.notification.assignment.planned_start')"/>
<xsl:value-of select="format:formatISODate(assignment-notification/assigneeStartDate)"/> 
<xsl:if test="assignment-notification/assigneeStartDate[.='']">
	(<xsl:value-of select="display:get('prm.notification.none')"/>)
</xsl:if>
<xsl:text>&#xa;</xsl:text>

<xsl:value-of select="display:get('prm.notification.assignment.planned_finish')"/>
<xsl:value-of select="format:formatISODate(assignment-notification/assigneeEndDate)"/>
<xsl:if test="assignment-notification/assigneeEndDate[.='']">
	(<xsl:value-of select="display:get('prm.notification.none')"/>)
</xsl:if>
<xsl:text>&#xa;</xsl:text>

<xsl:value-of select="display:get('prm.notification.assignment.priority')"/>
<xsl:value-of select="assignment-notification//priorityString"/>
<xsl:text>&#xa;</xsl:text><xsl:text>&#xa;</xsl:text>

<xsl:value-of select="display:get('prm.notification.assignment.task_apper_on_personal_page')"/>
<xsl:text>&#xa;</xsl:text><xsl:text>&#xa;</xsl:text>  

<xsl:value-of select="display:get('prm.notification.assignment.questions_contact_help')"/> 
<xsl:value-of select="url:formatAppURL('/help/HelpDesk.jsp')"/>.

</xsl:template>
</xsl:stylesheet>