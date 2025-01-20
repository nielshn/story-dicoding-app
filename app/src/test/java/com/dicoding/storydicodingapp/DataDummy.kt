package com.dicoding.storydicodingapp

import com.dicoding.storydicodingapp.data.api.response.ListStoryItem

object DataDummy {
    fun generateDummyQuotaResponse(): List<ListStoryItem> {
        val listStory = ArrayList<ListStoryItem>()
        for (i in 0..10) {
            val story = ListStoryItem(
                "https://story-api.dicoding.dev/images/stories/photos-1698059079604_3H1c_1HA.jpg",
                "2023-10-23T11:04:39.611Z",
                "test $i",
                "100",
                110.71106,
                "id $i",
                -7.2361833
            )
            listStory.add(story)
        }
        return listStory
    }
}