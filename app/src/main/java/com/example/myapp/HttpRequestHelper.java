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
    public String POST(Contact contact){
        InputStream is = null;
        String result = "";

        try {
            URL urlCon = new URL("http://192.249.19.244:1880/api/contacts");
            HttpURLConnection httpCon = (HttpURLConnection)urlCon.openConnection();

            String json = "";

            // build jsonObject

            JSONObject jsonObject = new JSONObject();

            jsonObject.accumulate("name", contact.getName());

            jsonObject.accumulate("number", contact.getNumber());


            // convert JSONObject to JSON to String

            json = jsonObject.toString();

            Log.d(TAG, "json: " + json);


            // Set some headers to inform server about the type of the content

            httpCon.setRequestProperty("Accept", "application/json");

            httpCon.setRequestProperty("Content-type", "application/json");
            httpCon.setRequestProperty("Accept-Charset", "UTF-8"); // Accept-Charset 설정.


            // OutputStream으로 POST 데이터를 넘겨주겠다는 옵션.
            httpCon.setDoOutput(true);

            // InputStream으로 서버로 부터 응답을 받겠다는 옵션.
            httpCon.setDoInput(true);



            OutputStream os = httpCon.getOutputStream();

            os.write(json.getBytes("UTF-8"));

            os.flush();

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

//    public String DELETE(Contact contact){
//        InputStream is = null;
//        String result = "";
//
//        try {
//            URL urlCon = new URL("http://192.249.19.244:1880/api/contacts");
//            HttpURLConnection httpCon = (HttpURLConnection)urlCon.openConnection();
//
//            String json = "";
//
//            // build jsonObject
//
//            JSONObject jsonObject = new JSONObject();
//
//            jsonObject.accumulate("_id", contact.getId());
//
//            // convert JSONObject to JSON to String
//
//            json = jsonObject.toString();
//
//            Log.d(TAG, "json: " + json);
//
//
//            // Set some headers to inform server about the type of the content
//
//            httpCon.setRequestProperty("Accept", "application/json");
//
//            httpCon.setRequestProperty("Content-type", "application/json");
//            httpCon.setRequestProperty("Accept-Charset", "UTF-8"); // Accept-Charset 설정.
//
//
//            // OutputStream으로 POST 데이터를 넘겨주겠다는 옵션.
//            httpCon.setRequestMethod("DELETE");
//
//            // InputStream으로 서버로 부터 응답을 받겠다는 옵션.
//            httpCon.setDoInput(true);
//
//
//            OutputStream os = httpCon.getOutputStream();
//
//            os.write(json.getBytes("UTF-8"));
//
//            os.flush();
//
//            // receive response as inputStream
//
//            try {
//                is = httpCon.getInputStream();
//
//                // convert inputstream to string
//
//                if(is != null)
//                    result = convertStreamToString(is);
//                else
//                    result = "Did not work!";
//            }
//
//            catch (IOException e) {
//                e.printStackTrace();
//            }
//
//            finally {
//                httpCon.disconnect();
//            }
//        }
//
//        catch (IOException e) {
//            e.printStackTrace();
//            Log.d(TAG, e.toString());
//        }
//
//        catch (Exception e) {
//            Log.d(TAG, e.toString());
//        }
//
//        return result;
//    }

    public String GETAll(){
        InputStream is = null;
        String result = "";

        try {
            URL urlCon = new URL("http://192.249.19.244:1880/api/contacts");
            HttpURLConnection httpCon = (HttpURLConnection)urlCon.openConnection();

            String json = "";


            // Set some headers to inform server about the type of the content

            httpCon.setRequestProperty("Accept", "application/json");

            httpCon.setRequestProperty("Content-type", "application/json");



            // OutputStream으로 GET
            httpCon.setRequestMethod("GET");

            // InputStream으로 서버로 부터 응답을 받겠다는 옵션.
            httpCon.setDoInput(true);



//            OutputStream os = httpCon.getOutputStream();
//
//            os.write(json.getBytes("euc-kr"));
//
//            os.flush();

            // receive response as inputStream

            try {

                is = httpCon.getInputStream();

                // convert inputstream to string

                if(is != null){
                    result = convertStreamToString(is);
                }


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
