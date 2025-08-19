
package sae.java.controleur;

import sae.java.exceptions.BaseException;
import static sae.java.exceptions.CodeErreur.*;

/**
 * Permet le formatage de données pour résoudre des questions d'affichages <br><br>
 * 
 * Toutes les fonctions étant statiques, il est inutile d'instancier cette classe. <br>
 * Il suffit de l'importer dans le fichier voulu pour pouvoir réutiliser les fonctions.<br> <br>
 * 
 * Comment importer cette classe ? <br>
 * <code>
 * import sae.java.donnees.principal.Format;
 * </code>
 * @author Mathieu Corne
 */
public class Format {
    
    /**
    * Vérifie qu'un <code>nombre</code> est compris dans la <code>base</code> <br>
    * Particulièrement utile lorsque l'on traite des données en degrés (base 360/180 si orientation), en minutes ou secondes (base 60) ou en heure (base 24)
    */
    public static boolean checkBase(int nombre, int base) {
        return 0 <= nombre && nombre < base;
    }
    
    /**
    * Remplit de 0 les chiffres qui manque pour un nombre d'une <code>base</code> donnée <br>
    * Utile pour des questions d'affichage d'horaires
    */
    public static String fillZeros(int nombre, int base) throws BaseException {
        // Vérifie que le nombre soit bien de la base correspondante, sinon lève une erreur
        if (checkBase(nombre,base) == false) {
            throw new BaseException(BaseException);
        }
        
        // Ajoute les zéros en s'adaptant au format du nombre
        String nombreFill = String.valueOf(nombre);
        for (int i = nombre, j = base; i < 10 && j > 10; i = i*10, j = j%10) {
            nombreFill = "0"+nombreFill;
        }
        return nombreFill;
    }
    
    /**
    * Ajoute des parenthèses aux nombres négatifs <br>
    * Utile pour des questions d'affichage de coordonnées
    */
    public static String parenthèsesSiNégatif(double nombre) {
        String result;
        if (nombre >= 0) {
            result = String.valueOf(nombre);
        } else {
            result = "("+String.valueOf(nombre)+")";
        }
        return result;
    }
}
