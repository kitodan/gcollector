package com.root.gcolletctor;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

public class WelcomeActivity extends AppCompatActivity {
    private  static ProgressDialog progressDialog;
    private static Button buttonProceed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        //get the button ui id
        buttonProceed=(Button)findViewById(R.id.button_proceed);
        //create a progress dialog
        progressDialog = new ProgressDialog(this);

        //handling the button click event
        buttonProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.setMessage("..........");
                Intent mainActivity = new Intent(getApplicationContext(),MainActivity.class);
                progressDialog.dismiss();
                startActivity(mainActivity);
            }
        });

    }
}
