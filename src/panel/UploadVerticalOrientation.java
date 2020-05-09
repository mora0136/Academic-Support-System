package panel;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class UploadVerticalOrientation extends UploadPanelDisabled {
    UploadVerticalOrientation(JPanel pane) throws IOException {
        super();
        setLayout(new GridLayout(2, 1));
    }
}
