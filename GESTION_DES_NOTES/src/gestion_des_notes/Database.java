package gestion_des_notes;

import java.sql.*;


public class Database {

    private static final String DB_URL = "jdbc:sqlite:" + System.getProperty("user.dir") + "/notes_etudiants.db";
    private static Connection connection = null;

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Class.forName("org.sqlite.JDBC");
                connection = DriverManager.getConnection(DB_URL);
                connection.createStatement().execute("PRAGMA foreign_keys = ON");
            } catch (ClassNotFoundException e) {
                throw new SQLException("Driver SQLite introuvable : " + e.getMessage());
            }
        }
        return connection;
    }

    public static void initialiserDB() {
        try (Statement stmt = getConnection().createStatement()) {

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS filieres (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    nom TEXT NOT NULL UNIQUE
                )
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS matieres (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    nom TEXT NOT NULL,
                    filiere_id INTEGER NOT NULL,
                    FOREIGN KEY (filiere_id) REFERENCES filieres(id) ON DELETE CASCADE,
                    UNIQUE(nom, filiere_id)
                )
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS etudiants (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    matricule TEXT NOT NULL UNIQUE,
                    nom TEXT NOT NULL,
                    sexe TEXT NOT NULL CHECK(sexe IN ('M','F')),
                    date_naissance TEXT NOT NULL,
                    lieu_naissance TEXT NOT NULL,
                    filiere_id INTEGER NOT NULL,
                    FOREIGN KEY (filiere_id) REFERENCES filieres(id) ON DELETE CASCADE
                )
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS notes (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    etudiant_id INTEGER NOT NULL,
                    matiere_id INTEGER NOT NULL,
                    interrogation REAL CHECK(interrogation BETWEEN 0 AND 20),
                    devoir REAL CHECK(devoir BETWEEN 0 AND 20),
                    FOREIGN KEY (etudiant_id) REFERENCES etudiants(id) ON DELETE CASCADE,
                    FOREIGN KEY (matiere_id) REFERENCES matieres(id) ON DELETE CASCADE,
                    UNIQUE(etudiant_id, matiere_id)
                )
            """);

          
            stmt.execute("DROP VIEW IF EXISTS vue_etudiants");
            stmt.execute("""
                CREATE VIEW vue_etudiants AS
                SELECT
                    e.id,
                    e.matricule,
                    e.nom,
                    e.sexe,
                    e.date_naissance,
                    e.lieu_naissance,
                    f.nom AS filiere,
                    e.filiere_id
                FROM etudiants e
                JOIN filieres f ON e.filiere_id = f.id
                ORDER BY e.nom
            """);

            stmt.execute("DROP VIEW IF EXISTS vue_notes");
            stmt.execute("""
                CREATE VIEW vue_notes AS
                SELECT
                    n.id,
                    e.matricule,
                    e.nom AS etudiant,
                    f.nom AS filiere,
                    m.nom AS matiere,
                    n.interrogation,
                    n.devoir,
                    ROUND(0.3 * n.interrogation + 0.7 * n.devoir, 2) AS moyenne,
                    n.etudiant_id,
                    n.matiere_id
                FROM notes n
                JOIN etudiants e ON n.etudiant_id = e.id
                JOIN matieres m ON n.matiere_id = m.id
                JOIN filieres f ON e.filiere_id = f.id
                ORDER BY e.nom, m.nom
            """);

            stmt.execute("DROP VIEW IF EXISTS vue_moyennes");
            stmt.execute("""
                CREATE VIEW vue_moyennes AS
                SELECT
                    e.id,
                    e.matricule,
                    e.nom,
                    f.nom AS filiere,
                    ROUND(AVG(0.3 * n.interrogation + 0.7 * n.devoir), 2) AS moyenne_generale,
                    COUNT(n.id) AS nb_matieres
                FROM etudiants e
                JOIN filieres f ON e.filiere_id = f.id
                LEFT JOIN notes n ON n.etudiant_id = e.id
                GROUP BY e.id, e.matricule, e.nom, f.nom
                ORDER BY moyenne_generale DESC
            """);

            stmt.execute("DROP VIEW IF EXISTS vue_bulletin");
            stmt.execute("""
                CREATE VIEW vue_bulletin AS
                SELECT
                    e.matricule,
                    e.nom,
                    e.sexe,
                    e.date_naissance,
                    e.lieu_naissance,
                    f.nom AS filiere,
                    m.nom AS matiere,
                    n.interrogation,
                    n.devoir,
                    ROUND(0.3 * n.interrogation + 0.7 * n.devoir, 2) AS moyenne,
                    CASE
                        WHEN ROUND(0.3 * n.interrogation + 0.7 * n.devoir, 2) >= 16 THEN 'Très Bien'
                        WHEN ROUND(0.3 * n.interrogation + 0.7 * n.devoir, 2) >= 14 THEN 'Bien'
                        WHEN ROUND(0.3 * n.interrogation + 0.7 * n.devoir, 2) >= 12 THEN 'Assez Bien'
                        WHEN ROUND(0.3 * n.interrogation + 0.7 * n.devoir, 2) >= 10 THEN 'Passable'
                        ELSE 'Insuffisant'
                    END AS mention
                FROM etudiants e
                JOIN filieres f ON e.filiere_id = f.id
                JOIN notes n ON n.etudiant_id = e.id
                JOIN matieres m ON n.matiere_id = m.id
                ORDER BY e.nom, m.nom
            """);

           
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as n FROM filieres");
            if (rs.next() && rs.getInt("n") == 0) {
                stmt.execute("INSERT INTO filieres (nom) VALUES ('SIL2 - 2ème Année')");
                String[] matieres = {"Mérise", "UML", "RO", "TEEO", "Anglais"};
                for (String m : matieres) {
                    stmt.execute("INSERT INTO matieres (nom, filiere_id) VALUES ('" + m + "', 1)");
                }
                String[][] etudiants = {
                    {"001", "BOURAIMA Nadira",   "F", "2005-11-21", "Porto-Novo"},
                    {"002", "KOUASSI Cica",      "F", "2004-06-06", "Avrankou"},
                    {"003", "MISSIKPODE Amel",   "M", "2005-07-12", "Avrankou"},
                    {"004", "CODJO Bidossessi",  "F", "2007-10-10", "Porto-Novo"},
                    {"005", "BOLOUWATIFE Akman", "M", "2006-10-10", "Congo Kinshasa"},
                };
                for (String[] e : etudiants) {
                    stmt.execute(String.format(
                        "INSERT INTO etudiants (matricule,nom,sexe,date_naissance,lieu_naissance,filiere_id) VALUES ('%s','%s','%s','%s','%s',1)",
                        e[0], e[1], e[2], e[3], e[4]
                    ));
                }
            }

        } catch (SQLException e) {
            javax.swing.JOptionPane.showMessageDialog(null,
                "Erreur base de données : " + e.getMessage(), "Erreur", javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }

    public static double calculerMoyenne(double interro, double devoir) {
        return Math.round((0.3 * interro + 0.7 * devoir) * 100.0) / 100.0;
    }

    public static String getMention(double moy) {
        if (moy >= 16) return "Très Bien";
        if (moy >= 14) return "Bien";
        if (moy >= 12) return "Assez Bien";
        if (moy >= 10) return "Passable";
        return "Insuffisant";
    }
}