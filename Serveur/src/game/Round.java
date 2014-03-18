package game;

import game.graphiques.Couleur;
import game.graphiques.Dessin;
import game.graphiques.Ligne;
import game.graphiques.Spline;
import game.joueurs.Joueur;
import game.joueurs.Role;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import tools.Protocol;

/**
 * Classe représentant une Round d'une Partie d'iSketch
 * @author adriean
 *
 */
public class Round {

	final Joueur dessinateur;
	final ArrayList<Joueur> chercheurs;
	final ArrayList<Joueur> trouveurs;
	Set<Joueur> cheatWarningList;
	ArrayList<Guess> suggestions;

	final Dessin sketch;
	final String mot; // copy

	Couleur currentColor;
	Integer currentSize;

	public Round(Joueur dessinateur, ArrayList<Joueur> chercheurs, String mot) {

		this.dessinateur = dessinateur;
		this.chercheurs = chercheurs;
		this.mot = mot;
		this.trouveurs = new ArrayList<>();
		this.suggestions =  new ArrayList<>();
		this.cheatWarningList = new HashSet<>();
		this.sketch = new Dessin();

		// Valeur défaulrs
		currentColor = new Couleur(); // black
		currentSize = 5;

	}

	public Joueur getDessinateur() {
		return dessinateur;
	}

	public Integer getCurrentSize() {
		return currentSize;
	}

	/**
	 * Détermine si des joueurs n'ont pas encore trouvé
	 * @return vrai s'il y a encore des chercheurs
	 */
	public synchronized boolean stillSearching() {
		return chercheurs.size() != 0;
	}

	public synchronized void setCurrentSize(Integer currentSize) {
		this.currentSize = currentSize;
	}

	public synchronized Couleur getCurrentColor() {
		return currentColor;
	}

	public synchronized void setCurrentColor(int r, int g, int b) {
		this.currentColor = new Couleur(r, g, b);
	}

	public synchronized Ligne addLigne(Integer x1, Integer y1, Integer x2,
			Integer y2) {
		return sketch.addLine(x1, y1, x2, y2, currentSize, currentColor);
	}

	public synchronized Spline addCourbe(Integer x1, Integer y1, Integer x2, Integer y2,
			Integer x3, Integer y3, Integer x4, Integer y4) {
		
		return sketch.addSpline(x1, y1, x2, y2,x3,y3,x4,y4, currentSize, currentColor); 
	}
	
	public synchronized String getDessinCommands() {
		return sketch.toCommand();
	}

	public synchronized void clearDrawing() {
		sketch.clear();
	}
	
	///// Partie
	
	
	public synchronized ArrayList<Joueur> getTrouveurs() {
		return trouveurs;
	}

	/**
	 * Suggestions
	 * 
	 */
	public synchronized void setHasFound(Joueur j) {
		// CHECK + sync
		trouveurs.add(j);
		j.setRoleCourrant(Role.trouveur);
		chercheurs.remove(j);

	}

	public synchronized boolean guess(String essai) {
		return mot.toLowerCase().equals(essai.toLowerCase());
	}

	public synchronized void addFalseGuess(Joueur j, String mot) {
		suggestions.add(new Guess(mot, j.getUsername()));
	}

	public Object getSuggestionCommand() {
		if (suggestions.isEmpty())
			return "";

		StringBuffer sb = new StringBuffer();
		for (Guess g : suggestions) {
			sb.append(Protocol.newGuess(g.guesserName, g.falseGuessed));
			sb.append("\n");
		}
		return sb.toString();
	}

	/**
	 * Cheat!!
	 */

	public synchronized boolean addCheatWarn(Joueur j) {
		return cheatWarningList.add(j);
	}

	public synchronized Integer getNbWarn() {
		return cheatWarningList.size();
	}

	// BONUX: end turn: FIGE, et set la raison victoire

	/**
	 * Classe interne Guess pour stocker les suggestions
	 * @author adriean
	 *
	 */
	class Guess {
		public String falseGuessed;
		public String guesserName;

		// note: pour éviter garder référence sur joueur

		public Guess(String falseGuessed, String guesserName) {
			this.falseGuessed = falseGuessed;
			this.guesserName = guesserName;
		}

	}

	

}
