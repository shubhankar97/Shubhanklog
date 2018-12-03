package com.shubhankarthinks.shubhankar1;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    CallbackManager callbackManager;
    TextView birthday,email,friends;
    ImageView img;

  ProgressDialog mDailogue;


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode,resultCode,data);
    }




    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        callbackManager=CallbackManager.Factory.create();
        email=(TextView) findViewById(R.id.email);
        birthday=(TextView)findViewById(R.id.birthday);
        friends=(TextView)findViewById(R.id.friends);
        img = (ImageView) findViewById(R.id.imageView);
     final LoginButton fb=(LoginButton)findViewById(R.id.fb);
     printKeyHash();
        fb.setReadPermissions(Arrays.asList("public_profile","user_birthday","email","user_friends"));

        fb.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                mDailogue=new ProgressDialog( MainActivity.this);
                mDailogue.setMessage("Retrieving .........");
                mDailogue.show();
                String accessToken=loginResult.getAccessToken().getToken();
                GraphRequest request=GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        mDailogue.dismiss();
                        Log.d("response",response.toString());
                        getData(object);

                    }
                });
                //Graph API
                Bundle parameters= new Bundle();
                parameters.putString("fields","id,email,birthday,friends");
                request.setParameters(parameters);
                request.executeAsync();

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });
    }

    private void getData(JSONObject object) {
        try{
            URL profile_picture=new URL("https://graph.facebook.com/"+object.getString("id")+"/picture?width=2506height=250");
            Picasso.with(this).load(profile_picture.toString()).into(img);
            email.setText(object.getString("email"));
            birthday.setText(object.getString("birthday"));

            friends.setText(object.getString("Friends:"+object.getJSONObject("friends").getJSONObject("summary").getString("total_count")));





        }
        catch (MalformedURLException e)
        {

e.printStackTrace();
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }


    private void printKeyHash(){
        try{
            PackageInfo info=getPackageManager().getPackageInfo("com.shubhankarthinks.shubhankar1",PackageManager.GET_SIGNATURES);
            for(Signature signature:info.signatures){
                MessageDigest md=MessageDigest.getInstance("Sha");
                md.update(signature.toByteArray());
                Log.d("KeyHash",Base64.encodeToString(md.digest(),Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e)
        {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
}
