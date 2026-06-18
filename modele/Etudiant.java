

public class Etudiant {
    private int id;
    private String prenom;
    private String nom;
    private String numEtudiant;
    private boolean archive;
    private Promotion promotion;
    private boolean aConsommeAnneeReserve = false;
    private String dateNaissance;    
    private String telephone;        
    private String email;

    public Etudiant(int id, String prenom, String nom, String numEtudiant) {
        this.id = id;
        this.prenom = prenom;
        this.nom = nom;
        this.numEtudiant = numEtudiant;
        this.archive = false;
        this.dateNaissance = "";
        this.telephone = "";
        this.email = "";
    }

    public int getId() { return id; }
    public String getPrenom() { return prenom; }
    public String getNom() { return nom; }
    public String getNumEtudiant() { return numEtudiant; }
    public boolean isArchive() { return archive; }
    public Promotion getPromotion() { return promotion; }
    public String getDateNaissance() { return dateNaissance; }
    public String getTelephone() { return telephone; }
    public String getEmail() { return email; }

    public void setId(int id) { this.id = id; }
    public void setPrenom(String prenom) { this.prenom = prenom; }
    public void setNom(String nom) { this.nom = nom; }
    public void setNumEtudiant(String numEtudiant) { this.numEtudiant = numEtudiant; }
    public void setArchive(boolean archive) { this.archive = archive; }
    public void setPromotion(Promotion promotion) { this.promotion = promotion; }
    public void setDateNaissance(String dateNaissance) { this.dateNaissance = dateNaissance; }
    public void setTelephone(String telephone) { this.telephone = telephone; }
    public void setEmail(String email) { this.email = email; }

    public String getNomComplet() { return prenom + " " + nom; }
    
    @Override
    public String toString() { return prenom + " " + nom; }
    public boolean aConsommeAnneeReserve() { return aConsommeAnneeReserve; }
    public void setConsommeAnneeReserve(boolean value) { this.aConsommeAnneeReserve = value; }
}