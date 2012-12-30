package com.vtrainer.activity;

import com.vtrainer.R;
import com.vtrainer.provider.VocabularyMetaData;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class CategoriesActivity extends Activity implements OnClickListener {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.categories);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
        case R.id.btn_cat1:
            intent = new Intent(this, VocabularyActivity.class);
            intent.putExtra(VocabularyMetaData.CATEGOTY_ID, R.array.cat_clothes_array);
            intent.putExtra(VocabularyMetaData.CATEGOTY_NAME, R.string.cat1_title);
            break;
        case R.id.btn_cat2:
            intent = new Intent(this, VocabularyActivity.class);
            intent.putExtra(VocabularyMetaData.CATEGOTY_ID, R.array.cat_traits_array);
            intent.putExtra(VocabularyMetaData.CATEGOTY_NAME, R.string.cat2_title);
            break;
        case R.id.btn_cat3:
            intent = new Intent(this, VocabularyActivity.class);
            intent.putExtra(VocabularyMetaData.CATEGOTY_ID, R.array.cat_sport_array);
            intent.putExtra(VocabularyMetaData.CATEGOTY_NAME, R.string.cat3_title);
            break;
        default:
            return;
        }

        startActivity(intent);
    }
  
}