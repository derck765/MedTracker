# Milestone 2 - MedTrack

GIF: https://discord.com/channels/1484038486746206288/1484038487945646182/1488358109826121840

User Stories Completed:

✅ Setup Android Project and Configured Dependencies

✅ Designed XML Layouts for all App Screens

✅ Implemented Bottom Tab Navigation Between Main Screens

✅ Research: Drug Interaction and Pharmacy APIs

✅ Research: Firestore Database Structure for MedTrack

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

### Wireframes

<img width="395" height="534" alt="Screenshot 2026-03-23 221523" src="https://github.com/user-attachments/assets/7e83f9c7-b422-45ba-8f59-91dd2ea6e84d" />

<img width="388" height="538" alt="Screenshot 2026-03-23 221508" src="https://github.com/user-attachments/assets/912c0ec5-1968-4fdf-9e6a-6235eed4e1ff" />

### Digital Wireframes

<img width="200" alt="Login_Signup" src="https://github.com/user-attachments/assets/933eef8d-4411-402a-b0ff-abc9a0aec684" />
<img width="200" alt="Dashboard" src="https://github.com/user-attachments/assets/8cac2020-ccc8-4ac2-864e-b8c514e6c71b" />
<img width="200" alt="Medications" src="https://github.com/user-attachments/assets/8c6aaafb-093b-48fa-b3a8-23f54fbd1f73" />
<img width="200" alt="Symptoms" src="https://github.com/user-attachments/assets/de622019-d6c3-4111-a6a5-2109f8c3f6a6" />
<img width="200" height="446" alt="Screenshot 2026-03-23 220846" src="https://github.com/user-attachments/assets/4b109db7-0278-4fb5-9744-8e7e6eaef615" />
<img width="820" height="252" alt="Screenshot 2026-03-23 221017" src="https://github.com/user-attachments/assets/4c2e21bf-b6ab-4e98-bc16-6db598615471" />
<img width="820" height="867" alt="add_meds" src="https://github.com/user-attachments/assets/0d3980f7-fbd0-451e-b938-d63e4e61852d" />

