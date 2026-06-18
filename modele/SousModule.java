


public class SousModule {
    private int id;
    private String nom;
    private double coefficient;
    private Module moduleParent;
    private Promotion promotion;
    private Enseignant enseignantAssigne;

    public SousModule(int id, String nom, double coefficient, Module moduleParent, Promotion promotion) {
        this.id = id;
        this.nom = nom;
        this.coefficient = coefficient;
        this.moduleParent = moduleParent;
        this.promotion = promotion;
        if (moduleParent != null) {
            moduleParent.ajouterSousModule(this);
        }
    }

    public int getId() { return id; }
    public String getNom() { return nom; }
    public double getCoefficient() { return coefficient; }
    public Module getModuleParent() { return moduleParent; }
    public Promotion getPromotion() { return promotion; }
    public Enseignant getEnseignantAssigne() { return enseignantAssigne; }
    
    public void setId(int id) { this.id = id; }
    public void setNom(String nom) { this.nom = nom; }
    public void setCoefficient(double coefficient) { this.coefficient = coefficient; }
    public void setModuleParent(Module moduleParent) { this.moduleParent = moduleParent; }
    public void setPromotion(Promotion promotion) { this.promotion = promotion; }
    public void setEnseignantAssigne(Enseignant enseignantAssigne) { this.enseignantAssigne = enseignantAssigne; }
    
    @Override
    public String toString() { return nom + " (coef " + coefficient + ")"; }
}