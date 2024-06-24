package com.tcd2019.smartaidkit;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.Calendar;

public class EditAidkit extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_edit_aidkit);

        final TextView[] textViewArr = {(TextView) findViewById(R.id.etMediName1), (TextView) findViewById(R.id.etMediName2), (TextView) findViewById(R.id.etMediName3), (TextView) findViewById(R.id.etMediName4), (TextView) findViewById(R.id.etMediName5), (TextView) findViewById(R.id.etMediName6), (TextView) findViewById(R.id.etMediName7), (TextView) findViewById(R.id.etMediName8), (TextView) findViewById(R.id.etMediName9), (TextView) findViewById(R.id.etMediName10), (TextView) findViewById(R.id.etMediName11), (TextView) findViewById(R.id.etMediName12)};
        final Button[] buttonArr = {(Button) findViewById(R.id.btnExpDate1), (Button) findViewById(R.id.btnExpDate2), (Button) findViewById(R.id.btnExpDate3), (Button) findViewById(R.id.btnExpDate4), (Button) findViewById(R.id.btnExpDate5), (Button) findViewById(R.id.btnExpDate6), (Button) findViewById(R.id.btnExpDate7), (Button) findViewById(R.id.btnExpDate8), (Button) findViewById(R.id.btnExpDate9), (Button) findViewById(R.id.btnExpDate10), (Button) findViewById(R.id.btnExpDate11), (Button) findViewById(R.id.btnExpDate12)};
        SharedPreferences sharedPreferences = getSharedPreferences("FirstAidKit", 0);
        String string = sharedPreferences.getString("mediName", "[]");
        Log.i("nameData", string);
        String string2 = sharedPreferences.getString("expDate", "[]");
        Log.i("dateData", string2);
        String[] strArr = new Gson().fromJson(string, new String[12].getClass());
        if (strArr.length != 0) {
            for (int i = 0; i < textViewArr.length; i++) {
                String str = strArr[i];
                if (str.equals("-")) {
                    str = "";
                }
                textViewArr[i].setText(str);
            }
        }
        String[] strArr2 = new Gson().fromJson(string2, new String[12].getClass());
        if (strArr2.length != 0) {
            for (int i2 = 0; i2 < buttonArr.length; i2++) {
                String str2 = strArr2[i2];
                if (str2.equals("-")) {
                    str2 = "사용기한 설정";
                }
                buttonArr[i2].setText(str2);
            }
        }
        for (Button button : buttonArr) {
            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    final Button button = (Button) findViewById(view.getId());

                    Calendar instance = Calendar.getInstance();
                    new DatePickerDialog(EditAidkit.this, new DatePickerDialog.OnDateSetListener() {
                        public void onDateSet(DatePicker datePicker, int i, int i2, int i3) {
                            button.setText(i + ". " + (i2 + 1) + ". " + i3 + ". ");
                        }
                    }, instance.get(1), instance.get(2), instance.get(5)).show();
                }});
            button.setOnLongClickListener(new View.OnLongClickListener() {
                    public boolean onLongClick(View view) {
                        ((Button) findViewById(view.getId())).setText("사용기한 설정");
                        return true;
                    }
                });
        }
        ((Button) findViewById(R.id.btnSaveSetting)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String[] strArr = new String[12];
                String[] strArr2 = new String[12];
                for (int i = 0; i < buttonArr.length; i++) {
                    String str = "" + textViewArr[i].getText();
                    String str2 = (String) buttonArr[i].getText();
                    if (str.equals("")) {
                        str = "-";
                    }
                    if (str2.equals("사용기한 설정")) {
                        str2 = "-";
                    }
                    strArr[i] = str;
                    strArr2[i] = str2;
                }
                Gson create = new GsonBuilder().create();
                String json = create.toJson((Object) strArr);
                String json2 = create.toJson((Object) strArr2);
                Log.i("mediJson", json);
                Log.i("expJson", json2);
                SharedPreferences.Editor edit = getSharedPreferences("FirstAidKit", 0).edit();
                edit.putString("mediName", json);
                edit.putString("expDate", json2);
                edit.apply();
                finish();
            }
        });
    }
}
