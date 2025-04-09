package com.example.receipe_app2

fun Row.getAllInstructions(): String {
    return listOfNotNull(
        mANUAL01, mANUAL02, mANUAL03, mANUAL04, mANUAL05,
        mANUAL06, mANUAL07, mANUAL08, mANUAL09, mANUAL10,
        mANUAL11, mANUAL12, mANUAL13, mANUAL14, mANUAL15,
        mANUAL16, mANUAL17, mANUAL18, mANUAL19, mANUAL20
    ).joinToString("\n")
}

fun Row.getAllImages(): Array<String?> {
    return arrayOf(
        mANUALIMG01, mANUALIMG02, mANUALIMG03, mANUALIMG04, mANUALIMG05,
        mANUALIMG06, mANUALIMG07, mANUALIMG08, mANUALIMG09, mANUALIMG10,
        mANUALIMG11, mANUALIMG12, mANUALIMG13, mANUALIMG14, mANUALIMG15,
        mANUALIMG16, mANUALIMG17, mANUALIMG18, mANUALIMG19, mANUALIMG20
    )
}
