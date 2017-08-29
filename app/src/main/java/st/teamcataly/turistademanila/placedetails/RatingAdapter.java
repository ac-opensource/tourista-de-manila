package st.teamcataly.turistademanila.placedetails;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.List;

import st.teamcataly.turistademanila.R;
import st.teamcataly.turistademanila.data.Feedback;


public class RatingAdapter extends PagerAdapter {
    private final List<Feedback> feedbacks;
    private Context context;

    public RatingAdapter(Context c, List<Feedback> feedbacks) {
        context = c;
        this.feedbacks = feedbacks;
    }

    public int getCount() {
        return feedbacks.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return object == view;
    }

    @Override
    public Object instantiateItem(ViewGroup collection, int position) {
        LayoutInflater inflater = LayoutInflater.from(context);
        ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.rating_dialog, collection, false);
        RatingBar ratingBar = (RatingBar) layout.findViewById(R.id.dialog_ratingbar);
        ratingBar.setRating(feedbacks.get(position).getRating());
        ratingBar.setIsIndicator(true);
        Button updateButton = (Button) layout.findViewById(R.id.rate_dialog_button);
        updateButton.setVisibility(View.GONE);
        TextView textView = (TextView) layout.findViewById(R.id.rate_title);
        textView.setVisibility(View.GONE);
        EditText feedbackForm = (EditText) layout.findViewById(R.id.feedback);
        feedbackForm.setEnabled(false);
        feedbackForm.setFocusable(false);
        feedbackForm.setText(feedbacks.get(position).getComment());
        collection.addView(layout);
        return layout;
    }

    @Override
    public void destroyItem(ViewGroup collection, int position, Object view) {
        collection.removeView((View) view);
    }
}