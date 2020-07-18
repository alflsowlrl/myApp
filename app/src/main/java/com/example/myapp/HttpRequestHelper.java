package com.example.myapp;

import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpRequestHelper {
    private final String TAG = "HTTP_HELPER";

    public String REQUEST(String Method, String uri, String json){
        InputStream is = null;
        String result = "";

        try {
            URL urlCon = new URL(uri);
            HttpURLConnection httpCon = (HttpURLConnection)urlCon.openConnection();

            // Set some headers to inform server about the type of the content

            httpCon.setRequestProperty("Accept", "application/json");

            httpCon.setRequestProperty("Content-type", "application/json");
            httpCon.setRequestProperty("Accept-Charset", "UTF-8"); // Accept-Charset 설정.


            // OutputStream으로 POST 데이터를 넘겨주겠다는 옵션.
            httpCon.setRequestMethod(Method);

            // InputStream으로 서버로 부터 응답을 받겠다는 옵션.
            httpCon.setDoInput(true);


            if(Method.equals("POST") || Method.equals("DELETE")){
                OutputStream os = httpCon.getOutputStream();

                os.write(json.getBytes("UTF-8"));

                os.flush();
            }


            // receive response as inputStream

            try {
                is = httpCon.getInputStream();
                // convert inputstream to string
                if(is != null)
                    result = convertStreamToString(is);
                else
                    result = "Did not work!";

            }

            catch (IOException e) {
                e.printStackTrace();
            }

            finally {
                httpCon.disconnect();
            }
        }

        catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, e.toString());
        }

        catch (Exception e) {
            Log.d(TAG, e.toString());
        }

        return result;
    }

    public String POST(Contact contact){
        String json = "";

        try {
            URL urlCon = new URL("http://192.249.19.244:1880/api/contacts");


            // build jsonObject

            JSONObject jsonObject = new JSONObject();

            jsonObject.accumulate("name", contact.getName());

            jsonObject.accumulate("number", contact.getNumber());


            json = jsonObject.toString();
        }
        catch (Exception e){e.printStackTrace();}

        return REQUEST("POST", "http://192.249.19.244:1880/api/contacts", json);
    }

    public String DELETE(Contact contact){
        String json = "";

        try {
            URL urlCon = new URL("http://192.249.19.244:1880/api/contacts");


            // build jsonObject

            JSONObject jsonObject = new JSONObject();

            jsonObject.accumulate("name", contact.getName());

            jsonObject.accumulate("number", contact.getNumber());


            json = jsonObject.toString();
        }
        catch (Exception e){e.printStackTrace();}

        Log.d(TAG, "delete: " + json);

        return REQUEST("DELETE", "http://192.249.19.244:1880/api/contacts", json);
    }

    public String GETAll(){
        return REQUEST("GET", "http://192.249.19.244:1880/api/contacts", "");
    }

    public String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line + "\n");
        }
        is.close();
        return sb.toString();
    }
}
