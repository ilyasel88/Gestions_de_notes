
public class Note {
    private Etudiant etudiant;
    private SousModule sousModule;
    private double valeur;
    private double valeurRattrapage; // -1 means no rattrapage

    public Note(Etudiant etudiant, SousModule sousModule, double valeur) {
        this(etudiant, sousModule, valeur, -1);
    }
    
    public Note(Etudiant etudiant, SousModule sousModule, double valeur, double valeurRattrapage) {
        this.etudiant = etudiant;
        this.sousModule = sousModule;
        this.valeur = valeur;
        this.valeurRattrapage = valeurRattrapage;
    }

    public Etudiant getEtudiant() { return etudiant; }
    public SousModule getSousModule() { return sousModule; }
    public double getValeur() { return valeur; }
    public double getValeurRattrapage() { return valeurRattrapage; }
    public double getNoteDefinitive() { return Math.max(valeur, valeurRattrapage); }
    
    public void setEtudiant(Etudiant etudiant) { this.etudiant = etudiant; }
    public void setSousModule(SousModule sousModule) { this.sousModule = sousModule; }
    public void setValeur(double valeur) { this.valeur = valeur; }
    public void setValeurRattrapage(double valeurRattrapage) { this.valeurRattrapage = valeurRattrapage; }
}