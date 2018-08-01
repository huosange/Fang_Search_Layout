package com.search.searchview;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class SearchView extends LinearLayout {

    private Context context;

    private EditText et_search;
    private LinearLayout search_block;
    private SearchListView listView;
    private TextView tv_clear;
    private ImageView search_back;

    private float textSizeSearch;
    private int textColorSearch;
    private String textHintSearch;
    private float searchBlockHeight;
    private int searchBlockColor;

    private ICallBack callBack;
    private BCallBack bCallBack;

    private RecordSQLiteOpenHelper helper;
    private SimpleCursorAdapter adapter;
    private SQLiteDatabase db;

    public SearchView(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public SearchView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initAttrs(attrs);
        init();
    }

    public SearchView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initAttrs(attrs);
        init();
    }

    private void initAttrs(AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.Search_View);
        //字体大小
        textSizeSearch = typedArray.getDimension(R.styleable.Search_View_textSizeSearch, 48);
        //搜索框的字体颜色
        int defaultColor = context.getResources().getColor(R.color.colorText);
        textColorSearch = typedArray.getColor(R.styleable.Search_View_textColorSearch, defaultColor);
        //搜索框提示内容
        textHintSearch = typedArray.getString(R.styleable.Search_View_textHintSearch);
        //搜索框的高度
        searchBlockHeight = typedArray.getDimension(R.styleable.Search_View_searchBlockHeight, 150);
        //搜索框背景颜色
        int defaultColor2 = getResources().getColor(R.color.colorDefault);
        searchBlockColor = typedArray.getColor(R.styleable.Search_View_searchBlockColor, defaultColor2);
        //释放资源
        typedArray.recycle();
    }

    private void init() {
        initView();

        helper = new RecordSQLiteOpenHelper(context);
        queryData("");

        et_search.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (callBack != null) {
                        callBack.searchAction(et_search.getText().toString());
                    }
                    Toast.makeText(context, "需要搜索的是：" + et_search.getText(), Toast.LENGTH_SHORT).show();
                    boolean hasData = hasData(et_search.getText().toString().trim());
                    if (!hasData) {
                        insertData(et_search.getText().toString().trim());
                        queryData("");
                    }
                }
                return false;
            }
        });

        tv_clear.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteData();
                queryData("");
            }
        });

        search_back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bCallBack != null) {
                    bCallBack.backAction();
                }
                Toast.makeText(context, "返回到上一页", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initView() {
        LayoutInflater.from(context).inflate(R.layout.search_layout, this);

        et_search = findViewById(R.id.et_search);
        et_search.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeSearch);
        et_search.setTextColor(textColorSearch);
        et_search.setHint(textHintSearch);

        search_block = findViewById(R.id.search_block);
        LinearLayout.LayoutParams params = (LayoutParams) search_block.getLayoutParams();
        params.height = (int) searchBlockHeight;
        search_block.setBackgroundColor(searchBlockColor);
        search_block.setLayoutParams(params);

        listView = findViewById(R.id.listview);
        tv_clear = findViewById(R.id.tv_clear);
        tv_clear.setVisibility(INVISIBLE);

        search_back = findViewById(R.id.search_back);
    }

    //点击键盘搜索的操作
    public void setOnClickSearch(ICallBack callBack) {
        this.callBack = callBack;
    }

    //点击返回的操作
    public void setOnClickBack(BCallBack callBack) {
        this.bCallBack = callBack;
    }

    private void queryData(String tempName) {
        Cursor cursor = helper.getReadableDatabase().rawQuery("select id as _id,name from records where name like '%" + tempName + "%' order by id desc", null);
        adapter = new SimpleCursorAdapter(context, android.R.layout.simple_list_item_1, cursor, new String[]{"name"}, new int[]{android.R.id.text1});
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        if (tempName.equals("") && cursor.getCount() != 0) {
            tv_clear.setVisibility(VISIBLE);
        } else {
            tv_clear.setVisibility(INVISIBLE);
        }
    }

    private boolean hasData(String tempName) {
        Cursor cursor = helper.getReadableDatabase().rawQuery("select id as _id,name from records where name=?", new String[]{tempName});
        //判断是否有下一个
        return cursor.moveToNext();
    }

    private void insertData(String tempName) {
        db = helper.getWritableDatabase();
        db.execSQL("insert into records(name) values('" + tempName + "')");
        db.close();
    }

    private void deleteData() {
        db = helper.getWritableDatabase();
        db.execSQL("delete from records");
        db.close();
        tv_clear.setVisibility(INVISIBLE);
    }
}
