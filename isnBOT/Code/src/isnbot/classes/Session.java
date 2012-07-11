package isnbot.classes;

import android.os.Parcel;
import android.os.Parcelable;

public class Session implements Parcelable
{
	private final String	sNomRobot;
	private final String	sAdrRobot;
	private final String	sUtilisateur;
	private final String	sAnnee;
	private final String	sMois;
	private final String	sJour;
	private final String	sHeures;
	private final String	sMinutes;
	private final String	sSecondes;

	/**
	 * Constructeur permettant l'initialisation des valeurs de la session
	 * 
	 * @param sNomRobot
	 *            Nom du robot utilisé pour la session
	 * @param sAdrRobot
	 *            Adresse MAC du robot utilisé pour la session
	 * @param sUtilisateur
	 *            Nom de l'utilisateur de la session
	 * @param sAnnee
	 *            Année au démarrage de la session
	 * @param sMois
	 *            Mois au démarrage de la session
	 * @param sJour
	 *            Jour au démarrage de la session
	 * @param sHeures
	 *            Heures au démarrage de la session
	 * @param sMinutes
	 *            Minutes au démarrage de la session
	 * @param sSecondes
	 *            Secondes au démarrage de la session
	 */
	public Session(String sNomRobot, String sAdrRobot, String sUtilisateur,
			String sAnnee, String sMois, String sJour, String sHeures,
			String sMinutes, String sSecondes)
	{
		this.sNomRobot = sNomRobot;
		this.sAdrRobot = sAdrRobot;
		this.sUtilisateur = sUtilisateur;
		this.sAnnee = sAnnee;
		this.sMois = sMois;
		this.sJour = sJour;
		this.sHeures = sHeures;
		this.sMinutes = sMinutes;
		this.sSecondes = sSecondes;
	}

	public Session(String[] sSession)
	{
		this.sNomRobot = sSession[0];
		this.sAdrRobot = sSession[1];
		this.sUtilisateur = sSession[2];
		this.sAnnee = sSession[3];
		this.sMois = sSession[4];
		this.sJour = sSession[5];
		this.sHeures = sSession[6];
		this.sMinutes = sSession[7];
		this.sSecondes = sSession[8];
	}

	public String getNomRobot()
	{
		return sNomRobot;
	}

	public String getAdrRobot()
	{
		return sAdrRobot;
	}

	public String getUtilisateur()
	{
		return sUtilisateur;
	}

	public String getAnnee()
	{
		return sAnnee;
	}

	public String getMois()
	{
		return sMois;
	}

	public String getJour()
	{
		return sJour;
	}

	public String getHeures()
	{
		return sHeures;
	}

	public String getMinutes()
	{
		return sMinutes;
	}

	public String getSecondes()
	{
		return sSecondes;
	}

	public int describeContents()
	{
		return 0;
	}

	/**
	 * Ecrit toutes les données de l'objet à l'interieur du Parcel passé en
	 * paramètre
	 * 
	 * @param ParcelDestination
	 *            Parcel dans lequel sera "stocké" les informations de l'objet
	 * @param nFlags
	 *            Inutilisé car placement et récupération des String dans le
	 *            même ordre
	 */
	public void writeToParcel(Parcel ParcelDestination, int nFlags)
	{
		ParcelDestination.writeString(this.sNomRobot);
		ParcelDestination.writeString(this.sAdrRobot);
		ParcelDestination.writeString(this.sUtilisateur);
		ParcelDestination.writeString(this.sAnnee);
		ParcelDestination.writeString(this.sMois);
		ParcelDestination.writeString(this.sJour);
		ParcelDestination.writeString(this.sHeures);
		ParcelDestination.writeString(this.sMinutes);
		ParcelDestination.writeString(this.sSecondes);
	}

	/**
	 * CREATOR permettant la régénération de l'objet. Tous les Parcelables
	 * doivent avoir les deux méthodes utilisées ci-dessous.
	 */
	public static final Parcelable.Creator<Session>	CREATOR	= new Parcelable.Creator<Session>()
															{
																public Session createFromParcel(
																		Parcel ParcelSource)
																{
																	return new Session(
																			ParcelSource);
																}

																public Session[] newArray(
																		int nTaille)
																{
																	return new Session[nTaille];
																}
															};

	/**
	 * Constructeur qui prend un Parcel en paramètre et régénère l'objet
	 * correspondant à ce Parcel.
	 * 
	 * @param ParcelSource
	 *            Parcel utilisé pour régénérer l'objet
	 */
	private Session(Parcel ParcelSource)
	{
		this.sNomRobot = ParcelSource.readString();
		this.sAdrRobot = ParcelSource.readString();
		this.sUtilisateur = ParcelSource.readString();
		this.sAnnee = ParcelSource.readString();
		this.sMois = ParcelSource.readString();
		this.sJour = ParcelSource.readString();
		this.sHeures = ParcelSource.readString();
		this.sMinutes = ParcelSource.readString();
		this.sSecondes = ParcelSource.readString();
	}

}
