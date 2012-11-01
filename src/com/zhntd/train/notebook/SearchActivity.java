package com.zhntd.train.notebook;

import java.lang.ref.WeakReference;

import android.app.Activity;
import android.content.AsyncQueryHandler;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.AdapterView.OnItemClickListener;

import com.zhntd.train.notebook.utils.NotebookMetaData;
import com.zhntd.train.notebook.utils.NotebookMetaData.NotebookRecord;

public class SearchActivity extends Activity {
	
	private static final int TOKEN = 3102;
	
	private EditText searchEditText;
	private ListView searchListView;
	private QueryHandler queryHandler;
	private SimpleCursorAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.search);

		queryHandler = new QueryHandler(this);
		searchEditText = (EditText) findViewById(R.id.searchEdit);
		searchListView = (ListView) findViewById(R.id.searchListView);

		searchEditText.addTextChangedListener(watcher);

		setOnItemClickListener();
		startQuery("");
	}

	private void setOnItemClickListener() {
		searchListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				Intent intent = new Intent(SearchActivity.this,
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
		bundle.putInt(NotebookMetaData.NotebookRecord._ID, cursor
				.getInt(NotebookRecord.TBID_INDEX));
		bundle.putString(NotebookMetaData.NotebookRecord.TBTITLE, cursor
				.getString(NotebookRecord.TBTITLE_INDEX));
		bundle.putString(NotebookMetaData.NotebookRecord.TBCONTENT, cursor
				.getString(NotebookRecord.TBCONTENT_INDEX));
		bundle.putFloat(NotebookMetaData.NotebookRecord.TBEMOTION, cursor
				.getFloat(NotebookRecord.TBEMOTION_INDEX));
		bundle.putString(NotebookMetaData.NotebookRecord.TBDATESTRING, cursor
				.getString(NotebookRecord.TBDATESTRING_INDEX));
		bundle.putLong(NotebookMetaData.NotebookRecord.TBDATELONG, cursor
				.getLong(NotebookRecord.TBDATELONG_INDEX));
		bundle.putString(NotebookMetaData.NotebookRecord.TBSOUNDURI, cursor
				.getString(NotebookRecord.TBSOUNDURI_INDEX));
		bundle.putString(NotebookMetaData.NotebookRecord.TBVIDEOURI, cursor
				.getString(NotebookRecord.TBVIDEOURI_INDEX));
		return bundle;
	}

	TextWatcher watcher = new TextWatcher() {

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}

		@Override
		public void afterTextChanged(Editable s) {
			startQuery(s.toString());
		}
	};

	private void setAdapter(Cursor cursor) {
		if (cursor != null) {
			adapter = new SimpleCursorAdapter(this,
					android.R.layout.simple_list_item_2, cursor, new String[] {
							NotebookMetaData.NotebookRecord.TBTITLE,
							NotebookMetaData.NotebookRecord.TBDATESTRING },
					new int[] { android.R.id.text1, android.R.id.text2 });
			searchListView.setAdapter(adapter);
		} else {
			searchListView.setAdapter(null);

		}
	}

	private class QueryHandler extends AsyncQueryHandler {
		protected final WeakReference<SearchActivity> mActivity;
		private boolean isFirst = true;;

		public QueryHandler(Context context) {
			super(context.getContentResolver());
			mActivity = new WeakReference<SearchActivity>(
					(SearchActivity) context);
		}

		@Override
		protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
			final SearchActivity activity = mActivity.get();
			if (activity != null && !activity.isFinishing()) {
				if (cursor != null) {
					if (isFirst) {
						activity.setAdapter(cursor);
						isFirst = false;
					}
					if (cursor.getCount() > 0) {
						activity.adapter.changeCursor(cursor);
					} else {
						activity.adapter.changeCursor(null);
						cursor.close();
					}
				}
				super.onQueryComplete(token, cookie, cursor);
			}
		}
	}

	private void startQuery(String condition) {
		String searchCondition = NotebookRecord.TBTITLE + " like '%"
				+ condition + "%'" + " or " + NotebookRecord.TBCONTENT
				+ " like '%" + condition + "%'";
		queryHandler.startQuery(TOKEN, null,
				NotebookMetaData.NotebookRecord.CONTENT_URI,
				NotebookRecord.PROJECTION, searchCondition, null, null);
	}

}
