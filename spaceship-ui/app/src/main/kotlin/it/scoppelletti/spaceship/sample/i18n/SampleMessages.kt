package it.scoppelletti.spaceship.sample.i18n

import it.scoppelletti.spaceship.i18n.AndroidResourceMessageSpec
import it.scoppelletti.spaceship.i18n.MessageSpec
import it.scoppelletti.spaceship.sample.R

object SampleMessages {

    fun errorCodeDuplicate(code: String): MessageSpec =
            AndroidResourceMessageSpec(R.string.err_code_duplicate,
                    "An item with code %s already exists.", arrayOf(code))

    fun errorItemNotFound(id: Int): MessageSpec =
            AndroidResourceMessageSpec(R.string.err_item_notfound,
                    "No item with id %d found.", arrayOf(id))

    fun messageExceptionTest(n: Int): MessageSpec =
            AndroidResourceMessageSpec(R.string.msg_exceptionTest,
                    "Message test #%d.", arrayOf(n))

    fun promptDeleting(): MessageSpec =
            AndroidResourceMessageSpec(R.string.msg_deleting,
                    "The item will be deleted.")
}
