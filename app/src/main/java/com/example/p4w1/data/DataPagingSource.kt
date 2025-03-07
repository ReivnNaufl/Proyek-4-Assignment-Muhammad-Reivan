package com.example.p4w1.data

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState

class DataPagingSource(private val dao: DataDao) : PagingSource<Int, DataEntity>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, DataEntity> {
        return try {
            val page = params.key ?: 0 // Start from page 0 if no key is provided
            val limit = params.loadSize
            val offset = page * limit

            // Log the page, offset, and limit
            println("Loading page $page (offset=$offset, limit=$limit)")

            // Load a page of data from the database
            val items = dao.loadPage(offset, limit)

            // Log the number of items loaded
            println("Loaded ${items.size} items")

            // Return the loaded data along with the previous and next keys
            LoadResult.Page(
                data = items,
                prevKey = if (page > 0) page - 1 else null,
                nextKey = if (items.isNotEmpty()) page + 1 else null
            )
        } catch (e: Exception) {
            // Log any errors
            Log.e("DataPagingSource", "Error loading page: ${e.message}")
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, DataEntity>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}