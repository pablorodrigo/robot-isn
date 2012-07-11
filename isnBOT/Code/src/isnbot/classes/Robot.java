package isnbot.classes;

public class Robot
{
	private final String	sNom;
	private final String	sAdresse;
	private final boolean	estRobot;
	public boolean			bConnexion;

	public Robot(String sNom, String sAdresse)
	{
		this.sNom = sNom;
		this.sAdresse = sAdresse;
		this.estRobot = false;
		bConnexion = false;
	}

	public Robot(String sNom, String sAdresse, boolean estRobot)
	{
		this.sNom = sNom;
		this.sAdresse = sAdresse;
		this.estRobot = estRobot;
		bConnexion = false;
	}

	public String getNom()
	{
		return this.sNom;
	}

	public String getAdresse()
	{
		return this.sAdresse;
	}

	public boolean estRobot()
	{
		return this.estRobot;
	}

}
