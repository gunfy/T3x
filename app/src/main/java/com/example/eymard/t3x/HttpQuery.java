package com.example.eymard.t3x;

import android.content.ContentValues;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by eymard on 08/12/2015.
 */


public class HttpQuery extends AsyncTask<String, String, String> {

    private String Donnees = "";
    public AsyncResponse delegate = null;
    private String reponse = "";

    public HttpQuery(String s, AsyncResponse delegate) {
        this.Donnees = s;
        this.delegate = delegate;
    }

    @Override
    protected void onPostExecute(String result) { //// onPostExecute displays the results of the AsyncTask.

        delegate.processFinish(result);
    }

    @Override
    protected String doInBackground(String... params) {
        String response = "";
        try {
            //Connection à mon serveur
            URL url = new URL("http://emamene.free.fr/index.php");

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(10000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            ContentValues values = new ContentValues();
            values.put("request", this.Donnees);

            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter( new OutputStreamWriter(os, "UTF-8"));
            writer.write(getDonnees(values));
            writer.flush();
            writer.close();
            os.close();

            //reponse
            int responseCode=conn.getResponseCode();

            if (responseCode == HttpsURLConnection.HTTP_OK) {
                String line;
                BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line=br.readLine()) != null) {
                    response+=line;
                }
            }
            else {
                response="";
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    private String getDonnees(ContentValues params) throws UnsupportedEncodingException
    {
        StringBuilder result = new StringBuilder();

        result.append("request");
        result.append("=");
        result.append(params.get("request"));

        return result.toString();
    }


}
