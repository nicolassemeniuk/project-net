<%--
 * Copyright 2000-2009 Project.net Inc.
 *
 * This file is part of Project.net.
 * Project.net is free software: you can redistribute it and/or modify it under the terms of 
 * the GNU General Public License as published by the Free Software Foundation, version 3 of the License.
 * 
 * Project.net is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Project.net.
 * If not, see http://www.gnu.org/licenses/gpl-3.0.html
--%>


<%@page import="net.project.security.SessionManager"%>
<a name="interchange_use"></a>

<table border="0" width="80%" cellpadding="0" cellspacing="0">
<tr class="channelHeader">
	<td width=1%><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-left_end.gif" width=8 height=15 alt="" border="0"></td>
	<td nowrap class="channelHeader"><nobr>Using viecon.interchange</nobr></td>
	<td align=right width=1%><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif" width=8 height=15 alt="" border="0"></td>
</tr>
</table>
<p>
<h1 class="ChapterTitle">
<a name="pgfId=1252"></a>Using Viecon.interchange</h1>

<div class="BodyAfterTitle"><a name="pgfId=4080"></a>This chapter cover
the most common procedures for running Viecon.interchange.</div>

<h1 class="h1">
<a name="pgfId=5825"></a>Overview</h1>

<div class="Body"><a name="pgfId=5826"></a>Viecon.interchange is a conversion
utility that automates the file translation process between files with
DGN and DWG or DXF formats. Interchange runs from a DOS prompt, or as an
NT service where you can set up "watched" directories for automatic translations
or do single or batched conversions. Although Interchange is derived from
MicroStation, Interchange is a fully stand-alone product.</div>
<img SRC="<%=SessionManager.getJSPRootURL()%>/images/help/interchn_translations.jpg" height=388 width=507>
<div class="Body"><a name="pgfId=8188"></a>In operation, Interchange searches
a specified source directory (and its subdirectories) for source files
(for example, DGN). It compares the date and time stamps of all of these
files against a database that contains file name, date and time information.
Interchange automatically converts any source files that are newer than
its corresponding destination file. Since the number of files associated
with a project can be quite large, with each Interchange iteration, only
the source files that have changed since the last iteration will be operated
on. Interchange will also check reference file dependencies. If the main
source design file itself has not changed, but any attached reference files
have, Interchange will update the destination file accordingly.</div>

<div class="Body"><a name="pgfId=8189"></a>Interchange also checks dependencies
with MicroStation BASIC import/export settings files. If the source design
files have not changed, but the settings files have, Interchange will update
each destination file that it needs to.</div>

<div class="Body"><a name="pgfId=8186"></a>Typically, Interchange is used
to synchronize files in offices using multiple design formats, although
Interchange does not support round-tripping. Interchange further helps
your synchronization by tracking files that have already been converted
in a logfile. The logfile also helps speed batch conversions, since files
with the same time stamp as the logfile record are not updated.</div>

<div class="Body"><a name="pgfId=8125"></a>This chapter addresses the following
tasks:</div>

<ul>
<li class="Bullet">
<a name="pgfId=8128"></a><a href="#13535" class="XRef">See
Getting Started</a></li>

<li class="Bullet">
<a name="pgfId=8156"></a><a href="#16596" class="XRef">See
Common Procedures</a></li>

<li class="Bullet">
<a name="pgfId=8138"></a><a href="#41375" class="XRef">See
Using Interchange as an NT Service</a></li>

<li class="Bullet">
<a name="pgfId=8142"></a><a href="#28010" class="XRef">See
Floating Directory Appended on Source Trees</a></li>

<li class="Bullet">
<a name="pgfId=8168"></a><a href="#31385" class="XRef">See
Command File Usage</a></li>

<li class="Bullet">
<a name="pgfId=8147"></a><a href="#14897" class="XRef">See
Simultaneous Export and Import</a></li>
</ul>

<h1 class="h1">
<a name="pgfId=6033"></a><a name="13535"></a>Getting Started</h1>

<div class="Body"><a name="pgfId=6038"></a>This section assumes you have
installed Interchange. Refer to Chapter 1 for installation instructions.</div>

<h2 class="Procedure">
<a name="pgfId=6096"></a>Load Interchange</h2>

<ol>
<li class="1Step">
<a name="pgfId=6097"></a>Open a DOS shell by selecting Start > Programs
> Command Prompt or</li>
</ol>

<div class="Item"><a name="pgfId=6115"></a>double-click on the Viecon.interchange
icon to open the DOS shell.</div>

<div class="2Step"><a name="pgfId=6098"></a>At the DOS prompt, type interchange.</div>

<div class="LItem"><a name="pgfId=6169"></a>The usage key appears:</div>

<table frame="border" >
<caption><tbody>
<br></tbody></caption>

<tr>
<th>
<div class="CellHeading"><a name="pgfId=6127"></a>Switch</div>
</th>

<th>
<div class="CellHeading"><a name="pgfId=6129"></a>Function</div>
</th>
</tr>

<tr>
<td>
<div class="CellBody"><a name="pgfId=6177"></a>-x&lt;dwgin|dwgout|dxfout
[,Assocfile]</div>
</td>

<td>
<div class="CellBody"><a name="pgfId=6179"></a>Required parameter. Import
DWG (dwgin) or export DGN (dwgout) with an optional file name which associates
source files to the desired MicroStation BASIC files and project configuration
files.</div>

<div class="CellBody"><a name="pgfId=6183"></a>Uses an optional file that
contains source file names associated with DWG BASIC files</div>
</td>
</tr>

<tr>
<td>&lt;?Pub Caret1></td>

<td>
<div class="CellBody"><a name="pgfId=6131"></a>-f&lt;e:exts|w|x></div>
</td>

<td>
<div class="CellBody"><a name="pgfId=6133"></a>Uses a filter to exclude
files from batch conversion based on the extension name</div>

<ul>
<li class="CellBullet">
<a name="pgfId=6211"></a>e excludes files with extension names.</li>

<li class="CellBullet">
<a name="pgfId=6211"></a>w excludes DWG files</li>

<li class="CellBullet">
<a name="pgfId=6214"></a>x excludes DXF files</li>

<li class="CellBullet">
<a name="pgfId=6215"></a>Use pipes (|) to add more options and comma (,)
to add more extensions.</li>
</ul>
</td>
</tr>

<tr>
<td>
<div class="CellBody"><a name="pgfId=6422"></a>-d&lt; m | r | b ></div>
</td>

<td>
<div class="CellBody"><a name="pgfId=6426"></a>Dependency for re-translation.</div>

<ul>
<li class="CellBullet">
<a name="pgfId=6433"></a><i>m</i> - master file</li>

<li class="CellBullet">
<a name="pgfId=6434"></a><i>r</i> - reference file (DGN only)</li>

<li class="CellBullet">
<a name="pgfId=6435"></a><i>b</i> - MicroStation BASIC file. No dependency
(-d alone) always re-translates files. Default: check all.&nbsp;</li>
</ul>
</td>
</tr>

<tr>
<td>
<div class="CellBody"><a name="pgfId=6135"></a>-i&lt;mins></div>
</td>

<td>
<div class="CellBody"><a name="pgfId=6137"></a>Sets the time between each
batch process.</div>
</td>
</tr>

<tr>
<td>
<div class="CellBody"><a name="pgfId=6139"></a>-l</div>
</td>

<td>
<div 
class="CellBody"><a name="pgfId=6141"></a>Lists files that would
be translated, but do not translate them.</div>
</td>
</tr>

<tr>
<td>
<div class="CellBody"><a name="pgfId=6143"></a>-m&lt;filename></div>
</td>

<td>
<div class="CellBody"><a name="pgfId=6145"></a>Redirect all messages to
a message log file.</div>
</td>
</tr>

<tr>
<td>
<div class="CellBody"><a name="pgfId=6147"></a>-p</div>
</td>

<td>
<div 
class="CellBody"><a name="pgfId=6149"></a>Required Parameter. Project
name.</div>
</td>
</tr>

<tr>
<td>
<div class="CellBody"><a name="pgfId=6191"></a>-r</div>
</td>

<td>
<div 
class="CellBody"><a name="pgfId=6193"></a>Retains complete root path
of the source in the destination.</div>
</td>
</tr>

<tr>
<td>
<div class="CellBody"><a name="pgfId=6151"></a>-s</div>
</td>

<td>
<div 
class="CellBody"><a name="pgfId=6153"></a>Process subdirectories</div>
</td>
</tr>

<tr>
<td>
<div class="CellBody"><a name="pgfId=6155"></a>-u</div>
</td>

<td>
<div 
class="CellBody"><a name="pgfId=6157"></a>Updates only if the file
or subdirectory exists in the destination</div>
</td>
</tr>

<tr>
<td>
<div class="CellBody"><a name="pgfId=6159"></a>-U</div>
</td>

<td>
<div 
class="CellBody"><a name="pgfId=6161"></a>Update filter applied only
to subdirectories.</div>
</td>
</tr>

<tr>
<td>
<div class="CellBody"><a name="pgfId=6163"></a>@&lt;file></div>
</td>

<td>
<div class="CellBody"><a name="pgfId=6168"></a>Reads Interchange commands
from file</div>
</td>
</tr>
</table>

<h1 class="h1">
<a name="pgfId=8316"></a>Required Parameter Summary</h1>

<div class="Body"><a name="pgfId=8357"></a>The required set of parameters
in any command sequence or script are:</div>

<ul>
<li class="Bullet">
<a name="pgfId=8358"></a>-x(dwgout|etc.)</li>

<li class="Bullet">
<a name="pgfId=8359"></a>-p - for the project specification</li>

<li class="Bullet">
<a name="pgfId=8362"></a>one directory (to specify the location of the
watched directory).</li>
</ul>

<div class="Body">The minimum command stream is:
<p><tt>interchange &lt;source> -p&lt;project_name> &lt;destination> [flags]</tt>
<br>&nbsp;</div>

<div class="Body"><a name="pgfId=8365"></a>When this stream is input, the
result is placed in the specified directory name/out directory.</div>

<h2 class="h2">
<a name="pgfId=8354"></a>Project name</h2>

<div class="Body"><a name="pgfId=8319"></a>A project name must appear in
the command stream. This name is assigned and obtained during the project
registration process.</div>

<h1 class="h1">
<a name="pgfId=7036"></a>Usage examples</h1>

<div class="Body"><a name="pgfId=7063"></a>Following are two possible command
streams and their results.</div>

<h2 class="Procedure">
<a name="pgfId=7066"></a>To specify multiple watched directories</h2>

<ol>
<li class="1Step">
<a name="pgfId=7067"></a>Key in:<a name="pgfId=7068"></a><tt>interchange
-xdwgout -pmyproject c:\project1 c:\project2\*.dgn c:\dwgs</tt></li>
</ol>

<div class="Code"><a name="pgfId=7069"></a></div>

<div class="Item"><a name="pgfId=7070"></a>exports all design files in
<i>c:\project1</i>
and all the design files with extension .DGN in <i>c:\project2
</i>to DWG
files and places them in the target directory <i>c:\dwgs</i> .</div>

<div class="Item"><a name="pgfId=7071"></a></div>

<div class="Body"><a name="pgfId=7072"></a>The target directory can be
omitted by using the asterisk `*'. The resulting files will then be exported
to the same root directory as the source.</div>

<h2 class="Procedure">
<a name="pgfId=7073"></a>To export files with the directory specified by
an *</h2>

<ol>
<li class="1Step">
<a name="pgfId=7074"></a>Key in:<a name="pgfId=7075"></a><tt>interchange
-xdwgout -s -pmyproject c:\project1 \\neptune.mycompany\project2 *</tt></li>
</ol>

<div class="Item"><a name="pgfId=7077"></a>exports all design files and
subdirectories in <i>c:\project1</i> to DWG files in <i>c:\project1\out</i>
and the design files in \\neptune.mycompany\project2 to \\neptune.mycompany\project2\out.</div>

<h1 class="h1">
<a name="pgfId=7062"></a>Floating Directory Appended on Source Trees</h1>

<div class="Body"><a name="pgfId=7037"></a>In a command stream, the last
directory name is always the destination tree. However, a wildcard character
(for example, the asterisk *) can be used as a floating directory for destination.
A floating directory is a sub-directory appended at the end of each source
directory as a destination directory for that source directory. The wildcard
character still must be the last directory name specified on command line.
If a name is followed by the wildcard character, it will be used as the
name of the sub-directory. If no name is followed by the wildcard character,
the default name "out" will be used.</div>

<div class="Body"><a name="pgfId=7038"></a>Following are some examples
to illustrate the usage:</div>

<div class="Code"><a name="pgfId=7039"></a><tt>interchange -xdwgout -pmyproject
e:\project1 \\neptune\project2 * -pIDabc</tt></div>

<div class="Body"><a name="pgfId=7040"></a>will put translated DWG files
on <i>e:\project1\out\</i> and <i>\\neptune\project2\out\ </i>respectively.</div>

<div class="Code"><a name="pgfId=7041"></a><tt>interchange -xdwgout -pmyproject
e:\project1 \\neptune\project2 *dwg -pIDabc</tt></div>

<div class="Body"><a name="pgfId=7042"></a>will put translated DWG files
on <i>e:\project1\dwg\
</i>and <i>\\neptune\project2\dwg\ </i>respectively.</div>

<div class="Body"><a name="pgfId=7043"></a>If the sub-directory name contains
a space character, make sure to quote it, for example:</div>

<div class="Code"><a name="pgfId=7044"></a><tt>interchange -xdwgout -pmyproject
e:\project1 \\neptune\project2 *"output dwg files" -pIDabc</tt></div>


<p class="Body"><a name="pgfId=7045"></a>will put translated DWG files
on <i>e:\project1\output dwg files\</i> and <i>\\neptune\project2\output
dwg files\</i> respectively.
<h2 class="h2">
<a name="pgfId=7034"></a>Batch conversions</h2>

<div class="Body"><a name="pgfId=5851"></a>Once started, Interchange runs
runs cycle after cycle. Between any two cycles the process sleeps for the
given interval minutes. If -i is not specified, it sleeps for 10 seconds
by default. If zero interval time is chosen, it runs continuously without
pause.
<br>&nbsp;
<h2>
<a name="project_configuration_files"></a>Project Configuration Files</h2>
A MicroStation Project Configuration File can be associated with a source
directory. This is done in the same association file for MicroStation BASIC,
i.e. the file specified with switch -x (e.g. -xdwgout,c:\data\BasicPcf.asoc).
The association file lists in such order: source file, MicroStation BASIC
file, and a semicolon followed by a PCF file. Note that PCF file specification
is optional. Here is an example what an association file should look like:
<dir><tt>k:\projects\Olympic2000\dgn f:\data\Olympic.bas;Olympic2000.pcf</tt>
<br><tt>k:\projects\Whitehouse f:\data\whitehouse.bas;f:\data\Whitehouse.pcf</tt>
<br><tt>k:\projects g:\defaultdata\dwgcontrol.bas</tt></dir>
In this example, Interchange will convert files whose root directory is
k:\projects\Olympic2000\dgn using the MicroStation BASIC file f:\data\Olympic.bas
and the project configuration file \Bentley\Workspace\projects\Olympic2000.pcf.
Note that a PCF file is recommended to be placed in MicroStation's project
directory, as set by the variable _USTN_PROJECT (\Bentley\Workspace\projects\
by default). If the project configuration file does not exist in that folder,
the full folder specification is required (e.g. f:\data\Whitehouse.pcf
set forth in the second line). The 3rd line lets Interchange use g:\defaultdata\dwgcontrol.bas
to process files whose root folder is k:\projects, but not on sub-directories
k:\projects\Olympic2000\dgn and k:\projects\Whitehouse, using g:\defaultdata\dwgcontrol.bas
with the default project configuration file.</div>

<h1 class="h1">
<a name="pgfId=5857"></a><a name="16596"></a>Common Procedures</h1>

<h2 class="Procedure">
<a name="pgfId=5858"></a>How to launch Interchange from a DOS prompt</h2>

<ol>
<li class="1Step">
<a name="pgfId=11979"></a>At a DOS prompt, key in Interchange followed
by the desired parameters.</li>
</ol>

<h2 class="Procedure">
<a name="pgfId=11983"></a>How to launch Interchange from a menu</h2>

<ol>
<li class="1Step">
<a name="pgfId=11984"></a>Use the path Programs > Viecon.interchange V7.1
> Viecon.interchange.</li>
</ol>

<div class="Item"><a name="pgfId=7647"></a>This opens a DOS window.</div>

<div class="2Step"><a name="pgfId=7653"></a>At this prompt, key in interchange
followed by the desired parameters.</div>

<h2 class="Procedure">
<a name="pgfId=7619"></a>How to launch Viecon.interchange as an NT service</h2>

<ol>
<li class="1Step">
<a name="pgfId=6447"></a><a href="#41375" class="XRef">See
Using Viecon.interchange as an NT Service</a></li>
</ol>

<h2 class="Procedure">
<a name="pgfId=5860"></a>To set up a watched directories</h2>

<ol>
<li class="1Step">
<a name="pgfId=6011"></a>The watched directory is specified in the command
line. The watched directory(ies) is the first path keyed in after the command
<i>interchange</i>
. One instance of <i>Interchange </i>can watch multiple directories by
keying in additional paths as shown in the earlier example. The final path
in the command line is the location to which the translated data will be
stored.</li>
</ol>

<h2 class="Procedure">
<a name="pgfId=7668"></a>How to run a single conversion</h2>

<ol>
<li class="1Step">
<a name="pgfId=7669"></a>Place a single file in the watched directory.</li>
</ol>

<h2 class="Procedure">
<a name="pgfId=5861"></a>How to use translation tables</h2>

<ol>
<li class="1Step">
<a name="pgfId=6463"></a>These files are stored in <i>\Bentley\home\msint.
</i>See
Chapters 3 and 4 in this guide and Chapter 6 of the File Portability/Translation
Guide.</li>
</ol>

<h2 class="h2">
<a name="pgfId=6705"></a>Retranslation Dependency</h2>

<div class="Body"><a name="pgfId=6442"></a>You may specify any combination
of MicroStation Basic, master, reference or none as triggers to retranslate
files. Each parameter can be added in any order, together or separated
by a comma.</div>

<div class="Body"><a name="pgfId=6340"></a>Examples:</div>

<div class="Body"><a name="pgfId=6342"></a><i>-dm,b</i> (or <i>-dmb</i>
) will re-translate files if either master file or MicroStation Basic file
is newer</div>

<div class="Body"><a name="pgfId=6344"></a>-<i> dr,m,b</i> (or <i>-drmb</i>
) will re-translate files if any of master, Basic and reference files is
newer</div>

<div class="Body"><a name="pgfId=6346"></a>-dfiles always get re-translated
regardless file status.</div>

<h2 class="h2">
<a name="pgfId=6348"></a>Translation Log viewing</h2>

<div class="Body"><a name="pgfId=6351"></a>The very first instance will
work exactly as it did in previous versions. Additional instance after
the first one while its still running, will add a process ID to the log
file. In case you may see multiple log files simultaneously, for example:</div>

<div class="Code"><a name="pgfId=6353"></a>\Bentley\Logs\interchange.files.log
-> first instance</div>

<div class="Code"><a name="pgfId=6354"></a>\Bentley\Logs\interchange.files67.log
-> another instance with process ID 67</div>

<div class="Code"><a name="pgfId=6355"></a>\Bentley\Logs\interchange.files204.log
-> another instance with process ID 204</div>

<div class="Body"><a name="pgfId=6357"></a>Other files that may have collision
problem are also handled in similar fashion but since they are internal
or for debug use we don't have to expose them to users. A log file is a
recorder of each batch run of translation. When re-translation started
it gets overridden by new batch. The dead files list always has the same
name and it is shared by all instances.</div>

<h1 class="Prod-h1top">
<a name="pgfId=6358"></a><a name="41375"></a>Using Viecon.interchange as
an NT Service</h1>

<div class="Body"><a name="pgfId=6754"></a>This section discusses the various
procedures required to run Viecon.interchange as an NT service.</div>

<h2 class="Procedure">
<a name="pgfId=6527"></a>To Install Viecon.interchange as an NT Service</h2>

<ol>
<li class="1Step">
<a name="pgfId=6548"></a>On the command line, key in: <tt>icserve -install</tt></li>
</ol>

<ul>
<li class="Note">
<a name="pgfId=6549"></a>If installation succeeds, you will get a prompt:
"Bentley Interchange installed."</li>
</ul>

<h2 class="Procedure">
<a name="pgfId=6670"></a>To start Viecon.interchange service from a DOS
window</h2>

<ol>
<li class="1Step">
<a name="pgfId=6553"></a>On the command line: <tt>net start interchange</tt></li>
</ol>

<ul>
<li class="Note">
<a name="pgfId=6554"></a>If service has started successfully, you will
get a prompt: "The Bentley Interchange service was started successfully."</li>
</ul>

<h2 class="Procedure">
<a name="pgfId=6677"></a>To start Viecon.interchange service from a menu
(Windows NT)</h2>

<ol>
<li class="1Step">
<a name="pgfId=6555"></a>On the desktop, go to Start menu, select Settings,
then open Control Panel folder. Double click on Services icon. Select Bentley
Viecon.interchange. Click on Start button.</li>
</ol>

<h2 class="Procedure">
<a name="pgfId=6677"></a>To start Viecon.interchange service from a menu
(Windows 2000)</h2>

<ol>
<li class="1Step">
<a name="pgfId=6555"></a>On the desktop, go to Start menu, select Settings,
then open Control Panel folder. Double click on Administrative Tools icon.
Select Bentley Viecon.interchange. Click on Start button.</li>
</ol>

<h2 class="h2">
<a name="pgfId=6742"></a>NT Service logs</h2>

<div 
class="Body"><a name="pgfId=6743"></a>This log is stored in binary
form in \Bentley\logs\trace.log. This binary file can be viewed by keying
in:</div>

<div class="Code"><a name="pgfId=6744"></a><tt>blogdump</tt></div>

<div class="Body"><a name="pgfId=6557"></a>This will display the logs created
by the NT service from the start of the process up to the current moment.</div>

<h2 class="Procedure">
<a name="pgfId=6564"></a>To Stop Viecon.interchange service from the command
line</h2>

<ol>
<li class="1Step">
<a name="pgfId=6566"></a>On command line, key-in:<tt> net stop interchange</tt></li>
</ol>
"The Bentley Interchange service was stopped successfully." prompts on
success.
<h2 class="Procedure">
<a name="pgfId=6782"></a>To Stop Viecon.interchange service from a dialog
box</h2>

<ol>
<li class="1Step">
<a name="pgfId=6805"></a>In the service control manager dialog box, select
Bentley Interchange and click on Stop button.</li>
</ol>

<div class="Item"><a name="pgfId=6814"></a>"The Bentley Interchange service
was stopped successfully." prompts on success.</div>

<h2 class="Procedure">
<a name="pgfId=6796"></a>To uninstall Viecon.interchange from your desktop</h2>

<ol>
<li class="1Step">
<a name="pgfId=6571"></a>On command line, key-in:<tt> icserve -remove</tt></li>
</ol>
"Bentley Interchange removed." prompts on success.

<p class="Body"><a name="pgfId=6574"></a>As you can see these actions all
come in pairs, start-stop, install-remove. Service is only an option and
you can still run Interchange as you did before. The only caution is that
you must run these paired actions in the correct order when you run the
service.
<h2 class="h2">
<a name="pgfId=6827"></a>Status checks</h2>

<div class="Body"><a name="pgfId=6835"></a>During the course of running
the service, you can check status using command <i>blogdump</i> which dumps
history of the interchange service to the DOS shell. Use the command blogdump
-? to see more options using this program. The log data is stored in \Bentley\Logs\trace.log
which grows and wraps around when the default log size limit is reached.
If you delete the log file, a new one will be created when you start the
service again.</div>

<h2 class="h2">
<a name="pgfId=6904"></a>Control Interchange</h2>

<div class="Body"><a name="pgfId=6905"></a>In the current implementation,
an installed service does provide interactive user access to users. A manual
intervention option described below.</div>

<div class="Body"><a name="pgfId=6906"></a>An access hook on interchange
can be performed manually. The access hook requires a specified command
file, <i>\Bentley\Program\interchange\temp\!msint.cmd.macro! </i>This file
should include all commands, source directories and destinations. You can
use a text editor to create a command file (refer to the specs for a sample)
and copy the file to \Bentley\Program\interchange\temp\!msint.cmd.macro!.
Interchange server constantly waits for this file. It does not act until
this file is present and valid. If you happened to created an invalid file,
interchange will rename it to #iccmd.save# and enter the wait state again
until a new!msint.cmd.macro! is present. If the file is valid, interchange
will put it into action and begin to search files to synchronize source
and targets.</div>

<div class="Body"><a name="pgfId=6907"></a>At the end of each run, interchange
always checks for <i>!msint.cmd.macro!</i> and updates the input if necessary.</div>

<h1 class="h1">
<a name="pgfId=6939"></a><a name="28010"></a>Floating Directory Appended
on Source Trees</h1>

<div class="Body"><a name="pgfId=6941"></a>In a command stream, the last
directory name is always the destination tree. However, a wildcard character
(for example, the asterisk *) can be used as a floating directory for destination.
A floating directory is a sub-directory appended at the end of each source
directory as a destination directory for that source directory. The wildcard
character still must be the last directory name specified on command line.
If a name is followed by the wildcard character, it will be used as the
name of the sub-directory. If no name is followed by the wildcard character,
the default name "out" will be used.</div>

<div class="Body"><a name="pgfId=6996"></a>Following are some examples
to illustrate the usage:</div>

<div class="Code"><a name="pgfId=6943"></a>interchange -xdwgout -pmyproject
e:\project1 \\neptune\project2 * -pIDabc</div>

<div class="Body"><a name="pgfId=6945"></a>will put translated DWG files
on <i>e:\project1\out\</i> and <i>\\neptune\project2\out\ </i>respectively.</div>

<div class="Code"><a name="pgfId=6947"></a>interchange -xdwgout -pmyproject
e:\project1 \\neptune\project2 *dwg -pIDabc</div>

<div class="Body"><a name="pgfId=6949"></a>will put translated DWG files
on <i>e:\project1\dwg\
</i>and <i>\\neptune\project2\dwg\ </i>respectively.</div>

<div class="Body"><a name="pgfId=6951"></a>If the sub-directory name contains
a space character, make sure to put the name is quotes, for example:</div>

<div class="Code"><a name="pgfId=6953"></a><tt>interchange -xdwgout -pmyproject
e:\project1 \\neptune\project2 *"output dwg files" -pIDabc</tt></div>


<p class="Body"><a name="pgfId=7023"></a>will put translated DWG files
on <i>e:\project1\output dwg files\</i> and <i>\\neptune\project2\output
dwg files\</i> respectively.
<h1 class="Prod-h1top">
<a name="pgfId=7680"></a><a name="31385"></a>Command File Usage</h1>

<div class="Body"><a name="pgfId=7748"></a>When multiple source files and
command line parameters are used, a command file is strongly recommended.
A command file is called with the following string:</div>

<div class="Code"><a name="pgfId=7751"></a>interchange @mycommands.list</div>

<div class="Body"><a name="pgfId=7753"></a>where <i>mycommands.list
</i>may
appear as:</div>

<div class="Code"><a name="pgfId=7755"></a># List of all commands, source
and/or destination files/paths. It is</div>

<div class="Code"><a name="pgfId=7756"></a># a convenient way for processing
mutiple files at a time.</div>

<div class="Code"><a name="pgfId=7757"></a># For example, add DGN files/paths
into a variable, say dgnFiles:</div>

<div class="Code"><a name="pgfId=7758"></a>#</div>

<div class="Code"><a name="pgfId=7759"></a># dgnFiles = c:/dgns/project1
\</div>


<p class="Code"><a name="pgfId=7760"></a># c:/dgns/project2 \

<p class="Code"><a name="pgfId=7761"></a># c:/dgns/project3 \

<p class="Code"><a name="pgfId=7762"></a># c:/dwgs/allProjects

<p class="Code"><a name="pgfId=7763"></a># and put it into command line:

<p class="Code"><a name="pgfId=7764"></a># -s -xdwgout $(dgnFiles)

<p class="Code"><a name="pgfId=7765"></a>#

<p class="Code"><a name="pgfId=7766"></a># Use an asterisk character *
at the end of the file list to avoid

<p class="Code"><a name="pgfId=7767"></a># explicit destination. Thus all
files are to be exported to the same

<p class="Code"><a name="pgfId=7768"></a># source paths under a sub-directory
\out.

<p class="Code"><a name="pgfId=7769"></a>%if import=1

<p class="Code"><a name="pgfId=7770"></a>######### do import

<p class="Code"><a name="pgfId=7771"></a>%else # do export

<p class="Code"><a name="pgfId=7772"></a>fileList = k:/project1 \

<p class="Code"><a name="pgfId=7773"></a>"c:/a path with spaces must use
quotes/project2" \

<p class="Code"><a name="pgfId=7774"></a>//mars.abc.com/project3 \

<p class="Code"><a name="pgfId=7775"></a>*

<p class="Code"><a name="pgfId=7776"></a>transOptions = -xdwgout -s

<p class="Code"><a name="pgfId=7777"></a>$(fileList) $(transOptions)

<p class="Code"><a name="pgfId=7778"></a>%endif

<p class="Code"><a name="pgfId=7879"></a>
<h1 class="Prod-h1top">
<a name="pgfId=7880"></a><a name="14897"></a>Simultaneous Export and Import</h1>

<div class="Body"><a name="pgfId=7881"></a>Viecon.Interchange can perform
both import and export at the same time. To run <i>dwgin</i> and <i>dwgout</i>
from the same command file you need to separate the execution command according
to an environment variable. For example, in window 2 you will perform file
import. You may set an NT environment variable in the window (only in desired
window) like this:</div>

<div class="Code"><a name="pgfId=7909"></a><tt>set abc_dwg2dgn=1</tt></div>

<div class="Body"><a name="pgfId=7911"></a>This variable can also be set
in the batch file with which that you start your import window each time.
Do not set it as a system environment variable via System Properties dialog
box on NT. After you set above environment variable for the desired window,
you can write your command file to look like this:</div>

<div class="Code"><a name="pgfId=7914"></a>...</div>

<div class="Code"><a name="pgfId=8066"></a>#### add your folder list</div>

<div class="Code"><a name="pgfId=7915"></a>FolderList = folder1 \</div>

<div class="Code"><a name="pgfId=7916"></a>folder 2 \</div>

<div class="Code"><a name="pgfId=7917"></a>...</div>


<p class="Code"><a name="pgfId=7918"></a>

<p class="Code"><a name="pgfId=7919"></a>%if defined $(abc_dwg2dgn)

<p class="Code"><a name="pgfId=7920"></a>action = -xdwgin,g:\Bentley\Home\msint\303-MID-primary-assoc.list

<p class="Code"><a name="pgfId=7921"></a>%else

<p class="Code"><a name="pgfId=7922"></a>action = -xdwgout,g:\Bentley\Home\msint\303-MID-primary-assoc.list

<p class="Code"><a name="pgfId=7923"></a>%endif

<p class="Code"><a name="pgfId=7924"></a>

<p class="Code"><a name="pgfId=7925"></a>### now take the action according
to environment variable set forth:

<p class="Code"><a name="pgfId=7926"></a>

<p class="Code"><a name="pgfId=7927"></a>$(FolderList) $(action) -p,303-MID-Current.pcf

<p class="Code"><a name="pgfId=7928"></a>

<p class="Code"><a name="pgfId=7929"></a>### end

<p class="Body"><a name="pgfId=7930"></a>

<p class="Body"><a name="pgfId=7931"></a>The command file shown above can
thus be used for both import and export, but you must make sure the window
in which interchange is running has the correct environment variable set.
If incorrectly set, you will get two export instances.

<p class="Body"><a name="pgfId=7933"></a>You can have two or more instances
of viecon.interchange running simultaneously. However, there are a few
details that will ensure reliable operation of the code.
<ul>
<li class="Bullet">
<a name="pgfId=7935"></a>Avoid using the same destination for two instances
of interchange. If a translated file is to be written to the same output
file, there may be unexpected results.</li>

<li class="Bullet">
<a name="pgfId=8021"></a>Avoid a round-trip processing. For example, the
following chain is not recommended. The source files of instance 1 become
the destination files of instance 2 and the destination files of instance
1 become source files for instance 2. Such a circulation can cause unexpected
results.</li>

<li class="Bullet">
<a name="pgfId=8022"></a>Be sure to clean up the <i>\Bentley\Logs\ </i>directory
prior to running multiple instances so you will not be confused by logs
generated from last time interchange was run.</li>
</ul>

<div class="Body"><a name="pgfId=7941"></a></div>

<div class="Body"><a name="pgfId=8027"></a>When you want to use multiple
project configuration files in multiple instances environment, you can
also use the NT environment variables for windows, as we did above. For
example each window can have its own _USTN_PROJECTNAME set to activate
different project configuration files.</div>

<h1 class="h1">
<a name="pgfId=8206"></a>Glossary</h1>

<div class="Body"><a name="pgfId=8209"></a>Definitions, acronyms, and abbreviations
pertaining to viecon.interchange.</div>

<div class="Body"><a name="pgfId=8210"></a><i>Conversion configuration
or settings files</i> - MicroStation BASIC files and any mapping tables
involved in a conversion. These may be stored locally or in ModelVista's
data store.</div>

<div class="Body"><a name="pgfId=8211"></a><i>Command File</i> - a file
that contains command line options and source and/or destination specifications.
It supports simple statement logic (such as, %if, %else, %endif, etc.)
and configuration variable expansions using $(...). It can be used to customize
the viecon.interchange process on a per-project basis.</div>

<div class="Body"><a name="pgfId=8212"></a><i>Destination or destination
or output directories</i> - the root directory where the output of the
conversion is placed.</div>

<div class="Body"><a name="pgfId=8213"></a><i>Destination file extension</i>
- interchange will automatically assign a file extension of .dgn when it
is performing a DWG to DGN conversion. It will automatically assign a file
extension of .dwg when doing a DGN to DWG conversion, and an extension
of .dxf for DGN to DXF conversions.</div>

<div class="Body"><a name="pgfId=8214"></a><i>Execution frequency</i> -
a time interval that interchange pauses between the end of a conversion
iteration and the start of the next.</div>

<div class="Body"><a name="pgfId=8215"></a><i>File extension or type</i>
- the part of the file name that is typically to the right of the period
(.) in a file name. The file extension is typically (but not always) three
characters. Using file extensions, a user can specify which files in the
source directory are unwanted files that they want to filter out or ignore.
For example, in order to convert only DWG files from a source directory
that contains both DWG and DXF files, the user can specify to filter out
the .dxf file extension.</div>

<div class="Body"><a name="pgfId=8216"></a><i>Source directories</i> -
the directories where the source design data resides. This is where interchange
will scan for data to convert, comparing what is there against a database
that contains file date and time information for each respective destination
file. When one or more files in the source directory changes, the file(s)
will be converted -- the result of the translation will populate the destination
directory.</div>


