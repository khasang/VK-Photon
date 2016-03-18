package com.khasang.vkphoto.presentation.model;

import android.content.Context;
import android.os.Bundle;
import android.preference.ListPreference;
import android.util.AttributeSet;
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
public class MaterialListPreference extends ListPreference {
    private MaterialDialog.Builder mBuilder;
    private Context context;

    public MaterialListPreference(Context context) {
        super(context);
        this.context = context;
    }

    public MaterialListPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    @Override
    protected void showDialog(Bundle state) {
        mBuilder = new MaterialDialog.Builder(context);
        mBuilder.title(getTitle());
        mBuilder.customView(R.layout.pref_dialog_language, true);
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
                int checkedRadioButtonId = ((RadioGroup) view.findViewById(R.id.rg_languages)).getCheckedRadioButtonId();
                String value = ((RadioButton) view.findViewById(checkedRadioButtonId)).getText().toString();
                Logger.d(value);
                setValue(value);
            }
        });
//        mBuilder.items(getEntries());
//        mBuilder.itemsCallback(new MaterialDialog.ListCallback() {
//
//            @Override
//            public void onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
//                onClick(null, DialogInterface.BUTTON_POSITIVE);
//                dialog.dismiss();
//
//                if (which >= 0 && getEntryValues() != null) {
//                    String value = getEntryValues()[which].toString();
//                    if (callChangeListener(value))
//                        setValue(value);
//                }
//            }
//        });

//        final View contentView = onCreateDialogView();
//        if (contentView != null) {
//            onBindDialogView(contentView);
//            mBuilder.customView(contentView, false);
//        } else
//            mBuilder.content(getDialogMessage());

        mBuilder.show();
    }

}