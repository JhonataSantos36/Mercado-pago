package com.mercadopago;

import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.mercadopago.examples.R;

public class ExampleActivity extends AppCompatActivity {

    @Override
    public void onDestroy() {
        super.onDestroy();

        ImageView imageView = (ImageView) findViewById(R.id.image);
        imageView.setImageDrawable(null);
    }
}
