package gestion_des_notes;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BulletinPanel extends JPanel {

    private JTextField txtMatricule;
    private JPanel bulletinZone;

    public BulletinPanel(MainFrame frame) {
        setLayout(new BorderLayout(0, 10));
        setBackground(MainFrame.FOND);

        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(MainFrame.FOND);
        top.add(MainFrame.creerEntete("📄  Bulletin Individuel"), BorderLayout.NORTH);

        JPanel sel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        sel.setBackground(MainFrame.FOND);
        sel.add(new JLabel("Matricule étudiant :"));
        txtMatricule = new JTextField(15);
        txtMatricule.setFont(MainFrame.FONT_NORMAL);
        JButton btnAfficher = MainFrame.creerBouton("📄  Afficher", MainFrame.CHOCOLAT);
        btnAfficher.addActionListener(e -> afficherBulletin());
        txtMatricule.addActionListener(e -> afficherBulletin());
        sel.add(txtMatricule);
        sel.add(btnAfficher);
        top.add(sel, BorderLayout.CENTER);
        add(top, BorderLayout.NORTH);

        bulletinZone = new JPanel(new BorderLayout());
        bulletinZone.setBackground(MainFrame.FOND);
        JScrollPane scroll = new JScrollPane(bulletinZone);
        scroll.setBorder(null);
        add(scroll, BorderLayout.CENTER);
    }

    private void afficherBulletin() {
        bulletinZone.removeAll();
        String mat = txtMatricule.getText().trim();
        if (mat.isEmpty()) return;

        try {
            Connection conn = Database.getConnection();
            ResultSet rse = conn.createStatement().executeQuery(
                "SELECT e.id, e.nom, e.sexe, e.date_naissance, e.lieu_naissance, f.nom, e.filiere_id " +
                "FROM etudiants e JOIN filieres f ON e.filiere_id=f.id WHERE e.matricule='" + mat + "'");

            if (!rse.next()) {
                JOptionPane.showMessageDialog(this, "Aucun étudiant avec le matricule : " + mat);
                return;
            }

            int eId      = rse.getInt(1);
            String nom   = rse.getString(2);
            String sexe  = rse.getString(3).equals("M") ? "Masculin" : "Féminin";
            String ddn   = rse.getString(4);
            String lieu  = rse.getString(5);
            String filNom = rse.getString(6);
            int filId    = rse.getInt(7);

            // Matières via vue_bulletin
            ResultSet rsm = conn.createStatement().executeQuery(
                "SELECT DISTINCT matiere FROM vue_bulletin WHERE matricule='" + mat + "' ORDER BY matiere");
            List<String> matNoms = new ArrayList<>();
            while (rsm.next()) { matNoms.add(rsm.getString(1)); }

            // Construction du bulletin
            JPanel bulletin = new JPanel();
            bulletin.setLayout(new BoxLayout(bulletin, BoxLayout.Y_AXIS));
            bulletin.setBackground(Color.WHITE);
            bulletin.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(MainFrame.CHOCOLAT, 2),
                new EmptyBorder(20, 25, 20, 25)));
            bulletin.setMaximumSize(new Dimension(780, Integer.MAX_VALUE));

            // En-tête bulletin
            JLabel lblInstitut = new JLabel("UATM/GASA FORMATION", SwingConstants.CENTER);
            lblInstitut.setFont(new Font("Segoe UI", Font.BOLD, 16));
            lblInstitut.setForeground(MainFrame.CHOCOLAT);
            lblInstitut.setAlignmentX(CENTER_ALIGNMENT);

            JLabel lblTitre = new JLabel("BULLETIN DE NOTES", SwingConstants.CENTER);
            lblTitre.setFont(new Font("Segoe UI", Font.BOLD, 20));
            lblTitre.setForeground(MainFrame.CHOCOLAT);
            lblTitre.setAlignmentX(CENTER_ALIGNMENT);

            JLabel lblAnnee = new JLabel("Filière : " + filNom + "  |  Année : 2025-2026", SwingConstants.CENTER);
            lblAnnee.setFont(MainFrame.FONT_NORMAL);
            lblAnnee.setForeground(Color.GRAY);
            lblAnnee.setAlignmentX(CENTER_ALIGNMENT);

            JSeparator sep1 = new JSeparator();
            sep1.setForeground(MainFrame.CHOCOLAT);
            sep1.setMaximumSize(new Dimension(Integer.MAX_VALUE, 2));

            bulletin.add(lblInstitut);
            bulletin.add(Box.createVerticalStrut(4));
            bulletin.add(lblTitre);
            bulletin.add(Box.createVerticalStrut(4));
            bulletin.add(lblAnnee);
            bulletin.add(Box.createVerticalStrut(10));
            bulletin.add(sep1);
            bulletin.add(Box.createVerticalStrut(12));

            // Infos étudiant
            JPanel infoGrid = new JPanel(new GridLayout(0, 2, 10, 6));
            infoGrid.setBackground(new Color(235, 245, 251));
            infoGrid.setBorder(new EmptyBorder(10, 15, 10, 15));
            infoGrid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));

            String[][] infos = {
                {"Nom :", nom}, {"Matricule :", mat},
                {"Sexe :", sexe}, {"Date de naissance :", ddn},
                {"Lieu de naissance :", lieu}
            };
            for (String[] info : infos) {
                JLabel k = new JLabel(info[0]); k.setFont(new Font("Segoe UI", Font.BOLD, 12)); k.setForeground(MainFrame.CHOCOLAT);
                JLabel v = new JLabel(info[1]); v.setFont(MainFrame.FONT_NORMAL);
                infoGrid.add(k); infoGrid.add(v);
            }
            bulletin.add(infoGrid);
            bulletin.add(Box.createVerticalStrut(15));

            // Tableau des notes
            String[] cols = {"Matière", "Interrogation /20", "Devoir /20", "Moyenne (30%I+70%D)", "Mention"};
            DefaultTableModel tModel = new DefaultTableModel(cols, 0) {
                public boolean isCellEditable(int r, int c) { return false; }
            };

            // Utilisation de la vue vue_bulletin
            List<Double> moyennes = new ArrayList<>();
            ResultSet rsb = conn.createStatement().executeQuery(
                "SELECT matiere, interrogation, devoir, moyenne, mention FROM vue_bulletin WHERE matricule='" + mat + "' ORDER BY matiere");
            while (rsb.next()) {
                double moy = rsb.getDouble("moyenne");
                moyennes.add(moy);
                tModel.addRow(new Object[]{
                    rsb.getString("matiere"),
                    rsb.getDouble("interrogation"),
                    rsb.getDouble("devoir"),
                    moy + "/20",
                    rsb.getString("mention")
                });
            }

            JTable tNotes = new JTable(tModel);
            tNotes.setFont(MainFrame.FONT_NORMAL);
            tNotes.setRowHeight(28);
            tNotes.setGridColor(new Color(220, 230, 240));
            JTableHeader th = tNotes.getTableHeader();
            th.setFont(new Font("Segoe UI", Font.BOLD, 12));
            th.setBackground(new Color(62, 32, 12));
            th.setForeground(new Color(205, 127, 50));
            th.setPreferredSize(new Dimension(0, 35));
            th.setOpaque(true);

            // Centrer toutes les cellules
            DefaultTableCellRenderer center = new DefaultTableCellRenderer();
            center.setHorizontalAlignment(SwingConstants.CENTER);
            for (int i = 0; i < cols.length; i++)
                tNotes.getColumnModel().getColumn(i).setCellRenderer(center);

            tNotes.setMaximumSize(new Dimension(Integer.MAX_VALUE, tNotes.getPreferredSize().height + 40));
            bulletin.add(new JScrollPane(tNotes));
            bulletin.add(Box.createVerticalStrut(15));

            // Récapitulatif
            double moyGen = moyennes.isEmpty() ? 0 :
                Math.round(moyennes.stream().mapToDouble(x -> x).average().orElse(0) * 100.0) / 100.0;

            JPanel recap = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 10));
            recap.setBackground(MainFrame.CHOCOLAT);
            recap.setMaximumSize(new Dimension(Integer.MAX_VALUE, 55));

            JLabel lblMoy = new JLabel("Moyenne générale : " + (moyennes.isEmpty() ? "—" : moyGen + "/20"));
            lblMoy.setFont(new Font("Segoe UI", Font.BOLD, 14));
            lblMoy.setForeground(Color.WHITE);

            JLabel lblMen = new JLabel("Mention : " + (moyennes.isEmpty() ? "—" : Database.getMention(moyGen)));
            lblMen.setFont(new Font("Segoe UI", Font.BOLD, 14));
            lblMen.setForeground(MainFrame.BRONZE);

            recap.add(lblMoy);
            recap.add(lblMen);
            bulletin.add(recap);

            // Bouton imprimer
            JButton btnPrint = MainFrame.creerBouton("🖨️  Imprimer", new Color(80, 80, 80));
            btnPrint.setAlignmentX(CENTER_ALIGNMENT);
            btnPrint.addActionListener(e -> {
                try { tNotes.print(); } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Erreur impression : " + ex.getMessage());
                }
            });
            bulletin.add(Box.createVerticalStrut(10));
            bulletin.add(btnPrint);

            JPanel wrap = new JPanel(new FlowLayout(FlowLayout.CENTER));
            wrap.setBackground(MainFrame.FOND);
            wrap.add(bulletin);
            bulletinZone.add(wrap, BorderLayout.NORTH);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erreur : " + e.getMessage());
        }

        bulletinZone.revalidate();
        bulletinZone.repaint();
    }
}