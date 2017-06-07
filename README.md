![alt text](https://travis-ci.org/cpe305Spring17/spring2017-project-AndyRewLy.svg?branch=master)

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

# Github Pages

https://cpe305spring17.github.io/spring2017-project-AndyRewLy/

# Screen Captures
<p><img src="https://github.com/cpe305Spring17/spring2017-project-AndyRewLy/blob/master/websiteImages/Screenshot_20170604-135549.png?raw=true" width="290">
    <img src="https://github.com/cpe305Spring17/spring2017-project-AndyRewLy/blob/master/websiteImages/Screenshot_20170604-140034.png?raw=true" width="290">
    <img src="https://github.com/cpe305Spring17/spring2017-project-AndyRewLy/blob/master/websiteImages/Screenshot_20170604-140132.png?raw=true" width="290"></p>
<p><img src="https://github.com/cpe305Spring17/spring2017-project-AndyRewLy/blob/master/websiteImages/Screenshot_20170604-140139.png?raw=true" width="290">
    <img src="https://github.com/cpe305Spring17/spring2017-project-AndyRewLy/blob/master/websiteImages/Screenshot_20170604-140153.png?raw=true" width="290"></p>
    
# Class Diagrams

<img src="https://github.com/cpe305Spring17/spring2017-project-AndyRewLy/blob/master/websiteImages/Factory%20Design%20Pattern.png?raw=true">
<img src="https://github.com/cpe305Spring17/spring2017-project-AndyRewLy/blob/master/websiteImages/Singleton%20Design%20Pattern.png?raw=true">

# Design Patterns
The design patterns I used include the Factory Design Pattern and the Singleton Design Pattern.
The factory design pattern is used to handle UI components with the fragments to ensure that I'm able to create specific fragments based off of criteria.
The singleton design pattern is used to make sure that I do not have more than one instance of either the database connector or the Today class.
