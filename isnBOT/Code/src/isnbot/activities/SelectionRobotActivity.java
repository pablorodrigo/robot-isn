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
 * Classe d'interface permettant � l'utilisateur de chercher et/ou s�lectionner
 * un robot afin de d�marrer une session
 * 
 * @author Maxime BOUCHENOIRE
 * 
 */
public class SelectionRobotActivity extends Activity
{
	private final String		TAG					= "SelectionRobotActivity >>>";
	/** Liste contenant les robots trouv�s � proximit� */
	private ListView			listeRobotsTrouves;
	/** Liste contenant les robots appareill�s au t�l�phone */
	private ListView			listeRobotsAppaireilles;
	/** Barre de chargement de la recherche de robots � proximit� */
	private ProgressBar			pbRechercheRobots;
	/** Barre de chargement de l'activation du Bluetooth */
	private ProgressBar			pbActivationBluetooth;
	/** Repr�sente le Bluetooth du t�l�phone */
	private BluetoothAdapter	mBluetoothAdapter;
	/** Adapter des robots appareill�s */
	private ListeRobotsAdapter	btArrayAdapterAppareilles;
	/** Adapter des robots � proximit� */
	private ListeRobotsAdapter	btArrayAdapterTrouves;
	/** Conteneur des robots appareill�s */
	private ArrayList<Robot>	mRobotsAppareilles	= new ArrayList<Robot>();
	/** Conteneur des robots trouv�s */
	private ArrayList<Robot>	mRobotsTrouves		= new ArrayList<Robot>();
	/** Permet la gestion des �venements li�s au Bluetooth */
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
		// On initialise les listes des robots appair�s/trouv�s
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

		// On initialise les BluetoothAdapter des robots appair�s/trouv�s
		this.btArrayAdapterAppareilles = new ListeRobotsAdapter(this,
				mRobotsAppareilles);

		this.btArrayAdapterTrouves = new ListeRobotsAdapter(this,
				mRobotsTrouves);

		activerBluetooth();
		afficherRobotsAppaireilles();

		// BroadcastReceiver d�clench� � chaque fois qu'un appareil est d�tect�
		// par bluetooth lors de la recherche.
		this.mBroadcastReceiver = new BroadcastReceiver()
		{
			public void onReceive(final Context mContext, final Intent mIntent)
			{
				gererEvenement(mIntent);
			}
		};

		// On cr�e un IntentFilter qui d�tecte des �v�nements li�s au Bluetooth
		IntentFilter mIntentFilter = new IntentFilter();
		// On d�tecte la trouvaille d'un robot
		mIntentFilter.addAction(BluetoothDevice.ACTION_FOUND);
		// On d�tecte l'arr�t de la recherche Bluetooth
		mIntentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		// On d�tecte le changement d'�tat du Bluetooth
		mIntentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
		// On active un Receiver utilisant l'IntentFilter param�tr� pr�cedemment
		registerReceiver(this.mBroadcastReceiver, mIntentFilter);
	}

	/**
	 * Slot permettant � l'application d'arr�ter le Bluetooth sur le t�l�phone
	 * lorsque l'utilisateur quitte cet �cran
	 */
	@Override
	protected void onDestroy()
	{
		// Si le Bluetooth est activ�
		if (this.mBluetoothAdapter.isEnabled())
		{
			// On d�sactive le Bluetooth
			this.mBluetoothAdapter.disable();
		}
		// On d�sactive le Receiver d'�v�nements Bluetooth
		unregisterReceiver(this.mBroadcastReceiver);
		super.onDestroy();
	}

	/**
	 * Slot du bouton de recherche d'un appareil par bluetooth.
	 * 
	 * @param v
	 *            Bouton de recherche des appareils � proximit�
	 */
	public void onBoutonRechercheClicked(final View v)
	{
		// Si le Bluetooth est activ�
		if (this.mBluetoothAdapter.isEnabled())
		{
			// On vide la liste des robots trouv�s
			this.btArrayAdapterTrouves.clear();
			this.listeRobotsTrouves.setAdapter(this.btArrayAdapterTrouves);
			// On d�marre la recherche des robots � proximit�s
			this.mBluetoothAdapter.startDiscovery();
			// On active la ProgressBar de recherche
			this.pbRechercheRobots.setVisibility(View.VISIBLE);
		}
		else
		// Si le Bluetooth n'est pas activ�
		{
			// On affiche un message alertant l'utilisateur que le Bluetooth
			// n'est pas activ�
			Toast.makeText(this, "Le Bluetooth n'est pas activ� !",
					Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * Active le bluetooth si n�cessaire, et emp�che l'utilisateur de faire quoi
	 * que ce soit si le t�l�phone ne dispose pas du bluetooth
	 */
	private void activerBluetooth()
	{
		// On initialise l'attribut repr�sentant le Bluetooth du t�l�phone
		this.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

		// Si le t�l�phone supporte le Bluetooth
		if (this.mBluetoothAdapter != null)
		{
			// Si le Bluetooth n'est pas activ�
			if (!this.mBluetoothAdapter.isEnabled())
			{
				// On active le Bluetooth
				this.mBluetoothAdapter.enable();
				// On rend visible le bouton de recherche des robots
				this.pbActivationBluetooth.setVisibility(View.VISIBLE);
			}
			else
			// Si le Bluetooth est activ�
			{
				// On affiche les robots appair�s au t�l�phone
				afficherRobotsAppaireilles();
			}
		}
		else
		// Si le t�p�hone ne supporte pas le Bluetooth
		{
			// On cache le bouton de recherche des robots
			Button btnSearch = (Button) findViewById(R.id.search_button);
			btnSearch.setVisibility(View.GONE);
		}
	}

	/**
	 * M�thode permetant de g�rer les �venements li�s au Bluetooth
	 * 
	 * @param mIntent
	 *            Intent contenant les informations sur l'�venement
	 */
	private void gererEvenement(final Intent mIntent)
	{
		// On r�cup�re l'action correspondante � l'�vennement "re�ue"
		final String sAction = mIntent.getAction();
		// Si un appareil Bluetooth � �t� trouv�
		if (BluetoothDevice.ACTION_FOUND.equals(sAction))
		{
			// On cr�e l'objet Bluetooth
			final BluetoothDevice mAppareilBluetooth = mIntent
					.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
			// On r�cup�re la classe de l'appareil
			final int nClasseBluetooth = mAppareilBluetooth.getBluetoothClass()
					.getDeviceClass();

			// On r�cup�re le nom du robot
			String sNomRobot = mAppareilBluetooth.getName();
			if (sNomRobot == null)
			{
				sNomRobot = "Robot inconnu";
			}
			// On r�cup�re l'adresse du robot
			final String sAdresseRobot = mAppareilBluetooth.getAddress();
			// On ajoute le robot dans le conteneur des robots trouv�s
			this.mRobotsTrouves.add(new Robot(sNomRobot, sAdresseRobot,
					(nClasseBluetooth == JOUET_ROBOT)));

			this.listeRobotsTrouves.setAdapter(this.btArrayAdapterTrouves);
		}
		// Si la recherche est termin�e
		if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(sAction))
		{
			// On arr�te la ProgressBar de recherche
			this.pbRechercheRobots.setVisibility(View.GONE);
			// Si aucun robot n'a �t� trouv�
			if (this.mRobotsTrouves.isEmpty())
			{
				// On averti l'utilisateur
				this.mRobotsTrouves
						.add(new Robot(
								"Aucun p�riph�rique � proximit�",
								"V�rifiez si le bluetooth des �ventuels p�riph�riques/robots � proximit� est activ�.",
								false));
				this.listeRobotsTrouves.setAdapter(this.btArrayAdapterTrouves);
			}
		}
		// Si l'�tat du Bluetooth a chang�
		if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(sAction))
		{
			// Si le Bluetooth s'est activ�
			if (this.mBluetoothAdapter.isEnabled())
			{
				// On cache la ProgressBar d'activation du Bluetooth
				this.pbActivationBluetooth.setVisibility(View.GONE);
				this.bBluetoothActive = true;
				// On affiche les robots appair�s
				afficherRobotsAppaireilles();
			}
			else
			{
				this.bBluetoothActive = false;
			}
		}
	}

	/**
	 * Affiche les robots appair�s au t�l�phone dans la listView ad�quate.
	 */
	private void afficherRobotsAppaireilles()
	{
		// On initialise le conteneur des robots appair�s
		this.btArrayAdapterAppareilles.clear();
		// On vide la liste des robots appair�s
		this.listeRobotsAppaireilles.setAdapter(this.btArrayAdapterAppareilles);
		// On r�cup�re les appareils Bluetooth appair�s
		final Set<BluetoothDevice> mAppareilsAppaires = this.mBluetoothAdapter
				.getBondedDevices();

		// Si des appareils Bluetooth sont appair�s
		if (mAppareilsAppaires.size() > 0)
		{
			// Pour chaque appareil appair�
			for (BluetoothDevice sAppareil : mAppareilsAppaires)
			{
				// On r�cup�re la classe de l'appareil
				final int nClasseAppareil = sAppareil.getBluetoothClass()
						.getDeviceClass();

				// Si l'appareil est un JOUET de type ROBOT

				// On r�cup�re son nom
				final String sNom = sAppareil.getName();
				// On r�cup�re son adresse
				final String sAdresse = sAppareil.getAddress();
				// On ajoute le robot dans le conteneur des robots appair�s

				this.mRobotsAppareilles.add(new Robot(sNom, sAdresse,
						(nClasseAppareil == JOUET_ROBOT)));

			}

			// Si aucun robot n'est appareill�
			if (this.mRobotsAppareilles.size() == 0)
			{
				// On averti l'utilisateur
				if (bBluetoothActive)
				{
					this.mRobotsAppareilles.add(new Robot(
							"Aucun robot appair�", "", false));
				}
				else
				{
					this.mRobotsAppareilles.add(new Robot(
							"Activation du bluetooth...", "", false));
				}
			}
		}
		else
		// Si aucun appareil Bluetooth est appair�
		{
			// On averti l'utilisateur
			this.mRobotsAppareilles.add(new Robot("Aucun appareil appair�", "",
					false));
		}

		this.listeRobotsAppaireilles.setAdapter(this.btArrayAdapterAppareilles);
	}
}
