package com.example.yourdoctordemo3.Common;

import android.annotation.SuppressLint;
import android.util.Log;
import android.widget.TextView;
import androidx.annotation.NonNull;
import com.example.yourdoctordemo3.HeartRateManager.Heart_Rate;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class FireStoreHelper extends GoogleSignInHelper{

    @SuppressLint("StaticFieldLeak")
    private static FirebaseFirestore db;
    private static final HashMap<String, Object> documentData = new HashMap<>();

    private static final String collectionsName = "email";
    private static final String dataName = "name";
    private static final String TAG = "Data of ";
    private static final String successfulMessage = " Successfully inserted!";
    private static final String tvError = "No Data Found!";
    private static final String documentSnapshotError = "Document Snapshot from FireStore is NULL";

    public static void insertData(@NonNull String documentPath, @NonNull String dataIdentifier, @NonNull List<String> insertedData, @NonNull String usersName) {
        documentData.clear();

        if (db == null) {
            setDbInstance();
        }

        insertedData.iterator().forEachRemaining(data -> {
            documentData.put(dataIdentifier + insertedData.indexOf(data), data);
        });

        documentData.put(dataName, usersName);
        db.collection(collectionsName).document(documentPath).set(documentData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        insertedData.iterator().forEachRemaining(data -> {
                            Log.i(TAG, documentData.get(dataName) + ": " + documentData.get(dataIdentifier
                                    + insertedData.indexOf(data)) + successfulMessage);
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        Log.e(TAG, "Error inserting the data!", e);
                    }
                });
    }

    @SuppressLint("SetTextI18n")
    public static void readData(TextView tv, DataType dataType) {
        if (db == null) {
            setDbInstance();
        }

        DocumentReference docRef = null;

        if (DataType.TYPE_HEART_RATE_BPM == dataType) {
            docRef = db.collection(collectionsName).document("HeartRate");
        }
        if (DataType.TYPE_SLEEP_SEGMENT == dataType) {
            docRef = db.collection(collectionsName).document("SleepData");
        }

        if (docRef != null) {
            docRef.get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot documentSnapshot = task.getResult();
                                if (documentSnapshot.exists()) {
                                    tv.setText(getDataToBePrinted(documentSnapshot, dataType));
                                } else {
                                    Log.d("DocError ", "No such document");
                                }
                            } else {
                                Log.d("TaskError ", " get failed with ", task.getException());
                            }
                        }
                    });
        } else {
            tv.setText(tvError);
            Log.w("Document Error ", documentSnapshotError);
        }
    }

    private static String getDataToBePrinted(DocumentSnapshot docSnap, DataType dataType) {
        Map<String, Object> dataMap = Objects.requireNonNull(docSnap.getData());

        String stringData = tvError;

        if (DataType.TYPE_HEART_RATE_BPM == dataType &&
                dataMap.keySet().stream().anyMatch(str -> str.contains("AvgRate"))) {

            stringData = (String) dataMap.keySet()
                    .stream()
                    .filter(str -> str.contains("AvgRate"))
                    .map(dataMap::get)
                    .collect(Collectors.toList())
                    .get(0);
        }
        if (DataType.TYPE_SLEEP_SEGMENT == dataType &&
                dataMap.keySet().stream().anyMatch(str -> str.contains("Sleep"))) {
            StringBuilder sb = new StringBuilder();

            dataMap.keySet()
                    .stream()
                    .filter(str -> str.contains("Sleep"))
                    .map(dataMap::get)
                    .collect(Collectors.toList())
                    .stream()
                    .sorted()
                    .forEach(str -> sb.append(((String) str).substring(0, 12))
                            .append("\n")
                            .append(((String) str).substring(12))
                            .append("\n"));

            stringData = sb.toString();
        }
        return stringData;
    }

    private static void setDbInstance() {
        if (db == null) {
            db = FirebaseFirestore.getInstance();
        }
    }
}
