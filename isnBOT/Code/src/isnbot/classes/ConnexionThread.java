package isnbot.classes;

import android.os.Handler;
import android.os.Looper;

public class ConnexionThread extends Thread
{
	private Session			mSession;
	private Communication	mCommunication;
	private Handler			mConnexionHandler;

	public ConnexionThread(Session mSession, Communication mCommunication,
			Handler mConnexionHandler)
	{
		this.mSession = mSession;
		this.mCommunication = mCommunication;
		this.mConnexionHandler = mConnexionHandler;
	}

	@Override
	public void run()
	{
		connecter();
	}

	private void connecter()
	{
		Looper.prepare();

		Robot mRobot = new Robot(this.mSession.getNomRobot(),
				this.mSession.getAdrRobot());

		this.mConnexionHandler.sendEmptyMessageDelayed(0, 10000);

		if (this.mCommunication.connecter(mRobot, this.mSession))
		{
			this.mConnexionHandler.sendEmptyMessage(1);
		}
		else
		{
			this.mConnexionHandler.sendEmptyMessage(0);
		}

		Looper.loop();
	}
}
