package isnbot.classes;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.util.Log;

/**
 * NOTE PERSONELLE :
 * J'ai fais le choix d'utiliser ce système de relation entre threads et queues car :
 * -Système standard de producteur/consommateur
 * -Les linkedblockingqueue permettent une utilisation de méthodes bloquantes et simples à comprendre
 * -L'encapsulation et toutes les grosses taches sont threadées 
 * 
 * -AJOUTER EN-TETE BLUETOOTH DANS FICHIER DE COMMANDES ET A LEXECUTION ?
 */

/**
 * Classe permettant de se connecter à un robot Lego NXT Mindstorms depuis un
 * terminal Android, ainsi que de communiquer avec lui via le protocole LCP.
 * Cette communication se fait via un Thread d'écriture, qui va envoyer les
 * données sous forme d'octets écris sur un OutputStream sur le socket
 * Bluetooth, "relié" au robot. Un Thread de lecture va quand à lui récupérer
 * dans une boucle les octets sur l'InputStream du même socket.
 * 
 * Cette classe implémente des méthodes permettant de gêrer cette communication
 * à un niveau plus élevé pour son utilisateur. Par exemple, la méthode
 * "envoyerCommande" lui permet d'envoyer une commande du protocole LCP (sans
 * l'en-tête Bluetooth, géré automatiquement) et de récupérer sa réponse.
 * 
 * @file Communication.java
 * @brief Classe permettant de communiquer avec un robot Lego NXT Mindstorms via
 *        un terminal Android.
 * 
 * @author Maxime BOUCHENOIRE
 * @author STS IRIS, Lycée Nicolas APPERT, ORVAULT (FRANCE)
 * @since 2012-01-06
 * @version 0.8
 * @date 2012-04-04
 * 
 * @todo Implémenter la communication avec le firmware Lego
 * 
 * @bug <date du bug> - <CORRIGE> - <Intitulé précis du bug>
 */
public class Communication implements ConstantesLCP
{
	/**
	 * Classe permettant d'initialiser le socket Bluetooth entre le terminal
	 * Android et le robot, et informe sa classe conteneur du résultat de la
	 * connexion via une file dans laquelle on placera un boolean adéquante.
	 * 
	 * @file Communication.java
	 * @brief Classe thread interne à la classe Communication permettant de se
	 *        connecter à un robot Lego NXT Mindstorms.
	 * 
	 * @author Maxime BOUCHENOIRE
	 * @author STS IRIS, Lycée Nicolas APPERT, ORVAULT (FRANCE)
	 * @since 2012-01-06
	 * @version 1.0
	 * @date 2012-04-04
	 * 
	 * @todo terminée ?
	 * 
	 * @bug <date du bug> - <CORRIGE> - <Intitulé précis du bug>
	 * 
	 */
	private class InitialisationSocketThread extends Thread
	{
		/** TAG de la classe pour lecture des logs plus clair */
		private final String	TAG			= "InitialisationSocketThread >>>";
		/** Permet de savoir si un échec a déjà été relayé */
		private boolean			bEchec		= false;
		/** Permet de traçer l'état de la connexion */
		private boolean			bConnexion	= true;

		@Override
		public void run()
		{
			Log.i(TAG, "Départ du thread de connexion avec le robot");
			connecter();
		}

		/**
		 * Procédure permettant de se connecter au robot et d'informer la classe
		 * conteneur du résultat.
		 * 
		 * @pre Le BluetoothDevice et BluetoothSocket de la classe Communication
		 *      ne doivent pas être à null, même si ces cas sont traités.
		 * @post La classe Communication doit savoir si la connexion a réussie
		 *       ou échouée via la file correspondante.
		 * 
		 * @test Voir la procédure dans le fichier associé.
		 * @see BluetoothSocket
		 * @see BluetoothDevice
		 * @see relayerReussiteConnexion()
		 * @see relayerEchecConnexion()
		 */
		private void connecter()
		{
			try
			{
				// Vérication de référence null
				if (mBluetoothDevice != null)
				{
					// On initialise le socket
					// de la classe Communication avec l'UUID choisi
					mBluetoothSocket = mBluetoothDevice
							.createRfcommSocketToServiceRecord(mUUID);
				}
				else
				{
					relayerEchecConnexion(new IOException(
							"mBluetoothDevice == null"));
				}

				// Vérication de référence null
				if (mBluetoothSocket != null)
				{
					// procédure bloquante, soit la méthode se termine, soit une
					// éxception est lancée
					mBluetoothSocket.connect();
				}
				else
				{
					relayerEchecConnexion(new IOException(
							"mBluetoothSocket == null"));
				}
			}
			catch (IOException mException)
			{
				relayerEchecConnexion(mException);
			}

			// Si la connexion a réussie
			if (this.bConnexion)
			{
				relayerReussiteConnexion();
			}
		}

		/**
		 * Procédure appelée si l'instruction "this.mBluetoothSocket.connect();"
		 * s'est déroulée sans exception, et qu'aucune référence null n'a été
		 * générée. Place la valeur VRAI dans la file de connexion qui sera lue
		 * par la classe Communication.
		 * 
		 * @pre La file d'initialisation de la connexion doit avoir été
		 *      initialisée.
		 * @post La file de connexion passée par référence doit contenir la
		 *       valeur VRAI.
		 * 
		 * @test Voir la procédure dans le fichier associé.
		 * @see relayerEchecConnexion()
		 * @see demarrerCommandesReponsesThreads()
		 */
		private void relayerReussiteConnexion()
		{
			this.bConnexion = true;

			try
			{// Prévient la classe Communication appelant que la connexion à
				// réussie
				mFileInitialisationSocket.put(new Boolean(true));
			}
			catch (InterruptedException mException)
			{
				Log.e(TAG, "put" + mException);
				this.bConnexion = false;
			}

			Log.i(TAG, "Connexion au robot réussie");
			yield();
		}

		/**
		 * Procédure appelée si l'instruction "this.mBluetoothSocket.connect();"
		 * a lancée une exception, et donc que le socket n'est pas connecté.
		 * Place la valeur FAUX dans la file de connexion qui sera lue par la
		 * classe Communication.
		 * 
		 * @pre La file d'initialisation de la connexion doit avoir été
		 *      initialisée.
		 * @post La file de connexion passée par référence doit contenir la
		 *       valeur FAUX
		 * 
		 * @test Voir la procédure dans le fichier associé.
		 * @see relayerReussiteConnexion()
		 * @see arreterInitialisationSocketThread()
		 * @see arreterCommandesReponsesThreads()
		 */
		private void relayerEchecConnexion(IOException mException)
		{
			this.bConnexion = false;

			if (!this.bEchec)
			{
				this.bEchec = true;

				try
				{
					// Prévient la classe Communication que la connexion a
					// échouée
					mFileInitialisationSocket.put(new Boolean(false));
					Log.e(TAG, "put", mException);
				}
				catch (InterruptedException mException2)
				{
					Log.e(TAG, "put", mException2);
					bConnexion = false;
				}

				Log.i(TAG, "Connexion au robot échouée");

				if (mBluetoothSocket != null)
				{
					annulerConnexion();
					bConnexion = false;
				}

				yield();
			}
		}

		/**
		 * Procédure appelée par "relayerEchecConnexion()" ou à l'arrêt de la
		 * communication, permettant de fermer le socket Bluetooth de la classe
		 * conteneur.
		 * 
		 * @pre Le socket de la classe Communication doit avoir été initialisé.
		 * @post Le socket de la classe Communication doit être fermé.
		 * 
		 * @test Voir la procédure dans le fichier associé.
		 * @see relayerEchecConnexion()
		 * @see
		 */
		public void annulerConnexion()
		{
			try
			{
				if (mBluetoothSocket != null)
				{
					mBluetoothSocket.close();
				}
			}
			catch (IOException mException)
			{
				Log.e(TAG, "close", mException);
				bConnexion = false;
			}

			mBluetoothSocket = null;
		}
	}

	/**
	 * Thread interne à la classe Communication, permettant d'écrire en boucle
	 * sur l'OutputStream lié au socket Bluetooth de la classe Communication.
	 * Les données écritent viennent d'une file bloquante alimentée par la
	 * classe Communication.
	 * 
	 * @file Communication.java
	 * @brief Classe permettant de d'écrire des données sur le socket Bluetooth
	 *        lié au robot
	 * 
	 * @author Maxime BOUCHENOIRE
	 * @author STS IRIS, Lycée Nicolas APPERT, ORVAULT (FRANCE)
	 * @since 2012-01-06
	 * @version 1.0
	 * @date 2012-04-04
	 * 
	 * @todo Implémenter la communication avec le firmware Lego ?
	 * 
	 * @bug <date du bug> - <CORRIGE> - <Intitulé précis du bug>
	 * 
	 */
	private class EnvoiCommandesThread extends Thread
	{
		/** TAG de la classe pour lecture des logs plus clair */
		private final String	TAG	= "EcritureThread >>>";
		/** OuputStream permettant l'envoi de données sur le socket Bluetooth */
		private OutputStream	mOutputStream;
		/** Boolean permettant de boucler le thread d'écriture */
		private boolean			bExecution;

		/**
		 * Constructeur de la classe EnvoiCommandesThread, il permet à ce thread
		 * de récupérer l'OutputStream lié au socket de la classe Communication.
		 * 
		 * @pre Le BluetoothSocket de la classe Communication doit être
		 *      initialisé et connecté.
		 * @post L'OutputStream lié au socket Bluetooth doit être initialisé.
		 * 
		 * @test Voir la procédure dans le fichier associé.
		 * @see BluetoothSocket
		 * @see LinkedBlockingQueue
		 */
		public EnvoiCommandesThread()
		{
			try
			{
				this.mOutputStream = mBluetoothSocket.getOutputStream();
			}
			catch (IOException mException)
			{
				Log.e(TAG, "getOutputStream", mException);
				bConnexion = false;
			}
		}

		@Override
		public void run()
		{
			Log.i(TAG, "Départ du thread d'envoi de commandes");

			this.bExecution = true;
			while (this.bExecution && this.mOutputStream != null
					&& !Thread.currentThread().isInterrupted())
			{
				gererFileCommandes();
				yield();
			}

		}

		/**
		 * Procédure tournant en boucle permettant la gestion de la file des
		 * commandes à envoyer.
		 * 
		 * @pre La file des commandes doit être initialisée.
		 * @post Le prochain objet placé sur la file doit être récupéré et placé
		 *       sur le socket Bluetooth.
		 * 
		 * @test Voir la procédure dans le fichier associé.
		 * @see BluetoothSocket
		 * @see LinkedBlockingQueue
		 */
		private void gererFileCommandes()
		{
			try
			{
				// On récupère les données sur la file des commandes
				byte[] mDonneesFile = mFileCommandes.take();
				// On les écrit sur l'OutputStream du socket Bluetooth
				byte[] byNumeroEchange = new byte[4];
				byte[] byCommande = new byte[mDonneesFile.length - 4];
				System.arraycopy(mDonneesFile, 0, byNumeroEchange, 0, 4);
				System.arraycopy(mDonneesFile, 4, byCommande, 0,
						byCommande.length);
				int nNumeroEchange = ByteBuffer.wrap(byNumeroEchange).getInt();
				envoyerCommande(byCommande, nNumeroEchange);
			}
			catch (Exception mException)
			{
				Log.e(TAG, "gererFileCommandes", mException);
				this.bExecution = false;
				bConnexion = false;
			}
		}

		/**
		 * Procédure permettant de passer son tableau d'octets passé en
		 * paramètre sur l'OutputStream du socket Bluetooth.
		 * 
		 * @post La commande passé en paramètre doit être écrite sur
		 *       l'OutputStream du socket, avec l'en-tête Bluetooth.
		 * 
		 * @param byCommande
		 *            La commande (protocole LCP sans en-tête Bluetooth) à
		 *            envoyer sur le socket Bluetooth.
		 * @test Voir la procédure dans le fichier associé.
		 * @see OutputStream
		 */
		private void envoyerCommande(byte[] byCommande, int nNumeroEchange)
				throws IOException
		{

			// On quitte immédiatement la procédure si la commande est NULL
			if (byCommande != null)
			{
				// On construit l'en-tête Bluetooth
				byte[] byTailleCommande = new byte[2];
				byTailleCommande[0] = (byte) byCommande.length;
				byTailleCommande[1] = (byte) ((byCommande.length >> 8) & 0xff);

				// On ajoute l'en-tête Bluetooth à la commande et on l'envoi
				// sur
				// l'OutputStream du socket Bluetooth
				byte[] byTrameBluetooth = ajouterEnTeteBluetooth(
						byTailleCommande, byCommande);

				if (byTrameBluetooth != null)
				{
					if (bLogs)
					{
						mFichiersLogs.ecrireCommande(byCommande,
								nNumeroEchange, System.currentTimeMillis());
					}
					this.mOutputStream.write(byTrameBluetooth);
					this.mOutputStream.flush();
				}
			}
		}

		/**
		 * Fonction permettant de concatener deux tableaux d'octets, ici
		 * utilisée pour ajouter l'en-tête Bluetooth à une commande du protocole
		 * LCP.
		 * 
		 * @param byTailleCommande
		 *            Le premier tableau d'octets (taille de la commande).
		 * @param byCommande
		 *            Le deuxième tableau d'octets, ajouté à la suite du premier
		 *            (commande).
		 * @return Un tableau d'octets qui rassemble les deux passés en
		 *         paramètres.
		 * @test Voir la procédure dans le fichier associé.
		 */
		private byte[] ajouterEnTeteBluetooth(byte[] byTailleCommande,
				byte[] byCommande)
		{

			// Si un des deux paramètres est NULL
			if ((byTailleCommande == null) || (byCommande == null))
			{
				Log.e(TAG,
						"La taille de la commande ou la commande est égale à NULL : ");
				// On quitte immédiatement la fonction
				return null;
			}

			// On récupère la taille des deux tableaux
			int nTaille1 = byTailleCommande.length;
			int nTaille2 = byCommande.length;

			// On crée un nouveau tableau de la taille des deux autres tableaux
			// réunis
			byte[] byTrameBluetooth = new byte[nTaille1 + nTaille2];
			// On copie les deux paramètres dans ce nouveau tableau à leur place
			// adéquate
			System.arraycopy(byTailleCommande, 0, byTrameBluetooth, 0, nTaille1);
			System.arraycopy(byCommande, 0, byTrameBluetooth, nTaille1,
					nTaille2);

			// On retourne le nouveau tableau
			return byTrameBluetooth;
		}

		/**
		 * Procédure appelée si par la méthode "arreterCommandesReponsesThreads"
		 * afin de changer l'état du boolean permettant la boucle du thread
		 * d'envoi des commandes, et ainsi l'arrêter. Vide également la file des
		 * commandes.
		 * 
		 * @post La file d'écriture doit être vide et le boolean de bouclage à
		 *       FAUX, donc le thread va s'arrêter.
		 * 
		 * @test Voir la procédure dans le fichier associé.
		 * @see arreterCommandesReponsesThreads()
		 */
		public void arreterGestionCommandes()
		{
			// Tant qu'il y a des commandes dans la file
			while (!mFileCommandes.isEmpty()
					&& !Thread.currentThread().isInterrupted())
			{
				// On attends que le thread vide la file
			}
			this.bExecution = false;
			// On vide la file par précaution
			mFileCommandes.clear();
			// On facilite la tâche au ramasse miettes
			mFileCommandes = null;
		}
	}

	/**
	 * Thread interne à la classe Communication, permettant de lire en boucle
	 * sur l'IutputStream lié au socket Bluetooth de la classe Communication.
	 * Les données reçues sont inscrites sur la file correspondante qui sera lue
	 * par la classe Communication.
	 * 
	 * @file Communication.java
	 * @brief Classe permettant de lire des données sur le socket Bluetooth lié
	 *        au robot
	 * 
	 * @author Maxime BOUCHENOIRE
	 * @author STS IRIS, Lycée Nicolas APPERT, ORVAULT (FRANCE)
	 * @since 2012-01-06
	 * @version 1.0
	 * @date 2012-04-04
	 * 
	 * @todo Implémenter la communication avec le firmware Lego ?
	 * 
	 * @bug <date du bug> - <CORRIGE> - <Intitulé précis du bug>
	 * 
	 */
	private class ReceptionReponsesThread extends Thread
	{
		/** TAG de la classe pour lecture des logs plus clair */
		private final String	TAG			= "LectureThread >>>";
		/** IuputStream permettant la reception de données sur le socket */
		public InputStream		mInputStream;
		/** Boolean permettant de boucler le thread de lecture */
		boolean					bExecution	= true;

		/**
		 * Constructeur de la classe ReceptionReponsesThread, il permet à ce
		 * thread de récupérer l'IutputStream lié au socket de la classe
		 * Communication.
		 * 
		 * @pre Le BluetoothSocket de la classe Communication doit être
		 *      initialisé et connecté.
		 * @post L'IutputStream lié au socket Bluetooth doit être initialisé.
		 * 
		 * @test Voir la procédure dans le fichier associé.
		 * @see BluetoothSocket
		 * @see LinkedBlockingQueue
		 */
		public ReceptionReponsesThread()
		{
			try
			{
				this.mInputStream = mBluetoothSocket.getInputStream();
			}
			catch (IOException mException)
			{
				Log.e(TAG, "getInputStream", mException);
				this.bExecution = false;
				bConnexion = false;
			}
		}

		@Override
		public void run()
		{
			while (this.bExecution && this.mInputStream != null
					&& !Thread.currentThread().isInterrupted())
			{
				gererFileReponses();

				yield();
			}
		}

		/**
		 * Procédure tournant en boucle permettant la gestion de la file des
		 * réponses reçues.
		 * 
		 * @pre La file des réponses doit être initialisée.
		 * @post La prochaine réponse récupéré sur l'InputStream doit être placé
		 *       dans la file des réponses
		 * 
		 * @test Voir la procédure dans le fichier associé.
		 * @see BluetoothSocket
		 * @see LinkedBlockingQueue
		 */
		private void gererFileReponses()
		{
			byte[] byReponse = null;

			byReponse = receptionnerReponse();

			// Si on a obtenu une réponse sur l'InputStream du socket
			if (byReponse != null)
			{
				try
				{
					// On place cette réponse sur la file de lecture
					mFileReponses.put(byReponse);
				}
				catch (InterruptedException mException)
				{
					Log.e(TAG, "put", mException);
					this.bExecution = false;
					bConnexion = false;
				}
			}
			else
			{
				this.bExecution = false;
				bConnexion = false;
			}
		}

		/**
		 * Procédure appelée si par la méthode "arreterCommandesReponsesThreads"
		 * afin de changer l'état du boolean permettant la boucle du thread de
		 * réception des réponses, et ainsi l'arrêter. Vide également la file
		 * des commandes.
		 * 
		 * @post La file d'écriture doit être vide et le boolean de bouclage à
		 *       FAUX, donc le thread va s'arrêter.
		 * 
		 * @test Voir la procédure dans le fichier associé.
		 * @see arreterCommandesReponsesThreads()
		 */
		public void arreterGestionReponses()
		{
			// Tant qu'il y a des réponses dans la file
			while (!mFileReponses.isEmpty()
					&& !Thread.currentThread().isInterrupted())
			{
				// On attends que le thread vide la file
			}

			this.bExecution = false;
			// On vide la file par précaution
			mFileReponses.clear();
			// On facilite la tâche au ramasse miettes
			mFileReponses = null;
		}

		/**
		 * Fonction appelée en boucle si l'on est en mode
		 * PACKET_STREAM_CONNECTED afin de récupérer les données sur
		 * l'InputStream du socket Bluetooth et les placer dans la file de
		 * lecture.
		 * 
		 * @pre L'attribut InputStream doit être initialisé (donc le socket doit
		 *      être connecté)
		 * @post La file de réponses doit être vide et le boolean de bouclage à
		 *       FAUX, donc le thread va s'arrêter.
		 * 
		 * @return La réponse à une commande du robot sous le protocole LCP.
		 * @retval NULL si aucune réponse n'est disponible
		 * 
		 * @test Voir la procédure dans le fichier associé.
		 * @see arreterIOThreads()
		 * @see OutputStream
		 */
		private byte[] receptionnerReponse()
		{
			int lsbTailleReponse = -1;

			try
			{
				// On récupère la premiere valeur disponible sur l'InputStream,
				// correspondant au LSB de la taille de la réponse.
				lsbTailleReponse = this.mInputStream.read();
			}
			catch (Exception mException)
			{
				Log.e(TAG, "read lsbTailleReponse", mException);
				bConnexion = false;
			}

			// Si aucune valeur n'est disponible
			if (lsbTailleReponse < 0 || bConnexion == false)
			{
				// On arrête la procédure en retournant NULL
				return null;
			}

			int msbTailleReponse = 0;

			try
			{
				// On récupère la deuxieme valeur disponible sur l'InputStream,
				// correspondant au MSB de la taille de la réponse
				msbTailleReponse = this.mInputStream.read();

			}
			catch (IOException mException2)
			{
				Log.e(TAG, "read msbTailleReponse", mException2);
				bConnexion = false;
			}

			// Si aucune valeur n'est disponible
			if (msbTailleReponse < 0 || bConnexion == false)
			{
				// On arrête la procédure en retournant NULL
				return null;
			}

			// On récupère la taille totale de la réponse dans un int
			int nTailleReponse = lsbTailleReponse | (msbTailleReponse << 8);
			// On crée un tableau d'octets de la taille de la réponse présumée
			byte[] byReponse = new byte[nTailleReponse];
			//
			for (int i = 0; i < nTailleReponse; i++)
			{
				try
				{
					byReponse[i] = (byte) this.mInputStream.read();
				}
				catch (IOException mException)
				{
					Log.e(TAG, "read", mException);
					bConnexion = false;
				}
			}

			return byReponse;
		}
	}

	// -- ATTRIBUTS DE LA CLASSE COMMUNICATION --\\

	/** TAG de la classe pour lecture des logs plus clair */
	private final String				TAG		= "Communication >>>";
	/** Correspond au Bluetooth du téléphone */
	private BluetoothAdapter			mBluetoothAdapter;
	/** Correspond au robot auquel on souhaite se connecter */
	private BluetoothDevice				mBluetoothDevice;
	/** Socket Bluetooth lié au robot */
	private BluetoothSocket				mBluetoothSocket;
	/** Correspond au robot auquel on souhaite se connecter */
	private Robot						mRobot;
	/** Thread d'initialisation du socket Bluetooth */
	private InitialisationSocketThread	mInitialisationSocketThread;
	/** Thread de réception des réponses */
	private ReceptionReponsesThread		mReceptionReponsesThread;
	/** Thread d'envoi des commandes */
	private EnvoiCommandesThread		mEnvoiCommandesThread;
	/** File de gestion de l'initialisation de la connexion */
	private SynchronousQueue<Boolean>	mFileInitialisationSocket;
	/** File contenant les réponses reçues */
	private LinkedBlockingQueue<byte[]>	mFileReponses;
	/** File contenant les commandes à envoyer */
	private LinkedBlockingQueue<byte[]>	mFileCommandes;
	/** Fichiers de logs */
	private FichiersLogs				mFichiersLogs;
	/** Représente l'état de la connexion */
	private boolean						bConnexion;
	/** Indique si la communication doit être loguée */
	private boolean						bLogs;
	/** Handler permettant de gerer les erreurs de communication */
	private Handler						mHandler;
	/**
	 * Universally Unique Identifier, sert à identifier l'application pour
	 * l'usage du Bluetooth sur le téléphone
	 */
	private static final UUID			mUUID	= UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

	/**
	 * Constructeur de la classe Communication, il initialise tous les attributs
	 * à NULL, pour être sur de connaitre leur valeur par la suite.
	 * 
	 * @post Tous tous les attributs doivent être à NULL.
	 * 
	 * @test Voir la procédure dans le fichier associé.
	 */
	public Communication(boolean bLogs, Handler mHandler)
	{
		this.bLogs = bLogs;
		this.mHandler = mHandler;
		this.mBluetoothAdapter = null;
		this.mBluetoothDevice = null;
		this.mBluetoothSocket = null;
		this.mInitialisationSocketThread = null;
		this.mReceptionReponsesThread = null;
		this.mEnvoiCommandesThread = null;
	}

	/**
	 * Procédure permettant de terminer le thread d'initialisation du socket et
	 * de lui appliquer la valeur null.
	 * 
	 * @post Le thread d'initialisation du socket doit être null.
	 * 
	 * @test Voir la procédure dans le fichier associé.
	 */
	private void arreterInitialisationThread()
	{
		if (this.mInitialisationSocketThread != null)
		{
			this.mInitialisationSocketThread.annulerConnexion();
			this.mInitialisationSocketThread = null;
		}

		this.bConnexion = false;
	}

	/**
	 * Procédure permettant d'arreter la connexion et de fermer les fichiers de
	 * logs.
	 * 
	 * @post Les threads de communication sont arreté, et les fichiers de logs
	 *       fermés.
	 * 
	 * @test Voir la procédure dans le fichier associé.
	 */
	public void deconnecter()
	{
		this.bConnexion = false;

		arreterCommandesReponsesThreads();
		arreterInitialisationThread();
		if (bLogs)
		{
			this.mFichiersLogs.fermer();
		}
	}

	/**
	 * Cette procédure permet de se connecter à un robot et d'ouvrir les
	 * fichiers de logs.
	 * 
	 * @post Les threads de communications sont lancés si la connexion à
	 *       réussie, les fichiers de logs sont également ouverts.
	 * 
	 * @param mRobot
	 *            Le robot auquel on souhaite se connecter
	 * @param mSession
	 *            La session qui sert à creer les fichiers de logs
	 * 
	 * @return VRAI si la connexion a réussie, FAUX sinon
	 * 
	 */
	public boolean connecter(Robot mRobot, Session mSession)
	{
		boolean bConnexion = true;

		this.mFileInitialisationSocket = new SynchronousQueue<Boolean>();

		if (this.mBluetoothAdapter == null)
		{
			this.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		}
		else
		{
			Log.w(TAG, "BluetoothAdapter déjà initialisé");
		}

		if (this.mBluetoothAdapter != null)
		{
			this.mBluetoothDevice = this.mBluetoothAdapter
					.getRemoteDevice(mRobot.getAdresse());
		}
		else
		{
			bConnexion = false;
			Log.e(TAG, "mBluetoothAdapter == null");
		}

		if ((mBluetoothDevice != null) && (bConnexion))
		{
			try
			{

				this.mInitialisationSocketThread = new InitialisationSocketThread();
				this.mInitialisationSocketThread.start();

				Boolean socketEstablished = this.mFileInitialisationSocket
						.take();
				Thread.yield();

				if ((socketEstablished != null) && (bConnexion))
				{
					bConnexion = socketEstablished.booleanValue();
					this.mRobot = mRobot;
				}
				else
				{
					bConnexion = false;
				}
			}
			catch (InterruptedException mException)
			{
				Log.e(TAG, "Erreur de connexion : ", mException);
				bConnexion = false;
			}
		}
		else
		{
			bConnexion = false;
		}

		if (bConnexion)
		{
			demarrerCommandesReponsesThreads();
			if (bLogs)
			{
				this.mFichiersLogs = new FichiersLogs(mSession);
				this.mFichiersLogs.ouvrir();
			}
		}

		this.bConnexion = bConnexion;
		return bConnexion;
	}

	public synchronized byte[] envoyerCommande(byte[] byCommande,
			int nNumeroEchange)
	{
		byte[] byReponse = null;

		if (byCommande != null && estConnecte())
		{
			if (ajouterFileCommande(byCommande, nNumeroEchange))
			{
				if (byCommande[0] == DIRECT_COMMAND_REPLY)
				{
					byReponse = attendreReponse();
					if (bLogs && byReponse != null)
					{
						this.mFichiersLogs.ecrireReponse(byReponse,
								nNumeroEchange, System.currentTimeMillis());
					}
				}
			}
		}

		return byReponse;
	}

	private byte[] attendreReponse()
	{
		byte byReponse[] = null;

		try
		{
			byReponse = this.mFileReponses.poll(500, TimeUnit.MILLISECONDS);
		}
		catch (InterruptedException mException)
		{
			Log.e(TAG, "poll", mException);
			bConnexion = false;
		}

		return byReponse;
	}

	private void demarrerCommandesReponsesThreads()
	{
		arreterCommandesReponsesThreads();

		this.mFileReponses = new LinkedBlockingQueue<byte[]>();
		this.mFileCommandes = new LinkedBlockingQueue<byte[]>();

		this.mEnvoiCommandesThread = new EnvoiCommandesThread();
		this.mReceptionReponsesThread = new ReceptionReponsesThread();

		this.mEnvoiCommandesThread.start();
		this.mReceptionReponsesThread.start();
	}

	/**
	 * Procédure permettant de terminer les threads d'envoi de commandes et
	 * reception de réponses et de leur appliquer la valeur null.
	 * 
	 * @post Les threads d'envoi de commandes et reception de réponses doivent
	 *       être null.
	 * 
	 * @test Voir la procédure dans le fichier associé.
	 */
	private void arreterCommandesReponsesThreads()
	{
		if (this.mReceptionReponsesThread != null)
		{
			this.mReceptionReponsesThread.arreterGestionReponses();
			this.mReceptionReponsesThread = null;
		}

		if (this.mEnvoiCommandesThread != null)
		{
			this.mEnvoiCommandesThread.arreterGestionCommandes();
			this.mEnvoiCommandesThread = null;
		}

		this.bConnexion = false;
	}

	/**
	 * Cette procédure ajoute le paramètre qui lui a été passé à une queue
	 * d'écriture, elle même passée par référence au thread d'écriture. Ce
	 * thread se chargera de transferer les informations de cette file au socket
	 * Bluetooth via un OutputStream.
	 * 
	 * @pre La fonction "open(NXTInfo nxt, int mode)" de cette même classe doit
	 *      avoir été appelée et avoir retournée VRAI, la connexion avec le
	 *      robot doit donc être active.
	 * @post Le paramètre de la méthode (sous forme d'un tableau d'octets) doit
	 *       être ajouté à la file d'écriture
	 * @param byCommande
	 *            La commande à envoyer au robot, suivant le protocole LCP (Lego
	 *            Communication Protocol)
	 * @test Voir la procédure dans le fichier associé.
	 * @throws IOException
	 *             Quand la file d'écriture est pleine ou la valeur de la
	 *             commande est "null"
	 * @see EcritureThread
	 */
	private boolean ajouterFileCommande(byte[] byCommande, int nNumeroEchange)
	{
		boolean bAjoutFile = true;

		if (byCommande != null)
		{
			if (this.mFileCommandes.size() < 2147483647) // int
			{
				try
				{
					byte[] byNumeroEchange = ByteBuffer.allocate(4)
							.putInt(nNumeroEchange).array();
					byte[] byCommandeFile = new byte[byCommande.length + 4];
					System.arraycopy(byNumeroEchange, 0, byCommandeFile, 0, 4);
					System.arraycopy(byCommande, 0, byCommandeFile, 4,
							byCommande.length);
					this.mFileCommandes.put(byCommandeFile);
				}
				catch (InterruptedException mException)
				{
					bAjoutFile = false;
					this.deconnecter();
					Log.e(TAG, "put", mException);
				}
				Thread.yield();
			}
			else
			{
				bAjoutFile = false;
				Log.e(TAG, "La file des commandes pleine");
			}
		}
		else
		{
			bAjoutFile = false;
			Log.e(TAG, "La commande à écrire dans la file est null");
		}

		return bAjoutFile;
	}

	public boolean estConnecte()
	{
		return this.bConnexion;
	}

}
