package gestion_des_notes;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.sql.*;

public class AccueilPanel extends JPanel {

    public AccueilPanel(MainFrame frame) {
        setLayout(new BorderLayout(0, 20));
        setBackground(MainFrame.FOND);

        add(MainFrame.creerEntete("🏠  Tableau de Bord"), BorderLayout.NORTH);

        JPanel centre = new JPanel(new BorderLayout(0, 20));
        centre.setBackground(MainFrame.FOND);

        // ── Statistiques ──────────────────────────────
        JPanel stats = new JPanel(new GridLayout(1, 3, 15, 0));
        stats.setBackground(MainFrame.FOND);

        try {
            Connection conn = Database.getConnection();
            int nbEtu = 0, nbFil = 0, nbMat = 0;
            ResultSet rs;

            rs = conn.createStatement().executeQuery("SELECT COUNT(*) FROM etudiants");
            if (rs.next()) nbEtu = rs.getInt(1);

            rs = conn.createStatement().executeQuery("SELECT COUNT(*) FROM filieres");
            if (rs.next()) nbFil = rs.getInt(1);

            rs = conn.createStatement().executeQuery("SELECT COUNT(*) FROM matieres");
            if (rs.next()) nbMat = rs.getInt(1);

            stats.add(creerStatCard(String.valueOf(nbEtu), "Étudiants",  new Color(62, 32, 12)));
            stats.add(creerStatCard(String.valueOf(nbFil), "Filières",   new Color(114, 27, 50)));
            stats.add(creerStatCard(String.valueOf(nbMat), "Matières",   new Color(93, 48, 18)));

        } catch (SQLException e) {
            stats.add(new JLabel("Erreur : " + e.getMessage()));
        }

        centre.add(stats, BorderLayout.NORTH);

        // ── Accès rapides ──────────────────────────────
        JPanel acces = new JPanel(new GridLayout(2, 3, 15, 15));
        acces.setBackground(MainFrame.FOND);
        acces.setBorder(new EmptyBorder(10, 0, 0, 0));

        String[][] liens = {
            {"👤  Étudiants",         "Gérer les étudiants"},
            {"📝  Saisie des notes",  "Saisir les notes par filière"},
            {"📊  Relevé de notes",   "Classement par mérite"},
            {"📄  Bulletin",          "Bulletin individuel"},
            {"📚  Matières",          "Gérer les matières"},
            {"🎓  Filières",          "Gérer les filières"},
        };
        String[] pages = {"etudiants","notes","releve","bulletin","matieres","filieres"};

        for (int i = 0; i < liens.length; i++) {
            final String page = pages[i];
            JPanel card = new JPanel(new BorderLayout());
            card.setBackground(Color.WHITE);
            card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 230, 240), 1),
                new EmptyBorder(20, 20, 20, 20)));

            JLabel titre = new JLabel(liens[i][0]);
            titre.setFont(new Font("Segoe UI", Font.BOLD, 13));
            titre.setForeground(MainFrame.CHOCOLAT);

            JLabel sous = new JLabel(liens[i][1]);
            sous.setFont(MainFrame.FONT_PETIT);
            sous.setForeground(Color.GRAY);

            JButton btn = MainFrame.creerBouton("Ouvrir →", MainFrame.CHOCOLAT);
            btn.addActionListener(e -> {
                switch (page) {
                    case "etudiants" -> frame.afficherPage(new EtudiantsPanel(frame));
                    case "notes"     -> frame.afficherPage(new NotesPanel(frame));
                    case "releve"    -> frame.afficherPage(new RelevePanel(frame));
                    case "bulletin"  -> frame.afficherPage(new BulletinPanel(frame));
                    case "matieres"  -> frame.afficherPage(new MatieresPanel(frame));
                    case "filieres"  -> frame.afficherPage(new FilieresPanel(frame));
                }
            });

            JPanel info = new JPanel(new GridLayout(2, 1, 0, 4));
            info.setBackground(Color.WHITE);
            info.add(titre);
            info.add(sous);

            card.add(info, BorderLayout.CENTER);
            card.add(btn, BorderLayout.SOUTH);
            acces.add(card);
        }

        centre.add(acces, BorderLayout.CENTER);

        JLabel info = new JLabel("Mot de passe administrateur par défaut : admin123", SwingConstants.CENTER);
        info.setFont(MainFrame.FONT_PETIT);
        info.setForeground(Color.GRAY);
        centre.add(info, BorderLayout.SOUTH);

        add(centre, BorderLayout.CENTER);
    }

    private JPanel creerStatCard(String valeur, String label, Color couleur) {
        JPanel card = new JPanel(new GridLayout(2, 1));
        card.setBackground(couleur);
        card.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel lblVal = new JLabel(valeur, SwingConstants.CENTER);
        lblVal.setFont(new Font("Segoe UI", Font.BOLD, 42));
        lblVal.setForeground(Color.WHITE);

        JLabel lblLbl = new JLabel(label, SwingConstants.CENTER);
        lblLbl.setFont(MainFrame.FONT_NORMAL);
        lblLbl.setForeground(new Color(220, 235, 245));

        card.add(lblVal);
        card.add(lblLbl);
        return card;
    }
}