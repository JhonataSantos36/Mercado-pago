package com.mercadopago;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;
import android.widget.TextView;

public class TermsAndConditionsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView();

        // Set terms and conditions
        if (getIntent().getStringExtra("termsAndConditions") != null) {
            TextView termsAndConditions = (TextView) findViewById(R.id.termsAndConditions);
            termsAndConditions.setText(getIntent().getStringExtra("termsAndConditions"));
        }
        else
        {
            WebView webview = new WebView(this);
            setContentView(webview);
            //TODO ir a pagina correspondiente segun PK
            webview.loadUrl("https://www.mercadopago.com.mx/ayuda/terminos-y-condiciones_715");
        }
    }

    protected void setContentView() {

        setContentView(R.layout.activity_terms_and_conditions);
    }
}
