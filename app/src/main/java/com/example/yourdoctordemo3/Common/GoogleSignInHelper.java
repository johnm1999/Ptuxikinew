package com.example.yourdoctordemo3.Common;


import static android.app.PendingIntent.getActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.google.android.gms.auth.api.identity.GetSignInIntentRequest;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.auth.api.signin.*;

import static androidx.core.app.ActivityCompat.startActivityForResult;

public class GoogleSignInHelper {


    @SuppressLint("StaticFieldLeak")
    private static GoogleSignInClient client;

    private static final GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestProfile()
            .requestId()
            .build();





    //To be used in case of googleFit transactions(with Google DB)
    public static void signIn(Activity activityToLaunchIntent, Context context) {
        if (client == null) {
            client = GoogleSignIn.getClient(context, gso);
        }

        Intent signInIntent = client.getSignInIntent();
        startActivityForResult(activityToLaunchIntent, signInIntent, 1, null);
    }

    public static GoogleSignInAccount getGoogleAccount(Context context, GoogleSignInOptionsExtension optionsExtension) {
        return GoogleSignIn.getAccountForExtension(context, optionsExtension);
    }

    /* public static String  returnEmail(){    //    edw den kserw giati den douleuei  SOS SOS
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(activity.getApplicationContext());// edw einai to provlia pou prepei na vrw
        String personalEmail = acct.getId();
        return personalEmail;
    }*/


}
