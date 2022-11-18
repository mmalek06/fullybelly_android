package com.fullybelly.views.adapters.images;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.fullybelly.R;

public class ListViewAdapter extends BaseAdapter {

    private Activity mActivity;
    private String[] mData;
    private static LayoutInflater mInflater = null;
    public ImageLoader mImageLoader;

    public ListViewAdapter(Activity a, String[] d) {
        mActivity = a;
        mData = d;
        mInflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mImageLoader = new ImageLoader(mActivity.getApplicationContext());
    }

    @Override
    public int getCount() {
        return mData.length;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi=convertView;

        if(convertView == null) {
            vi = mInflater.inflate(R.layout.partner_image, null);
        }

        ImageView image=(ImageView)vi.findViewById(R.id.image);

        mImageLoader.DisplayImage(mData[position], image);

        return vi;
    }
}
