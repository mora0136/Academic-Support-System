package panel;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class UploadEditPanel extends UploadPanel {
    UploadEditPanel(JPanel pane) throws IOException {
        super(pane);
        setLayout(new GridLayout(2, 1));
        leftPanel.remove(backResetPanel);
        rightPanel.remove(saveUploadPanel);
        descPanel.remove(templateStatement);
        contactsPanel.remove(searchField);
        contactsPanel.remove(authorsLabel);
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
        for(Component com : addedContactsList.getComponents()){
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
