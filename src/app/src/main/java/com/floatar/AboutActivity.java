package com.floatar;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class AboutActivity extends AppCompatActivity {

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        // Set the app version number dynamically
        TextView versionNumber = findViewById(R.id.version_number);
        versionNumber.setText(getString(R.string.version_number) + " " + BuildConfig.VERSION_NAME);

        // Set a click listener on the developer's email address to open an email intent
        TextView developerEmail = findViewById(R.id.developer_email);
        developerEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto",getString(R.string.developer_email), null));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Regarding Your App");
                startActivity(Intent.createChooser(emailIntent, "Send email..."));
            }
        });
    }
}
