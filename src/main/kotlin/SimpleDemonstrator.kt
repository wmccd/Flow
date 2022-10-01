import kotlinx.coroutines.*
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect

class SimpleDemonstrator {

    fun demonstrate(){
        val users = usersFlow()
        runBlocking {
            val job = launch {
                println("launch Couroutine Name:<${this.coroutineContext}>")
                //collect the emit values
                users.collect{
                    println(it)
                }
            }

            delay(5000)
            println("I have enough now.")
            job.cancelAndJoin()
            println("Finishing up.")
        }
    }

    private fun usersFlow(): Flow<String> = flow{
        println("flow   Couroutine Name:<${currentCoroutineContext()}>")
        repeat(100){
            delay(1000)
            emit("Timestamp: ${System.currentTimeMillis()}") //emit the
        }
    }
}