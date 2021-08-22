package com.example.candy.model.repository

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.candy.model.api.ChallengeApi
import com.example.candy.model.data.Challenge

class ChallengeListRepositoryImpl(
    private val challengeApi: ChallengeApi,
    private val context: Context
): ChallengeListRepository {


    override suspend fun searchPossibleChallenge(apiKey: String, lastChallengeId: Int, size: Int): ArrayList<Challenge>? {
        // 통신코드 작성
        val request = challengeApi.getPossibleChallengeList(apiKey, lastChallengeId, size)

        if(request.isSuccessful){
            Log.d("possibleChallengeList", request.body().toString())

            return request.body()!!.response
        }
        else {
            // 실패 시
            Toast.makeText(context, "도전 가능 리스트 조회 오류", Toast.LENGTH_SHORT).show()
            return null
        }
   }

    override suspend fun touchLikeBtn(apiKey: String, challengeId: Int, previousState: Boolean): Boolean {
        val request = challengeApi.touchLikeBtn(apiKey, challengeId)

        if(request.isSuccessful){
            Log.d("api test", "challenge like success" )

            if(previousState)
                Toast.makeText(context, "찜 목록에서 챌린지가 제거됩니다", Toast.LENGTH_SHORT ).show()
            else
                Toast.makeText(context, "찜 목록에 챌린지가 추가됩니다", Toast.LENGTH_SHORT ).show()

            return request.body()!!.isSuccess
        }
        else{
            Toast.makeText(context, "찜하기 실패", Toast.LENGTH_SHORT ).show()
            return false
        }
    }

}