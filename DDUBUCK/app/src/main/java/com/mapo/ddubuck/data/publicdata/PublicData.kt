package com.mapo.ddubuck.data.publicdata

interface PublicDataForm {
    val name : String
    val x : Double
    val y : Double
}

data class PublicData(
    val cafe: List<Cafe>,
    val carFreeRoad: List<CarFreeRoad>,
    val petCafe: List<PetCafe>,
    val petRestaurant: List<PetRestaurant>,
    val publicToilet: List<PublicToilet>,
    val publicRestArea: List<PublicRestArea>,
    val recommendPath : List<RecommendPathDTO>,
)

/*
"recommendPath": [
        {
            "name": "홍익로~와우산로21길",
            "path": [
                {
                    "x": 37.556295,
                    "y": 126.924931
                },
                {
                    "x": 37.556295,
                    "y": 126.924931
                },
                {
                    "x": 37.556295,
                    "y": 126.924931
                },
                {
                    "x": 37.556048,
                    "y": 126.924555
                },
                {
                    "x": 37.555793,
                    "y": 126.924244
                },
                {
                    "x": 37.555393,
                    "y": 126.923557
                },
                {
                    "x": 37.554525,
                    "y": 126.922795
                },
                {
                    "x": 37.554357,
                    "y": 126.922452
                }
            ],
            "picture": "https://ddubuk.s3.ap-northeast-2.amazonaws.com/recommendFile/B1.svg",
            "walkTime": 600,
            "distance": 1000,
            "altitudes": 5,
            "division": "master",
            "dist": 2956.2863337343624
        }
    ]
 */