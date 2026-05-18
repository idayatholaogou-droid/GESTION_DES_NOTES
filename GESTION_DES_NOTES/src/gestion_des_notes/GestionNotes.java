package gestion_des_notes;

import javax.swing.*;


public class GestionNotes {

    public static void main(String[] args) {
      
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

       
        Database.initialiserDB();

       
        SwingUtilities.invokeLater(() -> {
            LoginFrame login = new LoginFrame();
            login.setVisible(true);
        });
    }
}