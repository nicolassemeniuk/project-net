<?xml version="1.0"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:display="xalan://net.project.base.property.PropertyProvider" extension-element-prefixes="display" >

<!--=====================================================================
|
|    $RCSfile$
|   $Revision: 10492 $
|       $Date: 2003-02-07 22:14:14 -0300 (vie, 07 feb 2003) $
|     $Author: matt $
|
| Displays a workflow error (exception)
======================================================================-->

<xsl:output method="html"/>

<xsl:template match="/">
    <xsl:apply-templates select="//workflow_exception" />
</xsl:template>

<xsl:template match="workflow_exception">
    <xsl:call-template name="display_error">
    	<xsl:with-param name="code" select="error_code" />
    	<xsl:with-param name="default" select="default_message" />
    </xsl:call-template>
</xsl:template>

<xsl:template name="display_error">
    <xsl:param name="code">0</xsl:param>
    <xsl:param name="default"><xsl:value-of select="display:get('prm.workflow.errors.nodescription.message')"/></xsl:param>
<xsl:choose>

<!-- Insert code handling here for example:
<xsl:when test="$code=999">
	Error number 999 occurred
</xsl:when>
-->

<xsl:when test="$code=100">
<xsl:value-of disable-output-escaping="yes" select="display:get('prm.workflow.errors.workflowmodified.message')"/>
</xsl:when>
<xsl:when test="$code=101">
<xsl:value-of disable-output-escaping="yes" select="display:get('prm.workflow.errors.workflowlocked.message')"/>
</xsl:when>
<xsl:when test="$code=200">
<xsl:value-of disable-output-escaping="yes" select="display:get('prm.workflow.errors.stepmodified.message')"/>
</xsl:when>
<xsl:when test="$code=201">
<xsl:value-of disable-output-escaping="yes" select="display:get('prm.workflow.errors.steplocked.message')"/>
</xsl:when>
<xsl:when test="$code=250">
<xsl:value-of disable-output-escaping="yes" select="display:get('prm.workflow.errors.stepmodified.message')"/>
</xsl:when>
<xsl:when test="$code=300">
<xsl:value-of disable-output-escaping="yes" select="display:get('prm.workflow.errors.transitionmodified.message')"/>
</xsl:when>
<xsl:when test="$code=301">
<xsl:value-of disable-output-escaping="yes" select="display:get('prm.workflow.errors.transitionlocked.message')"/>
</xsl:when>
<xsl:when test="$code=400">
<xsl:value-of disable-output-escaping="yes" select="display:get('prm.workflow.errors.rulemodified.message')"/>
</xsl:when>
<xsl:when test="$code=1000">
<xsl:value-of disable-output-escaping="yes" select="display:get('prm.workflow.errors.transitionproblem.clickreturn.message')"/>
</xsl:when>
<xsl:when test="$code=1001">
<xsl:value-of disable-output-escaping="yes" select="display:get('prm.workflow.errors.transitionproblem.clickreturn.message')"/>
</xsl:when>
<xsl:when test="$code=1002">
<xsl:value-of disable-output-escaping="yes" select="display:get('prm.workflow.errors.transitionproblem.envelopenotstored.message')"/>
</xsl:when>
<xsl:when test="$code=1003">
<xsl:value-of disable-output-escaping="yes" select="display:get('prm.workflow.errors.transitionproblem.historynotstored.message')"/>
</xsl:when>
<xsl:when test="$code=1004">
<xsl:value-of disable-output-escaping="yes" select="display:get('prm.workflow.errors.transitionproblem.rulesnotdetermined.message')"/>
</xsl:when>
<xsl:when test="$code=1005">
<xsl:value-of disable-output-escaping="yes" select="display:get('prm.workflow.errors.transition.notificationnotsent.message')"/>
</xsl:when>
<xsl:when test="$code=1500">
<xsl:value-of select="display:get('prm.workflow.errors.objectnotadded.message')"/>
</xsl:when>

<!-- End of error code handling -->

<xsl:otherwise>
<xsl:value-of disable-output-escaping="yes" select="display:get('prm.workflow.errors.problem.message')"/>
	<xsl:value-of select="$default" />
</xsl:otherwise>
</xsl:choose>
</xsl:template>

</xsl:stylesheet>
