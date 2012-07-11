package isnbot.activities;

import isnbot.classes.GestionnaireDrawables;
import isnbot.classes.Commande;
import isnbot.classes.Communication;
import isnbot.classes.ConnexionThread;
import isnbot.classes.Session;

import java.nio.ByteBuffer;
import java.util.Arrays;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class InformationsRobotActivity extends Activity
{
	private ProgressDialog					mEcranChargement;
	private Session							mSession;
	private boolean							bConnexion					= false;

	private String							sNomRobot					= "";
	private int								nPuissanceSignal			= 0;
	private short							nNiveauBatterie				= 0;
	private String							sVersionFirmware			= "";
	private String							sVersionProtocole			= "";
	private int								nMemoireDisponible			= 0;

	private TextView						labelNomRobot;
	private TextView						labelAdresseRobot;
	private TextView						labelPuissanceSignal;
	private TextView						labelNiveauBatterie;
	private TextView						labelVersionFirmware;
	private TextView						labelVersionProtocole;
	private TextView						labelMemoireDisponible;

	private ImageView						iconePuissanceSignal;
	private ImageView						iconeNiveauBatterie;
	private ImageView						iconeMemoireDisponible;

	private RecuperateurInformationsThread	mRecuperateurInformations	= new RecuperateurInformationsThread();

	/**
	 * Handler permettant la gestion de la connexion au robot
	 */
	private Handler							mConnexionHandler			= new Handler()
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
																					mRecuperateurInformations
																							.start();
																					labelAdresseRobot
																							.setText(mSession
																									.getAdrRobot());
																					mEcranChargement
																							.dismiss();
																					break;
																				}
																			}
																		};

	private Handler							mInformationsHandler		= new Handler()
																		{
																			@Override
																			public void handleMessage(
																					Message msg)
																			{
																				afficherInformations();
																			}
																		};

	private Handler							mErreursHandler				= new Handler()
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

	private Communication					mCommunication				= new Communication(
																				true,
																				mErreursHandler);

	private void afficherInformations()
	{

		this.labelNomRobot.setText(this.sNomRobot);

		this.labelPuissanceSignal.setText(Integer
				.toString(this.nPuissanceSignal));
		this.iconePuissanceSignal.setImageDrawable(GestionnaireDrawables
				.getSignalIcone(this, this.nPuissanceSignal));

		this.labelNiveauBatterie.setText(Integer.toString(this.nNiveauBatterie)
				+ " mV");
		this.iconeNiveauBatterie.setImageDrawable(GestionnaireDrawables
				.getBatterieIcone(this, this.nNiveauBatterie));

		this.labelMemoireDisponible.setText(Integer
				.toString(this.nMemoireDisponible) + " Ko");
		this.iconeMemoireDisponible.setImageDrawable(GestionnaireDrawables
				.getMemoireIcone(this, this.nMemoireDisponible));

		this.labelVersionFirmware.setText(this.sVersionFirmware);

		this.labelVersionProtocole.setText(this.sVersionProtocole);
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.informations_robot);

		Intent thisIntent = getIntent();
		mSession = (Session) thisIntent.getParcelableExtra("mSession");

		connecter();
	}

	private void connecter()
	{
		mEcranChargement = ProgressDialog.show(this, "Veuillez patienter",
				"Connexion avec le robot...", true);

		Intent thisIntent = getIntent();
		mSession = (Session) thisIntent.getParcelableExtra("mSession");

		new ConnexionThread(this.mSession, this.mCommunication,
				this.mConnexionHandler).start();
	}

	public void onRenommerRobotClicked(View v)
	{
		Toast.makeText(this, "Non implémenté",
				Toast.LENGTH_SHORT).show();
	}

	private class RecuperateurInformationsThread extends Thread
	{
		private static final String	TAG				= "RecuperateurInformationsThread >>>";
		private boolean				bExecution		= true;
		private int					nNumeroEchange	= 0;

		public void run()
		{
			byte[] byNomRobot = new byte[15];

			while (bExecution && !Thread.currentThread().isInterrupted())
			{
				byte[] byBatteryLevel = mCommunication.envoyerCommande(
						Commande.GET_BATTERY_LEVEL(), nNumeroEchange++);

				byte[] byFirmwareVersion = mCommunication.envoyerCommande(
						Commande.GET_FIRMWARE_VERSION(), nNumeroEchange++);

				byte[] byDeviceInfo = mCommunication.envoyerCommande(
						Commande.GET_DEVICE_INFO(), nNumeroEchange++);

				short nTestBatterie = (short) ((byBatteryLevel[3] & 0xFF) | (byBatteryLevel[4] << 8));
				if ((nTestBatterie >= 0) && (nTestBatterie <= 9000)
						&& (nTestBatterie != 380))
				{
					nNiveauBatterie = nTestBatterie;
				}

				if (byFirmwareVersion != null)
				{
					sVersionFirmware = (Integer
							.toString(byFirmwareVersion[6] + 48))
							+ "."
							+ Integer.toString(byFirmwareVersion[5]);

					sVersionProtocole = (Integer
							.toString(byFirmwareVersion[4] + 48))
							+ "."
							+ Integer.toString(byFirmwareVersion[3]);
				}
				else
				{
					sVersionFirmware = "?.?";
					sVersionProtocole = "?.?";
				}

				if (byDeviceInfo != null)
				{
					System.arraycopy(byDeviceInfo, 3, byNomRobot, 0, 15);
					sNomRobot = new String(byNomRobot);

					nPuissanceSignal = ((byDeviceInfo[25] & 0xFF)
							| (byDeviceInfo[26] << 8)
							| (byDeviceInfo[27] << 16) | (byDeviceInfo[28] << 24));

					nMemoireDisponible = ((byDeviceInfo[29] & 0xFF)
							| (byDeviceInfo[30] << 8)
							| (byDeviceInfo[31] << 16) | (byDeviceInfo[32] << 24));
				}
				else
				{
					sNomRobot = "?";
				}

				mInformationsHandler.sendEmptyMessage(0);

				try
				{
					sleep(1000);
				}
				catch (InterruptedException mException)
				{
					Log.e(TAG, "sleep", mException);
				}
			}
		}

		public void arreterRecuperation()
		{
			this.bExecution = false;
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
		// On arrête le thread de récupération
		this.mRecuperateurInformations.arreterRecuperation();
		if (this.mCommunication != null)
		{
			// Si on est connecté avec le robot
			if (this.mCommunication.estConnecte())
			{
				this.mCommunication.deconnecter(); // On se déconnecte
				this.mCommunication = null; // On supprime la référence
				this.bConnexion = false;
			}
		}
	}
}
