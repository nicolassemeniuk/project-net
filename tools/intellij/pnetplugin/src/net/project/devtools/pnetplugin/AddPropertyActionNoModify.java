/*-------------------------
 * (c)2003 Matthew Flower
 * All rights reserved
 *-------------------------*/
package net.project.devtools.pnetplugin;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataConstants;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.SelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.JFrame;

/**
 * This action adds a property to the database without changing the text in the
 * application.
 *
 * @author Matthew Flower
 * @since Version 0.2
 */
public class AddPropertyActionNoModify extends AnAction {
    /**
     * Implement this method to provide your action handler.
     *
     * @param e Carries information on the invocation place
     */
    public void actionPerformed(AnActionEvent e) {
        Editor editor = (Editor)e.getDataContext().getData(DataConstants.EDITOR);
        SelectionModel selectionModel = editor.getSelectionModel();

        //Get the current selection.  If there is one, we will populate the
        //property name with it.
        String selectedText = selectionModel.getSelectedText();
        if (selectedText == null) {
            selectedText = "";
        } else {
            if (selectedText.startsWith("\"")) {
                selectedText = selectedText.substring(1);
            }
            if (selectedText.endsWith("\"")) {
                selectedText = selectedText.substring(0, selectedText.length()-1);
            }
        }


        JFrame frame = (JFrame)SwingUtilities.getRoot(editor.getComponent());
        AddPropertyDialog apd = new AddPropertyDialog(frame, true, Settings.getInstance(e.getDataContext()));
        apd.setPropertyName(selectedText);
        apd.setPropertyValueToInsert("");
        apd.show();
    }
}
