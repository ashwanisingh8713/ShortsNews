fun main() {
    println("Hello, world!!!")
    findMax()
}


fun findMax()  {
    val arr = listOf(1, 2, 3, 4, 5, 6)
    val sum = 9
    var startIndex = 0
    val endWindow = arr.size
    var totalSum = 0

    cancel@ while(startIndex < endWindow) {
        totalSum = 0
        for(i in startIndex until endWindow) {
            totalSum += arr[i]
            if(totalSum == sum) {
                println("sub array starts from $startIndex and end at $i")
                break@cancel
            }
        }
        startIndex++

    }

}