<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:format="xalan://net.project.util.XSLFormat"
    extension-element-prefixes="format">
<xsl:output method="html"/>

<xsl:template match="/">
    <xsl:apply-templates select="container/container_contents/entry[@type='doc_container']" />
</xsl:template>

<xsl:template match="entry">
    <tr>
        <td>
            <input type="hidden" name="{object_id}" value="{object_type}"/>
        </td>
        <td align="left">
            <img src="../images/folder.gif"/>
        </td>
        <td align="left" class="tableContent">
            <a href="{/container/jsp_root_url}/document/TraverseFolderProcessing.jsp?id={object_id}&amp;module=10&amp;Page=/document/MoveObjectFolderBrowser.jsp">
                <xsl:value-of select="name"/>
            </a>
        </td>
        <td align="left" class="tableContent">
            <xsl:value-of select="format:formatISODateTime(last_modified)" />
        </td>
    </tr>
    <tr class="tableLine">
        <td colspan="4" class="tableLine">
            <img src="../images/spacers/trans.gif" width="1" height="1" border="0" alt=""/>
        </td>
    </tr>
</xsl:template>

</xsl:stylesheet>
