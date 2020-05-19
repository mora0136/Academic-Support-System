package panel;

import java.awt.*;

public class UploadVerticalOrientation extends UploadPanelDisabled {
    /**
     * The Vertical orientation has a different dimension then what was expected inside the addDocumentListener for window
     * size. To fix the scale of font, the font is set to 48.
     */
    public UploadVerticalOrientation(){
        super();
        setLayout(new GridLayout(2, 1));
        mainFont = 48;
    }
}
