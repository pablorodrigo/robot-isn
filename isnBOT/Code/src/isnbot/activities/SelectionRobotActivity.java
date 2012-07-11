package isnbot.activities;

import isnbot.classes.ListeRobotsAdapter;
import isnbot.classes.Robot;

import java.util.ArrayList;
import java.util.Set;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

/**
 * Classe d'interface permettant à l'utilisateur de chercher et/ou sélectionner
 * un robot afin de démarrer une session
 * 
 * @author Maxime BOUCHENOIRE
 * 
 */
public class SelectionRobotActivity extends Activity
{
	private final String		TAG					= "SelectionRobotActivity >>>";
	/** Liste contenant les robots trouvés à proximité */
	private ListView			listeRobotsTrouves;
	/** Liste contenant les robots appareillés au téléphone */
	private ListView			listeRobotsAppaireilles;
	/** Barre de chargement de la recherche de robots à proximité */
	private ProgressBar			pbRechercheRobots;
	/** Barre de chargement de l'activation du Bluetooth */
	private ProgressBar			pbActivationBluetooth;
	/** Représente le Bluetooth du téléphone */
	private BluetoothAdapter	mBluetoothAdapter;
	/** Adapter des robots appareillés */
	private ListeRobotsAdapter	btArrayAdapterAppareilles;
	/** Adapter des robots à proximité */
	private ListeRobotsAdapter	btArrayAdapterTrouves;
	/** Conteneur des robots appareillés */
	private ArrayList<Robot>	mRobotsAppareilles	= new ArrayList<Robot>();
	/** Conteneur des robots trouvés */
	private ArrayList<Robot>	mRobotsTrouves		= new ArrayList<Robot>();
	/** Permet la gestion des évenements liés au Bluetooth */
	private BroadcastReceiver	mBroadcastReceiver;
	private boolean				bBluetoothActive	= false;

	/** Valeur de la classe Bluetooth Robot - Jouet */
	private final int			JOUET_ROBOT			= 2052;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.selection_robot);

		// On initialise et cache les ProgressBar
		this.pbRechercheRobots = (ProgressBar) findViewById(R.id.progressBar1);
		this.pbRechercheRobots.setVisibility(View.GONE);
		this.pbActivationBluetooth = (ProgressBar) findViewById(R.id.pbActivationBluetooth);
		this.pbActivationBluetooth.setVisibility(View.GONE);

		int[] colors =
		{ 0xFF00000F, 0xFF767679, 0xFF77777A }; // red for the example
		// On initialise les listes des robots appairés/trouvés
		this.listeRobotsTrouves = (ListView) findViewById(R.id.devicesfound);
		this.listeRobotsTrouves.setDivider(new GradientDrawable(
				Orientation.LEFT_RIGHT, colors));
		this.listeRobotsTrouves.setDividerHeight(1);
		this.listeRobotsTrouves.setClickable(true);
		this.listeRobotsAppaireilles = (ListView) findViewById(R.id.devicespaired);
		this.listeRobotsAppaireilles.setDivider(new GradientDrawable(
				Orientation.LEFT_RIGHT, colors));
		this.listeRobotsAppaireilles.setDividerHeight(1);
		this.listeRobotsAppaireilles.setClickable(true);

		// On initialise les BluetoothAdapter des robots appairés/trouvés
		this.btArrayAdapterAppareilles = new ListeRobotsAdapter(this,
				mRobotsAppareilles);

		this.btArrayAdapterTrouves = new ListeRobotsAdapter(this,
				mRobotsTrouves);

		activerBluetooth();
		afficherRobotsAppaireilles();

		// BroadcastReceiver déclenché à chaque fois qu'un appareil est détecté
		// par bluetooth lors de la recherche.
		this.mBroadcastReceiver = new BroadcastReceiver()
		{
			public void onReceive(final Context mContext, final Intent mIntent)
			{
				gererEvenement(mIntent);
			}
		};

		// On crée un IntentFilter qui détecte des évènements liés au Bluetooth
		IntentFilter mIntentFilter = new IntentFilter();
		// On détecte la trouvaille d'un robot
		mIntentFilter.addAction(BluetoothDevice.ACTION_FOUND);
		// On détecte l'arrêt de la recherche Bluetooth
		mIntentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		// On détecte le changement d'état du Bluetooth
		mIntentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
		// On active un Receiver utilisant l'IntentFilter paramétré précedemment
		registerReceiver(this.mBroadcastReceiver, mIntentFilter);
	}

	/**
	 * Slot permettant à l'application d'arrêter le Bluetooth sur le téléphone
	 * lorsque l'utilisateur quitte cet écran
	 */
	@Override
	protected void onDestroy()
	{
		// Si le Bluetooth est activé
		if (this.mBluetoothAdapter.isEnabled())
		{
			// On désactive le Bluetooth
			this.mBluetoothAdapter.disable();
		}
		// On désactive le Receiver d'événements Bluetooth
		unregisterReceiver(this.mBroadcastReceiver);
		super.onDestroy();
	}

	/**
	 * Slot du bouton de recherche d'un appareil par bluetooth.
	 * 
	 * @param v
	 *            Bouton de recherche des appareils à proximité
	 */
	public void onBoutonRechercheClicked(final View v)
	{
		// Si le Bluetooth est activé
		if (this.mBluetoothAdapter.isEnabled())
		{
			// On vide la liste des robots trouvés
			this.btArrayAdapterTrouves.clear();
			this.listeRobotsTrouves.setAdapter(this.btArrayAdapterTrouves);
			// On démarre la recherche des robots à proximités
			this.mBluetoothAdapter.startDiscovery();
			// On active la ProgressBar de recherche
			this.pbRechercheRobots.setVisibility(View.VISIBLE);
		}
		else
		// Si le Bluetooth n'est pas activé
		{
			// On affiche un message alertant l'utilisateur que le Bluetooth
			// n'est pas activé
			Toast.makeText(this, "Le Bluetooth n'est pas activé !",
					Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * Active le bluetooth si nécessaire, et empêche l'utilisateur de faire quoi
	 * que ce soit si le téléphone ne dispose pas du bluetooth
	 */
	private void activerBluetooth()
	{
		// On initialise l'attribut représentant le Bluetooth du téléphone
		this.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

		// Si le téléphone supporte le Bluetooth
		if (this.mBluetoothAdapter != null)
		{
			// Si le Bluetooth n'est pas activé
			if (!this.mBluetoothAdapter.isEnabled())
			{
				// On active le Bluetooth
				this.mBluetoothAdapter.enable();
				// On rend visible le bouton de recherche des robots
				this.pbActivationBluetooth.setVisibility(View.VISIBLE);
			}
			else
			// Si le Bluetooth est activé
			{
				// On affiche les robots appairés au téléphone
				afficherRobotsAppaireilles();
			}
		}
		else
		// Si le tépéhone ne supporte pas le Bluetooth
		{
			// On cache le bouton de recherche des robots
			Button btnSearch = (Button) findViewById(R.id.search_button);
			btnSearch.setVisibility(View.GONE);
		}
	}

	/**
	 * Méthode permetant de gérer les évenements liés au Bluetooth
	 * 
	 * @param mIntent
	 *            Intent contenant les informations sur l'évenement
	 */
	private void gererEvenement(final Intent mIntent)
	{
		// On récupère l'action correspondante à l'évennement "reçue"
		final String sAction = mIntent.getAction();
		// Si un appareil Bluetooth à été trouvé
		if (BluetoothDevice.ACTION_FOUND.equals(sAction))
		{
			// On crée l'objet Bluetooth
			final BluetoothDevice mAppareilBluetooth = mIntent
					.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
			// On récupère la classe de l'appareil
			final int nClasseBluetooth = mAppareilBluetooth.getBluetoothClass()
					.getDeviceClass();

			// On récupère le nom du robot
			String sNomRobot = mAppareilBluetooth.getName();
			if (sNomRobot == null)
			{
				sNomRobot = "Robot inconnu";
			}
			// On récupère l'adresse du robot
			final String sAdresseRobot = mAppareilBluetooth.getAddress();
			// On ajoute le robot dans le conteneur des robots trouvés
			this.mRobotsTrouves.add(new Robot(sNomRobot, sAdresseRobot,
					(nClasseBluetooth == JOUET_ROBOT)));

			this.listeRobotsTrouves.setAdapter(this.btArrayAdapterTrouves);
		}
		// Si la recherche est terminée
		if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(sAction))
		{
			// On arrête la ProgressBar de recherche
			this.pbRechercheRobots.setVisibility(View.GONE);
			// Si aucun robot n'a été trouvé
			if (this.mRobotsTrouves.isEmpty())
			{
				// On averti l'utilisateur
				this.mRobotsTrouves
						.add(new Robot(
								"Aucun périphérique à proximité",
								"Vérifiez si le bluetooth des éventuels périphériques/robots à proximité est activé.",
								false));
				this.listeRobotsTrouves.setAdapter(this.btArrayAdapterTrouves);
			}
		}
		// Si l'état du Bluetooth a changé
		if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(sAction))
		{
			// Si le Bluetooth s'est activé
			if (this.mBluetoothAdapter.isEnabled())
			{
				// On cache la ProgressBar d'activation du Bluetooth
				this.pbActivationBluetooth.setVisibility(View.GONE);
				this.bBluetoothActive = true;
				// On affiche les robots appairés
				afficherRobotsAppaireilles();
			}
			else
			{
				this.bBluetoothActive = false;
			}
		}
	}

	/**
	 * Affiche les robots appairés au téléphone dans la listView adéquate.
	 */
	private void afficherRobotsAppaireilles()
	{
		// On initialise le conteneur des robots appairés
		this.btArrayAdapterAppareilles.clear();
		// On vide la liste des robots appairés
		this.listeRobotsAppaireilles.setAdapter(this.btArrayAdapterAppareilles);
		// On récupère les appareils Bluetooth appairés
		final Set<BluetoothDevice> mAppareilsAppaires = this.mBluetoothAdapter
				.getBondedDevices();

		// Si des appareils Bluetooth sont appairés
		if (mAppareilsAppaires.size() > 0)
		{
			// Pour chaque appareil appairé
			for (BluetoothDevice sAppareil : mAppareilsAppaires)
			{
				// On récupère la classe de l'appareil
				final int nClasseAppareil = sAppareil.getBluetoothClass()
						.getDeviceClass();

				// Si l'appareil est un JOUET de type ROBOT

				// On récupère son nom
				final String sNom = sAppareil.getName();
				// On récupère son adresse
				final String sAdresse = sAppareil.getAddress();
				// On ajoute le robot dans le conteneur des robots appairés

				this.mRobotsAppareilles.add(new Robot(sNom, sAdresse,
						(nClasseAppareil == JOUET_ROBOT)));

			}

			// Si aucun robot n'est appareillé
			if (this.mRobotsAppareilles.size() == 0)
			{
				// On averti l'utilisateur
				if (bBluetoothActive)
				{
					this.mRobotsAppareilles.add(new Robot(
							"Aucun robot appairé", "", false));
				}
				else
				{
					this.mRobotsAppareilles.add(new Robot(
							"Activation du bluetooth...", "", false));
				}
			}
		}
		else
		// Si aucun appareil Bluetooth est appairé
		{
			// On averti l'utilisateur
			this.mRobotsAppareilles.add(new Robot("Aucun appareil appairé", "",
					false));
		}

		this.listeRobotsAppaireilles.setAdapter(this.btArrayAdapterAppareilles);
	}
}
