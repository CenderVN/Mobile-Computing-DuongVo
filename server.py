import requests
import random
from flask import Flask, jsonify

app = Flask(__name__)

# This acts as our 'Message Queue'
# None = No message waiting
# Data = Message ready to be delivered
pending_meme = None

# Configuration
CAT_API_URL = "https://api.thecatapi.com/v1/images/search?limit=1"
# Add your own custom words here
WORDS = ["OIIA", "CHEX", "OULU", "HUNGRY", "SUCCESS", "ERROR", "ANDROID", "MEOWL", "FINLAND", "SPINNING"]

# --- ROUTE 1: THE CONTROL PANEL ---
# Visit http://localhost:5000/send-meme in your browser to "send" a message to your phone
@app.route('/send-meme', methods=['GET'])
def trigger_meme():
    global pending_meme
    try:
        # 1. Fetch a dynamic image/gif link from The Cat API
        response = requests.get(CAT_API_URL)
        response.raise_for_status()
        cat_url = response.json()[0]['url']

        # 2. Generate random meme text
        top = random.choice(WORDS)
        bottom = random.choice(WORDS)

        # 3. Load the "Message Queue" with the template your Java app expects
        pending_meme = {
            "toptext": top,
            "bottomtext": bottom,
            "imagedirectory": cat_url, # URL link for Glide to load
            "audioname": "oiia",       # Matches your local raw/oiia.mp3
            "subtitles": [
                f"INCOMING MESSAGE: {top}!",
                "oia oia iiaioia",
                "oia oia oe",
                f"End of transmission: {bottom}"
            ]
        }
        print(f"DEBUG: New meme loaded into queue: {top} - {bottom}")
        return f"""
        <html>
            <body style="font-family: sans-serif; text-align: center; padding-top: 50px;">
                <h1 style="color: green;">Meme Loaded Successfully!</h1>
                <p><b>Top:</b> {top} | <b>Bottom:</b> {bottom}</p>
                <img src="{cat_url}" style="max-width: 300px; border-radius: 10px; margin: 10px 0;">
                <p>Your Android app will receive this on its next background check.</p>
                <p><i>Note: After the app reads this once, it will be deleted from the server.</i></p>
            </body>
        </html>
        """
    except Exception as e:
        return f"<h1 style='color: red;'>Error fetching from API</h1><p>{str(e)}</p>", 500

# --- ROUTE 2: THE APP ENDPOINT ---
# Your Android WorkManager will ping http://[YOUR_IP]:5000/get-meme
@app.route('/get-meme', methods=['GET'])
def get_meme():
    global pending_meme
    
    if pending_meme is not None:
        # FIRST ACCESS: Deliver the payload and immediately clear the queue
        payload = pending_meme
        pending_meme = None 
        print("DEBUG: Meme delivered to device. Queue is now empty.")
        return jsonify(payload), 200
    else:
        # SUBSEQUENT ACCESS: Return an error until a new meme is loaded
        # This simulates a "one-time" message delivery system
        print("DEBUG: Access Denied. No meme waiting or already consumed.")
        return jsonify({
            "error": "Access Denied", 
            "message": "No new memes. Use /send-meme to load one."
        }), 403

if __name__ == '__main__':
    # '0.0.0.0' makes the server accessible to your phone on the same Wi-Fi
    app.run(host='0.0.0.0', port=5000, debug=True)