
package sae.java.exceptions;

import java.io.IOException;

/**
 *
 * @author Mathieu
 */
public class idCouleurInvalidException extends IOException {
    
    public idCouleurInvalidException() {
        super("idCouleurInvalidException - idCouleur doit être compris entre 1 et kmax");
    }
}

