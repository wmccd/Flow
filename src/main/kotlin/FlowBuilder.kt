import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import java.awt.desktop.SystemSleepListener

class FlowBuilder {

    suspend fun demonstrate(){
        flowOfDemo()
        collectionConverters()
        //willNeverFinish()
    }

    private fun willNeverFinish() {
        runBlocking {
            willAlwaysEmit().collect{
                println("Timestamp: $it")
            }
        }
    }

    suspend private fun willAlwaysEmit(): Flow<Long> = flow{
       while(true){
           emit(System.currentTimeMillis())
       }
    }

    private suspend fun flowOfDemo() {
        flowOf(1,2,3,4,5)
            .collect{
                println("Collected $it")
            }
    }

    private suspend fun collectionConverters(){

        listOf(1, 2, 3)
            .asFlow()
            .collect{
                println("List to Flow $it")
            }

        setOf("The", "Mighty", "Bobbins")
            .asFlow()
            .collect{
                println("Set to Flow $it")
            }

        hashMapOf(Pair(1,true), Pair(2, false), Pair(3, true))
            .asIterable()
            .asFlow()
            .collect{
                println("Map to Flow $it")
            }
    }
}