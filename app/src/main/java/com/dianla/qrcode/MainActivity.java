package com.dianla.qrcode;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dianla.qrlibrary.TestGeneratectivity;
import com.dianla.qrlibrary.TestScanActivity;
import com.dianla.qrlibrary.util.EncodingHandler;
import com.google.zxing.WriterException;

import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    private static final int REQUEST_CODE_QRCODE_PERMISSIONS = 1;

    private final static int SCANNIN_GREQUEST_CODE = 1;

    /**
     * 显示扫描结果
     */
    private TextView mTextView;
    /**
     * 显示扫描拍的图片
     */
    private ImageView mImageView;

    /**
     * 输入框产生二维码
     *
     * @param savedInstanceState
     */
    private EditText qrStrEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.qr_scan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, TestScanActivity.class));
            }
        });

        findViewById(R.id.qr_create).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, TestGeneratectivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivityForResult(intent, SCANNIN_GREQUEST_CODE);
            }
        });

        mTextView = (TextView) findViewById(R.id.result);
        mImageView = (ImageView) findViewById(R.id.qrcode_bitmap);
        qrStrEditText = (EditText) findViewById(R.id.et_qr_string);

        //产生二维码
        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String contentString = qrStrEditText.getText().toString();
                try {
                    if (!contentString.equals("")) {
                        Bitmap qrCodeBitmap = EncodingHandler.createQRCode(contentString, 350);
                        mImageView.setImageBitmap(qrCodeBitmap);
                        mTextView.setText(contentString);
                    } else {
                        Toast.makeText(getApplicationContext(), "Text can be not empty", Toast.LENGTH_SHORT).show();
                    }
                } catch (WriterException e) {
                    e.printStackTrace();
                }
            }
        });

        //产生条形码
        findViewById(R.id.button3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String contentString = qrStrEditText.getText().toString();
                int size = contentString.length();
                for (int i = 0; i < size; i++) {
                    int c = contentString.charAt(i);
                    if ((19968 <= c && c < 40623)) {
                        Toast.makeText(getApplicationContext(), "text not be chinese", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                Bitmap mBmpOneCode = null;
                try {
                    if (contentString != null && !"".equals(contentString)) {
                        mBmpOneCode = EncodingHandler.CreateOneDCode(contentString);
                        mTextView.setText(contentString);
                    }
                } catch (WriterException e) {
                    e.printStackTrace();
                }
                if (mBmpOneCode != null) {
                    mImageView.setImageBitmap(mBmpOneCode);
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        requestCodeQRCodePermissions();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SCANNIN_GREQUEST_CODE:
                if(resultCode == RESULT_OK){
                    Bundle bundle = data.getExtras();
                    //显示扫描到的内容
                    mTextView.setText(bundle.getString("result"));
                    //显示
                    mImageView.setImageBitmap((Bitmap) data.getParcelableExtra("bitmap"));
                }
                break;
        }
    }

    @AfterPermissionGranted(REQUEST_CODE_QRCODE_PERMISSIONS)
    private void requestCodeQRCodePermissions() {
        String[] perms = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (!EasyPermissions.hasPermissions(this, perms)) {
            EasyPermissions.requestPermissions(this, "扫描二维码需要打开相机和散光灯的权限", REQUEST_CODE_QRCODE_PERMISSIONS, perms);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {

    }
}
