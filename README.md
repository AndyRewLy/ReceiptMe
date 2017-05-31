# ReceiptMe

ReceiptMe is an Android Application that allows users to take photos of receipts/upload receipts and store receipt data.  Users can see their spendings over time and also budget themselves.

# OCR

This application utilizes Google's OCR API to parse receipts and read information from them.  OCR usage references an example project createed from the Google tutorial on OCR.  OCR can be see within the controller.camera package.  

OCR is also directly used within the model.ocr where TextBlocks are read and stored.

# Model Package

The model package contains a purchased item which represents a single receipt entry for a user's receipt.

Within the DAO package all references/connections to the database are created in a singleton class.  The DAO used to create purchased items can be found within this package.

# View Package

The view package contains all Android activities and deals with maintenance of the UI.  The Factory Pattern is used within the fragment package to create different fragments based off of user input and the current screen.
