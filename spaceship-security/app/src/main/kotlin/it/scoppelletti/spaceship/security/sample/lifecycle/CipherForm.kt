package it.scoppelletti.spaceship.security.sample.lifecycle

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import it.scoppelletti.spaceship.security.sample.BR
import it.scoppelletti.spaceship.security.sample.R
import it.scoppelletti.spaceship.types.StringExt

class CipherForm : BaseObservable() {

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

    @get:Bindable
    var plainText: String = StringExt.EMPTY
        set(value) {
            if (value != field) {
                field = value
                notifyPropertyChanged(BR.plainText)
            }
        }

    @get:Bindable
    var cipherText: String = StringExt.EMPTY
        set(value) {
            if (value != field) {
                field = value
                notifyPropertyChanged(BR.cipherText)
            }
        }

    fun validate(): Boolean {
        var valid = true

        if (!validateAlias()) {
            valid = false
        }

        return valid
    }
}
