package com.example.candy.model.data

import com.google.gson.annotations.SerializedName

data class ChallengeLike(
        @SerializedName("success")val isSuccess: Boolean
)

data class ChallengeDetailResponse(
        @SerializedName("success")val isSuccess: Boolean,
        @SerializedName("response")val response: ChallengeDetail,
        @SerializedName("error")val error: Error
)

data class ChallengeDetail(
        @SerializedName("assignedCandy")val assignedCandy: Int,
        @SerializedName("category")var category: String,
        @SerializedName("challengeId")val challengeId: Int,
        @SerializedName("description")val description: String,
        @SerializedName("level")val level: Int,
        @SerializedName("likeDone")val likeDone: Boolean,
        @SerializedName("problemCount")val problemCount: Int,
        @SerializedName("requiredScore")val requiredScore: Int,
        @SerializedName("subTitle")val subTitle: String,
        @SerializedName("title")val title: String,
        @SerializedName("totalScore")val totalScore: Int
)


data class OnGoingChallengeResponse(
        @SerializedName("success")val isSuccess: Boolean,
        @SerializedName("response")val response: ArrayList<OnGoingChallenge>,
        @SerializedName("error")val error: Error
)

data class LectureCheckRequestBody(
        @SerializedName("challengeId") val challengeId : Int,
        @SerializedName("lectureId") val lectureId : Int
)

data class ChallengeComplete(
        @SerializedName("challengeId") val challengeId: Int,
        @SerializedName("category") var category: String,
        @SerializedName("requiredScore") val requiredScore: Int,
        @SerializedName("totalScore") val totalScore: Int,
        @SerializedName("title") val title: String,
        @SerializedName("subTitle") val subTitle: String,
        @SerializedName("lectureId") val lectureId: Int,
        @SerializedName("assignedCandy") val assignedCandy: Int
)

data class ChallengeCompleteList(
        @SerializedName("success") val isSuccess: Boolean,
        @SerializedName("response") val response: ArrayList<ChallengeComplete>
)

