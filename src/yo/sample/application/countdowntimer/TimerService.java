package yo.sample.application.countdowntimer;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.PowerManager;

public class TimerService extends Service {

	Context mContent;
	int counter;
	Timer timer;
	public PowerManager.WakeLock wl;

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		mContent = this;
		counter = intent.getIntExtra("counter", 0);
		if (counter != 0) {
			PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
			wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK
					+ PowerManager.ON_AFTER_RELEASE, "My Tag");
			wl.acquire();
			startTimer();
		}
	}

	public void startTimer() {
		if (timer != null) {
			timer.cancel();
		}
		timer = new Timer();
		final android.os.Handler handler = new android.os.Handler();

		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				handler.post(new Runnable() {
					@Override
					public void run() {
						// Log.d(ACCOUNT_SERVICE, "counter = " + counter);
						if (counter == -1) {
							timer.cancel();
							if (wl.isHeld()) {
								wl.release();
							}
							showAlarm();
						} else {
							CountdownTimerActivity.countdown(counter);
							counter = counter - 1;
						}
					}

					private void showAlarm() {
						Intent intent = new Intent(mContent, TimerService.class);
						mContent.stopService(intent);
						intent = new Intent(mContent, AlarmDialog.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						mContent.startActivity(intent);
					}
				});
			}
		}, 0, 1000);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		timer.cancel();
		if (wl.isHeld()) {
			wl.release();
		}
	}

}
