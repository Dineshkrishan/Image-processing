from flask import Flask, request, jsonify
from skimage.metrics import structural_similarity as ssim
import cv2
import numpy as np

app = Flask(__name__)

@app.route('/compare', methods=['POST'])
def compare_images():
    img1 = cv2.imdecode(np.frombuffer(request.files['img1'].read(), np.uint8), cv2.IMREAD_COLOR)
    img2 = cv2.imdecode(np.frombuffer(request.files['img2'].read(), np.uint8), cv2.IMREAD_COLOR)

    gray1 = cv2.cvtColor(img1, cv2.COLOR_BGR2GRAY)
    gray2 = cv2.cvtColor(img2, cv2.COLOR_BGR2GRAY)

    score, _ = ssim(gray1, gray2, full=True)
    return jsonify({"similarity": round(score, 4)})

if __name__ == '__main__':
    app.run(debug=True)
