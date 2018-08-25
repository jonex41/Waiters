package com.wisdomgabriel.www.waiters;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import id.zelory.compressor.Compressor;

/**
 * Created by MR AGUDA on 15-Nov-17.
 */

public class PostActivity extends AppCompatActivity implements  View.OnClickListener{

    private static final int REQUEST_CAMERA = 45;


    private Button changeimage;

    private static int  GALLERYPICK =1;
     private String image;



    private StorageReference storageReferenceimage;
    private StorageReference storageReference_cut;
    private ProgressDialog LoadingProgressBar;


    private Bitmap imageBitmap = null;
    private FirebaseFirestore firestore;
    private CollectionReference collectionReference;
    private String userChoosenTask;
    private Bitmap compressedImageFile;
    private Uri resultUrl = null;


    private String downloadUrlReal = " ";
    private String downloadUrlthumb=" ";



    private DocumentReference docref;
    private ImageView profileimage;
    private String strings;
    private String namestring;
    private String pricestring;
    private String decpstring;
    private EditText food_nameedt;
    private EditText food_price;
    private EditText food_descp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.post_activity);
        Utility.requestPermission(PostActivity.this,  Manifest.permission.READ_EXTERNAL_STORAGE);

        strings = getIntent().getStringExtra("string");

         food_nameedt = (EditText)findViewById(R.id.foodname);
         food_price = (EditText)findViewById(R.id.foodprice);
         food_descp = (EditText)findViewById(R.id.fooddesc);

        Button button = (Button) findViewById(R.id.postfoodbtn);
        button.setOnClickListener(this);

        Toolbar mToobar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToobar);

        getSupportActionBar().setTitle(strings.toUpperCase());


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        firestore = FirebaseFirestore.getInstance();







        storageReferenceimage = FirebaseStorage.getInstance().getReference().child(strings);
        profileimage = (ImageView) findViewById(R.id.imageview_profile11);


        LoadingProgressBar = new ProgressDialog(this);


        profileimage.setOnClickListener(this);




    }





    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                resultUrl = result.getUri();
                profileimage.setImageURI(resultUrl);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(PostActivity.this, "Error : " + error, Toast.LENGTH_LONG).show();
                LoadingProgressBar.dismiss();
            }

        }
    }


    @Override
    public void onClick(View v) {
        int t = v.getId();
        switch (t){

            case R.id.imageview_profile11:
           dialogStuffs();
           break;

            case R.id.postfoodbtn:
                postfood();
                break;
        }

    }
public void postfood(){
    LoadingProgressBar.setTitle("Uploading");
    LoadingProgressBar.setMessage("Please wait while we upload your food");
    LoadingProgressBar.setIndeterminate(false);
    LoadingProgressBar.show();
    namestring = food_nameedt.getText().toString();
    pricestring = food_price.getText().toString();
    decpstring = food_descp.getText().toString();
    if(resultUrl != null && namestring != null&& pricestring != null&& decpstring !=null) {

        File image_filepath = new File(resultUrl.getPath());
        long length = image_filepath.length() / 1024 *100;

        Toast.makeText(this, "size"+length, Toast.LENGTH_LONG).show();




        try {
            imageBitmap = new Compressor(PostActivity.this).setMaxWidth(200).setMaxHeight(200).setQuality(100).
                    compressToBitmap(image_filepath);

            //  ImageCompression imageCompression = new ImageCompression(getApplicationContext());
            //   imageCompression.compressImage(String.valueOf(resultUrl));


        } catch (IOException e) {
            e.printStackTrace();
        }


        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        final byte[] byteArray = baos.toByteArray();


        final StorageReference filepath_cut = storageReferenceimage.child("foodrealimage"+System.currentTimeMillis() + ".jpg");


        final UploadTask uploadTask = filepath_cut.putBytes(byteArray);


        uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task_cut) {

                if (task_cut.isSuccessful()) {


                    uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {

                                throw task.getException();
                            }

                            // Continue with the task to get the download URL
                            return filepath_cut.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                Uri downloadUri = task.getResult();

                                downloadUrlReal = String.valueOf(downloadUri);

                                File newThumbFile = new File(resultUrl.getPath());
                                try {

                                    compressedImageFile = new Compressor(PostActivity.this)
                                            .setMaxHeight(50)
                                            .setMaxWidth(50)
                                            .setQuality(1)
                                            .compressToBitmap(newThumbFile);

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                compressedImageFile.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                                byte[] thumbData = baos.toByteArray();


                                final StorageReference reference1 = storageReferenceimage.child("foodThumbimage"+System.currentTimeMillis() + ".jpg");;
                                final UploadTask uploadTask = reference1.putBytes(thumbData);


                                uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                        if (task.isSuccessful()) {

                                            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                                                @Override
                                                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                                    if (!task.isSuccessful()) {

                                                        throw task.getException();
                                                    }

                                                    // Continue with the task to get the download URL
                                                    return reference1.getDownloadUrl();
                                                }
                                            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Uri> task) {
                                                    if (task.isSuccessful()) {
                                                        Uri downloadUri = task.getResult();

                                                        downloadUrlthumb  = String.valueOf(downloadUri);
                                                        // userDatabase.setThumbIamgeUri(String.valueOf(downloadUri));



                                                        Map<String, Object> map = new HashMap<>();
                                                        map.put("imageUrl", downloadUrlthumb);
                                                        map.put( Constants.PROFILEIMAGE, downloadUrlReal);
                                                        map.put("foodName", namestring);
                                                        map.put("foodPrice",pricestring );
                                                        map.put("foodDescription",decpstring);

                                                        FirebaseFirestore.getInstance().collection(strings).add(map).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                                                Toast.makeText(PostActivity.this, "food image uploaded successfully", Toast.LENGTH_LONG).show();
                                                                LoadingProgressBar.dismiss();
                                                            }
                                                        });



                                                    } else {
                                                        // Handle failures
                                                        //  mProgressDialog.dismiss();
                                                        LoadingProgressBar.dismiss();
                                                        Toast.makeText(getApplicationContext(), "unable to get Url", Toast.LENGTH_LONG).show();

                                                        // ...
                                                    }
                                                }
                                            });


                                        } else {
                                            // Handle failures
                                            //    mProgressDialog.dismiss();
                                            LoadingProgressBar.dismiss();
                                            Toast.makeText(getApplicationContext(), "unable to get Url", Toast.LENGTH_LONG).show();

                                            // ...
                                        }
                                    }
                                });


                            } else {
                                // Handle failures
                                //  mProgressDialog.dismiss();
                                LoadingProgressBar.dismiss();
                                Toast.makeText(getApplicationContext(), "unable to get Url", Toast.LENGTH_LONG).show();

                                // ...
                            }
                        }
                    });



                }

            }
        });
    }else {
        LoadingProgressBar.dismiss();
        Toast.makeText(getApplicationContext(), "no image selected", Toast.LENGTH_LONG).show();
    }

}

    private void dialogStuffs(){
        final CharSequence[] items = { "Post food image", "Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(PostActivity.this);
        builder.setTitle("Food image");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {


                if (items[item].equals("Post food image")) {

                        CropImage.activity()
                                .setGuidelines(CropImageView.Guidelines.ON)
                                .setMinCropResultSize(512, 512)
                                .start(PostActivity.this);



                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();


    }

}
