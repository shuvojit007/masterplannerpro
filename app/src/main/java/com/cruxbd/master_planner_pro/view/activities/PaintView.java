package com.cruxbd.master_planner_pro.view.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.cruxbd.master_planner_pro.R;
import com.cruxbd.master_planner_pro.utils.DrawAttribsDialog;
import com.cruxbd.master_planner_pro.utils.DrawPackage.enums.DrawingCapture;
import com.cruxbd.master_planner_pro.utils.DrawPackage.enums.DrawingMode;
import com.cruxbd.master_planner_pro.utils.DrawPackage.enums.DrawingTool;
import com.cruxbd.master_planner_pro.utils.DrawPackage.views.DrawView;
import com.cruxbd.master_planner_pro.utils.RequestTextDialog;
import com.cruxbd.master_planner_pro.utils.SaveBitmapDialog;
import com.cruxbd.master_planner_pro.utils.SelectChoiceDialog;
import com.leinardi.android.speeddial.SpeedDialActionItem;
import com.leinardi.android.speeddial.SpeedDialOverlayLayout;
import com.leinardi.android.speeddial.SpeedDialView;

import java.util.Objects;

public class PaintView extends AppCompatActivity {

    // CONSTANTS
    private final int STORAGE_PERMISSIONS = 1000;
    private final int STORAGE_PERMISSIONS2 = 2000;

    // VIEWS
    private Toolbar mToolbar;
    private DrawView mDrawView;
    private MenuItem mMenuItemRedo;
    private MenuItem mMenuItemUndo;

    SpeedDialView speedDialView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paint_view);
        init();
        Listener();

    }

    private void Listener() {
        mDrawView.setOnDrawViewListener(new DrawView.OnDrawViewListener() {
            @Override
            public void onStartDrawing() {
                canUndoRedo();
            }

            @Override
            public void onEndDrawing() {
                canUndoRedo();
            }

            @Override
            public void onClearDrawing() {
                canUndoRedo();
            }

            @Override
            public void onRequestText() {
                RequestTextDialog requestTextDialog =
                        RequestTextDialog.newInstance("");
                requestTextDialog.setOnRequestTextListener(new RequestTextDialog.OnRequestTextListener() {
                    @Override
                    public void onRequestTextConfirmed(String requestedText) {
                        mDrawView.refreshLastText(requestedText);
                    }

                    @Override
                    public void onRequestTextCancelled() {
                        mDrawView.cancelTextRequest();
                    }
                });
                requestTextDialog.show(getSupportFragmentManager(), "requestText");
            }

            @Override
            public void onAllMovesPainted() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        canUndoRedo();

                    }
                }, 300);
            }
        });

    }

    private void init() {
        SpeedDialOverlayLayout overlayLayout = findViewById(R.id.overlay);
        speedDialView = findViewById(R.id.speedDial);
        speedDialView.setOverlayLayout(overlayLayout);
        speedDialView.inflate(R.menu.paint_menu);
        speedDialView.setOnActionSelectedListener(speedDialActionItem -> {
            switch (speedDialActionItem.getId()) {
                case R.id.action_draw_attrs:
                    changeDrawAttribs();
                    return false;
                case R.id.action_draw_delete:
                    mDrawView.restartDrawing();
                    return false;
                case R.id.action_draw_tool:
                    changeDrawTool();
                    return false;
                case R.id.action_draw_mode:
                    changeDrawMode();
                    return false;
                case R.id.action_draw_save:
                    requestPermissions(0);

                    return false;
                default:
                    return false;
            }
        });


        //Todo Toolbar
        mToolbar = findViewById(R.id.location_app_bar);
        setSupportActionBar(mToolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle("Draw");
        mToolbar.setTitleTextColor(Color.WHITE);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        //Todo mDrawView
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
                R.drawable.back);


        mDrawView = findViewById(R.id.draw_view);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.paint_menu2, menu);
        mMenuItemUndo = menu.getItem(0);
        mMenuItemRedo = menu.getItem(1);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_undo:
                if (mDrawView.canUndo()) {
                    mDrawView.undo();
                    canUndoRedo();
                }
                break;
            case R.id.action_redo:
                if (mDrawView.canRedo()) {
                    mDrawView.redo();
                    canUndoRedo();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void canUndoRedo() {
        if (!mDrawView.canUndo()) {
            mMenuItemUndo.setEnabled(false);
            mMenuItemUndo.setIcon(R.drawable.paint_undo_disable);
        } else {
            mMenuItemUndo.setEnabled(true);
            mMenuItemUndo.setIcon(R.drawable.paint_undo);
        }
        if (!mDrawView.canRedo()) {
            mMenuItemRedo.setEnabled(false);
            mMenuItemRedo.setIcon(R.drawable.paint_redo_disable);
        } else {
            mMenuItemRedo.setEnabled(true);
            mMenuItemRedo.setIcon(R.drawable.paint_redo);
        }
    }

    private void changeDrawAttribs() {
        DrawAttribsDialog drawAttribsDialog = DrawAttribsDialog.newInstance();
        drawAttribsDialog.setPaint(mDrawView.getCurrentPaintParams());
        drawAttribsDialog.setOnCustomViewDialogListener(newPaint -> {
            mDrawView.setDrawColor(newPaint.getColor())
                    .setPaintStyle(newPaint.getStyle())
                    .setDither(newPaint.isDither())
                    .setDrawWidth((int) newPaint.getStrokeWidth())
                    .setDrawAlpha(newPaint.getAlpha())
                    .setAntiAlias(newPaint.isAntiAlias())
                    .setLineCap(newPaint.getStrokeCap())
                    .setFontFamily(newPaint.getTypeface())
                    .setFontSize(newPaint.getTextSize());
//                If you prefer, you can easily refresh new attributes using this method
//                mDrawView.refreshAttributes(newPaint);
        });
        drawAttribsDialog.show(getSupportFragmentManager(), "drawAttribs");
    }

    private void changeDrawTool() {
        SelectChoiceDialog selectChoiceDialog =
                SelectChoiceDialog.newInstance("Select a draw tool",
                        "PEN", "LINE", "ARROW", "RECTANGLE", "CIRCLE", "ELLIPSE");
        selectChoiceDialog.setOnChoiceDialogListener(new SelectChoiceDialog.OnChoiceDialogListener() {
            @Override
            public void onChoiceSelected(int position) {
                mDrawView.setDrawingTool(DrawingTool.values()[position]);
            }
        });
        selectChoiceDialog.show(getSupportFragmentManager(), "choiceDialog");
    }

    private void changeDrawMode() {
        SelectChoiceDialog selectChoiceDialog =
                SelectChoiceDialog.newInstance("Select a draw mode",
                        "DRAW", "TEXT", "ERASER");
        selectChoiceDialog.setOnChoiceDialogListener(new SelectChoiceDialog.OnChoiceDialogListener() {
            @Override
            public void onChoiceSelected(int position) {
                mDrawView.setDrawingMode(DrawingMode.values()[position]);
            }
        });
        selectChoiceDialog.show(getSupportFragmentManager(), "choiceDialog");
    }

    private void requestPermissions(int option) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(PaintView.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(PaintView.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(PaintView.this,
                        new String[]{
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        STORAGE_PERMISSIONS);
            } else {
                saveDraw();
            }
        } else {
            saveDraw();

        }
    }

    private void saveDraw() {
        SaveBitmapDialog saveBitmapDialog = SaveBitmapDialog.newInstance();
        Object[] createCaptureResponse = mDrawView.createCapture(DrawingCapture.BITMAP);
        saveBitmapDialog.setPreviewBitmap((Bitmap) createCaptureResponse[0]);
        saveBitmapDialog.setPreviewFormat(String.valueOf(createCaptureResponse[1]));
        saveBitmapDialog.setOnSaveBitmapListener(new SaveBitmapDialog.OnSaveBitmapListener() {
            @Override
            public void onSaveBitmapCompleted() {
                Snackbar.make(speedDialView, "Capture saved succesfully!", 2000).show();
            }

            @Override
            public void onSaveBitmapCanceled() {
                Snackbar.make(speedDialView, "Capture saved canceled.", 2000).show();
            }
        });
        saveBitmapDialog.show(getSupportFragmentManager(), "saveBitmap");
    }
}

