import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking

fun main(args: Array<String>) {

        runBlocking {
                FlowComponents().demonstrate()
                FlowBuilder().demonstrate()
                FlowProcessing().demonstrate()
                FlowTerminals().demonstrate()
        }

        SimpleDemonstrator().demonstrate()

}


