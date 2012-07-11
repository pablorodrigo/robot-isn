package isnbot.classes;

import android.util.Log;

public class Commande implements ConstantesLCP
{
	public static final String	TAG		= "Commande >>>";

	private boolean				bDouble	= false;
	private byte[]				byCommande;
	private byte[]				byCommande2;
	private String				sCommande;
	private String				sCommentaire;

	/**
	 * Cf. documentation Lego
	 * 
	 * @param nPort
	 *            Moteur sur lequel on souhaite appliquer la commande
	 * @param nPuissance
	 *            Puissance (en %) du moteur
	 * @return La commande correspondante (cf. Documentation Lego)
	 */
	public static byte[] SET_OUTPUT_STATE(int nPort, int nPuissance)
	{
		return new byte[]
		{ DIRECT_COMMAND_REPLY, SET_OUTPUT_STATE, (byte) nPort,
				(byte) nPuissance, MOTORON + /* BRAKE */+REGULATED,
				REGULATION_MODE_IDLE, (byte) MOTOR_TURNRATIO_NONE,
				MOTOR_RUN_STATE_IDLE, (byte) MOTOR_LIMIT_NONE,
				(byte) (MOTOR_LIMIT_NONE >>> 8),
				(byte) (MOTOR_LIMIT_NONE >>> 16),
				(byte) (MOTOR_LIMIT_NONE >>> 24) };
	}

	/**
	 * Cf. documentation Lego
	 * 
	 * @param nPort
	 *            Le port du capteur photosensible
	 * @param nMode
	 *            Couleur de la LED
	 * @return La commande correspondante
	 */
	public static byte[] SET_INPUT_MODE(int nPort, int nMode)
	{
		return new byte[]
		{ DIRECT_COMMAND_REPLY, SET_INPUT_MODE, (byte) nPort, (byte) nMode,
				RAWMODE };
	}

	/**
	 * Cf. documentation Lego
	 * 
	 * @return La commande correspondante
	 */
	public static byte[] GET_BATTERY_LEVEL()
	{
		return new byte[]
		{ DIRECT_COMMAND_REPLY, GET_BATTERY_LEVEL };
	}

	/**
	 * Cf. documentation Lego
	 * 
	 * @return La commande correspondante
	 */
	public static byte[] GET_FIRMWARE_VERSION()
	{
		return new byte[]
		{ SYSTEM_COMMAND_REPLY, GET_FIRMWARE_VERSION };
	}

	/**
	 * Cf. documentation Lego
	 * 
	 * @return La commande correspondante
	 */
	public static byte[] GET_DEVICE_INFO()
	{
		return new byte[]
		{ SYSTEM_COMMAND_REPLY, GET_DEVICE_INFO };
	}

	/**
	 * Cf. documentation Lego
	 * 
	 * @param nPort
	 *            Port du capteur dont on veut récupérer les valeurs
	 * @return La commande correspondante
	 */
	public static byte[] GET_INPUT_VALUES(int nPort)
	{
		return new byte[]
		{ DIRECT_COMMAND_REPLY, GET_INPUT_VALUES, (byte) nPort };
	}

	/**
	 * Cf. documentation Lego
	 * 
	 * @param nPort
	 *            Port du moteur dont on veut récupérer les valeurs
	 * @return La commande correspondante
	 */
	public static byte[] GET_OUTPUT_STATE(int nPort)
	{
		return new byte[]
		{ DIRECT_COMMAND_REPLY, GET_OUTPUT_STATE, (byte) nPort };
	}

	/**
	 * Cf. documentation Lego
	 * 
	 * @param nPort
	 *            Port du moteur dont on veut réinitialiser le compteur
	 * @return La commande correspondante
	 */
	public static byte[] RESET_MOTOR_POSITION(int nPort)
	{
		return new byte[]
		{ DIRECT_COMMAND_REPLY, RESET_MOTOR_POSITION, (byte) nPort, 0x01 };
	}

	/**
	 * Cf. documentation Lego
	 * 
	 * @param nPort
	 *            Port dont on veut faire une lecture low speed
	 * @return La commande correspondante
	 */
	public static byte[] LS_READ(int nPort)
	{
		return new byte[]
		{ DIRECT_COMMAND_REPLY, LS_READ, (byte) nPort };
	}

	/**
	 * Cf. documentation Lego
	 * 
	 * @param nPort
	 *            Port dont on veut faire une écriture low speed
	 * @return La commande correspondante
	 */
	public static byte[] LS_WRITE(int nPort)
	{
		return new byte[]
		{ DIRECT_COMMAND_NOREPLY, LS_WRITE, (byte) nPort, 2, 1, 0x02, 0x42 };
	}

	public Commande(byte[] byCommande)
	{
		this.byCommande = byCommande;
	}

	public Commande(String sCommande, String sCommentaire)
	{
		try
		{
			// On éssai de récupérer une commande dans le fichier
			this.byCommande = stringEnTableauOctets(sCommande);
			this.sCommande = sCommande.toUpperCase();
			this.sCommentaire = sCommentaire;

			if (this.sCommentaire.equals(""))
			{
				this.sCommentaire = "Aucun commentaire";
			}
		}
		catch (Exception mException)
		{
			// Si cette commande est invalide
			this.byCommande = new byte[]
			{ 0x00 };
			this.sCommande = "";
			this.sCommentaire = ("COMMANDE INVALIDE ! : \n")
					.concat(sCommentaire);
			Log.e(TAG, "stringEnTableauOctets", mException);
		}

	}

	public Commande(int nPort, byte byPowerOuMode, boolean bOutputState)
	{
		if (bOutputState)
		{
			this.byCommande = SET_OUTPUT_STATE(nPort, byPowerOuMode);
		}
		else
		{
			this.byCommande = SET_INPUT_MODE(nPort, byPowerOuMode);
		}
	}

	public Commande(int nPort1, byte byPower1, int nPort2, byte byPower2)
	{
		this.byCommande = SET_OUTPUT_STATE(nPort1, byPower1);
		this.byCommande2 = SET_OUTPUT_STATE(nPort2, byPower2);
		this.bDouble = true;
	}

	public void setCommande(byte[] byCommande)
	{
		this.byCommande = byCommande;
	}

	public void setCommande2(byte[] byCommande)
	{
		this.byCommande2 = byCommande;
	}

	public byte[] getCommande()
	{
		return this.byCommande;
	}

	public byte[] getCommande2()
	{
		return this.byCommande2;
	}

	/**
	 * @return Un String contenant une ligne pour chaque octet contenu dans une
	 *         commande, au format suivant = 1 : FF\r\n
	 */
	public static String getAffichable(byte[] byCommande)
	{
		if (byCommande == null)
			return new String("NULL");

		if (byCommande.length == 1)
			return new String("Commande invalide");

		String sCommandeAffichable = "";

		// Pour chaque octet de la commande
		for (int i = 0; i < byCommande.length; i++)
		{
			// On transforme l'octet en sa "version" String
			String sOctet = byteToString(byCommande[i]);
			if (i < 10) // Si le numéro de l'octet est inférieur à 10
			{
				// On décale l'affichage pour l'alignement général de la colonne
				sCommandeAffichable += String.valueOf(i) + "   : " + sOctet
						+ "\r\n";
			}
			else
			// Si le numéro de l'octet est supérieur ou égal à 10
			{
				// On fait un affichage "standard"
				sCommandeAffichable += String.valueOf(i) + " : " + sOctet
						+ "\r\n";
			}
		}

		return sCommandeAffichable;
	}

	public String getCommentaire()
	{
		return (this.sCommentaire == null ? new String("Aucun commentaire")
				: this.sCommentaire);
	}

	public boolean estDouble()
	{
		return this.bDouble;
	}

	/**
	 * Retire tous les espaces contenus dans la commande passée en paramètre
	 * 
	 * @param sCommandeAvecEspaces
	 *            La commande contenant des éspaces entre chaque "octet"
	 * @return La commande sans espace
	 */
	private static String enleverEspaces(String sCommandeAvecEspaces)
	{
		String sCommande = sCommandeAvecEspaces;
		int nPositionEspace = 0;

		// Tant qu'il y a des espaces dans la commande
		while ((nPositionEspace = sCommande.indexOf(' ')) != -1)
		{
			// On récupère la partie avant l'espace
			String sAvantEspace = sCommande.substring(0, nPositionEspace);
			// On récupère la partie après l'espace
			String sApresEspace = sCommande.substring(nPositionEspace + 1);
			// On concatène les deux parties
			sCommande = sAvantEspace.concat(sApresEspace);
		}

		return sCommande;
	}

	/**
	 * 
	 * @param sCommandeAvecEspaces
	 *            La commande avec les potentiels espaces
	 * @return Le tableau d'octets correspondant à la commande écrite au format
	 *         ASCII dans le fichier de commandes
	 * @throws Exception
	 *             Si la conversion se déroule mal
	 */
	private static byte[] stringEnTableauOctets(String sCommandeAvecEspaces)
			throws Exception
	{
		// On enlève les espaces dans la commande
		String sCommande = enleverEspaces(sCommandeAvecEspaces);
		int nTailleString = sCommande.length();

		// Si il manque un digit dans la commande
		if (nTailleString % 2 != 0)
		{
			throw new Exception("Commande à convertir invalide");
		}

		// La taille du tableau est divisée par deux par rapport à la taille de
		// la commande car deux caracteres = 1 octet
		byte[] byCommande = new byte[nTailleString / 2];

		// Tous les deux caracteres jusqu'à la taille de la commande-1
		for (int i = 0; i < nTailleString - 1; i += 2)
		{
			// Le premier digit est décalé de 4 bits vers la gauche
			byte byPremierDigit = (byte) (caractereEnOctet(sCommande.charAt(i)) << 4);
			// On récupère le deuxième digit sans le décaler
			byte bySecondDigit = caractereEnOctet(sCommande.charAt(i + 1));
			int j = i / 2;
			// On place les 2*4 bits dans un octet et cet octet dans le
			// tableau
			byCommande[j] = (byte) (byPremierDigit + bySecondDigit);
		}

		// On retourne le tableau
		return byCommande;
	}

	/**
	 * permet de transformer chaque caractère au format ASCII en sa valeur
	 * hexadécimale correspondante (ex. 'F' donnera 0x0F).
	 * 
	 * @param cCaractere
	 *            Caractère au format ASCII
	 * @return Valeur héxadécimale correspondant au caractère (ex. 'F' = 0x0F)
	 */
	private static byte caractereEnOctet(char cCaractere) throws Exception
	{
		byte byRetour = 0;

		// Si le caractère est compris entre '0' et '9'
		if ('0' <= cCaractere && cCaractere <= '9')
		{
			byRetour = (byte) (cCaractere - '0');
		}
		// Si le caractère est compris entre 'a' et 'f'
		else if ('a' <= cCaractere && cCaractere <= 'f')
		{
			byRetour = (byte) (cCaractere - 'W');
		}
		// Si le caractère est compris entre 'A' et 'F'
		else if ('A' <= cCaractere && cCaractere <= 'F')
		{
			byRetour = (byte) (cCaractere - '7');
		}
		// Si le caractère n'est pas valide
		else
		{
			throw new Exception("Caractère à convertir invalide");
		}

		// On retourne l'équivalent hexadécimale du caractère
		return byRetour;
	}

	public static String byteToString(byte byOctet)
	{
		String sOctet = "";

		sOctet = Integer.toString(byOctet & 0xFF, 16);
		sOctet = sOctet.toUpperCase();

		if (sOctet.length() == 1)
		{
			sOctet = new String("0" + sOctet);
		}

		return sOctet;
	}
}
