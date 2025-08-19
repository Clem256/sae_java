package sae.java.modele;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import sae.java.controleur.Fichier;

/**
 * Représente une arête entre deux sommets dans un graphe. Cette classe gère
 * l'affichage des conflits entre vols et avec un filtrage des données.
 */
public class Arete extends JFrame {

    private final Sommet s1;
    private final Sommet s2;
    private final List<Sommet> listeSommet = new ArrayList<>();
    private final List<Point> listePoint = new ArrayList<>();
    private static DefaultTableModel modele_t;
    private static JTable table_conflit;
    private static final JFrame f_conflit = new JFrame();
    private static final Map<String, Integer> hauteurMap = new HashMap<>();
    private static final JButton Vol1 = new JButton("Choix via vol 1");
    private static final JButton Vol2 = new JButton("Choix via vol 2");
    private static final JButton hauteur = new JButton("Choix via hauteur");
    private static final JButton toutesColonnes = new JButton("Filtrer sur toutes les colonnes");
    private static final List<Integer> colonneFiltre = new ArrayList<>();
    private final Color couleurBase;
    private static TableRowSorter<DefaultTableModel> trie;
    private static final JTextField val_rechercher = new JTextField();
    private static int nb_conflit = 0;
    private static JLabel Lnb_conflit;

    //obligatoire pour l'initiation du tableau
    static {
        initTable(); // Initialisation de la table des conflits 
    }

    /**
     * Constructeur de la classe Arete.
     *
     * @param s1 Premier sommet de l'arête
     * @param s2 Deuxième sommet de l'arête
     */
    public Arete(Sommet s1, Sommet s2) {
        nb_conflit = 0;
        this.s1 = s1;
        this.s2 = s2;
        couleurBase = Vol1.getBackground(); // pour sauvegarder les couleurs de base des boutons
        ajoutLigne();
        initActions();
        nb_conflit = 0;
    }

    /**
     * Initialise la table des conflits. Cette méthode crée la manière dont sont
     * placer les éléments de la fenêtre et de la table pour afficher les
     * conflits entre vols.
     */
    private static void initTable() {
        if (f_conflit.getContentPane().getComponentCount() == 0) {
            // Création du panneau 
            JPanel filtre = new JPanel(new GridBagLayout());
            GridBagConstraints cont = new GridBagConstraints();
            cont.fill = GridBagConstraints.BOTH;
            cont.gridx = 0;
            cont.gridy = 0;

            // Configuration de la fenêtre des conflits
            f_conflit.setTitle("Liste des Conflits");
            f_conflit.setSize(800, 600);

            // Ajout des boutons de filtrage
            filtre.add(Vol1, cont);
            cont.gridx = 1;
            filtre.add(Vol2, cont);
            cont.gridx = 2;
            filtre.add(hauteur, cont);
            cont.gridx = 3;
            filtre.add(toutesColonnes, cont);

            // Ajout du champ de recherche
            cont.gridx = 0;
            cont.gridy = 1;
            cont.gridwidth = 4;
            filtre.add(val_rechercher, cont);

            // Création table des conflits et ajout à la fenêtre principale
            table_conflit = new JTable();
            f_conflit.getContentPane().add(filtre, BorderLayout.NORTH);
            JScrollPane scrollPane = new JScrollPane(table_conflit);
            f_conflit.add(scrollPane);

            // Définition de colonnes
            String[] nom_colonne = {"Vol 1", "Vol 2", "Coordonnées 1", "Coordonnées 2", "Hauteur"};
            modele_t = new DefaultTableModel(nom_colonne, 0);
            table_conflit.setModel(modele_t);

            // début trie
            trie = new TableRowSorter<>(modele_t);
            table_conflit.setRowSorter(trie);

            // listener pour le filtre
            val_rechercher.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {
                    MajFiltre();
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    MajFiltre();
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                    MajFiltre();
                }
            });

            //efface valeur de val_rechercher
            val_rechercher.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    super.mouseClicked(e);
                    val_rechercher.setText("");
                }
            });
        } else {
            clearTable(); // Efface la table s'il données déja présentes
        }
    }

    /**
     * Efface toutes les lignes de la table des conflits.
     */
    private static void clearTable() {
        nb_conflit = 0;
        modele_t.setRowCount(0); // Réinitialisation nombre de lignes
        hauteurMap.clear(); // Effacement map
    }

    /**
     * Met à jour le filtre de la table des conflits en fonction du texte entré
     * dans le champ de recherche.
     */
    public static void MajFiltre() {
        String filtre_texte = val_rechercher.getText().trim(); // Récupération du texte dans val_rechercher
        if (filtre_texte.length() == 0) {
            trie.setRowFilter(null); // Si le champ de recherche est vide, retire tout filtre
        } else {
            try {
                // regex permet de triée en fonction d'un pattern , sachant que la pattern ira de gauche à droite
                String regex = "^" + Pattern.quote(filtre_texte.toLowerCase());
                Pattern pattern = Pattern.compile(regex);
                trie.setRowFilter(new RowFilter<Object, Object>() {
                    @Override
                    // ? = type générique qui permet à la méthode de fonctionner avec n'importe quel type de valeur
                    public boolean include(RowFilter.Entry<? extends Object, ? extends Object> entry) {
                        // Vérifie si une des colonnes sélectionnées contient le texte filtré
                        for (int col : colonneFiltre) {
                            if (pattern.matcher(entry.getStringValue(col).toLowerCase()).find()) {
                                return true;
                            }
                        }
                        return false;
                    }
                });
            } catch (Exception e) {
                trie.setRowFilter(null); // En cas d'erreur, retire tout filtre
            }
        }
    }

    /**
     * Initialise les actions des boutons de filtrage pour la table des
     * conflits. Chaque bouton applique un filtre en fonction des colonnes.
     */
    private void initActions() {
        ActionListener vol1Listener = e -> {
            colonneFiltre.clear(); // Efface les filtres précédents
            colonneFiltre.add(0); // Filtre sur la première colonne (vol 1)
            Vol1.setBackground(Color.red);
            Vol2.setBackground(couleurBase);
            hauteur.setBackground(couleurBase);
            toutesColonnes.setBackground(couleurBase);
            MajFiltre();
        };
        ActionListener vol2Listener = e -> {
            colonneFiltre.clear(); // Efface les filtres précédents
            colonneFiltre.add(1); // Filtre sur la deuxième colonne (vol 2)
            Vol1.setBackground(couleurBase);
            Vol2.setBackground(Color.red);
            hauteur.setBackground(couleurBase);
            toutesColonnes.setBackground(couleurBase);
            MajFiltre();
        };

        ActionListener hauteurListener = e -> {
            colonneFiltre.clear(); // Efface les filtres précédents
            colonneFiltre.add(4); // Filtre sur la cinquième colonne (hauteur)
            Vol1.setBackground(couleurBase);
            Vol2.setBackground(couleurBase);
            hauteur.setBackground(Color.red);
            toutesColonnes.setBackground(couleurBase);
            MajFiltre();
        };

        ActionListener toutesColonnesListener = e -> {
            colonneFiltre.clear(); // Efface les filtres précédents
            for (int i = 0; i < table_conflit.getColumnCount(); i++) {
                colonneFiltre.add(i); // Filtre sur toutes les colonnes
            }
            Vol1.setBackground(couleurBase);
            Vol2.setBackground(couleurBase);
            hauteur.setBackground(couleurBase);
            toutesColonnes.setBackground(Color.red);
            MajFiltre();
        };

        // Ajout des listeners aux boutons de filtrage
        Vol1.addActionListener(vol1Listener);
        Vol2.addActionListener(vol2Listener);
        hauteur.addActionListener(hauteurListener);
        toutesColonnes.addActionListener(toutesColonnesListener);

        // Configuration des boutons de filtrage pour pas de bordure et couleur sur les boutons
        Vol1.setOpaque(true);
        Vol1.setBorderPainted(false);
        Vol2.setOpaque(true);
        Vol2.setBorderPainted(false);
        hauteur.setOpaque(true);
        hauteur.setBorderPainted(false);
        toutesColonnes.setOpaque(true);
        toutesColonnes.setBorderPainted(false);
    }

    /**
     * Ajoute une ligne représentant le conflit entre les deux sommets et leurs
     * vols associés. Cette méthode ajoute les informations nécessaires à la
     * table des conflits.
     */
    private void ajoutLigne() {
        // Récupération des vols associés aux sommets
        Vol vol1 = s1.getVolAssocie();
        Vol vol2 = s2.getVolAssocie();

        // Récupération des points d'intersection entre les deux vols
        Point pointInter1 = vol1.getPointInter(vol2);
        Point pointInter2 = vol2.getPointInter(vol1);

        // Ajout des sommets à la liste des sommets de l'arête
        listeSommet.add(s1);
        listeSommet.add(s2);

        // Ajout des points d'intersection à la liste des points de l'arête
        listePoint.add(pointInter1);
        listePoint.add(pointInter2);

        // Génération des clés pour les vols associés
        String clé1 = vol1.toString();
        String clé2 = vol2.toString();

        // Calcul des hauteurs des conflits pour chaque vol
        int hauteur1 = hauteurMap.getOrDefault(clé1, 0) + 1;
        int hauteur2 = hauteurMap.getOrDefault(clé2, 0) + 1;

        // Mise à jour des hauteurs dans la map
        hauteurMap.put(clé1, hauteur1);
        hauteurMap.put(clé2, hauteur2);

        // Sélection de la hauteur maximale pour la ligne de conflit
        Object[] row = {s1, s2, pointInter1, pointInter2, Math.max(hauteur1, hauteur2)};

        // Ajout de la ligne au modèle de la table des conflits
        modele_t.addRow(row);
    }

    /**
     * @return Le premier sommet de l'arête
     */
    public Sommet getS1() {
        return s1;
    }

    /**
     * @return Le deuxième sommet de l'arête
     */
    public Sommet getS2() {
        return s2;
    }

    /**
     * @return Le point de collision entre les deux vols associés aux sommets
     */
    public Point getPointCollision() {
        return s1.volAssocie.getPointInter(s2.getVolAssocie());
    }

    /**
     * @return Le nombre total de conflits détectés
     */
    public int getNb_conflit() {
        return nb_conflit;
    }

    /**
     * Modifie le nombre total de conflits détecté.
     *
     * @param nb_conflit nouveau nombre de conflits
     */
    public void setNb_conflit(int nb_conflit) {
        this.nb_conflit = nb_conflit;
    }

    /**
     * Renvoie une chaîne de caractères représentant l'arête, avec les détails
     * des vols et des points de collision.
     *
     * @return texte pour représenter l'arête en conflit
     */
    @Override
    public String toString() {
        if (s1.volAssocie == null) {
            return String.valueOf(s1.valeurAssocie);
        } else {
            Vol vol1 = s1.getVolAssocie();
            Vol vol2 = s2.getVolAssocie();
            Point pointInter1 = vol1.getPointInter(vol2);
            Point pointInter2 = vol2.getPointInter(vol1);
            nb_conflit++; // Incrémente le nombre total de conflits détectés
            return s1 + " en conflit avec " + s2 + " " + pointInter1 + " " + pointInter2;
        }
    }

    /**
     * Affiche la fenêtre principale des conflits. Cette méthode rend la fenêtre
     * des conflits visible à l'utilisateur.
     */
    public static void afficherTab() {
        f_conflit.setVisible(true);
    }

    /**
     * Retourne la liste des hauteurs de conflit pour les vols dans cette arête.
     *
     * @return La liste des hauteurs de conflit
     */
    public static List<Integer> getHauteurs() {
        List<Integer> hauteurs = new ArrayList<>();
        for (int hauteur : hauteurMap.values()) {
            hauteurs.add(hauteur);
        }
        if (hauteurs.isEmpty()) {
            hauteurs.add(1); // Ajoute des valeurs par défaut si aucune hauteur est présente
            hauteurs.add(2);
        }
        return hauteurs;
    }

    /**
     * Retourne le nombre de lignes de la table.
     *
     * @return Le nombre de lignes
     */
    public static int getNombreDeLignes() {
        return modele_t.getRowCount();
    }
}
