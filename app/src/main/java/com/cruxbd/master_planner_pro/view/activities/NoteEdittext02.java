package com.cruxbd.master_planner_pro.view.activities;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;


import com.cruxbd.master_planner_pro.R;
import com.cruxbd.master_planner_pro.utils.CustomEditText.CustomText;
import com.muddzdev.styleabletoastlibrary.StyleableToast;


public class NoteEdittext02 extends AppCompatActivity {
    Toolbar tl;

    private CustomText customText;
    String notes = " ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_edittext02);

        notes =getIntent().getStringExtra("notes");
        tl =  findViewById(R.id.toolbar);
        setSupportActionBar(tl);
        getSupportActionBar();
        customText = findViewById(R.id.custom);
        // ImageGetter coming soon...
//        customText.fromHtml("<b>Insert text here.......</b>");
//        customText.setSelection(customText.getEditableText().length());

        customText.setHint("Write your note here...");
        customText.setText(notes);


        setupBold();
        setupItalic();
        setupUnderline();
        setupStrikethrough();
        setupBullet();
        setupQuote();
        setupLink();
        setupClear();
    }

    private void setupBold() {
        ImageButton bold = findViewById(R.id.bold);
        bold.setOnClickListener(v -> customText.bold(!customText.contains(customText.FORMAT_BOLD)));
    }

    private void setupItalic() {
        ImageButton italic = findViewById(R.id.italic);

        italic.setOnClickListener(v -> customText.italic(!customText.contains(customText.FORMAT_ITALIC)));

        italic.setOnLongClickListener(v -> {
            StyleableToast.makeText(NoteEdittext02.this, ""+R.string.toast_italic, Toast.LENGTH_SHORT, R.style.mytoast).show();
            return true;
        });
    }

    private void setupUnderline() {
        ImageButton underline = findViewById(R.id.underline);

        underline.setOnClickListener(v -> customText.underline(!customText.contains(customText.FORMAT_UNDERLINED)));


    }

    private void setupStrikethrough() {
        ImageButton strikethrough = findViewById(R.id.strikethrough);
        strikethrough.setOnClickListener(v -> customText.strikethrough(!customText.contains(customText.FORMAT_STRIKETHROUGH)));
    }

    private void setupBullet() {
        ImageButton bullet = findViewById(R.id.bullet);
        bullet.setOnClickListener(v -> customText.bullet(!customText.contains(customText.FORMAT_BULLET)));
    }

    private void setupQuote() {
        ImageButton quote = findViewById(R.id.quote);
        quote.setOnClickListener(v -> customText.quote(!customText.contains(customText.FORMAT_QUOTE)));
    }

    private void setupLink() {
        ImageButton link = findViewById(R.id.link);
        link.setOnClickListener(v -> showLinkDialog());
    }

    private void setupClear() {
        ImageButton clear = findViewById(R.id.clear);
        clear.setOnClickListener(v -> customText.clearFormats());
    }

    private void showLinkDialog() {
        final int start = customText.getSelectionStart();
        final int end = customText.getSelectionEnd();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);

        View view = getLayoutInflater().inflate(R.layout.dialog_link, null, false);
        final EditText editText = view.findViewById(R.id.edit);
        builder.setView(view);
        builder.setTitle(R.string.dialog_title);

        builder.setPositiveButton(R.string.dialog_button_ok, (dialog, which) -> {
            String link = editText.getText().toString().trim();
            if (TextUtils.isEmpty(link)) {
                return;
            }

            // When KnifeText lose focus, use this method
            customText.link(link, start, end);
        });

        builder.setNegativeButton(R.string.dialog_button_cancel, (dialog, which) -> {
            // DO NOTHING HERE
        });

        builder.create().show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.note, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.undo:
                customText.undo();
                break;
            case R.id.redo:
                customText.redo();
                break;
            case R.id.github:
                Intent returnIntent = new Intent();
                returnIntent.putExtra("data",customText.getText().toString());
                setResult(Activity.RESULT_OK,returnIntent);
                finish();


                //startActivity(new Intent(NoteEdittext02.this,AddTodoDetails.class).putExtra("data",customText.getText().toString()));
          //      StyleableToast.makeText(this,    customText.getText().toString(), Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}