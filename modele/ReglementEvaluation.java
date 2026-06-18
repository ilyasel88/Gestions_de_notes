

import java.util.*;

public class ReglementEvaluation {
    
    // Récupérer les sous-modules depuis la base de données
    private static List<SousModule> getSousModules(Module module, StockageMySQL db) {
        List<SousModule> result = new ArrayList<>();
        for (SousModule sm : db.getAllSousModules()) {
            if (sm.getModuleParent().getId() == module.getId()) {
                result.add(sm);
            }
        }
        return result;
    }
    
    // Validation d'un module
    public static boolean estModuleValide(Module module, Etudiant etudiant, StockageMySQL db) {
        double moyenneModule = calculerMoyenneModule(module, etudiant, db);
        if (moyenneModule < 11) return false;
        
        for (SousModule sm : getSousModules(module, db)) {
            Note note = trouverNote(etudiant, sm, db);
            if (note != null && note.getNoteDefinitive() < 6) return false;
            if (note == null) return false;
        }
        return true;
    }
    
    // Calcul moyenne module
    public static double calculerMoyenneModule(Module module, Etudiant etudiant, StockageMySQL db) {
        double sommeNotes = 0;
        double sommeCoef = 0;
        for (SousModule sm : getSousModules(module, db)) {
            double note = calculerMoyenneSousModule(sm, etudiant, db);
            if (note >= 0) {
                sommeNotes += note * sm.getCoefficient();
                sommeCoef += sm.getCoefficient();
            }
        }
        return sommeCoef > 0 ? sommeNotes / sommeCoef : -1;
    }
    
    // Calcul moyenne sous-module
    public static double calculerMoyenneSousModule(SousModule sm, Etudiant etudiant, StockageMySQL db) {
        Note note = trouverNote(etudiant, sm, db);
        if (note == null) return -1;
        return note.getNoteDefinitive();
    }
    
    // Calcul moyenne générale
    public static double calculerMoyenneGenerale(List<Module> modules, Etudiant etudiant, StockageMySQL db) {
        double sommeNotes = 0;
        int nbModules = 0;
        for (Module m : modules) {
            double moyModule = calculerMoyenneModule(m, etudiant, db);
            if (moyModule >= 0) {
                sommeNotes += moyModule;
                nbModules++;
            }
        }
        return nbModules > 0 ? sommeNotes / nbModules : -1;
    }
    
    // Validation année
    public static ResultatValidation validerAnnee(Etudiant etudiant, List<Module> modules, StockageMySQL db) {
        double moyenneGenerale = calculerMoyenneGenerale(modules, etudiant, db);
        int modulesNonValides = 0;
        boolean noteModuleInferieure6 = false;
        
        for (Module m : modules) {
            if (!estModuleValide(m, etudiant, db)) {
                modulesNonValides++;
            }
            double moyModule = calculerMoyenneModule(m, etudiant, db);
            if (moyModule < 6 && moyModule >= 0) {
                noteModuleInferieure6 = true;
            }
        }
        
        boolean valide = (moyenneGenerale >= 11) && (modulesNonValides <= 2) && !noteModuleInferieure6;
        
        return new ResultatValidation(valide, moyenneGenerale, modulesNonValides, noteModuleInferieure6);
    }
    
    // Trouver note
    private static Note trouverNote(Etudiant etudiant, SousModule sm, StockageMySQL db) {
        for (Note n : db.getNotesByEtudiant(etudiant.getId())) {
            if (n.getSousModule().getId() == sm.getId()) {
                return n;
            }
        }
        return null;
    }
    
    // Classe résultat
    public static class ResultatValidation {
        public boolean valide;
        public double moyenneGenerale;
        public int modulesNonValides;
        public boolean noteModuleInferieure6;
        
        public ResultatValidation(boolean valide, double moyenneGenerale, int modulesNonValides, boolean noteModuleInferieure6) {
            this.valide = valide;
            this.moyenneGenerale = moyenneGenerale;
            this.modulesNonValides = modulesNonValides;
            this.noteModuleInferieure6 = noteModuleInferieure6;
        }
    }
}