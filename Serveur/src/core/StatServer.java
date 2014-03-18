package core;

import game.joueurs.Comptes;
import game.joueurs.JoueurEnregistre;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import tools.IO;

/**
 * Thread server de statistiques.
 * 
 * @author adriean
 * 
 */
public class StatServer extends Thread {

	private Comptes comptesJoueurs;
	private ServerSocket statSock;
	int port;
	private static final String BAD_REQUEST = "<html>\n<head><title>400 Bad Request</title></head>\n<body bgcolor=\"white\">\n<center><h1>400 Bad Request</h1></center>\n</body>\n</html>";

	private Integer nbStatsSucces;
	private Integer nbStatsWrongRequest;
	private Integer nbStatsDisconnect; // exception

	// Entetes
	private static final String HEADER = "<!DOCTYPE html>\n"
			+ "<html>\n"
			+ "  <head>\n"
			+ "    <meta charset=\"UTF-8\">\n"
			+ "    <title>Statistiques AsSketch/title>\n"
			+ "  <!-- TODO Smaal css  -->\n"
			+ // TODO
			"  </head>\n"
			+ "  <body>\n"
			+ "<h1> Statistiques Des joueurs de AssKecth</h1>\n"
			+ "<table>\n"
			+ "<tr><th>Joueur</th><th>Nb Parties Jouées</th><th>Nb victoires</th><th>Nb Victoires Moyennes</th><th>Score Moyen</th><th>Score Total</th><tr>\n";
	private static final String FOOTER = "</table>\n</body>\n</html>";

	// Beeeurk, dégeullase

	// SEE throw pour
	public StatServer(Comptes c, int port) throws IOException {

		// thread Config
		this.setName("StatsThread");
		this.setDaemon(true);

		this.comptesJoueurs = c;
		this.port = port;
		statSock = new ServerSocket(port);
		// serverSocket: backlog??

		// stats
		nbStatsSucces = 0;
		nbStatsDisconnect = 0;
		nbStatsWrongRequest = 0;

	}

	public void run() {

		while (true) {

			try {
				Socket client = statSock.accept();
				// client.setSoTimeout(1000); // PARAM

				BufferedReader isr = new BufferedReader(new InputStreamReader(
						client.getInputStream()));
				DataOutputStream dos = new DataOutputStream(
						client.getOutputStream());

				String command = isr.readLine();

				// Check commande et agis en conséquence
				if (command.contains("GET") && command.contains("HTTP/1.1")) {

					dos.writeChars(constructPage());
					nbStatsSucces++;
					IO.trace("StatsServer: Stats envoyées");
				} else {
					dos.writeChars(BAD_REQUEST);
					IO.trace("StatsServer: Mauvaise Requete");
					nbStatsWrongRequest++;
					// TODO spam protect
				}

				dos.close();
				isr.close();

			} catch (IOException e) {
				IO.traceDebug("StatsServer: problème connexion/ Relai brisé");
				// TODO stat fausse requete , à améliorer
				nbStatsDisconnect++;

			}

		}

	}

	private String constructPage() {
		// BONUX: look how to create template (haml ou autre??)

		StringBuilder sb = new StringBuilder(HEADER);

		for (JoueurEnregistre j : comptesJoueurs.getJoueurs()) {
			// TODO: sort sort?

			sb.append("<tr><td>").append(j.getUsername()).append("</td>");
			sb.append("<td>").append(j.nbPartiesJouees()).append("</td>");
			sb.append("<td>").append(j.nbVictoires()).append("</td>");
			sb.append("<td>").append(j.nbMoyenVictoire()).append("</td>");
			sb.append("<td>").append(j.scoreMoyen()).append("</td>");
			sb.append("<td>").append(j.scoreTotal()).append("</td>");

			sb.append("</td></tr>\n");
		}

		sb.append(FOOTER);
		return sb.toString();

	}

}
