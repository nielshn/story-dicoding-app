package com.dicoding.storydicodingapp.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.dicoding.storydicodingapp.data.api.response.ListStoryItem
import com.dicoding.storydicodingapp.data.api.retrofit.ApiConfig
import com.dicoding.storydicodingapp.data.local.database.StoryDatabase
import com.dicoding.storydicodingapp.data.local.entity.RemoteKeys
import com.dicoding.storydicodingapp.data.local.pref.LoginPreferences
import kotlinx.coroutines.flow.first

@Suppress("UNREACHABLE_CODE")
@OptIn(ExperimentalPagingApi::class)
class StoryRemoteMediator(
    private val database: StoryDatabase,
    private val preference: LoginPreferences
) : RemoteMediator<Int, ListStoryItem>() {

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, ListStoryItem>
    ): MediatorResult {
        val page = getPageForLoadType(loadType, state)
        return try {
            val getToken = preference.getSession().first()
            val apiService = ApiConfig.getApiService(getToken.token)
            val responseData = apiService.getStories(page, state.config.pageSize)
            val stories = responseData.listStory
            val storyList = stories.map { story ->
                ListStoryItem(
                    story.photoUrl,
                    story.createdAt,
                    story.name,
                    story.description,
                    story.lon,
                    story.id,
                    story.lat
                )
            }
            val endOfPaginationReached = responseData.listStory.isEmpty()
            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    database.remoteKeysDao().deleteRemoteKeys()
                    database.storyDao().deleteAll()
                }

                val prevKey = if (page == 1) null else page - 1
                val nextKey = if (endOfPaginationReached) null else page + 1
                val keys = responseData.listStory.map {
                    RemoteKeys(id = it.id, prevKey = prevKey, nextKey = nextKey)
                }
                database.remoteKeysDao().insertAll(keys)
                database.storyDao().insertStory(storyList)
            }
            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (e: Exception) {
            return MediatorResult.Error(e)
        }
    }

    private suspend fun getPageForLoadType(loadType: LoadType, state: PagingState<Int, ListStoryItem>): Int {
        return when (loadType) {
            LoadType.REFRESH -> getRemoteKeyClosestToCurrentPosition(state)?.nextKey?.minus(1) ?: INITIAL_PAGE_INDEX
            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)
                remoteKeys?.prevKey ?: return INITIAL_PAGE_INDEX
            }
            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                remoteKeys?.nextKey ?: return INITIAL_PAGE_INDEX
            }
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, ListStoryItem>): RemoteKeys? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()?.let { data ->
            database.remoteKeysDao().getRemoteKeysId(data.id)
        }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, ListStoryItem>): RemoteKeys? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()?.let { data ->
            database.remoteKeysDao().getRemoteKeysId(data.id)
        }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, ListStoryItem>): RemoteKeys? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { id ->
                database.remoteKeysDao().getRemoteKeysId(id)
            }
        }
    }

    companion object {
        const val INITIAL_PAGE_INDEX = 1
    }
}
