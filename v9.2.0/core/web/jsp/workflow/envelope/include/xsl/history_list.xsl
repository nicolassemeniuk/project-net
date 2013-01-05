<?xml version="1.0"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:display="xalan://net.project.base.property.PropertyProvider"
        extension-element-prefixes="display" >

<xsl:output method="html"/>

<xsl:template match="*|/"><xsl:apply-templates/></xsl:template>

<xsl:template match="text()|@*"><xsl:value-of select="."/></xsl:template>

<xsl:template match="history_list">
<table align="left" border="0" cellspacing="0" cellpadding="0" width="100%">
  <tr class="tableHeader" align="left">
		<th class="tableHeader"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></th>
		<th class="tableHeader"><xsl:value-of select="display:get('prm.workflow.envelope.include.historylist.action.label')"/></th>
		<th class="tableHeader"><xsl:value-of select="display:get('prm.workflow.envelope.include.historylist.details.label')"/></th>
		<th class="tableHeader"><xsl:value-of select="display:get('prm.workflow.envelope.include.historylist.on.label')"/></th>
		<th class="tableHeader"><xsl:value-of select="display:get('prm.workflow.envelope.include.historylist.by.label')"/></th>
		<th class="tableHeader"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></th>
	</tr>
	<tr class="tableLine">
		<td colspan="6" class="tableLine"><img src="../images/spacers/trans.gif" width="1" height="2" border="0" alt=""/></td>
	</tr>
	<!-- Insert history line items or a message if there are none -->
	<xsl:apply-templates select="history" />
	<xsl:if test="count(history)=0"><xsl:call-template name="no_history" /></xsl:if>
</table>
</xsl:template>

<xsl:template match="history">
<tr class="tableContent" align="left"> 
  <td class="tableContent"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
  <td class="tableContent"><xsl:apply-templates select="action_name"/></td>
  <td class="tableContent">
    <xsl:apply-templates select="history_message" />
	</td>
  <td class="tableContent"><xsl:apply-templates select="action_datetime" /></td>
  <td class="tableContent"><xsl:apply-templates select="action_by_full_name" /></td>
  <td class="tableContent"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
</tr>
<tr class="tableLine">
  <td colspan="6" class="tableLine"><img src="../images/spacers/trans.gif" width="1" height="1" border="0" alt=""/></td>
</tr></xsl:template>

<xsl:template name="no_history">
<tr class="tableContent" align="left"> 
  <td class="tableContent"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
  <td class="tableContent" colspan="2"><xsl:value-of select="display:get('prm.workflow.envelope.include.historylist.nohistory.error.message')"/></td>
  <td class="tableContent"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
</tr></xsl:template>

<xsl:template match="action_name"><xsl:apply-templates/></xsl:template>

<xsl:template match="action_datetime"><xsl:apply-templates/></xsl:template>

<xsl:template match="history_message">  <!-- Displays history message xml -->
  <xsl:choose>
    <xsl:when test="../history_action_id='100'">
      <xsl:call-template name="entered_step"><xsl:with-param name="message" select="." /></xsl:call-template>
	  </xsl:when>
	  <xsl:when test="../history_action_id='200'">
      <xsl:call-template name="performed_transition"><xsl:with-param name="message" select="." /></xsl:call-template>
	  </xsl:when>
	  <xsl:when test="../history_action_id='300'">
	    <xsl:call-template name="sent_notification"><xsl:with-param name="message" select="." /></xsl:call-template>
	  </xsl:when>
	  <xsl:otherwise>
          <xsl:value-of select="display:get('prm.workflow.envelope.include.historylist.nodetails.error.message')"/>
	  </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template name="entered_step">
  <!-- Called when the action is 100, assumes history_message contains a step -->
  <xsl:param name="message" />
  <xsl:variable name="step" select="$message/step" />
  <xsl:value-of select="$step/name" /> - <xsl:value-of select="$step/description" />
</xsl:template>

<xsl:template name="performed_transition">
  <!-- Called when the action is 200, assumes history_message contains a transition -->
  <xsl:param name="message" />
  <xsl:variable name="transition" select="$message/transition" />
  <xsl:value-of select="$transition/transition_verb" /> - <xsl:value-of select="$transition/description" />

</xsl:template>

<xsl:template name="sent_notification">
  <!-- Called when the action is 300, assumes history_message contains an event -->
  <xsl:param name="message" />
  <xsl:variable name="subscription" select="$message/subscription" />
  <xsl:value-of select="display:get('prm.workflow.envelope.include.historylist.event.label')" /> - <xsl:value-of select="$subscription/description" />

</xsl:template>

</xsl:stylesheet>
