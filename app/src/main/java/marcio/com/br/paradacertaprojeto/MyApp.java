package marcio.com.br.paradacertaprojeto;

import android.app.Application;

/**
 * Created by Erick on 07/10/2017.
 */
import android.app.Application;
import com.facebook.appevents.AppEventsLogger;

public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        AppEventsLogger.activateApp(this);
    }

}
