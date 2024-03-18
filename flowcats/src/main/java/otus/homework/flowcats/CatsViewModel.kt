package otus.homework.flowcats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

class CatsViewModel(
    private val catsRepository: CatsRepository
) : ViewModel() {

    private val _factUiState = MutableStateFlow<CatFactsUiState>(
        CatFactsUiState.Success(
            Fact(
                createdAt = "",
                deleted = false,
                id = "",
                text = "",
                source = "",
                used = false,
                type = "",
                user = "",
                updatedAt = ""
            )
        )
    )
    val factUiState: StateFlow<CatFactsUiState> = _factUiState

    init {
        viewModelScope.launch {
            catsRepository.listenForCatFacts()
                .flowOn(Dispatchers.IO)
                .catch {
                    _factUiState.value = CatFactsUiState.Error(it)
                }.collect {
                    _factUiState.value = CatFactsUiState.Success(it)
                }
        }
    }
}

class CatsViewModelFactory(private val catsRepository: CatsRepository) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        CatsViewModel(catsRepository) as T
}

sealed class CatFactsUiState {
    data class Success(val fact: Fact) : CatFactsUiState()
    data class Error(val exception: Throwable) : CatFactsUiState()
}