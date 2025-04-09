package com.example.receipe_app2

import com.google.gson.annotations.SerializedName

data class api_response(
    @SerializedName("COOKRCP01")
    val cOOKRCP01: COOKRCP01
)

data class COOKRCP01(
    @SerializedName("RESULT")
    val rESULT: RESULT,
    @SerializedName("row")
    val row: List<Row>,
    @SerializedName("total_count")
    val totalCount: String
)

data class RESULT(
    @SerializedName("CODE")
    val cODE: String,
    @SerializedName("MSG")
    val mSG: String
)

data class Row(
    @SerializedName("ATT_FILE_NO_MAIN")
    val aTTFILENOMAIN: String,
    @SerializedName("ATT_FILE_NO_MK")
    val aTTFILENOMK: String,
    @SerializedName("HASH_TAG")
    val hASHTAG: String,
    @SerializedName("INFO_CAR")
    val iNFOCAR: String,
    @SerializedName("INFO_ENG")
    val iNFOENG: String,
    @SerializedName("INFO_FAT")
    val iNFOFAT: String,
    @SerializedName("INFO_NA")
    val iNFONA: String,
    @SerializedName("INFO_PRO")
    val iNFOPRO: String,
    @SerializedName("INFO_WGT")
    val iNFOWGT: String,
    @SerializedName("MANUAL01")
    val mANUAL01: String,
    @SerializedName("MANUAL02")
    val mANUAL02: String,
    @SerializedName("MANUAL03")
    val mANUAL03: String,
    @SerializedName("MANUAL04")
    val mANUAL04: String,
    @SerializedName("MANUAL05")
    val mANUAL05: String,
    @SerializedName("MANUAL06")
    val mANUAL06: String,
    @SerializedName("MANUAL07")
    val mANUAL07: String,
    @SerializedName("MANUAL08")
    val mANUAL08: String,
    @SerializedName("MANUAL09")
    val mANUAL09: String,
    @SerializedName("MANUAL10")
    val mANUAL10: String,
    @SerializedName("MANUAL11")
    val mANUAL11: String,
    @SerializedName("MANUAL12")
    val mANUAL12: String,
    @SerializedName("MANUAL13")
    val mANUAL13: String,
    @SerializedName("MANUAL14")
    val mANUAL14: String,
    @SerializedName("MANUAL15")
    val mANUAL15: String,
    @SerializedName("MANUAL16")
    val mANUAL16: String,
    @SerializedName("MANUAL17")
    val mANUAL17: String,
    @SerializedName("MANUAL18")
    val mANUAL18: String,
    @SerializedName("MANUAL19")
    val mANUAL19: String,
    @SerializedName("MANUAL20")
    val mANUAL20: String,
    @SerializedName("MANUAL_IMG01")
    val mANUALIMG01: String,
    @SerializedName("MANUAL_IMG02")
    val mANUALIMG02: String,
    @SerializedName("MANUAL_IMG03")
    val mANUALIMG03: String,
    @SerializedName("MANUAL_IMG04")
    val mANUALIMG04: String,
    @SerializedName("MANUAL_IMG05")
    val mANUALIMG05: String,
    @SerializedName("MANUAL_IMG06")
    val mANUALIMG06: String,
    @SerializedName("MANUAL_IMG07")
    val mANUALIMG07: String,
    @SerializedName("MANUAL_IMG08")
    val mANUALIMG08: String,
    @SerializedName("MANUAL_IMG09")
    val mANUALIMG09: String,
    @SerializedName("MANUAL_IMG10")
    val mANUALIMG10: String,
    @SerializedName("MANUAL_IMG11")
    val mANUALIMG11: String,
    @SerializedName("MANUAL_IMG12")
    val mANUALIMG12: String,
    @SerializedName("MANUAL_IMG13")
    val mANUALIMG13: String,
    @SerializedName("MANUAL_IMG14")
    val mANUALIMG14: String,
    @SerializedName("MANUAL_IMG15")
    val mANUALIMG15: String,
    @SerializedName("MANUAL_IMG16")
    val mANUALIMG16: String,
    @SerializedName("MANUAL_IMG17")
    val mANUALIMG17: String,
    @SerializedName("MANUAL_IMG18")
    val mANUALIMG18: String,
    @SerializedName("MANUAL_IMG19")
    val mANUALIMG19: String,
    @SerializedName("MANUAL_IMG20")
    val mANUALIMG20: String,
    @SerializedName("RCP_NA_TIP")
    val rCPNATIP: String,
    @SerializedName("RCP_NM")
    val rCPNM: String,
    @SerializedName("RCP_PARTS_DTLS")
    val rCPPARTSDTLS: String,
    @SerializedName("RCP_PAT2")
    val rCPPAT2: String,
    @SerializedName("RCP_SEQ")
    val rCPSEQ: String,
    @SerializedName("RCP_WAY2")
    val rCPWAY2: String
)
