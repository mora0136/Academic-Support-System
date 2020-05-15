package panel;

import java.awt.*;
import java.io.IOException;

public class UploadVerticalOrientation extends UploadPanelDisabled {
    public UploadVerticalOrientation() throws IOException {
        super();
        setLayout(new GridLayout(2, 1));
        mainFont = 48;
    }
}
