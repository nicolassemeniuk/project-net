
select * from PN_PROPERTY a where a.PROPERTY like '%prm.project.propertiesedit.channel.financialstatus.title%';

-- Project Create
insert into PN_PROPERTY values (2000,'en','text','prm.project.create.wizard.financialcalculation.heading','Project Finance Calculation Method','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.project.create.wizard.costcenter.label','Cost Center','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.project.create.wizard.budgetedtotalcost.label','Budgeted Total Cost','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.project.create.wizard.costcompletion.select','Calculation Method','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.project.create.wizard.completion.projectcosts.label','From Project Cost Data','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.project.create.wizard.completion.projectcosts.description','Dinamically set the project costs from the costs data.','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.project.create.wizard.costs.label','Manually insert the project costs values.','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.project.create.wizard.currentestimatedtotalcost.label','Current Estimated Total Cost','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.project.create.wizard.actualcosttodate.label','Actual Cost To Date','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.project.create.wizard.estimatedroi.label','Estimated ROI','A',0,1,null);

insert into PN_PROPERTY values (2000,'en','text','prm.project.create.wizard.budgetedtotalcost','Budgeted Total Cost','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.project.create.wizard.costcenter','Cost Center','A',0,1,null);

-- Project Edit
insert into PN_PROPERTY values (2000,'en','text','prm.project.propertiesedit.channel.finanacialcompletion.title','Project Finance Calculation Method','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.project.propertiesedit.finanacialcompletion.select','Calculation Method','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.project.propertiesedit.completion.projectcosts.label','From Project Costs Data','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.project.propertiesedit.completion.projectcosts.description','Dinamically set the project costs from the costs values.','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.project.propertiesedit.costs.label','Manually insert the project costs values.','A',0,1,null);

--Project Meta Properties
insert into pn_project_space_meta_prop values (8,'CostCalculationMethod', 3);

-- Space definition
insert into PN_PROPERTY values (2000,'en','text','prm.application.nav.space.financial','Financial','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','boolean','prm.financial.isenabled','1','A',0,0,null);
insert into PN_PROPERTY values (2000,'en','text','prm.space.spacetypes.financial.name','Financial','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','css','prm.global.css.financial','/styles/financial.css','A',0,0,null);

-- Financial portfolio
insert into PN_PROPERTY values (2000,'en','text','prm.financial.financialportfolio.title','Business List','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.financial.financialportfolio.channel.memberof.title', 'Business finances you have access to','A',0,1,null);

-- Module
insert into PN_MODULE values (175,'financial_space','@prm.financialspace.module.description', 1, null);
insert into PN_PROPERTY values (2000,'en','text','prm.finacialsspace.module.description','Financial Workspace','A',0,1,null);

-- Object type
insert into PN_OBJECT_TYPE values ('financial','pn_financial_space','@prm.global.financial.objecttype.financial',null,1,0,0);
insert into PN_PROPERTY values (2000,'en','text','prm.global.financial.objecttype.financial','Financial','A',0,1,null);

-- Dashboard
insert into PN_PROPERTY values (2000,'en','boolean','prm.financial.dashboard.isenabled','1','A',0,0,null);
insert into PN_PROPERTY values (2000,'en','text','prm.financial.dashboard.title','Dashboard','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.financial.dashboard.toolbox.title','Toolbox','A',0,1,null);

insert into PN_PROPERTY values (2000,'en','text','prm.financial.main.modify.button.tooltip','Edit Financial','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.financial.main.properties.button.tooltip','View Properties','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.financial.main.personalize.button.tooltip','Personalize Page','A',0,1,null);

insert into PN_PROPERTY values (2000,'en','text','prm.financial.dashboard.goto.title','Go to','A',0,1,null);
insert into PN_PROPERTY values (2000,'en','text','prm.financial.dashboard.goto.reports.label','Reports','A',0,1,null);

insert into PN_PROPERTY values (2000,'en','text','prm.financial.dashboard.projects.channel.title','Projects','A',0,1,null);
