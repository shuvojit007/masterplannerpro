package com.cruxbd.master_planner_pro.view.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.ImageView;



import com.cruxbd.master_planner_pro.R;
import com.squareup.picasso.Picasso;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

import br.com.felix.imagezoom.ImageZoom;


public class PaintList extends AppCompatActivity {

    RecyclerView paintrec;
    List<String> mPlist;
    Toolbar mToolbar;
    Context cn;
    private final int STORAGE_PERMISSIONS = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paint_list);
        cn = this;
        init();

    }

    private void init() {
        mToolbar = findViewById(R.id.pltoolbar);
        mToolbar.setTitle("Drawing");
        mToolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mPlist = new ArrayList<>();
        paintrec = findViewById(R.id.paintrec);
     //   paintrec.setLayoutManager(new GridLayoutManager(cn, 2));
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(3, LinearLayoutManager.VERTICAL);
        paintrec.setLayoutManager(staggeredGridLayoutManager);


    }

    private void requestPermissions(int option) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(PaintList.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(PaintList.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(PaintList.this,
                        new String[]{
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        STORAGE_PERMISSIONS);
            } else {

                LoadImage();
            }
        } else {

            LoadImage();

        }
    }

    public void LoadImage() {

        final File directory = new File(Environment.getExternalStorageDirectory(), "Crux");
        if (!directory.exists()) {
            directory.mkdirs();
        }
        File[] files = directory.listFiles();
        for (int i = 0; i < files.length; i++) {
            File imgFile = new File(Environment.getExternalStorageDirectory() + "/Crux/" + files[i].getName());
            if (imgFile.exists()) {
                mPlist.add(imgFile.getAbsolutePath().toString());
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath().toString()); //this is the bitmap for the image

            }

        }
    }


    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder>{

         public List<String>Plist;
            public Context cn ;

        public MyAdapter(List<String> plist, Context cn) {
            Plist = plist;
            this.cn = cn;
        }

        @NonNull
        @Override
        public MyAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(cn);
            View itemView = inflater.inflate(R.layout.paint_list_rec, parent, false);
         ViewHolder viewHolder = new ViewHolder(itemView);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder( MyAdapter.ViewHolder holder, int position) {

            Bitmap myBitmap = BitmapFactory.decodeFile(Plist.get(position));
            holder.img.setImageBitmap(myBitmap);


            holder.img.setOnLongClickListener(v -> {
               File file = new File(Plist.get(position));
                if (file.exists()) {
                    LayoutInflater inflater = LayoutInflater.from(cn);
                    AlertDialog.Builder builder = new AlertDialog.Builder(cn);
                    final View dview = inflater.inflate(R.layout.remove_paint,null);
                    builder.setView(dview);
                    final AlertDialog dialog= builder.create();
                    final Button cancel = dview.findViewById(R.id.paint_btn_cancel);
                    final Button yes = dview.findViewById(R.id.paint_btn_remove);
                    yes.setOnClickListener(v12 -> {
                        file.delete();
                        Plist.clear();
                        LoadImage();
                        notifyDataSetChanged();
                        dialog.dismiss();
                    });

                    cancel.setOnClickListener(v1 -> dialog.dismiss());
                    dialog.show();
                }
                return false;
            });

        }

        @Override
        public int getItemCount() {
            return Plist.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            ImageZoom img;
            View mView;
            public ViewHolder(View itemView) {
                super(itemView);

                img =itemView.findViewById(R.id.paintlistimage);
                mView = itemView;

            }
        }


    }

    @Override
    protected void onStart() {
        super.onStart();
        mPlist.clear();
        requestPermissions(0);
        paintrec.setAdapter(new MyAdapter(mPlist,cn));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.masterplanner_create_projcet, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.mpcreate) {
            startActivity(new Intent(cn,PaintView.class));
        }

        return super.onOptionsItemSelected(item);
    }
}

