// Copyright 2020 Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package software.aws.toolkits.jetbrains.utils

import com.intellij.openapi.Disposable
import com.intellij.openapi.application.AppUIExecutor
import com.intellij.openapi.application.ModalityState
import com.intellij.openapi.application.impl.coroutineDispatchingContext
import com.intellij.openapi.util.Disposer
import kotlinx.coroutines.coroutineScope
import kotlin.coroutines.CoroutineContext

// FIX_WHEN_MIN_IS_2019_3 this can be removed and the actual runUnlessDisposed can be used which has more features
// like actually stopping if it is disposed. For now, we have to do that part manually
suspend fun <T> Disposable.runUnlessDisposed(block: suspend () -> T): T? = coroutineScope {
    if (!Disposer.isDisposed(this@runUnlessDisposed)) {
        block()
    } else {
        null
    }
}

fun getCoroutineUiContext(modalityState: ModalityState = ModalityState.any(), disposable: Disposable? = null): CoroutineContext {
    val uiThread = AppUIExecutor.onUiThread(modalityState)
    return if (disposable == null) {
        uiThread
    } else {
        uiThread.expireWith(disposable)
    }.coroutineDispatchingContext()
}
