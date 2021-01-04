package de.annee.softheat

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import de.annee.softheat.model.SharedModel
import kotlinx.android.synthetic.main.fragment_second.*


/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class SecondFragment : Fragment(), RotaryKnobView.RotaryKnobListener {

    private val model: SharedModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_second, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        knob.listener = this

        Log.w("Debug", "current Value is ${model.currentTarget.value}")

        view.viewTreeObserver.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                view.viewTreeObserver.removeOnGlobalLayoutListener(this)

                knob.applyValue(model.currentTarget.value ?: 20, true)
            }
        })

        view.findViewById<Button>(R.id.button_second).setOnClickListener {
            findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
        }

        model.currentTarget.observe(viewLifecycleOwner, Observer { s ->
            view.findViewById<TextView>(R.id.current_target).text = "$s°C"

            // TODO man könnte hier auf den eingestellten Wert reagieren. Das hätte zur Folge,
            //      dass Änderungen durch andere Nutzer sich sofort in der Oberfläche spiegeln.
            //      Allerdings ist das Verhalten beim Scrollen evtl. etwas eigenartig, weil
            //      der Knopf etwas "springt"
           // knob.applyValue(s, true)
        })
    }

    override fun onRotate(value: Int) {

        model.setNewTarget(value)
    }
}