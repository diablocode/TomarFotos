package mx.udg.cucea.tomarfotos

import android.Manifest.permission.CAMERA
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import mx.udg.cucea.tomarfotos.ui.theme.TomarFotosTheme

class MainActivity : ComponentActivity() {
    private val imageViewModel by lazy { ImageViewModel() }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val hasPermission =
            ContextCompat.checkSelfPermission(
                this@MainActivity,
                CAMERA
            ) == PackageManager.PERMISSION_GRANTED

        if (!hasPermission) {
            ActivityCompat.requestPermissions(this, arrayOf(CAMERA), 1)
        }
        val cameraLauncher: ActivityResultLauncher<Intent> =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val bitmap: Bitmap = result.data?.extras?.get("data") as Bitmap
                    imageViewModel.setImageBitmap(bitmap)
                }
            }
        setContent {
            TomarFotosTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting(cameraLauncher)
                }
            }
        }
    }


    @Composable
    fun Greeting(cameraLauncher: ActivityResultLauncher<Intent>) {
        var imageBitmap = imageViewModel.imageBitmap.value
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = CenterHorizontally
        ) {
            Button(
                onClick = {
                    val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    cameraLauncher.launch(takePictureIntent)
                },
                modifier = Modifier.padding(16.dp)
            ) {
                Text(text = "Take Picture")
            }

            imageBitmap?.let {
                Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = "Captured Image",
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}
class ImageViewModel : ViewModel() {
    val imageBitmap = mutableStateOf<Bitmap?>(null)

    fun setImageBitmap(bitmap: Bitmap?) {
        imageBitmap.value = bitmap
    }
}