package isnbot.classes;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import android.os.Environment;
import android.util.Log;

/**
 * Classe permettant la cr�ation des fichiers de logs li�s � une session ainsi
 * que leur gestion, � savoir l'�criture des commandes/r�ponses ainsi que la
 * r�cup�ration des commandes plac�s dans un fichier de commandes rempli
 * manuellement.
 * 
 * La gestion des �critures se fait par l'int�rm�diaire de deux
 * LinkedBlockingQueue (files), une pour l'�criture des commandes et l'autre
 * pour l'�criture des r�ponses. L'appel des m�thodes ecrireCommande() et
 * ecrireReponse() est donc non bloquant puisque ce sont des threads qui sont
 * charg�s de r�cup�r�s les donn�es plac�es dans ces files par ces m�thodes les
 * placer dans les fichiers avec les informations qui leurs sont li�es.
 * 
 * Cette m�thode permet de "threader" les grosses suites d'instructions, telles
 * que le calcul de l'heure jusqu'aux milli secondes, et de l'�criture dans les
 * fichiers. Les threads d'�criture sont internes car ils permettent une
 * utilisation avanc�es des attributs de la classe FichiersLogs.
 * 
 * @file FichiersLogs.java
 * @brief Classe permettant la gestion des fichiers de logs li�s � une session.
 * 
 * @author Maxime BOUCHENOIRE
 * @author STS IRIS, Lyc�e Nicolas APPERT, ORVAULT (FRANCE)
 * @since 2012-01-06
 * @version 1.0
 * @date 2012-04-04
 * 
 * @bug <date du bug> - <CORRIGE> - <Intitul� pr�cis du bug>
 * 
 * @see ouvrir()
 * @see ecrireCommande()
 * @see ecrireReponse()
 * @see fermer()
 */
public class FichiersLogs
{
	/**
	 * Thread permettant la cr�ation des fichiers de logs et le lancement des
	 * threads d'�criture si l'op�ration s'est d�roul�e correctement. Le
	 * r�sultat de cette cr�ation et du lancement des threads sera plac� dans
	 * une file bloquante, qui sera lue et dont le r�sultat sera retourn� par la
	 * fonction ouvrir() (qui aura au pr�alable lanc� le thread d'ouverture), de
	 * telle sorte que l'on connaisse le d�roulement de ce processus en dehors
	 * du thread.
	 * 
	 * @file FichiersLogs.java
	 * @brief Classe permettant la cr�ation des fichiers de logs et le lancement
	 *        des threads d'�criture.
	 * 
	 * @author Maxime BOUCHENOIRE
	 * @author STS IRIS, Lyc�e Nicolas APPERT, ORVAULT (FRANCE)
	 * @since 2012-01-06
	 * @version 1.0
	 * @date 2012-04-04
	 * 
	 * @bug <date du bug> - <CORRIGE> - <Intitul� pr�cis du bug>
	 */
	private class OuvertureFichiersThread extends Thread
	{
		/** TAG de la classe pour lecture des logs plus clair */
		private String	TAG;

		public OuvertureFichiersThread()
		{
			this.TAG = "FichiersLogs.CreationThread >>>";
		}

		@Override
		public void run()
		{
			// On cr�� et ouvre les fichiers de logs
			if (creerFichiersLogs())
			{
				relayerReussiteCreation();
			}
			else
			{
				relayerEchecCreation();
			}

			yield();
		}

		private void relayerReussiteCreation()
		{
			// On lance les threads d'�criture si la cr�ation des
			// fichiers de logs s'est d�roul�e correctement
			lancerThreadsEcriture();

			Log.i(TAG,
					"La cr�ation des fichiers de logs s'est d�roul�e correctement");

			try
			{
				mFileCreation.put(new Boolean(true));
			}
			catch (InterruptedException mException)
			{
				Log.e(TAG,
						"Echec � la pr�vention de la r�ussite de la cr�ation des fichiers de logs : "
								+ mException);
			}
		}

		private void relayerEchecCreation()
		{
			Log.i(TAG, "La cr�ation des fichiers de logs s'est mal d�roul�e");

			try
			{
				mFileCreation.put(new Boolean(false));
			}
			catch (InterruptedException mException)
			{
				Log.e(TAG,
						"Echec � la pr�vention de l'�chec de la cr�ation des fichiers de logs : "
								+ mException);
			}
		}

		/**
		 * Proc�dure appel�e dans le thread si la cr�ation des fichiers de log
		 * s'est d�roul�e avec succ�s. Initialise les files, initialise et
		 * lances les threads d'�criture, ainsi que le s�maphore de gestion des
		 * acc�s aux fichiers de logs.
		 * 
		 * @post Les files de commandes/r�ponses doivent �tre initialis�es, de
		 *       m�me pour le s�maphore d'acc�s aux fichiers de logs. Les
		 *       threads d'�criture doivent �galement �tre lanc�s.
		 * 
		 * @test Voir la proc�dure dans le fichier associ�.
		 * @see mFileCommandes
		 * @see mFileReponses
		 * @see mSemaphoreFichiers
		 * @see CommandesThread
		 * @see ReponsesThread
		 */
		private void lancerThreadsEcriture()
		{
			/*
			 * // On instancie les files des commandes/r�ponses mFileCommandes =
			 * new LinkedBlockingQueue<byte[]>(); mFileReponses = new
			 * LinkedBlockingQueue<byte[]>();
			 * 
			 * // On initialise et instancie les threads de commandes/r�ponses
			 * mEcritureReponsesThread = new EcritureReponsesThread();
			 * mEcritureCommandesThread = new EcritureCommandesThread();
			 * 
			 * // On initialise le s�maphore d'acc�s aux fichiers de logs
			 * mSemaphoreFichiers = new Semaphore(1);
			 * 
			 * // On d�marre les threads d'�criture des commandes/r�ponses
			 * mEcritureCommandesThread.start();
			 * mEcritureReponsesThread.start();
			 */

			mFileItems = new LinkedBlockingQueue<byte[]>();

			mEcritureThread = new EcritureThread();

			mEcritureThread.start();
		}

		/**
		 * Cr�e le r�pertoire de la session, ainsi que les deux fichiers de log
		 * (ASCII et binaire). Initialise �galement les deux flux attributs de
		 * la classe FichiersLogs permettants d'inscrire des commandes et
		 * r�ponses dans ces fichiers.
		 * 
		 * @post Les fichiers de logs doivent �tre cr��s et les buffers
		 *       exploitables.
		 * 
		 * @return VRAI si les fichiers ont �t� cr�es, FAUX sinon (car ils
		 *         �xistent d�j�)
		 */
		private boolean creerFichiersLogs()
		{
			boolean bRetour = true;

			// On r�cup�re la racine du stockage externe du t�l�phone
			final File mRacine = Environment.getExternalStorageDirectory();
			// Cr�ation du r�pertoire des fichiers de session
			File applicationRepertoire = new File(mRacine + "/isnBOT");
			// Si le r�pertoire n'�xiste pas
			if (!applicationRepertoire.exists())
			{
				applicationRepertoire.mkdir(); // On cr�e le r�pertoire
			}

			final File repertoireApplication = Environment
					.getExternalStorageDirectory();

			// Cr�ation du r�pertoire de la session
			sCheminAbsolu = repertoireApplication + "/isnBOT/" + sNom;
			File repertoireSession = new File(sCheminAbsolu);

			if (!repertoireSession.exists()) // Si la session n'�xiste pas
			{
				repertoireSession.mkdir();
			}
			else
			// Si la session �xiste
			{
				Log.w(TAG,
						"La session �xiste d�j�, on utilisera les fichiers d�j� existants");
			}

			// Cr�ation du couple de fichier
			File mFichierAscii = new File(repertoireApplication + "/isnBOT/"
					+ sNom, sNom + ".txt");
			File mFichierBinaire = new File(repertoireApplication + "/isnBOT/"
					+ sNom, sNom + ".bin");

			// Initialisation des flux attributs
			try
			{
				if (repertoireApplication.canWrite() && mFichierAscii != null
						&& mFichierBinaire != null)
				{
					final FileWriter mAsciiWriter = new FileWriter(
							mFichierAscii, true);
					final FileWriter mBinaireWriter = new FileWriter(
							mFichierBinaire, true);

					mBufferAscii = new BufferedWriter(mAsciiWriter);
					mBufferBinaire = new BufferedWriter(mBinaireWriter);
				}
				else
				{
					Log.e(TAG,
							"La condition (repertoireApplication.canWrite() && mFichierAscii != null && mFichierBinaire != null) retourne FAUX");
					bRetour = false;
				}
			}
			catch (Exception mException)
			{
				bRetour = false;
				Log.e(TAG,
						"Erreur � l'initialisation des buffers d'�criture dans les fichiers de logs : ",
						mException);
			}

			return bRetour;
		}
	}

	/**
	 * Thread permettant l'�criture des items plac�es sur la file correspondante
	 * dans les fichiers de logs. Il y ajoute leur en-t�te contenant l'heure de
	 * l'�criture et le num�ro de l'�change.
	 * 
	 * @file FichiersLogs.java
	 * @brief Thread permettant l'�criture des items plac�es sur la file
	 *        correspondante dans les fichiers de logs.
	 * 
	 * @author Maxime BOUCHENOIRE
	 * @author STS IRIS, Lyc�e Nicolas APPERT, ORVAULT (FRANCE)
	 * @since 2012-01-06
	 * @version 1.0
	 * @date 2012-04-04
	 * 
	 * @bug <date du bug> - <CORRIGE> - <Intitul� pr�cis du bug>
	 */
	private class EcritureThread extends Thread
	{
		/** TAG de la classe pour lecture des logs plus clair */
		private String	TAG;
		/**
		 * Boolean permettant de boucler le thread d'�criture, et ainsi
		 * l'arr�ter lorsque n�cessaire.
		 */
		private boolean	bExecution	= true;

		public EcritureThread()
		{
			this.TAG = "FichiersLogs.EcritureThread >>>";
		}

		@Override
		public void run()
		{
			while (this.bExecution && !Thread.currentThread().isInterrupted())
			{
				// Gestion en boucle de la file des commandes
				gererFileItems();
				yield();
			}

		}

		/**
		 * Proc�dure appel�e dans son thread, g�re la r�cup�ration des commandes
		 * dans le thread avec leur num�ro d'�change, et les �cris dans les
		 * fichiers lorsque le s�maphore est disponible.
		 * 
		 * @post Les la commande en t�te de sa file doit �tre �crite dans les
		 *       fichiers de logs.
		 * 
		 * @test Voir la proc�dure dans le fichier associ�.
		 * @see mFileCommandes
		 * @see mSemaphoreFichiers
		 */
		private void gererFileItems()
		{
			try
			{
				// On r�cup�re la commande et son num�ro d'�change dans sa file
				byte[] byDonneesEncapsulees = mFileItems.take();

				// Commande ou r�ponse ?
				byte byCommandeOuReponse = byDonneesEncapsulees[0];
				// On y �xtrait l'heure d'envoi/r�ception de l'item
				long lMillisecondesDepuis1970 = decapsulerHeure(byDonneesEncapsulees);
				// On y �xtrait le num�ro d'�change
				int nNumeroEchange = decapsulerNumeroEchange(byDonneesEncapsulees);
				// On y �trait la commande
				byte[] byItem = decapsulerItem(byDonneesEncapsulees);

				String tag = (byCommandeOuReponse == 0 ? TAG_COMMANDE
						: TAG_REPONSE);

				// On �crit l'item commande et son en-t�te
				ecrire(lMillisecondesDepuis1970, tag, byCommandeOuReponse,
						nNumeroEchange, byItem);

				// On c�de la priorit� car le traitement est termin�
				yield();
			}
			catch (InterruptedException mException)
			{
				Log.e(TAG,
						"Erreur � la r�cup�ration d'une commande sur la file correspondante, ou du s�maphore : ",
						mException);
			}
		}

		/**
		 * Proc�dure bloquante appel�e de l'�xterieur afin d'arr�ter ce thread
		 * d'�criture. La m�thode se d�bloquera quand le thread aura fini de
		 * traiter toutes les commandes situ�es dans leur file. Le thread sera
		 * �galement arr�t� durant cette proc�dure.
		 * 
		 * @post La file des commandes doit �tre vide et son thread d'�criture
		 *       arr�t�.
		 * 
		 * @test Voir la proc�dure dans le fichier associ�.
		 * @see mFileCommandes
		 * @see mEcritureCommandesThread
		 */
		public void arreterEcriture()
		{
			// Tant qu'il y a des items dans la file
			while (!mFileItems.isEmpty()
					&& !Thread.currentThread().isInterrupted())
			{
				// On attends qu'elle soit vid�e par le thread
			}
			// On stop la bouche du thread
			this.bExecution = false;
			// On vide la file par pr�caution
			mFileItems.clear();
			// On facilite la t�che au ramasse miettes
			mFileItems = null;
		}
	}

	/** TAG de la classe pour lecture des logs plus clair */
	private final String				TAG;
	/** TAG pour l'�criture d'une commande */
	private final String				TAG_COMMANDE;
	/** TAG pour l'�criture d'une r�ponse */
	private final String				TAG_REPONSE;
	/** TRUE si les fichiers sont ouverts, FAUX sinon */
	private boolean						bOuverts;
	/** Nom de la session */
	private String						sNom;
	/** Chemin absolu du r�pertoire de la session */
	private String						sCheminAbsolu;
	/** Thread d'ouverture des fichiers de logs */
	private OuvertureFichiersThread		mOuvertureFichiersThread;
	/** Thread d'�criture des items dans les fichiers de logs */
	private EcritureThread				mEcritureThread;
	/** File de la cr�ation des fichiers de logs */
	private SynchronousQueue<Boolean>	mFileCreation;
	/** File contenant les items � �crire dans les fichiers de logs */
	private LinkedBlockingQueue<byte[]>	mFileItems;
	/** Buffer permettant l'�criture dans le fichier au format ASCII */
	private BufferedWriter				mBufferAscii;
	/** Buffer permettant l'�criture dans le fichier au format binaire */
	private BufferedWriter				mBufferBinaire;

	/**
	 * Constructeur de la classe FichiersLogs, il permet simplement
	 * d'initialiser l'attribut correspondant au nom de la session.
	 * 
	 * @post L'attribut sNom doit �tre initialis� correctement
	 * @param mSession
	 *            Session utilis� pour creer et/ou utiliser les fichiers de
	 *            logs.
	 * @test Voir la proc�dure dans le fichier associ�.
	 * @see mSession
	 */
	public FichiersLogs(final Session mSession)
	{
		this.TAG = "FichiersLogs >>>";
		this.TAG_COMMANDE = "_commande";
		this.TAG_REPONSE = "_reponse";
		this.sCheminAbsolu = null;
		this.mOuvertureFichiersThread = null;
		this.mEcritureThread = null;
		this.mFileCreation = null;
		this.mBufferAscii = null;

		if (mSession != null)
		{
			// On cr�e le String correspondant � la session
			this.sNom = mSession.getUtilisateur() + "_" + mSession.getAnnee()
					+ "_" + mSession.getMois() + "_" + mSession.getJour() + "_"
					+ mSession.getHeures() + "h_" + mSession.getMinutes()
					+ "min_" + mSession.getMinutes() + "sec";
		}
		else
		{
			Log.e(TAG,
					"Erreur au constructeur, la Session pass�e en param�tre est null");
		}

		if (this.sNom == null)
		{
			Log.e(TAG,
					"Valeur du nom de la session = null � l'initialisation de l'attribut");
		}
	}

	/**
	 * Destructeur de la classe FichiersLogs, facilite l�g�rement la t�che du
	 * ramasse miette.
	 */
	public void finalize()
	{
		// On facilite l�g�rement la t�che au ramasse miettes
		this.sNom = null;
		this.sCheminAbsolu = null;
	}

	/**
	 * Fonction appel�e en boucle si l'on est en mode PACKET_STREAM_CONNECTED
	 * afin de r�cup�rer les donn�es sur l'InputStream du socket Bluetooth et
	 * les placer dans la file de lecture.
	 * 
	 * @pre L'attribut InputStream doit �tre initialis� (donc le socket doit
	 *      �tre connect�)
	 * @post La file de lecture doit �tre vide et le boolean de bouclage � FAUX,
	 *       donc le thread va s'arr�ter.
	 * 
	 * @return La r�ponse � une commande du robot sous le protocole LCP.
	 * @retval NULL si aucune r�ponse n'est disponible
	 * 
	 * @test Voir la proc�dure dans le fichier associ�.
	 * @see arreterIOThreads()
	 * @see OutputStream
	 */
	public boolean ouvrir()
	{
		// Si le constructeur n'a pas �t� appel�, et donc que l'attribut
		// correspondant au nom de la session n'est pas initialis�, on retourne
		// directement FAUX. On retourne �galement FAUX si le nom d'utilisateur
		// ne correspond pas aux attentes du pattern.
		;
		if ((this.sNom == null)
				|| (Pattern.compile("[a-zA-z]+([ '-][a-zA-Z]+)*").matcher(
						this.sNom).matches()))
		{
			Log.e(TAG,
					"Le nom d'utilisateur est incoh�rent � l'ouverture des fichiers de logs");
			return false;
		}

		// On admet la r�ussite de la cr�ation des fichiers
		boolean bRetour = true;

		// On instancie la file de cr�ation
		this.mFileCreation = new SynchronousQueue<Boolean>();

		// On instancie et d�marre le thread de cr�ation;
		this.mOuvertureFichiersThread = new OuvertureFichiersThread();
		this.mOuvertureFichiersThread.start();

		// On itinialise le Boolean qui recevra la valeur contenu dans la file
		// de cr�ation
		Boolean bCreationFichiers = null;

		try
		{
			// On attends l'information sur la r�ussite ou non de la cr�ation
			// des fichiers
			bCreationFichiers = this.mFileCreation.poll(500,
					TimeUnit.MILLISECONDS);
		}
		catch (InterruptedException mException)
		{
			Log.e(TAG,
					"Erreur � la r�cup�ration de l'information sur la cr�ation des fichiers de logs.",
					mException);
		}

		// Si aucune �rreur n'a eu lieu lors de la r�cup�ration du Boolean
		// correspondant � l'�tat de la cr�ation des fichiers
		if (bCreationFichiers != null)
		{
			// On transtype le Boolean en boolean
			bRetour = bCreationFichiers.booleanValue();
		}
		else
		// Si les fichiers n'ont pas �t� cr��s
		{
			bRetour = false;
			Log.e(TAG, "Boolean bCreationFichiers = null");
		}

		// On d�truit la file de cr�ation pour aider le ramasse miettes
		this.mFileCreation.clear();
		this.mFileCreation = null;

		this.bOuverts = bRetour;
		return bRetour;
	}

	/**
	 * Arr�te les threads d'�criture
	 * 
	 * @return VRAI si les flux ont �t� ferm�s, FAUX sinon
	 */
	public boolean fermer()
	{
		this.bOuverts = false;
		boolean bFermeture = true;

		if (this.bOuverts)
		{
			this.mEcritureThread.arreterEcriture();
			this.mEcritureThread = null;

			try
			{
				this.mBufferAscii.flush();
				this.mBufferBinaire.flush();
				this.mBufferAscii.close();
				this.mBufferBinaire.close();
				this.mBufferAscii = null;
				this.mBufferBinaire = null;
			}
			catch (IOException mException)
			{
				bFermeture = false;
				Log.e(TAG,
						"Erreur au flush/fermeture des buffers � l'appel de fermer()",
						mException);
			}

		}

		return bFermeture;
	}

	private boolean ecrire(long lMillisecondesDepuis1970,
			String sCommandeOuReponse, byte byCommandeOuReponse,
			int nNumeroEchange, byte[] byItem)
	{

		boolean bEcritureOk = true;

		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(lMillisecondesDepuis1970);

		SimpleDateFormat formatter = new SimpleDateFormat("hh:mm:ss:SSS");
		final String sTime = formatter.format(calendar.getTime());

		// D�termination de l'heure de l'envoi de la commande, jusqu'aux
		// millisecondes
		final int nPremierEspace = sTime.indexOf(":", 0); // 2
		final int nDeuxiemeEspace = sTime.indexOf(":", nPremierEspace + 1); // 5
		final int nTroisiemeEspace = sTime.indexOf(":", nDeuxiemeEspace + 1); // 8
		final String sHeures = sTime.substring(0, nPremierEspace);
		final String sMinutes = sTime.substring(nPremierEspace + 1,
				nDeuxiemeEspace);
		final String sSecondes = sTime.substring(nDeuxiemeEspace + 1,
				nTroisiemeEspace);
		final String sMilliSecondes = sTime.substring(nTroisiemeEspace + 1);
		final int nHeures = Integer.parseInt(sHeures);
		final int nMinutes = Integer.parseInt(sMinutes);
		final int nSecondes = Integer.parseInt(sSecondes);
		final int nMilliSecondes = Integer.parseInt(sMilliSecondes);

		try
		{
			// On �crit "l'en t�te" de l'item dans le fichier ASCII
			this.mBufferAscii.write(sTime + sCommandeOuReponse
					+ String.valueOf(nNumeroEchange) + " = ");

			// On �crit "l'en t�te" de l'item dans le fichier binaire
			this.mBufferBinaire.write((byte) nHeures);
			this.mBufferBinaire.write((byte) nMinutes);
			this.mBufferBinaire.write((byte) nSecondes);
			this.mBufferBinaire.write((short) nMilliSecondes);
			this.mBufferBinaire.write(byCommandeOuReponse);
			this.mBufferBinaire.write((short) nNumeroEchange);

			// Pour chaque octet du tableau d'octets
			for (int i = 0; i < byItem.length; i++)
			{
				// On �crit l'octet dans le fichier ASCII
				this.mBufferAscii.write(Commande.byteToString(byItem[i]) + " ");
				// On �crit l'octet dans le fichier binaire
				this.mBufferBinaire.write(byItem[i]);
			}

			// On fait un retour � la ligne pour le fichier ASCII
			this.mBufferAscii.write("\r\n");
			// On fait un "retour � la ligne" pour le fichier binaire
			this.mBufferBinaire.write("\r\n");
		}
		catch (IOException mException)
		{
			Log.e(TAG, "Erreur � l'�criture d'une" + sCommandeOuReponse
					+ " sur les buffers", mException);
		}

		try
		{
			this.mBufferAscii.flush();
			this.mBufferBinaire.flush();
		}
		catch (IOException mException)
		{
			Log.e(TAG, "Erreur au flush des buffers lors de l'�criture d'une"
					+ sCommandeOuReponse + " ", mException);
		}

		return bEcritureOk;

	}

	public boolean ecrireCommande(byte[] byCommande, int nNumeroEchange,
			long lMillisecondesDepuis1970)
	{
		if (!this.bOuverts)
		{
			Log.e(TAG,
					"Les fichiers de logs ne sont pas ouverts, �criture impossible");
			return false;
		}
		if (byCommande == null)
		{
			return false;
		}

		boolean bEcriture = true;

		try
		{
			byte[] byItemEncapsule = encapsulerItem(byCommande, nNumeroEchange,
					lMillisecondesDepuis1970, (byte) 0);

			if (byItemEncapsule != null)
			{
				this.mFileItems.put(byItemEncapsule);
			}
			else
			{
				bEcriture = false;
			}

			Thread.yield();

		}
		catch (InterruptedException mException)
		{
			bEcriture = false;
			Log.e(TAG,
					"Erreur au placement d'une commande dans la file correspondante",
					mException);
		}

		return bEcriture;
	}

	public boolean ecrireReponse(byte[] byReponse, int nNumeroEchange,
			long lMillisecondesDepuis1970)
	{
		if (!this.bOuverts)
		{
			Log.e(TAG,
					"Les fichiers de logs ne sont pas ouverts, �criture impossible");
			return false;
		}

		boolean bEcriture = true;

		try
		{
			byte[] byItemEncapsule = encapsulerItem(byReponse, nNumeroEchange,
					lMillisecondesDepuis1970, (byte) 1);

			if (byItemEncapsule != null)
			{
				this.mFileItems.put(byItemEncapsule);
			}
			else
			{
				bEcriture = false;
			}

			Thread.yield();
		}
		catch (InterruptedException mException)
		{
			bEcriture = false;
			Log.e(TAG,
					"Erreur au placement d'une r�ponse dans la file correspondante",
					mException);
		}

		return bEcriture;
	}

	private byte[] encapsulerItem(byte[] byItem, int nNumeroEchange,
			long lMillisecondesDepuis1970, byte byComandeOuReponse)
	{
		// Si un des deux param�tres est NULL
		if (byItem == null)
		{
			Log.e(TAG,
					" La donn�e dont il faut ajouter le num�ro d'�change est �gale � NULL");
			// On quitte imm�diatement la fonction
			return null;
		}

		// On transforme le long en byte[] pour pouvoir le metre dans la file
		byte byMillisecondesDepuis1970[] = ByteBuffer.allocate(8)
				.putLong(lMillisecondesDepuis1970).array();

		// On transforme l'int en byte[] pour pouvoir le metre dans la file
		byte byNumeroEchange[] = ByteBuffer.allocate(4).putInt(nNumeroEchange)
				.array();

		// On r�cup�re la taille de la commande/r�ponse
		int nTailleItem = byItem.length;
		// On cr�e un nouveau tableau d'une taille ad�quate
		byte[] byItemEncapsule = new byte[1 + 8 + 4 + nTailleItem];
		// On copie les tableaux d'octets les uns apr�s les autres
		byItemEncapsule[0] = byComandeOuReponse;
		System.arraycopy(byMillisecondesDepuis1970, 0, byItemEncapsule, 1, 8);
		System.arraycopy(byNumeroEchange, 0, byItemEncapsule, 9, 4);
		System.arraycopy(byItem, 0, byItemEncapsule, 13, nTailleItem);

		// On retourne le nouveau tableau
		return byItemEncapsule;
	}

	public void logByteArray(byte[] byItem, String sNom)
	{
		String sDonnees = Integer.toString(byItem[0] & 0xFF, 16) + "-";
		for (int i = 1; i < byItem.length; i++)
		{
			sDonnees += Integer.toString(byItem[i] & 0xFF, 16) + "-";
		}
		Log.i(TAG, sNom + " : " + sDonnees);
	}

	private long decapsulerHeure(byte[] byItemEncapsule)
	{
		byte[] byHeure = new byte[8];
		System.arraycopy(byItemEncapsule, 1, byHeure, 0, 8);

		return ByteBuffer.wrap(byHeure).getLong();
	}

	private int decapsulerNumeroEchange(byte[] byItemEncapsule)
	{
		byte[] byNumeroEchange = new byte[4];
		System.arraycopy(byItemEncapsule, 9, byNumeroEchange, 0, 4);

		return ByteBuffer.wrap(byNumeroEchange).getInt();
	}

	private byte[] decapsulerItem(byte[] byItemEncapsule)
	{
		int nTailleDonnees = byItemEncapsule.length - (1 + 8 + 4);
		byte[] byDonnes = new byte[nTailleDonnees];
		System.arraycopy(byItemEncapsule, 13, byDonnes, 0, nTailleDonnees);

		return byDonnes;
	}

	public boolean sontOuverts()
	{
		return this.bOuverts;
	}
}
