
package sae.java.modele;

import java.util.ArrayList;
import sae.java.exceptions.kMaxInvalidException;

/**
 *
 * @author Mathieu Corne
 */
public class Graphe {
    
    private int kmax = -1;
    private ArrayList<Aeroport> aeroports;
    private ArrayList<Sommet> sommets;
    private ArrayList<Arete> aretes;
    
    public Graphe() {
        aeroports = new ArrayList<>();
        sommets = new ArrayList<>();
        aretes = new ArrayList<>();
    }
    
    public boolean ajouterAeroport(Aeroport aeroportAjoute){
        boolean success;
        if (aeroportAjoute == null) {
            success = false;
        } else {
            success = true;
            aeroports.add(aeroportAjoute);
        }
        return success;
    }
    
    public boolean ajouterSommet(Sommet sommetAjoute){
        boolean success;
        if (sommetAjoute == null) {
            success = false;
        } else {
            success = true;
            sommets.add(sommetAjoute);
        }
        return success;
    }
    
    public boolean ajouterArete(Arete areteAjoutee){
        boolean success;
        if (areteAjoutee == null) {
            success = false;
        } else {
            success = true;
            aretes.add(areteAjoutee);
        }
        return success;
    }

    public void setKmax(int kmax) throws kMaxInvalidException {
        if (kmax < 0) {
            throw new kMaxInvalidException();
        } else {
            this.kmax = kmax;
        }
    }

    public int getKmax() {
        return kmax;
    }

    public ArrayList<Aeroport> getAeroports() {
        return aeroports;
    }
    
    public ArrayList<Sommet> getSommets() {
        return sommets;
    }
    
    public ArrayList<Arete> getAretes() {
        return aretes;
    }
    
    public int getNbAeroports() {
        return aeroports.size();
    }
    
    public int getNbSommets() {
        return sommets.size();
    }
    
    public int getNbAretes() {
        return aretes.size();
    }
    
    public Aeroport getAeroportAt(int index) {
        return aeroports.get(index);
    }
    
    public Sommet getSommetAt(int index) {
        return sommets.get(index);
    }
    
    public Arete getAreteAt(int index) {
        return aretes.get(index);
    }
    
    public Aeroport getAeroportWith(String code) {
        Aeroport aeroportTrouve = null;
        for(Aeroport aeroportCherche:aeroports) {
            if (code.compareTo(aeroportCherche.getCode())==0) {
                aeroportTrouve = aeroportCherche;
            }
        }
        return aeroportTrouve;
    }
    
    public Sommet getSommetOf(int nombre) {
        Sommet sommetTrouve = null;
        for(Sommet sommetCherche:sommets) {
            if (sommetCherche.getValeurAssocie() == nombre) {
                sommetTrouve = sommetCherche;
            }
        }
        return sommetTrouve;
    }
    
    public boolean possedeSommetOf(int nombre) {
        return getSommetOf(nombre) != null;
    }
    
    
    @Override
    public String toString() {
        return String.valueOf(this.getNbSommets())+" Sommet(s) - "+String.valueOf(this.getNbAretes())+" Arete(s)";
    }
}
