package px_android_services.myapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.mercadopago.lite.callbacks.Callback;
import com.mercadopago.lite.core.MercadoPagoServices;
import com.mercadopago.lite.exceptions.ApiException;
import com.mercadopago.lite.model.Issuer;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MercadoPagoServices mercadoPagoServices = new MercadoPagoServices.Builder()
                .setContext(this)
                .setPublicKey("TEST-ad365c37-8012-4014-84f5-6c895b3f8e0a")
                .build();

        mercadoPagoServices.getIssuers("visa", "450995", new Callback<List<Issuer>>() {
            @Override
            public void success(List<Issuer> issuers) {
                Toast.makeText(MainActivity.this, "Success", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void failure(ApiException apiException) {
                Toast.makeText(MainActivity.this, "Failure", Toast.LENGTH_SHORT).show();
            }
        });


    }
}
