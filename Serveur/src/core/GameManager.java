package core;

import game.Dictionnaire;
import game.Round;
import game.Tchat;
import game.graphiques.Ligne;
import game.graphiques.Spline;
import game.joueurs.Joueur;
import game.joueurs.JoueurEnregistre;
import game.joueurs.ListeJoueur;
import game.joueurs.Role;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import core.exceptions.IllegalCommandException;

import tools.IO;
import tools.Protocol;

/**
 * Classe (Thread) qui gère une partie.
 * 
 * @author adriean
 * 
 */
public class GameManager extends Thread {

	private Server server;

	private ListeJoueur joueurs;
	private Dictionnaire dico;

	// Timer
	private final ExecutorService timer;
	private final Object endRound;
	// HERE : switch to atomicBoolean (utilisé quand partie annulée)
	// ou enum Etat dans round avec statut fin partie
	private final AtomicBoolean wordFound;
	private final Runnable timerGame;
	private final Runnable timerFound;
	// BONUX: timer inactivité du dessinateur

	private final ArrayList<Tchat> messages;

	// Cheat Warning

	// Rounds
	ArrayList<Round> rounds;
	Round tourCourrant;

	public GameManager(Server server, ListeJoueur joueurs, Dictionnaire dico) {
		this.setName("Game Manager");
		this.server = server;
		this.joueurs = joueurs;
		this.rounds = new ArrayList<>();
		this.tourCourrant = null;
		this.dico = dico;
		this.messages = new ArrayList<>();

		this.timer = Executors.newFixedThreadPool(2);
		this.wordFound = new AtomicBoolean(false); // used as sync var
		this.endRound = new Object();

		this.timerFound = new Runnable() {
			@Override
			public void run() {
				try {
					synchronized (wordFound) {
						wordFound.wait();
					}
					IO.trace("Début timer motTrouvé de Xs");
					TimeUnit.SECONDS.sleep(ASSketchServer.options.tfound);

					IO.trace("Temps écoulé");
					synchronized (endRound) {
						endRound.notify();
					}
				} catch (InterruptedException e) {
					IO.traceDebug("Timer Found Interrompu");
				}
			}
		};
		this.timerGame = new Runnable() {
			@Override
			public void run() {
				try {
					IO.trace("Début timer tour de Xs");
					TimeUnit.SECONDS.sleep(ASSketchServer.options.tround);
					IO.trace("Temps écoulé");
					synchronized (endRound) {
						endRound.notify();
					}
				} catch (InterruptedException e) {
					IO.traceDebug("Timer Round Interrompu");
				}
			}
		};
	}

	/**
	 * Fonction principale du Game Manager. Gère le début de parti, l'ensemble
	 * des round puis la fin de partie.
	 */
	public void run() {

		IO.trace("Démarrage Game Manager");
		Integer i = 1;

		// Détermine role de passage
		joueurs.figer();

		IO.trace("Début de la partie! ");
		IO.trace("Liste des joueurs: " + joueurs);

		for (Joueur dessinateur : joueurs.getOrdre()) {
			// Check si joueur ne s'est pas déconnecté entretemps

			if (joueurs.getJoueurs().size() <= 1) {
				IO.trace("Plus qu'un seul joueur en lice, on arrete");
				// TODO: à faire aussi in game (handle exit général)
				break;
			}

			if (joueurs.checkStillConnected(dessinateur)) {
				IO.trace("Nouveau Round n°" + i + ", dessinateur "
						+ dessinateur);

				manageRound(dessinateur);
				i++;

				// Pause entre parties
				try {
					TimeUnit.SECONDS.sleep(ASSketchServer.options.tpause);
				} catch (InterruptedException e) {
					IO.traceDebug("Jeu interrompu (ne devrait pas avoir lieu)");
				}

			} else {
				IO.trace("Round annulé, " + dessinateur.getUsername()
						+ "ayant quitté le jeu avant son tour");
			}

		}
		// Handle score game
		// ordonne list par Result
		synchronized (joueurs) {
			List<Joueur> finalJoueurs = joueurs.getJoueurs();
			// tri liste
			Collections.sort(finalJoueurs, Joueur.joueurComparateur);
			broadcastJoueurs(Protocol.newScoreGame(finalJoueurs));
			// Mise à jour "position"
			int pos = 1;
			for (Joueur j : finalJoueurs) {
				// BONUX ! handle ex aeqo
				j.setFinalPosition(pos);
				// TODO: save si joueur enregistré
				pos++;

				// Sauvegarde les résultats
				if (j instanceof JoueurEnregistre)
					((JoueurEnregistre) j).saveResult();
			}
			// CHECK !!
			IO.trace("Ordre d'arrivé: " + finalJoueurs.toString());

		}

		broadcastJoueurs("GOODBYE/");
		joueurs.close();

		timer.shutdown();
		IO.trace("Fini de Joueur!!");

	}

	/**
	 * Fonction de gestion d'un tour unique.
	 * 
	 * @param dessinateur
	 *            le joueur qui sera le dessinateur lors du tour présent
	 */
	private void manageRound(Joueur dessinateur) {
		String mot = dico.getWord();
		// set roles
		ArrayList<Joueur> chercheurs = new ArrayList<>();
		for (Joueur j : joueurs.getJoueurs()) {
			if (j.equals(dessinateur)) {
				j.setRoleCourrant(Role.dessinateur);

			} else {
				j.setRoleCourrant(Role.chercheur);
				chercheurs.add(j);
			}
		}

		// Crée nouveau objet tour.
		tourCourrant = new Round(dessinateur, chercheurs, mot);
		rounds.add(tourCourrant);

		// Met en place le timer
		wordFound.set(false);
		Future<?> futureGame = timer.submit(timerGame);
		Future<?> futureFound = timer.submit(timerFound);

		// alt with futures.
		// http://stackoverflow.com/questions/2275443/how-to-timeout-a-thread

		// Avertit début du tour
		dessinateur.send(Protocol.newRoundDesinateur(mot));

		broadcastJoueursExcept(Protocol.newRoundChercheur(dessinateur),
				dessinateur);

		IO.trace("Partie en cours");
		synchronized (endRound) {
			try {
				endRound.wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// TODO traitement supplémentaire
			// get raison fin

		}
		IO.trace("Fin du round");
		// arrete les timer si tournent encore
		futureGame.cancel(true);
		futureFound.cancel(true);

		// remet joueur etat indéterminé
		for (Joueur j : joueurs.getJoueurs()) {
			j.setRoleCourrant(Role.indéterminé);
		}

		// ---------------
		// Gère résultats
		ArrayList<Joueur> trouveurs = tourCourrant.getTrouveurs();

		// compute score
		// TODO handle skip, quit,
		if (!trouveurs.isEmpty()) {

			broadcastJoueurs(Protocol.newEndRound(trouveurs.get(0), mot));

			int i = 0;
			for (Joueur j : trouveurs) {
				j.addScore(10);
				i++;
			}
			dessinateur.addScore(10 + i - 1);
			// compilateur s'occupera de simplifier cela

		} else {
			// no winner

			broadcastJoueurs(Protocol.newEndRound(null, mot));

		}
		broadcastJoueurs(Protocol.newScoreRound(joueurs.getJoueurs()));

	}

	// Transmetteur
	// CHECK; still usefull? (ramener ptetre ceux du niveau serveur?
	/**
	 * Diffuse un message à l'ensemble des joueurs
	 * 
	 * @param message
	 */
	public void broadcastJoueurs(final String message) {
		server.broadcastJoueurs(message);
		// leger surcout, mais bon, pas duplication code
	}

	/**
	 * Diffuse un message à l'ensemble des joueurs à l'exception d'un
	 * 
	 * @param message
	 * @param deaf
	 *            le sourd
	 */
	public void broadcastJoueursExcept(final String message, final Joueur deaf) {
		server.broadcastJoueursExcept(message, deaf);
	}

	// /////////////////////
	// Méthodes ou les game Joueur Handler envoient message!
	// HERE URGENT, synchronize or not?
	/**
	 * Gère l'ajout d'une ligne
	 * 
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 */
	void addLigne(Integer x1, Integer y1,
	// SEE: surchage communication, traite ptetre pas au bon niveau. (mais à
	// vouloir séparer donnée de envoi message)
			Integer x2, Integer y2) {

		Joueur d = tourCourrant.getDessinateur();

		Ligne l = tourCourrant.addLigne(x1, y1, x2, y2);
		broadcastJoueursExcept(Protocol.newLigne(l), d);
		IO.trace("Ligne ajoutée par " + d + ":" + l);
	}

	/**
	 * Gère l'ajout d'une courbe
	 * 
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @param x3
	 * @param y3
	 * @param x4
	 * @param y4
	 */
	public void addCourbe(Integer x1, Integer y1, Integer x2, Integer y2,
			Integer x3, Integer y3, Integer x4, Integer y4) {
		Joueur d = tourCourrant.getDessinateur();

		Spline s = tourCourrant.addCourbe(x1, y1, x2, y2, x3, y3, x4, y4);
		broadcastJoueursExcept(Protocol.newCourbe(s), d);
		IO.trace("Courbe ajoutée par " + d + ":" + s);

	}

	/**
	 * Traite le changement de taille du dessinateur
	 * 
	 * @param taille
	 *            épaisseur du trait
	 */
	void setSize(Integer taille) {
		tourCourrant.setCurrentSize(taille);
		IO.trace("Taille dessin fixée à " + taille);
	}

	/**
	 * Traite le changement de couleur du dessinateur
	 * 
	 * @param r
	 *            rouge
	 * @param g
	 *            vert
	 * @param b
	 *            blue
	 */
	void setColor(Integer r, Integer g, Integer b) {
		tourCourrant.setCurrentColor(r, g, b);
		IO.trace("Taille dessin fixée à " + r + "/" + g + "/" + b + "/");
	}

	/**
	 * Gère la remise à jour du dessin
	 */
	public void clearDrawing() {
		tourCourrant.clearDrawing();
		broadcastJoueurs(Protocol.newCleared());
		IO.trace("Le dessin viens d'être effacé par le dessinateur courant");

	}

	// //// Partie

	/**
	 * Traite un avertissement de la part du joueur spécifié en paramètre
	 * 
	 * @param j
	 */
	void notifyCheat(Joueur j) {
		if (tourCourrant.addCheatWarn(j)) {
			IO.trace("Joueur " + j + " viens de prévenir d'un cheat");
			broadcastJoueurs(Protocol.newWarned(j));
			if (tourCourrant.getNbWarn() >= ASSketchServer.options.nbCheatWarn) {
				IO.trace("Trop c'est trop, on arrete de jouer");
				synchronized (endRound) {
					// sémantique différente que l'énoncé
					// exclu le joueur. (how? on lui envoi quoi.)
					// va rester sur notre sémantique: en rajoutant malus point
					j.malusCheat(ASSketchServer.options.cheatPenalty);
					// TODO: message cheat confirmed au joueurs

					endRound.notify();
				}

			} else {
				IO.trace("Joueur " + j + " avait déjà prévenu d'un cheat");
				// MAYBE: balance mot protocole
			}
		}

	}

	/**
	 * Gère le départ du dessinateur en cours de jeu
	 */
	public void handleDessinateurExit() {

		if (wordFound.get()) {
			IO.trace("Mot trouvé, le jeu continue donc");
		} else {
			synchronized (endRound) {
				endRound.notify();
			}
			IO.trace("Le dessinateur étant parti sans que personne ait trouvé: interrompt round");
			// TODO message ?

		}
		// TODO: handle exit: gerer arret partit si tous le monde gone. (laisse
		// tomber calcul scores)
	}

	/**
	 * Essaye de faire passer le dessinateur
	 * Lance exception Commande illégale si mot déjà trouvé
	 * @throws IllegalCommandException 
	 */
	//TODO: protect more...
	public void tryPass() throws IllegalCommandException {
		if(wordFound.get()){
		IO.trace("Pass refusé du dessinateur");	
			throw new IllegalCommandException("Mot a déjà été trouvé");
		} else {
			
			IO.trace("Dessinateur courrant passe son tour.");
			synchronized (endRound) {
				endRound.notify();
			}
		}
	}

	/**
	 * Traite la suggestion du joueur j et agis en conséquence
	 * 
	 * @param j
	 *            le joueur qui propose
	 * @param mot
	 *            mot proposé
	 */
	void tryGuess(Joueur j, String mot) {
		// CHECK in game statut

		// Si bonne suggestion
		if (tourCourrant.guess(mot)) {
			IO.trace("Guess réussi de " + j + " : " + mot);
			j.addMotTrouvé();
			tourCourrant.setHasFound(j);

			broadcastJoueurs(Protocol.newWordFound(j));

			// vérifie si tous n'ont pas trouvé
			if (!tourCourrant.stillSearching()) {
				synchronized (endRound) {
					endRound.notify();
				}
			} // handle timeout si mot pas déjà trouvé
			else if (!wordFound.get()) {
				synchronized (wordFound) {
					wordFound.set(true);
					wordFound.notify();
				}

				broadcastJoueurs(Protocol
						.newWordFoundTimeout(ASSketchServer.options.tfound));
			}

		} else {
			broadcastJoueurs(Protocol.newGuess(j, mot));
			j.addFalseSuggestion();
			tourCourrant.addFalseGuess(j, mot);
			IO.trace("Guess infructuex de " + j + " : '" + mot + "'");
		}

		IO.trace("Joueur " + j + " suggere '" + mot + "'");

	}

	// /// Tchat and spectateur

	/**
	 * Génère un résumé pour les spectateur arrivant en cours de route
	 * 
	 * @return
	 */
	public String getRecap() {
		StringBuffer sb = new StringBuffer();
		sb.append("WELCOME/\n");

		// Informe des autres joueurs
		for (Joueur j : joueurs.getJoueurs()) {
			sb.append(Protocol.newConnected(j)).append("\n");
		}

		if (tourCourrant != null) {
			// Envoi le score
			sb.append(Protocol.newScoreRound(joueurs.getJoueurs()))
					.append("\n");
			// averti du dessinateur?

			sb.append(Protocol.newRoundChercheur(tourCourrant.getDessinateur()))
					.append("\n");
			sb.append(tourCourrant.getDessinCommands()).append("\n");
			// Liste suggestion tour courrant
			sb.append(tourCourrant.getSuggestionCommand()); // backlash inclu

		}

		// ajoute messages
		for (Tchat m : messages)
			sb.append(m.toCommand()).append("\n");

		return sb.toString();
	}

	/**
	 * Enregistre et diffuse un message du tchat
	 * 
	 * @param auteur
	 * @param message
	 */
	public void sendTchat(Joueur auteur, String message) {
		Tchat tmp = new Tchat(message, auteur);
		messages.add(tmp);
		this.broadcastJoueurs(tmp.toCommand());

	}

}
