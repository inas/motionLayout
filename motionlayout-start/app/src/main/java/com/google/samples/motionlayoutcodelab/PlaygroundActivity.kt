package com.google.samples.motionlayoutcodelab

import android.content.Context
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.appbar.AppBarLayout
import com.google.samples.motionlayoutcodelab.databinding.ActivityPlaygroundBinding


class PlaygroundActivity: AppCompatActivity()  {

    private lateinit var mBinding: ActivityPlaygroundBinding
    private lateinit var mPagerAdapter: FlightSearchResultPagerAdapter
    private lateinit var mSearchResultAdapter: FlightSearchResultAdapter
    private lateinit var mSearchResultFlexiAdapter: FlightSearchResultAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_playground)

        setupViewPager()
        setupRecyclerView()
        coordinateMotion()
    }

    private fun setupViewPager() {
        mPagerAdapter = FlightSearchResultPagerAdapter()
        mBinding.flightViewPagerSearchResult.adapter = mPagerAdapter
        mBinding.flightLayoutTabFlexi.setupWithViewPager(mBinding.flightViewPagerSearchResult)

        mBinding.flightViewPagerSearchResult.addOnPageChangeListener(object: ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) { }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) { }

            override fun onPageSelected(position: Int) {
                if (position == FLEXI_PAGE_INDEX) {
                    if ((mBinding.recyclerViewFlexi.layoutManager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition() > 0) {
                        if ((mBinding.recyclerViewRegular.layoutManager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition() == 0) {
                            checkSeek()
                            //                        mBinding.recyclerViewFlexi.scrollBy(0, 200)
//                        mBinding.appBarHeader.offsetTopAndBottom(200)
//                        mBinding.vInstallmentHeader.visibility = View.GONE
                            setAppBarOffset(convertDpToPixel(88f).toInt())
                        }

                    } else {
//                        mBinding.vInstallmentHeader.visibility = View.VISIBLE
                        mBinding.appBarHeader.setExpanded(true)
                    }
                } else {
                    if ((mBinding.recyclerViewRegular.layoutManager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition() > 0) {
                        if ((mBinding.recyclerViewFlexi.layoutManager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition() == 0) {
                            checkSeek()
                            //                        mBinding.recyclerViewRegular.scrollBy(0, 200)
//                        mBinding.appBarHeader.offsetTopAndBottom(-200)
//                        mBinding.vInstallmentHeader.visibility = View.GONE
                            setAppBarOffset(convertDpToPixel(88f).toInt())
                        }

                    } else {
//                        mBinding.vInstallmentHeader.visibility = View.VISIBLE
                        mBinding.appBarHeader.setExpanded(true)
                    }
                }
            }

        })
    }

    private fun checkSeek() {
        val seek = mBinding.appBarHeader.totalScrollRange.toFloat() * (mBinding.vInstallmentHeader.translationY / convertDpToPixel(114f))
        Log.d("debugseek", "seek: $seek")
    }

    private fun setupRecyclerView() {
        mSearchResultAdapter = FlightSearchResultAdapter()
        mSearchResultFlexiAdapter = FlightSearchResultAdapter()

        mBinding.recyclerViewRegular.layoutManager = LinearLayoutManager(this)
        mBinding.recyclerViewRegular.adapter = mSearchResultAdapter

        mBinding.recyclerViewFlexi.layoutManager = LinearLayoutManager(this)
        mBinding.recyclerViewFlexi.adapter = mSearchResultFlexiAdapter

        val flights = mutableListOf<String>()
        for (i in 0..30) {
            flights.add("Flight $i")
        }

        mSearchResultAdapter.itemList = flights
        mSearchResultAdapter.notifyDataSetChanged()

        mSearchResultFlexiAdapter.itemList = flights.dropLast(10)
        mSearchResultFlexiAdapter.notifyDataSetChanged()

        mBinding.flightLayoutTabFlexi.post {
            mBinding.toolbar.minimumHeight = mBinding.flightLayoutTabFlexi.height

        }
        mBinding.vInstallmentHeader.viewTreeObserver.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                mBinding.vInstallmentHeader.viewTreeObserver.removeOnGlobalLayoutListener(this)
                mBinding.motionLayout.getConstraintSet(R.id.end)?.setTranslationY(R.id.flight_layout_tab_flexi, (mBinding.vInstallmentHeader.height + mBinding.flightLayoutTabFlexi.height).toFloat())
            }
        })

    }

    private fun coordinateMotion() {
        val appBarLayout: AppBarLayout = mBinding.appBarHeader
        val motionLayout: MotionLayout = mBinding.motionLayout

        val listener = AppBarLayout.OnOffsetChangedListener { unused, verticalOffset ->
            val seekPosition = -verticalOffset / appBarLayout.totalScrollRange.toFloat()
            motionLayout.progress = seekPosition
        }

        appBarLayout.addOnOffsetChangedListener(listener)
    }

    private fun setAppBarOffset(offsetPx: Int) {
        val params = mBinding.appBarHeader.layoutParams as CoordinatorLayout.LayoutParams
        val behavior = params.behavior as AppBarLayout.Behavior
        behavior.onNestedPreScroll(mBinding.coordinatorLayout, mBinding.appBarHeader, mBinding.recyclerViewRegular, 0, offsetPx, intArrayOf(0, 0), 0)
//        behavior.topAndBottomOffset = -offsetPx
    }

    fun Context.convertDpToPixel(dp: Float): Float {
        return dp * (resources.displayMetrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT)
    }

    fun Context.convertPixelToDp(px:Float): Float {
        return px / (resources.displayMetrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT)
    }

    companion object {
        const val ONEWAY = 100
        const val DEPART = 101
        const val RETURN = 102

        const val DEPART_ROUTE_INDEX = 0

        const val FLEXI_PAGE_INDEX = 0
        const val REGULAR_PAGE_INDEX = 1
    }

    inner class FlightSearchResultPagerAdapter : PagerAdapter() {

        var isFlexi: Boolean = true

        override fun instantiateItem(collection: ViewGroup, position: Int): Any {
            return when (position) {
                FLEXI_PAGE_INDEX -> if (isFlexi) mBinding.flightLayoutContainerFlexi else mBinding.flightLayoutContainerRegular
                else -> mBinding.flightLayoutContainerRegular
            }
        }

        override fun getCount(): Int {
            return if (isFlexi) 2 else 1
        }

        override fun isViewFromObject(arg0: View, arg1: Any): Boolean {
            return arg0 === arg1
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            // No super
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return when (position) {
                FLEXI_PAGE_INDEX -> "FLEXI"
                else -> "REGULAR"
            }
        }
    }
}