import numpy as np
import tensorflow as tf
import streamlit as st
from tensorflow.keras.preprocessing.image import img_to_array
import tempfile

# Set up the page configuration
st.set_page_config(page_title="Tomato Disease Classification", page_icon="🌿", layout="centered")

# Custom CSS styling for the front end
def set_custom_style():
    return """
        <style>
        /* Reset spacing and set dark theme */
        .stApp {
            background-color: #1a1a1a;
        }
        
        /* Container styling */
        .main-container {
            padding: 0; /* Removed extra padding */
            max-width: 1200px;
            margin: 0 auto;
            background-color: #1a1a1a; /* Extend background color to container */
        }
        
        /* Header styling */
        .header {
            text-align: center;
            color: white;
            margin: 0; /* Removed all margins */
        }

        .main-title {
            font-size: 3rem;
            font-weight: bold;
            color: #ff6347; /* Tomato color */
            margin: 0; /* Removed spacing below the title */
        }

        .sub-title {
            font-size: 1.2rem;
            color: #eeeeee;
            margin: 0; /* Removed spacing */
        }

        /* Guidelines box styling */
        .guidelines-box {
            background-color: #333333;
            padding: 1rem;
            border-radius: 8px;
            margin-top: 1rem;
        }

        .results-placeholder {
            text-align: center;
            color: #eeeeee;
        }

        /* Prediction result styling */
        .predicted-class {
            text-align: center;
            font-size: 32px;
            color: #ff6347; /* Tomato red */
        }
        
        .confidence-score {
            text-align: center;
            font-size: 22px;
            color: #2e7d32; /* Green for confidence score */
        }
        </style>
    """

# Apply custom styles
st.markdown(set_custom_style(), unsafe_allow_html=True)

# Title and subtitle with updated styles
st.markdown('<div class="main-container">', unsafe_allow_html=True)
st.markdown(
    '<div class="header">'
    '<h1 class="main-title">🌿 Tomato Disease Classification 🌿</h1>'
    '<p class="sub-title">Upload an image of a tomato leaf to predict if it has any disease.</p>'
    '</div>',
    unsafe_allow_html=True
)

# Define the class names
class_names = ['Early Blight', 'Late Blight', 'Healthy']

# Function to make a prediction
def predict(model, img):
    img_array = img_to_array(img)
    img_array = tf.expand_dims(img_array, 0)  # Create a batch

    predictions = model.predict(img_array)
    predicted_class = class_names[np.argmax(predictions[0])]
    confidence = round(100 * np.max(predictions[0]), 2)
    return predicted_class, confidence

# Load the trained model
model = tf.keras.models.load_model('model.h5')

# Streamlit file uploader with an icon
uploaded_file = st.file_uploader("🌿 Choose an image", type=["jpg", "jpeg", "png"])

if uploaded_file is not None:
    # Save to a temporary location
    with tempfile.NamedTemporaryFile(delete=False, suffix=".jpg") as tmp_file:
        tmp_file.write(uploaded_file.read())
        temp_file_path = tmp_file.name

    # Load and process the image
    img = tf.keras.preprocessing.image.load_img(temp_file_path, target_size=(256, 256))
    predicted_class, confidence = predict(model, img)

    # Display the image with enhanced caption style
    st.image(img, caption="Uploaded Leaf Image", use_column_width=True)
    st.markdown(
        f'<h1 class="predicted-class">{predicted_class}</h1>',
        unsafe_allow_html=True,
    )
    st.markdown(
        f'<h2 class="confidence-score">Confidence: {confidence}%</h2>',
        unsafe_allow_html=True,
    )
