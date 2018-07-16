/*
 * Copyright (C) 2008-2015 Dario Scoppelletti, <http://www.scoppelletti.it/>.
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
 * limitations under the License.
 */

package it.scoppelletti.spaceship.types

import java.util.UUID

/**
 * Operations on UUIDs.
 *
 * * <a href="http://www.ietf.org/rfc/rfc4122.txt" TARGET="_top">RFC 4122: A Universally Unique IDentifier (UUID) URN Namespace</a>
 */
public object UuidExt {

    /**
     * NIL.
     *
     * * RFC 4122, section 4.1.7
     */
    public val NIL: UUID = UUID(0, 0)
}