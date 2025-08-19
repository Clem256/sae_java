package Map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;

/**
 * Cette classe va créer le graphe des vols mais ne va pas l'afficher sans demande
 * @author User
 */
public class GrapheStats {

    private Graph graph = new SingleGraph("vol");
    private int nbNoeux = 0;
    private int nbArete = 0;
    private double DegreeMoy;
    private int NbComposants = 0;
    private int Diametre = 0;
    Map<Double, Double> coord = new HashMap<Double, Double>();
    ArrayList<String> code = new ArrayList<String>();
    ArrayList<String> vol = new ArrayList<String>();
    /**
     * Constructeur de la fonction qui permet d'effectuer différentes méthodes pour créer un graphe,
     * avec gestion de certaines erreurs pour les aretes déja existante
     * @param coord
     * @param code
     * @param vol
     */
    public GrapheStats(Map<Double, Double> coord, ArrayList<String> code, ArrayList<String> vol) {
        this.coord = coord;
        this.code = code;
        this.vol = vol;
        try {
            //premier coord.get donne les valeurs pairs de l'array et la 2nd les valeurs impaires
            for (int i = 0; i < code.size(); i++) {
                ajouterAirport(graph, code.get(i), coord.get(i * 2), coord.get(i * 2 + 1));
            }
            ajouterVol(graph, vol);
        } 
        //si l'arete existe déja , alors il la supprime
        catch (org.graphstream.graph.EdgeRejectedException e) {
            supprimerArreteEntreNodes(graph, vol.get(0), vol.get(1));
        }
    }
    /**
     * Méthode qui permet de supprimer une arete entre 2 node.
     * Elle récupère 2 id , qui correspondent à 2 code , et cherche à savoir si une arete est déja présente.
     * Si l'arete (edge) est déja présente , alors la méthode la supprime.
     * @param graph
     * @param id1
     * @param id2 
     */
    public void supprimerArreteEntreNodes(Graph graph, String id1, String id2) {
        Node node1 = graph.getNode(id1);
        Node node2 = graph.getNode(id2);

        if (node1 != null && node2 != null) {
            for (Edge edge : node1.getEdgeSet()) {
                Node source = edge.getSourceNode();
                Node dest = edge.getTargetNode();
                if ((source.equals(node1) && dest.equals(node2)) || (source.equals(node2) && dest.equals(node1))) {
                    graph.removeEdge(edge);
                    return;
                }
            }
        }
    }

    /**
     * Méthode qui ajoute les aéroport avec leur code sur le graphe
     * Les codes sont récupérées via LectureAeVol , auquels ont ajouter un code et 2 coordonnées x et y .
     * Cela va donc permettre de créer des points au bonne coordonnées
     * @param graph
     * @param code
     * @param x
     * @param y
     * @return
     */
    public Graph ajouterAirport(Graph graph, String code, Double x, Double y) {
        graph.addNode(code).setAttribute("ui.label", code);
        graph.getNode(code).setAttribute("latitude", x);
        graph.getNode(code).setAttribute("longitude", y);
        return graph;
    }

    /**
     * Méthode qui permet de créer les différents vols entre différentes nodes
     * Elle récupère les valeurs de 2 aéroports dans la ArrayList vol , pour ensuite récupérée les nodes correspondantes 
     * Si ces nodes existent , elle va ensuite créer une arrete 
     * @param graph
     * @param vol
     */
    public void ajouterVol(Graph graph, ArrayList<String> vol) {
        for (int i = 0; i < vol.size(); i += 2) {
            String lme = vol.get(i);
            String mlh = vol.get(i + 1);
            Node e1 = graph.getNode(lme);
            Node e2 = graph.getNode(mlh);

            if (e1 == null || e2 == null) {
                continue;
            }
            for (Edge ed : e1.getEdgeSet()) {
                if (ed.getSourceNode().equals(e2) || ed.getTargetNode().equals(e2)) {
                    graph.removeEdge(ed);
                    break;
                }
            }

            String edgeId = lme + "-" + mlh;
            graph.addEdge(edgeId, e1, e2);
        }
    }

    /**
     * Méthode pour afficher le graphe
     */
    public void displayGraph() {
        graph.display();
    }

    /**
     * Liste des getters/Setters
     *
     * @return
     */
    public Graph getGraph() {
        return graph;
    }

    public void setGraph(Graph graph) {
        this.graph = graph;
    }

    public int getNbNoeux() {
        return nbNoeux;
    }

    public void setNbNoeux(int nbNoeux) {
        this.nbNoeux = nbNoeux;
    }

    public int getNbArete() {
        return nbArete;
    }

    public void setNbArete(int nbArete) {
        this.nbArete = nbArete;
    }

    public Double getDegreeMoy() {
        return DegreeMoy;
    }

    public void setDegreeMoy(Double DegreeMoy) {
        this.DegreeMoy = DegreeMoy;
    }

    public int getNbComposants() {
        return NbComposants;
    }

    public void setNbComposants(int NbComposants) {
        this.NbComposants = NbComposants;
    }

    public int getDiametre() {
        return Diametre;
    }

    public void setDiametre(int Diametre) {
        this.Diametre = Diametre;
    }

    public Map<Double, Double> getCoord() {
        return coord;
    }

    public void setCoord(Map<Double, Double> coord) {
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
}
