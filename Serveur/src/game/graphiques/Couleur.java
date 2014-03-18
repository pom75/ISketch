package game.graphiques;

/**
 * Simple Classe couleur format RGB
 * @author 2863379
 *
 */
public class Couleur {

	Integer r;
	Integer g;
	Integer b;

	// BONUX: range checking: 0/255
	// to hsv.

	
	/**
	 * Constructeur Couleur
	 * @param r rouge
	 * @param g vert/green
	 * @param b bleu
	 */
	public Couleur(int r, int g, int b) {

		this.r = r;
		this.g = g;
		this.b = b;
	}

	/**
	 * Constructeur noir par default
	 */
	public Couleur() {
		this(0,0,0);
		
	}

	// RENAME
	/**
	 * Construit le code  #rrggbb
	 * @return chaine de couleur format hexademal
	 */
	public String toHexa() {

		return "#" + Integer.toHexString(0x100 | r).substring(1)
				+ Integer.toHexString(0x100 | g).substring(1)
				+ Integer.toHexString(0x100 | b).substring(1);
		// "#424242";
		// leading 0 fix:
		// http://stackoverflow.com/questions/8689526/integer-to-two-digits-hex-in-java

		// ASK maybe change to a format?
		// Performance: format: 855, stringbuffer: 93
		// return String.format("#%02x%02x%02x", r,g,b);

	}

	/**
	 * To String classique r/g/b/
	 */
	public String toString() {
		return r + "/" + g + "/" + b + "/";
	}

	@SuppressWarnings("unused")
	public static void main(String[] args) {

		long prev_time = System.currentTimeMillis();
		long time;
		Couleur c = new Couleur(25, 42, 230);

		for (int i = 0; i < 100000; i++) {
			String s = c.toHexa();
		}

		time = System.currentTimeMillis() - prev_time;

		System.out.println("temps execution: " + time);

	}

}
