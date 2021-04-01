package com.example.ddubuck

import com.google.gson.Gson
import com.naver.maps.geometry.LatLng

//var 로 바꿔놓은건 임시용이라서 그럼

data class WalkRecord (
    //경로 정보 모음
        val path : List<LatLng>,
        val altitudes : List<Float>,
        val speeds : List<Float>,
    //경과시간 (sec)
        val walkTime : Long,
    //발걸음 수
        val stepCount : Int,
    //거리 (m)
        val distance : Double,
) {

    fun getCalorie() : Double {
        //https://github.com/IoT-Heroes/KidsCafeSolution_App/issues/2 참고해서 만들었습니다
        val met = when(speeds.average()) {
            in 0.0..4.0 -> 2.0 // 느리게 걷기
            in 4.0..8.0 -> 3.8 // 보통 걷기
            in 8.0..12.0 -> 4.0 // 빠르게 걷기
            else -> 5.0 // 전력질주
        }
        return (met * (3.5 * 65 * (walkTime /60))) * 0.001 * 5
    }

    //TODO WalkRecord To Json 구현하기
    fun getJson() : String {
        return Gson().toJson(
            WalkRecord(path, altitudes, speeds, walkTime, stepCount, distance))
    }
}

/*
        //Retrofit 객체 생성
        val retrofit = Retrofit.Builder()
            .baseUrl(this.URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        //retrofit 객체를 통해 인터페이스 생성
        val service = retrofit.create(CometChatFriendsService::class.java)

        //Body에 담을 데이터 생성
        val friends = ArrayList<String>()
        friends.add(friendID)
        val body = HashMap<String, List<String>>()
        body.put("accepted", friends)

        service.addFriend(context.getString(R.string.apiKey), context.getString(R.string.appID),
            body, version, uid)?.enqueue(object : Callback<Data> {
            override fun onFailure(call: Call<Data>, t: Throwable) {
                Log.d("CometChatAPI::", "Failed API call with call: " + call +
                        " + exception: " + t)
            }

            override fun onResponse(call: Call<Data>, response: Response<Data>) {
                Log.d("Response:: ", response.body().toString())
                val friends = response.body()!!.data.accepted

                Log.d("Friends:: ", friends.toString())
                for (friendName in friends.keys) {
                    Log.d("${friendName}:: ", friends[friendName].toString())
                }

            }
        })
 */