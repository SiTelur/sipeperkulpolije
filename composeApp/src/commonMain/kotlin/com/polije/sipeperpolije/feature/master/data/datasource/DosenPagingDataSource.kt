package com.polije.sipeperpolije.feature.master.data.datasource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.polije.sipeperpolije.feature.master.data.model.DosenModel
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns

class DosenPagingDataSource(val supabase: SupabaseClient) : PagingSource<Int, DosenModel>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, DosenModel> {
        return try {
            val offset = params.key ?: 0
            val limit = params.loadSize

            val data = supabase
                .from("dosen")
                .select(columns = Columns.list("id", "nama")) {
                    range(
                        from = offset.toLong(),
                        to = (offset + limit - 1).toLong()
                    )
                }
                .decodeList<DosenModel>()

            LoadResult.Page(
                data,
                prevKey = if (offset == 0) null else offset - limit,
                nextKey = if (data.isEmpty()) null else offset + limit
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, DosenModel>): Int? {
        return state.anchorPosition?.let { position ->
            state.closestPageToPosition(position)?.prevKey?.plus(1) ?: state.closestPageToPosition(
                position
            )?.nextKey?.minus(1)
        }
    }
}