package com.zennenga.cows_mobile_client;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

public class EventCreation extends Activity {
	String tgc = "";
	View temp = null;
	int tries = 0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_event_creation);
		tgc = getIntent().getStringExtra("TGC");
	}

	@Override	
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.event_creation, menu);
		return true;
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent d)	{
		this.tgc = d.getStringExtra("tgc");
		submitHandler(this.temp);
	}
	
	public void backHandler(View v)	{
		int i = 0;
		while (!Utility.deauth())	{
			Utility.deauth();
			if (i > 10) break;
			i++;
		}
		finish();
	}
	
	public void submitHandler(View v)	{
		String response = doEvent(tgc);
		
		if (response == null)	{
			setError("Invalid response from server.");
			return;
		}
		
		String[] pieces = response.split(":", 1);
		
		if (!pieces[0].equals("0"))	{
			switch(Integer.parseInt(pieces[0]))	{
			case -1:
				//Generic error
				setError("Error: " + pieces[1] + " Please retry your submission.");
				return;
			case -2:
				//Failed Auth
				Intent i = new Intent(v.getContext(), CasAuth.class);
				i.putExtra("retryingAuth",true);
				i.putExtra("error", pieces[1]);
				startActivityForResult(i, 1);
				this.temp = v;
				return;
			case -3:
				//Event Error
				setError("Event error: " + pieces[1] + " Please fix this error, and retry your submission.");
				return;
			case -4:
				//cURL error
				this.tries++;
				if (this.tries >= 5)	{
					setError("Network Error: " + pieces[1]);
					return;
				}
				else submitHandler(v);
				return;
			default:
				//Generic Error
				setError("Error: " + pieces[1]);
				return;
			}
		}
		else	{
			setError("");
			this.tries = 0;
			Intent i = new Intent(v.getContext(), DoneOrMore.class);
			i.putExtra("TGC", tgc);	
			startActivity(i);
			finish();
		}
	}
	
	private void setError(String error)	{
		((TextView)findViewById(R.id.error)).setText(error);
		return;
	}

	private String doEvent(String tgc) {
		String getString = "?ticket=" + tgc;
		//TODO: fill out getString
		HttpResponse out = null;
		DefaultHttpClient client = new DefaultHttpClient();
		HttpGet request = new HttpGet("http://128.120.151.3/development/CowsMobileProxy.php" + getString);
		
		try {
			out = client.execute(request);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return out.toString();
	}

}
