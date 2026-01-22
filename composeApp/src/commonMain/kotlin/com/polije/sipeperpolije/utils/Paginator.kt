package com.polije.sipeperpolije.utils

class Paginator<Key, Item>(
    private val initialKey: Key,
    private val onLoadUpdated: (Boolean) -> Unit,
    private val onRequest: suspend (nextKey: Key) -> Result<List<Item>>,
    private val getNextKey: suspend (currentKey: Key, result: List<Item>) -> Key,
    private val onError: suspend (Throwable?) -> Unit,
    private val onSuccess: suspend (result: List<Item>, newKey: Key) -> Unit,
    private val endReached: (currentKey: Key, result: List<Item>) -> Boolean
) {

    private var currentKey = initialKey
    private var isMakingRequest = false
    private var isEndReached = false

    suspend fun loadNextItems() {
        if (isMakingRequest || isEndReached) {
            return
        }

        isMakingRequest = true
        onLoadUpdated(true)

        val result = onRequest(currentKey)
        isMakingRequest = false

        val item = result.getOrElse {
            onError(it)
            onLoadUpdated(false)
            return
        }

        currentKey = getNextKey(currentKey, item)

        onSuccess(item, currentKey)

        onLoadUpdated(false)

        isEndReached = endReached(currentKey, item)
    }

    fun reset() {
        currentKey = initialKey
        isEndReached = false
    }
}