package com.mercadopago.paymentresult;

import com.mercadopago.components.Action;
import com.mercadopago.components.ActionDispatcher;
import com.mercadopago.paymentresult.components.Header;
import com.mercadopago.paymentresult.components.Icon;
import com.mercadopago.paymentresult.props.HeaderProps;
import com.mercadopago.paymentresult.props.IconProps;

import junit.framework.Assert;

import org.junit.Test;

/**
 * Created by vaserber on 11/2/17.
 */

public class HeaderTest {

    private static final int ICON_IMAGE = 1;
    private static final int BACKGROUND = 2;
    private static final int BADGE_IMAGE = 3;
    private static final String LABEL = "label";
    private static final String TITLE = "title";
    private static final String HEIGHT = "wrap";

    @Test
    public void testHeaderHasIconComponent() {

        final HeaderProps headerProps = getMockedHeaderProps();
        final Icon iconComponent = getIconComponent(headerProps);

        Assert.assertNotNull(iconComponent);
    }

    @Test
    public void testIconComponentHasValidProps() {

        final HeaderProps headerProps = getMockedHeaderProps();
        final Icon iconComponent = getIconComponent(headerProps);
        final IconProps iconProps = iconComponent.props;

        Assert.assertEquals(iconProps.iconImage, ICON_IMAGE);
        Assert.assertEquals(iconProps.badgeImage, BADGE_IMAGE);
    }

    private HeaderProps getMockedHeaderProps() {
        return new HeaderProps.Builder()
                .setLabel(LABEL)
                .setTitle(TITLE)
                .setHeight(HEIGHT)
                .setIconImage(ICON_IMAGE)
                .setBackground(BACKGROUND)
                .setBadgeImage(BADGE_IMAGE)
                .build();
    }

    private Icon getIconComponent(HeaderProps headerProps) {
        final MockedActionDispatcher dispatcher = new MockedActionDispatcher();
        final Header headerComponent = new Header(headerProps, dispatcher);
        return headerComponent.getIconComponent();
    }

    private class MockedActionDispatcher implements ActionDispatcher {

        @Override
        public void dispatch(Action action) {

        }
    }
}
