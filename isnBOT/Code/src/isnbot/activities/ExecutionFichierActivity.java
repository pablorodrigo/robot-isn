package isnbot.activities;

import isnbot.classes.Commande;
import isnbot.classes.Communication;
import isnbot.classes.ConnexionThread;
import isnbot.classes.FichierCommandes;
import isnbot.classes.ListeFichiersCommandesAdapter;
import isnbot.classes.Session;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.app.TabActivity;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

public class ExecutionFichierActivity extends TabActivity
{
	public static final String				TAG						= "ExecutionFichierActivity >>>";
	// Attributs d'interface
	private ProgressDialog					mEcranChargement;
	private ListView						labelListeFichiers;
	private TextView						tvValeurCommentaire;
	private TextView						tvValeurCommande;
	private TextView						tvLabelCommande;
	private TextView						tvValeurReponse;

	private Communication					mCommunication;

	// Réponse du robot à chaque commande envoyée
	private byte[]							byReponse;
	private byte[]							byCommande;

	public ArrayList<Commande>				mCommandes;
	private Commande						mCommandeEnAttente;

	public int								nIndexCommandeEnAttente	= 0;
	public boolean							bPremiereOuverture;

	private boolean							bConnexion;

	private Session							mSession;

	private int								nNumeroEchange			= 1;

	private ListeFichiersCommandesAdapter	mAdapter;

	private TabHost							tabHost;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		initialiserSession();
		initialiserOnglets();

		connecter();
	}

	private void connecter()
	{
		mEcranChargement = ProgressDialog.show(this, "Veuillez patienter",
				"Connexion avec le robot...", true);

		this.mCommunication = new Communication(true, mErreursHandler);

		new ConnexionThread(this.mSession, this.mCommunication,
				this.mConnexionHandler).start();
	}

	/**
	 * Handler permettant la gestion de la connexion au robot
	 */
	private Handler	mConnexionHandler	= new Handler()
										{
											@Override
											public void handleMessage(
													Message msg)
											{
												switch (msg.what)
												{
												// La connexion a échouée
												case 0:
													// Retour au menu
													mEcranChargement.dismiss();
													if (!bConnexion)
														finish();
													break;
												// La connexion a réussie
												case 1:
													bConnexion = true;
													mEcranChargement.dismiss();
													break;
												}
											}
										};

	private Handler	mCommandeHandler	= new Handler()
										{
											@Override
											public void handleMessage(
													Message msg)
											{
												mEcranChargement.dismiss();
												switch (msg.what)
												{
												// L'envoi de la commande a
												// échoué
												case 0:
													// On averti l'utilisateur
													Toast.makeText(
															getBaseContext(),
															"Erreur à l'envoi de la commande !",
															Toast.LENGTH_SHORT)
															.show();
													break;

												case 1:
													tvValeurReponse
															.setText(Commande
																	.getAffichable(byReponse));
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

	private class EnvoiCommandeThread extends Thread
	{
		private byte[]	byCommande;

		public EnvoiCommandeThread(byte[] byCommande)
		{
			this.byCommande = byCommande;
		}

		public void run()
		{
			int nBufferedNumeroEchange = nNumeroEchange;
			nNumeroEchange++;
			byReponse = mCommunication.envoyerCommande(this.byCommande,
					nBufferedNumeroEchange);
			mCommandeHandler.sendEmptyMessage(1);
		}
	}

	public void afficherCommande()
	{
		if (this.mCommandes.size() != 0)
		{
			try
			{
				this.mCommandeEnAttente = this.mCommandes
						.get(this.nIndexCommandeEnAttente);
				this.byCommande = this.mCommandeEnAttente.getCommande();
				this.tvValeurCommande.setText(Commande
						.getAffichable(this.byCommande));
				this.tvLabelCommande.setText("Commande "
						+ Integer.toString(this.nIndexCommandeEnAttente + 1)
						+ "/" + this.mCommandes.size() + " :");
				this.tvValeurCommentaire.setText(this.mCommandeEnAttente
						.getCommentaire());
				
				if (this.mCommandeEnAttente.getCommande().length != 1)
				{
					this.tvValeurReponse.setText("Commande non envoyée");
				}
				else
				{
					this.tvValeurReponse.setText("Commande invalide");
				}
			}
			catch (IndexOutOfBoundsException mException)
			{
				Log.e("afficherCommande()", "IndexOutOfBoundsException : "
						+ mException);
			}

			this.tabHost.setCurrentTab(1);
		}
		else
		{
			Toast.makeText(getBaseContext(),
					"Aucune commande n'a été trouvée !", Toast.LENGTH_SHORT)
					.show(); // On averti l'utilisateur
		}
	}

	public void onCommandePrecedenteClicked(View v)
	{
		if (this.bPremiereOuverture)
		{
			if (this.nIndexCommandeEnAttente > 0)
			{
				this.nIndexCommandeEnAttente--;
				afficherCommande();
			}
			else
			{
				Toast.makeText(getBaseContext(),
						"Aucune commande précédente !", Toast.LENGTH_SHORT)
						.show(); // On averti l'utilisateur
			}
		}
		else
		{
			Toast.makeText(getBaseContext(),
					"Veuillez sélectionner un fichier", Toast.LENGTH_SHORT)
					.show(); // On averti l'utilisateur
		}
	}

	public void onCommandeSuivanteClicked(View v)
	{
		if (this.bPremiereOuverture)
		{
			if (this.nIndexCommandeEnAttente < mCommandes.size() - 1)
			{
				this.nIndexCommandeEnAttente++;
				afficherCommande();
			}
			else
			{
				Toast.makeText(getBaseContext(), "Aucune commande suivante !",
						Toast.LENGTH_SHORT).show(); // On averti l'utilisateur
			}
		}
		else
		{
			Toast.makeText(getBaseContext(),
					"Veuillez sélectionner un fichier", Toast.LENGTH_SHORT)
					.show(); // On averti l'utilisateur
		}
	}

	public void onEnvoyerCommandeClicked(View v)
	{
		if (this.bPremiereOuverture)
		{
			new EnvoiCommandeThread(mCommandes
					.get(this.nIndexCommandeEnAttente).getCommande()).start();
		}
		else
		{
			Toast.makeText(getBaseContext(),
					"Veuillez sélectionner un fichier", Toast.LENGTH_SHORT)
					.show(); // On averti l'utilisateur
		}

	}

	public void onArretUrgenceClicked(View v)
	{
		Toast.makeText(getBaseContext(), "Arrêt d'urgence !",
				Toast.LENGTH_SHORT).show(); // On averti l'utilisateur

		byte[] byArretUrgence = new byte[]
		{ 0x00, 0x04, (byte) 0xFF, 0x00, 0x07, 0x00, 0x00, 0x00, 0x00, 0x00,
				0x00, 0x00 };
		new EnvoiCommandeThread(byArretUrgence).start();
	}

	/**
	 * Récupère les informations de la session via l'Intent passé par l'Activity
	 * précédente et initialise l'attribut mSession
	 */
	private void initialiserSession()
	{
		Intent thisIntent = getIntent();
		mSession = (Session) thisIntent.getParcelableExtra("mSession");
	}

	private void initialiserOnglets()
	{
		tabHost = getTabHost();
		LayoutInflater.from(this).inflate(R.layout.fichier_commandes,
				tabHost.getTabContentView(), true);

		tabHost.addTab(tabHost
				.newTabSpec("Sélection d'un fichier")
				.setIndicator(
						"",
						getResources().getDrawable(
								R.drawable.ic_menu_sort_by_size))
				.setContent(R.id.llChoixFichier));

		tabHost.addTab(tabHost
				.newTabSpec("Envoi des commandes")
				.setIndicator("",
						getResources().getDrawable(R.drawable.ic_menu_goto_big))
				.setContent(R.id.llEnvoiCommandes));

		for (int i = 0; i < tabHost.getTabWidget().getTabCount(); i++)
		{
			tabHost.getTabWidget().getChildAt(i).getLayoutParams().height = 70;
		}

		this.tvValeurCommande = (TextView) findViewById(R.id.tvCommande);
		this.tvLabelCommande = (TextView) findViewById(R.id.tvHeaderCommande);
		this.tvValeurCommentaire = (TextView) findViewById(R.id.tvCommentaire);
		this.tvValeurReponse = (TextView) findViewById(R.id.tvReponse);

		this.labelListeFichiers = (ListView) findViewById(R.id.lvFichiersCommandes);
		int[] colors =
		{ 0xFF005893, 0xFF0073BF, 0xFF0091F0 }; // red for the example
		labelListeFichiers.setDivider(new GradientDrawable(
				Orientation.LEFT_RIGHT, colors));
		labelListeFichiers.setDividerHeight(1);
		this.labelListeFichiers.setClickable(true);

		this.labelListeFichiers.setAdapter(FichierCommandes
				.getAdapterFichiers(this));
	}

	private void arreterThreads()
	{
		if (this.mCommunication != null)
		{
			// Si on est connecté avec le robot
			if (this.mCommunication.estConnecte())
			{
				this.mCommunication.deconnecter(); // On se déconnecte
				this.mCommunication = null;
				this.bConnexion = false;
			}
		}
	}

	@Override
	protected void onDestroy()
	{
		arreterThreads();
		super.onDestroy();
	}
}
