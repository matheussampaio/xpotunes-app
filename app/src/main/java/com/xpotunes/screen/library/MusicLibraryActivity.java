package com.xpotunes.screen.library;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.xpotunes.R;
import com.xpotunes.utils.Logger;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.api.BackgroundExecutor;

@EActivity(R.layout.activity_music_library)
public class MusicLibraryActivity extends AppCompatActivity {

    private static final int SELECT_MUSIC_RESULT = 101;

    @ViewById(R.id.toolbar)
    Toolbar mToolbar;

    @ViewById(R.id.fab)
    FloatingActionButton mFActionBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_library);
        setSupportActionBar(mToolbar);

        fetchMusicFiles();
    }

    @Override
    protected void onStop() {
        BackgroundExecutor.cancelAll("fetch-music-files", true);
        super.onStop();
    }

    @Background(id="fetch-music-files")
    void fetchMusicFiles() {
        ContentResolver contentResolver = getContentResolver();
        Uri uri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor cursor = contentResolver.query(uri, null, null, null, null);

        if (cursor == null) {
            // query failed, handle error.
            Logger.d("query failed, handle error");

        } else if (!cursor.moveToFirst()) {
            // no media on the device
            Logger.d("no media on the device");

            cursor.close();
        } else {
            int titleColumn = cursor.getColumnIndex(android.provider.MediaStore.Audio.Media.TITLE);
            int idColumn = cursor.getColumnIndex(android.provider.MediaStore.Audio.Media._ID);

            do {
                long thisId = cursor.getLong(idColumn);
                String thisTitle = cursor.getString(titleColumn);

                Logger.d("thisTitle = " + thisTitle);

                // ...process entry...
            } while (cursor.moveToNext());

            Logger.d("MainActivity.selectMusics finisehd");
            cursor.close();
        }
    }

    @Click(R.id.fab)
    void clickActionButton(View view) {
        Intent intent = getIntent();
        setResult(RESULT_OK, intent);
        finish();
    }

    @OnActivityResult(SELECT_MUSIC_RESULT)
    void onResult(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            // do something to add music on listview;
        }
    }
}