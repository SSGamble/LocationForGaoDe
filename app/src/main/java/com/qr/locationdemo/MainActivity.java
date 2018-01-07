package com.qr.locationdemo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * ====================== 主界面 ========================
 * 获取定位信息
 * @author SGamble
 */
public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_SELECT_ADDRESS_CODE = 2;
    TextView tv_address;
    Button btn_select_address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        applyPermission(); //申请权限
        initView();
        setListener();
    }

    private void initView() {
        tv_address = (TextView) findViewById(R.id.tv_address);
        btn_select_address = (Button) findViewById(R.id.btn_select_address);
    }

    /**
     * 点击事件
     */
    private void setListener() {
        btn_select_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            Intent intent = new Intent(MainActivity.this, LocationSelectActivity.class);
            startActivityForResult(intent, REQUEST_SELECT_ADDRESS_CODE);
            }
        });
    }

    /**
     * 申请权限
     */
    private void applyPermission() {
        //需要请求的权限列表
        List<String> permissionList = new ArrayList<>();
        //检查权限
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //添加权限到 待申请权限列表中
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        //申请权限
        if (!permissionList.isEmpty()) {
            String[] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(MainActivity.this, permissions, 1);
        }
    }

    /**
     * 权限请求的返回结果
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            // requestCode即所声明的权限获取码，在checkSelfPermission时传入
            case 1:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    showLogD("申请权限成功");
                } else {
                    // 没有获取到权限，做特殊处理
//                    showLogE("请求权限失败");
//                    showToast("请手动开启权限");
                }
                break;
            default:
                break;
        }
    }

    /**
     * Intent 回调，接收信上个界面的息
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SELECT_ADDRESS_CODE && resultCode == 2) {
            double latitude = data.getDoubleExtra("latitude", 0.0);
            double longitude = data.getDoubleExtra("longitude", 0.0);
            String address = data.getStringExtra("address");
            tv_address.setText("详细地址：" + address + "\n经度：" + longitude + "\n纬度：" + latitude);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}

