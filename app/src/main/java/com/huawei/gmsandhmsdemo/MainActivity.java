package com.huawei.gmsandhmsdemo;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

import com.huawei.hmf.tasks.Task;
import com.huawei.hms.auth.api.signin.HuaweiIdSignIn;
import com.huawei.hms.auth.api.signin.HuaweiIdSignInClient;
import com.huawei.hms.common.ApiException;
import com.huawei.hms.support.api.hwid.HuaweiIdSignInOptions;
import com.huawei.hms.support.api.hwid.SignInHuaweiId;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final String CLIENT_ID = "649702601015-3m470ualrcc81hrnk8alv79uqkohjmqs.apps.googleusercontent.com";
    private static final int HMS_REQUEST_CODE = 1111;
    private static final int GMS_REQUEST_CODE = 2222;
    private Button mHmsSignBtn;
    private Button mGmsSignBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mHmsSignBtn = findViewById(R.id.hms_sign_btn);
        mHmsSignBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signInByHms();
            }
        });

        mGmsSignBtn = findViewById(R.id.gms_sign_btn);
        mGmsSignBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signInByGms();
            }
        });

        boolean isHmsAvailable = HmsGmsUtil.isHmsAvailable(this);
        boolean isGmsAvailable = HmsGmsUtil.isGmsAvailable(this);
        Log.i(TAG, "isHmsAvailable: " + isHmsAvailable + ", isGmsAvailable: " + isGmsAvailable);
        if (isHmsAvailable && !isGmsAvailable) {
            // Only hms, Sign In by huawei account.
            mHmsSignBtn.setVisibility(View.VISIBLE);
        } else if (!isHmsAvailable && isGmsAvailable) {
            // Only gms, Sign In by google account.
            mGmsSignBtn.setVisibility(View.VISIBLE);
        } else if (isHmsAvailable && isGmsAvailable) {
            // both hsm and hms, decide by developer.
            mGmsSignBtn.setVisibility(View.VISIBLE);
            mHmsSignBtn.setVisibility(View.VISIBLE);
        } else if (!isHmsAvailable && !isGmsAvailable) {
            // neither hms and gms, decide by developer.
            mHmsSignBtn.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Sign In by HMS
     */
    private void signInByHms() {
        HuaweiIdSignInOptions signInOptions = new HuaweiIdSignInOptions.Builder(HuaweiIdSignInOptions.DEFAULT_SIGN_IN)
                .requestServerAuthCode()
                .build();
        HuaweiIdSignInClient client = HuaweiIdSignIn.getClient(this, signInOptions);
        startActivityForResult(client.getSignInIntent(), HMS_REQUEST_CODE);
    }

    /**
     * Sign In by GMS
     */
    private void signInByGms() {
        GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestServerAuthCode(CLIENT_ID)
                .build();
        GoogleSignInClient client = GoogleSignIn.getClient(this, signInOptions);
        startActivityForResult(client.getSignInIntent(), GMS_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(TAG, "onActivityResult requestCode: " + requestCode);
        if (HMS_REQUEST_CODE == requestCode) {
            Task<SignInHuaweiId> signInHuaweiIdTask = HuaweiIdSignIn.getSignedInAccountFromIntent(data);
            if (signInHuaweiIdTask.isSuccessful()) {
                SignInHuaweiId huaweiAccount = signInHuaweiIdTask.getResult();
                Log.i(TAG, "HSM sign in success, serverAuthCode:" + huaweiAccount.getServerAuthCode());
                Toast.makeText(this, "HMS sign in success", Toast.LENGTH_LONG).show();
            } else {
                // HMS Sign In falied.
                Log.e(TAG, "HMS sign in failed : " + ((ApiException) signInHuaweiIdTask.getException()).getStatusCode());
                Toast.makeText(this, "HMS sign in failed", Toast.LENGTH_LONG).show();
            }
        } else if (GMS_REQUEST_CODE == requestCode) {
            com.google.android.gms.tasks.Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(com.google.android.gms.common.api.ApiException.class);
                Log.i(TAG, "GMS sign in success, serverAuthCode:" + account.getServerAuthCode());
                Toast.makeText(this, "GMS sign in success", Toast.LENGTH_LONG).show();
            } catch (com.google.android.gms.common.api.ApiException e) {
                // GMS Sign In failed
                Log.e(TAG, "GMS sign in failed", e);
                Toast.makeText(this, "GMS sign in failed", Toast.LENGTH_LONG).show();
            }
        }
    }

}
