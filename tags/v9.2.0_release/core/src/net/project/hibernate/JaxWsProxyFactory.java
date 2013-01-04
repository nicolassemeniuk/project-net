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
package net.project.hibernate;

import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.springframework.beans.factory.config.AbstractFactoryBean;

/**
 * Custom factory class for webserices JaxWsProxyFactoryBeans
 *   
 *
 */
public class JaxWsProxyFactory extends AbstractFactoryBean {
	
	/**
	 *  web-service address 
	 */
	String address;
	
	/**
	 *  web-service class 
	 */	
	Class serviceClass;
	
	/**
	 *  web-service end point 
	 */	
	String endPoint;
	
	/* (non-Javadoc)
	 * @see org.springframework.beans.factory.config.AbstractFactoryBean#createInstance()
	 */
	@Override
	protected Object createInstance() throws Exception {
		JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
		factory.setAddress(address + endPoint);
		factory.setServiceClass(serviceClass);
		factory.getServiceFactory().setWrapped(false);
		return factory;
	}

	/* (non-Javadoc)
	 * @see org.springframework.beans.factory.config.AbstractFactoryBean#getObjectType()
	 */
	@Override
	public Class getObjectType() {
		return JaxWsProxyFactoryBean.class;
	}

   
	public JaxWsProxyFactory(ParamManager paramManager, Class serviceClass, String endPoint){
		address = paramManager.getAddress();
		this.serviceClass = serviceClass;
		this.endPoint = endPoint;
	}

	public boolean isSingleton() {
		return true;
	}

	
}
