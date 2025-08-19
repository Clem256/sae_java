/**
 * classe pour dessiner une arête sur un JXMapViewer.
 */
package Map;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.painter.Painter;
import org.jxmapviewer.viewer.GeoPosition;

/**
 * La classe RoutePainter implémente une interface Painter pour dessiner une arête sur un JXMapViewer.
 */
public class RoutePainter implements Painter<JXMapViewer> {

    private final Color color;
    private final boolean antiAlias = true;
    private final List<GeoPosition> track;

    /**
     * Constructeur Route Painter avec une piste et une couleur.
     * @param track La liste de GeoPositions représentant l'itinéraire.
     * @param color La couleur à utiliser.
     */
    public RoutePainter(List<GeoPosition> track, Color color) {
        this.track = new ArrayList<>(track);
        this.color = color;
    }

    /**
     * Peint la ligne sur un objet Graphics2D donné et sur la carte.
     * @param g L'objet Graphics2D sur lequel peindre.
     * @param map JXMapViewer où l'itinéraire doit être peint.
     * @param w  largeur 
     * @param h  hauteur
     */
    @Override
    public void paint(Graphics2D g, JXMapViewer map, int w, int h) {
        g = (Graphics2D) g.create();
        //retourne un rectangle qui à les coordonnées de viewport
        Rectangle rect = map.getViewportBounds();
        g.translate(-rect.x, -rect.y);
        //applique l'anti-aliasing pour le dessin des lignes
        if (antiAlias) {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        }
        g.setColor(Color.BLACK);
        g.setStroke(new BasicStroke(4));
        drawRoute(g, map);
        g.setColor(color);
        g.setStroke(new BasicStroke(2));
        drawRoute(g, map);
        g.dispose();
    }
    
    /**
     * Dessine l'arête sur l'objet Graphics2D donné avec des positions géographiques sur la carte.
     * @param g L'objet Graphics2D sur lequel dessiner.
     * @param map Le composant JXMapViewer où l'itinéraire doit être dessiné.
     */
    private void drawRoute(Graphics2D g, JXMapViewer map) {
        int lastX = 0;
        int lastY = 0;
        boolean first = true;

        for (GeoPosition gp : track) {
            //récupère la tuile (Tile) sont située à la coordonnée de gp
            Point2D pt = map.getTileFactory().geoToPixel(gp, map.getZoom());

            if (first) {
                first = false;
            } else {
                g.drawLine(lastX, lastY, (int) pt.getX(), (int) pt.getY());
            }

            lastX = (int) pt.getX();
            lastY = (int) pt.getY();
        }
    }
    
    /**
     * Récupère la liste de GeoPositions représentant l'itinéraire.
     * 
     * @return La liste de GeoPositions de l'itinéraire.
     */
    public List<GeoPosition> getTrack() {
        return track;
    }
}
