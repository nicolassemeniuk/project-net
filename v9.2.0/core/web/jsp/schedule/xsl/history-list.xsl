<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:display="xalan://net.project.base.property.PropertyProvider"
    xmlns:format="xalan://net.project.util.XSLFormat"
    extension-element-prefixes="display format" >
    <xsl:output method="html"/>
    <xsl:template match="/">
        <table width="100%" cellpadding="0" border="0" cellspacing="0">
            <tr align="left">
                <td><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
                <td><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
                <td><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
                <td><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
                <th nowrap="1" valign="bottom" colspan="3" style="border-left:thin solid #CCCCFF; border-top:thin solid #CCCCFF; border-bottom:thin solid #CCCCFF; padding-right:3px; text-align:center; border-collapse: collapse;" class="tableHeader"><xsl:value-of select="display:get('prm.schedule.taskview.history.work.column')"/></th>
                <th nowrap="1" valign="bottom" colspan="2" style="border-left:thin solid #CCCCFF; border-top:thin solid #CCCCFF; border-bottom:thin solid #CCCCFF; border-right:thin solid #CCCCFF; text-align:center; border-collapse: collapse;" class="tableHeader"><xsl:value-of select="display:get('prm.schedule.taskview.history.duration.column')"/></th>
                <td><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
            </tr>
            <tr align="left">
                <td><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
                <th nowrap="1" valign="bottom" class="tableHeader"><xsl:value-of select="display:get('prm.schedule.taskview.history.datechanged.column')"/></th>
                <th nowrap="1" valign="bottom" class="tableHeader"><xsl:value-of select="display:get('prm.schedule.taskview.history.plannedstart.column')"/></th>
                <th nowrap="1" valign="bottom" class="tableHeader"><xsl:value-of select="display:get('prm.schedule.taskview.history.plannedfinish.column')"/></th>
                <th nowrap="1" valign="bottom" style="border-left:thin solid #CCCCFF; text-align:center;" class="tableHeader"><xsl:value-of select="display:get('prm.schedule.taskview.history.plannedwork.column')"/></th>
                <th nowrap="1" valign="bottom" style="border-left:thin solid #CCCCFF; text-align:center;" class="tableHeader"><xsl:value-of select="display:get('prm.schedule.taskview.history.workcomplete.column')"/></th>
                <th nowrap="1" valign="bottom" style="border-left:thin solid #CCCCFF; text-align:center;" class="tableHeader"><xsl:value-of select="display:get('prm.schedule.taskview.history.workvariance.column')"/></th>
                <th nowrap="1" valign="bottom" style="border-left:thin solid #CCCCFF; text-align:center;" class="tableHeader"><xsl:value-of select="display:get('prm.schedule.taskview.history.actualduration.column')"/></th>
                <th nowrap="1" valign="bottom" style="border-left:thin solid #CCCCFF; border-right:thin solid #CCCCFF; text-align:center;" class="tableHeader"><xsl:value-of select="display:get('prm.schedule.taskview.history.durationvariance.column')"/></th>
                <th nowrap="1" valign="bottom" class="tableHeader"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text><xsl:value-of select="display:get('prm.schedule.taskview.history.changedby.column')"/></th>
            </tr>
            <tr>
                <td></td>
                <td colspan="9" class="headerSep"></td>
            </tr>

            <xsl:apply-templates/>
        </table>
    </xsl:template>

    <xsl:template match="history/task|history/summary">

        <xsl:variable name="workVarianceClass">
             <xsl:choose>
                <xsl:when test="workVarianceAmount &lt; 0">
                  negativeVariance
                </xsl:when>
                <xsl:when test="workVarianceAmount &gt; 0">
                  positiveVariance
                </xsl:when>
            </xsl:choose>
        </xsl:variable>

                <xsl:variable name="durationVarianceClass">
             <xsl:choose>
                <xsl:when test="durationVarianceAmount &lt; 0">
                  negativeVariance
                </xsl:when>
                <xsl:when test="durationVarianceAmount &gt; 0">
                  positiveVariance
                </xsl:when>
            </xsl:choose>
        </xsl:variable>

        <tr>
            <td></td>
             <td class="tableContent">
                <div title ="{format:formatISODateTime(dateModified)}">
                    <xsl:value-of select="format:formatISODateTime(dateModified)"/>
                </div>
            </td>
            <td class="tableContent">
                <div title ="{format:formatISODateTime(startDateTime)}">
                    <xsl:value-of select="format:formatISODate(startDateTime)"/>
                </div>
            </td>
             <td class="tableContent">
                <div title ="{format:formatISODateTime(endDateTime)}">
                    <xsl:value-of select="format:formatISODate(endDateTime)"/>
                </div>
            </td>
            <td class="tableContent" style="text-align:right; padding-right:15px;"><xsl:value-of select="workString"/></td>
            <td class="tableContent" style="text-align:right; padding-right:15px;"><xsl:value-of select="workCompleteString"/></td>
            <td class="tableContent {$workVarianceClass}" style="text-align:right; padding-right:15px;"><xsl:value-of select="workVariance"/></td>
            <td class="tableContent" style="text-align:right; padding-right:15px;"><xsl:value-of select="duration"/></td>
            <td class="tableContent {$durationVarianceClass}" style="text-align:right; padding-right:15px;"><xsl:value-of select="durationVariance"/></td>
            <td class="tableContent"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text><xsl:value-of select="./modifiedBy"/><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text> </td>
        </tr>
        <tr>
            <td></td>
            <td colspan="9" class="rowSep"></td>
        </tr>
    </xsl:template>

    <xsl:template match="baseline">
        <tr>
            <td></td>
            <td colspan="9" class="tableHeader" style="background-color:#CCCCCC">
                <xsl:value-of select="display:get('prm.schedule.taskview.history.baseline.header', name, format:formatISODateTime(creationDate))"/>
            </td>
        </tr>
        <tr>
            <td></td>
            <td colspan="9" class="rowSep"></td>
        </tr>
    </xsl:template>

    <xsl:template match="nobaseline">
        <tr>
            <td></td>
            <td colspan="9" class="tableHeader" style="background-color:#CCCCCC">
                <xsl:value-of select="display:get('prm.schedule.taskview.history.versionsbeforeinitialbaseline')"/>
            </td>
        </tr>
        <tr>
            <td></td>
            <td colspan="9" class="rowSep"></td>
        </tr>
    </xsl:template>

</xsl:stylesheet>
	