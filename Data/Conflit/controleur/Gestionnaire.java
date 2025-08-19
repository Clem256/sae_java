package sae.java.controleur;

import java.util.ArrayList;
import sae.java.modele.Graphe;

/**
 *  La classe Gestionnaire permet de gérer l'ensemble des fonctionnalités autour d'un graphe.
 *  Il est possible d'utiliser plusieurs instances gestionnaires pour traiter différents graphes.
 * @author Mathieu Corne
 */
public class Gestionnaire {
    
    private ArrayList<ErreurFichier> logsGrapheINT = null;
    private ArrayList<ErreurFichier> logsAeroports = null;
    private ArrayList<ErreurFichier> logsVols = null;
    private Graphe graphe;
    
    /**
    * Gestionnaire qui lit et charge les données d'un fichier d'aéroports et un fichier de vols et génère un graphe des intersections des vols
     * @param cheminAeroport
     * @param cheminVol
    */
    public Gestionnaire(String cheminAeroport, String cheminVol){
        this.graphe = new Graphe();
        this.logsAeroports = Fichier.LireFichierAeroports(cheminAeroport, this);
        this.logsVols = Fichier.LireFichierVols(cheminVol, this);
        
    }
    
    public Gestionnaire(String cheminGraphe){
        this.graphe = new Graphe();
        this.logsGrapheINT = Fichier.LireFichierGraphe(cheminGraphe, this);
    }

    public ArrayList<ErreurFichier> getLogsAeroports() {
        return logsAeroports;
    }

    public ArrayList<ErreurFichier> getLogsVols() {
        return logsVols;
    }
    
    /**
    * Renvoie le <code>Graphe</code> d'une instance de gestionnaire
     * @return 
    */
    public Graphe getGraphe() {
        return graphe;
    }
}