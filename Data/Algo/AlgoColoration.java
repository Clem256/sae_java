package algo;

import java.awt.Color;
import java.util.Iterator;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import java.io.FileWriter;
import java.io.IOException;

/**
 * La classe AlgoColo contient des méthodes pour la coloration des sommets d'un
 * graphe.
 */
public class AlgoColoration {

    /**
     * Effectue la première méthode de coloration des sommets d'un graphe.
     *
     * @param g Le graphe à colorier.
     * @param kmax Le nombre maximal de couleurs autorisé.
     * @param cont Le compteur utilisé pour le nom du fichier de sortie.
     * @return Le nombre de sommets ayant dépassé le nombre maximal de couleurs.
     */
    public static int coloration1(Graph g, int kmax, int cont) {
        // Initialisation des attributs des noeuds du graphe
        int nb = 0;
        int max = g.getNodeCount();
        int coul = 1;
        for (Node n : g.getNodeSet()) {
            n.addAttribute("coloration 1", 0);
            n.addAttribute("degre", 0);
            n.addAttribute("check", 0);
            n.addAttribute("col", 0);
        }

        // Calcul du degré de chaque noeud
        for (Node n : g.getNodeSet()) {
            int temp = n.getDegree();
            n.setAttribute("degre", temp);
        }

        for (int i = 0; i < max; i++) { // Parcours des degrés possibles de 0 à max-1
            for (Node n : g.getNodeSet()) {
                if ((int) n.getAttribute("degre") == i && (int) n.getAttribute("check") == 0) {
                    // Si le degré du nœud est égal à i et n'a pas encore été vérifié (check = 0)
                    Iterator it = n.getNeighborNodeIterator(); // Itérateur sur les voisins du nœud
                    while (it.hasNext()) {
                        Node voisin = (Node) it.next(); // Sélection du prochain voisin
                        if ((int) voisin.getAttribute("coloration 1") == coul) {
                            // Si le voisin a la même couleur que coul
                            coul++;
                        } else {
                            // Sinon, on assigne la couleur coul au nœud n
                            n.setAttribute("coloration 1", coul);
                            // On marque le nœud comme vérifié avec cette couleur
                            n.setAttribute("check", coul);
                        }
                        // Actualisation des attributs coloration 1 et check du nœud n
                        n.setAttribute("coloration 1", coul);
                        n.setAttribute("check", coul);
                    }
                }
                if ((int) n.getAttribute("coloration 1") > kmax && (int) n.getAttribute("col") == 0) {
                    // Si la couleur assignée dépasse kmax
                    n.setAttribute("col", 1);
                    nb++;
                }
            }
        }

        // Finalisation de la coloration
        for (Node n : g.getNodeSet()) {
            if ((int) n.getAttribute("coloration 1") == 0) {
                n.setAttribute("coloration 1", 1);
            }
            if ((int) n.getAttribute("coloration 1") > kmax) {
                n.setAttribute("coloration 1", kmax);
            }
            int test = n.getAttribute("coloration 1");
            int test2 = n.getAttribute("degre");
            int test3 = n.getIndex();
            System.out.println("couleur : " + test);
            System.out.println("degre : " + test2);
            System.out.println("index : " + test3);
        }
        System.out.println(nb);
        colorierGraphe(g, "coloration 1");
        WriteToFile(g, cont);
        return nb;
    }

    /**
     * Effectue la deuxième méthode de coloration des sommets d'un graphe.
     *
     * @param g Le graphe à colorier.
     * @param kmax Le nombre maximal de couleurs autorisé.
     * @param cont Le compteur utilisé pour le nom du fichier de sortie.
     * @return Le nombre de sommets ayant dépassé le nombre maximal de couleurs.
     */
    public static int coloration2(Graph g, int kmax, int cont) {
        // Initialisation des attributs des noeuds du graphe
        int nb = 0;
        int max = g.getNodeCount();
        int fin_colo = 0;
        int deg_max = 0;
        int coul = 1;
        int check = 0;
        for (Node n : g.getNodeSet()) {
            n.addAttribute("coloration 1", 0);
            n.addAttribute("degre", 0);
            n.addAttribute("check", 0);
            n.addAttribute("col", 0);
        }

        // Calcul du degré de chaque noeud
        for (Node n : g.getNodeSet()) {
            int temp = n.getDegree();
            n.setAttribute("degre", temp);
        }

        // Algorithme de coloration
        while (fin_colo == 0) {
            check = 0; 
            deg_max = 0;
            coul = 1; // Couleur départ

            // Parcours pour trouver le degré maximal parmi les nœuds non colorés
            for (Node n : g.getNodeSet()) {
                if ((int) n.getAttribute("degre") > deg_max && (int) n.getAttribute("check") == 0) {
                    deg_max = n.getAttribute("degre"); // Met à jour le degré maximal
                }
            }

            // Parcours pour attribuer une couleur aux nœuds ayant le degré maximal
            for (Node n : g.getNodeSet()) {
                if ((int) n.getAttribute("degre") == 0) {
                    // Si le nœud a un degré de 0, il est coloré avec la couleur 1
                    n.setAttribute("coloration 1", 1);
                    n.setAttribute("check", 1); 
                }
                if ((int) n.getAttribute("degre") == deg_max && (int) n.getAttribute("check") == 0) {
                    // Si le nœud a le degré maximal et n'a pas encore été coloré
                    check = 1; 
                    Iterator it = n.getNeighborNodeIterator(); // Itérateur sur les voisins du nœud
                    while (it.hasNext()) {
                        Node voisin = (Node) it.next(); // Sélection du prochain voisin
                        if ((int) voisin.getAttribute("coloration 1") == coul) {
                            coul++; 
                        } else {
                            n.setAttribute("coloration 1", coul); // Attribue la couleur au nœud
                            n.setAttribute("check", coul); // Marque le nœud comme coloré avec cette couleur
                        }
                        // Actualisation des attributs coloration 1 et check du nœud
                        n.setAttribute("coloration 1", coul);
                        n.setAttribute("check", coul);
                    }
                }
            }

            // Vérifie si tous les nœuds ont été colorés
            fin_colo = 1;
            for (Node n : g.getNodeSet()) {
                if ((int) n.getAttribute("check") == 0) {
                    fin_colo = 0;
                }
            }
        }

        // Finalisation de la coloration
        for (Node n : g.getNodeSet()) {
            if ((int) n.getAttribute("coloration 1") > kmax) {
                n.setAttribute("col", 1);
                nb++;
            }
            if ((int) n.getAttribute("coloration 1") == 0) {
                n.setAttribute("coloration 1", 1);
            }
            if ((int) n.getAttribute("coloration 1") > kmax) {
                n.setAttribute("coloration 1", kmax);
            }
            int test = n.getAttribute("coloration 1");
            int test2 = n.getAttribute("degre");
            int test3 = n.getIndex();
        }
        System.out.println(nb);
        colorierGraphe(g, "coloration 1");
        WriteToFile(g, cont);
        return nb;
    }

    /**
     * Colore visuellement le graphe selon les attributs de coloration
     * spécifiés.
     *
     * @param g Le graphe à colorier.
     * @param attribut L'attribut de coloration des noeuds.
     */
    public static void colorierGraphe(Graph g, String attribut) {
        int max = g.getNodeCount();
        Color[] cols = new Color[max + 1];
        for (int i = 0; i <= max; i++) {
            //génère couleur aléatoire
            cols[i] = Color.getHSBColor((float) (Math.random()), 0.8f, 0.9f);
        }

        for (Node n : g) {
            int col = n.getAttribute(attribut);
            //effectue le changement de couleur visuellement, aléatoire
            if (n.hasAttribute("ui.style")) {
                n.setAttribute("ui.style", "fill-color:rgba(" + cols[col].getRed() + ","
                        + cols[col].getGreen() + "," + cols[col].getBlue() + ",200);");
            } else {
                n.addAttribute("ui.style", "fill-color:rgba(" + cols[col].getRed() + ","
                        + cols[col].getGreen() + "," + cols[col].getBlue() + ",200);");
            }
        }
    }

    /**
     * Écrit la coloration des noeuds du graphe dans un fichier texte.
     *
     * @param g Le graphe contenant les noeuds colorés.
     * @param cont Le compteur utilisé pour le nom du fichier de sortie.
     */
    public static void WriteToFile(Graph g, int cont) {
        try {
            FileWriter writer = new FileWriter("colo-" + cont + "eval.txt");
            for (Node n : g) {
                String data = n.getIndex() + " ; " + n.getAttribute("coloration 1") + "\n";
                writer.write(data);
            }
            writer.close();
            System.out.println("Écriture réussie dans le fichier.");
        } catch (IOException e) {
            System.err.println("Une erreur est survenue lors de l'écriture dans le fichier.");
        }
    }
}
