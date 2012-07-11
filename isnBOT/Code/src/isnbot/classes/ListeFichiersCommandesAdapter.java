package isnbot.classes;

import isnbot.activities.ExecutionFichierActivity;
import isnbot.activities.R;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ListeFichiersCommandesAdapter extends BaseAdapter
{
	private final String				TAG	= "ListeFichiersCommandesAdapter >>>";
	private ArrayList<FichierCommandes>	mFichiersCommandes;

	private LayoutInflater				mInflater;

	private ExecutionFichierActivity	mContext;

	public ListeFichiersCommandesAdapter(ExecutionFichierActivity mContext,
			ArrayList<FichierCommandes> mFichiersCommandes)
	{
		this.mContext = mContext;
		this.mInflater = LayoutInflater.from(mContext);
		this.mFichiersCommandes = mFichiersCommandes;
	}

	public int getCount()
	{
		return mFichiersCommandes.size();
	}

	public Object getItem(int index)
	{
		return mFichiersCommandes.get(index);
	}

	public long getItemId(int index)
	{
		return 0;
	}

	public View getView(final int nPosition, View convertView, ViewGroup parent)
	{
		FichierCommandesView mFichierCommandesView;

		if (convertView == null)
		{
			mFichierCommandesView = new FichierCommandesView();
			convertView = mInflater.inflate(R.layout.fichier_commandes_view,
					null);

			mFichierCommandesView.tvNom = (TextView) convertView
					.findViewById(R.id.tvNom);
			mFichierCommandesView.tvNombreCommandes = (TextView) convertView
					.findViewById(R.id.tvNombreCommandes);
			mFichierCommandesView.tvTaille = (TextView) convertView
					.findViewById(R.id.tvTaille);
			mFichierCommandesView.tvDerniereModification = (TextView) convertView
					.findViewById(R.id.tvDerniereModification);

			convertView.setTag(mFichierCommandesView);
		}
		else
		{
			mFichierCommandesView = (FichierCommandesView) convertView.getTag();
		}

		final String sNom = mFichiersCommandes.get(nPosition).getNom();
		final String sNombreCommandes = mFichiersCommandes.get(nPosition)
				.getNombreCommandes();
		final String sTaille = mFichiersCommandes.get(nPosition).getTaille();
		final String sDateModification = mFichiersCommandes.get(nPosition)
				.getDerniereModification();

		mFichierCommandesView.tvNom.setText(sNom);
		mFichierCommandesView.tvNombreCommandes.setText(sNombreCommandes);
		mFichierCommandesView.tvTaille.setText(sTaille);
		mFichierCommandesView.tvDerniereModification.setText(sDateModification);

		convertView.setClickable(true);
		convertView.setFocusable(true);

		OnClickListener myClickListener = new OnClickListener()
		{
			public void onClick(View v)
			{
				chargerCommandes(nPosition);
				v.setPressed(true);
			}
		};

		convertView.setOnClickListener(myClickListener);
		convertView.setPadding(10, 10, 10, 10);

		return convertView;
	}

	private void chargerCommandes(int nPosition)
	{
		try
		{
			mContext.bPremiereOuverture = true;
			mContext.mCommandes = mFichiersCommandes.get(nPosition)
					.getCommandes();
			mContext.nIndexCommandeEnAttente = 0;
			mContext.afficherCommande();
		}
		catch (FileNotFoundException mException)
		{
			Log.e("Fichier non trouvé", mException.getMessage());
		}
	}

	public void clear()
	{
		this.mFichiersCommandes.clear();
	}
}
