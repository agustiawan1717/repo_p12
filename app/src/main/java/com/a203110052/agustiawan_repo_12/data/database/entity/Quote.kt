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

package com.a203110052.agustiawan_repo_12.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@Entity(tableName = "quote")
@JsonClass(generateAdapter = true)
data class Quote(
    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = false)
    val primaryId: Int,
    @ColumnInfo(name = "_internalId")
    @Json(name = "id")
    val _internalId: Int,
    val title: String,
    val quote: String,
    val author: Int,
)
