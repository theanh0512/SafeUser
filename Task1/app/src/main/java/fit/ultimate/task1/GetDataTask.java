package fit.ultimate.task1;

import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pham on 12/24/2015.
 */
class GetDataTask extends AsyncTask<String, Void, List<String>> {
    private final String LOG_TAG = GetDataTask.class.getSimpleName();
    private final Context mContext;
    private String result = "results";
    private String mUrlString = "http://mootask.com/api/taskcontroller/tasks?from=0&max=20";

    GetDataTask(Context context) {
        mContext = context;
        if (!isOnline()) {
            displayNoInternetDialog();
        }
    }

    public static String convertArrayToString(int[] array) {
        String strSeparator = "__,__";
        String str = "";
        if (array != null) {
            for (int i = 0; i < array.length; i++) {
                str = str + array[i];
                if (i < array.length - 1) {
                    str = str + strSeparator;
                }
            }
        }
        return str;
    }

    public static int[] convertStringToArray(String str) {
        String strSeparator = "__,__";
        String[] arr = str.split(strSeparator);
        int[] arrInt = new int[arr.length];
        for (int i = 0; i < arr.length; i++) {
            arrInt[i] = Integer.parseInt(arr[i]);
        }
        return arrInt;
    }

    private void displayNoInternetDialog() {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setTitle(mContext.getString(R.string.alert_title))
                    .setMessage(mContext.getString(R.string.alert_message))
                    .setCancelable(false)
                    .setNegativeButton(mContext.getString(R.string.alert_button), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        } catch (Exception e) {
            Log.d(MainActivity.class.getSimpleName(), "Show Dialog: " + e.getMessage());
        }
    }

    //Check whether there is an internet connection
    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    @Override
    protected List<String> doInBackground(String... strings) {
        List<String> jsonData = new ArrayList<>();
        URL url;
        try {
            url = new URL(mUrlString);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            InputStream is = new BufferedInputStream(urlConnection.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();

            String line;
            try {
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            result = sb.toString();

            //parse json data
            parseJsonData(jsonData);
            Log.i(LOG_TAG, "URL Queried: " + mUrlString);
        } catch (IOException e) {
            e.printStackTrace();
        }


        return jsonData;
    }

    private void parseJsonData(List<String> jsonArray) {
        try {
            JSONArray jsonArrayMain = new JSONArray(result);
            JSONObject jo = jsonArrayMain.getJSONObject(0);
            JSONArray jArrayImage = jo.getJSONArray("images");
            jsonArray.clear();

            int len = jArrayImage.length();
            //if (jArray.length() == 1) len = 2;
            for (int i = 0; i < len; i++) {
                JSONObject json = jArrayImage.getJSONObject(i);
                String caption = json.getString("caption");
                int entityId = json.getInt("entityId");

                MainActivity.arrayListTask.add(new Task(caption, entityId));
            }

            JSONArray jArrayProducts = jo.getJSONArray("products");
            jsonArray.clear();

            len = jArrayProducts.length();
            //if (jArray.length() == 1) len = 2;
            for (int i = 0; i < len; i++) {
                JSONObject json = jArrayProducts.getJSONObject(i);
                String caption = json.getString("description");
                int entityId = json.getInt("entityId");

                MainActivity.arrayListTask.add(new Task(caption, entityId));
            }

            JSONArray jArrayRewards = jo.getJSONArray("rewards");
            jsonArray.clear();

            len = jArrayRewards.length();
            //if (jArray.length() == 1) len = 2;
            for (int i = 0; i < len; i++) {
                JSONObject json = jArrayRewards.getJSONObject(i);
                String caption = json.getString("instruction");
                int entityId = json.getInt("entityId");

                MainActivity.arrayListTask.add(new Task(caption, entityId));
            }

            JSONArray jArrayTags = jo.getJSONArray("tags");
            jsonArray.clear();

            len = jArrayTags.length();
            //if (jArray.length() == 1) len = 2;
            for (int i = 0; i < len; i++) {
                JSONObject json = jArrayTags.getJSONObject(i);
                String caption = json.getString("name");
                int entityId = json.getInt("entityId");

                MainActivity.arrayListTask.add(new Task(caption, entityId));
            }

        } catch (Exception e) {
            // TODO: handle exception
            Log.e("log_tag", "Error Parsing Data " + e.toString());
        }
    }

    @Override
    protected void onPostExecute(List<String> strings) {
        MainActivity.taskAdapter.swapArrayList(MainActivity.arrayListTask);
    }
}
