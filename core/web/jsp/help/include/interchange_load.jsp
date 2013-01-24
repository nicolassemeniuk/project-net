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
<a name="interchange_load"></a>

<table border="0" width="80%" cellpadding="0" cellspacing="0">
<tr class="channelHeader">
	<td width=1%><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-left_end.gif" width=8 height=15 alt="" border="0"></td>
	<td nowrap class="channelHeader"><nobr>Loading viecon.interchange</nobr></td>
	<td align=right width=1%><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif" width=8 height=15 alt="" border="0"></td>
</tr>
</table>
<p>
<h1 class="ChapterTitle">
<a name="pgfId=1252"></a>Introduction, Setup and Installation</h1>

<div class="BodyAfterTitle"><a name="pgfId=4080"></a>This chapter covers
product capabilities, setup, and installation procedures for Viecon.interchange<sup>TM</sup>.</div>

<h1 class="h1">
<a name="pgfId=5825"></a>Overview</h1>

<div class="Body"><a name="pgfId=6106"></a>Viecon.interchange simplifies
the process of maintaining synchronized design data in DGN and DWG/DXF
formats by automating the conversion of design data. It is a standalone
utility built from the same source code used for MicroStation. Viecon.interchange
works by "watching" specified directories or folders containing DGN and/or
DWG/DXF files or their dependencies (e.g. MicroStation BASIC settings file,
reference files, etc.) When Interchange detects that a source file or one
of its dependencies has changed, it automatically creates or updates the
respective destination file. Any source file that has not changed since
the last time it was synchronized with its respective destination file
is left alone. Interchange recycles through the specified directories/folders
after a user-specified interval. Interchange supports MicroStation design
files and AutoCAD DWG/DXF file formats up to and including A2K. Viecon.interchange
is currently a standalone utility; downloading and installation on a host
computer is required.</div>

<div class="Body"><a name="pgfId=6217"></a>With the supported command options,
users can specify:</div>

<ul>
<li class="Bullet">
<a name="pgfId=6226"></a>How often they want the program run between iterations.</li>

<li class="Bullet">
<a name="pgfId=6227"></a>Where the source files are located</li>

<li class="Bullet">
<a name="pgfId=6228"></a>Where the destination/destination files should
be.</li>
</ul>

<div class="Body"><a name="pgfId=6164"></a>Viecon.interchange supports
the synchronized conversions specified below:</div>

<ul>
<li class="Bullet">
<a name="pgfId=6111"></a>DWG and DXF to DGN</li>

<li class="Bullet">
<a name="pgfId=6112"></a>DWG or DXF to DGN</li>

<li class="Bullet">
<a name="pgfId=6113"></a>DGN to DWG</li>

<li class="Bullet">
<a name="pgfId=6114"></a>DGN to DXF</li>
</ul>

<h1 class="h1">
<a name="pgfId=5997"></a>Installation</h1>

<div class="Body">
<ol>
<li>
Go to the location specified on the Download page.</li>

<div class="Body">
<li>
The installation procedure for Viecon.interchange is straightforward using
the Web-based InstallShield to download and install this product. Click
to begin the download and installation process.</li>

<li>
When the License notification appear, click Yes.</li>

<li>
Verify (or change) the installed directory path. Note the directory where
the program is downloaded to. The license will have to be installed in
this path. It is suggested to place Viecon.interchange in a path different
from that used for MicroStation (if that program is on the same server).</li>

<li>
Click Next.</li>

<li>
Select the options you wish to install. Both should be installed. If you
do not require solids, you need not load the Parasolid/ACIS utilities.
Without this optional code, all solids are dropped to wireframe primitives
or the solids may be ignored as an option by specifying this in the basic
program as shown in Chapter Six of the File Portability/Translation Guide.</li>

<li>
Select the type of installation you wish to perform. This type is based
on how you want Viecon.interchange to run.</li>

<li>
Click Next.</li>

<li>
The software is now ready to be installed. Click Next again to begin installing
the software.</li>

<li>
When the installation is complete, please read the Readme file.</li>
</div>
</ol>
</div>

<h2 class="h1">
<a name="pgfId=6052"></a>Licensing</h2>

<div class="h1">In addition to installing the software, you must also download
a license file.</div>

<div class="h1">
<ol>
<li>
Download the license from the path http://www.viecon.com/applications/interchange/lic.html.</li>

<li>
Please save the license file as interchange.lic in the \Bentley\Program\licensing\
directory/folder on the drive when you have installed Viecon.interchange.</li>
</ol>
</div>

<h2 class="h1">
<a name="pgfId=5837"></a>System Requirements</h2>

<ul>
<li class="h1">
At least 300MHz Pentium class computer or workstation (server preferred)</li>

<li class="h1">
At least 128MB physical RAM (more recommended).</li>

<li class="h1">
Microsoft Windows 2000, Windows NT 4 or NT Server.</li>

<li class="h1">
Sufficient disk space on the destination drive to accommodate data conversion.</li>
</ul>

<div class="Body"><a name="pgfId=6001"></a>As this program operates as
a stand-alone application and includes only those portions of MicroStation
needed to run the translation, a working copy of MicroStation is not needed.</div>

<h2 class="h2">
<a name="pgfId=5840"></a>Disk Space</h2>

<ul>
<li class="Note">
<a name="pgfId=5841"></a>During the installation process a dependency database
is generated and maintained by Viecon.interchange on an output tree. Do
not delete this file because Viecon.interchange will have to regenerate
it the next time it is run. When the code checks to see if this file has
been created, and if it is not there, every file will be translated again.</li>
</ul>

<div class="Body">
<h2>
<a name="pgfId=5843"></a>Footprint</h2>
</div>
Download is approximately 21MB. Complete install occupies approximately
44MB of disk space.
<h1 class="h1">
<a name="pgfId=5906"></a><a name="20861"></a>Installing Viecon.interchange
as an NT Service</h1>

<div class="h1">During Installation, there is an option to have installed
Interchange as an NT service.</div>

<div class="h1">If you always want Interchange to run as an NT service
and it has not been installed as such, then uninstall Interchange, and
reinstall it, this time selecting the Install as NT service option.</div>

<p><br>&nbsp; Starting Interchange as an NT service is also discussed in
the next <a href="Help.jsp?page=interchange&section=use">Chapter</a>.


