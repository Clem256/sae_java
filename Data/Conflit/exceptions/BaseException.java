
package sae.java.exceptions;

import java.io.IOException;
import static sae.java.exceptions.CodeErreur.*;

/**
 *
 * @author Mathieu Corne
 */
public class BaseException extends IOException {
    
    public BaseException(CodeErreur code) {
        super(code+" - Nombre n'etant pas de base "+getBase(code));
    }
    
    public static String getBase(CodeErreur code) {
        double base;
        switch (code) {
            case MinuteArcInvalidException:
            case MinuteInvalidException:
            case SecondeArcInvalidException:
                base = 60;
                break;
            case HeureInvalidException:
                base = 24;
                break;
            case DegreInvalidException:
                base = 180;
                break;
            default:
                base = Double.NaN;
                break;
        }
        return String.valueOf(base);
    }
}
