package panel;

import javax.swing.*;
import java.awt.*;

/*
 * This disabled version of the upload Panel removes the Template Statements and Contact List from address book.
 * This also, as the name implies, disables all fields inside so they are non editable and display as such.
 * This is utilised anytime an upload needs to be shown such as in edit Panel and log Panel.
 */

public class UploadPanelDisabled extends UploadPanel{
    public UploadPanelDisabled(){
        super(new JPanel(new CardLayout()));
        leftPanel.remove(backResetPanel);
        rightPanel.remove(saveUploadPanel);
        sp.remove(templateStatement);
        contactsPanel.remove(searchField);
        contactsPanel.remove(authorsLabel);
        contactsListPanel.remove(contactsLabel);
        contactsListPanel.remove(notAddedContactList);
        contactsListPanel.remove(contactListScroll);
        SpringUtilities.makeCompactGrid(contactsListPanel, 2, 1, 0, 0, 5, 5);
        //Remove the first element as this is displays the info of the drag feature, this is not needed in this view
        DefaultListModel l = (DefaultListModel) attachedFileList.getModel();
        l.remove(0);

        for(Component com : titlePanel.getComponents()){
            com.setEnabled(false);
        }
        for(Component com : descPanel.getComponents()){
            com.setEnabled(false);
        }
        descriptionTextArea.setEnabled(false); //is inside split pane so set false manually
        for(Component com : filePanel.getComponents()){
            com.setEnabled(false);
        }
        attachedFileList.setEnabled(false); //is inside scroll pane sp manually set
        for(Component com : typeDatePanel.getComponents()){
            com.setEnabled(false);
        }
        for(Component com : contactsPanel.getComponents()){
            com.setEnabled(false);
        }
        for(Component com : contactsListPanel.getComponents()){
            com.setEnabled(false);
        }
        addedContactsList.setEnabled(false);
        for(Component com : servicesPanel.getComponents()){
            com.setEnabled(false);
        }
    }
}
