# Driver Onboarding Module
<hr/>

### Features :

- Sign Up as a driver
- Onboarding driver through various steps:
  - document collection
  - background verification
  - shipment of tracking device
- Allow driver to mark itself available to drive

### Workflow :

- User logs in to the client app, and click on the option, **Join as a driver**.
- on click, client calls the service, if the user's driver profile is created, service replies with profile snapshot, else server replies with No content HTTP status.
- if driver profile is not found, user is greeted with form to enter basic info details for driver profile.
- once user submits the details, user's driver profile is created with status **created**.
- For user profile with created status, user is greeted with page requesting to upload mandatory documents for background verification.
- Once user uploads all necessary documents, user profile moves to pending_verification status.
- For all user profiles with pending_verification status, documents are verified from backend, & objection is raised against each document if necessary.
- on background verification objection, profile status moves back to created status, & is also displayed objection comments, to suggest .
- if background verification succeeds, a shipment is created with onboarding kit (tracking device).
- order for the tracking device is handled by 3p service, a contract is defined with the 3p service to update onboarding service with order status.
- once tracking device is delivered to the user, profile status is moved to complete status.
- once user is completely onboarded as a driver , user can mark itself as available to drive.