package com.e_suratpermintaan.core.domain.entities.requests

import com.google.gson.annotations.SerializedName

data class CreateSP(

    @field:SerializedName("id_proyek")
    val id_proyek: String,

    @field:SerializedName("jenis")
    val jenis: String,

    @field:SerializedName("id_user")
    val id_user: String
)