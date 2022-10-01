import kotlinx.coroutines.flow.*

class FlowTerminals {

    suspend fun demonstrate(){
        firstDemo()
    }

    private suspend fun firstDemo() {

        val flow = flowOf(1, 2, 3, 4, 5)
            .map { it * it }

        println( "First: ${flow.first()}" )
        println( "Last: ${flow.last()}" )
        println( "Count: ${flow.count()}" )
        println( "Reduce: ${flow.reduce{ acc, value -> acc + value}}" )
        println( "Sum: ${flow.sum()}" )

    }

    suspend fun Flow<Int>.sum(): Int{
        var sum = 0
        collect{
            sum += it
        }
        return sum
    }
}