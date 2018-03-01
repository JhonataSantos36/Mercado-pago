package com.mercadopago.payerInformation;

import com.mercadopago.exceptions.MercadoPagoError;
import com.mercadopago.mocks.IdentificationTypes;
import com.mercadopago.model.Identification;
import com.mercadopago.model.IdentificationType;
import com.mercadopago.mvp.OnResourcesRetrievedCallback;
import com.mercadopago.presenters.PayerInformationPresenter;
import com.mercadopago.providers.PayerInformationProvider;
import com.mercadopago.views.PayerInformationView;

import org.junit.Test;

import java.util.List;

import static junit.framework.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by mromar on 10/4/17.
 */

public class PayerInformationPresenterTest {

    @Test
    public void whenInitializePresenterThenInitializeIdentificationTypes() {
        MockedView mockedView = new MockedView();
        MockedProvider provider = new MockedProvider();

        List<IdentificationType> identificationTypesList = IdentificationTypes.getIdentificationTypes();
        provider.setIdentificationTypesResponse(identificationTypesList);

        PayerInformationPresenter presenter = new PayerInformationPresenter();
        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(provider);

        presenter.initialize();

        assertTrue(mockedView.initializeIdentificationTypes);
    }

    @Test
    public void whenGetIdentificationTypesFailThenShowError() {
        MockedView mockedView = new MockedView();
        MockedProvider provider = new MockedProvider();

        MercadoPagoError mercadoPagoError = new MercadoPagoError("bad_request", false);
        provider.setIdentificationTypesResponse(mercadoPagoError);

        PayerInformationPresenter presenter = new PayerInformationPresenter();
        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(provider);

        presenter.initialize();

        assertTrue(mockedView.showError);
        assertTrue(mockedView.mercadoPagoError.getMessage().equals("bad_request"));
    }

    @Test
    public void clearErrorNameWhenNameIsValid() {
        MockedView mockedView = new MockedView();
        MockedProvider provider = new MockedProvider();

        List<IdentificationType> identificationTypesList = IdentificationTypes.getIdentificationTypes();
        provider.setIdentificationTypesResponse(identificationTypesList);

        PayerInformationPresenter presenter = new PayerInformationPresenter();
        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(provider);

        presenter.setIdentificationName("Name");

        assertTrue(presenter.validateName());
        assertTrue(mockedView.clearErrorName);
        assertTrue(mockedView.clearErrorView);
    }

    @Test
    public void setErrorViewWhenNameIsNotValid() {
        MockedView mockedView = new MockedView();
        MockedProvider provider = new MockedProvider();

        List<IdentificationType> identificationTypesList = IdentificationTypes.getIdentificationTypes();
        provider.setIdentificationTypesResponse(identificationTypesList);

        PayerInformationPresenter presenter = new PayerInformationPresenter();
        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(provider);

        presenter.setIdentificationName("");

        assertFalse(presenter.validateName());
        assertTrue(mockedView.setErrorName);
        assertTrue(mockedView.setErrorView);
    }

    @Test
    public void setErrorViewWhenNumberIsNotValid() {
        MockedView mockedView = new MockedView();
        MockedProvider provider = new MockedProvider();

        List<IdentificationType> identificationTypesList = IdentificationTypes.getIdentificationTypes();
        provider.setIdentificationTypesResponse(identificationTypesList);

        IdentificationType identificationType = getIdentificationTypeCPF();
        Identification identification = getIdentificationWithWrongNumberCPF();

        PayerInformationPresenter presenter = new PayerInformationPresenter();
        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(provider);
        presenter.setIdentificationType(identificationType);
        presenter.setIdentification(identification);

        assertFalse(presenter.validateIdentificationNumber());
        assertTrue(mockedView.setErrorIdentificationNumber);
        assertTrue(mockedView.setErrorView);
    }

    @Test
    public void clearErrorNumberWhenNumberIsValid() {
        MockedView mockedView = new MockedView();
        MockedProvider provider = new MockedProvider();

        List<IdentificationType> identificationTypesList = IdentificationTypes.getIdentificationTypes();
        provider.setIdentificationTypesResponse(identificationTypesList);

        IdentificationType identificationType = getIdentificationTypeCPF();
        Identification identification = getIdentificationCPF();

        PayerInformationPresenter presenter = new PayerInformationPresenter();
        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(provider);
        presenter.setIdentificationType(identificationType);
        presenter.setIdentification(identification);

        assertTrue(presenter.validateIdentificationNumber());
        assertTrue(mockedView.clearErrorIdentificationNumber);
        assertTrue(mockedView.clearErrorView);
    }

    private Identification getIdentificationCPF() {
        String type = "CPF";
        String identificationNumber = "89898989898";

        Identification identification = new Identification();
        identification.setNumber(identificationNumber);
        identification.setType(type);

        return identification;
    }

    private Identification getIdentificationWithWrongNumberCPF() {
        String type = "CPF";
        String identificationNumber = "";

        Identification identification = new Identification();
        identification.setNumber(identificationNumber);
        identification.setType(type);

        return identification;
    }

    private IdentificationType getIdentificationTypeCPF() {
        String type = "number";
        String id = "CPF";
        String name = "CPF";

        IdentificationType identificationType = new IdentificationType();
        identificationType.setType(type);
        identificationType.setId(id);
        identificationType.setMaxLength(11);
        identificationType.setMinLength(11);
        identificationType.setName(name);

        return identificationType;
    }

    private class MockedView implements PayerInformationView {

        private boolean initializeIdentificationTypes;
        private boolean showError;
        private boolean clearErrorView;
        private boolean clearErrorName;
        private boolean setErrorName;
        private boolean setErrorView;
        private boolean clearErrorIdentificationNumber;
        private boolean setErrorIdentificationNumber;
        private String errorMessage;

        private MercadoPagoError mercadoPagoError;


        @Override
        public void initializeIdentificationTypes(List<IdentificationType> identificationTypes) {
            this.initializeIdentificationTypes = true;
        }

        @Override
        public void setIdentificationNumberRestrictions(String type) {
            //Add test
        }

        @Override
        public void clearErrorIdentificationNumber() {
            this.clearErrorIdentificationNumber = true;
        }

        @Override
        public void clearErrorName() {
            this.clearErrorName = true;
        }

        @Override
        public void clearErrorLastName() {
            //Add test
        }

        @Override
        public void setErrorIdentificationNumber() {
            this.setErrorIdentificationNumber = true;
        }

        @Override
        public void setErrorName() {
            this.setErrorName = true;
        }

        @Override
        public void setErrorLastName() {
            //Add test
        }

        @Override
        public void setErrorView(String message) {
            this.errorMessage = message;
            this.setErrorView = true;
        }

        @Override
        public void clearErrorView() {
            this.clearErrorView = true;
        }

        @Override
        public void showInputContainer() {
            //Add test
        }

        @Override
        public void showError(MercadoPagoError error, String requestOrigin) {
            this.mercadoPagoError = error;
            this.showError = true;
        }

        @Override
        public void showProgressBar() {
            //Add test
        }

        @Override
        public void hideProgressBar() {
            //Add test
        }
    }

    private class MockedProvider implements PayerInformationProvider {

        private boolean shouldFail;
        private MercadoPagoError failedResponse;
        private List<IdentificationType> successfulIdentificationTypesResponse;

        @Override
        public void getIdentificationTypesAsync(OnResourcesRetrievedCallback<List<IdentificationType>> onResourcesRetrievedCallback) {
            if (shouldFail) {
                onResourcesRetrievedCallback.onFailure(failedResponse);
            } else {
                onResourcesRetrievedCallback.onSuccess(successfulIdentificationTypesResponse);
            }
        }

        @Override
        public String getInvalidIdentificationNumberErrorMessage() {
            return null;
        }

        @Override
        public String getInvalidIdentificationNameErrorMessage() {
            return null;
        }

        @Override
        public String getInvalidIdentificationLastNameErrorMessage() {
            return null;
        }

        @Override
        public String getInvalidIdentificationBusinessNameErrorMessage() {
            return null;
        }

        @Override
        public String getMissingPublicKeyErrorMessage() {
            return null;
        }

        @Override
        public String getMissingIdentificationTypesErrorMessage() {
            return null;
        }

        public void setIdentificationTypesResponse(List<IdentificationType> identificationTypes) {
            shouldFail = false;
            successfulIdentificationTypesResponse = identificationTypes;
        }

        public void setIdentificationTypesResponse(MercadoPagoError exception) {
            shouldFail = true;
            failedResponse = exception;
        }
    }
}
