package com.example.bdafahim.easyrent;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Post_Add extends AppCompatActivity  {
    //variables of used button and edittext
    private Button post;
    private EditText housetxt, roadtxt, addresstxt, phonetxt, emailtxt,rentxt,nametxt;

    private double lati, longi;
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef;
    private  String userID;
    private Rent_Add rent_add;
    private ProgressDialog progressDialog;
    private RadioGroup radioGroup;
    private GpsTracker gpsTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post__add);

      //  INTT();
        post = findViewById(R.id.submit);
        housetxt = findViewById(R.id.house_no);
        roadtxt = findViewById(R.id.road_no);
        addresstxt = findViewById(R.id.area);
        phonetxt = findViewById(R.id.phone_no);
        emailtxt = findViewById(R.id.email);
        nametxt = findViewById(R.id.owner_name);
        rentxt = findViewById(R.id.rentfee);
        progressDialog = new ProgressDialog(this);
        radioGroup = findViewById(R.id.radio_group);
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();


        try {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 101);
            }
        } catch (Exception e){
            e.printStackTrace();
        }


       post.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               toastMessage("ButtonClicked");
               getLocation(view);
               POST_PROCESS();
               toastMessage("Post process Complete");
           }
       });


    }

    void POST_PROCESS(){

            String house_no, road_no, address, phone_no, email_no,ownername;
            int rentfee;
            progressDialog.setMessage("Posting in process...");
            progressDialog.show();
            address = addresstxt.getText().toString();
            phone_no = phonetxt.getText().toString();
            email_no = emailtxt.getText().toString();
            house_no = housetxt.getText().toString();
            road_no = roadtxt.getText().toString();
            ownername = nametxt.getText().toString();
            rentfee = Integer.parseInt(rentxt.getText().toString());

            int selected = radioGroup.getCheckedRadioButtonId();
            RadioButton rb =(RadioButton) findViewById(selected);

            String type = rb.getText().toString();

            rent_add = new Rent_Add(house_no,ownername,road_no,address,phone_no,email_no,type,rentfee,lati,longi);

            mFirebaseDatabase.getReference("Users")
                    .child(mAuth.getCurrentUser().getUid())
                    .child("Posts").push()
                    .setValue(rent_add).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        progressDialog.dismiss();
                        Toast.makeText(Post_Add.this, "Posting Successful", Toast.LENGTH_LONG).show();
                        finish();
                    } else {
                        progressDialog.dismiss();
                        //display a failure message
                        Toast.makeText(Post_Add.this,task.getException().getMessage(),Toast.LENGTH_LONG).show();
                    }
                }
            });


    }
    public void getLocation(View view){
        gpsTracker = new GpsTracker(Post_Add.this);
        if(gpsTracker.canGetLocation()){
          lati = gpsTracker.getLatitude();
            longi= gpsTracker.getLongitude();

        }else{
            gpsTracker.showSettingsAlert();
        }
    }
    private void toastMessage(String message){
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }
}
