import javax.swing.*;
import java.awt.*;

public class AssDriver {
    public static void main(String[] args) {
        JFrame frame = new JFrame();
        JPanel cardPane = new JPanel();
        CardLayout cardLayout = new CardLayout();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame.setLayout(cardLayout);
        cardPane.setLayout(cardLayout);

        HomePanel home = new HomePanel(cardPane);
        UploadPanel upload = new UploadPanel(cardPane);
        cardPane.add(home, "Home");
        cardPane.add(upload, "Upload");

//        cardLayout.show(frame, "Home");
        frame.add(cardPane);

        frame.pack();
        frame.setVisible(true);
    }
}
