package com.santhossh.restaurant_management_system;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RatingBar;
import android.widget.TextView;
import java.util.ArrayList;

public class FeedbackAdapter_laks extends BaseAdapter {

    private Context context;
    private ArrayList<Feedback_laks> feedbackLaksList;

    public FeedbackAdapter_laks(Context context, ArrayList<Feedback_laks> feedbackLaksList) {
        this.context = context;
        this.feedbackLaksList = feedbackLaksList;
    }

    @Override
    public int getCount() {
        return feedbackLaksList.size();
    }

    @Override
    public Object getItem(int position) {
        return feedbackLaksList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_laks, parent, false);
        }

        Feedback_laks currentFeedbackLaks = (Feedback_laks) getItem(position);

        // Set Name instead of Order ID
        TextView nameView = convertView.findViewById(R.id.orderId);
        nameView.setText("Name: " + currentFeedbackLaks.getName());

        // Set title (feedback details)
        TextView titleView = convertView.findViewById(R.id.reviewTitle);
        titleView.setText(currentFeedbackLaks.getTitle());

        // Set rating
        RatingBar ratingBar = convertView.findViewById(R.id.ratingBar);
        ratingBar.setRating(currentFeedbackLaks.getRating());

        return convertView;
    }
}
