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
 * Classe permettant la création des fichiers de logs liés à une session ainsi
 * que leur gestion, à savoir l'écriture des commandes/réponses ainsi que la
 * récupération des commandes placés dans un fichier de commandes rempli
 * manuellement.
 * 
 * La gestion des écritures se fait par l'intérmédiaire de deux
 * LinkedBlockingQueue (files), une pour l'écriture des commandes et l'autre
 * pour l'écriture des réponses. L'appel des méthodes ecrireCommande() et
 * ecrireReponse() est donc non bloquant puisque ce sont des threads qui sont
 * chargés de récupérés les données placées dans ces files par ces méthodes les
 * placer dans les fichiers avec les informations qui leurs sont liées.
 * 
 * Cette méthode permet de "threader" les grosses suites d'instructions, telles
 * que le calcul de l'heure jusqu'aux milli secondes, et de l'écriture dans les
 * fichiers. Les threads d'écriture sont internes car ils permettent une
 * utilisation avancées des attributs de la classe FichiersLogs.
 * 
 * @file FichiersLogs.java
 * @brief Classe permettant la gestion des fichiers de logs liés à une session.
 * 
 * @author Maxime BOUCHENOIRE
 * @author STS IRIS, Lycée Nicolas APPERT, ORVAULT (FRANCE)
 * @since 2012-01-06
 * @version 1.0
 * @date 2012-04-04
 * 
 * @bug <date du bug> - <CORRIGE> - <Intitulé précis du bug>
 * 
 * @see ouvrir()
 * @see ecrireCommande()
 * @see ecrireReponse()
 * @see fermer()
 */
public class FichiersLogs
{
	/**
	 * Thread permettant la création des fichiers de logs et le lancement des
	 * threads d'écriture si l'opération s'est déroulée correctement. Le
	 * résultat de cette création et du lancement des threads sera placé dans
	 * une file bloquante, qui sera lue et dont le résultat sera retourné par la
	 * fonction ouvrir() (qui aura au préalable lancé le thread d'ouverture), de
	 * telle sorte que l'on connaisse le déroulement de ce processus en dehors
	 * du thread.
	 * 
	 * @file FichiersLogs.java
	 * @brief Classe permettant la création des fichiers de logs et le lancement
	 *        des threads d'écriture.
	 * 
	 * @author Maxime BOUCHENOIRE
	 * @author STS IRIS, Lycée Nicolas APPERT, ORVAULT (FRANCE)
	 * @since 2012-01-06
	 * @version 1.0
	 * @date 2012-04-04
	 * 
	 * @bug <date du bug> - <CORRIGE> - <Intitulé précis du bug>
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
			// On créé et ouvre les fichiers de logs
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
			// On lance les threads d'écriture si la création des
			// fichiers de logs s'est déroulée correctement
			lancerThreadsEcriture();

			Log.i(TAG,
					"La création des fichiers de logs s'est déroulée correctement");

			try
			{
				mFileCreation.put(new Boolean(true));
			}
			catch (InterruptedException mException)
			{
				Log.e(TAG,
						"Echec à la prévention de la réussite de la création des fichiers de logs : "
								+ mException);
			}
		}

		private void relayerEchecCreation()
		{
			Log.i(TAG, "La création des fichiers de logs s'est mal déroulée");

			try
			{
				mFileCreation.put(new Boolean(false));
			}
			catch (InterruptedException mException)
			{
				Log.e(TAG,
						"Echec à la prévention de l'échec de la création des fichiers de logs : "
								+ mException);
			}
		}

		/**
		 * Procédure appelée dans le thread si la création des fichiers de log
		 * s'est déroulée avec succès. Initialise les files, initialise et
		 * lances les threads d'écriture, ainsi que le sémaphore de gestion des
		 * accès aux fichiers de logs.
		 * 
		 * @post Les files de commandes/réponses doivent être initialisées, de
		 *       même pour le sémaphore d'accès aux fichiers de logs. Les
		 *       threads d'écriture doivent également être lancés.
		 * 
		 * @test Voir la procédure dans le fichier associé.
		 * @see mFileCommandes
		 * @see mFileReponses
		 * @see mSemaphoreFichiers
		 * @see CommandesThread
		 * @see ReponsesThread
		 */
		private void lancerThreadsEcriture()
		{
			/*
			 * // On instancie les files des commandes/réponses mFileCommandes =
			 * new LinkedBlockingQueue<byte[]>(); mFileReponses = new
			 * LinkedBlockingQueue<byte[]>();
			 * 
			 * // On initialise et instancie les threads de commandes/réponses
			 * mEcritureReponsesThread = new EcritureReponsesThread();
			 * mEcritureCommandesThread = new EcritureCommandesThread();
			 * 
			 * // On initialise le sémaphore d'accès aux fichiers de logs
			 * mSemaphoreFichiers = new Semaphore(1);
			 * 
			 * // On démarre les threads d'écriture des commandes/réponses
			 * mEcritureCommandesThread.start();
			 * mEcritureReponsesThread.start();
			 */

			mFileItems = new LinkedBlockingQueue<byte[]>();

			mEcritureThread = new EcritureThread();

			mEcritureThread.start();
		}

		/**
		 * Crée le répertoire de la session, ainsi que les deux fichiers de log
		 * (ASCII et binaire). Initialise également les deux flux attributs de
		 * la classe FichiersLogs permettants d'inscrire des commandes et
		 * réponses dans ces fichiers.
		 * 
		 * @post Les fichiers de logs doivent être créés et les buffers
		 *       exploitables.
		 * 
		 * @return VRAI si les fichiers ont été crées, FAUX sinon (car ils
		 *         éxistent déjà)
		 */
		private boolean creerFichiersLogs()
		{
			boolean bRetour = true;

			// On récupère la racine du stockage externe du téléphone
			final File mRacine = Environment.getExternalStorageDirectory();
			// Création du répertoire des fichiers de session
			File applicationRepertoire = new File(mRacine + "/isnBOT");
			// Si le répertoire n'éxiste pas
			if (!applicationRepertoire.exists())
			{
				applicationRepertoire.mkdir(); // On crée le répertoire
			}

			final File repertoireApplication = Environment
					.getExternalStorageDirectory();

			// Création du répertoire de la session
			sCheminAbsolu = repertoireApplication + "/isnBOT/" + sNom;
			File repertoireSession = new File(sCheminAbsolu);

			if (!repertoireSession.exists()) // Si la session n'éxiste pas
			{
				repertoireSession.mkdir();
			}
			else
			// Si la session éxiste
			{
				Log.w(TAG,
						"La session éxiste déjà, on utilisera les fichiers déjà existants");
			}

			// Création du couple de fichier
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
						"Erreur à l'initialisation des buffers d'écriture dans les fichiers de logs : ",
						mException);
			}

			return bRetour;
		}
	}

	/**
	 * Thread permettant l'écriture des items placées sur la file correspondante
	 * dans les fichiers de logs. Il y ajoute leur en-tête contenant l'heure de
	 * l'écriture et le numéro de l'échange.
	 * 
	 * @file FichiersLogs.java
	 * @brief Thread permettant l'écriture des items placées sur la file
	 *        correspondante dans les fichiers de logs.
	 * 
	 * @author Maxime BOUCHENOIRE
	 * @author STS IRIS, Lycée Nicolas APPERT, ORVAULT (FRANCE)
	 * @since 2012-01-06
	 * @version 1.0
	 * @date 2012-04-04
	 * 
	 * @bug <date du bug> - <CORRIGE> - <Intitulé précis du bug>
	 */
	private class EcritureThread extends Thread
	{
		/** TAG de la classe pour lecture des logs plus clair */
		private String	TAG;
		/**
		 * Boolean permettant de boucler le thread d'écriture, et ainsi
		 * l'arrêter lorsque nécessaire.
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
		 * Procédure appelée dans son thread, gêre la récupération des commandes
		 * dans le thread avec leur numéro d'échange, et les écris dans les
		 * fichiers lorsque le sémaphore est disponible.
		 * 
		 * @post Les la commande en tête de sa file doit être écrite dans les
		 *       fichiers de logs.
		 * 
		 * @test Voir la procédure dans le fichier associé.
		 * @see mFileCommandes
		 * @see mSemaphoreFichiers
		 */
		private void gererFileItems()
		{
			try
			{
				// On récupère la commande et son numéro d'échange dans sa file
				byte[] byDonneesEncapsulees = mFileItems.take();

				// Commande ou réponse ?
				byte byCommandeOuReponse = byDonneesEncapsulees[0];
				// On y éxtrait l'heure d'envoi/réception de l'item
				long lMillisecondesDepuis1970 = decapsulerHeure(byDonneesEncapsulees);
				// On y éxtrait le numéro d'échange
				int nNumeroEchange = decapsulerNumeroEchange(byDonneesEncapsulees);
				// On y étrait la commande
				byte[] byItem = decapsulerItem(byDonneesEncapsulees);

				String tag = (byCommandeOuReponse == 0 ? TAG_COMMANDE
						: TAG_REPONSE);

				// On écrit l'item commande et son en-tête
				ecrire(lMillisecondesDepuis1970, tag, byCommandeOuReponse,
						nNumeroEchange, byItem);

				// On cède la priorité car le traitement est terminé
				yield();
			}
			catch (InterruptedException mException)
			{
				Log.e(TAG,
						"Erreur à la récupération d'une commande sur la file correspondante, ou du sémaphore : ",
						mException);
			}
		}

		/**
		 * Procédure bloquante appelée de l'éxterieur afin d'arrêter ce thread
		 * d'écriture. La méthode se débloquera quand le thread aura fini de
		 * traiter toutes les commandes situées dans leur file. Le thread sera
		 * également arrêté durant cette procédure.
		 * 
		 * @post La file des commandes doit être vide et son thread d'écriture
		 *       arrêté.
		 * 
		 * @test Voir la procédure dans le fichier associé.
		 * @see mFileCommandes
		 * @see mEcritureCommandesThread
		 */
		public void arreterEcriture()
		{
			// Tant qu'il y a des items dans la file
			while (!mFileItems.isEmpty()
					&& !Thread.currentThread().isInterrupted())
			{
				// On attends qu'elle soit vidée par le thread
			}
			// On stop la bouche du thread
			this.bExecution = false;
			// On vide la file par précaution
			mFileItems.clear();
			// On facilite la tâche au ramasse miettes
			mFileItems = null;
		}
	}

	/** TAG de la classe pour lecture des logs plus clair */
	private final String				TAG;
	/** TAG pour l'écriture d'une commande */
	private final String				TAG_COMMANDE;
	/** TAG pour l'écriture d'une réponse */
	private final String				TAG_REPONSE;
	/** TRUE si les fichiers sont ouverts, FAUX sinon */
	private boolean						bOuverts;
	/** Nom de la session */
	private String						sNom;
	/** Chemin absolu du répertoire de la session */
	private String						sCheminAbsolu;
	/** Thread d'ouverture des fichiers de logs */
	private OuvertureFichiersThread		mOuvertureFichiersThread;
	/** Thread d'écriture des items dans les fichiers de logs */
	private EcritureThread				mEcritureThread;
	/** File de la création des fichiers de logs */
	private SynchronousQueue<Boolean>	mFileCreation;
	/** File contenant les items à écrire dans les fichiers de logs */
	private LinkedBlockingQueue<byte[]>	mFileItems;
	/** Buffer permettant l'écriture dans le fichier au format ASCII */
	private BufferedWriter				mBufferAscii;
	/** Buffer permettant l'écriture dans le fichier au format binaire */
	private BufferedWriter				mBufferBinaire;

	/**
	 * Constructeur de la classe FichiersLogs, il permet simplement
	 * d'initialiser l'attribut correspondant au nom de la session.
	 * 
	 * @post L'attribut sNom doit être initialisé correctement
	 * @param mSession
	 *            Session utilisé pour creer et/ou utiliser les fichiers de
	 *            logs.
	 * @test Voir la procédure dans le fichier associé.
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
			// On crée le String correspondant à la session
			this.sNom = mSession.getUtilisateur() + "_" + mSession.getAnnee()
					+ "_" + mSession.getMois() + "_" + mSession.getJour() + "_"
					+ mSession.getHeures() + "h_" + mSession.getMinutes()
					+ "min_" + mSession.getMinutes() + "sec";
		}
		else
		{
			Log.e(TAG,
					"Erreur au constructeur, la Session passée en paramètre est null");
		}

		if (this.sNom == null)
		{
			Log.e(TAG,
					"Valeur du nom de la session = null à l'initialisation de l'attribut");
		}
	}

	/**
	 * Destructeur de la classe FichiersLogs, facilite légèrement la tâche du
	 * ramasse miette.
	 */
	public void finalize()
	{
		// On facilite légèrement la tâche au ramasse miettes
		this.sNom = null;
		this.sCheminAbsolu = null;
	}

	/**
	 * Fonction appelée en boucle si l'on est en mode PACKET_STREAM_CONNECTED
	 * afin de récupérer les données sur l'InputStream du socket Bluetooth et
	 * les placer dans la file de lecture.
	 * 
	 * @pre L'attribut InputStream doit être initialisé (donc le socket doit
	 *      être connecté)
	 * @post La file de lecture doit être vide et le boolean de bouclage à FAUX,
	 *       donc le thread va s'arrêter.
	 * 
	 * @return La réponse à une commande du robot sous le protocole LCP.
	 * @retval NULL si aucune réponse n'est disponible
	 * 
	 * @test Voir la procédure dans le fichier associé.
	 * @see arreterIOThreads()
	 * @see OutputStream
	 */
	public boolean ouvrir()
	{
		// Si le constructeur n'a pas été appelé, et donc que l'attribut
		// correspondant au nom de la session n'est pas initialisé, on retourne
		// directement FAUX. On retourne également FAUX si le nom d'utilisateur
		// ne correspond pas aux attentes du pattern.
		;
		if ((this.sNom == null)
				|| (Pattern.compile("[a-zA-z]+([ '-][a-zA-Z]+)*").matcher(
						this.sNom).matches()))
		{
			Log.e(TAG,
					"Le nom d'utilisateur est incohérent à l'ouverture des fichiers de logs");
			return false;
		}

		// On admet la réussite de la création des fichiers
		boolean bRetour = true;

		// On instancie la file de création
		this.mFileCreation = new SynchronousQueue<Boolean>();

		// On instancie et démarre le thread de création;
		this.mOuvertureFichiersThread = new OuvertureFichiersThread();
		this.mOuvertureFichiersThread.start();

		// On itinialise le Boolean qui recevra la valeur contenu dans la file
		// de création
		Boolean bCreationFichiers = null;

		try
		{
			// On attends l'information sur la réussite ou non de la création
			// des fichiers
			bCreationFichiers = this.mFileCreation.poll(500,
					TimeUnit.MILLISECONDS);
		}
		catch (InterruptedException mException)
		{
			Log.e(TAG,
					"Erreur à la récupération de l'information sur la création des fichiers de logs.",
					mException);
		}

		// Si aucune érreur n'a eu lieu lors de la récupération du Boolean
		// correspondant à l'état de la création des fichiers
		if (bCreationFichiers != null)
		{
			// On transtype le Boolean en boolean
			bRetour = bCreationFichiers.booleanValue();
		}
		else
		// Si les fichiers n'ont pas été créés
		{
			bRetour = false;
			Log.e(TAG, "Boolean bCreationFichiers = null");
		}

		// On détruit la file de création pour aider le ramasse miettes
		this.mFileCreation.clear();
		this.mFileCreation = null;

		this.bOuverts = bRetour;
		return bRetour;
	}

	/**
	 * Arrête les threads d'écriture
	 * 
	 * @return VRAI si les flux ont été fermés, FAUX sinon
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
						"Erreur au flush/fermeture des buffers à l'appel de fermer()",
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

		// Détermination de l'heure de l'envoi de la commande, jusqu'aux
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
			// On écrit "l'en tête" de l'item dans le fichier ASCII
			this.mBufferAscii.write(sTime + sCommandeOuReponse
					+ String.valueOf(nNumeroEchange) + " = ");

			// On écrit "l'en tête" de l'item dans le fichier binaire
			this.mBufferBinaire.write((byte) nHeures);
			this.mBufferBinaire.write((byte) nMinutes);
			this.mBufferBinaire.write((byte) nSecondes);
			this.mBufferBinaire.write((short) nMilliSecondes);
			this.mBufferBinaire.write(byCommandeOuReponse);
			this.mBufferBinaire.write((short) nNumeroEchange);

			// Pour chaque octet du tableau d'octets
			for (int i = 0; i < byItem.length; i++)
			{
				// On écrit l'octet dans le fichier ASCII
				this.mBufferAscii.write(Commande.byteToString(byItem[i]) + " ");
				// On écrit l'octet dans le fichier binaire
				this.mBufferBinaire.write(byItem[i]);
			}

			// On fait un retour à la ligne pour le fichier ASCII
			this.mBufferAscii.write("\r\n");
			// On fait un "retour à la ligne" pour le fichier binaire
			this.mBufferBinaire.write("\r\n");
		}
		catch (IOException mException)
		{
			Log.e(TAG, "Erreur à l'écriture d'une" + sCommandeOuReponse
					+ " sur les buffers", mException);
		}

		try
		{
			this.mBufferAscii.flush();
			this.mBufferBinaire.flush();
		}
		catch (IOException mException)
		{
			Log.e(TAG, "Erreur au flush des buffers lors de l'écriture d'une"
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
					"Les fichiers de logs ne sont pas ouverts, écriture impossible");
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
					"Les fichiers de logs ne sont pas ouverts, écriture impossible");
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
					"Erreur au placement d'une réponse dans la file correspondante",
					mException);
		}

		return bEcriture;
	}

	private byte[] encapsulerItem(byte[] byItem, int nNumeroEchange,
			long lMillisecondesDepuis1970, byte byComandeOuReponse)
	{
		// Si un des deux paramètres est NULL
		if (byItem == null)
		{
			Log.e(TAG,
					" La donnée dont il faut ajouter le numéro d'échange est égale à NULL");
			// On quitte immédiatement la fonction
			return null;
		}

		// On transforme le long en byte[] pour pouvoir le metre dans la file
		byte byMillisecondesDepuis1970[] = ByteBuffer.allocate(8)
				.putLong(lMillisecondesDepuis1970).array();

		// On transforme l'int en byte[] pour pouvoir le metre dans la file
		byte byNumeroEchange[] = ByteBuffer.allocate(4).putInt(nNumeroEchange)
				.array();

		// On récupère la taille de la commande/réponse
		int nTailleItem = byItem.length;
		// On crée un nouveau tableau d'une taille adéquate
		byte[] byItemEncapsule = new byte[1 + 8 + 4 + nTailleItem];
		// On copie les tableaux d'octets les uns après les autres
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
