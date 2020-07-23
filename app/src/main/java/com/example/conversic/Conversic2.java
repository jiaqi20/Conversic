package com.example.conversic;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

public class Conversic2 extends AppCompatActivity {

    private static final int PERMISSION = 9;
    private static final int ACTIVITY = 86;

    private Button  btnBrowse, btnConvert, btnCLib;
    private TextView txtViewDisplay;
    private EditText fileDescription;

    private Uri uri;

    private FirebaseUser user;
    private StorageReference storageRef;
    private DatabaseReference databaseRef;

    private StorageTask uploadTask;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversic2);

        user = FirebaseAuth.getInstance().getCurrentUser();
        storageRef = FirebaseStorage.getInstance().getReference("uploads");
        databaseRef = FirebaseDatabase.getInstance().getReference("uploads");

        txtViewDisplay = findViewById(R.id.textViewDisplay);
        fileDescription = findViewById(R.id.editTextDescription);

        btnBrowse = findViewById(R.id.buttonBrowse);
        btnConvert = findViewById(R.id.buttonConvert);
        btnCLib = findViewById(R.id.buttonCLib);

        btnBrowse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                browseFile();
            }
        });

        btnConvert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uriOutput = convertFile();
                uploadFile(uriOutput);
            }
        });

        btnCLib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToLibActivity();
            }
        });
    }

    private void moveToLibActivity() {
        Intent intent = new Intent(Conversic2.this, LibraryActivity.class);
        startActivity(intent);
    }

    private void fileManager() {
        Intent intent = new Intent();
        //pdf setType is "application/pdf"
        //xml setType is "text/xml"
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, ACTIVITY);
    }

    private void browseFile() {
        //check permission
        if (ContextCompat.checkSelfPermission(Conversic2.this,
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            fileManager();
        } else {
            ActivityCompat.requestPermissions(Conversic2.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ACTIVITY && resultCode == RESULT_OK && data != null) {
            uri = data.getData();

            txtViewDisplay.setText("File chosen!");
        } else {
            Toast.makeText(Conversic2.this, "Please select file", Toast.LENGTH_SHORT).show();
        }
    }

    private Uri convertFile() {
        return null;
    }

    private void uploadFile(Uri uri) {
        if(uploadTask != null && uploadTask.isInProgress()) {
            Toast.makeText(Conversic2.this, "Upload in progress", Toast.LENGTH_LONG).show();
        } else {
            if(uri != null) {
                final StorageReference fileReference = storageRef.child(System.currentTimeMillis()
                        + "." + getFileExtension(uri));

                uploadTask = fileReference.putFile(uri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressDialog = new ProgressDialog(Conversic2.this);
                                        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                                        progressDialog.setProgress(0);
                                        progressDialog.setTitle("Uploading file...");
                                        progressDialog.show();
                                    }
                                }, 500);
                                fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        Upload upload = new Upload(fileDescription.getText().toString().trim(),
                                                uri.toString()); // toString or getPath?
                                        String uploadID = databaseRef.push().getKey();
                                        databaseRef.child(user.getUid()).child(uploadID).setValue(upload);

                                        Toast.makeText(Conversic2.this, "Upload successfully",
                                                Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(Conversic2.this, e.getMessage(),
                                        Toast.LENGTH_LONG).show();
                            }
                        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                int progress = (int) (100.0 * taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                                progressDialog.setProgress(progress);
                            }
                        });
            } else {
                Toast.makeText(Conversic2.this, "No file selected", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }
}
