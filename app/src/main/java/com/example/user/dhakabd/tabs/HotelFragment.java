package com.example.user.dhakabd.tabs;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.user.dhakabd.R;
import com.example.user.dhakabd.model.Content;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class HotelFragment extends Fragment {

    private View mView;
    private RecyclerView mRecyclerView;
    private FloatingActionButton mFabAddContent;
    private AlertDialog mAlertDialogAddContent;

    private final int REQUEST_CODE_FOR_READ_EXTERNAL_STORAGE = 1;
    private static final int REQUEST_CODE_FOR_IMAGE_CHOOSEN = 2;

    private LinearLayout mText_filed_container_linearLayout, mChoose_image_container_linearLayout;
    private ImageView mView_image;
    private Uri mImageUri;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseReference;
    private StorageReference mStorageReference;
    private List<Content> mContents = new ArrayList<>();
    RecyclerViewAdapter mRecyclerViewAdapter;

    public HotelFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_hotel, container, false);

        mRecyclerView = mView.findViewById(R.id.recyclerView);
        mFabAddContent = mView.findViewById(R.id.add_content);

        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Hotels");
        mStorageReference = FirebaseStorage.getInstance().getReference().child("Hotels");

        mAuth = FirebaseAuth.getInstance();



        try{
            if(mAuth.getCurrentUser() != null){
                mFabAddContent.setVisibility(View.VISIBLE);
            }else{
                mFabAddContent.setVisibility(View.GONE);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        mRecyclerViewAdapter = new RecyclerViewAdapter(mContents,getContext(),"Hotels");
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);

        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mRecyclerViewAdapter);

        loadContent();

        //Clicking for adding content
        mFabAddContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View add_content_view = LayoutInflater.from(getContext())
                        .inflate(R.layout.layout_item_add,null);

                mChoose_image_container_linearLayout = add_content_view.findViewById(R.id.choose_image_container_linearLayout);
                ImageButton close_dialog_btn = add_content_view.findViewById(R.id.close_btn);

                mText_filed_container_linearLayout = add_content_view.findViewById(R.id.text_field_container_linearLayout);
                mView_image = add_content_view.findViewById(R.id.show_image);
                final EditText image_title_editText = add_content_view.findViewById(R.id.add_image_title);
                final EditText description_editText = add_content_view.findViewById(R.id.add_image_description);
                final EditText phone_number_editText = add_content_view.findViewById(R.id.add_site_phone_number);
                final EditText website_add_edtiText = add_content_view.findViewById(R.id.add_website);
                final ProgressBar add_content_progressBar = add_content_view.findViewById(R.id.add_content_progressBar);

                Button save_btn = add_content_view.findViewById(R.id.save_btn);

                mAlertDialogAddContent = new AlertDialog.Builder(getContext(), android.R.style.Theme_DeviceDefault_Light_NoActionBar_Fullscreen)
                        .setView(add_content_view)
                        .setCancelable(false)
                        .show();

                //Close dialog
                close_dialog_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mAlertDialogAddContent.dismiss();
                    }
                });

                //Choose Image
                mChoose_image_container_linearLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        chooseImage();
                    }
                });

                //Save to database
                save_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final String title = image_title_editText.getText().toString().trim();
                        final String description = description_editText.getText().toString().trim();
                        final String phone_number_text = phone_number_editText.getText().toString().trim();
                        final String website = website_add_edtiText.getText().toString().trim();

                        if(mImageUri != null && !TextUtils.isEmpty(title) && !TextUtils.isEmpty(description)
                                && !TextUtils.isEmpty(phone_number_text) && !TextUtils.isEmpty(website)){

                            add_content_progressBar.setVisibility(View.VISIBLE);
                            //Storing image
                            String fileName = getFileName(mImageUri);
                            final String key = ""+ System.currentTimeMillis();
                            StorageReference imageStorage = mStorageReference.child(key + fileName);

                            imageStorage.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    //Get the download url from completed task
                                    final String downloadUrl = taskSnapshot.getDownloadUrl().toString();

                                    //Storing data to database
                                    Map<String,Object> data_obj = new HashMap<>();
                                    data_obj.put("image",downloadUrl);
                                    data_obj.put("title",title);
                                    data_obj.put("description",description);
                                    data_obj.put("phone_number",phone_number_text);
                                    data_obj.put("website",website);

                                    mDatabaseReference.child(key).setValue(data_obj)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful()){
                                                        mAlertDialogAddContent.dismiss();
                                                    }else{
                                                        Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                    add_content_progressBar.setVisibility(View.GONE);
                                                }
                                            });
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    add_content_progressBar.setVisibility(View.GONE);
                                    Toast.makeText(getContext(), "Can't upload image", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }else{
                            Toast.makeText(getContext(), "All fields are required.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


            }
        });

        return mView;
    }

    private void loadContent() {

        mContents.clear();
        mDatabaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot.exists()){
                    Content content = dataSnapshot.getValue(Content.class);

                    content.setContent_id(dataSnapshot.getKey());
                    mContents.add(content);
                    mRecyclerViewAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void chooseImage(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);

        startActivityForResult(Intent.createChooser(intent, "Select image"),
                REQUEST_CODE_FOR_IMAGE_CHOOSEN);
    }
    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContext().getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_FOR_READ_EXTERNAL_STORAGE &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            chooseImage();
        } else {
            Snackbar.make(mView, "Please provide permission.", Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_CODE_FOR_IMAGE_CHOOSEN && resultCode == RESULT_OK && data != null){
            mImageUri = data.getData();

            //set visibility to text holder container
            mText_filed_container_linearLayout.setVisibility(View.VISIBLE);

            //load to imageview
            Glide.with(mView).load(mImageUri).into(mView_image);

        }
    }


}
