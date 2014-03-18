package game.joueurs;

import java.util.ArrayList;
import java.util.Collections;

import tools.IO;

// TODO: améliorer: durabilité. moyen de préserver joueurs plus connectés

public class ListeJoueur {
	
	// private ArrayList<Joueur> joueursConnexion; + joueur Partie?
	private ArrayList<Joueur> joueurs; // still connected
	private boolean locked;
	private final Integer nbMax;
	private Joueur[] ordreJoueurs;
	// liste figée des joueurs (onles gardes, mais c'est pas dans elle qu'on
	// écrit

	
	// MAYBE Scores? here?

	public synchronized void addJoueur(Joueur j) {
		// TODO: check capacity inside
		joueurs.add(j);
	}

	public synchronized void removeJoueur(Joueur j) {
		joueurs.remove(j);
	}

	public ListeJoueur(Integer nbMax) {
		super();
		this.joueurs = new ArrayList<>();
		this.locked = false;
		this.nbMax = nbMax;
	}

	public synchronized boolean isLoginDuplicate(String login) {
		for (Joueur j : joueurs) {
			if (j.getUsername().equals(login))
				return true;
		}
		return false;
	}

	public synchronized boolean isLocked() {
		return locked;
	}

	public synchronized boolean isReady() {
		return joueurs.size() == nbMax;
	}

	public synchronized boolean isEmpty() {
		return joueurs.isEmpty();
	}

	public synchronized void figer() {
		// joueursPartie = joueursConnexion.clone();
		// copie partie pour garder liste initiale?
		locked = true;
		Collections.shuffle(joueurs);
		ordreJoueurs = joueurs.toArray(new Joueur[joueurs.size()]);

	}

	public ArrayList<Joueur> getJoueurs() {
		return joueurs;
	}

	public Joueur[] getOrdre() {
		// Checking to do
		return ordreJoueurs;

	}

	public boolean checkStillConnected(Joueur j) {

		return joueurs.contains(j);

	}

	public String toString() {
		// TODO improve
		return joueurs.toString();

	}

	// ferme toutes socket existante, et détrui reste objets
	public synchronized void close() {

		//
		for (Joueur j : joueurs) {
			IO.trace(j.toString());

			j.close();
			// buggée: le close broadcast le message ????
		}
		joueurs.clear(); /// adapt.

	}
	

}
