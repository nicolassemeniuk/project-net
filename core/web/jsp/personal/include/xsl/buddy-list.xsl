<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:display="xalan://net.project.base.property.PropertyProvider">

<xsl:template match="buddy_list">
	<TABLE bgcolor="#99cccc" border="0" cellpadding="2" cellspacing="0" class="FRAME">
	<TBODY>
	<TR>
		<TD class="FRAMETITLE"><A class="FRAMETITLE" href="TeamMembersOnline.jsp" target="main"><xsl:value-of select="display:get('prm.personal.main.teammatesonline.link')"/></A></TD>
		<TD class="FRAMETITLE" align="right" valign="middle"><IMG src="../images/window_buttons.gif" width="47" height="14"/></TD>
	</TR>
	<TR>
		<TD colspan="2">
			<TABLE cellpadding="1" cellspacing="1" class="WINDOW" width="100%" border="0">
			<TBODY>
			<TR bgcolor="#E6E6E6">
				<TD><xsl:value-of select="display:get('prm.personal.main.person.label')"/></TD>
			</TR>
			<xsl:apply-templates select="person"/>
			</TBODY>
			</TABLE>
		</TD>
	</TR>
	</TBODY>
	</TABLE>
</xsl:template>

<xsl:template match="buddy_list/person">
	<tr>
		<td>
			<xsl:element name="A">
				<xsl:attribute name="href">../resource/PersonDetail.jsp?id=<xsl:value-of select="./person_id"/>&amp;referrer=dashboard</xsl:attribute>
				<xsl:value-of select="./full_name"/>
			</xsl:element>
		</td>
	</tr>
</xsl:template>

</xsl:stylesheet>