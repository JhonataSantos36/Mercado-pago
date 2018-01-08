package com.mercadopago.paymentresult.components;

import android.view.LayoutInflater;
import android.view.View;

import com.mercadopago.R;
import com.mercadopago.components.Renderer;
import com.mercadopago.customviews.MPTextView;
import com.mercadopago.paymentresult.components.AccreditationComment;

/**
 * Created by vaserber on 11/13/17.
 */

public class AccreditationCommentRenderer extends Renderer<AccreditationComment> {

    @Override
    public View render() {
        final View accreditationCommentView = LayoutInflater.from(context).inflate(R.layout.mpsdk_accreditation_comment, null, false);
        final MPTextView commentTextView = accreditationCommentView.findViewById(R.id.mpsdkAccreditationTimeComment);
        setText(commentTextView, component.props.comment);

        return accreditationCommentView;
    }
}
