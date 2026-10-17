package com.paystack.samplemovieticket.view;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabsIntent;

import com.google.android.material.textfield.TextInputLayout;
import com.paystack.samplemovieticket.BuildConfig;
import com.paystack.samplemovieticket.R;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class PaymentActivity extends AppCompatActivity {

    private static final String TAG = "PaymentActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        TextInputLayout cardNumber = findViewById(R.id.til_card_number);
        TextInputLayout cardExpiry = findViewById(R.id.til_card_expiry);
        TextInputLayout cardCvv = findViewById(R.id.til_card_cvv);
        cardNumber.setVisibility(View.GONE);
        cardExpiry.setVisibility(View.GONE);
        cardCvv.setVisibility(View.GONE);

        Button button = findViewById(R.id.btn_make_payment);
        button.setOnClickListener(v -> startNovaHostedPay());
    }

    private void startNovaHostedPay() {
        new Thread(() -> {
            try {
                String authUrl = initializeNovaPayment();
                runOnUiThread(() -> openHostedPay(authUrl));
            } catch (Exception error) {
                Log.e(TAG, "initialize failed", error);
                runOnUiThread(() ->
                        Toast.makeText(this, "Could not start payment", Toast.LENGTH_LONG).show());
            }
        }).start();
    }

    private String initializeNovaPayment() throws Exception {
        URL url = new URL(BuildConfig.PAYMENT_API_BASE + "/transaction/initialize");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        JSONObject metadata = new JSONObject();
        metadata.put("product_id", BuildConfig.NOVA_PRODUCT_ID);

        JSONObject body = new JSONObject();
        body.put("email", "customer@email.com");
        body.put("metadata", metadata);

        byte[] payload = body.toString().getBytes(StandardCharsets.UTF_8);
        try (OutputStream os = connection.getOutputStream()) {
            os.write(payload);
        }

        int code = connection.getResponseCode();
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                code >= 200 && code < 300 ? connection.getInputStream() : connection.getErrorStream(),
                StandardCharsets.UTF_8));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();

        if (code < 200 || code >= 300) {
            throw new IllegalStateException("HTTP " + code + ": " + response);
        }

        JSONObject json = new JSONObject(response.toString());
        JSONObject data = json.getJSONObject("data");
        return data.getString("authorization_url");
    }

    private void openHostedPay(String authorizationUrl) {
        CustomTabsIntent tabs = new CustomTabsIntent.Builder().build();
        tabs.launchUrl(this, Uri.parse(authorizationUrl));
    }
}
