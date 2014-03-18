package game.graphiques;

import tools.IO;

/**
 * Classe représentant les courbes
 * 
 * @author adriean
 * 
 */
public class Spline extends Forme {

	final Integer x1;
	final Integer y1;
	final Integer x2;
	final Integer y2;

	final Integer x3;
	final Integer y3;
	final Integer x4;
	final Integer y4;

	public Spline(Integer x1, Integer y1, Integer x2, Integer y2, Integer x3,
			Integer y3, Integer x4, Integer y4, Integer taille, Couleur couleur) {
		super(taille, couleur);
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
		this.x3 = x3;
		this.y3 = y3;
		this.x4 = x4;
		this.y4 = y4;
	}

	/**
	 * Génère commande protocole décrivant la ligne
	 */
	@Override
	public String toCommand() {
		return "COURBE/" + x1 + "/" + y1 + "/" + x2 + "/" + y2 + "/" + x3 + "/"
				+ y3 + "/" + x4 + "/" + y4 + "/" + couleur.r + "/" + couleur.g
				+ "/" + couleur.b + "/" + taille + "";
	}

	/**
	 * Description textuelle de la ligne
	 */
	@Override
	public String toString() {

		return "Courbe de bézier de (" + x1 + "," + y1 + "), (" + x2 + "," + y2
				+ "), (" + x3 + "," + y3 + "), (" + x4 + "," + y4
				+ ") de couleur (" + couleur.r + "/" + couleur.g + "/"
				+ couleur.b + ") et taille " + taille;
		// Format more readable, but slower
	}

	public static void main(String[] a) {
		// TODO: passer à test JUnit
		IO.trace(new Spline(1, 2, 3, 4, 6, 7, 5, 8, 10, new Couleur())
				.toString());
	}
}
