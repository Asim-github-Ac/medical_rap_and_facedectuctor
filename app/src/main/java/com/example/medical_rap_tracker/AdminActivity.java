package com.example.medical_rap_tracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.medical_rap_tracker.Adapter.AdminView;
import com.example.medical_rap_tracker.Adapter.DoctorAdapter;
import com.example.medical_rap_tracker.Model.AdminAuth;
import com.example.medical_rap_tracker.Model.DoctorModel;
import com.example.medical_rap_tracker.SharedPrefrence.PrefManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class AdminActivity extends AppCompatActivity {
    Toolbar toolbar;
    RecyclerView recyclerView;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    AdminView adminView;
    List<AdminAuth> authList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        sharedPreferences=getSharedPreferences("adminAcc",MODE_PRIVATE);
         editor=sharedPreferences.edit();
        toolbar = findViewById(R.id.toolbar);
        recyclerView = findViewById(R.id.rvadmin);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        GetData();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.admin_optional_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.emp:
                startActivity(new Intent(getApplicationContext(), AddNewEmpActivity.class));

                break;
            case R.id.signout:
                Toast.makeText(getApplicationContext(), "Sign Out", Toast.LENGTH_SHORT).show();
                editor.putString("email","");
                editor.putString("pass","");
                editor.apply();
                editor.commit();
                SharedPreferences sharedPreferences = getSharedPreferences("HashMap", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("map","{}");
                editor.apply();
                editor.commit();
                FirebaseAuth.getInstance().signOut();
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

    public void GetData() {
        PrefManager prefManager = new PrefManager(this);
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("Admin Under Users").document("data").collection(prefManager.getUserEmail()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                if (queryDocumentSnapshots.isEmpty()) {
                    Toast.makeText(AdminActivity.this, "user Not Found", Toast.LENGTH_SHORT).show();
                } else {
                    List<AdminAuth> auths = queryDocumentSnapshots.toObjects(AdminAuth.class);
                    authList.addAll(auths);
                    adminView = new AdminView(getApplicationContext(), authList);
                    recyclerView.setAdapter(adminView);

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(AdminActivity.this, "Error" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to exit?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        AdminActivity.super.onBackPressed();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }
}