package game.joueurs;

import java.io.IOException;
import java.io.Serializable;
import java.util.Comparator;

import tools.IO;
import core.Connexion;

// HERE DOC
/**
 * Classe Joueur
 * @author adriean
 *
 */
public class Joueur implements Serializable {

	private static final long serialVersionUID = -7046469152632576540L;

	protected final String username;
	public transient Resultat currentResult; //CHECK

	// on ne conserve pas la connexion, ni le role courrant
	protected transient Connexion connexion;
	protected transient Role roleCourrant;

	// TODO: autres variables à créer!

	public Joueur(Connexion client, String login) throws IOException {
		connexion = client;
		username = login;
		currentResult = new Resultat();

		roleCourrant = Role.indéterminé;
	}

	public String toString() {
		return "Joueur:" + username
		// +" [host:"+this.getHost()+"]"
		;
	}

	// GETTER/SETTER
	public String getUsername() {
		return this.username;
	}

	public Role getRoleCourrant() {
		return roleCourrant;
	}

	public void setRoleCourrant(Role roleCourrant) {
		this.roleCourrant = roleCourrant;
	}

	// SEND, READ, and Close
	public  void send(String command) {
		try {
			connexion.send(command);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// ASK? is getConnexion a good practice?

	public  String readCommand() throws IOException {
		return connexion.getCommand();
	}

	public void close() {
		try {
			connexion.close();
			IO.trace("Connexion du joueur " + this.username
					+ " viens d'etre fermé");
			// TODO Trace. (faire un level d'importance?)

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	// SEE synchronied: ask when realy usefull?

	public Integer getScore() {
		return currentResult.score;
	}

	public void addScore(Integer score) {
		currentResult.addScore(score);
	}

	public void setFinalPosition(Integer pos) {
		currentResult.setPosition(pos);
	}

	public void addMotTrouvé() {
		currentResult.addMotTrouvé();
	}

	public void addFalseSuggestion() {
		currentResult.addFalseSuggestion();
	}

	// Comparators

	public int compareResult(Joueur j) {
		return this.currentResult.compareTo(j.currentResult);
	}

	/**
	 * Comparateur entre joueurs
	 */
	public static Comparator<Joueur> joueurComparateur = new Comparator<Joueur>() {
		@Override
		public int compare(Joueur o1, Joueur o2) {
			return o1.compareResult(o2);
		} // TODO to joueur comparateur
	};

	public void malusCheat(Integer penalty) {
		currentResult.malusCheat(penalty);
	}

}
