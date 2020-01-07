package com.example.thelasttempl;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

public class loginactivity extends AppCompatActivity {

    private TextView top,bottom;
    private EditText id,password;
    private Button button;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;// this is a listener th
    // is triggers a thread duing registering , signing, or change in current user
    private FirebaseUser currentuser;


    private FirebaseFirestore db= FirebaseFirestore.getInstance();
    private CollectionReference collectionReference=db.collection("Users");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loginactivity);

        top=findViewById(R.id.textView);
        bottom=findViewById(R.id.textView2);

        id=findViewById(R.id.id);
        password=findViewById(R.id.password);
        button=findViewById(R.id.button);


        bottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(loginactivity.this,MainActivity.class);
                startActivity(intent);
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login(id.getText().toString().trim(),password.getText().toString().trim());
                
                
            }
        });

    }

    private void login(String email, String pwd) {

        if(!TextUtils.isEmpty(email)&&
        !TextUtils.isEmpty(pwd)){

            firebaseAuth.signInWithEmailAndPassword(email, pwd)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            FirebaseUser user=firebaseAuth.getCurrentUser();
                            assert user!=null;
                            String currentUserid=user.getUid();

                            collectionReference
                                    .whereEqualTo("userid",currentUserid)
                                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                        @Override
                                        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots,
                                                            @Nullable FirebaseFirestoreException e) {
                                         if(e!=null){ // this exception should not occur
                                             return;
                                         }
                                        }
                                    });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(loginactivity.this,"chutiya hai kya?  sahi likh",Toast.LENGTH_LONG).show();
                }
            });

        }else{
            Toast.makeText(loginactivity.this,"chutiya hai kya?",Toast.LENGTH_LONG).show();

        }
    }
}
