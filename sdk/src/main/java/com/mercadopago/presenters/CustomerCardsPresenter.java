package com.mercadopago.presenters;

import com.mercadopago.callbacks.FailureRecovery;
import com.mercadopago.callbacks.OnSelectedCallback;
import com.mercadopago.exceptions.MercadoPagoError;
import com.mercadopago.model.Card;
import com.mercadopago.model.Customer;
import com.mercadopago.mvp.MvpPresenter;
import com.mercadopago.mvp.TaggedCallback;
import com.mercadopago.providers.CustomerCardsProvider;
import com.mercadopago.util.ApiUtil;
import com.mercadopago.views.CustomerCardsView;

import java.util.List;

import static com.mercadopago.util.TextUtil.isEmpty;

/**
 * Created by mromar on 4/10/17.
 */

public class CustomerCardsPresenter extends MvpPresenter<CustomerCardsView, CustomerCardsProvider> {

    private String mCustomTitle;
    private String mSelectionConfirmPromptText;
    private String mActionMessage;
    private FailureRecovery mFailureRecovery;

    private List<Card> mCards;

    public void initialize() {
        if (mCards == null) {
            getCustomerAsync();
        } else {
            getView().showCards(mCards, mActionMessage, getOnSelectedCallback());
        }
    }

    private void getCustomerAsync() {
        getView().showProgress();

        getResourcesProvider().getCustomer(new TaggedCallback<Customer>(ApiUtil.RequestOrigin.GET_CUSTOMER) {
            @Override
            public void onSuccess(Customer customer) {
                mCards = customer.getCards();
                getView().hideProgress();
                getView().showCards(mCards, mActionMessage, getOnSelectedCallback());
            }

            @Override
            public void onFailure(MercadoPagoError error) {
                getView().showError(error, ApiUtil.RequestOrigin.GET_CUSTOMER);
                getView().hideProgress();

                setFailureRecovery(new FailureRecovery() {
                    @Override
                    public void recover() {
                        getCustomerAsync();
                    }
                });
            }
        });
    }

    private OnSelectedCallback<Card> getOnSelectedCallback() {
        return new OnSelectedCallback<Card>() {
            @Override
            public void onSelected(Card card) {
                if (card != null) {
                    resolveCardResponse(card);
                } else {
                    resolveActionMessage();
                }
            }
        };
    }

    private void resolveCardResponse(Card card) {
        if (isConfirmPromptEnabled()) {
            getView().showConfirmPrompt(card);
        } else {
            getView().finishWithCardResult(card);
        }
    }

    private void resolveActionMessage() {
        getView().finishWithOkResult();
    }

    private boolean isConfirmPromptEnabled() {
        return !isEmpty(mSelectionConfirmPromptText);
    }

    public void recoverFromFailure() {
        if (mFailureRecovery != null) {
            mFailureRecovery.recover();
        }
    }

    public List<Card> getCards() {
        return mCards;
    }

    public void setCards(List<Card> cards) {
        mCards = cards;
    }

    public String getCustomTitle() {
        return mCustomTitle;
    }

    public void setCustomTitle(String customTitle) {
        mCustomTitle = customTitle;
    }

    public String getSelectionConfirmPromptText() {
        return mSelectionConfirmPromptText;
    }

    public void setSelectionConfirmPromptText(String selectionConfirmPromptText) {
        mSelectionConfirmPromptText = selectionConfirmPromptText;
    }

    public void setCustomActionMessage(String customActionMessage) {
        mActionMessage = customActionMessage;
    }

    public void setFailureRecovery(FailureRecovery failureRecovery) {
        mFailureRecovery = failureRecovery;
    }
}
