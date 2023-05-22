# Firebase App Check. Deluxe edition

The "Firebase App Check" feature is used to verify the authenticity and integrity of requests, and can be used in 2 cases:
- protect requests to Firebase services, such as the Firebase Realtime Database, Firebase Cloud Firestore, Firebase Authentication, and Firebase Cloud Functions.
- protect your backend resources from abuse, such as denial-of-service attacks and unauthorized access attempts. It works by generating a token that can be included with requests from your app, which is then validated by the Firebase backend services.

For more information check [Firebase official documentation](https://firebase.google.com/docs/app-check)


## 💡 Which problems we are solving?

- synchronized requests to fetch **Firebase App Check Token** to safe quota limit
- quick integration of **Firebase App Check Token** for multiple requests in your application
- delay before retry attempts in case of getting errors from Firebase SDK


## ⚡ 1. Synchronized network request

Android **Firebase AppCheck** SDK implements cache for the token itself. In case there is no available token in the cache or you want to make **forceRefresh** - SDK will do network request to fetch token from Firebase Backend Service.

Actual problem is that this code is not synchronized inside Firebase Android SDK and it means that if you are doing 2 (and more) parallel function calls - it will produce multiple network requests into Firebase Backend Service.

Considering that Firebase Android SDK has [**quota limit**](https://firebase.google.com/docs/app-check#quotas_limits),  you should avoid this. Good news that have already done it for you. Check details at the bottom.

You can find details about proper integration [here](../main/firebase-appcheck-synchronizer/README.md) 
