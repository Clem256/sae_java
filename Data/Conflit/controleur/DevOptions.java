
package sae.java.controleur;

import java.util.HashMap;

/**
 * OBLIGATOIREMENT Instanciable (Peut être instancié en classe Anonyme) - Représente l'ensemble des options de développement <br> <br>
 * 
 * Comment importer cette classe ? <br>
 * <code>
 * import sae.java.donnees.principal.Format;
 * </code>
 * @author Mathieu Corne
 */
public class DevOptions {
    
    public static HashMap<String, Boolean> options;
    
    /**
     * Définit les options de développement, notamment les logs, les commentaires et la correcteur de fichier
     */
    public DevOptions() {
        this.options = new HashMap<>();
        options.put("devCommentaires", false);
        options.put("comChargementFichier", false);
        options.put("comColoration", false);
        options.put("detailsChargementFichier", false);
        options.put("detailsAeroports", false);
        options.put("detailsSommets", false);
        options.put("detailsAretes", false);
        options.put("detailsColoration", false);
        options.put("correcteurFichier", false);
        options.put("choixCompagnieLibre", false);
    }
}
