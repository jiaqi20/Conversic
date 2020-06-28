package com.example.conversic;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
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

import org.jfugue.integration.MusicXmlParser;
import org.jfugue.pattern.Pattern;
import org.jfugue.theory.Note;
import org.staccato.SignatureSubparser;
import org.staccato.StaccatoParserListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import nu.xom.ParsingException;

public class Conversic1 extends AppCompatActivity {

    private static final int PERMISSION = 9;
    private static final int ACTIVITY = 86;

    private Button btnBack, btnBrowse, btnConvert, btnUpload, btnCLib;
    private TextView txtViewDisplay, txtViewMusicString;
    private EditText fileDescription;

    private Uri uri;
    private Uri contentUri;

    private static final double semiquaver = 0.9375;
    private static final double quaver = semiquaver * 2;
    private static final double crotchet = quaver * 2;
    private static final double minim = crotchet * 2;

    private static final int rest = 0;
    private static final String barLine = "b";

    private int C;
    private int D;
    private int E;
    private int F;
    private int G;
    private int A;
    private int B;

    private FirebaseUser user;
    private StorageReference storageRef;
    private DatabaseReference databaseRef;

    private StorageTask uploadTask;

    private ProgressBar progressUpload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversic1);

        user = FirebaseAuth.getInstance().getCurrentUser();
        storageRef = FirebaseStorage.getInstance().getReference("uploads");
        databaseRef = FirebaseDatabase.getInstance().getReference("uploads");

        txtViewDisplay = findViewById(R.id.textViewDisplay);
        txtViewMusicString = findViewById(R.id.textViewMusicString);
        fileDescription = findViewById(R.id.editTextDescription);

        btnBack = findViewById(R.id.buttonBack);
        btnBrowse = findViewById(R.id.buttonBrowse);
        btnConvert = findViewById(R.id.buttonConvert);
        btnUpload = findViewById(R.id.buttonUpload);
        btnCLib = findViewById(R.id.buttonCLib);

        progressUpload = findViewById(R.id.progressUpload);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveBack();
            }
        });

        btnBrowse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                browseFile();
            }
        });

        btnConvert.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                Uri uriOutput = null;
                try {
                    //uriOutput = convertFile();
                    convertFile();
                } catch (ParserConfigurationException | IOException | ParsingException e) {
                    Toast.makeText(Conversic1.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadFile();
            }
        });

        btnCLib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToLibActivity();
            }
        });
    }

    private void moveBack(){
        Intent intent = new Intent(Conversic1.this, ConversicActivity.class);
        startActivity(intent);
    }

    private void moveToLibActivity() {
        Intent intent = new Intent(Conversic1.this, LibraryActivity.class);
        startActivity(intent);
    }

    private void fileManager() {
        Intent intent = new Intent();
        //pdf setType is "application/pdf"
        //image setType is "image/*"
        //xml setType is "text/xml"
        intent.setType("text/xml");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, ACTIVITY);
    }

    private void browseFile() {
        //check permission
        if (ContextCompat.checkSelfPermission(Conversic1.this,
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            fileManager();
        } else {
            ActivityCompat.requestPermissions(Conversic1.this,
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
            Toast.makeText(Conversic1.this, "Please select file", Toast.LENGTH_SHORT).show();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void convertFile() throws ParserConfigurationException, IOException, ParsingException {
        StaccatoParserListener listener = new StaccatoParserListener();
        MusicXmlParser parser = new MusicXmlParser();

        parser.addParserListener(listener);

        File file = FileUtil.from(Conversic1.this, uri);

        parser.parse(file);

        Pattern pattern = listener.getPattern();

        SignatureSubparser signatureSubparser = new SignatureSubparser();

        GetElement notes = new GetElement();

        List<NoteAndBarLine> list = notes.getNotesUsed(pattern);

        byte rootPosition = signatureSubparser.
                convertAccidentalCountToKeyRootPositionInOctave(-notes.getKey(), notes.getScale());
        String keySignature = signatureSubparser.createKeyString(rootPosition, notes.getScale());

        updateValue(keySignature.charAt(0));

        List<String> converted = new ArrayList<>();

        for(NoteAndBarLine element : list) {
            if(NoteAndBarLine.isNote(element)) {
                if(element.getNote().getOriginalString().charAt(0) == 'C') {
                    converted.add(C + getRhythm(element.getNote()));
                } else if(element.getNote().getOriginalString().charAt(0) == 'D') {
                    converted.add(D + getRhythm(element.getNote()));
                } else if(element.getNote().getOriginalString().charAt(0) == 'E') {
                    converted.add(E + getRhythm(element.getNote()));
                } else if(element.getNote().getOriginalString().charAt(0) == 'F') {
                    converted.add(F + getRhythm(element.getNote()));
                } else if(element.getNote().getOriginalString().charAt(0) == 'G') {
                    converted.add(G + getRhythm(element.getNote()));
                } else if(element.getNote().getOriginalString().charAt(0) == 'A') {
                    converted.add(A + getRhythm(element.getNote()));
                } else if(element.getNote().getOriginalString().charAt(0) == 'B') {
                    converted.add(B + getRhythm(element.getNote()));
                } else if(element.getNote().getOriginalString().charAt(0) == 'R') {
                    converted.add(rest + getRhythm(element.getNote()));
                }
            } else {
                converted.add(element.toString() + barLine);
            }
        }

        txtViewMusicString.setText(uri.toString());

        createPdf(converted);
        //return uri of converted sheet so that it can be uploaded to library
        //return createPdf(converted);
    }

    //hardcode
    private void updateValue(char c) {
        if(c == 'C') {
            C = 1;
            D = C + 1;
            E = D + 1;
            F = E + 1;
            G = F + 1;
            A = G + 1;
            B = A + 1;
        } else if(c == 'D') {
            D = 1;
            E = D + 1;
            F = E + 1;
            G = F + 1;
            A = G + 1;
            B = A + 1;
            C = B + 1;
        } else if(c == 'E') {
            E = 1;
            F = E + 1;
            G = F + 1;
            A = G + 1;
            B = A + 1;
            C = B + 1;
            D = C + 1;
        } else if(c == 'F') {
            F = 1;
            G = F + 1;
            A = G + 1;
            B = A + 1;
            C = B + 1;
            D = C + 1;
            E = D + 1;
        } else if(c == 'G') {
            G = 1;
            A = G + 1;
            B = A + 1;
            C = B + 1;
            D = C + 1;
            E = D + 1;
            F = E + 1;
        } else if(c == 'A') {
            A = 1;
            B = A + 1;
            C = B + 1;
            D = C + 1;
            E = D + 1;
            F = E + 1;
            G = F + 1;
        } else if(c == 'B') {
            B = 1;
            C = B + 1;
            D = C + 1;
            E = D + 1;
            F = E + 1;
            G = F + 1;
            A = G + 1;
        }
    }

    private String getRhythm(Note note) {
        if(note.getDuration() == semiquaver) {
            return "s";
        } else if(note.getDuration() == quaver) {
            return "q";
        } else if(note.getDuration() == crotchet) {
            return "c";
        } else if(note.getDuration() == minim) {
            return "m";
        }
        return "";
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void createPdf(List<String> items) {
        PdfDocument pdf = new PdfDocument();
        Paint title = new Paint();
        Paint paint = new Paint();
        Paint paintLine = new Paint();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(200,
                400, 1).create();
        PdfDocument.Page page = pdf.startPage(pageInfo);
        Canvas canvas = page.getCanvas();

        title.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("" + fileDescription.getText(), 100, 50, title);

        int x = 20;
        int y = 100;
        for(String item : items) {
            canvas.drawText(String.valueOf(item.charAt(0)), x, y, paint);
            if(String.valueOf(item.charAt(1)).equals("q")) {
                canvas.drawLine(x - 3, y + 10,x + 10, y + 10, paintLine);
            } else if(String.valueOf(item.charAt(1)).equals("m")) {
                canvas.drawLine(x + 10, y - 5, x + 15, y - 5, paintLine);
            } else if(String.valueOf(item.charAt(1)).equals("s")) {
                canvas.drawLine(x - 5, y + 15,x + 10, y + 15, paintLine);
            }
            x = x + 20;
            if(x > 180) {
                x = 20;
                y = y + 40;
            }
        }
        pdf.finishPage(page);
        //getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS),"test.pdf"
        String path = "/sdcard/Documents/test2.pdf";
        File file = new File(path);
        try {
            pdf.writeTo(new FileOutputStream(file));
        } catch (IOException e) {
            e.printStackTrace();
        }
        pdf.close();
        //uploadFile(Uri.parse("file://" + path));
        contentUri = FileProvider.getUriForFile(this, "com.example.conversic", file);
    }



    private void uploadFile() {
        if(uploadTask != null && uploadTask.isInProgress()) {
            Toast.makeText(Conversic1.this, "Upload in progress", Toast.LENGTH_LONG).show();
        } else {
            if(contentUri != null) {
                final StorageReference fileReference = storageRef.child(System.currentTimeMillis()
                        + "." + getFileExtension(contentUri));

                uploadTask = fileReference.putFile(contentUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressUpload.setProgress(0);
                                    }
                                }, 500);
                                fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        Upload upload = new Upload(fileDescription.getText().toString().trim() +
                                                "." + getFileExtension(contentUri),
                                                uri.toString()); // toString or getPath?
                                        String uploadID = databaseRef.push().getKey();
                                        databaseRef.child(user.getUid()).child(uploadID).setValue(upload);

                                        Toast.makeText(Conversic1.this, "Upload successfully",
                                                Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(Conversic1.this, e.getMessage(),
                                        Toast.LENGTH_LONG).show();
                            }
                        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                int progress = (int) (100.0 * taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                                progressUpload.setProgress(progress);
                            }
                        });
            } else {
                Toast.makeText(Conversic1.this, "No file selected", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }
}
