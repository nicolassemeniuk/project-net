<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:display="xalan://net.project.base.property.PropertyProvider"
	xmlns:format="xalan://net.project.util.XSLFormat"
    extension-element-prefixes="display format" >

<xsl:variable name="num_minor_units" select="/gantt/timescale/minor_scale/total_sub_units" />

<xsl:variable name="num_major_units" select="/gantt/timescale/major_scale/total_units" />

<xsl:variable name="tasks" select="/gantt/timescale/tasklist/task" />

<xsl:variable name="row_height" select="'20'" />

<xsl:variable name="minor_block_width" select="20" />

<xsl:variable name="scale_sub_unit" select="/gantt/timescale/minor_scale/scale_sub_unit" />

<xsl:variable name="major_unit_colspan" select="round($scale_sub_unit * 4.25)" />

<xsl:variable name="taskTypeMilestone" select="'milestone'" />

<xsl:variable name="taskTypeTask" select="'task'" />

<!-- OBSOLETE
<xsl:variable name="total_minor_blocks" select="/gantt/timescale/major_scale/total_units * /gantt/timescale/minor_scale/total_units" />
-->





<xsl:template match="timescale/major_scale">

<tr class="majorScale">
	
	<td class="timeline" colspan="{/gantt/timescale/minor_scale/total_units}" nowrap="">
		<table border="0" cellPadding="0" cellSpacing="1" width="100%">
			<tbody>
				<xsl:apply-templates select="major_unit"/>
			</tbody>
		</table>
	</td>

</tr>

</xsl:template>

<xsl:template match="gantt"><xsl:call-template name="gantt_css" />
<xsl:call-template name="legend" />

<table border="0" cellPadding="0" cellSpacing="0">
	<tbody><tr class="border"><td>
		<TABLE border="0" cellPadding="0" cellSpacing="1">
		<tbody>
		  <tr>
		    <td>
						<TABLE border="0" cellPadding="0" cellSpacing="1" width="100%" class="timeline">
							<tbody>
								<xsl:apply-templates select="timescale" />
								<xsl:apply-templates select="tasklist" />
							</tbody>
						</TABLE>
		</td>
			</tr>
		</tbody>
		</TABLE>
	</td></tr></tbody>
</table></xsl:template>

<xsl:template match="timescale/minor_scale">	<xsl:param name="minor_units" select="/gantt/timescale/minor_scale/minor_unit" />

	<xsl:variable name="scale_type" select="./resolution" />
			
	<tr class="minorScale">

		<xsl:for-each select="$minor_units">
			<xsl:call-template name="minor_unit"/>
		</xsl:for-each>	

	</tr>
</xsl:template>

<xsl:template match="timescale"><tr class="majorScale">

	<td nowrap="" />
	<td class="timeline" colspan="{$num_minor_units}" nowrap="">
		<table border="0" cellPadding="0" cellSpacing="1" width="100%">
			<tbody>

				<xsl:apply-templates select="major_scale" />
				<xsl:apply-templates select="minor_scale" />

			</tbody>
		</table>
	</td>

</tr></xsl:template>

<xsl:template match="major_unit" name="major_unit"><td class="majorScale" nowrap="">
	<xsl:value-of select="."/>
</td></xsl:template>

<xsl:template match="minor_unit" name="minor_unit"><xsl:variable name="unit" select="."/>

<td class="minorScale">
	<xsl:value-of select="$unit"/>
</td>
	</xsl:template>

<xsl:template match="get_minor_unit_for_block" name="get_minor_unit_for_block">
<xsl:param name="current_block" />

<xsl:variable name="minor_units" select="/gantt/timescale/minor_scale/minor_unit"/>
<xsl:variable name="index" select="($current_block mod $num_minor_units) + 1" />

<xsl:value-of select="$minor_units[number($index)]" />
</xsl:template>

<xsl:template match="tasklist" name="tasklist"><xsl:for-each select="/gantt/tasklist/task">
	<tr class="taskRow">
		<td class="taskHeader" nowrap="">
			<xsl:value-of select="name" />
		</td>
		
		<xsl:call-template name="task"/>
	</tr>
	<tr class="spacerRow"></tr>
</xsl:for-each>
</xsl:template>

<xsl:template match="tasklist/task" name="task">
<xsl:param name="block_position" select="0"/>
	
		<xsl:if test="number($block_position) &lt; number($num_minor_units)">
	
			<!-- 
				this gets the current "minor unit" (ie, day, week) for the 
				current grid location
			-->
			<xsl:variable name="minor_unit">
				<xsl:call-template name="get_minor_unit_for_block">
					<xsl:with-param name="current_block" select="$block_position"/>
				</xsl:call-template>
			</xsl:variable>
		
		
			<!-- 
				this prints out the correct task block while taking into account
				the current minor unit and calendar conditions
		
			<xsl:variable name="non_working_day">
				<xsl:call-template name="is_non_working_day">
					<xsl:with-param name="day" select="$minor_unit" />
				</xsl:call-template>
			</xsl:variable>

			<xsl:variable name="has_active_task">
				<xsl:call-template name="has_active_task">
					<xsl:with-param name="block_count" select="$block_position" />
				</xsl:call-template>
			</xsl:variable>
				
			-->
		
			<xsl:choose>
		
				<xsl:when test="number($block_position) &lt; number(start_block)">
				
					<xsl:variable name="num_empty_blocks" select="start_block - $block_position" />				

					<xsl:call-template name="draw_empty_block">
						<xsl:with-param name="num_blocks" select="$num_empty_blocks" />
					</xsl:call-template>

					<xsl:call-template name="task">
						<xsl:with-param name="block_position" select="$block_position + $num_empty_blocks" />
					</xsl:call-template>

				</xsl:when>

				<xsl:when test="$block_position = start_block">
				

					<xsl:choose>
					
						<xsl:when test="type = $taskTypeMilestone">
						
							<xsl:call-template name="draw_milestone">
								<xsl:with-param name="remaining_blocks" select="$num_minor_units - $block_position" />
								<xsl:with-param name="milestone_date" select="start_time" />
							</xsl:call-template>
				
							<!-- finally, recursively call this template to complete the row if necessary -->
							<xsl:call-template name="task">
								<xsl:with-param name="block_position" select="$block_position + 3" />
							</xsl:call-template>	

						</xsl:when>

						<xsl:when test="type = $taskTypeTask">

							<!-- first draw the task block -->
							<xsl:call-template name="draw_task">
								<xsl:with-param name="num_blocks" select="duration_blocks" />
								<xsl:with-param name="pct_complete" select="percent_complete" />
							</xsl:call-template>

							<!-- then, if there's room, draw the percent complete label -->

							<xsl:variable name="label_blocks">
								<xsl:choose>
									<xsl:when test="number($num_minor_units - $block_position - duration_blocks) &gt;= 2">
										<xsl:value-of select="2" />
										
									</xsl:when>
									<xsl:otherwise>
										<xsl:value-of select="0" />
									</xsl:otherwise>
								</xsl:choose>
							</xsl:variable>

							<xsl:call-template name="draw_completion_label">
								<xsl:with-param name="num_blocks" select="$label_blocks" />
								<xsl:with-param name="pct_complete" select="format:formatPercent(percent_complete)" />
							</xsl:call-template>
									

							<!-- finally, recursively call this template to complete the row if necessary -->
							<xsl:call-template name="task">
								<xsl:with-param name="block_position" select="$block_position + duration_blocks + $label_blocks" />
							</xsl:call-template>	
					
						</xsl:when>
					</xsl:choose>

				</xsl:when>	

				<xsl:when test="number($block_position) &gt; number(start_block)">
				
					<xsl:variable name="num_empty_blocks" select="$num_minor_units - $block_position" />				

					<xsl:call-template name="draw_empty_block">
						<xsl:with-param name="num_blocks" select="$num_empty_blocks" />
					</xsl:call-template>

					<xsl:call-template name="task">
						<xsl:with-param name="block_position" select="$block_position + $num_empty_blocks" />
					</xsl:call-template>

				</xsl:when>

			</xsl:choose>

		</xsl:if>
</xsl:template>

<xsl:template match="is_non_working_day" name="is_non_working_day"><xsl:param name="day"/>

<xsl:variable name="non_working_day" select="0" />

<!-- NEED TO PUT THIS BACK
<xsl:choose>
	<xsl:when test="contains($day, 'Sat')|contains($day), 'Sun')">
		<xsl:variable name="non_working_day" select="1" />
	</xsl:when>
	<xsl:otherwise>
		<xsl:variable name="non_working_day" select="0" />
	</xsl:otherwise>
</xsl:choose>
-->
<xsl:value-of select="$non_working_day" /></xsl:template>

<xsl:template match="has_active_task" name="has_active_task"><xsl:param name="block_count" />

<xsl:variable name="block" select="$block_count + 1" />
<xsl:variable name="starting_block" select="number(start_block)" />
<xsl:variable name="duration" select="number(duration_blocks)" />
<xsl:variable name="ending_block" select="($starting_block + $duration) - 1" />


<xsl:variable name="has_active_task">
    <xsl:choose>
    	<xsl:when test="number($block &gt;= $starting_block) and number($block &lt;= $ending_block)">1</xsl:when>
    	<xsl:otherwise>0</xsl:otherwise>
    </xsl:choose>
</xsl:variable>

<xsl:value-of select="$has_active_task" /></xsl:template>

<xsl:template match="draw_task" name="draw_task"><xsl:param name="num_blocks" />
<xsl:param name="pct_complete" />

<xsl:variable name="pct_complete_mx" select="$pct_complete div 100" />

<xsl:variable name="pct_complete_width_px" select="$minor_block_width * $num_blocks * $pct_complete_mx" />

<xsl:variable name="offset">
    <xsl:choose>
    	<xsl:when test="$pct_complete_mx = 1">2</xsl:when>
    	<xsl:otherwise>0</xsl:otherwise>
    </xsl:choose>
</xsl:variable>

<td class="activeTask" colspan="{$num_blocks}">
	<xsl:choose>
		<xsl:when test="$pct_complete_width_px > 0">
			<img class="taskPercentComplete" width="{$pct_complete_width_px + $offset}px" src="../images/lblack.gif" />
		</xsl:when>
		<xsl:otherwise>
			<img class="taskPercentComplete" src="../images/spacer.gif" />
		</xsl:otherwise>
	</xsl:choose>
</td>
</xsl:template>

<xsl:template match="draw_empty_block" name="draw_empty_block"><xsl:param name="num_blocks" />

	<td class="inactiveTask" colspan="{$num_blocks}"></td>
</xsl:template>

<xsl:template match="draw_completion_label" name="draw_completion_label"><xsl:param name="num_blocks" />
<xsl:param name="pct_complete" />


<xsl:if test="number($num_blocks) &gt;= 2">

	<td class="taskInfo" colspan="{$num_blocks}">
		<xsl:value-of select="$pct_complete" />
	</td>

</xsl:if></xsl:template>

<xsl:template match="draw_milestone" name="draw_milestone"><xsl:param name="remaining_blocks" />
<xsl:param name="milestone_date" />


<xsl:choose>

	<xsl:when test="number($remaining_blocks) &gt;= 3">

		<td class="taskInfo" colspan="3">
			<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
			<img class="milestone" src="../images/milestone.gif" />
			<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
			<xsl:value-of select="$milestone_date" />
		</td>
	
	</xsl:when>
	
	<xsl:otherwise>
	
		<td class="taskInfo" colspan="1">
			<img class="milestone" src="../images/milestone.gif" />
		</td>

	</xsl:otherwise>

</xsl:choose></xsl:template>

<xsl:template match="legend" name="legend"><TABLE border="0" cellPadding="0" cellSpacing="0" bgcolor="#000000">
  <TBODY>
  <TR>
    <TD>
      <TABLE align="center" border="0" cellPadding="4" cellSpacing="1">
        <TBODY>
        <TR>
          <TD class="legend"><B>Legend:</B></TD>
          <TD class="legend"><IMG class="legend" alt="" border="0" align="textTop" src="/images/summary.gif" width="25px" /> Task Summary</TD>
          <TD class="legend"><IMG class="legend" alt="" border="0" align="textTop" src="/images/milestone.gif" width="12" /> Milestone</TD>
        </TR>
	</TBODY>
	</TABLE>
	</TD></TR></TBODY>
	</TABLE>
<p/></xsl:template>

<xsl:template match="gantt_css" name="gantt_css"><style>

	TABLE.timeline {
		background-color: #ffffff
	}
	TR.border {
		background-color: #000000
	}
	TR.spacerRow {
		BACKGROUND-COLOR: #ffffff;
		height: 4px
	}
	TR.majorScale {
		BACKGROUND-COLOR: #ffffff;
		height: 12px
	}
	TR.minorScale {
		BACKGROUND-COLOR: #ffffff;
		height: 12px
	}
	TR.taskRow {
		BACKGROUND-COLOR: #fffffff;
		height: 12px;
		margin: 0px
	}
	TD.timeline {
		font-family: Verdana,Arial,Helvetica; 
		font-size: 10px;
		text-decoration: none; 
		text-align: center;
		vertical-align: bottom; 
		background-color: #ffffff;
		height: 12px
	}
	TD.majorScale {
		font-family: Verdana,Arial,Helvetica; 
		font-size: 10px;
		text-decoration: none; 
		text-align: center;
		vertical-align: bottom; 
		background-color: #ccccff;
		width: 170px;
		height: 12px
	}
	TD.minorScale {
		font-family: Verdana,Arial,Helvetica; 
		font-size: 10px;
		text-decoration: none; 
		text-align: center;
		vertical-align: bottom; 
		background-color: #ccccff;
		width: 40px;
		height: 12px;
		margin-top: 0px
	}
	TD.nonWorkingDay {
		font-family: Verdana,Arial,Helvetica; 
		font-size: 10px;
		text-decoration: none; 
		text-align: center;
		vertical-align: bottom; 
		background-color: #cccccc;
		width: 20px;
		height: 12px;
		margin-top: 0px
	}
	TD.taskHeader {
		font-family: Verdana,Arial,Helvetica; 
		font-size: 10px;
		text-decoration: none; 
		text-align: left;
		vertical-align: middle; 
		background-color: #eeeeee
	}
	TD.activeTask {
		background-color: #3333ff;
		width: 7px;
		height: 12px;
		margin-top: 0px
	}
	TD.inactiveTask {
		font-family: Verdana,Arial,Helvetica; 
		font-size: 10px;
		text-decoration: none; 
		text-align: center;
		vertical-align: bottom;
		background-color: #ffffff;
		width: 7px;
		height: 12px;
		margin-top: 0px
	}
	TD.taskInfo {
		font-family: Verdana,Arial,Helvetica; 
		font-size: 10px;
		text-decoration: none; 
		text-align: left;
	}
	TD.legend {
		font-family: Verdana,Arial,Helvetica; 
		font-size: 10px;
		text-decoration: none; 
		background-color: #ffffff
	}
	IMG.taskPercentComplete {
		margin-top: 0px;
		vertical-align: middle;
		height=3px 
	}
	IMG.columnDivider {
		margin-top: 0px;
		align: right
		width=1px 
	}
	IMG.legend {
		margin-top: 0px;
		align: textTop;
		border: 0
	}

</style>
</xsl:template>

<xsl:output method="html"/>
</xsl:stylesheet>

