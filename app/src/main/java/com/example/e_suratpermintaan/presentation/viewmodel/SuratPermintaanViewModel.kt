package com.example.e_suratpermintaan.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.e_suratpermintaan.core.data.datasource.SuratPermintaanDataSource
import com.e_suratpermintaan.core.domain.entities.requests.CreateSP
import com.e_suratpermintaan.core.domain.entities.responses.CreateSPResponse
import com.e_suratpermintaan.core.domain.entities.responses.DeleteSPResponse
import com.e_suratpermintaan.core.domain.entities.responses.MyDataResponse
import com.e_suratpermintaan.domain.usecases.suratpermintaan.AddSuratPermintaanUseCase
import com.e_suratpermintaan.domain.usecases.suratpermintaan.ReadAllDataSuratPermintaanUseCase
import com.e_suratpermintaan.domain.usecases.suratpermintaan.ReadMyDataSuratPermintaanUseCase
import com.e_suratpermintaan.domain.usecases.suratpermintaan.RemoveSuratPermintaanUseCase
import io.reactivex.rxjava3.core.Observable

class SuratPermintaanViewModel(
    private val addSuratPermintaanUseCase: AddSuratPermintaanUseCase,
    private val readAllDataSuratPermintaanUseCase: ReadAllDataSuratPermintaanUseCase,
    private val readMyDataSuratPermintaanUseCase: ReadMyDataSuratPermintaanUseCase,
    private val removeSuratPermintaanUseCase: RemoveSuratPermintaanUseCase
) : ViewModel(), SuratPermintaanDataSource {

    override fun add(sp: CreateSP): Observable<CreateSPResponse> = addSuratPermintaanUseCase.invoke(sp)

    override fun readAllData(id_user: String): Observable<MyDataResponse> =
        readAllDataSuratPermintaanUseCase.invoke(id_user)

    override fun readMyData(id_user: String): Observable<MyDataResponse> =
        readMyDataSuratPermintaanUseCase.invoke(id_user)

    override fun remove(id_sp: String): Observable<DeleteSPResponse> =
        removeSuratPermintaanUseCase.invoke(id_sp)

}