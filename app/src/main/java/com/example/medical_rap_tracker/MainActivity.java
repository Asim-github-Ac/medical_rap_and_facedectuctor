package com.example.medical_rap_tracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.medical_rap_tracker.SharedPrefrence.PrefManager;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    Button btnStart;
    Toolbar toolbar;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnStart=findViewById(R.id.button3);
        toolbar=findViewById(R.id.toolbar2);
        sharedPreferences=getPreferences(MODE_PRIVATE);
        editor=sharedPreferences.edit();
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);
        int val=sharedPreferences.getInt("val",0);
        if (val==1)
        {
            btnStart.setText("Started");
        }

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editor.putInt("val",1);
                editor.commit();
                editor.apply();
                btnStart.setText("Started");
                startActivity(new Intent(getApplicationContext(),DataUploadActivity.class));
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.emp_optional_menu, menu);

        return super.onCreateOptionsMenu(menu);

    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.end:
               // startActivity(new Intent(getApplicationContext(), AddNewEmpActivity.class));

                break;
            case R.id.signout:
                FirebaseAuth.getInstance().signOut();
                SharedPreferences sharedPreferences = getSharedPreferences("HashMap", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("map","{}");
                editor.apply();
                editor.commit();
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                PrefManager prefManager=new PrefManager(this);
                prefManager.setUser_Login_Type("");
                break;
            default:
                Toast.makeText(getApplicationContext(), "Item not found", Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to exit?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        MainActivity.super.onBackPressed();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        startActivity(new Intent(getApplicationContext(), VerificationActivity.class));
    }
}