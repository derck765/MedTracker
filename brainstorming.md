# App Idea Brainstorming
## App Ideas 
1. **Virtual Library App** - Organizes books from various sources (ebooks, audiobooks, physical book copies) and makes it easy to keep track of books currently owned, return due dates, and a wishlist. Can also share ebooks with friends.
2. **Studying Timer App** - Makes it easy to time and plan study sessions to keep the user focused on their work. Uses the accelerometer to check if the phone was picked up during a study session and will otherwise reset the study timer. Can track overall data trends so the user knows their study habits (i.e. when they study most effectively without distraction)
3. **Parking Helper App** - Allows the user to save their GPS location so that they can remember where they parked their car. There will be a custom notes section for the user to add information such as what level or section they parked in as well. The Google Maps API will redirect the user back to the marked location in the fastest walking route.
4. **SafeWalk** - An AI-powered navigation app for women that suggests the safest route to a destination based on crime data, time of day, traffic, and street lighting.
5. **MedTrack** - An app for people managing multiple medications. Tracks doses, sends reminders, logs symptoms, and flags dangerous drug interactions using a pharmacy API.
6. **RentSmart** - An app for first-time renters that allows users to scan or upload a lease, and AI will flag sketchy or unfair clauses, explain confusing legal terms, and scores the overall lease. Can also provide negotiation scrips, landlord reputation scores, compares rent prices in the area, and provides a move-in checklist.
7. **GrubVenture** - A restaurant discovery app for indecisive eaters. Users set filters like cuisine, price, and distance and then spin a wheel that randomly picks a restaurant nearby. Has a group mode where everyone votes on filters and the wheel picks something everyone agreed on.
8. **ReadingCircle** - A social book club app for friend groups and strangers with shared taste. Users create or join a reading circle, vote on the next book as a group, and follow a shared reading schedule with chapter milestones. Has a threaded discussion board per chapter so people can post reactions as they read without spoiling others who are behind.
9. **GainTrack** - A fitness companion app where users log every workout with sets, reps, and weight, search a food database to log meals and track daily macros, and set personalized goals for calories, protein, carbs, and fat based on their body stats and target. Tracks progressive overload on every lift and adjusts nutrition targets weekly based on real weight trends so your plan always reflects your actual progress.
10. **LensBin** - Designed for anyone with a cluttered garage or moving boxes, this app creates a digital map of physical storage. Users take a photo of the items inside a box before sealing it and assign that box a number or a QR code. The app saves the image and a brief list of contents to the phone’s Internal Storage; when the user needs to find their "winter boots" later, they simply search the app to see exactly which box number contains them and what the contents look like. This project is perfect for practicing Camera Intents and image URI management.
11. **QuietZone** - This is a location-based tool that automatically manages phone volume based on the user's surroundings. Using the Google Maps Geofencing API, users can mark "Quiet Zones" like their library, office, or classroom. When the app detects the phone has entered those GPS coordinates, it triggers a system command to switch the ringer to "Do Not Disturb" or "Vibrate." This is an excellent beginner project for understanding Background Services and location permissions in Android.


## Idea Evaluations 
**Virtual Library App**

**Category**: Entertainment/Education 
* Books can be saved in a personal digital library
    * Audio book files or pdf files if available can be linked to the entry and viewed in the app.
* Online books can be shared with friends like a library loan system.
* Gives AI-powered suggestions with Gemini API to suggest new books to add to your library/wishlist. 
* Can find nearby libraries with your location data to possibly find hard copies of books in your wishlist
- Will send notification reminders for library book due dates for the ones that are physical copies.

    ### Evaluation
    **Mobile:** This app is better as a phone app than as a website because it is much more convenient to use than using a web browser. The location data will be used on the phone to let the user know if there are nearby libraries with the book the user is looking for. Also, notifications will be used to let users know when books are shared with them or to remind of due dates for hard-copy book returns.
        
    **Story:** The value of the app to the audience lies in the fact that it provides one centralized place for the user to keep track of their reading, including both audiobooks that they have/want to read/want to buy as well as remembering deadlines for physical books that they have checked out. The audience of people that would use this app would be people who enjoy reading and want an organized way of keeping track of the books they want to read/are reading and have a way to share it with others. 

    **Market:** The size and scale of the user base for this app would be rather niche for a smaller group of people who need a way to keep track digitally of all of their books through different media.
        
    **Habit:** The app isn't meant to be habit forming or addictive, it's just a way for users to access, organize, and find news books to enjoy. The average user probably would interact with it a few times a week to read in their free time.
        
    **Scope:** The app itself is not extremely challenging, except for possibly integrating the Gemini API for AI suggestions and having to provide context about the user and their current profile. The app itself is intended at its core to be a book organization app for users to keep track of their e-books, audiobooks, and physical library books.

**MedTrack**

**Category**: Health & Fitness / Medical Utility

**Features**:

* Allows users to create a digital schedule for multiple medications, including dosage amounts and specific times of day.

* Sends high-priority push notifications to remind users to take their pills, with a "log" feature to confirm the dose was successfully taken.

* Includes a symptom checker/logger where users can note how they feel daily to track side effects or recovery progress over time.

* Integrates a pharmacy API to automatically flag potentially dangerous drug-to-drug interactions based on the user's active medication list.

### Evaluation

**Mobile**: This app is essential as a mobile application because medication management is a time-sensitive task that occurs throughout the day, often when the user is away from a computer. The mobile platform allows for immediate haptic and audio alerts that are much harder to ignore than an email or a website notification. Additionally, the phone’s camera can eventually be used for OCR (Optical Character Recognition) to scan prescription labels directly into the database, making the setup process much faster for elderly users or those with many prescriptions.

**Story**: The value of the app lies in providing safety and peace of mind for people managing complex health routines. It acts as a digital fail-safe to prevent missed doses or accidental double-dosing, which can be life-threatening. The target audience includes the elderly, individuals with chronic illnesses, and caregivers who need a reliable way to monitor their loved ones' health adherence from a distance.

**Market**: The market for this app is massive and consistently growing due to an aging global population and the increasing prevalence of chronic health conditions. While there are many "reminder" apps, a specialized tool that focuses specifically on clinical safety and drug interaction data appeals to a wide demographic of health-conscious consumers and medical professionals.

**Habit**: This app is designed to be highly habitual, with users interacting with it multiple times every single day. The habit is "forced" by the user's medical necessity; however, the app reinforces this by providing a sense of accomplishment through a "streaks" or "adherence score" system that shows the user they are successfully managing their health.

**Scope**: The technical scope is moderate but requires high attention to detail regarding data accuracy. The core functionality—a local SQLite database for meds and a notification manager—is very straightforward in Android Studio. The primary challenge is the integration of a reliable medical API for the drug interaction feature and ensuring the app remains functional even when the device is in "Do Not Disturb" or "Power Saving" modes.

**GainTrack**


**Category**: Health & Fitness / Productivity

**Features**:

* Users log workout data including specific exercises, sets, repetitions, and weight lifted to monitor strength gains.

* Includes a comprehensive food database search that allows users to log daily meals and automatically calculate their intake of calories and macronutrients (protein, carbs, fats).

* Automatically calculates personalized fitness goals based on the user's body statistics (height, weight, age) and specific targets like muscle gain or fat loss.

* Features a "Progressive Overload" tracker that analyzes lifting history and a "Smart Nutrition" engine that adjusts weekly calorie targets based on actual scale weight trends.

### Evaluation

**Mobile**: This app is significantly more effective as a mobile application than a website because users need to log data in real-time while at the gym or in the kitchen. The mobile platform allows for quick entry of sets immediately after finishing an exercise and the ability to scan barcodes of food items using the phone's camera for instant nutritional logging. Being able to access a workout plan on a portable device while moving between gym equipment is a primary requirement for the target audience.

**Story**: The value of the app lies in removing the guesswork from body transformations by linking training performance with nutritional data. It solves the common problem of "plateauing" by showing users exactly when they need to increase their lifting weight or adjust their calories to keep seeing results. The target audience includes dedicated gym-goers, athletes, and bodybuilders who want a data-driven approach to their fitness rather than relying on intuition or manual paper logs.

**Market**: The market for fitness tracking is massive and highly competitive, but there is always a demand for apps that integrate both "the kitchen and the gym" into one seamless interface. While many apps do one or the other, a tool that specifically automates the relationship between lifting progress and caloric adjustment appeals to a more serious "power user" segment of the fitness community.

**Habit**: This app is designed to be extremely habit-forming, requiring multiple interactions per day, once for every meal and once for every workout session. The user is incentivized to return to the app to see their progress charts move upward and to ensure they stay within their daily "macro" budget, creating a strong loop of accountability and achievement.

**Scope**: The technical scope of this app is the most challenging of the set, as it requires managing two complex systems: a workout logger and a nutrition tracker. In Android Studio, this would involve creating a sophisticated Room database with multiple related tables (Exercises, Workouts, Meals, Ingredients) and implementing logic for weekly weight-averaging algorithms. Integrating a third-party food database API is also a significant task, but the core functionality of logging and graphing progress is a great way to master data visualization in Android.


### Final App Chosen - MedTrack
