package com.example.user.dhakabd;


import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.user.dhakabd.tabs.AmusementFragment;
import com.example.user.dhakabd.tabs.HospitalFragment;
import com.example.user.dhakabd.tabs.HotelFragment;
import com.example.user.dhakabd.tabs.InstituteFragment;
import com.example.user.dhakabd.tabs.PoliceStationFragment;
import com.example.user.dhakabd.tabs.RestaurantFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private ViewPagerAdapter mPagerAdapter;
    private Toolbar mToolbar;

    private static int sLogin =0, sLogout = 1;
    private FirebaseAuth mAuth;
    private AlertDialog mAlertDialogLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTabLayout = findViewById(R.id.tap_layout);
        mViewPager = findViewById(R.id.view_pager);
        mToolbar = findViewById(R.id.main_toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Dhaka, Bangladesh");

        mPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        mAuth = FirebaseAuth.getInstance();

        mPagerAdapter.addFragment(new HotelFragment(),"Hotel");
        mPagerAdapter.addFragment(new RestaurantFragment(),"Restaurant");
        mPagerAdapter.addFragment(new AmusementFragment(),"Amusement");
        mPagerAdapter.addFragment(new PoliceStationFragment(),"Police Station");
        mPagerAdapter.addFragment(new HospitalFragment(),"Hospital");
        mPagerAdapter.addFragment(new InstituteFragment(),"Institute");

        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setOffscreenPageLimit(2);

        mTabLayout.setupWithViewPager(mViewPager);

//        mTabLayout.getTabAt(0).setIcon(R.drawable.ic_hotel);
//        mTabLayout.getTabAt(1).setIcon(R.drawable.ic_restaurant);
//        mTabLayout.getTabAt(2).setIcon(R.drawable.ic_amusement);
//        mTabLayout.getTabAt(2).setIcon(R.drawable.ic_security);
//        mTabLayout.getTabAt(2).setIcon(R.drawable.ic_hospital);
//        mTabLayout.getTabAt(2).setIcon(R.drawable.ic_hospital);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        try{
            if(mAuth.getCurrentUser() != null){
                menu.add(Menu.NONE, sLogout, 1, "Logout");

            }else{
                menu.add(Menu.NONE, sLogin, 0, "Login");

            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == sLogin){
            performLogin();
        }else if(id == sLogout){
            mAuth.signOut();
            startActivity(new Intent(MainActivity.this,MainActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }

    private void performLogin() {

        View login_layout = getLayoutInflater().inflate(R.layout.layout_login, null);

        final EditText emailEditText = login_layout.findViewById(R.id.login_email);
        final EditText passwordEditText = login_layout.findViewById(R.id.login_password);
        final ProgressBar loginProgressBar = login_layout.findViewById(R.id.login_progressbar);
        Button login_btn = login_layout.findViewById(R.id.login_btn);
        Button login_cancel = login_layout.findViewById(R.id.login_cancel);



        mAlertDialogLogin = new AlertDialog.Builder(MainActivity.this)
                .setTitle("Log In")
                .setView(login_layout)
                .setCancelable(false)
                .show();

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();

                if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)){
                    loginProgressBar.setVisibility(View.VISIBLE);
                    mAuth.signInWithEmailAndPassword(email,password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()){
                                        startActivity(new Intent(MainActivity.this,MainActivity.class));

                                    }else{
                                        Toast.makeText(MainActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                    loginProgressBar.setVisibility(View.GONE);
                                }
                            });
                }else{
                    Toast.makeText(MainActivity.this, "All fields are required.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        login_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAlertDialogLogin.dismiss();
            }
        });
    }
}
