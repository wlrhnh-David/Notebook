package com.zhntd.train.notebook;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.zhntd.train.notebook.utils.FileOperation;
import com.zhntd.train.notebook.utils.NotebookMetaData;
import com.zhntd.train.notebook.utils.NotebookMetaData.NotebookRecord;
import com.zhntd.train.notebook.utils.Utils;

public class RecordListActivity extends Activity implements OnClickListener {

	private static final int TOKEN = 1102;

	private ListView recordListView;
	private Button newRecordButton;

	private SimpleCursorAdapter adapter;
	private ContentResolver contResolver;
	private Handler myHandler;
	private DataOperationHandler dataOperationHandler;

	private class MyHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case Utils.HANDLER_DATA_CHANGED:
				startQuery();
				break;

			case Utils.HANDLER_SHOW_TOAST:
				showToast(msg.arg1);
				break;

			default:
				break;
			}
			super.handleMessage(msg);
		}
	}

	ContentObserver observer = new ContentObserver(null) {
		public void onChange(boolean selfChange) {
			myHandler.sendEmptyMessage(Utils.HANDLER_DATA_CHANGED);
		};
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		dataOperationHandler = new DataOperationHandler(this);
		myHandler = new MyHandler();
		contResolver = getContentResolver();
		recordListView = (ListView) findViewById(R.id.mainListView);
		newRecordButton = (Button) findViewById(R.id.main_but_new);
		newRecordButton.setOnClickListener(this);
		registerForContextMenu(recordListView);
		startQuery();
		setListItemClick();
		contResolver.registerContentObserver(
				NotebookMetaData.NotebookRecord.CONTENT_URI, true, observer);
	}

	private void setAdapter(Cursor cursor) {
		adapter = new SimpleCursorAdapter(this,
				android.R.layout.simple_list_item_2, cursor, new String[] {
						NotebookMetaData.NotebookRecord.TBTITLE,
						NotebookMetaData.NotebookRecord.TBDATESTRING },
				new int[] { android.R.id.text1, android.R.id.text2 });
		recordListView.setAdapter(adapter);
	}

	private void setListItemClick() {
		recordListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				Intent intent = new Intent(RecordListActivity.this,
						DetailActivity.class);
				Cursor cursor = adapter.getCursor();
				cursor.moveToPosition(position);
				intent.putExtras(composeBundleByCursor(cursor));
				startActivity(intent);
			}
		});
	}

	private Bundle composeBundleByCursor(Cursor cursor) {
		Bundle bundle = new Bundle();
		bundle.putInt(NotebookMetaData.NotebookRecord._ID,
				cursor.getInt(NotebookRecord.TBID_INDEX));
		bundle.putString(NotebookMetaData.NotebookRecord.TBTITLE,
				cursor.getString(NotebookRecord.TBTITLE_INDEX));
		bundle.putString(NotebookMetaData.NotebookRecord.TBCONTENT,
				cursor.getString(NotebookRecord.TBCONTENT_INDEX));
		bundle.putFloat(NotebookMetaData.NotebookRecord.TBEMOTION,
				cursor.getFloat(NotebookRecord.TBEMOTION_INDEX));
		bundle.putString(NotebookMetaData.NotebookRecord.TBDATESTRING,
				cursor.getString(NotebookRecord.TBDATESTRING_INDEX));
		bundle.putLong(NotebookMetaData.NotebookRecord.TBDATELONG,
				cursor.getLong(NotebookRecord.TBDATELONG_INDEX));
		bundle.putString(NotebookMetaData.NotebookRecord.TBSOUNDURI,
				cursor.getString(NotebookRecord.TBSOUNDURI_INDEX));
		bundle.putString(NotebookMetaData.NotebookRecord.TBVIDEOURI,
				cursor.getString(NotebookRecord.TBVIDEOURI_INDEX));
		return bundle;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.main_but_new:
			addNewRecord();
			break;
		default:
			break;
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		MenuInflater inInflater = getMenuInflater();
		inInflater.inflate(R.menu.menucontext, menu);
		super.onCreateContextMenu(menu, v, menuInfo);
	}

	private void addNewRecord() {
		Intent intent = new Intent(Utils.INTENT_NEW);
		startActivity(intent);
	}

	private void editRecord() {
		Intent intent = new Intent(Utils.INTENT_EDIT);
		Cursor cursor = adapter.getCursor();
		intent.putExtras(composeBundleByCursor(cursor));
		startActivity(intent);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_con_edit:
			editRecord();
			break;

		case R.id.menu_con_delete:
			showAlertDialog(Utils.DELETE);
			break;

		default:
			break;
		}
		return super.onContextItemSelected(item);
	}

	private boolean deleteRecordById() {
		Cursor cursor = adapter.getCursor();
		Uri deleteUri = ContentUris.withAppendedId(
				NotebookMetaData.NotebookRecord.CONTENT_URI,
				cursor.getInt(NotebookRecord.TBID_INDEX));
		dataOperationHandler.startDelete(TOKEN, null, deleteUri, null, null);
		return true;
	}

	private void showAlertDialog(int id) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		switch (id) {
		case Utils.DELETE:
			builder.setTitle(R.string.del_dialog_title);
			builder.setMessage(R.string.del_dialog_content);
			builder.setPositiveButton(R.string.del_dialog_but_ok,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							deleteRecordById();
						}
					});
			builder.setNegativeButton(R.string.del_dialog_but_cancel, null);
			builder.create().show();
			break;

		default:
			break;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menuall, menu);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		MenuItem menuBackup = menu.findItem(R.id.menu_backup);
		MenuItem menuSearch = menu.findItem(R.id.menu_search);
		MenuItem menuPwdCancel = menu.findItem(R.id.menu_pass_cancel);
		SharedPreferences preferences = getSharedPreferences(
				Utils.PASS_SHARE_FILE, 0);
		String password = preferences.getString(Utils.PASSWORD, "");
		boolean isPassProtected = preferences.getBoolean(Utils.IS_PASS_PROTECT,
				false);
		if (adapter != null) {
			if (adapter.getCount() > 0) {
				menuBackup.setVisible(true);
				menuSearch.setVisible(true);
			} else {
				menuBackup.setVisible(false);
				menuSearch.setVisible(false);
			}
		} else {
			menuBackup.setVisible(false);
			menuSearch.setVisible(false);
		}

		if (isPassProtected || !TextUtils.isEmpty(password)) {
			menuPwdCancel.setVisible(true);
		} else {
			menuPwdCancel.setVisible(false);
		}
		return true;
	}

	private class BackupThread extends Thread implements OnCancelListener,
			android.content.DialogInterface.OnClickListener {
		Message msg;;
		ProgressDialog proDialog;
		boolean mCanceled = false;
		StringBuffer strBuffer = null;
		FileOperation fileOperation = new FileOperation();

		public BackupThread(ProgressDialog proDialog) {
			this.proDialog = proDialog;
		}

		@Override
		public void run() {
			msg = new Message();
			if (fileOperation.doBackupReady()) {
				Cursor cursor = getContentResolver().query(
						NotebookMetaData.NotebookRecord.CONTENT_URI,
						NotebookRecord.PROJECTION, null, null, null);
				while (!mCanceled && cursor.moveToNext()) {
					proDialog.incrementProgressBy(1);
					fileOperation.backupLine(composeLineByCursor(cursor));
				}
				proDialog.dismiss();
				fileOperation.doWriterClose();

				msg.what = Utils.HANDLER_SHOW_TOAST;
				msg.arg1 = R.string.backup_succ;
				myHandler.sendMessage(msg);
			} else {
				msg.what = Utils.HANDLER_SHOW_TOAST;
				msg.arg1 = R.string.backup_no_ready;
				myHandler.sendMessage(msg);
			}
		}

		private StringBuffer composeLineByCursor(Cursor cursor) {

			strBuffer = new StringBuffer();
			strBuffer.append(cursor.getInt(NotebookRecord.TBID_INDEX)).append(
					Utils.SEPERATOR);

			if (!TextUtils.isEmpty(cursor
					.getString(NotebookRecord.TBTITLE_INDEX))) {
				strBuffer.append(
						cursor.getString(NotebookRecord.TBTITLE_INDEX).replace(
								"\n", "")).append(Utils.SEPERATOR);
			} else {
				strBuffer.append(Utils.REPLACER).append(Utils.SEPERATOR);
			}

			if (!TextUtils.isEmpty(cursor
					.getString(NotebookRecord.TBCONTENT_INDEX))) {
				strBuffer.append(
						cursor.getString(NotebookRecord.TBCONTENT_INDEX)
								.replace("\n", "")).append(Utils.SEPERATOR);
			} else {
				strBuffer.append(Utils.REPLACER).append(Utils.SEPERATOR);
			}

			strBuffer.append(cursor.getString(NotebookRecord.TBEMOTION_INDEX))
					.append(Utils.SEPERATOR);
			strBuffer.append(
					cursor.getString(NotebookRecord.TBDATESTRING_INDEX))
					.append(Utils.SEPERATOR);
			strBuffer.append(cursor.getString(NotebookRecord.TBDATELONG_INDEX))
					.append(Utils.SEPERATOR);

			if (!TextUtils.isEmpty(cursor
					.getString(NotebookRecord.TBSOUNDURI_INDEX))) {
				strBuffer.append(
						cursor.getString(NotebookRecord.TBSOUNDURI_INDEX)
								.replace("\n", "")).append(Utils.SEPERATOR);
			} else {
				strBuffer.append(Utils.REPLACER).append(Utils.SEPERATOR);
			}

			if (!TextUtils.isEmpty(cursor
					.getString(NotebookRecord.TBVIDEOURI_INDEX))) {
				strBuffer.append(
						cursor.getString(NotebookRecord.TBVIDEOURI_INDEX)
								.replace("\n", "")).append("\n");
			} else {
				strBuffer.append(Utils.REPLACER).append("\n");
			}

			return strBuffer;
		}

		@Override
		public void onClick(DialogInterface dialog, int which) {
			if (which == DialogInterface.BUTTON_NEGATIVE) {
				mCanceled = true;
				proDialog.dismiss();
			}
		}

		@Override
		public void onCancel(DialogInterface dialog) {
			mCanceled = true;
		}
	}

	private void showToast(int id) {
		Toast.makeText(this, id, Toast.LENGTH_SHORT).show();
	}

	private ArrayList<String> getIdList() {
		ArrayList<String> idList = new ArrayList<String>();
		Cursor cur = adapter.getCursor();
		if (cur != null) {
			cur.moveToPosition(-1);
			while (cur.moveToNext()) {
				idList.add(String.valueOf(cur
						.getInt(NotebookMetaData.NotebookRecord.TBID_INDEX)));
			}
		}
		return idList;
	}

	private class RestoreThread extends Thread implements OnCancelListener,
			DialogInterface.OnClickListener {
		String[] strs;
		Message msg;
		boolean mCanceled = false;
		ArrayList<String> idList;
		FileOperation fileOperation;
		ProgressDialog proDialog;

		public RestoreThread(ProgressDialog proDialog) {
			this.proDialog = proDialog;
			idList = getIdList();
			fileOperation = new FileOperation();
		}

		@Override
		public void run() {
			msg = new Message();
			if (!fileOperation.isFileExisted()) {
				showToast(R.string.backup_file_not_existed);
			} else {
				fileOperation.doRestoreReady();
				String str = fileOperation.readLine();
				while (!mCanceled && str != null) {
					strs = str.split(Utils.SEPERATOR);
					if (idList.contains(strs[0])) {
						dataOperationHandler.startUpdate(TOKEN, null,
								NotebookMetaData.NotebookRecord.CONTENT_URI,
								getValuesByFile(strs, Utils.RESTORE_UPDATE),
								NotebookMetaData.NotebookRecord._ID + " =?",
								new String[] { strs[0] + "" });
					} else {
						dataOperationHandler.startInsert(TOKEN, null,
								NotebookMetaData.NotebookRecord.CONTENT_URI,
								getValuesByFile(strs, Utils.RESTORE_INSERT));
					}
					str = fileOperation.readLine();
				}
				myHandler.sendEmptyMessage(Utils.HANDLER_DATA_CHANGED);
				msg.what = Utils.HANDLER_SHOW_TOAST;
				msg.arg1 = R.string.restore_succ;
				myHandler.sendMessage(msg);
				proDialog.dismiss();
			}
		}

		private ContentValues getValuesByFile(String[] strs, int id) {
			Log.d("--------strs.count-----", "count = " + strs.length);
			for (int i = 0; i < strs.length; i++) {
				Log.d("-------------", "i = " + i + " s = " + strs[i]);
			}
			ContentValues values = new ContentValues();

			if (id == Utils.RESTORE_INSERT) {
				values.put(NotebookRecord._ID, strs[0]);
			}
			values.put(NotebookRecord.TBTITLE,
					strs[1].equals(Utils.REPLACER) ? "" : strs[1]);
			values.put(NotebookRecord.TBCONTENT,
					strs[2].equals(Utils.REPLACER) ? "" : strs[2]);
			values.put(NotebookRecord.TBEMOTION, strs[3]);
			values.put(NotebookRecord.TBDATESTRING, strs[4]);
			values.put(NotebookRecord.TBDATELONG, strs[5]);
			values.put(NotebookRecord.TBSOUNDURI,
					strs[6].equals(Utils.REPLACER) ? "" : strs[6]);
			values.put(NotebookRecord.TBVIDEOURI,
					strs[7].equals(Utils.REPLACER) ? "" : strs[7]);
			return values;
		}

		@Override
		public void onClick(DialogInterface dialog, int which) {
			if (which == DialogInterface.BUTTON_NEGATIVE) {
				mCanceled = true;
				proDialog.dismiss();
			}
		}

		@Override
		public void onCancel(DialogInterface dialog) {
			mCanceled = true;
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_new:
			addNewRecord();
			return true;

		case R.id.menu_restore:
			startBackupOrRestoreThread(Utils.THREAD_RESTORE);
			return true;

		case R.id.menu_backup:
			startBackupOrRestoreThread(Utils.THREAD_BACKUP);
			return true;

		case R.id.menu_search:
			Intent intent = new Intent(RecordListActivity.this,
					SearchActivity.class);
			startActivity(intent);
			return true;

		case R.id.menu_pass:
			startPwdSetActivity();
			return true;

		case R.id.menu_pass_cancel:
			cancelPwdProtection();
			return true;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void startBackupOrRestoreThread(int index) {
		ProgressDialog proDialog = new ProgressDialog(this);
		switch (index) {
		case Utils.THREAD_BACKUP:
			BackupThread backupThread = new BackupThread(proDialog);
			proDialog.setButton(DialogInterface.BUTTON_NEGATIVE,
					getString(R.string.backup_but_cancel), backupThread);
			proDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			int count = adapter.getCount();
			if (count > 0) {
				proDialog.setMax(count);
			}
			proDialog.show();
			backupThread.start();
			break;

		case Utils.THREAD_RESTORE:
			RestoreThread restoreThread = new RestoreThread(proDialog);
			proDialog.setButton(DialogInterface.BUTTON_NEGATIVE,
					getString(R.string.restore_dialog_but_cancel),
					restoreThread);
			proDialog.setMessage(getString(R.string.restore_dialog_content));
			proDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			proDialog.show();
			restoreThread.start();
			break;

		default:
			break;
		}
	}

	private void cancelPwdProtection() {
		SharedPreferences preferences = getSharedPreferences(
				Utils.PASS_SHARE_FILE, 0);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putBoolean(Utils.IS_PASS_PROTECT, false);
		editor.putString(Utils.PASSWORD, "");
		if (editor.commit()) {
			showToast(R.string.pass_protection_cancel);
		}

	}

	private void startPwdSetActivity() {
		Intent intent = new Intent(RecordListActivity.this,
				PwdSetingActivity.class);
		startActivity(intent);
	}

	@Override
	protected void onDestroy() {
		getContentResolver().unregisterContentObserver(observer);
		super.onDestroy();
	}

	private class DataOperationHandler extends AsyncQueryHandler {

		private boolean isFirst = true;
		private RecordListActivity mActivity;

		public DataOperationHandler(Context context) {
			super(context.getContentResolver());
			this.mActivity = (RecordListActivity) context;
		}

		@Override
		protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
			if (cursor != null) {
				if (isFirst) {
					mActivity.setAdapter(cursor);
					isFirst = false;
				}
				if (cursor.getCount() > 0) {
					mActivity.adapter.changeCursor(cursor);
				} else {
					mActivity.adapter.changeCursor(null);
					cursor.close();
				}
			}
			super.onQueryComplete(token, cookie, cursor);
		}

		@Override
		protected void onDeleteComplete(int token, Object cookie, int result) {
			if (result > 0) {
				mActivity.showToast(R.string.del_success);
			}
			super.onDeleteComplete(token, cookie, result);
		}

	}

	private void startQuery() {
		dataOperationHandler.startQuery(TOKEN, null,
				NotebookMetaData.NotebookRecord.CONTENT_URI,
				NotebookRecord.PROJECTION, null, null, null);
	}

}