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

package com.a203110052.agustiawan_repo_12.ui

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.a203110052.flower_core.Resource
import com.a203110052.agustiawan_repo_12.data.database.entity.Quote
import com.a203110052.agustiawan_repo_12.data.repository.QuoteRepository
import com.a203110052.agustiawan_repo_12.extensions.foldApiStates
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    quoteRepository: QuoteRepository
) : ViewModel() {
    val currentPageNo: MutableLiveData<Int> = MutableLiveData(1)

    private val commandsChannel = Channel<Command>()
    private val commands = commandsChannel.receiveAsFlow()

    private val oneShotEventsChannel = Channel<Event>()
    val events = oneShotEventsChannel.receiveAsFlow()

    init {
        changePage(1)
    }

    private val getQuotesForPage = { page: Int ->
        quoteRepository.getRandomQuote(page, onFailed = { errorBody, statusCode ->
            Log.i("getRandomQuote", "onFailure => $errorBody ,$statusCode")
            viewModelScope.launch {
                errorBody?.let { oneShotEventsChannel.send(Event.Error(it)) }
            }
            currentPageNo.postValue(currentPageNo.value?.minus(1))
        })
    }

    val quotes = commands.flatMapLatest { command ->
        flow {
            when (command) {
                is Command.ChangePageCommand -> {
                    getQuotesForPage(command.page).foldApiStates({ quote ->
                        delay(250)
                        emit(State.UIState(quote, currentPageNo.value ?: 1))
                    }, { emit(it) }, { emit(it) })
                }
            }

        }
    }.asLiveData(viewModelScope.coroutineContext)

    fun changePage(page: Int) {
        viewModelScope.launch {
            currentPageNo.value = page
            commandsChannel.send(Command.ChangePageCommand(page))
        }
    }

    sealed class Command {
        data class ChangePageCommand(val page: Int) : Command()
    }

    sealed class State {
        data class UIState(val quote: Quote, val currentPage: Int) : State()
        data class SuccessState(val resource: Resource<*>) : State()
        data class ErrorState(val errorMessage: String, val statusCode: Int) : State()
        data class LoadingState(val loading: Boolean = true) : State()
    }

    sealed class Event {
        data class Error(val message: String) : Event()
    }
}
