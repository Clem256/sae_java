package sae.java.controleur;

import Map.ListeVol;
import sae.java.controleur.Gestionnaire;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import sae.java.modele.Aeroport;
import sae.java.modele.Arete;
import sae.java.modele.Coordonnees;
import sae.java.modele.Graphe;
import sae.java.modele.Horaire;
import sae.java.modele.Sommet;
import sae.java.modele.Vol;



/**
 * Non instanciable - Permet de lire et charger des données des différents fichiers pour générer des graphes.
 * @author Mathieu Corne
 */
public class Fichier {
     private static String chemin_ae = null;
    private static ArrayList<String> val_vol = new ArrayList<>();
    private static HashMap<Double, Double> coord = new HashMap<>();
    private static ArrayList<String> code = new ArrayList<>();
    private static ArrayList<String> vol = new ArrayList<>();
    private static ListeVol listeVol;
    private double coordonnee;
    /**
    * Permet de lire et charger un fichier d'aeroports <br>
    * Format attendu : "CodeAéroport; LieuAéroport; DegréLatitude; MinuteLatitude; SecondeLatitude; OrientationLatitude; DegréLongitude; MinuteLongitude; SecondeLongitude; OrientationLongitude"
    * @author Mathieu Corne
     * @param chemin
     * @param gestion
    */
    public static ArrayList<ErreurFichier> LireFichierAeroports(String chemin, Gestionnaire gestion){
        
        // Il y a 2 erreurs par défaut (si le chemin est invalide et si le fichier est vide)
        ArrayList<ErreurFichier> logs = new ArrayList<>();
        int noLigne = 0;
        logs.add(new ErreurFichier(noLigne, "Chemin invalide"));
        FileReader monFichier;
        BufferedReader tampon;
        
        try {
            // Ouverture et mise en tampon du fichier
            monFichier = new FileReader(chemin);
            tampon = new BufferedReader(monFichier);
            
            // Chemin valide, on enlève l'erreur en réinitialisant logs
            logs = new ArrayList<>();
            logs.add(new ErreurFichier(noLigne, "Fichier vide"));
            if (DevOptions.options.get("devCommentaires") && DevOptions.options.get("comChargementFichier")) {
                System.out.println("Ouverture du fichier : "+chemin);
            }
            
            Graphe graphe =  gestion.getGraphe();
            
            while (true) {
                // Lecture de la ligne de la prochaine ligne
                String ligne = tampon.readLine();
                noLigne = noLigne + 1;
                // Arrêter la lecture après être arrivé à la fin du fichier
                if (ligne == null) {
                    break;
                }
                logs = new ArrayList<>();
                // Calcule le nombre actuel de logs d'erreurs fichier
                int nbAnciensLogs = logs.size();
                
                // Divise la ligne en colonnes d'après le séparateur ";" puis les ajoute dans un tableau de String
                String[] valeurs = ligne.split(";");
                
                // -- VERIFICATION DU FICHIER 
                
                // Vérifie si le nombre d'arguments est valide (=10)
                if (valeurs.length != 10) {
                    // Nombre d'arguments invalide
                    logs.add(new ErreurFichier(noLigne,"Nombre d'arguments invalides - Requis [9] Actuel["+String.valueOf(valeurs.length)+"]"));
                } else {
                    // Nombre d'arguments valide
                    
                    // Vérifie si les types de valeur sont corrects;
                    for (int i = 2; i < 9; i++) {
                        if ((2 <= i && i <= 4) || (6 <= i && i <= 8)) {
                            try { 
                                int testInt = Integer.parseInt(valeurs[i]);
                            } catch(NumberFormatException e) {
                                logs.add(new ErreurFichier(noLigne,"Argument ["+String.valueOf(i)+"] = "+valeurs[i]+" - Non convertissable en entier"));
                            }
                        } else {
                            if (valeurs[i].length() > 1) {
                                logs.add(new ErreurFichier(noLigne,"Argument ["+String.valueOf(i)+"] = "+valeurs[i]+" - Seul un caractere doit etre renseigne"));
                            }
                            
                            
                            // On vérifie si l'orientation des Coordonnees est valide en fonction de la latitude et la longitude
                            try {
                                char testChar = valeurs[i].charAt(0);
                                if ((i == 5 && (testChar != 'N') && !(testChar != 'S'))) {
                                    logs.add(new ErreurFichier(noLigne,"Argument ["+String.valueOf(i)+"] = "+valeurs[i]+" - Le caractere ne correspond pas a une orientation de latitude"));
                                }
                                if ((i == 9 && (testChar != 'E') && !(testChar != 'O'))) {
                                    logs.add(new ErreurFichier(noLigne,"Argument ["+String.valueOf(i)+"] = "+valeurs[i]+" - Le caractere ne correspond pas a une orientation de longitude"));
                                }
                            } catch (IndexOutOfBoundsException e) {
                                logs.add(new ErreurFichier(noLigne,"Argument ["+String.valueOf(i)+"] = "+valeurs[i]+" - Non convertissable en caractere"));
                            }
                        }
                    }
                    
                }
                // Ajout des données des colonnes au Gestionnaire
                Aeroport aeroportAjoute;
                if (nbAnciensLogs == logs.size()) {
                    aeroportAjoute = new Aeroport(valeurs[0], valeurs[1], new Coordonnees(Integer.parseInt(valeurs[2]), Integer.parseInt(valeurs[3]), Integer.parseInt(valeurs[4]), valeurs[5].charAt(0)), new Coordonnees(Integer.parseInt(valeurs[6]), Integer.parseInt(valeurs[7]), Integer.parseInt(valeurs[8]), valeurs[9].charAt(0)));
                    if (DevOptions.options.get("devCommentaires") && DevOptions.options.get("detailsChargementFichier") && DevOptions.options.get("detailsAeroports")) {
                        System.out.println("Aeroport ajoute - "+aeroportAjoute);
                    }
                } else {
                    aeroportAjoute = null;
                }
                graphe.ajouterAeroport(aeroportAjoute);
            }
            
            if (logs.isEmpty() && DevOptions.options.get("devCommentaires") && DevOptions.options.get("comChargementFichier")) {
                System.out.println("Lecture et chargement des Aeroports Termines");
            }
            
            // Fermeture du fichier
            tampon.close();
            monFichier.close();
                    
        } catch (IOException e) {
        }
        if (logs.isEmpty()) {
            logs = null;
        }
        return logs;
    }
    
    /**
    * Permet de lire et charger un fichier de Vols <br>
    * Format attendu : "NomVol; CodeAéroportDépart; CodeAéroportArrivée; HeureDépart; MinuteDépart; Durée"
    * @author Mathieu Corne
    */
    public static ArrayList<ErreurFichier> LireFichierVols(String chemin, Gestionnaire gestion){
        
        // Il y a 2 erreurs par défaut (si le chemin est invalide et si le fichier est vide)
        ArrayList<ErreurFichier> logs = new ArrayList<>();
        int noLigne = 0;
        logs.add(new ErreurFichier(noLigne, "Chemin invalide"));
        FileReader monFichier;
        BufferedReader tampon;
        
        try {
            // Ouverture et mise en tampon du fichier
            monFichier = new FileReader(chemin);
            tampon = new BufferedReader(monFichier);
            
            // Chemin valide, on enlève l'erreur en réinitialisant logs
            logs = new ArrayList<>();
            logs.add(new ErreurFichier(noLigne, "Fichier vide"));
            if (DevOptions.options.get("devCommentaires") && DevOptions.options.get("comChargementFichier")) {
                System.out.println("Ouverture du fichier : "+chemin);
            }
            
            Graphe graphe =  gestion.getGraphe();
            
            while (true) {
                // Lecture de la ligne de la prochaine ligne
                String ligne = tampon.readLine();
                noLigne = noLigne + 1;
                // Arrêter la lecture après être arrivé à la fin du fichier
                if (ligne == null) {
                    break;
                }
                logs = new ArrayList<>();
                // Calcule le nombre actuel de logs d'erreurs fichier
                int nbAnciensLogs = logs.size();
                
                // Divise la ligne en colonnes d'après le séparateur ";" puis les ajoute dans un tableau de String
                String[] valeurs = ligne.split(";");
                
                // -- VERIFICATION DU FICHIER 
                
                // Vérifie si le nombre d'arguments est valide (=6)
                if (valeurs.length != 6) {
                    // Nombre d'arguments invalide
                    logs.add(new ErreurFichier(noLigne,"Nombre d'arguments invalides - Requis [9] Actuel["+String.valueOf(valeurs.length)+"]"));
                } else {
                    // Nombre d'arguments valide
                    
                    // Vérifie si les types de valeur sont corrects;
                    for (int i = 0; i < 6; i++) {
                        if ((3 <= i && i <= 5)) {
                            try { 
                                int testInt = Integer.parseInt(valeurs[i]);
                            } catch(NumberFormatException e) {
                                logs.add(new ErreurFichier(noLigne,"Argument ["+String.valueOf(i)+"] = "+valeurs[i]+" - Non convertissable en entier"));
                            }
                        } else {
                            if (i == 0) {
                                // On vérifie la partie Compagnie de l'identifiant de Vol en fonction de l'activation du mode ChoixCompagnieLibre
                                if (DevOptions.options.get("choixCompagnieLibre")) {
                                    int indexFinCodeCompagnie = -1;
                                    while (true){
                                        try {
                                            valeurs[i].charAt(indexFinCodeCompagnie + 1);
                                            indexFinCodeCompagnie = indexFinCodeCompagnie + 1;
                                        } catch (IndexOutOfBoundsException e) {
                                            break;
                                        }
                                    }
                                    if (indexFinCodeCompagnie == -1) {
                                        logs.add(new ErreurFichier(noLigne,"Argument ["+String.valueOf(i)+"] = "+valeurs[i]+" - Identifiant de Vol Invalide (Format = COMPAGNIE000..) - COMPAGNIE doit etre en lettres majuscules"));
                                    }
                                } else {
                                    // On vérifie que l'identifiant de vol commence par 'AF'
                                    if ((valeurs[i].substring(0, 2).compareTo("AF"))!= 0) {
                                        logs.add(new ErreurFichier(noLigne,"Argument ["+String.valueOf(i)+"] = "+valeurs[i]+" - Doit commencer par AF (seule la compagnie Air France est autorisée)"));
                                    }
                                }

                                // On vérifie que la partie de l'identifiant après la compagnie est uniquement composé de nombres
                                try {
                                    int testInt = Integer.parseInt(valeurs[i].substring(2));
                                } catch(NumberFormatException e) {
                                    logs.add(new ErreurFichier(noLigne,"Argument ["+String.valueOf(i)+"][2:"+String.valueOf(valeurs[i].length()-1)+"] = "+valeurs[i].substring(2).charAt(i)+" - Identifiant de Vol Invalide (Format = COMPAGNIE000..) - COMPAGNIE doit etre suivi du suite de chiffre"));
                               }
                            } else {
                                if (valeurs[i].length() != 3) {
                                    logs.add(new ErreurFichier(noLigne,"Argument ["+String.valueOf(i)+"] = "+valeurs[i]+" - Doit contenir 3 caracteres"));
                                } else {
                                    if(graphe.getAeroportWith(valeurs[i]) == null) {
                                        logs.add(new ErreurFichier(noLigne,"Argument ["+String.valueOf(i)+"] = "+valeurs[i]+" - Ne correspond pas a un Aeroport enregistre"));
                                    }
                                }
                            }
                            
                        }
                    }
                    
                }
                // Ajout des données des colonnes au Gestionnaire
                Sommet sommetAjoute;
                if (nbAnciensLogs == logs.size()) {
                    Vol vol = new Vol(valeurs[0], graphe.getAeroportWith(valeurs[1]), graphe.getAeroportWith(valeurs[2]), new Horaire(Integer.parseInt(valeurs[3]), Integer.parseInt(valeurs[4])), Integer.parseInt(valeurs[5]));
                    sommetAjoute = new Sommet(graphe, vol);
                    if (DevOptions.options.get("devCommentaires") && DevOptions.options.get("detailsChargementFichier") && DevOptions.options.get("detailsSommets")) {
                        System.out.println("Sommet ajoute - "+sommetAjoute);
                    }
                } else {
                    sommetAjoute = null;
                }
                graphe.ajouterSommet(sommetAjoute);
            }
            
            if (logs.isEmpty() && DevOptions.options.get("devCommentaires") && DevOptions.options.get("comChargementFichier")) {
                System.out.println("Lecture et chargement des Sommets Termines");
            }
            
            // On détermine les arcs entre les sommets
            for (int i = 0; i < graphe.getNbSommets(); i++) {
                Sommet s1 = graphe.getSommetAt(i);
                Vol volA = s1.getVolAssocie();
                for (int j = i+1; j < graphe.getNbSommets(); j++) {
                    Sommet s2 = graphe.getSommetAt(j);
                    Vol volB = s2.getVolAssocie();
                    if (volA.possedeInter(volB) && volA.estARisque(volB)) {
                        Arete areteAjoute = new Arete(s1,s2);
                        graphe.ajouterArete(areteAjoute);
                        if (DevOptions.options.get("devCommentaires") && DevOptions.options.get("detailsChargementFichier") && DevOptions.options.get("detailsAretes")) {
                            System.out.println("Arete ajoute - "+areteAjoute);
                        }
                    }
                }
            }
            
            if (logs.isEmpty() && DevOptions.options.get("devCommentaires") && DevOptions.options.get("comChargementFichier")) {
                System.out.println("Lecture et chargement des Aretes Termines");
            }
            
            // Fermeture du fichier
            tampon.close();
            monFichier.close();
                    
        } catch (IOException e) {
        }
        if (logs.isEmpty()) {
            logs = null;
        }
        return logs;
    }
    
    /**
    * Permet de lire et charger un fichier de Graphe préconstruit <br>
    * Première ligne : kmax; <br>
    * Deuxième ligne : nbSommets; <br>
    * Format attendu : "IdSommet1 | IdSommet2"
    * @author Mathieu Corne
     * @param chemin
     * @param gestion
    */
    public static ArrayList<ErreurFichier> LireFichierGraphe(String chemin, Gestionnaire gestion){
        // Il y a 2 erreurs par défaut (si le chemin est invalide et si le fichier est vide)
        ArrayList<ErreurFichier> logs = new ArrayList<>();
        int noLigne = 0;
        logs.add(new ErreurFichier(noLigne, "Chemin invalide"));
        FileReader monFichier;
        BufferedReader tampon;
        
        try {
            // Ouverture et mise en tampon du fichier
            monFichier = new FileReader(chemin);
            tampon = new BufferedReader(monFichier);
            
            // Chemin valide, on enlève l'erreur en réinitialisant logs
            logs = new ArrayList<>();
            if (DevOptions.options.get("devCommentaires") && DevOptions.options.get("comChargementFichier")) {
                System.out.println("Ouverture du fichier : "+chemin);
            }
            
            Graphe graphe =  gestion.getGraphe();
            
            String ligne = tampon.readLine(); // kmax
            if (ligne == null) {
                logs.add(new ErreurFichier(noLigne, "Fichier vide"));
            }
            graphe.setKmax(Integer.parseInt(ligne));
            noLigne = noLigne+1;
            // On vérifie que kMax est un entier
            try {
                Integer.parseInt(ligne);
            } catch(NumberFormatException e) {
                logs.add(new ErreurFichier(noLigne," - Valeur pour kMax Invalide - Non convertissable en entier"));
            }
            ligne = tampon.readLine(); // nbSommets
            noLigne = noLigne+1;
            try {
                Integer.parseInt(ligne);
            } catch(NumberFormatException e) {
                logs.add(new ErreurFichier(noLigne," - Valeur pour nbSommets Invalide - Non convertissable en entier"));
            }


            while (true) {
                // Lecture de la ligne de la prochaine ligne
                ligne = tampon.readLine();
                noLigne = noLigne + 1;
                // Arrêter la lecture après être arrivé à la fin du fichier
                if (ligne == null) {
                    break;
                }
                logs = new ArrayList<>();
                // Calcule le nombre actuel de logs d'erreurs fichier
                int nbAnciensLogs = logs.size();

                // Divise la ligne en colonnes d'après le séparateur ";" puis les ajoute dans un tableau de String
                String[] valeurs = ligne.split(" ");

                // -- VERIFICATION DU FICHIER 

                // Vérifie si le nombre d'arguments est valide (=6)
                if (valeurs.length != 2) {
                    // Nombre d'arguments invalide
                    logs.add(new ErreurFichier(noLigne,"Nombre d'arguments invalides - Requis [2] Actuel["+String.valueOf(valeurs.length)+"]"));
                } else {
                    // Nombre d'arguments valide

                    // Vérifie si les types de valeur sont corrects;
                    for (int i = 0; i < 2; i++) {
                        try { 
                                int testInt = Integer.parseInt(valeurs[i]);
                        } catch(NumberFormatException e) {
                                logs.add(new ErreurFichier(noLigne,"Argument ["+String.valueOf(i)+"] = "+valeurs[i]+" - Non convertissable en entier"));
                        }
                    }

                }
                // Ajout des données des colonnes au Gestionnaire
                Arete areteAjoute;
                if (nbAnciensLogs == logs.size()) {
                    Sommet SommetA;
                    Sommet SommetB = new Sommet(graphe,Integer.parseInt(valeurs[0]));
                    if (graphe.possedeSommetOf(Integer.parseInt(valeurs[0]))) {
                        SommetA = graphe.getSommetOf(Integer.parseInt(valeurs[0]));
                    } else {
                        SommetA = new Sommet(graphe,Integer.parseInt(valeurs[0]));
                        graphe.ajouterSommet(SommetA);
                        if (DevOptions.options.get("devCommentaires") && DevOptions.options.get("detailsChargementFichier") && DevOptions.options.get("detailsSommets")) {
                            System.out.println("Sommet ajoute - "+SommetA);
                        }
                    }
                    if (graphe.possedeSommetOf(Integer.parseInt(valeurs[0]))) {
                        SommetB = graphe.getSommetOf(Integer.parseInt(valeurs[0]));
                    } else {
                        SommetB = new Sommet(graphe,Integer.parseInt(valeurs[1]));
                        graphe.ajouterSommet(SommetB);
                        if (DevOptions.options.get("devCommentaires") && DevOptions.options.get("detailsChargementFichier") && DevOptions.options.get("detailsSommets")) {
                            System.out.println("Sommet ajoute - "+SommetB);
                        }
                    }
                    areteAjoute = new Arete(SommetA, SommetB);
                    if (DevOptions.options.get("devCommentaires") && DevOptions.options.get("detailsChargementFichier") && DevOptions.options.get("detailsAretes")) {
                        System.out.println("Arete ajoute - "+areteAjoute);
                    }
                } else {
                    areteAjoute = null;
                }
                graphe.ajouterArete(areteAjoute);
                
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (logs.isEmpty()) {
            logs = null;
        }
        return logs;
    }
    
    public static int nbLigne(String chemin){
        FileReader monFichier;
        BufferedReader tampon;
        int nbLigne = 0;
        try {
            // Ouverture et mise en tampon du fichier
            monFichier = new FileReader(chemin);
            tampon = new BufferedReader(monFichier);
            String ligne;
            
            while (true) {
                // lit une ligne et la transmet a la variable ligne
                ligne = tampon.readLine();
                if (ligne == null) {
                    break;
                } else {
                    nbLigne = nbLigne + 1;
                }
            }
            
            // Fermeture du fichier
            tampon.close();
            monFichier.close();
        } catch (IOException ex) {
            Logger.getLogger(Fichier.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }
}

