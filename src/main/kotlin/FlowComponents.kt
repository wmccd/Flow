import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlin.coroutines.CoroutineContext

class FlowComponents {

    suspend fun demonstrate(){
        allTogether()
        emittingAndCollecting()
        catchError()
    }

    private suspend fun emittingAndCollecting() {
        collector()
    }

    private suspend fun collector(){
        val em = emittor()
        coroutineScope {
            em.collect{
                println("Collecting: $it")
            }
        }
    }

    private fun emittor(): Flow<Long> = flow{
        repeat(4){
            delay(100)
            val now = System.currentTimeMillis()
            println("Emitting: $now")
            emit(now)
        }
    }


    private suspend fun allTogether() {
        flow {
            repeat(3) {
                delay(100)
                emit("Bobbins ${System.currentTimeMillis()}") //flow builder
            }
        }
            .onEach { println("onEach: $it") }           //intermediate operation
            .onStart { println("onStart") }               //intermediate operation
            .onCompletion { println("onCompletion") }     //intermediate operation
            .catch { emit("Error") }                //intermediate operation
            .collect { println("collect $it") }           //terminal operation
    }

    private suspend fun catchError() {
        flow {
            emit("Mighty")
            delay(100)
            emit("Bobbins")
            delay(100)
            throw Error("Bobbins Error")
        }
        .catch {
            emit("Error")
        }
        .collect {
            println("collect $it")
        }
    }
}