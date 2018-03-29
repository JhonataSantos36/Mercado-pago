package com.mercadopago.presenters;

import com.mercadopago.callbacks.FailureRecovery;
import com.mercadopago.exceptions.MercadoPagoError;
import com.mercadopago.lite.model.Identification;
import com.mercadopago.lite.model.IdentificationType;
import com.mercadopago.lite.model.Payer;
import com.mercadopago.mvp.MvpPresenter;
import com.mercadopago.mvp.TaggedCallback;
import com.mercadopago.providers.PayerInformationProvider;
import com.mercadopago.util.ApiUtil;
import com.mercadopago.views.PayerInformationView;

import java.util.ArrayList;
import java.util.List;

import static com.mercadopago.util.TextUtil.isEmpty;

/**
 * Created by mromar on 9/25/17.
 */

public class PayerInformationPresenter extends MvpPresenter<PayerInformationView, PayerInformationProvider> {

    //Activity parameters
    private Payer mPayer;

    //Payer info
    private String mIdentificationNumber;
    private String mIdentificationName;
    private String mIdentificationLastName;
    private String mIdentificationBusinessName;
    private Identification mIdentification;
    private IdentificationType mIdentificationType;
    private List<IdentificationType> mIdentificationTypes;

    private FailureRecovery mFailureRecovery;

    private static final int DEFAULT_IDENTIFICATION_NUMBER_LENGTH = 12;
    private static final String IDENTIFICATION_TYPE_CPF = "CPF";

    public PayerInformationPresenter() {
        mIdentification = new Identification();
    }

    public void initialize() {
        getIdentificationTypesAsync();
    }

    private void getIdentificationTypesAsync() {
        getView().showProgressBar();

        getResourcesProvider().getIdentificationTypesAsync(new TaggedCallback<List<IdentificationType>>(ApiUtil.RequestOrigin.GET_IDENTIFICATION_TYPES) {
            @Override
            public void onSuccess(List<IdentificationType> identificationTypes) {
                resolveIdentificationTypes(identificationTypes);
                getView().hideProgressBar();
                getView().showInputContainer();
            }

            @Override
            public void onFailure(MercadoPagoError error) {
                if (isViewAttached()) {
                    getView().showError(error, ApiUtil.RequestOrigin.GET_IDENTIFICATION_TYPES);
                    setFailureRecovery(new FailureRecovery() {
                        @Override
                        public void recover() {
                            getIdentificationTypesAsync();
                        }
                    });
                }
            }
        });
    }

    private void resolveIdentificationTypes(List<IdentificationType> identificationTypes) {
        if (identificationTypes.isEmpty()) {
            getView().showError(new MercadoPagoError(getResourcesProvider().getMissingIdentificationTypesErrorMessage(), false), ApiUtil.RequestOrigin.GET_IDENTIFICATION_TYPES);
        } else {
            mIdentificationType = identificationTypes.get(0);
            getView().initializeIdentificationTypes(identificationTypes);
            mIdentificationTypes = getCPFIdentificationTypes(identificationTypes);
        }
    }

    private List<IdentificationType> getCPFIdentificationTypes(List<IdentificationType> identificationTypes) {
        List<IdentificationType> identificationTypesList = new ArrayList<>();

        for (IdentificationType identificationType : identificationTypes) {
            if (identificationType.getId().equals(IDENTIFICATION_TYPE_CPF)) {
                identificationTypesList.add(identificationType);
            }
        }

        return identificationTypesList;
    }


    public void saveIdentificationNumber(String identificationNumber) {
        mIdentificationNumber = identificationNumber;
        mIdentification.setNumber(identificationNumber);
    }

    public void saveIdentificationName(String identificationName) {
        mIdentificationName = identificationName;
    }

    public void saveIdentificationLastName(String identificationLastName) {
        mIdentificationLastName = identificationLastName;
    }

    public int getIdentificationNumberMaxLength() {
        int maxLength = DEFAULT_IDENTIFICATION_NUMBER_LENGTH;

        if (mIdentificationType != null) {
            maxLength = mIdentificationType.getMaxLength();
        }
        return maxLength;
    }

    public void saveIdentificationType(IdentificationType identificationType) {
        mIdentificationType = identificationType;
        if (identificationType != null) {
            mIdentification.setType(identificationType.getId());
            getView().setIdentificationNumberRestrictions(identificationType.getType());
        }
    }

    public void createPayer() {
        mPayer = new Payer();

        mPayer.setFirstName(mIdentificationName);
        mPayer.setLastName(mIdentificationLastName);
        mPayer.setIdentification(mIdentification);
    }

    public FailureRecovery getFailureRecovery() {
        return mFailureRecovery;
    }

    public void setFailureRecovery(FailureRecovery failureRecovery) {
        mFailureRecovery = failureRecovery;
    }

    public void recoverFromFailure() {
        if (mFailureRecovery != null) {
            mFailureRecovery.recover();
        }
    }


    public boolean validateIdentificationNumber() {
        boolean isIdentificationNumberValid = validateIdentificationNumberLength();

        if (isIdentificationNumberValid) {
            getView().clearErrorView();
            getView().clearErrorIdentificationNumber();
        } else {
            getView().setErrorView(getResourcesProvider().getInvalidIdentificationNumberErrorMessage());
            getView().setErrorIdentificationNumber();
        }

        return isIdentificationNumberValid;
    }

    private boolean validateIdentificationNumberLength() {
        if (mIdentificationType != null) {
            if ((mIdentification != null) &&
                    (mIdentification.getNumber() != null)) {
                int len = mIdentification.getNumber().length();
                Integer min = mIdentificationType.getMinLength();
                Integer max = mIdentificationType.getMaxLength();
                if ((min != null) && (max != null)) {
                    return ((len <= max) && (len >= min));
                } else {
                    return validateNumber();
                }
            } else {
                return false;
            }
        } else {
            return validateNumber();
        }
    }

    private boolean validateNumber() {
        return mIdentification != null && validateIdentificationType() && !isEmpty(mIdentification.getNumber());
    }

    private boolean validateIdentificationType() {
        return mIdentification != null && !isEmpty(mIdentification.getType());
    }

    public boolean checkIsEmptyOrValidName() {
        return isEmpty(mIdentificationName) || validateName();
    }

    public boolean checkIsEmptyOrValidLastName() {
        return isEmpty(mIdentificationLastName) || validateLastName();
    }

    public boolean validateName() {
        boolean isNameValid = validateString(mIdentificationName);

        if (isNameValid) {
            getView().clearErrorView();
            getView().clearErrorName();
        } else {
            getView().setErrorView(getResourcesProvider().getInvalidIdentificationNameErrorMessage());
            getView().setErrorName();
        }

        return isNameValid;
    }

    public boolean validateLastName() {
        boolean isLastNameValid = validateString(mIdentificationLastName);

        if (isLastNameValid) {
            getView().clearErrorView();
            getView().clearErrorLastName();
        } else {
            getView().setErrorView(getResourcesProvider().getInvalidIdentificationLastNameErrorMessage());
            getView().setErrorLastName();
        }

        return isLastNameValid;
    }

    public boolean validateBusinessName() {
        boolean isBusinessNameValid = validateString(mIdentificationBusinessName);

        if (isBusinessNameValid) {
            getView().clearErrorView();
            //TODO fix when cnpj is available
            getView().clearErrorName();
        } else {
            getView().setErrorView(getResourcesProvider().getInvalidIdentificationBusinessNameErrorMessage());
            //TODO fix when cnpj is available
            getView().setErrorName();
        }

        return isBusinessNameValid;
    }

    private boolean validateString(String string) {
        return !isEmpty(string);
    }


    public Payer getPayer() {
        return mPayer;
    }

    public IdentificationType getIdentificationType() {
        return mIdentificationType;
    }

    public String getIdentificationNumber() {
        return mIdentificationNumber;
    }

    public String getIdentificationName() {
        return mIdentificationName;
    }

    public String getIdentificationLastName() {
        return mIdentificationLastName;
    }

    public Identification getIdentification() {
        return mIdentification;
    }

    public List<IdentificationType> getIdentificationTypes() {
        return mIdentificationTypes;
    }

    public void setPayer(Payer mPayer) {
        this.mPayer = mPayer;
    }

    public void setIdentificationType(IdentificationType mIdentificationType) {
        this.mIdentificationType = mIdentificationType;
    }

    public void setIdentificationNumber(String mIdentificationNumber) {
        this.mIdentificationNumber = mIdentificationNumber;
    }

    public void setIdentificationName(String mIdentificationName) {
        this.mIdentificationName = mIdentificationName;
    }

    public void setIdentificationLastName(String mIdentificationLastName) {
        this.mIdentificationLastName = mIdentificationLastName;
    }

    public void setIdentification(Identification mIdentification) {
        this.mIdentification = mIdentification;
    }

    public void setIdentificationTypesList(List<IdentificationType> mIdentificationTypes) {
        this.mIdentificationTypes = mIdentificationTypes;
    }
}
