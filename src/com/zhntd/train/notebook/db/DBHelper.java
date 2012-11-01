package com.zhntd.train.notebook.db;

import com.zhntd.train.notebook.utils.NotebookMetaData;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
	
	public DBHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		StringBuffer sBuffer = new StringBuffer();
		sBuffer.append("CREATE TABLE ").append(NotebookMetaData.TBNAME)
			   .append(" (")
			   .append(NotebookMetaData.NotebookRecord._ID).append(" INTEGER PRIMARY KEY AUTOINCREMENT, ")
			   .append(NotebookMetaData.NotebookRecord.TBTITLE).append(" TEXT NOT NULL, ")
			   .append(NotebookMetaData.NotebookRecord.TBCONTENT).append(" TEXT NOT NULL, ")
			   .append(NotebookMetaData.NotebookRecord.TBEMOTION).append(" FLOAT DEFAULT 0, ")
			   .append(NotebookMetaData.NotebookRecord.TBDATESTRING).append(" TEXT NOT NULL, ")
			   .append(NotebookMetaData.NotebookRecord.TBDATELONG).append(" LONG NOT NULL, ")
			   .append(NotebookMetaData.NotebookRecord.TBSOUNDURI).append(" TEXT DEFAULT NULL, ")
			   .append(NotebookMetaData.NotebookRecord.TBVIDEOURI).append(" TEXT DEFAULT NULL)");
		db.execSQL(sBuffer.toString());
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + NotebookMetaData.TBNAME);
		onCreate(db);
	}

}
