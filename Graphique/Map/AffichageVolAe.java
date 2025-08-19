package Map;

import Graphique.Coloration.AffichageGrapheColo;
import Data.LecteurFichier.LectureVolAeroport;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.*;
import java.io.IOException;
import java.util.*;
import java.util.logging.*;
import javax.swing.*;
import javax.swing.event.*;
import org.jxmapviewer.*;
import org.jxmapviewer.input.*;
import org.jxmapviewer.painter.*;
import org.jxmapviewer.painter.Painter;
import org.jxmapviewer.viewer.*;
import sae.java.modele.*;

public final class AffichageVolAe extends JXMapViewer {

    private Map<List<GeoPosition>, Color> couleurMap;
    private JXMapViewer mapViewer;
    private Map<Double, Double> coordonnee;
    private ArrayList<String> code;
    private ArrayList<String> vol;
    private List<GeoPosition> posi;
    private Map<String, GeoPosition> positionsMap;
    private Map<String, GeoPosition> volCoord;
    private Set<Waypoint> waypoints;
    private JTextField rechercheAe, rechercheHauteur;
    private JFrame frame;
    private JPanel p;
    private JMenuBar menu;
    private JMenu mapMenu, graphe;
    private JMenuItem AjouterAeroport, AfficherGraphe, ListeConflit, partie_graphe;
    private JLabel choix, Stats, NbNoeud, NbArete, DegreeMoy, nbComposants, diametre, val_securite, nbConflit, trieHauteur;
    private int marge_securite = 15;
    private JLabel Modification, Marge_secu;
    private JTextField val_modif;
    private JButton valider;
    private LectureVolAeroport LectureVolAeroport;
    private int nbNoeud = 0;
    private int nbArete = 0;
    private JPopupMenu suggestionsPopup;
    private ListeVol lvi;

    /**
     * Constructeur utiliser dans la classe vol
     */
    public AffichageVolAe() {
    }

    /**
     * Constructeur qui récupère des valeurs depuis LectureAeVol , ainsi que
     * initiation des arraylist et map utiliser. Initiation de l'interface
     * graphique via init
     * @param coord
     * @param code
     * @param vol
     */
    public AffichageVolAe(Map<Double, Double> coord, ArrayList<String> code, ArrayList<String> vol) {
        this.coordonnee = coord;
        couleurMap = new HashMap<>();

        this.code = code;
        this.vol = vol;
        this.posi = new ArrayList<>();
        this.positionsMap = new HashMap<>();
        this.volCoord = new HashMap<>();
        this.waypoints = new HashSet<>();
        init();
    }

    public void init() {
        //tentative de changement de style (celui du système)
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | UnsupportedLookAndFeelException e) {
        }

        frame = new JFrame("Affichage Graphe");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //création de la map , de la classe JXMapViewer
        mapViewer = new JXMapViewer();
        TileFactoryInfo info = new OSMTileFactoryInfo();
        DefaultTileFactory tileFactory = new DefaultTileFactory(info);
        mapViewer.setTileFactory(tileFactory);

        //zoom et moyen de bouger sur la carte
        mapViewer.setZoom(8);
        mapViewer.setAddressLocation(new GeoPosition(46.1753, 2.4650));
        PanMouseInputListener mil = new PanMouseInputListener(mapViewer);
        mapViewer.addMouseListener(mil);
        mapViewer.addMouseMotionListener(mil);
        mapViewer.addMouseWheelListener(new ZoomMouseWheelListenerCenter(mapViewer));

        frame.setLayout(new BorderLayout());
        frame.getContentPane().add(mapViewer, BorderLayout.CENTER);
        //appele de initMenu pour initialiser le JMenuBar ainsi que ces fonctionnalitées
        initMenu();
        frame.getContentPane().add(p, BorderLayout.EAST);
        //initiation des autres composants de l'interface graphique
        initComposants();
        //popup pour suggestion
        suggestionsPopup = new JPopupMenu();

        frame.setSize(800, 600);
        frame.setVisible(true);
        //récupérer les valeurs de recherche1 à chaque mise à jour
        rechercheAe.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                filtre();
                Majsuggestions();

            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filtre();
                Majsuggestions();

            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                filtre();
                Majsuggestions();

            }
        });
        //garder focus
        rechercheAe.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (suggestionsPopup.isVisible()) {
                    rechercheAe.requestFocus();
                }
            }
        });
        //supprimer les valeurs de recherche1 lors du clic
        rechercheAe.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                rechercheAe.setText("");
            }

        });
        //méthode pour les mises à jours des positions
        MajPositions();
        //méthode pour les mises à jours des points
        MajWaypoints();
        //méthode affichage de certaines parties de la map
        afficherPoint();

    }

    /**
     * Méthode qui permet de suggérer des vols lors de l'utilisation de
     * rechercheAe , avec un JPopMenu
     */
    private void Majsuggestions() {
        // Récupère le texte entré 
        String entree = rechercheAe.getText().toLowerCase();

        // Supprime toutes les suggestions précédentes
        suggestionsPopup.removeAll();

        // Si l'entrée est vide, cache la popup et quitte la méthode.
        if (entree.isEmpty()) {
            suggestionsPopup.setVisible(false);
            return;
        }

        //ensemble pour stockée les valeurs filtrée
        Set<String> filtreDonnee = new HashSet<>();

        for (String item : vol) {
            // Si un vol commence par le texte entré,ajout de ce vol à filtreDonnee
            if (item.toLowerCase().startsWith(entree)) {
                filtreDonnee.add(item);
            }
        }

        // Si aucune donnée ne correspond au filtre, cache le popup et quitte la méthode.
        if (filtreDonnee.isEmpty()) {
            suggestionsPopup.setVisible(false);
            return;
        }

        for (String item : filtreDonnee) {
            //création d'un menuItem pour chaque élément dans filtreDonnee
            JMenuItem menuItem = new JMenuItem(item);
            menuItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // mise à jour du champ de recherche avec l'élément cliqué
                    rechercheAe.setText(item);
                    suggestionsPopup.setVisible(false);
                }
            });
            // Ajoute l'élément à la popup
            suggestionsPopup.add(menuItem);
        }

        // Affiche le popup sous champs texte
        suggestionsPopup.show(rechercheAe, 0, rechercheAe.getHeight());
        suggestionsPopup.setVisible(true);
    }

    /**
     * Cette méthode permet l'initialisation du menu ainsi que des actions pour
     * chacune des parties du menu
     */
    public void initMenu() {
        p = new JPanel(new GridBagLayout());
        GridBagConstraints cont = new GridBagConstraints();
        cont.fill = GridBagConstraints.BOTH;
        cont.gridx = 0;
        cont.gridy = 0;
        //création du menu et des différentes partie de celui ci
        menu = new JMenuBar();
        mapMenu = new JMenu("Map");
        graphe = new JMenu("Graphe");

        menu.add(mapMenu);
        menu.add(graphe);

        AjouterAeroport = new JMenuItem("Ajout vol");

        mapMenu.add(AjouterAeroport);

        AfficherGraphe = new JMenuItem("Affichage du graphe");
        mapMenu.add(AfficherGraphe);
        partie_graphe = new JMenuItem("Partie graphe");
        graphe.add(partie_graphe);

        ListeConflit = new JMenuItem("Liste des conflits");
        mapMenu.add(ListeConflit);
        frame.setJMenuBar(menu);
        //lors du clic sur ListeConflit
        ListeConflit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Arete.afficherTab();
            }
        });
        //lors du clic sur Partie_graphe
        partie_graphe.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AffichageGrapheColo agc = new AffichageGrapheColo();
                agc.setVisible(true);
                frame.dispose();
            }
        });
        //lors du clic sur AjouterVol
        AjouterAeroport.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    new LectureVolAeroport();
                    frame.dispose();
                } catch (UnsupportedLookAndFeelException ex) {
                    //enregistrement des erreurs "grave" (générée par netbeans)
                    Logger.getLogger(AffichageVolAe.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(AffichageVolAe.class.getName()).log(Level.SEVERE, null, ex);
                }
                //NbArete.setText(" " + nbArete);
            }
        });
        //lors du clic sur AfficherGraphe
        AfficherGraphe.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                GrapheStats gs = new GrapheStats(coordonnee, code, vol);
                gs.displayGraph();
            }
        });

    }

    /**
     * Méthode qui initie tout les labels , JTextField et bouton nécessaire au
     * fonctionnement des stats , du changement de valeur et des filtres
     */
    public void initComposants() {
        JPanel pStats = new JPanel(new GridBagLayout());
        Stats = new JLabel("<html><font size='5'><u>Statistiques :</u></font></html>");
        NbNoeud = new JLabel("Nombre de nœuds : " + nbNoeud);
        NbArete = new JLabel("Nombre d'arêtes : " + nbArete);
        DegreeMoy = new JLabel("Degré Moyen : " + calculerDegreMoyen());
        nbComposants = new JLabel("Nombre de composants : " + calculerComposantsConnexes());
        diametre = new JLabel("Diamètre : " + calculerDiametre());
        val_securite = new JLabel("Valeur de sécurité : " + marge_securite + " min");
        JLabel filtre = new JLabel("Par aéroport :");
        rechercheAe = new JTextField(5);
        rechercheAe.setPreferredSize(new Dimension(150, 25));
        choix = new JLabel("<html><font size='5'><u>Choix pour le filtrage:</u></font></html>");
        nbConflit = new JLabel("Nombre de conflit :" + 0);
        trieHauteur = new JLabel("Par hauteur");
        rechercheHauteur = new JTextField(5);
        //placement composant
        GridBagConstraints cont = new GridBagConstraints();
        cont.fill = GridBagConstraints.BOTH;

        cont.gridx = 0;
        cont.gridy = 1;
        pStats.add(Stats, cont);
        cont.gridy = 2;
        pStats.add(nbConflit, cont);
        cont.gridy = 3;
        pStats.add(NbNoeud, cont);
        cont.gridy = 4;
        pStats.add(NbArete, cont);
        cont.gridy = 5;
        pStats.add(DegreeMoy, cont);
        cont.gridy = 6;
        pStats.add(nbComposants, cont);
        cont.gridy = 7;
        pStats.add(diametre, cont);
        cont.gridy = 8;
        pStats.add(val_securite, cont);
        pStats.add(Box.createRigidArea(new Dimension(0, 10)));
        Modification = new JLabel("<html><font size='5'><u>Modification :</u></font></html>");
        Marge_secu = new JLabel("Marge de sécurité :");
        val_modif = new JTextField(5);
        val_modif.setPreferredSize(new Dimension(150, 25));
        valider = new JButton("Valider");
        cont.gridy = 9;
        cont.gridx = 0;
        pStats.add(Modification, cont);
        cont.gridy = 10;
        pStats.add(Marge_secu, cont);
        cont.gridx = 1;
        pStats.add(val_modif, cont);
        cont.gridx = 0;
        cont.gridy = 12;
        cont.gridwidth = 2;
        pStats.add(valider, cont);

        cont.gridx = 0;
        cont.gridy = 13;
        pStats.add(choix, cont);
        cont.gridy = 14;
        pStats.add(filtre, cont);
        cont.gridx = 1;
        pStats.add(rechercheAe, cont);
        cont.gridx = 0;
        cont.gridy = 15;
        pStats.add(trieHauteur, cont);
        cont.gridx = 1;
        pStats.add(rechercheHauteur, cont);
        //pour chaque mise à jour de rechercheHauteur , filtre mis à jour
        rechercheHauteur.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                filtre();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filtre();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                filtre();
            }
        });
        //action lors de l'appuie sur valider
        valider.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int nouveauSeuil = Integer.parseInt(val_modif.getText());
                    Vol.setSeuilEcartHoraire(nouveauSeuil);
                    val_securite.setText("Marge de sécurité : " + nouveauSeuil + " min");
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Veuillez entrer un nombre valide.", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        frame.getContentPane().add(pStats, BorderLayout.WEST);
    }

    /**
     * @return nombre de noeuds
     */
    private int calculerNombreNoeuds() {
        return positionsMap.size();
    }

    /**
     * @return calcul du degree moyen à partir d'autre valeur calculer
     */
    private double calculerDegreMoyen() {
        return (double) (2 * nbArete) / nbNoeud;
    }

    /**
     * Calcul les composants Connexes, en parcourant les voisins
     *
     * @return Le nombre de composants connexe
     */
    private int calculerComposantsConnexes() {
        int composant = 0;
        Set<GeoPosition> visiter = new HashSet<>();

        // Parcours de toutes les positions dans la carte (positionsMap)
        for (GeoPosition position : positionsMap.values()) {
            // Si la position n'a pas encore été visitée
            if (!visiter.contains(position)) {
                composant++;
                visiteVoisins(position, visiter); //explorer les voisins
            }
        }
        return composant;
    }

    /**
     * Méthode qui permet de parcourir tous les voisins d'une position donnée
     *
     * @param debut la position de départ
     * @param visiter ensemble des positions déja visitée
     */
    private void visiteVoisins(GeoPosition debut, Set<GeoPosition> visiter) {
        Queue<GeoPosition> queue = new LinkedList<>();
        queue.add(debut); // Ajoute la position de départ à la file d'attente
        visiter.add(debut); // Marque la position de départ comme visitée

        // Parcours pour explorer les voisins
        while (!queue.isEmpty()) {
            GeoPosition current = queue.poll(); // Retire le premier élément de la file

            // Pour chaque voisin de la position actuelle
            for (GeoPosition voisin : trouverVoisins(current)) {
                if (!visiter.contains(voisin)) {
                    visiter.add(voisin); // Marque le voisin comme visité
                    queue.add(voisin); // Ajoute le voisin pour l'exploration
                }
            }
        }
    }

    /**
     * Méthode qui trouve les voisins d'une position donnée dans le graphe
     *
     * @param position La position dont on cherche les voisins
     * @return Une liste des voisins de la position
     */
    private List<GeoPosition> trouverVoisins(GeoPosition position) {
        List<GeoPosition> voisins = new ArrayList<>();

        // Parcours de toutes les arêtes pour trouver les voisins 
        for (List<GeoPosition> arete : CreerArete()) {
            if (arete.size() > 1) {
                if (arete.get(0).equals(position)) {
                    voisins.add(arete.get(1));
                } else if (arete.get(1).equals(position)) {
                    voisins.add(arete.get(0));
                }
            }
        }
        return voisins;
    }

    /**
     * Calcul du diamètre du graphe représenté par positionsMap
     *
     * @return Le diamètre du graphe
     */
    private int calculerDiametre() {
        int diametre = 0;

        // Parcours de toutes les positions dans la carte (positionsMap)
        for (GeoPosition p1 : positionsMap.values()) {
            // Calcul des distances depuis p1 à toutes les autres positions
            Map<GeoPosition, Integer> distances = calculerDistances(p1);
            for (int distance : distances.values()) {
                if (distance > diametre) {
                    diametre = distance; // Met à jour le diamètre si une distance plus grande est trouvée
                }
            }
        }
        return diametre;
    }

    /**
     * Calcul des distances depuis une source donnée dans le graphe
     *
     * @param source position source à partir de laquelle ont calcule les
     * distances
     * @return Une map contenant les distances depuis la source
     */
    private Map<GeoPosition, Integer> calculerDistances(GeoPosition source) {
        Map<GeoPosition, Integer> distances = new HashMap<>();
        Queue<GeoPosition> queue = new LinkedList<>();
        queue.add(source); // Ajoute la source à la file d'attente
        distances.put(source, 0);

        // Parcours en largeur pour calculer les distances
        while (!queue.isEmpty()) {
            GeoPosition current = queue.poll(); // Retire le premier élément de la file

            int currentDistance = distances.get(current);

            // Pour chaque voisin de la position actuelle
            for (GeoPosition voisin : trouverVoisins(current)) {
                if (!distances.containsKey(voisin)) {
                    distances.put(voisin, currentDistance + 1); // Met à jour la distance au voisin
                    queue.add(voisin); // Ajoute le voisin à la file d'attente pour exploration
                }
            }
        }
        return distances;
    }

    /**
     * /**
     * Méthode qui permet de mettre à jour les positions sur la map
     */
    public void MajPositions() {
        // Copie des coordonnées actuelles dans une liste modifiable
        List<Map.Entry<Double, Double>> Liste_coord = new ArrayList<>(coordonnee.entrySet());
        int Coord_index = 0;
        for (String NomVol : vol) {
            // Vérifie s'il y a encore des coordonnées disponibles
            if (Coord_index < Liste_coord.size()) {
                Map.Entry<Double, Double> Cord_entry = Liste_coord.get(Coord_index);
                // Crée une nouvelle position (géoposition) avec les coordonnées
                GeoPosition position = new GeoPosition(Cord_entry.getKey(), Cord_entry.getValue());
                // Met à jour la map
                volCoord.put(NomVol, position);
                // Met à jour la carte principale des positions
                positionsMap.put(NomVol, position);
                Coord_index++;
            }
        }

        // Ajoute les positions valides à la liste posi
        for (String Obj_vol : vol) {
            GeoPosition position = volCoord.get(Obj_vol);
            if (position != null) {
                posi.add(position);
            }
        }

        // Calcul du nombre de nœuds + mise à jour du label
        nbNoeud = calculerNombreNoeuds();
        NbNoeud.setText("Nombre de nœuds : " + nbNoeud);
    }

    public void MajWaypoints() {
        waypoints.clear();
        // Crée des waypoints ou aéroports à partir de chaque position dans posi
        for (GeoPosition position : posi) {
            waypoints.add(new DefaultWaypoint(position));
        }
    }

    /**
     * Méthode qui permet d'afficher les points sur la map
     */
    public void afficherPoint() {
        // Crée un WaypointPainter pour afficher les d'aéroport
        WaypointPainter<Waypoint> AeroportPainter = new WaypointPainter<>();
        AeroportPainter.setWaypoints(waypoints);

        // Ajuste le zoom
        mapViewer.zoomToBestFit(new HashSet<>(posi), 0.7);
        // Liste des éléments à dessiner
        List<Painter<JXMapViewer>> painters = new ArrayList<>();
        List<Integer> hauteurs = Arete.getHauteurs();
        int taillePosi = posi.size();
        //pour hauteur unique , pour ne pas avoir plusieurs couleurs pour une hauteur
        Set<Integer> uniqueHauteurs = new HashSet<>(hauteurs);

        // Map pour associer chaque hauteur à une couleur
        Map<Integer, Color> hauteurCouleurMap = new HashMap<>();

        // Génère des couleurs pour chaque hauteur 
        Color[] colors = GénérationCouleur(uniqueHauteurs.size());

        int index = 0;
        // Associe chaque hauteur unique à une couleur
        for (Integer hauteur : uniqueHauteurs) {
            hauteurCouleurMap.put(hauteur, colors[index++]);
        }
        //donne le cas ou il n'y a pas plus d'une hauteur
        while (hauteurs.size() < (taillePosi / 2)) {
            hauteurs.add(1);
        }

        // Crée des arêtes et les associe à des couleurs et painters
        for (int i = 0; i < taillePosi - 1; i += 2) {
            // Récupère une paire de positions géographiques pour former une arête
            List<GeoPosition> liste_arete = posi.subList(i, i + 2);

            // si 2 positions ne sont pas null
            if (liste_arete.size() == 2 && liste_arete.get(0) != null && liste_arete.get(1) != null) {
                // Obtient la hauteur associée à cette arête
                int hauteur = hauteurs.get(i / 2);
                //recherche couleur
                Color color;
                if (hauteur == 0) {
                    color = Color.BLACK;
                } else {
                    color = hauteurCouleurMap.get(hauteur);
                }

                // Associe cette arête à sa couleur
                couleurMap.put(liste_arete, color);

                // dessin de l'arete , avec la couleur choisie précédement
                painters.add(new RoutePainter(liste_arete, color));
                nbArete++;
            }
        }
        //cas impaire
        if (taillePosi % 2 != 0) {
            // Récupère la dernière position
            GeoPosition dernierPoint = posi.get(taillePosi - 1);
            List<GeoPosition> dernierArete = new ArrayList<>();
            dernierArete.add(dernierPoint);
            Color color = CouleurPourAreteBase();
            couleurMap.put(dernierArete, color);
            painters.add(new RoutePainter(dernierArete, color));
        }

        // Met à jour les informations d'affichage
        nbConflit.setText("Nombre de conflit :" + 0);
        mapViewer.setOverlayPainter(new CompoundPainter<>(painters));
        NbArete.setText("Nombre d'arêtes : " + nbArete);
        DegreeMoy.setText("Degré Moyen : " + calculerDegreMoyen());
        nbComposants.setText("Nombre de composants : " + calculerComposantsConnexes());
        diametre.setText("Diamètre : " + calculerDiametre());
        nbConflit.setText("Nombre de conflit :" + Arete.getNombreDeLignes());

        // Ajoute le WaypointPainter à la liste des painters , pour dessiner les points et les aretes
        painters.add(AeroportPainter);
        mapViewer.setOverlayPainter(new CompoundPainter<>(painters));
    }

    /**
     * Méthode qui permet de générée des couleurs
     *
     * @param n
     * @return un tableau de couleur aléatoire
     */
    private Color[] GénérationCouleur(int n) {
        // Génère un tableau de x couleurs aléatoires
        Color[] colors = new Color[n];
        Random rand = new Random();
        for (int i = 0; i < n; i++) {
            colors[i] = new Color(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256));
        }
        return colors;
    }

    /**
     * Méthode qui donne une couleur de base pour les cas impaires
     *
     * @return
     */
    private Color CouleurPourAreteBase() {
        return Color.BLACK;
    }

    /**
     * Méthode qui permet de créer des aretes en fonction de différentes
     * positions , qui son contenue dans la List posi
     *
     * @return liste arete
     */
    public List<List<GeoPosition>> CreerArete() {
        // Crée des arêtes à partir de la liste des positions
        List<List<GeoPosition>> edges = new ArrayList<>();
        //récupère 2 valeurs consécutives pour créer une arete
        for (int i = 0; i < posi.size() - 1; i += 2) {
            edges.add(posi.subList(i, i + 2));
        }
        //cas impaire
        if (posi.size() % 2 != 0) {
            List<GeoPosition> d_edge = new ArrayList<>();
            d_edge.add(posi.get(posi.size() - 1));
            edges.add(d_edge);
        }
        return edges;
    }

    public void filtre() {
        // Filtre les positions et les arêtes en fonction de critères de recherche
        String recherche = rechercheAe.getText().toLowerCase();
        String hauteurText = rechercheHauteur.getText();
        int hauteurFiltre;
        if (hauteurText.isEmpty()) {
            hauteurFiltre = -1;
        } else {
            hauteurFiltre = Integer.parseInt(hauteurText);
        }
        //stock les valeurs filtrer
        List<GeoPosition> Posi_filtrer = new ArrayList<>();
        //stock les aretes créer
        List<List<GeoPosition>> Aretes = CreerArete();

        // Filtrer par nom
        if (rechercheAe != null) {
            for (String volItem : vol) {
                if (volItem.toLowerCase().contains(recherche)) {
                    //volCord prend les items correspondant pour les utiliser dans la mise à jour de la map                
                    GeoPosition position = volCoord.get(volItem);
                    if (position != null) {
                        //ajout filtre
                        Posi_filtrer.add(position);
                    }
                }
            }
            //meme chose mais avec le code
            for (String codeItem : code) {
                if (codeItem.toLowerCase().contains(recherche)) {
                    GeoPosition position = volCoord.get(codeItem);
                    if (position != null && !Posi_filtrer.contains(position)) {
                        Posi_filtrer.add(position);
                    }
                }
            }
        }

        // Filtrer par hauteur
        List<Painter<JXMapViewer>> painters = new ArrayList<>();
        List<Integer> hauteurs = Arete.getHauteurs();

        for (int i = 0; i < Aretes.size(); i++) {
            //récupère la liste des aretes
            List<GeoPosition> edge = Aretes.get(i);
            int hauteur;

            // Détermine la hauteur 
            if (i < hauteurs.size()) {
                hauteur = hauteurs.get(i);
            } else {
                hauteur = 1;  // si aucune hauteur déterminé , alors on l'attribut de base à 1
            }

            // Filtre les arêtes en fonction de la hauteur
            if (hauteurFiltre == -1 || hauteur == hauteurFiltre) {
                // Vérifie si l'une des extrémités de l'arête est dans Posi_filtrer
                if (Posi_filtrer.contains(edge.get(0)) || (edge.size() > 1 && Posi_filtrer.contains(edge.get(1)))) {
                    Color color = couleurMap.getOrDefault(edge, Color.RED);
                    //création arete via routepainter
                    painters.add(new RoutePainter(edge, color));
                }
            }
            // Filtre les arêtes en fonction des positions filtrées (en fonction des noms/codes)
            if (Posi_filtrer.contains(edge.get(0)) || (edge.size() > 1 && Posi_filtrer.contains(edge.get(1)))) {
                // Vérifie si la hauteur de l'arête correspond à hauteurFiltre, ou si hauteurFiltre est -1
                if (hauteurFiltre == -1 || hauteur == hauteurFiltre) {
                    // Récupère la couleur de l'arête depuis couleurMap, RED = couleur par défaut
                    Color color = couleurMap.getOrDefault(edge, Color.RED);
                    painters.add(new RoutePainter(edge, color));
                }
            }

        }

        // Met à jour les waypoints sur la carte
        MajWaypoints();
        WaypointPainter<Waypoint> AeroportPainter = new WaypointPainter<>();
        AeroportPainter.setWaypoints(waypoints);
        painters.add(AeroportPainter);
        mapViewer.setOverlayPainter(new CompoundPainter<>(painters));
    }

    /**
     * Méthode qui permet l'ajout des waypoints ou aéroports en fonction de la
     * map coordonnee
     */
    public void ajouterWaypoints() {
        if (coordonnee == null || coordonnee.isEmpty()) {
            return;
        }
        for (Map.Entry<Double, Double> entry : coordonnee.entrySet()) {
            // Récupère la clé (latitude)
            double lat = entry.getKey();
            // Récupère la valeur (longitude)
            double lon = entry.getValue();
            // création des coordonnées géographiques du waypoints
            GeoPosition position = new GeoPosition(lat, lon);
            // Crée un nouveau waypoint avec cette position et l'ajoute à la liste des waypoints
            waypoints.add(new DefaultWaypoint(position));
        }
    }

    /**
     * Liste des getters/Setters
     *
     * @return
     */
    public Map<Double, Double> getCoordonnee() {
        return coordonnee;
    }

    public void setCoordonnee(Map<Double, Double> coordonnee) {
        this.coordonnee = coordonnee;
    }

    public JXMapViewer getMapViewer() {
        return mapViewer;
    }

    public JFrame getFrame() {
        return frame;
    }

    public void setFrame(JFrame frame) {
        this.frame = frame;
    }

    public void setMapViewer(JXMapViewer mapViewer) {
        this.mapViewer = mapViewer;
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

    public List<GeoPosition> getPosi() {
        return posi;
    }

    public void setPosi(List<GeoPosition> posi) {
        this.posi = posi;
    }

    public Map<String, GeoPosition> getPositionsMap() {
        return positionsMap;
    }

    public void setPositionsMap(Map<String, GeoPosition> positionsMap) {
        this.positionsMap = positionsMap;
    }

    public Map<String, GeoPosition> getvolCoord() {
        return volCoord;
    }

    public void setVolCoordinates(Map<String, GeoPosition> volCoord) {
        this.volCoord = volCoord;
    }

    public int getMarge_securite() {
        return marge_securite;
    }

    public void setMarge_securite(int marge_securite) {
        this.marge_securite = marge_securite;
    }
}
