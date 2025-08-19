
package sae.java.exceptions;

import java.io.IOException;

/**
 *
 * @author Mathieu
 */
public class kMaxInvalidException extends IOException {
    
    public kMaxInvalidException() {
        super("kMaxInvalidException - kmax doit être supérieur à 0");
    }
}
