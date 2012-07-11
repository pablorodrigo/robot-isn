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
 * J'ai fais le choix d'utiliser ce syst�me de relation entre threads et queues car :
 * -Syst�me standard de producteur/consommateur
 * -Les linkedblockingqueue permettent une utilisation de m�thodes bloquantes et simples � comprendre
 * -L'encapsulation et toutes les grosses taches sont thread�es 
 * 
 * -AJOUTER EN-TETE BLUETOOTH DANS FICHIER DE COMMANDES ET A LEXECUTION ?
 */

/**
 * Classe permettant de se connecter � un robot Lego NXT Mindstorms depuis un
 * terminal Android, ainsi que de communiquer avec lui via le protocole LCP.
 * Cette communication se fait via un Thread d'�criture, qui va envoyer les
 * donn�es sous forme d'octets �cris sur un OutputStream sur le socket
 * Bluetooth, "reli�" au robot. Un Thread de lecture va quand � lui r�cup�rer
 * dans une boucle les octets sur l'InputStream du m�me socket.
 * 
 * Cette classe impl�mente des m�thodes permettant de g�rer cette communication
 * � un niveau plus �lev� pour son utilisateur. Par exemple, la m�thode
 * "envoyerCommande" lui permet d'envoyer une commande du protocole LCP (sans
 * l'en-t�te Bluetooth, g�r� automatiquement) et de r�cup�rer sa r�ponse.
 * 
 * @file Communication.java
 * @brief Classe permettant de communiquer avec un robot Lego NXT Mindstorms via
 *        un terminal Android.
 * 
 * @author Maxime BOUCHENOIRE
 * @author STS IRIS, Lyc�e Nicolas APPERT, ORVAULT (FRANCE)
 * @since 2012-01-06
 * @version 0.8
 * @date 2012-04-04
 * 
 * @todo Impl�menter la communication avec le firmware Lego
 * 
 * @bug <date du bug> - <CORRIGE> - <Intitul� pr�cis du bug>
 */
public class Communication implements ConstantesLCP
{
	/**
	 * Classe permettant d'initialiser le socket Bluetooth entre le terminal
	 * Android et le robot, et informe sa classe conteneur du r�sultat de la
	 * connexion via une file dans laquelle on placera un boolean ad�quante.
	 * 
	 * @file Communication.java
	 * @brief Classe thread interne � la classe Communication permettant de se
	 *        connecter � un robot Lego NXT Mindstorms.
	 * 
	 * @author Maxime BOUCHENOIRE
	 * @author STS IRIS, Lyc�e Nicolas APPERT, ORVAULT (FRANCE)
	 * @since 2012-01-06
	 * @version 1.0
	 * @date 2012-04-04
	 * 
	 * @todo termin�e ?
	 * 
	 * @bug <date du bug> - <CORRIGE> - <Intitul� pr�cis du bug>
	 * 
	 */
	private class InitialisationSocketThread extends Thread
	{
		/** TAG de la classe pour lecture des logs plus clair */
		private final String	TAG			= "InitialisationSocketThread >>>";
		/** Permet de savoir si un �chec a d�j� �t� relay� */
		private boolean			bEchec		= false;
		/** Permet de tra�er l'�tat de la connexion */
		private boolean			bConnexion	= true;

		@Override
		public void run()
		{
			Log.i(TAG, "D�part du thread de connexion avec le robot");
			connecter();
		}

		/**
		 * Proc�dure permettant de se connecter au robot et d'informer la classe
		 * conteneur du r�sultat.
		 * 
		 * @pre Le BluetoothDevice et BluetoothSocket de la classe Communication
		 *      ne doivent pas �tre � null, m�me si ces cas sont trait�s.
		 * @post La classe Communication doit savoir si la connexion a r�ussie
		 *       ou �chou�e via la file correspondante.
		 * 
		 * @test Voir la proc�dure dans le fichier associ�.
		 * @see BluetoothSocket
		 * @see BluetoothDevice
		 * @see relayerReussiteConnexion()
		 * @see relayerEchecConnexion()
		 */
		private void connecter()
		{
			try
			{
				// V�rication de r�f�rence null
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

				// V�rication de r�f�rence null
				if (mBluetoothSocket != null)
				{
					// proc�dure bloquante, soit la m�thode se termine, soit une
					// �xception est lanc�e
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

			// Si la connexion a r�ussie
			if (this.bConnexion)
			{
				relayerReussiteConnexion();
			}
		}

		/**
		 * Proc�dure appel�e si l'instruction "this.mBluetoothSocket.connect();"
		 * s'est d�roul�e sans exception, et qu'aucune r�f�rence null n'a �t�
		 * g�n�r�e. Place la valeur VRAI dans la file de connexion qui sera lue
		 * par la classe Communication.
		 * 
		 * @pre La file d'initialisation de la connexion doit avoir �t�
		 *      initialis�e.
		 * @post La file de connexion pass�e par r�f�rence doit contenir la
		 *       valeur VRAI.
		 * 
		 * @test Voir la proc�dure dans le fichier associ�.
		 * @see relayerEchecConnexion()
		 * @see demarrerCommandesReponsesThreads()
		 */
		private void relayerReussiteConnexion()
		{
			this.bConnexion = true;

			try
			{// Pr�vient la classe Communication appelant que la connexion �
				// r�ussie
				mFileInitialisationSocket.put(new Boolean(true));
			}
			catch (InterruptedException mException)
			{
				Log.e(TAG, "put" + mException);
				this.bConnexion = false;
			}

			Log.i(TAG, "Connexion au robot r�ussie");
			yield();
		}

		/**
		 * Proc�dure appel�e si l'instruction "this.mBluetoothSocket.connect();"
		 * a lanc�e une exception, et donc que le socket n'est pas connect�.
		 * Place la valeur FAUX dans la file de connexion qui sera lue par la
		 * classe Communication.
		 * 
		 * @pre La file d'initialisation de la connexion doit avoir �t�
		 *      initialis�e.
		 * @post La file de connexion pass�e par r�f�rence doit contenir la
		 *       valeur FAUX
		 * 
		 * @test Voir la proc�dure dans le fichier associ�.
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
					// Pr�vient la classe Communication que la connexion a
					// �chou�e
					mFileInitialisationSocket.put(new Boolean(false));
					Log.e(TAG, "put", mException);
				}
				catch (InterruptedException mException2)
				{
					Log.e(TAG, "put", mException2);
					bConnexion = false;
				}

				Log.i(TAG, "Connexion au robot �chou�e");

				if (mBluetoothSocket != null)
				{
					annulerConnexion();
					bConnexion = false;
				}

				yield();
			}
		}

		/**
		 * Proc�dure appel�e par "relayerEchecConnexion()" ou � l'arr�t de la
		 * communication, permettant de fermer le socket Bluetooth de la classe
		 * conteneur.
		 * 
		 * @pre Le socket de la classe Communication doit avoir �t� initialis�.
		 * @post Le socket de la classe Communication doit �tre ferm�.
		 * 
		 * @test Voir la proc�dure dans le fichier associ�.
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
	 * Thread interne � la classe Communication, permettant d'�crire en boucle
	 * sur l'OutputStream li� au socket Bluetooth de la classe Communication.
	 * Les donn�es �critent viennent d'une file bloquante aliment�e par la
	 * classe Communication.
	 * 
	 * @file Communication.java
	 * @brief Classe permettant de d'�crire des donn�es sur le socket Bluetooth
	 *        li� au robot
	 * 
	 * @author Maxime BOUCHENOIRE
	 * @author STS IRIS, Lyc�e Nicolas APPERT, ORVAULT (FRANCE)
	 * @since 2012-01-06
	 * @version 1.0
	 * @date 2012-04-04
	 * 
	 * @todo Impl�menter la communication avec le firmware Lego ?
	 * 
	 * @bug <date du bug> - <CORRIGE> - <Intitul� pr�cis du bug>
	 * 
	 */
	private class EnvoiCommandesThread extends Thread
	{
		/** TAG de la classe pour lecture des logs plus clair */
		private final String	TAG	= "EcritureThread >>>";
		/** OuputStream permettant l'envoi de donn�es sur le socket Bluetooth */
		private OutputStream	mOutputStream;
		/** Boolean permettant de boucler le thread d'�criture */
		private boolean			bExecution;

		/**
		 * Constructeur de la classe EnvoiCommandesThread, il permet � ce thread
		 * de r�cup�rer l'OutputStream li� au socket de la classe Communication.
		 * 
		 * @pre Le BluetoothSocket de la classe Communication doit �tre
		 *      initialis� et connect�.
		 * @post L'OutputStream li� au socket Bluetooth doit �tre initialis�.
		 * 
		 * @test Voir la proc�dure dans le fichier associ�.
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
			Log.i(TAG, "D�part du thread d'envoi de commandes");

			this.bExecution = true;
			while (this.bExecution && this.mOutputStream != null
					&& !Thread.currentThread().isInterrupted())
			{
				gererFileCommandes();
				yield();
			}

		}

		/**
		 * Proc�dure tournant en boucle permettant la gestion de la file des
		 * commandes � envoyer.
		 * 
		 * @pre La file des commandes doit �tre initialis�e.
		 * @post Le prochain objet plac� sur la file doit �tre r�cup�r� et plac�
		 *       sur le socket Bluetooth.
		 * 
		 * @test Voir la proc�dure dans le fichier associ�.
		 * @see BluetoothSocket
		 * @see LinkedBlockingQueue
		 */
		private void gererFileCommandes()
		{
			try
			{
				// On r�cup�re les donn�es sur la file des commandes
				byte[] mDonneesFile = mFileCommandes.take();
				// On les �crit sur l'OutputStream du socket Bluetooth
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
		 * Proc�dure permettant de passer son tableau d'octets pass� en
		 * param�tre sur l'OutputStream du socket Bluetooth.
		 * 
		 * @post La commande pass� en param�tre doit �tre �crite sur
		 *       l'OutputStream du socket, avec l'en-t�te Bluetooth.
		 * 
		 * @param byCommande
		 *            La commande (protocole LCP sans en-t�te Bluetooth) �
		 *            envoyer sur le socket Bluetooth.
		 * @test Voir la proc�dure dans le fichier associ�.
		 * @see OutputStream
		 */
		private void envoyerCommande(byte[] byCommande, int nNumeroEchange)
				throws IOException
		{

			// On quitte imm�diatement la proc�dure si la commande est NULL
			if (byCommande != null)
			{
				// On construit l'en-t�te Bluetooth
				byte[] byTailleCommande = new byte[2];
				byTailleCommande[0] = (byte) byCommande.length;
				byTailleCommande[1] = (byte) ((byCommande.length >> 8) & 0xff);

				// On ajoute l'en-t�te Bluetooth � la commande et on l'envoi
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
		 * utilis�e pour ajouter l'en-t�te Bluetooth � une commande du protocole
		 * LCP.
		 * 
		 * @param byTailleCommande
		 *            Le premier tableau d'octets (taille de la commande).
		 * @param byCommande
		 *            Le deuxi�me tableau d'octets, ajout� � la suite du premier
		 *            (commande).
		 * @return Un tableau d'octets qui rassemble les deux pass�s en
		 *         param�tres.
		 * @test Voir la proc�dure dans le fichier associ�.
		 */
		private byte[] ajouterEnTeteBluetooth(byte[] byTailleCommande,
				byte[] byCommande)
		{

			// Si un des deux param�tres est NULL
			if ((byTailleCommande == null) || (byCommande == null))
			{
				Log.e(TAG,
						"La taille de la commande ou la commande est �gale � NULL : ");
				// On quitte imm�diatement la fonction
				return null;
			}

			// On r�cup�re la taille des deux tableaux
			int nTaille1 = byTailleCommande.length;
			int nTaille2 = byCommande.length;

			// On cr�e un nouveau tableau de la taille des deux autres tableaux
			// r�unis
			byte[] byTrameBluetooth = new byte[nTaille1 + nTaille2];
			// On copie les deux param�tres dans ce nouveau tableau � leur place
			// ad�quate
			System.arraycopy(byTailleCommande, 0, byTrameBluetooth, 0, nTaille1);
			System.arraycopy(byCommande, 0, byTrameBluetooth, nTaille1,
					nTaille2);

			// On retourne le nouveau tableau
			return byTrameBluetooth;
		}

		/**
		 * Proc�dure appel�e si par la m�thode "arreterCommandesReponsesThreads"
		 * afin de changer l'�tat du boolean permettant la boucle du thread
		 * d'envoi des commandes, et ainsi l'arr�ter. Vide �galement la file des
		 * commandes.
		 * 
		 * @post La file d'�criture doit �tre vide et le boolean de bouclage �
		 *       FAUX, donc le thread va s'arr�ter.
		 * 
		 * @test Voir la proc�dure dans le fichier associ�.
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
			// On vide la file par pr�caution
			mFileCommandes.clear();
			// On facilite la t�che au ramasse miettes
			mFileCommandes = null;
		}
	}

	/**
	 * Thread interne � la classe Communication, permettant de lire en boucle
	 * sur l'IutputStream li� au socket Bluetooth de la classe Communication.
	 * Les donn�es re�ues sont inscrites sur la file correspondante qui sera lue
	 * par la classe Communication.
	 * 
	 * @file Communication.java
	 * @brief Classe permettant de lire des donn�es sur le socket Bluetooth li�
	 *        au robot
	 * 
	 * @author Maxime BOUCHENOIRE
	 * @author STS IRIS, Lyc�e Nicolas APPERT, ORVAULT (FRANCE)
	 * @since 2012-01-06
	 * @version 1.0
	 * @date 2012-04-04
	 * 
	 * @todo Impl�menter la communication avec le firmware Lego ?
	 * 
	 * @bug <date du bug> - <CORRIGE> - <Intitul� pr�cis du bug>
	 * 
	 */
	private class ReceptionReponsesThread extends Thread
	{
		/** TAG de la classe pour lecture des logs plus clair */
		private final String	TAG			= "LectureThread >>>";
		/** IuputStream permettant la reception de donn�es sur le socket */
		public InputStream		mInputStream;
		/** Boolean permettant de boucler le thread de lecture */
		boolean					bExecution	= true;

		/**
		 * Constructeur de la classe ReceptionReponsesThread, il permet � ce
		 * thread de r�cup�rer l'IutputStream li� au socket de la classe
		 * Communication.
		 * 
		 * @pre Le BluetoothSocket de la classe Communication doit �tre
		 *      initialis� et connect�.
		 * @post L'IutputStream li� au socket Bluetooth doit �tre initialis�.
		 * 
		 * @test Voir la proc�dure dans le fichier associ�.
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
		 * Proc�dure tournant en boucle permettant la gestion de la file des
		 * r�ponses re�ues.
		 * 
		 * @pre La file des r�ponses doit �tre initialis�e.
		 * @post La prochaine r�ponse r�cup�r� sur l'InputStream doit �tre plac�
		 *       dans la file des r�ponses
		 * 
		 * @test Voir la proc�dure dans le fichier associ�.
		 * @see BluetoothSocket
		 * @see LinkedBlockingQueue
		 */
		private void gererFileReponses()
		{
			byte[] byReponse = null;

			byReponse = receptionnerReponse();

			// Si on a obtenu une r�ponse sur l'InputStream du socket
			if (byReponse != null)
			{
				try
				{
					// On place cette r�ponse sur la file de lecture
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
		 * Proc�dure appel�e si par la m�thode "arreterCommandesReponsesThreads"
		 * afin de changer l'�tat du boolean permettant la boucle du thread de
		 * r�ception des r�ponses, et ainsi l'arr�ter. Vide �galement la file
		 * des commandes.
		 * 
		 * @post La file d'�criture doit �tre vide et le boolean de bouclage �
		 *       FAUX, donc le thread va s'arr�ter.
		 * 
		 * @test Voir la proc�dure dans le fichier associ�.
		 * @see arreterCommandesReponsesThreads()
		 */
		public void arreterGestionReponses()
		{
			// Tant qu'il y a des r�ponses dans la file
			while (!mFileReponses.isEmpty()
					&& !Thread.currentThread().isInterrupted())
			{
				// On attends que le thread vide la file
			}

			this.bExecution = false;
			// On vide la file par pr�caution
			mFileReponses.clear();
			// On facilite la t�che au ramasse miettes
			mFileReponses = null;
		}

		/**
		 * Fonction appel�e en boucle si l'on est en mode
		 * PACKET_STREAM_CONNECTED afin de r�cup�rer les donn�es sur
		 * l'InputStream du socket Bluetooth et les placer dans la file de
		 * lecture.
		 * 
		 * @pre L'attribut InputStream doit �tre initialis� (donc le socket doit
		 *      �tre connect�)
		 * @post La file de r�ponses doit �tre vide et le boolean de bouclage �
		 *       FAUX, donc le thread va s'arr�ter.
		 * 
		 * @return La r�ponse � une commande du robot sous le protocole LCP.
		 * @retval NULL si aucune r�ponse n'est disponible
		 * 
		 * @test Voir la proc�dure dans le fichier associ�.
		 * @see arreterIOThreads()
		 * @see OutputStream
		 */
		private byte[] receptionnerReponse()
		{
			int lsbTailleReponse = -1;

			try
			{
				// On r�cup�re la premiere valeur disponible sur l'InputStream,
				// correspondant au LSB de la taille de la r�ponse.
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
				// On arr�te la proc�dure en retournant NULL
				return null;
			}

			int msbTailleReponse = 0;

			try
			{
				// On r�cup�re la deuxieme valeur disponible sur l'InputStream,
				// correspondant au MSB de la taille de la r�ponse
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
				// On arr�te la proc�dure en retournant NULL
				return null;
			}

			// On r�cup�re la taille totale de la r�ponse dans un int
			int nTailleReponse = lsbTailleReponse | (msbTailleReponse << 8);
			// On cr�e un tableau d'octets de la taille de la r�ponse pr�sum�e
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
	/** Correspond au Bluetooth du t�l�phone */
	private BluetoothAdapter			mBluetoothAdapter;
	/** Correspond au robot auquel on souhaite se connecter */
	private BluetoothDevice				mBluetoothDevice;
	/** Socket Bluetooth li� au robot */
	private BluetoothSocket				mBluetoothSocket;
	/** Correspond au robot auquel on souhaite se connecter */
	private Robot						mRobot;
	/** Thread d'initialisation du socket Bluetooth */
	private InitialisationSocketThread	mInitialisationSocketThread;
	/** Thread de r�ception des r�ponses */
	private ReceptionReponsesThread		mReceptionReponsesThread;
	/** Thread d'envoi des commandes */
	private EnvoiCommandesThread		mEnvoiCommandesThread;
	/** File de gestion de l'initialisation de la connexion */
	private SynchronousQueue<Boolean>	mFileInitialisationSocket;
	/** File contenant les r�ponses re�ues */
	private LinkedBlockingQueue<byte[]>	mFileReponses;
	/** File contenant les commandes � envoyer */
	private LinkedBlockingQueue<byte[]>	mFileCommandes;
	/** Fichiers de logs */
	private FichiersLogs				mFichiersLogs;
	/** Repr�sente l'�tat de la connexion */
	private boolean						bConnexion;
	/** Indique si la communication doit �tre logu�e */
	private boolean						bLogs;
	/** Handler permettant de gerer les erreurs de communication */
	private Handler						mHandler;
	/**
	 * Universally Unique Identifier, sert � identifier l'application pour
	 * l'usage du Bluetooth sur le t�l�phone
	 */
	private static final UUID			mUUID	= UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

	/**
	 * Constructeur de la classe Communication, il initialise tous les attributs
	 * � NULL, pour �tre sur de connaitre leur valeur par la suite.
	 * 
	 * @post Tous tous les attributs doivent �tre � NULL.
	 * 
	 * @test Voir la proc�dure dans le fichier associ�.
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
	 * Proc�dure permettant de terminer le thread d'initialisation du socket et
	 * de lui appliquer la valeur null.
	 * 
	 * @post Le thread d'initialisation du socket doit �tre null.
	 * 
	 * @test Voir la proc�dure dans le fichier associ�.
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
	 * Proc�dure permettant d'arreter la connexion et de fermer les fichiers de
	 * logs.
	 * 
	 * @post Les threads de communication sont arret�, et les fichiers de logs
	 *       ferm�s.
	 * 
	 * @test Voir la proc�dure dans le fichier associ�.
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
	 * Cette proc�dure permet de se connecter � un robot et d'ouvrir les
	 * fichiers de logs.
	 * 
	 * @post Les threads de communications sont lanc�s si la connexion �
	 *       r�ussie, les fichiers de logs sont �galement ouverts.
	 * 
	 * @param mRobot
	 *            Le robot auquel on souhaite se connecter
	 * @param mSession
	 *            La session qui sert � creer les fichiers de logs
	 * 
	 * @return VRAI si la connexion a r�ussie, FAUX sinon
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
			Log.w(TAG, "BluetoothAdapter d�j� initialis�");
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
	 * Proc�dure permettant de terminer les threads d'envoi de commandes et
	 * reception de r�ponses et de leur appliquer la valeur null.
	 * 
	 * @post Les threads d'envoi de commandes et reception de r�ponses doivent
	 *       �tre null.
	 * 
	 * @test Voir la proc�dure dans le fichier associ�.
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
	 * Cette proc�dure ajoute le param�tre qui lui a �t� pass� � une queue
	 * d'�criture, elle m�me pass�e par r�f�rence au thread d'�criture. Ce
	 * thread se chargera de transferer les informations de cette file au socket
	 * Bluetooth via un OutputStream.
	 * 
	 * @pre La fonction "open(NXTInfo nxt, int mode)" de cette m�me classe doit
	 *      avoir �t� appel�e et avoir retourn�e VRAI, la connexion avec le
	 *      robot doit donc �tre active.
	 * @post Le param�tre de la m�thode (sous forme d'un tableau d'octets) doit
	 *       �tre ajout� � la file d'�criture
	 * @param byCommande
	 *            La commande � envoyer au robot, suivant le protocole LCP (Lego
	 *            Communication Protocol)
	 * @test Voir la proc�dure dans le fichier associ�.
	 * @throws IOException
	 *             Quand la file d'�criture est pleine ou la valeur de la
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
			Log.e(TAG, "La commande � �crire dans la file est null");
		}

		return bAjoutFile;
	}

	public boolean estConnecte()
	{
		return this.bConnexion;
	}

}
