package com.wj.kotlintest.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import com.wj.kotlintest.R
import com.wj.kotlintest.adapter.MoviesListAdapter
import com.wj.kotlintest.base.BaseSwipeBackActivity
import com.wj.kotlintest.databinding.ActivityFavoriteBinding
import com.wj.kotlintest.entity.MoviesEntity
import com.wj.kotlintest.handler.MoviesListHandler
import com.wj.kotlintest.mvp.FavoritePresenter
import com.wj.kotlintest.mvp.FavoriteView
import javax.inject.Inject

/**
 * 最喜欢的电影列表界面
 */
class FavoriteActivity
    : BaseSwipeBackActivity<FavoritePresenter, ActivityFavoriteBinding>(),
        FavoriteView {

    /** 电影列表适配器 */
    @Inject
    lateinit var adapter: MoviesListAdapter

    companion object {
        /**
         * 界面入口
         *
         * @param context Context 对象
         */
        fun actionStart(context: Context) {
            context.startActivity(Intent(context, FavoriteActivity::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorite)

        // 绑定 MVP
        presenter.attach(this)

        // 绑定数据、Handler
        adapter.mData = arrayListOf()
        adapter.mHandler = FavoriteHandler()

        // 设置 RecyclerView 布局管理、适配器
        mBinding.swipeTarget.layoutManager = GridLayoutManager(mContext, 2)
        mBinding.swipeTarget.adapter = adapter

        // 设置下拉刷新监听
        mBinding.swipe.setOnRefreshListener {
            // 初始化数据
            presenter.getFavoriteList()
        }

        // 显示加载中界面
        loading()
        // 加载数据
        presenter.getFavoriteList()
    }

    override fun initTitleBar() {
        showTitleBar()
        setIvLeft()
        setTitleStr("最喜欢的电影")
        setToolbarHide()
    }

    override fun onNoDataClick() {
        super.onNoDataClick()
        presenter.getFavoriteList()
    }

    override fun listComplete() {
        mBinding.swipe.onComplete()
    }

    override fun notify(list: ArrayList<MoviesEntity>) {
        adapter.mData.clear()
        adapter.mData.addAll(list)
        adapter.notifyDataSetChanged()
    }

    /**
     * 最喜欢的电影列表事件处理
     */
    inner class FavoriteHandler : MoviesListHandler {
        /**
         * 条目点击，跳转电影详情
         *
         * @param entity 电影信息实体
         */
        override fun onMoviesItemClick(entity: MoviesEntity) {
            MoviesDetailsActivity.actionStart(mContext, entity)
        }
    }
}
