package com.example.clockview.screens.clock

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.Calendar
import java.util.concurrent.TimeUnit

class ClockViewFragmentViewModel(application: Application) : AndroidViewModel(application) {

    val timeLD = MutableLiveData<Calendar>()

    private val compositeDisposable = CompositeDisposable()

    init {
        checkTime()
    }

    private fun checkTime() {
        val disposable = Observable.interval(1, TimeUnit.SECONDS)
            .doOnSubscribe { timeLD.postValue(Calendar.getInstance()) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { timeLD.postValue(Calendar.getInstance()) }
        compositeDisposable.add(disposable)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }
}