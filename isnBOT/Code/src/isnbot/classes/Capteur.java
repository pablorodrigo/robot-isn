package isnbot.classes;

import android.widget.ProgressBar;
import android.widget.TextView;

public class Capteur
{
	private String		sNom;
	private String		sCapteur;
	private int			nPort;
	private byte		byType;
	private byte		byMode;
	private byte[]		byReponse;
	private TextView	tvNom;
	private TextView	tvValeur;
	private ProgressBar	pbValeur;

	public Capteur(String sNom)
	{
		this.sNom = sNom;
		this.sCapteur = "";
		this.nPort = 0;
		this.byType = 0;
		this.byMode = 0;
		this.tvNom = null;
		this.tvValeur = null;
		this.pbValeur = null;
	}

	public void setNom(String sNom)
	{
		this.sNom = sNom;
	}

	public void setCapteur(String sCapteur)
	{
		this.sCapteur = sCapteur;
	}

	public void setPort(int nPort)
	{
		this.nPort = nPort;
	}

	public void setType(byte byType)
	{
		this.byType = byType;
	}

	public void setMode(byte byMode)
	{
		this.byMode = byMode;
	}

	public void setReponse(byte[] byReponse)
	{
		this.byReponse = byReponse;
	}

	public void setTvNom(TextView tvNom)
	{
		this.tvNom = tvNom;
	}

	public void setTvValeur(TextView tvValeur)
	{
		this.tvValeur = tvValeur;
	}

	public void setProgressBar(ProgressBar pbValeur)
	{
		this.pbValeur = pbValeur;
	}

	public String getNom()
	{
		return this.sNom;
	}

	public String getCapteur()
	{
		return this.sCapteur;
	}

	public int getPort()
	{
		return this.nPort;
	}

	public byte getType()
	{
		return this.byType;
	}

	public byte getMode()
	{
		return this.byMode;
	}

	public byte[] getReponse()
	{
		return this.byReponse;
	}

	public TextView getTvNom()
	{
		return this.tvNom;
	}

	public TextView getTvValeur()
	{
		return this.tvValeur;
	}

	public ProgressBar getPbValeur()
	{
		return this.pbValeur;
	}
}
