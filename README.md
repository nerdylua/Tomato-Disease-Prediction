# Tomato Disease Prediction

This project aims to accurately predict and identify various diseases affecting tomato plants using image-based analysis and machine learning techniques. This tool can help farmers and agricultural professionals quickly identify diseases and apply the necessary treatments to improve crop health and yield.

## Table of Contents
- [Overview](#overview)
- [Features](#features)
- [Technologies Used](#technologies-used)
- [Dataset](#dataset)
- [Installation](#installation)
- [Usage](#usage)
- [Model Training](#model-training)
- [Model Evaluation](#model-evaluation)
- [Future Work](#future-work)
- [Contributing](#contributing)
- [License](#license)
- [Acknowledgements](#acknowledgements)

---

## Overview
Tomato plants are prone to various diseases that can significantly affect crop quality and yield. This project leverages computer vision and deep learning techniques to detect diseases from images of tomato leaves. By using a well-trained convolutional neural network (CNN), the model can classify images into various disease categories, allowing for timely and accurate treatment recommendations.

## Features
- **Image-based Disease Detection**: Uses deep learning to classify tomato leaf images into different disease categories.
- **Real-time Inference**: Provides predictions quickly, making it suitable for field usage.
- **Scalable and Customizable**: Allows for additional disease classes and new plant types in future versions.

## Technologies Used
- **Python**: Core programming language for the project.
- **TensorFlow/Keras**: Framework for building and training deep learning models.
- **OpenCV**: For image processing and augmentation.
- **Jupyter Notebook**: Used for interactive model development and experimentation.
- **Numpy and Pandas**: For data manipulation and preprocessing.

## Dataset
The dataset used in this project consists of labeled images of tomato leaves affected by various diseases as well as healthy leaves. The images are preprocessed and augmented to improve the model's performance and generalization.

Dataset source: [Kaggle: Tomato Disease Dataset](https://www.kaggle.com/datasets/arjuntejaswi/plant-village) 

**Classes:**
1. Healthy
2. Early Blight
3. Late Blight

## Installation
1. Clone the repository:
   ```bash
   git clone https://github.com/nerdylelouch/Tomato-Disease-Prediction.git
   cd Tomato-Disease-Prediction
2. Install the required packages:
   ```bash
   pip install -r requirements.txt

## Usage
1. Pre-trained Model Inference:
   - Run the following command to load a saved model and make predictions:
     ```bash
     python predict.py --image_path "path_to_image.jpg"
   - Replace `"path_to_image.jpg"` with the path to the leaf image you want to classify.
1. Training a New Model:
   - If you want to train the model from scratch with your dataset, refer to the [Model Training section](#model-training).

## Model Training
To train the model, follow these steps:
1. Organize your dataset in the following structure:
  ```bash
data/
├── train/
│   ├── Early_Blight/
│   ├── Late_Blight/
│   ├── Healthy/
│   └── ...
└── test/
    ├── Early_Blight/
    ├── Late_Blight/
    ├── Healthy/
    └── ...
```
2. Run the training script:
   ```bash
   python train.py --epochs 50 --batch_size 32
- Adjust the `--epochs` and `--batch_size` parameters as per your system capabilities and requirements.
3. The model and training logs will be saved in the 'models/' directory.
## Model Evaluation
To evaluate the trained model's performance on the test dataset:
```bash
python evaluate.py --model_path models/trained_model.h5
```
This script will display metrics such as accuracy and precision.

## Future Work
- **Additional Classes**: Expand the model to include more diseases and support other plant types.
- **Mobile Integration**: Develop a mobile app to allow users to upload leaf images directly from the field for immediate diagnosis.
- **Improved Augmentation**: Experiment with advanced augmentation techniques to further improve model generalization.

## Contributing
Contributions are welcome! Please follow these steps to contribute:
1. Fork the repository.
2. Create a new branch (`git checkout -b feature/new-feature`).
3. Make your changes and commit (`git commit -am 'Add new feature'`).
4. Push to the branch (`git push origin feature/new-feature`).
5. Open a pull request.

## License
This project is licensed under the MIT License. See the [LICENSE](./LICENSE) file for more details.

## Acknowledgements
- The dataset was sourced from [Kaggle](https://www.kaggle.com/datasets/arjuntejaswi/plant-village) or specify the actual dataset provider.
- Special thanks to the open-source community for the libraries and resources that made this project possible.

Enjoy using the Tomato Disease Prediction tool and feel free to contribute to make it even better!
```vbnet

This README is designed to provide clarity and ease of use for users and contributors. Let me know if you'd like adjustments!
```
