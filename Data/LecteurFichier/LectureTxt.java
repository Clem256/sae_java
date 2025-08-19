/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Data.LecteurFichier;

import Graphique.Coloration.AffichageGrapheColo;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.graphstream.graph.Graph;

public class LectureTxt {

    private Graph graph;
    private int kmax;
    private int sommet;
    private int nb_arete = 0;
    private long debut;
    private String nom_fichier;

    /**
     * Cette méthode permet la lecture d'un fichier .txt Elle permet également
     * de séparer les informations et de les utiliser pour les sommets (sommet),
     * la coloration (kmax) ou les 2 identifiants pour relier 2 nodes.
     *
     * @param graph
     */
    public LectureTxt(Graph graph) {
        this.graph = graph;

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Ouvrir le fichier");
        //filtre pour afficher uniquement les txt
        FileNameExtensionFilter filter = new FileNameExtensionFilter("txt files (*.txt)", "txt");
        fileChooser.setFileFilter(filter);
        //fenetre pour choisir le fichier
        int result = fileChooser.showOpenDialog(null);
        
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            String nomFichier = selectedFile.getName();
            nom_fichier = nomFichier;
            System.out.println("Nom fichier: " + nomFichier);
            //essayer de lire le fichier
            try (BufferedReader reader = new BufferedReader(new FileReader(selectedFile))) {
                try {
                    kmax = Integer.parseInt(reader.readLine());
                    sommet = Integer.parseInt(reader.readLine());
                    debut = System.currentTimeMillis();
                } catch (NumberFormatException e) {
                    //erreur format
                    JOptionPane.showMessageDialog(null, "Erreur de fichier/format", "Erreur", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                String ligne;
                //transfert du nombre de sommet et de kmax
                AffichageGrapheColo agt = new AffichageGrapheColo(graph, kmax, sommet);
                
                while ((ligne = reader.readLine()) != null) {
                    //split pour les valeurs du fichier
                    String[] valeurs = ligne.split("\\s+");
                    if (valeurs.length >= 2) {
                        int identifiant1;
                        int identifiant2;
                        try {
                            identifiant1 = Integer.parseInt(valeurs[0]);
                            identifiant2 = Integer.parseInt(valeurs[1]);
                        } catch (NumberFormatException e) {
                            System.out.println("Erreur format: " + ligne);
                            //passer à la ligne d'après
                            continue;
                        }
                        //ajout aretes
                        AffichageGrapheColo.ajoutArete(graph, identifiant1, identifiant2);
                        nb_arete++;
                    } else {
                        System.err.println("Erreur, ligne incorrecte : " + ligne);
                    }
                }
            } catch (IOException e) {
                System.err.println("Erreur dans le chargement");
            }

            long fin = System.currentTimeMillis();
            long duree = fin - debut;
            //calcul du temps total d'éxécution
            System.out.println("Temps d'exécution: " + duree + " millisecondes");
        }
    }
    /**
     * Liste des getters/setters
     * @return 
     */
    public int getNb_arete() {
        return nb_arete;
    }

    public void setNb_arete(int nb_arete) {
        this.nb_arete = nb_arete;
    }

    public int getKmax() {
        return kmax;
    }

    public int getSommet() {
        return sommet;
    }

    public String getNom_fichier() {
        return nom_fichier;
    }
    
    
}