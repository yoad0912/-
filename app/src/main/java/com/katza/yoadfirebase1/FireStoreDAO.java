package com.katza.yoadfirebase1;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class FireStoreDAO {
    private static final String COLLECTION_NAME = "students";  //Data Access Object


    public void createDocument(String name, String id, int grade) {
        Map<String, Object> studentRecord = new HashMap<>();
        studentRecord.put("fullName",name);
        studentRecord.put("id",id);
        studentRecord.put("grade",grade);


        //FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = FirebaseFirestore.getInstance().
                collection(COLLECTION_NAME).document(id);


        docRef.set(studentRecord).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d("DAO_FIREBASE", "SUCCESFUL WRITE!");
            }
            }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("DAO_FIREBASE",e.getMessage().toString());
            }


        });
    }
    public void readDocumentById(String docID, IFirestoreCallback iFirestoreCallback) {
        DocumentReference docRef = FirebaseFirestore.getInstance()
                .collection(COLLECTION_NAME).document(docID);

        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    Student student = documentSnapshot.toObject(Student.class);
                    ArrayList<Student> students = new ArrayList<>();
                    if (student != null) {
                        students.add(student);
                    }
                    // החזרת הנתונים למסך דרך הממשק
                    iFirestoreCallback.onDataReceived(students);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("DAO_FIREBASE", e.getMessage());
            }
        });

        }
        public void readAllDocuments(IFirestoreCallback iFirestoreCallback){
            CollectionReference colRef = FirebaseFirestore.getInstance()
            .collection(COLLECTION_NAME);
            colRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                ArrayList<Student> students = new ArrayList<>();
                for(DocumentSnapshot docSS : queryDocumentSnapshots) {
                 Student student = docSS.toObject(Student.class);
                 //Log.d("DAO_FIREBASE", "SUCCESFUL READ!" + student);
                 students.add(student);
                }
                iFirestoreCallback.onDataReceived(students);
                }
            });
        }
        public void readQueryDocuments(int grade, IFirestoreCallback iFirestoreCallback){
        CollectionReference colRef = FirebaseFirestore.getInstance()
                .collection(COLLECTION_NAME);
            //colRef.where(Filter.or(Filter.lessThan("grade",80),Filter.equalTo("id","0390"))).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
        colRef.whereGreaterThan("grade",grade).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {

            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                ArrayList<Student> students = new ArrayList<>();
                for(DocumentSnapshot docSS : queryDocumentSnapshots) {
                    Student student = docSS.toObject(Student.class);
                    //Log.d("DAO_FIREBASE", "SUCCESFUL READ!" + student);
                    students.add(student);
                }
                iFirestoreCallback.onDataReceived(students);
            }
        });
        }
}


