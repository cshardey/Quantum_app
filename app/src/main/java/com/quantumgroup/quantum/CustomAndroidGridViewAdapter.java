package com.quantumgroup.quantum;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;


import com.balysv.materialripple.MaterialRippleLayout;


import de.hdodenhof.circleimageview.CircleImageView;


/**
 * Created by Mr. Vanson on 8/21/2016.
 */
public class CustomAndroidGridViewAdapter extends BaseAdapter{
    private Quantum mContext;
    private final String[] string;
    private final int[] Imageid;

    public CustomAndroidGridViewAdapter(Quantum context,String[] string,int[] Imageid ) {
        this.mContext=context;
        this.Imageid = Imageid;
        this.string = string;

    }

    @Override
    public int getCount() {
        return string.length;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int p) {
        return p;
    }

    @Override
    public boolean isEnabled(int position) {
        return super.isEnabled(position);
    }

    @Override
    public View getView(final int p, View convertView, ViewGroup parent) {
        View grid;
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {

            grid = new View(mContext);
            grid = inflater.inflate(R.layout.list, null);
            TextView textView = (TextView) grid.findViewById(R.id.gridview_text);
            CircleImageView imageView = (CircleImageView)grid.findViewById(R.id.menu_img);
            MaterialRippleLayout rippleView = (MaterialRippleLayout) grid.findViewById(R.id.ripple);
            textView.setText(string[p]);
            rippleView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                   mContext.itemClicked(p);
                }
            });
            imageView.setImageResource(Imageid[p]);

        } else {
            grid = (View) convertView;
        }


        return grid;
    }




}
