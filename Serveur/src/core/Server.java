package core;

import game.Dictionnaire;
import game.joueurs.Comptes;
import game.joueurs.Joueur;
import game.joueurs.JoueurEnregistre;
import game.joueurs.ListeJoueur;
import game.joueurs.Role;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicBoolean;

import tools.IO;
import tools.Protocol;
import core.exceptions.InvalidCommandException;

/**
 * Le serveur (thread), classe principale
 * 
 * @author adriean
 * 
 */
public class Server extends Thread {

	// BONUX: see finals?
	private Integer port;
	protected Integer nbMax;
	protected Dictionnaire dico;

	// Threads and connexions
	private ServerSocket sockServ;

	protected LinkedList<Socket> waitingSockets;
	private ConnexionStacker cs;
	private ConnexionHandler ch[];
	private StatServer statisticServer;
	private ArrayList<JoueurHandler> gamerListeners;
	// BONUX: descendre au niveau du game handler quand mise en place rooms

	private AtomicBoolean gameOn;
	private GameManager gm;

	private Comptes comptesJoueurs;
	private ListeJoueur joueurs;
	private ArrayList<Connexion> spectateurs;

	// Autres
	private final static String ACTION_POLICY_STRING = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<cross-domain-policy>"
			+ "<allow-access-from domain=\"*\" to-ports=\"*\" secure=\"false\" />"
			+ "<site-control permitted-cross-domain-policies=\"master-only\" />"
			+ "</cross-domain-policy>\0";

	/**
	 * Simple constructeurs
	 * 
	 * @param opt
	 *            les options crées avec JCommander
	 */
	public Server() {

		this.setName("Server");
		try {

			// OBJECTS
			dico = new Dictionnaire(ASSketchServer.options.dicoFile);
			nbMax = ASSketchServer.options.nbJoueurs;
			joueurs = new ListeJoueur(nbMax);
			spectateurs = new ArrayList<>();

			try {
				this.comptesJoueurs = Comptes
						.deserialize(ASSketchServer.options.comptesFile);
				IO.traceDebug(comptesJoueurs.toString());
			} catch (IOException e) {
				IO.trace("Problème avec fichier de comptes, commence avec fichier vierge");
				this.comptesJoueurs = new Comptes();
			}

			port = ASSketchServer.options.port;
			sockServ = new ServerSocket(port);

			waitingSockets = new LinkedList<Socket>();

			gameOn = new AtomicBoolean(false);

			// THREADS.
			cs = new ConnexionStacker();
			ch = new ConnexionHandler[2]; // BONUX PARAM
			for (int i = 0; i < ch.length; i++) {
				ch[i] = new ConnexionHandler(i);
			}
			gm = new GameManager(this, joueurs, dico);
			gamerListeners = new ArrayList<JoueurHandler>();

			// TODO essayer de le relancer? recatch, tenté de relancer
			statisticServer = new StatServer(comptesJoueurs,
					ASSketchServer.options.portStats);
			
			// mode debug
			if (ASSketchServer.options.debug)
				IO.turnOnDebugMode();
					
			if (ASSketchServer.options.actionMode) {
				IO.trace("Mode Action Script mis en place!");
			}

		} catch (IOException e) {
			// BONUX To improve error handling (ressayer ouvrir Serversocket)
			// ou dico par default
			System.err.println("error:" + e.getMessage());
			// e.printStackTrace();
			System.exit(1);
		}

	} // End of Constructeur

	/** Getters */

	GameManager getGameManager() {
		return gm;
	}

	/**
	 * déterminer si un jeu est en train de tourner
	 * 
	 * @return vrai si trourne
	 */
	public synchronized boolean isInGame() {
		return gameOn.get();
	}

	/** Socket Handling */

	/**
	 * Ajoute une nouvelles socket à la liste d'attente
	 * 
	 * @param s
	 *            la socket à ajouter.
	 */
	public synchronized void addWaitingSocket(Socket s) {
		waitingSockets.add(s);
	}

	/**
	 * Détermine si connnexion en attente
	 * 
	 * @return vrai si en attente
	 */
	synchronized boolean waitingConnexion() {
		return (waitingSockets.size() != 0);
	}

	/**
	 * Retourne une socket en attente
	 * 
	 * @return la premiere socket qui attend
	 */
	public synchronized Socket takeWaitingSocket() {
		return waitingSockets.pollFirst();
	}

	/** Joueurs Handling */

	/**
	 * ajoute un joueur à la liste des joueurs
	 * 
	 * @param j
	 *            le joueur
	 */
	public synchronized void addJoueur(Joueur j) {
		joueurs.addJoueur(j);
	}

	/**
	 * Supprime le joueur de la liste
	 * 
	 * @param j
	 *            le joueur
	 */
	public synchronized void removeJoueur(Joueur j) {
		joueurs.removeJoueur(j);
	}

	/**
	 * Diffuse un message à tous les joueurs
	 * 
	 * @param message
	 *            le message
	 */
	public void broadcastJoueurs(final String message) {
		broadcastJoueursExcept(message, null);
		// leger surcout, mais bon, pas duplication code
	}

	/**
	 * Diffuse un message à tous les joueurs à l'exception d'un
	 * 
	 * @param message
	 *            le message
	 * @param deaf
	 *            le sourd
	 */
	public void broadcastJoueursExcept(final String message, final Joueur deaf) {

		// SEE: Disable Thread submit. (non deterministic)
		// Runnable messenger = new Runnable() {
		// @Override
		// public void run() {

		synchronized (joueurs) {
			// SEE: test if performant or not??
			if (joueurs.isEmpty()) {
				return;
			}

			IO.traceDebug("Liste joueurs broadcasté: " + joueurs.toString());
			for (Joueur j : joueurs.getJoueurs()) {
				if (!j.equals(deaf))
					j.send(message);
			}
		}
		// MAYBE; spec option?
		// Envoi au spectateurs
		synchronized (spectateurs) {

			for (Connexion s : spectateurs) {
				try {
					s.send(message);
				} catch (IOException e) {
					IO.trace("Spectateur déconnecté");
					spectateurs.remove(s);

				}
			}

		}

		IO.trace("Message \"" + message + "\" broadcasté ");

		// }
		// };
		// workers.submit(messenger);

	}

	/**
	 * Ajoute un thread de gestion des joueurs
	 * 
	 * @param j
	 */
	private void addGamerListener(Joueur j) {
		JoueurHandler gl = new JoueurHandler(this, j);
		synchronized (gamerListeners) {
			gamerListeners.add(gl);
			gl.start();

		}

	}

	/**
	 * --------------SERVER RUN ----------
	 * 
	 */

	/**
	 * Méthode run du serveur, lance les différents threads, etc.
	 */
	public void run() {

		cs.setDaemon(true);
		cs.start();
		for (ConnexionHandler chi : ch) {

			chi.setDaemon(true);
			chi.start();
		}
		IO.trace("Lancement des threads gérants les connexions entrantes");

		// CHECK error. si mal initialisé
		statisticServer.start();
		IO.trace("Lancement Server de Statique");

		// Lance partie
		handleGame();

		// BONUX: multiple room en parralèle avec plusieurs GM.
		while (ASSketchServer.options.daemon) {
			// restore environnement pour nouvelle partie!
			// probablement pas la meilleur organisation
			// TODO Check
			for (JoueurHandler j : gamerListeners)
				j.interrupt();
			gamerListeners.clear();
			joueurs = new ListeJoueur(nbMax);
			gm = new GameManager(this, joueurs, dico);

			// Relance la partie
			handleGame();
		}

	}

	/**
	 * Fonction de gestion de partie (factorisation powaa)
	 */
	public void handleGame() {
		IO.trace("Serveur lance une nouvelle partie, en attente de joueurs");
		synchronized (gameOn) {
			try {
				gameOn.wait();
				gameOn.set(true);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				IO.trace("Erreur précédent le lancement de la partie");
			}
		}

		IO.trace("Active game Manager");
		gm.start();

		// Attente de la fin du jeu.
		try {
			gm.join();
			gameOn.set(false);
		} catch (InterruptedException e) {
			IO.trace("Arret inattendu du serveur");
		}
	}

	/**
	 * Lance une sauvegarde des comptes joueurs.
	 */
	public void sauvegardeComptes() {
		try {
			IO.traceDebug("Sauvegarde des comptes");
			comptesJoueurs.serialize(ASSketchServer.options.comptesFile);

		} catch (IOException e) {
			e.printStackTrace();
			IO.trace("Problème à sauvegarde des comptes");
		}
	}

	/**
	 * Stack new connexion
	 */
	class ConnexionStacker extends Thread {

		public ConnexionStacker() {
			this.setName("ConnexionStacker");
		}

		public void run() {
			@SuppressWarnings("resource")
			// close in another thread
			Socket client = new Socket();
			try {
				while (true) {
					client = sockServ.accept();

					// Traitement spécifique Actionscript
					// TEMPORARY (possible leak)
					if (ASSketchServer.options.actionMode) {
						PrintWriter outchan = new PrintWriter(
								client.getOutputStream(), true);

						outchan.print(ACTION_POLICY_STRING);
						// Flash handling
						IO.traceDebug("Putain de policy envoyé automatiquement....");
						outchan.flush();
					}

					IO.trace("Nouvelle connexion incoming mise en attente.");
					synchronized (waitingSockets) {
						addWaitingSocket(client);
						waitingSockets.notify();
					}
				}
			} catch (Throwable t) {
				t.printStackTrace(System.err);
			}

		}
	} // end of ConnexionStacker

	/*******
	 * Handle Connection
	 * 
	 */

	class ConnexionHandler extends Thread {

		private Socket client;
		private BufferedReader inchan;
		private PrintWriter outchan;

		public ConnexionHandler(int i) {
			this.setName("ConnexionHandler<" + i + ">");
		}

		public ConnexionHandler() {
			this(-1);
		}

		// blockant sur la socket traitée en cours,
		// readtimeout pour contourner problème
		// REFACTOR!!!
		public void run() {

			// Q? ou variables instances

			HandleLoop: while (true) {
				synchronized (waitingSockets) {
					while (!waitingConnexion()) {
						try {
							IO.traceDebug("Attente Connexion Socket");
							waitingSockets.wait();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

					IO.trace("Traitement connexion en attente.");
					client = takeWaitingSocket();
				}
				// BONUX handle error.

				// // TRAITEMENT CONNEXION
				try {

					inchan = new BufferedReader(new InputStreamReader(
							client.getInputStream(), Charset.forName("UTF-8")
									.newDecoder()));
					outchan = new PrintWriter(new OutputStreamWriter(
							client.getOutputStream(), Charset.forName("UTF-8")
									.newEncoder()), true); // nota :autoflush

					// Met un timeout à la lecture sur la socket
					client.setSoTimeout(4000);
					// BONUX: temps augmente au fur et à mesure

					// Lecture commande
					String command = null;
					// WARNING: is that the good way to do?
					TryReadTimeout: while (true) {
						try {
							command = inchan.readLine();
							IO.traceDebug(command);
							// TODO: handle raw deconnection

							// Code spécifique pour le client actionscript
							// Handling of ActionsScript ask
							if (ASSketchServer.options.actionMode
									&& command.contains("policy-file-request")) {
								IO.traceDebug("Et un actionscript qui se pointe");
								outchan.print(ACTION_POLICY_STRING);

								// Flash handling
								outchan.flush();
								// read new command
								IO.traceDebug("Policy file envoyé");
								// closeConnexion("BYEBYE");
								// IO.traceDebug("fermé connexion Action");

								continue HandleLoop;
								// se déconnecte immédiatement
							}

							break TryReadTimeout;

						} catch (SocketTimeoutException ste) {
							//
							if (waitingConnexion()) {
								IO.traceDebug("Autre socket en attente, renvoie dormir celle courante.");
								// inchan.close();outchan.close(); // SEE
								// (risque fermer socket)
								// restore?
								synchronized (waitingSockets) {
									waitingSockets.add(client);
									waitingSockets.notify();
								}
								continue HandleLoop;
							} else { // si personne attend
								IO.traceDebug("Et un tour de manège");
								continue TryReadTimeout;
							}
						}
					}
					// NOTA: was intialy a dowhile(false), mais marche pas bien
					// sur avec continue

					// Bonux CONNEXION HANDLING

					// / ----- TRAITEMENT REPONSE
					try {
						String[] tokens = Protocol.parseCommand(command,
								Role.nonconnecté);

						switch (tokens[0]) {
						// Demande de connection
						case "CONNECT":

							synchronized (joueurs) {
								if (!joueurs.isLocked()) {

									String joueurName = tokens[1];

									// Check Login
									while (joueurs.isLoginDuplicate(joueurName)
											|| !comptesJoueurs
													.isFreeUsername(joueurName)) {
										joueurName = joueurName + "'";
										// idéalement compteur (symbol
										// générateur)
										// TODO improved symbol
									}

									// NOTE: à partir de ce stade là, en théorie
									// tout est bon, donc on créer l'objet
									// connexion, puis joueur
									Connexion con = new Connexion(client,
											inchan, outchan);
									Joueur jou = new Joueur(con, joueurName);

									setUpNewJoueur(jou);

								} else {
									IO.trace("Connexion Refusée, jeu plein");
									closeConnexion("GAME_FULL");
								}
							}
							break;

						// Demande d'enregistrement
						case "REGISTER":
							// token 1: user, token2 : mdp
							JoueurEnregistre newJoueur;

							synchronized (comptesJoueurs) {

								if (!comptesJoueurs.isFreeUsername(tokens[1])) {
									IO.trace("Tentative de creation compte pour nom déjà existant");
									closeConnexion(Protocol.newAccessDenied());
									break;
								}

								newJoueur = new JoueurEnregistre(new Connexion(
										client, inchan, outchan), tokens[1],
										tokens[2]);

								comptesJoueurs.addCompte(newJoueur);

								sauvegardeComptes();
							}

							if (!joueurs.isLocked()) {
								setUpNewJoueur(newJoueur);
							} else {
								IO.trace("Connexion Refusée, jeu plein");
								closeConnexion("GAME_FULL");
							}

							break;

						// Demande de login à compte enregistré
						case "LOGIN":
							if (!joueurs.isLocked()) {
								JoueurEnregistre joueurLog = comptesJoueurs
										.getJoueur(tokens[1]);
								if (joueurLog == null) {
									IO.trace("Tentative de login compte non existant");
									closeConnexion(Protocol.newAccessDenied());
									break;
								}
								if (!joueurLog.checkPassword(tokens[2])) {
									IO.trace("Tentative de login avec mot de passe invalide");
									closeConnexion(Protocol.newAccessDenied());
									break;
								}
								// Joueur déjà connecté
								if (joueurs.isLoginDuplicate(joueurLog
										.getUsername())) {
									IO.trace("Utilisateur déjà connecté");
									closeConnexion(Protocol.newAccessDenied());
									break;
								}

								// met à jour la connexion:
								joueurLog.setConnexion(new Connexion(client,
										inchan, outchan));

								setUpNewJoueur(joueurLog);
							} else {
								IO.trace("Connexion Refusée, jeu plein");
								closeConnexion("GAME_FULL");
							}
							break;

						// Demande de "Spectatage"
						case "SPECTATOR":
							Connexion specCo = new Connexion(client, inchan,
									outchan);
							// inchan useless...
							specCo.send("WELCOME/");
							// send catch Back
							specCo.send(gm.getRecap());
							spectateurs.add(specCo);

							break;

						// Pour Toute autre commande protocole valide, mais non
						// acceptable now.
						default:
							throw new InvalidCommandException(
									"C'est pas le moment de demander ceci");
						}

					} catch (InvalidCommandException e) {
						// Utilise close connexion, puisque ne communique pas
						// via objet connexion
						closeConnexion(Protocol.newAccessDenied());
						IO.traceDebug("Kick out Bolos");
					}

					// Fin traitement Connexion reçue en cours

				} catch (SocketException se) {
					IO.trace("Socket deconnectée avant connexion joueur");
					// TODO close connection?? . maybe not up to date
				} catch (NullPointerException e) {
					// TODO ?? rajoute
					IO.traceDebug("Déconnexion barbabre (null)");
					// e.printStackTrace();
				} catch (IOException e) {
					// TODO ?? rajoute
					// e.printStackTrace();
					IO.traceDebug("IOexception");
				}

			}

		}

		/**
		 * Méthode pour factoriser le fermeture connexion avec envoi de message
		 * 
		 * @param message
		 * @throws IOException
		 */
		public void closeConnexion(String message) throws IOException {
			outchan.println(message);
			closeConnexion();

		}

		public void closeConnexion() throws IOException {
			inchan.close();
			outchan.close();
			client.close();
		}

		/**
		 * Gère la connexion d'un nouveau joueur. (et lui envoi toutes les infos
		 * nécessaires)
		 * 
		 * @param jou
		 */
		public void setUpNewJoueur(Joueur jou) {
			IO.trace("Nouveau Joueur Connecté: " + jou.getUsername());

			addGamerListener(jou);

			// Confirme la connexion
			IO.traceDebug("Envoi confirmation connexion");
			jou.send(Protocol.newWelcomed(jou));

			// Informe des autres joueurs
			for (Joueur j : joueurs.getJoueurs()) {
				jou.send(Protocol.newConnected(j));
			}

			// Averti les Autres joueurs du nouveau Joueur
			broadcastJoueursExcept(Protocol.newConnected(jou), jou);

			// Ajoute le joueur à la lisre des joueurs
			joueurs.addJoueur(jou);

			// Lance le jeu si tout le monde est là
			if (joueurs.isReady()) {
				synchronized (gameOn) {
					gameOn.notify();
				}
			}
		}

	} // end of ConnexionHandler

}
