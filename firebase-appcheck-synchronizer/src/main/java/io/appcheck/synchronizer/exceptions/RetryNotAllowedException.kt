package io.appcheck.synchronizer.exceptions

class RetryNotAllowedException : Exception {

    constructor(message: String?) : super(message)
}