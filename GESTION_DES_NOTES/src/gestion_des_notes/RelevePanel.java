package gestion_des_notes;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;
import java.util.*;

public class RelevePanel extends JPanel {

    private JComboBox<String> cbFiliere;
    private JTable table;
    private DefaultTableModel model;

    public RelevePanel(MainFrame frame) {
        setLayout(new BorderLayout(0, 10));
        setBackground(MainFrame.FOND);

        // En-tête
        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(MainFrame.FOND);
        top.add(MainFrame.creerEntete("📊  Relevé de Notes Collectif"), BorderLayout.NORTH);

        JPanel sel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        sel.setBackground(MainFrame.FOND);
        sel.add(new JLabel("Filière :"));
        cbFiliere = new JComboBox<>();

        try {
            ResultSet rs = Database.getConnection().createStatement()
                .executeQuery("SELECT nom FROM filieres ORDER BY nom");
            while (rs.next()) cbFiliere.addItem(rs.getString(1));
        } catch (SQLException ignored) {}

        JButton btnAfficher = MainFrame.creerBouton("📊  Afficher", MainFrame.CHOCOLAT);
        btnAfficher.addActionListener(e -> afficherReleve());
        sel.add(cbFiliere);
        sel.add(btnAfficher);
        top.add(sel, BorderLayout.CENTER);
        add(top, BorderLayout.NORTH);

        // Tableau
        String[] cols = {"Rang", "Matricule", "Nom", "Moy. Générale", "Mention"};
        model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        table.setFont(MainFrame.FONT_NORMAL);
        table.setRowHeight(30);
        table.setSelectionBackground(new Color(235, 245, 251));
        table.setGridColor(new Color(230, 235, 240));

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setBackground(new Color(62, 32, 12));
        header.setForeground(new Color(205, 127, 50));
        header.setPreferredSize(new Dimension(0, 38));
        header.setOpaque(true);

        int[] widths = {60, 100, 220, 130, 130};
        for (int i = 0; i < widths.length; i++)
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        // Renderer couleur mention
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object val, boolean sel, boolean foc, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                setHorizontalAlignment(SwingConstants.CENTER);
                if (!sel) {
                    c.setBackground(row % 2 == 0 ? new Color(245, 250, 255) : Color.WHITE);
                    if (col == 4 && val != null) {
                        String m = val.toString();
                        switch (m) {
                            case "Très Bien"  -> c.setForeground(new Color(30, 132, 73));
                            case "Bien"       -> c.setForeground(new Color(46, 117, 182));
                            case "Assez Bien" -> c.setForeground(new Color(100, 100, 180));
                            case "Passable"   -> c.setForeground(new Color(180, 130, 0));
                            default           -> c.setForeground(new Color(192, 57, 43));
                        }
                    } else {
                        c.setForeground(MainFrame.CHOCOLAT);
                    }
                }
                return c;
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(220, 230, 240)));
        add(scroll, BorderLayout.CENTER);

        afficherReleve();
    }

    private void afficherReleve() {
        model.setRowCount(0);
        String filNom = (String) cbFiliere.getSelectedItem();
        if (filNom == null) return;

        try {
            Connection conn = Database.getConnection();
            ResultSet rsf = conn.createStatement().executeQuery(
                "SELECT id FROM filieres WHERE nom='" + filNom + "'");
            if (!rsf.next()) return;
            int filId = rsf.getInt(1);

            // Utilisation de la vue vue_moyennes
            ResultSet rs = conn.createStatement().executeQuery(
                "SELECT matricule, nom, moyenne_generale FROM vue_moyennes WHERE filiere='" + filNom + "' ORDER BY moyenne_generale DESC");

            int rang = 1;
            while (rs.next()) {
                String mat = rs.getString("matricule");
                String nom = rs.getString("nom");
                double moy = rs.getDouble("moyenne_generale");
                boolean hasNote = !rs.wasNull() && moy > 0;
                model.addRow(new Object[]{
                    rang++, mat, nom,
                    hasNote ? moy + "/20" : "—",
                    hasNote ? Database.getMention(moy) : "—"
                });
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erreur : " + e.getMessage());
        }
    }
}