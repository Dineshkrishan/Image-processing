from flask import Flask, render_template, request, redirect, url_for, jsonify
import os
import threading
import forgery
import thresholding
import cv2
import base64
import shutil

app = Flask(__name__)
BASE_UPLOAD_FOLDER = './uploads'
ORIGINAL_UPLOAD_FOLDER = os.path.join(BASE_UPLOAD_FOLDER, 'originals')
TEST_UPLOAD_FOLDER = os.path.join(BASE_UPLOAD_FOLDER, 'tests')
SCORES_FILE = './scores.json'

processing_status = {'status': 'Not Started', 'progress': 0}
log_messages = []

# Registering a custom filter for base64 encoding
@app.template_filter('b64encode')
def b64encode_filter(data):
    return base64.b64encode(data).decode('utf-8')

@app.route('/')
def index():
    full_cleanup()  # Perform a full cleanup when starting again
    # Ensure upload folders exist
    os.makedirs(ORIGINAL_UPLOAD_FOLDER, exist_ok=True)
    os.makedirs(TEST_UPLOAD_FOLDER, exist_ok=True)
    return render_template('index.html')

@app.route('/display-dataset', methods=['POST'])
def display_dataset():
    # Clear the original upload folder before saving new images
    shutil.rmtree(ORIGINAL_UPLOAD_FOLDER)
    os.makedirs(ORIGINAL_UPLOAD_FOLDER, exist_ok=True)

    if 'dataset_folder' not in request.files:
        return "No folder uploaded.", 400

    files = request.files.getlist('dataset_folder')
    
    # Save uploaded images to ORIGINAL_UPLOAD_FOLDER
    for file in files:
        if file and file.filename.lower().endswith(('.png', '.jpg', '.jpeg')):
            filename = file.filename.split('/')[-1]
            file.save(os.path.join(ORIGINAL_UPLOAD_FOLDER, filename))

    sample_images = get_sample_images(ORIGINAL_UPLOAD_FOLDER)
    return render_template('display.html', images=sample_images)

@app.route('/preprocess', methods=['POST'])
def preprocess_dataset():
    processing_status['status'] = "Processing"
    processing_status['progress'] = 0
    log_messages.clear()

    def run_preprocessing():
        def progress_callback(progress):
            processing_status['progress'] = progress

        def message_callback(message):
            log_messages.append(message)

        thresholding.threshold(ORIGINAL_UPLOAD_FOLDER, progress_callback, message_callback)
        processing_status['status'] = "Finished"

    threading.Thread(target=run_preprocessing).start()
    return render_template('preprocessing.html')

@app.route('/progress')
def progress():
    return jsonify(processing_status)

@app.route('/logs')
def logs():
    return jsonify({'logs': log_messages})

@app.route('/upload-image')
def upload_image():
    return render_template('upload.html')

@app.route('/forgery-check', methods=['POST'])
def forgery_check():
    # Clear the test upload folder before saving new test image
    cleanup_test_images()

    if 'file' not in request.files:
        return "No file uploaded.", 400

    file = request.files['file']
    if file.filename == '':
        return "No selected file.", 400

    filepath = os.path.join(TEST_UPLOAD_FOLDER, file.filename)
    file.save(filepath)

    result = forgery.forgery(filepath, ORIGINAL_UPLOAD_FOLDER)

    sample_images = get_sample_images(ORIGINAL_UPLOAD_FOLDER)
    uploaded_image = cv2.imread(filepath)
    _, uploaded_buffer = cv2.imencode('.jpg', uploaded_image)

    return render_template('result.html',
                           images=sample_images,
                           uploaded_image=uploaded_buffer.tobytes(),
                           result=result)

def full_cleanup():
    """Delete the entire uploads folder and scores.json."""
    if os.path.exists(SCORES_FILE):
        os.remove(SCORES_FILE)
    if os.path.exists(BASE_UPLOAD_FOLDER):
        shutil.rmtree(BASE_UPLOAD_FOLDER)
    os.makedirs(ORIGINAL_UPLOAD_FOLDER, exist_ok=True)
    os.makedirs(TEST_UPLOAD_FOLDER, exist_ok=True)

def cleanup_test_images():
    """Delete only the test images folder."""
    if os.path.exists(TEST_UPLOAD_FOLDER):
        shutil.rmtree(TEST_UPLOAD_FOLDER)
        os.makedirs(TEST_UPLOAD_FOLDER, exist_ok=True)

def get_sample_images(path, limit=5):
    images = []
    for filename in os.listdir(path)[:limit]:
        img_path = os.path.join(path, filename)
        if os.path.isfile(img_path):
            img = cv2.imread(img_path)
            _, buffer = cv2.imencode('.jpg', img)
            images.append(buffer.tobytes())
    return images

if __name__ == '__main__':
    app.run(debug=True)
