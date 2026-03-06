import requests
import random
import base64
import os
import time
import json
from flask import Flask, request, jsonify

app = Flask(__name__)

# Folder setup
UPLOAD_FOLDER = os.path.join('static', 'uploads')
os.makedirs(UPLOAD_FOLDER, exist_ok=True)

# List to act as a proper mailbox
message_db = []

CAT_API_URL = "https://api.thecatapi.com/v1/images/search?limit=1"
WORDS = ["OIIA", "CHEX", "OULU", "HUNGRY", "SUCCESS", "ERROR", "ANDROID", "MEOWL", "FINLAND", "SPINNING"]

def log_divider():
    print("\n" + "="*60)

# --- ROUTE: APP SENDING MESSAGE ---
@app.route('/api/send', methods=['POST'])
def receive_from_app():
    log_divider()
    print(">>> [INCOMING POST] Message received from App")
    
    try:
        data = request.json
        sender = data.get("sender", "Unknown")
        receiver = data.get("receiver", "Unknown")
        meme_data = data.get("meme", {})
        
        print(f"FROM: {sender} | TO: {receiver}")
        
        image_content = meme_data.get("imagedirectory", "")

        # Handle Base64 decoding if it's a local image
        if image_content and not image_content.startswith("http"):
            print(f"DEBUG: Found Base64 image data ({len(image_content)} chars). Decoding...")
            img_bytes = base64.b64decode(image_content)
            
            filename = f"meme_{int(time.time())}.jpg"
            filepath = os.path.join(UPLOAD_FOLDER, filename)
            
            with open(filepath, "wb") as f:
                f.write(img_bytes)

            public_url = f"{request.host_url}static/uploads/{filename}"
            data["meme"]["imagedirectory"] = public_url
            print(f"DEBUG: Image saved to disk as: {filename}")
            print(f"DEBUG: Data updated with public URL: {public_url}")
        else:
            print(f"DEBUG: Using standard image URL: {image_content[:50]}...")

        # Add to the list (The Mailbox)
        message_db.append(data)
        print(f"STATUS: Message added to queue. Total pending messages: {len(message_db)}")
        log_divider()
        
        return jsonify({"status": "success"}), 200

    except Exception as e:
        print(f"!!! ERROR: {str(e)}")
        log_divider()
        return jsonify({"status": "error", "message": str(e)}), 500

# --- ROUTE: APP FETCHING MESSAGES ---
@app.route('/get-meme', methods=['GET'])
def get_meme():
    log_divider()
    target_user = request.args.get('user', 'Guest')
    print(f"<<< [FETCH REQUEST] User '{target_user}' is checking their mailbox...")

    # Filter messages for this user or "Everyone"
    my_messages = [m for m in message_db if m.get('receiver') == target_user or m.get('receiver') == 'Everyone']
    
    if my_messages:
        print(f"MATCH FOUND: Delivering {len(my_messages)} messages to {target_user}")
        for msg in my_messages:
            print(f" - Message from: {msg.get('sender')}")
            message_db.remove(msg) # Clear from server after delivery
        
        log_divider()
        return jsonify(my_messages), 200
    else:
        print(f"EMPTY: No new messages for {target_user}")
        log_divider()
        return jsonify({"error": "No messages"}), 403

# --- ROUTE: BROWSER DEBUG SENDER ---
@app.route('/send-meme', methods=['GET'])
def trigger_meme():
    log_divider()
    print("--- [WEB TRIGGER] Manual cat meme generated via browser ---")
    try:
        response = requests.get(CAT_API_URL)
        cat_url = response.json()[0]['url']
        top = random.choice(WORDS)

        package = {
            "sender": "System_Cat",
            "receiver": "Everyone",
            "meme": {
                "toptext": top,
                "bottomtext": "BROWSER TRIGGER",
                "imagedirectory": cat_url, 
                "audioname": "oiia",       
                "subtitles": ["Meow!", "You triggered a debug message."]
            }
        }
        message_db.append(package)
        print(f"DEBUG: System message queued for Everyone. Image: {cat_url}")
        log_divider()
        return f"<h1>Debug Meme Queued!</h1><p>Target: Everyone</p><img src='{cat_url}' width='300'>"
    except Exception as e:
        print(f"!!! ERROR: {str(e)}")
        return str(e), 500

if __name__ == '__main__':
    print("\n" + "*"*60)
    print("CATCONNECT SERVER STARTING...")
    print(f"Upload directory: {os.path.abspath(UPLOAD_FOLDER)}")
    print("*"*60 + "\n")
    app.run(host='0.0.0.0', port=5000, debug=True)