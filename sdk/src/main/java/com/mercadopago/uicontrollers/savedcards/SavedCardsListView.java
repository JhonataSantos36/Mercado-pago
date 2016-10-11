package com.mercadopago.uicontrollers.savedcards;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mercadopago.R;
import com.mercadopago.adapters.CustomerCardsAdapter;
import com.mercadopago.callbacks.OnSelectedCallback;
import com.mercadopago.model.Card;
import com.mercadopago.uicontrollers.CustomViewController;

import java.util.List;

/**
 * Created by mreverter on 5/10/16.
 */
public class SavedCardsListView implements CustomViewController {

    private View mView;
    private RecyclerView mSavedCardsRecyclerView;
    private TextView mFooterTextView;
    private Context mContext;
    private String mFooterText;
    private List<Card> mCards;
    private OnSelectedCallback<Card> mOnSelectedCallback;
    private Integer mSelectionImageResId;
    private View mFooter;

    public SavedCardsListView(Context context, List<Card> cards, String footerText, int selectionImageResId, OnSelectedCallback<Card> onSelectedCallback) {
        mContext = context;
        mFooterText = footerText;
        mCards = cards;
        mSelectionImageResId = selectionImageResId;
        mOnSelectedCallback = onSelectedCallback;
    }

    @Override
    public void initializeControls() {
        mSavedCardsRecyclerView = (RecyclerView) mView.findViewById(R.id.mpsdkCustomerCardsList);
        mFooterTextView = (TextView) mView.findViewById(R.id.mpsdkDescription);
        mFooter = mView.findViewById(R.id.footer);
    }

    @Override
    public View inflateInParent(ViewGroup parent, boolean attachToRoot) {
        mView = LayoutInflater.from(mContext)
                .inflate(R.layout.mpsdk_saved_cards_list_row, parent, attachToRoot);
        return mView;
    }

    @Override
    public View getView() {
        return mView;
    }

    public void drawInParent(ViewGroup parent) {
        inflateInParent(parent, true);
        initializeControls();
        if (!TextUtils.isEmpty(mFooterText)) {
            mFooter.setVisibility(View.VISIBLE);
            mFooterTextView.setText(mFooterText);
        }
        mSavedCardsRecyclerView.setHasFixedSize(true);

        // Set a linear layout manager
        mSavedCardsRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));

        // Load cards
        mSavedCardsRecyclerView.setAdapter(new CustomerCardsAdapter(mContext, mCards, mOnSelectedCallback, mSelectionImageResId));

    }
}
