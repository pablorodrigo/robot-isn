//--------------------------------------------------------------------------------------------------------------------------------------------------------------------
/** @file		Panel.java
 * 	@brief		Panel.java sert à créer le panneau graphique de la proglet et à créer une démonstration.
 * 	@author 	Bryan KIRCHENER
 * 	@author 	STS IRIS, Lycée Nicolas APPERT, ORVAULT (FRANCE)
 * 	@since		01-2012
 *  @version	0.1
 *  @date		04-2012
 *  
 *  Le fichier Panel.java sert à la création d'un fenêtre graphique qui se situera dans l'onglet "Proglet progBot".
 *  Il permet également de créer une démonstration pour par exemple montrer les possibilités de la proglet, ici ce sera une détection des murs avec affichage par LED des distances.
 *  
 *  
 *  @todo		Créer une fenêtre graphique dans l'onglet "proglet progBot".
 *  
 */
//--------------------------------------------------------------------------------------------------------------------------------------------------------------------
package org.javascool.proglets.progBot;
import static org.javascool.macros.Macros.*;
import static org.javascool.proglets.progBot.Functions.*;
import javax.swing.JPanel;
import lejos.nxt.ADSensorPort;
import lejos.nxt.ColorSensor;
import lejos.nxt.ColorSensor.Color;
import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.nxt.TouchSensor;
import lejos.nxt.UltrasonicSensor;
import javax.swing.JPanel;
import javax.swing.JLabel;

/** Définit le panneau graphique de la proglet «progBot» 
 *
 * @see <a href="Panel.java.html">code source</a>
 * @serial exclude
 */
public class Panel extends JPanel
{
	private static final long serialVersionUID = 1L;

	public Panel() {}

  /** Démo de la proglet. */
 public void start() {
	  
	 	/** Création des objets dynamiques */
	 	UltrasonicSensor sonar = new UltrasonicSensor(SensorPort.S3);
		TouchSensor ts = new TouchSensor(SensorPort.S1);
		ColorSensor cs = new ColorSensor(SensorPort.S2);

		
		int light = cs.getLightValue() +10;
		
		
		cs.setFloodlight(0);
		
		Motor.A.forward();
		while (true)
		{
			int distance = sonar.getDistance() ;
			if (distance < 40)
			{
				cs.setFloodlight(0);
				Motor.C.backward();
			}
			else
			{
				Motor.C.forward();
				if ( (distance < 50) )
				{
					cs.setFloodlight(2);
				}
				else
				{
					cs.setFloodlight(1);
				}
			}
		}

  
 }
}
