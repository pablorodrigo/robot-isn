//--------------------------------------------------------------------------------------------------------------------------------------------------------------------
/** @file		Panel.java
 * 	@brief		Panel.java sert � cr�er le panneau graphique de la proglet et � cr�er une d�monstration.
 * 	@author 	Bryan KIRCHENER
 * 	@author 	STS IRIS, Lyc�e Nicolas APPERT, ORVAULT (FRANCE)
 * 	@since		01-2012
 *  @version	0.1
 *  @date		04-2012
 *  
 *  Le fichier Panel.java sert � la cr�ation d'un fen�tre graphique qui se situera dans l'onglet "Proglet progBot".
 *  Il permet �galement de cr�er une d�monstration pour par exemple montrer les possibilit�s de la proglet, ici ce sera une d�tection des murs avec affichage par LED des distances.
 *  
 *  
 *  @todo		Cr�er une fen�tre graphique dans l'onglet "proglet progBot".
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

/** D�finit le panneau graphique de la proglet �progBot� 
 *
 * @see <a href="Panel.java.html">code source</a>
 * @serial exclude
 */
public class Panel extends JPanel
{
	private static final long serialVersionUID = 1L;

	public Panel() {}

  /** D�mo de la proglet. */
 public void start() {
	  
	 	/** Cr�ation des objets dynamiques */
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
