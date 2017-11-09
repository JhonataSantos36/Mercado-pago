package px_android_services.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.mercadopago.tracking.tracker.MPTracker;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MPTracker.getInstance().initTracker("TEST-ad365c37-8012-4014-84f5-6c895b3f8e0a", "MLA", "v1.0.0", this);
        MPTracker.getInstance().trackPayment(8888L, "credit_card");

        MPTracker.getInstance().trackToken("test-token-123456");
    }
}
