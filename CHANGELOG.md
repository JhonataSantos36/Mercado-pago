Change Log
==========

## VERSION 0.9.15

_25-09-2015_

* Fix: Navigates PaymentMethodsActivity if no params are set to retrieve saved cards

## VERSION 0.9.14

_24-09-2015_

* Fix: Application label removed from sdk module
* Fix: All value vars prefixed with mpsdk_ to avoid name conflicts
* Fix: BankDealsAdapter supports null recommended message

## VERSION 0.9.13

_22-09-2015_

* New: English language support

## VERSION 0.9.12

_18-09-2015_

* New: Bank deals menu button available via .setShowBankDeals(true) for VaultActivity
* New: VaultActivity now pre-selects last used credit card
* Fix: VaultActivity, issuer not set in getInstallments method for saved credit cards
* Fix: VaultActivity, flow problems pressing back key after PaymentMethodsActivity navigation
* Fix: GPS feature use set as optional
* Fix: Some minor visual changes

## VERSION 0.9.11

_19-06-2015_

* New: Bank deals widget and components
* New: API 9 support!
* Fix: Change to AppCompatActivity
* Fix: Create token exception not retrieving error cause
* Fix: Sonatype default credentials

## VERSION 0.9.10

_25-04-2015_

* New: Upgrade Retrofit to 1.9
* New: Upgrade Target SDK version to 22
* New: Upgrade AppCompat Support Lib to 22.1
* New: Upgrade Build Tools to 22.0.1
* Fix: keep-alive is false for MercadoPago class methods
* Fix: Congrats button style
* Fix: MerchantServer class now uses okhttp
