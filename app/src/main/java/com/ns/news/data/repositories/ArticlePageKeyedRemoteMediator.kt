package com.ns.pagingwithnetwork.reddit.repository.articleDb

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.LoadType.*
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.ns.libpagingwithnetwork.reddit.article.Cell
import com.ns.pagingwithnetwork.reddit.api.RedditApi
import com.ns.pagingwithnetwork.reddit.db.RedditDb
import com.ns.libpagingwithnetwork.reddit.article.CellsItem
import com.ns.libpagingwithnetwork.reddit.article.SectionPageRemote
import com.ns.pagingwithnetwork.reddit.db.CellItemsDao
import com.ns.pagingwithnetwork.reddit.db.SectionPageRemoteDao
import retrofit2.HttpException
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class ArticlePageKeyedRemoteMediator(
    private val db: RedditDb,
    private val redditApi: RedditApi,
    private val sectionId: String,
    private val url: String
) : RemoteMediator<Int, Cell>() {
    private val cellItemsDao: CellItemsDao = db.cellItems()
    private val remoteKeyDao: SectionPageRemoteDao = db.remotePage()

    override suspend fun initialize(): InitializeAction {
        // Require that remote REFRESH is launched on initial load and succeeds before launching
        // remote PREPEND / APPEND.
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, Cell>
    ): MediatorResult {

        try {
            // Get the closest item from PagingState that we want to load data around.
            val loadKey = when (loadType) {
                REFRESH -> 1
                PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                APPEND -> {
                    // Query DB for SubredditRemoteKey for the subreddit.
                    // SubredditRemoteKey is a wrapper object we use to keep track of page keys we
                    // receive from the Reddit API to fetch the next or previous page.
                    val remoteKey = db.withTransaction {
                        remoteKeyDao.remotePageBySection(sectionId)
                    }

                    // We must explicitly check if the page key is null when appending, since the
                    // Reddit API informs the end of the list by returning null for page key, but
                    // passing a null key to Reddit API will fetch the initial page.
                    if (remoteKey.nextPageKey == 0) {
                        return MediatorResult.Success(endOfPaginationReached = true)
                    }

                    remoteKey.nextPageKey
                }
            }

            Log.i("Ashwani", "##########################################################")
            Log.i("Ashwani", "loadType :: $loadType")
            Log.i("Ashwani", "sectionId :: $sectionId")
            Log.i("Ashwani", "nextPageKey :: $loadKey")
            Log.i("Ashwani", "PageState.initialLoadSize :: ${state.config.initialLoadSize}")
            Log.i("Ashwani", "PageState.pageSize :: ${state.config.pageSize}")

            var data = redditApi.getArticleNdWidget(url+loadKey).data


            val items = data.cells

            db.withTransaction {
                if (loadType == REFRESH) {
                    cellItemsDao.deleteBySectionId(sectionId)
                    remoteKeyDao.deleteBySection(sectionId)
                }
                val nextLoadKey = loadKey + 1
                remoteKeyDao.insert(SectionPageRemote(sectionId, nextLoadKey))
                var cells = items
                    .orEmpty()
                    .map { CellsItem.of(it, sectionId) }
                cellItemsDao.insertAll(cells)
            }

            return MediatorResult.Success(endOfPaginationReached = items.isEmpty())
        } catch (e: IOException) {
            return MediatorResult.Error(e)
        } catch (e: HttpException) {
            return MediatorResult.Error(e)
        }
    }
}
