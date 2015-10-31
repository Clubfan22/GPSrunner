package me.diskstation.ammon.gpsrunner.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.common.GoogleApiAvailability;

import me.diskstation.ammon.gpsrunner.R;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        TextView mapsLabel = (TextView) findViewById(R.id.mapsLegalLabel);
        GoogleApiAvailability gaa = GoogleApiAvailability.getInstance();
        String googleMapsLicense = gaa.getOpenSourceSoftwareLicenseInfo(getApplicationContext());
        mapsLabel.setText(googleMapsLicense);
    }
}
