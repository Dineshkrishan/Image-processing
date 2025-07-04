Signature Detection
Detects signature copied is false or real as the exact copy that is given as input

🧠 Overview
The Signature Forgery Detection App is a powerful AI-based tool that helps verify the authenticity of handwritten signatures using image processing and machine learning. This application is especially useful for banks, educational institutions, legal bodies, and document verification services, offering an automated and reliable way to detect forgeries in real-time.

🔍 Key Features
📤 Upload and compare signature images
✅ Detects whether a signature is genuine or forged
📊 Displays a similarity score
🧠 Uses deep learning (CNNs or Siamese Networks)
💻 Web-based interface for ease of use
🗃️ Trained on real signature datasets (e.g., CEDAR)
🛠️ Tech Stack
Component	Technology
Frontend	HTML, CSS, JavaScript
Backend	Python, Flask
ML Framework	TensorFlow / PyTorch
Image Processing	OpenCV, NumPy
Deployment	Streamlit / Heroku / Localhost
⚙️ How It Works
Image Upload: User uploads two signature images – one reference and one test.
Preprocessing: Images are converted to grayscale, resized, and normalized.
Feature Extraction: Convolutional Neural Networks (CNN) extract key features.
Comparison: A Siamese Network or feature-based algorithm compares the signatures.
Prediction: Model returns a similarity score and verdict (Genuine or Forged).
📦 Dataset Used
We use the CEDAR Signature Dataset, a popular dataset for offline handwritten signature verification, and optionally other datasets like GPDS Synthetic Corpus.

📎 CEDAR Dataset Download

🖥️ Installation & Running Locally
🔧 Prerequisites
Python 3.7+
pip
📦 Step-by-step
Clone the repository:
git clone https://github.com/yourusername/signature-forgery-detection.git
cd signature-forgery-detection
🧠 Model Training - Signature Forgery Detection
This guide explains how to train the deep learning model used for detecting signature forgeries in the Signature Forgery Detection App.

🗂️ Dataset Preparation
To train the model, you need a dataset containing genuine and forged signatures.

🔸 Dataset Used
CEDAR Signature Dataset
Download Link
Each folder should contain .png or .jpg signature images.
