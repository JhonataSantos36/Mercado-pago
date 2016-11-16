[![License](https://img.shields.io/badge/license-MIT-lightgrey.svg?style=flat)](https://github.com/mercadopago/px-android)

#MercadoPago - Android SDK

The MercadoPago Android SDK make it easy to collect your user's credit card details inside your android app. By creating tokens, MercadoPago handles the bulk of PCI compliance by preventing sensitive card data from hitting your server.

![screenshot](https://cloud.githubusercontent.com/assets/11367894/20360613/2611416c-ac13-11e6-9656-054e2d062252.png)

##Installation

###Android Studio

Add this line to your app's `build.gradle` inside the `dependencies` section:

    compile ('com.mercadopago:sdk:WORKSHOP@aar') { transitive = true }

###Eclipse

1. Clone the repository.
2. Be sure you've installed the Android SDK with API Level 21 and _android-support-v7_
3. Import the _sdk_ folder into Android Studio
4. Add the folder as library.

### ProGuard

If you're planning on optimizing your app with ProGuard, make sure that you exclude the MercadoPago bindings. You can do this by adding the following to your app's `proguard.cfg` file:

    -keep class com.mercadopago.** { *; }

## Documentation

+ [Read more about this 6 steps integration.](https://github.com/mercadopago/px-android)
+ [Check out MercadoPago Developers Site!](http://www.mercadopago.com.ar/developers)

## Feedback

You can join the MercadoPago Developers Community on MercadoPago Developers Site:

+ [English](https://www.mercadopago.com.ar/developers/en/community/forum/)
+ [Español](https://www.mercadopago.com.ar/developers/es/community/forum/)
+ [Português](https://www.mercadopago.com.br/developers/pt/community/forum/)
