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

package com.a203110052.flower_core

import com.a203110052.flower_core.implement.Response

sealed class ApiResponse<T> {
    companion object {
        fun <T> create(error: Throwable): ApiErrorResponse<T> {
            return ApiErrorResponse(
                errorMessage = error.message ?: "Unknown error",
                httpStatusCode = 0
            )
        }

        fun <T> create(response: Response<T>): ApiResponse<T> {
            return if (response.isSuccessful) {
                val body = response.body()
                val headers = response.headers()
                if (body == null || response.code == 204) {
                    ApiEmptyResponse()
                } else {
                    ApiSuccessResponse(body, headers)
                }
            } else {
                ApiErrorResponse(
                    errorMessage = response.description,
                    httpStatusCode = response.code
                )
            }
        }
    }
}

data class ApiSuccessResponse<T>(
    val body: T?,
    val headers: Set<Map.Entry<String, List<String>>>
) : ApiResponse<T>()

/**
 * Separate class for HTTP 204 responses so that we can make ApiSuccessResponse's body non-null.
 */
class ApiEmptyResponse<T> : ApiResponse<T>()

data class ApiErrorResponse<T>(
    val errorMessage: String,
    val httpStatusCode: Int
) : ApiResponse<T>()

//@JvmInline
//value class ErrorMessage(val message: String)
//
//@JvmInline
//value class HttpStatusCode(val code: Int)
