package com.khasang.vkphoto.presentation.model;

import android.content.Context;
import android.os.Bundle;
import android.preference.ListPreference;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.khasang.vkphoto.R;
import com.khasang.vkphoto.util.Logger;

/**
 * Created by Иричи on 16.03.2016.
 */
public class MaterialSyncListPreference extends ListPreference {
    private MaterialDialog.Builder mBuilder;
    private Context context;

    public MaterialSyncListPreference(Context context) {
        super(context);
        this.context = context;
    }

    public MaterialSyncListPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    @Override
    protected void showDialog(Bundle state) {
        mBuilder = new MaterialDialog.Builder(context);
        mBuilder.title(getTitle());
        View view = LayoutInflater.from(context).inflate(R.layout.pref_dialog_sync_frequency, null);
        String value = getValue();
        if (value != null) {
            RadioButton radioButton;
            if (value.equals(context.getString(R.string.min_15))) {
                radioButton = (RadioButton) view.findViewById(R.id.rb_15);
            } else if(value.equals(context.getString(R.string.min_30))){
                radioButton = (RadioButton) view.findViewById(R.id.rb_30);
            }else if(value.equals(context.getString(R.string.hours_3))){
                radioButton = (RadioButton) view.findViewById(R.id.rb_3_hours);
            }else if(value.equals(context.getString(R.string.hours_6))){
                radioButton = (RadioButton) view.findViewById(R.id.rb_6_hours);
            }else if(value.equals(context.getString(R.string.day))){
                radioButton = (RadioButton) view.findViewById(R.id.rb_day);
            }else if(value.equals(context.getString(R.string.never))){
                radioButton = (RadioButton) view.findViewById(R.id.rb_never);
            }else{
                radioButton = (RadioButton) view.findViewById(R.id.rb_hour);
            }
            radioButton.setChecked(true);
        }
        mBuilder.customView(view, true);
        mBuilder.negativeText(R.string.cancel);
        mBuilder.onNegative(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(MaterialDialog dialog, DialogAction which) {
                dialog.dismiss();
            }
        });
        mBuilder.positiveText(R.string.st_btn_ok);
        mBuilder.onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(MaterialDialog dialog, DialogAction which) {
                View view = dialog.getView();
                int checkedRadioButtonId = ((RadioGroup) view.findViewById(R.id.rg_sync_frequency)).getCheckedRadioButtonId();
                RadioButton radioButton = (RadioButton) view.findViewById(checkedRadioButtonId);
                String value = radioButton.getText().toString();
                Logger.d(value);
                setSummary(value);
                setValue(value);
            }
        });
        mBuilder.show();
    }

}