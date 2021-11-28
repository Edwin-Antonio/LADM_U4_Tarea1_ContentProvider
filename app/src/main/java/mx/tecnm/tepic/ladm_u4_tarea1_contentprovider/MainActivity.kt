package mx.tecnm.tepic.ladm_u4_tarea1_contentprovider

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.CallLog
import android.provider.ContactsContract
import androidx.core.app.ActivityCompat
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    //Los valores de los permisos deben de estar por encima de los 5
    val permiso = 5

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if(ActivityCompat.checkSelfPermission(this,android.Manifest.permission.READ_CALL_LOG)!=
            PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_CALL_LOG,
                android.Manifest.permission.READ_CONTACTS),
                permiso)

        }else{
            leerLlamadas()
            leerContactos()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode==permiso){
            leerLlamadas()
            leerContactos()
        }
    }

    private fun leerLlamadas() {
        var resultado = ""
        /*Tipos de llamadas
        * 1 -> Llamada Entrante
        * 2 -> Llamada Saliente
        * 3 -> Llamada Perdida
        * 4 -> Cooreo de voz
        * 5 -> Llamada Rechazada
        * 6 -> Numeros bloqueados
        * */

        if(ActivityCompat.checkSelfPermission(this,android.Manifest.permission.READ_CALL_LOG)!=
            PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_CALL_LOG), permiso)
        }
            //Uri.parse("content://call_log/calls");
            var cursor = contentResolver.query(CallLog.Calls.CONTENT_URI,null,null,null,null)
            if(cursor!!.moveToFirst()){
                var posColumnaNumero = cursor.getColumnIndex(CallLog.Calls.NUMBER)
                var posColumnaTipo = cursor.getColumnIndex(CallLog.Calls.TYPE)
                var posColumnaFecha = cursor.getColumnIndex(CallLog.Calls.DATE)
                var callType =""

                do{
                    when(cursor.getString(posColumnaTipo).toInt()){
                        1 -> callType = "Llamada Entrante"
                        2 -> callType = "Llamada Saliente"
                        3 -> callType = "Llamada Perdida"
                        4 -> callType = "Cooreo de voz"
                        5 -> callType = "Llamada Rechazada"
                        6 -> callType = "Llamada Saliente"
                        else -> callType = "Numeros bloqueados"
                    }
                    val fechaLlamada = cursor.getString(posColumnaFecha)
                    resultado += "Numero: "+cursor.getString(posColumnaNumero)+
                            "\nTipo de Llamada: "+callType+"\nFecha: "+Date(fechaLlamada.toLong())+"\n-------------------------\n"
                }while (cursor.moveToNext())
            }else{
                resultado = "NO HAY LLAMADAS"
            }
            lista.setText(resultado)
    }

    private fun leerContactos() {
        var resultado = ""
        //Uri.parse("content://com.android.contacts/contacts")
        //ContactsContract.Contacts.CONTENT_URI
        val cursorContactos = contentResolver.query(ContactsContract.Contacts.CONTENT_URI,null,null,null,null)
        if(cursorContactos!!.moveToFirst()){
            var hola = cursorContactos.getColumnIndex(ContactsContract.Contacts._ID)
            var hola2 = cursorContactos.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
            var hola3 = cursorContactos.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)
            do {
                var idContact = cursorContactos.getString(hola)


                var nombreContact = cursorContactos.getString(hola2)

                var telefonoContactos = ""

                //Pregunta si el contacto tiene telefono

                if(cursorContactos.getInt(hola3)>0){
                    var celCursor = contentResolver.query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                        arrayOf(idContact.toString()),null)
                    while (celCursor!!.moveToNext()){
                        var hola4 = celCursor!!.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                        telefonoContactos += celCursor.getString(hola4)+" \n"
                    }
                    celCursor!!.close()
                }
                resultado += "ID: "+idContact+"\nNombre: "+nombreContact+"\nTelefono: "+telefonoContactos+"\n--------------------\n"
            }while (cursorContactos.moveToNext())
        }else {
            resultado = "Contactos: No hay contactos capturados"
        }
        txt_contacts.setText(resultado)
    }
}