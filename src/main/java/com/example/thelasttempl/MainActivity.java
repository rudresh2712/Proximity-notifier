package com.example.thelasttempl;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import util.TodoApi;

public class MainActivity extends AppCompatActivity {

    private EditText name,color,email,password;
    private TextView top,bottom;
    private Button button;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentuser;

    private FirebaseFirestore db= FirebaseFirestore.getInstance();
    private CollectionReference collectionReference=db.collection("Users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        top=findViewById(R.id.textView);
        bottom=findViewById(R.id.textView2);
        name=findViewById(R.id.name);
        email=findViewById(R.id.id);
        color=findViewById(R.id.color);
        password=findViewById(R.id.password);
        button=findViewById(R.id.button);




        bottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this,loginactivity.class);
                startActivity(intent);
//                Intent in=new Intent(MainActivity.this,MapsActivity.class);
//                startActivity(in);
            }
        });

        firebaseAuth=FirebaseAuth.getInstance();

//        firebaseAuth.createUserWithEmailAndPassword(email, password)
//                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
//                            // Sign in success, update UI with the signed-in user's information
////                            Log.d(TAG, "createUserWithEmail:success");
////                            FirebaseUser user = mAuth.getCurrentUser();
////                            updateUI(user);
//                        } else {
//                            // If sign in fails, display a message to the user.
//                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
//                            Toast.makeText(MainActivity.this, "Authentication failed.",
//                                    Toast.LENGTH_SHORT).show();
//                           // updateUI(null);
//                        }
//
//                        // ...
//                    }
//                });

        authStateListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                currentuser=firebaseAuth.getCurrentUser();
                if(currentuser!=null)
                {//user is already logged in

                }
                else
                {// no user yet
               Log.d("tag","no current user");
                }
            }
        };

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!TextUtils.isEmpty(email.getText().toString())
                  &&!TextUtils.isEmpty(name.getText().toString())
                && !TextUtils.isEmpty(password.getText().toString()))
                {
                    String semail=email.getText().toString();
                    String sname=name.getText().toString();
                    String spasssword=password.getText().toString();
                    createuserEmailAccount(semail,spasssword,sname);
                }

                else
                    Toast.makeText(MainActivity.this,"EmptyFields not allowed",
                            Toast.LENGTH_LONG).show();

            }
        });
    }

    private void createuserEmailAccount(String email, String password, final String name)
    {
        if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)
                && !TextUtils.isEmpty(email) )
        {
             firebaseAuth.createUserWithEmailAndPassword(email,password)
                     .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                         @Override
                         public void onComplete(@NonNull Task<AuthResult> task) {
                           // task here is the actual user that we created
                             if(task.isSuccessful()){
                                 // we take usr to our activity
                                 currentuser=firebaseAuth.getCurrentUser();
                                 assert currentuser!=null;
                                 final String currentuserid=currentuser.getUid();

                                 // here we create a user map so that we can create user collection

                                 Map<String,String> userobj=new HashMap<>();
                                 userobj.put("userid",currentuserid);
                                 userobj.put("username",name);


                                 //save to our firestore database
                                 //probably user that we created
                                 collectionReference.add(userobj)
                                         .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                             @Override
                                             public void onSuccess(DocumentReference documentReference) {

                                                 documentReference.get()
                                                         .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                             @Override
                                                             public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                          if(task.getResult().exists())
                                                          {

                                                           String name=task.getResult().getString("name");

                                                           Intent in=new Intent(MainActivity.this,MapsActivity.class);
//                                                           in.putExtra("username",name);
//                                                           in.putExtra("userid",currentuserid);
                                                              TodoApi todoapi=new TodoApi();
                                                              todoapi.setUsename(name);
                                                              todoapi.setUserid(currentuserid);
                                                              Log.d("Logged in"," yes ");
                                                           startActivity(in);
                                                           //   Toast.makeText(MainActivity.this, "on the map", Toast.LENGTH_SHORT).show();
                                                          }

                                                             }
                                                         });
                                             }
                                         })
                                         .addOnFailureListener(new OnFailureListener() {
                                             @Override
                                             public void onFailure(@NonNull Exception e) {

                                                      Toast.makeText(MainActivity.this,"Unable to signup",
                                                              Toast.LENGTH_LONG).show();
                                                      Log.d("NOt signup","error here");
                                             }
                                         });

                             }
                         }
                     })
                     .addOnFailureListener(new OnFailureListener() {
                         @Override
                         public void onFailure(@NonNull Exception e) {

                         }
                     });
        }else
        {

        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        currentuser= firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(authStateListener);
    }
}
