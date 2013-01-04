<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:display="xalan://net.project.base.property.PropertyProvider"
    xmlns:format="xalan://net.project.util.XSLFormat"
    extension-element-prefixes="display format">

    <xsl:output method="html"/>

    <xsl:template match="/">
        <table border="0" width="100%" vspace="0" cellpadding="2" cellspacing="0">
            <xsl:for-each select="document_properties">
                <tr>
                    <td align="left" colspan="6"></td>
                </tr>
                <tr>
                    <td align="right" class="tableHeader">
                        <xsl:value-of select="display:get('prm.document.documentproperties.documentname.label')"/>
                    </td>
                    <td align="right">
                        <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
                    </td>
                    <td colspan="4" align="left" class="tableContent">
                        <a target="view_window" href="{./jsp_root_url}/servlet/DownloadDocument?id={./object_id}">
                            <xsl:value-of select="./name"/>
                        </a>
                        <a target="view_window" href="{./jsp_root_url}/servlet/DownloadDocument?id={./object_id}">
                            <img align="bottom" border="0" src="..{./app_icon_url}"/>
                        </a>
                    </td>
                </tr>
                <tr>
                    <td align="right" class="tableHeader">
                        <xsl:value-of select="display:get('prm.document.documentproperties.vaultpath.label')"/>
                    </td>
                    <td align="right">
                        <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
                    </td>
                    <td colspan="4" align="left" class="tableContent">
                        <xsl:apply-templates select="path"/>
                    </td>
                </tr>
                <tr>
                    <td align="right" class="tableHeader">
                        <xsl:value-of select="display:get('prm.document.documentproperties.filename.label')"/>
                    </td>
                    <td align="right">
                        <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
                    </td>
                    <td colspan="4" align="left" class="tableContent">
                        <xsl:value-of select="./short_file_name"/>
                    </td>
                </tr>
                <tr>
                    <td align="right" class="tableHeader">
                        <xsl:value-of select="display:get('prm.document.documentproperties.description.label')"/>
                    </td>
                    <td align="right">
                        <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
                    </td>
                    <td align="left" colspan="4" class="tableContent">
                        <xsl:value-of select="./description"/>
                    </td>
                </tr>
                <tr>
                    <td align="right" class="tableHeader">
                        <xsl:value-of select="display:get('prm.document.documentproperties.notes.label')"/>
                    </td>
                    <td align="right">
                        <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
                    </td>
                    <td align="left" class="tableContent">
                        <xsl:value-of select="./notes"/>
                    </td>
                    <td align="right" class="tableHeader">
                        <xsl:value-of select="display:get('prm.document.documentproperties.lastmodified.label')"/>
                    </td>
                    <td align="left">
                        <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
                    </td>
                    <td align="left" class="tableContent">
                        <xsl:value-of select="format:formatISODateTime(./last_modified)"/>
                    </td>
                </tr>
                <tr>
                    <td align="right" class="tableHeader">
                        <xsl:value-of select="display:get('prm.document.documentproperties.author.label')"/>
                    </td>
                    <td align="right">
                        <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
                    </td>
                    <td align="left" class="tableContent">
                        <xsl:value-of select="./author"/>
                    </td>
                    <td align="right" class="tableHeader">
                        <xsl:value-of select="display:get('prm.document.documentproperties.filesize.label')"/>
                    </td>
                    <td align="left">
                        <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
                    </td>
                    <td align="left" class="tableContent">
                        <xsl:value-of select="./file_size"/>
                    </td>
                </tr>
                <tr>
                    <td align="right" class="tableHeader">
                        <xsl:value-of select="display:get('prm.document.documentproperties.fileformat.label')"/>
                    </td>
                    <td align="left">
                        <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
                    </td>
                    <td align="left" class="tableContent">
                        <xsl:value-of select="./file_format"/>
                    </td>
                    <td align="right" class="tableHeader">
                        <xsl:value-of select="display:get('prm.document.documentproperties.status.label')"/>
                    </td>
                    <td align="right">
                        <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
                    </td>
                    <td align="left" class="tableContent">
                        <xsl:value-of select="./status"/>
                    </td>
                </tr>
                <tr>
                    <td align="right" class="tableHeader">
                        <xsl:value-of select="display:get('prm.document.documentproperties.version.label')"/>
                    </td>
                    <td align="left">
                        <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
                    </td>
                    <td align="left" class="tableContent">
                        <xsl:value-of select="format:formatNumber(version_num)"/>
                    </td>
                    <td align="right" class="tableHeader">
                        <xsl:value-of select="display:get('prm.document.documentproperties.timesviewed.label')"/>
                    </td>
                    <td align="left">
                        <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
                    </td>
                    <td align="left" class="tableContent">
                        <xsl:value-of select="format:formatNumber(num_times_viewed)"/>
                    </td>
                </tr>
                <tr>
                    <td align="right" class="tableHeader">
                        <xsl:value-of select="display:get('prm.document.documentproperties.workflows.label')"/>
                    </td>
                    <td align="left">
                        <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
                    </td>
                    <td align="left" class="tableContent">
                        <xsl:value-of select="format:formatNumber(active_workflows)"/>
                    </td>
                    <td align="right" class="tableHeader">
                        <xsl:value-of select="display:get('prm.document.documentproperties.discussions.label')"/>
                    </td>
                    <td align="left">
                        <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
                    </td>
                    <td align="left" class="tableContent">
                        <xsl:value-of select="format:formatNumber(discussion_count)"/>
                    </td>
                </tr>
                <tr>
                    <td align="right">
                        <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
                    </td>
                    <td align="right">
                        <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
                    </td>
                    <td align="left">
                        <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
                    </td>
                    <td align="right">
                        <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
                    </td>
                    <td align="left">
                        <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
                    </td>
                    <td align="left">
                        <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
                    </td>
                </tr>

                <tr>
                    <td colspan="6">
                        <table border="0" cellspacing="0" cellpadding="0" vspace="0" width="100%">
                            <tr class="channelHeader">
                                <td width="1%">
                                    <img src="../images/icons/channelbar-left_end.gif" width="8" height="15" alt="" border="0"/>
                                </td>
                                <td class="channelHeader">
                                    <xsl:value-of select="display:get('prm.document.documentproperties.channel.versioncontrolstatus.title')"/>
                                </td>
                                <td width="1%" align="right">
                                    <img src="../images/icons/channelbar-right_end.gif" width="8" height="15" alt="" border="0"/>
                                </td>
                            </tr>
                        </table>
                    </td>
                </tr>

                <tr>
                    <td align="right" class="tableHeader">
                        <xsl:value-of select="display:get('prm.document.documentproperties.checkedout.label')"/>
                    </td>
                    <td align="right">
                        <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
                    </td>
                    <td align="left" class="tableContent">
                        <xsl:choose>
                            <xsl:when test="is_cko=1">
                                <xsl:value-of select="display:get('prm.document.documentproperties.checkedout.true')"/>
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:value-of select="display:get('prm.document.documentproperties.checkedout.false')"/>
                            </xsl:otherwise>
                        </xsl:choose>
                    </td>
                    <td align="right">
                        <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
                    </td>
                    <td align="left">
                        <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
                    </td>
                    <td align="left">
                        <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
                    </td>
                </tr>
                <tr>
                    <td align="right" class="tableHeader">
                        <xsl:value-of select="display:get('prm.document.documentproperties.checkedoutby.label')"/>
                    </td>
                    <td align="right">
                        <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
                    </td>
                    <td align="left" class="tableContent">
                        <xsl:value-of select="./cko_by"/>
                    </td>
                    <td align="right">
                        <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
                    </td>
                    <td align="left">
                        <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
                    </td>
                    <td align="left">
                        <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
                    </td>
                </tr>
                <tr>
                    <td align="right" class="tableHeader">
                        <xsl:value-of select="display:get('prm.document.documentproperties.return.label')"/>
                    </td>
                    <td align="right">
                        <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
                    </td>
                    <td align="left" class="tableContent">
                        <xsl:value-of select="format:formatISODate(./cko_return)"/>
                    </td>
                    <td align="right">
                        <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
                    </td>
                    <td align="left">
                        <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
                    </td>
                    <td align="left">
                        <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
                    </td>
                </tr>

            </xsl:for-each>
        </table>

    </xsl:template>

    <xsl:template match="path">
        <xsl:apply-templates select="node">
            <xsl:sort select="level" order="ascending"/>
        </xsl:apply-templates>
    </xsl:template>

    <xsl:template match="node">
        <xsl:if test="level > 1">
            /
        </xsl:if>
        <a href="{/document_properties/jsp_root_url}/document/TraverseFolderProcessing.jsp?id={object_id}&amp;module=10">
            <xsl:value-of select="name"/>
        </a>
    </xsl:template>
</xsl:stylesheet>


