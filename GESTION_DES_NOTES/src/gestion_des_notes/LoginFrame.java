package gestion_des_notes;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

public class LoginFrame extends JFrame {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private int tentatives = 0;

    public LoginFrame() {
        setTitle("Connexion — UATM/GASA FORMATION");
        setSize(450, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        construireInterface();
    }

    private void construireInterface() {
        setLayout(new BorderLayout());

        // En-tête
        JPanel entete = new JPanel();
        entete.setLayout(new BoxLayout(entete, BoxLayout.Y_AXIS));
        entete.setBackground(new Color(93, 48, 18));
        entete.setBorder(new EmptyBorder(30, 20, 30, 20));

        JLabel lblIcon = new JLabel("🎓", SwingConstants.CENTER);
        lblIcon.setFont(new Font("Segoe UI", Font.PLAIN, 48));
        lblIcon.setForeground(Color.WHITE);
        lblIcon.setAlignmentX(CENTER_ALIGNMENT);

        JLabel lblTitre = new JLabel("UATM/GASA FORMATION");
        lblTitre.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitre.setForeground(Color.WHITE);
        lblTitre.setAlignmentX(CENTER_ALIGNMENT);

        JLabel lblSous = new JLabel("Gestion des Notes Etudiants");
        lblSous.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSous.setForeground(new Color(245, 235, 225));
        lblSous.setAlignmentX(CENTER_ALIGNMENT);

        entete.add(lblIcon);
        entete.add(Box.createVerticalStrut(8));
        entete.add(lblTitre);
        entete.add(Box.createVerticalStrut(5));
        entete.add(lblSous);
        add(entete, BorderLayout.NORTH);

        // Formulaire
        JPanel form = new JPanel(null);
        form.setBackground(Color.WHITE);
        form.setPreferredSize(new Dimension(450, 340));

        JLabel lblConnexion = new JLabel("Connexion");
        lblConnexion.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblConnexion.setForeground(new Color(93, 48, 18));
        lblConnexion.setBounds(45, 20, 300, 30);
        form.add(lblConnexion);

        JLabel lblUser = new JLabel("Nom d'utilisateur");
        lblUser.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblUser.setForeground(new Color(93, 48, 18));
        lblUser.setBounds(45, 65, 200, 20);
        form.add(lblUser);

        txtUsername = new JTextField();
        txtUsername.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtUsername.setBounds(45, 88, 360, 40);
        txtUsername.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(245, 235, 225), 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        form.add(txtUsername);

        JLabel lblPass = new JLabel("Mot de passe");
        lblPass.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblPass.setForeground(new Color(93, 48, 18));
        lblPass.setBounds(45, 145, 200, 20);
        form.add(lblPass);

        txtPassword = new JPasswordField();
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtPassword.setBounds(45, 168, 360, 40);
        txtPassword.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(245, 235, 225), 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        txtPassword.addActionListener(e -> seConnecter());
        form.add(txtPassword);

        JButton btnConnexion = new JButton("Se connecter");
        btnConnexion.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnConnexion.setBackground(new Color(93, 48, 18));
        btnConnexion.setForeground(Color.WHITE);
        btnConnexion.setBorderPainted(false);
        btnConnexion.setFocusPainted(false);
        btnConnexion.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnConnexion.setBounds(45, 228, 360, 42);
        btnConnexion.addActionListener(e -> seConnecter());
        btnConnexion.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btnConnexion.setBackground(new Color(93, 48, 18)); }
            public void mouseExited(MouseEvent e)  { btnConnexion.setBackground(new Color(93, 48, 18)); }
        });
        form.add(btnConnexion);

        JButton btnAnnuler = new JButton("Annuler / Fermer");
        btnAnnuler.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnAnnuler.setBackground(new Color(93, 48, 18));
        btnAnnuler.setForeground(Color.WHITE);
        btnAnnuler.setBorderPainted(false);
        btnAnnuler.setFocusPainted(false);
        btnAnnuler.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAnnuler.setBounds(45, 280, 360, 42);
        btnAnnuler.addActionListener(e -> System.exit(0));
        btnAnnuler.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btnAnnuler.setBackground(new Color(93, 48, 18)); }
            public void mouseExited(MouseEvent e)  { btnAnnuler.setBackground(new Color(245, 235, 225)); }
        });
        form.add(btnAnnuler);

        add(form, BorderLayout.CENTER);

        // Pied de page
        JPanel footer = new JPanel();
        footer.setLayout(new BoxLayout(footer, BoxLayout.Y_AXIS));
        footer.setBackground(new Color(93, 48, 18));
        footer.setBorder(new EmptyBorder(8, 20, 8, 20));

        JLabel lblInfo = new JLabel("Utilisateur : PROUDLY  |  Mot de passe : 1412");
        lblInfo.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lblInfo.setForeground(Color.GRAY);
        lblInfo.setAlignmentX(CENTER_ALIGNMENT);

        JLabel lblAnnee = new JLabel("SIL2 - 2eme Annee - 2025/2026");
        lblAnnee.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lblAnnee.setForeground(Color.GRAY);
        lblAnnee.setAlignmentX(CENTER_ALIGNMENT);

        footer.add(lblInfo);
        footer.add(Box.createVerticalStrut(3));
        footer.add(lblAnnee);
        add(footer, BorderLayout.SOUTH);
    }

    private void seConnecter() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();

        if (username.equals("PROUDLY") && password.equals("1412")) {
            dispose();
            SwingUtilities.invokeLater(() -> {
                MainFrame frame = new MainFrame();
                frame.setVisible(true);
            });
        } else {
            tentatives++;
            txtPassword.setText("");
            if (tentatives >= 3) {
                JOptionPane.showMessageDialog(this,
                    "Trop de tentatives echouees.\nL'application va se fermer.",
                    "Acces refuse", JOptionPane.ERROR_MESSAGE);
                System.exit(0);
            } else {
                JOptionPane.showMessageDialog(this,
                    "Nom d'utilisateur ou mot de passe incorrect.\nTentative " + tentatives + "/3",
                    "Erreur de connexion", JOptionPane.ERROR_MESSAGE);
                txtUsername.requestFocus();
            }
        }
    }
}