package it.scoppelletti.spaceship.sample.i18n

import it.scoppelletti.spaceship.i18n.AndroidResourceMessageSpec
import it.scoppelletti.spaceship.i18n.MessageSpec
import it.scoppelletti.spaceship.sample.R

object SampleMessages {

    fun errorCodeDuplicate(code: String): MessageSpec =
            AndroidResourceMessageSpec(R.string.err_code_duplicate,
                    "err_code_duplicate", arrayOf(code))

    fun errorItemNotFound(id: Int): MessageSpec =
            AndroidResourceMessageSpec(R.string.err_item_notfound,
                    "err_item_notfound", arrayOf(id))

    fun messageExceptionTest(n: Int): MessageSpec =
            AndroidResourceMessageSpec(R.string.msg_exceptionTest,
                    "msg_exceptionTest", arrayOf(n))

    fun promptDeleting(): MessageSpec =
            AndroidResourceMessageSpec(R.string.msg_deleting, "msg_deleting.")
}
