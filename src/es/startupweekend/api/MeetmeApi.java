package es.startupweekend.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.AbstractHttpEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import es.startupweekend.model.User;

import android.util.Base64;
import android.util.Log;

public class MeetmeApi implements MeetmeApiInterface{

    private String TAG = MeetmeApi.class.getName();
    private String API_URL = "http://elchudi.xen.prgmr.com:8090";
    
    private String GET_USERS = API_URL + "/get_users";
    private String GET_USER = API_URL + "/get_user";
    private String GET_CONNECTIONS = API_URL + "/get_connections";
    private String ADD_CONNECTION = API_URL + "/add_connection";
    private String ADD_USER = API_URL + "/add_user";
    protected static final String JSON_TYPE = "application/json";
    protected static final String XML_TYPE = "text/xml";
    
    private static final String KEY_USER_ID  = "user_id";
    private static final String KEY_EXTRA_DATA  = "extra_data";
    private static final String KEY_NAME  = "name";
    private static final String KEY_IMG  = "img";
    private static final String KEY_USER_TYPE  = "user_type";
    private static final String KEY_USER_1  = "user_1";
    private static final String KEY_USER_2  = "user_2";
    
    private static final int BUFFERSIZE = 1024;
    private String CONTENT = "Content-Type";
    private JSONObject responseJson;

    public enum HttpRequestType {
        get, post, put, delete,
    }

    protected StringEntity createJSONRequestForRegister(String[] names, Object[] values) {
        try {
            JSONObject json = createJsonFromParams(names, values);
            StringEntity entity = new StringEntity(json.toString());
            entity.setContentType(XML_TYPE);
            return entity;
        } catch (UnsupportedEncodingException e) {
            Log.d(TAG, "error creating entity from json Encodign", e);
        } catch (JSONException e) {
            Log.d(TAG, "error creating entity from json json", e);
        }
        return null;
    }


    private JSONObject createJsonFromParams(String[] names, Object[] values) throws JSONException {
        JSONObject json = new JSONObject();
        for (int i = 0; i < values.length; i++) {
            json.put(names[i], values[i]);
        }
        return json;
    }

    protected StringEntity createJSONRequest(String[] names, String[] values) {
        try {
            JSONObject json = new JSONObject();
            for (int i = 0; i < values.length; i++) {
                json.put(names[i], values[i]);
            }
            StringEntity entity = new StringEntity(json.toString());
            entity.setContentType(XML_TYPE);
            return entity;
        } catch (UnsupportedEncodingException e) {
            Log.d(TAG, "error creating entity from json Encodign", e);
        } catch (JSONException e) {
            Log.d(TAG, "error creating entity from json json", e);
        }
        return null;
    }

    protected Map<String, String> getBasicHeaders() {
        Map<String, String> headers = new HashMap<String, String>();
        headers.put(CONTENT, JSON_TYPE);
        return headers;
    }

    
    protected HttpResponse callApi(String url, Map<String, String> headers, HttpRequestType type,
            AbstractHttpEntity reqEntity, boolean authorization) {
        Log.d(TAG, "callApi " + url);

        HttpClient httpclient = new DefaultHttpClient();

        HttpRequestBase request = null;
        if (type == HttpRequestType.post) {
            request = new HttpPost(url);
            if (reqEntity != null) {
                ((HttpPost) request).setEntity(reqEntity);
            }
        } else if (type == HttpRequestType.get) {
            request = new HttpGet(url);
        } else if (type == HttpRequestType.put) {
            request = new HttpPut(url);
            if (reqEntity != null) {
                ((HttpPut) request).setEntity(reqEntity);
            }
        } else if (type == HttpRequestType.delete) {
            request = new HttpDelete(url);
        } else {
            assert false;
        }

        if (headers != null) {
            for (String key : headers.keySet()) {
                request.addHeader(key, headers.get(key));
            }
        }

        HttpResponse response = null;

        try {
            response = httpclient.execute(request);
        } catch (IOException e) {
            Log.d(TAG, "Error requesting url", e);
        }

//        Log.v(TAG, "Response received " + response.getStatusLine());
//
//        int resultado = response.getStatusLine().getStatusCode();
//        if (resultado == 400) {
//            Log.d(TAG, "error in petition");
//        }
           return response;
    }

    public JSONObject getResponseInfo(HttpResponse response) {
        HttpEntity entity = response.getEntity();
        try {
            responseJson = getJSONObject(entity);
            Log.i(TAG, responseJson.toString());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return responseJson;
    }

    protected JSONObject getJSONObject(HttpEntity entity) throws IOException, JSONException {
        JSONObject json = new JSONObject();
        if (entity != null) {
            InputStream instream = entity.getContent();
            String result = convertStreamToString(instream);
            json = new JSONObject(result);
            System.gc();
            instream.close();
        }
        return json;
    }

    private String convertStreamToString(InputStream is) throws IOException {
        Writer writer = new StringWriter();
        char[] buffer = new char[BUFFERSIZE];
        try {
            Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            int n;
            while ((n = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, n);
            }
        } finally {
            is.close();
        }
        return writer.toString();
    }

    
/*
    public String getToken() {
        HttpResponse response = callApi(GET_TOKEN_URL, null, HttpRequestType.get, null, true);

        JSONObject responseJson = null;
        try {
            responseJson = getResponseInfo(response);
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }

        try {
            String token = responseJson.getString(KEY_TOKEN);
            return token;
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }
*/
    

    /*
     * Returns a list of  user_ids connected to this users. It will return also the id of the same user 
     * because it's a crappy implementation
     * @see es.sw.meetup.backendapi.MeetmeApiInterface#getConnections(java.lang.String)
     */
    @Override
    public List<String> getConnections(String userId) {
        List<String> to_ret = new ArrayList<String>();
        String[] userKeys = { KEY_USER_ID};
        String[] userValues = { userId};
        JSONObject userJson = null;
        try {
            userJson = createJsonFromParams(userKeys, userValues);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        StringEntity entity = createJSONRequestForRegister(userKeys, userValues);
        HttpResponse response = callApi(GET_CONNECTIONS, getBasicHeaders(), HttpRequestType.post, entity, false);

        if (response.getStatusLine().getStatusCode() == 200) {
            JSONObject info = getResponseInfo(response);
            try {
                JSONArray array = info.getJSONArray("meetmeup");
                for(int i = 0; i<array.length(); i++){
                    JSONObject o = (JSONObject) array.get(i);
                    
                    to_ret.add(o.getString(KEY_USER_1));
                    to_ret.add(o.getString(KEY_USER_2));
                        
                    
                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
         
            return to_ret;
        } else {
            return to_ret;
        }       
    }

    @Override
    public List<User> getUsers() {
        List<User> to_ret = new ArrayList<User>();

        HttpResponse response = callApi(GET_USERS, getBasicHeaders(), HttpRequestType.get, null, false);

        if (response.getStatusLine().getStatusCode() == 200) {
            JSONObject info = getResponseInfo(response);
            try {
                JSONArray array = info.getJSONArray("meetmeup");
                for(int i = 0; i<array.length(); i++){
                    JSONObject o = (JSONObject) array.get(i);
                    User u = new User(o.getString(KEY_USER_ID), o.getString(KEY_NAME), o.getString(KEY_USER_TYPE), o.getString(KEY_IMG), o.getString(KEY_EXTRA_DATA), null);
                    to_ret.add(u);
                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            return to_ret;
        } else {
            return to_ret;
        }       
    }

    @Override
    public User getUser(String userId) {
//        List<String> to_ret = new ArrayList<String>();
        User u = null;
        String[] userKeys = { KEY_USER_ID};
        String[] userValues = { userId};
        JSONObject userJson = null;
        try {
            userJson = createJsonFromParams(userKeys, userValues);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        StringEntity entity = createJSONRequestForRegister(userKeys, userValues);
        HttpResponse response = callApi(GET_USER, getBasicHeaders(), HttpRequestType.post, entity, false);

        if (response.getStatusLine().getStatusCode() == 200) {
            JSONObject o = getResponseInfo(response);
            try {
                    u = new User(o.getString(KEY_USER_ID), o.getString(KEY_NAME), o.getString(KEY_USER_TYPE), o.getString(KEY_IMG), o.getString(KEY_EXTRA_DATA), null);
                }
             catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return u;
        } else {
            return u;
        }       
    }

    @Override
    public boolean addConnection(String userId1, String userId2) {
//        List<User> to_ret = new ArrayList<User>();
      String[] userKeys = { KEY_USER_1, KEY_USER_2};
      String[] userValues = { userId1, userId2};
      JSONObject userJson = null;
      try {
          userJson = createJsonFromParams(userKeys, userValues);
      } catch (JSONException e) {
          e.printStackTrace();
      }
      StringEntity entity = createJSONRequestForRegister(userKeys, userValues);
      HttpResponse response = callApi(ADD_CONNECTION, getBasicHeaders(), HttpRequestType.get, entity, false);

      if (response.getStatusLine().getStatusCode() == 200) {
          
          return true;
      } else {
          return false;
      }       
        
    }


    @Override
    public boolean registerUser(String userId, String name, String category, String imgRaw, String extraData) {
        String[] userKeys = { KEY_USER_ID, KEY_NAME, KEY_USER_TYPE, KEY_IMG, KEY_EXTRA_DATA};
        String[] userValues = { userId, name, category, imgRaw, extraData};
        JSONObject userJson = null;
        try {
            userJson = createJsonFromParams(userKeys, userValues);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        StringEntity entity = createJSONRequestForRegister(userKeys, userValues);
        HttpResponse response = callApi(ADD_USER, getBasicHeaders(), HttpRequestType.post, entity, false);

        if (response.getStatusLine().getStatusCode() == 200) {
            return true;
        } else {
            return false;
        }
}



}
