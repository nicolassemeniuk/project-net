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
package net.project.versioncheck;

public class VersionControlProxy implements net.project.versioncheck.VersionControl {
  private String _endpoint = null;
  private net.project.versioncheck.VersionControl versionControl = null;
  
  public VersionControlProxy() {
    _initVersionControlProxy();
  }
  
  private void _initVersionControlProxy() {
    try {
      versionControl = (new net.project.versioncheck.VersionControlServiceLocator()).getVersionControl();
      if (versionControl != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)versionControl)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)versionControl)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (versionControl != null)
      ((javax.xml.rpc.Stub)versionControl)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public net.project.versioncheck.VersionControl getVersionControl() {
    if (versionControl == null)
      _initVersionControlProxy();
    return versionControl;
  }
  
  public java.lang.String getVersion(java.lang.String clientIpAddress, java.lang.String clientCurrentVersion, int numberOfUsers) throws java.rmi.RemoteException{
    if (versionControl == null)
      _initVersionControlProxy();
    return versionControl.getVersion(clientIpAddress, clientCurrentVersion, numberOfUsers);
  }
  
  
}