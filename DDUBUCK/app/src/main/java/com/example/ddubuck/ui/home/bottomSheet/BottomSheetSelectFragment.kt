package com.example.ddubuck.ui.home.bottomSheet


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.viewpager2.widget.ViewPager2
import com.example.ddubuck.R
import com.example.ddubuck.data.home.CourseItem
import com.example.ddubuck.data.home.WalkRecord
import com.example.ddubuck.ui.home.HomeMapViewModel
import com.naver.maps.geometry.LatLng
import java.util.*

class BottomSheetSelectFragment : Fragment() {
    private val homeMapViewModel: HomeMapViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.bottom_sheet_select,container, false)
        val sheetViewPager : ViewPager2 = rootView.findViewById(R.id.sheet_select_rv)
        val mAdapter = BottomSheetSelectRvAdapter(fooArray, parentFragmentManager)
        sheetViewPager.adapter = mAdapter
        sheetViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(i: Int) {
                super.onPageSelected(i)
                if(fooArray[i].isFreeWalk) {
                    homeMapViewModel.passPathData(listOf(LatLng(37.56362279298406, 126.90926225749905),LatLng(37.56362279298406, 126.90926225749905)))
                }else{
                    homeMapViewModel.passPathData(fooArray[i].walkRecord.path)
                }
            }
        })
        return rootView
    }

    companion object {
        private val fooRoute = listOf( //9개
                LatLng(37.56362279298406, 126.90926225749905),
                LatLng(37.56345663522066, 126.9091328029345),
                LatLng(37.56314632623486, 126.90784351195998),
                LatLng(37.56396493508562, 126.90736905196479),
                LatLng(37.56417998056722, 126.90825278385154),
                LatLng(37.56375202367158, 126.90831947940694),
                LatLng(37.56332059071951, 126.90851459284085),
                LatLng(37.56346358071265, 126.909140550899),
                LatLng(37.5637076839577, 126.9092733697774),
        )
        private val fooAltitude = listOf(1.0F,3.0F,2.0F,5.0F,7.0F,1.0F,2.0F,4.0F,2.0F)
        private val fooSpeed = listOf(0.1F,0.2F,0.2F,0.3F,0.6F,3F,5F,3F,5F)
        private val fooArray = arrayListOf(
            CourseItem(
                true,
                "자유산책",
                "자유산책입니다",
                WalkRecord(listOf(), 0.0, 0.0, 1, 1, 1.0)
            ),
            CourseItem(
                false,
                "코스산책",
                "코스산책입니다",
                WalkRecord(fooRoute, fooAltitude.average(), fooSpeed.average(), 325, 5683, 900.0)
            ),
            CourseItem(
                    false,
                    "성산로4안길 테스트 코스",
            "경사가 좀 있지만 거리는 짧은 간단한 코스입니다. ",
                    WalkRecord(
                            listOf(
                                    LatLng(37.5634487, 126.9095964),
                                    LatLng(37.5634487, 126.9095964),
                                    LatLng(37.5633626, 126.909694),
                                    LatLng(37.5633571, 126.9097694),
                                    LatLng(37.5633571, 126.90984),
                                    LatLng(37.5633221, 126.9099255),
                                    LatLng(37.5633134, 126.9099768),
                                    LatLng(37.5632954, 126.9100538),
                                    LatLng(37.5632686, 126.9101073),
                                    LatLng(37.5632467, 126.9101699),
                                    LatLng(37.5632112, 126.910241),
                                    LatLng(37.5631607, 126.9103086),
                                    LatLng(37.5631374, 126.9103636),
                                    LatLng(37.5631046, 126.9104265),
                                    LatLng(37.5630928, 126.9104946),
                                    LatLng(37.5630647, 126.910558),
                                    LatLng(37.5630556, 126.9106346),
                                    LatLng(37.5630485, 126.9107188),
                                    LatLng(37.5630194, 126.9107888),
                                    LatLng(37.5629645, 126.9108356),
                                    LatLng(37.5629391, 126.9108982),
                                    LatLng(37.5629178, 126.9109625),
                                    LatLng(37.5628923, 126.911015),
                                    LatLng(37.5628343, 126.9110621),
                                    LatLng(37.5627825, 126.9111007),
                                    LatLng(37.562794, 126.9111654),
                                    LatLng(37.5628479, 126.9111989),
                                    LatLng(37.5628893, 126.9112494),
                                    LatLng(37.5629422, 126.9113111),
                                    LatLng(37.5630043, 126.9113603),
                                    LatLng(37.5630721, 126.9114157),
                                    LatLng(37.5631237, 126.9114621),
                                    LatLng(37.5631659, 126.9115185),
                                    LatLng(37.5632176, 126.9115605),
                                    LatLng(37.5632818, 126.9116114),
                                    LatLng(37.563334, 126.9116627),
                                    LatLng(37.5633851, 126.911589),
                                    LatLng(37.5634156, 126.9115281),
                                    LatLng(37.5634431, 126.9114677),
                                    LatLng(37.563473, 126.9113982),
                                    LatLng(37.5635015, 126.9113472),
                                    LatLng(37.563515, 126.9112821),
                                    LatLng(37.5635183, 126.9112276),
                                    LatLng(37.5635257, 126.9111676),
                                    LatLng(37.5635646, 126.911103),
                                    LatLng(37.5636038, 126.9110487),
                                    LatLng(37.5636418, 126.9109895),
                                    LatLng(37.5636675, 126.9109325),
                                    LatLng(37.5636661, 126.9108817),
                                    LatLng(37.5636367, 126.9108278),
                                    LatLng(37.5636023, 126.9107581),
                                    LatLng(37.5635353, 126.9107046),
                                    LatLng(37.5634556, 126.9106759),
                                    LatLng(37.5633873, 126.9106403),
                                    LatLng(37.5633273, 126.9106282),
                                    LatLng(37.5633331, 126.9105438),
                                    LatLng(37.5633615, 126.9104891),
                                    LatLng(37.5633889, 126.9104018),
                                    LatLng(37.5634081, 126.9103501),
                                    LatLng(37.5634461, 126.910275),
                                    LatLng(37.5634975, 126.910197),
                                    LatLng(37.5635037, 126.9101374),
                                    LatLng(37.5634898, 126.9100751),
                                    LatLng(37.5635484, 126.9100265),
                                    LatLng(37.5635873, 126.9099652),
                                    LatLng(37.563618, 126.9098981),
                                    LatLng(37.5636427, 126.9098263),
                                    LatLng(37.5636503, 126.9097682),
                                    LatLng(37.563677, 126.9096886),
                                    LatLng(37.563695, 126.9096351),
                                    LatLng(37.5637293, 126.9095621),
                                    LatLng(37.563764, 126.9094942),
                                    LatLng(37.5637895, 126.9094328),
                            ),
                            listOf(36.273F,17.716F,8.217F,8.098F,6.959F,6.089F,6.096F,5.968F,4.096F,4.03F,4.027F,4.045F,4.047F,4.154F,5.528F,4.474F,5.382F,3.634F,4.058F,3.378F,3.493F,4.101F,4.0F,4.015F,3.89F,4.855F,5.439F,4.064F,4.042F,5.43F,6.045F,5.582F,4.079F,4.12F,6.463F,7.698F,7.969F,5.968F,4.092F,4.101F,4.905F,6.106F,6.106F,6.056F,5.183F,4.148F,5.804F,4.477F,4.081F,4.076F,4.07F,4.159F,4.158F,4.098F,4.91F,6.114F,6.112F,6.001F,6.015F,6.023F,6.025F,4.748F,5.372F,6.039F,6.057F,6.022F,4.45F,4.096F,4.086F,4.103F,4.087F).average(),
                            listOf(2.2850745F,2.231419F,1.8323104F,1.2427931F,1.0168366F,1.4252578F,1.2969129F,0.87330353F,0.95543486F,1.1152202F,1.159526F,1.163301F,0.7323433F,0.97935826F,1.080985F,1.0844249F,0.9402625F,0.57910275F,1.0228742F,1.0462228F,0.82830733F,0.42001262F,0.569097F,0.90020376F,0.97791094F,0.9100287F,0.87062603F,0.95780647F,1.0784539F,1.0578516F,1.0011464F,0.95464677F,1.0094901F,0.36221814F,1.3652068F,1.2659861F,0.9151175F,0.97190046F,0.83896244F,0.846014F,0.86373615F,0.86929697F,0.9738966F,0.9405922F,0.92884946F,0.36063546F,0.3820255F,0.363634F,0.8273605F,1.0257097F,1.0750376F,1.0602535F,1.1451268F,0.90960175F,0.9265632F,1.0658382F,1.0389845F,0.99926394F,1.081557F,1.1682404F,0.65749264F,1.0621904F,1.060041F,1.0258611F,0.9792899F,0.80706793F,1.1155007F,1.1113594F,1.1130321F,1.036313F,0.6483078F).average(),
                            360,
                            3021,
                            487.4403199747573,
                    )
            ),
        )


    }
}