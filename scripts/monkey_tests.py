"""
 Requirements: sample app installed in emulator, currently only supported api 27 1080x1920 device
 RUN IN OSX:
 ~/Library/Android/sdk/tools/bin/monkeyrunner scripts/monkey_tests.py 
"""
from com.android.monkeyrunner import MonkeyRunner

API_SLEEP = 5
SHORT_SLEEP_ACTIVITY_TRANSITION = 1.5
enter_position = (990, 1670)
floating_position = (500, 1650)
first_circular_option = (296, 780)


def run_sample_app(current_device):
    package = 'com.mercadopago.example'
    activity = 'com.mercadopago.CheckoutExampleActivity'
    current_device.startActivity(component=package + '/' + activity)
    MonkeyRunner.sleep(SHORT_SLEEP_ACTIVITY_TRANSITION)


def touch_keyboard_enter(current_device):
    x, y = enter_position
    current_device.touch(x, y, 'DOWN_AND_UP')


def press_floating_ok(current_device):
    x, y = floating_position
    current_device.touch(x, y, 'DOWN_AND_UP')
    MonkeyRunner.sleep(SHORT_SLEEP_ACTIVITY_TRANSITION)


def navigate_to_payment_methods_screen(current_device):
    current_device.touch(554, 1080, 'DOWN_AND_UP')
    MonkeyRunner.sleep(API_SLEEP)


def navigate_to_credit_card_screen(current_device):
    x, y = first_circular_option
    # card
    current_device.touch(x, y, 'DOWN_AND_UP')
    MonkeyRunner.sleep(SHORT_SLEEP_ACTIVITY_TRANSITION)
    # credit card
    device.touch(x, y, 'DOWN_AND_UP')
    MonkeyRunner.sleep(SHORT_SLEEP_ACTIVITY_TRANSITION)


def fill_card_screen(current_device, card_info):
    # Card screen
    current_device.type(card_info['card_number'])
    touch_keyboard_enter(current_device)
    MonkeyRunner.sleep(0.5)
    # Card holder name
    current_device.type(card_info['card_holder_name'])
    touch_keyboard_enter(current_device)
    MonkeyRunner.sleep(0.5)
    current_device.type(card_info['expiration_date'])
    touch_keyboard_enter(current_device)
    MonkeyRunner.sleep(0.5)
    # SECURITY CODE
    current_device.type(card_info['sec_code'])
    touch_keyboard_enter(current_device)
    MonkeyRunner.sleep(0.5)
    # DNI
    current_device.type(card_info['dni'])
    touch_keyboard_enter(current_device)
    # END CREDIT CARD, LOADING INSTALLMENTS
    MonkeyRunner.sleep(API_SLEEP)


def select_installments(current_device):
    # INSTALLMENTS 1
    current_device.touch(500, 900, 'DOWN_AND_UP')
    MonkeyRunner.sleep(1)


def finish_like_crazy(current_device):
    for x in range(0, 10):
        current_device.press('KEYCODE_BACK', 'DOWN_AND_UP')
        MonkeyRunner.sleep(0.2)


def test_card(current_device, card):
    finish_like_crazy(current_device)
    run_sample_app(current_device)
    navigate_to_payment_methods_screen(current_device)
    navigate_to_credit_card_screen(current_device)
    fill_card_screen(current_device, card)
    select_installments(current_device)
    press_floating_ok(current_device)
    press_floating_ok(current_device)
    finish_like_crazy(current_device)


device = MonkeyRunner.waitForConnection()

visa_arg = {
    "card_number": "4509953566233704",
    "card_holder_name": "APRO",
    "expiration_date": "1122",
    "sec_code": '123',
    "dni": "12345678"
}

master_arg = {
    "card_number": "5031755734530604",
    "card_holder_name": "APRO",
    "expiration_date": "1122",
    "sec_code": '123',
    "dni": "12345678"
}

american_arg = {
    "card_number": "371180303257522",
    "card_holder_name": "APRO",
    "expiration_date": "1122",
    "sec_code": '1231',
    "dni": "12345678"
}

test_card(device, visa_arg)
test_card(device, master_arg)
test_card(device, american_arg)



# Stabilize and make a picture to compare
# MonkeyRunner.sleep(5)
# image = device.takeSnapshot()
# image.writeToFile('card_visa_congrats.png', 'png')
