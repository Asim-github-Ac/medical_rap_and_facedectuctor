package com.example.medical_rap_tracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.example.medical_rap_tracker.Model.AdminAuth;
import com.example.medical_rap_tracker.SharedPrefrence.PrefManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class SplashActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    List<AdminAuth> authList = new ArrayList<>();
    PrefManager prefManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        prefManager=new PrefManager(this);

     new Handler().postDelayed(new Runnable() {
         @Override
         public void run() {
             mAuth = FirebaseAuth.getInstance();
             if (mAuth.getCurrentUser() != null) {
                 String email = mAuth.getCurrentUser().getEmail();
                 getVerFication(email);
                 System.out.println("email is _____________"+email);
             }else if (prefManager.getUser_Login_Type().equals("user")){
                 Intent intent =new Intent(getApplicationContext(), VerificationActivity.class);
                 intent.putExtra("key","user");
                 startActivity(intent);
                 finish();
             }
             else if (prefManager.getUser_Login_Type().equals("admin")){
                 Intent intent =new Intent(getApplicationContext(), AdminActivity.class);
                 startActivity(intent);
                 finish();
             }
             else {
                 Toast.makeText(getApplicationContext(), "account not found", Toast.LENGTH_SHORT).show();
                 startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                 finish();
             }

         }
     },3000);
    }

    public void getVerFication(String email) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("Admins").document("data").collection(email).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (queryDocumentSnapshots.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "You Need to Register", Toast.LENGTH_SHORT).show();
                } else {
                    List<AdminAuth> auths = queryDocumentSnapshots.toObjects(AdminAuth.class);
                    authList.addAll(auths);
                    if (authList.get(0).getType().equals("Admin")) {
                        Intent intent = new Intent(getApplicationContext(), AdminActivity.class);
                        startActivity(intent);
                        finish();
                        System.out.println("admin_______________" + authList.get(0).getType());


                    } else if (authList.get(0).getType().equals("Users")) {
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        finish();
                        System.out.println("users_______________" + authList.get(0).getType());

                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(getApplicationContext(), "Error" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}