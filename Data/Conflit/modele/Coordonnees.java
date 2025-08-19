package sae.java.modele;

import static sae.java.controleur.Format.checkBase;
import static sae.java.exceptions.CodeErreur.*;
import sae.java.exceptions.BaseException;
/**
 *
 * @author Mathieu Corne
 */
public class Coordonnees {
    
    private int degre;
    private int minute;
    private int seconde;
    private char orientation;
    
    /**
     * Instancie des coordonnees DMS avec orientation
     * @param degre Degré
     * @param minute Minute d'arc
     * @param seconde Seconde d'arc
     * @param orientation Orientation
     */
    public Coordonnees(int degre, int minute, int seconde, char orientation) {
        this.degre = degre;
        this.minute = minute;
        this.seconde = seconde;
        this.orientation = orientation;
    }

    /**
     * Définit des degrés de manière sécurisé
     * @param degre Nombre de degrés qui sert à définir l'attribut <code>degre</code>
     * @throws DegreInvalidException Lève une exception indiquant que la variable <code>degre</code> ne respecte pas le domaine de définition de l'attribut <code>degre</code>
     */
    public void setDegre(int degre) throws BaseException {
        if (checkBase(degre, 180)) {
            this.degre = degre;
        } else {
            throw new BaseException(DegreInvalidException);
        }
    }
    
    
    /**
    * Définit des minutes d'arc de manière sécurisé
    * @param minute Nombre de minutes d'arc qui sert à définir l'attribut <code>minute</code>
    * @throws MinuteInvalidException Lève une exception indiquant que la variable <code>minuteString</code> ne respecte pas le domaine de définition de l'attribut <code>minute</code>
    */
    public void setMinute(int minute) throws BaseException {
        if (checkBase(minute, 60)) {
            this.minute = minute;
        } else {
            throw new BaseException(MinuteArcInvalidException);
        }
    }
    
    /**
    * Définit des secondes d'arc de manière sécurisé
    * @param seconde Nombre de secondes d'arc qui sert à définir l'attribut <code>seconde</code>
    * @throws SecondeInvalidException Lève une exception indiquant que la variable <code>secondeString</code> ne respecte pas le domaine de définition de l'attribut <code>seconde</code>
    */
    public void setSeconde(int seconde) throws BaseException {
        if (checkBase(seconde, 60)) {
            this.seconde = seconde;
        } else {
            throw new BaseException(SecondeArcInvalidException);
        }
    }
    
    public void setOrientation(char orientation) {
        this.orientation = orientation;
    }

    public int getDegre() {
        return degre;
    }

    public int getMinute() {
        return minute;
    }

    public int getSeconde() {
        return seconde;
    }
    
    public char getOrientation() {
        return orientation;
    }
    
    public double getCoeffOrientation() {
        int orientationINT;
        if (this.orientation == 'N' || this.orientation == 'E') {
            orientationINT = 1;
        } else {
            orientationINT = -1;
        }
        return orientationINT;
    }
    
    
    public double getCoordonneesDMStoDD() {
        return this.getCoeffOrientation()*((double) degre+ (double) minute/60+ (double) seconde/3600);
    }
    
    public double getCoordonneesDD() {
        double result;
        
        if (orientation == 'N' || orientation == 'S') {
            result = (double) Math.round(this.getCoordonneesDMStoDD()*10000000)/10000000;
        } else {
            result = (double) Math.round(this.getCoordonneesDMStoDD()*100000000)/100000000;
        }
        return result;
    }
    
    public double getRadians() {
        return this.getCoordonneesDD() * Math.PI/180;
    }
    
    @Override
    public String toString() {
        return this.degre+" "+this.minute+"'"+this.seconde+"\" "+this.orientation;
    }
}
