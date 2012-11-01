package com.zhntd.train.notebook.utils;

import android.net.Uri;
import android.provider.BaseColumns;

public class NotebookMetaData {
	public static final String AUTHORIY = "com.zhntd.train.notebook.NotebookProvider";
	public static final String DBNAME = "notebook.db";
	public static final String TBNAME = "records";
	public static final int DBVERSION = 1;

	public static final class NotebookRecord implements BaseColumns {
		public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORIY
				+ "/records");
		public static final Uri CONTENT_ID_URI_BASE
        = Uri.parse("content://" + AUTHORIY + "/records/");
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.notebookprovider.record";
		public static final String CONTENT_TYPE_ITEM = "vnd.android.cursor.item/vnd.notebookprovider.record";
		
		public static final String TBNAME = "records";
	
		public static final String TBTITLE = "title";
		public static final String TBCONTENT = "content";
		public static final String TBEMOTION = "emotion";
		public static final String TBDATESTRING = "date_string";
		public static final String TBDATELONG = "date_long";
		public static final String TBSOUNDURI = "sound_uri";
		public static final String TBVIDEOURI = "video_uri";
		
		public static final String ORDERBY = "_id desc";
		
		public static final String[] PROJECTION = new String[]{
			   _ID,                //0
			   TBTITLE,            //1
			   TBCONTENT,          //2
			   TBEMOTION,          //3
			   TBDATESTRING,       //4
			   TBDATELONG,         //5
			   TBSOUNDURI,         //6
			   TBVIDEOURI          //7
		};
		public static final int TBID_INDEX = 0;
		public static final int TBTITLE_INDEX = 1;
		public static final int TBCONTENT_INDEX = 2;
		public static final int TBEMOTION_INDEX = 3;
		public static final int TBDATESTRING_INDEX = 4;
		public static final int TBDATELONG_INDEX = 5;
		public static final int TBSOUNDURI_INDEX = 6;
		public static final int TBVIDEOURI_INDEX = 7;
		
	}
}
