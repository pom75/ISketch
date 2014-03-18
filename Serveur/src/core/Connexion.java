package core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;

import tools.IO;

/**
 * Représente une connexion (socket+flux)
 * @author adriean
 *
 */
public class Connexion {

	private Socket socket;
	private BufferedReader inchan;
	private PrintWriter outchan;

	public Connexion(Socket socket, BufferedReader inchan, PrintWriter outchan)
			throws SocketException {
		super();
		this.socket = socket;
		this.inchan = inchan;
		this.outchan = outchan;
		socket.setSoTimeout(0); // cause throw
	}

	public Connexion(Socket s) throws IOException {
		socket = s;
		inchan = new BufferedReader(new InputStreamReader(
				socket.getInputStream()));
		outchan = new PrintWriter(socket.getOutputStream());
		s.setSoTimeout(0); // reset timeout
	}

	/**
	 * Ferme la connexion, en envoyant préalablement un message
	 * @param message le message
	 * @throws IOException
	 */
	public void close(String message) throws IOException {
		outchan.println(message);
		close();

	}

	/**
	 * Ferme la connexion
	 * @throws IOException
	 */
	public void close() throws IOException {
		socket.shutdownInput();
		socket.shutdownOutput();
		// Note :induce IOException pour les writer/reader qui l'utilise, meme
		// si peut éventuellement etre caché par le buffer
		socket.close();
		// Note: socket fermée par fermeture des channels avec close
		IO.traceDebug("Socket fermée");
	}

	/**
	 * Envoi un message à la connexion
	 * @param message le texte à faire parvenir
	 * @throws IOException
	 */
	public void send(String message) throws IOException {

		outchan.print(message + "\n\u0000"); // SEE flash test
		outchan.flush();
		// outchan.println(message);
		// if not autoflush use: outchan.flush();
	}

	/**
	 * Lit une commande sur la connexion
	 * @return la commande envoyée par le client
	 * @throws IOException
	 */
	public String getCommand() throws IOException {
		return inchan.readLine();
	}

}
