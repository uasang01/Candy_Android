package com.example.candy.home.adapter

import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.candy.databinding.ChallengelistItemViewLoadingBinding
import com.example.candy.databinding.ItemHomeRecyclerviewMychallengeBinding
import com.example.candy.model.data.Challenge
import com.example.candy.model.data.OnGoingChallenge
import java.util.logging.Handler

class MyChallengeAdapter(
    private val itemClicked: (position: Int) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val VIEW_TYPE_CHALLENGE = 0
    private val VIEW_TYPE_LOADING = 1
    private var challenges: ArrayList<OnGoingChallenge> = arrayListOf()


    // 아이템뷰에 챌린지가 들어가는 경우 ViewHolder
    inner class ChallengeViewHolder(private val binding: ItemHomeRecyclerviewMychallengeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.moveToLectureBtn.setOnClickListener {
                itemClicked(layoutPosition)
            }
        }

        fun bind(challenge: OnGoingChallenge) {
            binding.title = challenge.title
            binding.subTitle = challenge.subTitle
            binding.category = challenge.category
            binding.candy = challenge.assignedCandy
            binding.requiredScore = challenge.requiredScore
        }
    }

    // 아이템뷰에 프로그레스바가 들어가는 경우 ViewHolder
    inner class LoadingViewHolder(private val binding: ChallengelistItemViewLoadingBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }

    // 뷰의 타입을 정해주는 곳이다.
    override fun getItemViewType(position: Int): Int {
        // 게시물과 프로그레스바 아이템뷰를 구분할 기준이 필요. 프로그레스바 : id < 0
        return when (challenges[position].id < 0) {
            true -> VIEW_TYPE_LOADING
            false -> VIEW_TYPE_CHALLENGE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_CHALLENGE -> {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding =
                    ItemHomeRecyclerviewMychallengeBinding.inflate(layoutInflater, parent, false)
                ChallengeViewHolder(binding)
            }
            else -> {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding =
                    ChallengelistItemViewLoadingBinding.inflate(layoutInflater, parent, false)
                LoadingViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ChallengeViewHolder)
            holder.bind(challenges[position])
    }

    override fun getItemCount(): Int = challenges.size

    fun setList(challenges: ArrayList<OnGoingChallenge>) {
        this.challenges = challenges
        notifyDataSetChanged()
    }

    fun getLastChallengeId(): Int{
        Log.d("getLastChallengeId", "$challenges")
        if(challenges.size == 0) {
            return 1000
        } else if(challenges.size == 1){        // progress bar 만 들어있을 경우
            if(challenges[0].id >= 0){
                return challenges[0].id
            }else{
                return 1000
            }
        } else {
            val lastIndex = challenges.size-1
            if(challenges[lastIndex].id >= 0){
                return challenges[lastIndex].id
            }else{
                return challenges[lastIndex-1].id
            }
        }
    }

    fun addLoading(){
        if(challenges.isNotEmpty() && challenges.last().id >= 0){
            challenges.add(OnGoingChallenge(-1, "","","",1,1,1,false))
        }
    }

    fun deleteLoading(){
        android.os.Handler(Looper.getMainLooper()).postDelayed({
            if(challenges.isNotEmpty() && challenges.last().id < 0){
                challenges.apply{
                    Log.d("deleteLoading()",last().toString())
                    val lastIndex = lastIndex
                    removeAt(lastIndex)
                    notifyItemRemoved(lastIndex);
                    notifyItemRangeChanged(lastIndex, getItemCount() - lastIndex);
                }
            }
        }, 2000)
    }
}