# InstaHomeCare Technician Manager

**Small Jobs. Instant Solutions.**  
*Internal Technician Management Application for InstaHomeCare (Salem, Tamil Nadu)*

---

## 🛠️ Purpose & Architecture

InstaHomeCare Technician Manager is an internal Android application built exclusively for InstaHomeCare administration to document, interview, verify, search, and manage skilled technicians (Electricians, Plumbers, Housekeeping, and specialized trades) across Salem city and surrounding zones.

Technicians **do not self-register**; all records are added directly after in-person meetings and phone assessments. The app operates completely **offline** without requiring subscriptions, login, or cloud APIs.

### Key Technical Specs:
- **Language**: Kotlin 2.2+
- **UI Framework**: Jetpack Compose (Material Design 3)
- **Local Database**: Room Database 2.7 (with reactive Kotlin `Flow` and KSP)
- **Architecture**: MVVM (Model-View-ViewModel with Single Source of Truth)
- **Export Formats**: 
  - Microsoft Excel CSV (UTF-8 with BOM `\uFEFF` and Excel formula text format `="<phone>"` to preserve leading zeros and Tamil characters)
  - Full JSON Backup & Restore with pre-import validation and duplicate-prevention merging
- **Dialer Integration**: System Phone Dialer (`Intent.ACTION_DIAL`) – never calls automatically

---

## 📋 Technician Fields (21 Tracked Parameters)

1. **Technician ID**: Auto-generated sequence (e.g. `IHC-0001`, `IHC-0002`...)
2. **Full Name**: (Required)
3. **Mobile Number**: (Required, preserves leading zeros and country codes; live duplicate warning)
4. **Alternate Mobile Number**: (Optional)
5. **Hometown**: (e.g. Attur, Omalur, Namakkal, etc.)
6. **Current Town**: Defaults to Salem
7. **Address / Locality**: Specific street or landmark
8. **Trade**: Electrician, Plumber, Housekeeping, Other (with custom specification)
9. **Additional Skills**: Multi-skill notes (e.g., Inverter wiring, RO service, Tile drilling)
10. **Professional Experience**: Years in trade
11. **Current Occupation**: Employment status (freelance, shop, daily wage)
12. **Own Vehicle**: Yes / No / Not specified
13. **Tools Available**: Yes / Some / No / Not specified
14. **Interested in Joining InstaHomeCare**: Yes / No / Undecided
15. **Preferred Working Hours**:
    - Available whenever called, subject to confirmation
    - Morning only
    - Evening only
    - Full-time
    - Weekends only
    - Call in advance to confirm
    - Other
16. **Preferred Service Area**:
    - Entire Salem city
    - Selected zones only
17. **Preferred Zones / Localities**: Multi-select chip tags (Hasthampatti, Ammapet, Suramangalam, Kondalampatti, Shevapet, Fairlands, Alagapuram, Salem Junction, Gugai, Attur, etc.) + custom zone input
18. **Date Met**: Interview / meeting date
19. **Follow-up / Verification Status**: Verified, Pending verification, Follow-up required, On hold, Rejected
20. **Additional Notes & Observations**: Meeting takeaways
21. **Timestamps**: Automatic creation and update timestamps

*Note: In compliance with company privacy policy, Aadhaar and bank details are strictly not collected.*

---

## 🚀 Building & Running the Project

### Prerequisites
- Android Studio Hedgehog (2023.1.1) or newer / Ladybug
- JDK 17 or JDK 21
- Android SDK 34+

### Steps:
1. Open Android Studio and select **Open** -> Select the project root folder.
2. Wait for Gradle to sync dependencies automatically.
3. Connect an Android device with USB debugging enabled, or start an Android Virtual Device (AVD).
4. Run the app:
   - Click **Run 'app'** (`Shift + F10`)
   - Or run from terminal:
     ```bash
     gradle assembleDebug
     ```
5. To run all unit and Robolectric tests:
   ```bash
   gradle :app:testDebugUnitTest
   ```

### Generated APK Location
After running `gradle assembleDebug`, the debug APK is generated at:
`app/build/outputs/apk/debug/app-debug.apk`

---

## 💾 CSV & Backup Specifications

### CSV / Excel Export
- Written using Android Storage Access Framework (SAF) so you can save directly to Downloads, Google Drive, or SD card.
- Encoded with **UTF-8 with BOM** (`0xEF, 0xBB, 0xBF`) to ensure Tamil and Unicode names display cleanly when opened in Microsoft Excel on Windows or Mac.
- Phone numbers are formatted as text formulas (`="0994535111"`) to prevent Excel from dropping leading zeros or displaying scientific notation.

### JSON Backup & Restore
- Exports complete database schema into structured JSON with metadata (app tag, record count, timestamps).
- Validates any imported file before applying changes.
- Provides choice between:
  - **Merge**: Adds new records while skipping duplicate IDs or phone numbers.
  - **Replace All**: Clears existing database and imports backup after user confirmation.
