package com.floatar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.appcheck.interop.BuildConfig;

public class AboutActivity extends AppCompatActivity {
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        // Set the app version number dynamically
        TextView versionNumber = findViewById(R.id.version_number);
        versionNumber.setText(BuildConfig.VERSION_NAME);

        // Set a click listener on the developer's email address to open an email intent
        TextView developerEmail1 = findViewById(R.id.developer_email);
        developerEmail1.setOnClickListener(v -> {
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                    "mailto",getString(R.string.developers_email), null));
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Regarding Your App");
            startActivity(Intent.createChooser(emailIntent, "Send email..."));
        });

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.about);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
