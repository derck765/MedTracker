# Milestone 1 - MedTrack (Unit 7)

## Table of Contents

1. [Overview](#Overview)
1. [Product Spec](#Product-Spec)
1. [Wireframes](#Wireframes)

## Overview

### Description

MedTrack is a smart medication management assistant designed to ensure patient safety and adherence to medical prescriptions. It allows users to digitize their pill schedules, receive high-priority alerts for doses, and log symptoms to track their health over time. By integrating a pharmacy API, the app provides a critical layer of safety by automatically flagging dangerous drug-to-drug interactions, making it an essential tool for chronic patients, the elderly, and their caregivers

### App Evaluation

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

## Product Spec

### 1. User Features (Required and Optional)
**Required Features**
* **User Authentication:** Sign-up and Login functionality (Email/Password or Google Auth) to secure private health data.
* * **Medication Logging:** User can enter drug name, dosage, and frequency.
* **Scheduling & Notifications:** Automated push alerts triggered at specific times to remind the user to take their medicine.
* **Dose Confirmation:** A simple "Mark as Taken" button within the notification or app to log adherence.
* **Symptom Journal:** A daily log where users can record how they feel (e.g., "Dizzy," "Nauseous," "Energetic").
 

**Optional Features**
* **OCR Scanner:** Use the camera to scan a prescription bottle and auto-fill the drug name/info.
* **Caregiver Sync:** Ability to email or share a weekly adherence report with a doctor or family member.
* **Refill Reminders:** Notifies the user when their pill count is low based on the doses taken.
* **Drug Interaction Check:** Automated warning if a newly added drug conflicts with an existing one in the list via API.

### 2. Screen Archetypes

* **Log In/Sign Up:** User authentication screens to access the profile.
* **Dashboard/Home:** Displays the "Next Dose" countdown and a daily checklist of medications.
* **Medication Library:** A list of all current prescriptions where users can add, edit, or delete entries.
* **Add Medication:** A form-based screen to input drug details and set the schedule.
* **Symptom Log:** A calendar or list view showing daily symptom entries and trends over time.
* **Interaction Alerts:** A pop-up or dedicated warning screen triggered when a medical conflict is detected.

### 3. Navigation

**Tab Navigation (Tab to Screen)**
* **Home:** View daily schedule and dose confirmation.
* **My Meds:** View/edit the full library of current prescriptions.
* **Health Log:** View symptom tracking and history.
* **Settings:** Manage user profile and notification preferences.

**Flow Navigation (Screen to Screen)**

* **Launch App** $\rightarrow$ If not logged in $\rightarrow$ **Login/Sign Up Screen**
* **Login/Sign Up** $\rightarrow$ Successfully Authenticated $\rightarrow$ **Home Screen**
* **Home Screen** $\rightarrow$ Click "Add" $\rightarrow$ **Add Medication Screen**
* **My Meds Screen** $\rightarrow$ Click specific drug $\rightarrow$ **Medication Details/Edit Screen**
* **Add Medication Screen** $\rightarrow$ Submit $\rightarrow$ **Interaction Alert Screen** (if a conflict is found)
* **Health Log Screen** $\rightarrow$ Click "New Entry" $\rightarrow$ **Symptom Entry Form**

## Wireframes

##Digital Wireframe

<img src="https://image2url.com/r2/default/images/1774317372639-8743e853-6fac-404b-9680-9c706146ffe9.png" width=600>
 
<img src="https://image2url.com/r2/default/images/1774317372639-8743e853-6fac-404b-9680-9c706146ffe9.png)](https://image2url.com/r2/default/images/1774317402131-10995fa5-c08b-4584-b0db-1babdddedede.png" width=600>
  
<img src="https://image2url.com/r2/default/images/1774317478262-e4de1344-bb11-4e50-8f5f-6a321fba33ef.png" width=600>
  
<img src="https://image2url.com/r2/default/images/1774317509456-9ee19216-06e2-476e-9dba-bb327fa82984.png" width=600>

<img src=" https://image2url.com/r2/default/images/1774317602155-03fe83e6-3d5f-4d7a-8606-10f6fd7f6716.png" width=600>




  



<br>
