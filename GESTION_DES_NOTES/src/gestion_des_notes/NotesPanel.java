package gestion_des_notes;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NotesPanel extends JPanel {

    private MainFrame frame;
    private JComboBox<String> cbFiliere;
    private JPanel grillePanel;
    private Map<String, JTextField[]> champNotes = new HashMap<>();

    public NotesPanel(MainFrame frame) {
        this.frame = frame;
        setLayout(new BorderLayout(0, 10));
        setBackground(MainFrame.FOND);

        // En-tête
        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(MainFrame.FOND);
        top.add(MainFrame.creerEntete("📝  Saisie des Notes"), BorderLayout.NORTH);

        // Sélection filière
        JPanel sel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        sel.setBackground(MainFrame.FOND);
        sel.add(new JLabel("Filière :"));
        cbFiliere = new JComboBox<>();

        try {
            ResultSet rs = Database.getConnection().createStatement()
                .executeQuery("SELECT nom FROM filieres ORDER BY nom");
            while (rs.next()) cbFiliere.addItem(rs.getString(1));
        } catch (SQLException ignored) {}

        JButton btnCharger = MainFrame.creerBouton("📋  Charger", MainFrame.CHOCOLAT);
        btnCharger.addActionListener(e -> chargerGrille());

        sel.add(cbFiliere);
        sel.add(btnCharger);
        top.add(sel, BorderLayout.CENTER);
        add(top, BorderLayout.NORTH);

        // Zone grille
        grillePanel = new JPanel(new BorderLayout());
        grillePanel.setBackground(MainFrame.FOND);
        JScrollPane scroll = new JScrollPane(grillePanel);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(220, 230, 240)));
        add(scroll, BorderLayout.CENTER);

        chargerGrille();
    }

    private void chargerGrille() {
        grillePanel.removeAll();
        champNotes.clear();
        String filNom = (String) cbFiliere.getSelectedItem();
        if (filNom == null) return;

        try {
            Connection conn = Database.getConnection();

            // Récupérer filière ID
            ResultSet rsf = conn.createStatement().executeQuery(
                "SELECT id FROM filieres WHERE nom='" + filNom + "'");
            if (!rsf.next()) return;
            int filId = rsf.getInt(1);

            // Matières et étudiants
            List<int[]> matieres = new ArrayList<>();
            List<String> nomsMatieres = new ArrayList<>();
            ResultSet rsm = conn.createStatement().executeQuery(
                "SELECT id, nom FROM matieres WHERE filiere_id=" + filId + " ORDER BY nom");
            while (rsm.next()) {
                matieres.add(new int[]{rsm.getInt(1)});
                nomsMatieres.add(rsm.getString(2));
            }

            List<int[]> etudiants = new ArrayList<>();
            List<String> nomsEtu = new ArrayList<>();
            ResultSet rse = conn.createStatement().executeQuery(
                "SELECT id, nom FROM etudiants WHERE filiere_id=" + filId + " ORDER BY nom");
            while (rse.next()) {
                etudiants.add(new int[]{rse.getInt(1)});
                nomsEtu.add(rse.getString(2));
            }

            if (etudiants.isEmpty() || matieres.isEmpty()) {
                grillePanel.add(new JLabel("Aucun étudiant ou matière dans cette filière.", SwingConstants.CENTER));
                grillePanel.revalidate(); grillePanel.repaint();
                return;
            }

            // Notes existantes
            Map<String, double[]> notesMap = new HashMap<>();
            ResultSet rsn = conn.createStatement().executeQuery(
                "SELECT n.etudiant_id, n.matiere_id, n.interrogation, n.devoir " +
                "FROM notes n JOIN etudiants e ON n.etudiant_id=e.id WHERE e.filiere_id=" + filId);
            while (rsn.next()) {
                notesMap.put(rsn.getInt(1) + "_" + rsn.getInt(2),
                    new double[]{rsn.getDouble(3), rsn.getDouble(4)});
            }

            // Construction de la grille
            int nbCols = 1 + matieres.size() * 2;
            JPanel grille = new JPanel(new GridLayout(0, nbCols, 2, 2));
            grille.setBackground(MainFrame.FOND);
            grille.setBorder(new EmptyBorder(10, 10, 10, 10));

            // En-tête
            JLabel lblEtu = creerCellule("Étudiant", MainFrame.CHOCOLAT, Color.WHITE, true);
            grille.add(lblEtu);
            for (String mNom : nomsMatieres) {
                JLabel lI = creerCellule(mNom + " — Int.", MainFrame.CHOCOLAT, Color.WHITE, true);
                JLabel lD = creerCellule(mNom + " — Dev.", MainFrame.CHOCOLAT, Color.WHITE, true);
                grille.add(lI);
                grille.add(lD);
            }

            // Lignes étudiants
            for (int i = 0; i < etudiants.size(); i++) {
                int eId = etudiants.get(i)[0];
                Color bg = i % 2 == 0 ? new Color(235, 245, 251) : Color.WHITE;
                JLabel lblN = creerCellule(nomsEtu.get(i), bg, MainFrame.CHOCOLAT, false);
                grille.add(lblN);

                for (int j = 0; j < matieres.size(); j++) {
                    int mId = matieres.get(j)[0];
                    String key = eId + "_" + mId;
                    double[] vals = notesMap.getOrDefault(key, new double[]{-1, -1});

                    JTextField tfI = new JTextField(vals[0] >= 0 ? String.valueOf(vals[0]) : "", 4);
                    JTextField tfD = new JTextField(vals[1] >= 0 ? String.valueOf(vals[1]) : "", 4);
                    tfI.setHorizontalAlignment(JTextField.CENTER);
                    tfD.setHorizontalAlignment(JTextField.CENTER);
                    tfI.setFont(MainFrame.FONT_NORMAL);
                    tfD.setFont(MainFrame.FONT_NORMAL);

                    champNotes.put(key, new JTextField[]{tfI, tfD});
                    grille.add(tfI);
                    grille.add(tfD);
                }
            }

            grillePanel.add(grille, BorderLayout.NORTH);

            // Bouton enregistrer
            JButton btnSave = MainFrame.creerBouton("💾  Enregistrer toutes les notes", MainFrame.CHOC_MOYEN);
            btnSave.addActionListener(e -> enregistrerNotes());
            JPanel btnP = new JPanel();
            btnP.setBackground(MainFrame.FOND);
            btnP.add(btnSave);
            grillePanel.add(btnP, BorderLayout.CENTER);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erreur : " + e.getMessage());
        }

        grillePanel.revalidate();
        grillePanel.repaint();
    }

    private JLabel creerCellule(String texte, Color bg, Color fg, boolean bold) {
        JLabel lbl = new JLabel(texte, SwingConstants.CENTER);
        lbl.setOpaque(true);
        lbl.setBackground(bg);
        lbl.setForeground(fg);
        lbl.setFont(bold ? new Font("Segoe UI", Font.BOLD, 11) : MainFrame.FONT_PETIT);
        lbl.setBorder(new EmptyBorder(6, 4, 6, 4));
        return lbl;
    }

    private void enregistrerNotes() {
        int erreurs = 0;
        try {
            Connection conn = Database.getConnection();
            for (Map.Entry<String, JTextField[]> entry : champNotes.entrySet()) {
                String[] ids = entry.getKey().split("_");
                int eId = Integer.parseInt(ids[0]);
                int mId = Integer.parseInt(ids[1]);
                String si = entry.getValue()[0].getText().trim();
                String sd = entry.getValue()[1].getText().trim();
                if (si.isEmpty() && sd.isEmpty()) continue;
                try {
                    double i = si.isEmpty() ? -1 : Double.parseDouble(si.replace(",", "."));
                    double d = sd.isEmpty() ? -1 : Double.parseDouble(sd.replace(",", "."));
                    if ((i != -1 && (i < 0 || i > 20)) || (d != -1 && (d < 0 || d > 20))) {
                        erreurs++; continue;
                    }
                    String sql = String.format(
                        "INSERT INTO notes (etudiant_id, matiere_id, interrogation, devoir) VALUES (%d,%d,%s,%s) " +
                        "ON CONFLICT(etudiant_id, matiere_id) DO UPDATE SET interrogation=%s, devoir=%s",
                        eId, mId,
                        i == -1 ? "NULL" : String.valueOf(i),
                        d == -1 ? "NULL" : String.valueOf(d),
                        i == -1 ? "NULL" : String.valueOf(i),
                        d == -1 ? "NULL" : String.valueOf(d));
                    conn.createStatement().execute(sql);
                } catch (NumberFormatException ex) { erreurs++; }
            }
            if (erreurs > 0)
                JOptionPane.showMessageDialog(this, erreurs + " note(s) invalide(s) ignorée(s).", "Attention", JOptionPane.WARNING_MESSAGE);
            else
                JOptionPane.showMessageDialog(this, "Notes enregistrées avec succès !");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erreur : " + e.getMessage());
        }
    }
}