package com.zhntd.train.notebook;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.AsyncQueryHandler;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio.Media;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.zhntd.train.notebook.utils.NotebookMetaData;
import com.zhntd.train.notebook.utils.Utils;

import java.lang.ref.WeakReference;

public class EditActivity extends Activity implements OnClickListener {
	
	private static final int SOUND_REQUEST_CODE = 1;
	private static final int VIDEO_REQUEST_CODE = 2;
	private static final int TOKEN = 2102;

	private Button saveButton;
	private Button cancelButton;
	private EditText titleEditText;
	private EditText contentEditText;
	private TextView soundTextView;
	private TextView videoTextView;
	private RatingBar emotionRatingBar;
	private ImageButton soundImageButton;
	private ImageButton videoImageButton;

	private boolean isEdit = false;
	private boolean hasSound = true;
	private boolean hasVideo = true;
	private InsertAndUpdateHandler dataHandler;

	private int _id;
	private Uri newSoundUri;
	private Uri newVideoUri;
	private String soundUri;
	private String videoUri;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit);

		titleEditText = (EditText) findViewById(R.id.entry_edit_title);
		contentEditText = (EditText) findViewById(R.id.entry_edit_content);
		emotionRatingBar = (RatingBar) findViewById(R.id.entry_ratingbar_emotion);

		saveButton = (Button) findViewById(R.id.entry_but_save);
		cancelButton = (Button) findViewById(R.id.entry_but_cancel);
		soundImageButton = (ImageButton) findViewById(R.id.entry_but_sound);
		videoImageButton = (ImageButton) findViewById(R.id.entry_but_video);
		soundTextView = (TextView) findViewById(R.id.entry_but_sound_text);
		videoTextView = (TextView) findViewById(R.id.entry_but_video_text);

		soundImageButton.setOnClickListener(this);
		videoImageButton.setOnClickListener(this);
		saveButton.setOnClickListener(this);
		cancelButton.setOnClickListener(this);

		dataHandler = new InsertAndUpdateHandler(this);

		Intent intent = getIntent();
		if (intent != null && Utils.INTENT_EDIT.equals(intent.getAction())) {
			isEdit = true;
			setTitle(R.string.edit_page);
			initView(intent);
		} else {
			setTitle(R.string.new_page);
		}
	}

	private void initView(Intent intent) {
		Bundle bundle = intent.getExtras();
		_id = bundle.getInt(NotebookMetaData.NotebookRecord._ID);
		titleEditText.setText(bundle
				.getString(NotebookMetaData.NotebookRecord.TBTITLE));
		emotionRatingBar.setRating(bundle
				.getFloat(NotebookMetaData.NotebookRecord.TBEMOTION));
		contentEditText.setText(bundle
				.getString(NotebookMetaData.NotebookRecord.TBCONTENT));
		soundUri = bundle.getString(NotebookMetaData.NotebookRecord.TBSOUNDURI);
		videoUri = bundle.getString(NotebookMetaData.NotebookRecord.TBVIDEOURI);

		if (TextUtils.isEmpty(soundUri)) {
			hasSound = false;
		} else {
			soundTextView.setText(R.string.entry_but_sound_text);
		}

		if (TextUtils.isEmpty(videoUri)) {
			hasVideo = false;
		} else {
			videoTextView.setText(R.string.entry_but_video_text);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.entry_but_cancel:
			finish();
			break;

		case R.id.entry_but_save:
			if (isEdit) {
				saveEditRecord();
			} else {
				saveNewRecord();
			}
			break;

		case R.id.entry_but_sound:
			Intent intentSound = new Intent(Media.RECORD_SOUND_ACTION);
			if (hasSound) {
					showAlertDialog(Utils.SOUND, intentSound);
				} else {
					startActivityForResult(intentSound, SOUND_REQUEST_CODE);
				}
			break;

		case R.id.entry_but_video:
			Intent intentVideo = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
				if (hasVideo) {
					showAlertDialog(Utils.VIDEO, intentVideo);
				} else {
					startActivityForResult(intentVideo, VIDEO_REQUEST_CODE);
				}
			break;

		default:
			break;
		}
	}

	private void showAlertDialog(int id, Intent i) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		final Intent intent = i;

		if (id == Utils.SOUND) {
			builder.setTitle(R.string.edit_sound_delete_title);
			builder.setMessage(R.string.edit_sound_delete_content);
			builder.setPositiveButton(R.string.edit_but_delete_sure,
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							startActivityForResult(intent, SOUND_REQUEST_CODE);
						}
					});
			builder.setNegativeButton(R.string.edit_but_delete_cancel, null);
		} else if (id == Utils.VIDEO) {
			builder.setTitle(R.string.edit_video_delete_title);
			builder.setMessage(R.string.edit_video_delete_content);
			builder.setPositiveButton(R.string.edit_but_delete_sure,
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							startActivityForResult(intent, VIDEO_REQUEST_CODE);
						}
					});
			builder.setNegativeButton(R.string.edit_but_delete_cancel, null);
		}
		builder.create().show();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case SOUND_REQUEST_CODE:
			if (data != null) {
				newSoundUri = data.getData();
				if (newSoundUri != null) {
					hasSound = true;
					soundTextView.setText(R.string.entry_but_sound_text);
				}
			}
			break;

		case VIDEO_REQUEST_CODE:
			if (data != null) {
				newVideoUri = data.getData();
				if (newVideoUri != null) {
					hasVideo = true;
					videoTextView.setText(R.string.entry_but_video_text);
				}
			}
			break;

		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void saveNewRecord() {
		if (!isInvalid()) {
			dataHandler.startInsert(TOKEN, null,
					NotebookMetaData.NotebookRecord.CONTENT_URI,
					getContentValues());
		} else {
			showToast(R.string.edit_save_invalid);
		}
	}

	private void saveEditRecord() {
		if (!isInvalid()) {
			dataHandler.startUpdate(TOKEN, null, NotebookMetaData.NotebookRecord.CONTENT_URI, 
					getContentValues(), NotebookMetaData.NotebookRecord._ID
					+ " =?", new String[] { _id + "" });
		} else {
			Toast.makeText(this, R.string.edit_save_invalid, Toast.LENGTH_SHORT)
					.show();
		}
	}

	private ContentValues getContentValues() {

		ContentValues values = new ContentValues();

		String time = (String) DateFormat.format(Utils.DATE_FORMAT,
				System.currentTimeMillis());

		values.put(NotebookMetaData.NotebookRecord.TBTITLE, titleEditText
				.getText().toString());
		values.put(NotebookMetaData.NotebookRecord.TBCONTENT, contentEditText
				.getText().toString());
		values.put(NotebookMetaData.NotebookRecord.TBEMOTION,
				emotionRatingBar.getRating());
		values.put(NotebookMetaData.NotebookRecord.TBDATESTRING, time);
		values.put(NotebookMetaData.NotebookRecord.TBDATELONG,
				System.currentTimeMillis());

		if (newSoundUri != null && !TextUtils.isEmpty(newSoundUri.toString())) {
			values.put(NotebookMetaData.NotebookRecord.TBSOUNDURI,
					newSoundUri.toString());
		}

		if (newVideoUri != null && !TextUtils.isEmpty(newVideoUri.toString())) {
			values.put(NotebookMetaData.NotebookRecord.TBVIDEOURI,
					newVideoUri.toString());
		}

		return values;
	}

	private boolean isInvalid() {
		return TextUtils.isEmpty(titleEditText.getText())
				&& TextUtils.isEmpty(contentEditText.getText());
	}

	private class InsertAndUpdateHandler extends AsyncQueryHandler {

		protected final WeakReference<EditActivity> mActivity;

		public InsertAndUpdateHandler(Context context) {
			super(context.getContentResolver());
			mActivity = new WeakReference<EditActivity>((EditActivity) context);
		}

		@Override
		protected void onInsertComplete(int token, Object cookie, Uri rowUri) {
			EditActivity activity = mActivity.get();
			if (rowUri != null) {
				activity.showToast(R.string.entry_save_success);
				finish();
			} else {
				activity.showToast(R.string.entry_save_fail);
			}
			super.onInsertComplete(token, cookie, rowUri);
		}

		@Override
		protected void onUpdateComplete(int token, Object cookie, int count) {
			EditActivity activity = mActivity.get();
			if (count > 0) {
				activity.showToast(R.string.entry_save_success);
				finish();
			} else {
				activity.showToast(R.string.entry_save_fail);
			}
			super.onUpdateComplete(token, cookie, count);
		}

	}
	
	private void showToast(int id) {
		Toast.makeText(this, id, Toast.LENGTH_SHORT).show();
	}
}
