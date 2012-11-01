package com.zhntd.train.notebook;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.zhntd.train.notebook.utils.NotebookMetaData;
import com.zhntd.train.notebook.utils.Utils;

public class DetailActivity extends Activity implements OnClickListener {

	private static final int MENU_EDIT = 1001;

	private static final String SPACES = "       ";

	private Bundle bundle;
	private TextView dateTextView;
	private TextView titleTextView;
	private TextView contentTextView;
	private ImageView emotionImageView;
	private ImageButton soundImageButton;
	private ImageButton videoImageButton;

	private int _id;
	private long date_long;
	private float emotion;
	private String title;
	private String content;
	private String date_string;
	private String sounduri;
	private String videouri;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.detail);

		dateTextView = (TextView) findViewById(R.id.detail_date);
		titleTextView = (TextView) findViewById(R.id.detail_title);
		contentTextView = (TextView) findViewById(R.id.detail_content);
		emotionImageView = (ImageView) findViewById(R.id.emotion_imageView);

		soundImageButton = (ImageButton) findViewById(R.id.but_detail_sound);
		videoImageButton = (ImageButton) findViewById(R.id.but_detail_video);
		soundImageButton.setOnClickListener(this);
		videoImageButton.setOnClickListener(this);

		initView();
	}

	private SpannableString replaceByIcon(String str) {
		SpannableString ss = new SpannableString(str);

		Drawable d_b = getResources().getDrawable(R.drawable.b);
		Drawable d_f = getResources().getDrawable(R.drawable.f);
		Drawable d_g = getResources().getDrawable(R.drawable.g);
		Drawable d_m = getResources().getDrawable(R.drawable.m);
		Drawable d_o = getResources().getDrawable(R.drawable.o);
		Drawable d_t = getResources().getDrawable(R.drawable.t);
		Drawable d_v = getResources().getDrawable(R.drawable.v);
		Drawable d_y = getResources().getDrawable(R.drawable.y);
		Drawable d_z = getResources().getDrawable(R.drawable.z);

		d_b.setBounds(0, 0, d_b.getIntrinsicWidth(), d_b.getIntrinsicHeight());
		d_f.setBounds(0, 0, d_f.getIntrinsicWidth(), d_f.getIntrinsicHeight());
		d_g.setBounds(0, 0, d_g.getIntrinsicWidth(), d_g.getIntrinsicHeight());
		d_m.setBounds(0, 0, d_m.getIntrinsicWidth(), d_m.getIntrinsicHeight());
		d_o.setBounds(0, 0, d_o.getIntrinsicWidth(), d_o.getIntrinsicHeight());
		d_t.setBounds(0, 0, d_t.getIntrinsicWidth(), d_t.getIntrinsicHeight());
		d_v.setBounds(0, 0, d_v.getIntrinsicWidth(), d_v.getIntrinsicHeight());
		d_y.setBounds(0, 0, d_y.getIntrinsicWidth(), d_y.getIntrinsicHeight());
		d_z.setBounds(0, 0, d_z.getIntrinsicWidth(), d_z.getIntrinsicHeight());

		ImageSpan span_b = new ImageSpan(d_b, ImageSpan.ALIGN_BOTTOM);
		ImageSpan span_f = new ImageSpan(d_f, ImageSpan.ALIGN_BOTTOM);
		ImageSpan span_g = new ImageSpan(d_g, ImageSpan.ALIGN_BOTTOM);
		ImageSpan span_m = new ImageSpan(d_m, ImageSpan.ALIGN_BOTTOM);
		ImageSpan span_o = new ImageSpan(d_o, ImageSpan.ALIGN_BOTTOM);
		ImageSpan span_t = new ImageSpan(d_t, ImageSpan.ALIGN_BOTTOM);
		ImageSpan span_v = new ImageSpan(d_v, ImageSpan.ALIGN_BOTTOM);
		ImageSpan span_y = new ImageSpan(d_y, ImageSpan.ALIGN_BOTTOM);
		ImageSpan span_z = new ImageSpan(d_z, ImageSpan.ALIGN_BOTTOM);


		if (str.indexOf("b") >= 0) {
			int index_b_start = str.indexOf("b");
			int index_b_end = index_b_start + "b".length();
			ss.setSpan(span_b, index_b_start, index_b_end,
					Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		}

		if (str.indexOf("f") >= 0) {
			int index_f_start = str.indexOf("f");
			int index_f_end = index_f_start + "f".length();
			ss.setSpan(span_f, index_f_start, index_f_end,
					Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		}

		if (str.indexOf("g") >= 0) {
			int index_g_start = str.indexOf("g");
			int index_g_end = index_g_start + "g".length();
			ss.setSpan(span_g, index_g_start, index_g_end,
					Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		}

		if (str.indexOf("m") >= 0) {
			int index_m_start = str.indexOf("m");
			int index_m_end = index_m_start + "m".length();
			ss.setSpan(span_m, index_m_start, index_m_end,
					Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		}

		if (str.indexOf("o") >= 0) {
			int index_o_start = str.indexOf("o");
			int index_o_end = index_o_start + "o".length();
			ss.setSpan(span_o, index_o_start, index_o_end,
					Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		}

		if (str.indexOf("t") >= 0) {
			int index_t_start = str.indexOf("t");
			int index_t_end = index_t_start + "t".length();
			ss.setSpan(span_t, index_t_start, index_t_end,
					Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		}

		if (str.indexOf("v") >= 0) {
			int index_v_start = str.indexOf("v");
			int index_v_end = index_v_start + "v".length();
			ss.setSpan(span_v, index_v_start, index_v_end,
					Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		}

		if (str.indexOf("y") >= 0) {
			int index_y_start = str.indexOf("y");
			int index_y_end = index_y_start + "y".length();
			ss.setSpan(span_y, index_y_start, index_y_end,
					Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		}

		if (str.indexOf("z") >= 0) {
			int index_z_start = str.indexOf("z");
			int index_z_end = index_z_start + "z".length();
			ss.setSpan(span_z, index_z_start, index_z_end,
					Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
		return ss;
	}

	private void initView() {
		Intent intent = getIntent();
		if (intent != null) {
			bundle = intent.getExtras();
			if (bundle != null) {
				_id = bundle.getInt(NotebookMetaData.NotebookRecord._ID);

				title = bundle
						.getString(NotebookMetaData.NotebookRecord.TBTITLE);
				content = bundle
						.getString(NotebookMetaData.NotebookRecord.TBCONTENT);
				emotion = bundle
						.getFloat(NotebookMetaData.NotebookRecord.TBEMOTION);
				date_string = bundle
						.getString(NotebookMetaData.NotebookRecord.TBDATESTRING);
				sounduri = bundle
						.getString(NotebookMetaData.NotebookRecord.TBSOUNDURI);
				videouri = bundle
						.getString(NotebookMetaData.NotebookRecord.TBVIDEOURI);
				titleTextView.setText(SPACES + title);
				dateTextView.setText(SPACES + date_string);
				if (!TextUtils.isEmpty(content)) {
					contentTextView.setText(replaceByIcon(content));
				} else {
					contentTextView.setTextColor(Color.CYAN);
					contentTextView.setText(SPACES + getResources().getString(R.string.edit_no_content));
				}

				setEmotionImage((int) emotion / 1);

				if (!TextUtils.isEmpty(sounduri)) {
					soundImageButton.setVisibility(View.VISIBLE);
				}

				if (!TextUtils.isEmpty(videouri)) {
					videoImageButton.setVisibility(View.VISIBLE);
				}
			} else {
				finish();
			}
		} else {
			Toast.makeText(this, R.string.detail_record_not_exist,
					Toast.LENGTH_SHORT).show();
		}

	}

	private void setEmotionImage(int emotion) {
		switch (emotion) {
		case 0:
			emotionImageView.setImageResource(R.drawable.star_0);
			break;
		case 1:
			emotionImageView.setImageResource(R.drawable.star_1);
			break;
		case 2:
			emotionImageView.setImageResource(R.drawable.star_2);
			break;
		case 3:
			emotionImageView.setImageResource(R.drawable.star_3);
			break;
		case 4:
			emotionImageView.setImageResource(R.drawable.star_4);
			break;
		case 5:
			emotionImageView.setImageResource(R.drawable.star_5);
			break;

		default:
			break;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, MENU_EDIT, 0, R.string.detail_menu_edit);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_EDIT:
			editFromDetail();
			break;

		default:
			break;
		}
		return true;
	}

	private void editFromDetail() {
		Intent intent = new Intent(Utils.INTENT_EDIT);
		intent.putExtras(composeBundle());
		startActivity(intent);
		finish();
	}

	private Bundle composeBundle() {
		bundle.putInt(NotebookMetaData.NotebookRecord._ID, _id);
		bundle.putString(NotebookMetaData.NotebookRecord.TBTITLE, title);
		bundle.putString(NotebookMetaData.NotebookRecord.TBCONTENT, content);
		bundle.putFloat(NotebookMetaData.NotebookRecord.TBEMOTION, emotion);
		bundle.putString(NotebookMetaData.NotebookRecord.TBDATESTRING,
				date_string);
		bundle.putLong(NotebookMetaData.NotebookRecord.TBDATELONG, date_long);
		bundle.putString(NotebookMetaData.NotebookRecord.TBSOUNDURI, sounduri);
		bundle.putString(NotebookMetaData.NotebookRecord.TBVIDEOURI, videouri);
		return bundle;
	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		switch (v.getId()) {
		case R.id.but_detail_sound:
			if (sounduri != null) {
				intent.setData(Uri.parse(sounduri));
				startActivity(intent);
			}
			break;

		case R.id.but_detail_video:
			if (videouri != null) {
				intent.setData(Uri.parse(videouri));
				startActivity(intent);
			}
			break;

		default:
			break;
		}
	}
}
