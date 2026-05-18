package gestion_des_notes;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;

public class MatieresPanel extends JPanel {

    private JTable table;
    private DefaultTableModel model;
    private MainFrame frame;

    public MatieresPanel(MainFrame frame) {
        this.frame = frame;
        setLayout(new BorderLayout(0, 10));
        setBackground(MainFrame.FOND);

        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(MainFrame.FOND);
        top.add(MainFrame.creerEntete("📚  Gestion des Matières"), BorderLayout.NORTH);

        JPanel barre = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        barre.setBackground(MainFrame.FOND);
        JButton btnAjouter   = MainFrame.creerBouton("➕  Ajouter",   MainFrame.CHOCOLAT);
        JButton btnSupprimer = MainFrame.creerBouton("🗑️  Supprimer", MainFrame.ROUGE_CLAIR);
        btnAjouter.addActionListener(e -> ajouterMatiere());
        btnSupprimer.addActionListener(e -> supprimerMatiere());
        barre.add(btnAjouter);
        barre.add(btnSupprimer);
        top.add(barre, BorderLayout.CENTER);
        add(top, BorderLayout.NORTH);

        String[] cols = {"Matière", "Filière"};
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
            ResultSet rs = Database.getConnection().createStatement().executeQuery(
                "SELECT m.nom, f.nom FROM matieres m JOIN filieres f ON m.filiere_id=f.id ORDER BY f.nom, m.nom");
            while (rs.next())
                model.addRow(new Object[]{rs.getString(1), rs.getString(2)});
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erreur : " + e.getMessage());
        }
    }

    private void ajouterMatiere() {
        JTextField txtNom = new JTextField();
        JComboBox<String> cbFil = new JComboBox<>();
        try {
            ResultSet rs = Database.getConnection().createStatement().executeQuery("SELECT nom FROM filieres ORDER BY nom");
            while (rs.next()) cbFil.addItem(rs.getString(1));
        } catch (SQLException ignored) {}

        int res = JOptionPane.showConfirmDialog(frame,
            new Object[]{"Nom de la matière :", txtNom, "Filière :", cbFil},
            "Ajouter une matière", JOptionPane.OK_CANCEL_OPTION);
        if (res != JOptionPane.OK_OPTION) return;

        String nom = txtNom.getText().trim();
        String filNom = (String) cbFil.getSelectedItem();
        if (nom.isEmpty() || filNom == null) return;

        try {
            ResultSet rsf = Database.getConnection().createStatement().executeQuery(
                "SELECT id FROM filieres WHERE nom='" + filNom + "'");
            if (rsf.next()) {
                Database.getConnection().createStatement().execute(
                    "INSERT INTO matieres (nom, filiere_id) VALUES ('" + nom + "', " + rsf.getInt(1) + ")");
                chargerDonnees();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erreur : " + e.getMessage());
        }
    }

    private void supprimerMatiere() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Sélectionnez une matière."); return; }
        if (!MainFrame.verifierAdmin(this)) { JOptionPane.showMessageDialog(this, "Mot de passe incorrect."); return; }
        String nom = (String) model.getValueAt(row, 0);
        String filNom = (String) model.getValueAt(row, 1);
        int rep = JOptionPane.showConfirmDialog(this, "Supprimer la matière '" + nom + "' ?", "Confirmation", JOptionPane.YES_NO_OPTION);
        if (rep != JOptionPane.YES_OPTION) return;
        try {
            Database.getConnection().createStatement().execute(
                "DELETE FROM matieres WHERE nom='" + nom + "' AND filiere_id=(SELECT id FROM filieres WHERE nom='" + filNom + "')");
            chargerDonnees();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erreur : " + e.getMessage());
        }
    }
}