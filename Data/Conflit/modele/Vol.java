


package sae.java.modele;

import Map.AffichageVolAe;

/**
 * Représente un Vol. Possède un nom, un Aéroport de départ et un Aéroport d'arrivée ainsi qu'un horaire de départ et une durée.
 * @see Supprimer la variable <code>vitesseAvion</code> et créer une méthode calculerVitesseAvion() qui utilise la durée et l'horaire de départ pour en déduire la vitesse <br>
 * ATTENTION il faut calculer absolument la <code>vitesseAvion</code> pour déterminer l'horaire de chacun des deux vols au point d'intersection lors d'un conflit.
 * @author Mathieu Corne
 */
public class Vol {
    private String nom;
    private Aeroport aeroportDepart;
    private Aeroport aeroportArrivee;
    private Horaire horaireDepart;
    private int duree;
    
    private static double RayonTerre = 6371;
    private static double decimaleX = 6;
    private static double decimaleY = 5;
    private AffichageVolAe ava;
    // Seuil de l'écart de minutes sous lequel deux avions peuvent entrer en collision
    private static int seuilEcartHoraire = 15; //qualité dev
    
    /**
     * Instancie un Vol rapidement à partir de données bruts (chaines de caractères) provenant d'un fichier de vols <br>
     * @param nom
     * @param aeroportDepart
     * @param aeroportArrivee
     * @param horaireDepart Horaire de départ (HH:MM:SS
     * @param duree Durée totale du Vol
     * @param seuilEcartHoraire Seuil de l'écart de minutes sous lequel deux avions peuvent entrer en collision
     */
    public Vol(String nom, Aeroport aeroportDepart, Aeroport aeroportArrivee, Horaire horaireDepart, int duree) {
        this.nom = nom;
        this.aeroportDepart = aeroportDepart;
        this.aeroportArrivee = aeroportArrivee;
        this.horaireDepart = horaireDepart;
        this.duree = duree;
        aeroportDepart.ajouterAuDepartDe(this);
        aeroportArrivee.ajouterArriveeDe(this);
    }
    
    /**
     * Renvoie le nom <code>nom</code> d'une instance <code>Vol</code> 
     * @return Renvoie le nom <code>nom</code> d'une instance <code>Vol</code> 
     */
    public String getNom() {
        return nom;
    }

    /**
     * Renvoie l'aéroport de départ <code>aeroportDepart</code> d'une instance <code>Vol</code> 
     * @return Renvoie l'aéroport de départ <code>aeroportDepart</code> d'une instance <code>Vol</code> 
     */
    public Aeroport getAeroportDepart() {
        return aeroportDepart;
    }
    
    /**
     * Renvoie l'aéroport d'arrivée <code>aeroportArrivee</code> d'une instance <code>Vol</code> 
     * @return Renvoie l'aéroport d'arrivée <code>aeroportArrivee</code> d'une instance <code>Vol</code> 
     */
    public Aeroport getAeroportArrivee() {
        return aeroportArrivee;
    }
    
    /**
     * Renvoie l'horaire de départ <code>horaireDepart</code> d'une instance <code>Vol</code> 
     * @return Renvoie l'horaire de départ <code>horaireDepart</code> d'une instance <code>Vol</code>
     */
    public Horaire getHoraireDepart() {
        return horaireDepart;
    }
    
    /**
     * Renvoie la duree <code>duree</code> d'une instance <code>Vol</code> 
     * @return <code>duree</code> d'une instance <code>Vol</code> 
     */
    public int getDuree() {
        return duree;
    }
    
    /**
     * Renvoie une chaine de caractères représenant un vol à partir des deux codes des Aéroports concaténés  
     * @return Renvoie le seuil de l'écart de sécurité <code>seuilEcartHoraire</code> garantissant l'intégrité des <code>Vols</code>
     */
    public String getCodes() {
        return aeroportDepart.getCode()+"-"+aeroportArrivee.getCode();
    }
    
    /**
     * Renvoie une chaine de caractères représenant un vol à partir des deux codes des Aéroports concaténés  
     * @return Renvoie le seuil de l'écart de sécurité <code>seuilEcartHoraire</code> garantissant l'intégrité des <code>Vols</code>
     */
    public String getHoraires() {
        return getHoraireDepart()+" - "+getHoraireArrivee();
    }
    
    
    
    
    
    
    
    
    
    // Variables calculées
    
    public Horaire getHoraireArrivee() {
        return horaireDepart.getHoraire(duree);
    }
    
    
    public Point getPointDepart() {
        double x = (double) Math.round(RayonTerre * Math.cos(aeroportDepart.getLatitude().getRadians()) * Math.sin(aeroportDepart.getLongitude().getRadians())* Math.pow(10, decimaleX))/Math.pow(10, decimaleX);
        double y = (double) Math.round(RayonTerre * Math.cos(aeroportDepart.getLatitude().getRadians()) * Math.cos(aeroportDepart.getLongitude().getRadians())* Math.pow(10, decimaleY))/Math.pow(10, decimaleY);
        return new Point(x,y);
    }
    
    public Point getPointArrivee() {
        double x = (double) Math.round(RayonTerre * Math.cos(aeroportArrivee.getLatitude().getRadians()) * Math.sin(aeroportArrivee.getLongitude().getRadians())* Math.pow(10, decimaleX))/Math.pow(10, decimaleX);
        double y = (double) Math.round(RayonTerre * Math.cos(aeroportArrivee.getLatitude().getRadians()) * Math.cos(aeroportArrivee.getLongitude().getRadians())* Math.pow(10, decimaleY))/Math.pow(10, decimaleY);
        return new Point(x,y);
    }
    
    // Paramètre m
    public double getCoefficientM() {
        return (getPointArrivee().getY()-getPointDepart().getY())/(getPointArrivee().getX()-getPointDepart().getX()) ;
    }
    
    // Paramètre p
    public double getOrdonneeP() {
        // y = mx + p
        // p = y - mx
        return getPointDepart().getY() - getPointDepart().getX() * this.getCoefficientM();
    }
    
    public double getXMin() {
        double xMin;
        double xA = getPointDepart().getX();
        double xB = getPointArrivee().getX();
        if (xA <= xB) {
            xMin = xA;
        } else {
            xMin = xB;
        }
        return xMin;
    }
    
    public double getXMax() {
        double xMax;
        double xA = getPointDepart().getX();
        double xB = getPointArrivee().getX();
        if (xA > xB) {
            xMax = xA;
        } else {
            xMax = xB;
        }
        return xMax;
    }
    
    public static double getDistanceHorizontale(Point premierPoint, Point secondPoint) {
        return secondPoint.getX() - premierPoint.getX();
    }
    
    public double getVitesseHorizontale() {
        /*
        vitesse = distance / duree
        */
        return getDistanceHorizontale(getPointDepart(), getPointArrivee())/duree;
    }
    
    public double getDistanceHorizontaleTotale() {
        return getDistanceHorizontale(getPointDepart(), getPointArrivee());
    }
    
    
    
    /**
     * Renvoie le Point d'intersection <code>pointInter(xInter, yInter)</code> entre deux vols s'il existe. Sinon renvoie pointInter(NaN,NaN).
     * @param secondVol
     * @return Si existe, <code>pointInter(xInter, yInter)</code> sinon pointInter(NaN,NaN).
     */
    public Point getPointInter(Vol secondVol) {
        double m1 = getCoefficientM();
        double p1 = getOrdonneeP();
        double m2 = secondVol.getCoefficientM();
        double p2 = secondVol.getOrdonneeP();
        // A déterminer
        double xInter;
        double yInter;
        Point pointInter;
        /* 
        Soit volA et volB deux vols (segments) appartenant à dA et dB respectivement 
        Soit fA(x)=m1*x+p1 et f2(x)=m2*x+p2 les équations réduites de dA et db respectivements
        
        Si m1 = m2 alors d1 et d2 sont parallèles
        Sinon elles sont sécantes.
        */
        if (m1 == m2) {
            // Parallèles
            if (p1 == p2) {
                /* Confondues
                Sachant que m1 = m2,
                Si p1 = p2 alors f1(x) = f2(x)
                Ainsi, d1 et d2 confondues
                
                _________________
                
                Soit xA et xB les points de départs respectives des vols A et B
                Soit vitesseA et vitesseB les vitesses respectives des vols A et B
                Soit hDepA et hDep B les horaires en minutes de départ respectives des vols A et B

                Pour déterminer les équations de position des deux avions à l'instant t.
                
                posA(t) = xA + vitesseA * (t)
                posB(t) = xB + vitesseB * (t)
                
                Or cela voudrait dire que les deux avions partent en même temps
                Ici les deux avions peuvent partir à des horaires de départs différentes
                Il faut donc calculer l'écart entre le départ du premier avion et le second avion
                de tel sorte que hDepPremierAvion + ecart = hDepDeuxiemeAvion
                
                Si hDepA > hDepB alors ecart = hDepA-hDepB (t minutes après le départ de A)
                posA(t) = xA + vitesseA * (t)
                posB(t) = xB + vitesseB * (t+ecart)
                
                xB + vitesseB * (tInter+ecart) = xA + tInter * vitesseA
                xB + tInter*vitesseB + ecart*vitesseB = xA + tInter*vitesseA
                tInter*vitesseB - tInter*vitesseA = xA - xB - ecart*vitesseB
                tInter*(vitesseB - vitesseA) = xA - xB - ecart*vitesseB
                tInter = (xA - xB - ecart*vitesseB)/(vitesseB - vitesseA)
                
                Si hDepB > hDepA alors ecart = hDepB-hDepA (t minutes après le départ de B)
                posA(t) = xA + vitesseA * (t+ecart)
                posB(t) = xB + vitesseB * (t)
                           
                xA + vitesseA * (tInter+ecart) = xB + tInter * vitesseB
                xA + tInter*vitesseA + ecart*vitesseA = xB + tInter*vitesseB
                tInter*vitesseA - tInter*vitesseB = xB - xA - ecart*vitesseA
                tInter*(vitesseA-vitesseB) = xB - xA - ecart*vitesseA
                tInter = (xB - xA - ecart*vitesseA)/(vitesseA - vitesseB)
                
                sinon ecart = 0
                posA(t) = xA + vitesseA * (t)
                posB(t) = xB + vitesseB * (t)
                
                xA + tInter * vitesseA  = xB + tInter * vitesseB
                tInter * vitesseA - tInter * vitesseB = xB - xA
                tInter*(vitesseA-vitesseB)=xB - xA
                tInter = (xB - xA)/(vitesseA-vitesseB)
                
                ________________
                tInter correspond à la durée en minutes après le départ du volB
                avant l'intersection avec le second avion. 

                Leur point de collision xInter est celui vérifiant posA(tInter)=posB(tInter).
                
                
                
                On utilise ensuite une des deux formules de position
                xInter = posA(tInter)
                
                yInter = m1*xInter + p1
                
                
                */
                double xA = getPointDepart().getX();
                double xB = secondVol.getPointDepart().getX();
                double vitesseA = getVitesseHorizontale(); // km/minutes
                double vitesseB = secondVol.getVitesseHorizontale();
                double hDepA = getHoraireDepart().getMinutes();
                double hDepB = secondVol.getHoraireDepart().getMinutes();
                
                double ecart;
                double tInter;
                if (hDepA > hDepB){
                    ecart = hDepA-hDepB;
                    tInter = (xB - xA - ecart*vitesseA)/(vitesseA - vitesseB);
                    xInter = xA + vitesseA * tInter;
                } else if (hDepA < hDepB) {
                    ecart = hDepB-hDepA;
                    tInter = (xB - xA - ecart*vitesseA)/(vitesseA - vitesseB);
                    xInter = xA + vitesseA * (tInter+ecart);
                } else {
                    tInter = (xB - xA)/(vitesseA - vitesseB);
                    xInter = xA + vitesseA * (tInter);
                }
//                System.out.println("tINTER            "+tInter);
                
                
                
                
////                System.out.println(""+xA+" "+xB+" "+vitesseA+" "+vitesseB+" "+hDepA+" "+hDepB);
                yInter = m1*xInter + p1;
                pointInter = new Point((double) Math.round(xInter* Math.pow(10, decimaleX))/Math.pow(10, decimaleX), (double) Math.round(yInter* Math.pow(10, decimaleY))/Math.pow(10, decimaleY));
            } else {
                // Pas de point d'intersection
                pointInter = new Point(Double.NaN,Double.NaN);
            }
        } else {
            /* Sécantes
            Sachant que m1 = m2,
            Si p1 = p2 alors f1(x) = f2(x)
            Ainsi, d1 et d2 sécantes


            Trouver le point d'intersection revient à trouver xInter puis à
            déterminer yInter.

            m1*xInter+p1 = m2*xInter+p2
            m1*xInter = m2*xInter+p2-p1
            m1*xInter-m2*xInter = p2-p1
            xInter(m1-m2) = p2-p1
            xInter = (p2-p1)/(m1-m2)
            
            Cependant, bien que le point d'intersection d'abscisse est forcément sur la droite associée à la fonction affine, 
            il peut ne pas être dans l'intervalle [xMin;xMax] des deux vols.
            
            Il faut donc vérifier xMin < xInter < xMax pour les deux vols
            
            On peut maintenant déterminer yInter d'après xInter car
            yInter = m1*xInter+p1 = m2*xInter+p2
            */
            xInter = (p2-p1)/(m1-m2);
            double xMinA = getXMin();
            double xMaxA = getXMax();
            double xMinB = secondVol.getXMin();
            double xMaxB = secondVol.getXMax();
            if (xMinA <= xInter && xInter <= xMaxA && xMinB <= xInter && xInter <= xMaxB) {
                yInter = m1*xInter+p1;
                pointInter = new Point((double) Math.round(xInter* Math.pow(10, decimaleX))/Math.pow(10, decimaleX), (double) Math.round(yInter* Math.pow(10, decimaleY))/Math.pow(10, decimaleY));
            } else {
                pointInter = new Point(Double.NaN,Double.NaN);
            }
            
            
            
            
        }
        return pointInter;
    }
    
    public boolean possedeInter(Vol secondVol) {
        return !(Double.isNaN(getPointInter(secondVol).getX())) || !(Double.isNaN(getPointInter(secondVol).getX()));
    }

    
    
    public double getDistanceInter(Vol secondVol) {
        return getDistanceHorizontale(getPointDepart(), getPointInter(secondVol));
    }
    
    /**
     * Connaissant <code> distanceTotale</code>, <code> distanceInter</code> et <code> dureeTotale</code>,
     * Calcule la durée entre le Point de départ au point d'Intersection l'intersection
     * Si le point d'intersection existe, renvoie la durée, sinon renvoie NaN.
    
     * Démonstration du calcul de la durée :
    <code>
    vitesse = distanceTotale/dureeTotale
    vitesse = distanceInter/dureeInter
    vitesse * dureeInter = distanceInter
    dureeInter = distanceInter/vitesse
    </code>
     * @param secondVol
     * @return Si le point d'intersection existe, renvoie la durée, sinon renvoie NaN.
    */
    public double getDureeInter(Vol secondVol) {
        return getDistanceInter(secondVol)/getVitesseHorizontale();
    }
    
    
    
    public Horaire getHoraireInter(Vol secondVol) {
        Horaire horaireInter;
        if (possedeInter(secondVol)) {
            horaireInter = horaireDepart.getHoraire((int) getDureeInter(secondVol));
        } else {
            horaireInter = null;
        }
        return horaireInter;
    }
    
    public boolean estARisque(Vol secondVol) {
        /*
        nbMinutes(h1) - nbMinutes(h2) = differenceMinutes
        
        - seuil < difference < seuil
        
        */
        ava = new AffichageVolAe();
        seuilEcartHoraire = ava.getMarge_securite();
        boolean aRisque;
        Horaire hInterA = getHoraireInter(secondVol);
        Horaire hInterB = secondVol.getHoraireInter(this);
        if (hInterA == null || hInterB == null) {
            aRisque = false;
        } else {
            int m1 = hInterA.getMinutes();
            int m2 = hInterB.getMinutes();
            int differenceMinutes = m1 - m2;
            aRisque = -seuilEcartHoraire < differenceMinutes && differenceMinutes < seuilEcartHoraire;
        }
        return aRisque;
    }
    
    
    
    @Override
    public String toString() {
        return "Vol "+this.nom+" - "+this.aeroportDepart.getCode()+" > "+this.aeroportArrivee.getCode()+" - "+this.getHoraireDepart();
    }
    
    /**
     * Modifie le seuil de l'écart de sécurité <code>seuilEcartHoraire</code> garantissant l'intégrité des <code>Vols</code>
     * @param seuilEcartHoraire nouvelle valeur du seuil de l'écart de sécurité.
     */
    public static void setSeuilEcartHoraire(int seuilEcartHoraire) {
        Vol.seuilEcartHoraire = seuilEcartHoraire;
       
    }
    
    /**
     * Renvoie le seuil de l'écart de sécurité <code>seuilEcartHoraire</code> garantissant l'intégrité des <code>Vols</code>
     * @return Renvoie le seuil de l'écart de sécurité <code>seuilEcartHoraire</code> garantissant l'intégrité des <code>Vols</code>
     */
    public static int getSeuilEcartHoraire() {
        return seuilEcartHoraire;
    }
    
}
