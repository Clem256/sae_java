package Map;

import java.awt.*;
import java.awt.event.*;
import java.text.*;
import java.util.*;
import java.util.regex.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;

public class ListeVol extends JFrame {

    private ArrayList<String> val_vol;
    private TableRowSorter<DefaultTableModel> trie;
    private JButton nom, ae1, ae2, h, durée, heureDepart, heureArrivée;
    private final int[] colonneFiltre = {0};
    private final JTextField val_recherche = new JTextField();
    private DefaultTableModel Modele_t;
    JTable table_vol;
    private Color couleurBase;
    /**
     * Constructeur pour get des valeurs
     */
    public ListeVol() {

    }

    /**
     * Constructeur , qui ignitie la JTable via ModeleTable et le placement des
     * boutons via init(). Un addDocumentListener est également présent pour
     * mettre à jour les valeurs en fonction du filtre. Un MouseAdapter est
     * présent pour réinitialiser le contenue de val_recherche.
     *
     * @param val_vol
     */
    public ListeVol(ArrayList<String> val_vol) {
        this.val_vol = val_vol;
        init();
        ModeleTable();
        table_vol = new JTable(Modele_t);
        JScrollPane scroll = new JScrollPane(table_vol);

        trie = new TableRowSorter<>(Modele_t);
        table_vol.setRowSorter(trie);
        //mise à jour des valeurs pour le filtre
        val_recherche.getDocument().addDocumentListener(new DocumentListener() {
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
        //lors clic , enlever tout les éléments de val_recherche
        val_recherche.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                val_recherche.setText("");
            }
        });
        add(scroll, BorderLayout.CENTER);
        pack();
    }

    /**
     * Méthode qui permet l'initialisation de l'interface graphique , c'est à
     * dire le tableau avec les boutons et le JTextField. Cette méthode appelera
     * action , qui permet de modifier la valeur de colonneFiltre , pour que le
     * filtre soit fonctionnelle en fonction des boutons cliquer.
     */
    private void init() {
        nom = new JButton("Filtre via nom");
        ae1 = new JButton("Filtre via Depart");
        ae2 = new JButton("Filtre via arrivée");
        h = new JButton("Filtre via une horaire");
        durée = new JButton("Filtre via durée");
        heureDepart = new JButton("Filtre via heure départ");
        heureArrivée = new JButton("Filtre via heure arrivée");

        setTitle("Liste des vols");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1000, 1000);
        setLayout(new BorderLayout());

        JPanel filtrePanel = new JPanel();
        filtrePanel.setLayout(new GridBagLayout());
        GridBagConstraints cont = new GridBagConstraints();
        cont.fill = GridBagConstraints.HORIZONTAL;
        cont.gridx = 0;
        cont.gridy = 0;
        filtrePanel.add(nom, cont);
        cont.gridx = 1;
        filtrePanel.add(ae1, cont);
        cont.gridx = 2;
        filtrePanel.add(ae2, cont);
        cont.gridx = 3;
        filtrePanel.add(h, cont);
        cont.gridx = 4;
        filtrePanel.add(durée, cont);
        cont.gridx = 5;
        filtrePanel.add(heureArrivée, cont);
        cont.gridx = 6;
        filtrePanel.add(heureDepart, cont);
        cont.gridx = 0;
        cont.gridy = 1;
        cont.gridwidth = 7;
        filtrePanel.add(val_recherche, cont);
        couleurBase = nom.getBackground();
        add(filtrePanel, BorderLayout.NORTH);
        setVisible(true);

        action();
    }

    /**
     * Méthode qui permet de créer le premier modèle pour la première table.
     * Elle va créer un modèle pour la table , avec un tableau de String
     * possédant le nom des colonnes et un tableau double d'object avec les
     * valeurs issus de val_vol sur 8 colonnes
     */
    private void ModeleTable() {
        int size = val_vol.size();
        int nb_ligne = (int) Math.ceil(size / 6.0);
        Object[][] val = new Object[nb_ligne][8];
        int ligne = 0;
        int col = 0;

        for (int i = 0; i < val_vol.size(); i++) {
            val[ligne][col] = val_vol.get(i);
            col++;

            if (col >= 6) {
                col = 0;
                ligne++;
            }
        }

        for (int i = 0; i < nb_ligne; i++) {
            String heure = (String) val[i][3];
            String minute = (String) val[i][4];
            String duree = (String) val[i][5];
            //deux dernière ligne
            val[i][6] = heure + ":" + minute;
            val[i][7] = CalculArrivée(heure, minute, duree);
        }
        String[] nom_colonne = {"Nom vol", "Aéroport départ", "Aéroport arrivée", "Heure", "Minute", "Durée", "Heure départ", "Heure Fin"};
        Modele_t = new DefaultTableModel(val, nom_colonne);
    }

    /**
     * Méthode qui permet de calculer l'heure d'arrivée , avec l'aide de la
     * classe Calendar.Elle va permettre de calculer l'heure d'arrivé , en
     * utilisant la classe Calendar pour évitée les erreurs du au passage entre
     * les heures et minutes.
     *
     * @param heure
     * @param minute
     * @param duree
     * @return
     */
    public String CalculArrivée(String heure, String minute, String duree) {
        //format date
        SimpleDateFormat formatDate = new SimpleDateFormat("HH:mm");
        try {
            Date TempsDépart = formatDate.parse(heure + ":" + minute);
            Calendar calendrier = Calendar.getInstance();
            calendrier.setTime(TempsDépart);
            int duréeMin = Integer.parseInt(duree);
            //calcul pour le temps de fin , en rajoutant les minutes
            calendrier.add(Calendar.MINUTE, duréeMin);
            return formatDate.format(calendrier.getTime());
        } catch (ParseException e) {
            return "Temps invalide";
        }
    }

    /**
     * Méthode qui permet de mettre à jour la JTable avec de nouvelle Donnée.
     * Cette nouvelle valeur est donnée dans AffichageVolAe , pour changer la
     * valeur lors du traitement d'un autre fichier. Elle va créer un modèle
     * pour la table , avec un tableau de String possédant le nom des colonnes
     * et un tableau double d'object avec les valeurs issus de val_vol sur 8
     * colonnes
     *
     * @param nouvelleDonnée
     */
    public void MajModeleTable(ArrayList<String> nouvelleDonnée) {
        this.val_vol = nouvelleDonnée;
        int size = val_vol.size();
        int nb_ligne = (int) Math.ceil(size / 6.0);
        Object[][] val = new Object[nb_ligne][8];
        int ligne = 0;
        int col = 0;

        for (int i = 0; i < val_vol.size(); i++) {
            val[ligne][col] = val_vol.get(i);
            col++;

            if (col >= 6) {
                col = 0;
                ligne++;
            }
        }

        for (int i = 0; i < nb_ligne; i++) {
            String heure = (String) val[i][3];
            String minute = (String) val[i][4];
            String duree = (String) val[i][5];
            //colonne 6 et 7
            val[i][6] = heure + ":" + minute;
            val[i][7] = CalculArrivée(heure, minute, duree);
        }
        //méthode qui définit les données du modele du JTable
        Modele_t.setDataVector(val, new Object[]{"Nom vol", "Aéroport départ", "Aéroport arrivée", "Heure", "Minute", "Durée", "Heure départ", "Heure Fin"});
    }

    /**
     * Méthode qui permet de filtrer le Jtable en fonction d'un patern Elle va
     * récupérer la valeur de val_recherche et etre appeler via un
     * DocumentListener. Elle va utiliser colonneFiltre pour savoir qu'elle
     * colonne filtrer , dont la valeur est définit dans action()
     */
    public void MajFiltre() {
        String filtre_texte = val_recherche.getText().trim();
        action();
        if (filtre_texte.length() == 0) {
            trie.setRowFilter(null);
        } else {
            try {
                //partie qui permet de donnée les vols si on donne une horaire entre une heure de départ et une heure d'arrivée
                if (colonneFiltre[0] == 3) {
                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                    Date input = sdf.parse(filtre_texte);

                    trie.setRowFilter(new RowFilter<Object, Object>() {
                        @Override
                        // ? = type générique qui permet à la méthode de fonctionner avec n'importe quel type de valeur
                        public boolean include(RowFilter.Entry<? extends Object, ? extends Object> entry) {
                            try {
                                Date depart = sdf.parse(entry.getStringValue(3) + ":" + entry.getStringValue(4));
                                Date arrivée = sdf.parse(entry.getStringValue(7));
                                //true su l'heure donnée est située entre l'heure de départ et d'arrivée
                                return input.after(depart) && input.before(arrivée);
                            } catch (ParseException ex) {
                                return false;
                            }
                        }
                    });
                } else {
                    // regex permet de triée en fonction d'un pattern , sachant que la pattern ira de gauche à droite
                    String regex = "^" + Pattern.quote(filtre_texte.toLowerCase());
                    //compile string et return pattern
                    Pattern pattern = Pattern.compile(regex);
                    trie.setRowFilter(new RowFilter<Object, Object>() {
                        @Override
                        // ? = type générique qui permet à la méthode de fonctionner avec n'importe quel type de valeur
                        public boolean include(RowFilter.Entry<? extends Object, ? extends Object> entry) {
                            for (int col : colonneFiltre) {
                                //recherche valeur présente dans le paterne
                                if (pattern.matcher(entry.getStringValue(col).toLowerCase()).find()) {
                                    return true;
                                }
                            }
                            return false;
                        }
                    });
                }
            } catch (ParseException e) {
                trie.setRowFilter(null);
            }
        }
    }

    /**
     * Méthode qui permet de changer la valeur de colonneFiltre pour la classe
     * MajFiltre , tout en changeant la couleur du bouton choisis Si le bouton
     * est choisi , alors celui-ci prend la couleur rouge et tous les autres
     * récupères leur couleur d'origine
     */
    public void action() {
        //pour chaque bouton , change la valeur de colonneFiltre pour adapter à une colonne et change la couleur de tout les boutons
        nom.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //active le filtre sur la première colonne et change la couleur du bouton cliquée , idem pour les suivants mais avec des boutons 
                //différents
                colonneFiltre[0] = 0;
                nom.setBackground(Color.red);
                ae1.setBackground(couleurBase);
                ae2.setBackground(couleurBase);
                h.setBackground(couleurBase);
                durée.setBackground(couleurBase);
                heureArrivée.setBackground(couleurBase);
                heureDepart.setBackground(couleurBase);

            }
        });
        ae1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                colonneFiltre[0] = 1;
                nom.setBackground(couleurBase);
                ae1.setBackground(Color.red);
                ae2.setBackground(couleurBase);
                h.setBackground(couleurBase);
                durée.setBackground(couleurBase);
                heureArrivée.setBackground(couleurBase);
                heureDepart.setBackground(couleurBase);
            }
        });
        ae2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                colonneFiltre[0] = 2;
                nom.setBackground(couleurBase);
                ae1.setBackground(couleurBase);
                ae2.setBackground(Color.red);
                h.setBackground(couleurBase);
                durée.setBackground(couleurBase);
                heureArrivée.setBackground(couleurBase);
                heureDepart.setBackground(couleurBase);
            }
        });
        h.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                colonneFiltre[0] = 3;
                nom.setBackground(couleurBase);
                ae1.setBackground(couleurBase);
                ae2.setBackground(couleurBase);
                h.setBackground(Color.red);
                durée.setBackground(couleurBase);
                heureArrivée.setBackground(couleurBase);
                heureDepart.setBackground(couleurBase);
                MajFiltre();
            }
        });
        durée.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                colonneFiltre[0] = 5;
                nom.setBackground(couleurBase);
                ae1.setBackground(couleurBase);
                ae2.setBackground(couleurBase);
                h.setBackground(couleurBase);
                durée.setBackground(Color.red);
                heureArrivée.setBackground(couleurBase);
                heureDepart.setBackground(couleurBase);
            }
        });
        heureArrivée.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                colonneFiltre[0] = 6;
                nom.setBackground(couleurBase);
                ae1.setBackground(couleurBase);
                ae2.setBackground(couleurBase);
                h.setBackground(couleurBase);
                durée.setBackground(couleurBase);
                heureArrivée.setBackground(Color.red);
                heureDepart.setBackground(couleurBase);
            }
        });
        heureDepart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                colonneFiltre[0] = 7;
                nom.setBackground(couleurBase);
                ae1.setBackground(couleurBase);
                ae2.setBackground(couleurBase);
                h.setBackground(couleurBase);
                durée.setBackground(couleurBase);
                heureDepart.setBackground(couleurBase);
                heureDepart.setBackground(Color.red);
            }
        });
        //couleur des bordures de base et changement des couleurs des boutons en eux meme et pas des contours
        nom.setOpaque(true);
        nom.setBorderPainted(false);
        ae1.setOpaque(true);
        ae1.setBorderPainted(false);
        ae2.setOpaque(true);
        ae2.setBorderPainted(false);
        h.setOpaque(true);
        h.setBorderPainted(false);
        durée.setOpaque(true);
        durée.setBorderPainted(false);
        heureDepart.setOpaque(true);
        heureDepart.setBorderPainted(false);
        heureArrivée.setOpaque(true);
        heureArrivée.setBorderPainted(false);
    }
    public void setTable_vol(JTable table_vol) {
        this.table_vol = table_vol;
    }

    /**
     * Méthode qui permet d'appelée la mise à jour de la JTable table_vol à
     * partir d'une nouvelle donnée
     *
     * @param NouvDonnée
     */
    public void MajTable(ArrayList<String> NouvDonnée) {
        MajModeleTable(NouvDonnée);
        trie.setModel(Modele_t);
    }

    /**
     * Méthode qui permet de mettre à jour la valeur de val_vol
     *
     * @param newValVol
     */
    public void MajValVol(ArrayList<String> newValVol) {
        this.val_vol = newValVol;
        MajModeleTable(this.val_vol);
        //signale une modification de la table au modèle
        Modele_t.fireTableDataChanged();
    }

}
