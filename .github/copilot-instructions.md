# Project coding standards

project purpose bulid a mobile app in android studio using java language : bulid A coffee shop app :
coffee shop app :

---

## 📁 Updated Project Structure with Layout Screens

```
com.coffeecorner.app
├── activities
│   ├── SplashActivity.java              → layout: activity_splash.xml
│   ├── OnboardingActivity.java          → layout: activity_onboarding.xml
│   ├── LoginActivity.java               → layout: activity_login.xml
│   ├── RegisterActivity.java            → layout: activity_register.xml
│   ├── CheckoutActivity.java            → layout: activity_checkout.xml
│   ├── OrderTrackingActivity.java       → layout: activity_order_tracking.xml
│   ├── RewardsActivity.java             → layout: activity_rewards.xml
│   ├── FeedbackActivity.java            → layout: activity_feedback.xml
│   ├── AboutUsActivity.java             → layout: activity_about_us.xml
│   └── SettingsActivity.java            → layout: activity_settings.xml
│
├── fragments
│   ├── HomeFragment.java                → layout: fragment_home.xml
│   ├── MenuFragment.java                → layout: fragment_menu.xml
│   ├── ProductDetailsFragment.java      → layout: fragment_product_details.xml
│   ├── CartFragment.java                → layout: fragment_cart.xml
│   ├── OrderHistoryFragment.java        → layout: fragment_order_history.xml
│   ├── ProfileFragment.java             → layout: fragment_profile.xml
│   ├── EditProfileFragment.java         → layout: fragment_edit_profile.xml
│   ├── NotificationFragment.java        → layout: fragment_notification.xml
│   └── LoyaltyFragment.java             → layout: fragment_loyalty.xml
│
├── adapters
│   ├── MenuAdapter.java
│   ├── OrderAdapter.java
│   ├── NotificationAdapter.java
│   └── RewardAdapter.java
│
├── models
│   ├── User.java
│   ├── Product.java
│   ├── Order.java
│   ├── CartItem.java
│   └── Notification.java
│
├── viewmodels
│   ├── UserViewModel.java
│   ├── ProductViewModel.java
│   ├── CartViewModel.java
│   └── OrderViewModel.java
│
├── repositories
│   ├── UserRepository.java
│   ├── ProductRepository.java
│   ├── CartRepository.java
│   └── OrderRepository.java
│
├── network
│   ├── ApiService.java
│   └── RetrofitClient.java
│
├── utils
│   ├── Constants.java
│   ├── PreferencesHelper.java
│   ├── Validator.java
│   └── AppUtils.java
│
├── res
│   ├── layout
│   │   ├── activity_splash.xml
│   │   ├── activity_onboarding.xml
│   │   ├── activity_login.xml
│   │   ├── activity_register.xml
│   │   ├── activity_checkout.xml
│   │   ├── activity_order_tracking.xml
│   │   ├── activity_rewards.xml
│   │   ├── activity_feedback.xml
│   │   ├── activity_about_us.xml
│   │   ├── activity_settings.xml
│   │   ├── fragment_home.xml
│   │   ├── fragment_menu.xml
│   │   ├── fragment_product_details.xml
│   │   ├── fragment_cart.xml
│   │   ├── fragment_order_history.xml
│   │   ├── fragment_profile.xml
│   │   ├── fragment_edit_profile.xml
│   │   ├── fragment_notification.xml
│   │   └── fragment_loyalty.xml
│   ├── drawable/                       → icons, backgrounds
│   ├── values/
│   │   ├── strings.xml
│   │   ├── colors.xml
│   │   └── styles.xml
│   └── mipmap/                         → launcher icons
│
└── AndroidManifest.xml
```

---

### ✅ Highlights

- Each screen (20 total) is represented by either an `Activity` or `Fragment`.
- Every screen has a dedicated layout XML file under `res/layout/`.
- Organized into proper packages: `activities`, `fragments`, `models`, `viewmodels`, etc.
- Supports scalability with MVVM architecture.
- Easily extendable for Firebase integration and payment gateways.

---

Would you like a sample layout XML (e.g., for the login or menu screen) or Java code for one of the
screens?
