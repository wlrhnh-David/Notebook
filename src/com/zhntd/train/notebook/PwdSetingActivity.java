package com.zhntd.train.notebook;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.zhntd.train.notebook.utils.Utils;

public class PwdSetingActivity extends Activity implements OnClickListener {

	private EditText passEditText;
	private EditText passAgainEditText;
	private Button cancelButton;
	private Button saveButton;

	private String password;
	private boolean isPassProtected = false;
	private SharedPreferences pwdPreferences;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		pwdPreferences = getSharedPreferences(Utils.PASS_SHARE_FILE, 0);
		password = pwdPreferences.getString(Utils.PASSWORD, "");
		isPassProtected = pwdPreferences.getBoolean(Utils.IS_PASS_PROTECT,
				false);

		// Judge whether exist password, if exist, show dialog and input old password.
		if (isPassProtected && !TextUtils.isEmpty(password)) {
			final AlertDialog.Builder builder = new AlertDialog.Builder(this);
			LayoutInflater inflater = getLayoutInflater();
			View view = inflater.inflate(R.layout.pass, null);
			final EditText editText = (EditText) view
					.findViewById(R.id.edit_Pass);
			builder.setTitle(R.string.pass_set_title);
			builder.setView(view);
			builder.setPositiveButton(R.string.del_dialog_but_ok,
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							if (editText.getText().toString().trim()
									.equals(password)) {
								setContentView(R.layout.pass_set);
								initView();
							} else {
								showToast(R.string.pass_login_invalid);
							}
						}
					});
			builder.setNegativeButton(R.string.pass_but_cancel,
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							builder.create().dismiss();
							finish();
						}
					}).setOnKeyListener(new OnKeyListener() {
						@Override
						public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
							if (KeyEvent.KEYCODE_BACK == keyCode) {
								Log.e("", "-----------------");
								dialog.dismiss();
								PwdSetingActivity.this.finish();
							}
							return false;
						}
					});
			if(!PwdSetingActivity.this.isFinishing())
				builder.create().show();
		} else {
			setContentView(R.layout.pass_set);
			initView();
		}

	}

	private void initView() {
		passEditText = (EditText) findViewById(R.id.edit_pass_set);
		passAgainEditText = (EditText) findViewById(R.id.edit_pass_set_again);

		cancelButton = (Button) findViewById(R.id.but_set_cancel);
		saveButton = (Button) findViewById(R.id.but_set_save);

		cancelButton.setOnClickListener(this);
		saveButton.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.but_set_save:
			savePassword();
			break;

		case R.id.but_set_cancel:
			finish();
			break;

		default:
			break;
		}
	}

	private void savePassword() {
		SharedPreferences.Editor editor = pwdPreferences.edit();
		if (!TextUtils.isEmpty(passEditText.getText())) {
			if (passEditText.getText().toString().trim()
					.equals(passAgainEditText.getText().toString().trim())) {
				editor.putBoolean(Utils.IS_PASS_PROTECT, true);
				editor.putString(Utils.PASSWORD, passAgainEditText.getText()
						.toString().trim());
				editor.commit();
				showToast(R.string.pass_set_succ);
				finish();
			} else {
				reset();
				showToast(R.string.pass_set_not_same);
			}
		} else {
			showToast(R.string.pass_set_is_null);
		}

	}

	private void reset() {
		passEditText.setText("");
		passAgainEditText.setText("");
	}

	private void showToast(int id) {
	    Toast.makeText(this, id, Toast.LENGTH_SHORT).show();
	}
	
}
