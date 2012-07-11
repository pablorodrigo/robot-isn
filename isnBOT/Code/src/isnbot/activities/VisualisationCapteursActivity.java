package isnbot.activities;

import isnbot.classes.Capteur;
import isnbot.classes.Commande;
import isnbot.classes.Communication;
import isnbot.classes.ConnexionThread;
import isnbot.classes.ConstantesLCP;
import isnbot.classes.Session;

import java.nio.ByteBuffer;
import java.util.Arrays;

import android.app.ProgressDialog;
import android.app.TabActivity;
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
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

public class VisualisationCapteursActivity extends TabActivity implements
		ConstantesLCP
{
	private static final String			TAG						= "VisualisationCapteursActivity >>>";
	private Session						mSession;

	private Capteur[]					mCapteurs;
	private Capteur[]					mMoteurs;

	private int							nDelaiScrutation;

	private ProgressDialog				mEcranChargement;

	private volatile int				nInitialisationCapteurs	= 7;

	private Communication				mCommunication;

	private ScrutationCapteurThread[]	mCapteursThreads;
	private ScrutationMoteurThread[]	mMoteursThreads;

	static SharedPreferences			mPreferences;
	static SharedPreferences.Editor		mPreferencesEditor;
	private boolean						bConnexion				= false;

	private boolean						bBoucle					= true;

	private volatile int				nNumeroEchange			= 1;

	private boolean[]					bScrutationCapteurs		= new boolean[4];
	private boolean[]					bScrutationMoteurs		= new boolean[3];

	private Handler						ConnexionHandler		= new Handler()
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
																			{
																				afficherToast("Robot non disponible");
																				finish();
																			}
																			break;

																		case 1:
																			mEcranChargement
																					.setMessage("Initialisation des ports...");
																			initialiserTypesEtModes();
																			break;

																		case 2:
																			mEcranChargement
																					.dismiss();
																			demarrerThreadsScrutation();

																			getWindow()
																					.addFlags(
																							WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
																			bConnexion = true;
																			break;

																		default:
																			break;
																		}
																	}
																};

	private Handler						ScrutationHandler		= new Handler()
																{
																	@Override
																	public void handleMessage(
																			Message msg)
																	{
																		mEcranChargement
																				.dismiss();
																		switch (msg.what)
																		{
																		case PORT_1:
																			afficherValeurPort(
																					PORT_1,
																					true);
																			break;

																		case PORT_2:
																			afficherValeurPort(
																					PORT_2,
																					true);
																			break;

																		case PORT_3:
																			afficherValeurPort(
																					PORT_3,
																					true);
																			break;

																		case PORT_4:
																			afficherValeurPort(
																					PORT_4,
																					true);
																			break;
																		case PORT_A + 4:
																			afficherValeurPort(
																					PORT_A,
																					false);
																			break;
																		case PORT_B + 4:
																			afficherValeurPort(
																					PORT_B,
																					false);
																			break;
																		case PORT_C + 4:
																			afficherValeurPort(
																					PORT_C,
																					false);
																			break;
																		}
																	}
																};

	private Handler						mErreursHandler			= new Handler()
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

		mPreferences = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());
		mPreferencesEditor = mPreferences.edit();

		recupererSession();
		initialiserOnglets();
		initialiserOptions();

		/*
		 * mCapteurs[0].getTvNom().setText("Port 1 : Capteur à ultrasons");
		 * mCapteurs[1].getTvNom().setText("Port 2 : Capteur photosensible");
		 * mCapteurs[2].getTvNom().setText("Port 3 : Aucun capteur");
		 * mCapteurs[3].getTvNom().setText("Port 4 : Capteur tactile");
		 * 
		 * mCapteurs[0].getTvValeur().setText("Distance : 34 cm");
		 * mCapteurs[1].getTvValeur().setText("Luminosité : 47%");
		 * mCapteurs[2].getTvValeur().setText("Aucun capteur sur ce port");
		 * mCapteurs[3].getTvValeur().setText("Etat : 1");
		 * 
		 * mCapteurs[0].getPbValeur().setVisibility(View.VISIBLE);
		 * mCapteurs[1].getPbValeur().setVisibility(View.VISIBLE);
		 * 
		 * mCapteurs[3].getPbValeur().setVisibility(View.VISIBLE);
		 * mCapteurs[0].getPbValeur().setMax(255);
		 * mCapteurs[0].getPbValeur().setProgress(34);
		 * mCapteurs[1].getPbValeur().setMax(100);
		 * mCapteurs[1].getPbValeur().setProgress(47);
		 * mCapteurs[2].getPbValeur().setMax(1);
		 * mCapteurs[2].getPbValeur().setProgress(0);
		 * mCapteurs[3].getPbValeur().setMax(1);
		 * mCapteurs[3].getPbValeur().setProgress(1);
		 * 
		 * mMoteurs[0].getPbValeur().setMax(360);
		 * mMoteurs[0].getPbValeur().setProgress(98);
		 * mMoteurs[1].getPbValeur().setMax(360);
		 * mMoteurs[1].getPbValeur().setProgress(125);
		 * mMoteurs[2].getPbValeur().setMax(360);
		 * mMoteurs[2].getPbValeur().setProgress(32);
		 * 
		 * 
		 * mMoteurs[0].getTvValeur().setText("Rotation : 98° = (0*360°+98°)");
		 * mMoteurs[1].getTvValeur().setText("Rotation : 845° = (2*360°+125°)");
		 * mMoteurs
		 * [2].getTvValeur().setText("Rotation : -392° = (-1*360°-32°)");
		 */

		connecter();
	}

	private void connecter()
	{
		mEcranChargement = ProgressDialog.show(this, "Veuillez patienter",
				"Connexion avec le robot...", true);

		this.mCommunication = new Communication(true, mErreursHandler);

		new ConnexionThread(this.mSession, this.mCommunication,
				this.ConnexionHandler).start();
	}

	private void initialiserTypesEtModes()
	{
		new TypeEtModeThread(PORT_1, true).start();
		new TypeEtModeThread(PORT_2, true).start();
		new TypeEtModeThread(PORT_3, true).start();
		new TypeEtModeThread(PORT_4, true).start();

		new TypeEtModeThread(PORT_A, false).start();
		new TypeEtModeThread(PORT_B, false).start();
		new TypeEtModeThread(PORT_C, false).start();
	}

	private class ScrutationMoteurThread extends Thread
	{
		private static final String	TAG			= "ScrutationMoteurThread >>>";
		private int					nPort;
		private boolean				bScrutation	= true;

		public ScrutationMoteurThread(int nPort)
		{
			this.nPort = nPort;
		}

		@Override
		public void run()
		{
			Looper.prepare();

			while (bScrutation && !Thread.currentThread().isInterrupted())
			{
				if (!(mCapteurs[this.nPort].getCapteur().equals(ID_AUCUN)))
				{
					if (mCommunication.estConnecte())
					{
						byte[] byReponse = mCommunication.envoyerCommande(
								Commande.GET_OUTPUT_STATE(this.nPort),
								nNumeroEchange);

						mMoteurs[this.nPort].setReponse(byReponse);

						if (bScrutationMoteurs[this.nPort])
						{
							ScrutationHandler.sendEmptyMessage(this.nPort + 4);
						}

						try
						{
							sleep(nDelaiScrutation);
						}
						catch (Exception mException)
						{
							Log.e(TAG, "sleep", mException);
						}
					}
					else
					{
						afficherToast("Erreur de communication");
						finish();
					}
				}
			}

			if (this.nPort == 0)
				Log.d(TAG, "fin scrutation port 0");

			Looper.loop();
		}

		public void arreterScrutation()
		{
			this.bScrutation = false;
		}
	}

	private void afficherToast(String sMessage)
	{
		Toast.makeText(this, sMessage, Toast.LENGTH_SHORT).show();
	}

	private class ScrutationCapteurThread extends Thread
	{
		private static final String	TAG			= "ScrutationCapteurThread >>>";
		private int					nPort;
		private boolean				bScrutation	= true;

		public ScrutationCapteurThread(int nPort)
		{
			this.nPort = nPort;
		}

		@Override
		public void run()
		{
			Looper.prepare();

			while (bScrutation && !Thread.currentThread().isInterrupted())
			{
				// Si il y a un capteur sur ce port
				if (!(mCapteurs[this.nPort].getCapteur().equals(ID_AUCUN)))
				{
					if (mCommunication.estConnecte())
					{
						// Si c'est un capteur à ultrasons
						if (mCapteurs[this.nPort].getCapteur().equals(
								CAPTEUR_ULTRASONS))
						{
							// -- LS_WRITE -- //
							byte[] lsWrite = Commande.LS_WRITE(this.nPort);

							int nBufferedNumeroEchange = nNumeroEchange;
							nNumeroEchange++;
							mCommunication.envoyerCommande(lsWrite,
									nBufferedNumeroEchange);

							try
							{
								sleep(nDelaiScrutation);
							}
							catch (Exception mException)
							{
								Log.e(TAG, "sleep", mException);
							}

							byte[] lsRead = Commande.LS_READ(this.nPort);

							nBufferedNumeroEchange = nNumeroEchange;
							nNumeroEchange++;
							byte[] byReponse = mCommunication.envoyerCommande(
									lsRead, nBufferedNumeroEchange);

							mCapteurs[this.nPort].setReponse(byReponse); // byBuff
						}
						else
						// Autre capteur
						{
							byte[] byCommande = Commande
									.GET_INPUT_VALUES(this.nPort);

							int nBufferedNumeroEchange = nNumeroEchange;
							nNumeroEchange++;
							byte[] byReponse = mCommunication.envoyerCommande(
									byCommande, nBufferedNumeroEchange);

							mCapteurs[this.nPort].setReponse(byReponse);

							try
							{
								sleep(nDelaiScrutation);
							}
							catch (InterruptedException mException)
							{
								Log.e(TAG, "sleep", mException);
							}
						}

						if (bScrutationCapteurs[this.nPort])
						{
							ScrutationHandler.sendEmptyMessage(this.nPort);
						}
					}
				}
			}

			Looper.loop();
		}

		public void arreterScrutation()
		{
			this.bScrutation = false;
		}
	}

	private class TypeEtModeThread extends Thread
	{
		private static final String	TAG	= "TypeEtModeThread";
		private int					nPort;
		private boolean				bCapteur;

		public TypeEtModeThread(int nPort, boolean bCapteur)
		{
			this.nPort = nPort;
			this.bCapteur = bCapteur;
		}

		@Override
		public void run()
		{
			if (mCommunication != null)
			{
				if (mCommunication.estConnecte())
				{
					if (bCapteur)
					{
						byte[] byCommande =
						{ (byte) DIRECT_COMMAND_REPLY, SET_INPUT_MODE,
								(byte) this.nPort,
								mCapteurs[this.nPort].getType(),
								mCapteurs[this.nPort].getMode() };
						int nBufferedNumeroEchange = nNumeroEchange;
						nNumeroEchange++;
						mCommunication.envoyerCommande(byCommande,
								nBufferedNumeroEchange);

					}
					else
					{
						byte[] byResetMotorPosotion = Commande
								.RESET_MOTOR_POSITION(this.nPort);
						int nBufferedNumeroEchange = nNumeroEchange;
						nNumeroEchange++;
						mCommunication.envoyerCommande(byResetMotorPosotion,
								nBufferedNumeroEchange);
					}

					nInitialisationCapteurs--;
					if (nInitialisationCapteurs == 0)
					{

						ConnexionHandler.sendEmptyMessage(2);
					}
				}
				else
				{
					afficherToast("Erreur de communication");
					finish();
				}
			}
		}
	}

	private void recupererSession()
	{
		Intent thisIntent = getIntent();
		mSession = (Session) thisIntent.getParcelableExtra("mSession");
	}

	private void initialiserOnglets()
	{
		TabHost tabHost = getTabHost();
		LayoutInflater.from(this).inflate(R.layout.visualisation_capteurs,
				tabHost.getTabContentView(), true);

		tabHost.addTab(tabHost
				.newTabSpec("tab1")
				.setIndicator("",
						getResources().getDrawable(R.drawable.ic_menu_view_big))
				.setContent(R.id.llSensors));

		tabHost.addTab(tabHost
				.newTabSpec("tab2")
				.setIndicator(
						"",
						getResources().getDrawable(
								R.drawable.ic_menu_preferences))
				.setContent(R.id.llOptions));

		for (int i = 0; i < tabHost.getTabWidget().getTabCount(); i++)
		{
			tabHost.getTabWidget().getChildAt(i).getLayoutParams().height = 70;
		}

		ArrayAdapter<CharSequence> adapterCapteur = ArrayAdapter
				.createFromResource(this, R.array.typeCapteurArray,
						android.R.layout.simple_spinner_item);
		adapterCapteur
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		ArrayAdapter<CharSequence> adapterMoteur = ArrayAdapter
				.createFromResource(this, R.array.connexionMoteurArray,
						android.R.layout.simple_spinner_item);
		adapterMoteur
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		ArrayAdapter<CharSequence> adapterRefresh = ArrayAdapter
				.createFromResource(this, R.array.refresh_array,
						android.R.layout.simple_spinner_item);
		adapterRefresh
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		Spinner spCapteur1 = (Spinner) findViewById(R.id.spPort1);
		spCapteur1.setAdapter(adapterCapteur);
		spCapteur1.setSelection(mPreferences.getInt("nCapteur1", ID_AUCUN));
		// spCapteur1.setSelection(2);

		Spinner spCapteur2 = (Spinner) findViewById(R.id.spPort2);
		spCapteur2.setAdapter(adapterCapteur);
		spCapteur2.setSelection(mPreferences.getInt("nCapteur2", ID_AUCUN));
		// spCapteur2.setSelection(1);

		Spinner spCapteur3 = (Spinner) findViewById(R.id.spPort3);
		spCapteur3.setAdapter(adapterCapteur);
		spCapteur3.setSelection(mPreferences.getInt("nCapteur3", ID_AUCUN));
		// spCapteur3.setSelection(3);

		Spinner spCapteur4 = (Spinner) findViewById(R.id.spPort4);
		spCapteur4.setAdapter(adapterCapteur);
		spCapteur4.setSelection(mPreferences.getInt("nCapteur4", ID_AUCUN));
		// spCapteur4.setSelection(0);

		Spinner spMoteurA = (Spinner) findViewById(R.id.spMoteurA);
		spMoteurA.setAdapter(adapterMoteur);
		spMoteurA.setSelection(mPreferences.getInt("nMoteur1", ID_DECONNECTE));
		// spMoteurA.setSelection(0);

		Spinner spMoteurB = (Spinner) findViewById(R.id.spMoteurB);
		spMoteurB.setAdapter(adapterMoteur);
		spMoteurB.setSelection(mPreferences.getInt("nMoteur2", ID_DECONNECTE));
		// spMoteurB.setSelection(0);

		Spinner spMoteurC = (Spinner) findViewById(R.id.spMoteurC);
		spMoteurC.setAdapter(adapterMoteur);
		spMoteurC.setSelection(mPreferences.getInt("nMoteur3", ID_DECONNECTE));
		// spMoteurC.setSelection(0);

		Spinner spRefresh = (Spinner) findViewById(R.id.spRefresh);
		spRefresh.setAdapter(adapterRefresh);
		spRefresh.setSelection(mPreferences.getInt("nRefresh", 0));

		spCapteur1
				.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
				{
					public void onItemSelected(AdapterView<?> parent,
							View view, int pos, long id)
					{
						mPreferencesEditor.putInt("nCapteur1", pos);
						mPreferencesEditor.commit();
						setupOptions(PORT_1, true);

						if (pos != ID_AUCUN)
						{
							gererThreads(0, true, true);
						}
						else
						{
							gererThreads(0, true, false);
						}
					}

					public void onNothingSelected(AdapterView<?> parent)
					{
					}
				});

		spCapteur2
				.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
				{
					public void onItemSelected(AdapterView<?> parent,
							View view, int pos, long id)
					{
						mPreferencesEditor.putInt("nCapteur2", pos);
						mPreferencesEditor.commit();
						setupOptions(PORT_2, true);

						if (pos != ID_AUCUN)
						{
							gererThreads(1, true, true);
						}
						else
						{
							gererThreads(1, true, false);
						}
					}

					public void onNothingSelected(AdapterView<?> parent)
					{
					}
				});

		spCapteur3
				.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
				{
					public void onItemSelected(AdapterView<?> parent,
							View view, int pos, long id)
					{
						mPreferencesEditor.putInt("nCapteur3", pos);
						mPreferencesEditor.commit();
						setupOptions(PORT_3, true);

						if (pos != ID_AUCUN)
						{
							gererThreads(2, true, true);
						}
						else
						{
							gererThreads(2, true, false);
						}
					}

					public void onNothingSelected(AdapterView<?> parent)
					{
					}
				});

		spCapteur4
				.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
				{
					public void onItemSelected(AdapterView<?> parent,
							View view, int pos, long id)
					{
						mPreferencesEditor.putInt("nCapteur4", pos);
						mPreferencesEditor.commit();
						setupOptions(PORT_4, true);

						if (pos != ID_AUCUN)
						{
							gererThreads(3, true, true);
						}
						else
						{
							gererThreads(3, true, false);
						}
					}

					public void onNothingSelected(AdapterView<?> parent)
					{
					}
				});

		spMoteurA
				.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
				{
					public void onItemSelected(AdapterView<?> parent,
							View view, int pos, long id)
					{
						mPreferencesEditor.putInt("nMoteur1", pos);
						mPreferencesEditor.commit();
						setupOptions(PORT_A, false);

						if (pos != ID_DECONNECTE)
						{
							gererThreads(0, false, true);
						}
						else
						{
							gererThreads(0, false, false);
						}
					}

					public void onNothingSelected(AdapterView<?> parent)
					{
					}
				});

		spMoteurB
				.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
				{
					public void onItemSelected(AdapterView<?> parent,
							View view, int pos, long id)
					{
						mPreferencesEditor.putInt("nMoteur2", pos);
						mPreferencesEditor.commit();
						setupOptions(PORT_B, false);

						if (pos != ID_DECONNECTE)
						{
							gererThreads(1, false, true);
						}
						else
						{
							gererThreads(1, false, false);
						}
					}

					public void onNothingSelected(AdapterView<?> parent)
					{
					}
				});

		spMoteurC
				.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
				{
					public void onItemSelected(AdapterView<?> parent,
							View view, int pos, long id)
					{
						mPreferencesEditor.putInt("nMoteur3", pos);
						mPreferencesEditor.commit();
						setupOptions(PORT_C, false);

						if (pos != ID_DECONNECTE)
						{
							gererThreads(2, false, true);
						}
						else
						{
							gererThreads(2, false, false);
						}
					}

					public void onNothingSelected(AdapterView<?> parent)
					{
					}
				});

		spRefresh
				.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
				{
					public void onItemSelected(AdapterView<?> parent,
							View view, int pos, long id)
					{
						mPreferencesEditor.putInt("nRefresh", pos);
						mPreferencesEditor.commit();
						setupOptions(PORT_DEFAUT, true);
					}

					public void onNothingSelected(AdapterView<?> parent)
					{
					}
				});

		mCapteurs = new Capteur[4];
		for (int i = 0; i < 4; i++)
		{
			mCapteurs[i] = new Capteur("nCapteur" + String.valueOf(i + 1));
			mCapteurs[i].setPort(i);
			bScrutationCapteurs[i] = true;
		}

		mMoteurs = new Capteur[3];
		for (int i = 0; i < 3; i++)
		{
			mMoteurs[i] = new Capteur("nMoteur" + String.valueOf(i + 1));
			mMoteurs[i].setPort(i);
			bScrutationMoteurs[i] = true;
		}

		mCapteurs[PORT_1].setTvNom((TextView) findViewById(R.id.tvNamePort1));
		mCapteurs[PORT_2].setTvNom((TextView) findViewById(R.id.tvNamePort2));
		mCapteurs[PORT_3].setTvNom((TextView) findViewById(R.id.tvNamePort3));
		mCapteurs[PORT_4].setTvNom((TextView) findViewById(R.id.tvNamePort4));

		mCapteurs[PORT_1].setTvValeur((TextView) findViewById(R.id.tvValue1));
		mCapteurs[PORT_2].setTvValeur((TextView) findViewById(R.id.tvValue2));
		mCapteurs[PORT_3].setTvValeur((TextView) findViewById(R.id.tvValue3));
		mCapteurs[PORT_4].setTvValeur((TextView) findViewById(R.id.tvValue4));

		mMoteurs[PORT_A]
				.setTvValeur((TextView) findViewById(R.id.tvValeurMoteurA));
		mMoteurs[PORT_B]
				.setTvValeur((TextView) findViewById(R.id.tvValeurMoteurB));
		mMoteurs[PORT_C]
				.setTvValeur((TextView) findViewById(R.id.tvValeurMoteurC));

		mCapteurs[PORT_1]
				.setProgressBar((ProgressBar) findViewById(R.id.pbPort1));
		mCapteurs[PORT_2]
				.setProgressBar((ProgressBar) findViewById(R.id.pbPort2));
		mCapteurs[PORT_3]
				.setProgressBar((ProgressBar) findViewById(R.id.pbPort3));
		mCapteurs[PORT_4]
				.setProgressBar((ProgressBar) findViewById(R.id.pbPort4));

		mMoteurs[PORT_A]
				.setProgressBar((ProgressBar) findViewById(R.id.pbMoteurA));
		mMoteurs[PORT_B]
				.setProgressBar((ProgressBar) findViewById(R.id.pbMoteurB));
		mMoteurs[PORT_C]
				.setProgressBar((ProgressBar) findViewById(R.id.pbMoteurC));

		for (int i = 0; i < 3; i++)
		{
			mMoteurs[i].getPbValeur().setMax(360);
		}

	}

	private void initialiserOptions()
	{
		setupOptions(PORT_DEFAUT, true);
		setupOptions(PORT_1, true);
		setupOptions(PORT_2, true);
		setupOptions(PORT_3, true);
		setupOptions(PORT_4, true);

		setupOptions(PORT_A, false);
		setupOptions(PORT_B, false);
		setupOptions(PORT_C, false);
	}

	private void setupOptions(int nPort, boolean bCapteur)
	{
		if (nPort == PORT_DEFAUT)
		{
			setRefreshValue();
		}
		else
		{
			setValeurPort(nPort, bCapteur);
			setAffichagePort(nPort, bCapteur);
		}
	}

	private void setRefreshValue()
	{
		switch (mPreferences.getInt("nRefresh", 0))
		{
		case 0:
			this.nDelaiScrutation = 500;
			break;

		case 1:
			this.nDelaiScrutation = 1000;
			break;

		case 2:
			this.nDelaiScrutation = 1500;
			break;

		case 3:
			this.nDelaiScrutation = 2000;
		}
	}

	private void setValeurPort(int nPort, boolean bCapteur)
	{
		if (bCapteur)
		{
			int nCapteur = mPreferences.getInt(mCapteurs[nPort].getNom(),
					ID_AUCUN);

			switch (nCapteur)
			{
			case ID_TACTILE:
				mCapteurs[nPort].setType(SWITCH);
				mCapteurs[nPort].setMode(BOOLEANMODE);
				mCapteurs[nPort].setCapteur(CAPTEUR_TACTILE);
				break;

			case ID_PHOTOSENSIBLE:
				mCapteurs[nPort].setType(FLOODLIGHT_OFF);
				mCapteurs[nPort].setMode(RAWMODE);
				mCapteurs[nPort].setCapteur(CAPTEUR_PHOTOSENSIBLE);
				break;

			case ID_ULTRASONS:
				mCapteurs[nPort].setType(LOWSPEED_9V);
				mCapteurs[nPort].setMode(CONTINUOUSMODE);
				mCapteurs[nPort].setCapteur(CAPTEUR_ULTRASONS);
				break;

			case ID_AUCUN:
				mCapteurs[nPort].setCapteur(CAPTEUR_AUCUN);
				break;
			}
		}
		else
		{
			int nMoteur = mPreferences.getInt(mMoteurs[nPort].getNom(), 1);

			switch (nMoteur)
			{
			case 0:
				mMoteurs[nPort].setCapteur(CAPTEUR_ROTATION);
				break;
			case 1:
				mMoteurs[nPort].setCapteur(CAPTEUR_AUCUN);
				break;
			}
		}

	}

	private void setAffichagePort(int nPort, boolean bCapteur)
	{
		if (bCapteur)
		{
			if (mCapteurs[nPort].getCapteur().equals(CAPTEUR_TACTILE))
			{
				mCapteurs[nPort].getPbValeur().setVisibility(View.VISIBLE);
				mCapteurs[nPort].getPbValeur().setMax(1);

				this.bScrutationCapteurs[nPort] = true;
			}
			else if (mCapteurs[nPort].getCapteur()
					.equals(CAPTEUR_PHOTOSENSIBLE))
			{
				mCapteurs[nPort].getPbValeur().setVisibility(View.VISIBLE);
				mCapteurs[nPort].getPbValeur().setMax(100);

				this.bScrutationCapteurs[nPort] = true;
			}
			else if (mCapteurs[nPort].getCapteur().equals(CAPTEUR_ULTRASONS))
			{
				mCapteurs[nPort].getPbValeur().setVisibility(View.VISIBLE);
				mCapteurs[nPort].getPbValeur().setMax(256);

				this.bScrutationCapteurs[nPort] = true;
			}
			else if (mCapteurs[nPort].getCapteur().equals(CAPTEUR_AUCUN))
			{
				mCapteurs[nPort].getPbValeur().setVisibility(View.GONE);
				mCapteurs[nPort].getTvValeur().setText(
						"Aucun capteur sur ce port");

				this.bScrutationCapteurs[nPort] = false;
			}

			mCapteurs[nPort].getTvNom().setText(
					"Port " + String.valueOf(nPort + 1) + " : "
							+ mCapteurs[nPort].getCapteur());
		}
		else
		{
			if (!mMoteurs[nPort].getCapteur().equals(CAPTEUR_ROTATION))
			{
				mMoteurs[nPort].getPbValeur().setVisibility(View.GONE);

				mMoteurs[nPort].getTvValeur().setText(
						"Aucun moteur sur ce port");

				this.bScrutationMoteurs[nPort] = false;
			}
			else
			{
				mMoteurs[nPort].getPbValeur().setVisibility(View.VISIBLE);

				this.bScrutationMoteurs[nPort] = true;
			}
		}

	}

	private void afficherValeurPort(int nPort, boolean bCapteur)
	{
		if (bCapteur)
		{
			if (!bScrutationCapteurs[nPort])
				return;

			byte byTypeCapteur = this.mCapteurs[nPort].getType();
			byte[] byReponse = this.mCapteurs[nPort].getReponse();
			TextView tvValeurCapteur = this.mCapteurs[nPort].getTvValeur();
			ProgressBar pbValeurCapteur = this.mCapteurs[nPort].getPbValeur();

			if (byReponse == null
					|| !(byReponse.length == 16 || byReponse.length == 20))
			{
				tvValeurCapteur.setText("Réponse du capteur invalide");
				pbValeurCapteur.setProgress(0);
			}
			else
			{
				if (byTypeCapteur == SWITCH)
				{
					int nValeurTactile = byReponse[12] & 0xFF;
					String sValeurAffichee = "Réponse du capteur invalide";

					if (nValeurTactile == 1 || nValeurTactile == 0)
					{
						sValeurAffichee = "Etat : "
								+ Integer.toString(nValeurTactile, 16);
					}
					else
					{
						nValeurTactile = 0;
					}

					tvValeurCapteur.setText(sValeurAffichee);
					pbValeurCapteur.setProgress(nValeurTactile);
				}
				else if (byTypeCapteur == FLOODLIGHT_OFF)
				{
					int nValeurLuminosite = byReponse[13] * 256 + byReponse[12];
					String sValeurAffichee = "Réponse du capteur invalide";

					if ((nValeurLuminosite <= 100) && (nValeurLuminosite >= 0))
					{
						sValeurAffichee = "Luminosité : "+Integer.toString(byReponse[13] * 256
								+ byReponse[12])+"%";
					}
					else
					{
						nValeurLuminosite = 0;
					}
					
					tvValeurCapteur.setText(sValeurAffichee);
					pbValeurCapteur.setProgress(nValeurLuminosite);
				}
				else if (byTypeCapteur == LOWSPEED_9V)
				{
					int nDistance = byReponse[4] & 0xff;
					String sDistance = "";

					if (nDistance >= 255 || nDistance < 0)
					{
						sDistance = "Distance : ? cm";
						nDistance = 0;
					}
					else if (nDistance == 0)
					{
						sDistance = "Réponse du capteur invalide";
					}
					else
					{
						sDistance = "Distance : " + Integer.toString(nDistance)
								+ " cm";
					}

					tvValeurCapteur.setText(sDistance);
					pbValeurCapteur.setProgress(nDistance);
				}
			}
		}
		else
		{
			if (!bScrutationMoteurs[nPort])
				return;

			byte[] byReponse = this.mMoteurs[nPort].getReponse();
			TextView tvValeurMoteur = this.mMoteurs[nPort].getTvValeur();
			ProgressBar pbValeurMoteur = this.mMoteurs[nPort].getPbValeur();

			if (byReponse == null || byReponse.length != 25)
			{
				tvValeurMoteur.setText("Reponse du moteur invalide");
				pbValeurMoteur.setProgress(0);
			}
			else
			{
				byte[] byTachoCount = new byte[4];
				System.arraycopy(byReponse, 13, byTachoCount, 0, 4);
				byte[] inverseTacho = new byte[4];
				inverseTacho[0] = byTachoCount[3];
				inverseTacho[1] = byTachoCount[2];
				inverseTacho[2] = byTachoCount[1];
				inverseTacho[3] = byTachoCount[0];
				int nTachoLimit = ByteBuffer.wrap(inverseTacho).getInt();
				int nRotationTotale = nTachoLimit;
				int nRatioCycle = 0;
				float fRatioCycle = 0;

				nRatioCycle = nRotationTotale / 360;
				fRatioCycle = (float) ((float) nRotationTotale / 360.0);
				fRatioCycle -= nRatioCycle;

				int nCycleActuel = (int) (fRatioCycle * 360);

				String sCycleActuel = Integer.toString(nCycleActuel);
				sCycleActuel = (sCycleActuel.contains("-") ? sCycleActuel : "+"
						+ sCycleActuel);

				nRatioCycle = (sCycleActuel.contains("-") ? nRatioCycle
						- (2 * nRatioCycle) : nRatioCycle);

				String sRotationTotale = Integer.toString(nRotationTotale);

				String sRatioCycle = Integer.toString(nRatioCycle);
				sRatioCycle = (nRotationTotale < 0 ? "-" + sRatioCycle
						: sRatioCycle);

				tvValeurMoteur.setText(sRotationTotale + "° = (" + sRatioCycle
						+ "*360°" + sCycleActuel + "°)");

				nCycleActuel = (nCycleActuel < 0 ? nCycleActuel
						- (nCycleActuel * 2) : nCycleActuel);
				pbValeurMoteur.setProgress(nCycleActuel);
			}
		}
	}

	private void demarrerThreadsScrutation()
	{
		this.mCapteursThreads = new ScrutationCapteurThread[4];

		for (int i = 0; i < 4; i++)
		{
			if (this.bScrutationCapteurs[i])
			{
				this.mCapteursThreads[i] = new ScrutationCapteurThread(i);
				this.mCapteursThreads[i].start();
			}
		}

		this.mMoteursThreads = new ScrutationMoteurThread[3];

		for (int i = 0; i < 3; i++)
		{
			if (this.bScrutationMoteurs[i])
			{
				this.mMoteursThreads[i] = new ScrutationMoteurThread(i);
				this.mMoteursThreads[i].start();
			}
		}
	}

	@Override
	protected void onDestroy()
	{
		arreterThreads();
		super.onDestroy();
	}

	private void arreterThreads()
	{
		if (this.mCommunication != null)
		{
			if (this.mCommunication.estConnecte())
			{
				for (byte i = 0; i < 4; i++)
				{
					if (this.mCapteursThreads[i] != null)
					{
						this.mCapteursThreads[i].arreterScrutation();
					}
				}

				for (byte i = 0; i < 3; i++)
				{
					if (this.mMoteursThreads[i] != null)
					{
						this.mMoteursThreads[i].arreterScrutation();
					}
				}

				this.mCommunication.deconnecter();
			}
		}
	}

	private void gererThreads(int nPort, boolean bCapteur, boolean bActivation)
	{
		Log.d(TAG,
				"gererThreads(" + Integer.toString(nPort) + ", "
						+ Boolean.toString(bCapteur) + ", "
						+ Boolean.toString(bActivation) + ")");
		if (bCapteur)
		{
			if (bActivation)
			{
				this.mCapteursThreads[nPort] = new ScrutationCapteurThread(
						nPort);
				this.mCapteursThreads[nPort].start();

				new TypeEtModeThread(nPort, true).start();

				bScrutationCapteurs[nPort] = true;
			}
			else
			{
				bScrutationCapteurs[nPort] = false;

				if (this.mCapteursThreads[nPort] != null)
				{
					this.mCapteursThreads[nPort].arreterScrutation();
				}
			}
		}
		else
		{
			if (bActivation)
			{
				this.mMoteursThreads[nPort] = new ScrutationMoteurThread(nPort);
				this.mMoteursThreads[nPort].start();

				new TypeEtModeThread(nPort, true).start();

				bScrutationMoteurs[nPort] = true;
			}
			else
			{
				bScrutationMoteurs[nPort] = false;

				if (this.mMoteursThreads[nPort] != null)
				{
					this.mMoteursThreads[nPort].arreterScrutation();
				}
			}
		}
	}
}