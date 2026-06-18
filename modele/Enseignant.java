
import java.util.ArrayList;
import java.util.List;

public class Enseignant {
    private int id;
    private String nom;
    private List<SousModule> sousModulesAssignes = new ArrayList<>();

    public Enseignant(int id, String nom) {
        this.id = id;
        this.nom = nom;
    }

    public int getId() { return id; }
    public String getNom() { return nom; }
    public List<SousModule> getSousModulesAssignes() { return sousModulesAssignes; }
    public void setSousModulesAssignes(List<SousModule> list) { this.sousModulesAssignes = list; }
    public void setNom(String nom) { this.nom = nom; }
    public void setId(int id) { this.id = id; }
    
    @Override
    public String toString() { return nom; }
}