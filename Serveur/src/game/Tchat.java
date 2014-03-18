package game;

import game.joueurs.Joueur;

import java.util.Calendar;
import java.util.Date;

import tools.Protocol;

/**
 * Classe représenter un message échangé sur le Chat
 * 
 * @author adriean
 * 
 */
public class Tchat {

	public String message;
	public Joueur auteur;
	public Date timestamps;

	/**
	 * Retourne le message sous forme de commande
	 * 
	 * @return
	 */
	public String toCommand() {
		return Protocol.newListen(auteur, message);
	}

	@Override
	public String toString() {
		return "Message \"" + message + "\" de " + auteur.getUsername();
	}

	public Tchat(String message, Joueur auteur) {
		super();
		this.message = message;
		this.auteur = auteur;
		this.timestamps = Calendar.getInstance().getTime();
	}

}
