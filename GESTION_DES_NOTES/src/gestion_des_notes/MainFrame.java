package gestion_des_notes;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Fenêtre principale de l'application
 * UATM/GASA FORMATION — Projet SIL2 2026
 */
public class MainFrame extends JFrame {

    // Couleurs
    public static final Color CHOCOLAT     = new Color(62, 32, 12);    
    public static final Color ROUGE_VIN    = new Color(114, 27, 50);  
    public static final Color BRONZE       = new Color(205, 127, 50); 
    public static final Color FOND         = new Color(245, 235, 225);
    public static final Color CHOC_MOYEN   = new Color(93, 48, 18);   
    public static final Color ROUGE_CLAIR  = new Color(145, 40, 65);  

    // Polices
    public static final Font FONT_TITRE   = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_NORMAL  = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font FONT_PETIT   = new Font("Segoe UI", Font.PLAIN, 11);

    // Mot de passe admin
    public static final String ADMIN_PASSWORD = "1412";

    private JPanel panelContenu;
    private JPanel sidebar;

    public MainFrame() {
        setTitle("Gestion des Notes Étudiants — UATM/GASA 2026");
        setSize(1150, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(900, 600));
        construireInterface();
        afficherPage(new AccueilPanel(this));
    }

    private void construireInterface() {
        setLayout(new BorderLayout());

        // ── EN-TÊTE ──────────────────────────────────────
        JPanel entete = new JPanel(new BorderLayout());
        entete.setBackground(new Color(62, 32, 12));
        entete.setBorder(new EmptyBorder(12, 20, 12, 20));

        JLabel lblTitre = new JLabel("🎓  Gestion des Notes Étudiants");
        lblTitre.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitre.setForeground(Color.WHITE);
        entete.add(lblTitre, BorderLayout.WEST);

        JLabel lblSous = new JLabel("UATM/GASA FORMATION — SIL 2ème Année — 2025/2026");
        lblSous.setFont(FONT_PETIT);
        lblSous.setForeground(new Color(176, 200, 224));
        entete.add(lblSous, BorderLayout.EAST);

        add(entete, BorderLayout.NORTH);

        // ── SIDEBAR ──────────────────────────────────────
        sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(CHOCOLAT);
        sidebar.setPreferredSize(new Dimension(200, 0));
        sidebar.setBorder(new EmptyBorder(10, 0, 10, 0));

        JLabel lblMenu = new JLabel("  MENU");
        lblMenu.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblMenu.setForeground(new Color(144, 175, 197));
        lblMenu.setBorder(new EmptyBorder(5, 10, 8, 0));
        sidebar.add(lblMenu);

        String[][] menus = {
            {"🏠  Accueil",           "accueil"},
            {"👤  Étudiants",         "etudiants"},
            {"📝  Saisie des notes",  "notes"},
            {"📊  Relevé de notes",   "releve"},
            {"📄  Bulletin",          "bulletin"},
            {"📚  Matières",          "matieres"},
            {"🎓  Filières",          "filieres"},
        };

        for (String[] menu : menus) {
            sidebar.add(creerBoutonMenu(menu[0], menu[1]));
        }

        add(sidebar, BorderLayout.WEST);

        // ── ZONE CONTENU ─────────────────────────────────
        panelContenu = new JPanel(new BorderLayout());
        panelContenu.setBackground(FOND);
        panelContenu.setBorder(new EmptyBorder(15, 15, 15, 15));
        add(panelContenu, BorderLayout.CENTER);

        // ── PIED DE PAGE ─────────────────────────────────
        JPanel footer = new JPanel();
        footer.setBackground(new Color(62, 32, 12));
        footer.setBorder(new EmptyBorder(6, 10, 6, 10));
        JLabel lblFooter = new JLabel("© 2025 UATM/GASA FORMATION — Projet SIL2 — 2ème Année");
        lblFooter.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lblFooter.setForeground(new Color(150, 170, 190));
        footer.add(lblFooter);
        add(footer, BorderLayout.SOUTH);
    }

    private JButton creerBoutonMenu(String texte, String page) {
        JButton btn = new JButton(texte);
        btn.setFont(FONT_NORMAL);
        btn.setForeground(Color.WHITE);
        btn.setBackground(CHOCOLAT);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(new EmptyBorder(10, 16, 10, 16));
        btn.setMaximumSize(new Dimension(200, 45));
        btn.setMinimumSize(new Dimension(200, 45));
        btn.setPreferredSize(new Dimension(200, 45));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(ROUGE_VIN); }
            public void mouseExited(MouseEvent e)  { btn.setBackground(CHOCOLAT); }
        });

        btn.addActionListener(e -> {
            switch (page) {
                case "accueil"   -> afficherPage(new AccueilPanel(this));
                case "etudiants" -> afficherPage(new EtudiantsPanel(this));
                case "notes"     -> afficherPage(new NotesPanel(this));
                case "releve"    -> afficherPage(new RelevePanel(this));
                case "bulletin"  -> afficherPage(new BulletinPanel(this));
                case "matieres"  -> afficherPage(new MatieresPanel(this));
                case "filieres"  -> afficherPage(new FilieresPanel(this));
            }
        });

        return btn;
    }

    public void afficherPage(JPanel page) {
        panelContenu.removeAll();
        panelContenu.add(page, BorderLayout.CENTER);
        panelContenu.revalidate();
        panelContenu.repaint();
    }

    public static boolean verifierAdmin(Component parent) {
        JPasswordField pwd = new JPasswordField();
        int ok = JOptionPane.showConfirmDialog(parent,
            new Object[]{"Mot de passe administrateur :", pwd},
            "Authentification", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        return ok == JOptionPane.OK_OPTION &&
               new String(pwd.getPassword()).equals(ADMIN_PASSWORD);
    }

    public static JButton creerBouton(String texte, Color couleur) {
        JButton btn = new JButton(texte);
        btn.setFont(FONT_NORMAL);
        btn.setBackground(couleur);
        btn.setForeground(Color.WHITE);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(8, 16, 8, 16));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(couleur.darker()); }
            public void mouseExited(MouseEvent e)  { btn.setBackground(couleur); }
        });
        return btn;
    }

    public static JPanel creerEntete(String titre) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
        p.setBackground(FOND);
        JLabel lbl = new JLabel(titre);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lbl.setForeground(CHOCOLAT);
        p.add(lbl);
        return p;
    }
}