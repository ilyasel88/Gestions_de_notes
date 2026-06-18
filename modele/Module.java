


import java.util.ArrayList;
import java.util.List;

public class Module {
    private int id;
    private String nom;
    private String description;
    private List<SousModule> sousModules = new ArrayList<>();

    public Module(int id, String nom, String description) {
        this.id = id;
        this.nom = nom;
        this.description = description;
    }

    public int getId() { return id; }
    public String getNom() { return nom; }
    public String getDescription() { return description; }
    public List<SousModule> getSousModules() { return sousModules; }
    
    public void setId(int id) { this.id = id; }
    public void setNom(String nom) { this.nom = nom; }
    public void setDescription(String description) { this.description = description; }
    public void ajouterSousModule(SousModule sm) { sousModules.add(sm); }
    
    @Override
    public String toString() { return nom; }
}