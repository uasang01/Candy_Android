package com.example.candy.challenge.pagerFragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.candy.utils.HorizontalItemDecorator
import com.example.candy.challenge.ChallengeDetailActivity
import com.example.candy.challenge.adapter.PossibleChallengeRecyclerAdapter
import com.example.candy.challenge.adapter.categoryRecyclerAdapter.ChallengeCategoryRecyclerAdapter
import com.example.candy.challenge.viewmodel.PossibleChallengeViewModel
import com.example.candy.databinding.FragmentPossibleChallengeBinding
import com.example.candy.model.data.Challenge
import com.example.candy.model.injection.Injection
import com.example.candy.utils.CurrentUser


class PossibleFragment: Fragment() {

    private var possibleChallengeBinding: FragmentPossibleChallengeBinding? = null

    private val categoryList = arrayListOf<String>()  // 카테고리
    //private lateinit var possibleChallengeList : List<Challenge>
    private lateinit var viewModel: PossibleChallengeViewModel

    // 카테고리
    private var CurrentCategory = "전체"

    //private lateinit var navController: NavController // 챌린지 선택 시 챌린지 소개 화면으로 넘어가기 위함

    private var page = 1 // 리스트 10개가 1page
    private var No_More_Data = false  // 서버에서 데이터 마지막까지 다 가져 오면 true 된다

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?):
            View? {
        val binding = FragmentPossibleChallengeBinding.inflate(inflater, container, false)
        possibleChallengeBinding = binding

        return possibleChallengeBinding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("fragment check","PossibleFragment onViewCreated")
        Log.d("token check", CurrentUser.userToken!!)

        // navigation
       // navController = Navigation.findNavController(view)

        // 카테고리 recycler
       /* categoryList.add("ALL")
        categoryList.add("영어")
        categoryList.add("수학")
        categoryList.add("국어")
        categoryList.add("과학")
        categoryList.add("한국사") */

        possibleChallengeBinding!!.recyclerPossibleChallengeCategory.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        possibleChallengeBinding!!.recyclerPossibleChallengeCategory.adapter = ChallengeCategoryRecyclerAdapter(categoryList,
                { category -> onCategoryItemClicked(category) })
        possibleChallengeBinding!!.recyclerPossibleChallengeCategory.addItemDecoration(
            HorizontalItemDecorator(20)
        )



        possibleChallengeBinding!!.recyclerPossibleChallenge.layoutManager = LinearLayoutManager(context)
        possibleChallengeBinding!!.recyclerPossibleChallenge.adapter =
                PossibleChallengeRecyclerAdapter(
                        ArrayList<Challenge>(),
                        touchLikeImage={ x: Challenge, y: Int ->
                            // 하트 이미지 터치로 찜하기, 활성화 상태에서 다시 터치하면 찜 취소
                            viewModel.touchLikeImage(x.challengeId, x.likeDone)
                            x.likeDone = !x.likeDone
                            (possibleChallengeBinding!!.recyclerPossibleChallenge.adapter as PossibleChallengeRecyclerAdapter).
                                    notifyItemRangeChanged(y, 1)
                        },
                        selectChallenge={
                            // 챌린지 선택 시 해당 챌린지 소개 화면으로 이동
                            val intent = Intent(activity, ChallengeDetailActivity::class.java)
                            intent.putExtra("challengeId", it.challengeId)
                            intent.putExtra("lectureId", it.lectureId)
                            intent.putExtra("isCompleted", false)
                            startActivity(intent)

                        }
                    )
        //possibleChallengeBinding!!.recyclerPossibleChallenge.addItemDecoration(VerticalItemDecorator(10))


        viewModel = ViewModelProvider(viewModelStore, object: ViewModelProvider.Factory{
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return PossibleChallengeViewModel(
                    Injection.provideRepoRepository(context!!)
                ) as T
            }
        }).get(PossibleChallengeViewModel::class.java)


        // 카테고리 가져오기
        viewModel.getCategories().observe(viewLifecycleOwner){ data ->
            //update ui
            Log.d("api test check", "possible getCategories / $data")
            (possibleChallengeBinding!!.recyclerPossibleChallengeCategory.adapter as ChallengeCategoryRecyclerAdapter).setCategories(data)
        }

        // 도전 가능 챌린지 리스트 observe
        viewModel.possibleChallengeLiveData.observe(viewLifecycleOwner,{
            (possibleChallengeBinding!!.recyclerPossibleChallenge.adapter as PossibleChallengeRecyclerAdapter).updateList(it)

        })

        // progressbar observe
        viewModel.progressVisible.observe(viewLifecycleOwner, {
            if(it){
                possibleChallengeBinding!!.progressbar.visibility = View.VISIBLE
            }
            else{
                possibleChallengeBinding!!.progressbar.visibility = View.GONE
            }
        })




        // 리사클러뷰 무한스크롤 구현
        possibleChallengeBinding!!.recyclerPossibleChallenge.addOnScrollListener(object: RecyclerView.OnScrollListener(){

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                Log.d("api test check", "possibleRecycler onScrollStateChanged called")

                val lastVisibleItemPosition =
                        (recyclerView.layoutManager as LinearLayoutManager?)!!.findLastCompletelyVisibleItemPosition()
                val totalItemViewCount = recyclerView.adapter!!.itemCount-1

                if(newState == 2 && !recyclerView.canScrollVertically(1)
                        && lastVisibleItemPosition == totalItemViewCount){
                    Log.d("api test check", "possible recycler 스크롤 마지막 도달로 인한 호출")

                    // 마지막 챌린지 아이디 받아오기
                    var lastChallengeId = (possibleChallengeBinding!!.recyclerPossibleChallenge.adapter as PossibleChallengeRecyclerAdapter)
                            .getLastChallengeId(totalItemViewCount)

                    // 로딩 아이템 뷰 추가
                    (possibleChallengeBinding!!.recyclerPossibleChallenge.adapter as PossibleChallengeRecyclerAdapter).addLoading()

                    // 요청
                    page++

                    Log.d("api test check", "possible recycler 스크롤 마지막 호출 : lastChallengeId : ${lastChallengeId}")
                    viewModel.getAllPossibleChallengeList(lastChallengeId, 10, false, CurrentCategory)



                }
            }

            /*
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val lastVisibleItemViewPosition =
                        (recyclerView.layoutManager as LinearLayoutManager?)!!.findLastCompletelyVisibleItemPosition()
                val totalItemViewCount = recyclerView.adapter!!.itemCount-1

                // 스크롤 마지막에 도달 시
                if(lastVisibleItemViewPosition == totalItemViewCount && !No_More_Data){
                    (possibleChallengeBinding!!.recyclerPossibleChallenge.adapter as PossibleChallengeRecyclerAdapter).deleteLoading()

                    var lastChallengeId = (possibleChallengeBinding!!.recyclerPossibleChallenge.adapter as PossibleChallengeRecyclerAdapter)
                                .getLastChallengeId(totalItemViewCount - 1)

                    // 페이지 증가
                    page++


                    viewModel.getAllPossibleChallengeList(lastChallengeId, 10, false)

                }

            }
            */
        })

    }

    // 카테고리 선택 시 데이터를 다시 로드.
    private fun onCategoryItemClicked(category: String) {
        Log.d("api test check", "CategoryItemClicked() category : $category ")
        CurrentCategory = category

        page = 1

        (possibleChallengeBinding!!.recyclerPossibleChallenge.adapter as PossibleChallengeRecyclerAdapter).dataSetClear()
        viewModel.getAllPossibleChallengeList(100000000, 10, true, CurrentCategory)

    }

    override fun onResume() {
        super.onResume()
        Log.d("fragment check","PossibleFragment onResume")
        Log.d("api test check", "PossibleFragment Current Category $CurrentCategory")

        page = 1
        No_More_Data = false

        (possibleChallengeBinding!!.recyclerPossibleChallenge.adapter as PossibleChallengeRecyclerAdapter).dataSetClear()
        viewModel.getAllPossibleChallengeList(100000000, 10, true, CurrentCategory)
    }

    override fun onDestroyView() {
        possibleChallengeBinding = null
        super.onDestroyView()
    }
}