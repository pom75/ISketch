package game.joueurs;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

import core.Connexion;

/**
 *  Classe Joueur enregistré qui étend Joueur en rajoutant mot de passe, et histoirique/resultats
 * @author adriean
 *
 */
public class JoueurEnregistre extends Joueur implements Serializable {

	private static final long serialVersionUID = 6884148533126977784L;

	private String password;
	private ArrayList<Resultat> scores;

	public JoueurEnregistre(Connexion client, String login, String mdp)
			throws IOException {
		super(client, login);
		this.password = mdp;
		this.scores = new ArrayList<>();
	}

	/**
	 * Vérifie si password communiqué corresponda à celui du joueur
	 * @param mdp
	 * @return Vrai si ok
	 */
	public boolean checkPassword(String mdp) {
		return mdp.equals(password);
	}

	/**
	 * Nombre de victoires du joueurs
	 * @return
	 */
	public int nbVictoires() {
		int tot = 0;
		for (Resultat r : scores) {
			if (r.position == 1)
				tot++;
		}
		return tot;
	}

	
	/**
	 * Nombre departies Jouées du joueurs
	 * @return
	 */
	public int nbPartiesJouees() {
		return this.scores.size();
	}

	/**
	 * Nombre moyen de victoire du joueur
	 * @return
	 */
	public double nbMoyenVictoire() {
		double nbPartie = scores.size();
		return nbPartie == 0 ? 0 : nbVictoires() / nbPartie;
	}

	/**
	 * Score total du joueur
	 * 
	 * @return Le score cumulé sur ensemble des parties
	 */
	public int scoreTotal() {

		int tot = 0;
		for (Resultat r : scores) {
			tot = r.score;
		}
		return tot;
	}

	/**
	 * Score Moyen du joueur
	 * @return
	 */
	public double scoreMoyen() {
		double nbPartie = scores.size();
		return nbPartie == 0 ? 0 : scoreTotal() / nbPartie;
	}

	/**
	 * Archive résultats courrant, et réinitialse celui ci.
	 */
	public void saveResult() {
		scores.add(currentResult);
		currentResult = new Resultat();
	}

	/**
	 * Change la connexion du joueur.
	 * @param c
	 */
	public void setConnexion(Connexion c) {
		this.connexion = c;
	}

}
