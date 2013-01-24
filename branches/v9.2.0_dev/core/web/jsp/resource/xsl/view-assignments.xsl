<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:display="xalan://net.project.base.property.PropertyProvider"
	xmlns:format="xalan://net.project.util.XSLFormat"
    extension-element-prefixes="display format" >
	
<xsl:output method="html"/>

    <!-- Declare external variables -->
    <xsl:param name="JspRootUrl" />
    <xsl:param name="returnTo" />

  <xsl:template match="/">
    
      <table width="100%" border="0" id="assignmentDetailsTable">
        <tr class="tableHeader" align="left">
          <td></td>
          <th nowrap="1" valign="bottom" class="tableHeader"><xsl:value-of select="display:get('prm.personal.assignments.assignment.column')"/></th>
          <th nowrap="1" valign="bottom" class="tableHeader"><xsl:value-of select="display:get('prm.personal.assignments.spacename.column')"/></th>
          <th nowrap="1" valign="bottom" class="tableHeader"><xsl:value-of select="display:get('prm.personal.assignments.type.column')"/></th>
          <th nowrap="1" valign="bottom" class="tableHeader">
              <xsl:value-of select="display:get('prm.personal.assignments.startdate.column.line1')"/><br/>
              <xsl:value-of select="display:get('prm.personal.assignments.startdate.column.line2')"/>
          </th>
          <th nowrap="1" valign="bottom" class="tableHeader">
              <xsl:value-of select="display:get('prm.personal.assignments.enddate.column.line1')"/><br/>
              <xsl:value-of select="display:get('prm.personal.assignments.enddate.column.line2')"/>
          </th>
          <th nowrap="1" valign="bottom" class="tableHeader">
              <xsl:value-of select="display:get('prm.personal.assignments.actualstartdate.column.line1')"/><br/>
              <xsl:value-of select="display:get('prm.personal.assignments.actualstartdate.column.line2')"/>
          </th>
          <th nowrap="1" valign="bottom" class="tableHeader">
              <xsl:value-of select="display:get('prm.personal.assignments.percentcomplete.column.line1')"/><br/>
              <xsl:value-of select="display:get('prm.personal.assignments.percentcomplete.column.line2')"/>
          </th>
          <th nowrap="1" valign="bottom" class="tableHeader"><xsl:value-of select="display:get('prm.personal.assignments.work.column')"/></th>
          <th nowrap="1" valign="bottom" class="tableHeader">
              <xsl:value-of select="display:get('prm.personal.assignments.workcompleted.column.line1')"/><br/>
              <xsl:value-of select="display:get('prm.personal.assignments.workcompleted.column.line2')"/>
          </th>
          <th nowrap="1" valign="bottom" class="tableHeader">
              <xsl:value-of select="display:get('prm.personal.assignments.workremaining.column.line1')"/><br/>
              <xsl:value-of select="display:get('prm.personal.assignments.workremaining.column.line2')"/>
          </th>
        </tr>
        <tr class="tableLine">
          <td  colspan="11" class="headerSep"></td>
        </tr>
        <xsl:apply-templates select="assignment_list/assignment" />
      </table>
  </xsl:template>
  
  <xsl:template match="assignment_list/assignment">
    <xsl:variable name="is_owner" select = "./primary_owner" />
    <tr class="tableContent" id="tableContent">
      <td><input type="checkbox" name="objectID" value="{object_id}"/></td>
      <td class="tableContent">
          <input type="hidden" name="capture_work_{object_id}" value="{can_capture_work}"/>
          <a id="href{object_id}" href="{$JspRootUrl}/servlet/AssignmentController/View?module=160&amp;objectID={object_id}&amp;personID={person_id}&amp;returnTo={$returnTo}">
              <xsl:choose>
                  <xsl:when test="string-length(object_name) > 40">
                      <xsl:value-of select="display:get('prm.global.textformatter.truncatedstringformat', substring(object_name,1,40))"/>
                  </xsl:when>
                  <xsl:otherwise>
                      <xsl:value-of select="object_name"/>
                  </xsl:otherwise>
              </xsl:choose>
          </a>
      </td>
      <td class="tableContent" id="workspaceName{object_id}">
          <xsl:choose>
              <xsl:when test="string-length(space_name) > 40">
                  <xsl:value-of select="display:get('prm.global.textformatter.truncatedstringformat', substring(space_name, 1, 40))"/>
              </xsl:when>
              <xsl:otherwise>
                  <xsl:value-of select="space_name"/>
              </xsl:otherwise>
          </xsl:choose>
      </td>
      <td class="tableContent" id="objectType{object_id}"><xsl:value-of select="./object_type_pretty"/></td>
      <td class="tableContent" id="startTime{object_id}"><xsl:value-of select="format:formatISODate(start_time)"/></td>
      <td class="tableContent" id="endTime{object_id}"><xsl:value-of select="format:formatISODate(end_time)"/></td>
      <td class="tableContent" id="actualStart{object_id}"><xsl:value-of select="format:formatISODate(actual_start)"/></td>
      <td class="tableContent" id="percentComplete{object_id}"><xsl:value-of select="format:formatPercent(percent_complete)"/></td>
      <td class="tableContent" id="work{object_id}"><xsl:value-of select="work"/></td>
      <td class="tableContent" id="workComplete{object_id}"><xsl:value-of select="work_complete"/></td>
      <td class="tableContent" id="workRemaining{object_id}"><xsl:value-of select="work_remaining"/></td>
    </tr>
    
    <tr class="tableLine">
      <td  colspan="11" class="rowSep"></td>
    </tr>
  </xsl:template>
  
</xsl:stylesheet>
