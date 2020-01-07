package com.example.thelasttempl;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ListActivity;
import android.content.Intent;
import android.location.Address;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
//import com.google.type.Date;

//import java.util.Date;

import java.sql.Date;
import java.util.Calendar;

import model.Marker;
import util.TodoApi;

public class SavingTodo extends AppCompatActivity {

    public static final int  GALLERY_CODE=1;
     private EditText title,todo;
    private Button savemarker;
    private ImageView backimage,camera;
    private TextView username;

    private Uri imageuri;

    //passed between activities
    private String currentuserid,currentusername;
    private Address address;

    private FirebaseUser user;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;


   //Connections to firebase
    private FirebaseFirestore db=FirebaseFirestore.getInstance();
    private StorageReference storageReference;
    private CollectionReference collectionReference= db.collection("Marker");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saving_todo);

        title=findViewById(R.id.editText);
        username=findViewById(R.id.textView3);
        todo=findViewById(R.id.editText3);
        savemarker=findViewById(R.id.button);
        backimage=findViewById(R.id.imageView2);
        camera=findViewById(R.id.imageView3);

        storageReference= FirebaseStorage.getInstance().getReference();
        //No clue why this line is written

//        Bundle bundle=getIntent().getExtras();


//        if(bundle!=null)
//            latlng=bundle.getString("address")

        firebaseAuth=FirebaseAuth.getInstance();

         if(TodoApi.getInstance()!=null)
         {
             currentusername=TodoApi.getInstance().getUsename();
             currentuserid=TodoApi.getInstance().getUserid();
             address=TodoApi.getInstance().getAddress();
             username.setText(currentusername);


         }

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in=new Intent(Intent.ACTION_GET_CONTENT);
                in.setType("image/*");
                startActivityForResult(in,1);

            }
        });
         savemarker.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 save();
             }
         });

    }

    private void save() {

        title=findViewById(R.id.editText);

        todo=findViewById(R.id.editText3);
        final String T=title.getText().toString().trim();
        final String Todo=todo.getText().toString().trim();

        if(!TextUtils.isEmpty(T) && !TextUtils.isEmpty(Todo)
           && imageuri!=null)
        {
            final StorageReference filepath= storageReference
                    .child("marker images")   // marker images/image.jpeg
                    .child("image "+ Timestamp.now().getSeconds());

            filepath.putFile(imageuri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                       // this is where we save our marker data
                            //Method added for below problem
                          filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                              @Override
                              public void onSuccess(Uri uri) {
                                  String imgurl=uri.toString();
                                  Calendar cal=Calendar.getInstance();

                                  Marker marker=new Marker();
                                  marker.setTitle(T);
                                  marker.setTodoinfo(Todo);
                                  marker.setImageUri(imgurl);
                                  marker.setTimeadded((java.sql.Timestamp) cal.getTime());
                                  //this was from stack overflow
                                  marker.setUserid(currentuserid);
                                  marker.setUsername(currentusername);

                                  collectionReference.add(marker)
                                          .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                              @Override
                                              public void onSuccess(DocumentReference documentReference) {
                                                 startActivity(new Intent(SavingTodo.this,
                                                         MarkerList.class));
                                                 finish();
                                              }
                                          })
                                          .addOnFailureListener(new OnFailureListener() {
                                              @Override
                                              public void onFailure(@NonNull Exception e) {
                                                Log.e("not added marker",e.getMessage());
                                              }
                                          });
                              }
                          });


//                            Marker marker=new Marker();
//                            marker.setTitle(T);
//                            marker.setTodoinfo(Todo);
//                            marker.setImageUri(imageuri);
 //            this line was not working so the


                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
        }else{
            Log.d("empty"," irrelevent");
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data!=null){
            imageuri=data.getData();
            backimage.setImageURI(imageuri);
        }
    }


}
