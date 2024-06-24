package com.tcd2019.smartaidkit;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.google.gson.Gson;
import java.util.ArrayList;

public class CustomDialog extends Dialog {
    private CustomDialogListener dialogListener;

    public interface CustomDialogListener {
        void onAddClicked();
    }

    public void setDialogListener(CustomDialogListener customDialogListener) {
        dialogListener = customDialogListener;
    }

    public CustomDialog(@NonNull final Context context) {
        super(context);
        setContentView(R.layout.dialog_add_tag);
        String string = context.getSharedPreferences("FirstAidKit", 0).getString("mediName", "[]");
        Log.i("tagData", string);
        final CheckBox[] checkBoxArr = {(CheckBox) findViewById(R.id.checkBox1), (CheckBox) findViewById(R.id.checkBox2), (CheckBox) findViewById(R.id.checkBox3), (CheckBox) findViewById(R.id.checkBox4), (CheckBox) findViewById(R.id.checkBox5), (CheckBox) findViewById(R.id.checkBox6), (CheckBox) findViewById(R.id.checkBox7), (CheckBox) findViewById(R.id.checkBox8), (CheckBox) findViewById(R.id.checkBox9), (CheckBox) findViewById(R.id.checkBox10), (CheckBox) findViewById(R.id.checkBox11), (CheckBox) findViewById(R.id.checkBox12)};
        String[] strArr = (String[]) new Gson().fromJson(string, new String[12].getClass());
        if (strArr.length != 0) {
            for (int i = 0; i < checkBoxArr.length; i++) {
                checkBoxArr[i].setText(strArr[i]);
            }
        }
        ((Button) findViewById(R.id.btn_save_tag)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                TextView textView = (TextView) findViewById(R.id.etTagName);
                if (textView.length() > 0) {
                    CustomDialog customDialog = CustomDialog.this;
                    customDialog.addTag("" + textView.getText(), checkBoxArr, context);
                    dialogListener.onAddClicked();
                    dismiss();
                    return;
                }
                Toast.makeText(context, "태그명을 입력해주세요.", Toast.LENGTH_LONG).show();
            }
        });
    }


    private void addTag(String str, CheckBox[] checkBoxArr, Context context) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < checkBoxArr.length; i++) {
            if (checkBoxArr[i].isChecked()) {
                String valueOf = String.valueOf(i);
                if (i == 10) {
                    valueOf = "A";
                } else if (i == 11) {
                    valueOf = "B";
                }
                sb.append(valueOf);
            }
        }
        SharedPreferences sharedPreferences = context.getSharedPreferences("FirstAidKit", 0);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        String string = sharedPreferences.getString("tag", "[]");
        Log.i("tagData", string);
        String string2 = sharedPreferences.getString("tagNum", "[]");
        Log.i("numData", string2);
        ArrayList arrayList = (ArrayList) new Gson().fromJson(string, new ArrayList().getClass());
        arrayList.indexOf("#" + str);
        arrayList.add("#" + str);
        ArrayList arrayList2 = (ArrayList) new Gson().fromJson(string2, new ArrayList().getClass());
        arrayList2.add(sb);
        edit.putString("tag", new Gson().toJson((Object) arrayList));
        edit.apply();
        edit.putString("tagNum", new Gson().toJson((Object) arrayList2));
        edit.apply();
    }
}
