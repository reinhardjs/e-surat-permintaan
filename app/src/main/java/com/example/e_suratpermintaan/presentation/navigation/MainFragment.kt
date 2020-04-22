package com.example.e_suratpermintaan.presentation.navigation

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.e_suratpermintaan.core.domain.entities.responses.*
import com.example.e_suratpermintaan.R
import com.example.e_suratpermintaan.framework.helpers.NavOptionsHelper
import com.example.e_suratpermintaan.framework.sharedpreference.ProfilePreference
import com.example.e_suratpermintaan.presentation.adapter.SuratPermintaanAdapter
import com.example.e_suratpermintaan.presentation.base.BaseFragment
import com.example.e_suratpermintaan.presentation.viewmodel.MasterViewModel
import com.example.e_suratpermintaan.presentation.viewmodel.SuratPermintaanViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import io.reactivex.rxjava3.core.Observable
import kotlinx.android.synthetic.main.dialog_ajukan_sp.view.*
import kotlinx.android.synthetic.main.fragment_main.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


/**
 * A simple [Fragment] subclass.
 */

class MainFragment : BaseFragment() {

    private val suratPermintaanViewModel: SuratPermintaanViewModel by viewModel()
    private val masterViewModel: MasterViewModel by viewModel()
    private val profilePreference: ProfilePreference by inject()

    private lateinit var idUser: String
    private lateinit var idProyek: String
    private lateinit var namaJenis: String

    private val proyekList: ArrayList<DataMasterProyek> = ArrayList()
    private val jenisList: ArrayList<DataMasterJenis> = ArrayList()

    lateinit var suratPermintaanAdapter: SuratPermintaanAdapter

    override fun layoutId(): Int = R.layout.fragment_main

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.clearFocus()

        btnLogout.setOnClickListener {
            profilePreference.removeProfile()

            val navOptions =
                NavOptionsHelper.getInstance().addBackToSplashAnim()
                    .clearBackStack(R.id.welcomeFragment).build()
            it.findNavController()
                .navigate(R.id.action_mainFragment_to_splashScreen, null, navOptions)
        }

        btnAjukan.setOnClickListener {
            startShowDialog()
        }

        val profileId = profilePreference.getProfile()?.id

        if (profileId != null) {
            idUser = profileId

            val spObservable = suratPermintaanViewModel.readMyData(profileId)
            val proyekObservable = masterViewModel.getProyekList(profileId)
            val jenisObservable = masterViewModel.getJenisList(profileId)

            disposable = Observable.concat(spObservable, proyekObservable, jenisObservable)
                .subscribe(this::handleResponse, this::handleError)
        }
    }

    private fun handleResponse(response: Any) {
        when (response) {
            is MyDataResponse -> {

                val suratPermintaanList: List<DataMyData?>? = response.data
                val spList: ArrayList<SuratPermintaan?> = arrayListOf()

                suratPermintaanList?.forEach {
                    spList.add(it)
                }

                suratPermintaanAdapter = SuratPermintaanAdapter(spList)
                suratPermintaanAdapter.setOnClickListener(object: SuratPermintaanAdapter.OnClickItemListener {
                    override fun onClick(view: View, item: SuratPermintaan?) {
                        var bundle = bundleOf("id_sp" to (item as DataMyData).id)
                        val navOptions =
                            NavOptionsHelper.getInstance().addDefaultAnim().build()
                        view.findNavController()
                            .navigate(R.id.action_mainFragment_to_detailSuratPermintaanFragment, bundle, navOptions)
                    }
                })

                recyclerView.layoutManager = LinearLayoutManager(requireContext())
                recyclerView.adapter = suratPermintaanAdapter

            }
            is MasterProyekResponse -> {

                response.data?.forEach {
                    if (it != null) {
                        proyekList.add(it)
                    }
                }

            }
            is MasterJenisResponse -> {

                response.data?.forEach {
                    if (it != null) {
                        jenisList.add(it)
                    }
                }
            }
            is CreateSPResponse -> {

                toastNotify(response.message)

            }
        }
    }

    private fun handleError(error: Throwable) {
        Toast.makeText(context, error.message.toString(), Toast.LENGTH_LONG).show()
    }

    private fun startShowDialog() {

        val alertDialogBuilder =
            MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialogTheme)
                .setTitle("Ajukan Surat Permintaan")

        var alertDialog = alertDialogBuilder.create()

        val dialogRootView =
            requireActivity().layoutInflater.inflate(R.layout.dialog_ajukan_sp, null)

        val proyekAdapter =
            ArrayAdapter(requireContext(), R.layout.material_spinner_item, proyekList)
        val jenisAdapter = ArrayAdapter(requireContext(), R.layout.material_spinner_item, jenisList)

        dialogRootView.spinnerProyek.setAdapter(proyekAdapter)
        dialogRootView.spinnerJenis.setAdapter(jenisAdapter)
        dialogRootView.btnAjukan.setOnClickListener {
            val selectedProyek = dialogRootView.spinnerProyek.text.toString()
            val selectedJenis = dialogRootView.spinnerJenis.text.toString()

            idProyek = proyekList.find { it.nama == selectedProyek }?.id.toString()
            namaJenis = jenisList.find { it.nama == selectedJenis }?.nama.toString()

            alertDialog.hide()

            alertDialog = alertDialogBuilder
                .setMessage("Apakah Anda yakin ingin mengajukan?")
                .setPositiveButton("Ya") { _, _ ->

                    //                    val createSP = CreateSP(idProyek, namaJenis, idUser)
                    disposable = suratPermintaanViewModel.add(idProyek, namaJenis, idUser)
                        .subscribe(this::handleResponse, this::handleError)

                    toastNotify("ID PROYEK : $idProyek \nNama Jenis : $namaJenis \nID USER : $idUser")

                    alertDialog.hide()

                }.create()

            alertDialog.show()
        }


        alertDialog.setView(dialogRootView)
        alertDialog.show()
    }
}
