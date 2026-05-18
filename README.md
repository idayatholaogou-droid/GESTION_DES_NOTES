
GestionNotes.java	Point d'entrée (méthode main) – lance la page de connexion
LoginFrame.java	Fenêtre de connexion avec authentification (admin/admin123)
MainFrame.java	Fenêtre principale : sidebar + navigation + constantes de couleurs
Database.java	Connexion SQLite, création tables + vues, données de démo, calculs
AccueilPanel.java	Tableau de bord : statistiques et accès rapides
EtudiantsPanel.java	CRUD étudiants : ajout, modification, suppression sécurisée
NotesPanel.java	Grille de saisie des notes par filière (Interrogation + Devoir)
RelevePanel.java	Relevé collectif classé par ordre de mérite (via vue_moyennes)
BulletinPanel.java	Bulletin individuel avec impression (via vue_bulletin)
MatieresPanel.java	CRUD matières par filière
FilieresPanel.java	CRUD filières


6. Description des Fonctionnalités
6.1 Page de connexion
Au démarrage, une page de connexion s’affiche. L’utilisateur doit saisir son nom d’utilisateur et son mot de passe. Après 3 tentatives échouées, l’application se ferme automatiquement.
•	Nom d'utilisateur : PROUDLY
•	Mot de passe :  1412

6.2 Gestion des étudiants (CRUD)
•	Ajout : saisie du matricule, nom, sexe, date et lieu de naissance, filière
•	Consultation : tableau interactif avec toutes les informations
•	Modification : sélection + authentification par mot de passe
•	Suppression : confirmation + authentification par mot de passe

6.3 Saisie des notes
La page de saisie affiche une grille complète : lignes = étudiants, colonnes = matières (Interrogation / Devoir). Un bouton unique enregistre toutes les notes. La validation s’assure que chaque note est entre 0 et 20.

6.4 Relevé de notes collectif
Le relevé utilise la vue vue_moyennes pour afficher automatiquement les étudiants classés par ordre de mérite avec leur moyenne générale et leur mention.

6.5 Bulletin individuel
En saisissant le matricule, l’application génère un bulletin complet via la vue vue_bulletin : informations étudiant, détail des notes par matière, moyenne générale et mention. Un bouton d’impression est disponible.
