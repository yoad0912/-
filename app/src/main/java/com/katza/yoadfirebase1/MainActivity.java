package com.katza.yoadfirebase1;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private FireStoreDAO fsDAO;

    private ImageView ivStudent;
    EditText etTz, etFullname, etGrade;
    Button btnInsert, btnSelect, btnSelectAll, btnUpdate, btnDelete, btnSelectImage;

    TextView tvContent;

    private ActivityResultLauncher<String> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        fsDAO = new FireStoreDAO();

        etTz = findViewById(R.id.tz);
        etFullname = findViewById(R.id.fullname);
        etGrade = findViewById(R.id.grade);
        btnInsert = findViewById(R.id.btnInsert);
        btnSelect = findViewById(R.id.btnSelect);
        btnSelectAll = findViewById(R.id.btnSelectAll);
        tvContent = findViewById(R.id.tvcontent);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnDelete = findViewById(R.id.btnDelete);
        ivStudent = findViewById(R.id.ivStudent);
        btnSelectImage = findViewById(R.id.btnSelectImage);

        // מנגנון לבחירת תמונה מהגלריה
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        try {
                            InputStream inputStream = getContentResolver().openInputStream(uri);
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                            ivStudent.setImageBitmap(bitmap);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

        btnSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imagePickerLauncher.launch("image/*");
            }
        });

        // הוספת תלמיד
        btnInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = etFullname.getText().toString().trim();
                String id = etTz.getText().toString().trim();
                String gradeStr = etGrade.getText().toString().trim();

                if (name.isEmpty() || id.isEmpty() || gradeStr.isEmpty()) {
                    Toast.makeText(MainActivity.this, "נא למלא את כל השדות", Toast.LENGTH_SHORT).show();
                    return;
                }

                int grade = Integer.parseInt(gradeStr);
                String imageBase64 = convertImageViewToBase64(ivStudent);

                fsDAO.createDocument(name, id, grade, imageBase64);
                Toast.makeText(MainActivity.this, "תלמיד נוסף בהצלחה!", Toast.LENGTH_SHORT).show();
            }
        });

        // שליפה לפי ציון
        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String gradeStr = etGrade.getText().toString().trim();
                if (gradeStr.isEmpty()) {
                    Toast.makeText(MainActivity.this, "נא להזין ציון לסינון", Toast.LENGTH_SHORT).show();
                    return;
                }

                int grade = Integer.parseInt(gradeStr);
                fsDAO.readQueryDocuments(grade, new IFirestoreCallback() {
                    @Override
                    public void onDataReceived(ArrayList<Student> students) {
                        tvContent.setText("");
                        for (int i = 0; i < students.size(); i++) {
                            Student student = students.get(i);
                            tvContent.setText(tvContent.getText().toString() + student.toString());

                            // מציג ב-ImageView את התמונה של התלמיד הראשון שנשלף
                            if (i == 0 && student.getImageBase64() != null) {
                                displayBase64Image(student.getImageBase64(), ivStudent);
                            }
                        }
                    }
                });
            }
        });

        // שליפת הכל
        btnSelectAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tvContent.setText("");
                fsDAO.readAllDocuments(new IFirestoreCallback() {
                    @Override
                    public void onDataReceived(ArrayList<Student> students) {
                        for (int i = 0; i < students.size(); i++) {
                            Student student = students.get(i);
                            tvContent.setText(tvContent.getText().toString() + student.toString());

                            // מציג ב-ImageView את התמונה של התלמיד הראשון שנשלף
                            if (i == 0 && student.getImageBase64() != null) {
                                displayBase64Image(student.getImageBase64(), ivStudent);
                            }
                        }
                    }
                });
            }
        });

        // עדכון ציון
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String docId = etTz.getText().toString().trim();
                String gradeStr = etGrade.getText().toString().trim();

                if (docId.isEmpty() || gradeStr.isEmpty()) {
                    Toast.makeText(MainActivity.this, "נא להזין ת.ז. וציון חדש לעדכון", Toast.LENGTH_SHORT).show();
                    return;
                }

                int newGrade = Integer.parseInt(gradeStr);
                fsDAO.updateDocument(docId, newGrade);
                Toast.makeText(MainActivity.this, "הציון עודכן!", Toast.LENGTH_SHORT).show();
            }
        });

        // מחיקה
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String docId = etTz.getText().toString().trim();
                if (docId.isEmpty()) {
                    Toast.makeText(MainActivity.this, "נא להזין ת.ז. למחיקה", Toast.LENGTH_SHORT).show();
                    return;
                }

                fsDAO.deleteDocument(docId);
                Toast.makeText(MainActivity.this, "התלמיד נמחק!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        fsDAO.stopListening();
    }

    // המרת תמונה מ-ImageView למחרוזת Base64 (לשמירה במסד)
    private String convertImageViewToBase64(ImageView imageView) {
        BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
        if (drawable == null) return "";

        Bitmap bitmap = drawable.getBitmap();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();

        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    // המרת מחרוזת Base64 בחזרה לתמונה והצגתה ב-ImageView
    private void displayBase64Image(String base64String, ImageView imageView) {
        if (base64String != null && !base64String.isEmpty()) {
            try {
                byte[] decodedString = Base64.decode(base64String, Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                imageView.setImageBitmap(decodedByte);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}