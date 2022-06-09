#!/bin/sh

cd /home/acarlos/usr/local/workspace/helmet_detection/custom-tf2/data/tflite/
rm -Rf saved_model/
cd /home/acarlos/usr/local/workspace/helmet_detection/custom-tf2/data/tflite/tflite_with_metadata/
mv detect.tflite /home/acarlos/usr/local/workspace/helmet_detection/app/src/main/assets/detect.tflite
cd ..
rm detect.tflite
cd ..
rm test.record train.record train_labels.csv test_labels.csv label_map.pbtxt
cd train_labels/
rm *.xml
cd ..
cd test_labels/
rm *.xml
cd ..
cd inference_graph/
rm -Rf *
cd ..
cd images/
rm *.jpeg
cd ..
cp labelmap.txt /home/acarlos/usr/local/workspace/helmet_detection/app/src/main/assets/labelmap.txt
cd ..
cd training
rm !\(.gitignore\) -Rf *
killall tensorboard