package ms.android.finalProject;


import java.util.ArrayList;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.telephony.gsm.SmsManager;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
 
public class smsit extends Activity 
{
    Button btnSendSMS;
    EditText txtPhoneNo;
    EditText txtMessage;
    String message;
	private BroadcastReceiver sendReciever;
	private BroadcastReceiver deliverReciever;
	private ArrayList<PendingIntent> sendApii = new ArrayList<PendingIntent>();
	private ArrayList<PendingIntent> devliverApii = new ArrayList<PendingIntent>();
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.smsloc);        
 
        btnSendSMS = (Button) findViewById(R.id.btnSendSMS);
        txtPhoneNo = (EditText) findViewById(R.id.txtPhoneNo);
        txtMessage = (EditText) findViewById(R.id.txtMessage);
        
        //retrieving the data
        Bundle b = getIntent().getExtras();
		message = b.getString("msg_body");
		
		
		txtMessage.setText(message);
 
        btnSendSMS.setOnClickListener(new View.OnClickListener() 
        {
            public void onClick(View v) 
            {                
                String phoneNo = txtPhoneNo.getText().toString();
                String message = txtMessage.getText().toString();                 
                if (phoneNo.length()>0 && message.length()>0)                
                    sendSMS(phoneNo, message);                
                else
                    Toast.makeText(getBaseContext(), 
                        "Please enter both phone number and message.", 
                        Toast.LENGTH_SHORT).show();
            }
        });
        
        sendReciever = new BroadcastReceiver() {
			
			@Override
			public void onReceive(Context context, Intent intent) {
				 switch (getResultCode())
	                {
	                    case Activity.RESULT_OK:
	                        Toast.makeText(getBaseContext(), "SMS sent", 
	                                Toast.LENGTH_SHORT).show();
	                        break;
	                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
	                        Toast.makeText(getBaseContext(), "Generic failure", 
	                                Toast.LENGTH_SHORT).show();
	                        break;
	                    case SmsManager.RESULT_ERROR_NO_SERVICE:
	                        Toast.makeText(getBaseContext(), "No service", 
	                                Toast.LENGTH_SHORT).show();
	                        break;
	                    case SmsManager.RESULT_ERROR_NULL_PDU:
	                        Toast.makeText(getBaseContext(), "Null PDU", 
	                                Toast.LENGTH_SHORT).show();
	                        break;
	                    case SmsManager.RESULT_ERROR_RADIO_OFF:
	                        Toast.makeText(getBaseContext(), "Radio off", 
	                                Toast.LENGTH_SHORT).show();
	                        break;
	                }
				
			}
		};
		
		 deliverReciever = new BroadcastReceiver() {
			
			@Override
			public void onReceive(Context context, Intent intent) {
				 switch (getResultCode())
	                {
	                    case Activity.RESULT_OK:
	                        Toast.makeText(getBaseContext(), "SMS delivered", 
	                                Toast.LENGTH_SHORT).show();
	                        break;
	                    case Activity.RESULT_CANCELED:
	                        Toast.makeText(getBaseContext(), "SMS not delivered", 
	                                Toast.LENGTH_SHORT).show();
	                        break;                        
	                }
				
			}
		};
    }
  //---sends an SMS message to another device---
    private void sendSMS(String phoneNumber, String message)
    {        
		String SENT = "SMS_SENT";
		String DELIVERED = "SMS_DELIVERED";

		PendingIntent sentPI = PendingIntent.getBroadcast(this, 0, new Intent(
				SENT), 0);
		sendApii.add(sentPI);

		PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0,
				new Intent(DELIVERED), 0);

		devliverApii.add(deliveredPI);

		// ---when the SMS has been sent---
		registerReceiver(sendReciever, new IntentFilter(SENT));

		// ---when the SMS has been delivered---
		registerReceiver(deliverReciever, new IntentFilter(DELIVERED));

		SmsManager sms = SmsManager.getDefault();
		// sms.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);
		ArrayList<String> parts = sms.divideMessage(message);
		sms.sendMultipartTextMessage(phoneNumber, null, parts, sendApii,
				devliverApii);
        
    }
    
    
    
    @Override
    public void onBackPressed() {
    	unregisterReceiver(sendReciever);
    	unregisterReceiver(deliverReciever);
    	super.onBackPressed();
    }
}