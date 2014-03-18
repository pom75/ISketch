package game.graphiques;

import java.util.Calendar;
import java.util.Date;

/**
 * Classe abstraite forme
 * 
 * @author adriean
 * 
 */
public abstract class Forme {

	Date dateAjout;
	Integer taille;
	Couleur couleur;

	public Forme(Integer taille, Couleur couleur) {
		this.dateAjout = Calendar.getInstance().getTime();
		this.taille = taille;
		this.couleur = couleur;
	}

	public abstract String toCommand();

	@Override
	public abstract String toString();

}
