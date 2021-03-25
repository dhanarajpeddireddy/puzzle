package com.dana.puzzle;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.dana.puzzle.game.Constants;
import com.dana.puzzle.game.PuzzleActivity;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements ImageAdapter.IcallBack {
    static final int REQUEST_PERMISSION_READ_EXTERNAL_STORAGE = 3;
    static final int REQUEST_IMAGE_GALLERY = 4;

    String[] files;

    GridView grid;

    ImageAdapter imageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

        AssetManager am = getAssets();
        try {
            files  = am.list(Constants.ASSET_FOLDER_NAME);
            imageAdapter.updatelist(files);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void init() {
        grid = findViewById(R.id.grid);
        imageAdapter=new ImageAdapter(null,this,this);
        grid.setAdapter(imageAdapter);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_GALLERY && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            Intent intent = new Intent(this, PuzzleActivity.class);
            intent.putExtra("mCurrentPhotoUri", uri.toString());
            startActivity(intent);
        }
    }

    public void onImageFromGalleryClick(View view) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSION_READ_EXTERNAL_STORAGE);
        } else {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, REQUEST_IMAGE_GALLERY);
        }
    }

    @Override
    public void onClickImageAdapterItem(String name) {
        Log.e("name",name);
        Intent intent = new Intent(this, PuzzleActivity.class);
        intent.putExtra(Constants.ASSET_NAME, name);
        startActivity(intent);
    }

}
