package com.example.medical_rap_tracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.medical_rap_tracker.Model.AdminAuth;
import com.example.medical_rap_tracker.Model.AdminModel;
import com.example.medical_rap_tracker.Model.EmpModel;
import com.example.medical_rap_tracker.SharedPrefrence.PrefManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference mRef;
    PrefManager prefManager;
    FirebaseFirestore firestore;
    List<EmpModel> empModelList = new ArrayList<>();
    Button btnLogin;
    TextInputLayout inputLayoutEmailAddress, inputLayoutPassword;
    TextView tvCreateNewAccount;
    RadioGroup radioGroup;
    ProgressDialog progressDialog;
    List<AdminAuth> authList = new ArrayList<>();
    String accountType = "Users";
    SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences=getSharedPreferences("adminAcc",MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        progressDialog = new ProgressDialog(this);
        mAuth=FirebaseAuth.getInstance();
        prefManager = new PrefManager(this);
        firestore = FirebaseFirestore.getInstance();
        progressDialog.setTitle("Verification");
        progressDialog.setMessage("Loading.......");
        String email= sharedPreferences.getString("email","");
        String pass= sharedPreferences.getString("pass","");



        if (!email.isEmpty() && !pass.isEmpty())
        {
            Toast.makeText(getApplicationContext(), ""+email+pass, Toast.LENGTH_SHORT).show();
            progressDialog.show();
            mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {

                        String UID = mAuth.getCurrentUser().getUid();
                        System.out.println("u id ---------------" + UID);
                        accountType="Admin";
                        GetVerFication(email);

                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();

                    System.out.println("error is ________________" + e.getMessage());
                }
            });

        }
        setContentView(R.layout.activity_login_activty);
        radioGroup = findViewById(R.id.radioGroup2);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Verification");
        progressDialog.setMessage("Loading.......");



        initView();
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.radioButton4:
                        accountType = "Admin";
                        Toast.makeText(getApplicationContext(), "Clicked on Admin", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.radioButton3:
                        accountType = "Users";
                        Toast.makeText(getApplicationContext(), "clicked on user", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        Toast.makeText(getApplicationContext(), "Please Select Account Type", Toast.LENGTH_SHORT).show();
                        break;


                }
            }
        });
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.show();
                String emailAddress = inputLayoutEmailAddress.getEditText().getText().toString().trim();
                String password = inputLayoutPassword.getEditText().getText().toString().trim();
                if (accountType == "Admin") {
                    if (!emailAddress.isEmpty() && !password.isEmpty()) {

                        mAuth.signInWithEmailAndPassword(emailAddress, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    String UID = mAuth.getCurrentUser().getUid();
                                    System.out.println("u id ---------------" + UID);
                                    editor.putString("email",emailAddress);
                                    editor.putString("pass",password);
                                    editor.apply();
                                    editor.commit();
                                    PrefManager prefManager = new PrefManager(getApplicationContext());
                                    prefManager.setUserEmail(emailAddress);
                                    GetVerFication(emailAddress);

                                }

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();

                                System.out.println("error is ________________" + e.getMessage());
                            }
                        });
                    } else {
                        if (emailAddress.isEmpty()) {
                            inputLayoutEmailAddress.setError("Enter Email Address");
                        } else if (password.isEmpty()) {
                            inputLayoutPassword.setError("Please Enter Password");
                        } else {

                        }

                    }


                } else if (accountType == "Users") {


                    GetVerFication(emailAddress);
                    PrefManager prefManager = new PrefManager(getApplicationContext());
                    prefManager.setUserEmail(emailAddress);


                }
            }
        });
        tvCreateNewAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                builder.setCancelable(false);
                builder.setTitle("Important");
                builder.setMessage("User's just create Admin account. Then Add number of empolyee's");
                builder.setPositiveButton("Understand", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        startActivity(new Intent(getApplicationContext(), SignupActivity.class));

                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builder.create().show();
            }
        });
    }

    private void initView() {
        inputLayoutEmailAddress = findViewById(R.id.textInputLayout);
        inputLayoutPassword = findViewById(R.id.textInputLayout2);
        btnLogin = findViewById(R.id.button);
        tvCreateNewAccount = findViewById(R.id.textView);
        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        mRef = firebaseDatabase.getReference("admins");
    }

    public void GetVerFication(String email) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("Admins").document("data").collection(email).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (queryDocumentSnapshots.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "You Need to Register", Toast.LENGTH_SHORT).show();
                } else {
                    List<AdminAuth> auths = queryDocumentSnapshots.toObjects(AdminAuth.class);
                    authList.addAll(auths);
                    if (authList.get(0).getType().equals("Admin")) {
                        if (accountType.equals(authList.get(0).getType())) {
                            Intent intent = new Intent(getApplicationContext(), AdminActivity.class);
                            intent.putExtra("key","admin");
                            startActivity(intent);
                            finish();
                            prefManager.setUser_Login_Type("admin");
                            progressDialog.dismiss();
                            System.out.println("admin_______________" + authList.get(0).getType());
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(LoginActivity.this, "Please Select Admin", Toast.LENGTH_SHORT).show();
                        }

                    } else if (authList.get(0).getType().equals("Users")) {
                        if (accountType.equals(authList.get(0).getType())) {
                            getveri();
                            SharedPreferences sharedPreferences = getSharedPreferences("HashMap", MODE_PRIVATE);
                            String face=sharedPreferences.getString("map","{}");
                            if (!face.equals("{}"))
                            {
                               Intent intent= new Intent(getApplicationContext(), VerificationActivity.class);
                                intent.putExtra("key","user");
                               startActivity(intent);
                                finish();
                                prefManager.setUser_Login_Type("user");
                            }
                            progressDialog.dismiss();
                            System.out.println("users_______________" + authList.get(0).getType());
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(LoginActivity.this, "Account Not Found", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(LoginActivity.this, "Error" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getveri() {

            firestore.collection("Admins").document("data").collection(prefManager.getUserEmail()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    if (queryDocumentSnapshots.isEmpty()) {
                        Toast.makeText(getApplicationContext(), "Something Went Wrong", Toast.LENGTH_SHORT).show();
                    } else {
                        List<EmpModel> empModels = queryDocumentSnapshots.toObjects(EmpModel.class);
                        empModelList.addAll(empModels);
                       String userface = empModelList.get(0).getFace().toString();
                        SharedPreferences sharedPreferences = getSharedPreferences("HashMap", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("map",userface);
                        editor.commit();
                        editor.apply();
                        final Handler handler = new Handler(Looper.getMainLooper());
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                //Do something after 100ms
                                Toast.makeText(getApplicationContext(), "" + userface, Toast.LENGTH_SHORT).show();
                            }
                        }, 4000);



                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

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
                        LoginActivity.super.onBackPressed();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }
}