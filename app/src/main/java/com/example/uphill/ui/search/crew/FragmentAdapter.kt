package com.example.uphill.ui.search.crew

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.uphill.ui.search.CrewSingleton
import com.example.uphill.ui.search.crew.dashboard.CrewDashboardFragment
import com.example.uphill.ui.search.crew.member.CrewMemberFragment

class FragmentAdapter(fm : FragmentManager): FragmentPagerAdapter(fm) {
    //position 에 따라 원하는 Fragment로 이동시키기
    override fun getItem(position: Int): Fragment {
        val fragment =  when(position)
        {
            0->CrewMemberFragment().newInstant()
            1->CrewDashboardFragment().newInstant()
            else -> CrewMemberFragment().newInstant()
        }
        return fragment
    }

    //tab의 개수만큼 return
    override fun getCount(): Int = 2

    //tab의 이름 fragment마다 바꾸게 하기
    override fun getPageTitle(position: Int): CharSequence {
        val crew = CrewSingleton.selectedCrew
        val title = when(position)
        {
            0->"${crew?.crewName}"
            1->"대회"
            else -> "Null"
        }
        return title     }
}