package com.zhntd.train.notebook.provider;

import java.util.HashMap;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import com.zhntd.train.notebook.db.DBHelper;
import com.zhntd.train.notebook.utils.NotebookMetaData;
import com.zhntd.train.notebook.utils.NotebookMetaData.NotebookRecord;

public class NotebookProvider extends ContentProvider {

	public static final UriMatcher uriMatcher;
	public static final int RECORD_COLLECTION = 1;
	public static final int RECORD_SIGNLE = 2;
	private DBHelper helper;
	private SQLiteDatabase db;

	static {
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI(NotebookMetaData.AUTHORIY, "records",
				RECORD_COLLECTION);
		uriMatcher
				.addURI(NotebookMetaData.AUTHORIY, "records/#", RECORD_SIGNLE);
	}

	public static HashMap<String, String> recordsHashMap = null;

	static {
		recordsHashMap = new HashMap<String, String>();
		recordsHashMap.put(NotebookMetaData.NotebookRecord._ID,
				NotebookMetaData.NotebookRecord._ID);
		recordsHashMap.put(NotebookMetaData.NotebookRecord.TBTITLE,
				NotebookMetaData.NotebookRecord.TBTITLE);
		recordsHashMap.put(NotebookMetaData.NotebookRecord.TBCONTENT,
				NotebookMetaData.NotebookRecord.TBCONTENT);
		recordsHashMap.put(NotebookMetaData.NotebookRecord.TBEMOTION,
				NotebookMetaData.NotebookRecord.TBEMOTION);
		recordsHashMap.put(NotebookMetaData.NotebookRecord.TBDATESTRING,
				NotebookMetaData.NotebookRecord.TBDATESTRING);
		recordsHashMap.put(NotebookMetaData.NotebookRecord.TBDATELONG,
				NotebookMetaData.NotebookRecord.TBDATELONG);
		recordsHashMap.put(NotebookMetaData.NotebookRecord.TBSOUNDURI,
				NotebookMetaData.NotebookRecord.TBSOUNDURI);
		recordsHashMap.put(NotebookMetaData.NotebookRecord.TBVIDEOURI,
				NotebookMetaData.NotebookRecord.TBVIDEOURI);

	}

	@Override
	public String getType(Uri uri) {
		switch (uriMatcher.match(uri)) {
		case RECORD_COLLECTION:
			return NotebookMetaData.NotebookRecord.CONTENT_TYPE;

		case RECORD_SIGNLE:
			return NotebookMetaData.NotebookRecord.CONTENT_TYPE_ITEM;

		default:
			return null;
		}
	}

	@Override
	public boolean onCreate() {
		helper = new DBHelper(getContext(), NotebookMetaData.DBNAME, null,
				NotebookMetaData.DBVERSION);
		return true;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		db = helper.getWritableDatabase();
		long rowId = db.insert(NotebookMetaData.TBNAME, null, values);
		if (rowId > 0) {
			Uri newUri = ContentUris.withAppendedId(uri, rowId);
			getContext().getContentResolver().notifyChange(newUri, null);
			return newUri;
		}

		return null;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		db = helper.getWritableDatabase();
		String where = NotebookRecord._ID + "=" + uri.getPathSegments().get(1);
		int id = db.delete(NotebookMetaData.TBNAME, where, selectionArgs);
		getContext().getContentResolver().notifyChange(uri, null);
		return id;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
		switch (uriMatcher.match(uri)) {
		case RECORD_COLLECTION:
			builder.setTables(NotebookMetaData.TBNAME);
			builder.setProjectionMap(recordsHashMap);
			break;

		case RECORD_SIGNLE:
			builder.setTables(NotebookMetaData.TBNAME);
			builder.setProjectionMap(recordsHashMap);
			builder.appendWhere(NotebookMetaData.NotebookRecord._ID + "="
					+ uri.getPathSegments().get(1));
			break;

		default:
			builder.setTables(NotebookMetaData.TBNAME);
			builder.setProjectionMap(recordsHashMap);
			break;
		}

		String orderBy;
		if (TextUtils.isEmpty(sortOrder)) {
			orderBy = NotebookMetaData.NotebookRecord.ORDERBY;
		} else {
			orderBy = sortOrder;
		}
		db = helper.getWritableDatabase();
		Cursor cursor = builder.query(db, projection, selection, selectionArgs,
				null, null, orderBy);
		return cursor;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		db = helper.getWritableDatabase();
		int count = db.update(NotebookMetaData.TBNAME, values, selection,
				selectionArgs);
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

}
