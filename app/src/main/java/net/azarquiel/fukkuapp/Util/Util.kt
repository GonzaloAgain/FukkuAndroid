package net.azarquiel.fukkuapp.Util

import android.Manifest
import android.app.ProgressDialog
import android.content.pm.PackageManager
import android.net.Uri
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import com.robertlevonyan.components.picker.ItemModel
import com.robertlevonyan.components.picker.PickerDialog
import kotlinx.android.synthetic.main.activity_add_producto.*
import org.jetbrains.anko.indeterminateProgressDialog

object Util {

    private lateinit var p: ProgressDialog

    fun inicia(activity: AppCompatActivity){
        p=activity.indeterminateProgressDialog("Uploading product")
        p.show()
    }

    fun finaliza(){
        p.hide()
    }

    fun activatePermissionCheck(activity: AppCompatActivity){
        var permissionCheck= ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)

        if(permissionCheck == PackageManager.PERMISSION_DENIED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_FINE_LOCATION)){

            }else{
                ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),1)
            }
        }
    }
}