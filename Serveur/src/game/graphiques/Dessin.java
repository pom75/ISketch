package game.graphiques;

import java.util.LinkedList;

import tools.IO;

/**
 * Classe Dessin (liste de tracés)
 * 
 * @author adriean
 * 
 */
public class Dessin {

	// SEE Why linked?
	LinkedList<Forme> tracés;

	public Dessin() {

		this.tracés = new LinkedList<>();
	}

	/**
	 * Ajoute une nouvelle ligne au tracé
	 * 
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @param taille
	 * @param couleur
	 * @return
	 */
	public Ligne addLine(Integer x1, Integer y1, Integer x2, Integer y2,
			Integer taille, Couleur couleur) {
		Ligne l = new Ligne(x1, y1, x2, y2, taille, couleur);
		IO.traceDebug("Ligne ajoutée: " + l); // SEE up?
		tracés.add(l);
		return l;
	}

	/**
	 * Ajoute une nouvelle courbe au tracé
	 * 
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @param x3
	 * @param y3
	 * @param x4
	 * @param y4
	 * @param currentSize
	 * @param currentColor
	 * @return
	 */
	public Spline addSpline(Integer x1, Integer y1, Integer x2, Integer y2,
			Integer x3, Integer y3, Integer x4, Integer y4,
			Integer currentSize, Couleur currentColor) {

		Spline s = new Spline(x1, y1, x2, y2, x3, y3, x4, y4, currentSize,
				currentColor);
		IO.traceDebug("Courbe ajoutée: " + s);
		tracés.add(s);
		return s;
	}

	/**
	 * Reset le dessin
	 */
	public void clear() {
		tracés.clear();
	}

	/**
	 * Converti le dessin en liste de commandes pour le représenter
	 * 
	 * @return
	 */
	public String toCommand() {
		StringBuffer sb = new StringBuffer();

		for (Forme f : tracés)
			sb.append(f.toCommand()).append("\n");

		return sb.toString();

	}

	/**
	 * Crée la commande pour décrire la dernière forme ajoutée
	 * 
	 * @return
	 */
	public String getLastCommand() {
		return tracés.peekLast().toCommand();

	}

}
