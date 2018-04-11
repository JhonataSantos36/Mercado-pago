package com.mercadopago.providers;

import com.mercadopago.model.IdentificationType;
import com.mercadopago.mvp.TaggedCallback;
import com.mercadopago.mvp.ResourcesProvider;

import java.util.List;

/**
 * Created by mromar on 22/09/17.
 */

public interface PayerInformationProvider extends ResourcesProvider {

    void getIdentificationTypesAsync(final TaggedCallback<List<IdentificationType>> taggedCallback);

    String getInvalidIdentificationNumberErrorMessage();

    String getInvalidIdentificationNameErrorMessage();

    String getInvalidIdentificationLastNameErrorMessage();

    String getInvalidIdentificationBusinessNameErrorMessage();

    String getMissingPublicKeyErrorMessage();

    String getMissingIdentificationTypesErrorMessage();
}
