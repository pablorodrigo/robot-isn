package isnbot.activities;

import isnbot.classes.Commande;
import isnbot.classes.Communication;
import isnbot.classes.ConnexionThread;
import isnbot.classes.ConstantesLCP;
import isnbot.classes.Session;

import java.util.concurrent.LinkedBlockingQueue;

import android.app.ProgressDialog;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Classe d'interface permettant le pilotage standard du robot. Il est ainsi
 * possible de piloter rapidement le robot, ou finement les actionneurs.
 * 
 * @author Maxime BOUCHENOIRE
 * 
 */
public class PilotageStandardActivity extends TabActivity implements
		ConstantesLCP
{
	private static final String				TAG					= "PilotageStandardActivity >>>";
	// Attributs d'interface
	// ProgressDialog d'attente deconnexion avec le robot
	private ProgressDialog					mEcranChargement;
	// TextViews indiquants les puissances des moteurs du robot
	private TextView						labelPuissanceGlobale;
	private TextView						labelPuissanceA;
	private TextView						labelPuissanceB;
	private TextView						labelPuissanceC;
	// SeekBars permettants de régler les puissances des moteurs du robot
	private SeekBar							mBarrePuissanceGlobale;
	private SeekBar							mBarrePuissanceA;
	private SeekBar							mBarrePuissanceB;
	private SeekBar							mBarrePuissanceC;

	/** VRAI si l'état de la connexion est connu, FAUX sinon */
	private volatile boolean				bConnexion			= false;

	/** Objet contenant les informations sur la session */
	private Session							mSession;

	// Diférentes puissances des moteurs du robot
	private int								nPuissanceGlobale	= 50;
	private int								nPuissanceA			= 50;
	private int								nPuissanceB			= 50;
	private int								nPuissanceC			= 50;

	// Ports des moteurs droite et gauches pour le pilotage simplifié (-1)
	private int								nPortMoteurGauche;
	private int								nPortMoteurDroite;
	/** Port du capteur photosensible -1 */
	private int								nPortCapteurPhotosensible;

	/** private Communication mCommunication; */
	private Communication					mCommunication;

	/** Réponse du robot à chaque commande envoyée */
	volatile byte[]							byReponse;

	/** Permet la récupération des options */
	private static SharedPreferences		mPreferences;
	/** Permet la modidication des options */
	private static SharedPreferences.Editor	mPreferencesEditor;

	/** Numéro de l'échange permettant d'identifier un couple commande/réponse */
	private int								nNumeroEchange		= 1;

	private LinkedBlockingQueue<Commande>	mFileCommandes;

	private EnvoiCommandesThread			mEnvoiCommandesThread;

	private Context							mContext			= this;

	/**
	 * Handler permettant la gestion de la connexion au robot
	 */
	private Handler							mConnexionHandler	= new Handler()
																{
																	@Override
																	public void handleMessage(
																			Message msg)
																	{
																		switch (msg.what)
																		{
																		case 0:
																			mEcranChargement
																					.dismiss();
																			if (!bConnexion)
																				finish();
																			break;
																		case 1:
																			bConnexion = true;
																			mEcranChargement
																					.dismiss();
																			break;
																		}
																	}
																};

	private Handler							mErreursHandler		= new Handler()
																{
																	@Override
																	public void handleMessage(
																			Message msg)
																	{
																		arreterThreads();
																		mCommunication
																				.deconnecter();
																	}
																};

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// Initialisation des préférences (options)
		mPreferences = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());
		mPreferencesEditor = mPreferences.edit();

		initialiserOnglets(); // Initialisation des onglets
		initialiserOptions(); // Application des options
		initialisersBarresPuissances(); // Initialisations des seekBars

		this.mFileCommandes = new LinkedBlockingQueue<Commande>();
		this.mEnvoiCommandesThread = new EnvoiCommandesThread();
		this.mEnvoiCommandesThread.start();

		connecter();
	}

	private void connecter()
	{
		mEcranChargement = ProgressDialog.show(this, "Veuillez patienter",
				"Connexion avec le robot...", true);

		this.mCommunication = new Communication(true, mErreursHandler);

		Intent thisIntent = getIntent();
		mSession = (Session) thisIntent.getParcelableExtra("mSession");

		new ConnexionThread(this.mSession, this.mCommunication,
				this.mConnexionHandler).start();
	}

	private void ajouterCommande(Commande byCommande)
	{
		try
		{
			this.mFileCommandes.put(byCommande);
		}
		catch (InterruptedException mException)
		{
			Log.e(TAG, "put", mException);
		}
	}

	public void avancer(View v)
	{
		ajouterCommande(new Commande(nPortMoteurGauche,
				(byte) nPuissanceGlobale, nPortMoteurDroite,
				(byte) nPuissanceGlobale));
	}

	public void reculer(View v)
	{
		ajouterCommande(new Commande(nPortMoteurGauche,
				(byte) -nPuissanceGlobale, nPortMoteurDroite,
				(byte) -nPuissanceGlobale));
	}

	public void tournerGauche(View v)
	{
		ajouterCommande(new Commande(nPortMoteurGauche, (byte) 0,
				nPortMoteurDroite, (byte) nPuissanceGlobale));
	}

	public void tournerDroite(View v)
	{
		ajouterCommande(new Commande(nPortMoteurGauche,
				(byte) nPuissanceGlobale, nPortMoteurDroite, (byte) 0));
	}

	public void deriverGauche(View v)
	{
		ajouterCommande(new Commande(nPortMoteurGauche,
				(byte) (nPuissanceGlobale / 2), nPortMoteurDroite,
				(byte) nPuissanceGlobale));
	}

	public void deriverDroite(View v)
	{
		ajouterCommande(new Commande(nPortMoteurGauche,
				(byte) nPuissanceGlobale, nPortMoteurDroite,
				(byte) (nPuissanceGlobale / 2)));
	}

	public void pivoterDroite(View v)
	{
		ajouterCommande(new Commande(nPortMoteurGauche,
				(byte) nPuissanceGlobale, nPortMoteurDroite,
				(byte) -nPuissanceGlobale));
	}

	public void pivoterGauche(View v)
	{
		ajouterCommande(new Commande(nPortMoteurGauche,
				(byte) -nPuissanceGlobale, nPortMoteurDroite,
				(byte) nPuissanceGlobale));
	}

	public void arreter(View v)
	{
		ajouterCommande(new Commande(nPortMoteurGauche, (byte) 0,
				nPortMoteurDroite, (byte) 0));
	}

	public void avancerA(View v)
	{
		ajouterCommande(new Commande(PORT_A, (byte) nPuissanceA, true));
	}

	public void reculerA(View v)
	{
		ajouterCommande(new Commande(PORT_A, (byte) -nPuissanceA, true));
	}

	public void arreterA(View v)
	{
		ajouterCommande(new Commande(PORT_A, (byte) 0, true));
	}

	public void avancerB(View v)
	{
		ajouterCommande(new Commande(PORT_B, (byte) nPuissanceB, true));
	}

	public void reculerB(View v)
	{
		ajouterCommande(new Commande(PORT_B, (byte) -nPuissanceB, true));
	}

	public void arreterB(View v)
	{
		ajouterCommande(new Commande(PORT_B, (byte) 0, true));
	}

	public void avancerC(View v)
	{
		ajouterCommande(new Commande(PORT_C, (byte) nPuissanceC, true));
	}

	public void reculerC(View v)
	{
		ajouterCommande(new Commande(PORT_C, (byte) -nPuissanceC, true));
	}

	public void arreterC(View v)
	{
		ajouterCommande(new Commande(PORT_C, (byte) 0, true));
	}

	public void allumerRouge(View v)
	{
		ajouterCommande(new Commande(nPortCapteurPhotosensible, FLOODLIGHT_RED,
				false));
	}

	public void allumerBleu(View v)
	{
		ajouterCommande(new Commande(nPortCapteurPhotosensible,
				FLOODLIGHT_BLUE, false));
	}

	public void allumerVert(View v)
	{
		ajouterCommande(new Commande(nPortCapteurPhotosensible,
				FLOODLIGHT_GREEN, false));
	}

	public void etteindre(View v)
	{
		ajouterCommande(new Commande(nPortCapteurPhotosensible, FLOODLIGHT_OFF,
				false));
	}

	/**
	 * Classe threadée permettant l'envoi de commandes "SETOUTPUTSTATE" ou
	 * "SETINPUTMODE" selon le contructeur utilisé. Ici, le "SETOUTPUTSTATE" est
	 * utilisé "par défaut" et permet la commande des moteurs. Le "SETINPUTMODE"
	 * permet seulement la modification de la couleur de la LED du capteur
	 * photosensible
	 * 
	 * @author Maxime BOUCHENOIRE
	 * 
	 */
	class EnvoiCommandesThread extends Thread
	{
		private static final String	TAG				= "EnvoiCommandesThread >>>";
		private boolean				bEnvoi			= true;
		private int					nNumeroEchange	= 1;

		/**
		 * Méthode appelée lorsque le thread est démarré (thread.start()). La
		 * commande sera donc ici envoyée après avoir été préparée dans le
		 * constructeur.
		 */
		public void run()
		{
			Looper.prepare();
			
			while (bEnvoi && !Thread.currentThread().isInterrupted())
			{
				try
				{
					if (mCommunication != null)
					{
						if (mCommunication.estConnecte())
						{
							Commande mCommande = mFileCommandes.take();
							byte[] byCommande = mCommande.getCommande();
							mCommunication.envoyerCommande(byCommande,
									nNumeroEchange);
							nNumeroEchange++;
							if (mCommande.estDouble())
							{
								byte[] byCommande2 = mCommande.getCommande2();
								mCommunication.envoyerCommande(byCommande2,
										nNumeroEchange);
								nNumeroEchange++;
							}
						}
						else
						{
							Toast.makeText(mContext, "Erreur de communication",
									Toast.LENGTH_SHORT);
						}
					}
					else
					{
						Toast.makeText(mContext, "Erreur de communication",
								Toast.LENGTH_SHORT);
					}
				}
				catch (InterruptedException mException)
				{
					Log.e(TAG, "take", mException);
					this.bEnvoi = false;
				}
			}
			
			Looper.loop();
		}

		public void arreterEnvois()
		{
			this.bEnvoi = false;
		}
	}

	/**
	 * Initialise les 4 seekBars de puissance (une globale et trois
	 * individuelles)
	 */
	private void initialisersBarresPuissances()
	{
		labelPuissanceGlobale = (TextView) findViewById(R.id.tvPuissance);
		labelPuissanceA = (TextView) findViewById(R.id.tvPuissanceA);
		labelPuissanceB = (TextView) findViewById(R.id.tvPuissanceB);
		labelPuissanceC = (TextView) findViewById(R.id.tvPuissanceC);

		mBarrePuissanceGlobale = (SeekBar) findViewById(R.id.sbPuisance);
		mBarrePuissanceA = (SeekBar) findViewById(R.id.sbPuisanceA);
		mBarrePuissanceB = (SeekBar) findViewById(R.id.sbPuisanceB);
		mBarrePuissanceC = (SeekBar) findViewById(R.id.sbPuisanceC);

		mBarrePuissanceGlobale.setProgress(50);
		mBarrePuissanceA.setProgress(50);
		mBarrePuissanceB.setProgress(50);
		mBarrePuissanceC.setProgress(50);

		mBarrePuissanceGlobale
				.setOnSeekBarChangeListener(new OnSeekBarChangeListener()
				{
					public void onStartTrackingTouch(SeekBar arg0)
					{
					}

					public void onStopTrackingTouch(SeekBar seekBar)
					{

					}

					public void onProgressChanged(SeekBar seekBar,
							int progress, boolean fromUser)
					{
						nPuissanceGlobale = progress;
						labelPuissanceGlobale.setText("Puissance : "
								+ Integer.toString(nPuissanceGlobale) + "%");
					}

				});

		mBarrePuissanceA
				.setOnSeekBarChangeListener(new OnSeekBarChangeListener()
				{
					public void onStartTrackingTouch(SeekBar arg0)
					{
					}

					public void onStopTrackingTouch(SeekBar seekBar)
					{
					}

					public void onProgressChanged(SeekBar seekBar,
							int progress, boolean fromUser)
					{
						nPuissanceA = progress;
						labelPuissanceA.setText(Integer.toString(nPuissanceA)
								+ "%");
					}

				});

		mBarrePuissanceB
				.setOnSeekBarChangeListener(new OnSeekBarChangeListener()
				{
					public void onStartTrackingTouch(SeekBar arg0)
					{
					}

					public void onStopTrackingTouch(SeekBar seekBar)
					{
					}

					public void onProgressChanged(SeekBar seekBar,
							int progress, boolean fromUser)
					{
						nPuissanceB = progress;
						labelPuissanceB.setText(Integer.toString(nPuissanceB)
								+ "%");
					}

				});

		mBarrePuissanceC
				.setOnSeekBarChangeListener(new OnSeekBarChangeListener()
				{
					public void onStartTrackingTouch(SeekBar arg0)
					{
					}

					public void onStopTrackingTouch(SeekBar seekBar)
					{
					}

					public void onProgressChanged(SeekBar seekBar,
							int progress, boolean fromUser)
					{
						nPuissanceC = progress;
						labelPuissanceC.setText(Integer.toString(nPuissanceC)
								+ "%");
					}
				});
	}

	/**
	 * Initialise les onglets de l'interface
	 */
	private void initialiserOnglets()
	{
		TabHost tabHost = getTabHost();
		LayoutInflater.from(this).inflate(R.layout.pilotage_standard,
				tabHost.getTabContentView(), true);

		tabHost.addTab(tabHost
				.newTabSpec("pilotageRapideTab")
				.setIndicator("",
						getResources().getDrawable(R.drawable.ic_menu_goto_big))
				.setContent(R.id.pilotageRapide));

		tabHost.addTab(tabHost
				.newTabSpec("pilotageFinTab")
				.setIndicator(
						"",
						getResources().getDrawable(
								R.drawable.ic_menu_sort_by_size))
				.setContent(R.id.pilotageFin));

		tabHost.addTab(tabHost
				.newTabSpec("optionsTab")
				.setIndicator(
						"",
						getResources().getDrawable(
								R.drawable.ic_menu_preferences))
				.setContent(R.id.options));

		for (int i = 0; i < tabHost.getTabWidget().getTabCount(); i++)
		{
			tabHost.getTabWidget().getChildAt(i).getLayoutParams().height = 70;
		}
	}

	/**
	 * Applique les options de pilotage, à savoir les ports du moteurs droite et
	 * gauche, ainsi que le port du capteur photosensible
	 */
	private void initialiserOptions()
	{
		ArrayAdapter<CharSequence> adapterMotors = ArrayAdapter
				.createFromResource(this, R.array.portMoteurArray,
						android.R.layout.simple_spinner_item);
		adapterMotors
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		ArrayAdapter<CharSequence> adapterSensors = ArrayAdapter
				.createFromResource(this, R.array.portCapteurArray,
						android.R.layout.simple_spinner_item);
		adapterSensors
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		nPortMoteurDroite = mPreferences.getInt("nPortMoteurDroite", 0);
		nPortMoteurGauche = mPreferences.getInt("nPortMoteurGauche", 2);
		nPortCapteurPhotosensible = mPreferences.getInt("nPortLed", 3);

		Spinner spDroite = (Spinner) findViewById(R.id.spDroite);
		spDroite.setAdapter(adapterMotors);
		spDroite.setSelection(nPortMoteurDroite);
		Spinner spGauche = (Spinner) findViewById(R.id.spGauche);
		spGauche.setAdapter(adapterMotors);
		spGauche.setSelection(nPortMoteurGauche);
		Spinner spLed = (Spinner) findViewById(R.id.spLed);
		spLed.setAdapter(adapterSensors);
		spLed.setSelection(nPortCapteurPhotosensible);

		spDroite.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
		{
			public void onItemSelected(AdapterView<?> parent, View view,
					int pos, long id)
			{
				Object item = parent.getItemAtPosition(pos);
				String sPort = item.toString();
				if (sPort.equals("Port A"))
				{
					nPortMoteurDroite = 0;
					mPreferencesEditor.putInt("nPortMoteurDroite", 0);
					mPreferencesEditor.commit();
				}
				if (sPort.equals("Port B"))
				{
					nPortMoteurDroite = 1;
					mPreferencesEditor.putInt("nPortMoteurDroite", 1);
					mPreferencesEditor.commit();
				}
				if (sPort.equals("Port C"))
				{
					nPortMoteurDroite = 2;
					mPreferencesEditor.putInt("nPortMoteurDroite", 2);
					mPreferencesEditor.commit();
				}
			}

			public void onNothingSelected(AdapterView<?> parent)
			{
			}
		});

		spGauche.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
		{
			public void onItemSelected(AdapterView<?> parent, View view,
					int pos, long id)
			{
				Object item = parent.getItemAtPosition(pos);
				String sPort = item.toString();
				if (sPort.equals("Port A"))
				{
					nPortMoteurGauche = 0;
					mPreferencesEditor.putInt("nPortMoteurGauche", 0);
				}
				if (sPort.equals("Port B"))
				{
					nPortMoteurGauche = 1;
					mPreferencesEditor.putInt("nPortMoteurGauche", 1);
				}
				if (sPort.equals("Port C"))
				{
					nPortMoteurGauche = 2;
					mPreferencesEditor.putInt("nPortMoteurGauche", 2);
				}

				mPreferencesEditor.commit();
			}

			public void onNothingSelected(AdapterView<?> parent)
			{
			}
		});

		spLed.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
		{
			public void onItemSelected(AdapterView<?> parent, View view,
					int pos, long id)
			{
				Object item = parent.getItemAtPosition(pos);
				String sPort = item.toString();
				if (sPort.equals("Port 1"))
				{
					nPortCapteurPhotosensible = 0;
					mPreferencesEditor.putInt("nPortLed", 0);
				}
				else if (sPort.equals("Port 2"))
				{
					nPortCapteurPhotosensible = 1;
					mPreferencesEditor.putInt("nPortLed", 1);
				}
				else if (sPort.equals("Port 3"))
				{
					nPortCapteurPhotosensible = 2;
					mPreferencesEditor.putInt("nPortLed", 2);
				}
				else if (sPort.equals("Port 4"))
				{
					nPortCapteurPhotosensible = 3;
					mPreferencesEditor.putInt("nPortLed", 3);
				}

				mPreferencesEditor.commit();
			}

			public void onNothingSelected(AdapterView<?> parent)
			{
			}
		});
	}

	@Override
	protected void onDestroy()
	{
		arreterThreads();
		super.onDestroy();
	}

	private void arreterThreads()
	{
		if (mEnvoiCommandesThread != null)
		{
			this.mEnvoiCommandesThread.arreterEnvois();
		}

		if (this.mCommunication != null)
		{
			// Si on est connecté avec le robot
			this.mCommunication.deconnecter(); // On se déconnecte
			this.mCommunication = null; // on supprime la référence
			this.bConnexion = false;
		}
	}
}
