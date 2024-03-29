package com.qr.locationdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.qr.locationdemo.adapter.PoiAdapter;
import com.qr.locationdemo.bean.PoiBean;
import java.util.ArrayList;
import java.util.List;


/**
 * ====================== 搜索地点 界面 ========================
 *
 * @author SGamble
 */
public class SearchAddressActivity extends AppCompatActivity implements PoiSearch.OnPoiSearchListener, AutoListView.OnRefreshListener, AutoListView.OnLoadListener {
    private static final String TAG = "SearchAddressActivity";
    EditText et_search;
    LinearLayout ll_loading;
    AutoListView lv_list;
    TextView tv_no_data;
    Button btnSearch;
    private int currentPage = 0;
    private PoiSearch.Query query;// POI查询条件类
    private PoiSearch poiSearch;// POI搜索

    private List<PoiBean> poiData = new ArrayList<>();
    private List<PoiItem> savePoiItem=new ArrayList<>();
    private PoiAdapter mAdapter;
    private String mKeyWord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_address);
        initView();
        setListener();

        Log.e(TAG, "onCreate: ");

    }

    private void initView() {
        et_search= (EditText) findViewById(R.id.et_search);
        ll_loading= (LinearLayout) findViewById(R.id.ll_loading);
        lv_list= (AutoListView) findViewById(R.id.lv_list);
        tv_no_data= (TextView) findViewById(R.id.tv_no_data);
        btnSearch= (Button) findViewById(R.id.btnSearch);
    }

    private void setListener() {
        mAdapter = new PoiAdapter(this, poiData);
        lv_list.setAdapter(mAdapter);
        lv_list.setOnRefreshListener(this);
        lv_list.setOnLoadListener(this);
        lv_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PoiItem poiItem = savePoiItem.get((int) id);
                Intent intent=new Intent();
                intent.putExtra("poiItem",poiItem);
                setResult(6,intent);
                finish();
            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search();
            }
        });
    }
    
    /**
     * 查找
     * @author Gamble
     */
    public void search() {
        mKeyWord = et_search.getText().toString();
        if (TextUtils.isEmpty(mKeyWord)) {
            Toast.makeText(this, "请输入您要查找的地点", Toast.LENGTH_SHORT).show();
            return;
        }
        poiData.clear();
        currentPage=0;
        doSearchQuery(mKeyWord);
    }

    /**
     * POI 查询
     * @author Gamble
     */
    protected void doSearchQuery(String keyWord) {
        if(currentPage==0) {
            savePoiItem.clear();
            ll_loading.setVisibility(View.VISIBLE);// 显示进度框
            lv_list.setVisibility(View.GONE);
        }
        query = new PoiSearch.Query(keyWord, "", "");// 第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）
        query.setPageSize(10);// 设置每页最多返回多少条poiitem
        query.setPageNum(currentPage);// 设置查第一页
        query.setCityLimit(true);
        poiSearch = new PoiSearch(this, query);
        poiSearch.setOnPoiSearchListener(this);
        poiSearch.searchPOIAsyn();
    }

    @Override
    public void onPoiSearched(PoiResult poiResult, int rCode) {
        lv_list.onLoadComplete();

        if (rCode == AMapException.CODE_AMAP_SUCCESS) {
            if (poiResult != null && poiResult.getQuery() != null) {// 搜索poi的结果

                if (poiResult.getQuery().equals(query)) {// 是否是同一条
                    if(currentPage==0){
                        ll_loading.setVisibility(View.GONE);// 隐藏对话框
                        lv_list.setVisibility(View.VISIBLE);
                        tv_no_data.setVisibility(View.GONE);
                    }
                    // 取得搜索到的poiitems有多少页
                    List<PoiItem> poiItems = poiResult.getPois();// 取得第一页的poiitem数据，页数从数字0开始
                    savePoiItem.addAll(poiItems);
                    List<PoiBean> tem = new ArrayList<>();
                    if (poiItems != null && poiItems.size() > 0) {
                        for (int i = 0; i < poiItems.size(); i++) {
                            PoiItem poiItem = poiItems.get(i);
                            PoiBean bean = new PoiBean();
                            bean.setTitleName(poiItem.getTitle());
                            bean.setCityName(poiItem.getCityName());
                            bean.setAd(poiItem.getAdName());
                            bean.setSnippet(poiItem.getSnippet());
                            bean.setPoint(poiItem.getLatLonPoint());
                            Log.e(TAG, ""
                                    + poiItem.getTitle() + ","
                                    + poiItem.getProvinceName() + ","
                                    + poiItem.getCityName() + ","
                                    + poiItem.getAdName() + ","//区
                                    + poiItem.getSnippet() + ","
                                    + poiItem.getLatLonPoint() + "\n");
                            tem.add(bean);
                        }
                        poiData.addAll(tem);
                        mAdapter.notifyDataSetChanged();
                    }
                } else {
                    //没有结果
                    ll_loading.setVisibility(View.GONE);// 隐藏对话框
                    lv_list.setVisibility(View.GONE);
                    tv_no_data.setVisibility(View.VISIBLE);
                }
            } else {
                Toast.makeText(this, "搜索失败：" + rCode, Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    public void onPoiItemSearched(PoiItem poiItem, int i) { }

    /**
     * 刷新
     * @author Gamble
     */
    @Override
    public void onRefresh() {
        lv_list.onRefreshComplete();
    }
    
    /**
     * 加载下一页数据
     * @author Gamble
     */
    @Override
    public void onLoad() {
        currentPage++;
        doSearchQuery(mKeyWord);
    }
}
