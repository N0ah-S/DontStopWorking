package de.deverror.dsw.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Timer;

public class ThreadUtil {

    public static void startDelayedInMain(final Runnable r, final long millis) {
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                r.run();
            }
        }, millis/1000f);
        /*new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(millis);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Gdx.app.postRunnable(r);
            }
        }).start();*/
    }

}
