

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
