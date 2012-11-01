package com.zhntd.train.notebook;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.zhntd.train.notebook.utils.Utils;

public class LoginActivity extends Activity implements OnClickListener{
	
	private String password;
	private boolean isPassProtected = false;
	private SharedPreferences preferences;

	private Button cancelButton;
	private Button okButton;
	private EditText loginEditText;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		
		preferences = getSharedPreferences(Utils.PASS_SHARE_FILE, 0);
		password = preferences.getString(Utils.PASSWORD, "");
		isPassProtected = preferences.getBoolean(Utils.IS_PASS_PROTECT, false);
		setTitle(R.string.pass_set_title);

		if (isPassProtected && !TextUtils.isEmpty(password)) {
			setContentView(R.layout.login);
			
			loginEditText = (EditText) findViewById(R.id.editLogin);
			cancelButton = (Button) findViewById(R.id.but_login_cancel);
			okButton = (Button) findViewById(R.id.but_login_ok);
			
			cancelButton.setOnClickListener(this);
			okButton.setOnClickListener(this);
		} else {
			Intent intent = new Intent(LoginActivity.this,
					RecordListActivity.class);
			startActivity(intent);
			finish();
		}
		
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.but_login_ok:
			checkPassword();
			break;

		case R.id.but_login_cancel:
			finish();
			break;

		default:
			break;
		}
	}
	
	private void checkPassword() {
		if (loginEditText.getText().toString().trim().equals(password)) {
			Intent intent = new Intent(LoginActivity.this,
					RecordListActivity.class);
			startActivity(intent);
			finish();
		} else {
			showToast(R.string.pass_login_invalid);
		} 
	}
	
	private void showToast(int id) {
		Toast.makeText(this, id, Toast.LENGTH_SHORT).show();
	}
}