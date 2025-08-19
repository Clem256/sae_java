package Data.LecteurFichier;

import Map.AffichageVolAe;
import Map.ListeVol;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileNameExtensionFilter;
import sae.java.controleur.DevOptions;
import sae.java.controleur.Gestionnaire;
/**
 * Cette classe contient les lecteurs de fichier pour les aéroports et les vols , avec gestion des erreurs 
 * @author User
 */
public final class LectureVolAeroport {

    private static String chemin_ae = null;
    private ArrayList<String> val_vol = new ArrayList<>();
    private HashMap<Double, Double> coord = new HashMap<>();
    private ArrayList<String> code = new ArrayList<>();
    private ArrayList<String> vol = new ArrayList<>();
    private ListeVol listeVol;
    private double coordonnee;
    private String nom_fichier;
    private String chemin_fichier_vol;
    private int cptErreur = 0;
    public LectureVolAeroport() throws UnsupportedLookAndFeelException, IOException {
        //changement de style
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | UnsupportedLookAndFeelException e) {
            System.err.println("Erreur design fenetre");
        }
        cptErreur = 0;
        //appelle des méthodes pour lire les valeurs des fichiers
        lireCoordonnees();
        lireVol();
        //Pour calculer les conflits
        new DevOptions();

        Gestionnaire g;
        g = new Gestionnaire(chemin_ae, chemin_fichier_vol);
        //pour l'affichage de la map et le 2nd JTable sur les données de vol
        AffichageVolAe am = new AffichageVolAe(coord, code, vol);
        listeVol = new ListeVol(val_vol);
    }
    /**
     * Méthode qui permet d'ouvrir un dialogue 
     */
    private void lireCoordonnees() {
        if (chemin_ae == null) {
            JFileChooser fileChooser = new JFileChooser();
            
            fileChooser.setDialogTitle("Ouvrir le fichier des coordonnées");
            FileNameExtensionFilter filter = new FileNameExtensionFilter("TXT files (*.txt)", "txt");

            fileChooser.setFileFilter(filter);
            //ouvre une boite de dialogue pour la sélection du fichier
            int result = fileChooser.showOpenDialog(null);
            //si un clic est effectuer
            if (result == JFileChooser.APPROVE_OPTION) {
                chemin_ae = fileChooser.getSelectedFile().getPath();
            }
        }

        if (chemin_ae != null) {
            try {
                try (BufferedReader reader = new BufferedReader(new FileReader(chemin_ae))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        String[] val = line.split(";");
                        //si erreur , on l'ignore
                        if (val.length != 10) {
                            continue;
                        }
                        ajouterCoord(val);
                    }
                }
            } catch (IOException e) {
                System.err.println("Erreur de la lecture du fichier");
            }
        }
    }

    public void ajouterCoord(String[] val) {
        try {
            String nom = val[0];
            double degLat = Double.parseDouble(val[2]);
            double minLat = Double.parseDouble(val[3]);
            double secLat = Double.parseDouble(val[4]);
            String orientationLat = val[5];
            double degLong = Double.parseDouble(val[6]);
            double minLong = Double.parseDouble(val[7]);
            double secLong = Double.parseDouble(val[8]);
            String orientationLong = val[9];

            double latitude = calculCoordonnee(degLat, minLat, secLat, orientationLat);
            double longitude = calculCoordonnee(degLong, minLong, secLong, orientationLong);

            code.add(nom);
            coord.put(latitude, longitude);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Erreur dans le format", "Erreur dans le format", JOptionPane.WARNING_MESSAGE);
        }
    }

    public double calculCoordonnee(double deg, double min, double sec, String orientation) {
        coordonnee = deg + min / 60.0 + sec / 3600.0;
        //si N ou E alors * 1 , sinon * -1
        if (orientation.equals("N") || orientation.equals("E")) {
            return coordonnee;
        } else {
            return -coordonnee;
        }
    }

    public void lireVol() throws FileNotFoundException, IOException {
        JFileChooser fileName = new JFileChooser();
        fileName.setDialogTitle("Ouvrir le fichier des vols");
        FileNameExtensionFilter filter = new FileNameExtensionFilter("CSV files (*.csv)", "csv");
        fileName.setFileFilter(filter);
        //dialogue pour le choix du fichier
        int result = fileName.showOpenDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {
            chemin_fichier_vol = fileName.getSelectedFile().getPath();
            nom_fichier = fileName.getSelectedFile().getName();
            System.out.println("Fichier ouvert : " + nom_fichier);

            try (BufferedReader tampon = new BufferedReader(new FileReader(chemin_fichier_vol))) {
                String ligne;
                while ((ligne = tampon.readLine()) != null) {
                    try {
                        String[] valeurs = ligne.split(";");
                        //si erreur on change de split pour le lire
                        if (valeurs.length < 6) {
                            if (cptErreur == 0){
                                JOptionPane.showMessageDialog(null,"Erreur dans le fichier , ligne" + ligne , "Erreur fichier" , JOptionPane.ERROR_MESSAGE);
                            }
                            cptErreur++;
                            valeurs = ligne.split(",");
                            
                            if (valeurs.length < 6) {
                                System.err.println("Format incorect: " + ligne);
                                System.out.println();
                            } else {
                                //tranfert des données avec split ,
                                vol.add(valeurs[1]);
                                vol.add(valeurs[2]);
                                val_vol.add(valeurs[0]);
                                val_vol.add(valeurs[1]);
                                val_vol.add(valeurs[2]);
                                val_vol.add(valeurs[3]);
                                val_vol.add(valeurs[4]);
                                val_vol.add(valeurs[5]);
                            }
                        } else {
                            //transfert de donnée avec spilt ;
                            vol.add(valeurs[1]);
                            vol.add(valeurs[2]);
                            val_vol.add(valeurs[0]);
                            val_vol.add(valeurs[1]);
                            val_vol.add(valeurs[2]);
                            val_vol.add(valeurs[3]);
                            val_vol.add(valeurs[4]);
                            val_vol.add(valeurs[5]);
                        }

                    } catch (IllegalArgumentException e) {
                        System.err.println("Erreur de lecture de la ligne: " + ligne + ". " + e.getMessage());
                    }
                }
            }
        }

        if (listeVol != null) {
            listeVol.MajValVol(val_vol);
        }
    }
    /**
     * Liste des getters/setters
     * @return 
     */
    public HashMap<Double, Double> getCoord() {
        return coord;
    }

    public void setCoord(HashMap<Double, Double> coord) {
        this.coord = coord;
    }

    public ArrayList<String> getCode() {
        return code;
    }

    public void setCode(ArrayList<String> code) {
        this.code = code;
    }

    public ArrayList<String> getVol() {
        return vol;
    }

    public void setVol(ArrayList<String> vol) {
        this.vol = vol;
    }

    public static String getChemin_ae() {
        return chemin_ae;
    }

    public static void setChemin_ae(String chemin_ae) {
        LectureVolAeroport.chemin_ae = chemin_ae;
    }

    public String getNom_fichier() {
        return nom_fichier;
    }

    public void setNom_fichier(String nom_fichier) {
        this.nom_fichier = nom_fichier;
    }
}
