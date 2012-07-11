package isnbot.activities;

import isnbot.classes.Session;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

/**
 * Classe d'interface permettant � l'utilisateur de la session de choisir son
 * "activit�"
 * 
 * @author Maxime BOUCHENOIRE
 * 
 */
public class MenuActivity extends Activity
{
	// Objet contenant les informations sur la session
	private Session	mSession;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.menu);

		// R�cup�ration des informations de la session, chaque param�tre pass�
		// au constructeur est de la forme suivante :
		// thisIntent.getExtras().getString("ClefExtra")
		final Intent thisIntent = getIntent();
		mSession = (Session) thisIntent.getParcelableExtra("mSession");

		TextView labelRobot = (TextView) findViewById(R.id.tvRobot);
		labelRobot.setText(mSession.getNomRobot().concat(" - ")
				.concat(mSession.getAdrRobot()));

		// Affichage des informations de la session dans le TextView attribut
		TextView labelSession = (TextView) findViewById(R.id.tvSession);
		labelSession.setText(mSession.getUtilisateur() + "_"
				+ mSession.getAnnee() + "_" + mSession.getMois() + "_"
				+ mSession.getJour() + "_" + mSession.getHeures() + "h_"
				+ mSession.getMinutes() + "min_" + mSession.getSecondes()
				+ "sec");
	}

	/**
	 * Slot du bouton "Informations sur le robot" d�marre l'Activity
	 * correspondante
	 * 
	 * @param v
	 *            Bouton "Informations sur le robot"
	 */
	public void onInformationsClicked(View v)
	{
		Intent iInformationsRobotActivity = new Intent(MenuActivity.this,
				InformationsRobotActivity.class);
		iInformationsRobotActivity.putExtra("mSession", mSession);
		startActivity(iInformationsRobotActivity);
	}

	/**
	 * Slot du bouton "Pilotage Standard" d�marre l'Activity correspondante
	 * 
	 * @param v
	 *            Bouton "Pilotage Standard"
	 */
	public void onPilotageStandardClicked(View v)
	{
		Intent iPilotageStandardActivity = new Intent(MenuActivity.this,
				PilotageStandardActivity.class);
		iPilotageStandardActivity.putExtra("mSession", mSession);
		startActivity(iPilotageStandardActivity);
	}

	/**
	 * Slot du bouton "Capteurs", d�marre l'Activity correspondante
	 * 
	 * @param v
	 *            Bouton "Capteurs"
	 */
	public void onCapteursClicked(View v)
	{
		Intent iCapteursActivity = new Intent(MenuActivity.this,
				VisualisationCapteursActivity.class);
		iCapteursActivity.putExtra("mSession", mSession);
		startActivity(iCapteursActivity);
	}

	/**
	 * Slot du bouton "Commandes Vocales", d�marre l'Activity correspondante
	 * 
	 * @param v
	 *            Bouton "Commandes Vocales"
	 */
	public void onCommandesVocalesClicked(View v)
	{
		Intent iVocalActivity = new Intent(MenuActivity.this,
				VocalActivity.class);
		iVocalActivity.putExtra("mSession", mSession);
		startActivity(iVocalActivity);
	}

	/**
	 * Slot du bouton "Execution d'un fichier", d�marre l'Activity
	 * correspondante
	 * 
	 * @param v
	 *            Bouton "Execution d'un fichier"
	 */
	public void onExecutionFichierClicked(View v)
	{
		Intent iExecutionFichierActivity = new Intent(MenuActivity.this,
				ExecutionFichierActivity.class);
		iExecutionFichierActivity.putExtra("mSession", mSession);
		startActivity(iExecutionFichierActivity);
	}

	/**
	 * Slot du bouton "Aide", d�marre l'Activity correspondante
	 * 
	 * @param v
	 *            Bouton "Aide"
	 */
	public void onAideClicked(View v)
	{

	}
}
