package io.appcheck.synchronizer.domain.entity

sealed class FirebaseRequestTokenStrategy {

    class Basic(val refresh: Boolean) : FirebaseRequestTokenStrategy()

    object Limited : FirebaseRequestTokenStrategy()
}