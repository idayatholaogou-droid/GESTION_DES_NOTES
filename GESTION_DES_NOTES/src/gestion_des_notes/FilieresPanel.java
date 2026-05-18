package gestion_des_notes;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;

public class FilieresPanel extends JPanel {

    private JTable table;
    private DefaultTableModel model;
    private MainFrame frame;

    public FilieresPanel(MainFrame frame) {
        this.frame = frame;
        setLayout(new BorderLayout(0, 10));
        setBackground(MainFrame.FOND);

        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(MainFrame.FOND);
        top.add(MainFrame.creerEntete("🎓  Gestion des Filières"), BorderLayout.NORTH);

        JPanel barre = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        barre.setBackground(MainFrame.FOND);
        JButton btnAjouter   = MainFrame.creerBouton("➕  Ajouter",   MainFrame.CHOCOLAT);
        JButton btnSupprimer = MainFrame.creerBouton("🗑️  Supprimer", MainFrame.ROUGE_CLAIR);
        btnAjouter.addActionListener(e -> ajouterFiliere());
        btnSupprimer.addActionListener(e -> supprimerFiliere());
        barre.add(btnAjouter);
        barre.add(btnSupprimer);
        top.add(barre, BorderLayout.CENTER);
        add(top, BorderLayout.NORTH);

        String[] cols = {"Nom de la filière"};
        model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        table.setFont(MainFrame.FONT_NORMAL);
        table.setRowHeight(30);
        table.setGridColor(new Color(230, 235, 240));
        JTableHeader th = table.getTableHeader();
        th.setFont(new Font("Segoe UI", Font.BOLD, 12));
        th.setBackground(new Color(62, 32, 12));
        th.setForeground(new Color(205, 127, 50));
        th.setPreferredSize(new Dimension(0, 38));
        th.setOpaque(true);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(220, 230, 240)));
        add(scroll, BorderLayout.CENTER);

        chargerDonnees();
    }

    private void chargerDonnees() {
        model.setRowCount(0);
        try {
            ResultSet rs = Database.getConnection().createStatement()
                .executeQuery("SELECT nom FROM filieres ORDER BY nom");
            while (rs.next()) model.addRow(new Object[]{rs.getString(1)});
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erreur : " + e.getMessage());
        }
    }

    private void ajouterFiliere() {
        String nom = JOptionPane.showInputDialog(frame, "Nom de la filière :", "Nouvelle filière", JOptionPane.PLAIN_MESSAGE);
        if (nom == null || nom.trim().isEmpty()) return;
        try {
            Database.getConnection().createStatement().execute(
                "INSERT INTO filieres (nom) VALUES ('" + nom.trim() + "')");
            chargerDonnees();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erreur : filière déjà existante.");
        }
    }

    private void supprimerFiliere() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Sélectionnez une filière."); return; }
        if (!MainFrame.verifierAdmin(this)) { JOptionPane.showMessageDialog(this, "Mot de passe incorrect."); return; }
        String nom = (String) model.getValueAt(row, 0);
        int rep = JOptionPane.showConfirmDialog(this,
            "Supprimer la filière '" + nom + "' et toutes ses données ?",
            "Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (rep != JOptionPane.YES_OPTION) return;
        try {
            Database.getConnection().createStatement().execute(
                "DELETE FROM filieres WHERE nom='" + nom + "'");
            chargerDonnees();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erreur : " + e.getMessage());
        }
    }
}