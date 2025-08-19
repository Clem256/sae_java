/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package sae.java.modele;

import java.util.ArrayList;
import sae.java.exceptions.idCouleurInvalidException;

/**
 *
 * @author Mathieu Corne
 */
public class Sommet {
    Graphe grapheAssocie;
    int couleur = -1;
    
    int valeurAssocie;
    Vol volAssocie;
    
    ArrayList<Sommet> aretes;
    
    public Sommet(Graphe g, Vol volAssocie) {
        this.grapheAssocie = g;
        this.volAssocie = volAssocie;
        this.aretes = new ArrayList<>();
    }
    
    public Sommet(Graphe g, int valeurAssocie) {
        this.grapheAssocie = g;
        this.valeurAssocie = valeurAssocie;
        this.aretes = new ArrayList<>();
    }
    public void setCouleur(int couleur) throws idCouleurInvalidException {
        if (1 <= couleur && couleur < grapheAssocie.getKmax()) {
            this.couleur = couleur;
        } else {
            throw new idCouleurInvalidException();
        }
    }

    public Graphe getGrapheAssocie() {
        return grapheAssocie;
    }

    public int getCouleur() {
        return couleur;
    }
    
    public int getValeurAssocie() {
        return valeurAssocie;
    }
    
    public Vol getVolAssocie() {
        return volAssocie;
    }
    
    public ArrayList<Sommet> getAretes() {
        return aretes;
    }
    
    @Override
    public String toString() {
        String typeAssociee;
        if (volAssocie == null) {
            typeAssociee = String.valueOf(valeurAssocie);
        } else {
            typeAssociee = volAssocie.getCodes();
        }
        return typeAssociee;
//        return volAssocie.getCodes()+" - "+volAssocie.getPointDepart()+" "+volAssocie.getPointArrivee();
    }
}
