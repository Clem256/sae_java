
package sae.java.exceptions;

import java.io.IOException;

/**
 *
 * @author Mathieu Corne
 */
public class AeroportInvalidException extends IOException {
    
    public AeroportInvalidException() {
        super("AeroportInvalidException - Ce vol contient un aéroport non présent dans la liste fournie des aéroports");
    }
    
}
