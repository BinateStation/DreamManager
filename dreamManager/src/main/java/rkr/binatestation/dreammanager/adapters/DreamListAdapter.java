package rkr.binatestation.dreammanager.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Currency;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import rkr.binatestation.dreammanager.R;
import rkr.binatestation.dreammanager.activities.DreamActivity;
import rkr.binatestation.dreammanager.models.DreamModel;

import static rkr.binatestation.dreammanager.utils.Constants.KEY_DREAM_MODEL;
import static rkr.binatestation.dreammanager.utils.GeneralUtils.monthsBetweenDates;

/**
 * Created by RKR on 16-03-2017.
 * DreamListAdapter.
 */

public class DreamListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "DreamListAdapter";
    private Currency mCurrency;
    private List<DreamModel> mDreamModelList = new ArrayList<>();

    public DreamListAdapter() {
        mCurrency = Currency.getInstance(Locale.getDefault());
    }

    public void setDreamModelList(List<DreamModel> dreamModelList) {
        this.mDreamModelList = dreamModelList;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new DreamItemHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_dream_item, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        DreamItemHolder itemHolder = holder instanceof DreamItemHolder ? ((DreamItemHolder) holder) : null;
        DreamModel dreamModel = mDreamModelList.get(position);
        if (itemHolder != null && dreamModel != null) {
            Context context = itemHolder.itemView.getContext();
            itemHolder.mDreamNameTextView.setText(dreamModel.getName());
            itemHolder.mAchieveOnTextView.setText(String.format("Likely to achieve on %s", DateUtils.formatDateTime(context, dreamModel.getAchieveDate(), DateUtils.FORMAT_NO_MONTH_DAY)));
            itemHolder.mTargetAmount.setText(String.format(Locale.getDefault(), "%s%.2f/%.2f", mCurrency.getSymbol(), dreamModel.getAmountSpentTillDay(), dreamModel.getTargetAmount()));
            itemHolder.mPerMonthCost.setText(String.format(Locale.getDefault(), "Save %s%.2f/Month", mCurrency.getSymbol(), calculateAmountToSpentPerMonth(dreamModel)));
            itemHolder.mProgressBar.setMax((int) dreamModel.getTargetAmount());
            itemHolder.mProgressBar.setProgress((int) dreamModel.getAmountSpentTillDay());
            setImageView(itemHolder.mDreamImageView, dreamModel.getImagePath());
        }
    }

    private double calculateAmountToSpentPerMonth(DreamModel dreamModel) {
        Log.d(TAG, "calculateAmountToSpentPerMonth() called");
        double amountDouble = dreamModel.getTargetAmount() - dreamModel.getAmountSpentTillDay();
        int noOfMonths = monthsBetweenDates(new Date(), new Date(dreamModel.getAchieveDate()));
        double amountPerMonth = amountDouble / noOfMonths;
        Log.d(TAG, "calculateAmountToSpentPerMonth() returned: " + amountPerMonth);
        return amountPerMonth;
    }

    private void setImageView(final ImageView mDreamImageView, final String imagePath) {
        mDreamImageView.post(new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
                Context context = mDreamImageView.getContext();
                int dimen = (int) context.getResources().getDimension(R.dimen.item_image_size);
                Bitmap resized = ThumbnailUtils.extractThumbnail(bitmap, dimen, dimen);
                mDreamImageView.setImageBitmap(resized);
                if (resized == null) {
                    mDreamImageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_image_black_24dp));
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return mDreamModelList.size();
    }

    private class DreamItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView mDreamImageView;
        private TextView mDreamNameTextView;
        private TextView mAchieveOnTextView;
        private TextView mTargetAmount;
        private TextView mPerMonthCost;
        private ProgressBar mProgressBar;

        DreamItemHolder(View itemView) {
            super(itemView);

            mDreamImageView = (ImageView) itemView.findViewById(R.id.ADI_dream_image);
            mDreamNameTextView = (TextView) itemView.findViewById(R.id.ADI_dream_name);
            mAchieveOnTextView = (TextView) itemView.findViewById(R.id.ADI_achieve_on);
            mTargetAmount = (TextView) itemView.findViewById(R.id.ADI_target_amount);
            mPerMonthCost = (TextView) itemView.findViewById(R.id.ADI_to_spent_per_month);
            mProgressBar = (ProgressBar) itemView.findViewById(R.id.ADI_dream_progress);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Context context = v.getContext();
            context.startActivity(new Intent(context, DreamActivity.class)
                    .putExtra(KEY_DREAM_MODEL, mDreamModelList.get(getAdapterPosition())));
        }
    }
}
