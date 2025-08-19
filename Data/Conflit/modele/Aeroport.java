
package sae.java.modele;

import java.util.ArrayList;
import sae.java.modele.Coordonnees;

/**
 * Représente un Aéroport ainsi que ces différents attributs (code, lieu, latitude, longitude).
 * @author Mathieu Corne
 */
public class Aeroport {
    private String code;
    private String lieu;
    private Coordonnees latitude;
    private Coordonnees longitude;
    private ArrayList<Vol> volsDepart;
    private ArrayList<Vol> volsArrivee;
    
    /**
     * Instancie un Aéroport rapidement à partir de données bruts (chaines de caractères) provenant d'un fichier d'aéroports <br>
     * @param code Le code à 3 letres représentant un Aéroport
     * @param lieu Le lieu où se situe l'aéroport
     * @param latitude latitude de l'Aéroport (Coordonnées DMS : Degré/Minute/Seconde avec Orientation)
     * @param longitude longitude de l'Aéroport (Coordonnées DMS : Degré/Minute/Seconde avec Orientation)
     */
    public Aeroport(String code, String lieu, Coordonnees latitude, Coordonnees longitude) {
        this.code = code;
        this.lieu = lieu;
        this.latitude = latitude;
        this.longitude = longitude;
        volsDepart = new ArrayList<>();
        volsArrivee = new ArrayList<>();
    }
    
    public void ajouterAuDepartDe(Vol v){
        volsDepart.add(v);
    }
    
    public void ajouterArriveeDe(Vol v){
        volsArrivee.add(v);
    }

    /**
     * Renvoie le <code>code<code> d'une instance d'<code>Aeroport</code>
     * @return Renvoie le <code>code<code> d'une instance d'<code>Aeroport</code>
     */
    public String getCode() {
        return code;
    }
    
    /**
     * Renvoie le <code>lieu<code> d'une instance d'<code>Aeroport</code>
     * @return Renvoie le <code>lieu<code> d'une instance d'<code>Aeroport</code>
     */
    public String getLieu() {
        return lieu;
    }
    
    /**
     * Renvoie la <code>latitude<code> d'une instance d'<code>Aeroport</code>
     * @return Renvoie la <code>latitude<code> d'une instance d'<code>Aeroport</code>
     */
    public Coordonnees getLatitude() {
        return latitude;
    }

    /**
     * Renvoie la <code>longitude<code> d'une instance d'<code>Aeroport</code>
     * @return Renvoie la <code>longitude<code> d'une instance d'<code>Aeroport</code>
     */
    public Coordonnees getLongitude() {
        return longitude;
    }
    
    public ArrayList<Vol> getVolsDepart() {
        return volsDepart;
    }
    
    public ArrayList<Vol> getVolsArrivee() {
        return volsArrivee;
    }
    
    /**
     * Renvoie les informations complètes d'une instance d'<code>Aeroport</code>
     * @return Renvoie les informations complètes d'une instance d'<code>Aeroport</code>
     */
    @Override
    public String toString() {
        return "Aeroport "+this.code+":"+this.lieu+" - "+this.latitude.toString()+" - "+this.longitude.toString();
    }
}
