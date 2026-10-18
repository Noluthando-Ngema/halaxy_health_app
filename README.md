# Halaxy Health App

Halaxy Health is an Android mobile application designed to connect patients with healthcare professionals in a simple and secure way. 
The app was developed using Kotlin and Android Studio and serves as a complete appointment booking platform. It supports three main user roles which are patients, doctors, and an admin. 
The patient side allows users to create an account, log in securely, and access a personalized dashboard that greets them by name and provides quick access to key features. 
Patients can make a booking by finding doctors in real time, view their existing appointments under My Appointments, explore health education content through the Learning Centre, and manage their personal information in the Edit Profile or Settings screen.
The settings screen also includes the ability to update name, surname, email and password, select a preferred payment method, switch between light and dark mode, and log out of the application.

The doctor side of the application has its own dedicated login and signup flow. When a doctor registers, their account is not immediately active. Instead, the account is created with a pending status and requires admin approval.
This was implemented to ensure that only verified doctors can appear on the platform. The statuses used in the system are pending, approved, and declined. 
Once approved, a doctor can log in and access the doctor dashboard to manage appointments. The admin side is responsible for reviewing all new doctor registrations and approving or declining them accordingly, which helps maintain the integrity of the platform.

From a technical perspective, the app uses XML for its user interface and follows an activity-based architecture. Data persistence is handled locally using Room Database, which is the main database class called AppDatabase. 
The database includes data access objects such as UserDao, DoctorDao, and AppointmentDao, and entities such as User, Doctor, and Appointment. All database operations are performed using Kotlin Coroutines with lifecycleScope to keep the UI responsive. 
For security, user passwords are never stored in plain text. They are hashed using the PasswordHasher class before being saved. User session and theme preferences are stored using SharedPreferences. 
The app uses two preference files, one called halaxy_session for storing the logged in email and another called theme_prefs for storing the dark mode preference and payment method selection.

A key feature of the app is its theme system, which is implemented in ThemeHelper. The helper reads the dark mode boolean from SharedPreferences and applies the appropriate night mode using AppCompatDelegate. 
It also recursively changes the background colors of the root layout, cards, and input fields, and adjusts text colors so that the interface remains readable in both light and dark modes, while preserving the maroon branding of the buttons. 

To set up and run the project, the repository should be cloned and opened in Android Studio Ladybug or a newer version. After syncing Gradle, the app can be run on an emulator or physical device with a minimum SDK of 24. No external API keys or services are required as the app currently runs entirely on a local Room database.

During development, several common crashes were identified and fixed. One was a NullPointerException on setOnClickListener which occurred when the ID referenced in Kotlin did not exist in the XML layout, such as txtGoSignup. This was fixed by ensuring the XML IDs match the code or by using a safe call with a question mark. Another was an InflateException stating that a layout_width attribute must be supplied, which happened because some TextViews in activity_settings were missing required width and height attributes. A third was a crash caused by using the double exclamation operator on getUserByEmail when the email was empty. This was resolved by replacing it with a safe null check.

Future improvements planned for Halaxy Health include migrating from the local Room database to Firebase Authentication and Firestore for cloud synchronization, adding a doctor availability calendar, integrating a payment gateway, adding push notifications for appointment reminders, and implementing search and filtering of doctors by specialty. The project was built for educational purposes as part of mobile development coursework under the package name com.example.halaxy_health.
