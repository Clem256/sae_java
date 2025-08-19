
package sae.java.controleur;

import java.util.HashMap;

/**
 *
 * @author Mathieu Corne
 */
public class ErreurFichier {

    int ligne;
    String description;
    
    public ErreurFichier(int ligne, String description) {
        this.ligne = ligne;
        this.description = description;
    }
    
    @Override
    public String toString() {
        String result;
        if (1 <= ligne) {
            result = "Ligne "+ligne+" Invalide - "+description;
        } else {
            result = description;
        }
        return result;
    }
}
