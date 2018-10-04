package com.cruxbd.master_planner_pro.adapters;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cruxbd.master_planner_pro.R;

/**
 * Created by Mridul on 09-Apr-18.
 */

public class SlideAdapter extends PagerAdapter {
    final int sdk = android.os.Build.VERSION.SDK_INT;
    Context context;
    LayoutInflater inflater;

    // list of images
    public int[] lst_images = {
            R.drawable.master_planner_borderless,
            R.drawable.earth_location_slider,
            R.drawable.format_list_checks,
            R.drawable.history_slide,
            R.drawable.cloud_check
    };

    //list of titles
    public String[] lst_title = {
            "Master Planner",
            "Location Based Reminder",
            "To-do, Reminder",
            "Countdown",
            "Cloud Backup and Data Privacy"
    };

    //list of description
    public String[] lst_description = {

            "Plan your project and tasks using our card type “Master Planner”. " +
                    "You can also organize your billing & expenses by Master Planner.",
            " Set reminder based on location you are entering or leaving.",

            "Create and organize your To-do, Reminder by Tag list so it’s easy to filter.",

            "Add your task in countdown section to be more focused.",

            "Backup your data in your own google drive account and we do not track your data."

    };

    //list of background color
//    public  int[] lst_background_color = {
//            Color.rgb(66, 175, 144),
//            Color.rgb(74, 178, 148),
//            Color.rgb(66, 175, 144),
//            Color.rgb(74, 178, 148),
//    };

    public int[] lst_background_color = {
            R.drawable.slide_background1,
            R.drawable.slide_background2,
            R.drawable.slide_background3,
            R.drawable.slide_background4,
            R.drawable.slide_background5
    };


    public SlideAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return lst_title.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return (view==(LinearLayout)object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.slide, container, false);

        LinearLayout layoutslide = view.findViewById(R.id.slideLinearLayout);
        ImageView imgSlide = view.findViewById(R.id.slideImage);
        TextView tvTitle = view.findViewById(R.id.tvTitle);
        TextView tvDescription = view.findViewById(R.id.tvDescription);

//        layoutslide.setBackgroundColor(lst_background_color[position]);


//        if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
//            layoutslide.setBackgroundDrawable(ContextCompat.getDrawable(container.getContext(), lst_background_color[position]) );
//        } else {
//            layoutslide.setBackground(ContextCompat.getDrawable(container.getContext(), lst_background_color[position]));
//        }

        layoutslide.setBackgroundResource(lst_background_color[position]);
        imgSlide.setImageResource(lst_images[position]);
        tvTitle.setText(lst_title[position]);
        tvDescription.setText(lst_description[position]);

        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout)object);
    }
}
