from flask import Flask, jsonify, request

import ast
import sys
import pandas as pd
import pickle

# Create a Flask application
app = Flask(__name__)

# Define a route for the GET request
trained_model = 'disease_prediction_model.pkl'
with open(trained_model, 'rb') as file:
    model = pickle.load(file)


@app.route('/api/disease/predict', methods=['GET'])
def get_books():
    data = request.get_json()
    symptoms = data['symptoms']
    print(symptoms)
    req = pd.read_csv('request.csv')
    req.loc[:, :] = 0
    for item in symptoms:
        req[item] = 1.0
    disease_pred = model.predict(req)
    print(disease_pred[0])
    return disease_pred[0]


# Run the Flask application with custom IP and port
if __name__ == '__main__':
    app.run(host='0.0.0.0', port=9495, debug=True)
