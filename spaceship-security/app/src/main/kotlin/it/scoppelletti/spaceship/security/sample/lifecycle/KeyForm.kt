package it.scoppelletti.spaceship.security.sample.lifecycle

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import it.scoppelletti.spaceship.security.sample.BR
import it.scoppelletti.spaceship.security.sample.R
import it.scoppelletti.spaceship.types.StringExt
import java.lang.NumberFormatException

class KeyForm : BaseObservable() {

    @get:Bindable
    var alias: String = StringExt.EMPTY
        set(value) {
            if (value != field) {
                field = value
                notifyPropertyChanged(BR.alias)

                validateAlias()
            }
        }

    @get:Bindable
    var aliasError: Int = 0
        set(value) {
            if (value != field) {
                field = value
                notifyPropertyChanged(BR.aliasError)
            }
        }

    fun validateAlias(): Boolean {
        if (alias.isBlank()) {
            aliasError = R.string.err_alias_required
            return false
        }

        aliasError = 0
        return true
    }

    var expire: Int = 0
        set(value) {
            field = value
            expireString = value.toString()
        }

    @get:Bindable
    var expireString: String = "0"
        set(value) {
            if (value != field) {
                field = value
                notifyPropertyChanged(BR.expireString)

                validateExpire()
            }
        }

    @get:Bindable
    var expireError: Int = 0
        set(value) {
            if (value != field) {
                field = value
                notifyPropertyChanged(BR.expireError)
            }
        }

    fun validateExpire(): Boolean {
        val value: Int

        if (expireString.isBlank()) {
            expireError = R.string.err_expire_required
            return false
        }

        try {
            value = expireString.toInt()
        } catch (ex: NumberFormatException) {
            expireError = R.string.err_expire_integer
            return false
        }

        if (value < 0) {
            expireError = R.string.err_expire_valid
            return false
        }

        expire = value
        expireError = 0
        return true
    }

    fun validate(): Boolean {
        var valid = true

        if (!validateAlias()) {
            valid = false
        }

        if (!validateExpire()) {
            valid = false
        }

        return valid
    }
}