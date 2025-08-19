package Map;

import Data.LecteurFichier.LectureVolAeroport;
import Graphique.Coloration.AffichageGrapheColo;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * Cette classe représente celle d'initiation du projet , qui va etre utilisée
 * une seul fois. Elle va faire apparaitre une interface graphique avec 2
 * boutons avec une image dessus pour représenté les 2 parties de l'application
 * (map et graphe)
 *
 * @author User
 */
public final class ChoixPartie extends JFrame {

    JFrame frame = new JFrame();
    JButton b_vol;
    JButton b_colo;
    JPanel p = new JPanel(new GridBagLayout());
    JLabel nom = new JLabel("Bienvenue sur notre application PlaneItude");
    JLabel choix = new JLabel("Choisissez un des 2 modules");

    public ChoixPartie() {
        init();
    }

    /**
     * Méthode d'initiation de la fenetre de départ avec un changement de style
     * La fenetre va initialiser les labels , les 2 boutons et implémenter leur
     * fonctionnaliter
     */
    public void init() {
        try {
            //changement de style
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | UnsupportedLookAndFeelException e) {
        }
        frame.setTitle("PlaneItude");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        Dimension buttonSize = new Dimension(400, 400);
        
        // redimensionnement du bouton pour la partie map
        ImageIcon Iconmap = new ImageIcon("mapVierge.png");
        Image ImgMap = Iconmap.getImage();
        Image imgCorrect = ImgMap.getScaledInstance(buttonSize.width, buttonSize.height, Image.SCALE_SMOOTH);
        ImageIcon IconCorrect = new ImageIcon(imgCorrect);
        b_vol = new JButton("Vol", IconCorrect);

        // redimensionnement du bouton pour la partie graphe
        ImageIcon IconGraphe = new ImageIcon("grapheImage.png");
        Image ImgGraphe = IconGraphe.getImage();
        Image imgCorrectGraphe = ImgGraphe.getScaledInstance(buttonSize.width, buttonSize.height, Image.SCALE_SMOOTH);
        ImageIcon IconCorrect2 = new ImageIcon(imgCorrectGraphe);
        b_colo = new JButton("Coloration", IconCorrect2);

        b_vol.setPreferredSize(buttonSize);
        b_colo.setPreferredSize(buttonSize);
        
        // taille et police des labels
        nom.setFont(new Font("Arial", Font.BOLD, 24));
        choix.setFont(new Font("Arial", Font.BOLD, 18));

        nom.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        choix.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        nom.setForeground(new Color(50, 50, 150));
        choix.setForeground(new Color(50, 50, 150));

        GridBagConstraints cont = new GridBagConstraints();
        cont.fill = GridBagConstraints.BOTH;
        cont.insets = new Insets(10, 10, 10, 10);

        cont.gridx = 0;
        cont.gridy = 0;
        cont.gridwidth = 2;
        p.add(nom, cont);

        cont.gridy = 1;
        cont.gridwidth = 2;
        cont.gridx = 0;
        p.add(choix, cont);

        cont.gridwidth = 1;
        cont.gridy = 2;
        cont.gridx = 0;
        p.add(b_vol, cont);

        cont.gridx = 1;
        p.add(b_colo, cont);

        frame.getContentPane().add(p, BorderLayout.CENTER);
        
        // partie map et vol
        b_vol.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    // vers lecteur aeroport et vol
                    LectureVolAeroport agc = new LectureVolAeroport();
                } catch (UnsupportedLookAndFeelException | IOException ex) {
                    // enregistrement des erreurs "grave" (générée par netbeans)
                    Logger.getLogger(ChoixPartie.class.getName()).log(Level.SEVERE, null, ex);
                }
                frame.dispose();
            }
        });
        
        // partie coloration
        b_colo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // vers coloration
                AffichageGrapheColo agc = new AffichageGrapheColo();
                agc.setVisible(true);
                frame.dispose();
            }
        });

        frame.pack();
        frame.setLocationRelativeTo(null);  // centrer
        frame.setVisible(true);
    }

}
