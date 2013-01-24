/*-------------------------
 * (c)2002 Matthew Flower
 * All rights reserved
 *-------------------------*/
package net.project.devtools.pnetplugin;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.actionSystem.DataConstants;
import com.intellij.openapi.actionSystem.DataContext;

public class Settings {
    public String database = "jdbc:oracle:thin:@ape:1521:v80dev";
    public String ccDatabase = "";
    public String dbUserName = "pnet";
    public String dbPassword = "pnet";


    public static Settings getInstance(DataContext dc) {
        Project project = (Project)dc.getData(DataConstants.PROJECT);
        PnetPlugin plugin = (PnetPlugin)project.getComponent(PnetPlugin.class);
        return plugin.getSettings();
    }

    public static Settings getInstance(Project project) {
        PnetPlugin plugin = (PnetPlugin)project.getComponent(PnetPlugin.class);
        return plugin.getSettings();
    }
}
