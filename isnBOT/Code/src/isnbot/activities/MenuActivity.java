package isnbot.activities;

import isnbot.classes.Session;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

/**
 * Classe d'interface permettant à l'utilisateur de la session de choisir son
 * "activité"
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

		// Récupération des informations de la session, chaque paramètre passé
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
	 * Slot du bouton "Informations sur le robot" démarre l'Activity
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
	 * Slot du bouton "Pilotage Standard" démarre l'Activity correspondante
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
	 * Slot du bouton "Capteurs", démarre l'Activity correspondante
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
	 * Slot du bouton "Commandes Vocales", démarre l'Activity correspondante
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
	 * Slot du bouton "Execution d'un fichier", démarre l'Activity
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
	 * Slot du bouton "Aide", démarre l'Activity correspondante
	 * 
	 * @param v
	 *            Bouton "Aide"
	 */
	public void onAideClicked(View v)
	{

	}
}
