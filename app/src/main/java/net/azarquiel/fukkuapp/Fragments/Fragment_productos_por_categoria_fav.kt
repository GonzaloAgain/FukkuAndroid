package net.azarquiel.fukkuapp.Fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import net.azarquiel.fukkuapp.R

class Fragment_productos_por_categoria_fav : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        super.onCreate(savedInstanceState)
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_productos_por_categoria_fav, container, false)

        //... nos traemos las cositas del diseño y trabajamos.
        //... si queremos algo de la activity llamamos al método getActivity()
        //btnPulsa = rootView.findViewById(R.id.btnPulsa) as Button
        //btnPulsa.setOnClickListener(View.OnClickListener { tostada("Pulsaste...") })

        return rootView
    }

}