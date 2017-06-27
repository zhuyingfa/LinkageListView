package com.jeff.linkagelistview;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.View;

import com.jeff.linkagelistview.bean.Bean;

import java.util.ArrayList;

/**
 * Created by zyf on 2017/5/8.
 */

public class ItemDecoration extends RecyclerView.ItemDecoration {

    private static final String TAG = ItemDecoration.class.getSimpleName();
    private Context mContext;
    private ArrayList<Bean> mDataList = new ArrayList<>();
    private OnDecorationCallback mOnDecorationCallback;

    private Paint mPaint;
    private TextPaint mTextPaint;

    private int mTopGap = 50;          // 吸顶高
    private int mAlignBottom = 10;


    public ItemDecoration(Context context, ArrayList<Bean> dataList, OnDecorationCallback onDecorationCallback) {
        this.mContext = context;
        this.mDataList = dataList;
        this.mOnDecorationCallback = onDecorationCallback;

        Resources resources = mContext.getResources();
        mPaint = new Paint();
        mPaint.setColor(resources.getColor(R.color.black));
        mTextPaint = new TextPaint();
        mTextPaint.setColor(Color.WHITE);
        mTextPaint.setAntiAlias(true); // 去锯齿
        mTextPaint.setTextSize(25);
        mTextPaint.setTextAlign(Paint.Align.LEFT);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int pos = parent.getChildAdapterPosition(view);
        String groupId = mOnDecorationCallback.onGroupId(pos);
        if (groupId.equals("-1"))
            return;
        if (pos == 0 || isGroupFirstItem(pos)){
            outRect.top = mTopGap;   // 每组的头部都预留出位置
            if (mDataList.get(pos).getTitle().equals(""))
                outRect.top = 0;
        }else {
            outRect.top = 0;
        }
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);

        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();
        int childCount = parent.getChildCount();
        for (int i=0; i<childCount; i++){
            View view = parent.getChildAt(i);
            int pos = parent.getChildAdapterPosition(view);
            String groupId = mOnDecorationCallback.onGroupId(pos);
            if (groupId.equals("-1"))
                return;
            String textLine = mOnDecorationCallback.onGroupFirstStr(pos).toUpperCase();
            if (textLine.equals("")){
                float top = view.getTop();
                float bottom = view.getTop();
                c.drawRect(left, top, right, bottom, mPaint);
                return;
            }else {
                if (pos == 0 || isGroupFirstItem(pos)){  // 当前位置为0或为一组中的第一个时，显示顶部
                    float top = view.getTop() - mTopGap;
                    float bottom =  view.getTop();
                    c.drawRect(left, top, right, bottom, mPaint);
                    c.drawText(textLine, left, bottom, mTextPaint);
                }
            }

        }
    }

    // 在onDraw之后调用，此处做吸顶一直存在的功能
    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(c, parent, state);
        int itemCount = state.getItemCount();
        int childCount = parent.getChildCount();
        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();

        String preGroupId = "";
        String groupId = "-1";

        for (int i=0; i<childCount; i++){
            View view = parent.getChildAt(i);
            int position = parent.getChildAdapterPosition(view);

            preGroupId = groupId;
            groupId = mOnDecorationCallback.onGroupId(position);

            if (groupId.equals("-1") || groupId.equals(preGroupId))
                continue;
            String textLine = mOnDecorationCallback.onGroupFirstStr(position).toUpperCase();
            if (TextUtils.isEmpty(textLine))
                continue;

            int viewBottom = view.getBottom();
            // 当view.getTop()<mTopGap的时候，一直显示在顶部mTopGap的位置
            float textY = Math.max(mTopGap, view.getTop());

            if (position + 1 < itemCount){
                String nextGroupId = mOnDecorationCallback.onGroupId(position + 1);
                // 当后面一组的顶部位置到达当前组吸顶的底部时，将当前组吸顶往上移动（被顶出屏幕）
                if (!nextGroupId.equals(groupId) && viewBottom < textY){
                    textY = viewBottom;
                }
            }
            if (view.getTop() < textY){
                mOnDecorationCallback.onGroupFirstStr(textLine);
            }
            c.drawRect(left, textY - mTopGap, right, textY, mPaint);
            c.drawText(textLine, left + 2 * mAlignBottom, textY - mAlignBottom, mTextPaint);
        }
    }

    /**
     * 判断是否为组内的第一个item
     * @param pos
     * @return
     */
    private boolean isGroupFirstItem(int pos){
        if (pos == 0){
            return true;
        }else{
            String preGroupId = mOnDecorationCallback.onGroupId(pos - 1);
            String groupId = mOnDecorationCallback.onGroupId(pos);
            if (groupId.equals(preGroupId)){
                return false;
            }else {
                return true;
            }
        }
    }

    /**
     * 外部接口
     */
    interface OnDecorationCallback{
        String onGroupId(int pos);
        String onGroupFirstStr(int pos);
        void onGroupFirstStr(String title);
    }
}
