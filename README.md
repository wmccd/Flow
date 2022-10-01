#Flow

A flow is a stream of values that are computed asynchronously. These values can be collected as they arrive at the end of the flow. *collect* for Flow is like *forEach* for collections.

	interface Flow<out T>{
		suspeend fun collect(collector: FlowCollector<T>)
	}

*collect* is the only member functioin Flow, with all others being extension functions.


If you have a function that returns more than a single value then all the values have to be calculated and then supplied in a collection such as a List or a Set.

	fun allUsers(): List<User> = 
		api.getUsers().map{ it.toUser() }

The essence here is that the *List* or *Set* are fully calculated and since that may take time when must wait until all are calculated before any are available.

Flow is designed to be used with Coroutines. You don't have to but that would be fighting against the intentions of the language.

## Characteristics of Flow

Flow's terminal operations (like *collect*) suspend a coroutine instead of blocking a thread.

The flow builder is not suspending and does not require any scope. It is the terminal operation that is suspending and builds a relation to its parent coroutine.

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
    
    //launch Couroutine Name:<[StandaloneCoroutine{Active}@5f282abb, BlockingEventLoop@167fdd33]>
    //flow   Couroutine Name:<[StandaloneCoroutine{Active}@5f282abb, BlockingEventLoop@167fdd33]>
    //Timestamp: 1664644178247
    //Timestamp: 1664644179260
    //Timestamp: 1664644180265
    //Timestamp: 1664644181267
    //I have enough now.
    //Finishing up.



Some things to note:

* The Coroutine details in the inside the *launch*() coroutine builder is the same as in the usersFlow Flow function
* This means that the FLow function is running as part of that Coroutrine
* As soon as *job.cancelAndJoin()* is called then *usersFlow()* will cease to exist


## Flow Elements

Flow needs to start somewhere: usually with a *builder*.

The last operation is called the *terminal operation*.

Between the *builder* and the t*erminal operation* may be *intermediate operations*.

    suspend fun main(args: Array<String>) {

        flow {                                        //flow builder
            repeat(3) {
                delay(100)
                emit("Bobbins ${System.currentTimeMillis()}") 
            }
        }                        
        .onEach { println("onEach: $it") }            //intermediate operation
        .onStart { println("onStart") }               //intermediate operation
        .onCompletion { println("onCompletion") }     //intermediate operation
        .catch { emit("Error") }                      //intermediate operation
        .collect { println("collect $it") }           //terminal operation
    }
    
    //onStart
    //onEach: Bobbins 1664645954363
    //collect Bobbins 1664645954363
    //onEach: Bobbins 1664645954465
    //collect Bobbins 1664645954465
    //onEach: Bobbins 1664645954571
    //collect Bobbins 1664645954571
    //onCompletion

Let's have a look at the most useful components in action.

Look carefully at the flow object and you'll see that emit and collect are part of the same object. In the example above values are emitted and the same flow object collects them.

Now look at the example below. Notice that the *flow* object that is created in the declaration of the emmittor() function is actually the same object that we call *emit* and *collect* on.

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

#### Catching Errors

If an exception occurs in the flow then it can be caught in the *catch*() method. This gives you the opportunity to react and recover if desired.

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

## Flow Use Cases

The most typical use cases are:

* receiving messages that are sent through server side events such as web sockets and notifications
* observing user interactions such as text changes or clicks
* receiving update from sensors or other information about a device such as location or orientation
* observing changes in a database

Imagine you have a chat app and you are listening for incoming messages and update the UI accordingly.


#### Observing a Database table

    @Dao
    interface MyDao{
    	@Query("Select * fron userTable")
    	fun getData(): Flow<List<UserEntity>>
    }

## Flow Building

#### Known values

The simplest flow is known values with the *flowOf*() function

    private suspend fun flowOfDemo() {
        flowOf(1,2,3,4,5)
            .collect{
                println("Collected $it")
            }
    }    

#### Convertors

Collections such as List, Set or HashMap can be converted to a Flow

    listOf(1, 2, 3)
        .asFlow()
        .collect{
            println("List to Flow $it")
        }
        
    //List to Flow 1
    //List to Flow 2
    //List to Flow 3

    setOf("The", "Mighty", "Bobbins")
        .asFlow()
        .collect{
            println("Set to Flow $it")
        }
        
    //Set to Flow The
    //Set to Flow Mighty
    //Set to Flow Bobbins

    hashMapOf(Pair(1,true), Pair(2, false), Pair(3, true))
        .asIterable()
        .asFlow()
        .collect{
            println("Map to Flow $it")
        }     
    
    //Map to Flow 1=true
    //Map to Flow 2=false
    //Map to Flow 3=true


## Flow Processing

Flow is like a pipe through which values flow. As they do so they can be changed in different ways: dropped, multipled, transformed or combined.


### map

*map* transforms each element that flows into this transformation function.

    private suspend fun mapDemo() {
        (1..5).asFlow()
            .map { it * it }
            .collect{ println("map and collect $it") }
    }
    
    //map and collect 1
    //map and collect 4
    //map and collect 9
    //map and collect 16
    //map and collect 25

This example transforms an object to json

    fun getAllUsers(): Flow<UserJson> = {
    	userRepository.getAllUsers()
    	.map{
    		it.toUserJson()
    	}
    }    


### filter

*filter* returns a flow that contains only the values from the original flow that match the filter predicate.

    private suspend fun filterDemo() {
        (1..100).asFlow()
            .filter { it > 90 }
            .filter { it % 2 == 0}
            .collect{ println("filter and collect $it") }
    }
    
    //filter and collect 92
    //filter and collect 94
    //filter and collect 96
    //filter and collect 98
    //filter and collect 100     

### take
*take* is used to limit the flow to the first x elements

    private suspend fun takeDemo() {
        (1..100).asFlow()
            .take(5)
            .collect{ println("take and collect $it") }
    }
    
    //take and collect 1
    //take and collect 2
    //take and collect 3
    //take and collect 4
    //take and collect 5


### drop
*drop* is used to limit the flow by ignoring the first x elements

    private suspend fun dropDemo() {
        (1..100).asFlow()
            .drop(95)
            .collect{ println("drop and collect $it") }
    }
    
    //drop and collect 96
    //drop and collect 97
    //drop and collect 98
    //drop and collect 99
    //drop and collect 100


## Terminal Operators

In most cases collect can be used to cover virtually everything you need to do but there are additional Flow terminal operator available.

If you need something different you can always implement it yourself as with *sum*() below

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
    
    //First: 1
    //Last: 25
    //Count: 5
    //Reduce: 55  
    //Sum: 55  