package bmd.ym_onlline_detector;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

public class service  extends Service {




	String url = "http://pichak.net/yahoo/img.php?id="+Main.ID+"&on=pichak.net/yahoo/icone/on1.png&off=pichak.net/yahoo/icone/off1.png";

	int isOnline = 0;

	final Context this_context = this;

	Timer timer ;

	TimerTask  task;

	Handler handler = new Handler();

	check_online check1;

	String newEventTitle;

	public void onCreate() {

		super.onCreate();

		Log.d("bmd on create","Service Started");

		isOnline = 0;
		
		timer = new Timer();

		task = new TimerTask() {
			public void run() {
				handler.post(new Runnable() {
					public void run() {

						check1 = new check_online();
						
						if(haveNetworkConnection())
							check1.execute();
						
		
								
					}
				});
			}
		};

	}


	
	public void make_notification(String title,String body){
		Log.d("BMD","Onmake No");

		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this_context);
		mBuilder.setSmallIcon(R.drawable.ic_launcher);
		mBuilder.setContentTitle(title);
		mBuilder.setContentText(body);
		mBuilder.setAutoCancel(true);


		Intent resultIntent = new Intent(this, Main.class);
		android.app.TaskStackBuilder stackBuilder = android.app.TaskStackBuilder.create(this_context);
		stackBuilder.addParentStack(Main.class);

		stackBuilder.addNextIntent(resultIntent);

		android.app.PendingIntent rePendingIntent = stackBuilder.getPendingIntent(0, android.app.PendingIntent.FLAG_UPDATE_CURRENT);

		mBuilder.setContentIntent(rePendingIntent);

		NotificationManager mNotificationManager =
				(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

		mNotificationManager.notify("BMD noti.",111, mBuilder.build());

	}

	public int onStart(Intent intent, int flags, int startId) {
		Log.d("bmd on Start","Service Started");
		return START_STICKY;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		timer.schedule(task,0,60000);
		return super.onStartCommand(intent, flags, startId);
	}


	public void onDestroy() {
		Log.d("bmd destroy","service destroyed");
		super.onDestroy();
		timer.cancel();
		timer.purge();
		this.stopSelf();
	}



	public IBinder onBind(Intent arg0) {
		return null;
	}

	//not working
	//with 2 kind of http connection
	/*class check_online2  extends AsyncTask<Void, String, Void>{

		@Override
		protected Void doInBackground(Void... arg0) {
			System.out.println("d i b");
		
			HttpResponse response = null;
			try {        
			        HttpClient client = new DefaultHttpClient();
			        HttpGet request = new HttpGet();
			        request.setURI(new URI(url));
			        request.addHeader("id","bmd579");
			        request.addHeader("on","pichak.net/yahoo/icone/on1.png");
			        request.addHeader("off","pichak.net/yahoo/icone/off1.png");
			        
			        response= client.execute(request);
			        
			        
			    } catch (URISyntaxException e) {
			        e.printStackTrace();
			     } catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}   
		
			
			
			
			
			URLConnection con = null;
			try {
				con = new URL( url ).openConnection();
			} catch (MalformedURLException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			try {
				con.connect();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			
			
			
			
			try {
				System.out.println(
			response.getFirstHeader("Location")
			+"::"+
		    EntityUtils.toString(response.getEntity())
		    +"::"+
			con.getHeaderField( "Location" )
			+"::"+
			con.getURL()
			);

			} catch (ParseException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} 
			
			return null;
		}
		
		
	}*/
	
	//working  with json
	class check_online extends AsyncTask<Void, Void, Void>	{
		
		
		@Override
		protected Void doInBackground(Void... arg0) {
			try {

			Document doc1 =  Jsoup.connect(url)
			.ignoreContentType(true)
			.userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0")
			.referrer("http://www.google.com")
			.timeout(20000)
			.followRedirects(true)
			.get();
				
				System.out.println(doc1.location());
			
				if(doc1.location().equals("http://pichak.net/yahoo/icone/on1.png"))
					make_notification(Main.ID, "Became Onlineeeee");

				

			}catch (IOException e) {				Log.d("BMD jsoup error",e.getMessage());		}
			return null;
		}

	}


	private boolean haveNetworkConnection() {
		boolean haveConnectedWifi = false;
		boolean haveConnectedMobile = false;

		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo[] netInfo = cm.getAllNetworkInfo();
		for (NetworkInfo ni : netInfo) {
			if (ni.getTypeName().equalsIgnoreCase("WIFI"))
				if (ni.isConnected())
					haveConnectedWifi = true;
			if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
				if (ni.isConnected())
					haveConnectedMobile = true;
		}
		return haveConnectedWifi || haveConnectedMobile;
	}

}