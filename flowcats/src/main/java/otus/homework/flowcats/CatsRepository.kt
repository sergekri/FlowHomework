package otus.homework.flowcats

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach

class CatsRepository(
    private val catsService: CatsService,
    private val refreshIntervalMs: Long = 5000
) {

    private var counter = 0

    fun listenForCatFacts() = flow {
        while (true) {
            counter++
            val latestNews = catsService.getCatFact()
            emit(latestNews)
            delay(refreshIntervalMs)
        }
    }.onEach {
        if (counter == 5) {
            throw RuntimeException("You shall not pass")
        }
    }
}