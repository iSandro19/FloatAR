package com.floatar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;

import android.net.Uri;
import android.os.Bundle;
import android.view.SurfaceView;

import com.google.ar.core.ArCoreApk;
import com.google.ar.core.Config;
import com.google.ar.core.Session;
import com.google.ar.core.exceptions.CameraNotAvailableException;
import com.google.ar.core.exceptions.UnavailableException;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.ArFragment;

public class ARActivity extends AppCompatActivity {
    /*
    private ArFragment arFragment;
    private Session arSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ar_demo);

        // Verificar compatibilidad de ARCore en el dispositivo
        if (ArCoreApk.getInstance().checkAvailability(this) == ArCoreApk.Availability.UNSUPPORTED_DEVICE_NOT_CAPABLE) {
            // El dispositivo no es compatible con ARCore
            // Maneja la incompatibilidad aquí, si es necesario
        } else {
            // Configurar ARCore y cargar el modelo
            setupAR();
            loadModel();
        }
    }

    private void setupAR() {
        arFragment = new ArFragment();

        // Reemplazar el Fragmento existente en el contenedor con el nuevo ArFragment
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.arContainer, arFragment)
                .commit();

        // Verificar si ARCore está instalado y habilitar la funcionalidad AR
        try {
            arSession = arFragment.getArSceneView().getSession();
            assert arSession != null;
            Config config = new Config(arSession);
            // Configurar la sesión ARCore según tus necesidades
            arSession.configure(config);
            arFragment.getArSceneView().setupSession(arSession);
        } catch (Exception e) {
            // Manejar errores de ARCore no disponible aquí
        }
    }

    private void loadModel() {
        // Cargar el modelo desde el archivo .sfb
        Uri modelUri = Uri.parse("");
        ModelRenderable.builder()
                .setSource(this, modelUri)
                .build()
                .thenAccept(modelRenderable -> {
                    // Agregar el modelo a la escena cuando se haya cargado correctamente
                    if (arFragment != null && arFragment.isAdded()) {
                        Node modelNode = new Node();
                        modelNode.setRenderable(modelRenderable);

                        arFragment.getArSceneView().getScene().addChild(modelNode);
                    }
                })
                .exceptionally(throwable -> {
                    // Manejar errores de carga del modelo aquí, si es necesario
                    return null;
                });
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Administrar el ciclo de vida de ARCore
        try {
            if (arSession != null) {
                arSession.resume();
            }
        } catch (Exception e) {
            // Manejar errores de ARCore no disponible aquí
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Administrar el ciclo de vida de ARCore
        try {
            if (arSession != null) {
                arSession.pause();
            }
        } catch (Exception e) {
            // Manejar errores de ARCore no disponible aquí
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Administrar el ciclo de vida de ARCore
        try {
            if (arSession != null) {
                arSession.close();
                arSession = null;
            }
        } catch (Exception e) {
            // Manejar errores de ARCore no disponible aquí
        }
    }*/
}