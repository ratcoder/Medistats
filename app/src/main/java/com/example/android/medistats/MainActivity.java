package com.example.android.medistats;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.os.StrictMode;
import android.util.Log;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

//import static com.example.android.medistats.R.string.password;
import static com.example.android.medistats.SessionManager.KEY_NAME;
import static com.example.android.medistats.SessionManager.KEY_PASSWORD;

public class MainActivity extends AppCompatActivity
{
    TextView forgetpass;
    TextView signup;
    EditText user_name,pass;
    Button login;
    String myJSON;
    SessionManager session;
    private static final String URL = "192.168.43.224";
    private static final String UserLogin = "http://"+URL+"/ratcoder/Login.php";
    private static final String DoctorLogin = "http://"+URL+"/ratcoder/LoginDoctor.php";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        user_name = (EditText) findViewById(R.id.user_name);
        pass = (EditText) findViewById(R.id.password);
        forgetpass = (TextView) findViewById(R.id.forgetpass);
        signup = (TextView) findViewById(R.id.signup);
        session = new SessionManager(getApplicationContext());
        login = (Button)findViewById(R.id.login);
        StrictMode.ThreadPolicy policy=new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        if(session.isLoggedIn())
        {
            HashMap<String, String> user = new HashMap<String, String>();
            user = session.getUserDetails();
            String name=user.get(KEY_NAME);
            String pass=user.get(KEY_PASSWORD);
            getuserpass(name,pass);

            /*Intent intent = new Intent(this, MainActivity.class);
            String name=Name.getText().toString();
            intent.putExtra("name",name);
            this.startActivity(intent);*/
        }


       // Toast.makeText(getApplicationContext(), "User Login Status: " + session.isLoggedIn(), Toast.LENGTH_LONG).show();
        login.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View ar)
            {
                String name=""+user_name.getText().toString();
                String password=""+pass.getText().toString();
                getuserpass(name,password);
            }
        });

        forgetpass.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(MainActivity.this,ForgetPass.class);
                startActivity(intent);
                finish();
            }
        });

        signup.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(MainActivity.this,SignUp.class);
                startActivity(intent);
                finish();
            }
        });
    }

    public void gett(String result,String name,String pass)
    {
        Log.i("gett",result);


        myJSON=result;
        myJSON = myJSON.replace("\n", "").replace("\r", "");
        if(myJSON.equals("User"))
        {
            session.createLoginSession(name,pass);
            Toast.makeText(getApplicationContext(),"Welcome"+" "+user_name.getText(), Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, UserHome.class);
            String name1=user_name.getText().toString();
            intent.putExtra("name",name1);
            this.startActivity(intent);
        }
        else if(myJSON.equals("NotUser"))
        {
            checkdoctor(name,pass);
        }
        else if (myJSON.equals("Doctor"))
        {
            session.createLoginSession(name,pass);
            Toast.makeText(getApplicationContext(),"Welcome"+" "+user_name.getText(), Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, DocHome.class);
            String name1=user_name.getText().toString();
            intent.putExtra("name",name1);
            this.startActivity(intent);

        }
        else
        {
            Log.i("MainActivity","After logged in");
            Intent intent = new Intent(this, MainActivity.class);
            this.startActivity(intent);
        }
    }

    public void getuserpass(String name, String pass)
    {

        checkuser(name,pass);
    }

    public void checkdoctor(String name, String pass)
    {
        InputStream is = null;

        List<NameValuePair> nameValuePairs=new ArrayList<NameValuePair>(1);
        nameValuePairs.add(new BasicNameValuePair("name",name));
        nameValuePairs.add(new BasicNameValuePair("pass",pass));
        String result=null;
        try
        {
            HttpClient httpClient=new DefaultHttpClient(new BasicHttpParams());
            HttpPost httpPost=new HttpPost(DoctorLogin);
            InputStream inputStream=null;
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            HttpResponse response=httpClient.execute(httpPost);
            HttpEntity entity=response.getEntity();
            is=entity.getContent();
            BufferedReader reader=new BufferedReader(new InputStreamReader(is,"UTF-8"),8);
            StringBuilder sb=new StringBuilder();
            String line=null;
            while((line=reader.readLine())!=null)
            {
                sb.append(line+"\n");
            }
            result=sb.toString();

        }
        catch(ClientProtocolException e)
        {
            Log.e("ClientProtocol","Log_tag");

            e.printStackTrace();
        }
        catch (IOException e)
        {
            Log.e("Log_tag","IOException");
            e.printStackTrace();
        }
        finally
        {
            try{if(is!=null)is.close();}
            catch(Exception squish){}
        }
        gett(result,name,pass);
    }

    public void checkuser(String name, String pass)
    {

        InputStream is = null;

        List<NameValuePair> nameValuePairs=new ArrayList<NameValuePair>(1);
        nameValuePairs.add(new BasicNameValuePair("name",name));
        nameValuePairs.add(new BasicNameValuePair("pass",pass));
        String result=null;
        try
        {
            HttpClient httpClient=new DefaultHttpClient(new BasicHttpParams());
            HttpPost httpPost=new HttpPost(UserLogin);
            InputStream inputStream=null;
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            HttpResponse response=httpClient.execute(httpPost);
            HttpEntity entity=response.getEntity();
            is=entity.getContent();
            BufferedReader reader=new BufferedReader(new InputStreamReader(is,"UTF-8"),8);
            StringBuilder sb=new StringBuilder();
            String line=null;
            while((line=reader.readLine())!=null)
            {
                sb.append(line+"\n");
            }
            result=sb.toString();

        }
        catch(ClientProtocolException e)
        {
            Log.e("ClientProtocol","Log_tag");

            e.printStackTrace();
        }
        catch (IOException e)
        {
            Log.e("Log_tag","IOException");
            e.printStackTrace();
        }
        finally
        {
            try{if(is!=null)is.close();}
            catch(Exception squish){}
        }
        Log.i("shubham","name:"+name+"pass"+pass);
//        Log.i("shubham",result);


        gett(result,name,pass);

    }




    @Override
    protected void onResume()
    {
        super.onResume();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
    }

    @Override
    public void onBackPressed()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to exit?")
                .setCancelable(false)
                .setPositiveButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                })
                .setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        System.exit(0);
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
}
