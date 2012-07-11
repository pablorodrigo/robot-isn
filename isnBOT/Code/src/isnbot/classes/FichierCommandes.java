package isnbot.classes;

import isnbot.activities.ExecutionFichierActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

public class FichierCommandes
{
	private static final String	TAG						= "FichierCommandes";
	private Context				mContext				= null;
	private String				sNomFichier				= "";
	private int					nNombreCommandes		= 0;
	private long				lTaille					= 0;
	private long				lDerniereModification	= 00;

	public FichierCommandes(Context mContext, String sNomFichier, long lTaille,
			long lDerniereModification)
	{
		this.mContext = mContext;
		this.sNomFichier = sNomFichier;
		this.lTaille = lTaille;
		this.lDerniereModification = lDerniereModification;

		setNombreCommandes();
	}

	public String getNom()
	{
		return this.sNomFichier;
	}

	public String getNombreCommandes()
	{
		return Integer.toString(this.nNombreCommandes);
	}

	public String getTaille()
	{
		if (this.lTaille > 1000)
		{
			return Long.toString(this.lTaille / 1000).concat(" Ko");
		}
		else
		{
			return Long.toString(this.lTaille).concat(" Octets");
		}
	}

	public String getDerniereModification()
	{
		SimpleDateFormat formatter = new SimpleDateFormat(
				"yyyy/MM/dd - hh:mm:ss");
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(this.lDerniereModification);
		return formatter.format(calendar.getTime());
	}

	private void setNombreCommandes()
	{
		File mFichier = null;
		FileInputStream mFileInputStream = null;
		BufferedReader mBufferedReader = null;
		String sLigne = null;

		try
		{
			mFichier = new File(Environment.getExternalStorageDirectory()
					+ "/isnBOT/" + sNomFichier);
			mFileInputStream = new FileInputStream(mFichier);
			mBufferedReader = new BufferedReader(new InputStreamReader(
					mFileInputStream));
			sLigne = mBufferedReader.readLine();
		}
		catch (Exception mException)
		{
			Log.e(TAG, "readLine", mException);
		}

		while (sLigne != null)
		{
			this.nNombreCommandes++;
			sLigne = lireLigne(mBufferedReader);
		}

		try
		{
			mBufferedReader.close();
			mFileInputStream.close();
		}
		catch (IOException mException)
		{
			Log.e(TAG, "close", mException);
		}
	}

	/**
	 * Structure fichier utilisateur : ffffffffffff_commentaire = cette commande
	 * devrait faire avancer le robot\r\n
	 * 
	 * @param mContext
	 * @param sNomFichier
	 * @return Une List<Commande> contenant toutes les commandes contenues dans
	 *         le fichier
	 * @throws FileNotFoundException
	 */
	public ArrayList<Commande> getCommandes() throws FileNotFoundException
	{
		ArrayList<Commande> mCommandes = new ArrayList<Commande>();

		File mFichier = new File(Environment.getExternalStorageDirectory()
				+ "/isnBOT/" + this.sNomFichier);
		FileInputStream mFileInputStream = new FileInputStream(mFichier);
		BufferedReader mBufferedReader = new BufferedReader(
				new InputStreamReader(mFileInputStream));

		String sLigne = null;

		sLigne = lireLigne(mBufferedReader);

		while (sLigne != null)
		{
			int nPositionCommentaire = sLigne.indexOf("=");
			String sCommande = sLigne.substring(0, nPositionCommentaire);
			String sCommentaire = sLigne.substring(nPositionCommentaire + 1);

			mCommandes.add(new Commande(sCommande, sCommentaire));

			sLigne = lireLigne(mBufferedReader);
		}

		try
		{
			mBufferedReader.close();
			mFileInputStream.close();
		}
		catch (IOException mException)
		{
			Log.e(TAG, "close", mException);
		}

		return mCommandes;
	}

	private String lireLigne(BufferedReader mBufferedReader)
	{
		String sLigne = null;

		try
		{
			sLigne = mBufferedReader.readLine();
		}
		catch (Exception mException)
		{
			Log.e(TAG, "readLine", mException);
		}

		return sLigne;
	}

	public static ListeFichiersCommandesAdapter getAdapterFichiers(
			ExecutionFichierActivity mContext)
	{
		ArrayList<FichierCommandes> mFichiersCommandes = new ArrayList<FichierCommandes>();

		ListeFichiersCommandesAdapter mAdapter = new ListeFichiersCommandesAdapter(
				mContext, mFichiersCommandes);

		String sCheminRepertoire = Environment.getExternalStorageDirectory()
				+ "/isnBOT/";

		File mRepertoire = new File(sCheminRepertoire);

		FilenameFilter mFiltre = new FilenameFilter()
		{
			public boolean accept(File mRepertoire, String sNom)
			{
				if (!sNom.endsWith("sec"))
				{
					return true;
				}
				else
				{
					return false;
				}
			}
		};

		File[] mFichiers = mRepertoire.listFiles(mFiltre);

		if (mFichiers != null)
		{
			for (int i = 0; i < mFichiers.length; i++)
			{
				mFichiersCommandes.add(new FichierCommandes(mContext,
						mFichiers[i].getName(), mFichiers[i].length(),
						mFichiers[i].lastModified()));
			}
		}

		return mAdapter;
	}
}
