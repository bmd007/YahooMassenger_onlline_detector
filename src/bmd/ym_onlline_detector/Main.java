package bmd.ym_onlline_detector;




import org.jsoup.select.Evaluator.Id;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;



@SuppressLint("NewApi") public class Main extends Activity {

	Context this_context = this;
	
	
	
	boolean isStarted = false;
	
	boolean isIDchangalbe = true;
	
	static String ID="aftab938";
	
	public boolean is_Running(){

		ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
			String serv = service.service.getClassName();
			if(serv.equals("bmd.ym_onlline_detector_service"))return true;   
		}
		return false;
		
	}
	
	TextView status;
	 Button setID;
	 EditText IdText;
	 
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        	setContentView(R.layout.main);

        	status = (TextView)findViewById(R.id.status);
        	
        	IdText = (EditText)findViewById(R.id.iD);
        	
        	
        setID = (Button)findViewById(R.id.set_id);
        
        
        setID.setClickable(isIDchangalbe);
        setID.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				ID=IdText.getText().toString();
			}
		});
        	
        
        	
        	
        	
        Button sb = (Button)findViewById(R.id.start_service);
        
        sb.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				if(!isStarted && !is_Running())
				{	
					startService(new Intent(getApplicationContext(), service.class));
					isStarted = true;
					status.setText("service in running");
					isIDchangalbe=false;
			        setID.setClickable(isIDchangalbe);
				}
			}
		});
        
        
        
        
        Button sb2 = (Button)findViewById(R.id.stop_service);   
        
        sb2.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				{	
					stopService(new Intent(getApplicationContext(), service.class));
					status.setText("service in not running");
					isStarted=false;
					isIDchangalbe=true;
			        setID.setClickable(isIDchangalbe);
				}
			}
		});
     
        
    }
}
