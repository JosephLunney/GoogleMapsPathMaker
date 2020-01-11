package com.example.googlemapspractice;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class PermissionsAdapter extends BaseAdapter {

    String[] permissionsNames ;
    boolean[] permissions ;
    LayoutInflater mInflater ;

    public PermissionsAdapter(Context c, String[] permissionsNames, boolean[] permissions) {
        this.permissionsNames = permissionsNames ;
        this.permissions = permissions ;
        mInflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;
    }

    @Override
    public int getCount() {
        return permissionsNames.length;
    }

    @Override
    public Object getItem(int position) {
        return permissionsNames[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = mInflater.inflate(R.layout.permissions_check_list_detail, null);
        TextView permissionsItem = (TextView) v.findViewById(R.id.permissionItem) ;
        ImageView checkMark = (ImageView) v.findViewById(R.id.CheckOrX) ;

        permissionsItem.setText(permissionsNames[position]) ;

        if (permissions[position]) {
            checkMark.setImageResource(R.drawable.checkmark);
        } else {
            checkMark.setImageResource(R.drawable.redx);
        }



        return v;
    }
}
