// Copyright 2018 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package com.angadi.mlcodelab.cloudtextrecognition;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.angadi.mlcodelab.StillImageActivity;
import com.angadi.mlcodelab.TextModel;
import com.angadi.mlcodelab.VisionProcessorBase;
import com.angadi.mlcodelab.common.FrameMetadata;
import com.angadi.mlcodelab.common.GraphicOverlay;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;


import java.util.ArrayList;
import java.util.List;

/**
 * Processor for the cloud text detector demo.
 */
public class CloudTextRecognitionProcessor extends VisionProcessorBase<FirebaseVisionText> {

    private static final String TAG = "CloudTextRecProc";

    private final FirebaseVisionTextRecognizer detector;
    private Context context;

    public CloudTextRecognitionProcessor(Context context) {
        super();
        this.context=context;
        detector = FirebaseVision.getInstance().getCloudTextRecognizer();
    }

    @Override
    protected Task<FirebaseVisionText> detectInImage(FirebaseVisionImage image) {
        return detector.processImage(image);
    }

    @Override
    protected void onSuccess(
            @Nullable Bitmap originalCameraImage,
            @NonNull FirebaseVisionText text,
            @NonNull FrameMetadata frameMetadata,
            @NonNull GraphicOverlay graphicOverlay) {
        graphicOverlay.clear();
        if (text == null) {
            return; // TODO: investigate why this is needed
        }
        List<FirebaseVisionText.TextBlock> blocks = text.getTextBlocks();
        Log.e("FirebaseVisionText",""+ text.getText());

        TextModel textModel = new TextModel();
        textModel.setText(text.getText());

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(textModel.getText());
        builder.show();

        ArrayList<String> arrayList = new ArrayList<>();
        for (int i = 0; i < blocks.size(); i++) {
            List<FirebaseVisionText.Line> lines = blocks.get(i).getLines();
            for (int j = 0; j < lines.size(); j++) {

//                if (lines.get(j).getText().contains("Model") || lines.get(j).getText().contains("Model No") ||
//                        lines.get(j).getText().contains("MODEL NO") || lines.get(j).getText().contains("MODEL NUMBER")){
//                    Log.e("FirebaseVisionText2",""+ lines.get(j).getText());
//                }
//                if (lines.get(j).getText().contains("Serial") || lines.get(j).getText().contains("Serial No") ||
//                        lines.get(j).getText().contains("SERIAL NO") || lines.get(j).getText().contains("SERIAL NUMBER")){
//                    Log.e("FirebaseVisionText2",""+ lines.get(j).getText());
//                }

                List<FirebaseVisionText.Element> elements = lines.get(j).getElements();
                for (int l = 0; l < elements.size(); l++) {
                    CloudTextGraphic cloudTextGraphic = new CloudTextGraphic(graphicOverlay,
                            elements.get(l));
                    graphicOverlay.add(cloudTextGraphic);
//                    Log.e("cloudTextGraphic",""+ elements.get(l).getText());
                    arrayList.add(elements.get(l).getText());
//                    Log.e("cloudTextGraphic",""+ arrayList);
                }
            }
        }
        graphicOverlay.postInvalidate();
    }

    @Override
    protected void onFailure(@NonNull Exception e) {
        Log.w(TAG, "Cloud Text detection failed." + e);
    }
}
