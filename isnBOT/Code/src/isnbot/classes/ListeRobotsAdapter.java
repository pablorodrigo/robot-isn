package isnbot.classes;

import isnbot.activities.MenuActivity;
import isnbot.activities.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ListeRobotsAdapter extends BaseAdapter
{
	private final String		TAG	= "ListeRobotsAdapter >>>";
	private ArrayList<Robot>	mRobots;

	private LayoutInflater		inflater;

	private Context				context;

	public ListeRobotsAdapter(Context context, ArrayList<Robot> mRobots)
	{
		this.context = context;
		inflater = LayoutInflater.from(context);
		this.mRobots = mRobots;
	}

	public int getCount()
	{
		return mRobots.size();
	}

	public Object getItem(int index)
	{
		return mRobots.get(index);
	}

	public long getItemId(int index)
	{
		return 0;
	}

	public View getView(int position, View convertView, ViewGroup parent)
	{
		RobotView mRobotView;

		if (convertView == null)
		{
			mRobotView = new RobotView();
			convertView = inflater.inflate(R.layout.robot_view, null);
			mRobotView.tvNom = (TextView) convertView.findViewById(R.id.tvNom);
			mRobotView.tvAdresse = (TextView) convertView
					.findViewById(R.id.tvAdresse);
			mRobotView.ivIcon = (ImageView) convertView
					.findViewById(R.id.ivIcon);
			convertView.setTag(mRobotView);
		}
		else
		{
			mRobotView = (RobotView) convertView.getTag();
		}

		final String sNom = mRobots.get(position).getNom();
		final String sAdresse = mRobots.get(position).getAdresse();

		mRobotView.tvNom.setText(sNom);
		mRobotView.tvAdresse.setText(sAdresse);

		if (mRobots.get(position).estRobot())
		{
			int imageResource = context.getResources().getIdentifier(
					"@drawable/icon_nxt", null, context.getPackageName());
			Drawable image = context.getResources().getDrawable(imageResource);
			mRobotView.ivIcon.setImageDrawable(image);
		}
		else
		{
			int imageResource = context.getResources().getIdentifier(
					"@drawable/icon_warning", null, context.getPackageName());
			Drawable image = context.getResources().getDrawable(imageResource);
			mRobotView.ivIcon.setImageDrawable(image);
		}

		convertView.setClickable(true);
		convertView.setFocusable(true);

		OnClickListener myClickListener = new OnClickListener()
		{
			public void onClick(View v)
			{
				afficherFenetreSession(sNom, sAdresse);
				v.setPressed(true);
			}
		};

		convertView.setOnClickListener(myClickListener);
		convertView.setPadding(10, 10, 10, 10);

		return convertView;
	}

	/**
	 * Affiche l'alertDialog permettant d'entrer le nom d'utilisateur et ainsi
	 * paramétrer et ouvrir la session.
	 * 
	 * @param sNomRobot
	 *            Nom du robot
	 * @param sAdrRobot
	 *            Adresse MAC du robot
	 */
	private void afficherFenetreSession(final String sNomRobot,
			final String sAdresseRobot)
	{
		// On instancie le fichier XML d'interface
		final LayoutInflater factory = LayoutInflater.from(context);
		// On y ajoute l'XML de l'AlertDialog
		final View alertDialogView = factory.inflate(R.layout.validation_session, null);

		// On instancie l'AlertDialog
		AlertDialog.Builder adb = new AlertDialog.Builder(context);
		// On lui passe son interface
		adb.setView(alertDialogView);
		// On lui défini son titre
		adb.setTitle("Paramètres de session");
		// On lui défini son icône
		adb.setIcon(R.drawable.stat_sys_data_bluetooth_connected);

		// On récupère les informations sur la date de création de la session
		final Date mDate = new Date();
		final Calendar mCalendar = new GregorianCalendar();
		final int nAnnee = mCalendar.get(Calendar.YEAR);
		final int nMois = mCalendar.get(Calendar.MONTH);
		final int nJour = mCalendar.get(Calendar.DAY_OF_MONTH);
		final String sAnnee = String.valueOf(nAnnee);
		final String sMois = String.valueOf(nMois + 1);
		final String sJour = String.valueOf(nJour);
		final String sHeures = String.valueOf(mDate.getHours());
		final String sMinutes = String.valueOf(mDate.getMinutes());
		final String sSecondes = String.valueOf(mDate.getSeconds());

		// On affiche les informations sur la session dans l'AlertDialog
		TextView tvRobot = (TextView) alertDialogView
				.findViewById(R.id.tvRobot);
		tvRobot.setText(sNomRobot.concat(" - ").concat(sAdresseRobot));

		TextView tvSession = (TextView) alertDialogView
				.findViewById(R.id.tvSession);
		tvSession.setText("Utilisateur_" + sAnnee + "_" + sMois + "_" + sJour
				+ "_" + sHeures + "h_" + sMinutes + "min_" + sSecondes + "sec");

		// On ajoute un bouton "OK"
		adb.setPositiveButton("Commencer",
				new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface mDialogInterface,
							int nIndex)
					{
						// On vérifie le contenu du champs "Nom d'utilisateur"
						final EditText etNomUtilisateur = (EditText) alertDialogView
								.findViewById(R.id.etUsername);
						final String sUsername = etNomUtilisateur.getText()
								.toString();
						final Pattern mPattern = Pattern
								.compile("[a-zA-z]+([ '-][a-zA-Z]+)*");
						final Matcher mMatcher = mPattern.matcher(sUsername);

						// Si le contenu du champs est incorrect
						if (mMatcher.matches() == false)
						{
							// On averti l'utilisateur
							Toast.makeText(context,
									"Nom d'utilisateur incorrect !",
									Toast.LENGTH_SHORT).show();
						}
						else
						// si le contenu du champs est correct
						{
							// On crée un Intent pour lancer l'Activity du menu
							Intent iMenuActivity = new Intent(context,
									MenuActivity.class);

							String[] sSession = new String[9];
							sSession[0] = sNomRobot;
							sSession[1] = sAdresseRobot;
							sSession[2] = sUsername;
							sSession[3] = sAnnee;
							sSession[4] = sMois;
							sSession[5] = sJour;
							sSession[6] = sHeures;
							sSession[7] = sMinutes;
							sSession[8] = sSecondes;

							// On crée la session
							final Session mSession = new Session(sSession);

							// On passe l'objet de la session à la prochaine
							// Activity
							iMenuActivity.putExtra("mSession", mSession);

							// On démarre l'Activity du menu
							context.startActivity(iMenuActivity);
						}
					}
				});

		// On ajoute un bouton "Annuler"
		adb.setNegativeButton("Annuler", new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int which)
			{
				// Si l'utilisateur clic sur le bouton annuler, on ferme
				// simplement l'AlertDialog
			}
		});

		// On affiche l'AlertDialog
		adb.show();
	}

	public void clear()
	{
		this.mRobots.clear();
	}
}
