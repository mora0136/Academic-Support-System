package panel;

import javax.swing.*;
import java.awt.*;

public class UploadPanelDisabled extends UploadPanel{
    public UploadPanelDisabled(){
        super(new JPanel(new CardLayout()));
        leftPanel.remove(backResetPanel);
        rightPanel.remove(saveUploadPanel);
        descPanel.remove(templateStatement);
        contactsPanel.remove(searchField);
        contactsPanel.remove(authorsLabel);
        contactsListPanel.remove(contactsLabel);
        contactsListPanel.remove(notAddedContactList);
        contactsListPanel.remove(contactListScroll);
        for(Component com : titlePanel.getComponents()){
            com.setEnabled(false);
        }
        for(Component com : descPanel.getComponents()){
            com.setEnabled(false);
        }
        for(Component com : filePanel.getComponents()){
            com.setEnabled(false);
        }
        for(Component com : typeDatePanel.getComponents()){
            com.setEnabled(false);
        }
        for(Component com : contactsPanel.getComponents()){
            com.setEnabled(false);
        }
        for(Component com : contactsListPanel.getComponents()){
            com.setEnabled(false);
        }
        for(Component com : servicesPanel.getComponents()){
            com.setEnabled(false);
        }
        notAddedContactList.setEnabled(false);
        addedContactsList.setEnabled(false);
        mainFont = 42;

    }
}
