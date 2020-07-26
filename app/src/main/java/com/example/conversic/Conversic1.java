package com.example.conversic;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
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

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
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

    private Button btnBrowse, btnConvert;
    private TextView txtViewDisplay, txtViewMusicString;
    private EditText fileDescription;

    private String xmlPath; //filepath of musicxml file generated

    private Uri uri; //uri of browsed file
    private Uri contentUri; //content uri of converted pdf that is used for uploading to firebase

    //value for respective rhythms
    private static final double whole = 1;
    private static final double minim = whole/2;
    private static final double crotchet = minim/2;
    private static final double quaver = crotchet/2;
    private static final double semiquaver = quaver/2;

    private static final String barLine = "b";

    //dots
    private static final String up = "u";
    private static final String down = "d";
    private static final String none = "n";

    //value of notes including rest
    private static final int rest = 0;
    private int C;
    private int D;
    private int E;
    private int F;
    private int G;
    private int A;
    private int B;

    private char keySignature;

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

        btnBrowse = findViewById(R.id.buttonBrowse);
        btnConvert = findViewById(R.id.buttonConvert);

        progressUpload = findViewById(R.id.progressUpload);

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
                try {
                    convert();
                } catch (ParserConfigurationException | IOException | ParsingException e) {
                    Toast.makeText(Conversic1.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    /**
     * Directs to library activity.
     */
    private void moveToLibActivity() {
        Intent intent = new Intent(Conversic1.this, LibraryActivity.class);
        startActivity(intent);
    }

    private void fileManager() {
        Intent intent = new Intent();
        //pdf setType is "application/pdf"
        //image setType is "image/*"
        //xml setType is "text/xml"
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, ACTIVITY);
    }

    /**
     * Browse music staff img (jpg).
     */
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

    /**
     * Convert list of notes and bar lines into list of numbers and bar lines (with some details).
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void convertFile() throws ParserConfigurationException, IOException, ParsingException {
        StaccatoParserListener listener = new StaccatoParserListener();
        MusicXmlParser parser = new MusicXmlParser();

        parser.addParserListener(listener);

        File file = new File(xmlPath);
        //File file = FileUtil.from(Conversic1.this, uri);

        parser.parse(file); //parse musicxml file

        Pattern pattern = listener.getPattern();

        GetElement notes = new GetElement();

        List<NoteAndBarLine> list = notes.getNotesUsed(pattern); //get notes and barlines from pattern object

        //update number of notes according to the key signature
        this.keySignature = file.getName().charAt(0);
        updateValue(this.keySignature);

        List<String> converted = new ArrayList<>(); //initialise list of converted strings

        boolean isTrue = true; //checking first bar for the beat count only
        double beatCount = 0; //used to determine time signature

        for(NoteAndBarLine element : list) {
            //for Note object
            if(NoteAndBarLine.isNote(element)) {
                if (element.getNote().isRest()) { //Rest
                    converted.add(rest + getRhythm(element.getNote()) + none);
                    if (isTrue) {
                        beatCount = beatCount + element.getNote().getDuration();
                    }
                } else if (element.getNote().getOriginalString().charAt(0) == 'C') { //C
                    if (element.getNote().getOriginalString().charAt(1) == '3') {
                        converted.add(C + getRhythm(element.getNote()) + down);
                    } else if (element.getNote().getOriginalString().charAt(1) == '4') {
                        converted.add(C + getRhythm(element.getNote()) + none);
                    } else if (element.getNote().getOriginalString().charAt(1) == '5') {
                        converted.add(C + getRhythm(element.getNote()) + up);
                    }
                    if (isTrue) {
                        beatCount = beatCount + element.getNote().getDuration();
                    }
                } else if (element.getNote().getOriginalString().charAt(0) == 'D') { //D
                    if (element.getNote().getOriginalString().charAt(1) == '3') {
                        converted.add(D + getRhythm(element.getNote()) + down);
                    } else if (element.getNote().getOriginalString().charAt(1) == '4') {
                        converted.add(D + getRhythm(element.getNote()) + none);
                    } else if (element.getNote().getOriginalString().charAt(1) == '5') {
                        converted.add(D + getRhythm(element.getNote()) + up);
                    }
                    if (isTrue) {
                        beatCount = beatCount + element.getNote().getDuration();
                    }
                } else if (element.getNote().getOriginalString().charAt(0) == 'E') { //E
                    if (element.getNote().getOriginalString().charAt(1) == '3') {
                        converted.add(E + getRhythm(element.getNote()) + down);
                    } else if (element.getNote().getOriginalString().charAt(1) == '4') {
                        converted.add(E + getRhythm(element.getNote()) + none);
                    } else if (element.getNote().getOriginalString().charAt(1) == '5') {
                        converted.add(E + getRhythm(element.getNote()) + up);
                    }
                    if (isTrue) {
                        beatCount = beatCount + element.getNote().getDuration();
                    }
                } else if (element.getNote().getOriginalString().charAt(0) == 'F') { //F
                    if (element.getNote().getOriginalString().charAt(1) == '3') {
                        converted.add(F + getRhythm(element.getNote()) + down);
                    } else if (element.getNote().getOriginalString().charAt(1) == '4') {
                        converted.add(F + getRhythm(element.getNote()) + none);
                    } else if (element.getNote().getOriginalString().charAt(1) == '5') {
                        converted.add(F + getRhythm(element.getNote()) + up);
                    }
                    if (isTrue) {
                        beatCount = beatCount + element.getNote().getDuration();
                    }
                } else if (element.getNote().getOriginalString().charAt(0) == 'G') { //G
                    if (element.getNote().getOriginalString().charAt(1) == '3') {
                        converted.add(G + getRhythm(element.getNote()) + down);
                    } else if (element.getNote().getOriginalString().charAt(1) == '4') {
                        converted.add(G + getRhythm(element.getNote()) + none);
                    } else if (element.getNote().getOriginalString().charAt(1) == '5') {
                        converted.add(G + getRhythm(element.getNote()) + up);
                    }
                    if (isTrue) {
                        beatCount = beatCount + element.getNote().getDuration();
                    }
                } else if (element.getNote().getOriginalString().charAt(0) == 'A') { //A
                    if (element.getNote().getOriginalString().charAt(1) == '3') {
                        converted.add(A + getRhythm(element.getNote()) + down);
                    } else if (element.getNote().getOriginalString().charAt(1) == '4') {
                        converted.add(A + getRhythm(element.getNote()) + none);
                    } else if (element.getNote().getOriginalString().charAt(1) == '5') {
                        converted.add(A + getRhythm(element.getNote()) + up);
                    }
                    if (isTrue) {
                        beatCount = beatCount + element.getNote().getDuration();
                    }
                } else if (element.getNote().getOriginalString().charAt(0) == 'B') { //B
                    if (element.getNote().getOriginalString().charAt(1) == '3') {
                        converted.add(B + getRhythm(element.getNote()) + down);
                    } else if (element.getNote().getOriginalString().charAt(1) == '4') {
                        converted.add(B + getRhythm(element.getNote()) + none);
                    } else if (element.getNote().getOriginalString().charAt(1) == '5') {
                        converted.add(B + getRhythm(element.getNote()) + up);
                    }
                    if (isTrue) {
                        beatCount = beatCount + element.getNote().getDuration();
                    }
                }
            } else { // for Bar line object
                converted.add(element.toString() + barLine + none);
                isTrue = false;
            }
        }

        String timeSignature = "";

        if(beatCount == minim) {
            timeSignature = "2/4";
        } else if(beatCount == minim + crotchet){
            timeSignature = "3/4";
        } else if(beatCount == whole) {
            timeSignature = "4/4";
        }

        //create pdf using converted list
        createPdf(converted, this.keySignature, timeSignature);

        //upload pdf file to google firebase and library
        uploadFile();

        //done
        txtViewMusicString.setText("Conversic!");
        Toast.makeText(Conversic1.this, "Convert and upload successfully!",
                Toast.LENGTH_LONG).show();
    }

    //hardcode
    /**
     * Update the value of conversion for respective key signatures.
     * @param c First letter name of key signature.
     */
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

    /**
     * Check the duration of note.
     * @param note Note.
     * @return Specific letter name for each rhythm.
     */
    private String getRhythm(Note note) {
        if(note.getDuration() == semiquaver) {
            return "s";
        } else if(note.getDuration() == quaver) {
            return "q";
        } else if(note.getDuration() == crotchet) {
            return "c";
        } else if(note.getDuration() == minim) {
            return "m";
        } else if(note.getDuration() == whole) {
            return "w";
        }
        return "";
    }

    /**
     * Create pdf file of converted sheet (numbered notation music sheet).
     * @param items List of notes and bar lines.
     * @param key Key signature.
     * @param time Time signature.
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void createPdf(List<String> items, char key, String time) {
        PdfDocument pdf = new PdfDocument();
        Paint title = new Paint();
        Paint paint = new Paint();
        Paint paintLine = new Paint();
        Paint paintDot = new Paint();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(2480,
                3508, 1).create();
        PdfDocument.Page page = pdf.startPage(pageInfo);
        Canvas canvas = page.getCanvas();

        paint.setTextSize(50);

        title.setTextSize(100);

        title.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("" + fileDescription.getText(), 1240, 312, title);

        title.setTextSize(50);
        title.setTextAlign(Paint.Align.LEFT);
        canvas.drawText("1 = " + key + "  " + time, 118, 687, title);

        int x = 118;
        int y = 937;
        for(int i = 0; i < items.size() ; i++) {
            String item = items.get(i);
            String next = "";
            if(i<items.size()-1) {
                next = items.get(i + 1);
            }
            if(i == 0) {
                x = x + 29;
            }
            if(String.valueOf(item.charAt(1)).equals(barLine)) {
                canvas.drawText(String.valueOf(item.charAt(0)), x, y, paint);
                x = x + 29*3;
                if((2480 - x) < 725) {
                    x = 118;
                    y = y + 250;
                }
            } else {
                canvas.drawText(String.valueOf(item.charAt(0)), x, y, paint);
            }
            if(String.valueOf(item.charAt(2)).equals(up)) {
                canvas.drawCircle(x + 15, y - 50, 3, paintDot);
            } else if(String.valueOf(item.charAt(2)).equals(down)) {
                canvas.drawCircle(x + 6, y + 15, 10, paintDot);
            }
            if(String.valueOf(item.charAt(1)).equals("q")) {
                x = x + 29*4;
                canvas.drawLine(x - 29*4 , y + 10,x -29*3, y + 10, paintLine);
            } else if(String.valueOf(item.charAt(1)).equals("m")) {
                x = x + 29*4;
                canvas.drawLine(x, y - 15, x + 29, y - 15, paintLine);
                x = x + 29*4;
            } else if(String.valueOf(item.charAt(1)).equals("s")) {
                canvas.drawLine(x, y + 8,x + 29, y + 8, paintLine);
                canvas.drawLine(x, y + 12,x + 29, y + 12, paintLine);
                if(item.charAt(1) != next.charAt(1)){
                    x = x + 29;
                }
            } else if(String.valueOf(item.charAt(1)).equals("c")) {
                x = x + 29 * 4;
            } else if(String.valueOf(item.charAt(1)).equals("w")) {
                x = x + 29*4;
                canvas.drawLine(x, y - 15,x + 29, y - 15, paintLine);
                x = x + 29*4;
                canvas.drawLine(x, y - 15,x + 29, y - 15, paintLine);
                x = x + 29*4;
                canvas.drawLine(x, y - 15,x + 29, y - 15, paintLine);
                x = x + 29*4;
            }
            // Update output coordinates
            x = x + 29;
            if(x > 2350) {
                x = 118;
                y = y + 250;
            }
        }
        pdf.finishPage(page);

        //write the created pdf file into sdcard of android emulator
        String path = "/sdcard/Documents/sheet.pdf";
        File file = new File(path);
        try {
            pdf.writeTo(new FileOutputStream(file));
        } catch (IOException e) {
            e.printStackTrace();
        }
        pdf.close();
        contentUri = FileProvider.getUriForFile(this, "com.example.conversic", file);
    }

    /**
     * Upload the converted pdf file to Google Firebase (Storage and Realtime Database) and library.
     */
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

    /**
     * Process the staff music image using python code.
     */
    private void imgProcessing() throws IOException {
        initPython();
        File imageFile = FileUtil.from(Conversic1.this, uri);

        txtViewMusicString.setText(imageFile.getPath());

        Python py = Python.getInstance();

        //name of python file
        final PyObject pyObject = py.getModule("main");

        //name of function and its arguments
        PyObject obj = pyObject.callAttr("main", imageFile.getPath());
        xmlPath = obj.toString();

        //txtViewMusicString.setText("yay");


    }

    /**
     * Redirects to library activity after image processing and conversion.
     */
    private void convert() throws IOException, ParsingException, ParserConfigurationException {
        imgProcessing();
        convertFile();
        moveToLibActivity();
    }

    /**
     * Initialise python before running the code.
     */
    private void initPython() {
        if(!Python.isStarted()) {
            Python.start(new AndroidPlatform(this));
        }
    }

}
