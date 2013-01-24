<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:output method="html"/>

<xsl:template match="/">
    <xsl:apply-templates select="rule" />
</xsl:template>

<xsl:template match="rule">
<table border="0" cellpadding="0" cellspacing="0" width="100%">
  <tr>
    <td colspan="2"><xsl:apply-templates select="custom_property_list" /></td>
  </tr>
</table></xsl:template>

<xsl:template match="custom_property_list"><table border="0" width="100%" cellpadding="0" cellspacing="0">
	<tr><th width="25%"></th><th></th></tr>
	<xsl:apply-templates select="property" />
</table></xsl:template>

<xsl:template match="property">
<!-- Call appropriate property renderer based on input type -->
<tr>
<xsl:choose>
<xsl:when test="input/@type='checkboxlist'">
  <xsl:call-template name="checkboxlist_render">
    <xsl:with-param name="property" select="." />
  </xsl:call-template>
</xsl:when>
<xsl:when test="input/@type='dropdownlist'">
  <xsl:call-template name="dropdownlist_render">
    <xsl:with-param name="property" select="." />
  </xsl:call-template>
</xsl:when>
<xsl:when test="input/@type='text'">
  <xsl:call-template name="text_render">
    <xsl:with-param name="property" select="." />
  </xsl:call-template>
</xsl:when>
</xsl:choose>
</tr></xsl:template>

<xsl:template name="checkboxlist_render">
<xsl:param name="property"/>
<!-- Checkboxlist Renderer
     This displays the property label across the top then lists the options as table rows
	 and places a checkbox beside each
-->
<td colspan="2">
  <!-- Insert hidden input type with same name as checkbox to force a checkbox list to _always_ be sent -->
  <xsl:element name="input">
    <xsl:attribute name="type">hidden</xsl:attribute>
	<xsl:attribute name="name"><xsl:value-of select="$property/name"/></xsl:attribute>
	<xsl:attribute name="value">0</xsl:attribute>
  </xsl:element>
  <!-- End of hidden input -->  
<table border="0" cellpadding="0" cellspacing="0" width="100%">
<!-- Avinash: change label value and add a label for the checkboxes  -->
  <tr><td colspan="1" align="left" class="tableHeader"><xsl:value-of select="$property/label" /></td>
  <td colspan="2" align="left" class="tableHeader"><xsl:value-of select="$property/labelR" /></td></tr>
 <!-- Avinash: change label value and add a label for the checkboxes  --> 
  <tr><td colspan="6" class="tableLine"><img src="../images/spacers/trans.gif" width="1" height="2" border="0" alt=""/></td></tr>
  <xsl:for-each select="$property/input/option">
  	<xsl:sort select="." order="ascending" />
    <tr>
	  <!-- Display the option text -->
      <td class="tableContent"><xsl:apply-templates select="." /></td>
      <td class="tableContent">
	    <!-- Insert the checkbox -->
        <xsl:element name="input">
          <xsl:attribute name="type">checkbox</xsl:attribute>
  	      <xsl:attribute name="name"><xsl:value-of select="$property/name"/></xsl:attribute>
	      <xsl:attribute name="id"><xsl:value-of select="."/></xsl:attribute>
	      <xsl:attribute name="value"><xsl:value-of select="@value"/></xsl:attribute>
		  <xsl:if test="@selected='1'"><xsl:attribute name="checked" /></xsl:if>
        </xsl:element>
      </td>
	  <td class="tableContent" width="50%"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
    </tr>
    <tr><td colspan="3" class="tableLine"><img src="../images/spacers/trans.gif" width="1" height="1" border="0" alt=""/></td></tr>
  </xsl:for-each>
</table>
</td>
</xsl:template>

<xsl:template name="dropdownlist_render">
    <xsl:param name="property"/>
    <td class="fieldRequired"><xsl:value-of select="$property/label" />:</td>
    <td class="tableContent">
      <xsl:element name="select">
		<xsl:attribute name="name"><xsl:value-of select="$property/name" /></xsl:attribute>
          <xsl:for-each select="$property/input/option">
            <xsl:element name="option">
	          <xsl:attribute name="value"><xsl:value-of select="@value"/></xsl:attribute>
		      <xsl:if test="@selected='1'"><xsl:attribute name="selected" /></xsl:if>
			  <xsl:value-of select="." />
            </xsl:element>
          </xsl:for-each>
      </xsl:element>
    </td>
</xsl:template>

<xsl:template name="text_render">
    <xsl:param name="property"/>
    <xsl:variable name="input" select="$property/input" />
    <td class="fieldRequired"><xsl:value-of select="$property/label" />:</td>
    <td class="tableContent">
      <xsl:element name="input">
        <xsl:attribute name="type">text</xsl:attribute>
    	<xsl:attribute name="name"><xsl:value-of select="$property/name" /></xsl:attribute>
    	<xsl:attribute name="maxlength"><xsl:value-of select="$input/maxlength" /></xsl:attribute>
    	<xsl:attribute name="size"><xsl:value-of select="$input/size" /></xsl:attribute>
    	<xsl:attribute name="value"><xsl:value-of select="$input/value" /></xsl:attribute>
      </xsl:element>
    </td>
</xsl:template>

</xsl:stylesheet>
