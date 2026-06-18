
public class Utilisateur {
    private int id;
    private String nom;
    private String motDePasse;
    private String role;  // "PLANNING", "ENSEIGNANT", "ETUDIANT", "RESPONSABLE_FILIERE"
    private String filiere;

    public Utilisateur(int id, String nom, String motDePasse, String role, String filiere) {
        this.id = id;
        this.nom = nom;
        this.motDePasse = motDePasse;
        this.role = role;
        this.filiere = filiere;
    }

    public int getId() { return id; }
    public String getNom() { return nom; }
    public String getMotDePasse() { return motDePasse; }
    public String getRole() { return role; }
    public String getFiliere() { return filiere; }
    
    public void setId(int id) { this.id = id; }
    public void setNom(String nom) { this.nom = nom; }
    public void setMotDePasse(String motDePasse) { this.motDePasse = motDePasse; }
    public void setRole(String role) { this.role = role; }
    public void setFiliere(String filiere) { this.filiere = filiere; }
    
    @Override
    public String toString() { return nom + " (" + role + ")"; }
}