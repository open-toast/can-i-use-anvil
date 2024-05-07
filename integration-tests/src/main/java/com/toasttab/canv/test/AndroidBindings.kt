package com.toasttab.canv.test

import dagger.android.ContributesAndroidInjector

interface AndroidBindings {
    @ContributesAndroidInjector
    fun bindActivity(): Activity
}
