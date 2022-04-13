package com.example.logiapplication.carrier.ui.syncup

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.logiapplication.R
import com.example.logiapplication.databinding.CarrierFragmentSyncupBinding
import ingenieria.jhr.bluetoothjhr.BluetoothJhr
import java.util.*


class SyncUpFragment : Fragment() {

    private lateinit var syncUpViewModel: SyncUpViewModel
    private var _binding: CarrierFragmentSyncupBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    lateinit var turnOnBlue: Button
    lateinit var viewDevices: Button
    lateinit var adapterBlue: BluetoothAdapter
    lateinit var listDevices: ListView
    private val mDeviceList: ArrayList<String> = ArrayList()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        syncUpViewModel =
            ViewModelProvider(this).get(SyncUpViewModel::class.java)

        _binding = CarrierFragmentSyncupBinding.inflate(inflater, container, false)
        val root: View = binding.root

        viewDevices = binding.root.findViewById(R.id.btn_devices_sync)
        turnOnBlue = binding.root.findViewById(R.id.btn_on_bluetooth)
        listDevices = binding.root.findViewById(R.id.lv_devices)

        val bluetoothManager = requireContext().getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        adapterBlue = bluetoothManager.adapter


        turnOnBlue.setOnClickListener(View.OnClickListener {
            val Intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(Intent, 1)
            Toast.makeText(requireContext(), "Bluetooth encendido",Toast.LENGTH_SHORT).show()

            val Intent2 = Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE)
            startActivityForResult(Intent2, 2)

        })
        //Conectar a dispositivo
        val bluetoothJhr = BluetoothJhr(requireContext(), listDevices, ConnectionActivity::class.java)

        viewDevices.setOnClickListener(View.OnClickListener{
            val all_devices: Set<BluetoothDevice> = adapterBlue.getBondedDevices()
            if (all_devices.size > 0) {
                for (currentDevice in all_devices) {
                    mDeviceList.add(
                        """
                Device Name: ${currentDevice.name}
                Device Address: ${currentDevice.address}
                """.trimIndent()
                    )
                    listDevices.setAdapter(
                        ArrayAdapter(requireContext(),
                            android.R.layout.simple_list_item_1, mDeviceList
                        )
                    )
                }
            }
        })
        listDevices.setOnItemClickListener { adapterView, view, i, l ->
            bluetoothJhr.bluetoothSeleccion(i)
        }

        return root
    }

    override fun onResume() {
        super.onResume()
        turnOnBlue = binding.root.findViewById(R.id.btn_on_bluetooth)

        val bluetoothManager = requireContext().getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        adapterBlue = bluetoothManager.adapter

        turnOnBlue.setOnClickListener(View.OnClickListener {
            val Intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(Intent, 1)
            Toast.makeText(requireContext(), "Bluetooth encendido",Toast.LENGTH_SHORT).show()
        })


    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}