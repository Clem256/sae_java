
package sae.java.modele;

import sae.java.exceptions.BaseException;
import static sae.java.exceptions.CodeErreur.*;
import static sae.java.controleur.Format.*;


/**
 *
 * @author Mathieu
 */
public class Horaire {
    private int heure;
    private int minute;
    
    public Horaire(int heure, int minute) {
        try {
            this.setHeure(heure);
            this.setMinute(minute);
        } catch (BaseException e) {
            System.out.println(e);
        }
    }
    
    private void setHeure(int heure) throws BaseException {
        if (checkBase(minute, 24)) {
            this.heure = heure;
        } else {
            throw new BaseException(HeureInvalidException);
        }
    }

    private void setMinute(int minute) throws BaseException {
        if (checkBase(minute, 60)) {
            this.minute = minute;
        } else {
            throw new BaseException(MinuteInvalidException);
        }
    }
    
    public int getHeure() {
        return heure;
    }

    public int getMinute() {
        return minute;
    }
    
    // Variables calculées
    
    public int getMinutes() {
        return heure*60 + minute;
    }
    
    public Horaire getHoraire(int duree) {
        int heureH = heure + (minute+duree)/60;
        int minuteH = (minute+duree)%60;
        return new Horaire(heureH,minuteH);
    }
    
    @Override
    public String toString() {
        String resultString;
        try {
            resultString = fillZeros(this.heure, 24)+":"+fillZeros(this.minute, 60);
        } catch (BaseException e) {
            resultString = e.getMessage();
        }
        return resultString;
    }
}
