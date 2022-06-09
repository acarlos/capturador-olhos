#!/bin/sh

# clone the tensorflow models on the colab cloud vm
#git clone --q https://github.com/tensorflow/models.git

#navigate to /models/research folder to compile protos
#cd models/research

# Compile protos.
#protoc object_detection/protos/*.proto --python_out=.

# Install TensorFlow Object Detection API.
#cp object_detection/packages/tf2/setup.py .
#python3 -m pip install .

# testing the model builder

#pip install numpy --upgrade
#python3 object_detection/builders/model_builder_tf2_test.py

# Create and unpack the images and labels PascalVOC
#unzip /mydrive/customTF2/images.zip -d .
#unzip /mydrive/customTF2/annotations.zip -d .

#Copy images and annotations
echo '#Copy images and annotations'
cd /home/acarlos/usr/local/workspace/helmet_detection/custom-tf2/images/
cp ./com_capacete/*.jpeg ../data/images/
cp ./com_capacete_bicicleta/*.jpeg ../data/images/
cp ./sem_capacete/*.jpeg ../data/images/
cd ..
cd annotations
cp *.xml ../data/annotations/

# Go to directory
echo '# Go to directory'
cd /home/acarlos/usr/local/workspace/helmet_detection/custom-tf2/data/

# Create test_labels & train_labels - 20% of 211
echo '# Create test_labels & train_labels - 20% of 190'
ls annotations/* | sort -R | head -38 | xargs -I{} mv {} test_labels/

# Moves the rest of the labels ( 1096 labels ) to the training dir: `train_labels`
echo '# Moves the rest of the labels ( 1096 labels ) to the training dir: train_labels'
ls annotations/* | xargs -I{} mv {} train_labels/

#Create the CSV  and the “label_map.pbtxt” file
echo '#Create the CSV  and the “label_map.pbtxt” file'
python3 xml_to_csv.py

#Create train.record & test.record 
#Usage:
#!python3 generate_tfrecord.py output.csv output_pb.txt /path/to/images output.tfrecords
#For train.record
echo 'Create train.record & test.record'
python3 generate_tfrecord.py train_labels.csv  label_map.pbtxt images/ train.record
#For test.record
python3 generate_tfrecord.py test_labels.csv  label_map.pbtxt images/ test.record

#Currently, TFLite supports only SSD models (excluding EfficientDet)
#ssd_mobilenet_v2_fpnlite_320x320_coco17_tpu-8
#Get the model pipeline config file, make changes to it, and put it inside the data folder(.config)

#Load Tensorboard
echo '#Load Tensorboard'
tensorboard --logdir '/home/acarlos/usr/local/workspace/helmet_detection/custom-tf2/training' &

cd /home/acarlos/usr/local/workspace/models/research/object_detection
#Set variables
echo 'Set variables'
PIPELINE_CONFIG_PATH=/home/acarlos/usr/local/workspace/helmet_detection/custom-tf2/data/ssd_mobilenet_v2_fpnlite_320x320_coco17_tpu-8/pipeline.config
MODEL_DIR=/home/acarlos/usr/local/workspace/helmet_detection/custom-tf2/training
NUM_TRAIN_STEPS=10000
SAMPLE_1_OF_N_EVAL_EXAMPLES=1

#Training using model_main_tf2.py
echo '#Training using model_main_tf2.py'
python3 model_main_tf2.py --pipeline_config_path=/home/acarlos/usr/local/workspace/helmet_detection/custom-tf2/data/ssd_mobilenet_v2_fpnlite_320x320_coco17_tpu-8/pipeline.config --model_dir=/home/acarlos/usr/local/workspace/helmet_detection/custom-tf2/training --alsologtostderr

#Evaluate model
#echo '#Evaluating using model_main_tf2.py'
#python3 model_main_tf2.py --pipeline_config_path=/home/acarlos/usr/local/workspace/helmet_detection/custom-tf2/data/ssd_mobilenet_v2_fpnlite_320x320_coco17_tpu-8/pipeline.config --model_dir=/home/acarlos/usr/local/workspace/helmet_detection/custom-tf2/training/ --checkpoint_dir=/home/acarlos/usr/local/workspace/helmet_detection/custom-tf2/training/ --alsologtostderr & 

#Test your trained model
echo '#Test your trained model'
python3 exporter_main_v2.py --trained_checkpoint_dir=/home/acarlos/usr/local/workspace/helmet_detection/custom-tf2/training --pipeline_config_path=/home/acarlos/usr/local/workspace/helmet_detection/custom-tf2/data/ssd_mobilenet_v2_fpnlite_320x320_coco17_tpu-8/pipeline.config --output_directory /home/acarlos/usr/local/workspace/helmet_detection/custom-tf2/data/inference_graph

#CONVERTING THE TRAINED SSD MODEL TO TFLITE MODEL
#Export SSD TFlite graph
echo 'Export SSD TFlite graph'
python3 export_tflite_graph_tf2.py --pipeline_config_path /home/acarlos/usr/local/workspace/helmet_detection/custom-tf2/data/ssd_mobilenet_v2_fpnlite_320x320_coco17_tpu-8/pipeline.config --trained_checkpoint_dir /home/acarlos/usr/local/workspace/helmet_detection/custom-tf2/training --output_directory /home/acarlos/usr/local/workspace/helmet_detection/custom-tf2/data/tflite

#Convert the TensorFlow saved model to the TFlite model
echo '#Convert the TensorFlow saved model to the TFlite model'
saved_model_cli show --dir /home/acarlos/usr/local/workspace/helmet_detection/custom-tf2/data/tflite/saved_model --tag_set serve --all

#Convert to TFlite
echo '#Convert to TFlite'
tflite_convert --saved_model_dir=/home/acarlos/usr/local/workspace/helmet_detection/custom-tf2/data/tflite/saved_model --output_file=/home/acarlos/usr/local/workspace/helmet_detection/custom-tf2/data/tflite/detect.tflite

#Attach metadata to the TFLite model
echo '#Attach metadata to the TFLite model'
cd /home/acarlos/usr/local/workspace/helmet_detection/custom-tf2/data/
python3 tflite_metadata.py

#Clean it up
/home/acarlos/usr/local/workspace/helmet_detection/custom-tf2/./clean.sh



