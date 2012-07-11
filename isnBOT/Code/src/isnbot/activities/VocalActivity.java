package isnbot.activities;

import isnbot.classes.Commande;
import isnbot.classes.Communication;
import isnbot.classes.ConnexionThread;
import isnbot.classes.ConstantesLCP;
import isnbot.classes.Robot;
import isnbot.classes.Session;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lejos.pc.comm.NXTCommException;
import lejos.pc.comm.NXTCommFactory;
import lejos.pc.comm.NXTInfo;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class VocalActivity extends Activity implements ConstantesLCP
{
	private final String				TAG			= "VocalActivity >>>";
	static final int					check		= 1234;

	static SharedPreferences			preferences;
	static SharedPreferences.Editor		prefEditor;

	ArrayAdapter<String>				mCommandes;
	ListView							lvCommandes;

	Communication						mCommunication;

	int									nPortLed	= 1;

	byte[]								reply;

	int									nVitesse;

	boolean								connected;

	ProgressDialog						mEcranChargement;

	private Session						mSession;

	private LinkedBlockingQueue<byte[]>	mFileCommandes;

	private EnvoiCommandesThread		mEnvoiCommandesThread;

	private boolean						bConnexion	= false;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.vocal);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		mCommandes = new ArrayAdapter<String>(this,
				R.layout.list_row_layout_even, R.id.text);

		lvCommandes = (ListView) findViewById(R.id.lvCommands);
		lvCommandes.setStackFromBottom(true);
		lvCommandes.setAdapter(mCommandes);
		lvCommandes.setCacheColorHint(0);

		this.mFileCommandes = new LinkedBlockingQueue<byte[]>();

		recupererSession();
		connecter();
	}

	private Handler	ConnexionHandler	= new Handler()
										{
											@Override
											public void handleMessage(
													Message msg)
											{

												switch (msg.what)
												{
												case 0:
													mEcranChargement.dismiss();
													if (!bConnexion)
														finish();
													break;

												case 1:
													mEcranChargement.dismiss();
													mEnvoiCommandesThread = new EnvoiCommandesThread();
													mEnvoiCommandesThread
															.start();
													bConnexion = true;
													break;
												}
											}
										};

	private Handler	mErreursHandler		= new Handler()
										{
											@Override
											public void handleMessage(
													Message msg)
											{
												arreterThreads();
												mCommunication.deconnecter();
											}
										};

	private void connecter()
	{
		mEcranChargement = ProgressDialog.show(this, "Veuillez patienter",
				"Connexion avec le robot...", true);

		this.mCommunication = new Communication(true, mErreursHandler);

		new ConnexionThread(this.mSession, this.mCommunication,
				this.ConnexionHandler).start();
	}

	private class EnvoiCommandesThread extends Thread
	{
		private final String	TAG				= "EnvoiCommandesThread >>>";
		private int				nNumeroEchange	= 1;
		private boolean			bExecution		= true;

		@Override
		public void run()
		{
			byte[] byCommande;

			while (bExecution && !Thread.currentThread().isInterrupted())
			{
				try
				{
					byCommande = mFileCommandes.take();
					
					mCommunication.envoyerCommande(byCommande, nNumeroEchange);
					this.nNumeroEchange++;
				}
				catch (InterruptedException mException)
				{
					Log.e(TAG,
							"Erreur à la récupération d'une commande sur la file correspondante",
							mException);
				}
			}
		}

		public void arreterEnvois()
		{
			while (!mFileCommandes.isEmpty())
			{
				// On attends que la file se vide
			}

			this.bExecution = false;
		}
	}

	public void command(View v)
	{
		voice();
	}

	public void voice()
	{
		Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
				RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		i.putExtra(RecognizerIntent.EXTRA_PROMPT, "Enoncez une commande !");
		startActivityForResult(i, check);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);

		boolean bValid = true;
		try
		{
			if (requestCode == check && resultCode == RESULT_OK)
			{
				ArrayList<String> results = data
						.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
				String sCommand = results.get(0);

				if (sCommand.contains("van"))
				{
					Pattern paVitesse = Pattern.compile("\\d+");
					Matcher makeMatch = paVitesse.matcher(sCommand);
					if (makeMatch.find())
					{
						String sVitesse = makeMatch.group();
						nVitesse = Integer.decode(sVitesse);
						afficherCommande("J'avance à " + sVitesse + "% !");
						this.mFileCommandes.put(Commande.SET_OUTPUT_STATE(
								PORT_A, nVitesse));
						this.mFileCommandes.put(Commande.SET_OUTPUT_STATE(
								PORT_C, nVitesse));
					}
					else
					{
						afficherCommande("J'avance !");
						this.mFileCommandes.put(Commande.SET_OUTPUT_STATE(
								PORT_A, nVitesse));
						this.mFileCommandes.put(Commande.SET_OUTPUT_STATE(
								PORT_C, nVitesse));
					}
				}
				else if (sCommand.contains("cul"))
				{
					Pattern paVitesse = Pattern.compile("\\d+");
					Matcher makeMatch = paVitesse.matcher(sCommand);
					if (makeMatch.find())
					{
						String sVitesse = makeMatch.group();
						nVitesse = Integer.decode(sVitesse);
						afficherCommande("Je recule à " + sVitesse + "% !");
						this.mFileCommandes.put(Commande.SET_OUTPUT_STATE(
								PORT_A, -nVitesse));
						this.mFileCommandes.put(Commande.SET_OUTPUT_STATE(
								PORT_C, -nVitesse));
					}
					else
					{
						afficherCommande("Je recule !");
						this.mFileCommandes.put(Commande.SET_OUTPUT_STATE(
								PORT_A, -nVitesse));
						this.mFileCommandes.put(Commande.SET_OUTPUT_STATE(
								PORT_C, -nVitesse));
					}
					lvCommandes.setAdapter(mCommandes);
				}
				else if (sCommand.contains("roi"))
				{
					afficherCommande("Je tourne à droite !");
					this.mFileCommandes.put(Commande.SET_OUTPUT_STATE(PORT_A,
							nVitesse / 2));
					this.mFileCommandes.put(Commande.SET_OUTPUT_STATE(PORT_C,
							nVitesse));
				}
				else if (sCommand.contains("che") || sCommand.contains("gau"))
				{
					afficherCommande("Je tourne à gauche !");
					this.mFileCommandes.put(Commande.SET_OUTPUT_STATE(PORT_A,
							nVitesse));
					this.mFileCommandes.put(Commande.SET_OUTPUT_STATE(PORT_C,
							nVitesse / 2));
				}
				else if (sCommand.contains("top"))
				{
					mCommandes.add("Je m'arrête !");
					this.mFileCommandes.put(Commande
							.SET_OUTPUT_STATE(PORT_A, 0));
					this.mFileCommandes.put(Commande
							.SET_OUTPUT_STATE(PORT_C, 0));

				}
			}
		}
		catch (InterruptedException e)
		{
			Log.e(TAG, "put", e);
		}

		if (bValid)
		{
			voice();
		}

	}

	private void recupererSession()
	{
		Intent thisIntent = getIntent();
		this.mSession = (Session) thisIntent.getParcelableExtra("mSession");
	}

	private void afficherCommande(String sCommande)
	{
		mCommandes.add(sCommande);
		lvCommandes.setAdapter(mCommandes);
	}

	@Override
	protected void onDestroy()
	{
		arreterThreads();
		super.onDestroy();
	}

	private void arreterThreads()
	{
		this.mEnvoiCommandesThread.arreterEnvois();
		if (this.mCommunication != null)
		{
			// Si on est connecté avec le robot
			if (this.mCommunication.estConnecte())
			{
				mCommunication.deconnecter(); // On se déconnecte
				bConnexion = false;
			}
		}
	}
}
