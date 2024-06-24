package com.tcd2019.smartaidkit;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.gson.Gson;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import me.gujun.android.taggroup.TagGroup;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_ENABLE_BT = 2739;
    private BluetoothAdapter mBluetoothAdapter = null;
    private BluetoothDevice mBtDevice;
    private BluetoothSocket mBtSocket;
    private InputStream mInputStream;
    private OutputStream mOutputStream;
    String nameData1;
    String numData1;

    @Override
    protected void onCreate(Bundle bundle) {
        boolean z = false;
        super.onCreate(bundle);
        setContentView(R.layout.activity_main);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        BluetoothAdapter bluetoothAdapter = mBluetoothAdapter;
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "블루투스를 지원하지 않는 기기입니다.", Toast.LENGTH_LONG).show();
            finish();
            return;
        } else if (!bluetoothAdapter.isEnabled()) {
            startActivityForResult(new Intent("android.bluetooth.adapter.action.REQUEST_ENABLE"), REQUEST_ENABLE_BT);
        }
        Iterator<BluetoothDevice> it = mBluetoothAdapter.getBondedDevices().iterator();
        while (it.hasNext()) {
            BluetoothDevice next = it.next();
            if (next.getAddress().equals("98:D3:81:FD:6B:D9")) {
                mBtDevice = next;
                z = true;
                break;
            }
            Log.i("BtDevice", next.getName() + " " + next.getAddress());
        }
        Log.i("BtDevice", "Already Paird: " + z);
        if (!z) {
            Toast.makeText(this, "블루투스 설정에서 'FsfAidKit'을 페어링 한 후에 실행하여 주세요.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        new BlueToothConnectTask().execute();
        ((Button) findViewById(R.id.btn_search_medi)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (getPackageList()) {
                    Intent launchIntentForPackage = getPackageManager().getLaunchIntentForPackage("kr.health.dikmobile");
                    startActivity(launchIntentForPackage);
                    return;
                }
                startActivity(new Intent("android.intent.action.VIEW", Uri.parse("market://details?id=kr.health.dikmobile")));
            }
        });
        ((Button) findViewById(R.id.btn_edit_aidkit)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                MainActivity mainActivity = MainActivity.this;
                mainActivity.startActivity(new Intent(mainActivity, EditAidkit.class));
            }
        });
        final SharedPreferences sharedPreferences = getSharedPreferences("FirstAidKit", 0);
        nameData1 = sharedPreferences.getString("tag", "[]");
        Log.i("tagData", nameData1);
        numData1 = sharedPreferences.getString("tagNum", "[]");
        Log.i("numData", numData1);
        ((Button) findViewById(R.id.btn_tag_add)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                CustomDialog customDialog = new CustomDialog(MainActivity.this);
                customDialog.setDialogListener(new CustomDialog.CustomDialogListener() {
                    public void onAddClicked() {
                        updateTag();
                        nameData1 = sharedPreferences.getString("tag", "[]");
                        numData1 = sharedPreferences.getString("tagNum", "[]");
                    }
                });
                customDialog.show();
            }
        });
        updateText();
        updateTag();
        ((TagGroup) findViewById(R.id.tag_group)).setOnTagClickListener(new TagGroup.OnTagClickListener() {
            public void onTagClick(String str) {
                Log.i("tag", str);
                ArrayList arrayList = new ArrayList();
                ArrayList arrayList2 = (ArrayList) new Gson().fromJson(numData1, new ArrayList().getClass());
                int indexOf = ((ArrayList) new Gson().fromJson(nameData1, arrayList.getClass())).indexOf(str);
                Log.i("num", (String) arrayList2.get(indexOf));
                try {
                    sendData((String) arrayList2.get(indexOf));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public boolean getPackageList() {
        PackageManager packageManager = getPackageManager();
        Intent intent = new Intent("android.intent.action.MAIN", (Uri) null);
        intent.addCategory("android.intent.category.LAUNCHER");
        List<ResolveInfo> queryIntentActivities = packageManager.queryIntentActivities(intent, 0);
        int i = 0;
        while (i < queryIntentActivities.size()) {
            try {
                if (queryIntentActivities.get(i).activityInfo.packageName.startsWith("kr.health.dikmobile")) {
                    return true;
                }
                i++;
            } catch (Exception unused) {
                return false;
            }
        }
        return false;
    }

    protected void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (i != REQUEST_ENABLE_BT) {
            return;
        }
        if (i2 == -1) {
            Toast.makeText(this, "블루투스가 활성화되었습니다.", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "블루투스 사용을 허용하셔야 정상적인 사용이 가능합니다.", Toast.LENGTH_LONG).show();
        }
    }

    protected void updateTag() {
        String string = getSharedPreferences("FirstAidKit", 0).getString("tag", "[]");
        Log.i("nameData", string);
        ArrayList arrayList = new ArrayList();
        ((TagGroup) findViewById(R.id.tag_group)).setTags((List<String>) (ArrayList) new Gson().fromJson(string, arrayList.getClass()));
    }

    protected void updateText() {
        TextView[] textViewArr = {(TextView) findViewById(R.id.tableText0), (TextView) findViewById(R.id.tableText1), (TextView) findViewById(R.id.tableText2), (TextView) findViewById(R.id.tableText3), (TextView) findViewById(R.id.tableText4), (TextView) findViewById(R.id.tableText5), (TextView) findViewById(R.id.tableText6), (TextView) findViewById(R.id.tableText7), (TextView) findViewById(R.id.tableText8), (TextView) findViewById(R.id.tableText9), (TextView) findViewById(R.id.tableText10), (TextView) findViewById(R.id.tableText11)};
        TextView[] textViewArr2 = {(TextView) findViewById(R.id.tableTextDate0), (TextView) findViewById(R.id.tableTextDate1), (TextView) findViewById(R.id.tableTextDate2), (TextView) findViewById(R.id.tableTextDate3), (TextView) findViewById(R.id.tableTextDate4), (TextView) findViewById(R.id.tableTextDate5), (TextView) findViewById(R.id.tableTextDate6), (TextView) findViewById(R.id.tableTextDate7), (TextView) findViewById(R.id.tableTextDate8), (TextView) findViewById(R.id.tableTextDate9), (TextView) findViewById(R.id.tableTextDate10), (TextView) findViewById(R.id.tableTextDate11)};
        SharedPreferences sharedPreferences = getSharedPreferences("FirstAidKit", 0);
        String string = sharedPreferences.getString("mediName", "[]");
        Log.i("nameData", string);
        String string2 = sharedPreferences.getString("expDate", "[]");
        Log.i("dateData", string2);
        String[] strArr = (String[]) new Gson().fromJson(string, new String[12].getClass());
        if (strArr.length != 0) {
            for (int i = 0; i < textViewArr.length; i++) {
                textViewArr[i].setText(strArr[i]);
            }
        }
        String[] strArr2 = (String[]) new Gson().fromJson(string2, new String[12].getClass());
        if (strArr2.length != 0) {
            for (int i2 = 0; i2 < textViewArr2.length; i2++) {
                textViewArr2[i2].setText(strArr2[i2]);
            }
        }
    }

    void openBT(BluetoothDevice bluetoothDevice) throws IOException {
        mBtSocket = bluetoothDevice.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
        mBtSocket.connect();
        mOutputStream = mBtSocket.getOutputStream();
        mInputStream = mBtSocket.getInputStream();
        Log.i("BtDevice", "Bluetooth Opened");
    }

    void sendData(String str) throws IOException {
        OutputStream outputStream = mOutputStream;
        if (outputStream != null) {
            outputStream.write(str.getBytes());
        }
    }

    void closeBT() throws IOException {
        OutputStream outputStream = mOutputStream;
        if (outputStream != null) {
            outputStream.close();
        }
        InputStream inputStream = mInputStream;
        if (inputStream != null) {
            inputStream.close();
        }
        BluetoothSocket bluetoothSocket = mBtSocket;
        if (bluetoothSocket != null) {
            bluetoothSocket.close();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            closeBT();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateText();
    }

    private class BlueToothConnectTask extends AsyncTask<Void, Boolean, Boolean> {
        ProgressDialog asyncDialog;

        private BlueToothConnectTask() {
            asyncDialog = new ProgressDialog(MainActivity.this);
        }

        protected void onPreExecute() {
            asyncDialog.setProgressStyle(0);
            asyncDialog.setMessage("스마트 구급상자에 연결 중입니다..");
            asyncDialog.setCancelable(false);
            asyncDialog.show();
            super.onPreExecute();
        }

        protected Boolean doInBackground(Void... voidArr) {
            try {
                openBT(mBtDevice);
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        protected void onPostExecute(Boolean bool) {
            asyncDialog.dismiss();
            Log.i("BlueToothConnectTask", "onPostExecute: " + bool);
            if (!bool.booleanValue()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("오류").setMessage("스마트 구급상자에 연결할 수 없습니다.\n구급상자의 전원 여부와 거리를 확인하십시오.\n앱을 재시작하여 재시도 할 수 있습니다.\n의약품 공급 기능의 사용이 제한됩니다.").setCancelable(false).setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                builder.create().show();
            }
            super.onPostExecute(bool);
        }
    }
}
