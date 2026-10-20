package com.katza.yoadfirebase1;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private FireStoreDAO fsDAO;
    EditText etTz, etFullname, etGrade;
    Button btnInsert, btnSelect, btnSelectAll, btnUpdate, btnDelete;

    TextView tvContent;

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


        FireStoreDAO fsDAO = new FireStoreDAO();


        btnInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fsDAO.createDocument(etFullname.getText().toString(),
                        etTz.getText().toString(),
                        Integer.parseInt(etGrade.getText().toString()));
            }
        });

// בתוך onCreate ב-MainActivity.java
        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int grade = Integer.parseInt(etGrade.getText().toString());
                fsDAO.readQueryDocuments(grade, new IFirestoreCallback() {
                    @Override
                    public void onDataReceived(ArrayList<Student> students) {
                        tvContent.setText("");
                        for(int i=0; i< students.size();i++)
                            tvContent.setText(tvContent.getText().toString() + students.get(i).toString());
                    }

                });

                /*
                String docId = etTz.getText().toString();

                fsDAO.readDocumentById(docId, new IFirestoreCallback() {
                    @Override
                    public void onDataReceived(ArrayList<Student> students) {
                        if (students != null && !students.isEmpty()) {
                            tvContent.setText(students.get(0).toString());
                        }
                    }
                });

                 */
            }
        });

        btnSelectAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tvContent.setText("");
                fsDAO.readAllDocuments(new IFirestoreCallback() {
                    @Override
                    public void onDataReceived(ArrayList<Student> students) {
                        for(int i=0; i< students.size();i++)
                            tvContent.setText(tvContent.getText().toString() + students.get(i).toString());
                    }
                });
            }
        });

        // בתוך ה-Activity
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 1. חילוץ והמרת הנתונים מה-UI
                String docId = etTz.getText().toString();
                int newGrade = Integer.parseInt(etGrade.getText().toString());

                // 2. קריאה לשכבת ה-DAO
                fsDAO.updateDocument(docId, newGrade);
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fsDAO.deleteDocument(etTz.getText().toString());
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        fsDAO.listenToAllDocumentsChange(); // תתחיל להאזין כשהמסך עולה
    }

    @Override
    protected void onStop() {
        super.onStop();
        fsDAO.stopListening(); // תפסיק להאזין כשהמשתמש יוצא
    }


}