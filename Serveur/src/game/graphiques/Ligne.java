package game.graphiques;

import tools.IO;

/**
 * Classe représentant les lignes
 * 
 * @author adriean
 * 
 */
public class Ligne extends Forme {

	final Integer x1;
	final Integer y1;
	final Integer x2;
	final Integer y2;

	public Ligne(Integer x1, Integer y1, Integer x2, Integer y2,
			Integer taille, Couleur couleur) {
		super(taille, couleur);
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
	}

	/**
	 * Génère commande protocole décrivant la ligne
	 */
	@Override
	public String toCommand() {
		return "LINE/" + x1 + "/" + y1 + "/" + x2 + "/" + y2 + "/" + couleur.r
				+ "/" + couleur.g + "/" + couleur.b + "/" + taille + "";
	}

	/**
	 * Description textuelle de la ligne
	 */
	@Override
	public String toString() {

		return "Ligne de (" + x1 + "," + y1 + ") à (" + x2 + "," + y2 + ")"
				+ " de couleur (" + couleur.r + "/" + couleur.g + "/"
				+ couleur.b + ") et taille " + taille;
		// Format more readable, but slower
	}

	public static void main(String[] a) {
		// TODO: passer à test JUnit
		IO.trace(new Ligne(1, 2, 3, 4, 6, new Couleur()).toString());
	}
}
