package tools;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Tools to trace. 
 * RENAME
 */
public class IO {

	static private File logfile = null;
	static private BufferedWriter logwriter = null;
	static private boolean printTime = true; //TODO
	static private boolean printThread = true;
	static private boolean debugMode = false;
	
	// Timing
	static private SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

	public static void setLogFile(String file) {
		// EXC

		if (logwriter != null) {
			// ferme le flux

			try {
				logwriter.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		logfile = new File(file);
		// TODO test existance fichier, pour ne pas écraser? (append sinon??)

		try {
			logwriter = new BufferedWriter(new FileWriter(logfile));

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.err.println("Nouvelle Trace dans fichier " + file);

	}
	

	public static void turnOnPrintThread (){
		printThread = true;
	}
	
	public static void turnOffPrintThread (){
		printThread = false;
	}
	
	public static void turnOnPrintTime (){
		printTime = true;
	}
	
	public static void turnOffPrintTime (){
		printTime = false;
	}
	
	public static void turnOnDebugMode (){
		debugMode = true;
	}
	
	public static void turnOffDebugMode (){
		debugMode = false;
	}
	
	// BONUX Overload (+ chaine println?)
	public static void trace(String message) {

		String result = message;
		
		// Ajoute timestamp et thread si demandé
		if(printTime || printThread)
		{	
			StringBuffer sb = new StringBuffer();
			if(printTime){
				sb.append("[").append(sdf.format(Calendar.getInstance().getTime())).append("]");
			}
			if(printThread){
				sb.append("{").append(Thread.currentThread().getName()).append("}  ");
			}
			sb.append(message);
			
			result = sb.toString();
		}
		
	
		// Affiche sortir standard
		System.out.println(result);
		
		// Affiche dang log si ouvert
		if (logwriter != null) {
			try {

				synchronized (logwriter) {
					logwriter.append(message);
					logwriter.append("\n");
				}

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		// TODO voir si ne peut pas plutot flusher
	}

	
	// WONDER: bonne idée? SEARCH annotation debug?
	public static void traceDebug(String message){
	   if (debugMode){
		   trace(message);
	   }
	}
	
	
	
	/**
	 * Ferme le logFile courant
	 */
	public static void endLogTrace() {
		if(logwriter == null ) return;
		
		try {
			synchronized (logwriter) {
				logwriter.flush();
				logwriter.close();
				logwriter = null;
				logfile = null;
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.err.println("Fin de la trace\n");
	}

	/**
	 * Flush logFile courant
	 */
	public static void flushLogTrace() {
		if(logwriter == null ) return;
		
		try {
			synchronized (logwriter) {
				logwriter.flush();
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.err.println("Fin de la trace ");
	}

	public static void main(String[] args) {
		IO.trace("a");
		IO.setLogFile("B");
		IO.turnOffPrintThread();
		IO.trace("B in B");
		IO.endLogTrace();
		IO.trace("C");
	}

}
