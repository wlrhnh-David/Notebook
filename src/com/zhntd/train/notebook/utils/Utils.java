package com.zhntd.train.notebook.utils;

public class Utils {
	
	// Uri for start New Activity and Edit Activity
	public static final String INTENT_NEW = "com.zhntd.notebook.new";
	public static final String INTENT_EDIT = "com.zhntd.notebook.edit";
	
	// Path of backup file
	public static final String PATH_FOLDER = "/sdcard/minibook";
	public static final String PATH = "/sdcard/minibook/backup";
	
	// Separator
	public static final String SEPERATOR = "###";
	public static final String REPLACER = "null";
	
	// Date format and media file name format
	public static final String DATE_FORMAT = "h:mm AA. MMM dd, yyyy";
	public static final String DATE_NAME_MEDIA = "MM/dd/yyyy-hh:mm:ss";
	
	// Password shared preference
	public static final String PASS_SHARE_FILE = "password_file";
	public static final String IS_PASS_PROTECT = "isPasswordProcted";
	public static final String PASSWORD = "password";
	
	
	public static final int SOUND = 101;
	public static final int VIDEO = 102;
	public static final int DELETE = 103;
	public static final int PWD_SET = 104;
	
	public static final int HANDLER_DATA_CHANGED = 105;
	public static final int HANDLER_SHOW_TOAST = 106;
	
	public static final int THREAD_BACKUP = 201;
	public static final int THREAD_RESTORE = 202;
	
	public static final int RESTORE_UPDATE = 203;
	public static final int RESTORE_INSERT = 204;
	
}
