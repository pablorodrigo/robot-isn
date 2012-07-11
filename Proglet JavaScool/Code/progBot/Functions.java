//--------------------------------------------------------------------------------------------------------------------------------------------------------------------
/** @file		Fonctions.java
 * 	@brief		Fonctions.java sert à déclarer les différentes fonctions qui pourront êtres utilisées par les étudiants
 * 	@author 	Bryan KIRCHENER
 * 	@author 	STS IRIS, Lycée Nicolas APPERT, ORVAULT (FRANCE)
 * 	@since		01-2012
 *  @version	0.1
 *  @date		04-2012
 *  
 *  Le fichier Fonctions.java permet de définir diverses fonctions qui pourront êtres utilisées par la suite par les étudiants travaillant sur la proglet <<progBot>>.
 *  Elle sert également à tester différentes fonctions afin de vérifier la connexion et l'envoie de fichiers au Robot NXT.
 *  
 *  @todo		Finir la connexion par USB
 *  
 */
//--------------------------------------------------------------------------------------------------------------------------------------------------------------------

package org.javascool.proglets.progBot;

import static org.javascool.macros.Macros.*;
import lejos.nxt.ADSensorPort;
import lejos.nxt.ColorSensor;
import lejos.nxt.ColorSensor.Color;
import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.nxt.TouchSensor;
import lejos.nxt.UltrasonicSensor;
import java.io.*;

/**
 * Définit les fonctions pour manipuler la proglet «progBot»
 * 
 * @see <a href="Functions.java.html">code source</a>
 * @serial exclude
 */

public class Functions {

	private static Panel getPane() {
		return getProgletPane();
	}

	public static void setMessage(String text) {
		
	}

	public static void avancer() {
		Motor.A.forward();
		Motor.C.forward();
	}

	public static void reculer() {
		Motor.A.backward();
		Motor.C.backward();
	}

	public static void tournerDroite() {
		Motor.A.stop();
		Motor.C.forward();
	}

	public static void tournerGauche() {
		Motor.A.forward();
		Motor.C.stop();
	}

	public static void arreter() {
		Motor.A.stop();
		Motor.C.stop();
	}

	public static void flasher() {
		String flasher;
		try {
			// Instruction susceptible de provoquer une erreur
			String[] command = { "cmd.exe", "/C", "Start", "nxjflash.bat" };
			Runtime r = Runtime.getRuntime();
			Process p = r.exec(command);
			p.waitFor();

		} catch (Exception e) {
			// Instruction de traitement de l'erreur
			System.out.println("erreur d'execution");
		}

	}
	
	
	public static void renommer() {
		String rename;
		try {
			// Instruction susceptible de provoquer une erreur
			String[] command = { "cmd.exe", "/C", "Start", "C:\\Renommer.bat" };
			Runtime r = Runtime.getRuntime();
			Process p = r.exec(command);
			p.waitFor();

		} catch (Exception e) {
			// Instruction de traitement de l'erreur
			System.out.println("erreur d'execution");
		}

	}

	public static void compiler() {
		String nxjc;
		try {
			// Instruction susceptible de provoquer une erreur
			String[] command = { "cmd.exe", "/C", "Start", "C:\\compilation" };
			Runtime r = Runtime.getRuntime();
			Process p = r.exec(command);
			p.waitFor();

		} catch (Exception e) {
			// Instruction de traitement de l'erreur
			System.out.println("erreur d'execution");
		}
	}

	public static void creerNXJ() {
		String nxjlink;
		try {
			String[] command = { "cmd.exe", "/C", "Start",
					"C:\\créationNXJ.bat" };
			Runtime r = Runtime.getRuntime();
			Process p = r.exec(command);
			p.waitFor();

		} catch (Exception e) {

			System.out.println("erreur d'execution");
		}
	}

	public static void transferer() {
		String nxjupload;
		try {
			String[] command = { "cmd.exe", "/C", "Start", "C:\\upload.bat" };
			Runtime r = Runtime.getRuntime();
			Process p = r.exec(command);
			p.waitFor();

		} catch (Exception e) {

			System.out.println("erreur d'execution");
		}
	}

	public static void browse() {
		String nxjc;
		try {
			// Instruction susceptible de provoquer une erreur
			String[] command = { "cmd.exe", "/C", "Start", "C:\\nxjbrowse.bat" };
			Runtime r = Runtime.getRuntime();
			Process p = r.exec(command);
			p.waitFor();

		} catch (Exception e) {
			// Instruction de traitement de l'erreur
			System.out.println("erreur d'execution");
		}
	}

	public static void upload() {
		File fTest = new File("C:\\");
		if (fTest.exists()) {
					
			try {
				String[] command = { "cmd.exe", "/C", "Start",
						"C:\\Renommer.bat" };
				Thread.sleep(2000);
				Runtime r = Runtime.getRuntime();
				Process p = r.exec(command);
				p.waitFor();

			} catch (Exception e) {

				System.out.println("erreur d'execution");
			}

			String nxjc;
			try {
				String[] command = { "cmd.exe", "/C", "Start",
						"C:\\compilation.bat" };
				Thread.sleep(2000);
				Runtime r = Runtime.getRuntime();
				Process p = r.exec(command);
				p.waitFor();

			} catch (Exception e) {

				System.out.println("erreur d'execution");
			}

			String nxjlink;
			try {
				String[] command = { "cmd.exe", "/C", "Start",
						"C:\\créationNXJ.bat" };
				Thread.sleep(3000);
				Runtime r = Runtime.getRuntime();
				Process p = r.exec(command);
				p.waitFor();

			} catch (Exception e) {

				System.out.println("erreur d'execution");
			}

			String nxjupload;
			try {
				String[] command = { "cmd.exe", "/C", "Start", "C:\\upload.bat" };
				Thread.sleep(2000);
				Runtime r = Runtime.getRuntime();
				Process p = r.exec(command);
				p.waitFor();

			} catch (Exception e) {

				System.out.println("erreur d'execution");
			}

		}

	}

}
