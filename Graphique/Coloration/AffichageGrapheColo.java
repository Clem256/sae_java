package Graphique.Coloration;

import Data.LecteurFichier.LectureTxt;
import Data.LecteurFichier.LectureVolAeroport;
import static algo.AlgoColoration.coloration1;
import Map.AffichageVolAe;
import algo.AlgoColoration;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import org.graphstream.algorithm.ConnectedComponents;
import org.graphstream.graph.Edge;
import org.graphstream.graph.EdgeRejectedException;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.swingViewer.View;
import org.graphstream.ui.swingViewer.Viewer;

public class AffichageGrapheColo extends JFrame {

    private Graph graph = new SingleGraph("t");
    private JPanel panel = new JPanel();
    private JLabel choixGraphetest = new JLabel("Choix du graphe à charger :" + "               ");
    private JButton choixGrapheB = new JButton("Choix");
    private JComboBox<String> listeAlgo;
    private JLabel choixAlgo;
    private JButton choixAlgoCombo;
    private JLabel stats;
    private JLabel nbConflit;
    private JLabel degreM;
    private JLabel compConnexe;
    private JLabel noeud;
    private JLabel arrete;
    private JLabel diametre;
    private JLabel k;
    private JButton changement;
    private int kmax;
    private int sommet;
    private int nbarete = 0;
    private int nb_deg_max = 0;
    private int composant_connexe;
    private int nb_diametre;
    private JLabel Modification = new JLabel("<html><font size='5'><u>Modification</u></font></html>");
    private JLabel changement_k = new JLabel("Kmax : ");
    private JTextField val_kmax = new JTextField(5);
    private JButton valider_modif = new JButton("Valider");
    JPanel affichageGraph = new JPanel();
    JButton affichage_g = new JButton("Afficher graphe");
    private JLabel degree_moy = new JLabel("Degrée moyen :" + 0);
    private int cpt_affichage = 0;
    private int nb = 0;
    JMenuBar menu;
    JMenu Vol, graphe;
    JMenuItem AffichageVol, affichageG, nonAffichageG;
    private Viewer viewer;
    private View view;
    private int viewX;
    private int viewY;
    private int conteur = 0;
    private String nom_fichier;
    private String temp2 = "";

    /**
     * Constructeur qui permet de récupérer 3 variables, initialiser dans
     * LectureTxt Elle permet aussi d'appeler les fonctions de créations de
     * l'interface graphique et de l'ajout d'une node
     *
     * @param graph
     * @param kmax
     * @param sommet
     */
    public AffichageGrapheColo(Graph graph, int kmax, int sommet) {

        this.graph = graph;
        this.kmax = kmax;
        this.sommet = sommet;
        this.setTitle("affichagegraphe");
        init();
        ajoutNode(graph, sommet);

    }

    /**
     * Constructeur qui peut être appelé de n'importe où et initialise également
     * l'interface graphique
     */
    public AffichageGrapheColo() {
        this.setTitle("affichagegraphe");
        init();
    }

    /**
     * Méthode qui permet de clear le graphe si néssésaire
     */
    private void resetGraph() {
        graph.clear();
    }

    /**
     * Cette méthode permet le placement de toutes les parties graphiques sur le
     * JPanel ainsi que les interactions avec les boutons
     */
    public void init() {
        //changement de style
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | UnsupportedLookAndFeelException e) {
            System.err.println("Erreur design fenetre");
        }
        //initiation des objets de l'interface graphique 
        choixAlgo = new JLabel("Choix d'un algorithme :");
        choixAlgoCombo = new JButton("Valider votre choix");
        stats = new JLabel("<html><font size='5'><u>Stats : </u></font></html>");
        nbConflit = new JLabel("Nombre de conflit :" + 0);
        degreM = new JLabel("Nombre de degré max :" + 0);
        compConnexe = new JLabel("Composant connexe :" + 0);
        noeud = new JLabel("Nombre de noeuds : " + 0);
        arrete = new JLabel("Nombre d'arrete :" + 0);
        diametre = new JLabel("Diametre :" + 0);
        k = new JLabel("Kmax : " + 0);
        String[] nomAlgo = {"Aucun", "DSatur", "WellshPowell"};
        listeAlgo = new JComboBox<>(nomAlgo);

        panel.setLayout(new BorderLayout());
        JPanel pEntree = new JPanel();
        pEntree.setLayout(new GridBagLayout());
        GridBagConstraints cont = new GridBagConstraints();
        cont.fill = GridBagConstraints.BOTH;

        menu = new JMenuBar();

        Vol = new JMenu("Map");
        graphe = new JMenu("Graphe");

        menu.add(Vol);
        menu.add(graphe);

        AffichageVol = new JMenuItem("Afficher vol");
        affichageG = new JMenuItem("Afficher graphe");
        nonAffichageG = new JMenuItem("Non affichage graphe");

        Vol.add(AffichageVol);

        graphe.add(affichageG);
        graphe.add(nonAffichageG);

        setJMenuBar(menu);

        cont.gridx = 0;
        cont.gridy = 1;
        pEntree.add(choixGraphetest, cont);

        cont.gridy = 2;
        pEntree.add(choixGrapheB, cont);

        cont.gridy = 3;
        pEntree.add(choixAlgo, cont);

        cont.gridy = 4;
        pEntree.add(listeAlgo, cont);

        cont.gridy = 5;
        pEntree.add(choixAlgoCombo, cont);

        JPanel pStat = new JPanel();
        pStat.setLayout(new GridBagLayout());
        pStat.setBorder(BorderFactory.createTitledBorder("<html><font size='5'><u>Statistiques : </u></font></html>"));

        cont.gridy = 6;
        pStat.add(nbConflit, cont);

        cont.gridy = 7;
        pStat.add(k, cont);

        cont.gridy = 8;
        pStat.add(degreM, cont);

        cont.gridy = 9;
        pStat.add(compConnexe, cont);

        cont.gridy = 10;
        pStat.add(noeud, cont);

        cont.gridy = 11;
        pStat.add(arrete, cont);

        cont.gridy = 12;
        pStat.add(diametre, cont);

        cont.gridy = 13;
        pStat.add(nbConflit, cont);

        cont.gridy = 14;
        pStat.add(degree_moy, cont);

        cont.gridy = 15;
        pStat.add(diametre, cont);

        cont.gridy = 16;
        pEntree.add(pStat, cont);

        cont.gridy = 17;
        pEntree.add(Modification, cont);

        cont.gridy = 18;
        pEntree.add(changement_k, cont);

        cont.gridy = 19;
        pEntree.add(val_kmax, cont);

        cont.gridy = 20;
        pEntree.add(valider_modif, cont);

        panel.add(pEntree, BorderLayout.WEST);

        //action lors de l'appuie sur AffichageVol
        AffichageVol.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    new LectureVolAeroport();
                } catch (UnsupportedLookAndFeelException ex) {
                    //enregistrement des erreurs "grave" (générée par netbeans)
                    Logger.getLogger(AffichageGrapheColo.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(AffichageGrapheColo.class.getName()).log(Level.SEVERE, null, ex);
                }
                dispose();
            }
        });

        //affichage du graphe 
        affichageG.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (view == null) {
                    viewer = new Viewer(graph, Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
                    viewer.enableAutoLayout();
                    view = viewer.addDefaultView(false);
                    panel.add((Component) view, BorderLayout.CENTER);

                    // Ajout du MouseWheelListener après l'initialisation de view
                    view.addMouseWheelListener(new MouseWheelListener() {
                        @Override
                        public void mouseWheelMoved(MouseWheelEvent e) {
                            int rotation = e.getWheelRotation();
                            double scaleFactor = 1.1;
                            // équivalent d'un if en une ligne
                            double zoomFactor = (rotation < 0) ? 1.0 / scaleFactor : scaleFactor;
                            view.getCamera().setViewPercent(view.getCamera().getViewPercent() * zoomFactor);
                        }
                    });

                }
                view.setVisible(true);
                panel.revalidate();
                panel.repaint();
            }
        });
        //enlever l'affichage du graphe
        nonAffichageG.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (view != null) {
                    view.setVisible(false);
                    panel.revalidate();
                    panel.repaint();
                }
            }
        });

        this.setContentPane(panel);
        this.pack();
        //permet de charger un fichier lors de l'appuie du bouton
        choixGrapheB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetGraph();
                LectureTxt lt = new LectureTxt(graph);
                nom_fichier = lt.getNom_fichier();
                //mise à jour des stats
                k.setText("Kmax : " + lt.getKmax());
                noeud.setText("Nombre de noeuds : " + lt.getSommet());
                arrete.setText("Nombre d'arete : " + lt.getNb_arete());
                DegreeMax();
                compConnexe.setText("Composant connexe :" + String.valueOf(calculConnexe(graph)));
                degree_moy.setText("Degrée Moyen :" + calculDegreeMoyen(graph));
                diametre.setText("Diametre :" + affichediametre(graph));
                panel.revalidate();
                panel.repaint();
            }
        });
        //JComboBox qui permet le choix de l'un des 2 algo
        choixAlgoCombo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String temp = (String) listeAlgo.getSelectedItem();
                if (temp.equals("WellshPowell")) {
                    nb = coloration1(graph, getKmax(), conteur);
                    conteur++;
                    try {
                        //écriture dans le fichier pour algo1
                        String data = temp2 + "\r" + nom_fichier + ";" + nb;
                        FileWriter writer2 = new FileWriter("coloration-groupeX.Y.csv");

                        writer2.write(data);

                        temp2 = data;

                        writer2.close();

                        System.out.println("Successfully wrote text to file.");

                    } catch (IOException ei) {
                        System.out.println("An error occurred.");
                        ei.printStackTrace();
                    }

                    nbConflit.setText("Nombre de conflit :" + nb);
                }
                if (temp.equals("DSatur")) {
                    nb = AlgoColoration.coloration2(graph, getKmax(), conteur);
                    conteur++;
                    try {
                        //écriture pour algo 2
                        String data = temp2 + "\r" + nom_fichier + ";" + nb;

                        FileWriter writer2 = new FileWriter("coloration-groupeX.Y.csv");

                        writer2.write(data);

                        temp2 = data;

                        writer2.close();

                        System.out.println("Successfully wrote text to file.");

                    } catch (IOException ei) {
                        System.out.println("An error occurred.");
                        ei.printStackTrace();
                    }
                    //mise à jour du nombre de conflit
                    nbConflit.setText("Nombre de conflit :" + nb);
                }
            }
        });
        //bouton pour valider et changer la nouvelle valeur de kmax 
        valider_modif.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    kmax = Integer.parseInt(val_kmax.getText());
                    k.setText("Kmax : " + kmax);
                } catch (NumberFormatException ex) {
                    //si la nouvelle valeur n'est pas un int
                    JOptionPane.showMessageDialog(null, "Valeur de Kmax incorrect ", "Erreur Kmax", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

    }

    /**
     * Fonction qui permet de calculer le degré max , en parcourant tout les
     * sommets
     */
    public void DegreeMax() {

        nb_deg_max = 0;
        for (Node node : graph) {
            int degree = node.getDegree();
            if (degree > nb_deg_max) {
                nb_deg_max = degree;
            }
        }

        degreM.setText("Nombre de degré max : " + nb_deg_max);
    }

    /**
     * Cette fonction permet d'ajouter de créer des nodes en leur attribuant des
     * codes
     *
     * @param graph
     * @param sommet
     */
    public void ajoutNode(Graph graph, int sommet) {
        for (int i = 1; i <= sommet; i++) {
            String nomNode = "Node_" + i;
            //vérifier si le nom de la Node n'est oas déja attribué
            if (!graph.getNodeSet().contains(nomNode)) {
                Node node = graph.addNode(nomNode);
                node.setAttribute("latitude", i);
                node.setAttribute("longitude", i);
            } else {
                System.out.println("Node existe déja: " + nomNode);
                return;
            }

        }
    }

    /**
     * Cette fonction est appelée dans LectureTxt pour créer les aretes de
     * chaque graphe en fonction de leur identifiant
     *
     * @param graph
     * @param identifiant1
     * @param identifiant2
     */
    public static void ajoutArete(Graph graph, int identifiant1, int identifiant2) {
        String id1 = "Node_" + identifiant1;
        String id2 = "Node_" + identifiant2;
        Node node1 = graph.getNode(id1);
        Node node2 = graph.getNode(id2);
        //si les nodes n'existe pas
        if (node1 == null || node2 == null) {
            return;
        }

        String nomEdge = id1 + "_" + id2;
        //si l'edge existe déja
        if (graph.getEdge(nomEdge) != null || graph.getEdge(id2 + "_" + id1) != null) {

            return;
        }

        try {
            Edge edge = graph.addEdge(nomEdge, node1, node2);

        } catch (EdgeRejectedException e) {
            System.err.println("Erreur edge ");
        }
    }

    /**
     * Cette fonction sert à afficher le graphe, en appelant cette fonction à la
     * fin de la Classe LectureTxt
     *
     * @param graph
     */
    public static int affichageGraphe(Graph graph, int kmax) {
        int nb = coloration1(graph, kmax, 0);
        //graph.display();
        return nb;

    }

    /**
     * Appelle de l'algorithme de calculConnexe présent dans graphstream
     *
     * @param graph
     * @return
     */
    public int calculConnexe(Graph graph) {
        ConnectedComponents cc = new ConnectedComponents();
        cc.init(graph);
        return cc.getConnectedComponentsCount();
    }

    /**
     * Fonction qui permet de retourner le degreeMoyen du graphe charger , avec
     * un format de 2 chiffre après la virgule
     *
     * @param graph
     * @return
     */
    public String calculDegreeMoyen(Graph graph) {
        int numNodes = graph.getNodeCount();
        int sommeDegree = 0;

        for (Node node : graph) {
            sommeDegree += node.getDegree();
        }

        double degree_moy = (double) sommeDegree / numNodes;
        DecimalFormat format = new DecimalFormat("0.00");

        String d_moy = format.format(degree_moy);
        return d_moy;
    }

    /**
     * Méthode qui permet de calculer le diametre , en parcourant tous les
     * voisins
     *
     * @param g
     * @return
     */
    public static int affichediametre(Graph g) {
        int diametre = 0, i = 0;
        for (Node n : g) {
            n.addAttribute("id", i);
            i++;
        }
        for (Node n : g) {
            int dmax = parcourslargeur(g, n);
            if (dmax > diametre) {
                diametre = dmax;
            }
        }
        return diametre;
    }

    /**
     * Effectue un parcours en largeur à partir du nœud spécifié dans le graphe
     * donné, et retourne le diamètre du plus grand chemin trouvé à partir de ce
     * nœud.
     * @param g Le graphe ou est effectuer le parcours.
     * @param n Le nœud de départ.
     * @return Le diamètre du plus grand chemin à partir du nœud donné.
     */
    public static int parcourslargeur(Graph g, Node n) {
        int taille = g.getNodeCount(); 
        int[] distance = new int[taille]; // Tableau pour stocker les distances depuis le nœud de départ
        Arrays.fill(distance, -1); // Initialisation des distances à -1 (non visité)

        Queue<Node> queue = new LinkedList<>(); // File pour le parcours en largeur
        distance[n.getAttribute("id")] = 0; // Distance du nœud de départ à lui-même est 0
        queue.add(n); // Ajout du nœud de départ à la file
        int dmax = 0; 

        // Parcours en largeur à partir du nœud de départ
        while (!queue.isEmpty()) {
            Node u = queue.poll(); // Récupération et suppression du premier nœud de la file
            Iterator<Node> ite = u.getNeighborNodeIterator(); // Itérateur sur les nœuds voisins de u

            while (ite.hasNext()) {
                Node voisin = ite.next(); // Récupération du nœud voisin
                int vid = voisin.getAttribute("id"); 

                if (distance[vid] == -1) { // Si pas encore visité
                    distance[vid] = distance[u.getAttribute("id")] + 1;
                    queue.add(voisin); // Ajout du nœud voisin à la file pour continuer le parcours
                }
            }
        }

        // Calcul du diamètre en trouvant la plus grande distance atteinte
        for (int dist : distance) {
            if (dist > dmax) {
                dmax = dist;
            }
        }

        return dmax;
    }

    /**
     * Getter et Setter utile pour la récupération de Kmax et des Sommets
     *
     * @return
     */
    public int getKmax() {
        return kmax;
    }

    public int getSommet() {
        return sommet;
    }

    public void setKmax(int kmax) {
        this.kmax = kmax;
        k.setText("Kmax : " + kmax);
    }

    public void setSommet(int sommet) {
        this.sommet = sommet;
        noeud.setText("Nombre de noeuds : " + sommet);
    }

    public Graph getGraph() {
        return graph;
    }

    public void setGraph(Graph graph) {
        this.graph = graph;
    }

}
