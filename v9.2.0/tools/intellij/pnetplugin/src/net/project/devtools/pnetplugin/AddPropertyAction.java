/*-------------------------
 * (c)2002 Matthew Flower
 * All rights reserved
 *-------------------------*/
package net.project.devtools.pnetplugin;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.editor.actionSystem.EditorAction;
import com.intellij.openapi.editor.actionSystem.EditorWriteActionHandler;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 * This class responds to the keystroke to add a new property while replacing
 * the existing text.
 *
 * @author Matthew Flower
 * @since Version 0.1
 */
public class AddPropertyAction extends EditorAction {
    private static class AddPropertyEditorAction extends EditorWriteActionHandler {
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

                Project project = editor.getProject();
                VirtualFile file = FileEditorManager.getInstance(project).getSelectedFiles()[0];

                PropertyFetchType pft = PropertyFetchType.getPropertyFetchType(editor,
                    file, apd.getAddedPropertyValue());
                String stringToInsert = pft.getPrefix() + addedProperty + pft.getSuffix();
                document.insertString(selectionModel.getSelectionStart(), stringToInsert);
            }
        }
    }

    public AddPropertyAction() {
        super(new AddPropertyEditorAction());
    }
}


/**
 * This class knows what type of tag (<display:get>, PropertyProvider.get(), etc)
 * to insert based on what the user has entered for a token value and based on
 * the context into which they are inserting it.
 *
 * @author Matthew Flower
 * @since Version 0.1
 */
class PropertyFetchType {
    public static final PropertyFetchType JSP_DISPLAY_GET = new PropertyFetchType("<display:get name=\"", "\"/>");
    public static final PropertyFetchType PROPERTY_PROVIDER_GET = new PropertyFetchType("PropertyProvider.get(\"", "\")");
    public static final PropertyFetchType ESCAPED_PROPERTY_PROVIDER_GET_WITH_PARAM = new PropertyFetchType("<%=PropertyProvider.get(\"", "\", PARAMETER)%>");
    public static final PropertyFetchType XSL_DISPLAY_GET = new PropertyFetchType("<xsl:value-of select=\"display:get('", "')\"/>");
    public static final PropertyFetchType AT_PREPENDED_GET = new PropertyFetchType("@", "");

    public static PropertyFetchType getPropertyFetchType(Editor editor, VirtualFile file, String propertyValue) {
        FileTypeManager ftm = FileTypeManager.getInstance();
        FileType fileType = ftm.getFileTypeByFile(file);
        String extension = file.getExtension();

        if (extension.equalsIgnoreCase("jsp")) {
            //TODO: JSP is interesting.  There are a number of contexts:
            //  1. Inside of a scriptlet (use PropertyProvider)
            //  2. Inside of HTML code but outside of a tag (use Display:get)
            //  3. Inside of HTML code, inside of a tag (use <%=PropertyProvider.get()%>)
            //  4. Inside of a Tag Library (switch to using the @ sign or switch
            //     attributes
            SelectionModel selectionModel = editor.getSelectionModel();
            Document document = editor.getDocument();
            String documentText = document.getText();

            if (isInsideScriptlet(documentText, selectionModel)) {
                return PROPERTY_PROVIDER_GET;
            } else if (isInsideHTMLTag(documentText, selectionModel)) {
                return PROPERTY_PROVIDER_GET;
            } else if (isInsideTagLibrary(documentText, selectionModel)) {
                //Check to see if the property value had any parameters
                if (propertyValue.matches(".*\\{\\d\\}.*")) {

                    //MAFTODO: figure out how to allow this tag to replace the double quotes around this property with single quotes.
                    return ESCAPED_PROPERTY_PROVIDER_GET_WITH_PARAM;
                } else {
                    return AT_PREPENDED_GET;
                }
            } else {
                return JSP_DISPLAY_GET;
            }
        } else if (extension.equalsIgnoreCase("java")) {
            return PROPERTY_PROVIDER_GET;
        } else if (extension.equalsIgnoreCase("xsl")) {
            return XSL_DISPLAY_GET;
        } else {
            return PROPERTY_PROVIDER_GET;
        }
    }

    private static boolean isInsideTagLibrary(String documentText, SelectionModel selectionModel) {
        //This is going to have to be pretty simple right now.
        //The method isn't very weel isolated -- it only returns the right thing
        //in very few situations and it wouldn't work if it was used outside of
        //the context of the current method.
        int selectionStart = selectionModel.getSelectionStart();
        int selectionEnd = selectionModel.getSelectionEnd();

        if (documentText.substring(selectionStart, selectionStart+1).equals("\"") &&
            documentText.substring(selectionEnd, selectionEnd+1).equals("\"")) {
            return true;
        } else {
            return false;
        }
    }

    private static boolean isInsideHTMLTag(String documentText, SelectionModel selectionModel) {
        return false;
    }

    private static boolean isInsideScriptlet(String documentText, SelectionModel selectionModel) {
        StringCharacterIterator ci = new StringCharacterIterator(documentText);
        ci.setIndex(selectionModel.getSelectionStart());

        char last = ' ';
        for (char current = ci.current(); current != CharacterIterator.DONE; current = ci.previous()) {
            if (current == '<' && last == '%') {
                return true;
            }

            if (current == '%' && last == '>') {
                return false;
            }
        }

        return false;
    }

    private final String prefix;
    private final String suffix;

    private PropertyFetchType(String prefix, String suffix) {
        this.prefix = prefix;
        this.suffix = suffix;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getSuffix() {
        return suffix;
    }

}
