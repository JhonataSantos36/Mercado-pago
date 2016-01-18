package com.mercadopago.controllers;

import android.app.Activity;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.mercadopago.R;
import com.mercadopago.adapters.PaymentMethodsSpinnerAdapter;
import com.mercadopago.callbacks.PaymentMethodSelectionCallback;
import com.mercadopago.core.MercadoPago;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.util.MercadoPagoUtil;

import java.util.List;

/**
 * Created by mreverter on 28/12/15.
 */
public class PaymentMethodGuessingController {

    private Activity mActivity;

    private ImageView mImagePaymentMethod;
    private EditText mCardNumber;
    private LinearLayout mPaymentMethodLayout;
    private Spinner mSpinnerPaymentMethods;

    private String mSavedBin;
    private String mSavedPaymentMethodId;
    private boolean mCardNumberBlocked;
    private List<PaymentMethod> mPaymentMethods;

    private PaymentMethodSelectionCallback mPaymentMethodSelectionCallback;

    public PaymentMethodGuessingController(Activity activity, List<PaymentMethod> paymentMethods, PaymentMethodSelectionCallback paymentMethodSelectionCallback){
        this.mActivity = activity;
        this.mPaymentMethods = paymentMethods;
        this.mPaymentMethodSelectionCallback = paymentMethodSelectionCallback;

        initializeComponents();
    }

    private void initializeComponents() {
        mImagePaymentMethod = (ImageView) mActivity.findViewById(R.id.pmImage);

        mCardNumber = (EditText) mActivity.findViewById(R.id.cardNumber);
        mCardNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable cardNumber) {
                if (cardNumber.length() < MercadoPago.BIN_LENGTH) {
                    if(mCardNumberBlocked)
                        unBlockCardNumbersInput(mCardNumber);
                    clearGuessing();
                }
                else
                    setPaymentMethodLayoutForBin(cardNumber.subSequence(0, MercadoPago.BIN_LENGTH).toString());
            }
        });
        mCardNumber.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (mCardNumberBlocked)
                    setInvalidCardNumberError();
                return false;
            }
        });

        mSavedPaymentMethodId = "";
        mSpinnerPaymentMethods = (Spinner) mActivity.findViewById(R.id.spinnerPaymentMethod);
        mPaymentMethodLayout = (LinearLayout) mActivity.findViewById(R.id.paymentMethodSelectionLayout);

        mPaymentMethodLayout.setVisibility(View.GONE);
        mImagePaymentMethod.setImageDrawable(null);

    }

    private void setPaymentMethodLayoutForBin(String bin) {
        if(!bin.equals(mSavedBin)) {
            mSavedBin = bin;
            List<PaymentMethod> validPaymentMethods = MercadoPago.getValidPaymentMethodsForBin(mSavedBin, this.mPaymentMethods);
            refreshPaymentMethodLayout(validPaymentMethods);
        }
    }

    private void clearGuessing() {
        mSavedBin = "";
        clearPaymentMethodLayout();
    }

    private void refreshPaymentMethodLayout(List<PaymentMethod> paymentMethods) {

        clearPaymentMethodLayout();

        if(paymentMethods.isEmpty()) {
            blockCardNumbersInput(mCardNumber);
            this.setInvalidCardNumberError();
        }
        else if(paymentMethods.size() == 1){
            showPaymentMethodImage(paymentMethods.get(0));
            mPaymentMethodSelectionCallback.onPaymentMethodSet(paymentMethods.get(0));
        }
        else{
            showPaymentMethodsSelector();
            populatePaymentMethodSpinner(paymentMethods);
        }
    }

    private void blockCardNumbersInput(EditText text) {
        int maxLength = MercadoPago.BIN_LENGTH;
        setInputMaxLength(text, maxLength);
        mCardNumberBlocked = true;
    }

    private void unBlockCardNumbersInput(EditText text){

        int maxLength = 16;
        setInputMaxLength(text, maxLength);
        mCardNumberBlocked = false;
    }

    private void setInputMaxLength(EditText text, int maxLength) {
        InputFilter[] fArray = new InputFilter[1];
        fArray[0] = new InputFilter.LengthFilter(maxLength);
        text.setFilters(fArray);
    }

    private void setInvalidCardNumberError() {
        mCardNumber.setError(mActivity.getString(R.string.mpsdk_invalid_card_luhn));
    }

    public void setCardNumberError(String cardNumberError) {
        mCardNumber.setError(cardNumberError);
    }

    public void requestFocusForCardNumber() {
        mCardNumber.requestFocus();
    }

    public String getCardNumberText() {
        return mCardNumber.getText().toString();
    }

    public void showPaymentMethodsSelector() {
        mPaymentMethodLayout.setVisibility(View.VISIBLE);
    }

    public void populatePaymentMethodSpinner(final List<PaymentMethod> paymentMethods) {

        mSpinnerPaymentMethods.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                PaymentMethod paymentMethod = (PaymentMethod) mSpinnerPaymentMethods.getSelectedItem();
                if (paymentMethod != null) {
                    if (!mSavedPaymentMethodId.equals(paymentMethod.getId())) {

                        mSavedPaymentMethodId = paymentMethod.getId();
                        mPaymentMethodSelectionCallback.onPaymentMethodSet(paymentMethod);
                        showPaymentMethodImage(paymentMethod);

                    }
                } else if (!mSavedPaymentMethodId.equals("")) {
                    mSavedPaymentMethodId = "";
                    mPaymentMethodSelectionCallback.onPaymentMethodCleared();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        mSpinnerPaymentMethods.setAdapter(new PaymentMethodsSpinnerAdapter(mActivity, paymentMethods));
    }

    public void showPaymentMethodImage(PaymentMethod paymentMethod) {
        mImagePaymentMethod.setImageResource(MercadoPagoUtil.getPaymentMethodIcon(mActivity, paymentMethod.getId()));
    }

    public void clearPaymentMethodLayout() {
        mPaymentMethodLayout.setVisibility(View.GONE);
        mImagePaymentMethod.setImageDrawable(null);
        mPaymentMethodSelectionCallback.onPaymentMethodCleared();
    }

    public void setPaymentMethodError(String error) {
        if(mPaymentMethodLayout.getVisibility() == View.VISIBLE) {
            PaymentMethodsSpinnerAdapter adapter = (PaymentMethodsSpinnerAdapter) mSpinnerPaymentMethods.getAdapter();
            View view = mSpinnerPaymentMethods.getSelectedView();
            adapter.setError(view, error);
        }
        else
            setCardNumberError(error);
    }
}
