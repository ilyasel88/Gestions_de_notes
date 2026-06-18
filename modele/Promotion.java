
public class Promotion {
    private int id;
    private String nom;
    private String filiere;
    private String anneeAcademique;

    public Promotion(int id, String nom, String filiere, String anneeAcademique) {
        this.id = id;
        this.nom = nom;
        this.filiere = filiere;
        this.anneeAcademique = anneeAcademique;
    }

    public int getId() { return id; }
    public String getNom() { return nom; }
    public String getFiliere() { return filiere; }
    public String getAnneeAcademique() { return anneeAcademique; }
    
    public void setId(int id) { this.id = id; }
    public void setNom(String nom) { this.nom = nom; }
    public void setFiliere(String filiere) { this.filiere = filiere; }
    public void setAnneeAcademique(String anneeAcademique) { this.anneeAcademique = anneeAcademique; }
    
    @Override
    public String toString() { return nom + " (" + filiere + ")"; }
}