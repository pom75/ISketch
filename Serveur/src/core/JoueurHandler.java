package core;

import game.joueurs.Joueur;
import game.joueurs.Role;

import java.io.IOException;

import tools.IO;
import tools.Protocol;
import core.exceptions.IllegalCommandException;
import core.exceptions.InvalidCommandException;
import core.exceptions.UnknownCommandException;

/**
 * Classe (Thread) qui va gérer un joueur.
 * 
 * @author adriean
 * 
 */
public class JoueurHandler extends Thread {

	// BONUX: DP State...

	final Joueur gamer;
	final String username;
	final Server server;
	final GameManager gm;

	// What else?

	public JoueurHandler(Server server, Joueur gamer) {
		super();
		this.gamer = gamer;
		this.username = gamer.getUsername();
		this.server = server;
		gm = server.getGameManager();

		// TEMP
		this.setDaemon(true);
	}

	// / Run Run Run
	/**
	 * Méthode Principale: boucle infinie à écouter le client, et agir en
	 * conséquence (selon le statut du joueur)
	 * 
	 */
	public void run() {
		try {
			while (!interrupted()) {
				try {
					// Récupère la commande du client
					// Thread bloqué ici tant que pas de réponse du client
					String textCommand = gamer.readCommand();

					// parse
					String[] parsedCommand = Protocol.parseCommand(textCommand,
							gamer.getRoleCourrant());

					// StringBuffer sb = new StringBuffer();
					// for(String i : parsedCommand)
					// sb.append(i).append(":");
					//
					// IO.traceDebug(sb.toString());

					
					
					// Handle la commande recu.
					switch (parsedCommand[0]) {
					
					case "EXIT":
						// vérifie que bon joueur,
						if (!parsedCommand[1].equals(username))
							throw new IllegalCommandException(
									"Nom Joueur Invalide");

						manageExit(true);
						break; // non atteint

					case "PASS":
						gm.tryPass();
						break;

					case "CHEAT":
						gm.notifyCheat(gamer);
						break;

					case "GUESS":
						gm.tryGuess(gamer, parsedCommand[1]);
						break;

					case "SET_LINE":
						try {
							gm.addLigne(Integer.parseInt(parsedCommand[1]),
									Integer.parseInt(parsedCommand[2]),
									Integer.parseInt(parsedCommand[3]),
									Integer.parseInt(parsedCommand[4]));
						} catch (NumberFormatException e) {
							throw new InvalidCommandException(
									"Les arguments doivent être des nombres");
						}

						// 404
						break;

					case "SET_COURBE":
						try {
							gm.addCourbe(Integer.parseInt(parsedCommand[1]),
									Integer.parseInt(parsedCommand[2]),
									Integer.parseInt(parsedCommand[3]),
									Integer.parseInt(parsedCommand[4]),
									Integer.parseInt(parsedCommand[5]),
									Integer.parseInt(parsedCommand[6]),
									Integer.parseInt(parsedCommand[7]),
									Integer.parseInt(parsedCommand[8]));
						} catch (NumberFormatException e) {
							throw new InvalidCommandException(
									"Les arguments doivent être des nombres");
						}

						// 404
						break;

					case "SET_SIZE":

						try {
							gm.setSize(Integer.parseInt(parsedCommand[1]));
						} catch (NumberFormatException e) {
							throw new InvalidCommandException(
									"Les arguments doivent être des nombres");
						}
						break;

					case "SET_COLOR":
						try {
							gm.setColor(Integer.parseInt(parsedCommand[1]),
									Integer.parseInt(parsedCommand[2]),
									Integer.parseInt(parsedCommand[3]));
						} catch (NumberFormatException e) {
							throw new InvalidCommandException(
									"Les arguments doivent être des nombres");
						}
						break;

					case "CLEAR":
						gm.clearDrawing();
						break;

					case "TALK":
						gm.sendTchat(gamer, parsedCommand[1]);
						break;

					default:
						// devrait pas arriver la en théorie.
						throw new UnknownCommandException(
								"Command inconnue, check if upcase");

					}

				} catch (NullPointerException e) {
					// Note: avec Buffered Reader, les endOfFile exception sont
					// cachées (comme toutes les IOExceptions)
					// il faut tester si readline renvoit pas null
					// on préférera utiliser une exception

					// DIS e.printStackTrace();
					IO.trace("Connexion coupéeee");
					manageExit(false);

				} catch (IOException e) {
					IO.traceDebug("IO exception: " + e.getMessage());
					manageExit(false);
				} catch (InvalidCommandException e) {

					IO.traceDebug("Commande invalide reçue:"
							+ gamer.getUsername() + ": " + e.getMessage());
					gamer.send(Protocol.newInvalidCommand(e));

				}
			} // end while

		} catch (ExitException e) {
			// MAYBE : mettre code de gestion directement ici?

			// retire le threads des handler.
			IO.traceDebug("Arret du thread handler courant");
		}
	}

	/**
	 * Gère la sortie du client
	 * @param cleanExit (savoir si on peut envoyer un message) 
	 * @throws ExitException exception qui permettra de sortir de la boucle
	 */
	private synchronized void manageExit(boolean cleanExit)
			throws ExitException {
		// Si jeux non lancé: if (!server.isInGame()) {NOtused?

		// retire liste, et se tue.
		if (cleanExit) {
			server.broadcastJoueurs(Protocol.newExited(gamer));
			gamer.close();
		}
		server.removeJoueur(gamer);

		// Si dessinateur
		if (gamer.getRoleCourrant().equals(Role.dessinateur)) {
			gm.handleDessinateurExit();
		}

		IO.traceDebug("Thread gestionnaire de " + gamer
				+ " s'arrete suite à la déconnexion de ce dernier");
		throw new ExitException();

	}

	/**
	 * Exception Locale, pour gérer les fin de partir. 
	 * (sortir boucle de diverses manières)
	 */
	class ExitException extends Exception {

		private static final long serialVersionUID = 7237200611853588544L;
	}
}
