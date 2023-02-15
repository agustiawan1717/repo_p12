/*
 *  Copyright (C) 2022 Rajesh Hadiya
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.a203110052.flower_retrofit.common

import com.a203110052.flower_core.implement.Response

internal fun <T> retrofit2.Response<T>.toCommonResponse(): Response<T> {
    return object : Response<T> {
        override val isSuccessful: Boolean
            get() = this@toCommonResponse.isSuccessful

        override val code: Int
            get() = this@toCommonResponse.code()

        override val description: String
            get() = this@toCommonResponse.errorBody()?.string() ?: this@toCommonResponse.message()

        override fun body(): T? {
            return this@toCommonResponse.body()
        }

        override fun headers(): Set<Map.Entry<String, List<String>>> {
            return this@toCommonResponse.headers().toMultimap().entries
        }
    }
}
