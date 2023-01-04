package com.ns.news.data.mappers

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.LoadType.*
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.ns.news.data.api.NewsApi
import com.ns.news.data.db.*
import retrofit2.HttpException
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class SectionRemoteMediator(
    private val db: NewsDb,
    private val newsApi: NewsApi,
    private val languageId: String,
    private val sectionMapper: DataMapper,
) : RemoteMediator<Int, Section>() {
    private val sectionDao: SectionDao = db.sectionDao()

    override suspend fun initialize(): InitializeAction {
        // Require that remote REFRESH is launched on initial load and succeeds before launching
        // remote PREPEND / APPEND.
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, Section>
    ): MediatorResult {
        try {
            var data = newsApi.getSection(languageId).data
            val items = data.sections
            db.withTransaction {
                if (loadType == REFRESH) {
                    sectionDao.deleteAll()
                }
                var cells = items
                    .orEmpty()
                    .map { sectionMapper.toDomain(it) }
                sectionDao.insertAll(cells)
            }

            return MediatorResult.Success(endOfPaginationReached = true)
        } catch (e: IOException) {
            return MediatorResult.Error(e)
        } catch (e: HttpException) {
            return MediatorResult.Error(e)
        }
    }
}
