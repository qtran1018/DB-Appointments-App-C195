NAME: DBClientApp

PURPOSE: to interface with a database and provide a GUI for users to read and write data to and from it. This program allows the viewing, editing, adding, and deletion of customer records and appointments.
AUTHOR: Quang Tran

	EMAIL: qtran16@wgu.edu
 
VERSION: 2

    DATE: 09 JUNE 2023
    
DEV TOOLS: IntelliJ IDEA 2023.1.1 (Ultimate Edition) Build #IU-231.8770.65

			Java JDK 20 x64
   
			JavaFX-SDK-20.0.1
   
DB Connector: MySQL-connector-j-8.0.33

INSTRUCTIONS:

	- Start the program.
	- Login with valid credentials
	- Navigate using the buttons on the left to open the associated page (e.g Home, Customers, Appointments)
	- View table data on each page, using the Refresh button on the top-right to refresh table data after any changes to the data.
	    - In Appointments, use the radio buttons on the top of the table to select the desired view (All appointments, monthly set by a Month and Year, or the Week's appointments)
	- Use the bottom buttons to perform the associated tasks (Add, Modify, or Delete records)
	    - To modify records, click and highlight the desired record, then click the modify button. Fill in all allowed fields with the desired data, then click Save.

Login Screen

![Login screen](https://github.com/user-attachments/assets/a9ae6bdf-9bef-41fe-9adc-1b9f1852a0cd)

See upcoming appointments when logging in

![Login screen upcoming appointments](https://github.com/user-attachments/assets/73ec8bb7-0547-4498-acea-875e9b479797)

Home screen

![Home screen with Contacts showing](https://github.com/user-attachments/assets/7f2aa045-8632-489f-8376-41d341c16790)

View all customers information

![Customer list](https://github.com/user-attachments/assets/e8e60576-bae7-4a4e-b6d3-b0686e8b3f82)

View all appointments

![Appointment screen ALL](https://github.com/user-attachments/assets/f2b3a477-74b5-4683-9d63-a469cf372a35)

Add or modify an appointment

![Add Appointment](https://github.com/user-attachments/assets/26db0a05-96c5-4a7a-aacb-534d06ae7e1d)

Uses convenient menus to select dates and times

![Add appointment with date widget](https://github.com/user-attachments/assets/3b868918-e8ff-4db3-ae35-b7b343ff03a6)

Our appointment has been added to the database and displayed in the list

![Appointments screen with added appt](https://github.com/user-attachments/assets/bee67280-c3e3-4c3d-98c7-b97086a93a27)

View appointments by the current week

![Weekly appointments](https://github.com/user-attachments/assets/f1d42320-cf72-4ab4-880d-1182b378a408)

View appointments on any given month and year

![Monthly Appointments with given month](https://github.com/user-attachments/assets/2c10918f-64d2-4247-9777-90c20fd506e7)

Warnings and confirmations when adding or deleting appointments

![Delete warning](https://github.com/user-attachments/assets/992cc665-fab9-4b8f-82cc-df7f55a8200a)

![Deleted appointment](https://github.com/user-attachments/assets/f425862d-e63e-42fe-b2cb-acc9282aff9e)

![Outside business hours warning](https://github.com/user-attachments/assets/2f2c8f05-b0c9-4f38-a938-ca2caa942a99)
