package gestion_des_notes;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;

public class EtudiantsPanel extends JPanel {

    private JTable table;
    private DefaultTableModel model;
    private MainFrame frame;

    public EtudiantsPanel(MainFrame frame) {
        this.frame = frame;
        setLayout(new BorderLayout(0, 10));
        setBackground(MainFrame.FOND);

        add(MainFrame.creerEntete("👤  Gestion des Étudiants"), BorderLayout.NORTH);

        // ── Barre d'outils ────────────────────────────
        JPanel barre = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        barre.setBackground(MainFrame.FOND);

        JButton btnAjouter   = MainFrame.creerBouton("➕  Ajouter",   MainFrame.CHOCOLAT);
        JButton btnModifier  = MainFrame.creerBouton("✏️  Modifier",  MainFrame.CHOCOLAT);
        JButton btnSupprimer = MainFrame.creerBouton("🗑️  Supprimer", MainFrame.ROUGE_CLAIR);
        JButton btnActualiser = MainFrame.creerBouton("🔄  Actualiser", new Color(80, 80, 80));

        btnAjouter.addActionListener(e -> ouvrirFormulaire(null));
        btnModifier.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { JOptionPane.showMessageDialog(this, "Sélectionnez un étudiant."); return; }
            if (!MainFrame.verifierAdmin(this)) { JOptionPane.showMessageDialog(this, "Mot de passe incorrect."); return; }
            ouvrirFormulaire((String) model.getValueAt(row, 0));
        });
        btnSupprimer.addActionListener(e -> supprimerEtudiant());
        btnActualiser.addActionListener(e -> chargerDonnees());

        barre.add(btnAjouter);
        barre.add(btnModifier);
        barre.add(btnSupprimer);
        barre.add(btnActualiser);
        add(barre, BorderLayout.CENTER);

        // ── Tableau ───────────────────────────────────
        String[] colonnes = {"Matricule", "Nom", "Sexe", "Date naissance", "Lieu naissance", "Filière"};
        model = new DefaultTableModel(colonnes, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        table.setFont(MainFrame.FONT_NORMAL);
        table.setRowHeight(30);
        table.setSelectionBackground(new Color(235, 245, 251));
        table.setSelectionForeground(MainFrame.CHOCOLAT);
        table.setGridColor(new Color(230, 235, 240));
        table.setShowGrid(true);

        // Style en-tête
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setBackground(new Color(62, 32, 12));
        header.setForeground(new Color(205, 127, 50));
        header.setPreferredSize(new Dimension(0, 38));
        header.setOpaque(true);

        // Largeurs colonnes
        int[] widths = {90, 200, 60, 130, 150, 150};
        for (int i = 0; i < widths.length; i++)
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(220, 230, 240)));
        add(scroll, BorderLayout.SOUTH);

        // Mettre le scroll en plein écran
        setLayout(new BorderLayout(0, 10));
        add(MainFrame.creerEntete("👤  Gestion des Étudiants"), BorderLayout.NORTH);
        add(barre, BorderLayout.CENTER);
        add(scroll, BorderLayout.SOUTH);

        setLayout(new BorderLayout(0, 10));
        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(MainFrame.FOND);
        top.add(MainFrame.creerEntete("👤  Gestion des Étudiants"), BorderLayout.NORTH);
        top.add(barre, BorderLayout.CENTER);
        add(top, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);

        chargerDonnees();
    }

    private void chargerDonnees() {
        model.setRowCount(0);
        try {
            Connection conn = Database.getConnection();
            ResultSet rs = conn.createStatement().executeQuery(
                "SELECT matricule, nom, sexe, date_naissance, lieu_naissance, filiere FROM vue_etudiants"
            );
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString(1), rs.getString(2), rs.getString(3),
                    rs.getString(4), rs.getString(5), rs.getString(6)
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erreur : " + e.getMessage());
        }
    }

    private void supprimerEtudiant() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Sélectionnez un étudiant."); return; }
        if (!MainFrame.verifierAdmin(this)) { JOptionPane.showMessageDialog(this, "Mot de passe incorrect."); return; }
        String mat = (String) model.getValueAt(row, 0);
        String nom = (String) model.getValueAt(row, 1);
        int rep = JOptionPane.showConfirmDialog(this, "Supprimer l'étudiant " + nom + " ?", "Confirmation", JOptionPane.YES_NO_OPTION);
        if (rep != JOptionPane.YES_OPTION) return;
        try {
            Database.getConnection().createStatement().execute("DELETE FROM etudiants WHERE matricule='" + mat + "'");
            chargerDonnees();
            JOptionPane.showMessageDialog(this, "Étudiant supprimé.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erreur : " + e.getMessage());
        }
    }

    private void ouvrirFormulaire(String matriculeExistant) {
        JDialog dlg = new JDialog(frame, matriculeExistant == null ? "Ajouter un étudiant" : "Modifier l'étudiant", true);
        dlg.setSize(440, 480);
        dlg.setLocationRelativeTo(frame);
        dlg.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridLayout(0, 2, 10, 10));
        form.setBorder(new EmptyBorder(20, 20, 20, 20));
        form.setBackground(Color.WHITE);

        JTextField txtMat  = new JTextField();
        JTextField txtNom  = new JTextField();
        JTextField txtDdn  = new JTextField();
        JTextField txtLieu = new JTextField();
        JComboBox<String> cbSexe    = new JComboBox<>(new String[]{"M", "F"});
        JComboBox<String> cbFiliere = new JComboBox<>();

        // Charger filières
        try {
            ResultSet rs = Database.getConnection().createStatement().executeQuery("SELECT nom FROM filieres ORDER BY nom");
            while (rs.next()) cbFiliere.addItem(rs.getString(1));
        } catch (SQLException ignored) {}

        // Pré-remplir si modification
        if (matriculeExistant != null) {
            try {
                ResultSet rs = Database.getConnection().createStatement().executeQuery(
                    "SELECT e.matricule, e.nom, e.sexe, e.date_naissance, e.lieu_naissance, f.nom " +
                    "FROM etudiants e JOIN filieres f ON e.filiere_id=f.id WHERE e.matricule='" + matriculeExistant + "'");
                if (rs.next()) {
                    txtMat.setText(rs.getString(1));  txtMat.setEnabled(false);
                    txtNom.setText(rs.getString(2));
                    cbSexe.setSelectedItem(rs.getString(3));
                    txtDdn.setText(rs.getString(4));
                    txtLieu.setText(rs.getString(5));
                    cbFiliere.setSelectedItem(rs.getString(6));
                }
            } catch (SQLException ignored) {}
        }

        form.add(new JLabel("Matricule :")); form.add(txtMat);
        form.add(new JLabel("Nom complet :")); form.add(txtNom);
        form.add(new JLabel("Sexe :")); form.add(cbSexe);
        form.add(new JLabel("Date naissance\n(AAAA-MM-JJ) :")); form.add(txtDdn);
        form.add(new JLabel("Lieu de naissance :")); form.add(txtLieu);
        form.add(new JLabel("Filière :")); form.add(cbFiliere);

        JButton btnSave = MainFrame.creerBouton("💾  Enregistrer", MainFrame.CHOC_MOYEN);
        btnSave.addActionListener(e -> {
            String mat = txtMat.getText().trim();
            String nom = txtNom.getText().trim();
            String sexe = (String) cbSexe.getSelectedItem();
            String ddn  = txtDdn.getText().trim();
            String lieu = txtLieu.getText().trim();
            String filNom = (String) cbFiliere.getSelectedItem();

            if (mat.isEmpty() || nom.isEmpty() || ddn.isEmpty() || lieu.isEmpty()) {
                JOptionPane.showMessageDialog(dlg, "Tous les champs sont obligatoires."); return;
            }
            try {
                ResultSet rsf = Database.getConnection().createStatement().executeQuery(
                    "SELECT id FROM filieres WHERE nom='" + filNom + "'");
                int filId = rsf.next() ? rsf.getInt(1) : 1;

                if (matriculeExistant == null) {
                    Database.getConnection().createStatement().execute(String.format(
                        "INSERT INTO etudiants (matricule,nom,sexe,date_naissance,lieu_naissance,filiere_id) VALUES ('%s','%s','%s','%s','%s',%d)",
                        mat, nom, sexe, ddn, lieu, filId));
                } else {
                    Database.getConnection().createStatement().execute(String.format(
                        "UPDATE etudiants SET nom='%s',sexe='%s',date_naissance='%s',lieu_naissance='%s',filiere_id=%d WHERE matricule='%s'",
                        nom, sexe, ddn, lieu, filId, mat));
                }
                JOptionPane.showMessageDialog(dlg, "Enregistré avec succès !");
                dlg.dispose();
                chargerDonnees();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dlg, "Erreur : " + ex.getMessage());
            }
        });

        JPanel btnPanel = new JPanel();
        btnPanel.setBackground(Color.WHITE);
        btnPanel.add(btnSave);

        dlg.add(form, BorderLayout.CENTER);
        dlg.add(btnPanel, BorderLayout.SOUTH);
        dlg.setVisible(true);
    }
}