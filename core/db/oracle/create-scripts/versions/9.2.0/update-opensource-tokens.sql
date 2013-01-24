/* 
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
*/

/** 
 * Enterprise deployment specific Configuration settings.
 *
 * The following tokens are set to configure the Project.net application for enterprise deployment.  Run this script
 * after the project.net creation or update scripts have been run.  
 * 
 * This SQL script can be run maunually as follows:
 * sqlplus pnet/password-for-pnet@your-database < update-opensource-tokens.sql
 * 
 *
 * prm.global.brand.logo.login/images/logo/powered_by_pnet_150.gif
 * prm.global.brand.logo.login.href	http://www.project.net
 * prm.global.poweredby.isenabled1
 * prm.global.footer.alignmentcenter
 * prm.global.footer.poweredby.logo	/images/logo/powered_by_pnet_150.gif  (new token)
 * prm.global.footer.poweredby.href	http://www.project.net
 * prm.global.footer.copyright.newline	1
 * prm.global.footer.copyrightCopyright 2000-2008 Project.net, Inc.
 * prm.global.footer.copyright.isenabled	1
 * prm.global.footer.copyright.href.isenabled	1
 * prm.global.footer.copyright.href	http://www.project.net
 * 
 * The following token seetings are reccomended for open source deployments:
 *  	
 * prm.global.license.create.creditcard.isenabled0
 * prm.global.license.create.defaultchargecode.isenabled 	0 
 * prm.global.license.create.defaultlicensekey.isenabled 	1 
 * prm.global.license.create.enteredlicensekey.isenabled 	1
 * prm.global.license.create.ischargecodeenabled 0
 * prm.global.license.create.trial.isenabled 1
 * prm.global.license.isenabled 1
 * prm.global.license.login.islicenserequired 0
 * prm.global.license.registration.isenabled1   
 * prm.global.registration.termsofuse.isenabled 0
 * prm.project.create.termsofuse.isenabled 0
 * prm.global.legal.termsofuseCopyright Project.net and others.  Licensed under the Project.net Public License 1.0 which can be viewed here: http://dev.project.net/licenses/PPL1.0/
 */


/* Updating required tokens for all brands. */
/* These should not be overridden by any brand configuration without a commercial license or written agreement from Project.net, inc. */

update pn_property set property_value ='/images/logo/powered_by_pnet_150.gif' where property='prm.global.brand.logo.login'
/
update pn_property set property_value ='http://www.project.net' where property='prm.global.brand.logo.login.href'
/
update pn_property set property_value ='0' where property='prm.global.poweredby.isenabled'
/
update pn_property set property_value ='center' where property='prm.global.footer.alignment'
/
update pn_property set property_value ='/images/logo/powered_by_pnet_150.gif' where property='prm.global.footer.poweredby.logo'
/
update pn_property set property_value ='http://www.project.net' where property='prm.global.footer.poweredby.href'
/
update pn_property set property_value ='0' where property='prm.global.footer.copyright.newline'
/
update pn_property set property_value ='Copyright 2000-2009 Project.net, Inc.' where property='prm.global.footer.copyright'
/
update pn_property set property_value ='1' where property='prm.global.footer.copyright.isenabled'
/
update pn_property set property_value ='1' where property='prm.global.footer.copyright.href.isenabled'
/
update pn_property set property_value ='http://www.project.net' where property='prm.global.footer.copyright.href'
/

/* Updating reccommended open source tokens only for the Defualt configuration */

update pn_property set property_value ='0' where property='prm.global.license.create.creditcard.isenabled' and context_id='2000'
/
update pn_property set property_value ='0' where property='prm.global.license.create.defaultchargecode.isenabled' and context_id='2000'
/
update pn_property set property_value ='1' where property='prm.global.license.create.defaultlicensekey.isenabled' and context_id='2000'
/
update pn_property set property_value ='1' where property='prm.global.license.create.enteredlicensekey.isenabled' and context_id='2000'
/
update pn_property set property_value ='0' where property='prm.global.license.create.ischargecodeenabled' and context_id='2000'
/
update pn_property set property_value ='1' where property='prm.global.license.create.trial.isenabled' and context_id='2000'
/
update pn_property set property_value ='1' where property='prm.global.license.isenabled' and context_id='2000'
/
update pn_property set property_value ='0' where property='prm.global.license.login.islicenserequired' and context_id='2000'
/
update pn_property set property_value ='1' where property='prm.global.license.registration.isenabled' and context_id='2000'
/
update pn_property set property_value ='0' where property='prm.global.registration.termsofuse.isenabled' and context_id='2000'
/
update pn_property set property_value ='0' where property='prm.project.create.termsofuse.isenabled' and context_id='2000'
/
update pn_property set property_value ='Copyright Project.net and others.  Licensed under the GNU General Public License 3.0 which can be viewed here: http://www.gnu.org/licenses/gpl-3.0.html' 	where property='prm.global.legal.termsofuse' and context_id='2000'
/

