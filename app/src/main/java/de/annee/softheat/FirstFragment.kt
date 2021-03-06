package de.annee.softheat

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import de.annee.softheat.model.SharedModel

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private val model: SharedModel by activityViewModels()

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_first, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.button_first).setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }

        model.currentTemperature.observe(viewLifecycleOwner, Observer { s ->
            view.findViewById<TextView>(R.id.current_temperature).text = s
        })
        model.currentHumidity.observe(viewLifecycleOwner, Observer { s ->
            view.findViewById<TextView>(R.id.current_humidity).text = s
        })
        model.currentBattery.observe(viewLifecycleOwner, Observer { s ->
            view.findViewById<TextView>(R.id.current_battery).text = s
        })
        model.currentMode.observe(viewLifecycleOwner, Observer { s ->
            view.findViewById<TextView>(R.id.current_mode).text = s
        })
    }
}