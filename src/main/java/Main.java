
import com.formdev.flatlaf.FlatLightLaf;
import UI.ExpenseTrackerApp;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {

        FlatLightLaf.setup();

        SwingUtilities.invokeLater(() -> {
            ExpenseTrackerApp app = new ExpenseTrackerApp();
            app.setVisible(true);
        });
    }
}
