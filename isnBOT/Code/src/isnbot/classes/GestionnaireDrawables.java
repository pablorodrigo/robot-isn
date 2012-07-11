package isnbot.classes;

import android.content.Context;
import android.graphics.drawable.Drawable;

public class GestionnaireDrawables
{
	public static final String	TAG	= "GestionnaireDrawables >>>";

	public static final Drawable getBatterieIcone(Context mContext,
			short nNiveauBatterie)
	{
		int nImageRessource = mContext.getResources().getIdentifier(
				"@drawable/stat_sys_battery_unknown", null,
				mContext.getPackageName());

		// nNiveauBatterie = (short) ((nNiveauBatterie / 9000) * 100);

		if (nNiveauBatterie < 0)
		{
			nImageRessource = mContext.getResources().getIdentifier(
					"@drawable/stat_sys_battery_unknown", null,
					mContext.getPackageName());
		}
		else if (nNiveauBatterie < 2000 && nNiveauBatterie >= 0)
		{
			nImageRessource = mContext.getResources().getIdentifier(
					"@drawable/stat_sys_battery_charge_anim0", null,
					mContext.getPackageName());
		}
		else if (nNiveauBatterie >= 2000 && nNiveauBatterie < 4000)
		{
			nImageRessource = mContext.getResources().getIdentifier(
					"@drawable/stat_sys_battery_charge_anim1", null,
					mContext.getPackageName());
		}
		else if (nNiveauBatterie >= 4000 && nNiveauBatterie < 6000)
		{
			nImageRessource = mContext.getResources().getIdentifier(
					"@drawable/stat_sys_battery_charge_anim2", null,
					mContext.getPackageName());
		}
		else if (nNiveauBatterie >= 6000 && nNiveauBatterie < 8000)
		{
			nImageRessource = mContext.getResources().getIdentifier(
					"@drawable/stat_sys_battery_charge_anim3", null,
					mContext.getPackageName());
		}
		else if (nNiveauBatterie > 8000)
		{
			nImageRessource = mContext.getResources().getIdentifier(
					"@drawable/stat_sys_battery_charge_anim4", null,
					mContext.getPackageName());
		}

		return mContext.getResources().getDrawable(nImageRessource);
	}

	public static final Drawable getSignalIcone(Context mContext,
			int nPuissanceSignal)
	{
		int nImageRessource = mContext.getResources().getIdentifier(
				"@drawable/stat_sys_signal_null", null,
				mContext.getPackageName());

		nPuissanceSignal = ((nPuissanceSignal / 65535) * 100);

		if (nPuissanceSignal <= 0)
		{
			nImageRessource = mContext.getResources().getIdentifier(
					"@drawable/stat_sys_signal_null", null,
					mContext.getPackageName());
		}
		else if (nPuissanceSignal < 25 && nPuissanceSignal > 0)
		{
			nImageRessource = mContext.getResources().getIdentifier(
					"@drawable/stat_sys_signal_1", null,
					mContext.getPackageName());
		}
		else if (nPuissanceSignal >= 25 && nPuissanceSignal < 50)
		{
			nImageRessource = mContext.getResources().getIdentifier(
					"@drawable/stat_sys_signal_2", null,
					mContext.getPackageName());
		}
		else if (nPuissanceSignal >= 50 && nPuissanceSignal < 75)
		{
			nImageRessource = mContext.getResources().getIdentifier(
					"@drawable/stat_sys_signal_3", null,
					mContext.getPackageName());
		}
		else if (nPuissanceSignal >= 75 && nPuissanceSignal <= 100)
		{
			nImageRessource = mContext.getResources().getIdentifier(
					"@drawable/stat_sys_signal_4", null,
					mContext.getPackageName());
		}
		else if (nPuissanceSignal > 100)
		{
			nImageRessource = mContext.getResources().getIdentifier(
					"@drawable/stat_sys_battery_null", null,
					mContext.getPackageName());
		}

		return mContext.getResources().getDrawable(nImageRessource);
	}

	public static Drawable getMemoireIcone(Context mContext,
			int nMemoireDisponible)
	{
		int nImageRessource = mContext.getResources().getIdentifier(
				"@drawable/stat_sys_battery_unknown", null,
				mContext.getPackageName());

		nMemoireDisponible = ((nMemoireDisponible / 65535) * 100);

		if (nMemoireDisponible < 10 && nMemoireDisponible >= 0)
		{
			nImageRessource = mContext.getResources().getIdentifier(
					"@drawable/stat_sys_battery_unknown", null,
					mContext.getPackageName());
		}
		else if (nMemoireDisponible >= 10 && nMemoireDisponible < 20)
		{
			nImageRessource = mContext.getResources().getIdentifier(
					"@drawable/stat_sys_battery_1", null,
					mContext.getPackageName());
		}
		else if (nMemoireDisponible >= 20 && nMemoireDisponible < 40)
		{
			nImageRessource = mContext.getResources().getIdentifier(
					"@drawable/stat_sys_battery_2", null,
					mContext.getPackageName());
		}
		else if (nMemoireDisponible >= 40 && nMemoireDisponible < 60)
		{
			nImageRessource = mContext.getResources().getIdentifier(
					"@drawable/stat_sys_battery_3", null,
					mContext.getPackageName());
		}
		else if (nMemoireDisponible >= 60 && nMemoireDisponible < 80)
		{
			nImageRessource = mContext.getResources().getIdentifier(
					"@drawable/stat_sys_battery_4", null,
					mContext.getPackageName());
		}
		else if (nMemoireDisponible >= 80 && nMemoireDisponible <= 100)
		{
			nImageRessource = mContext.getResources().getIdentifier(
					"@drawable/stat_sys_battery_5", null,
					mContext.getPackageName());
		}
		else if (nMemoireDisponible > 100)
		{
			nImageRessource = mContext.getResources().getIdentifier(
					"@drawable/stat_sys_battery_unknown", null,
					mContext.getPackageName());
		}

		return mContext.getResources().getDrawable(nImageRessource);
	}
}
