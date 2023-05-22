## 💥 Strategies

There are few strategies to fetch **Firebase App Check** token based on [Firebase App Check documentation](https://firebase.google.com/docs/app-check/android/custom-resource):
```kotlin
sealed class FirebaseRequestTokenStrategy {

    class Basic(val refresh: Boolean) : FirebaseRequestTokenStrategy()

    object Limited : FirebaseRequestTokenStrategy()
}
```

**Basic** is equivalent to:
```kotlin
FirebaseAppCheck.getInstance()
    .getAppCheckToken(forceRefresh)
```

**Limited** is equivalent to:
```kotlin
FirebaseAppCheck.getInstance()
    .getLimitedUseAppCheckToken()
```

## 💥 Usage

1. Create executor to fetch token:

```kotlin
val appCheckTokenExecutor = FirebaseAppCheckTokenExecutor(
    FirebaseRequestTokenStrategy.Basic(false)
)
```

2. Create token provider:

```kotlin
val tokenProvider = AppCheckTokenProviderFactory.getAppCheckTokenProvider(appCheckTokenExecutor)
```

3. Fetch token:

```kotlin
val tokenResult : Result<String> = tokenProvider.provideAppCheckToken()
```