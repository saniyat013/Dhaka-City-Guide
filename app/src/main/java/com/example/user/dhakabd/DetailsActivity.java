package com.example.user.dhakabd;


import android.Manifest;
import android.app.Fragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.user.dhakabd.model.Content;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DetailsActivity extends AppCompatActivity  {
    private static final int REQUEST_CODE_FOR_PHONE_CALL = 1;
    private ImageView mImageView,goToMap;
    private TextView mDescription;
    private FloatingActionButton mFabPhoneCall, mFabWebsiteVisit,details_location;
    private Toolbar mToolbar;
    private DatabaseReference mDatabaseReference;
    private Content mContent;

    private WebView mWebView;
    private GoogleMap googleMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        final String content_id = getIntent().getStringExtra("id");
        String fragment_name = getIntent().getStringExtra("fragment_name");

        mImageView = findViewById(R.id.details_image);
        mDescription = findViewById(R.id.details_description);
        mFabPhoneCall = findViewById(R.id.details_fab_phone_call);
        mFabWebsiteVisit = findViewById(R.id.details_fab_website);
        mToolbar = findViewById(R.id.details_toolbar);
     //   mWebView = (WebView) findViewById(R.id.webview);
        details_location=findViewById(R.id.details_location);
       // goToMap=findViewById(R.id.GoToMap);

      /*  SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);*/

        if(fragment_name.equals("Hotels")) {
            mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Hotels");
        }else if(fragment_name.equals("Amusement")){
            mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Amusement");

        }else if(fragment_name.equals("Hospital")){
            mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Hospital");

        }else if(fragment_name.equals("PoliceStation")){
            mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("PoliceStation");

        }else if(fragment_name.equals("Restaurant")){
            mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Restaurant");

        }
        else if(fragment_name.equals("Institutes")){
            mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Institutes");

        }


        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(content_id != null){
            mDatabaseReference.child(content_id)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()){
                                final Content content = dataSnapshot.getValue(Content.class);
                                mContent = content;
                                getSupportActionBar().setTitle(content.getTitle());

                                RequestOptions requestOptions = new RequestOptions();
                                requestOptions.placeholder(R.drawable.rsz_no_image_icon);
                                Glide.with(getApplicationContext()).setDefaultRequestOptions(requestOptions).load(content.getImage())
                                        .into(mImageView);

                                mDescription.setText(content.getDescription());
                                details_location.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                                        browserIntent.setData(Uri.parse(content.getMap()));
                                        startActivity(browserIntent);
                                    }
                                });
//                                mWebView.loadUrl("https://www.facebook.com");

                              //  mWebView.loadUrl(content.getWebsite());
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

            mFabPhoneCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Build.VERSION.SDK_INT >= 23) {
                        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                            makePhoneCall();
                        } else {
                            ActivityCompat.requestPermissions(DetailsActivity.this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CODE_FOR_PHONE_CALL);
                        }
                    } else {
                        //No need any permission
                        makePhoneCall();
                    }
                }
            });

            mFabWebsiteVisit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent viewIntent =
                            new Intent("android.intent.action.VIEW",
                                    Uri.parse(mContent.getWebsite()));
                    startActivity(viewIntent);
                }
            });
        }


    }



/*    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        LatLng latLng = new LatLng(lat, lng);
        this.googleMap.addMarker(new MarkerOptions().position(latLng));
        this.googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        this.googleMap.moveCamera(CameraUpdateFactory.zoomTo(15));
    }*/

    private void makePhoneCall() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        try {
//            Intent phoneCallIntent = new Intent(Intent.ACTION_CALL);
//            phoneCallIntent.setData(Uri.parse("tel:" + sPhoneNumber));
//            startActivity(phoneCallIntent);

            String dail = "tel:" + mContent.getPhone_number();
            startActivity(new Intent(Intent.ACTION_CALL,Uri.parse(dail)));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
//    private void showDirections() {
//        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
//                Uri.parse("http://maps.google.com/maps?daddr=" +
//                        lat + "," + lat));
//
//        if (intent.resolveActivity(getPackageManager()) != null) {
//            startActivity(intent);
//        }
//    }
}
