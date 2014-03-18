package game.joueurs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.TreeMap;

import tools.IO;

/**
 * Classe stockant les différents, comptes utilisateur.
 * 
 * @author adriean
 * 
 */
public class Comptes implements Serializable {

	private static final long serialVersionUID = -7358347607418941482L;

	TreeMap<String, JoueurEnregistre> comptes;

	// Autres param?
	public Comptes() {
		this.comptes = new TreeMap<>();
	}

	/**
	 * Ajoute un nouveau compte
	 * 
	 * @param j
	 *            le nouveau joueur Enregistré
	 */
	public synchronized void addCompte(JoueurEnregistre j) {
		// CHECK pas déjà dedans

		comptes.put(j.getUsername(), j);
	}

	/**
	 * 
	 * @param username
	 * @return
	 */
	public synchronized JoueurEnregistre getJoueur(String username) {
		return comptes.get(username);
	}

	/**
	 * Renvoi l'ensemble des comptes enregistré.
	 * 
	 * @return
	 */
	public synchronized Collection<JoueurEnregistre> getJoueurs() {
		return comptes.values();
	}

	/**
	 * Teste si nom d'utilisateur est libre
	 * 
	 * @param name
	 *            le nom à test
	 * @return vrai si libre
	 */
	public synchronized boolean isFreeUsername(String name) {
		return !comptes.containsKey(name);
	}

	/**
	 * Sérialise les comptes pour les sauvegarder vers un fichier
	 * 
	 * @param filepath
	 *            chemin vers le fichier
	 * @throws IOException
	 */
	public synchronized void serialize(String filepath) throws IOException {
		File f = new File(filepath);

		// TODO: see comment écraser fichier
		if (f.exists())
			f.delete();

		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(f));
		oos.writeObject(this);
		oos.close();
		IO.trace("Comptes on été sérialisés dans le fichier " + filepath);

	}

	/**
	 * DeSérialise les comptes depuis fichier
	 * 
	 * @param filepath
	 *            chemin vers le fichier de sauvegarde
	 * @throws IOException
	 */
	public synchronized static Comptes deserialize(String filepath)
			throws IOException {

		ObjectInputStream ois = new ObjectInputStream(new FileInputStream(
				filepath));
		Object tmp;
		try {
			tmp = ois.readObject();
		} catch (ClassNotFoundException e) {
			IO.trace("Classe Compte non trouvée....");
			throw new IOException("Problème désérialisation");
		} finally {
			ois.close();
		}

		if (!(tmp instanceof Comptes))
			throw new IOException("Fichier comptient pas un compte");

		return (Comptes) tmp;
	}

	/**
	 * Simple toString
	 */
	@Override
	public synchronized String toString() {
		if (comptes.isEmpty()) {
			return "Aucun Compte!";
		}
		StringBuffer sb = new StringBuffer();
		sb.append("Liste des comptes:\n");
		for (String joueur : comptes.keySet())
			sb.append(joueur).append(", ");
		sb.append("et bientot plus! :) ");

		return sb.toString();

	}

}
