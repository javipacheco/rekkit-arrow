package akme

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import arrow.effects.IO

class State<T>(initialValue: T? = null) {

    fun setValue(v: T?) {
        mutableState.value = v
    }

    fun setValue() {
        mutableState.value = getValue()
    }

    fun postValue(v: T?) {
        mutableState.postValue(v)
    }
    fun postValue() {
        mutableState.postValue(getValue())
    }

    fun getValue(): T? = mutableState.value

    fun getValueUnsafe(): T = mutableState.value!!

    fun isNull(): Boolean = mutableState.value == null

    fun observe(owner: LifecycleOwner, observer: Observer<T>) {
        state.observe(owner, observer)
    }

    private val mutableState = MutableLiveData<T>().apply { value = initialValue }

    private val state: LiveData<T> = mutableState

}