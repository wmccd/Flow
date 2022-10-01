import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*

class FlowProcessing {

    suspend fun demonstrate(){

        mapDemo()
        filterDemo()
        takeDemo()
        dropDemo()
    }

    private suspend fun dropDemo() {
        (1..100).asFlow()
            .drop(95)
            .collect{ println("drop and collect $it") }

        delay(100)
    }

    private suspend fun takeDemo() {
        (1..100).asFlow()
            .take(5)
            .collect{ println("take and collect $it") }

        delay(100)
    }

    private suspend fun filterDemo() {
        (1..100).asFlow()
            .filter { it > 90 }
            .filter { it % 2 == 0}
            .collect{ println("filter and collect $it") }

        delay(100)
    }

    private suspend fun mapDemo() {
        (1..5).asFlow()
            .map { it * it }
            .collect{ println("map and collect $it") }

        delay(100)
    }



}