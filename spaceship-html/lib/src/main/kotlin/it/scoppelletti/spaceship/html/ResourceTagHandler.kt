/*
 * Copyright (C) 2018 Dario Scoppelletti, <http://www.scoppelletti.it/>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limit
 */

package it.scoppelletti.spaceship.html

import android.content.Context
import android.content.res.Resources
import android.text.Editable
import it.scoppelletti.spaceship.types.trimRaw
import mu.KotlinLogging
import org.xml.sax.XMLReader
import javax.inject.Inject

/**
 * HTML custom tag for inserting the value of a resource.
 *
 * > This tag properly works only if you insert the
 * > `<it-scoppelletti-contentHandler/>` tag before.
 *
 * @see   it.scoppelletti.spaceship.html.ContentHandlerTagHandler
 * @since 1.0.0
 *
 * @constructor
 * @param       context   Context.
 * @param       resources Application's resources.
 */
public class ResourceTagHandler @Inject constructor(
        private val context: Context,
        private val resources: Resources
) : HtmlTagHandler(ResourceTagHandler.TAG) {

    override fun handleTag(
            output: Editable,
            start: Int, end: Int,
            xmlReader: XMLReader
    ) {
        val resId: Int
        val pkgName: String
        val stringValue: String
        val name: String?
        val resType: String?
        val attrs: Map<String, String>

        attrs = xmlReader.getCurrentAttributes()

        name = attrs.entries.firstOrNull {
            ResourceTagHandler.ATTR_NAME.equals(it.key, true)
        } ?.value

        if (name.isNullOrBlank()) {
            logger.error { """Attribute ${ResourceTagHandler.ATTR_NAME} not set
in tag $tag.""".trimRaw() }
            return
        }

        resType = attrs.entries.firstOrNull {
            ResourceTagHandler.ATTR_TYPE.equals(it.key, true)
        } ?.value

        if (resType.isNullOrBlank()) {
            logger.error { """Attribute ${ResourceTagHandler.ATTR_TYPE} not set
in tag $tag.""".trimRaw() }
            return
        }

        if (!resType.equals(ResourceTagHandler.TYPE_STRING, true)) {
            logger.error { "Resource type $resType not supported by tag $tag." }
            return
        }

        pkgName = context.packageName
        resId = resources.getIdentifier(name, resType, pkgName)
        if (resId == 0) {
            logger.error { """Resource $name of type $resType not found in
                |package $pkgName.""".trimRaw() }
            return
        }

        stringValue = resources.getString(resId)
        output.replace(start, end, stringValue)
    }

    public companion object {

        /**
         * The tag.
         */
        public const val TAG: String = "it-scoppelletti-resource"

        /**
         * Attribute containing the resource name.
         */
        public const val ATTR_NAME: String = "name"

        /**
         * Attribute containing the resource type.
         */
        public const val ATTR_TYPE: String = "type"

        /**
         * Resource type `string`.
         */
        public const val TYPE_STRING: String = "string"

        private val logger = KotlinLogging.logger {}
    }
}