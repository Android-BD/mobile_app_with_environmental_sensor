package csee.usf.edu;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;

public class SignUp extends Activity {
	
	String mEmail;
	String mPassword;
	String mPassConfirm;
	
	EditText mEmailView;
	EditText mPassView;
	EditText mPassConView;
	
	private UserRegistrationTask mRegTask = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign_up);
		
		mEmailView = (EditText)findViewById(R.id.signup_email);
		mPassView = (EditText)findViewById(R.id.signup_password);
		mPassConView = (EditText)findViewById(R.id.signup_password_confirm);
		
		
		findViewById(R.id.register_submit).setOnClickListener(
				new View.OnClickListener() 
				{
					@Override
					public void onClick(View view) 
					{
						attemptRegistration();
					}
				});
		
	}
	
	private void attemptRegistration()
	{
		mEmail = mEmailView.getText().toString();
		mPassword = mPassView.getText().toString();
		mPassConfirm = mPassConView.getText().toString();
		
		boolean cancel = false;
		View focusView = null;
		
		if (TextUtils.isEmpty(mPassword)) {
			mPassView.setError(getString(R.string.error_field_required));
			focusView = mPassView;
			cancel = true;
		} else if (mPassword.length() < 4) {
			mPassView.setError(getString(R.string.error_invalid_password));
			focusView = mPassView;
			cancel = true;
		} else if(!mPassword.equals(mPassConfirm)){
			mPassConView.setError(getString(R.string.error_invalid_password));
			focusView = mPassConView;
			cancel = true;
		}
		
		if (TextUtils.isEmpty(mEmail)) {
			mEmailView.setError(getString(R.string.error_field_required));
			focusView = mEmailView;
			cancel = true;
		} else if (!mEmail.contains("@")) {
			mEmailView.setError(getString(R.string.error_invalid_email));
			focusView = mEmailView;
			cancel = true;
		}

		if (cancel) {
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
		} else {
			// Show a progress spinner, and kick off a background task to
			// perform the user login attempt.
			
			//mLoginStatusMessageView.setText(R.string.login_progress_signing_in);
			//showProgress(true);
			
			mRegTask = new UserRegistrationTask();
			mRegTask.execute(mEmail, mPassword);
		}
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.sign_up, menu);
		return true;
	}
	
	public class UserRegistrationTask extends AsyncTask<String, Void, Boolean> 
	{
		/**
		 * The authorization URL to authenticate users
		 */
		public static final String authUrl = "http://shawnhathaway.com/iAmbience/sql/authenticateUser.php";
		
		/**
		 * The registration URL to register users
		 */
		public static final String regUrl = "http://shawnhathaway.com/iAmbience/sql/registerUser.php";
		
		@Override
		protected Boolean doInBackground(String... params) 
		{
			// TODO: attempt authentication against a network service.
			InputStream is = null;
			String result = "";
			String email = params[0];
			String password = params [1];
			
			java.util.ArrayList<NameValuePair> nameValuePairs = new java.util.ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("email",email));
			nameValuePairs.add(new BasicNameValuePair("password", password));
			
			try {
				HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(regUrl);
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity entity = response.getEntity();
                is = entity.getContent();
			} catch (Exception e) {
				Log.e("log", "Error posting to: " + regUrl);
				return false;
			}

            try 
            {
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) 
                {
                    sb.append(line + "\n");
                }
                is.close();

                result = sb.toString();
            } 
            catch (Exception e) 
            {
                Log.e("log", "Error converting following result to string: " + e.toString());
                return false;
            }
            
            result = result.replaceAll("\n", "");
            
            if(result.equals("true"))
            {
        		return true;
            }
            else
            {
            	ParseErrorAndLog(result);
            	return false;
            }
            	
			// TODO: register the new account here.
		}

		private void ParseErrorAndLog(String result) {
			return;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			if (success) {
				Intent homeIntent = new Intent(SignUp.this, HomeActivity.class);
				startActivity(homeIntent);
			} else {
				mPassView
						.setError(getString(R.string.error_incorrect_password));
				mPassView.requestFocus();
			}
		}

		@Override
		protected void onCancelled() {
			//mAuthTask = null;
			//showProgress(false);
		}
	}

}
