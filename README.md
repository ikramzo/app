# Color Tap 🎨

A simple, addictive color-matching game for Android with AdMob monetization.

## How to Play
- A color word (e.g., "RED") appears in a random color
- Tap **YES** if the word matches its displayed color
- Tap **NO** if they don't match
- Score as many points as you can in 30 seconds!

## Monetization
- **Banner ads** on main menu and game screen
- **Interstitial ads** every 3 games

## Setup for Publishing

### 1. Replace AdMob Test IDs
In `app/src/main/res/values/strings.xml`, replace the test IDs with your real AdMob IDs:
- `admob_app_id` — your AdMob App ID
- `admob_banner_id` — your banner ad unit ID
- `admob_interstitial_id` — your interstitial ad unit ID

### 2. Create AdMob Account
1. Go to [AdMob](https://admob.google.com)
2. Create an app and ad units (Banner + Interstitial)
3. Copy the IDs into `strings.xml`

### 3. Build & Publish
1. Open in Android Studio
2. Generate a signed APK/AAB (Build > Generate Signed Bundle)
3. Upload to [Google Play Console](https://play.google.com/console)

## Requirements
- Android Studio Arctic Fox+
- Min SDK 24 (Android 7.0)
- Google AdMob account for real ads
