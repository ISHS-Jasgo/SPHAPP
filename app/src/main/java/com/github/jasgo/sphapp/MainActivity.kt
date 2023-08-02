package com.github.jasgo.sphapp

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.annotation.UiThread
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.InfoWindow
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.util.FusedLocationSource
import okhttp3.*
import org.json.JSONObject
import java.net.URL
import java.net.URLEncoder
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread
import kotlin.math.round


class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    val placeList = listOf(
        "강남 MICE 관광특구/서울 강남구 영동대로 513 코엑스",
        "동대문 관광특구/서울 중구 신당동 853",
        "명동 관광특구/서울 중구 남대문로 81",
        "이태원 관광특구/서울 용산구 우사단로14길 11",
        "잠실 관광특구/서울 송파구 올림픽로34길 3-6 아이다",
        "종로·청계 관광특구/서울 종로구 장사동 171",
        "홍대 관광특구/서울 마포구 양화로 130",
        "경복궁/서울 종로구 사직로 161 경복궁",
        "광화문·덕수궁/서울 중구 세종대로 99 덕수궁",
        "보신각/서울 종로구 종로 54 보신각",
        "서울 암사동 유적/서울 강동구 올림픽로 875",
        "창덕궁·종묘/서울 종로구 율곡로 99",
        "가산디지털단지역/서울 금천구 벚꽃로 309 가산디지털단지역",
        "강남역/서울 강남구 강남대로 396",
        "건대입구역/서울 광진구 아차산로 243",
        "고덕역/서울 강동구 고덕로 253 고덕역",
        "고속터미널역/서울 서초구 신반포로 188",
        "교대역/서울 서초구 서초대로 294",
        "구로디지털단지역/서울 구로구 도림천로 477",
        "구로역/서울 구로구 구로중앙로 174 구로역",
        "군자역/서울 광진구 천호대로 550",
        "남구로역/서울 구로구 도림로 7",
        "대림역/서울 구로구 도림천로 351",
        "동대문역/서울 종로구 종로 302",
        "뚝섬역/서울 성동구 아차산로 18",
        "미아사거리역/서울 강북구 도봉로 50",
        "발산역/서울 강서구 공항대로 267",
        "북한산우이역/서울 강북구 우이동 16-20",
        "사당역/서울 동작구 남부순환로 2089",
        "삼각지역/서울 용산구 한강대로 180 4호선 삼각지역",
        "서울대입구역/서울 관악구 남부순환로 1822 서울대입구역사",
        "서울식물원·마곡나루역/서울 강서구 마곡중앙5로 2 9호선마곡나루역",
        "서울역/서울 용산구 한강대로 405",
        "선릉역/서울 강남구 테헤란로 340",
        "성신여대입구역/서울 성북구 동소문로 102",
        "수유역/서울 강북구 도봉로 338",
        "신논현역·논현역/서울 강남구 봉은사로 102",
        "신도림역/서울 구로구 새말로 117-21 신도림역",
        "신림역/서울 관악구 남부순환로 1614",
        "신촌·이대역/서울특별시 마포구 노고산동 31-11",
        "양재역/서울특별시 서초구 서초동 1366-9",
        "역삼역/서울특별시 강남구 역삼동 804",
        "연신내역/서울특별시 은평구 갈현동 397",
        "오목교역·목동운동장/서울특별시 양천구 목1동 406-30",
        "왕십리역/서울특별시 성동구 행당동 192",
        "용산역/서울특별시 용산구 한강로3가 40-999",
        "이태원역/서울특별시 용산구 이태원동 119-23",
        "장지역/서울특별시 송파구 장지동 201-5",
        "장한평역/서울특별시 동대문구 장안동 472-3",
        "천호역/서울특별시 강동구 천호동 455",
        "총신대입구(이수)역/서울특별시 동작구 사당동 144-4",
        "충정로역/서울특별시 서대문구 충정로3가 319-1",
        "합정역/서울특별시 마포구 서교동 393",
        "혜화역/서울특별시 종로구 명륜4가 96-5",
        "홍대입구역 9번 출구/서울특별시 마포구 동교동 165",
        "회기역/서울특별시 동대문구 휘경동 317-101",
        "4·19 카페거리/서울 강북구 수유동 535-221",
        "가락시장/서울 송파구 가락동 600",
        "가로수길/서울 강남구 신사동 ",
        "광장(전통)시장/서울 종로구 예지동 2-1",
        "김포공항/서울 강서구 방화동 886",
        "낙산공원·이화마을/서울 종로구 동숭동 산2-10",
        "노량진/서울특별시 동작구 노량진동",
        "덕수궁길·정동길/서울 중구 정동 4",
        "방배역 먹자골목/서울특별시 서초구 방배동 912-14",
        "북촌한옥마을/서울 종로구 계동 105",
        "서촌/서울 종로구 누하동 15",
        "성수카페거리/서울특별시 성동구 성수동2가 300-1",
        "수유리 먹자골목/서울 강북구수유동 141",
        "쌍문동 맛집거리/서울특별시 도봉구 쌍문동",
        "압구정로데오거리/서울 강남구 신사동 643-17",
        "여의도/서울특별시 영등포구 여의도동",
        "연남동/서울특별시 마포구 연남동",
        "영등포 타임스퀘어/서울 영등포구 영등포동4가 442",
        "외대앞/서울특별시 동대문구 이문동 360-5",
        "용리단길/서울 용산구 한강로2가",
        "이태원 앤틱가구거리/서울 용산구 이태원동",
        "인사동·익선동/서울특별시 종로구 인사동",
        "창동 신경제 중심지/서울특별시 도봉구 노해로 392 버스정류장",
        "청담동 명품거리/서울 강남구 청담동 145",
        "청량리 제기동 일대 전통시장/서울특별시 동대문구 약령중앙로 10",
        "해방촌·경리단길/서울특별시 용산구 용산동2가",
        "DDP(동대문디자인플라자)/서울 중구 을지로 281",
        "DMC(디지털미디어시티)/서울 마포구 성암로 255",
        "강서한강공원/서울 강서구 방화동 2-32",
        "고척돔/서울 구로구 경인로 430 고척스카이돔",
        "광나루한강공원/서울 강동구 암사동 659-1",
        "광화문광장/서울특별시 종로구 세종대로 175 세종이야기",
        "국립중앙박물관·용산가족공원/서울 용산구 서빙고로 137 국립중앙박물관",
        "난지한강공원/서울 마포구 한강난지로 162 한강공원 난지안내센터",
        "남산공원/서울 중구 삼일대로 231",
        "노들섬/서울특별시 용산구 양녕로 445",
        "뚝섬한강공원/자양동 704-1",
        "망원한강공원/서울 마포구 마포나루길 467 한강공원망원지구사무소",
        "반포한강공원/서울 서초구 신반포로11길 40 한강공원 반포 안내센터",
        "북서울꿈의숲/서울 강북구 월계로 173",
        "불광천/서울 서대문구 북가좌동",
        "서리풀공원·몽마르뜨공원/서울 서초구 서초동 산177-3",
        "서울대공원/경기 과천시 대공원광장로 102",
        "서울숲공원/서울 성동구 뚝섬로 273",
        "시청광장/서울특별시 중구 을지로 12 시청광장지하쇼핑센터",
        "아차산/경기 구리시 아천동",
        "양화한강공원/서울 영등포구 노들로 113 양화개나리점",
        "어린이대공원/서울 광진구 능동로 216",
        "여의도한강공원/서울 영등포구 여의동로 330 한강사업본부 여의도안내센터",
        "월드컵공원/서울 마포구 하늘공원로 86",
        "응봉산/서울 성동구 금호동4가 1540",
        "이촌한강공원/서울 용산구 이촌로72길 62 한강공원 이촌안내센터",
        "잠실종합운동장/서울 송파구 올림픽로 25 서울종합운동장",
        "잠실한강공원/서울 송파구 한가람로 65 한강사업본부 잠실안내센터",
        "잠원한강공원/서울 서초구 잠원동",
        "청계산/경기 과천시 막계동",
        "청와대/서울특별시 종로구 청와대로 1"
    )

    var locationSource: FusedLocationSource? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkPermission()

        val fm = supportFragmentManager
        val mapFragment = fm.findFragmentById(R.id.map) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.map, it).commit()
            }
        mapFragment.getMapAsync(this)
        locationSource = FusedLocationSource(this, 1000)
    }

    private fun checkPermission() {
        val internetPermission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.INTERNET)
        val accessFineLocationPermission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
        val accessCoarseLocationPermission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)

        if (internetPermission == PackageManager.PERMISSION_GRANTED &&
            accessFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
            accessCoarseLocationPermission == PackageManager.PERMISSION_GRANTED) {
            Log.d("permission", "권한 설정 완료")
        } else {
            requestPermission()
        }
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.INTERNET,
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION), 1000)
    }

    @UiThread
    override fun onMapReady(map: NaverMap) {
        map.locationSource = locationSource
        map.locationTrackingMode = LocationTrackingMode.Follow
        thread (start = true) {
            val client = OkHttpClient().newBuilder()
                .retryOnConnectionFailure(true)
                .build()
            val positionList = mutableListOf<LatLng>()
            val placeSizeList = mutableListOf<Double>()
            val peopleCountList = mutableListOf<Pair<Int, Int>>()
            val sphList = mutableListOf<Pair<Double, Double>>()
            var count = 0
            placeList.forEach { p ->
                run {
                    val place = p.split("/")[1].trim()
                    val p2 = p.split("/")[0]
                    Log.d("place", p2)
                    Log.d("count", (count++).toString() + " " + place)
                    try {
                        val request1 = getLatLngfromAddressRequest(place)
                        val response1 = client.newCall(request1).execute()
                        val responseString1 = response1.body?.string()
                        Log.d("response", responseString1!!)
                        val position = parseLatLng(responseString1)
                        positionList.add(position)
                    } catch (e: Exception) {
                        Log.d("exception", e.toString())
                    }
                    try {
                        val request2 = getPlaceSizeRequest(p2)
                        val response2 = client.newCall(request2).execute()
                        val responseString2 = response2.body?.string()
                        Log.d("response", responseString2!!)
                        val size = parsePlaceSize(responseString2)
                        placeSizeList.add(size)
                    } catch (e: Exception) {
                        Log.d("exception", e.toString())
                    }
                    try {
                        val request3 = getPeopleCountRequest(p2)
                        val response3 = client.newCall(request3).execute()
                        val responseString3 = response3.body?.string()
                        Log.d("response", responseString3!!)
                        val peopleCount = parsePeopleCount(responseString3)
                        Log.d("peopleCount", peopleCount.toString())
                        peopleCountList.add(peopleCount)
                    } catch (e: Exception) {
                        Log.d("exception", e.toString())
                    }
                    try {
                        val request4 = getPlaceForceRequest(p2)
                        val response4 = client.newCall(request4).execute()
                        val responseString4 = response4.body?.string()
                        Log.d("response", responseString4!!)
                        val force = parsePlaceDanger(responseString4)
                        Log.d("force", force.toString())
                        sphList.add(force)
                    } catch (e: Exception) {
                        Log.d("exception", e.toString())
                    }
                }
            }
            runOnUiThread {
                val markerList = mutableListOf<Marker>()
                positionList.forEach { position ->
                    run {
                        val marker = Marker()
                        marker.position = position
                        val infoWindow = InfoWindow()
                        infoWindow.adapter = object : InfoWindow.DefaultTextAdapter(this) {
                            override fun getText(infoWindow: InfoWindow): CharSequence {
                                val index = positionList.indexOf(position)
                                return "${placeList[index].split("/")[0].trim()}\n" +
                                        "면적: ${placeSizeList[index]}m²\n" +
                                        "현재 인구: ${peopleCountList[index].first}명 ~ ${peopleCountList[index].second}명\n" +
                                        "평균위험도: ${sphList[index].first}%\n" +
                                        "최고위험도: ${sphList[index].second}%"
                            }
                        }
                        marker.map = map
                        marker.setOnClickListener {
                            if (marker.infoWindow == null) {
                                infoWindow.open(marker)
                            } else {
                                infoWindow.close()
                            }
                            true
                        }
                        markerList.add(marker)
                    }
                }
            }
        }
    }

    private fun parseLatLng(responseString: String): LatLng {
        val jsonObject = JSONObject(responseString)
        val addresses = jsonObject.getJSONArray("addresses")
        val firstAddress = addresses.getJSONObject(0)
        val longitude = firstAddress.getDouble("x")
        val latitude = firstAddress.getDouble("y")
        return LatLng(latitude, longitude)
    }

    private fun getLatLngfromAddressRequest(place: String): Request {
        val query = URLEncoder.encode(place, "utf-8")
        val url = URL("https://naveropenapi.apigw.ntruss.com/map-geocode/v2/geocode?query=${query}")

        return Request.Builder()
            .addHeader("X-NCP-APIGW-API-KEY-ID", "qv9swowmcw")
            .addHeader("X-NCP-APIGW-API-KEY", "AtlzDVhNTZA7Ecin4rwoxQCbKqr8m0B3CYUYyGMU")
            .url(url)
            .build()
    }

    private fun getPeopleCountRequest(place: String): Request {
        val query = URLEncoder.encode(place, "utf-8")
        val url = URL("http://ishs.co.kr:8080/peopleCount?place=${query}")

        return Request.Builder()
            .url(url)
            .addHeader("Connection", "close")
            .build()
    }

    private fun parsePeopleCount(responseString: String): Pair<Int, Int> {
        val jsonObject = JSONObject(responseString)
        val peopleCountMin = jsonObject.getInt("peopleCountMin")
        val peopleCountMax = jsonObject.getInt("peopleCountMax")
        return Pair(peopleCountMin, peopleCountMax)
    }

    private fun getPlaceSizeRequest(place: String): Request {
        val query = URLEncoder.encode(place, "utf-8")
        val url = URL("http://ishs.co.kr:8080/placeSize?place=${query}")

        return Request.Builder()
            .url(url)
            .addHeader("Connection", "close")
            .build()
    }

    private fun parsePlaceSize(responseString: String): Double {
        val jsonObject = JSONObject(responseString)
        return jsonObject.getDouble("placeSize")
    }

    private fun getPlaceForceRequest(place: String): Request {
        val query = URLEncoder.encode(place, "utf-8")
        val url = URL("http://ishs.co.kr:8080/sphResult?place=${query}")

        return Request.Builder()
            .url(url)
            .addHeader("Connection", "close")
            .build()
    }

    private fun parsePlaceDanger(responseString: String): Pair<Double, Double> {
        val jsonObject = JSONObject(responseString)
        val mean = jsonObject.getDouble("MeanSPHResult")
        val max = jsonObject.getDouble("MaxSPHResult")
        return Pair(mean, max)
    }

}