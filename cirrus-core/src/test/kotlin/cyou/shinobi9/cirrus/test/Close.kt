package cyou.shinobi9.cirrus.test

fun main() {
    TEST_CIRRUS.config.reconnect = false
    TEST_CIRRUS.connectToBLive(22508204)

    Thread.sleep(2000)

    TEST_CIRRUS.stop()
    Thread.sleep(2000)
    println(TEST_CIRRUS.runningJob())
    println(TEST_CIRRUS.job)
    TEST_CIRRUS.connectToBLive(22508204)

    Thread.currentThread().join()
}
