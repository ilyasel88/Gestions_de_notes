
import java.sql.*;
import java.util.*;

public class StockageMySQL {
    private Connection connexion;

    public StockageMySQL() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            String url = "jdbc:mysql://localhost:3306/gestion_notes?useSSL=false&serverTimezone=UTC";
            String user = "root";
            String password = "root";
            connexion = DriverManager.getConnection(url, user, password);
            initialiserTables();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public Utilisateur authentifier(String nom, String motDePasse) {
        String sql = "SELECT * FROM utilisateur WHERE nom = ? AND mot_de_passe = ?";
        try (PreparedStatement ps = connexion.prepareStatement(sql)) {
            ps.setString(1, nom);
            ps.setString(2, motDePasse);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Utilisateur(rs.getInt("id"), rs.getString("nom"), 
                                       rs.getString("mot_de_passe"), rs.getString("role"), rs.getString("filiere"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public Etudiant getEtudiantParNom(String nomUtilisateur) {
    String sql = "SELECT e.*, p.id as promo_id, p.nom as promo_nom, p.filiere, p.annee_academique " +
                 "FROM etudiant e LEFT JOIN promotion p ON e.promotion_id = p.id " +
                 "WHERE CONCAT(e.prenom, ' ', e.nom) = ? " +
                 "OR CONCAT(e.prenom, e.nom) = ? " +
                 "OR CONCAT(e.nom, e.prenom) = ? " +
                 "OR e.num_etudiant = ?";
    try (PreparedStatement ps = connexion.prepareStatement(sql)) {
        ps.setString(1, nomUtilisateur);
        ps.setString(2, nomUtilisateur);
        ps.setString(3, nomUtilisateur);
        ps.setString(4, nomUtilisateur);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            Promotion promo = new Promotion(rs.getInt("promo_id"), rs.getString("promo_nom"),
                                            rs.getString("filiere"), rs.getString("annee_academique"));
            Etudiant e = new Etudiant(rs.getInt("id"), rs.getString("prenom"),
                                      rs.getString("nom"), rs.getString("num_etudiant"));
            e.setArchive(rs.getBoolean("archive"));
            e.setPromotion(promo);
            e.setDateNaissance(rs.getString("date_naissance"));
            e.setTelephone(rs.getString("telephone"));
            e.setEmail(rs.getString("email"));
            return e;
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return null;
}





public void saveEnseignant(Enseignant e) {
    String sql = "INSERT INTO enseignant (id, nom) VALUES (?,?) ON DUPLICATE KEY UPDATE nom=?";
    try (PreparedStatement ps = connexion.prepareStatement(sql)) {
        ps.setInt(1, e.getId());
        ps.setString(2, e.getNom());
        ps.setString(3, e.getNom());
        ps.executeUpdate();
    } catch (SQLException ex) {
        ex.printStackTrace();
    }
}

public void deleteEnseignant(int id) {
    
    Enseignant e = getEnseignantById(id);
    if (e == null) return;
    
    
    String nomUtilisateur = e.getNom().replaceAll("\\s+", "");
    supprimerUtilisateurEnseignant(nomUtilisateur);
    
    
    try (PreparedStatement ps = connexion.prepareStatement("DELETE FROM enseignant WHERE id = ?")) {
        ps.setInt(1, id);
        ps.executeUpdate();
    } catch (SQLException ex) {
        ex.printStackTrace();
    }
}

public void supprimerUtilisateurEnseignant(String username) {
    String sql = "DELETE FROM utilisateur WHERE nom = ? AND role = 'ENSEIGNANT'";
    try (PreparedStatement ps = connexion.prepareStatement(sql)) {
        ps.setString(1, username);
        ps.executeUpdate();
    } catch (SQLException e) {
        e.printStackTrace();
    }
}

public void creerUtilisateurEnseignant(String username, String password) {
    String sql = "INSERT INTO utilisateur (nom, mot_de_passe, role, filiere) VALUES (?, ?, 'ENSEIGNANT', NULL)";
    try (PreparedStatement ps = connexion.prepareStatement(sql)) {
        ps.setString(1, username);
        ps.setString(2, password);
        ps.executeUpdate();
    } catch (SQLException e) {
        
        if (e.getErrorCode() == 1062) {
            String updateSql = "UPDATE utilisateur SET mot_de_passe = ? WHERE nom = ? AND role = 'ENSEIGNANT'";
            try (PreparedStatement updatePs = connexion.prepareStatement(updateSql)) {
                updatePs.setString(1, password);
                updatePs.setString(2, username);
                updatePs.executeUpdate();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } else {
            e.printStackTrace();
        }
    }
}
    
    public Enseignant getEnseignantParNom(String nom) {
        String sql = "SELECT * FROM enseignant WHERE nom = ?";
        try (PreparedStatement ps = connexion.prepareStatement(sql)) {
            ps.setString(1, nom);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Enseignant(rs.getInt("id"), rs.getString("nom"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void initialiserTables() {
        String sqlUtilisateur = "CREATE TABLE IF NOT EXISTS utilisateur (" +
                "id INT PRIMARY KEY AUTO_INCREMENT, nom VARCHAR(100) UNIQUE, " +
                "mot_de_passe VARCHAR(100), role VARCHAR(50), filiere VARCHAR(100))";
        String sqlPromotion = "CREATE TABLE IF NOT EXISTS promotion (" +
                "id INT PRIMARY KEY AUTO_INCREMENT, nom VARCHAR(100), filiere VARCHAR(100), annee_academique VARCHAR(50))";
        String sqlEtudiant = "CREATE TABLE IF NOT EXISTS etudiant (" +
                "id INT PRIMARY KEY AUTO_INCREMENT, prenom VARCHAR(100), nom VARCHAR(100), " +
                "num_etudiant VARCHAR(50) UNIQUE, archive BOOLEAN, promotion_id INT, " +
                "date_naissance VARCHAR(20), telephone VARCHAR(20), email VARCHAR(100), " +  // NOUVEAU
                "FOREIGN KEY (promotion_id) REFERENCES promotion(id))";
        String sqlModule = "CREATE TABLE IF NOT EXISTS module (" +
                "id INT PRIMARY KEY AUTO_INCREMENT, nom VARCHAR(100), description VARCHAR(500))";
        String sqlEnseignant = "CREATE TABLE IF NOT EXISTS enseignant (" +
                "id INT PRIMARY KEY AUTO_INCREMENT, nom VARCHAR(100))";
        String sqlSousModule = "CREATE TABLE IF NOT EXISTS sous_module (" +
                "id INT PRIMARY KEY AUTO_INCREMENT, nom VARCHAR(100), coefficient DOUBLE, " +
                "module_id INT, promotion_id INT, enseignant_id INT, " +
                "FOREIGN KEY (module_id) REFERENCES module(id), " +
                "FOREIGN KEY (promotion_id) REFERENCES promotion(id), " +
                "FOREIGN KEY (enseignant_id) REFERENCES enseignant(id))";
        String sqlNote = "CREATE TABLE IF NOT EXISTS note (" +
                "etudiant_id INT, sous_module_id INT, valeur DOUBLE, " +
                "PRIMARY KEY(etudiant_id, sous_module_id), " +
                "FOREIGN KEY (etudiant_id) REFERENCES etudiant(id), " +
                "FOREIGN KEY (sous_module_id) REFERENCES sous_module(id))";
        String sqlValidation = "CREATE TABLE IF NOT EXISTS validation_promotion (" +
                "etudiant_id INT, promotion_id INT, valide BOOLEAN, " +
                "PRIMARY KEY(etudiant_id, promotion_id), " +
                "FOREIGN KEY (etudiant_id) REFERENCES etudiant(id), " +
                "FOREIGN KEY (promotion_id) REFERENCES promotion(id))";
        
        try (Statement stmt = connexion.createStatement()) {
            stmt.execute(sqlUtilisateur);
            stmt.execute(sqlPromotion);
            stmt.execute(sqlEtudiant);
            stmt.execute(sqlModule);
            stmt.execute(sqlEnseignant);
            stmt.execute(sqlSousModule);
            stmt.execute(sqlNote);
            stmt.execute(sqlValidation);
            try {
                stmt.execute("ALTER TABLE note ADD COLUMN valeur_rattrapage DOUBLE DEFAULT -1");
            } catch (SQLException e) { /* Column already exists */ }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    
    public List<Etudiant> getAllEtudiants() {
    List<Etudiant> list = new ArrayList<>();
    try (Statement stmt = connexion.createStatement();
         ResultSet rs = stmt.executeQuery("SELECT * FROM etudiant")) {
        while (rs.next()) {
            Etudiant e = new Etudiant(rs.getInt("id"), rs.getString("prenom"), 
                    rs.getString("nom"), rs.getString("num_etudiant"));
            e.setArchive(rs.getBoolean("archive"));
            e.setPromotion(getPromotionById(rs.getInt("promotion_id")));
            e.setDateNaissance(rs.getString("date_naissance"));
            e.setTelephone(rs.getString("telephone"));
            e.setEmail(rs.getString("email"));
            list.add(e);
        }
    } catch (SQLException e) { e.printStackTrace(); }
    return list;
}

    public Etudiant getEtudiantById(int id) {
    try (PreparedStatement ps = connexion.prepareStatement("SELECT * FROM etudiant WHERE id = ?")) {
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            Etudiant e = new Etudiant(rs.getInt("id"), rs.getString("prenom"), 
                    rs.getString("nom"), rs.getString("num_etudiant"));
            e.setArchive(rs.getBoolean("archive"));
            e.setPromotion(getPromotionById(rs.getInt("promotion_id")));
            e.setDateNaissance(rs.getString("date_naissance"));
            e.setTelephone(rs.getString("telephone"));
            e.setEmail(rs.getString("email"));
            return e;
        }
    } catch (SQLException e) { e.printStackTrace(); }
    return null;
}
public void creerUtilisateurEtudiant(String username, String password, String nomComplet) {
    
    String checkSql = "SELECT id FROM utilisateur WHERE nom = ?";
    try (PreparedStatement checkPs = connexion.prepareStatement(checkSql)) {
        checkPs.setString(1, username);
        ResultSet rs = checkPs.executeQuery();
        if (rs.next()) {
            
            String updateSql = "UPDATE utilisateur SET mot_de_passe = ? WHERE nom = ?";
            try (PreparedStatement updatePs = connexion.prepareStatement(updateSql)) {
                updatePs.setString(1, password);
                updatePs.setString(2, username);
                updatePs.executeUpdate();
                System.out.println("Mot de passe mis à jour pour: " + username);
            }
            return;
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    
    
    String sql = "INSERT INTO utilisateur (nom, mot_de_passe, role, filiere) VALUES (?, ?, 'ETUDIANT', NULL)";
    try (PreparedStatement ps = connexion.prepareStatement(sql)) {
        ps.setString(1, username);
        ps.setString(2, password);
        ps.executeUpdate();
        System.out.println("Utilisateur créé: " + username + " / " + password);
    } catch (SQLException e) {
        e.printStackTrace();
    }
}
public void supprimerUtilisateur(String username) {
    String sql = "DELETE FROM utilisateur WHERE nom = ? AND role = 'ETUDIANT'";
    try (PreparedStatement ps = connexion.prepareStatement(sql)) {
        ps.setString(1, username);
        ps.executeUpdate();
        System.out.println("Utilisateur supprimé: " + username);
    } catch (SQLException e) {
        e.printStackTrace();
    }
}

    public void saveEtudiant(Etudiant e) {
    String sql = "INSERT INTO etudiant (id, prenom, nom, num_etudiant, archive, promotion_id, date_naissance, telephone, email) " +
                 "VALUES (?,?,?,?,?,?,?,?,?) ON DUPLICATE KEY UPDATE prenom=?, nom=?, num_etudiant=?, archive=?, promotion_id=?, date_naissance=?, telephone=?, email=?";
    try (PreparedStatement ps = connexion.prepareStatement(sql)) {
        ps.setInt(1, e.getId());
        ps.setString(2, e.getPrenom());
        ps.setString(3, e.getNom());
        ps.setString(4, e.getNumEtudiant());
        ps.setBoolean(5, e.isArchive());
        ps.setInt(6, e.getPromotion() != null ? e.getPromotion().getId() : null);
        ps.setString(7, e.getDateNaissance());
        ps.setString(8, e.getTelephone());
        ps.setString(9, e.getEmail());
        ps.setString(10, e.getPrenom());
        ps.setString(11, e.getNom());
        ps.setString(12, e.getNumEtudiant());
        ps.setBoolean(13, e.isArchive());
        ps.setInt(14, e.getPromotion() != null ? e.getPromotion().getId() : null);
        ps.setString(15, e.getDateNaissance());
        ps.setString(16, e.getTelephone());
        ps.setString(17, e.getEmail());
        ps.executeUpdate();
    } catch (SQLException ex) {
        ex.printStackTrace();
    }
}

    public void archiverEtudiant(int id, boolean archiver) {
        try (PreparedStatement ps = connexion.prepareStatement("UPDATE etudiant SET archive = ? WHERE id = ?")) {
            ps.setBoolean(1, archiver);
            ps.setInt(2, id);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }
    
    public void deleteEtudiantComplet(int id) {
        Etudiant et = getEtudiantById(id);
        if (et == null) return;
        
        
        try (PreparedStatement ps = connexion.prepareStatement("DELETE FROM note WHERE etudiant_id = ?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
        
        
        try (PreparedStatement ps = connexion.prepareStatement("DELETE FROM validation_promotion WHERE etudiant_id = ?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
        
        
        supprimerUtilisateur(et.getNumEtudiant());
        supprimerUtilisateur(et.getPrenom() + et.getNom());
        supprimerUtilisateur(et.getNom().replaceAll("\\s+", "") + et.getPrenom().replaceAll("\\s+", ""));
        supprimerUtilisateur(et.getPrenom().replaceAll("\\s+", "") + et.getNom().replaceAll("\\s+", ""));
        
        
        try (PreparedStatement ps = connexion.prepareStatement("DELETE FROM etudiant WHERE id = ?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public List<Etudiant> getEtudiantsByPromotion(int promotionId) {
        List<Etudiant> list = new ArrayList<>();
        try (PreparedStatement ps = connexion.prepareStatement("SELECT * FROM etudiant WHERE promotion_id = ? AND archive = false")) {
            ps.setInt(1, promotionId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Etudiant e = new Etudiant(rs.getInt("id"), rs.getString("prenom"), 
                        rs.getString("nom"), rs.getString("num_etudiant"));
                e.setArchive(rs.getBoolean("archive"));
                e.setPromotion(getPromotionById(promotionId));
                e.setDateNaissance(rs.getString("date_naissance"));
                e.setTelephone(rs.getString("telephone"));
                e.setEmail(rs.getString("email"));
                list.add(e);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public List<Promotion> getAllPromotions() {
        List<Promotion> list = new ArrayList<>();
        try (Statement stmt = connexion.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM promotion")) {
            while (rs.next()) {
                list.add(new Promotion(rs.getInt("id"), rs.getString("nom"), 
                        rs.getString("filiere"), rs.getString("annee_academique")));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    
    public Promotion getPromotionById(int id) {
        try (PreparedStatement ps = connexion.prepareStatement("SELECT * FROM promotion WHERE id = ?")) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Promotion(rs.getInt("id"), rs.getString("nom"), 
                        rs.getString("filiere"), rs.getString("annee_academique"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

   



    public void savePromotion(Promotion p) {
        String sql = "INSERT INTO promotion (id, nom, filiere, annee_academique) VALUES (?,?,?,?) " +
                     "ON DUPLICATE KEY UPDATE nom=?, filiere=?, annee_academique=?";
        try (PreparedStatement ps = connexion.prepareStatement(sql)) {
            ps.setInt(1, p.getId());
            ps.setString(2, p.getNom());
            ps.setString(3, p.getFiliere());
            ps.setString(4, p.getAnneeAcademique());
            ps.setString(5, p.getNom());
            ps.setString(6, p.getFiliere());
            ps.setString(7, p.getAnneeAcademique());
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void deletePromotion(int id) {
        try (PreparedStatement ps = connexion.prepareStatement("DELETE FROM promotion WHERE id = ?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    //  METHODES MODULES 
    public List<Module> getAllModules() {
        List<Module> list = new ArrayList<>();
        try (Statement stmt = connexion.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM module")) {
            while (rs.next()) {
                list.add(new Module(rs.getInt("id"), rs.getString("nom"), rs.getString("description")));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public Module getModuleById(int id) {
        try (PreparedStatement ps = connexion.prepareStatement("SELECT * FROM module WHERE id = ?")) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Module(rs.getInt("id"), rs.getString("nom"), rs.getString("description"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public void saveModule(Module m) {
        String sql = "INSERT INTO module (id, nom, description) VALUES (?,?,?) ON DUPLICATE KEY UPDATE nom=?, description=?";
        try (PreparedStatement ps = connexion.prepareStatement(sql)) {
            ps.setInt(1, m.getId());
            ps.setString(2, m.getNom());
            ps.setString(3, m.getDescription());
            ps.setString(4, m.getNom());
            ps.setString(5, m.getDescription());
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void deleteModule(int id) {
        try (PreparedStatement ps = connexion.prepareStatement("DELETE FROM module WHERE id = ?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    //  METHODES SOUS-MODULES 
    public List<SousModule> getAllSousModules() {
        List<SousModule> list = new ArrayList<>();
        try (Statement stmt = connexion.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM sous_module")) {
            while (rs.next()) {
                Module m = getModuleById(rs.getInt("module_id"));
                Promotion p = getPromotionById(rs.getInt("promotion_id"));
                SousModule sm = new SousModule(rs.getInt("id"), rs.getString("nom"), 
                        rs.getDouble("coefficient"), m, p);
                Enseignant e = getEnseignantById(rs.getInt("enseignant_id"));
                sm.setEnseignantAssigne(e);
                list.add(sm);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public SousModule getSousModuleById(int id) {
        try (PreparedStatement ps = connexion.prepareStatement("SELECT * FROM sous_module WHERE id = ?")) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Module m = getModuleById(rs.getInt("module_id"));
                Promotion p = getPromotionById(rs.getInt("promotion_id"));
                SousModule sm = new SousModule(rs.getInt("id"), rs.getString("nom"), 
                        rs.getDouble("coefficient"), m, p);
                Enseignant e = getEnseignantById(rs.getInt("enseignant_id"));
                sm.setEnseignantAssigne(e);
                return sm;
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public void saveSousModule(SousModule sm) {
        String sql = "INSERT INTO sous_module (id, nom, coefficient, module_id, promotion_id, enseignant_id) " +
                     "VALUES (?,?,?,?,?,?) ON DUPLICATE KEY UPDATE nom=?, coefficient=?, module_id=?, promotion_id=?, enseignant_id=?";
        try (PreparedStatement ps = connexion.prepareStatement(sql)) {
            ps.setInt(1, sm.getId());
            ps.setString(2, sm.getNom());
            ps.setDouble(3, sm.getCoefficient());
            ps.setInt(4, sm.getModuleParent().getId());
            ps.setInt(5, sm.getPromotion().getId());
            ps.setInt(6, sm.getEnseignantAssigne() != null ? sm.getEnseignantAssigne().getId() : null);
            ps.setString(7, sm.getNom());
            ps.setDouble(8, sm.getCoefficient());
            ps.setInt(9, sm.getModuleParent().getId());
            ps.setInt(10, sm.getPromotion().getId());
            ps.setInt(11, sm.getEnseignantAssigne() != null ? sm.getEnseignantAssigne().getId() : null);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void deleteSousModule(int id) {
        try (PreparedStatement ps = connexion.prepareStatement("DELETE FROM sous_module WHERE id = ?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public List<SousModule> getSousModulesByEnseignant(int enseignantId) {
        List<SousModule> list = new ArrayList<>();
        try (PreparedStatement ps = connexion.prepareStatement("SELECT * FROM sous_module WHERE enseignant_id = ?")) {
            ps.setInt(1, enseignantId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Module m = getModuleById(rs.getInt("module_id"));
                Promotion p = getPromotionById(rs.getInt("promotion_id"));
                SousModule sm = new SousModule(rs.getInt("id"), rs.getString("nom"), 
                        rs.getDouble("coefficient"), m, p);
                Enseignant e = getEnseignantById(enseignantId);
                sm.setEnseignantAssigne(e);
                list.add(sm);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }


    public List<Enseignant> getAllEnseignants() {
    List<Enseignant> list = new ArrayList<>();
    try (Statement stmt = connexion.createStatement();
         ResultSet rs = stmt.executeQuery("SELECT * FROM enseignant")) {
        while (rs.next()) {
            list.add(new Enseignant(rs.getInt("id"), rs.getString("nom")));
        }
    } catch (SQLException e) { e.printStackTrace(); }
    return list;
}

    public Enseignant getEnseignantById(int id) {
        try (PreparedStatement ps = connexion.prepareStatement("SELECT * FROM enseignant WHERE id = ?")) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Enseignant(rs.getInt("id"), rs.getString("nom"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public List<Note> getNotesByEtudiant(int etudiantId) {
        List<Note> list = new ArrayList<>();
        try (PreparedStatement ps = connexion.prepareStatement("SELECT * FROM note WHERE etudiant_id = ?")) {
            ps.setInt(1, etudiantId);
            ResultSet rs = ps.executeQuery();
            Etudiant e = getEtudiantById(etudiantId);
            while (rs.next()) {
                SousModule sm = getSousModuleById(rs.getInt("sous_module_id"));
                list.add(new Note(e, sm, rs.getDouble("valeur"), rs.getDouble("valeur_rattrapage")));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public void saveNote(Note n) {
        String sql = "INSERT INTO note (etudiant_id, sous_module_id, valeur, valeur_rattrapage) VALUES (?,?,?,?) ON DUPLICATE KEY UPDATE valeur=?, valeur_rattrapage=?";
        try (PreparedStatement ps = connexion.prepareStatement(sql)) {
            ps.setInt(1, n.getEtudiant().getId());
            ps.setInt(2, n.getSousModule().getId());
            ps.setDouble(3, n.getValeur());
            ps.setDouble(4, n.getValeurRattrapage());
            ps.setDouble(5, n.getValeur());
            ps.setDouble(6, n.getValeurRattrapage());
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public double calculerMoyenneGenerale(int etudiantId) {
        List<Note> notes = getNotesByEtudiant(etudiantId);
        if (notes.isEmpty()) return -1;
        double somme = 0, totalCoef = 0;
        for (Note n : notes) {
            double noteDef = n.getNoteDefinitive();
            if (noteDef >= 0) {
                somme += noteDef * n.getSousModule().getCoefficient();
                totalCoef += n.getSousModule().getCoefficient();
            }
        }
        return totalCoef == 0 ? -1 : somme / totalCoef;
    }

    public double calculerMoyenneParModule(int etudiantId, int moduleId) {
        List<Note> notes = getNotesByEtudiant(etudiantId);
        double somme = 0, totalCoef = 0;
        for (Note n : notes) {
            if (n.getSousModule().getModuleParent().getId() == moduleId) {
                double noteDef = n.getNoteDefinitive();
                if (noteDef >= 0) {
                    somme += noteDef * n.getSousModule().getCoefficient();
                    totalCoef += n.getSousModule().getCoefficient();
                }
            }
        }
        return totalCoef == 0 ? -1 : somme / totalCoef;
    }

    //  METHODES VALIDATION 
    public void marquerValidation(int etudiantId, int promotionId, boolean valide) {
        String sql = "INSERT INTO validation_promotion (etudiant_id, promotion_id, valide) VALUES (?,?,?) ON DUPLICATE KEY UPDATE valide=?";
        try (PreparedStatement ps = connexion.prepareStatement(sql)) {
            ps.setInt(1, etudiantId);
            ps.setInt(2, promotionId);
            ps.setBoolean(3, valide);
            ps.setBoolean(4, valide);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public boolean isValidationExistante(int etudiantId, int promotionId) {
        try (PreparedStatement ps = connexion.prepareStatement("SELECT valide FROM validation_promotion WHERE etudiant_id = ? AND promotion_id = ?")) {
            ps.setInt(1, etudiantId);
            ps.setInt(2, promotionId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getBoolean("valide");
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }
}