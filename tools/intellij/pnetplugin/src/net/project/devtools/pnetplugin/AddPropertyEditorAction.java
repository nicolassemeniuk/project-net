/*-----------------------------------------------------------------------------+
|
|    $RCSfile$
|   $Revision: 12385 $
|       $Date: 2004-02-11 19:20:05 -0300 (mié, 11 feb 2004) $
|     $Author: matt $
|
+-----------------------------------------------------------------------------*/
package net.project.devtools.pnetplugin;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.editor.actionSystem.EditorWriteActionHandler;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.DataConstants;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.application.Application;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class AddPropertyEditorAction extends EditorWriteActionHandler {
    public void executeWriteAction(Editor editor, DataContext dataContext) {
        Document document = editor.getDocument();
        SelectionModel selectionModel = editor.getSelectionModel();

        //Get the text that we are going to add as a property
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

        //Show the add property dialog box in the middle of the screen.
        JFrame frame = (JFrame)SwingUtilities.getRoot(editor.getComponent());
        AddPropertyDialog apd = new AddPropertyDialog(frame, true, Settings.getInstance(dataContext));
        apd.setPropertyValueToInsert(selectedText);
        apd.show();

        //If the user didn't add a property, we are going to bail out now.
        if (apd.propertyAdded) {
            String addedProperty = apd.getAddedProperty();
            document.deleteString(selectionModel.getSelectionStart(), selectionModel.getSelectionEnd());

            //Project project = (Project)dataContext.getData(DataConstants.PROJECT);
            //VirtualFile file = FileEditorManager.getInstance(project).getSelectedFiles()[0];
            VirtualFile file = FileDocumentManager.getInstance().getFile(editor.getDocument());

            PropertyFetchType pft = PropertyFetchType.getPropertyFetchType(editor,file,
                apd.getAddedPropertyValue());
            String stringToInsert = pft.getPrefix() + addedProperty + pft.getSuffix();
            document.insertString(selectionModel.getSelectionStart(), stringToInsert);
        }
    }
}
