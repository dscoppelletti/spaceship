package it.scoppelletti.spaceship.sample.lifecycle

import android.databinding.BaseObservable
import android.databinding.Bindable
import android.text.TextUtils
import it.scoppelletti.spaceship.sample.BR
import it.scoppelletti.spaceship.sample.R
import it.scoppelletti.spaceship.sample.model.Item

class ItemForm : BaseObservable() {

    @get:Bindable
    var changed = false
        set(value) {
            if (value != field) {
                field = value
                notifyPropertyChanged(BR.changed)
            }
        }

    @get:Bindable
    var id: Int = 0
        set(value) {
            if (value != field) {
                field = value
                notifyPropertyChanged(BR.id)
            }
        }

    @get:Bindable
    var code: String? = null
        set(value) {
            if (!TextUtils.equals(value.orEmpty(), field.orEmpty())) {
                field = value
                notifyPropertyChanged(BR.code)
                changed = true

                validateCode()
            }
        }

    @get:Bindable
    var codeError = 0
        set(value) {
            if (value != field) {
                field = value
                notifyPropertyChanged(BR.codeError)
            }
        }

    fun validateCode(): Boolean {
        if (code.isNullOrBlank()) {
            codeError = R.string.err_code_required
            return false
        }

        codeError = 0
        return true
    }

    @get:Bindable
    var desc: String? = null
        set(value) {
            if (!TextUtils.equals(value.orEmpty(), field.orEmpty())) {
                field = value
                notifyPropertyChanged(BR.desc)
                changed = true

                validateDesc()
            }
        }

    @get:Bindable
    var descError: Int = 0
        set(value) {
            if (value != field) {
                field = value
                notifyPropertyChanged(BR.descError)
            }
        }

    fun validateDesc(): Boolean {
        if (desc.isNullOrBlank()) {
            descError = R.string.err_desc_required
            return false
        }

        descError = 0
        return true
    }

    fun copy(item: Item) {
        id = item.id
        code = item.code
        codeError = 0
        desc = item.desc
        descError = 0
        changed = false
    }

    fun validate(): Boolean {
        var valid = true

        if (!validateCode()) {
            valid = false
        }

        if (!validateDesc()) {
            valid = false
        }

        return valid
    }
}
