
--select * from PN_PROPERTY a where a.PROPERTY like '%prm.project.report.businessprojectsfinancialreport.xslpath%';

--update PN_PROPERTY a set a.property_value='/report/xsl/businessprojectsfinancialreport.xsl' where a.PROPERTY like '%prm.project.report.businessprojectsfinancialreport.xslpath%';
-- select * from PN_PROPERTY a where a.PROPERTY like '%prm.global.header%';

-- Project Create
insert into PN_PROPERTY values (2000,'en','text','prm.project.create.wizard.financialcalculation.heading','Project Finance Calculation Method','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.project.create.wizard.costcenter.label','Cost Center','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.project.create.wizard.budgetedtotalcost.label','Budgeted Total Cost','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.project.create.wizard.costcompletion.select','Calculation Method','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.project.create.wizard.completion.projectcosts.label','From Project Costs Data','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.project.create.wizard.completion.projectcosts.description','Dinamically set the project costs from the costs data.','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.project.create.wizard.costs.label','Global costs values','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.project.create.wizard.costs.description','Insert manually the project costs values.','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.project.create.wizard.currentestimatedtotalcost.label','Current Estimated Total Cost','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.project.create.wizard.materialscurrentestimatedtotalcost.label','Materials Current Estimated Total Cost','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.project.create.wizard.resourcescurrentestimatedtotalcost.label','Resources Current Estimated Total Cost','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.project.create.wizard.actualcosttodate.label','Actual Cost To Date','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.project.create.wizard.materialsactualcosttodate.label','Materials Actual Cost To Date','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.project.create.wizard.resourcesactualcosttodate.label','Resources Actual Cost To Date','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.project.create.wizard.estimatedroi.label','Estimated ROI','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.project.create.wizard.discretionalcost.label','Discretional Cost','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.project.create.wizard.discretionalactualcosttodate.label','Discretional Actual Cost To Date','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.project.create.wizard.discretionalcurrentestimatedtotalcost.label','Discretional Current Estimated Total Cost','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.project.create.wizard.budgetedtotalcost','Budgeted Total Cost','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.project.create.wizard.costcenter','Cost Center','A',0,1,null);

-- Project Edit
insert into PN_PROPERTY values (2000,'en','text','prm.project.propertiesedit.channel.finanacialcompletion.title','Project Finance Calculation Method','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.project.propertiesedit.costcompletion.select','Calculation Method','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.project.propertiesedit.completion.projectcosts.label','From Project Costs Data','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.project.propertiesedit.completion.projectcosts.description','Dinamically set the project costs from the costs data.','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.project.propertiesedit.costs.label','Global costs values','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.project.propertiesedit.costs.description','Insert manually the project costs values.','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.project.propertiesedit.discretionalactualcosttodate.label','Discretional Actual Cost To Date','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.project.propertiesedit.discretionalcurrentestimatedtotalcost.label','Discretional Current Estimated Total Cost','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.project.propertiesedit.materialscurrentestimatedtotalcost.label','Materials Current Estimated Total Cost','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.project.propertiesedit.resourcescurrentestimatedtotalcost.label','Resources Current Estimated Total Cost','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.project.propertiesedit.materialsactualcosttodate.label','Materials Actual Cost To Date','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.project.propertiesedit.resourcesactualcosttodate.label','Resources Actual Cost To Date','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.project.propertiesedit.prm.project.propertiesedit.discretionalactualcosttodateshouldbenumber.message','Discretional Actual Cost To Date should be a valid currency number','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.project.propertiesedit.discretionalcurrentestimatedtotalcostshouldbenumber.message','Discretional Current Estimated Total Cost should be a valid currency number','A',0,1,null);

--Project Meta Properties
insert into pn_project_space_meta_prop values (8,'CostCalculationMethod', 3);
insert into pn_project_space_meta_prop values (9,'DiscretionalActualCostToDate', 1);
insert into pn_project_space_meta_prop values (10,'MaterialsActualCostToDate', 1);
insert into pn_project_space_meta_prop values (11,'ResourcesActualCostToDate', 1);
insert into pn_project_space_meta_prop values (12,'DiscretionalCurrentEstimatedTotalCost', 1);
insert into pn_project_space_meta_prop values (13,'MaterialsCurrentEstimatedTotalCost', 1);
insert into pn_project_space_meta_prop values (14,'ResourcesCurrentEstimatedTotalCost', 1);

--Project status for financial calculation
insert into PN_PROPERTY values (2000,'en','boolean','prm.financial.project.notstarted.isenabled','0','A',0,0,null);
insert into PN_PROPERTY values (2000,'en','boolean','prm.financial.project.inprocess.isenabled','1','A',0,0,null);
insert into PN_PROPERTY values (2000,'en','boolean','prm.financial.project.onhold.isenabled','1','A',0,0,null);
insert into PN_PROPERTY values (2000,'en','boolean','prm.financial.project.completed.isenabled','1','A',0,0,null);
insert into PN_PROPERTY values (2000,'en','boolean','prm.financial.project.proposed.isenabled','0','A',0,0,null);
insert into PN_PROPERTY values (2000,'en','boolean','prm.financial.project.inplanning.isenabled','0','A',0,0,null);

-- Space definition
insert into PN_PROPERTY values (2000,'en','text','prm.application.nav.space.financial','Financial','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','boolean','prm.financial.isenabled','1','A',0,0,null);
insert into PN_PROPERTY values (2000,'en','text','prm.space.spacetypes.financial.name','Financial','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','css','prm.global.css.financial','/styles/financial.css','A',0,0,null);

-- Financial portfolio
insert into PN_PROPERTY values (2000,'en','text','prm.financial.financialportfolio.title','Business List','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.financial.financialportfolio.channel.memberof.title', 'Business finances you have access to','A',0,1,null);

insert into PN_PROPERTY values (2000,'en','text','prm.financial.financialportfolio.financial.label','Business','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.financial.financialportfolio.totalactualcost.label','Total Actual Cost','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.financial.financialportfolio.totalestimatedcost.label','Total Estimated Current Cost','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.financial.financialportfolio.totalbudgetedcost.label','Total Budgeted Cost','A',0,1,null);

-- Module
insert into PN_MODULE values (175,'financial_space','@prm.financialspace.module.description', 1, null);
insert into PN_PROPERTY values (2000,'en','text','prm.finacialsspace.module.description','Financial Workspace','A',0,1,null);

-- Object type
insert into PN_OBJECT_TYPE values ('financial','pn_financial_space','@prm.global.financial.objecttype.financial',null,1,0,0);
insert into PN_PROPERTY values (2000,'en','text','prm.global.financial.objecttype.financial','Financial','A',0,1,null);
insert into PN_OBJECT_TYPE values ('salary','pn_person_salary','@prm.global.financial.objecttype.salary',null,1,0,0);
insert into PN_PROPERTY values (2000,'en','text','prm.global.financial.objecttype.salary','Person Salary','A',0,1,null);

-- Financial - Dashboard
insert into PN_PROPERTY values (2000,'en','text','prm.financialspace.module.description', 'Dashboard','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','boolean','prm.financial.dashboard.isenabled','1','A',0,0,null);
insert into PN_PROPERTY values (2000,'en','text','prm.financial.dashboard.title','Dashboard','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.financial.dashboard.toolbox.title','Toolbox','A',0,1,null);

insert into PN_PROPERTY values (2000,'en','text','prm.financial.main.personalize.button.tooltip','Personalize Page','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.financial.dashboard.goto.title','Go to','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.financial.dashboard.goto.reports.label','Reports','A',0,1,null);

-- Financial - Dashboard - Projects Channel
insert into PN_PROPERTY values (2000,'en','text','prm.financial.dashboard.projects.channel.title','Projects Costs','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.financial.dashboard.projects.noprojects.label','There are no projects on the related business','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.financial.dashboard.projects.name.label','Project','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.financial.dashboard.projects.costcenter.label','Cost Center','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.financial.dashboard.projects.budgetedtotalcost.label','Budgeted','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.financial.dashboard.projects.currentestimatedtotalcost.label','Estimated','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.financial.dashboard.projects.actualcosttodate.label','Actual','A',0,1,null);

-- Financial - Dashboard - Project Total Costs Channel
insert into PN_PROPERTY values (2000,'en','text','prm.financial.dashboard.projecttotalcostschart.channel.title','Project Total Costs','A',0,1,null);

-- Financial - Dashboard - Financial Members Channel
insert into PN_PROPERTY values (2000,'en','text','prm.financial.dashboard.financialteam.channel.title','Financial Team','A',0,1,null);

-- Financial - Dashboard - Actual Costs Types Over Total
insert into PN_PROPERTY values (2000,'en','text','prm.financial.dashboard.actualcoststypesovertotalchart.channel.title','Actual Costs Types Over Total','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.financial.dashboard.chart.actualcoststypesovertotal.legend.materialsactualcosttodate.name','Materials','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.financial.dashboard.chart.actualcoststypesovertotal.legend.resourcesactualcosttodate.name','Resources','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.financial.dashboard.chart.actualcoststypesovertotal.legend.actualdiscretionalscosttodate.name','Discretional','A',0,1,null);

-- Financial - Directory
insert into PN_PROPERTY values (2000,'en','boolean','prm.financial.directory.isenabled','1','A',0,0,null);
insert into PN_PROPERTY values (2000,'en','text','prm.financial.nav.directory','Directory','A',0,1,null);

-- Schedule Properties
insert into PN_PROPERTY values (2000,'en','text','prm.schedule.tasklistdecorating.hasFinancialCosts.label','Task has financial costs','A',0,1,null);

-- Salary - Financial
insert into PN_PROPERTY values (2000,'en','boolean','prm.financial.salary.isenabled','1','A',0,0,null);
insert into PN_MODULE values (270,'salary','@prm.salary.module.description', 0, null);
insert into PN_PROPERTY values (2000,'en','text','prm.salary.module.description','Salary','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.financial.nav.salary','Salary','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.financial.salary.modify.button.tooltip','Modify Salary','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.financial.salary.create.button.tooltip','New Salary','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.financial.salary.tab.salary.title','Salary','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.financial.salary.roster.search.label','Search','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.financial.salary.tab.participants.title','Participants','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.financial.salary.title','Salary','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.directory.directorypage.roster.column.costbyhour','Cost By Hour','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.financial.salary.authorizationfailed.message','Failed security validation','A',0,1,null);

-- Salary - Personal - Salary history
insert into PN_PROPERTY values (2000,'en','boolean','prm.personal.salary.isenabled','1','A',0,0,null);
insert into PN_PROPERTY values (2000,'en','text','prm.personal.nav.salary','Salary','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.personal.salary.title','Salary','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.personal.salary.create.button.tooltip','New Salary','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.personal.salary.modify.button.tooltip','Modify Salary','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.personal.salary.tab.salaryhistory.title','Salary History','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.personal.salary.tab.periods.title','Periods','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.personal.salary.roster.searchFrom.label','From','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.personal.salary.roster.searchTo.label','To','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.personal.salary.roster.column.startdate','Start Date','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.personal.salary.roster.column.enddate','End Date','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.personal.salary.roster.column.costbyhour','Cost By Hour','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.personal.salary.roster.searchfieldfromincorrectformat.message','The start date is incorrect','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.personal.salary.roster.searchfieldtoincorrectformat.message','The end date is incorrect','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.personal.salary.roster.searchdatesincorrectrange.message','The start date cannot be previous to the last start date','A',0,1,null);

-- Salary - Personal - Modify salary
insert into PN_PROPERTY values (2000,'en','text','prm.personal.salarymodifypage.title','Modify Salary','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.personal.salary.modify.pagetitle','Modify Salary','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.personal.salary.modify.channel.costbyhour.title','Insert new salary value','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.personal.salary.modify.channel.costbyhour.warning','Warning: once you save a new salary period the date range can not be modified, although you will be able to change the range cost','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.personal.salary.modify.channel.costbyhour.dateFrom.label','From','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.personal.salary.modify.channel.costbyhour.dateTo.label','To','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.personal.salary.modify.channel.costbyhour.costbyhour.label','Cost By Hour','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.personal.salary.modify.salaryamountrequired.message','The salary is a required value','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.personal.salary.modify.salaryamountincorrect.message','The salary must be a positive value','A',0,1,null);

-- Salary - Personal - Create salary
insert into PN_PROPERTY values (2000,'en','text','prm.personal.salarycreatepage.title','Create Salary','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.personal.salary.create.pagetitle','New Salary','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.personal.salary.create.channel.costbyhour.title','Insert new salary value','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.personal.salary.create.channel.costbyhour.warning','Warning: once you save a new salary period the date range can not be modified, although you will be able to change the range cost','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.personal.salary.create.channel.costbyhour.dateFrom.label','From','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.personal.salary.create.channel.costbyhour.dateTo.label','To','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.personal.salary.create.channel.costbyhour.costbyhour.label','Cost By Hour','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.personal.salary.create.salaryamountrequired.message','The salary is a required value','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.personal.salary.create.salaryamountincorrect.message','The salary must be a positive value','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.personal.salary.create.startdaterequired.message','The start date is a required value','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.personal.salary.create.startdateincorrectformat.message','The start date is incorrect','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.personal.salary.create.startdateincorrectrange.message','The new start date cannot be previous to the last start date','A',0,1,null);

-- Salary - Financial - Contact Info
insert into PN_PROPERTY values (2000,'en','text','prm.financial.salary.tab.contactinfo.title','Contact Info','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.financial.salary.contactinfo.title','Contact Info','A',0,1,null);

-- Salary - Registration
insert into PN_PROPERTY values (2000,'en','text','prm.global.registration.financial.header','Your Salary Information','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.global.registration.financial.salary','Cost By Hour','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.global.registration.financial.salary.example','e.g. 20.75, 100.00','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.registration.userprofile.salaryamountrequired.message','The salary is a required value','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.registration.userprofile.salaryamountincorrect.message','The salary must be a positive value','A',0,1,null);

-- Setup
insert into PN_PROPERTY values (2000,'en','boolean','prm.financial.setup.isenabled','1','A',0,0,null);
insert into PN_PROPERTY values (2000,'en','text','prm.financial.nav.setup','Setup','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.financial.setup.module.history','Setup','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.financial.setup.channel.financialadministrator.title','Financial Administrator Settings','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','boolean','prm.financial.setup.directory.isenabled','1','A',0,0,null);
insert into PN_PROPERTY values (2000,'en','text','prm.financial.setup.directory.link','People and Roles','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.financial.setup.directory.label','Add or remove, people and roles in this financial','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.financial.setup.securitysettings.link','Security','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.financial.setup.securitysettings.label','Edit security settings for this financial','A',0,1,null);

--Completed Task Percentage
insert into PN_PROPERTY values (2000,'en','text','prm.global.taskcompletedpercentage','100','A',0,0,null);
insert into PN_PROPERTY values (2000,'en','boolean','prm.global.addmaterialsnotassignedontasks.isenabled','1','A',0,0,null);

-- Project Dashboard
insert into PN_PROPERTY values (2000,'en','text','prm.project.dashboard.financialchart.channel.title','Project Financial','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.project.dashboard.chart.totalcostchart.legend.actualcosttodate.name','Actual Cost To Date','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.project.dashboard.chart.totalcostchart.legend.currentestimatedtotalcost.name','Current Estimated Total Cost','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.project.dashboard.chart.totalcostchart.legend.budgetedtotalcost.name','Budgeted Total Cost','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','largetext','prm.project.dashboard.chart.totalcostchart.errors.nochartwithoutcosts.message','Cannot produce chart unless projects are present with budgeted and actual cost values.','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.project.dashboard.chart.totalcostchart.yaxis.label.name','Costs (in thousands)','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.project.dashboard.chart.totalcostchart.yaxis.font.name','Arial','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.project.dashboard.chart.totalcostchart.yaxis.font.size','12','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.project.dashboard.chart.totalcostchart.errors.multiplecurrencies.message','Chart cannot be produced for financial spaces with multiple currencies.','A',0,1,null);

-- TaskView - Financial
insert into PN_PROPERTY values (2000,'en','text','prm.schedule.taskview.financial.tab','Financial','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.schedule.taskview.financial.actualcosttodate.label','Actual Cost to Date','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.schedule.taskview.financial.currentestimatedtotalcost.label','Current Estimated Total Cost','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.schedule.main.financialcosts','Financials','A',0,1,null);

-- Reports 
insert into PN_PROPERTY values (2000,'en','text','prm.project.report.businessprojectsfinancialreport.name','Business Projects Financial Report','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.project.report.businessprojectsfinancialreport.description','List all the costs from projects of the associated business of the financial space','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.project.report.businessprojectsfinancialreport.xslpath','/report/xsl/businessprojectsfinancialreport.xsl','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.financial.report.projectname.name','Project Name','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.financial.report.projectdescription.name','Description','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.financial.report.projectactualcost.name','Actual Cost To Date','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.financial.report.projectestimatedcost.name','Current Estimated Cost','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.financial.report.projectbudgetedcost.name','Budgeted Cost','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.financial.report.totalprojects.name','Total Number of Projects','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.financial.report.totalactualcost.name','Total Actual Cost To Date','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.financial.report.totalestimatedcost.name','Total Current Estimated Cost','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.financial.report.totalbudgetedcost.name','Total Budgeted Cost','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.financial.report.noprojectsfound.name','No Projects on the Business founded','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.financial.report.common.reportsummary.name','Report Summary','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.financial.columndefs.projects.name','Projects','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.financial.columndefs.project.name','Project','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.financial.columndefs.project.description','Description','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.financial.columndefs.project.actualcost','Actual Cost To Date','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.financial.columndefs.project.currentestimatedcost','Current Estimated Total Cost','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.financial.columndefs.project.budgetedcost','Budgeted Cost','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.financial.report.businessprojectsfinancialreport.showallprojects.name','Show all Projects','A',0,1,null);


insert into PN_PROPERTY values (2000,'en','text','prm.project.report.actualcosttypesovertotalreport.name','Actual Cost Types Over Total','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.project.report.actualcosttypesovertotalreport.description','Display the types of cost values for each business project','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.project.report.actualcosttypesovertotalreport.xslpath','/report/xsl/actualcosttypesovertotalreport.xsl','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.financial.report.actualcosttypesovertotalreport.showallprojects.name','Show all Projects','A',0,1,null);

insert into PN_PROPERTY values (2000,'en','text','prm.financial.report.resourcesactualcosttodate.name','Resources Actual Cost To Date','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.financial.report.materialsactualcosttodate.name','Materials Actual Cost To Date','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.financial.report.discretionalactualcosttodate.name','Discretional Actual Cost To Date','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.financial.report.resourcestotalactualcosttodate.name','Total Resources Actual Cost To Date','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.financial.report.materialstotalactualcosttodate.name','Total Materials Actual Cost To Date','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.financial.report.discretionaltotalactualcosttodate.name','Total Discretional Actual Cost To Date','A',0,1,null);

insert into PN_PROPERTY values (2000,'en','text','prm.financial.columndefs.project.resourcesactualcosttodate','Resources Actual Cost To Date','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.financial.columndefs.project.materialsactualcosttodate','Materials Actual Cost To Date','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.financial.columndefs.project.discretionalactualcosttodate','Discretional Actual Cost To Date','A',0,1,null);

-- Report type and sequence
insert into pn_space_type_has_report_type values('financial','actr');
insert into pn_report_sequence values('actr',12);






