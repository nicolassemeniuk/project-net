<?xml version="1.0"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
xmlns:display="xalan://net.project.base.property.PropertyProvider"
extension-element-prefixes="display" >

<xsl:output method="html"/>

<xsl:template match="*|/"><xsl:apply-templates/></xsl:template>

<xsl:template match="text()|@*"><xsl:value-of select="."/></xsl:template>

<xsl:template match="rule_problem_list">
<!-- Grab the errors and warnings into variables for ease of use -->
<xsl:variable name="errors" select="rule_problem[is_error='1']" />
<xsl:variable name="warnings" select="rule_problem[is_error='0']" />

<table width="100%" border="0" cellpadding="0" cellspacing="0">
	<!-- If there are any errors -->
	<xsl:if test="$errors">
		<tr class="tableHeader" align="left">
			<th><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></th>
			<th colspan="2"><xsl:value-of select="display:get('prm.workflow.envelope.transitionproblem.errors.label')"/></th>
			<th><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></th>
		</tr>
		<!-- insert errors, one per row -->
		<xsl:apply-templates select="$errors" />
		<!-- Insert space if we're going to have warnings -->
		<xsl:if test="$warnings"><tr><td colspan="4"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td></tr></xsl:if>
	</xsl:if>

	<!-- If there are any warnings -->
	<xsl:if test="$warnings">
		<tr class="tableHeader" align="left">
			<th><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></th>
			<th colspan="2"><xsl:value-of select="display:get('prm.workflow.envelope.transitionproblem.warnings.label')"/></th>
			<th><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></th>
		</tr>
		<!-- insert warnings, one per row -->
		<xsl:apply-templates select="$warnings" />
	</xsl:if>

	<tr><td colspan="4"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td></tr>
	<tr>
	  <td class="actionBar" width="1%"><img src="{./jsp_root_url}/images/icons/actionbar-left_end.gif" width="8" height="27" alt="" border="0"/></td>
	  <td class="actionBar" align="right">
	  	<xsl:if test="not($errors)">
       		<a href="javascript:continueTransition();" class="channelNoUnderline"><xsl:value-of select="display:get('prm.workflow.envelope.transitionproblem.continue.button.label')"/><img src="{./jsp_root_url}/images/icons/actionbar-submit_off.gif" width="27" height="27" alt="" border="0" align="absmiddle"/></a>
    		<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
		</xsl:if>
       <a href="javascript:cancelTransition();" class="channelNoUnderline" ><xsl:value-of select="display:get('prm.workflow.envelope.transitionproblem.cancel.button.label')"/><img src="{./jsp_root_url}/images/icons/actionbar-cancel_off.gif" width="27" height="27" alt="" border="0" align="absmiddle"/></a>
	  </td>
  	  <td class="actionBar" align="right" width="1%"><img src="{./jsp_root_url}/images/icons/actionbar-right_end.gif" width="8" height="27" alt="" border="0"/></td>
  </tr>

	<!-- Insert buttons - ->
	<xsl:if test="not($errors)">
		<tr class="tableContent">
			<td><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
			<td align="right">Ignore warnings and continue<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
			<td align="left"><a href="javascript:continueTransition()">Continue</a></td>
			<td><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
		</tr>
	</xsl:if>
	<tr class="tableContent">
		<td><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
		<td align="right">Cancel transition<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
		<td align="left"><a href="javascript:cancelTransition()">Cancel</a></td>
		<td><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
	</tr> -->
</table>
</xsl:template>

<xsl:template match="rule_problem">
<tr class="tableContent">
	<td><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
	<td align="left" colspan="2">
		<xsl:value-of select="reason" />
	</td>
	<td><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
</tr>
</xsl:template>

</xsl:stylesheet>
