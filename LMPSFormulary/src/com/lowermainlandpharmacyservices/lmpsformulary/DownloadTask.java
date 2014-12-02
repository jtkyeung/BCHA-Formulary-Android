package com.lowermainlandpharmacyservices.lmpsformulary;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.PowerManager;
import android.widget.Toast;

public class DownloadTask extends AsyncTask<String, Integer, String> {
	 	private Context context;
	    private PowerManager.WakeLock mWakeLock;
	    private BufferedReader file = null;

	    public DownloadTask(Context context) {
	        this.context = context;
	    }

	    @Override
	    protected String doInBackground(String... sUrl) {
	        InputStream input = null;
	        OutputStream output = null;
	        HttpURLConnection connection = null;
	        try {
	            URL url = new URL(sUrl[0]);
	            connection = (HttpURLConnection) url.openConnection();
	            connection.connect();
	            System.out.println("urlconnection");

	            // expect HTTP 200 OK, so we don't mistakenly save error report
	            // instead of the file
	            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
	                return "Server returned HTTP " + connection.getResponseCode()
	                        + " " + connection.getResponseMessage();
	            }

	            // this will be useful to display download percentage
	            // might be -1: server did not report the length
	            int fileLength = connection.getContentLength();

	            // download the file
	            input = connection.getInputStream();
	            output = context.openFileOutput("test.csv", Context.MODE_PRIVATE);
	            //output = new FileOutputStream(context.getExternalFilesDir(Environment.DIRECTORY_DCIM).getPath());

	            byte data[] = new byte[4096];
	            long total = 0;
	            int count;
	            while ((count = input.read(data)) != -1) {
	                // allow canceling with back button
	                if (isCancelled()) {
	                    input.close();
	                    return null;
	                }
	                total += count;
	                // publishing the progress....
	                if (fileLength > 0) // only if total length is known
	                    publishProgress((int) (total * 100 / fileLength));
	                output.write(data, 0, count);
	            }
	        } catch (Exception e) {
	            return e.toString();
	        } finally {
	            try {
	                if (output != null)
	                    output.close();
	                if (input != null)
	                    input.close();
	            } catch (IOException ignored) {
	            }

	            if (connection != null)
	                connection.disconnect();
	        }
	        return null;
	    	
	    	/*HttpClient httpClient = new DefaultHttpClient();
			HttpContext localContext = new BasicHttpContext();
			HttpGet httpGet = new HttpGet("https://www.dropbox.com/sh/ctdjnxoemlx9hbr/AABotiW6CP_-JrGAh0mw1nkma/formulary.csv?dl=1");
			HttpResponse response;
			
			try {
				response = httpClient.execute(httpGet, localContext);
				file = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				return e.toString();
			} 
			return null;*/
	    }
	    
	    @Override
	    protected void onPreExecute() {
	        super.onPreExecute();
	        // take CPU lock to prevent CPU from going off if the user 
	        // presses the power button during download
	        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
	        mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
	             getClass().getName());
	        mWakeLock.acquire();
	       //mProgressDialog.show();
	    }

	    @Override
	    protected void onProgressUpdate(Integer... progress) {
	        super.onProgressUpdate(progress);
	        // if we get here, length is known, now set indeterminate to false
	        //mProgressDialog.setIndeterminate(false);
	        //mProgressDialog.setMax(100);
	        //mProgressDialog.setProgress(progress[0]);
	    }

	    @Override
	    protected void onPostExecute(String result) {
	        mWakeLock.release();
	        //mProgressDialog.dismiss();
	        if (result != null){
	            Toast.makeText(context,"Download error: "+result, Toast.LENGTH_LONG).show();
	        	System.out.println(result);
	        } else {
	            Toast.makeText(context,"File downloaded", Toast.LENGTH_SHORT).show();
	        }
	    }
	    
	    public BufferedReader getBufferedReader() {
	    	return file;
	    }
}
